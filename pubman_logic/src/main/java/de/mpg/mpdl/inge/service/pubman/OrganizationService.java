package de.mpg.mpdl.inge.service.pubman;

import java.util.List;

import de.mpg.mpdl.inge.model.exception.IngeEsServiceException;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.service.exceptions.AaException;

public interface OrganizationService extends GenericService<AffiliationVO> {


  public AffiliationVO open(String id, String authenticationToken) throws IngeEsServiceException,
      AaException;

  public AffiliationVO close(String id, String authenticationToken) throws IngeEsServiceException,
      AaException;

  public List<AffiliationVO> searchTopLevelOrganizations() throws IngeEsServiceException;

  public List<AffiliationVO> searchChildOrganizations(String parentAffiliationId)
      throws IngeEsServiceException;

}
