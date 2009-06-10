package de.mpg.escidoc.pubman.search;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;
import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.common_presentation.BaseListRetrieverRequestBean;
import de.mpg.escidoc.pubman.itemList.PubItemListSessionBean;
import de.mpg.escidoc.pubman.itemList.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.PubItemResultVO;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.ItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.interfaces.SearchResultElement;
import de.mpg.escidoc.services.search.Search;
import de.mpg.escidoc.services.search.query.ItemContainerSearchResult;
import de.mpg.escidoc.services.search.query.PlainCqlQuery;
import de.mpg.escidoc.services.search.query.SearchQuery.SortingOrder;

/**
 * This bean is an implementation of the BaseListRetrieverRequestBean class for the Search result list.
 * It executes the Search whenever the page is called with a GET cql parameter and a valid cql query.
 * It uses the PubItemListSessionBean as corresponding BasePaginatorListSessionBean.
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class SearchRetrieverRequestBean extends BaseListRetrieverRequestBean<PubItemVOPresentation, SORT_CRITERIA>
{
    
    public static String BEAN_NAME = "SearchRetrieverRequestBean";
    
    private static Logger logger = Logger.getLogger(SearchRetrieverRequestBean.class);
    
    /**
     * The HTTP-GET parameter name for the cql query
     */
    public static String parameterCqlQuery = "cql";
    
    /**
     * The HTTP-GET parameter name for the search type (advanced, simple, ...)
     */
    public static String parameterSearchType = "searchType";
    
    /**
     * The current cqlQuery
     */
    private String cqlQuery;
    
    /**
     * The total number of records from the search request
     */
    private int numberOfRecords;

    /**
     * An instance of the search service.
     */
    private Search searchService;
    
    /**
     * The type of the search (simple, advanced, ...)
     */
    private String searchType;
    
    public SearchRetrieverRequestBean()
    {
        super((PubItemListSessionBean)getSessionBean(PubItemListSessionBean.class), false);
    }
    
    @Override
    public int getTotalNumberOfRecords()
    {
        return numberOfRecords;
    }

    @Override
    public String getType()
    {
        return "SearchResult";
    }

    
    @Override
    public void init()
    {
    	try
        {
            InitialContext initialContext = new InitialContext();
            this.searchService = (Search) initialContext.lookup(Search.SERVICE_NAME);
            
        }
        catch (NamingException e)
        {
            logger.error("Error when trying to find search service.", e);
            error("Did not find Search service");
        }
    }

    /**
     * Reads out the qql query and the search type from HTTP-GET parameeters. If cql is null, an error message is shown. 
     * If search type is null, an default value is set
     */
    @Override
    public void readOutParameters()
    {
        HttpServletRequest request = (HttpServletRequest) getExternalContext().getRequest();
        
        // the following procedure is necessary because of the strange decoding in tomcat, when you fetch the 
        // parameters with the getParameter method. Japanese characters are decoded to a ISO format and this
        // messes up the characters. Therefore we take the complete query string, which is not decoded, extract the cql parameter
        // and decode it with UrlDecode.
        Map<String, String> paramMap = null;
        try
        {
            paramMap = CommonUtils.getDecodedUrlParameterMap(request.getQueryString());
        } catch (UnsupportedEncodingException e)
        {
            logger.error("Error during reading GET parameters.", e);
        }
        
        
        String cql = paramMap.get(parameterCqlQuery);
     
        
        if (cql==null || cql.equals(""))
        {
            setCqlQuery("");
            error("You have to call this page with a parameter \"cql\" and a cql query!"); 
            
        }
        else
        {
           setCqlQuery(cql);
        }
        
             
        String searchType = paramMap.get(parameterSearchType);
        if (searchType==null)
        {
            setSearchType("simple");
        }
        else
        {
            setSearchType(searchType);
        }
        
    }

    /**
     * Calls the search service and requests the items for the current cql query.
     */
    @Override
    public List<PubItemVOPresentation> retrieveList(int offset, int limit, SORT_CRITERIA sc)
    {
        List<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();
        checkSortCriterias(sc);
        try
        {
            PlainCqlQuery query = new PlainCqlQuery(getCqlQuery());
            query.setStartRecord(new PositiveInteger(String.valueOf(offset+1)));
            query.setMaximumRecords(new NonNegativeInteger(String.valueOf(limit)));
            if(!sc.getIndex().equals(""))
            {
                if (sc.getSortOrder().equals("descending"))
                {
                    query.setSortKeysAndOrder(sc.getIndex(), SortingOrder.DESCENDING);
                }
                   
                else
                {
                    query.setSortKeysAndOrder(sc.getIndex(), SortingOrder.ASCENDING);
                } 
            }
            
            ItemContainerSearchResult result = this.searchService.searchForItemContainer(query);
            
            pubItemList =  extractItemsOfSearchResult(result);
            this.numberOfRecords = Integer.parseInt(result.getTotalNumberOfResults().toString());
            
        }
        catch (Exception e)
        {
           error("Error in search!");
           logger.error("Error during search. ", e);
        }
        
        return pubItemList;
    }

    /**
     * Sets the current cql query
     * @param cqlQuery
     */
    public void setCqlQuery(String cqlQuery)
    {
        this.cqlQuery = cqlQuery;
        getBasePaginatorListSessionBean().getParameterMap().put(parameterCqlQuery, cqlQuery);
    }

    /**
     * Returns the current cql query
     * @return
     */
    public String getCqlQuery()
    {
        return cqlQuery;
    }
    
    /**
     * Returns the current cql query without blanks
     * @return
     */
    public String getNormalizedCqlQuery()
    {
        String ret = this.cqlQuery; 
        if (ret != null)
        {
            return java.net.URLEncoder.encode(ret);
        }
        else
        {
            return "";
        }         
    }
    
    /**
     * Helper method that transforms the result of the search into a list of PubItemVOPresentation objects.
     * @param result
     * @return
     */
    private ArrayList<PubItemVOPresentation> extractItemsOfSearchResult( ItemContainerSearchResult result ) { 
        
        List<SearchResultElement> results = result.getResultList();
        
        ArrayList<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();
        for( int i = 0; i < results.size(); i++ ) {
            //check if we have found an item
            if( results.get( i ) instanceof ItemResultVO ) {
                // cast to PubItemResultVO
                ItemResultVO item = (ItemResultVO)results.get( i );
                PubItemResultVO pubItemResult = new PubItemResultVO( item, item.getSearchHitList() ) ;
                PubItemVOPresentation pubItemPres = new PubItemVOPresentation(pubItemResult);
                pubItemList.add( pubItemPres );
            }
        }
        return pubItemList;
    }
    
    @Override
    public String getListPageName()
    {
        return "SearchResultListPage.jsp";
    }

    /**
     * Sets the search type (e.g. advanced, simple, ...) Can be used in the jspf in order to display search type specific elements.
     * @param searchType
     */
    public void setSearchType(String searchType)
    {
        this.searchType = searchType;
        getBasePaginatorListSessionBean().getParameterMap().put(parameterSearchType, searchType);
    }

    /**
     * Returns the search type (e.g. advanced, simple, ...) Can be used in the jspf in order to display search type specific elements
     * @return
     */
    public String getSearchType()
    {
        return searchType;
    }
    
    /**
     * Checks if the selected sorting criteria is currently available. If not (empty string), it displays a warning message to the user.
     * @param sc The sorting criteria to be checked
     */
    protected void checkSortCriterias(SORT_CRITERIA sc)
    {
        if  (sc.getIndex()== null || sc.getIndex().equals(""))
        {
            error(getMessage("depositorWS_sortingNotSupported").replace("$1", getLabel("ENUM_CRITERIA_"+sc.name())));
        }
        
    }

	@Override
	public boolean isItemSpecific() 
	{
		return false;
	}
}
