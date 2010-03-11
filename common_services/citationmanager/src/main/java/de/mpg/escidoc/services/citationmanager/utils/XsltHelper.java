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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.citationmanager.data.FontStyle;
import de.mpg.escidoc.services.citationmanager.data.FontStylesCollection;
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
			Pair keyValue = new Pair(idType, idValue);
			if (citationMap.size() == 0) {
				getXML();
			}
			if (citationMap.get(keyValue) == null) {
				citationStyle = "default";
			} else {
				citationStyle = citationMap.get(keyValue);
			}
		}

		// logger.info("CIT STYLE " + idType + ", idValue: " + idValue +
		// ", citation style: " + citationStyle);

		return citationStyle;

	}
	
	/**
	 * Reads all CONE-entries with a citation-style field filled in. 
	 * Generates a Map with citation styles and idValue-Type-Pairs. 
	 * @throws Exception
	 */
	public static void getXML() throws Exception {
		HttpClient client = new HttpClient();

		String coneUrl = PropertyReader.getProperty("escidoc.cone.service.url");

		GetMethod getMethod = new GetMethod(
				coneUrl
						+ "journals/query?format=rdf&escidoc:citation-style=*&m=full&l=0");

		client.executeMethod(getMethod);

		XMLReader xr;

		xr = XMLReaderFactory.createXMLReader();
		XmlHandler handler = new XmlHandler();
		xr.setContentHandler(handler);
		xr.setErrorHandler(handler);

		Reader r = new InputStreamReader(getMethod.getResponseBodyAsStream());
		xr.parse(new InputSource(r));

		citationMap = handler.getCitationStyleMap();
	}

}
