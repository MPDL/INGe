package de.mpg.mpdl.inge.services;

import de.escidoc.core.client.exceptions.application.security.AuthenticationException;
import de.mpg.mpdl.inge.model.exceptions.TechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.UserGroupVO;
import de.mpg.mpdl.inge.tech.exceptions.NotFoundException;

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
   * @param currentUser
   * @throws AuthenticationException
   * @throws TechnicalException
   * @return created user group
   */
  public UserGroupVO createUserGroupVO(UserGroupVO userGroup) throws AuthenticationException,
      TechnicalException;

  /**
   * Retrieves an user group for a user group ID
   * 
   * @param userGroupId The Id of the user group to get
   * @throws TechnicalException
   * @throws NotFoundException
   * @throws SecurityException
   * @return user group with the given userGroupId
   */
  public UserGroupVO readUserGroupVO(String userGroupId) throws TechnicalException,
      NotFoundException, SecurityException;


  /**
   * Updates an existing user group (will not change active/inactive state)
   * 
   * @param userGroup
   * @param currentUser
   * @throws AuthenticationException
   * @throws TechnicalException
   * @return modified user group
   */
  public UserGroupVO updateUserGroupVO(UserGroupVO userGroup) throws AuthenticationException,
      TechnicalException;


  /**
   * Deletes an existing user group (will not change active/inactive state)
   * 
   * @param userGroup
   * @param currentUser
   * @throws AuthenticationException
   * @throws TechnicalException
   * @return deleted user group
   */
  public UserGroupVO deleteUserGroupVO(UserGroupVO userGroup) throws AuthenticationException,
      TechnicalException;
}
