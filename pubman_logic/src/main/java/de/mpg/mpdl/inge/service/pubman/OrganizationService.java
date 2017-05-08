package de.mpg.mpdl.inge.service.pubman;

import java.util.List;

import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.services.IngeServiceException;

public interface OrganizationService extends GenericService<AffiliationVO> {
  
  public List<AffiliationVO> searchTopLevelOrganizations() throws IngeServiceException;
  
  public List<AffiliationVO> searchChildOrganizations(String parentAffiliationId) throws IngeServiceException;

}
