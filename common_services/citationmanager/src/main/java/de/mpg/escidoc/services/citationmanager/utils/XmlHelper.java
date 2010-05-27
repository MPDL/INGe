/*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.citationmanager.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.saxon.event.SaxonOutputKeys;

import org.apache.log4j.Logger;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.topologi.schematron.SchtrnParams;
import com.topologi.schematron.SchtrnValidator;

import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;

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

    private static final Logger logger = Logger.getLogger(XmlHelper.class);
	
//    public final static String DATASOURCES_XML_SCHEMA_FILE = "escidoc/soap/item/0.3/item-list.xsd";
    public final static String CITATIONSTYLE_XML_SCHEMA_FILE = "citation-style.xsd";
	public final static String SCHEMATRON_DIRECTORY =  "Schematron/";
    public final static String SCHEMATRON_FILE = SCHEMATRON_DIRECTORY + "layout-element.sch";
    
    // List of CDATA elemetns
    public final static String CDATAElements =  
    	"valid-if max-count max-length variable " + 		// CitationStyle definition
    	"{http://purl.org/dc/terms/}bibliographicCitation" 	// Snippet output
    	;

	private static TransformerFactory tf = new net.sf.saxon.TransformerFactoryImpl();		

	public static HashMap<String, Templates> templCache = new HashMap<String, Templates>(20);
	public static HashMap<String, JasperReport> jasperCache = new HashMap<String, JasperReport>(20);
	
	
    private static XPath xpath = XPathFactory.newInstance().newXPath();
    
    /**
     * Builds new DocumentBuilder
     * @return DocumentBuilder
     * @throws CitationStyleManagerException
     */
    public static DocumentBuilder createDocumentBuilder() throws CitationStyleManagerException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setIgnoringComments(true);
        dbf.setNamespaceAware(false);

        try
        {
            return dbf.newDocumentBuilder();
        }
        catch (ParserConfigurationException e)
        {
            throw new CitationStyleManagerException("Failed to create a document builder factory", e);
        }
    }
    
    /**
     * Creates new empty org.w3c.dom.Document 
     * @return org.w3c.dom.Document
     * @throws CitationStyleManagerException
     */
    public static Document createDocument() throws CitationStyleManagerException
    {
    	return createDocumentBuilder().newDocument(); 
    }

    /**
     * Creates new org.w3c.dom.Document 
     * @param xml
     * @return org.w3c.dom.Document
     * @throws Exception
     */
    public static Document createDocument(String xml) throws Exception
    {
    	return createDocument(xml.getBytes("UTF-8")); 
    }
    
    /**
     * Creates new org.w3c.dom.Document 
     * @param xml
     * @return org.w3c.dom.Document
     * @throws Exception
     */
    public static Document createDocument(byte[] xml) throws Exception
    {
    	DocumentBuilder db = createDocumentBuilder();
    	return db.parse(
    			new ByteArrayInputStream(xml), "UTF-8" 
    	); 
    }
    
    /**
     * Creates new org.w3c.dom.Document with Traversing possibility 
     * @param is <code>InputSource</code>
     * @return org.w3c.dom.Document
     * @throws CitationStyleManagerException
     */    
	public static Document parseDocumentForTraversing(InputSource is) throws CitationStyleManagerException
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder parser;
		try 
		{
			parser = dbf.newDocumentBuilder();
		} 
		catch (ParserConfigurationException e) 
		{
			throw new CitationStyleManagerException("Cannot create DocumentBuilder:", e);
		}

		// Check for the traversal module
		DOMImplementation impl = parser.getDOMImplementation();
		if (!impl.hasFeature("traversal", "2.0")) 
		{
			throw new CitationStyleManagerException ("A DOM implementation that supports traversal is required.");
		}
		Document doc;
		try 
		{
			doc = parser.parse(is);
		} 
		catch (Exception e) 
		{
			throw new CitationStyleManagerException ("Cannot parse InputSource to w3c document:", e);
		}
		
		return doc;
	}	    
    
    /**
     * Base procedure for xml serialization
     * @param doc - is org.w3c.dom.Document
     * @param streamResult r
     * @throws IOException
     */
    public static void outputBase(Document doc, StreamResult streamResult) throws IOException
    {
        DOMSource domSource = new DOMSource(doc);
    	TransformerFactory tf = TransformerFactory.newInstance();
        try
        {
            Transformer serializer = tf.newTransformer();
            //Output properties
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            // TODO: saxon specific, to get rid of it later
            serializer.setOutputProperty(SaxonOutputKeys.INDENT_SPACES, "4");
            serializer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, CDATAElements); 
            serializer.transform(domSource, streamResult);
        }
        catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
    
    /**
     * Writes org.w3c.dom.Document to XML file 
     * @param doc
     * @param xmlFileName
     * @throws CitationStyleManagerException
     * @throws IOException
     */
    public static void output(Document doc, String xmlFileName) throws CitationStyleManagerException, IOException 
    {
    
        FileOutputStream output = new FileOutputStream( xmlFileName );
        StreamResult streamResult = new StreamResult(output);
        outputBase(doc, streamResult);
    }
    
    /**
     * Writes org.w3c.dom.Document to String 
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
     * Writes org.w3c.dom.Document to OutputStream 
     * @param doc
     * @throws IOException 
     */
    public static OutputStream output(Document doc) throws IOException 
    {
    	OutputStream baos = new ByteArrayOutputStream();
        StreamResult streamResult = new StreamResult(baos);
        outputBase(doc, streamResult);
    	return baos;
    }
    
    /**
     * Writes org.w3c.dom.Document to OutputStream 
     * @param doc
     * @throws IOException 
     */
    public static void output(Document doc, OutputStream os) throws IOException 
    {
    	
        StreamResult streamResult = new StreamResult(os);
        outputBase(doc, streamResult);
    }
    
    
    /**
	* Maintain prepared stylesheets in memory for reuse
	*/
   public static Templates tryTemplCache(String path) throws TransformerException, FileNotFoundException, CitationStyleManagerException 
   {
	   Utils.checkName(path, "Empty XSLT name.");

	   InputStream is = ResourceUtil.getResourceAsStream(path);
	   
        Templates x = templCache.get(path);
        if (x==null) {
            x = tf.newTemplates(new StreamSource(is));
            templCache.put(path, x);
        }
        return x;
    }    
    
   public static JasperReport tryJasperCache(String cs) throws CitationStyleManagerException, IOException, JRException   
   {
	   Utils.checkName(cs, "Empty style name.");
	   
	   JasperReport jr = XmlHelper.jasperCache.get(cs);
	   
	   if (jr==null) {

		   //get default JasperDesign 
		   
		   String path = ResourceUtil.getPathToCitationStyles() + "citation-style.jrxml";
		   JasperDesign jd = JRXmlLoader.load(ResourceUtil.getResourceAsStream(path));
			
		   //populate page header
		    
		   jd.setName(cs);
		   JRDesignStaticText st = (JRDesignStaticText)jd.getTitle().getElementByKey("staticText");
	        if ( st != null )
	        	st.setText("Citation Style: " + cs);
			
	        //compile to the JasperReport
			jr = JasperCompileManager.compileReport(jd);
		    
			XmlHelper.jasperCache.put(cs, jr);
	   }
	   
	   return jr;
   }   
   
    /**
     * XML Schema validation (JAVAX)  
     * @param schemaUrl is the XML Schema 
     * @param xmlDocumentUrl is URI to XML to be validated
     * @throws CitationStyleManagerException 
     */
    public String validateSchema(final String schemaUrl, final String xmlDocumentUrl)    
    {   
    	try
    	{
    		// System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
    		// "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
    		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();     
    		factory.setNamespaceAware(true); 
    		factory.setValidating(true); 
    		factory.setXIncludeAware(true);
    		factory.setAttribute(
    			"http://java.sun.com/xml/jaxp/properties/schemaLanguage",
    			"http://www.w3.org/2001/XMLSchema" 
    		);
//    		factory.setAttribute(
//    			"http://java.sun.com/xml/jaxp/properties/schemaSource",
//    			 ResourceUtil.getResourceAsFile(schemaUrl)
//    		);
    		
    		
    		DocumentBuilder builder = factory.newDocumentBuilder();
    		Validator handler = new Validator(); 
    		builder.setErrorHandler(handler);
    		
    		builder.parse(ResourceUtil.getResourceAsFile(xmlDocumentUrl));
    		
    		if( handler.validationError )
    		{
    			return 
    			(
					"XML Document has Error: " +
					handler.saxParseException.getLineNumber() + ":" +
					handler.saxParseException.getColumnNumber() + ", " +
					handler.saxParseException.getMessage()
    			);
    		}
    	} 
    	catch(java.io.IOException ioe)    
    	{
    			return ( "IO problems: " + ioe.getMessage() );
    	}
    	catch (SAXException e) 
    	{            
				return ( "SAX problems: " + e.getMessage() );
    	}        
    	catch (ParserConfigurationException e) 
    	{
				return ( "Wrong ParserConfiguration: " + e.getMessage() );
    	}
    	
    	return null;
    }
    
