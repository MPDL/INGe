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

	// FontStyleCollection
	public static FontStylesCollection fsc = null;

	public static final String I18N_TAG = "localized";
	static Map<Pair, String> citationMap = new HashMap<Pair, String>();

	/**
	 * Load Default FontStylesCollection only once
	 * 
	 * @throws CitationStyleManagerException
	 */
	public static void loadFontStylesCollection()
			throws CitationStyleManagerException {
		if (fsc != null)
			return;
		try {
			fsc = FontStylesCollection.loadFromXml(ResourceUtil
					.getPathToCitationStyles()
					+ "font-styles.xml");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new CitationStyleManagerException(
					"Cannot loadFontStylesCollection: ", e);
		}
	}

	/**
	 * Converts snippet &lt;span&gt; tags to the appropriate JasperReport Styled
	 * Text. Note: If at least one &lt;span&gt; css class will not match
	 * FontStyle css, the snippet will be returned without any changes.
	 * 
	 * @param snippet
	 * @return converted snippet
	 * @throws CitationStyleManagerException
	 */
	public static String convertSnippetToJasperStyledText(String snippet)
			throws CitationStyleManagerException {

		snippet = removeI18N(snippet);

		loadFontStylesCollection();

		if (!Utils.checkVal(snippet) || fsc == null)
			return snippet;

		// logger.info("passed str:" + str);

		FontStyle fs;

		StringBuffer sb = new StringBuffer();
		String regexp = "(<span\\s+class=\"(\\w+)\".*?>)";
		Matcher m = Pattern.compile(regexp,
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(snippet);
		while (m.find()) {
			fs = fsc.getFontStyleByCssClass(m.group(2));
			// logger.info("fs:" + fs);

			// Rigorous: if at list once no css class has been found return str
			// as it is
			if (fs == null) {
				return snippet;
			} else {
				m.appendReplacement(sb, "<style" + fs.getStyleAttributes()
						+ ">");
			}
		}
		snippet = m.appendTail(sb).toString();

		snippet = Utils.replaceAllTotal(snippet, "</span>", "</style>");
		
		//replace all non-escaped & 
		snippet = Utils.replaceAllTotal(snippet, "\\&(?!amp;)", "&amp;");

		// logger.info("processed str:" + str);

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
		if (idType.equals("")) {
			citationStyle = "default";
		} else {
			// set the idType from to CONE to SFX and cut the SFX-Id from the URL
			if (idType.equals("CONE")){
				idValue = idValue.substring(idValue.lastIndexOf("/")+1);
				idType = "SFX";
			}
			Pair keyValue = new Pair(idType, idValue);
			if (citationMap.size() == 0) {
				getJournalsXML();
			}
			if (citationMap.get(keyValue) == null) {
				citationStyle = "default";
			} else {
				citationStyle = citationMap.get(keyValue);
			}
		}

		 logger.info("CIT STYLE " + idType + ", idValue: " + idValue +
		 ", citation style: " + citationStyle);

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
			PropertyReader.getProperty("escidoc.cone.service.url") + "journals/query?format=rdf&escidoc:citation-style=*&m=full&l=0";
		logger.info("cone query:" + coneQuery);
		GetMethod getMethod = new GetMethod(coneQuery);

		client.executeMethod(getMethod);

		XMLReader xr;

		xr = XMLReaderFactory.createXMLReader();
		JusXmlHandler handler = new JusXmlHandler();
		xr.setContentHandler(handler);
		xr.setErrorHandler(handler);

		Reader r = new InputStreamReader(getMethod.getResponseBodyAsStream());
		xr.parse(new InputSource(r));

		citationMap = handler.getCitationStyleMap();
	}

	
}

class JusXmlHandler extends DefaultHandler {

	private static final Logger logger = Logger.getLogger(JusXmlHandler.class);
	String currentElement = null;
	String citationStyle = null;
	String sfxValue = null;
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
		//gets the attribute with the URL of the item and cuts the SFXValue
		if (currentElement.equals("Description")&& attributes.getLength()!=0){
			sfxValue =  attributes.getValue("rdf:about");
			sfxValue = sfxValue.substring(sfxValue.lastIndexOf("/") + 1);
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
			journalIdTypeValue.setKey("SFX");
			journalIdTypeValue.setValue(sfxValue);
			citationStyleMap.put(journalIdTypeValue, citationStyle);
			
		} else if (currentElement.equals("type")& !tempString.trim().equals("")) {

			idType = tempString.substring(tempString.lastIndexOf("/") + 1);
			journalIdTypeValue = new Pair();
			journalIdTypeValue.setKey(idType);

		} else if (currentElement.equals("value") & !tempString.trim().equals("")) {
			journalIdTypeValue.setValue(tempString);
			citationStyleMap.put(journalIdTypeValue, citationStyle);
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
