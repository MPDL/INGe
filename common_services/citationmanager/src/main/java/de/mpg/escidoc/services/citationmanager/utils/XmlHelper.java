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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;

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
import net.sf.jasperreports.engine.JRReportFont;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.xml.JRXmlWriter;
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
import de.mpg.escidoc.services.citationmanager.data.FontStyle;
import de.mpg.escidoc.services.citationmanager.data.FontStylesCollection;

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
    public final static String FONT_STYLES_COLLECTION_FILE = "font-styles.xml";
    
    // List of CDATA elemetns
    public final static String CDATAElements =  
    	"valid-if max-count max-length variable " + 		// CitationStyle definition
    	"{http://purl.org/dc/terms/}bibliographicCitation" 	// Snippet output
    	;

	private static TransformerFactory tf = new net.sf.saxon.TransformerFactoryImpl();		

	public static HashMap<String, Templates> templCache = new HashMap<String, Templates>(20);
	public static HashMap<String, JasperReport> jasperCache = new HashMap<String, JasperReport>(20);
	
	//List of all available output formats
	public static HashMap<String, String[]> outputFormatsHash = null;
	
	
    private static XPath xpath = XPathFactory.newInstance().newXPath();

	private static HashMap<String, HashMap<String, String[]>> citationStylesHash = null;
	
	// FontStyleCollectiona Hash
	public static HashMap<String, FontStylesCollection> fsc = new HashMap<String, FontStylesCollection>();
	
    
    /**
     * Builds new DocumentBuilder
     * @return DocumentBuilder
     * @throws CitationStyleManagerException
     */
    public static DocumentBuilder createDocumentBuilder()
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
            throw new RuntimeException("Failed to create a document builder factory", e);
        }
    }
    
    /**
     * Creates new empty org.w3c.dom.Document 
     * @return org.w3c.dom.Document
     * @throws CitationStyleManagerException
     */
    public static Document createDocument()
    {
    	return createDocumentBuilder().newDocument(); 
    }

    /**
     * Creates new org.w3c.dom.Document 
     * @param xml
     * @return org.w3c.dom.Document
     * @throws RuntimeException
     */
    public static Document createDocument(String xml)
    {
    	try {
			return createDocument(xml.getBytes("UTF-8"));
		} catch (Exception e) {
            throw new RuntimeException("Cannot create Document", e);
		} 
    }
    
    /**
     * Creates new org.w3c.dom.Document 
     * @param xml
     * @return org.w3c.dom.Document
     * @throws CitationStyleManagerException 
     * @throws IOException 
     * @throws SAXException 
     * @throws Exception
     */
    public static Document createDocument(byte[] xml) 
    {
    	DocumentBuilder db = createDocumentBuilder();
    	try {
			return db.parse(
					new ByteArrayInputStream(xml), "UTF-8" 
			);
		}
    	catch (Exception e) 
		{
            throw new RuntimeException("Cannot create Document", e);
		} 

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
		   setPageHeader(jd, cs);
		   
		   //set default Report Style
		   setDefaultReportStyle(jd, cs);
	        
	       //compile to the JasperReport
		   jr = JasperCompileManager.compileReport(jd);
		    
		   XmlHelper.jasperCache.put(cs, jr);
	   }
	   
	   return jr;
   }   
   
    /**
     * Render report default style with values, taken from
     * font styles collection.
     * @param jasperDesign
     */
    private static void setDefaultReportStyle(JasperDesign jasperDesign, String cs) 
    {
        
    	JRStyle jrs = jasperDesign.getDefaultStyle();
    	
    	FontStyle dfs = loadFontStylesCollection(cs).getDefaultFontStyle();
        jrs.setFontName(dfs.getFontName());
        jrs.setFontSize(dfs.getFontSize());
        
        jrs.setForecolor(dfs.getForeColorAwt());
        jrs.setBackcolor(dfs.getBackColorAwt());
        jrs.setBold(dfs.getIsBold());
        jrs.setItalic(dfs.getIsItalic());
        jrs.setUnderline(dfs.getIsUnderline());
        jrs.setStrikeThrough(dfs.getIsStrikeThrough());
        jrs.setPdfFontName(dfs.getPdfFontName());
        jrs.setPdfEncoding(dfs.getPdfEncoding());
        jrs.setPdfEmbedded(true);
	
    }

    /**
     * Set report header 
	 * @param jasperDesign
	 * @param cs
     */
	private static void setPageHeader(JasperDesign jasperDesign, String cs) 
    {
	   jasperDesign.setName(cs);
	   JRDesignStaticText st = (JRDesignStaticText)jasperDesign.getTitle().getElementByKey("staticText");
        if ( st != null )
        	st.setText("Citation Style: " + cs);
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
	 * Load Default FontStylesCollection, singleton
	 * 1) if no citation style is given, return default {@link FontStylesCollection}
	 * 2) if citation style is given, check citation style directory. If there is 
	 *    a citation style specific {@link FontStylesCollection} in the citation style directory 
	 *    FontStylesCollection, get it; if not - get default FontStylesCollection    
	 * @param cs - citation style
	 * @return {@link FontStylesCollection}
	 * @throws CitationStyleManagerException
	 */
	public static FontStylesCollection loadFontStylesCollection(String cs)
	{
		// get default FontStyleCollection from __Default__ element for empty cs
		if (cs == null || "".equals(cs.trim()))
			return loadFontStylesCollection("__Default__");
		
		if (fsc.containsKey(cs))
			return fsc.get(cs);
		
		try 
		{
			// load __Default__ collection
			if ("__Default__".equalsIgnoreCase(cs))
			{
				fsc.put(cs, FontStylesCollection.loadFromXml(
					ResourceUtil.getPathToCitationStyles() + FONT_STYLES_COLLECTION_FILE
				));
			} 
			else
			{
				String path = ResourceUtil.getPathToCitationStyle(cs) + FONT_STYLES_COLLECTION_FILE;
				// get specific FontStyleCollection for citation style if exists 
				if (new File(path).exists())
				{
					fsc.put(cs, FontStylesCollection.loadFromXml(path));
				}
				// otherwise: get __Default_ one 
				else
				{
					fsc.put(cs, loadFontStylesCollection());
				}
			}
			return fsc.get(cs);
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(
					"Cannot loadFontStylesCollection: ", e);
		}
	}
	
    /**
	 * Load Default FontStylesCollection
	 * @return {@link FontStylesCollection}
	 * @throws CitationStyleManagerException
	 */	
	public static FontStylesCollection loadFontStylesCollection()
	{
		return loadFontStylesCollection(null);
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
	public static String[] getListOfStyles()
	{

		Object[] oa =  getCitationStylesHash().keySet().toArray(); 
		return Arrays.copyOf(oa, oa.length, String[].class);
		
	}

    /**
     * Checks whether the csName is in the list of Citation Styles
     * @param cs - Citation Style name 
     * @return <code>true</code> or <code>false</code>
     * @throws CitationStyleManagerException
     */
    public static boolean isCitationStyle(String cs) throws CitationStyleManagerException 
	{
		Utils.checkCondition( !Utils.checkVal(cs), "Empty name of the citation style");
		 
		return getCitationStylesHash().containsKey(cs);
		
	}    
	
    /**
     * Checks Output Format (<code>of</code>) availability for  Citation Style (<code>cs</code>) 
     * @param cs - Citation Style name
     * @param of - Output Format name
     * @return <code>true</code> or <code>false</code>
     * @throws CitationStyleManagerException
     */
    public static boolean citationStyleHasOutputFormat(String cs, String of) throws CitationStyleManagerException 
    {
		Utils.checkCondition( !Utils.checkVal(cs), "Empty name of the citation style");
		
		Utils.checkCondition( !Utils.checkVal(of), "Empty name of the output format");
		
    	return getCitationStylesHash().get(cs).containsKey(of);
    }
	

	/**
	 * Returns the list of the output formats (first element of the array)  
	 * for the citation style <code>csName</code>.  
	 * @param csName is name of citation style
	 * @return list of the output formats  
	 * @throws CitationStyleManagerException 
	 * @throws Exception 
	 * @throws IOException 
	 */
	public static String[] getOutputFormatsArray(String csName) throws CitationStyleManagerException 
	{
		Utils.checkCondition( !Utils.checkVal(csName), "Empty name of the citation style");
		Object[] oa =  getCitationStylesHash().get(csName).keySet().toArray(); 
		return Arrays.copyOf(oa, oa.length, String[].class);
	}

	/**
	 * Returns the mime-type for output format of the citation style
	 * @param csName is name of citation style
	 * @param outFormat is the output format 
	 * @return mime-type, or <code>null</code>, if no <code>mime-type</code> has been found    
	 * @throws CitationStyleManagerException if no <code>csName</code> or <code>outFormat</code> are defined 
	 */ 
	public static String getMimeType(String csName, String outFormat) throws CitationStyleManagerException{
		
		Utils.checkCondition( !Utils.checkVal(csName), "Empty name of the citation style");
		
		Utils.checkCondition( !Utils.checkVal(outFormat), "Empty name of the output format");
		
		HashMap<String, HashMap<String, String[]>> csh = getCitationStylesHash();
		
		Utils.checkCondition( !csh.containsKey(csName), "No citation style is defined: " + csName);
		
		Utils.checkCondition ( !csh.get(csName).containsKey(outFormat), "No output Format:  " + outFormat + " for citation style: " + csName + " is defined" );
		
    	return  csh.get(csName).get(outFormat)[0];
	}
        	
	
	 /**
     * Returns the file extension according to format name.
	 * @throws CitationStyleManagerException 
     */
    public static String getExtensionByName(String outputFormat) throws CitationStyleManagerException
    {
    	Utils.checkCondition( !Utils.checkVal(outputFormat), "Empty output format name");
    	
    	outputFormat = outputFormat.trim();
    	
    	HashMap <String, String[]> of = getOutputFormatsHash(); 
    	
    	return of.containsKey(outputFormat) ? of.get(outputFormat)[1] : of.get("pdf")[1];   
    }
	
	
	
	/**
	 * Get outputFormatsHash, where
	 * keys: names of output format 
	 * values: array of {mime-type, file-extension} for the output format
	 * @return outputFormatsHash
	 */
	public static HashMap<String, String[]> getOutputFormatsHash()    
	{
		
		if (outputFormatsHash == null) 
		{
			NodeList nl = null;
			try
			{
				nl = xpathNodeList(
						"/export-formats/output-formats/output-format",
						ResourceUtil.getResourceAsString(
								ResourceUtil.getPathToSchemas() 
							 	+ "explain-styles_new.xml"
						)
				);
			}
			catch (Exception e) 
			{
				throw new RuntimeException("Cannot process expain file:", e);
			}

				outputFormatsHash = new HashMap<String, String[]>();
//				logger.info("items:" + nl.getLength());
				for (int i = 0; i < nl.getLength(); i++)
				{
					Node n = nl.item(i);
					NodeList nll = n.getChildNodes();
					
					
					String name = null, format = null, ext = null;
					for ( int ii=0; ii < nll.getLength(); ii++)
					{	
						Node nn = nll.item(ii);
						
						if (nn.getNodeType() == Node.ELEMENT_NODE)
						{
							String nodeName = nn.getNodeName();
							if ("dc:title".equals(nodeName))
							{
								name = nn.getTextContent();
							}
							if ("dc:format".equals(nodeName))
							{
								format = nn.getTextContent();
							}
							if ("file-ext".equals(nodeName))
							{
								ext = nn.getTextContent();
							}
						}
					}
					outputFormatsHash.put(
							name, //key: output format key
							new String[]
							{
								format,//mime-type 
								ext //extension
							}
							
					);
				}
				return outputFormatsHash;
			
		}
		else
		{
			return outputFormatsHash;
		}

	}
	
	/**
	 * Return citationStylesHash 
	 * keys: citation style id
	 * value: hash of supported output formats
	 * @return citationStylesHash
	 */	
	public static HashMap<String, HashMap<String, String[]>> getCitationStylesHash() 
	{
		
		if (citationStylesHash == null) 
		{
			NodeList nl;
			try {
				nl = xpathNodeList(
						"/export-formats/export-format/identifier",
						ResourceUtil.getResourceAsString(
								ResourceUtil.getPathToSchemas() 
							 	+ "explain-styles_new.xml"
						)
				);
			}
			catch (Exception e) 
			{
				throw new RuntimeException("Cannot process expain file:", e);
			}
			citationStylesHash = new HashMap<String, HashMap<String, String[]>>();
			//for all export formats take identifiers
			for (int i = 0; i < nl.getLength(); i++)
			{
				Node n = nl.item(i);
				
				String exportFormat =  n.getTextContent();
				
				//find output formats
				NodeList exportFormatChildren =  n.getParentNode().getChildNodes();
				
				
				//find output formats element
				Node outputFormatsNode = findNode(exportFormatChildren, "output-formats");
				
				
				//if no export format identifier found, continue for
				HashMap<String, String[]> formatsHash = new HashMap<String, String[]>();
				if ( !(outputFormatsNode == null || outputFormatsNode.getTextContent() == null) )
				{
					String refs = outputFormatsNode.getAttributes().getNamedItem("refs").getTextContent();
					
					for (String outputFormat: refs.split("\\s+"))
					{
						//check outputFormat availability
						if ( getOutputFormatsHash().containsKey(outputFormat) )
							formatsHash.put(outputFormat, getOutputFormatsHash().get(outputFormat));
					}
				}
				
				citationStylesHash.put(exportFormat, formatsHash);
				
			}
			return citationStylesHash;
			
		}
		else
		{
			return citationStylesHash;
		}
		
	}
	
	
	/**
	 * Search for the first <code>Node</code> with the nodeName().equals(nodeName)
	 * in the nodeList   
	 * @param nodeList
	 * @param nodeName
	 * @return Node or null if no Node has been found 
	 */
	private static Node findNode(NodeList nodeList, String nodeName)
	{
		Node curNode;
		for (int i=0; i < nodeList.getLength(); i++)
		{
			curNode = nodeList.item(i);
			if ( curNode.getNodeType() == Node.ELEMENT_NODE && nodeName.equals(curNode.getNodeName()) )
			{
				return curNode;
			}
		}
		return null;
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
	public static String xpathString(String expr, String xml)
    {
    	return xpathString(expr, createDocument(xml)); 
    }
    
    public static String xpathString(String expr, Document doc)
    {
    	try {
			return (String) xpath.evaluate(
						expr, 
						doc, 
						XPathConstants.STRING
					);
		} 
    	catch (Exception e) 
		{
            throw new RuntimeException("Cannot evaluate XPath:", e);
		} 

    }
    
    public static Double xpathNumber(String expr, String xml)
    {
    	return xpathNumber(expr, createDocument(xml)); 
    }
    
    public static Double xpathNumber(String expr, Document doc)
    {
    	try {
			return (Double) xpath.evaluate(
					expr, 
					doc,
				
					XPathConstants.NUMBER
			);
    	}
		catch (Exception e) 
		{
            throw new RuntimeException("Cannot evaluate XPath:", e);
		} 
    }
    
    public static NodeList xpathNodeList(String expr, String xml)
    {
    	return xpathNodeList(expr, createDocument(xml)); 
    }
    
    public static NodeList xpathNodeList(String expr, Document doc) 
    {
    	try {
			return (NodeList) xpath.evaluate(
					expr, 
					doc, 
					XPathConstants.NODESET
			);
    	} 
    	catch (Exception e) 
		{
            throw new RuntimeException("Cannot evaluate XPath:", e);
		} 
    }
    
    public static Node xpathNode(String expr, String xml)
    {
    	return xpathNode(expr, createDocument(xml)); 
    }
    
    public static Node xpathNode(String expr, Document doc)
    {
    	try {
			return (Node) xpath.evaluate(
					expr, 
					doc, 
					XPathConstants.NODE
			);
    	}
    	catch (Exception e) 
		{
            throw new RuntimeException("Cannot evaluate XPath:", e);
		} 
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
