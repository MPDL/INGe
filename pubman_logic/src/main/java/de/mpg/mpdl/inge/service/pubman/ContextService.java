package de.mpg.mpdl.inge.service.pubman;

import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.services.IngeServiceException;

public interface ContextService extends GenericService<ContextVO> {

  public ContextVO open(String contextId, String authenticationToken) throws IngeServiceException,
      AaException;

  public ContextVO close(String contextId, String authenticationToken) throws IngeServiceException,
      AaException;

}
