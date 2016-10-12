package de.mpg.mpdl.inge.services;

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
   * @throws IngeServiceException
   * @return {@link String}
   */
  public String createUser(AccountUserVO user, String userId) throws IngeServiceException;

  /**
   * 
   * @param userId
   * @throws IngeServiceException
   * @return {@link AccountUserVO}
   */
  public AccountUserVO readUser(String userId) throws IngeServiceException;


  /**
   * 
   * @param user
   * @param userId
   * @throws IngeServiceException
   * @return modified user
   */
  public String updateUser(AccountUserVO user, String userId) throws IngeServiceException;



  /**
   * 
   * @param userId
   * @throws IngeServiceException
   * @return deleted user
   */
  public String deleteUser(String userId) throws IngeServiceException;
}
