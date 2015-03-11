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

import java.util.HashMap;

/**
 * Is called from sylesheet that transforms foxml to indexable document. Returns
 * most recent of six given dates. to call this class from stylesheet:
 * declaration in sylesheet-element: xmlns:component-accessor=
 * "xalan://de.escidoc.sb.gsearch.xslt.ComponentAccessor"
 * xmlns:lastdate-helper="xalan://de.escidoc.sb.gsearch.xslt.LastdateHelper"
 * extension-element-prefixes="component-accessor lastdate-helper" use:
 * <xsl:value-of select="lastdate-helper:getLastDate()"/>
 * 
 * @author MIH
 */
public class LastdateHelper {
	
	private static HashMap<String, String> dateElementNames
				= new HashMap<String, String>() {
		            {
		                put("created", ".created");
		                put("modified", ".modified");
		                put("submitted", ".dateSubmitted");
		                put("accepted", ".dateAccepted");
		                put("issued", ".issued");
		                put("released", ".published-online");
		            }
		        };

    /**
     * Get the date that is most recent out of the given Dates.
     * 
     * @param created
     *            creation-date
     * @param modified
     *            modify-date
     * @param submitted
     *            submission-date
     * @param accepted
     *            acception-date
     * @param issued
     *            issue-date
     * @param released
     *            release-date
     * @return String newest Date
     */
    public static String getLastDate(
        final String created, final String modified, final String submitted,
        final String accepted, final String issued, final String released) {
        String newestDate = "";
        if (created != null && !created.equals("")) {
            newestDate = created;
        }
        if (modified != null && !modified.equals("")
            && modified.compareTo(newestDate) > 0) {
            newestDate = modified;
        }
        if (submitted != null && !submitted.equals("")
            && submitted.compareTo(newestDate) > 0) {
            newestDate = submitted;
        }
        if (accepted != null && !accepted.equals("")
            && accepted.compareTo(newestDate) > 0) {
            newestDate = accepted;
        }
        if (issued != null && !issued.equals("")
            && issued.compareTo(newestDate) > 0) {
            newestDate = issued;
        }
        if (released != null && !released.equals("")
        		&& released.compareTo(newestDate) > 0) {
            newestDate = released;
        }

        return newestDate;
    }

    /**
     * Get the element-name of the date that is most recent 
     * out of the given Dates.
     * 
     * @param contextName
     *            name of the context of the index-names
     * @param created
     *            creation-date
     * @param modified
     *            modify-date
     * @param submitted
     *            submission-date
     * @param accepted
     *            acception-date
     * @param issued
     *            issue-date
     * @param released
     *            release-date
     * @return String newest Date
     */
    public static String getLastDateElement(final String contextName,
        final String created, final String modified, final String submitted,
        final String accepted, final String issued, final String released) {
        String elementName = "";
        String newestDate = "";
        if (created != null && !created.equals("")) {
            elementName = contextName + dateElementNames.get("created");
            newestDate = created;
        }
        if (modified != null && !modified.equals("")
            && modified.compareTo(newestDate) > 0) {
            elementName = contextName + dateElementNames.get("modified");;
            newestDate = modified;
        }
        if (submitted != null && !submitted.equals("")
            && submitted.compareTo(newestDate) > 0) {
            elementName = contextName + dateElementNames.get("submitted");;
            newestDate = submitted;
        }
        if (accepted != null && !accepted.equals("")
            && accepted.compareTo(newestDate) > 0) {
            elementName = contextName + dateElementNames.get("accepted");;
            newestDate = accepted;
        }
        if (issued != null && !issued.equals("")
            && issued.compareTo(newestDate) > 0) {
            elementName = contextName + dateElementNames.get("issued");;
            newestDate = issued;
        }
        if (released != null && !released.equals("")
        		&& released.compareTo(newestDate) > 0) {
            elementName = contextName + dateElementNames.get("released");
        }

        return elementName;
    }

}
