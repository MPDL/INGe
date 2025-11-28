package de.mpg.mpdl.inge.service.pubman.impl;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.ExistsQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl;
import de.mpg.mpdl.inge.db.repository.OrganizationRepository;
import de.mpg.mpdl.inge.es.dao.GenericDaoEs;
import de.mpg.mpdl.inge.es.dao.OrganizationDaoEs;
import de.mpg.mpdl.inge.es.dao.impl.ElasticSearchGenericDAOImpl;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
public class OrganizationServiceDbImpl extends GenericServiceImpl<AffiliationDbVO, String> implements OrganizationService, ReindexListener {

  public static final String INDEX_OBJECT_ID = "objectId";
  public static final String INDEX_METADATA_TITLE = "metadata.name";
  public static final String INDEX_METADATA_TITLE_KEYWORD = "metadata.name.keyword";
  public static final String INDEX_METADATA_ALTERNATIVE_NAMES = "metadata.alternativeNames";
  public static final String INDEX_METADATA_CITY = "metadata.city";
  public static final String INDEX_PARENT_AFFILIATIONS_OBJECT_ID = "parentAffiliation.objectId";
  public static final String INDEX_PREDECESSOR_AFFILIATIONS_OBJECT_ID = "predecessorAffiliations.objectId";
  public static final String INDEX_STATE = "publicStatus.keyword";
  public static final int OU_SEARCH_LIMIT = -2; // unbegrenzte Suche

  private static final Logger logger = LogManager.getLogger(OrganizationServiceDbImpl.class);

