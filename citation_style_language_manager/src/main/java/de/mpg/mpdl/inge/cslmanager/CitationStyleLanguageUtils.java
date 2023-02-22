/**
 * 
 */
package de.mpg.mpdl.inge.cslmanager;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import de.mpg.mpdl.inge.util.DOMUtilities;

/**
 * Utility class for static functions that are often needed when working with CSL
 * 
 * @author walter
 */
public class CitationStyleLanguageUtils {
  private static final Logger logger = Logger.getLogger(CitationStyleLanguageUtils.class);

  //  /**
  //   * gets a csl style from a url
  //   * 
  //   * @param url
  //   * @return csl style xml as String or null if no style could be found or read
  //   * @throws Exception
  //   */
  //  protected static String loadStyleFromUrl(String url) throws Exception {
  //    String style = null;
  //    try {
  //      style = CSLUtils.readURLToString(new URL(url), "UTF-8");
  //    } catch (MalformedURLException e) {
  //      logger.error("URL seems to be malformed, when trying to retrieve the csl style", e);
  //      throw new Exception(e);
  //    } catch (IOException e) {
  //      logger.error("IO-Problem, when trying to retrieve the csl style", e);
  //      throw new Exception(e);
  //    }
  //    return style;
  //  }

  /**
   * gets a csl style from a cone url delivered in json format
   * 
   * @param url
   * @return csl style xml as String or null if no style could be found or read
   * @throws Exception
   */
  protected static String loadStyleFromConeJsonUrl(String url) throws Exception {
    String xml = null;
    try {
      JsonFactory jfactory = new JsonFactory();
      // read JSON from url
      JsonParser jParser = jfactory.createParser(new URL(url + "?format=json"));
      while (jParser.nextToken() != null && jParser.nextToken() != JsonToken.END_OBJECT) {
        String fieldname = jParser.getCurrentName();
        if ("http_www_w3_org_1999_02_22_rdf_syntax_ns_value".equals(fieldname)) {
          // current token is "name",
          // move to next, which is "name"'s value
          jParser.nextToken();
          xml = jParser.getText();
          break;
        }
      }
      jParser.close();
    } catch (JsonParseException e) {
      logger.error("Error parsing json from URL (" + url + ")", e);
      throw new Exception(e);
    } catch (IOException e) {
      logger.error("Error getting json from URL (" + url + ")", e);
      throw new Exception(e);
    }
    if (logger.isDebugEnabled()) {
      logger.debug("Successfully parsed CSL-XML from URL (" + url + ")\n--------------------\n" + xml + "\n--------------------\n");
    }
    return xml;
  }

  /**
   * parses the default-locale value of a csl citation style
   * 
   * @return
   */
  protected static String parseDefaultLocaleFromStyle(String style) {
    String defaultLocale = null;
    try {
      Document doc = DOMUtilities.createDocument(style);
      NodeList styleTagList = doc.getElementsByTagName("style");
      if (styleTagList != null && styleTagList.getLength() != 0) {
        defaultLocale = styleTagList.item(0).getAttributes().getNamedItem("default-locale").getNodeValue();
      }
    } catch (ParserConfigurationException e) {
      logger.error("Wrong parser configuration", e);
      return null;
    } catch (SAXException e) {
      logger.error("Problem creating XML", e);
      return null;
    } catch (IOException e) {
      logger.error("Problem transforming String to InputStream", e);
      return null;
    } catch (Exception e) {
      // this is just the case when there is no attribute 'default-locale'
      if (logger.isDebugEnabled())
        logger.debug("Error getting default-locale attribute", e);
      return null;
    }
    return defaultLocale;
  }


  /**
   * parses a tag value out of an xml
   * 
   * @param xml
   * @param tagName
   * @return
   */
  /*
  public static String parseTagFromXml(String xml, String tagName, String namespaceUrl) {
    String tag = null;
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(IOUtils.toInputStream(xml, "UTF-8"));
      NodeList tagList = doc.getElementsByTagNameNS(namespaceUrl, tagName);
      if (tagList != null && tagList.getLength() != 0) {
        tag = tagList.item(0).getFirstChild().getNodeValue();
        if (logger.isDebugEnabled())
          logger.debug("successfully parsed tag <" + tagList.item(0).getNodeName() + ">");
      }
    } catch (ParserConfigurationException e) {
      logger.error("Wrong parser configuration", e);
      return null;
    } catch (SAXException e) {
      logger.error("Problem creating XML", e);
      return null;
    } catch (IOException e) {
      logger.error("Problem transforming String to InputStream", e);
      return null;
    } catch (Exception e) {
      // this is just the case when there is no attribute 'default-locale'
      if (logger.isDebugEnabled())
        logger.debug("Error getting value for <" + tagName + ">", e);
      return null;
    }
    return tag;
  }
  */
}
