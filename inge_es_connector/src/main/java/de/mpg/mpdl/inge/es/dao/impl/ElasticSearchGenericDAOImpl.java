package de.mpg.mpdl.inge.es.dao.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesResponse;
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
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.inge.es.connector.ElasticSearchClientProvider;
import de.mpg.mpdl.inge.es.dao.GenericDaoEs;
import de.mpg.mpdl.inge.es.util.ElasticSearchIndexField;
import de.mpg.mpdl.inge.es.util.ElasticSearchIndexField.Type;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.json.util.JsonObjectMapperFactory;
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
public class ElasticSearchGenericDAOImpl<E> implements GenericDaoEs<E> {

  private final static Logger logger = LogManager.getLogger(ElasticSearchGenericDAOImpl.class);

  @Autowired
  ElasticSearchClientProvider client;

  ObjectMapper mapper = JsonObjectMapperFactory.getObjectMapper();


  private String indexName;

  private String indexType;

  private Class<E> typeParameterClass;


  public ElasticSearchGenericDAOImpl(String indexName, String indexType, Class<E> typeParameterClass) {
    this.indexName = indexName;
    this.indexType = indexType;
    this.typeParameterClass = typeParameterClass;
  }


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
      IndexResponse indexResponse =
          client.getClient().prepareIndex().setIndex(indexName).setType(indexType).setId(id)
              .setSource(mapper.writeValueAsBytes(entity)).get();
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
          client.getClient().prepareIndex().setIndex(indexName).setType(indexType).setId(id)
              .setRefreshPolicy(RefreshPolicy.IMMEDIATE)
              .setSource(mapper.writeValueAsBytes(entity)).get();
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
      GetResponse getResponse =
          client.getClient().prepareGet().setIndex(indexName).setType(indexType).setId(id).get();
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
      UpdateResponse updateResponse =
          client.getClient().prepareUpdate().setIndex(indexName).setType(indexType).setId(id)
              .setRefreshPolicy(RefreshPolicy.IMMEDIATE).setDoc(mapper.writeValueAsBytes(entity))
              .get();
      return Long.toString(updateResponse.getVersion());
    } catch (Exception e) {
      throw new IngeTechnicalException(e);
    }

  }

  public String update(String id, E entity) throws IngeTechnicalException {

    try {
      UpdateResponse updateResponse =
          client.getClient().prepareUpdate().setIndex(indexName).setType(indexType).setId(id)
              .setDoc(mapper.writeValueAsBytes(entity)).get();
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
  public String delete(String id) {

    DeleteResponse deleteResponse =
        client.getClient().prepareDelete().setIndex(indexName).setType(indexType).setId(id).get();
    return deleteResponse.getId();

  }

  public SearchRetrieveResponseVO<E> search(SearchRetrieveRequestVO searchQuery)
      throws IngeTechnicalException {

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

      if (searchQuery.getLimit() != -1) {
        srb.setSize(searchQuery.getLimit());
      } else {
        srb.setSize(10000);
      }

      if (searchQuery.getSortKeys() != null) {
        for (SearchSortCriteria sc : searchQuery.getSortKeys()) {
          srb.addSort(sc.getIndexField(), SortOrder.valueOf(sc.getSortOrder().name()));
        }
      }

      logger.debug(srb.toString());
      SearchResponse response = srb.get();

      srrVO = getSearchRetrieveResponseFromElasticSearchResponse(response);
    } catch (Exception e) {
      throw new IngeTechnicalException(e.getMessage(), e);
    }


    return srrVO;

  }

  public SearchResponse searchDetailed(SearchSourceBuilder ssb, Scroll scroll)
      throws IngeTechnicalException {

    try {
      SearchRequestBuilder srb =
          client.getClient().prepareSearch(indexName).setTypes(indexType).setSource(ssb);
      if (scroll != null) {
        srb.setScroll(scroll);
      }
      logger.debug(srb.toString());
      return srb.get();
    } catch (Exception e) {
      throw new IngeTechnicalException(e.getMessage(), e);
    }


  }

  public SearchResponse searchDetailed(SearchSourceBuilder ssb) throws IngeTechnicalException {

    return searchDetailed(ssb, null);


  }

  public SearchResponse scrollOn(String scrollId, Scroll scroll) throws IngeTechnicalException {

    try {
      return client.getClient().prepareSearchScroll(scrollId).setScroll(scroll).get();
    } catch (Exception e) {
      throw new IngeTechnicalException(e.getMessage(), e);
    }


  }



  private SearchRetrieveResponseVO<E> getSearchRetrieveResponseFromElasticSearchResponse(
      SearchResponse sr) throws IOException {
    SearchRetrieveResponseVO<E> srrVO = new SearchRetrieveResponseVO<E>();
    srrVO.setOriginalResponse(sr);
    srrVO.setNumberOfRecords((int) sr.getHits().getTotalHits());

    List<SearchRetrieveRecordVO<E>> hitList = new ArrayList<>();
    srrVO.setRecords(hitList);
    for (SearchHit hit : sr.getHits().getHits()) {
      SearchRetrieveRecordVO<E> srr = new SearchRetrieveRecordVO<E>();
      hitList.add(srr);

      E itemVO = mapper.readValue(hit.getSourceAsString(), typeParameterClass);

      srr.setData(itemVO);
      srr.setPersistenceId(hit.getId());
    }


    return srrVO;
  }



  public Map<String, ElasticSearchIndexField> getIndexFields() throws IngeTechnicalException {
    String realIndexName = indexName;

    GetAliasesResponse aliasResp =
        client.getClient().admin().indices().prepareGetAliases(indexName).get();
    if (!aliasResp.getAliases().isEmpty()) {
      realIndexName = aliasResp.getAliases().keys().iterator().next().value;

    }
    GetMappingsResponse resp =
        client.getClient().admin().indices().prepareGetMappings(realIndexName).addTypes(indexType)
            .get();
    MappingMetaData mmd = resp.getMappings().get(realIndexName).get(indexType);

    Map<String, ElasticSearchIndexField> map =
        ElasticSearchIndexField.Factory.createIndexMapFromElasticsearch(mmd);
    ElasticSearchIndexField allField = new ElasticSearchIndexField();
    allField.setIndexName("_all");
    allField.setType(Type.TEXT);
    map.put("_all", allField);
    return map;

  }



}
