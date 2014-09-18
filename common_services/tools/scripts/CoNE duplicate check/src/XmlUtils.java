import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sun.java2d.pipe.BufferedBufImgOps;


public class XmlUtils {
	
	private final static String XML_FILE_PATH = "E:\\tmp\\rdf(2).xml";
	private final static String XPATH_EXPRESSION_TITLE = "/RDF/Description/title/text()";
	private final static String XPATH_EXPRESSION_ALTERNATIVE_TITLE = "/RDF/Description/alternative/text()";
	
	public static List<String> getUsernameFromConeRdf () {
		System.out.println("-------------------\nStarted parsing XML\n-------------------");
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		List<String> nameList = new ArrayList<String>();
		try {
		    builder = builderFactory.newDocumentBuilder();
			Document xmlDocument = builder.parse(new FileInputStream(XML_FILE_PATH));
			XPath xPath = XPathFactory.newInstance().newXPath();
			NamespaceContext namespaceContext = new NamespaceContext() {
				
				@Override
				public Iterator getPrefixes(String namespaceURI) {
					return null;
				}
				
				@Override
				public String getPrefix(String namespaceURI) {
					return null;
				}
				
				@Override
				public String getNamespaceURI(String prefix) {
					switch (prefix) {
					case "rdf": return "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
					case "dc": return "http://purl.org/dc/elements/1.1/";
					case "foaf": return "http://xmlns.com/foaf/0.1/";
					case "escidoc": return "http://purl.org/escidoc/metadata/terms/0.1/";
					case "dcterms": return "http://purl.org/dc/terms/";
					default: return XMLConstants.NULL_NS_URI;
					}
				}
			};
			xPath.setNamespaceContext(namespaceContext);
			NodeList nodeList = (NodeList) xPath.compile(XPATH_EXPRESSION_TITLE).evaluate(xmlDocument, XPathConstants.NODESET);
			for (int i = 0; i < nodeList.getLength(); i++) {
				nameList.add(nodeList.item(i).getNodeValue());
				System.out.println("Complete-name[" + i + "]: " + nodeList.item(i).getNodeValue());
			}
			nodeList = (NodeList) xPath.compile(XPATH_EXPRESSION_ALTERNATIVE_TITLE).evaluate(xmlDocument, XPathConstants.NODESET);
			for (int i = 0; i < nodeList.getLength(); i++) {
				nameList.add(nodeList.item(i).getNodeValue());
				System.out.println("Alternative-name[" + i + "]: " + nodeList.item(i).getNodeValue());
			}
			Collections.sort(nameList);
			System.out.println("Sorted List");
			for (String name : nameList) {
				System.out.println(name);
			}
		} catch (ParserConfigurationException e) {
			System.out.println("Error reading XML-Document [" + XmlUtils.class.getEnclosingMethod() + "]");
			e.printStackTrace(); 
			return null;
		} catch (SAXException e) {
			System.out.println("Error reading XML-Document [" + XmlUtils.class.getEnclosingMethod() + "]");
		    e.printStackTrace();
		    return null;
		} catch (IOException e) {
			System.out.println("Error getting Documents Content [" + XmlUtils.class.getEnclosingMethod() + "]");
		    e.printStackTrace();
		    return null;
		} catch (XPathExpressionException e) {
			System.out.println("Error evaluating xPath-Expression [" + XmlUtils.class.getEnclosingMethod() + "]");
			e.printStackTrace();
			return null;
		}
		return nameList;
	}
}
