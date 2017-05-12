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
import de.mpg.mpdl.inge.db.model.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl;
import de.mpg.mpdl.inge.db.repository.OrganizationRepository;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl.ID_PREFIX;
import de.mpg.mpdl.inge.es.dao.OrganizationDaoEs;
import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.util.EntityTransformer;
import de.mpg.mpdl.inge.es.exception.IngeEsServiceException;

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
  public List<AffiliationVO> searchTopLevelOrganizations() throws IngeEsServiceException {
    final QueryBuilder qb =
        QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("parentAffiliations"));
    final SearchRetrieveRequestVO<QueryBuilder> srr = new SearchRetrieveRequestVO<QueryBuilder>(qb);
    final SearchRetrieveResponseVO<AffiliationVO> response = this.organizationDao.search(srr);

    return response.getRecords().stream().map(rec -> rec.getData()).collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public AffiliationVO get(String id, String authenticationToken) throws IngeEsServiceException,
      AaException {
    return EntityTransformer.transformToOld(this.organizationRepository.findOne(id));
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
      throws IngeEsServiceException {
    final QueryBuilder qb =
        QueryBuilders.termQuery("parentAffiliations.objectId", parentAffiliationId);
    final SearchRetrieveRequestVO<QueryBuilder> srr = new SearchRetrieveRequestVO<QueryBuilder>(qb);
    final SearchRetrieveResponseVO<AffiliationVO> response = this.organizationDao.search(srr);

    return response.getRecords().stream().map(rec -> rec.getData()).collect(Collectors.toList());
  }

  @Override
  public SearchRetrieveResponseVO<AffiliationVO> search(SearchRetrieveRequestVO<QueryBuilder> srr,
      String authenticationToken) throws IngeEsServiceException, AaException {
    return this.organizationDao.search(srr);
  }

  @Override
  @Transactional
  public AffiliationVO create(AffiliationVO affVo, String authenticationToken)
      throws IngeEsServiceException, AaException, ItemInvalidException {

    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    AffiliationDbVO affToCreate = new AffiliationDbVO();
    updateOuWithValues(affVo, affToCreate, userAccount, true);


    affToCreate = organizationRepository.save(affToCreate);
    AffiliationVO affToReturn = EntityTransformer.transformToOld(affToCreate);
    organizationDao.create(affToCreate.getObjectId(), affToReturn);
    return affToReturn;
  }


  private void updateOuWithValues(AffiliationVO givenAff, AffiliationDbVO toBeUpdatedAff,
      AccountUserVO userAccount, boolean createNew) {
    Date currentDate = new Date();
    AccountUserDbRO mod = new AccountUserDbRO();
    mod.setName(userAccount.getName());
    mod.setObjectId(userAccount.getReference().getObjectId());

    toBeUpdatedAff.setMetadata(givenAff.getDefaultMetadata());
    toBeUpdatedAff.setLastModificationDate(currentDate);
    toBeUpdatedAff.setModifier(mod);


    // Set new parents
    if (givenAff.getParentAffiliations() != null) {
      toBeUpdatedAff.getParentAffiliations().clear();
      for (AffiliationRO affRo : givenAff.getParentAffiliations()) {
        AffiliationDbRO newAffRo = new AffiliationDbRO();
        newAffRo.setObjectId(affRo.getObjectId());
        toBeUpdatedAff.getParentAffiliations().add(newAffRo);
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



    if (createNew) {
      toBeUpdatedAff.setCreationDate(currentDate);
      toBeUpdatedAff.setCreator(mod);
      toBeUpdatedAff.setObjectId(idProviderService.getNewId(ID_PREFIX.OU));
      toBeUpdatedAff.setPublicStatus(State.CREATED);
    }
  }

  @Override
  @Transactional
  public AffiliationVO update(AffiliationVO affVO, String authenticationToken)
      throws IngeEsServiceException, AaException, ItemInvalidException {

    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    AffiliationDbVO affToBeUpdated =
        organizationRepository.findOne(affVO.getReference().getObjectId());
    if (affToBeUpdated == null) {
      throw new IngeEsServiceException("Organization with given id not found.");
    }
    updateOuWithValues(affVO, affToBeUpdated, userAccount, false);

    affToBeUpdated = organizationRepository.save(affToBeUpdated);

    AffiliationVO affToReturn = EntityTransformer.transformToOld(affToBeUpdated);
    organizationDao.update(affToBeUpdated.getObjectId(), affToReturn);
    return affToReturn;
  }

  @Override
  @Transactional
  public void delete(String id, String authenticationToken) throws IngeEsServiceException,
      AaException {
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    organizationRepository.delete(id);
    organizationDao.delete(id);


  }

  @Override
  @Transactional
  public AffiliationVO open(String id, String authenticationToken) throws IngeEsServiceException,
      AaException {
    return changeState(id, authenticationToken, State.OPENED);
  }


  @Override
  @Transactional
  public AffiliationVO close(String id, String authenticationToken) throws IngeEsServiceException,
      AaException {
    return changeState(id, authenticationToken, State.CLOSED);
  }

  private AffiliationVO changeState(String id, String authenticationToken, State state)
      throws IngeEsServiceException, AaException {
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    AffiliationDbVO affToBeUpdated = organizationRepository.findOne(id);
    if (affToBeUpdated == null) {
      throw new IngeEsServiceException("Organization with given id " + id + " not found.");
    }

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
}
