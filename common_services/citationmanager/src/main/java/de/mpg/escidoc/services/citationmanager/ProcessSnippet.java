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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.log4j.Logger;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
*
* HTML snippet generation class. 
*
* TODO: NS support 
* 
* @author vmakarenko (initial creation)
* @author $Author$ (last modification)
* @version $Revision$ $LastChangedDate$
*
*/
public class ProcessSnippet {

    private static final Logger logger = Logger.getLogger(ProcessSnippet.class);
    private static final String PUBLICATION_NS = "http://escidoc.mpg.de/metadataprofile/schema/0.1/";
    private static final String PARENT_ELEMENT_NAME = "content-model-specific";
    private static final String SNIPPET_ELEMENT_NAME = "dcterms:bibliographicCitation";
    private static final String ITEM_ELEMENT_NAME = "item";
    
	/**
	 * Takes org.w3c.dom.Document  doc, processes it with InputStream report,
	 * populates element PARENT_ELEMENT_NAME with child SNIPPET_ELEMENT_NAME.
	 * 
	 * @param doc org.w3c.dom.Document - item-list  
	 * @param params - report filling params
	 * @param is - report of citation styles in {@link InputStream}
	 * @param os - output xml in {@link OutputStream}
	 * @throws JRException
	 * @throws IOException
	 * @throws CitationStyleManagerException
	 */
	public void export(Document doc, Map params, final InputStream is, OutputStream os) throws JRException, IOException, CitationStyleManagerException {
		
		if ( doc == null ) 
			throw new CitationStyleManagerException("org.w3c.dom.Document doc is null");
		
		if ( is == null ) 
			throw new CitationStyleManagerException("Report InputStream is null");
		
		if ( os == null ) 
			throw new CitationStyleManagerException("Snippet OutputStream is null");
		
		if ( params == null ) 
			throw new CitationStyleManagerException("Filling parameters are null");
		
		
		Element root = doc.getDocumentElement(  );
		
		NodeList itemElements = root.getElementsByTagName( 
				getElementWithPrefix(XmlHelper.outputString(doc), ITEM_ELEMENT_NAME) 
		);
		
		int length = itemElements.getLength( ) ; 
		Node[] itemsArr = new Node[length];
		
		// clean up doc and populate items array
		for ( int i = 0; i < length; i++ )
		{
			//cloning variant:
//			Node n = itemElements.item(0);
			// clone all items into array
//			itemsArr[i] = n.cloneNode( true );
			// remove all items in document
			//root.removeChild(n);

			//simple variant
			itemsArr[i] = root.removeChild(itemElements.item(0));
		}

		
		// set up exporter
		JRExporter exporter = new JRHtmlExporter( );
		/* Switch off pagination and null pixel alignment for JRHtmlExporter */

		StringBuffer[] sb = new StringBuffer[length];
		String[] pea = new String[length];
		
        JasperReport jasperReport = (JasperReport)JRLoader.loadObject(is); 
		
		// generate snippets 
		for ( int i = 0; i < length; i++ )
		{
			//add saved item
			root.appendChild(itemsArr[i]);
			
			JasperPrint jasperPrint = JasperFillManager.fillReport(
					jasperReport,
					params,
					new JRXmlDataSource(doc, ProcessCitationStyles.REPORT_XML_ROOT_XPATH)
			);

			// hack to get parent element with prefix  
			// to be accessible in the next loop
			pea[i] = getElementWithPrefix(XmlHelper.outputString(doc), PARENT_ELEMENT_NAME);
			
			sb[i] = new StringBuffer();
			
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.setParameter(JRExporterParameter.OUTPUT_STRING_BUFFER, sb[i]);
			
			exporter.exportReport();
			
			//remove item
			root.removeChild(itemsArr[i]);
			
		}
		
		//append metadata with snippets in sb [] 
		for ( int i = 0; i < length; i++ )
		{
			Element snippetElement = doc.createElement( SNIPPET_ELEMENT_NAME );
			CDATASection snippetCDATASection = doc.createCDATASection(extractPureCitation(sb[i].toString()));
			snippetElement.appendChild(snippetCDATASection);
			
			// insert snippetElement after dc:title
//			NodeList nl = ((Element)itemsArr[i]).getElementsByTagName("profile:publication");
//			Element publicationElement = (Element)nl.item( 0 );
			
//			nl = publicationElement.getElementsByTagName("dc:title"); 
//			Element titleElement = (Element)nl.item( 0 );
//			
//			publicationElement.insertBefore(snippetElement, titleElement.getNextSibling());
			
			//OR add snippetElement as the last child of publicationElement  
			//publicationElement.appendChild(snippetElement);
			
			NodeList nl = ((Element)itemsArr[i]).getElementsByTagName( pea[i] );
			Element parentElement = (Element)nl.item( 0 );

			parentElement.appendChild(snippetElement);

			//add saved item
			root.appendChild(itemsArr[i]);
		}

		XmlHelper.output(doc, os);
		
	}
	
	
	/**
	 * Extracts pure citation html from generated html report 
	 * @param html - reportPrint html
	 * @return - pure citation style w/o html headers, tables, etc.
	 */
	private String extractPureCitation(String html)
	{
//		  <td style="text-align: justify;line-height: 1.6107178; "><span style="font-family: Arial; font-size: 12.0px;">Meier, H., &amp; Meier, H. (2007). PubMan: The first of all.</span></td>
		Pattern p = Pattern.compile("<td.*?((<span.*?</span>)+)</td>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	    Matcher m = p.matcher(html);
	    //omit first match, i.e. citation header
	    m.find();
	    return m.find() ? m.group(1) : html; 
	}

	/**
	 * Get of NS prefix (dirty hack, TODO: make everything with correct NS)  
	 * @param xml
	 * @return
	 */
	private String getElementWithPrefix(String xml, String element)
	{
		Pattern p = Pattern.compile("<([\\w-]+:)?" + element + "[^\\w-]");
	    Matcher m = p.matcher(xml);
	    return m.find() ? m.group(1) + element : element; 
	}

	
}
