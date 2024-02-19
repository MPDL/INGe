/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.citationmanager.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.inge.citationmanager.data.FontStyle;
import de.mpg.mpdl.inge.citationmanager.data.FontStylesCollection;
import de.mpg.mpdl.inge.citationmanager.data.Pair;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Function extensions for the citationmanager XSLTs
 *
 * @author Vlad Makarenko (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate: 2010-02-17 16:48:14 +0100 (Mi, 17 Feb 2010) $
 *
 */
public class XsltHelper {
  private static final Logger logger = Logger.getLogger(XsltHelper.class);

  private static Map<Pair, String> citationMap = new HashMap<>();
  private static long lastCitationMapUpdate = 0;

  private static final int FLAGS = Pattern.CASE_INSENSITIVE | Pattern.DOTALL;
  private static final Pattern SPANS_WITH_CLASS = Pattern.compile("<span\\s+class=\"(\\w+)\".*?>(.*?)</span>", FLAGS);
  private static final Pattern AMPS_ALONE = Pattern.compile("\\&(?!\\w+?;)", FLAGS);
  private static final Pattern ALL_TAGS_EXCEPT_STYLE = Pattern.compile("\\<(?!(\\/?style))", FLAGS);
  private static final Pattern ALL_TAGS_EXCEPT_SUB_SUP_STYLE = Pattern.compile("\\<(?!(\\/?style)|(\\/?(su[bp]|SU[BP])))", FLAGS);
  private static final Pattern SUBS_OR_SUPS = Pattern.compile("\\<(\\/?(su[bp]|SU[BP]))\\>", Pattern.DOTALL);

  /**
   * Reads all CONE-entries with a citation-style field filled in. Generates a Map with citation
   * styles and idValue-Type-Pairs.
   *
   * @throws Exception
   */
  public static void getJournalsXML() throws Exception {
    HttpClient client = new HttpClient();

    String coneQuery =
        // JUS-Testserver CoNE
        // "http://193.174.132.114/cone/journals/query?format=rdf&escidoc:citation-style=*&m=full&n=0";
        PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + "journals/query?format=json&escidoc:citation-style=*&m=full&n=0";
    GetMethod getMethod = new GetMethod(coneQuery);
    client.executeMethod(getMethod);

    BufferedReader buffer = new BufferedReader(new InputStreamReader(getMethod.getResponseBodyAsStream()));
    StringBuilder content = new StringBuilder();
    String line;
    while ((line = buffer.readLine()) != null) {
      content.append(line);
    }

    citationMap = getCitationStyleMap(content.toString());
  }

  /**
   * Parses a JSON and returns a citationStyleMap for JUS citations
   *
   * @return a Map<Pair, String> with citations; null if an error occurred
   */
  private static Map<Pair, String> getCitationStyleMap(String json) {
    Map<Pair, String> citationStyleMap = new HashMap<>();
    try {
      JsonNode node = new ObjectMapper().readTree(json);
      if (node.isArray()) {
        for (JsonNode journalObject : node) {
          String id = journalObject.get("id").asText();
          String citation = journalObject.get("http_purl_org_escidoc_metadata_terms_0_1_citation_style").asText();
          citationStyleMap.put(new Pair("CONE", id.substring(id.lastIndexOf("/") + 1)), citation);
          JsonNode identifiers = journalObject.get("http_purl_org_dc_elements_1_1_identifier");
          if (identifiers != null) {
            if (identifiers.isArray()) {
              for (JsonNode identifier : identifiers) {
                String value = identifier.get("http_www_w3_org_1999_02_22_rdf_syntax_ns_value").asText();
                String type = identifier.get("http_www_w3_org_2001_XMLSchema_instance_type").asText();
                type = type.substring(type.lastIndexOf("/") + 1);
                citationStyleMap.put(new Pair(type, value), citation);
              }
            } else {
              String value = identifiers.get("http_www_w3_org_1999_02_22_rdf_syntax_ns_value").asText();
              String type = identifiers.get("http_www_w3_org_2001_XMLSchema_instance_type").asText();
              type = type.substring(type.lastIndexOf("/") + 1);
              citationStyleMap.put(new Pair(type, value), citation);
            }
          }
        }
      }
    } catch (JsonProcessingException e) {
      logger.error("Error converting journal JSON", e);
      return null;
    }

    return citationStyleMap;
  }

  /**
   * Gets the citation style for given idType and idValue. Called from functions.xml for
   * Jus-citation style. Called for publications of type journal article and case note. If there is
   * no idType of the source, a default citation style is returned. Else gets the citation style for
   * the given idValue-Type-Pair.
   *
   * @param idType
   * @param idValue
   * @return
   * @throws Exception
   */
  public static String getCitationStyleForJournal(String idType, String idValue) throws Exception {
    String citationStyle = null;
    // if there is no idType, put the citation style to default
    if (idType.isEmpty()) {
      citationStyle = "default";
    } else {
      // if the type is CoNE, take the ID from the URL
      if (idType.equals("CONE")) {
        idValue = idValue.substring(idValue.lastIndexOf("/") + 1);
      }
      Pair keyValue = new Pair(idType, idValue);
      // Update citationMap if empty or if last update > 1h
      long timeSinceLastUpdate = System.currentTimeMillis() - lastCitationMapUpdate;
      if (citationMap.isEmpty() || timeSinceLastUpdate > (3600 * 1000)) {
        getJournalsXML();
        lastCitationMapUpdate = System.currentTimeMillis();
      }
      if (citationMap.get(keyValue) == null) {
        citationStyle = "default";
      } else {
        citationStyle = citationMap.get(keyValue);
        if (citationStyle.equalsIgnoreCase("Kurztitel_ZS Band, Heft (Jahr)") || citationStyle.equalsIgnoreCase("Titel_ZS Band, Heft (Jahr)")
            || citationStyle.equalsIgnoreCase("(Jahr) Band, Heft Titel_ZS")) {
        } else {
          // if the citation style is none of the three above, put it to default
          citationStyle = "default";
        }
      }
    }
    return citationStyle;
  }

  public static String[] escapeMarkupTags(String[] snippet) {
    if (snippet == null) {
      return null;
    }

    for (int i = 0; i < snippet.length; i++) {
      if (snippet[i] != null) {
        // escape ampersands
        snippet[i] = Utils.replaceAllTotal(snippet[i], AMPS_ALONE, "&amp;");
        // escape tags except <style> and optionally <sub><sup>
        snippet[i] =
            Utils.replaceAllTotal(snippet[i], isBalanced(snippet[i]) ? ALL_TAGS_EXCEPT_SUB_SUP_STYLE : ALL_TAGS_EXCEPT_STYLE, "&lt;");
      }
    }

    return snippet;
  }

  /**
   * Check of the balanced tags sup/sub
   *
   * @param snippet
   * @return <code>true</code> if balanced, <code>false</code> otherwise
   */
  public static boolean isBalanced(String snippet) {
    if (snippet == null)
      return true; // ????
    Stack<String> s = new Stack<>();
    Matcher m = SUBS_OR_SUPS.matcher(snippet);
    while (m.find()) {
      String tag = m.group(1);
      if (tag.toLowerCase().startsWith("su")) {
        s.push(tag);
      } else {
        if (s.empty() || !tag.equals("/" + s.pop())) {
          return false;
        }
      }
    }

    return s.empty();
  }

  public static String convertSnippetToHtml(String snippet) {
    FontStyle fs;
    FontStylesCollection fsc = XmlHelper.loadFontStylesCollection();
    if (!Utils.checkVal(snippet) || fsc == null)
      return snippet;
    StringBuilder sb = new StringBuilder();
    Matcher m = SPANS_WITH_CLASS.matcher(snippet);
    while (m.find()) {
      String cssClass = m.group(1);
      fs = fsc.getFontStyleByCssClass(cssClass);
      // Rigorous: if at list once no css class has been found return str as it is
      if (fs == null) {
        return snippet;
      } else {
        String str = "$2";
        if (fs.getIsStrikeThrough()) {
          str = "<s>" + str + "</s>";
        }
        if (fs.getIsUnderline()) {
          str = "<u>" + str + "</u>";
        }
        if (fs.getIsItalic()) {
          str = "<i>" + str + "</i>";
        }
        if (fs.getIsBold()) {
          str = "<b>" + str + "</b>";
        }
        str = "<span class=\"" + cssClass + "\">" + str + "</span>";
        m.appendReplacement(sb, str);
      }
    }
    snippet = m.appendTail(sb).toString();

    return snippet;
  }

  /**
   * Check CJK codepoints in <code>str</code>.
   *
   * @param str
   * @return <code>true</code> if <code>str</code> has at least one CJK codepoint, otherwise
   *         <code>false</code>.
   */
  public static boolean isCJK(String str) {
    if (str == null || str.trim().isEmpty())
      return false;
    for (int i = 0; i < str.length(); i++) {
      int codePoint = str.codePointAt(i);
      if (codePoint >= 19968 && codePoint <= 40911)
        return true;
    }
    return false;
  }
}
