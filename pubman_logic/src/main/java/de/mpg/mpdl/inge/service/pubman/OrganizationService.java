package de.mpg.mpdl.inge.service.pubman;

import java.util.Date;
import java.util.List;

import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

public interface OrganizationService extends GenericService<AffiliationDbVO, String> {

  public AffiliationDbVO open(String id, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public AffiliationDbVO close(String id, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public AffiliationDbVO addPredecessor(String id, Date modificationDate, String predecessorId, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public AffiliationDbVO removePredecessor(String id, Date modificationDate, String predecessorId, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public List<AffiliationDbVO> searchTopLevelOrganizations()
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public List<AffiliationDbVO> searchFirstLevelOrganizations()
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public List<AffiliationDbVO> searchChildOrganizations(String parentAffiliationId)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public List<AffiliationDbVO> searchAllChildOrganizations(String[] parentAffiliationIds, String ignoreOuId)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public List<AffiliationDbVO> searchSuccessors(String objectId)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public String getOuPath(String id) throws IngeTechnicalException, IngeApplicationException;

  public List<String> getIdPath(String id)
      throws IngeTechnicalException, IngeApplicationException, AuthenticationException, AuthorizationException;

  public List<String> getChildIdPath(String id) throws IngeTechnicalException, IngeApplicationException;

  public List<String> getAllChildrenOfMpg() throws IngeTechnicalException, IngeApplicationException;

  public void refreshAllChildrenOfMpg();
}