//    /**
//     * Validation of DataSource XML against the XML schema  
//     * @param xmlDocumentUrl is URI to XML to be validated 
//     * @throws CitationStyleManagerException 
//     * @throws IOException 
//     */

//    public void validateDataSourceXML(final String xmlDocumentUrl) throws CitationStyleManagerException, IOException{
//    	
//    	validateSchema(
//    			ResourceUtil.getUriToResources()
//    			+ ResourceUtil.SCHEMAS_DIRECTORY
//    			+ DATASOURCES_XML_SCHEMA_FILE
//    			, xmlDocumentUrl
//    	);
//    }

    
    /**
     * Validation of CitationStyle XML against 
     *  1) XML schema
     *  2) Schematron schema  
     * @param xmlDocumentUrl is URI to XML to be validated 
     * @throws IOException 
     */
    public String validateCitationStyleXML(final String cs) throws IOException
    {
    	String csFile = ResourceUtil.getPathToCitationStyleXML(cs);
    	logger.info("Document to be validated: " + csFile);
    	
    	// XML Schema validation
    	logger.info("XML Schema validation...");
    	String report =
    		validateSchema(
    			ResourceUtil.getUriToResources()
    			+ ResourceUtil.SCHEMAS_DIRECTORY
    			+ CITATIONSTYLE_XML_SCHEMA_FILE
    			, csFile
    		);
    	if ( report != null )
    	{
    			return report;
    	} 
    	logger.info("OK");
    	
    	// Schematron validation
    	logger.info( "Schematron validation..." );
        SchtrnValidator validator = new SchtrnValidator();
        validator.setEngineStylesheet(
    			ResourceUtil.getPathToSchemas()
    			+ SCHEMATRON_DIRECTORY 
    			+ "schematron-diagnose.xsl"
        );
        validator.setParams(new SchtrnParams());
        validator.setBaseXML(true);
        try {
			report = validator.validate(
					csFile,
					ResourceUtil.getPathToSchemas()
					+ SCHEMATRON_FILE
			);
		} 
        catch (TransformerConfigurationException e1) 
		{
        	return "Schematron validation problem (TransformerConfigurationException): " + e1.getMessage();
		} 
        catch (TransformerException e1) 
		{
        	return "Schematron validation problem (TransformerException): " + e1.getMessage();
		} 
		catch (Exception e1) 
		{
        	return "Schematron validation problem: " + e1.getMessage();
		} 
        if (report != null && report.contains("Report: "))
        {
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
    	public void error(SAXParseException exception) throws SAXException {
    		validationError = true;     
    		saxParseException = exception;     
    	} 
    	public void fatalError(SAXParseException exception) throws SAXException {
    		validationError = true;
    		saxParseException=exception;
    	}
    	public void warning(SAXParseException exception) throws SAXException { }     
    }

    
    public static Document getExplainDocument() throws CitationStyleManagerException
    {
    	Document doc = null;
    	try 
    	{
    		doc = parseDocumentForTraversing(
    				new InputSource(
    						ResourceUtil.getResourceAsStream(
    								ResourceUtil.getPathToSchemas() 
    								+ ResourceUtil.EXPLAIN_FILE
    						)  
    				)
    		);
    	} 
    	catch (Exception e) 
    	{
    		throw new CitationStyleManagerException("Cannot parse explain file", e);
    	}
    	return doc;
    }
    
 
	/* 
	 * Returns list of Citation Styles 
	 */
	public static String[] getListOfStyles() throws CitationStyleManagerException {

		NodeIterator ni = getFilteredNodes(new ExportFormatNodeFilter(), getExplainDocument());
		ArrayList<String> lof = new ArrayList<String>();
		Node n;
		while ((n = ni.nextNode()) != null)
		{
			lof.add(n.getTextContent());
		}		
		return lof.size()==0 ? null : lof.toArray(new String[lof.size()]);
	}

	

	/**
	 * Returns the list of the output formats (first element of the array) and mime-types 
	 * (second element) for the citation style <code>csName</code>.  
	 * @param csName is name of citation style
	 * @return list of the output formats  
	 * @throws CitationStyleManagerException
	 */
	public static List<String[]> getOutputFormatList(String csName) throws CitationStyleManagerException 
	{
		Utils.checkCondition( !Utils.checkVal(csName), "Empty name of the citation style");
		
		NodeIterator ni = getFilteredNodes(new OutputFormatNodeFilter(csName), getExplainDocument());

		ArrayList<String[]> ofal = new ArrayList<String[]>();
		Node n;
		while ((n = ni.nextNode()) != null)
		{
			Node fc = n.getFirstChild().getNextSibling();
			ofal.add(new String[] {
					fc.getTextContent(), //here should be name of output format 
					fc.getNextSibling()  //here is mime-type of the output format
					.getNextSibling()  
					.getTextContent() 
					});
		}
		return ofal;	    
	}
    
    

	/**
	 * Returns <code>org.w3c.dom.traversal.NodeIterator</code> for org.w3c.dom.Document traversing
	 *  
	 * @throws ExportManagerException
	 */
	private static NodeIterator getFilteredNodes(NodeFilter nodeFilter, Document doc) throws CitationStyleManagerException  
	{
		NodeIterator ni = ((DocumentTraversal) doc).createNodeIterator(
				doc.getDocumentElement(), 
				NodeFilter.SHOW_ELEMENT,
				nodeFilter, 
				true
		);	
		return ni;
	} 	
	
	/*****************/
	/** XPATH Utils **/
	/*****************/
    public static NodeList xpathNodeList(String expr, String xml) throws Exception
    {
    	return xpathNodeList(expr, createDocument(xml)); 
    }
    
    public static NodeList xpathNodeList(String expr, Document doc) throws Exception
    {
    	return (NodeList) xpath.evaluate(
    			expr, 
    			doc, 
    			XPathConstants.NODESET
    	);
    }    
    
    public static String xpathString(String expr, Document doc) throws Exception
    {
    	return (String) xpath.evaluate(
    				expr, 
    				doc, 
    				XPathConstants.STRING
    	);
    } 	
	
	
}

