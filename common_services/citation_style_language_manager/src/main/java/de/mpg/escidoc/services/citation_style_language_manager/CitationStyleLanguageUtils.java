/**
 * 
 */
package de.mpg.escidoc.services.citation_style_language_manager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import de.undercouch.citeproc.helper.CSLUtils;

/**
 * Utility class for static functions that are often needed when working with CSL
 * @author walter
 *
 */
public class CitationStyleLanguageUtils {

	private final static Logger logger = Logger.getLogger(CitationStyleLanguageUtils.class);
	
	/**
	 * gets a csl style from a url
	 * 
	 * @param url
	 * @return csl style xml as String or null if no style could be found or
	 *         read
	 * @throws Exception 
	 */
	protected static String loadStyleFromUrl(String url) throws Exception {
		String style = null;
		try {
			style = CSLUtils.readURLToString(new URL(url), "UTF-8");
		} catch (MalformedURLException e) {
			logger.error(
					"URL seems to be malformed, when trying to retrieve the csl style",
					e);
			throw new Exception(e);
		} catch (IOException e) {
			logger.error("IO-Problem, when trying to retrieve the csl style", e);
			throw new Exception(e);
		}
		return style;
	}
	
	/**
	 * gets a csl style from a cone json url
	 * 
	 * @param url
	 * @return csl style xml as String or null if no style could be found or
	 *         read
	 * @throws Exception 
	 */
	protected static String loadStyleFromJsonUrl(String url) throws Exception
	{
		String xml = null;

		try {
			JsonFactory jfactory = new JsonFactory();

			// read JSON from url
			JsonParser jParser = jfactory.createParser(new URL(url + "?format=json"));

			while (jParser.nextToken() != JsonToken.END_OBJECT) {

				String fieldname = jParser.getCurrentName();
				if ("http_www_w3_org_1999_02_22_rdf_syntax_ns_value".equals(fieldname)) {

				  // current token is "name",
			      // move to next, which is "name"'s value
				  jParser.nextToken();
				  xml = jParser.getText();
				  break;
				}
			}
			jParser.close();
		} catch (JsonParseException e) {
			logger.error("Error parsing json from URL (" + url + ")", e);
			throw new Exception(e);
		} catch (IOException e) {
			logger.error("Error getting json from URL (" + url + ")", e);
			throw new Exception(e);
		}
		if (logger.isDebugEnabled()) 
		{
			logger.debug("Successfully parsed CSL-XML from URL (" + url + ")\n--------------------\n" + xml +"\n--------------------\n");
		}
		return xml;
	}
}
