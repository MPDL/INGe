package de.mpg.mpdl.inge.service.pubman.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.inge.db.model.valueobjects.AccountUserDbRO;
import de.mpg.mpdl.inge.db.model.valueobjects.AffiliationDbRO;
import de.mpg.mpdl.inge.db.model.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.AffiliationDbVO.State;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl.ID_PREFIX;
import de.mpg.mpdl.inge.db.repository.OrganizationRepository;
import de.mpg.mpdl.inge.es.dao.OrganizationDaoEs;
import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.model.exception.IngeServiceException;
import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.util.EntityTransformer;

@Service
@Primary
public class OrganizationServiceDbImpl implements OrganizationService {

  public final static String INDEX_OBJECT_ID = "reference.objectId";

  private final static Logger logger = LogManager.getLogger(OrganizationServiceDbImpl.class);

  @Autowired
  private OrganizationDaoEs<QueryBuilder> organizationDao;

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
  public List<AffiliationVO> searchTopLevelOrganizations() throws IngeServiceException {
    final QueryBuilder qb =
        QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("parentAffiliations"));
    final SearchRetrieveRequestVO<QueryBuilder> srr = new SearchRetrieveRequestVO<QueryBuilder>(qb);
    final SearchRetrieveResponseVO<AffiliationVO> response = this.organizationDao.search(srr);

