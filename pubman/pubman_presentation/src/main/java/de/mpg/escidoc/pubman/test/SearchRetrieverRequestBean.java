package de.mpg.escidoc.pubman.test;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;

import de.mpg.escidoc.pubman.util.PubItemResultVO;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.ItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.OrderFilter;
import de.mpg.escidoc.services.common.valueobjects.interfaces.ItemContainerSearchResultVO;
import de.mpg.escidoc.services.search.Search;
import de.mpg.escidoc.services.search.query.ItemContainerSearchResult;
import de.mpg.escidoc.services.search.query.PlainCqlQuery;

public class SearchRetrieverRequestBean extends BaseListRetrieverRequestBean<PubItemVOPresentation, OrderFilter>
{
    
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
        if (cql==null)
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
    public List<PubItemVOPresentation> retrieveList(int offset, int limit, OrderFilter additionalFilters)
    {
        List<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();
        try
        {
            PlainCqlQuery query = new PlainCqlQuery(getCqlQuery());
            query.setStartRecord(new PositiveInteger(String.valueOf(offset+1)));
            query.setMaximumRecords(new NonNegativeInteger(String.valueOf(limit)));
            
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
