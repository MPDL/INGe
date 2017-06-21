package de.mpg.mpdl.inge.service.pubman;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.ValueObject;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

public interface GenericService<E extends ValueObject> {

  public E create(E object, String authenticationToken) throws IngeTechnicalException,
      AuthenticationException, AuthorizationException, IngeApplicationException;

  public E update(E object, String authenticationToken) throws IngeTechnicalException,
      AuthenticationException, AuthorizationException, IngeApplicationException;

  public void delete(String id, String authenticationToken) throws IngeTechnicalException,
      AuthenticationException, AuthorizationException, IngeApplicationException;

  public E get(String id, String authenticationToken) throws IngeTechnicalException,
      AuthenticationException, AuthorizationException, IngeApplicationException;

  // public void reindex() throws IngeServiceException, AaException;

  public SearchRetrieveResponseVO<SearchResponse, E> search(
      SearchRetrieveRequestVO<QueryBuilder> srr, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException;
}
