package de.mpg.mpdl.inge.es.dao;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import de.mpg.mpdl.inge.es.util.ElasticSearchIndexField;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;

/**
 * Generic Dao interface for elasticsearch
 *
 * @author haarlaender
 *
 * @param <E>
 */
public interface GenericDaoEs<E> {

  /**
   * creates a new object in elasticsearch for the entity with a specific id
   */
  String createImmediately(String id, E entity) throws IngeTechnicalException;

  String create(String id, E entity) throws IngeTechnicalException;

  /**
   * retrieves the object from elasticsearch for a given id
   */
  E get(String id) throws IngeTechnicalException;

  /**
   * updates the object with the given id and the new entity in elasticsearch
   *
   */
  String updateImmediately(String id, E entity) throws IngeTechnicalException;

  String update(String id, E entity) throws IngeTechnicalException;

  /**
   * deletes the object with the given id in elasticsearch
   */
  String deleteImmediatly(String id) throws IngeTechnicalException;

  String delete(String id) throws IngeTechnicalException;

  long deleteByQuery(Query query) throws IngeTechnicalException;

  /**
   * Use maxDocs <=1000 in order to disable scrolling for delete-by-query
   */
  long deleteByQuery(Query query, int maxDocs) throws IngeTechnicalException;


  boolean clearScroll(String scrollId) throws IngeTechnicalException;

  /**
   * searches in elasticsearch with a given searchQuery
   *
   * @param searchQuery
   * @return
   * @throws IngeTechnicalException
   */
  SearchRetrieveResponseVO<E> search(Map<String, ElasticSearchIndexField> indexMap, SearchRetrieveRequestVO searchQuery)
      throws IngeTechnicalException;


  ResponseBody<ObjectNode> searchDetailed(JsonNode searchRequest) throws IngeTechnicalException;

  ResponseBody<ObjectNode> searchDetailed(JsonNode searchRequest, long scrollTime) throws IngeTechnicalException;

  ResponseBody<ObjectNode> scrollOn(String scrollId, long scrollTime) throws IngeTechnicalException;


  /**
   * Retrieves the mapping for the index and transforms it into a map of ElasticSearchIndexField
   * objects, including information about the field name, its type and the nested path
   *
   * @return
   * @throws IngeTechnicalException
   */
  Map<String, ElasticSearchIndexField> getIndexFields() throws IngeTechnicalException;

}
