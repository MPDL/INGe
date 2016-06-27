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


  /**
   * activates an existing and closed Context
   * 
   * @param Context
   * @param currentUser
   * @throws AuthenticationException
   * @throws TechnicalException
   * @return opened Context
   */
  public ContextVO openContext(ContextVO Context) throws AuthenticationException,
      TechnicalException;


  /**
   * deactivates an existing and open Context
   * 
   * @param Context
   * @param currentUser
   * @throws AuthenticationException
   * @throws TechnicalException
   * @return closed Context
   */
  public ContextVO closeContext(ContextVO Context) throws AuthenticationException,
      TechnicalException;


  /**
   * Returns all open Contexts for which the current user is in the role "Depositor".
   * 
   * @exception TechnicalException,
   * @exception SecurityException
   */
  public java.util.List<ContextVO> getDepositingContextList() throws TechnicalException,
      SecurityException;


  /**
   * Returns all Contexts for the current user
   * 
   * @exception TechnicalException,
   * @exception SecurityException
   */
  public java.util.List<ContextVO> getAllContexts() throws TechnicalException, SecurityException;


  /**
   * Returns a list of Contexts satisfying the requirements of the search query
   * 
   * @param searchQuery The search query
   * @throws TechnicalException
   * @return list of Contexts satisfying the requirements of the searchQuery
   */
  public java.util.List<ContextVO> searchContext(String searchQuery) throws TechnicalException;

}
