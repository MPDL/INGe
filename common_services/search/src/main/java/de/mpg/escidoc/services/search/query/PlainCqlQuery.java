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

import de.mpg.escidoc.services.search.ItemContainerSearch.IndexDatabaseSelector;

/**
 * This query is used if you want to use a plain cql query.
 * 
 * @author endres
 * 
 */
public class PlainCqlQuery extends SearchQuery implements StandardSearchQuery
{
    /** Serial identifier. */
    private static final long serialVersionUID = 1L;
    /** Cql query. */
    private String cqlQuery = null;

    /**
     * {@inheritDoc}
     */
    public String getCqlQuery()
    {
        return cqlQuery;
    }

    /**
     * Create a query.
     * 
     * @param cqlQuery
     *            cql query
     */
    public PlainCqlQuery(String cqlQuery)
    {
        super();
        this.cqlQuery = cqlQuery;
    }

    /**
     * Create a query with a index database selector.
     * 
     * @param cqlQuery
     *            cql query
     * @param selector
     *            index database selector
     */
    public PlainCqlQuery(String cqlQuery, IndexDatabaseSelector selector)
    {
        super(selector);
        this.cqlQuery = cqlQuery;
    }
}
