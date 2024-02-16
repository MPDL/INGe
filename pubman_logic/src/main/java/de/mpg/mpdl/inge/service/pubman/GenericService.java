package de.mpg.mpdl.inge.service.pubman;

import java.io.Serializable;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

public interface GenericService<E, Id extends Serializable> extends GenericServiceBase<E> {

  E create(E object, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  E update(E object, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void delete(Id id, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  E get(Id id, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void reindexAll(String authenticationToken);

  void reindex(Id id, String authenticationToken) throws IngeTechnicalException;



}
