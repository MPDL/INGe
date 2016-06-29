package de.mpg.mpdl.inge.services;

import de.escidoc.core.client.exceptions.application.security.AuthenticationException;
import de.mpg.mpdl.inge.model.exceptions.TechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.tech.exceptions.NotFoundException;

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
   * Creates and activates a new Context if it is not already existing
   * 
   * @param Context
   * @param currentUser
   * @throws AuthenticationException
   * @throws TechnicalException
   * @return created Context
   */
  public ContextVO createContext(ContextVO Context) throws AuthenticationException,
      TechnicalException;

  /**
   * Retrieves an Context for a given Context ID
   * 
   * @param ContextId The Id of the Context to get
   * @throws TechnicalException
   * @throws NotFoundException
   * @throws SecurityException
   * @return Context with the given ContextId
   */
  public ContextVO readContext(String ContextId) throws TechnicalException, NotFoundException,
      SecurityException;

  /**
   * updates an existing Context (will not change open/close state)
   * 
   * @param Context
   * @param currentUser
   * @throws AuthenticationException
   * @throws TechnicalException
   * @return modified Context
   */
  public ContextVO updateContext(ContextVO Context) throws AuthenticationException,
      TechnicalException;


  /**
   * deletes an existing Context (will not change open/close state)
   * 
   * @param Context
   * @param currentUser
   * @throws AuthenticationException
   * @throws TechnicalException
   * @return deleted Context
   */
  public ContextVO deleteContext(ContextVO Context) throws AuthenticationException,
      TechnicalException;
}
