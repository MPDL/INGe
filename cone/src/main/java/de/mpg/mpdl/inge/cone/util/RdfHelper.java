/*
 *
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

package de.mpg.mpdl.inge.cone.util;

import java.io.StringWriter;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

import de.mpg.mpdl.inge.cone.ConeException;
import de.mpg.mpdl.inge.cone.Describable;
import de.mpg.mpdl.inge.cone.LocalizedString;
import de.mpg.mpdl.inge.cone.ModelList;
import de.mpg.mpdl.inge.cone.Pair;
import de.mpg.mpdl.inge.cone.TreeFragment;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Static Helper class for RDF formatting.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class RdfHelper {

  private RdfHelper() {

  }

  /**
   * Formats an List&lt;Pair&gt; into an RDF list.
   *
   * @param pairs A list of key-value pairs
   *
   * @return The RDF
   * @throws ConeException
   */
  public static String formatList(List<? extends Describable> pairs, ModelList.Model model) throws ConeException {

    StringWriter result = new StringWriter();

    result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    result.append("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" " + "xmlns:dc=\"http://purl.org/dc/elements/1.1/\"");

    if (null != model.getRdfAboutTag() && null != model.getRdfAboutTag().getNamespaceURI()
        && !"http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(model.getRdfAboutTag().getNamespaceURI()))

    {
      result.append(" xmlns:" + model.getRdfAboutTag().getPrefix() + "=\"" + model.getRdfAboutTag().getNamespaceURI() + "\"");
    }

    result.append(">\n");
    if (null != pairs) {

      for (Describable pair : pairs) {
        if (pair instanceof Pair) {
          String key = ((Pair) pair).getKey();
          try {
            result.append("\t<rdf:Description rdf:about=\"" + PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL)
                + key.replace("\"", "\\\"") + "\">\n");
            if (((Pair) pair).getValue() instanceof LocalizedString) {
              if (null != ((LocalizedString) ((Pair) pair).getValue()).getLanguage()) {
                result.append("\t\t<dc:title xml:lang=\"" + ((LocalizedString) ((Pair) pair).getValue()).getLanguage() + "\">"
                    + StringEscapeUtils.escapeXml10(((LocalizedString) ((Pair) pair).getValue()).getValue()) + "</dc:title>\n");
              } else {
                result.append("\t\t<dc:title>" + StringEscapeUtils.escapeXml10(((LocalizedString) ((Pair) pair).getValue()).getValue())
                    + "</dc:title>\n");
              }
            } else {
              result.append("\t\t<dc:title>" + StringEscapeUtils.escapeXml10(((Pair) pair).getValue().toString()) + "</dc:title>\n");
            }
            result.append("\t</rdf:Description>\n");
          } catch (Exception exception) {
            throw new ConeException(exception);
          }
        } else if (pair instanceof TreeFragment) {
          result.append(((TreeFragment) pair).toRdf(model));
        }
      }
    }

    result.append("</rdf:RDF>\n");

    return result.toString();
  }

  /**
   * Formats an a Map of triples into RDF.
   *
   * @param id The cone-id of the object
   * @param triples A map of s-p-o triples
   *
   * @return The RDF
   */
  public static String formatMap(String id, TreeFragment triples, ModelList.Model model) throws ConeException {
    if (null != triples) {
      StringWriter result = new StringWriter();

      result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      result.append("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"");


      if (null != model.getRdfAboutTag() && null != model.getRdfAboutTag().getNamespaceURI()
          && !"http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(model.getRdfAboutTag().getNamespaceURI())) {
        result.append(" xmlns:" + model.getRdfAboutTag().getPrefix() + "=\"" + model.getRdfAboutTag().getNamespaceURI() + "\"");
      }


      result.append(">\n");

      result.append(triples.toRdf(model));

      result.append("</rdf:RDF>\n");

      return result.toString();
    } else {
      return "";
    }
  }
}
