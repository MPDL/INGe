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
import java.util.ArrayList;
import java.util.List;
import org.apache.axis.types.NonNegativeInteger;

import de.mpg.escidoc.services.common.valueobjects.ItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.interfaces.SearchResultElement;

/**
 * Search result for a standard search query. The result consist of
 * ItemContainerSearchResultVOs which is the ADT type of ItemResultVO and
 * ContainerResultVO, and it consists of the corresponding cql query.
 * 
 * @author endres
 * 
 */
public class ItemContainerSearchResult extends SearchResult implements Serializable
{

    /** Serial identifier. */
    private static final long serialVersionUID = 1L;
    /** Result list. */
    private List<SearchResultElement> resultList = null;

    /**
     * Create a result.
     * 
     * @param results
     *            list of results
     * @param cqlQuery
     *            cql query
     * @param totalNumberOfResults
     *          total number of search results
     */
    public ItemContainerSearchResult(
            List<SearchResultElement> results,
            String cqlQuery, 
            NonNegativeInteger totalNumberOfResults)
    {
        super(cqlQuery, totalNumberOfResults);
        this.resultList = results;
    }

    /**
     * Getter for the results.
     * 
     * @return list of results
     */
    public List<SearchResultElement> getResultList()
    {
        return resultList;
    }
    
    /**
     * extracts items out of the search result list.
     * @return  list of items
     */
    public ArrayList<ItemResultVO> extractItemsOfSearchResult()
    { 
        
        ArrayList<ItemResultVO> itemList = new ArrayList<ItemResultVO>();
        for (int i = 0; i < resultList.size(); i++)
        {
            //check if we have found an item
            if (resultList.get(i) instanceof ItemResultVO)
            {
                // cast to PubItemResultVO
                ItemResultVO item = (ItemResultVO) resultList.get(i);
                itemList.add(item);
            }
        }
        return itemList;
    }
}
