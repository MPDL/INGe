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


/**
 * Is called from sylesheet that transforms foxml to indexable document. Returns
 * language. to call this class from stylesheet: declaration in
 * sylesheet-element: xmlns:component-accessor=
 * "xalan://de.escidoc.sb.gsearch.xslt.ComponentAccessor"
 * xmlns:language-helper="xalan://de.escidoc.sb.gsearch.xslt.LanguageHelper"
 * extension-element-prefixes="component-accessor language-helper" use:
 * <xsl:value-of select="language-helper:getTwoLetterLanguage()"/>
 * 
 * @author MIH
 */
public class LanguageHelper {

    /**
     * Get Language in ISO-Code or in IANA-Code. Return language in ISO-Code.
     * 
     * @param language
     *            language
     * @return String langauge in ISO-Code
     */
    public static String getTwoLetterLanguage(final String language) {
        String isoLanguage = "";
        if (language != null && !language.equals("")) {
            isoLanguage = language.toLowerCase();
            if (isoLanguage.matches(".*-.*") && !isoLanguage.startsWith("i")) {
                isoLanguage = isoLanguage.replaceFirst("-.*", "");
            }
            else if (isoLanguage.startsWith("i")) {
                isoLanguage = isoLanguage.replaceFirst(".*?-", "");
                if (isoLanguage.matches(".*-.*")) {
                    isoLanguage = isoLanguage.replaceFirst("-.*", "");
                }
            }
        }
        return isoLanguage;
    }

    /**
     * Check if indexLanguage is same than elementLanguage. If element has no
     * language, check if defaultLanguage is given.
     * 
     * @param indexLanguage
     *            indexLanguage
     * @param elementLanguage
     *            elementLanguage
     * @param defaultLanguage
     *            defaultLanguage
     * @return true if elementLanguage is same than indexLanguage
     */
    public static boolean checkLanguage(
        final String indexLanguage, final String elementLanguage,
        final String defaultLanguage) {
        String twoLetterIndexLanguage = getTwoLetterLanguage(indexLanguage);
        String twoLetterElementLanguage = getTwoLetterLanguage(elementLanguage);
        String twoLetterDefaultLanguage = getTwoLetterLanguage(defaultLanguage);

        if (twoLetterIndexLanguage.equals("")
            || twoLetterIndexLanguage.equals(twoLetterElementLanguage)) {
            return true;
        }
        if (twoLetterElementLanguage.equals("")
            && twoLetterIndexLanguage.equals(twoLetterDefaultLanguage)) {
            return true;
        }

        return false;
    }

}
