package de.mpg.mpdl.inge.service.pubman.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.dao.ContextDao;
import de.mpg.mpdl.inge.dao.PubItemDao;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl.ID_PREFIX;
import de.mpg.mpdl.inge.inge_validation.ItemValidatingService;
import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO.State;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria.SortOrder;
import de.mpg.mpdl.inge.model.valueobjects.VersionHistoryEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.util.PubItemUtil;
import de.mpg.mpdl.inge.services.IngeServiceException;

@Service
public class PubItemServiceImpl implements PubItemService {

  private final static Logger logger = LogManager.getLogger(PubItemServiceImpl.class);

  public static String INDEX_VERSION_OBJECT_ID = "version.objectId";
  public static String INDEX_VERSION_VERSIONNUMBER = "version.versionNumber";
  public static String INDEX_LATESTVERSION_VERSIONNUMBER = "latestVersion.versionNumber";
  public static String INDEX_VERSION_STATE = "version.state";
  public static String INDEX_PUBLIC_STATE = "publicStatus";
  public static String INDEX_OWNER_OBJECT_ID = "owner.objectId";
  public static String INDEX_CONTEXT_OBEJCT_ID = "context.objectId";
  public static String INDEX_LOCAL_TAGS = "localTags";

  @Autowired
  private PubItemDao<QueryBuilder> pubItemDao;

  @Autowired
  private AuthorizationService aaService;

  @Autowired
  private ContextDao<QueryBuilder> contextDao;

  @Autowired
  private IdentifierProviderServiceImpl idProviderService;

  @Override
  public PubItemVO create(PubItemVO pubItemVO, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException {
    long start = System.currentTimeMillis();
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);

    ContextVO context = contextDao.get(pubItemVO.getContext().getObjectId());

    PubItemVO pubItemToCreate =
        buildPubItemToCreate(pubItemVO, null, userAccount, context, 1, State.PENDING, State.PENDING);

    aaService.checkPubItemAa(pubItemToCreate, context, userAccount, "create");

    PubItemUtil.cleanUpItem(pubItemToCreate);
    validate(pubItemToCreate);

    String id = idProviderService.getNewId(ID_PREFIX.ITEM);
    String fullId = id + "_1";
    pubItemToCreate.getVersion().setObjectId(id);

    pubItemDao.create(fullId, pubItemToCreate);
    long time = System.currentTimeMillis() - start;
    logger.info("PubItem " + fullId + " successfully created in " + time + " ms");

    return get(fullId, authenticationToken);
  }

  private PubItemVO buildPubItemToCreate(PubItemVO templateVO, PubItemVO latestVersion,
      AccountUserVO userAccount, ContextVO context, int versionNumber, State versionState,
      State publicState) {
    PubItemVO pubItemToCreate = new PubItemVO(templateVO);

    Date currentDate = new Date();
    pubItemToCreate.setContentModel(null);
    pubItemToCreate.setContentModelHref(null);
    pubItemToCreate.setLatestRelease(null);
    pubItemToCreate.setLatestVersion(null);
    pubItemToCreate.setLockStatus(null);

    pubItemToCreate.setContext(context.getReference());
    pubItemToCreate.setPublicStatus(publicState);

    ItemRO itemRO = new ItemRO();
    pubItemToCreate.setVersion(itemRO);
    itemRO.setVersionNumber(versionNumber);
    itemRO.setModifiedByRO(userAccount.getReference());
    itemRO.setModificationDate(currentDate);
    itemRO.setState(versionState);



    if (latestVersion == null) {
      pubItemToCreate.setOwner(userAccount.getReference());
      pubItemToCreate.setCreationDate(currentDate);

    } else {
      itemRO.setObjectId(latestVersion.getVersion().getObjectId());
      pubItemToCreate.setOwner(latestVersion.getOwner());
      pubItemToCreate.setCreationDate(latestVersion.getCreationDate());
      itemRO.setPid(latestVersion.getPid());
      itemRO.setLastMessage(latestVersion.getVersion().getObjectId());
    }
    return pubItemToCreate;
  }

  @Override
  public PubItemVO update(PubItemVO pubItemVO, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException {

    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);

    PubItemVO latestVersion = getLatestVersion(pubItemVO.getVersion().getObjectId());

    ContextVO context = contextDao.get(pubItemVO.getContext().getObjectId());
    aaService.checkPubItemAa(latestVersion, context, userAccount, "update");

    State newVersionState = null;
    int newVersionNumber = latestVersion.getVersion().getVersionNumber();
    if (State.RELEASED.equals(latestVersion.getVersion().getState())) {
      newVersionState = State.PENDING;
      newVersionNumber++;
    } else {
      newVersionState = latestVersion.getVersion().getState();
    }

    PubItemVO pubItemToCreate =
        buildPubItemToCreate(pubItemVO, latestVersion, userAccount, context, newVersionNumber,
            newVersionState, latestVersion.getPublicStatus());

    PubItemUtil.cleanUpItem(pubItemToCreate);
    validate(pubItemToCreate);

    String newFullId = pubItemToCreate.getVersion().getObjectId() + "_" + newVersionNumber;

    if (newVersionNumber == latestVersion.getVersion().getVersionNumber()) {
      pubItemDao.update(newFullId, pubItemToCreate);
    } else {
      pubItemDao.create(newFullId, pubItemToCreate);
    }

    return get(newFullId, authenticationToken);
  }

