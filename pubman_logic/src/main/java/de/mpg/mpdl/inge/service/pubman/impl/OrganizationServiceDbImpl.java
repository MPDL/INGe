package de.mpg.mpdl.inge.service.pubman.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl.ID_PREFIX;
import de.mpg.mpdl.inge.db.repository.OrganizationRepository;
import de.mpg.mpdl.inge.es.dao.GenericDaoEs;
import de.mpg.mpdl.inge.es.dao.OrganizationDaoEs;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.pubman.ReindexListener;
import de.mpg.mpdl.inge.service.util.SearchUtils;
import de.mpg.mpdl.inge.util.PropertyReader;

@Service
@Primary
public class OrganizationServiceDbImpl extends GenericServiceImpl<AffiliationDbVO, String> implements OrganizationService, ReindexListener {

  public final static String INDEX_OBJECT_ID = "objectId";
  public final static String INDEX_METADATA_TITLE = "metadata.name";
  public final static String INDEX_METADATA_TITLE_KEYWORD = "metadata.name.keyword";
  public final static String INDEX_METADATA_ALTERNATIVE_NAMES = "metadata.alternativeNames";
  public final static String INDEX_METADATA_CITY = "metadata.city";
  public final static String INDEX_PARENT_AFFILIATIONS_OBJECT_ID = "parentAffiliation.objectId";
  public final static String INDEX_PREDECESSOR_AFFILIATIONS_OBJECT_ID = "predecessorAffiliations.objectId";
  public final static String INDEX_STATE = "publicStatus.keyword";
  public final static int OU_SEARCH_LIMIT = -2; // unbegrenzte Suche

  private static final Logger logger = LogManager.getLogger(OrganizationServiceDbImpl.class);

