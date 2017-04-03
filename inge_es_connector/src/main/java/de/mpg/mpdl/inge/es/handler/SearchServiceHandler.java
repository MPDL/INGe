package de.mpg.mpdl.inge.es.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;

import de.mpg.mpdl.inge.es.connector.ElasticSearchTransportClient;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.model.valueobjects.ValueObject;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.services.IngeServiceException;
import de.mpg.mpdl.inge.services.SearchInterface;
import de.mpg.mpdl.inge.util.PropertyReader;

public class SearchServiceHandler implements SearchInterface<QueryBuilder> {

  private final static String SEARCH_INDEX_ITEMS = PropertyReader.getProperty("item_index_name");
  private final static String SEARCH_INDEX_CONTEXTS = PropertyReader.getProperty("context_index_name");
  private final static String SEARCH_INDEX_ORGANIZATIONS = PropertyReader.getProperty("organization_index_name");
  
  private final static Logger logger = Logger.getLogger(SearchServiceHandler.class); 


  @Override
  public SearchRetrieveResponseVO searchForPubItems(SearchRetrieveRequestVO<QueryBuilder> searchQuery)
      throws IngeServiceException {

    return search(searchQuery, SEARCH_INDEX_ITEMS, PubItemVO.class);

  }
  
  @Override
  public SearchRetrieveResponseVO searchForContexts(SearchRetrieveRequestVO<QueryBuilder> searchQuery)
      throws IngeServiceException {

    return search(searchQuery, SEARCH_INDEX_CONTEXTS, ContextVO.class);

  }
  
  @Override
  public SearchRetrieveResponseVO searchForOrganizations(SearchRetrieveRequestVO<QueryBuilder> searchQuery)
      throws IngeServiceException {

    return search(searchQuery, SEARCH_INDEX_ORGANIZATIONS, OrganizationVO.class);

  }


  private SearchRetrieveResponseVO search(SearchRetrieveRequestVO<QueryBuilder> searchQuery,
      String searchIndex, Class resultObjectClass) throws IngeServiceException {
    SearchRetrieveResponseVO srrVO;

    try {
  
      SearchRequestBuilder secondSrb = ElasticSearchTransportClient.INSTANCE.search(searchIndex);
      secondSrb.setQuery(searchQuery.getQueryObject());
     
    
      if (searchQuery.getOffset() != 0) {
        secondSrb.setFrom(searchQuery.getOffset());
      }

      if (searchQuery.getLimit() != 0) {
        secondSrb.setSize(searchQuery.getLimit());
      }

      if (searchQuery.getSortKeys() != null) {
        for (SearchSortCriteria sc : searchQuery.getSortKeys()) {
          secondSrb.addSort(sc.getIndexField(), SortOrder.valueOf(sc.getSortOrder().name()));
        }
      }
      
      //logger.info(secondSrb.toString());
      SearchResponse response2 = secondSrb.get();
      //logger.info(response2.toString());

      srrVO = getSearchRetrieveResponseFromElasticSearchResponse(response2, resultObjectClass);
    } catch (Exception e) {
      throw new IngeServiceException(e.getMessage(), e);
    }


    return srrVO;

  }



  private SearchRetrieveResponseVO getSearchRetrieveResponseFromElasticSearchResponse(
      SearchResponse sr, Class resultObjectClass) throws IOException {
    SearchRetrieveResponseVO srrVO = new SearchRetrieveResponseVO();
    srrVO.setNumberOfRecords((int) sr.getHits().getTotalHits());

    List<SearchRetrieveRecordVO> hitList = new ArrayList<>();
    srrVO.setRecords(hitList);
    for (SearchHit hit : sr.getHits().getHits()) {
      SearchRetrieveRecordVO srr = new SearchRetrieveRecordVO();
      hitList.add(srr);

      ValueObject itemVO = (ValueObject)ElasticSearchTransportClient.INSTANCE.getMapper()
          .readValue(hit.getSourceAsString(), resultObjectClass);

      srr.setData(itemVO);
    }


    return srrVO;
  }



}
