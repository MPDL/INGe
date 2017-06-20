package de.mpg.mpdl.inge.service.pubman;

import java.util.Date;

import org.elasticsearch.index.query.QueryBuilder;

import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.model.exception.IngeServiceException;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.ValueObject;
import de.mpg.mpdl.inge.service.exceptions.AaException;

public interface GenericService<E extends ValueObject> {

  public E create(E object, String authenticationToken) throws IngeServiceException, AaException,
      ValidationException;

  public E update(E object, String authenticationToken) throws IngeServiceException, AaException,
      ValidationException;

  public void delete(String id, Date modificationDate, String authenticationToken)
      throws IngeServiceException, AaException;

  public E get(String id, String authenticationToken) throws IngeServiceException, AaException;

  // public void reindex() throws IngeServiceException, AaException;

  public SearchRetrieveResponseVO<E> search(SearchRetrieveRequestVO<QueryBuilder> srr,
      String authenticationToken) throws IngeServiceException, AaException;
}
