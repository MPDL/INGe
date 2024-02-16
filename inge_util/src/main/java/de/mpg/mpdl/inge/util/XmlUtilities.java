package de.mpg.mpdl.inge.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class XmlUtilities {

  /**
   * Logger for this class.
   */
  private static final Logger logger = Logger.getLogger(XmlUtilities.class);
  private static Map<String, Schema> schemas = null;

  /**
   * Assert that the XML is valid to the schema.
   *
   * @param xmlData The XML as string
   * @throws Exception Any exception
   */
  public static void assertXMLValid(final String xmlData) throws Exception {

    if (xmlData == null) {
      throw new IllegalArgumentException("assertXMLValid:xmlData is null");
    }

    if (schemas == null) {
      initializeSchemas();
    }

    String nameSpace = getNameSpaceFromXml(xmlData);

    logger.debug("Looking up namespace '" + nameSpace + "'");

    Schema schema = schemas.get(nameSpace);

    logger.debug("Schema: " + schema);

  }

  /**
   * @throws IOException
   * @throws SAXException
   * @throws ParserConfigurationException
   */
  private static void initializeSchemas() throws IOException, SAXException, ParserConfigurationException {
    File[] schemaFiles = ResourceUtil.getFilenamesInDirectory("xsd/", XmlUtilities.class.getClassLoader());
    schemas = new HashMap<String, Schema>();
    SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    for (File file : schemaFiles) {
      try {
        Schema schema = sf.newSchema(file);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        DefaultHandler handler = new DefaultHandler() {
          private String nameSpace = null;
          private boolean found = false;

          public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if (!found) {
              String tagName = null;
              int ix = qName.indexOf(":");
              if (ix >= 0) {
                tagName = qName.substring(ix + 1);
              } else {
                tagName = qName;
              }
              if ("schema".equals(tagName)) {
                nameSpace = attributes.getValue("targetNamespace");
                found = true;
              }
            }
          }

          public String toString() {
            return nameSpace;
          }
        };
        parser.parse(file, handler);
        if (handler.toString() != null) {
          schemas.put(handler.toString(), schema);
        } else {
          logger.warn("Error reading xml schema: " + file);
        }

      } catch (Exception e) {
        logger.warn("Invalid xml schema " + file);
        logger.debug("Stacktrace: ", e);
      }

    }
  }

  /**
   * @param xmlData
   * @return
   * @throws ParserConfigurationException
   * @throws SAXException
   * @throws IOException
   * @throws UnsupportedEncodingException
   */
  private static String getNameSpaceFromXml(final String xmlData)
      throws ParserConfigurationException, SAXException, IOException, UnsupportedEncodingException {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser parser = factory.newSAXParser();
    DefaultHandler handler = new DefaultHandler() {
      private String nameSpace = null;
      private boolean first = true;

      public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (first) {
          if (qName.contains(":")) {
            String prefix = qName.substring(0, qName.indexOf(":"));
            String attributeName = "xmlns:" + prefix;
            nameSpace = attributes.getValue(attributeName);
          } else {
            nameSpace = attributes.getValue("xmlns");
          }
          first = false;
        }

      }

      public String toString() {
        return nameSpace;
      }
    };
    parser.parse(new ByteArrayInputStream(xmlData.getBytes(StandardCharsets.UTF_8)), handler);
    String nameSpace = handler.toString();
    return nameSpace;
  }

  /**
   * Returns an XML-escaped String that can be used for writing an XML.
   *
   * @param input A string
   * @return The XML-escaped string
   */
  public static String escape(String input) {
    if (input != null) {
      input = input.replace("&", "&amp;");
      input = input.replace("<", "&lt;");
      input = input.replace("\"", "&quot;");
    }

    return input;
  }

}
