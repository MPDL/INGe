package de.mpg.mpdl.inge.tech.interfaces;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.tech.exceptions.NotFoundException;

/**
 * Interface for persisting and retrieving organizations
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public interface OrganizationInterface {
  /**
   * Creates and opens a new organization if it is not already existing
   * 
   * @param organization OrganizationVO to create
   * @throws SecurityException
   * @throws NotFoundException
   * @throws TechnicalException
   * @return created organization
   */
  public OrganizationVO createOrganization(OrganizationVO organization) throws SecurityException,
      NotFoundException, TechnicalException;

  /**
   * Retrieves an organization for a given organization ID
   * 
   * @param organizationId The Id of the organization to get
   * @throws TechnicalException
   * @throws NotFoundException
   * @throws SecurityException
   * @return user account with the given userAccountId
   */
  public OrganizationVO readOrganization(String organizationId) throws TechnicalException,
      NotFoundException, SecurityException;


  /**
   * Updates an existing organization (will not change open/close state)
   * 
   * @param organization Updated OrganizationVO
   * @throws SecurityException
   * @throws NotFoundException
   * @throws TechnicalException
   * @return modified organization
   */
  public OrganizationVO updateOrganization(OrganizationVO organization) throws SecurityException,
      NotFoundException, TechnicalException;


  /**
   * Deletes an existing organization
   * 
   * @param organizationId
   * @throws SecurityException
   * @throws NotFoundException
   * @throws TechnicalException
   * @return deleted organization
   */
  public OrganizationVO deleteOrganization(String organizationId) throws SecurityException,
      NotFoundException, TechnicalException;


  /**
   * Opens an existing organization which is in state closed
   * 
   * @param organizationId
   * @throws SecurityException
   * @throws NotFoundException
   * @throws TechnicalException
   * @return opened organization
   */
  public OrganizationVO openOrganization(String organizationId) throws SecurityException,
      NotFoundException, TechnicalException;


  /**
   * Closes an existing organization which is in state open
   * 
   * @param organizationID
   * @throws SecurityException
   * @throws NotFoundException
   * @throws TechnicalException
   * @return closed organization
   */
  public OrganizationVO closeOrganization(String organizationID) throws SecurityException,
      NotFoundException, TechnicalException;


  /**
   * Retrieves an organization for a given organization ID
   * 
   * @param organizationId The Id of the organization to get
   * @throws TechnicalException
   * @throws NotFoundException
   * @throws SecurityException
   * @return user account with the given userAccountId
   */
  public OrganizationVO getOrganization(String organizationId) throws TechnicalException,
      NotFoundException, SecurityException;


  /**
   * Returns a list of organizations satisfying the requirements of the searchQuery
   * 
   * @param searchQuery The search query
   * @throws TechnicalException
   * @return list of user accounts satisfying the requirements of the searchQuery
   */
  public java.util.List<OrganizationVO> searchOrganization(String searchQuery)
      throws TechnicalException;
}