  private final String mpgId = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_ROOT_ORGANIZATION_ID);

  //  private List<String> allChildrenOfMpg = new ArrayList<>();

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

    Query qb = BoolQuery.of(b1 -> b1.mustNot(ExistsQuery.of(eq -> eq.field(INDEX_PARENT_AFFILIATIONS_OBJECT_ID))._toQuery()))._toQuery();
    SearchRetrieveRequestVO srr =
        new SearchRetrieveRequestVO(qb, OU_SEARCH_LIMIT, 0, new SearchSortCriteria(INDEX_STATE, SearchSortCriteria.SortOrder.DESC),
            new SearchSortCriteria(INDEX_METADATA_TITLE_KEYWORD, SearchSortCriteria.SortOrder.DESC));
    SearchRetrieveResponseVO<AffiliationDbVO> response = this.search(srr, null);

    return response.getRecords().stream().map(SearchRetrieveRecordVO::getData).collect(Collectors.toList());
  }

  /**
   * Returns all first-level affiliations.
   *
   * @return all first-level affiliations
   * @throws Exception if framework access fails
   */
  public List<AffiliationDbVO> searchFirstLevelOrganizations()
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    BoolQuery.Builder qb = new BoolQuery.Builder();
    List<AffiliationDbVO> topLevelOus = this.searchTopLevelOrganizations();

    if (topLevelOus.isEmpty()) {
      return new ArrayList<>();
    }

    List<FieldValue> topLevelOuIds = new ArrayList<>();
    for (AffiliationDbVO affiliationDbVO : topLevelOus) {
      topLevelOuIds.add(FieldValue.of(affiliationDbVO.getObjectId()));
    }
    qb.filter(TermsQuery.of(t -> t.field(INDEX_PARENT_AFFILIATIONS_OBJECT_ID).terms(te -> te.value(topLevelOuIds)))._toQuery());

    SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(qb.build()._toQuery(), OU_SEARCH_LIMIT, 0,
        new SearchSortCriteria(INDEX_PARENT_AFFILIATIONS_OBJECT_ID, SearchSortCriteria.SortOrder.ASC),
        new SearchSortCriteria(INDEX_METADATA_TITLE_KEYWORD, SearchSortCriteria.SortOrder.ASC));

    SearchRetrieveResponseVO<AffiliationDbVO> response = this.search(srr, null);

    return response.getRecords().stream().map(SearchRetrieveRecordVO::getData).collect(Collectors.toList());
  }

  /**
   * Returns next child affiliations of a given affiliation.
   *
   * @param parentAffiliationId The parent affiliation
   *
   * @return next child affiliations
   * @throws Exception if framework access fails
   */
  public List<AffiliationDbVO> searchChildOrganizations(String parentAffiliationId)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    Query qb = TermQuery.of(t -> t.field(INDEX_PARENT_AFFILIATIONS_OBJECT_ID).value(parentAffiliationId))._toQuery();
    SearchRetrieveRequestVO srr =
        new SearchRetrieveRequestVO(qb, OU_SEARCH_LIMIT, 0, new SearchSortCriteria(INDEX_STATE, SearchSortCriteria.SortOrder.DESC),
            new SearchSortCriteria(INDEX_METADATA_TITLE_KEYWORD, SearchSortCriteria.SortOrder.ASC));
    SearchRetrieveResponseVO<AffiliationDbVO> response = this.search(srr, null);

    return response.getRecords().stream().map(SearchRetrieveRecordVO::getData).collect(Collectors.toList());
  }

  /**
   * Returns all child affiliations of given affiliations.
   *
   * @param parentAffiliationIds The parent affiliations
   *
   * @return all child affiliations
   * @throws Exception if framework access fails
   */
  public List<AffiliationDbVO> searchAllChildOrganizations(String[] parentAffiliationIds, String ignoreOuId)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    List<AffiliationDbVO> result = new ArrayList<>();
    for (String parentAffiliationId : parentAffiliationIds) {
      if (!parentAffiliationId.equals(ignoreOuId)) {
        result.add(this.get(parentAffiliationId, null));
        List<AffiliationDbVO> children = this.searchChildOrganizations(parentAffiliationId);
        List<String> childrenIds = new ArrayList<>();
        for (AffiliationDbVO child : children) {
          childrenIds.add(child.getObjectId());
        }
        if (!childrenIds.isEmpty()) {
          result.addAll(this.searchAllChildOrganizations(childrenIds.toArray(new String[0]), ignoreOuId));
        }
      }
    }

    return result;
  }

  public List<AffiliationDbVO> searchSuccessors(String objectId)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    Query qb = BoolQuery.of(b -> b.must(TermQuery.of(t -> t.field(INDEX_PREDECESSOR_AFFILIATIONS_OBJECT_ID).value(objectId))._toQuery()))
        ._toQuery();
    SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(qb, OU_SEARCH_LIMIT, 0);
    SearchRetrieveResponseVO<AffiliationDbVO> response = this.search(srr, null);

    return response.getRecords().stream().map(SearchRetrieveRecordVO::getData).collect(Collectors.toList());
  }

  private void fillWithChildOus(List<String> idList, String ouId) throws IngeTechnicalException {

    Query query = SearchUtils.baseElasticSearchQueryBuilder(getElasticSearchIndexFields(), INDEX_PARENT_AFFILIATIONS_OBJECT_ID, ouId);
    SearchRequest ssb = SearchRequest.of(sr -> sr.docvalueFields(dv -> dv.field(INDEX_OBJECT_ID)).query(query).size(500));

    ResponseBody<ObjectNode> resp = null;
    List<Hit> listHits = new ArrayList<>();
    JsonNode searchRequestNode = ElasticSearchGenericDAOImpl.toJsonNode(ssb);
    do {
      if (null == resp) {
        resp = this.organizationDao.searchDetailed(searchRequestNode, 120000);
        listHits.addAll(resp.hits().hits());
      } else {
        resp = this.organizationDao.scrollOn(resp.scrollId(), 120000);
        listHits.addAll(resp.hits().hits());
      }
    } while (!resp.hits().hits().isEmpty());

    if (null != resp) {
      getElasticDao().clearScroll(resp.scrollId());
    }


    if (!listHits.isEmpty()) {
      for (Hit<ObjectNode> hit : listHits) {
        fillWithChildOus(idList, hit.source().get(INDEX_OBJECT_ID).asText());
      }
    }
    idList.add(ouId);
  }

  @Transactional(rollbackFor = Throwable.class)
  public AffiliationDbVO addPredecessor(String id, Date modificationDate, String predecessorId, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    AffiliationDbVO affDbToBeUpdated = this.organizationRepository.findById(id).orElse(null);
    if (null == affDbToBeUpdated) {
      throw new IngeApplicationException("Organization with given id " + id + " not found.");
    }

    Principal principal = this.aaService.checkLoginRequired(authenticationToken);
    checkAa("addPredecessor", principal, affDbToBeUpdated);

    checkEqualModificationDate(modificationDate, affDbToBeUpdated.getLastModificationDate());

    List<AffiliationDbRO> predecessors = affDbToBeUpdated.getPredecessorAffiliations();

    AffiliationDbRO predecessorToBeAdded = null;
    for (AffiliationDbRO predecessor : predecessors) {
      if (predecessor.getObjectId().equals(predecessorId)) {
        predecessorToBeAdded = predecessor;
        break;
      }
    }

    if (null != predecessorToBeAdded) {
      throw new IngeApplicationException(
          "Predecessor with given id " + predecessorId + " already exists in ou " + affDbToBeUpdated.getObjectId());
    }

    predecessorToBeAdded = new AffiliationDbRO();
    predecessorToBeAdded.setObjectId(predecessorId);
    predecessors.add(predecessorToBeAdded);
    affDbToBeUpdated.setPredecessorAffiliations(predecessors);

    updateWithTechnicalMetadata(affDbToBeUpdated, principal.getUserAccount(), false);

    try {
      affDbToBeUpdated = getDbRepository().saveAndFlush(affDbToBeUpdated);
    } catch (DataAccessException e) {
      handleDBException(e);
    }

    getElasticDao().createImmediately(affDbToBeUpdated.getObjectId(), affDbToBeUpdated);

    return affDbToBeUpdated;
  }

  @Transactional(rollbackFor = Throwable.class)
  public AffiliationDbVO removePredecessor(String id, Date modificationDate, String predecessorId, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    AffiliationDbVO affDbToBeUpdated = this.organizationRepository.findById(id).orElse(null);
    if (null == affDbToBeUpdated) {
      throw new IngeApplicationException("Organization with given id " + id + " not found.");
    }

    Principal principal = this.aaService.checkLoginRequired(authenticationToken);
    checkAa("removePredecessor", principal, affDbToBeUpdated);

    checkEqualModificationDate(modificationDate, affDbToBeUpdated.getLastModificationDate());

    List<AffiliationDbRO> predecessors = affDbToBeUpdated.getPredecessorAffiliations();

    AffiliationDbRO predecessorToBeRemoved = null;
    for (AffiliationDbRO predecessor : predecessors) {
      if (predecessor.getObjectId().equals(predecessorId)) {
        predecessorToBeRemoved = predecessor;
      }
    }

    if (null == predecessorToBeRemoved) {
      throw new IngeApplicationException(
          "Predecessor with given id " + predecessorId + " does not exist in ou " + affDbToBeUpdated.getObjectId());
    }

    predecessors.remove(predecessorToBeRemoved);
    affDbToBeUpdated.setPredecessorAffiliations(predecessors);

    updateWithTechnicalMetadata(affDbToBeUpdated, principal.getUserAccount(), false);

    try {
      affDbToBeUpdated = getDbRepository().saveAndFlush(affDbToBeUpdated);
    } catch (DataAccessException e) {
      handleDBException(e);
    }

    getElasticDao().createImmediately(affDbToBeUpdated.getObjectId(), affDbToBeUpdated);

    return affDbToBeUpdated;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void delete(String id, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    AffiliationDbVO ouDbTobeDeleted = this.organizationRepository.findById(id).orElse(null);
    if (null == ouDbTobeDeleted) {
      throw new IngeApplicationException("Organization with given id " + id + " not found.");
    }

    super.delete(id, authenticationToken);

    if (null != ouDbTobeDeleted.getParentAffiliation()) {
      // ACHTUNG: siehe Kommentar bei AffiliationDbVO @Formula
      this.entityManager.flush();
      this.entityManager.clear();
      AffiliationDbVO parentVO = this.organizationRepository.findById(ouDbTobeDeleted.getParentAffiliation().getObjectId()).orElse(null);
      this.organizationDao.createImmediately(parentVO.getObjectId(), parentVO);
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

    Principal principal = this.aaService.checkLoginRequired(authenticationToken);
    AffiliationDbVO affDbToBeUpdated = this.organizationRepository.findById(id).orElse(null);
    if (null == affDbToBeUpdated) {
      throw new IngeApplicationException("Organization with given id " + id + " not found.");
    }

    checkEqualModificationDate(modificationDate, affDbToBeUpdated.getLastModificationDate());

    if (null != affDbToBeUpdated.getParentAffiliation() && AffiliationDbVO.State.OPENED == state) {
      AffiliationDbVO parentVo = this.organizationRepository.findById(affDbToBeUpdated.getParentAffiliation().getObjectId()).orElse(null);
      if (AffiliationDbVO.State.OPENED != parentVo.getPublicStatus()) {
        throw new IngeApplicationException("Parent organization " + parentVo.getObjectId() + " must be in state OPENED");
      }
    }

    checkAa((AffiliationDbVO.State.OPENED == state ? "open" : "close"), principal, affDbToBeUpdated);

    affDbToBeUpdated.setPublicStatus(state);
    updateWithTechnicalMetadata(affDbToBeUpdated, principal.getUserAccount(), false);
    try {
      affDbToBeUpdated = this.organizationRepository.saveAndFlush(affDbToBeUpdated);
    } catch (DataAccessException e) {
      handleDBException(e);
    }

    this.organizationDao.createImmediately(affDbToBeUpdated.getObjectId(), affDbToBeUpdated);

    return affDbToBeUpdated;
  }


  @Override
  protected AffiliationDbVO createEmptyDbObject() {
    return new AffiliationDbVO();
  }


  @Override
  protected List<String> updateObjectWithValues(AffiliationDbVO givenAff, AffiliationDbVO toBeUpdatedAff, AccountUserDbVO userAccount,
      boolean createNew) throws IngeApplicationException {

    if (createNew) {
      toBeUpdatedAff.setObjectId(this.idProviderService.getNewId(IdentifierProviderServiceImpl.ID_PREFIX.OU));
      toBeUpdatedAff.setPublicStatus(AffiliationDbVO.State.OPENED);
    }

    toBeUpdatedAff.setMetadata(givenAff.getMetadata());

    toBeUpdatedAff.setName(givenAff.getMetadata().getName());

    if (null == givenAff.getMetadata().getName() || givenAff.getMetadata().getName().trim().isEmpty()) {
      throw new IngeApplicationException("Please provide a name for the organization.");
    }

    // Set new parents, parents must be in state OPENED
    String oldParentAffId = null != toBeUpdatedAff.getParentAffiliation() ? toBeUpdatedAff.getParentAffiliation().getObjectId() : null;
    String newParentAffId = null != givenAff.getParentAffiliation() ? givenAff.getParentAffiliation().getObjectId() : null;

    toBeUpdatedAff.setPredecessorAffiliations(givenAff.getPredecessorAffiliations());

    List<String> reindexList = new ArrayList<>();

    if (null == oldParentAffId && null == newParentAffId) {
      return reindexList;
    }

    if ((null != oldParentAffId && null == newParentAffId) || (null == oldParentAffId && null != newParentAffId)
        || !oldParentAffId.equals(newParentAffId)) {

      if (null != oldParentAffId) {
        reindexList.add(oldParentAffId);
      }
      if (null != newParentAffId) {
        AffiliationDbVO newAffVo = this.organizationRepository.findById(newParentAffId).orElse(null);
        if (AffiliationDbVO.State.OPENED != newAffVo.getPublicStatus()) {
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
  @Transactional(readOnly = true)
  public String getNamePath(String id) throws IngeApplicationException {

    AffiliationDbVO affVo = this.organizationRepository.findById(id).orElse(null);
    if (null == affVo)
      throw new IngeApplicationException("Could not find organization with id " + id);

    StringBuilder ouPath = new StringBuilder();
    ouPath.append(affVo.getName());
    while (null != affVo.getParentAffiliation()) {
      ouPath.append(", ");
      ouPath.append(affVo.getParentAffiliation().getName());
      affVo = (AffiliationDbVO) affVo.getParentAffiliation();
    }

    return ouPath.toString();
  }

  @Transactional(readOnly = true)
  public List<AffiliationDbVO> getOuPath(String id) throws IngeApplicationException {

    AffiliationDbVO affVo = this.organizationRepository.findById(id).orElse(null);
    if (null == affVo)
      throw new IngeApplicationException("Could not find organization with id " + id);

    List<AffiliationDbVO> ouPath = new ArrayList<>();

    ouPath.add(affVo);
    while (null != affVo.getParentAffiliation()) {
      ouPath.add((AffiliationDbVO) affVo.getParentAffiliation());
      affVo = (AffiliationDbVO) affVo.getParentAffiliation();
    }

    return ouPath;
  }

  /**
   * Returns the path from the given id up to root parent
   */
  @Transactional(readOnly = true)
  public List<String> getIdPath(String id) throws IngeApplicationException {
    AffiliationDbVO affVo = this.organizationRepository.findById(id).orElse(null);
    if (null == affVo)
      throw new IngeApplicationException("Could not find organization with id " + id);

    List<String> idPath = new ArrayList<>();
    idPath.add(affVo.getObjectId());
    while (null != affVo.getParentAffiliation()) {
      idPath.add(affVo.getParentAffiliation().getObjectId());
      affVo = (AffiliationDbVO) affVo.getParentAffiliation();
    }

    return idPath;
  }

  @Transactional(readOnly = true)
  public List<String> getChildIdPath(String id) throws IngeTechnicalException {
    List<String> ouIds = new ArrayList<>();
    fillWithChildOus(ouIds, id);

    return ouIds;
  }

  @Override
  protected JpaRepository<AffiliationDbVO, String> getDbRepository() {
    return this.organizationRepository;
  }

  @Override
  protected GenericDaoEs<AffiliationDbVO> getElasticDao() {
    return this.organizationDao;
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

  //  @Override
  //  public List<String> getAllChildrenOfMpg() {
  //    return this.allChildrenOfMpg;
  //  }

  //  @Override
  //  @Scheduled(cron = "${inge.cron.refresh_all_children_of_mpg}")
  //  @PostConstruct
  //  public void refreshAllChildrenOfMpg() {
  //    try {
  //      logger.info("*** CRON (" + PropertyReader.getProperty(PropertyReader.INGE_CRON_REFRESH_ALL_CHILDREN_OF_MPG)
  //          + "): refreshAllChildrenOfMpg() started...");
  //      List<String> allChildrenOfMpg_ = this.getChildIdPath(this.mpgId);
  //      this.allChildrenOfMpg = allChildrenOfMpg_;
  //      logger.info("*** CRON: refreshAllChildrenOfMpg() finished (" + this.allChildrenOfMpg.size() + ").");
  //    } catch (IngeTechnicalException e) {
  //      logger.error("*** CRON: refreshAllChildrenOfMpg() failed!", e);
  //    }
  //  }
}
