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

import java.util.List;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.interfaces.ItemContainerSearchResultVO;
import de.mpg.escidoc.services.search.query.ExportSearchQuery;
import de.mpg.escidoc.services.search.query.SearchQuery;
import de.mpg.escidoc.services.search.query.StandardSearchQuery;

/**
 * Interface for the item and container search service.
 * @author tendres
 *
 */
public interface ItemContainerSearch {

	/**
     * The name to obtain this service.
     */
    String SERVICE_NAME = "ejb/de/mpg/escidoc/services/search/ItemContainerSearch";
    
    /**
     * Selects which index database which queried at the eSciDoc coreservice.
     *
     */
    public enum IndexDatabaseSelector {
    	/** This index database has all languages indexed, no stemming */
    	All,
    	/** This index database has the german languages indexed, with stemming */
    	English,
    	/** This index database has the english languages indexed, with  stemming */
    	German
    }
    
    /**
     * The Search interface searches for items and containers.
     * 
     * @param query
     * @return
     * @throws TechnicalException
     */
    public List<ItemContainerSearchResultVO> search( StandardSearchQuery query ) throws Exception;
    
    /**
     * The SearchAndExport interface is used to search for items and container 
     * and exports them to various formats. Only items are supported.
     * 
     * @param cqlSearchString
     * @param language
     * @param exportFormat
     * @param outputFormat
     * @return
     * @throws TechnicalException
     * @throws Exception 
     */
    public byte[] searchAndExport( ExportSearchQuery query ) throws Exception;
}
