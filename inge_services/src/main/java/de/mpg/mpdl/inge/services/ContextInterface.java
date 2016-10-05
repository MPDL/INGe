package de.mpg.mpdl.inge.services;

import de.escidoc.core.client.exceptions.application.security.AuthenticationException;
import de.mpg.mpdl.inge.model.exceptions.TechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.tech.exceptions.IngeServiceException;


/**
 * Interface for persisting and retrieving Contexts
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public interface ContextInterface {
  /**
   * 
   * @param context
   * @param contextId
   * @throws AuthenticationException
   * @throws TechnicalException
   * @return {@link String}
   */
  public String createContext(ContextVO context, String contextId) throws IngeServiceException;


  /**
   * 
   * @param contextId
   * @throws TechnicalException
   * @throws NotFoundException
   * @throws SecurityException
   * @return {@link ContextVO}
   */
  public ContextVO readContext(String contextId) throws IngeServiceException;

  /**
   * 
   * @param context
   * @param contextId
   * @throws AuthenticationException
   * @throws TechnicalException
   * @return {@link String}
   */
  public String updateContext(ContextVO context, String contextId) throws IngeServiceException;

  /**
   * 
   * @param contextId
   * @throws AuthenticationException
   * @throws TechnicalException
   * @return {@link String}
   */
  public String deleteContext(String contextId) throws IngeServiceException;
}
