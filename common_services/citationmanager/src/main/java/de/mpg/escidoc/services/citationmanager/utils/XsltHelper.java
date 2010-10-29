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

//import static org.junit.Assert.fail;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.citationmanager.data.FontStyle;
import de.mpg.escidoc.services.citationmanager.data.FontStylesCollection;
import de.mpg.escidoc.services.citationmanager.data.Pair;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ProxyHelper;

/**
 * Function extensions for the citationmanager XSLTs
 * 
 * @author Vlad Makarenko (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate: 2010-02-17 16:48:14 +0100 (Mi,
 *          17 Feb 2010) $
 * 
 */

public class XsltHelper {
	private static final Logger logger = Logger.getLogger(XsltHelper.class);


	public static final String I18N_TAG = "localized";
	static Map<Pair, String> citationMap = new HashMap<Pair, String>();

	/**
	 * Converts snippet &lt;span&gt; tags to the appropriate JasperReport Styled
	 * Text. Note: If at least one &lt;span&gt; css class will not match
	 * FontStyle css, the snippet will be returned without any changes.
	 * 
	 * @param snippet
	 * @return converted snippet
	 * @throws CitationStyleManagerException
	 */
	public static String convertSnippetToJasperStyledText(String cs, String snippet)
			throws CitationStyleManagerException {

		snippet = removeI18N(snippet);

		FontStylesCollection fsc = XmlHelper.loadFontStylesCollection(cs);
		
		logger.info("FSC for style " + cs + ": " + fsc);

		if (!Utils.checkVal(snippet) || fsc == null)
			return snippet;

		FontStyle fs;

		StringBuffer sb = new StringBuffer();
		String regexp = "<span\\s+class=\"(\\w+)\".*?>(.*?)</span>";
		Matcher m = Pattern.compile(regexp,
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(snippet);
		while (m.find()) {
			fs = fsc.getFontStyleByCssClass(m.group(1));
			// logger.info("fs:" + fs);

			// Rigorous: if at list once no css class has been found return str
			// as it is
			if (fs == null) {
				return snippet;
			} else {
				m.appendReplacement(sb, "<style" + fs.getStyleAttributes() + ">$2</style>");
			}
		}
		snippet = m.appendTail(sb).toString();

//		snippet = Utils.replaceAllTotal(snippet, "</span>", "</style>");
		
		//replace all non-escaped & 
		snippet = Utils.replaceAllTotal(snippet, "\\&(?!amp;)", "&amp;");
		
		//replace all < back to the entity  
		snippet = Utils.replaceAllTotal(snippet, "\\<(?!\\/?style)", "&lt;");

//		logger.info("processed str:" + snippet);

//		 logger.info("processed snippet:" + snippet);

		return snippet;
	}
	
	/**
	 * Converts snippet &lt;span&gt; tags to the HTML formatting, 
	 * i.e. <code><b>, <i>, <u>, <s></code>
	 * Text. Note: If at least one &lt;span&gt; css class will not match
	 * FontStyle css, the snippet will be returned without any changes.
	 * 
	 * @param snippet
	 * @return converted snippet
	 * @throws CitationStyleManagerException
	 */
	public static String convertSnippetToHtml(String snippet)
			throws CitationStyleManagerException {

//		snippet = removeI18N(snippet);

		FontStylesCollection fsc = XmlHelper.loadFontStylesCollection();

		if (!Utils.checkVal(snippet) || fsc == null)
			return snippet;

		logger.info("passed str:" + snippet);

		FontStyle fs;

		StringBuffer sb = new StringBuffer();
		String regexp = "<span\\s+class=\"(\\w+)\".*?>(.*?)</span>";
		Matcher m = Pattern.compile(regexp,
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(snippet);
		while (m.find()) {
			String cssClass = m.group(1);
			fs = fsc.getFontStyleByCssClass(cssClass);
			// logger.info("fs:" + fs);

			// Rigorous: if at list once no css class has been found return str
			// as it is
			if (fs == null) {
				return snippet;
			} else {
				String str = "$2";
				if (fs.getIsStrikeThrough()) {
					str = "<s>" + str + "</s>";
				}
				if (fs.getIsUnderline()) {
					str = "<u>" + str + "</u>";
				}
				if (fs.getIsItalic()) {
					str = "<i>" + str + "</i>";
				}
				if (fs.getIsBold()) {
					str = "<b>" + str + "</b>";
				}
				str = "<span class=\""+ cssClass + "\">" + str + "</span>";				
				m.appendReplacement(sb, str);
				
			}
		}
		snippet = m.appendTail(sb).toString();

		//replace all non-escaped & 
		snippet = Utils.replaceAllTotal(snippet, "\\&(?!amp;)", "&amp;");
		
		return snippet;
	}	

	public static String removeI18N(String snippet) {
		return Utils.replaceAllTotal(snippet, "<" + I18N_TAG
				+ "\\s+class=\"\\w+\".*?>(.*?)</" + I18N_TAG + ">", "$1");
	}
	
	/**
	 * Gets the citation style for given idType and idValue.
	 * Called from functions.xml for Jus-citation style.
	 * Called for publications of type journal article and case note. 
	 * If there is no idType of the source, a default citation style is returned.
	 * Else gets the citation style for the given idValue-Type-Pair.  
	 * @param idType
	 * @param idValue
	 * @return
	 * @throws Exception
	 */
	public static String getCitationStyleForJournal(String idType,
			String idValue) throws Exception {
		String citationStyle = null;
		// if there is no idType, put the citation style to default
		if (idType.equals("")) {
			citationStyle = "default";
		} else {
			// if the type is CoNE, take the ID from the URL
			if (idType.equals("CONE")){
				idValue = idValue.substring(idValue.lastIndexOf("/")+1);
			}
			Pair keyValue = new Pair(idType, idValue);
			if (citationMap.size() == 0) {
				getJournalsXML();
			}
			if (citationMap.get(keyValue) == null) {
				citationStyle = "default";
			} else {
				citationStyle = citationMap.get(keyValue);
				if (citationStyle.equalsIgnoreCase("Kurztitel_ZS Band, Heft (Jahr)") ||
					citationStyle.equalsIgnoreCase("Titel_ZS Band, Heft (Jahr)") ||
					citationStyle.equalsIgnoreCase("(Jahr) Band, Heft Titel_ZS")){
				}
				else {
					// if the citation style is none of the three above, put it to default
					citationStyle = "default";
				}
			}
		}
		return citationStyle;

	}
	
	/**
	 * Reads all CONE-entries with a citation-style field filled in. 
	 * Generates a Map with citation styles and idValue-Type-Pairs. 
	 * @throws Exception
	 */
	public static void getJournalsXML() throws Exception 
	{
		HttpClient client = new HttpClient();

		String coneQuery = 
		// JUS-Testserver CoNE
		//	"http://193.174.132.114/cone/journals/query?format=rdf&escidoc:citation-style=*&m=full&n=0";
		PropertyReader.getProperty("escidoc.cone.service.url") + "journals/query?format=rdf&escidoc:citation-style=*&m=full&n=0";
		logger.info("cone query:" + coneQuery);
		GetMethod getMethod = new GetMethod(coneQuery);

		ProxyHelper.executeMethod(client, getMethod);
		
		XMLReader xr;

		xr = XMLReaderFactory.createXMLReader();
		JusXmlHandler handler = new JusXmlHandler();
		xr.setContentHandler(handler);
		xr.setErrorHandler(handler);

		Reader r = new InputStreamReader(getMethod.getResponseBodyAsStream());
		xr.parse(new InputSource(r));

		citationMap = handler.getCitationStyleMap();
	}


	/**
	 * Check CJK codepoints in <code>str</code>.  
	 * @param str
	 * @return <code>true</code> if <code>str</code> has at least one CJK codepoint, otherwise <code>false</code>.     
	 */
	public static boolean isCJK(String str)
	{
		if (str==null || str.trim().equals("")) return false;
		for (int i = 0; i < str.length(); i++)
		{
			int codePoint = str.codePointAt(i);
			if(codePoint>=19968 && codePoint<=40911) return true;
		}
		return false;
	}    	
	
	
}

class JusXmlHandler extends DefaultHandler {

	private static final Logger logger = Logger.getLogger(JusXmlHandler.class);
	String currentElement = null;
	String citationStyle = null;
	String coneValue = null;
	String idType = null;
	private Map<Pair, String> citationStyleMap;
	int counter = 0;
	Pair journalIdTypeValue;

	/**
	 * Start reading an XML-File. 
	 * Creates a Map to hold the JournalId-Value-Pair and the corresponding citation style.
	 */
	@Override
	public void startDocument() throws SAXException {
		citationStyleMap = new HashMap<Pair, String>();
		super.startDocument();
	}

	/**
	 * Gets every element and set it to currentElement.
	 */
	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		
		if ("".equals(uri)) {
			currentElement = name;
		} else {
			currentElement = localName;
		}
		//gets the attribute with the URL of the item and cuts the coneValue
		if (currentElement.equals("Description")&& attributes.getLength()!=0){
			coneValue =  attributes.getValue("rdf:about");
			coneValue = coneValue.substring(coneValue.lastIndexOf("/") + 1);
		}
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}

	@Override
	public void endElement(String arg0, String arg1, String arg2)
	throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(arg0, arg1, arg2);

	}