  @Override
  public void delete(String id, String authenticationToken) throws IngeServiceException,
      AaException {
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);

    SearchRetrieveResponseVO<PubItemVO> resp = getAllVersions(id);
    if (resp.getNumberOfRecords() == 0) {
      throw new IngeServiceException("Item " + id + " not found");
    }

    PubItemVO latestPubItem = resp.getRecords().get(0).getData();
    ContextVO context =
        contextDao.get(latestPubItem.getContext().getObjectId()
            .replace("/ir/context/escidoc:", "pure_"));
    aaService.checkPubItemAa(latestPubItem, context, userAccount, "delete");

    for (SearchRetrieveRecordVO<PubItemVO> rec : resp.getRecords()) {
      pubItemDao.delete(rec.getPersistenceId());
    }

    logger.info("PubItem " + id + " successfully deleted");
  }

  @Override
  public PubItemVO get(String id, String authenticationToken) throws IngeServiceException,
      AaException {
    long start = System.currentTimeMillis();
    String[] splittedId = id.split("_");
    String version = null;
    String objectId = splittedId[0] + "_" + splittedId[1];
    if (splittedId.length == 3) {
      version = splittedId[2];
    }

    PubItemVO latestVersion = getLatestVersion(objectId);
    PubItemVO latestRelease = getLatestRelease(objectId);
    PubItemVO requestedItem;

    System.out.println("LV" + latestVersion);
    System.out.println("LR" + latestRelease);

    if (latestVersion == null) {
      throw new IngeServiceException("Item " + id + " not found");
    }

    AccountUserVO userAccount = null;
    if (authenticationToken != null) {
      userAccount = aaService.checkLoginRequired(authenticationToken);
    }
    ContextVO context =
        contextDao.get(latestVersion.getContext().getObjectId()
            .replace("/ir/context/escidoc:", "pure_"));

    if (version != null) {
      requestedItem = pubItemDao.get(id);
    } else {

      try {
        aaService.checkPubItemAa(latestVersion, context, userAccount, "get");
        requestedItem = latestVersion;
      } catch (AaException e) {
        System.out.println(e);
        requestedItem = latestRelease;
      }
    }
    if (requestedItem == null) {
      throw new IngeServiceException("Item " + id + "not found");
    }


    aaService.checkPubItemAa(requestedItem, context, userAccount, "get");


    requestedItem.setLatestVersion(latestVersion.getVersion());
    if (latestRelease != null) {
      requestedItem.setLatestRelease(latestRelease.getVersion());
    }
    long time = System.currentTimeMillis() - start;
    logger.info("PubItem " + id + " successfully retrieved in " + time + " ms");
    return requestedItem;
  }

  @Override
  public SearchRetrieveResponseVO<PubItemVO> search(SearchRetrieveRequestVO<QueryBuilder> srr,
      String authenticationToken) throws IngeServiceException, AaException {

    QueryBuilder authorizedQuery;

    if (authenticationToken == null) {
      authorizedQuery = aaService.modifyPubItemQueryForAa(srr.getQueryObject(), null);
    } else {
      AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
      authorizedQuery = aaService.modifyPubItemQueryForAa(srr.getQueryObject(), userAccount);
    }

    srr.setQueryObject(authorizedQuery);
    logger.info("Searching with authorized query: \n" + authorizedQuery.toString());
    return pubItemDao.search(srr);
  }

  @Override
  public PubItemVO submitPubItem(String pubItemId, String message, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException {
    return changeState(pubItemId, State.SUBMITTED, message, "submit", authenticationToken);
  }

  @Override
  public PubItemVO revisePubItem(String pubItemId, String message, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException {
    return changeState(pubItemId, State.IN_REVISION, message, "revise", authenticationToken);
  }

  @Override
  public PubItemVO releasePubItem(String pubItemId, String message, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException {
    return changeState(pubItemId, State.RELEASED, message, "release", authenticationToken);
  }

  @Override
  public PubItemVO withdrawPubItem(String pubItemId, String message, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException {
    return changeState(pubItemId, State.WITHDRAWN, message, "withdraw", authenticationToken);
  }


  private PubItemVO changeState(String id, State state, String message, String aaMethod,
      String authenticationToken) throws IngeServiceException, AaException, ItemInvalidException {
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    PubItemVO latestVersion = getLatestVersion(id);
    ContextVO context =
        contextDao.get(latestVersion.getContext().getObjectId()
            .replace("/ir/context/escidoc:", "pure_"));

    aaService.checkPubItemAa(latestVersion, context, userAccount, aaMethod);

    State newVersionState = state;
    State newPublicState = latestVersion.getPublicStatus();

    if (State.SUBMITTED.equals(state) && !State.RELEASED.equals(latestVersion.getPublicStatus())) {
      newPublicState = State.SUBMITTED;
    } else if (State.RELEASED.equals(state)) {
      newPublicState = State.RELEASED;
    }
    if (State.WITHDRAWN.equals(state)) {
      newVersionState = latestVersion.getVersion().getState();
      newPublicState = State.WITHDRAWN;
    }

    PubItemVO pubItemToCreate =
        buildPubItemToCreate(latestVersion, latestVersion, userAccount, context, latestVersion
            .getVersion().getVersionNumber(), newVersionState, newPublicState);

    pubItemToCreate.getVersion().setLastMessage(message);
    validate(pubItemToCreate);

    String fullId =
        pubItemToCreate.getVersion().getObjectId() + "_"
            + pubItemToCreate.getVersion().getVersionNumber();

    pubItemDao.update(fullId, pubItemToCreate);
    return get(fullId, authenticationToken);
  }

  private PubItemVO getLatestRelease(String objectId) throws IngeServiceException {
    QueryBuilder latestReleaseQuery =
        QueryBuilders.boolQuery().must(QueryBuilders.termQuery(INDEX_VERSION_OBJECT_ID, objectId))
            .must(QueryBuilders.termQuery(INDEX_VERSION_STATE, "RELEASED"));
    SearchSortCriteria sortByVersion =
        new SearchSortCriteria(INDEX_VERSION_OBJECT_ID, SortOrder.DESC);
    SearchRetrieveRequestVO<QueryBuilder> srr =
        new SearchRetrieveRequestVO<QueryBuilder>(latestReleaseQuery, 1, 0, sortByVersion);
    SearchRetrieveResponseVO<PubItemVO> resp = pubItemDao.search(srr);

    if (resp.getNumberOfRecords() > 0) {
      return resp.getRecords().get(0).getData();
    }
    return null;
  }


  private PubItemVO getLatestVersion(String objectId) throws IngeServiceException {
    QueryBuilder latestVersionQuery = QueryBuilders.termQuery(INDEX_VERSION_OBJECT_ID, objectId);
    SearchRetrieveResponseVO<PubItemVO> resp = executeSearchSortByVersion(latestVersionQuery, 1, 0);
    if (resp.getNumberOfRecords() > 0) {
      return resp.getRecords().get(0).getData();
    }
    return null;
  }

  private SearchRetrieveResponseVO<PubItemVO> getAllVersions(String objectId)
      throws IngeServiceException {
    QueryBuilder latestReleaseQuery = QueryBuilders.termQuery(INDEX_VERSION_OBJECT_ID, objectId);
    SearchRetrieveResponseVO<PubItemVO> resp =
        executeSearchSortByVersion(latestReleaseQuery, 10000, 0);

    return resp;
  }

  private SearchRetrieveResponseVO<PubItemVO> executeSearchSortByVersion(QueryBuilder query,
      int limit, int offset) throws IngeServiceException {

    SearchSortCriteria sortByVersion =
        new SearchSortCriteria(INDEX_VERSION_OBJECT_ID, SortOrder.DESC);
    SearchRetrieveRequestVO<QueryBuilder> srr =
        new SearchRetrieveRequestVO<QueryBuilder>(query, limit, offset, sortByVersion);
    return pubItemDao.search(srr);
  }

  private void validate(PubItemVO pubItem) throws IngeServiceException, ItemInvalidException {
    ValidationPoint vp = ValidationPoint.STANDARD;

    if (pubItem.getPublicStatus() != null && State.PENDING.equals(pubItem.getPublicStatus())) {
      vp = ValidationPoint.SAVE;
    }

    validate(pubItem, vp);
  }

  private void validate(PubItemVO pubItem, ValidationPoint vp) throws IngeServiceException,
      ItemInvalidException {
    try {
      ItemValidatingService.validate(pubItem, vp);
    } catch (ValidationException e) {
      throw new IngeServiceException(e);
    }
  }

  @Override
  public void reindex() {
    // TODO Auto-generated method stub

  }

  @Override
  public List<VersionHistoryEntryVO> getVersionHistory(String pubItemId, String authenticationToken)
      throws IngeServiceException, AaException {
    // TODO Auto-generated method stub
    return null;
  }
}
