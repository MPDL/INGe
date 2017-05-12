package de.mpg.mpdl.inge.services;

import de.mpg.mpdl.inge.es.exception.IngeEsServiceException;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;

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
   * @throws IngeEsServiceException
   * @return {@link String}
   */
  public String createUser(AccountUserVO user, String userId) throws IngeEsServiceException;

  /**
   * 
   * @param userId
   * @throws IngeEsServiceException
   * @return {@link AccountUserVO}
   */
  public AccountUserVO readUser(String userId) throws IngeEsServiceException;


  /**
   * 
   * @param user
   * @param userId
   * @throws IngeEsServiceException
   * @return modified user
   */
  public String updateUser(AccountUserVO user, String userId) throws IngeEsServiceException;



  /**
   * 
   * @param userId
   * @throws IngeEsServiceException
   * @return deleted user
   */
  public String deleteUser(String userId) throws IngeEsServiceException;
}