    return response.getRecords().stream().map(rec -> rec.getData()).collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public AffiliationVO get(String id, String authenticationToken) throws IngeServiceException,
      AaException {
    AccountUserVO userAccount = null;
    AffiliationVO ouVo = EntityTransformer.transformToOld(this.organizationRepository.findOne(id));

    if (authenticationToken != null) {
      userAccount = aaService.checkLoginRequired(authenticationToken);
    }

    checkAa(ouVo, userAccount, "get");

    return ouVo;
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
      throws IngeServiceException {
    final QueryBuilder qb =
        QueryBuilders.termQuery("parentAffiliations.objectId", parentAffiliationId);
    final SearchRetrieveRequestVO<QueryBuilder> srr = new SearchRetrieveRequestVO<QueryBuilder>(qb);
    final SearchRetrieveResponseVO<AffiliationVO> response = this.organizationDao.search(srr);

    return response.getRecords().stream().map(rec -> rec.getData()).collect(Collectors.toList());
  }

  @Override
  public SearchRetrieveResponseVO<AffiliationVO> search(SearchRetrieveRequestVO<QueryBuilder> srr,
      String authenticationToken) throws IngeServiceException, AaException {

    QueryBuilder qb = srr.getQueryObject();
    if (authenticationToken != null) {
      qb =
          aaService.modifyQueryForAa("de.mpg.mpdl.inge.service.pubman.OrganizationService", qb,
              aaService.checkLoginRequired(authenticationToken));
    } else {
      qb =
          aaService.modifyQueryForAa("de.mpg.mpdl.inge.service.pubman.OrganizationService", qb,
              null);
    }
    srr.setQueryObject(qb);
    System.out.println(srr.getQueryObject().toString());
    return this.organizationDao.search(srr);
  }

  @Override
  @Transactional
  public AffiliationVO create(AffiliationVO affVo, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException {

    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    AffiliationDbVO affToCreate = new AffiliationDbVO();
    List<String> reindexList = updateOuWithValues(affVo, affToCreate, userAccount, true);

    checkAa(EntityTransformer.transformToOld(affToCreate), userAccount, "create");

    affToCreate = organizationRepository.save(affToCreate);
    AffiliationVO affToReturn = EntityTransformer.transformToOld(affToCreate);
    organizationDao.create(affToCreate.getObjectId(), affToReturn);

    reindex(reindexList);

    return affToReturn;
  }



  private List<String> updateOuWithValues(AffiliationVO givenAff, AffiliationDbVO toBeUpdatedAff,
      AccountUserVO userAccount, boolean createNew) throws IngeServiceException {
    Date currentDate = new Date();
    AccountUserDbRO mod = new AccountUserDbRO();
    mod.setName(userAccount.getName());
    mod.setObjectId(userAccount.getReference().getObjectId());

    if (createNew) {
      toBeUpdatedAff.setCreationDate(currentDate);
      toBeUpdatedAff.setCreator(mod);
      toBeUpdatedAff.setObjectId(idProviderService.getNewId(ID_PREFIX.OU));
      toBeUpdatedAff.setPublicStatus(State.CREATED);
    }

    toBeUpdatedAff.setMetadata(givenAff.getDefaultMetadata());
    toBeUpdatedAff.setLastModificationDate(currentDate);
    toBeUpdatedAff.setModifier(mod);

    toBeUpdatedAff.setName(givenAff.getDefaultMetadata().getName());

    if (givenAff.getDefaultMetadata().getName() == null
        || givenAff.getDefaultMetadata().getName().trim().isEmpty()) {
      throw new IngeServiceException("Please provide a name for the organization.");
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
        if (newAffVo.getPublicStatus() != State.CREATED
            && newAffVo.getPublicStatus() != State.OPENED) {
          throw new AaException("Parent Affiliation " + newAffVo.getObjectId()
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
  @Transactional
  public AffiliationVO update(AffiliationVO affVO, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException {

    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    AffiliationDbVO affToBeUpdated =
        organizationRepository.findOne(affVO.getReference().getObjectId());
    if (affToBeUpdated == null) {
      throw new IngeServiceException("Organization with given id not found.");
    }
    List<String> reindexList = updateOuWithValues(affVO, affToBeUpdated, userAccount, false);

    checkAa(EntityTransformer.transformToOld(affToBeUpdated), userAccount, "update");

    affToBeUpdated = organizationRepository.save(affToBeUpdated);

    AffiliationVO affToReturn = EntityTransformer.transformToOld(affToBeUpdated);
    organizationDao.update(affToBeUpdated.getObjectId(), affToReturn);
    reindex(reindexList);
    return affToReturn;
  }

  @Override
  @Transactional
  public void delete(String id, String authenticationToken) throws IngeServiceException,
      AaException {

    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    AffiliationDbVO ouTobeDeleted = organizationRepository.findOne(id);

    checkAa(EntityTransformer.transformToOld(ouTobeDeleted), userAccount, "delete");

    organizationRepository.delete(id);
    organizationDao.delete(id);
    if (ouTobeDeleted.getParentAffiliation() != null) {
      AffiliationDbVO parentVO =
          organizationRepository.findOne(ouTobeDeleted.getParentAffiliation().getObjectId());
      organizationDao.create(parentVO.getObjectId(), EntityTransformer.transformToOld(parentVO));
    }



  }

  @Override
  @Transactional
  public AffiliationVO open(String id, String authenticationToken) throws IngeServiceException,
      AaException {
    return changeState(id, authenticationToken, State.OPENED);
  }


  @Override
  @Transactional
  public AffiliationVO close(String id, String authenticationToken) throws IngeServiceException,
      AaException {
    return changeState(id, authenticationToken, State.CLOSED);
  }

  private AffiliationVO changeState(String id, String authenticationToken, State state)
      throws IngeServiceException, AaException {
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    AffiliationDbVO affToBeUpdated = organizationRepository.findOne(id);
    if (affToBeUpdated == null) {
      throw new IngeServiceException("Organization with given id " + id + " not found.");
    }
    if (affToBeUpdated.getParentAffiliation() != null && state == State.OPENED) {
      AffiliationDbVO parentVo =
          organizationRepository.findOne(affToBeUpdated.getParentAffiliation().getObjectId());
      if (parentVo.getPublicStatus() != State.OPENED) {
        throw new AaException("Parent organization " + parentVo.getObjectId()
            + " must be in state OPENED");
      }
    }

    checkAa(EntityTransformer.transformToOld(affToBeUpdated), userAccount,
        (state == State.OPENED ? "open" : "close"));

    affToBeUpdated.setPublicStatus(state);

    affToBeUpdated = organizationRepository.save(affToBeUpdated);

    AffiliationVO affToReturn = EntityTransformer.transformToOld(affToBeUpdated);
    organizationDao.update(affToBeUpdated.getObjectId(), affToReturn);
    return affToReturn;
  }

  public void reindex() {

    Query<de.mpg.mpdl.inge.db.model.valueobjects.AffiliationDbVO> query =
        (Query<de.mpg.mpdl.inge.db.model.valueobjects.AffiliationDbVO>) entityManager
            .createQuery("SELECT ou FROM AffiliationVO ou");
    query.setReadOnly(true);
    query.setFetchSize(1000);
    query.setCacheable(false);
    ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);

    while (results.next()) {
      try {
        de.mpg.mpdl.inge.db.model.valueobjects.AffiliationDbVO object =
            (de.mpg.mpdl.inge.db.model.valueobjects.AffiliationDbVO) results.get(0);
        AffiliationVO aff = EntityTransformer.transformToOld(object);
        logger.info("Reindexing ou " + aff.getReference().getObjectId());
        organizationDao.create(aff.getReference().getObjectId(), aff);
      } catch (Exception e) {
        logger.error("Error while reindexing ", e);
      }


    }

  }

  private void checkAa(AffiliationVO aff, AccountUserVO userAccount, String method)
      throws AaException {
    aaService.checkAuthorization("de.mpg.mpdl.inge.service.pubman.OrganizationService", method,
        aff, userAccount);

  }

  private void reindex(List<String> ouList) throws IngeServiceException {
    // Reindex old and new Parents
    for (String ouId : ouList) {
      AffiliationVO affVo = EntityTransformer.transformToOld(organizationRepository.findOne(ouId));
      organizationDao.create(ouId, affVo);
    }

  }
}
