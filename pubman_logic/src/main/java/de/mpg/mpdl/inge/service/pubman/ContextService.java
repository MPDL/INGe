package de.mpg.mpdl.inge.service.pubman;

import java.util.Date;

import de.mpg.mpdl.inge.model.exception.IngeServiceException;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.service.exceptions.AaException;

public interface ContextService extends GenericService<ContextVO> {

  public ContextVO open(String contextId, Date modificationDate, String authenticationToken)
      throws IngeServiceException, AaException;

  public ContextVO close(String contextId, Date modificationDate, String authenticationToken)
      throws IngeServiceException, AaException;

}
