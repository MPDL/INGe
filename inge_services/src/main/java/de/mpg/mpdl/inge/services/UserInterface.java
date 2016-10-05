package de.mpg.mpdl.inge.services;

import de.escidoc.core.client.exceptions.application.security.AuthenticationException;
import de.mpg.mpdl.inge.model.exceptions.TechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.tech.exceptions.IngeServiceException;

/**
 * Interface for persisting and retrieving user s
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public interface UserInterface {
  /**
   * 
   * @param user
   * @param userId
   * @throws AuthenticationException
   * @throws TechnicalException
   * @return {@link String}
   */
  public String createUser(AccountUserVO user, String userId)  throws IngeServiceException;

  /**
   * 
   * @param userId
   * @throws TechnicalException
   * @throws NotFoundException
   * @throws SecurityException
   * @return {@link AccountUserVO}
   */
  public AccountUserVO readUser(String userId)  throws IngeServiceException;


  /**
   * 
   * @param user
   * @param userId
   * @throws AuthenticationException
   * @throws TechnicalException
   * @return modified user
   */
  public String updateUser(AccountUserVO user, String userId)  throws IngeServiceException;
     


  /**
   * 
   * @param userId
   * @throws AuthenticationException
   * @throws TechnicalException
   * @return deleted user
   */
  public String deleteUser(String userId)  throws IngeServiceException;
}
