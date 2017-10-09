package de.mpg.mpdl.inge.es.dao;

import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;

import de.mpg.mpdl.inge.es.util.ElasticSearchIndexField;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

/**
 * Generic Dao interface for elasticsearch
 * 
 * @author haarlaender
 * 
 * @param <E>
 * @param <Query>
 */
public interface GenericDaoEs<E> {

  /**
   * creates a new object in elasticsearch for the entity with a specific id
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @param vo
   * @return {@link String}
   */
  public String createImmediately(String id, E entity) throws IngeTechnicalException;

  public String create(String id, E entity) throws IngeTechnicalException;

  /**
   * retrieves the object from elasticsearch for a given id
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @return {@link ValueObject}
   */
  public E get(String id) throws IngeTechnicalException;

  /**
   * updates the object with the given id and the new entity in elasticsearch
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @param vo
   * @return {@link String}
   */
  public String updateImmediately(String id, E entity) throws IngeTechnicalException;


  /**
   * deletes the object with the given id in elasticsearch
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @return {@link String}
   */
  public String delete(String id);


  /**
   * searches in elasticsearch with a given searchQuery
   * 
   * @param searchQuery
   * @return
   * @throws IngeTechnicalException
   */
  public SearchRetrieveResponseVO<E> search(SearchRetrieveRequestVO searchQuery)
      throws IngeTechnicalException;


  /**
   * Retrieves the mapping for the index and transforms it into a map of ElasticSearchIndexField
   * objects, including information about the field name, its type and the nested path
   * 
   * @return
   * @throws IngeTechnicalException
   */
  public Map<String, ElasticSearchIndexField> getIndexFields() throws IngeTechnicalException;

}
