package de.mpg.mpdl.inge.service.pubman.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.inge.db.model.valueobjects.AffiliationDbRO;
import de.mpg.mpdl.inge.db.model.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl.ID_PREFIX;
import de.mpg.mpdl.inge.db.repository.OrganizationRepository;
import de.mpg.mpdl.inge.es.dao.GenericDaoEs;
import de.mpg.mpdl.inge.es.dao.OrganizationDaoEs;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.util.EntityTransformer;

@Service
@Primary
public class OrganizationServiceDbImpl extends GenericServiceImpl<AffiliationVO, AffiliationDbVO>
    implements OrganizationService {

  public final static String INDEX_OBJECT_ID = "reference.objectId";

  private final static Logger logger = LogManager.getLogger(OrganizationServiceDbImpl.class);

  @Autowired
  private OrganizationDaoEs organizationDao;

  @Autowired
  private OrganizationRepository organizationRepository;

  @PersistenceContext
  EntityManager entityManager;

  @Autowired
  private AuthorizationService aaService;

  @Autowired
  private IdentifierProviderServiceImpl idProviderService;

  /**
   * Returns all top-level affiliations.
   * 
   * @return all top-level affiliations
   * @throws Exception if framework access fails
   */
  public List<AffiliationVO> searchTopLevelOrganizations() throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException  {
    final QueryBuilder qb =
        QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("parentAffiliations"));
    final SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(qb);
    final SearchRetrieveResponseVO<AffiliationVO> response = this.search(srr, null);

    return response.getRecords().stream().map(rec -> rec.getData()).collect(Collectors.toList());
  }

  /**
   * Returns all child affiliations of a given affiliation.
   * 
   * @param parentAffiliation The parent affiliation
   * 
   * @return all child affiliations
   * @throws Exception if framework access fails
   */
  public List<AffiliationVO> searchChildOrganizations(String parentAffiliationId)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException  {
    final QueryBuilder qb =
        QueryBuilders.termQuery("parentAffiliations.objectId", parentAffiliationId);
    final SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(qb);
    final SearchRetrieveResponseVO<AffiliationVO> response = this.search(srr, null);

    return response.getRecords().stream().map(rec -> rec.getData()).collect(Collectors.toList());
  }

  public List<AffiliationVO> searchSuccessors(String objectId) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException  {
    final QueryBuilder qb =
        QueryBuilders.boolQuery().must(QueryBuilders.termQuery("predecessorAffiliations.objectId", objectId));
    final SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(qb);
    final SearchRetrieveResponseVO<AffiliationVO> response = this.search(srr, null);

    return response.getRecords().stream().map(rec -> rec.getData()).collect(Collectors.toList());
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void delete(String id, String authenticationToken) throws IngeTechnicalException,
      AuthenticationException, AuthorizationException, IngeApplicationException {

    AffiliationDbVO ouDbTobeDeleted = organizationRepository.findOne(id);
    super.delete(id, authenticationToken);

    if (ouDbTobeDeleted.getParentAffiliation() != null) {
      AffiliationVO ouVoTobeDeleted = EntityTransformer.transformToOld(ouDbTobeDeleted);

      AffiliationDbVO parentVO =
          organizationRepository.findOne(ouDbTobeDeleted.getParentAffiliation().getObjectId());
      organizationDao.create(parentVO.getObjectId(), EntityTransformer.transformToOld(parentVO));
    }



  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public AffiliationVO open(String id, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException {
    return changeState(id, modificationDate, authenticationToken, AffiliationDbVO.State.OPENED);
  }


  @Override
  @Transactional(rollbackFor = Throwable.class)
  public AffiliationVO close(String id, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException {
    return changeState(id, modificationDate, authenticationToken, AffiliationDbVO.State.CLOSED);
  }

  private AffiliationVO changeState(String id, Date modificationDate, String authenticationToken,
      AffiliationDbVO.State state) throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException {
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    AffiliationDbVO affDbToBeUpdated = organizationRepository.findOne(id);
    if (affDbToBeUpdated == null) {
      throw new IngeApplicationException("Organization with given id " + id + " not found.");
    }

    AffiliationVO affVoToBeUpdated = EntityTransformer.transformToOld(affDbToBeUpdated);

    checkEqualModificationDate(modificationDate, getModificationDate(affVoToBeUpdated));

    if (affDbToBeUpdated.getParentAffiliation() != null && state == AffiliationDbVO.State.OPENED) {
      AffiliationDbVO parentVo =
          organizationRepository.findOne(affDbToBeUpdated.getParentAffiliation().getObjectId());
      if (parentVo.getPublicStatus() != AffiliationDbVO.State.OPENED) {
        throw new IngeApplicationException("Parent organization " + parentVo.getObjectId()
            + " must be in state OPENED");
      }
    }

    checkAa((state == AffiliationDbVO.State.OPENED ? "open" : "close"), userAccount,
        affVoToBeUpdated);

    affDbToBeUpdated.setPublicStatus(state);
    updateWithTechnicalMetadata(affDbToBeUpdated, userAccount, false);
    try {
      affDbToBeUpdated = organizationRepository.saveAndFlush(affDbToBeUpdated);
    } catch (DataAccessException e) {
      handleDBException(e);
    }
    AffiliationVO affToReturn = EntityTransformer.transformToOld(affDbToBeUpdated);
    organizationDao.update(affDbToBeUpdated.getObjectId(), affToReturn);
    return affToReturn;
  }


  @Override
  protected AffiliationDbVO createEmptyDbObject() {
    return new AffiliationDbVO();
  }


  @Override
  protected List<String> updateObjectWithValues(AffiliationVO givenAff,
      AffiliationDbVO toBeUpdatedAff, AccountUserVO userAccount, boolean createNew)
      throws IngeTechnicalException, IngeApplicationException {


    if (createNew) {
      toBeUpdatedAff.setObjectId(idProviderService.getNewId(ID_PREFIX.OU));
      toBeUpdatedAff.setPublicStatus(AffiliationDbVO.State.CREATED);
    }

    toBeUpdatedAff.setMetadata(givenAff.getDefaultMetadata());

    toBeUpdatedAff.setName(givenAff.getDefaultMetadata().getName());

    if (givenAff.getDefaultMetadata().getName() == null
        || givenAff.getDefaultMetadata().getName().trim().isEmpty()) {
      throw new IngeApplicationException("Please provide a name for the organization.");
    }

    List<String> reindexList = new ArrayList<>();

    // Set new parents, parents must be in state CREATED or OPENED
    String oldParentAffId =
        toBeUpdatedAff.getParentAffiliation() != null ? toBeUpdatedAff.getParentAffiliation()
            .getObjectId() : null;
    String newParentAffId =
        givenAff.getParentAffiliations() != null && givenAff.getParentAffiliations().size() > 0 ? givenAff
            .getParentAffiliations().get(0).getObjectId()
            : null;


    if ((oldParentAffId != null && newParentAffId == null)
        || (oldParentAffId == null && newParentAffId != null)
        || !oldParentAffId.equals(newParentAffId)) {

      if (oldParentAffId != null) {
        reindexList.add(oldParentAffId);
      }
      if (newParentAffId != null) {
        AffiliationDbVO newAffVo = organizationRepository.findOne(newParentAffId);
        if (newAffVo.getPublicStatus() != AffiliationDbVO.State.CREATED
            && newAffVo.getPublicStatus() != AffiliationDbVO.State.OPENED) {
          throw new IngeApplicationException("Parent Affiliation " + newAffVo.getObjectId()
              + " has wrong state " + newAffVo.getPublicStatus().toString());
        }
        toBeUpdatedAff.setParentAffiliation(newAffVo);
        reindexList.add(newParentAffId);
      } else {
        toBeUpdatedAff.setParentAffiliation(null);
      }
    }


    if (givenAff.getPredecessorAffiliations() != null) {
      toBeUpdatedAff.getPredecessorAffiliations().clear();
      for (AffiliationRO affRo : givenAff.getPredecessorAffiliations()) {
        AffiliationDbRO newAffRo = new AffiliationDbRO();
        newAffRo.setObjectId(affRo.getObjectId());
        toBeUpdatedAff.getPredecessorAffiliations().add(newAffRo);
      }
    }

    return reindexList;
  }


  @Override
  protected AffiliationVO transformToOld(AffiliationDbVO dbObject) {
    return EntityTransformer.transformToOld(dbObject);
  }


  @Override
  protected JpaRepository<AffiliationDbVO, String> getDbRepository() {
    return organizationRepository;
  }


  @Override
  protected GenericDaoEs<AffiliationVO> getElasticDao() {
    return organizationDao;
  }


  @Override
  protected String getObjectId(AffiliationVO object) {
    return object.getReference().getObjectId();
  }

  @Override
  protected Date getModificationDate(AffiliationVO object) {
    return object.getLastModificationDate();
  }
}