	/**
	 * Gets the values of the elements. 
	 * For every id-type of journal a new Pair is created. 
	 * The key is set to the idType. The value is set to the value of the id. 
	 * The journalIdTypeValue and the citationStyle are putted in a HashMap. 
	 */
	public void characters(char ch[], int start, int length) {
		String tempString = new String(ch, start, length);
		if (currentElement.equals("citation-style")& !tempString.trim().equals("")) {
			// sets the SFX-Id and type
			citationStyle = tempString;
			
			journalIdTypeValue = new Pair();
			journalIdTypeValue.setKey("CONE");
			//logger.info("cone value " + coneValue + "; CS: " +citationStyle);
			journalIdTypeValue.setValue(coneValue);
			citationStyleMap.put(journalIdTypeValue, citationStyle);
//			logger.info("READ citation style " +  coneValue + ", Zitierstil: " + citationStyle);
		} 
		/*else if (currentElement.equals("type")& !tempString.trim().equals("")) {
			
			idType = tempString.substring(tempString.lastIndexOf("/") + 1);
			
			journalIdTypeValue = new Pair();
			journalIdTypeValue.setKey(idType);
			//logger.info("idType " + idType);

		} else if (currentElement.equals("value") & !tempString.trim().equals("")) {
			//logger.info("idValue " + tempString);
			journalIdTypeValue.setValue(tempString);
			logger.info("READ citation style 2 " +  tempString + ", Zitierstil: " + citationStyle);
			citationStyleMap.put(journalIdTypeValue, citationStyle);
		}*/
		else if (currentElement.equals("type")& !tempString.trim().equals("")) {
			idType = tempString.substring(tempString.lastIndexOf("/") + 1);
			
		}else if (currentElement.equals("value") & !tempString.trim().equals("")) {
			if (tempString.equals(coneValue)){}
			else {
				journalIdTypeValue = new Pair();
				journalIdTypeValue.setKey(idType);
				journalIdTypeValue.setValue(tempString);
				citationStyleMap.put(journalIdTypeValue, citationStyle);
				//logger.info("READ citation style 2 " +  tempString + ", Zitierstil: " + citationStyle);
			}
			
		}
	}

	/**
	 * Returns a HashMap with JournalId-Value-Pair and 
	 * the corresponding citation style.
	 * @return
	 */
	public Map<Pair,String> getCitationStyleMap(){
		return this.citationStyleMap;
	}

}	
