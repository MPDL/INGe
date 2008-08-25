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

package de.mpg.escidoc.services.structuredexportmanager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Set;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.OutputKeys;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Structured Export Manager. 
 * Converts PubMan item-list to one of the structured formats.   
 *
 * @author Vlad Makarenko (initial creation)
 * @author $Author: vdm $ (last modification)
 * @version $Revision: 67 $ $LastChangedDate: 2007-12-11 12:39:50 +0100 (Tue, 11 Dec 2007) $
 *
 */ 

public class StructuredExport implements StructuredExportHandler {


	private final static Logger logger = Logger.getLogger(StructuredExport.class);
	
	private final static String PATH_TO_SCHEMAS = "schemas/";
	private final static String PATH_TO_RESOURCES = "resources/";
	private final static String EXPLAIN_FILE = "explain-structured-formats.xml";
	
	public StructuredExport()
	{
//		 Use Saxon for XPath2.0 support
        System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
	}
	


	/* (
	 * Takes PubMan item-list and converts it to specified exportFormat. Uses XSLT.   
	 * @see de.mpg.escidoc.services.endnotemanager.StructuredExportHandler#getOutputString(java.lang.String, java.lang.String)
	 */
	
	public byte[] getOutput(String itemList, String exportFormat)
			throws StructuredExportXSLTNotFoundException,
			StructuredExportManagerException 
	{
			// check itemList
			if (itemList == null) 
				throw new StructuredExportManagerException("Item list is null");
			
			//check format
			HashMap<String, String> fh = getFormatsHash();
			if ( !fh.containsKey(exportFormat) ) 
				throw new StructuredExportManagerException("Format: " + exportFormat + " is not supported");
			
			// xml source
			javax.xml.transform.Source xmlSource =
				new javax.xml.transform.stream.StreamSource(new StringReader(itemList));
			
			// result
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			StringWriter sw = new StringWriter();
			javax.xml.transform.Result result =
//				new javax.xml.transform.stream.StreamResult(sw);
			new javax.xml.transform.stream.StreamResult(baos);

			// create an instance of TransformerFactory
			javax.xml.transform.TransformerFactory transFact =
				javax.xml.transform.TransformerFactory.newInstance(  );

			//set URIResolver for xsl:include or xsl:import
			transFact.setURIResolver(
					new URIResolver(){
						public Source resolve(String href, String base)
								throws TransformerException {
							logger.info("href: " + href);
							logger.info("base: " + base);
							InputStream is;
							try {
								is = getResource(PATH_TO_SCHEMAS + href);
							} catch (IOException e) {
								throw new TransformerException(e);
							} 
							return new StreamSource(is);
						}
					}
			);
			
			String xsltFileName;
			try 
			{
				
				
				xsltFileName = PATH_TO_SCHEMAS + fh.get(exportFormat);
				// xslt source
				javax.xml.transform.Source xsltSource =
					new javax.xml.transform.stream.StreamSource(
							getResource(xsltFileName)
				);
					
				
				 javax.xml.transform.Transformer trans = 
					 transFact.newTransformer(xsltSource);
					
				logger.debug("Transformer:" + trans);
				 
				trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//				logger.info("ENCODING:" + trans.getOutputProperty(OutputKeys.ENCODING)) ;
				
				trans.transform(xmlSource, result);
				
//				return sw.toString().getBytes();
				return baos.toByteArray();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				throw new StructuredExportXSLTNotFoundException("fi" + e);
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				throw new StructuredExportManagerException(e);
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				throw new StructuredExportManagerException(e);
			}
	}	

	/**
	 * Gets resources according to an execution environment
	 * @param fileName
	 * @return InputStream of resource
	 * @throws IOException
	 */
	private InputStream getResource(final String fileName) throws IOException
	{
		String path = PATH_TO_RESOURCES + fileName;
		InputStream fileIn = getClass()
								.getClassLoader()
								.getResourceAsStream(path);

		if (fileIn == null)
		{
			fileIn = new FileInputStream(path);
		}
		return fileIn;
	}


	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.exportmanager.StructuredExportHandler#explainFormats()
	 */
	public String explainFormats() throws StructuredExportManagerException 
	{
        BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(getResource(EXPLAIN_FILE), "UTF-8"));
		} catch (Exception e) {
			throw new StructuredExportManagerException(e); 
		}
        String line = null;
        String result = "";
        try {
			while ((line = br.readLine()) != null)
			{
			    result += line + "\n";
			}
		} catch (IOException e) {
			throw new StructuredExportManagerException(e); 
		}
        return result;
	}


	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.exportmanager.StructuredExportHandler#getFormatsList()
	 */
	public String[] getFormatsList() throws StructuredExportManagerException
	{
		Set<String> s = getFormatsHash().keySet(); 
		String[] fl = new String[ s.size() ];
		fl = (String[]) s.toArray( fl );
		return fl;
	}
	 
	
	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.structuredexportmanager.StructuredExportHandler#isStructuredFormat(java.lang.String)
	 */
	public boolean isStructuredFormat(String exportFormat)
	throws StructuredExportManagerException 
	{
		if ( exportFormat == null || exportFormat.trim().equals("") )
		{
			throw new StructuredExportManagerException("Empty export format");
		}
		return getFormatsHash().containsKey(exportFormat);
	}	

	
	
	/**
	 * Generates HashMap of export formats where key is export format id
	 * and value is the name of XSLT file of the export implementation 
	 * @return 
	 * @throws StructuredExportManagerException
	 */
	public HashMap<String, String> getFormatsHash() throws StructuredExportManagerException
	{
		Document doc;
		try {
			doc = createDocumentBuilder().parse(
						new InputSource(new StringReader(explainFormats()))
			);
		} catch (Exception e) {
			throw new StructuredExportManagerException(e); 
		}		
		Element root = doc.getDocumentElement( );
		
		//get all export-format elements
		NodeList formatElements = root.getElementsByTagName("export-format");
		
		HashMap<String, String> fh = new HashMap<String, String>();
		
		for (int i = 0; i < formatElements.getLength( ); i++)
		{ 
			Element n = (Element)formatElements.item(i);
			//populate key/value pars
			fh.put(
					n.getElementsByTagName("dc:identifier").item(0).getTextContent(),
					n.getAttribute("xslt")
			);
		}
		
		return fh;
	}
	
	
    /**
     * Builds new DocumentBuilder
     * @return DocumentBuilder
     * @throws ParserConfigurationException 
     */
    public static DocumentBuilder createDocumentBuilder() throws ParserConfigurationException  
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setIgnoringComments(true);
        dbf.setNamespaceAware(false);

        return dbf.newDocumentBuilder();
    }



}	