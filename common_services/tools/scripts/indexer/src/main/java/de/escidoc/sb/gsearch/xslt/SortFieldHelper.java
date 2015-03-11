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
 * Is called from sylesheet that transforms foxml to indexable document. 
 * Checks if sortfield with given name was already written before.
 * This can happen if elements of input-xml are not indexed with full path.
 * It might be we have an element title below element metadata 
 * and an element title below element event or source.
 * This results in a sort-field called sort.title that occurs twice in the
 * lucene-document. Having the sort-field twice in the same document
 * results in an error when sorting for this field while searching.
 * 
 * to call this class from stylesheet: 
 * declaration in
 * sylesheet-element:
 * xmlns:sortfield-helper="xalan://de.escidoc.sb.gsearch.xslt.SortFieldHelper"
 * extension-element-prefixes="sortfield-helper" use:
 * <xsl:value-of select="sortfield-helper:checkSortField(sortFieldName)"/>
 * 
 * @author MIH
 */
public class SortFieldHelper {

    private static HashMap<String, String> sortFields = new HashMap<String, String>();
    
    /**
     * constructor.
     * 
     */
    public SortFieldHelper() {
    }

    /**
     * Check if given sortField was already written before.
     * 
     * @param sortFieldName
     *            sortFieldName
     * @return true if sortField was written before
     */
    public static synchronized boolean checkSortField(final String sortFieldName) {
        if (sortFields.get(sortFieldName) == null) {
            sortFields.put(sortFieldName, "");
            return false;
        }
        return true;
    }

}
