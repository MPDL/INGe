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

package de.mpg.escidoc.services.citationmanager.xslt;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.jasperreports.engine.JRException;

import org.apache.log4j.Logger;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.mpg.escidoc.services.citationmanager.CitationStyleHandler;
import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.citationmanager.ProcessCitationStyles;
import de.mpg.escidoc.services.citationmanager.ProcessCitationStyles.OutFormats;
import de.mpg.escidoc.services.citationmanager.utils.ResourceUtil;
import de.mpg.escidoc.services.citationmanager.utils.Utils;
import de.mpg.escidoc.services.citationmanager.utils.XmlHelper;

/**
*
* Citation Style Executor Engine, XSLT-centric    
*
* @author Initial creation: vmakarenko 
* @author $Author$ (last modification)
* @version $Revision$ $LastChangedDate$
*
*/

public class CitationStyleExecutor implements CitationStyleHandler{

    private static final String PARENT_ELEMENT_NAME = "content-model-specific";
    private static final String SNIPPET_ELEMENT_NAME = "dcterms:bibliographicCitation";
    private static final String SNIPPET_NS = "http://purl.org/dc/terms/";
	
	
	private static final Logger logger = Logger.getLogger(CitationStyleExecutor.class);	
	/**
	 * @param args
	 */
	private static ProcessCitationStyles pcs = new ProcessCitationStyles();
	

	public String explainStyles() throws IllegalArgumentException, IOException,
			CitationStyleManagerException {
		
		return pcs.explainStyles();
	}

	public String getMimeType(String cs, String ouf)
			throws CitationStyleManagerException {
		// TODO Auto-generated method stub
		return pcs.getMimeType(cs, ouf);
	}

	public byte[] getOutput(String cs, String ouputFormat,
			String itemList) throws IOException, JRException,
			CitationStyleManagerException  {

		Utils.checkCondition( !Utils.checkVal(ouputFormat), "Output format is not defined");
		
		Utils.checkCondition( !"snippet".equals(ouputFormat), "The only snippet format is supported for the moment");
		
		Utils.checkCondition( !Utils.checkVal(itemList), "Empty item-list");
		
		int slashPos = ouputFormat.indexOf( "/" );
		String ouf = slashPos == -1 ? ouputFormat : ouputFormat.substring( slashPos + 1 );
		
		// TODO: mapping should be taken from explain-styles.xml 
		if (ouf.equals("vnd.oasis.opendocument.text")) 
			ouf = "odt";
		 
		try {
			OutFormats.valueOf(ouf);
		} catch (Exception e) {
			throw new CitationStyleManagerException( "Output format: " + ouputFormat + " is not supported" );
		}		
		
		StringWriter result = new StringWriter();
		
		TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl();		
		InputStream stylesheet = ResourceUtil.getResourceAsStream(
				ResourceUtil.CITATIONSTYLES_DIRECTORY 
        		+ "/" + cs  
        		+ "/CitationStyle.xsl"
		);

		Document itemListDoc; 
		try 
		{
			Transformer transformer = factory.newTransformer(new StreamSource(stylesheet));
			transformer.transform(new StreamSource(new StringReader(itemList)), new StreamResult(result));

			itemListDoc = XmlHelper.createDocument(itemList);
			
			logger.info(result.toString());
						
			NodeList itemListNodes;
			Object [] snipArr = extractSnippets(result.toString());
			try {
				itemListNodes = XmlHelper.xpathNodeList( "/item-list/item/properties/content-model-specific", itemListDoc);
				
				for (int i = 0; i < itemListNodes.getLength(); i++) 		
				{
					
					Element snippetElement = itemListDoc.createElement(SNIPPET_ELEMENT_NAME);
					snippetElement.setAttribute("xmlns:dcterms", SNIPPET_NS);
					
					CDATASection snippetCDATASection = itemListDoc.createCDATASection(
							(String)snipArr[i]
					);
					snippetElement.appendChild(snippetCDATASection);
					itemListNodes.item(i).appendChild(snippetElement);
				}
			} 
			catch (Exception e) 
			{
				throw new RuntimeException("Cannot insert snippet into item-list:", e);
			}		
			
		}	
		catch (Exception e) 
		{
	            throw new RuntimeException("Error by transformation:", e);
	    }
		//
//		return result.toString().getBytes("UTF-8");
		return XmlHelper.outputString(itemListDoc).getBytes("UTF-8");
		
	}
	

	public String[] getOutputFormats(String cs)
			throws CitationStyleManagerException {
		return pcs.getOutputFormats(cs);
	}

	public String[] getStyles() throws CitationStyleManagerException {
		// TODO Auto-generated method stub
		return pcs.getStyles();
	}

	public boolean isCitationStyle(String cs)
			throws CitationStyleManagerException {
		// TODO Auto-generated method stub
		return pcs.isCitationStyle(cs);
	}

	private String generateJasperReportDataSource (String snippets)
	{
		return null;
	}
	
	private Object[] extractSnippets(String snippetsXml)
	{
		Pattern p = Pattern.compile("<snippet:snippet\\s.*?>(.*?)</snippet:snippet>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	    Matcher m = p.matcher(snippetsXml);
	    
	    ArrayList<String> al = new ArrayList<String>();
	    while (m.find())
	    {
	    	al.add(m.group(1));
	    }
	    return al.toArray(); 
	}

	
	
	public static void main(String[] args) throws Exception {
		
		CitationStyleExecutor cse = new CitationStyleExecutor();
		
		
		//logger.info(pcst.explainStyles());
		logger.info(
				new String (
						cse.getOutput("APA", "snippet", ResourceUtil.getResourceAsString("DataSources/export_xml.xml")
						)));
		logger.info(
				new String (
						pcs.getOutput("APA", "snippet", ResourceUtil.getResourceAsString("DataSources/export_xml.xml")
						)));

	}	
}
