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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeFilter;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.topologi.schematron.SchtrnParams;
import com.topologi.schematron.SchtrnValidator;

import de.mpg.mpdl.inge.citationmanager.CitationStyleManagerException;
import de.mpg.mpdl.inge.citationmanager.data.FontStylesCollection;
import de.mpg.mpdl.inge.util.DOMUtilities;
import de.mpg.mpdl.inge.util.ResourceUtil;

/**
 *
 * XML processing helper
 *
 * @author vmakarenko (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class XmlHelper {

  private static final Logger logger = LogManager.getLogger(XmlHelper.class);

  public static final String CITATIONSTYLE_XML_SCHEMA_FILE = "citation-style.xsd";
  public static final String SCHEMATRON_DIRECTORY = "Schematron/";
  public static final String SCHEMATRON_FILE = SCHEMATRON_DIRECTORY + "layout-element.sch";
  public static final String FONT_STYLES_COLLECTION_FILE = "font-styles.xml";

  public static final String CSL = "CSL";
  public static final String PDF = "pdf";

  private static final TransformerFactory TF = new net.sf.saxon.TransformerFactoryImpl();

  public static final HashMap<String, Templates> templCache = new HashMap<>(20);

  // List of all available output formats
  public static HashMap<String, String[]> outputFormatsHash = null;

  private static final XPath xpath = XPathFactory.newInstance().newXPath();

  private static HashMap<String, HashMap<String, String[]>> citationStylesHash = null;

  // FontStyleCollectiona Hash
  public static final HashMap<String, FontStylesCollection> fsc = new HashMap<>();

  /**
   * Load Default FontStylesCollection, singleton 1) if no citation style is given, return default
   * {@link FontStylesCollection} 2) if citation style is given, check citation style directory. If
   * there is a citation style specific {@link FontStylesCollection} in the citation style directory
   * FontStylesCollection, get it; if not - get default FontStylesCollection
   *
   * @param cs - citation style
   * @return {@link FontStylesCollection}
   * @throws CitationStyleManagerException
   */
  private static FontStylesCollection loadFontStylesCollection(String cs) {
    // get default FontStyleCollection from __Default__ element for empty cs
    if (cs == null || cs.trim().isEmpty())
      return loadFontStylesCollection("__Default__");
    if (fsc.containsKey(cs))
      return fsc.get(cs);
    try {
      // load __Default__ collection
      if ("__Default__".equalsIgnoreCase(cs)) {
        fsc.put(cs, FontStylesCollection.loadFromXml(CitationUtil.getPathToCitationStyles() + FONT_STYLES_COLLECTION_FILE));
      } else {
        InputStream inputStream = ResourceUtil.getResourceAsStream(CitationUtil.getPathToCitationStyle(cs) + FONT_STYLES_COLLECTION_FILE,
            XmlHelper.class.getClassLoader());
        // get specific FontStyleCollection for citation style if exists
        if (inputStream != null) {
          fsc.put(cs, FontStylesCollection.loadFromXml(inputStream));
        }
        // otherwise: get __Default_ one
        else {
          fsc.put(cs, loadFontStylesCollection());
        }
      }
      return fsc.get(cs);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      throw new RuntimeException("Cannot loadFontStylesCollection: ", e);
    }
  }

  /**
   * Load Default FontStylesCollection
   *
   * @return {@link FontStylesCollection}
   * @throws CitationStyleManagerException
   */
  public static FontStylesCollection loadFontStylesCollection() {
    return loadFontStylesCollection(null);
  }

  /**
   * Maintain prepared stylesheets in memory for reuse
   */
  public static Templates tryTemplCache(String path) throws TransformerException, FileNotFoundException, CitationStyleManagerException {
    Utils.checkName(path, "Empty XSLT name.");

    InputStream is = ResourceUtil.getResourceAsStream(path, XmlHelper.class.getClassLoader());

    Templates x = templCache.get(path);
    if (x == null) {
      x = TF.newTemplates(new StreamSource(is));
      templCache.put(path, x);
    }

    return x;
  }

  /**
   * XML Schema validation (JAVAX)
   *
   * @param schemaUrl is the XML Schema
   * @param xmlDocumentUrl is URI to XML to be validated
   * @throws CitationStyleManagerException
   */
  private String validateSchema(final String schemaUrl, final String xmlDocumentUrl) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      factory.setValidating(true);
      factory.setXIncludeAware(true);
      factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");

      DocumentBuilder builder = factory.newDocumentBuilder();
      Validator handler = new Validator();
      builder.setErrorHandler(handler);

      builder.parse(ResourceUtil.getResourceAsFile(xmlDocumentUrl, XmlHelper.class.getClassLoader()));

      if (handler.validationError) {
        return ("XML Document has Error: " + handler.saxParseException.getLineNumber() + ":" + handler.saxParseException.getColumnNumber()
            + ", " + handler.saxParseException.getMessage());
      }
    } catch (java.io.IOException ioe) {
      return ("IO problems: " + ioe.getMessage());
    } catch (SAXException e) {
      return ("SAX problems: " + e.getMessage());
    } catch (ParserConfigurationException e) {
      return ("Wrong ParserConfiguration: " + e.getMessage());
    }

    return null;
  }

  public String validateCitationStyleXML(final String cs) throws IOException {
    String csFile = CitationUtil.getPathToCitationStyleXML(cs);
    logger.info("Document to be validated: " + csFile);

    // XML Schema validation
    logger.info("XML Schema validation...");
    String report =
        validateSchema(CitationUtil.getUriToResources() + CitationUtil.SCHEMAS_DIRECTORY + CITATIONSTYLE_XML_SCHEMA_FILE, csFile);
    if (report != null) {
      return report;
    }
    logger.info("OK");

    // Schematron validation
    logger.info("Schematron validation...");
    SchtrnValidator validator = new SchtrnValidator();
    validator.setEngineStylesheet(CitationUtil.getPathToSchemas() + SCHEMATRON_DIRECTORY + "schematron-diagnose.xsl");
    validator.setParams(new SchtrnParams());
    validator.setBaseXML(true);
    try {
      report = validator.validate(csFile, CitationUtil.getPathToSchemas() + SCHEMATRON_FILE);
    } catch (TransformerConfigurationException e1) {
      return "Schematron validation problem (TransformerConfigurationException): " + e1.getMessage();
    } catch (TransformerException e1) {
      return "Schematron validation problem (TransformerException): " + e1.getMessage();
    } catch (Exception e1) {
      return "Schematron validation problem: " + e1.getMessage();
    }
    if (report != null && report.contains("Report: ")) {
      return report;
    }
    logger.info("OK");

    return null;
  }

  /**
   * Validator class for XML Schema validation
   */
  private class Validator extends DefaultHandler {
    public boolean validationError = false;
    public SAXParseException saxParseException = null;

    public void error(SAXParseException exception) {
      validationError = true;
      saxParseException = exception;
    }

    public void fatalError(SAXParseException exception) {
      validationError = true;
      saxParseException = exception;
    }

  }

  /*
   * Returns list of Citation Styles
   */
  public static String[] getListOfStyles() {
    Object[] oa = getCitationStylesHash().keySet().toArray();
    return Arrays.copyOf(oa, oa.length, String[].class);
  }

  /**
   * Returns the list of the output formats (first element of the array) for the citation style
   * <code>csName</code>.
   *
   * @param csName is name of citation style
   * @return String[] of the output formats
   * @throws CitationStyleManagerException
   * @throws Exception
   * @throws IOException
   */
  public static String[] getOutputFormatsArray(String csName) throws CitationStyleManagerException {
    Utils.checkCondition(!Utils.checkVal(csName), "Empty name of the citation style");
    Object[] oa = getCitationStylesHash().get(csName).keySet().toArray();
    return Arrays.copyOf(oa, oa.length, String[].class);
  }

  /**
   * Returns the mime-type for output format of the citation style
   *
   * @param csName is name of citation style
   * @param outFormat is the output format
   * @return mime-type, or <code>null</code>, if no <code>mime-type</code> has been found
   * @throws CitationStyleManagerException if no <code>csName</code> or <code>outFormat</code> are
   *         defined
   */
  public static String getMimeType(String csName, String outFormat) throws CitationStyleManagerException {

    Utils.checkCondition(!Utils.checkVal(csName), "Empty name of the citation style");
    Utils.checkCondition(!Utils.checkVal(outFormat), "Empty name of the output format");

    HashMap<String, HashMap<String, String[]>> csh = getCitationStylesHash();

    Utils.checkCondition(!csh.containsKey(csName), "No citation style is defined: " + csName);
    Utils.checkCondition(!csh.get(csName).containsKey(outFormat),
        "No output Format:  " + outFormat + " for citation style: " + csName + " is defined");

    return csh.get(csName).get(outFormat)[0];
  }

  /**
   * Returns the file extension according to format name.
   *
   * @throws CitationStyleManagerException
   */
  public static String getExtensionByName(String outputFormat) throws CitationStyleManagerException {
    Utils.checkCondition(!Utils.checkVal(outputFormat), "Empty output format name");

    outputFormat = outputFormat.trim();

    HashMap<String, String[]> of = getOutputFormatsHash();

    return of.containsKey(outputFormat) ? of.get(outputFormat)[1] : of.get(XmlHelper.PDF)[1];
  }

  /**
   * Get outputFormatsHash, where keys: names of output format values: array of {mime-type,
   * file-extension} for the output format
   *
   * @return outputFormatsHash
   */
  public static HashMap<String, String[]> getOutputFormatsHash() {

    if (outputFormatsHash == null) {
      NodeList nl = null;
      try {
        nl = xpathNodeList("/export-formats/output-formats/output-format",
            ResourceUtil.getResourceAsString(CitationUtil.SCHEMAS_DIRECTORY + "explain-styles.xml", XmlHelper.class.getClassLoader()));
      } catch (Exception e) {
        throw new RuntimeException("Cannot process expain file:", e);
      }

      outputFormatsHash = new HashMap<>();

      for (int i = 0; i < nl.getLength(); i++) {
        Node n = nl.item(i);
        NodeList nll = n.getChildNodes();

        String name = null, format = null, ext = null;
        for (int ii = 0; ii < nll.getLength(); ii++) {
          Node nn = nll.item(ii);

          if (nn.getNodeType() == Node.ELEMENT_NODE) {
            String nodeName = nn.getNodeName();
            if ("dc:title".equals(nodeName)) {
              name = nn.getTextContent();
            }
            if ("dc:format".equals(nodeName)) {
              format = nn.getTextContent();
            }
            if ("file-ext".equals(nodeName)) {
              ext = nn.getTextContent();
            }
          }
        }
        outputFormatsHash.put(name, // key: output format key
            new String[] {format, // mime-type
                ext // extension
            }

        );
      }
    }

    return outputFormatsHash;
  }

  /**
   * Return citationStylesHash keys: citation style id value: hash of supported output formats
   *
   * @return citationStylesHash
   */
  public static HashMap<String, HashMap<String, String[]>> getCitationStylesHash() {
    if (citationStylesHash == null) {
      NodeList nl;
      try {
        nl = xpathNodeList("/export-formats/export-format/identifier",
            ResourceUtil.getResourceAsString(CitationUtil.SCHEMAS_DIRECTORY + "explain-styles.xml", XmlHelper.class.getClassLoader()));
      } catch (Exception e) {
        throw new RuntimeException("Cannot process expain file:", e);
      }
      citationStylesHash = new HashMap<>();
      // for all export formats take identifiers
      for (int i = 0; i < nl.getLength(); i++) {
        Node n = nl.item(i);

        String exportFormat = n.getTextContent();

        // find output formats
        NodeList exportFormatChildren = n.getParentNode().getChildNodes();

        // find output formats element
        Node outputFormatsNode = findNode(exportFormatChildren, "output-formats");

        // if no export format identifier found, continue for
        HashMap<String, String[]> formatsHash = new HashMap<>();
        if (!(outputFormatsNode == null || outputFormatsNode.getTextContent() == null)) {
          String refs = outputFormatsNode.getAttributes().getNamedItem("refs").getTextContent();

          for (String outputFormat : refs.split("\\s+")) {
            // check outputFormat availability
            if (getOutputFormatsHash().containsKey(outputFormat))
              formatsHash.put(outputFormat, getOutputFormatsHash().get(outputFormat));
          }
        }

        citationStylesHash.put(exportFormat, formatsHash);
      }
    }
    return citationStylesHash;
  }

  /**
   * Search for the first <code>Node</code> with the nodeName().equals(nodeName) in the nodeList
   *
   * @param nodeList
   * @param nodeName
   * @return Node or null if no Node has been found
   */
  private static Node findNode(NodeList nodeList, String nodeName) {
    Node curNode;
    for (int i = 0; i < nodeList.getLength(); i++) {
      curNode = nodeList.item(i);
      if (curNode.getNodeType() == Node.ELEMENT_NODE && nodeName.equals(curNode.getNodeName())) {
        return curNode;
      }
    }

    return null;
  }

  /*****************/

  /** XPATH Utils **/
  /*****************/
  private static NodeList xpathNodeList(String expr, String xml) {
    try {
      return xpathNodeList(expr, DOMUtilities.createDocument(xml));
    } catch (Exception e) {
      throw new RuntimeException("Cannot evaluate XPath:", e);
    }
  }

  private static NodeList xpathNodeList(String expr, Document doc) {
    try {
      return (NodeList) xpath.evaluate(expr, doc, XPathConstants.NODESET);
    } catch (Exception e) {
      throw new RuntimeException("Cannot evaluate XPath:", e);
    }
  }
}


class ExportFormatNodeFilter implements NodeFilter {
  public static final String DC_NS = "http://purl.org/dc/elements/1.1/";

  @Override
  public short acceptNode(Node n) {
    Node parent = n.getParentNode();
    if ("identifier".equals(n.getLocalName()) && DC_NS.equals(n.getNamespaceURI()) && parent != null
        && "export-format".equals(parent.getLocalName())) {
      return FILTER_ACCEPT;
    }

    return FILTER_SKIP;
  }
}
