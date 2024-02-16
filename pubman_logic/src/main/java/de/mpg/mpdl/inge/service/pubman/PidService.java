package de.mpg.mpdl.inge.service.pubman;

import java.net.URI;

import de.mpg.mpdl.inge.model.valueobjects.PidServiceResponseVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

/**
 * @author walter
 *
 */
public interface PidService {

  /**
   * creates a new PID for a given url
   *
   * @param url
   * @return PID
   * @throws IngeApplicationException
   * @throws TechnicalException
   */
  PidServiceResponseVO createPid(URI url) throws IngeApplicationException, TechnicalException;

}
