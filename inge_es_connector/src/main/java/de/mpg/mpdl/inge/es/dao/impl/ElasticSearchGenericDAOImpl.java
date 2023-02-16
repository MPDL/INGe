package de.mpg.mpdl.inge.es.dao.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.inge.es.connector.ElasticSearchClientProvider;
import de.mpg.mpdl.inge.es.dao.GenericDaoEs;
import de.mpg.mpdl.inge.es.util.ElasticSearchIndexField;
import de.mpg.mpdl.inge.es.util.ElasticSearchIndexField.Type;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

/**
 * ElasticSearchClient enables elasticsearch accessibility
 * 
 * @author frank (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public abstract class ElasticSearchGenericDAOImpl<E> implements GenericDaoEs<E> {

  private static final Logger logger = Logger.getLogger(ElasticSearchGenericDAOImpl.class);

  @Autowired
  protected ElasticSearchClientProvider client;

  protected ObjectMapper mapper = MapperFactory.getObjectMapper();

  protected String indexName;

  protected String indexType;

  protected Class<E> typeParameterClass;

  private static final int DEFAULT_SEARCH_SIZE = 100;
  private static final int MAX_SEARCH_SIZE = 10000;


  public ElasticSearchGenericDAOImpl(String indexName, String indexType, Class<E> typeParameterClass) {
    this.indexName = indexName;
    this.indexType = indexType;
    this.typeParameterClass = typeParameterClass;
  }


  protected JsonNode applyCustomValues(E entity) {
    JsonNode node = mapper.valueToTree(entity);
    return node;
  }

  protected abstract String[] getSourceExclusions();


  /**
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @param vo
   * @return {@link String}
   */
  public String create(String id, E entity) throws IngeTechnicalException {
    try {
      IndexResponse indexResponse = client.getClient().prepareIndex().setIndex(indexName).setType(indexType).setId(id)
          .setSource(mapper.writeValueAsBytes(applyCustomValues(entity)), XContentType.JSON).get();
      return indexResponse.getId();

    } catch (JsonProcessingException e) {
      throw new IngeTechnicalException(e);
    }


  }

  /**
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @param vo
   * @return {@link String}
   */
  public String createImmediately(String id, E entity) throws IngeTechnicalException {
    try {
      IndexResponse indexResponse =
          client.getClient().prepareIndex().setIndex(indexName).setType(indexType).setId(id).setRefreshPolicy(RefreshPolicy.IMMEDIATE)
              .setSource(mapper.writeValueAsBytes(applyCustomValues(entity)), XContentType.JSON).get();
      return indexResponse.getId();

    } catch (JsonProcessingException e) {
      throw new IngeTechnicalException(e);
    }


  }

  /**
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @return {@link ValueObject}
   */
  public E get(String id) throws IngeTechnicalException {
    try {
      GetResponse getResponse = client.getClient().prepareGet().setIndex(indexName).setType(indexType).setId(id).get();
      return mapper.readValue(getResponse.getSourceAsBytes(), typeParameterClass);
    } catch (Exception e) {
      throw new IngeTechnicalException(e);
    }


  }

  /**
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @param vo
   * @return {@link String}
   */
  public String updateImmediately(String id, E entity) throws IngeTechnicalException {

    try {
      UpdateResponse updateResponse = client.getClient().prepareUpdate().setIndex(indexName).setType(indexType).setId(id)
          .setRefreshPolicy(RefreshPolicy.IMMEDIATE).setDoc(mapper.writeValueAsBytes(applyCustomValues(entity)), XContentType.JSON).get();
      return Long.toString(updateResponse.getVersion());
    } catch (Exception e) {
      throw new IngeTechnicalException(e);
    }

  }

  public String update(String id, E entity) throws IngeTechnicalException {

    try {
      UpdateResponse updateResponse = client.getClient().prepareUpdate().setIndex(indexName).setType(indexType).setId(id)
          .setDoc(mapper.writeValueAsBytes(applyCustomValues(entity))).get();
      return Long.toString(updateResponse.getVersion());
    } catch (Exception e) {
      throw new IngeTechnicalException(e);
    }

  }


  /**
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @return {@link String}
   */
  public String deleteImmediatly(String id) {

    DeleteResponse deleteResponse =
        client.getClient().prepareDelete().setIndex(indexName).setType(indexType).setId(id).setRefreshPolicy(RefreshPolicy.IMMEDIATE).get();
    return deleteResponse.getId();

  }

  /**
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @return {@link String}
   */
  public String delete(String id) {

    DeleteResponse deleteResponse = client.getClient().prepareDelete().setIndex(indexName).setType(indexType).setId(id).get();
    return deleteResponse.getId();

  }


  public long deleteByQuery(QueryBuilder query) throws IngeTechnicalException {

    try {
      BulkByScrollResponse resp = DeleteByQueryAction.INSTANCE.newRequestBuilder(client.getClient()).filter(query).source(indexName).get();
      return resp.getDeleted();
    } catch (Exception e) {
      throw new IngeTechnicalException(e);
    }

  }


  public SearchRetrieveResponseVO<E> search(SearchRetrieveRequestVO searchQuery) throws IngeTechnicalException {

    SearchRetrieveResponseVO<E> srrVO;
    try {


      SearchRequestBuilder srb = client.getClient().prepareSearch(indexName).setTypes(indexType);
      if (searchQuery.getQueryBuilder() != null) {
        srb.setQuery(searchQuery.getQueryBuilder());
      }

      if (searchQuery.getAggregationBuilders() != null) {
        for (AggregationBuilder aggBuilder : searchQuery.getAggregationBuilders()) {
          srb.addAggregation(aggBuilder);
        }
      }



      if (searchQuery.getOffset() != 0) {
        srb.setFrom(searchQuery.getOffset());
      }

      if (searchQuery.getLimit() == -2) {
        srb.setSize(ElasticSearchGenericDAOImpl.MAX_SEARCH_SIZE);
      } else if (searchQuery.getLimit() == -1) {
        srb.setSize(ElasticSearchGenericDAOImpl.DEFAULT_SEARCH_SIZE);
      } else {
        srb.setSize(searchQuery.getLimit());
      }

      if (searchQuery.getSortKeys() != null) {
        for (SearchSortCriteria sc : searchQuery.getSortKeys()) {
          srb.addSort(sc.getIndexField(), SortOrder.valueOf(sc.getSortOrder().name()));
        }
      }

      if (getSourceExclusions() != null) {
        srb.setFetchSource(null, getSourceExclusions());
      }


      if (searchQuery.getScrollTime() != -1) {
        srb.setScroll(new Scroll(new TimeValue(searchQuery.getScrollTime())));
      }
      logger.debug(srb.toString());
      SearchResponse response = srb.get();

      srrVO = getSearchRetrieveResponseFromElasticSearchResponse(response, typeParameterClass);
    } catch (Exception e) {
      throw new IngeTechnicalException(e.getMessage(), e);
    }


    return srrVO;

  }

  public SearchResponse searchDetailed(SearchSourceBuilder ssb, long scrollTime) throws IngeTechnicalException {

    try {
      SearchRequestBuilder srb = client.getClient().prepareSearch(indexName).setTypes(indexType).setSource(ssb);
      if (scrollTime != -1) {
        srb.setScroll(new Scroll(new TimeValue(scrollTime)));
      }

      if (getSourceExclusions() != null && getSourceExclusions().length > 0) {
        if (ssb.fetchSource() == null) {
          srb.setFetchSource(null, getSourceExclusions());
        } else if (ssb.fetchSource().fetchSource()) {
          String[] excludes = ssb.fetchSource().excludes();
          String[] both = Stream
              .concat(Arrays.stream(getSourceExclusions()), (excludes != null ? Arrays.stream(excludes) : Arrays.stream(new String[0])))
              .toArray(String[]::new);
          ssb.fetchSource(ssb.fetchSource().includes(), both);
        }
      }


      logger.debug(srb.toString());
      return srb.get();
    } catch (Exception e) {
      throw new IngeTechnicalException(e.getMessage(), e);
    }


  }

  public SearchResponse searchDetailed(SearchSourceBuilder ssb) throws IngeTechnicalException {

    return searchDetailed(ssb, -1);


  }

  public SearchResponse scrollOn(String scrollId, long scrollTime) throws IngeTechnicalException {

    try {
      return client.getClient().prepareSearchScroll(scrollId).setScroll(new Scroll(new TimeValue(scrollTime))).get();
    } catch (Exception e) {
      throw new IngeTechnicalException(e.getMessage(), e);
    }


  }


  public static <E> SearchRetrieveResponseVO<E> getSearchRetrieveResponseFromElasticSearchResponse(SearchResponse sr, Class<E> clazz)
      throws IOException {
    SearchRetrieveResponseVO<E> srrVO = new SearchRetrieveResponseVO<E>();
    srrVO.setOriginalResponse(sr);
    srrVO.setNumberOfRecords((int) sr.getHits().getTotalHits());
    srrVO.setScrollId(sr.getScrollId());

    List<SearchRetrieveRecordVO<E>> hitList = new ArrayList<>();
    srrVO.setRecords(hitList);
    for (SearchHit hit : sr.getHits().getHits()) {
      SearchRetrieveRecordVO<E> srr = new SearchRetrieveRecordVO<E>();
      hitList.add(srr);

      E itemVO = MapperFactory.getObjectMapper().readValue(hit.getSourceAsString(), clazz);

      srr.setData(itemVO);
      srr.setPersistenceId(hit.getId());
    }


    return srrVO;
  }

  //SP: Alias-Suche funktioniert in ES 6.1 nicht mehr wie erwartet
  public Map<String, ElasticSearchIndexField> getIndexFields() throws IngeTechnicalException {
    //    String realIndexName = indexName;
    //
    //    GetAliasesResponse aliasResp = client.getClient().admin().indices().prepareGetAliases(indexName).get();
    //    if (!aliasResp.getAliases().isEmpty()) {
    //      realIndexName = aliasResp.getAliases().keys().iterator().next().value;
    //    }

    //    GetMappingsResponse resp = client.getClient().admin().indices().prepareGetMappings(realIndexName).addTypes(indexType).get();
    GetMappingsResponse resp = this.client.getClient().admin().indices().prepareGetMappings(this.indexName).addTypes(this.indexType).get();

    if (resp.getMappings().isEmpty() == false) { // SP: avoiding NullPointerException
      MappingMetaData mmd = resp.getMappings().iterator().next().value.get(this.indexType);

      Map<String, ElasticSearchIndexField> map = ElasticSearchIndexField.Factory.createIndexMapFromElasticsearch(mmd);
      ElasticSearchIndexField allField = new ElasticSearchIndexField();
      allField.setIndexName("_all");
      allField.setType(Type.TEXT);
      map.put("_all", allField);
      return map;
    }

    return new HashMap<String, ElasticSearchIndexField>();
  }

}
