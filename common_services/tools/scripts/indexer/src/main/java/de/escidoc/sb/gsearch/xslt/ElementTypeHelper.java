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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Is called from sylesheet that transforms foxml to indexable document.
 * Performs checks of strings for element-type. to call this class from stylesheet:
 * declaration in sylesheet-element: xmlns:component-accessor=
 * "xalan://de.escidoc.sb.gsearch.xslt.ComponentAccessor"
 * xmlns:element-type-helper="xalan://de.escidoc.sb.gsearch.xslt.ElementTypeHelper"
 * extension-element-prefixes="component-accessor element-type-helper" use:
 * <xsl:value-of select="element-type-helper:isDateOrDecimal($ELEMENT_VALUE)"/>
 * 
 * @author MIH
 */
public class ElementTypeHelper {

    //xs:date
    private static Pattern datePattern = Pattern.compile(
            "[0-9]*?-[0-9]*?-[0-9]*?[0-9TtZz\\+\\:\\.\\-]*");
    private static Matcher dateMatcher = datePattern.matcher("");
    
    //xs:decimal
    private static Pattern decimalPattern = Pattern.compile("[+-]?[0-9\\.]*");
    private static Matcher decimalMatcher = decimalPattern.matcher("");
    

    /**
     * Returns true if given term matches datePattern or decimalPattern.
     * 
     * @param term
     *            term
     * @return boolean true or false.
     */
    public static synchronized boolean isDateOrDecimal(final String term) {
        if (term.length() > 40) {
            return false;
        }
        if (dateMatcher.reset(term).matches() 
                || decimalMatcher.reset(term).matches()) {
            return true;
        }
        return false;
    }

}