  private final String mpgId = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_ROOT_ORGANISATION_ID);

  private List<String> allChildrenOfMpg = new ArrayList<String>();

  @Autowired
  private OrganizationDaoEs organizationDao;

  @Autowired
  private OrganizationRepository organizationRepository;

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
  public List<AffiliationDbVO> searchTopLevelOrganizations()
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    final QueryBuilder qb = QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(INDEX_PARENT_AFFILIATIONS_OBJECT_ID));
    final SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(qb, OU_SEARCH_LIMIT, 0,
        new SearchSortCriteria[] {new SearchSortCriteria(INDEX_STATE, SearchSortCriteria.SortOrder.DESC),
            new SearchSortCriteria(INDEX_METADATA_TITLE_KEYWORD, SearchSortCriteria.SortOrder.DESC)});
    final SearchRetrieveResponseVO<AffiliationDbVO> response = this.search(srr, null);

    return response.getRecords().stream().map(rec -> rec.getData()).collect(Collectors.toList());
  }

  /**
   * Returns all first-level affiliations.
   * 
   * @return all first-level affiliations
   * @throws Exception if framework access fails
   */
  public List<AffiliationDbVO> searchFirstLevelOrganizations()
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    final BoolQueryBuilder qb = QueryBuilders.boolQuery();

    final List<AffiliationDbVO> topLevelOus = this.searchTopLevelOrganizations();

    if (topLevelOus.size() == 0) {
      return new ArrayList<AffiliationDbVO>();
    }

    final List<String> topLevelOuIds = new ArrayList<String>();
    for (AffiliationDbVO affiliationDbVO : topLevelOus) {
      topLevelOuIds.add(affiliationDbVO.getObjectId());
    }
    qb.filter(QueryBuilders.termsQuery(INDEX_PARENT_AFFILIATIONS_OBJECT_ID, topLevelOuIds));

    final SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(qb, OU_SEARCH_LIMIT, 0,
        new SearchSortCriteria[] {new SearchSortCriteria(INDEX_PARENT_AFFILIATIONS_OBJECT_ID, SearchSortCriteria.SortOrder.ASC),
            new SearchSortCriteria(INDEX_METADATA_TITLE_KEYWORD, SearchSortCriteria.SortOrder.ASC)});

    final SearchRetrieveResponseVO<AffiliationDbVO> response = this.search(srr, null);

    return response.getRecords().stream().map(rec -> rec.getData()).collect(Collectors.toList());
  }

  /**
   * Returns next child affiliations of a given affiliation.
   * 
   * @param parentAffiliation The parent affiliation
   * 
   * @return next child affiliations
   * @throws Exception if framework access fails
   */
  public List<AffiliationDbVO> searchChildOrganizations(String parentAffiliationId)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    final QueryBuilder qb = QueryBuilders.termQuery(INDEX_PARENT_AFFILIATIONS_OBJECT_ID, parentAffiliationId);
    final SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(qb, OU_SEARCH_LIMIT, 0,
        new SearchSortCriteria[] {new SearchSortCriteria(INDEX_STATE, SearchSortCriteria.SortOrder.DESC),
            new SearchSortCriteria(INDEX_METADATA_TITLE_KEYWORD, SearchSortCriteria.SortOrder.ASC)});
    final SearchRetrieveResponseVO<AffiliationDbVO> response = this.search(srr, null);

    return response.getRecords().stream().map(rec -> rec.getData()).collect(Collectors.toList());
  }

  /**
   * Returns all child affiliations of given affiliations.
   * 
   * @param parentAffiliations The parent affiliations
   * 
   * @return all child affiliations
   * @throws Exception if framework access fails
   */
  public List<AffiliationDbVO> searchAllChildOrganizations(String[] parentAffiliationIds, String ignoreOuId)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    List<AffiliationDbVO> result = new ArrayList<AffiliationDbVO>();
    for (String parentAffiliationId : parentAffiliationIds) {
      if (!parentAffiliationId.equals(ignoreOuId)) {
        result.add(this.get(parentAffiliationId, null));
        List<AffiliationDbVO> children = this.searchChildOrganizations(parentAffiliationId);
        List<String> childrenIds = new ArrayList<String>();
        for (AffiliationDbVO child : children) {
          childrenIds.add(child.getObjectId());
        }
        if (childrenIds.size() > 0) {
          result.addAll(this.searchAllChildOrganizations(childrenIds.toArray(new String[childrenIds.size()]), ignoreOuId));
        }
      }
    }

    return result;
  }

  public List<AffiliationDbVO> searchSuccessors(String objectId)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    final QueryBuilder qb = QueryBuilders.boolQuery().must(QueryBuilders.termQuery(INDEX_PREDECESSOR_AFFILIATIONS_OBJECT_ID, objectId));
    final SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(qb, OU_SEARCH_LIMIT, 0);
    final SearchRetrieveResponseVO<AffiliationDbVO> response = this.search(srr, null);

    return response.getRecords().stream().map(rec -> rec.getData()).collect(Collectors.toList());
  }

  private void fillWithChildOus(List<String> idList, String ouId) throws IngeApplicationException, IngeTechnicalException {
    SearchSourceBuilder ssb = new SearchSourceBuilder();
    ssb.docValueField(INDEX_OBJECT_ID);
    ssb.query(SearchUtils.baseElasticSearchQueryBuilder(getElasticSearchIndexFields(), INDEX_PARENT_AFFILIATIONS_OBJECT_ID, ouId));
    ssb.size(500);

    SearchResponse resp = null;
    List<SearchHit> listHits = new ArrayList<SearchHit>();
    do {
      if (resp == null) {
        resp = organizationDao.searchDetailed(ssb, 120000);
        for (SearchHit searchHit : resp.getHits().getHits()) {
          listHits.add(searchHit);
        }
      } else {
        resp = organizationDao.scrollOn(resp.getScrollId(), 120000);
        for (SearchHit searchHit : resp.getHits().getHits()) {
          listHits.add(searchHit);
        }
      }
    } while (resp.getHits().getHits().length != 0);

    // StringBuilder sb = new StringBuilder();
    // sb.append(ouId + " size children: " + listHits.size() + " -> ");
    // for (SearchHit hit : listHits) {
    // sb.append((String) hit.field(INDEX_OBJECT_ID).getValue());
    // sb.append(" ");
    // }
    // logger.info(sb.toString());

    if (listHits.size() > 0) {
      for (SearchHit hit : listHits) {
        fillWithChildOus(idList, hit.field(INDEX_OBJECT_ID).getValue());
      }
    }

    idList.add(ouId);

    // sb = new StringBuilder();
    // sb.append(ouId + ": ");
    // for (String id : idList) {
    // sb.append(id);
    // sb.append(" ");
    // }
    // logger.info(sb.toString());
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void delete(String id, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    AffiliationDbVO ouDbTobeDeleted = organizationRepository.findOne(id);
    super.delete(id, authenticationToken);

    if (ouDbTobeDeleted.getParentAffiliation() != null) {
      // ACHTUNG: siehe Kommentar bei AffiliationDbVO @Formula
      entityManager.flush();
      entityManager.clear();
      AffiliationDbVO parentVO = organizationRepository.findOne(ouDbTobeDeleted.getParentAffiliation().getObjectId());
      organizationDao.createImmediately(parentVO.getObjectId(), parentVO);
    }



  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public AffiliationDbVO open(String id, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    return changeState(id, modificationDate, authenticationToken, AffiliationDbVO.State.OPENED);
  }


  @Override
  @Transactional(rollbackFor = Throwable.class)
  public AffiliationDbVO close(String id, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    return changeState(id, modificationDate, authenticationToken, AffiliationDbVO.State.CLOSED);
  }

  private AffiliationDbVO changeState(String id, Date modificationDate, String authenticationToken, AffiliationDbVO.State state)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Principal principal = aaService.checkLoginRequired(authenticationToken);
    AffiliationDbVO affDbToBeUpdated = organizationRepository.findOne(id);
    if (affDbToBeUpdated == null) {
      throw new IngeApplicationException("Organization with given id " + id + " not found.");
    }



    checkEqualModificationDate(modificationDate, affDbToBeUpdated.getLastModificationDate());

    if (affDbToBeUpdated.getParentAffiliation() != null && state == AffiliationDbVO.State.OPENED) {
      AffiliationDbVO parentVo = organizationRepository.findOne(affDbToBeUpdated.getParentAffiliation().getObjectId());
      if (parentVo.getPublicStatus() != AffiliationDbVO.State.OPENED) {
        throw new IngeApplicationException("Parent organization " + parentVo.getObjectId() + " must be in state OPENED");
      }
    }

    checkAa((state == AffiliationDbVO.State.OPENED ? "open" : "close"), principal, affDbToBeUpdated);

    affDbToBeUpdated.setPublicStatus(state);
    updateWithTechnicalMetadata(affDbToBeUpdated, principal.getUserAccount(), false);
    try {
      affDbToBeUpdated = organizationRepository.saveAndFlush(affDbToBeUpdated);
    } catch (DataAccessException e) {
      handleDBException(e);
    }

    organizationDao.createImmediately(affDbToBeUpdated.getObjectId(), affDbToBeUpdated);
    return affDbToBeUpdated;
  }


  @Override
  protected AffiliationDbVO createEmptyDbObject() {
    return new AffiliationDbVO();
  }


  @Override
  protected List<String> updateObjectWithValues(AffiliationDbVO givenAff, AffiliationDbVO toBeUpdatedAff, AccountUserDbVO userAccount,
      boolean createNew) throws IngeTechnicalException, IngeApplicationException {

    if (createNew) {
      toBeUpdatedAff.setObjectId(idProviderService.getNewId(ID_PREFIX.OU));
      toBeUpdatedAff.setPublicStatus(AffiliationDbVO.State.OPENED);
    }

    toBeUpdatedAff.setMetadata(givenAff.getMetadata());

    toBeUpdatedAff.setName(givenAff.getMetadata().getName());

    if (givenAff.getMetadata().getName() == null || givenAff.getMetadata().getName().trim().isEmpty()) {
      throw new IngeApplicationException("Please provide a name for the organization.");
    }

    // Set new parents, parents must be in state OPENED
    String oldParentAffId = toBeUpdatedAff.getParentAffiliation() != null ? toBeUpdatedAff.getParentAffiliation().getObjectId() : null;
    String newParentAffId = givenAff.getParentAffiliation() != null ? givenAff.getParentAffiliation().getObjectId() : null;

    toBeUpdatedAff.setPredecessorAffiliations(givenAff.getPredecessorAffiliations());

    List<String> reindexList = new ArrayList<>();

    if (oldParentAffId == null && newParentAffId == null) {
      return reindexList;
    }

    if ((oldParentAffId != null && newParentAffId == null) || (oldParentAffId == null && newParentAffId != null)
        || !oldParentAffId.equals(newParentAffId)) {

      if (oldParentAffId != null) {
        reindexList.add(oldParentAffId);
      }
      if (newParentAffId != null) {
        AffiliationDbVO newAffVo = organizationRepository.findOne(newParentAffId);
        if (newAffVo.getPublicStatus() != AffiliationDbVO.State.OPENED) {
          throw new IngeApplicationException(
              "Parent Affiliation " + newAffVo.getObjectId() + " has wrong state " + newAffVo.getPublicStatus().toString());
        }
        toBeUpdatedAff.setParentAffiliation(newAffVo);
        reindexList.add(newParentAffId);
      } else {
        toBeUpdatedAff.setParentAffiliation(null);
      }
    }



    return reindexList;
  }

  /**
   * Returns the ou path from the given id up to root parent
   */
  @Transactional
  public String getOuPath(String id) throws IngeTechnicalException, IngeApplicationException {

    AffiliationDbVO affVo = organizationRepository.findOne(id);
    if (affVo == null)
      throw new IngeApplicationException("Could not find organization with id " + id);

    StringBuilder ouPath = new StringBuilder();
    ouPath.append(affVo.getName());
    while (affVo.getParentAffiliation() != null) {
      ouPath.append(", ");
      ouPath.append(affVo.getParentAffiliation().getName());
      affVo = (AffiliationDbVO) affVo.getParentAffiliation();
    }

    return ouPath.toString();
  }


  /**
   * Returns the path from the given id up to root parent
   */
  @Transactional
  public List<String> getIdPath(String id) throws IngeTechnicalException, IngeApplicationException {

    AffiliationDbVO affVo = organizationRepository.findOne(id);
    if (affVo == null)
      throw new IngeApplicationException("Could not find organization with id " + id);

    List<String> idPath = new ArrayList<>();
    idPath.add(affVo.getObjectId());
    while (affVo.getParentAffiliation() != null) {
      idPath.add(affVo.getParentAffiliation().getObjectId());
      affVo = (AffiliationDbVO) affVo.getParentAffiliation();
    }

    return idPath;

  }


  @Transactional
  public List<String> getChildIdPath(String id) throws IngeTechnicalException, IngeApplicationException {
    List<String> ouIds = new ArrayList<>();
    fillWithChildOus(ouIds, id);
    return ouIds;
  }


  @Override
  protected JpaRepository<AffiliationDbVO, String> getDbRepository() {
    return organizationRepository;
  }


  @Override
  protected GenericDaoEs<AffiliationDbVO> getElasticDao() {
    return organizationDao;
  }


  @Override
  protected String getObjectId(AffiliationDbVO object) {
    return object.getObjectId();
  }

  @Override
  protected Date getModificationDate(AffiliationDbVO object) {
    return object.getLastModificationDate();
  }

  @Override
  @JmsListener(containerFactory = "queueContainerFactory", destination = "reindex-AffiliationDbVO")
  public void reindexListener(String id) throws IngeTechnicalException {
    reindex(id, false);
  }

  @Override
  public List<String> getAllChildrenOfMpg() throws IngeTechnicalException, IngeApplicationException {
    return this.allChildrenOfMpg;
  }

  @Override
  @Scheduled(fixedDelay = 600000, initialDelay = 60000)
  public void refreshAllChildrenOfMpg() {
    try {
      logger.info("CRON: refreshAllChildrenOfMpg() started...");
      List<String> allChildrenOfMpg_ = this.getChildIdPath(this.mpgId);
      this.allChildrenOfMpg = allChildrenOfMpg_;
      logger.info("CRON: refreshAllChildrenOfMpg() finished (" + this.allChildrenOfMpg.size() + ").");
    } catch (IngeTechnicalException | IngeApplicationException e) {
      logger.error("CRON: refreshAllChildrenOfMpg() failed!", e);
    }
  }

}
