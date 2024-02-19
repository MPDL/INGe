package de.mpg.mpdl.inge.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import net.sf.saxon.event.SaxonOutputKeys;

public class DOMUtilities {

  // List of CDATA elements
  // todo transfer to some citation project
  public final static String CDATAElements = "valid-if max-count max-length variable " + // CitationStyle
                                                                                         // definition

      "{http://purl.org/dc/terms/}bibliographicCitation" // Snippet output
  ;

  /**
   * Builds new DocumentBuilder
   *
   * @return DocumentBuilder
   * @throws ParserConfigurationException
   */
  private static DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
    return createDocumentBuilder(false);
  }

  /**
   * Builds new DocumentBuilder
   *
   * @return DocumentBuilder
   * @throws ParserConfigurationException
   */
  private static DocumentBuilder createDocumentBuilder(final boolean namespaceAwareness) throws ParserConfigurationException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setValidating(false);
    dbf.setIgnoringComments(true);
    dbf.setNamespaceAware(namespaceAwareness);

    return dbf.newDocumentBuilder();
  }

  /**
   * Creates new empty org.w3c.dom.Document
   *
   * @return org.w3c.dom.Document
   * @throws ParserConfigurationException
   */
  private static Document createDocument(final boolean namespaceAwareness) throws ParserConfigurationException {
    return createDocumentBuilder(namespaceAwareness).newDocument();
  }

  /**
   * Creates new empty org.w3c.dom.Document
   *
   * @return org.w3c.dom.Document
   * @throws ParserConfigurationException
   */
  public static Document createDocument() throws ParserConfigurationException {
    return createDocument(false);
  }

  /**
   * Creates new org.w3c.dom.Document
   *
   * @param xml
   * @return org.w3c.dom.Document
   * @throws ParserConfigurationException
   * @throws SAXException
   * @throws UnsupportedEncodingException
   */
  public static Document createDocument(String xml) throws IOException, ParserConfigurationException, SAXException {

    return createDocument(xml.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Parse the given xml String into a Document.
   *
   * @param xml The xml String.
   * @param namespaceAwareness namespace awareness (default is false)
   * @return The Document.
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws UnsupportedEncodingException
   * @throws SAXException
   * @throws Exception If anything fails.
   */
  public static Document createDocument(final String xml, final boolean namespaceAwareness)
      throws ParserConfigurationException, UnsupportedEncodingException, IOException, SAXException {
    return createDocumentBuilder(namespaceAwareness).parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), "UTF-8");
  }

  private static Document createDocument(byte[] xml) throws ParserConfigurationException, SAXException, IOException {
    return createDocumentBuilder().parse(new ByteArrayInputStream(xml), "UTF-8");
  }

  public static Document createDocument(Node sourceNode) throws Exception {

    Document doc = createDocument();
    Node source;
    if (sourceNode.getNodeType() == Node.DOCUMENT_NODE) {
      source = ((Document) sourceNode).getDocumentElement();
    } else {
      source = sourceNode;
    }

    Node node = doc.importNode(source, true);
    doc.appendChild(node);

    return doc;
  }


  // /**
  // * Writes org.w3c.dom.Document to OutputStream
  // *
  // * @param doc
  // * @throws IOException
  // */
  // public static OutputStream output(Document doc) throws IOException {
  // OutputStream baos = new ByteArrayOutputStream();
  // StreamResult streamResult = new StreamResult(baos);
  // outputBase(doc, streamResult);
  // return baos;
  // }

  /**
   * Writes org.w3c.dom.Document to XML file
   *
   * @param doc
   * @param xmlFileName
   * @throws IOException
   */
  public static void output(Document doc, String xmlFileName) throws IOException {

    FileOutputStream output = new FileOutputStream(xmlFileName);
    StreamResult streamResult = new StreamResult(output);
    outputBase(doc, streamResult);
  }

  // /**
  // * Writes org.w3c.dom.Document to OutputStream
  // *
  // * @param doc
  // * @throws IOException
  // */
  // public static void output(Document doc, OutputStream os) throws IOException {
  //
  // StreamResult streamResult = new StreamResult(os);
  // outputBase(doc, streamResult);
  // }

  /**
   * Base procedure for xml serialization
   *
   * @param doc - is org.w3c.dom.Document
   * @param streamResult r
   * @throws IOException
   */
  private static void outputBase(Document doc, StreamResult streamResult) throws IOException {
    DOMSource domSource = new DOMSource(doc);
    TransformerFactory tf = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
    try {
      Transformer serializer = tf.newTransformer();
      // Output properties
      serializer.setOutputProperty(OutputKeys.INDENT, "yes");
      serializer.setOutputProperty(OutputKeys.STANDALONE, "yes");
      // TODO: saxon specific, to get rid of it later
      serializer.setOutputProperty(SaxonOutputKeys.INDENT_SPACES, "4");
      serializer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, CDATAElements);
      serializer.transform(domSource, streamResult);
    } catch (Exception e) {
      throw new IOException(e.getMessage());
    }
  }

  /**
   * Writes org.w3c.dom.Document to String
   *
   * @param doc
   * @throws IOException
   */
  public static String outputString(Document doc) throws IOException {
    StringWriter output = new StringWriter();
    StreamResult streamResult = new StreamResult(output);
    outputBase(doc, streamResult);

    return output.toString();
  }

  /**
   * Delivers the value of one distinct node in an <code>org.w3c.dom.Document</code>.
   *
   * @param document The <code>org.w3c.dom.Document</code>
   * @param xpathExpression The XPath expression as string
   *
   * @return The value of the node
   *
   */
  public static String getValue(Document document, String xpathExpression) {
    XPathFactory factory = XPathFactory.newInstance();
    XPath xPath = factory.newXPath();
    try {
      return xPath.evaluate(xpathExpression, document);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Return the child of the node selected by the xPath.
   *
   * @param node The node.
   * @param xpathExpression The XPath expression as string
   *
   * @return The child of the node selected by the xPath
   *
   */
  public static Node selectSingleNode(final Node node, final String xpathExpression) {
    XPathFactory factory = XPathFactory.newInstance();
    XPath xPath = factory.newXPath();
    try {
      return (Node) xPath.evaluate(xpathExpression, node, XPathConstants.NODE);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Return the list of children of the node selected by the xPath.
   *
   * @param node The node.
   * @param xpathExpression The xPath.
   * @return The list of children of the node selected by the xPath.
   */
  public static NodeList selectNodeList(final Node node, final String xpathExpression) {
    XPathFactory factory = XPathFactory.newInstance();
    XPath xPath = factory.newXPath();
    try {
      return (NodeList) xPath.evaluate(xpathExpression, node, XPathConstants.NODESET);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // /**
  // * Return the text value of the selected attribute.
  // *
  // * @param node The node.
  // * @param xPath The xpath to select the node containint the attribute,
  // * @param attributeName The name of the attribute.
  // * @return The text value of the selected attribute.
  // * @throws Exception If anything fails.
  // */
  // public static String getAttributeValue(final Node node, final String xPath,
  // final String attributeName) throws Exception {
  // if (node == null) {
  // throw new IllegalArgumentException("getAttributeValue:node is null");
  //
  // }
  // if (xPath == null) {
  // throw new IllegalArgumentException("getAttributeValue:xPath is null");
  // }
  // if (attributeName == null) {
  // throw new IllegalArgumentException("getAttributeValue:attributeName is null");
  // }
  // String result = null;
  // Node attribute = selectSingleNode(node, xPath);
  // if (attribute.hasAttributes()) {
  // result = attribute.getAttributes().getNamedItem(attributeName).getTextContent();
  // }
  // return result;
  // }

  // /**
  // * Gets the value of the specified attribute of the root element from the document.
  // *
  // * @param document The document to retrieve the value from.
  // * @param attributeName The name of the attribute whose value shall be retrieved.
  // * @return Returns the attribute value.
  // * @throws Exception If anything fails.
  // * @throws TransformerException
  // */
  // public static String getRootElementAttributeValue(final Document document,
  // final String attributeName) throws Exception {
  // if (document == null) {
  // throw new IllegalArgumentException("getRootElementAttributeValue:document is null");
  // }
  // if (attributeName == null) {
  // throw new IllegalArgumentException("getRootElementAttributeValue:attributeName is null");
  // }
  // String xPath;
  // if (attributeName.startsWith("@")) {
  // xPath = "/*/" + attributeName;
  // } else {
  // xPath = "/*/@" + attributeName;
  // }
  //
  // String value = selectSingleNode(document, xPath).getTextContent();
  // return value;
  // }

  /**
   * Serialize the given Dom Object to a String.
   *
   * @param xml The Xml Node to serialize.
   * @param omitXMLDeclaration Indicates if XML declaration will be omitted.
   * @return The String representation of the Xml Node.
   * @throws Exception If anything fails.
   */
  protected static String toString(final Node xml, final boolean omitXMLDeclaration) throws Exception {
    if (xml == null) {
      throw new IllegalArgumentException("toString:xml is null");
    }
    String result = null;

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    // serialize
    DOMImplementation implementation = DOMImplementationRegistry.newInstance().getDOMImplementation("XML 3.0");
    DOMImplementationLS feature = (DOMImplementationLS) implementation.getFeature("LS", "3.0");
    LSSerializer serial = feature.createLSSerializer();
    LSOutput output = feature.createLSOutput();
    output.setByteStream(outputStream);
    serial.write(xml, output);

    result = output.toString();

    return result;
  }

  // /**
  // * Assert that the Element/Attribute selected by the xPath exists.
  // *
  // * @param message The message printed if assertion fails.
  // * @param node The Node.
  // * @param xPath The xPath.
  // * @throws Exception If anything fails.
  // */
  // public static boolean assertXMLExist(final Node node, final String xPath) throws Exception {
  //
  // if (node == null) {
  // throw new IllegalArgumentException("assertXMLExist:node is null");
  // }
  // if (xPath == null) {
  // throw new IllegalArgumentException("assertXMLExist:xPath is null");
  // }
  // NodeList nodes = selectNodeList(node, xPath);
  // return (nodes.getLength() > 0 ? true : false);
  // }
}
