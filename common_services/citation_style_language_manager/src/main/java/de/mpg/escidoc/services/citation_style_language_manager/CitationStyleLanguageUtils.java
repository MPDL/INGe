/**
 * 
 */
package de.mpg.escidoc.services.citation_style_language_manager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

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
	 */
	protected static String loadStyleFromUrl(String url) {
		String style = null;
		try {
			style = CSLUtils.readURLToString(new URL(url), "UTF-8");
		} catch (MalformedURLException e) {
			logger.error(
					"URL seems to be malformed, when trying to retrieve the csl style",
					e);
		} catch (IOException e) {
			logger.error("IO-Problem, when trying to retrieve the csl style", e);
		}
		return style;
	}
}
