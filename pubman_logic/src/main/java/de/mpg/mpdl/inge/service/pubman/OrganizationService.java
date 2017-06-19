package de.mpg.mpdl.inge.service.pubman;

import java.util.Date;
import java.util.List;

import de.mpg.mpdl.inge.model.exception.IngeServiceException;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.service.exceptions.AaException;

public interface OrganizationService extends GenericService<AffiliationVO> {


  public AffiliationVO open(String id, Date modificationDate, String authenticationToken)
      throws IngeServiceException, AaException;

  public AffiliationVO close(String id, Date modificationDate, String authenticationToken)
      throws IngeServiceException, AaException;

  public List<AffiliationVO> searchTopLevelOrganizations() throws IngeServiceException;

  public List<AffiliationVO> searchChildOrganizations(String parentAffiliationId)
      throws IngeServiceException;

  public List<AffiliationVO> searchSuccessors(String objectId) throws IngeServiceException;

}
