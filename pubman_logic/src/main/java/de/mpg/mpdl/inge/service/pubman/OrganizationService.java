package de.mpg.mpdl.inge.service.pubman;

import java.util.Date;
import java.util.List;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

public interface OrganizationService extends GenericService<AffiliationVO, String> {


  public AffiliationVO open(String id, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException;

  public AffiliationVO close(String id, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException;

  public List<AffiliationVO> searchTopLevelOrganizations() throws IngeTechnicalException,
      AuthenticationException, AuthorizationException, IngeApplicationException;

  public List<AffiliationVO> searchChildOrganizations(String parentAffiliationId)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException;

  public List<AffiliationVO> searchSuccessors(String objectId) throws IngeTechnicalException,
      AuthenticationException, AuthorizationException, IngeApplicationException;

  public List<String> getIdPath(String id, String token) throws IngeTechnicalException,
      IngeApplicationException, AuthenticationException, AuthorizationException;

}