class OutputFormatNodeFilter implements NodeFilter {
	
	public static final String DC_NS = "http://purl.org/dc/elements/1.1/";
	public String cs;

	public OutputFormatNodeFilter(String cs)
	{
		super();
		this.cs = cs;
	}
	
	public short acceptNode(Node n) {
		Node parent = n.getParentNode();
		if 
		(
				"output-format".equals(n.getLocalName()) 
				&& parent != null 
				&& "export-format".equals(parent.getLocalName())
				&& cs.equals(
						parent
						.getChildNodes()
						.item(3)						
						.getTextContent()// name, style
				   ) 				
		)		
		{
//			System.out.println("Matched-->" + n.getLocalName()  
//			+ ";parent:" + parent.getLocalName()
//			+ ";n.getNamespaceURI():" + n.getNamespaceURI()
//			);
			return FILTER_ACCEPT;
		}
		return FILTER_SKIP;
	}
}


class ExportFormatNodeFilter implements NodeFilter {
	
	public static final String DC_NS = "http://purl.org/dc/elements/1.1/";
	
	public short acceptNode(Node n) {
		Node parent = n.getParentNode();
//		System.out.println("I am here!!!!-->" + n.getLocalName()  
//				+ ";parent:" + parent.getLocalName()
//				+ ";n.getNamespaceURI():" + n.getNamespaceURI()
//				);
		if 
		(
				"identifier".equals(n.getLocalName())
				&& DC_NS.equals(n.getNamespaceURI()) 
				&& parent != null 
				&& "export-format".equals(parent.getLocalName())
		)		
		{
//			System.out.println("Matched-->" + n.getLocalName()  
//					+ ";parent:" + parent.getLocalName()
//					+ ";n.getNamespaceURI():" + n.getNamespaceURI()
//					);
			return FILTER_ACCEPT;
		}
		return FILTER_SKIP;
	}
}
