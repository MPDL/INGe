package de.mpg.mpdl.inge.services;

import de.escidoc.core.client.exceptions.application.security.AuthenticationException;
import de.mpg.mpdl.inge.model.exceptions.TechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.UserGroupVO;

/**
 * Interface for persisting and retrieving user groups
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public interface UserGroupInterface {
  /**
   * Creates and activates a new user group if it is not already existing
   * 
   * @param userGroup
   * @param userGroupId
   * @throws AuthenticationException
   * @throws TechnicalException
   * @return {@link String}
   */
  public String createUserGroup(UserGroupVO userGroup, String userGroupId) throws IngeServiceException;

  /**
   * 
   * @param userGroupId
   * @throws TechnicalException
   * @throws NotFoundException
   * @throws SecurityException
   * @return {@link UserGroupVO}
   */
  public UserGroupVO readUserGroup(String userGroupId) throws IngeServiceException;


  /**
   * 
   * @param userGroup
   * @param userGroupId
   * @throws AuthenticationException
   * @throws TechnicalException
   * @return {@link String}
   */
  public String updateUserGroup(UserGroupVO userGroup, String userGroupId) throws IngeServiceException;


  /**
   * 
   * @param userGroupId
   * @throws AuthenticationException
   * @throws TechnicalException
   * @return {@link String}
   */
  public String deleteUserGroup(String userGroupId)  throws IngeServiceException;
}
