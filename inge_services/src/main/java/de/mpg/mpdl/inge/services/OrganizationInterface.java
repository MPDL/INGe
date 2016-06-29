package de.mpg.mpdl.inge.services;

import de.mpg.mpdl.inge.model.exceptions.TechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
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
}
