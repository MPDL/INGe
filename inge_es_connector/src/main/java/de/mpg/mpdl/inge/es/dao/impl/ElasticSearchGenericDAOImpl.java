package de.mpg.mpdl.inge.es.dao.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.mpg.mpdl.inge.dao.GenericDao;
import de.mpg.mpdl.inge.es.connector.ElasticSearchTransportClientProvider;
import de.mpg.mpdl.inge.es.connector.ModelMapper;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.model.valueobjects.ValueObject;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.services.IngeServiceException;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * ElasticSearchTransportClient enables elasticsearch accessibility
 * 
 * @author frank (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class ElasticSearchGenericDAOImpl<E extends ValueObject> implements
    GenericDao<E, QueryBuilder> {


  @Autowired
  ElasticSearchTransportClientProvider client;

  @Autowired
  ModelMapper mapper;



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
  public String create(String id, E entity) throws IngeServiceException {
    try {
      IndexResponse indexResponse =
          client.getClient().prepareIndex().setIndex(indexName).setType(indexType).setId(id)
              .setSource(mapper.writeValueAsBytes(entity)).get();
      return indexResponse.getId();
    } catch (JsonProcessingException e) {
      throw new IngeServiceException(e);
    }

  }

  /**
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @return {@link ValueObject}
   */
  public E get(String id) throws IngeServiceException {
    try {
      GetResponse getResponse =
          client.getClient().prepareGet().setIndex(indexName).setType(indexType).setId(id).get();
      return mapper.readValue(getResponse.getSourceAsBytes(), typeParameterClass);
    } catch (Exception e) {
      throw new IngeServiceException(e);
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
  public String update(String id, E entity) throws IngeServiceException {

    try {
      UpdateResponse updateResponse =
          client.getClient().prepareUpdate().setIndex(indexName).setType(indexType).setId(id)
              .setDoc(mapper.writeValueAsBytes(entity)).get();
      return Long.toString(updateResponse.getVersion());
    } catch (Exception e) {
      throw new IngeServiceException(e);
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

  public SearchRetrieveResponseVO<E> search(SearchRetrieveRequestVO<QueryBuilder> searchQuery)
      throws IngeServiceException {

    SearchRetrieveResponseVO<E> srrVO;
    try {

      SearchRequestBuilder secondSrb =
          client.getClient().prepareSearch(indexName).setTypes(indexType);
      secondSrb.setQuery(searchQuery.getQueryObject());


      if (searchQuery.getOffset() != 0) {
        secondSrb.setFrom(searchQuery.getOffset());
      }

      if (searchQuery.getLimit() != -1) {
        secondSrb.setSize(searchQuery.getLimit());
      } else {
        secondSrb.setSize(10000);
      }

      if (searchQuery.getSortKeys() != null) {
        for (SearchSortCriteria sc : searchQuery.getSortKeys()) {
          secondSrb.addSort(sc.getIndexField(), SortOrder.valueOf(sc.getSortOrder().name()));
        }
      }

      // logger.info(secondSrb.toString());
      SearchResponse response2 = secondSrb.get();
      // logger.info(response2.toString());

      srrVO = getSearchRetrieveResponseFromElasticSearchResponse(response2);
    } catch (Exception e) {
      throw new IngeServiceException(e.getMessage(), e);
    }


    return srrVO;

  }

  private SearchRetrieveResponseVO<E> getSearchRetrieveResponseFromElasticSearchResponse(
      SearchResponse sr) throws IOException {
    SearchRetrieveResponseVO<E> srrVO = new SearchRetrieveResponseVO<E>();
    srrVO.setNumberOfRecords((int) sr.getHits().getTotalHits());

    List<SearchRetrieveRecordVO<E>> hitList = new ArrayList<>();
    srrVO.setRecords(hitList);
    for (SearchHit hit : sr.getHits().getHits()) {
      SearchRetrieveRecordVO<E> srr = new SearchRetrieveRecordVO<E>();
      hitList.add(srr);

      E itemVO = mapper.readValue(hit.getSourceAsString(), typeParameterClass);

      srr.setData(itemVO);
    }


    return srrVO;
  }



}