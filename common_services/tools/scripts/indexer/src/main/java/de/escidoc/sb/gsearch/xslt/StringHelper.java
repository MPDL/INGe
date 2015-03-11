/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */

package de.escidoc.sb.gsearch.xslt;

import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.w3c.dom.NodeList;

/**
 * Is called from sylesheet that transforms foxml to indexable document.
 * Performs different string-operations. to call this class from stylesheet:
 * declaration in sylesheet-element: xmlns:component-accessor=
 * "xalan://de.escidoc.sb.gsearch.xslt.ComponentAccessor"
 * xmlns:string-helper="xalan://de.escidoc.sb.gsearch.xslt.StringHelper"
 * extension-element-prefixes="component-accessor string-helper" use:
 * <xsl:value-of select="string-helper:getSubstringAfterLast($PID,'/')"/>
 * 
 * @author MIH
 */
public class StringHelper {

    private static Logger logger;

    private static final Pattern PATTERN_ID_WITHOUT_VERSION =
        Pattern.compile("([a-zA-Z0-9]+:[a-zA-Z0-9]+):[0-9]+");

    private static final Matcher MATCHER_ID_WITHOUT_VERSION =
        PATTERN_ID_WITHOUT_VERSION.matcher("");

    static {
        logger =
            Logger
                .getLogger(
                de.escidoc.sb.gsearch.xslt.StringHelper.class);
    }

  /**
     * Returns the substring after the last occurence of character.
     * 
     * @param term
     *            term
     * @param term1
     *            character
     * @return String substring of term after last occurence of term1.
     */
    public static String getSubstringAfterLast(
        final String term, final String term1) {
        if ((term == null || "".equals(term)) || (term1 == null || "".equals(term1))  
                || term.lastIndexOf(term1) == -1) {
            return term;
        }
        return term.substring(term.lastIndexOf(term1) + term1.length());
    }

//    /**
//     * Returns the substring after the last occurence of character.
//     * 
//     * @param term
//     *            term
//     * @param term1
//     *            character
//     * @return String substring of term after last occurence of term1.
//     */
//    public static String getSubstringAfterLast(
//        final NodeList nodeList, final String term1) {
//    	String term = "";
//    	if (nodeList != null && nodeList.getLength() == 1) {
//    		term = nodeList.item(0).getNodeValue();
//    	}
//        if ((term == null || "".equals(term)) || (term1 == null || "".equals(term1)) 
//                || term.lastIndexOf(term1) == -1) {
//            return term;
//        }
//        return term.substring(term.lastIndexOf(term1) + term1.length());
//    }

    /**
     * converts to lower case and ascii if possible, otherwise leave at it is.
     * 
     * @param input
     *            input
     * @return Normalized String.
     */
    public static String getNormalizedString(final String input) {
        if (input == null) {
            return null;
        }
        
        String normalizedString = Normalizer.normalize(input.toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
		if (logger.isTraceEnabled()) {
			logger.trace("getNormalizedString( " + input + " returning <"
					+ normalizedString + ">");
		}
        
        if (null == normalizedString || "".equals(normalizedString)) {
        	return input;
        }       
        
        return normalizedString;
    }

    /**
     * Removes the version-identifier of an escidoc-objId.
     * 
     * @param input
     *            objId
     * @return String objId without version-identifier.
     */
    public static synchronized String removeVersionIdentifier(final String input) {
        if (input == null) {
            return null;
        }
        String result = input;
        MATCHER_ID_WITHOUT_VERSION.reset(input);
        if (MATCHER_ID_WITHOUT_VERSION.find()) {
            result = MATCHER_ID_WITHOUT_VERSION.group(1);
        }
        return result;
    }

    /**
     * Removes the version-identifier of an escidoc-objId.
     * 
     * @param input
     *            objId
     * @return String objId without version-identifier.
     */
    public static synchronized String removeVersionIdentifier(
            final String input, final String pidSuffix) {
        if (input == null) {
            return null;
        }
        String result = input;
        MATCHER_ID_WITHOUT_VERSION.reset(input);
        if (MATCHER_ID_WITHOUT_VERSION.find()) {
            result = MATCHER_ID_WITHOUT_VERSION.group(1);
        }
        if (!(pidSuffix == null || "".equals(pidSuffix))) {
            result += ":" + pidSuffix;
        }
        return result;
    }

    /**
     * Fills number with zeros to be able to 
     * search with string-comparison.
     * 
     * @param input
     *            Number as String
     * @param length
     *            required length of number before comma
     * @param decimals
     *            required length of number after comma
     * @param separator
     *            comma or dot
     * @return String filled with 0s.
     */
    public String getNumericString(
            final String input, 
            final int length, 
            final int decimals, 
            final String separator) {
        if (input == null) {
            return null;
        }
        
        
        try {
            new Float(input);
        } catch (Exception e) {
            return input;
        }
        String modifiedInput = input;
        modifiedInput = modifiedInput.replaceAll(
                "[^0-9\\" + separator + "]", "");
        if (modifiedInput == null) {
            return input;
        }
        
        String[] parts = modifiedInput.split("\\" + separator);
        if (parts == null || parts.length == 0 
                || parts[0] == null || parts.length > 2) {
            return input;
        }
        StringBuffer output = new StringBuffer("");
        for (int i = parts[0].length(); i < length; i++) {
            output.append("0");
        }
        output.append(parts[0]).append(separator);
        int dLength = 0;
        if (parts.length == 2) {
            dLength = parts[1].length();
            output.append(parts[1]);
        }
        for (int i = dLength; i < decimals; i++) {
            output.append("0");
        }
        
        return output.toString();
    }
}
