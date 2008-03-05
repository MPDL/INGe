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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.citationmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.topologi.schematron.SchtrnParams;
import com.topologi.schematron.SchtrnValidator;

/**
*
* XML processing helper   
*
* @author $Author: vdm $ (last modification)
* @version $Revision: 151 $ $LastChangedDate: 2007-11-15 18:04:05 +0100 (Thu, 15 Nov 2007) $
*
*/

public class XmlHelper {

    private static final Logger logger = Logger.getLogger(XmlHelper.class);
	
    public final static String DATASOURCES_XML_SCHEMA_FILE = "escidoc/soap/item/0.3/item-list.xsd";
    public final static String CITATIONSTYLE_XML_SCHEMA_FILE = "citation-style.xsd";
	public final static String SCHEMATRON_DIRECTORY =  "Schematron/";
    public final static String SCHEMATRON_FILE = SCHEMATRON_DIRECTORY + "layout-element.sch";
    
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
     * Creates new org.w3c.dom.Document 
     * @return org.w3c.dom.Document
     * @throws CitationStyleManagerException
     */
    public static Document createDocument() throws CitationStyleManagerException
    {
    	return createDocumentBuilder().newDocument(); 
    }
    
    /**
     * Writes org.w3c.dom.Document to XML file 
     * @param doc
     * @param xmlFileName
     * @throws CitationStyleManagerException
     * @throws IOException
     */
    public static void output(Document doc, String xmlFileName) throws CitationStyleManagerException, IOException {
    
    	// TODO: to get rid of org.apache.xml.serialize.* 
        OutputFormat format = new OutputFormat(doc);
        format.setIndenting(true);
        format.setIndent(2);
        format.setCDataElements(Parameters.CDATAElements);
        format.setOmitComments(false);
        FileOutputStream output = new FileOutputStream( xmlFileName );
        XMLSerializer serializer = new XMLSerializer(output, format);
        serializer.serialize(doc);
    	
    }
    
    /**
     * Writes org.w3c.dom.Document to String 
     * @param doc
     * @throws IOException
     */
    public static String outputString(Document doc) throws IOException {
    	
    	// TODO: to get rid of org.apache.xml.serialize.* 
    	OutputFormat format = new OutputFormat(doc);
    	format.setIndenting(true);
    	format.setIndent(2);
    	format.setCDataElements(Parameters.CDATAElements);
        format.setOmitComments(false);
    	StringWriter output = new StringWriter();
    	XMLSerializer serializer = new XMLSerializer(output, format);
    	serializer.serialize(doc);
    	
    	return output.toString();
    	
    }
    
    /**
     * XML Schema validation (JAVAX)  
     * @param schemaUrl is the XML Schema 
     * @param xmlDocumentUrl is URI to XML to be validated
     * @throws CitationStyleManagerException 
     */
    public void validateSchema(final String schemaUrl, final String xmlDocumentUrl) throws CitationStyleManagerException   {    
    	try{
    		System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
    		"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
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
    		
    		if(handler.validationError == true) 
    			throw new CitationStyleManagerException (  
    					"XML Document has Error:" +
    					handler.validationError + " "+
    					handler.saxParseException.getMessage()
    			);
    	} catch(java.io.IOException ioe)    {
    		logger.info("xmlDocumentUrl :" + xmlDocumentUrl);
			throw new CitationStyleManagerException (  
					"IOException ", ioe
			);         
    	}
    	catch (SAXException e) {            
			throw new CitationStyleManagerException (  
					"SAXException" + e.getMessage()
			);         
    	}        
    	catch (ParserConfigurationException e) {
			throw new CitationStyleManagerException (  
					"ParserConfigurationException " + e.getMessage()
			);		
    	}       
    }
    
    /**
     * Validation of DataSource XML against the XML schema  
     * @param xmlDocumentUrl is URI to XML to be validated 
     * @throws CitationStyleManagerException 
     * @throws IOException 
     */

    public void validateDataSourceXML(final String xmlDocumentUrl) throws CitationStyleManagerException, IOException{
    	
    	validateSchema(
    			ResourceUtil.getUriToResources()
    			+ ResourceUtil.SCHEMAS_DIRECTORY
    			+ DATASOURCES_XML_SCHEMA_FILE
    			, xmlDocumentUrl
    	);
    }

    
    /**
     * Validation of CitationStyle XML against 
     *  1) XML schema
     *  2) Schematron schema  
     * @param xmlDocumentUrl is URI to XML to be validated 
     * @throws CitationStyleManagerException
     * @throws IOException 
     */
    public void validateCitationStyleXML(final String xmlDocumentUrl) throws CitationStyleManagerException, IOException{
    	
    	// XML Schema validation
    	logger.info("XML Schema validation...");
    	validateSchema(
    			ResourceUtil.getUriToResources()
    			+ ResourceUtil.SCHEMAS_DIRECTORY
    			+ CITATIONSTYLE_XML_SCHEMA_FILE
    			, xmlDocumentUrl
    	);
    	logger.info("OK"); 
    	
    	// Schematron validation
    	logger.info("Schematron validation..." + xmlDocumentUrl);
        SchtrnValidator validator = new SchtrnValidator();
        validator.setEngineStylesheet(
    			ResourceUtil.getPathToSchemas()
    			+ SCHEMATRON_DIRECTORY 
    			+ "schematron-diagnose.xsl"
        );
        validator.setParams(new SchtrnParams());
        validator.setBaseXML(true);
        try {
        	String info = validator.validate(
    				xmlDocumentUrl,
        			ResourceUtil.getPathToSchemas()
        			+ SCHEMATRON_FILE
            ); 
            if (info != null && info.contains("Report: "))
            	throw new CitationStyleManagerException(info);
        	logger.info("OK");
        } catch (Exception e) {
        	logger.info(e.getMessage());
        	throw new CitationStyleManagerException(e);
        }
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

 
	
}
