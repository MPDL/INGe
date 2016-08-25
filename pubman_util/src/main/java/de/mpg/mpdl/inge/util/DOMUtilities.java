package de.mpg.mpdl.inge.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;



public class DOMUtilities {

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

}
