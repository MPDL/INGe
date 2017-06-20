package de.mpg.mpdl.inge.service.pubman;

import java.util.Date;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

public interface ContextService extends GenericService<ContextVO> {

  public ContextVO open(String contextId, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException;

  public ContextVO close(String contextId, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException;

}
