package de.mpg.mpdl.inge.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import net.sf.saxon.event.SaxonOutputKeys;



public class DOMUtilities {

  // List of CDATA elemetns
  public final static String CDATAElements = "valid-if max-count max-length variable " + // CitationStyle
                                                                                         // definition

      "{http://purl.org/dc/terms/}bibliographicCitation" // Snippet output
  ;

  /**
   * Builds new DocumentBuilder
   * 
   * @return DocumentBuilder
   * @throws CitationStyleManagerException
   */
  public static DocumentBuilder createDocumentBuilder() {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setValidating(false);
    dbf.setIgnoringComments(true);
    dbf.setNamespaceAware(false);

    try {
      return dbf.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new RuntimeException("Failed to create a document builder factory", e);
    }
  }

  /**
   * Creates new empty org.w3c.dom.Document
   * 
   * @return org.w3c.dom.Document
   * @throws CitationStyleManagerException
   */
  public static Document createDocument() {
    return createDocumentBuilder().newDocument();
  }

  /**
   * Creates new org.w3c.dom.Document
   * 
   * @param xml
   * @return org.w3c.dom.Document
   * @throws RuntimeException
   */
  public static Document createDocument(String xml) {
    try {
      return createDocument(xml.getBytes("UTF-8"));
    } catch (Exception e) {
      throw new RuntimeException("Cannot create Document", e);
    }
  }

  /**
   * Creates new org.w3c.dom.Document
   * 
   * @param xml
   * @return org.w3c.dom.Document
   * @throws CitationStyleManagerException
   * @throws IOException
   * @throws SAXException
   * @throws Exception
   */
  public static Document createDocument(byte[] xml) {
    DocumentBuilder db = createDocumentBuilder();
    try {
      return db.parse(new ByteArrayInputStream(xml), "UTF-8");
    } catch (Exception e) {
      throw new RuntimeException("Cannot create Document", e);
    }

  }

  /**
   * Writes org.w3c.dom.Document to OutputStream
   * 
   * @param doc
   * @throws IOException
   */
  public static OutputStream output(Document doc) throws IOException {
    OutputStream baos = new ByteArrayOutputStream();
    StreamResult streamResult = new StreamResult(baos);
    outputBase(doc, streamResult);
    return baos;
  }


  /**
   * Writes org.w3c.dom.Document to XML file
   * 
   * @param doc
   * @param xmlFileName
   * @throws CitationStyleManagerException
   * @throws IOException
   */
  public static void output(Document doc, String xmlFileName) throws IOException {

    FileOutputStream output = new FileOutputStream(xmlFileName);
    StreamResult streamResult = new StreamResult(output);
    outputBase(doc, streamResult);
  }

  /**
   * Writes org.w3c.dom.Document to OutputStream
   * 
   * @param doc
   * @throws IOException
   */
  public static void output(Document doc, OutputStream os) throws IOException {

    StreamResult streamResult = new StreamResult(os);
    outputBase(doc, streamResult);
  }

  /**
   * Base procedure for xml serialization
   * 
   * @param doc - is org.w3c.dom.Document
   * @param streamResult r
   * @throws IOException
   */
  public static void outputBase(Document doc, StreamResult streamResult) throws IOException {
    DOMSource domSource = new DOMSource(doc);
    TransformerFactory tf =
        TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
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

}
