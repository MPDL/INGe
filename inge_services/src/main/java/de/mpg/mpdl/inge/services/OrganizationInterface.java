package de.mpg.mpdl.inge.services;

import de.mpg.mpdl.inge.model.exceptions.TechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;

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
   * 
   * @param organization
   * @param organizationId
   * @throws SecurityException
   * @throws NotFoundException
   * @throws TechnicalException
   * @return {@link String}
   */
  public String createOrganization(AffiliationVO organization, String organizationId)
		  throws IngeServiceException;

  /**
   * 
   * @param organizationId
   * @throws TechnicalException
   * @throws NotFoundException
   * @throws SecurityException
   * @return {@link AffiliationVO}
   */
  public AffiliationVO readOrganization(String organizationId) throws IngeServiceException;


  /**
   * 
   * @param organization
   * @param organizationId
   * @throws SecurityException
   * @throws NotFoundException
   * @throws TechnicalException
   * @return {@link String}
   */
  public String updateOrganization(AffiliationVO organization, String organizationId) throws IngeServiceException;


  /**
   * 
   * @param organizationId
   * @throws SecurityException
   * @throws NotFoundException
   * @throws TechnicalException
   * @return {@link String}
   */
  public String deleteOrganization(String organizationId) throws IngeServiceException;

}
