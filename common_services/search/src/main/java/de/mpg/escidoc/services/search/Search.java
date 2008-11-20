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

package de.mpg.escidoc.services.search;

import de.mpg.escidoc.services.search.query.ExportSearchQuery;
import de.mpg.escidoc.services.search.query.ExportSearchResult;
import de.mpg.escidoc.services.search.query.OrgUnitsSearchResult;
import de.mpg.escidoc.services.search.query.SearchQuery;
import de.mpg.escidoc.services.search.query.ItemContainerSearchResult;

/**
 * Interface for the item and container search service.
 * 
 * @author tendres
 * 
 */
public interface Search
{

    /**
     * The name to obtain this service.
     */
    String SERVICE_NAME = "ejb/de/mpg/escidoc/services/search/Search";

    /**
     * Searches for items and containers.
     * 
     * @param query
     *            search query
     * @return search result
     * @throws Exception
     *             if some errors occur during search
     */
    public ItemContainerSearchResult searchForItemContainer(SearchQuery query) throws Exception;

    /**
     * 
     * The SearchAndExport interface is used to search for items.
     * and exports them to various formats.
     * 
     * @param query
     *            an export search query
     * @return  
     *          an export search result
     * @throws Exception
     *             if some errors occur during search
     */
    public ExportSearchResult searchAndExportItems(ExportSearchQuery query) throws Exception;
    
    /**
     * Searches for organizational units.
     * 
     * @param query  search query
     * @return  search result including organizational units
     * @throws Exception  if some errors occur during search
     */
    public OrgUnitsSearchResult searchForOrganizationalUnits(SearchQuery query) throws Exception; 
}
