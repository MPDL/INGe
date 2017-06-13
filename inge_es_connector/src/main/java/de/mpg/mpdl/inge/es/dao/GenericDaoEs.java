package de.mpg.mpdl.inge.es.dao;

import de.mpg.mpdl.inge.model.exception.IngeServiceException;
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
public interface GenericDaoEs<E extends ValueObject, Query> {

  /**
   * creates a new object in elasticsearch for the entity with a specific id
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @param vo
   * @return {@link String}
   */
  public String create(String id, E entity) throws IngeServiceException;

  public String createNotImmediately(String id, E entity) throws IngeServiceException;

  /**
   * retrieves the object from elasticsearch for a given id
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @return {@link ValueObject}
   */
  public E get(String id) throws IngeServiceException;

  /**
   * updates the object with the given id and the new entity in elasticsearch
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @param vo
   * @return {@link String}
   */
  public String update(String id, E entity) throws IngeServiceException;


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
   * @throws IngeServiceException
   */
  public SearchRetrieveResponseVO<E> search(SearchRetrieveRequestVO<Query> searchQuery)
      throws IngeServiceException;

}