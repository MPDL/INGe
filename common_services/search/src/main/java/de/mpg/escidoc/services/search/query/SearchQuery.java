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

package de.mpg.escidoc.services.search.query;

import java.io.Serializable;

import de.mpg.escidoc.services.search.ItemContainerSearch.IndexDatabaseSelector;

/**
 * This is the base class for search queries. A search query always consists of
 * a index database selector. The default selector is chosen if none is
 * supplied.
 * 
 * @author endres
 * 
 */
public class SearchQuery implements Serializable
{

    private static final long serialVersionUID = 1L;

    /** Selects the lucene query index. */
    private IndexDatabaseSelector indexSelector;

    /** The default index database to be searched by the query. */
    private static final IndexDatabaseSelector INDEX_DEFAULT = IndexDatabaseSelector.All;

    /**
     * Constructor with index database selector.
     * 
     * @param indexSelector
     *            index database selector
     */
    public SearchQuery(IndexDatabaseSelector indexSelector)
    {
        this.indexSelector = indexSelector;
    }

    /**
     * Default constructor with no index database selector. Default is chosen.
     */
    public SearchQuery()
    {
        this.indexSelector = INDEX_DEFAULT;
    }

    /**
     * Get the index database selector.
     * 
     * @return index database selector
     */
    public IndexDatabaseSelector getIndexSelector()
    {
        return indexSelector;
    }
}
