package de.mpg.mpdl.inge.service.pubman;

import org.elasticsearch.index.query.QueryBuilder;

import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.ValueObject;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.services.IngeServiceException;

public interface GenericService<E extends ValueObject> {

  public E create(E object, String userToken) throws IngeServiceException, AaException,
      ItemInvalidException;

  public E update(E object, String userToken) throws IngeServiceException, AaException,
      ItemInvalidException;

  public void delete(String id, String userToken) throws IngeServiceException, AaException;

  public E get(String id, String userToken) throws IngeServiceException, AaException;

  public SearchRetrieveResponseVO<E> search(SearchRetrieveRequestVO<QueryBuilder> srr,
      String userToken) throws IngeServiceException, AaException;

}
