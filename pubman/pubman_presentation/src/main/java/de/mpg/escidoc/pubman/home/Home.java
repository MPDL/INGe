/*
*
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
* Copyright 2006-20110 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.home;


import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.search.SearchRetrieverRequestBean;
import de.mpg.escidoc.pubman.search.bean.criterion.Criterion;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.interfaces.SearchResultElement;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.search.Search;
import de.mpg.escidoc.services.search.query.ItemContainerSearchResult;
import de.mpg.escidoc.services.search.query.PlainCqlQuery;
import de.mpg.escidoc.services.search.query.SearchQuery;
import de.mpg.escidoc.services.search.query.SearchQuery.SortingOrder;

/**
 * Fragment class for the corresponding Home-JSP.
 * 
 * @author: Thomas Diebäcker, created 08.02.2007
 * @version: $Revision$ $LastChangedDate$
 * Revised by DiT: 14.08.2007
 */
public class Home extends FacesBean
{
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(Home.class);
    public static final String BEAN_NAME = "Home";

    // Faces navigation string
    public final static String LOAD_HOME = "loadHome";


    /**
     * Public constructor.
     */
    public Home()
    {
        this.init();
    }

    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation. 
     */
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
    }
    
    public List<PubItemVOPresentation> getLatest() throws Exception
    {
        InitialContext ictx = new InitialContext();
        Search search = (Search)ictx.lookup(Search.SERVICE_NAME);
        //SearchRetrieverRequestBean srrb = (SearchRetrieverRequestBean)ictx.lookup(SearchRetrieverRequestBean.BEAN_NAME);
        String cqlQuery = "escidoc.property.content-model.objid=" + PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication");
        SearchQuery cql = new PlainCqlQuery(cqlQuery);
        cql.setMaximumRecords("4");
        cql.setSortKeysAndOrder("sort.escidoc.last-modification-date", SortingOrder.DESCENDING);
        ItemContainerSearchResult icsr =  search.searchForItemContainer(cql);
        List<PubItemVOPresentation> list = SearchRetrieverRequestBean.extractItemsOfSearchResult(icsr);
        return list;
    }


}
