package de.mpg.escidoc.pubman.test;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;

import de.mpg.escidoc.pubman.test.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.escidoc.pubman.util.PubItemResultVO;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.ItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.OrderFilter;
import de.mpg.escidoc.services.common.valueobjects.interfaces.ItemContainerSearchResultVO;
import de.mpg.escidoc.services.search.Search;
import de.mpg.escidoc.services.search.query.ItemContainerSearchResult;
import de.mpg.escidoc.services.search.query.PlainCqlQuery;
import de.mpg.escidoc.services.search.query.SearchQuery.SortingOrder;

public class SearchRetrieverRequestBean extends BaseListRetrieverRequestBean<PubItemVOPresentation, SORT_CRITERIA>
{
    /*
    protected enum SORT_CRITERIA
    {
        TITLE ("escidoc.title"),
        EVENT_TITLE ("escidoc.any-event"),
        SOURCE_TITLE ("escidoc.any-source"),
        GENRE ("escidoc.genre"),
        DATE ("escidoc.any-dates"),
        CREATOR ("escidoc.complete-name"),
        PUBLISHING_INFO ("escidoc.publisher"),
        MODIFICATION_DATE ("escidoc.last-modification-date");
        
        private String index;
        
        SORT_CRITERIA(String index)
        {
            this.setIndex(index);
        }

        public void setIndex(String index)
        {
            this.index = index;
        }

        public String getIndex()
        {
            return index;
        }
        
    }
    */
    
    
    public static String BEAN_NAME = "SearchRetrieverRequestBean";
    
    public static String parameterCqlQuery = "cql";
    
    public static String parameterSearchType = "searchType";
    
    private String cqlQuery;
    
    private int numberOfRecords;

    private Search searchService;
    
    private String searchType;
    
    public SearchRetrieverRequestBean()
    {
        super((PubItemListSessionBean)getSessionBean(PubItemListSessionBean.class));
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
            
            /*
            List<SelectItem> sortCriteriaSelectItems = new ArrayList<SelectItem>();
            for(SORT_CRITERIA sc : SORT_CRITERIA.values())
            {
                sortCriteriaSelectItems.add(new SelectItem(sc.getIndex(), sc.name()));
            }
            */
            
        }
        catch (NamingException e)
        {
            error("Did not find Search service");
        }
    }

    @Override
    public void readOutParameters()
    {
        String cql = getExternalContext().getRequestParameterMap().get(parameterCqlQuery);
        if (cql==null || cql.equals(""))
        {
            setCqlQuery("");
            error("You have to call this page with a parameter \"cql\" and a cql query!"); 
            
        }
        else
        {
            setCqlQuery(URLDecoder.decode(cql));
        }
        
        
        String searchType = getExternalContext().getRequestParameterMap().get(parameterSearchType);
        if (searchType==null)
        {
            setSearchType("simple");
        }
        else
        {
            setSearchType(URLDecoder.decode(searchType));
        }
        
    }

    @Override
    public List<PubItemVOPresentation> retrieveList(int offset, int limit, SORT_CRITERIA sc)
    {
        List<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();
        try
        {
            PlainCqlQuery query = new PlainCqlQuery(getCqlQuery());
            query.setStartRecord(new PositiveInteger(String.valueOf(offset+1)));
            query.setMaximumRecords(new NonNegativeInteger(String.valueOf(limit)));
            query.setSortKeys(sc.getIndex());
            if (sc.getSortOrder().equals("descending"))
            {
                query.setSortKeysAndOrder(sc.getIndex(), SortingOrder.DESCENDING);
            }
               
            else
            {
                query.setSortKeysAndOrder(sc.getIndex(), SortingOrder.ASCENDING);
            } 
            
            ItemContainerSearchResult result = this.searchService.searchForItemContainer(query);
            
            pubItemList =  extractItemsOfSearchResult(result);
            //TODO To be changed
            this.numberOfRecords = Integer.parseInt(result.getTotalNumberOfResults().toString());
        }
        catch (Exception e)
        {
           error("Error in search!");
        }
        
        return pubItemList;
    }

    public void setCqlQuery(String cqlQuery)
    {
        this.cqlQuery = cqlQuery;
        getBasePaginatorListSessionBean().getParameterMap().put(parameterCqlQuery, cqlQuery);
    }

    public String getCqlQuery()
    {
        return cqlQuery;
    }
    
    private ArrayList<PubItemVOPresentation> extractItemsOfSearchResult( ItemContainerSearchResult result ) { 
        
        List<ItemContainerSearchResultVO> results = result.getResultList();
        
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

    public void setSearchType(String searchType)
    {
        this.searchType = searchType;
        getBasePaginatorListSessionBean().getParameterMap().put(parameterSearchType, searchType);
    }

    public String getSearchType()
    {
        return searchType;
    }
}
