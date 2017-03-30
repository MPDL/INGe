package de.mpg.mpdl.inge.es.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;

import de.mpg.mpdl.inge.es.connector.ElasticSearchTransportClient;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchQueryVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.services.IngeServiceException;
import de.mpg.mpdl.inge.services.SearchInterface;

public class SearchServiceHandler implements SearchInterface<QueryBuilder> {

  private final static String SEARCH_INDEX_ITEMS = "pure_search";
  private final static String SEARCH_INDEX_CONTEXTS = "pure_contexts";
  private final static String SEARCH_INDEX_ORGANIZATIONS = "organizational_units";


  @Override
  public SearchRetrieveResponseVO searchForPubItems(SearchQueryVO<QueryBuilder> searchQuery)
      throws IngeServiceException {

    return search(searchQuery, SEARCH_INDEX_ITEMS, PubItemVO.class);

  }
  
  @Override
  public SearchRetrieveResponseVO searchForContexts(SearchQueryVO<QueryBuilder> searchQuery)
      throws IngeServiceException {

    return search(searchQuery, SEARCH_INDEX_CONTEXTS, ContextVO.class);

  }
  
  @Override
  public SearchRetrieveResponseVO searchForOrganizations(SearchQueryVO<QueryBuilder> searchQuery)
      throws IngeServiceException {

    return search(searchQuery, SEARCH_INDEX_ORGANIZATIONS, OrganizationVO.class);

  }


  private SearchRetrieveResponseVO search(SearchQueryVO<QueryBuilder> searchQuery,
      String searchIndex, Class resultObjectClass) throws IngeServiceException {

    SearchRequestBuilder srb = ElasticSearchTransportClient.INSTANCE.search(searchIndex);
    SearchRetrieveResponseVO srrVO;
    try {
      srb.setQuery(searchQuery.getQueryObject());

      if (searchQuery.getOffset() != 0) {
        srb.setFrom(searchQuery.getOffset());
      }

      if (searchQuery.getLimit() != 0) {
        srb.setSize(searchQuery.getLimit());
      }

      if (searchQuery.getSortKeys() != null) {
        for (SearchSortCriteria sc : searchQuery.getSortKeys()) {
          srb.addSort(sc.getIndexField(), SortOrder.valueOf(sc.getSortOrder().name()));
        }
      }

      SearchResponse response = srb.get();

      srrVO = getSearchRetrieveResponseFromElasticSearchResponse(response, resultObjectClass);
    } catch (IOException e) {
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

      PubItemVO itemVO = (PubItemVO)ElasticSearchTransportClient.INSTANCE.getMapper()
          .readValue(hit.getSourceAsString(), resultObjectClass);

      srr.setData(itemVO);
    }


    return srrVO;
  }



}
