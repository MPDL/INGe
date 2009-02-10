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
import java.util.Properties;
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

import de.mpg.escidoc.services.citationmanager.data.FontStyle;
import de.mpg.escidoc.services.citationmanager.utils.Utils;
import de.mpg.escidoc.services.citationmanager.utils.XmlHelper;
import de.mpg.escidoc.services.framework.PropertyReader;


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
    private static final String SNIPPET_NS = "http://purl.org/dc/terms/";
    private static final String ITEM_ELEMENT_NAME = "item";
	private static final String URL_ELEMENT_NAME = "dc:identifier";
    
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
	public void export(Document doc, Map params, final JasperReport jr, OutputStream os) throws JRException, IOException, CitationStyleManagerException {
		
		if ( doc == null ) 
			throw new CitationStyleManagerException("org.w3c.dom.Document doc is null");
		
		if ( jr == null ) 
			throw new CitationStyleManagerException("Report is null");
		
		if ( os == null ) 
			throw new CitationStyleManagerException("Snippet OutputStream is null");
		
		if ( params == null ) 
			throw new CitationStyleManagerException("Filling parameters are null");
		
		 
		Element root = doc.getDocumentElement(  );
		
		// doesn't work for different prefixes! 
//		NodeList itemElements = root.getElementsByTagName( 
//				getElementWithPrefix(XmlHelper.outputString(doc), ITEM_ELEMENT_NAME) 
//		);
		// works everywhere
		//remove all text nodes
		NodeList itemElements = root.getChildNodes(); 
		int length = itemElements.getLength( ) ; 
		int k = 0;
		for ( int i = 0; i < length; i++ )
		{
			Node n = itemElements.item(k);
			if ( n.getNodeType() == Node.TEXT_NODE )
				root.removeChild(n);
			else
				k++;
		}
		
		itemElements = root.getChildNodes(); 
		length = itemElements.getLength( ) ; 
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
		
        //JasperReport jasperReport = (JasperReport)JRLoader.loadObject(is); 
		
		// generate snippets 
		for ( int i = 0; i < length; i++ )
		{
			//add saved item
			root.appendChild(itemsArr[i]);
			
			JasperPrint jasperPrint = JasperFillManager.fillReport(
					jr,
					params,
					new JRXmlDataSource(doc, jr.getQuery().getText())
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
			// logger.info("iteration:" + i + "; pea:" +  pea[i]);
			
			String citation = extractPureCitation(sb[i].toString());
			//do not add citation element if it is empty 
			if ( citation != null )
			{
			
				Element snippetElement = doc.createElement(SNIPPET_ELEMENT_NAME);
				snippetElement.setAttribute("xmlns:dcterms", SNIPPET_NS);
				
				addFrameworkPrefixUrl(doc, snippetElement, (Element)itemsArr[i]); 
				
				CDATASection snippetCDATASection = doc.createCDATASection(citation);
				snippetElement.appendChild(snippetCDATASection);
				
				NodeList nl = ((Element)itemsArr[i]).getElementsByTagName( pea[i] );
				Element parentElement = (Element)nl.item( 0 );
				
				parentElement.appendChild(snippetElement);
			}	

			//add saved item
			root.appendChild(itemsArr[i]);
		}

		XmlHelper.output(doc, os);
		
	}
	
	
	private void addFrameworkPrefixUrl(Document doc, Element snippetElement, Element item) throws IOException {

		
		

		
		String fw_url = null;
		try
		{
			fw_url = PropertyReader.getProperty("escidoc.framework_access.framework.url");
		}
		catch (Exception e) {
            throw new IOException("Error reading framework url:" + e.getMessage());
        }

		NodeList nl = item.getElementsByTagName("escidocComponents:components");
		if ( nl == null )
			return;
		
		Element e = (Element)nl.item(0);
		if ( e == null )
			return;
		
		nl = e.getElementsByTagName("escidocComponents:component");
		if ( nl == null )
			return;
		
		for ( int i = 0; i < nl.getLength(); i++ )
		{
			e = (Element)nl.item( i );
			e = (Element)e.getElementsByTagName("escidocComponents:content").item( 0 );
			if ( "internal-managed".equals(e.getAttribute( "storage" )) )
			{
				e.setAttribute( "xlink:href", fw_url + e.getAttribute( "xlink:href" ) );
			}
		}
	}


	/**
	 * Extracts pure citation html from generated html report
	 * see <code>net.sf.jasperreports.engine.export.oasis.JRHtmlExporter</code> for details
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
	    String snippet = null;
	    if (!m.find())
	    {
	    	return null;
	    }
	    else
	    {
		    //remove all <br/>
	    	snippet = Utils.replaceAllTotal(m.group(1), "<br/?>", "");
	    }
	    
	    return
	    	convertToAdditionalCssClass(
	    			convertStyledTextToCssClass(snippet)
	    	); 
	}
	
	/**
	 * Converts the style to the named CSS class  
	 * @param html - reportPrint html
	 * @return - Citaion style with CSS class representation
	 */
	private String convertStyledTextToCssClass(String html)
	{
//		  <td style="text-align: justify;line-height: 1.6107178; "><span style="font-family: Arial; font-size: 12.0px;">Meier, H., &amp; Meier, H. (2007). PubMan: The first of all.</span></td>
		
		// replace all the following attrs:
		String [] toBeRemoved = 
		{
				"color:\\s*#.+?;",
				"background-color:\\s*#.+?;",
				"font-size:.+?;",
				"text-align:.+?;",
				"font-family:.+?;" 
		}; 
		String regexp;
		Matcher m;
		for ( String str : toBeRemoved ) 
		{
			regexp = "(<span.*?)" + str + "(.*?>)";
			//logger.info(regexp);
			m = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(html);
			m.find();
			html = m.replaceAll("$1$2");
		}
		
		// replace all styles with CSS classes
		String [][] attrs = 
		{
//				{ "font-family:\\s*(.+?)", null }, 
				{ "font-weight:\\s*bold", "Bold" }, 
				{ "font-style:\\s*italic", "Italic" },
				{ "text-decoration:\\s*underline", "Underline" },
				{ "text-decoration:\\s*line-through", "Linethrough" },
				{ "vertical-align:\\s*super", "Super" },
				{ "vertical-align:\\s*sub", "Sub" }
		}; 
		
		regexp = "<span.*?style\\s*=\\s*?\"(.+?)\".*?>";
		//logger.info(regexp);
		String targetHtml = html;
		m = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(html);
		while (m.find())
		{
			String style = m.group(1);
			String CssClass = "";
			String regexp2;
			Matcher m2;
			for ( String[] attr : attrs ) 
			{
				regexp2 = attr[0] + "\\s*;";
				//logger.info(regexp2);
				m2 = Pattern.compile(regexp2, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(style);
				if ( m2.find() )
				{
					// font-family should be taken from the matched Group   
//					if ( attr[0].equals(attrs[0][0]) )
//					{
//						CssClass += m2.group(1);
//					}
//					else 
//					{
						CssClass += attr[1];
//					}	
				}
			}

			// set Default class in case of empty one
			if ( CssClass.equals("") )
				CssClass = "Default";
			
			targetHtml = targetHtml.replaceFirst(m.group(), "<span class=\"" + CssClass + "\">");
		}
		
		return targetHtml; 
	}
	
	/**
	 * Converts the style to the additional named CSS class (see FontStyle.cssClass)  
	 * @param html - reportPrint html
	 * @return - Citaion style with additional CSS class representation
	 */
	private String convertToAdditionalCssClass(String html)
	{
//		&lt;span class=&quot;DisplayDateStatus&quot;&gt; (2000). &lt;/span&gt;		
		
		Matcher m;
		m = Pattern
			.compile(FontStyle.CSS_CLASS_REGEXP, Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
			.matcher(html);
		return 	m.find() ? 
					m.replaceAll(FontStyle.CSS_CLASS_SUBST) :
					html;
	}

	/**
	 * Get of NS prefix (dirty hack, TODO: replace with proper NS support)  
	 * @param xml
	 * @return
	 */
	private String getElementWithPrefix(String xml, String element)
	{
		Pattern p = Pattern.compile("<([\\w-]+:)?" + element + "[^\\w-]");
	    Matcher m = p.matcher(xml);
	    return m.find() ? m.group(1) + element : element; 
	}
	
    public static void main(String[] args) 
    {
    	ProcessSnippet psn = new ProcessSnippet();
    	logger.info(psn.convertStyledTextToCssClass(
    			"<span style=\"" +
    				"font-family: Arial; " +
    				"font-size: 12.0px;" +
    				"color: #ffffff;" +
    				"background-color: #ffffff;" +
    				"text-align: justified;" +
    				"font-weight: bold;" +
    				"font-style: italic;" +
    				"text-decoration: underline;" +
    				"text-decoration: line-through;" +
    				"vertical-align: super;" +
    				"vertical-align: sub;" +
    			"\">" +
    			"Meier, H., &amp; Meier, H. (2007). PubMan: The first of all." +
    			"</span>" 
    		  + "Here the text further" +
    			"<span style=\"" +
	    			"font-family: Tahoma; " +
	    			"font-size: 12.0px;" +
	    			"color: #bbbbbb;" +
	    			"background-color: #aaaaaa;" +
	    			"text-align: left;" +
	    			"font-weight: bold;" +
	    			"font-style: italic;" +
	    			"text-decoration: underline;" +
	    			"text-decoration: line-through;" +
	    			"vertical-align: sub;" +
	    			"\">" +
    			"Meier, H., &amp; Meier, H. (2007). PubMan: The first of all." +
    			"</span>"
    			
    	));
    	
    	logger.info(
    			psn.convertToAdditionalCssClass(
    			psn.convertStyledTextToCssClass(
    			"<span style=\"" +
    				"font-family: Arial; " +
    				"font-size: 12.0px;" +
    				"color: #ffffff;" +
    				"background-color: #ffffff;" +
    				"text-align: justified;" +
    				"font-weight: bold;" +
    				"font-style: italic;" +
    				"text-decoration: underline;" +
    				"text-decoration: line-through;" +
    				"vertical-align: super;" +
    				"vertical-align: sub;" +
    			"\">" +
    			"Meier, H., &amp; Meier, H." +
    			"&lt;span class=&quot;BlaBlaBla&quot;&gt; (2007). &lt;/span&gt;" +
    			"PubMan: The first of all." +
    			"</span>"
    			))); 
	}

	
}
