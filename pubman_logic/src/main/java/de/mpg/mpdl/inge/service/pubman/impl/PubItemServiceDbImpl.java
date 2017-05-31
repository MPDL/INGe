package de.mpg.mpdl.inge.service.pubman.impl;

import java.util.Date;
import java.util.List;

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

import de.mpg.mpdl.inge.db.model.valueobjects.AuditDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.AuditDbVO.EventType;
import de.mpg.mpdl.inge.db.model.valueobjects.PubItemDbRO;
import de.mpg.mpdl.inge.db.model.valueobjects.PubItemDbRO.State;
import de.mpg.mpdl.inge.db.model.valueobjects.PubItemObjectDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.PubItemVersionDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.VersionableId;
import de.mpg.mpdl.inge.db.repository.AuditRepository;
import de.mpg.mpdl.inge.db.repository.ContextRepository;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl.ID_PREFIX;
import de.mpg.mpdl.inge.db.repository.ItemObjectRepository;
import de.mpg.mpdl.inge.db.repository.ItemRepository;
import de.mpg.mpdl.inge.es.dao.PubItemDaoEs;
import de.mpg.mpdl.inge.inge_validation.ItemValidatingService;
import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.model.exception.IngeServiceException;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria.SortOrder;
import de.mpg.mpdl.inge.model.valueobjects.VersionHistoryEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.util.EntityTransformer;
import de.mpg.mpdl.inge.service.util.PubItemUtil;

@Service
@Primary
public class PubItemServiceDbImpl implements PubItemService {

  private final static Logger logger = LogManager.getLogger(PubItemServiceDbImpl.class);


  @Autowired
  private AuthorizationService aaService;

  @Autowired
  private IdentifierProviderServiceImpl idProviderService;

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private ContextRepository contextRepository;

  @Autowired
  private ItemObjectRepository itemObjectRepository;

  @Autowired
  private AuditRepository auditRepository;

  @Autowired
  private PubItemDaoEs<QueryBuilder> pubItemDao;

  @PersistenceContext
  EntityManager entityManager;


  public static String INDEX_MODIFICATION_DATE = "version.modificationDate";


  public static String INDEX_LOCAL_TAGS = "localTags";


  public static String INDEX_CONTEXT_OBEJCT_ID = "context.objectId";


  public static String INDEX_OWNER_OBJECT_ID = "owner.objectId";


  public static String INDEX_PUBLIC_STATE = "publicStatus";


  public static String INDEX_VERSION_STATE = "version.state";


  public static String INDEX_LATESTVERSION_VERSIONNUMBER = "latestVersion.versionNumber";


  public static String INDEX_VERSION_VERSIONNUMBER = "version.versionNumber";


  public static String INDEX_VERSION_OBJECT_ID = "version.objectId";

  @Override
  @Transactional
  public PubItemVO create(PubItemVO pubItemVO, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException {
    long start = System.currentTimeMillis();
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);

    de.mpg.mpdl.inge.db.model.valueobjects.ContextDbVO contextNew =
        contextRepository.findOne(pubItemVO.getContext().getObjectId());
    ContextVO contextOld = EntityTransformer.transformToOld(contextNew);

    PubItemVersionDbVO pubItemToCreate =
        buildPubItemToCreate("dummyId", contextNew, pubItemVO.getMetadata(),
            pubItemVO.getLocalTags(), userAccount.getReference().getTitle(), userAccount
                .getReference().getObjectId());

    PubItemVO pubItemToCreateOld = EntityTransformer.transformToOld(pubItemToCreate);

    checkPubItemAa(pubItemToCreateOld, contextOld, userAccount, "create");

    validate(pubItemToCreateOld, ValidationPoint.SAVE);

    String id = idProviderService.getNewId(ID_PREFIX.ITEM);
    String fullId = id + "_1";
    pubItemToCreate.setObjectId(id);
    pubItemToCreate.getObject().setObjectId(id);

    pubItemToCreate = itemRepository.save(pubItemToCreate);
    PubItemVO itemToReturn = EntityTransformer.transformToOld(pubItemToCreate);

    createAuditEntry(pubItemToCreate, EventType.CREATE);
    reindex(pubItemToCreate);
    long time = System.currentTimeMillis() - start;
    logger.info("PubItem " + fullId + " successfully created in " + time + " ms");

    return itemToReturn;
  }

  private void createAuditEntry(PubItemVersionDbVO pubItem, EventType event) {
    AuditDbVO audit = new AuditDbVO();
    audit.setEvent(event);
    audit.setComment(pubItem.getLastMessage());
    audit.setModificationDate(pubItem.getModificationDate());
    audit.setModifier(pubItem.getModifiedBy());
    audit.setPubItem(pubItem);
    auditRepository.save(audit);
  }

  private PubItemVersionDbVO buildPubItemToCreate(String objectId,
      de.mpg.mpdl.inge.db.model.valueobjects.ContextDbVO context, MdsPublicationVO md,
      List<String> localTags, String modifierName, String modifierId) {
    Date currentDate = new Date();

    PubItemVersionDbVO pubItem = new PubItemVersionDbVO();
    pubItem.getFiles().clear();// TODO
    pubItem.setMetadata(md);
    pubItem.setLastMessage(null);
    pubItem.setModificationDate(currentDate);
    de.mpg.mpdl.inge.db.model.valueobjects.AccountUserDbRO mod =
        new de.mpg.mpdl.inge.db.model.valueobjects.AccountUserDbRO();
    mod.setName(modifierName);
    mod.setObjectId(modifierId);
    pubItem.setModifiedBy(mod);
    pubItem.setObjectId(objectId);
    pubItem.setState(de.mpg.mpdl.inge.db.model.valueobjects.PubItemDbRO.State.PENDING);
    pubItem.setVersionNumber(1);
    pubItem.setVersionPid(null);// TODO

    PubItemObjectDbVO pubItemObject = new PubItemObjectDbVO();
    pubItemObject.setContext(context);
    pubItemObject.setCreationDate(currentDate);
    pubItemObject.setLastModificationDate(currentDate);
    pubItemObject.setLatestVersion(pubItem);
    pubItemObject.setLocalTags(localTags);
    pubItemObject.setObjectId(objectId);
    pubItemObject.setOwner(mod);
    pubItemObject.setPid(null);// TODO
    pubItemObject.setPublicStatus(de.mpg.mpdl.inge.db.model.valueobjects.PubItemDbRO.State.PENDING);
    pubItemObject.setPublicStatusComment(null);

    pubItem.setObject(pubItemObject);
    return pubItem;
  }


  private PubItemDbRO updatePubItemWithTechnicalMd(PubItemVersionDbVO latestVersion,
      String modifierName, String modifierId) {
    Date currentDate = new Date();


    latestVersion.getFiles().clear();// TODO
    latestVersion.setModificationDate(currentDate);
    de.mpg.mpdl.inge.db.model.valueobjects.AccountUserDbRO mod =
        new de.mpg.mpdl.inge.db.model.valueobjects.AccountUserDbRO();
    mod.setName(modifierName);
    mod.setObjectId(modifierId);
    latestVersion.setModifiedBy(mod);
    latestVersion.getObject().setLastModificationDate(currentDate);

    return latestVersion;
  }


  @Override
  @Transactional
  public PubItemVO update(PubItemVO pubItemVO, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException {
    long start = System.currentTimeMillis();
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);

    PubItemVersionDbVO latestVersion =
        itemRepository.findLatestVersion(pubItemVO.getVersion().getObjectId());
    PubItemVO latestVersionOld = EntityTransformer.transformToOld(latestVersion);

    ContextVO context =
        EntityTransformer.transformToOld(contextRepository.findOne(pubItemVO.getContext()
            .getObjectId()));

    checkPubItemAa(latestVersionOld, context, userAccount, "update");

    if (State.RELEASED.equals(latestVersion.getState())) {
      entityManager.detach(latestVersion);
      // Reset latestRelase reference because it is the same object as latest version
      PubItemDbRO latestReleaseRO = new PubItemDbRO();
      latestReleaseRO.setObjectId(latestVersion.getObject().getLatestRelease().getObjectId());
      latestReleaseRO.setVersionNumber(latestVersion.getObject().getLatestRelease()
          .getVersionNumber());
      latestVersion.getObject().setLatestRelease(latestReleaseRO);

      // if current user is owner, set to status pending. Else, set to status submitted

      if (userAccount.isModerator(context.getReference())) {
        latestVersion.setState(de.mpg.mpdl.inge.db.model.valueobjects.PubItemDbRO.State.SUBMITTED);
      } else {
        latestVersion.setState(de.mpg.mpdl.inge.db.model.valueobjects.PubItemDbRO.State.PENDING);
      }


      latestVersion.setVersionNumber(latestVersion.getVersionNumber() + 1);
      latestVersion.getObject().setLatestVersion(latestVersion);
    }

    updatePubItemWithTechnicalMd(latestVersion, userAccount.getName(), userAccount.getReference()
        .getObjectId());
    latestVersion.setMetadata(pubItemVO.getMetadata());
    latestVersion.getObject().setLocalTags(pubItemVO.getLocalTags());


    latestVersionOld = EntityTransformer.transformToOld(latestVersion);
    validate(latestVersionOld);

    latestVersion = itemRepository.save(latestVersion);
    PubItemVO itemToReturn = EntityTransformer.transformToOld(latestVersion);
    createAuditEntry(latestVersion, EventType.UPDATE);
    reindex(latestVersion);
    logger.info("PubItem " + latestVersion.getObjectIdAndVersion() + " successfully updated in "
        + (System.currentTimeMillis() - start) + " ms");
    return itemToReturn;
  }

  @Override
  @Transactional
  public void delete(String id, String authenticationToken) throws IngeServiceException,
      AaException {

    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);

    PubItemVersionDbVO latestPubItemVersion = itemRepository.findLatestVersion(id);
    if (latestPubItemVersion == null) {
      throw new IngeServiceException("Item " + id + " not found");
    }

    PubItemVO latestPubItem = EntityTransformer.transformToOld(latestPubItemVersion);

    ContextVO context =
        EntityTransformer.transformToOld(contextRepository.findOne(latestPubItem.getContext()
            .getObjectId()));
    checkPubItemAa(latestPubItem, context, userAccount, "delete");

    itemObjectRepository.delete(latestPubItemVersion.getObject());

    SearchRetrieveResponseVO<PubItemVO> resp = getAllVersions(id);
    for (SearchRetrieveRecordVO<PubItemVO> rec : resp.getRecords()) {
      pubItemDao.delete(rec.getPersistenceId());
    }

    logger.info("PubItem " + id + " successfully deleted");

  }

  @Override
  @Transactional(readOnly = true)
  public PubItemVO get(String id, String authenticationToken) throws IngeServiceException,
      AaException {
    long start = System.currentTimeMillis();

    String[] splittedId = id.split("_");
    String objectId = splittedId[0] + "_" + splittedId[1];
    String version = null;
    if (splittedId.length == 3) {
      version = splittedId[2];
    }

    PubItemVO requestedItem = null;

    if (authenticationToken == null && version == null) {
      requestedItem = EntityTransformer.transformToOld(itemRepository.findLatestRelease(objectId));
    } else if (version != null) {
      requestedItem =
          EntityTransformer.transformToOld(itemRepository.findOne(new VersionableId(objectId,
              Integer.parseInt(version))));
    } else {
      requestedItem = EntityTransformer.transformToOld(itemRepository.findLatestVersion(objectId));
    }

    if (requestedItem != null && (authenticationToken != null || version != null)) {
      ContextVO context =
          EntityTransformer.transformToOld(contextRepository.findOne(requestedItem.getContext()
              .getObjectId()));
      try {
        AccountUserVO userAccount = null;
        if (authenticationToken != null) {
          userAccount = aaService.checkLoginRequired(authenticationToken);
        }
        checkPubItemAa(requestedItem, context, userAccount, "get");
      } catch (AaException e) {
        if (version == null) {
          requestedItem =
              EntityTransformer.transformToOld(itemRepository.findLatestRelease(objectId));
        }
      }
    }

    if (requestedItem == null) {
      throw new IngeServiceException("Item " + id + " not found");
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
      authorizedQuery =
          aaService
              .modifyQueryForAa(this.getClass().getCanonicalName(), srr.getQueryObject(), null);
    } else {
      AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
      authorizedQuery =
          aaService.modifyQueryForAa(this.getClass().getCanonicalName(), srr.getQueryObject(),
              userAccount);
    }

    srr.setQueryObject(authorizedQuery);
    logger.debug("Searching with authorized query: \n" + authorizedQuery.toString());
    return pubItemDao.search(srr);
  }

  @Override
  @Transactional
  public PubItemVO submitPubItem(String pubItemId, String message, String authenticationToken)
      throws IngeServiceException, AaException {
    return changeState(pubItemId, State.SUBMITTED, message, "submit", authenticationToken,
        EventType.SUBMIT);
  }

  @Override
  @Transactional
  public PubItemVO revisePubItem(String pubItemId, String message, String authenticationToken)
      throws IngeServiceException, AaException {
    return changeState(pubItemId, State.IN_REVISION, message, "revise", authenticationToken,
        EventType.REVISE);
  }

  @Override
  @Transactional
  public PubItemVO releasePubItem(String pubItemId, String message, String authenticationToken)
      throws IngeServiceException, AaException {
    return changeState(pubItemId, State.RELEASED, message, "release", authenticationToken,
        EventType.RELEASE);
  }

  @Override
  @Transactional
  public PubItemVO withdrawPubItem(String pubItemId, String message, String authenticationToken)
      throws IngeServiceException, AaException {
    return changeState(pubItemId, State.WITHDRAWN, message, "withdraw", authenticationToken,
        EventType.WITHDRAW);
  }

  private PubItemVO changeState(String id, State state, String message, String aaMethod,
      String authenticationToken, EventType auditEventType) throws IngeServiceException,
      AaException {
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);

    PubItemVersionDbVO latestVersion = itemRepository.findLatestVersion(id);

    PubItemVO latestVersionOld = EntityTransformer.transformToOld(latestVersion);

    ContextVO context =
        EntityTransformer.transformToOld(contextRepository.findOne(latestVersion.getObject()
            .getContext().getObjectId()));


    checkPubItemAa(latestVersionOld, context, userAccount, aaMethod);

    if (State.SUBMITTED.equals(state)
        && !de.mpg.mpdl.inge.db.model.valueobjects.PubItemDbRO.State.RELEASED.equals(latestVersion
            .getObject().getPublicStatus())) {
      latestVersion.getObject().setPublicStatus(State.SUBMITTED);
    }
    if (State.RELEASED.equals(state)) {
      latestVersion.getObject().setPublicStatus(State.RELEASED);
      latestVersion.getObject().setLatestRelease(latestVersion);
    }

    if (State.WITHDRAWN.equals(state)) {
      // change public state to withdrawn, leave version state as is
      latestVersion.getObject().setPublicStatus(State.WITHDRAWN);
    } else {
      latestVersion.setState(state);
    }


    updatePubItemWithTechnicalMd(latestVersion, userAccount.getName(), userAccount.getReference()
        .getObjectId());

    latestVersion.setLastMessage(message);
    latestVersion = itemRepository.save(latestVersion);
    PubItemVO itemToReturn = EntityTransformer.transformToOld(latestVersion);


    createAuditEntry(latestVersion, auditEventType);
    reindex(latestVersion);
    return itemToReturn;
  }


  private void reindex(PubItemVersionDbVO item) throws IngeServiceException {
    pubItemDao
        .delete(new VersionableId(item.getObjectId(), item.getVersionNumber() - 1).toString());

    pubItemDao.create(item.getObjectIdAndVersion(), EntityTransformer.transformToOld(item));
    if (item.getObject().getLatestRelease() != null
        && !item.getObjectIdAndVersion().equals(
            item.getObject().getLatestRelease().getObjectIdAndVersion())) {
      pubItemDao.create(item.getObject().getLatestRelease().getObjectIdAndVersion(),
          EntityTransformer
              .transformToOld((PubItemVersionDbVO) item.getObject().getLatestRelease()));
    }
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
      PubItemUtil.cleanUpItem(pubItem);
      ItemValidatingService.validate(pubItem, vp);
    } catch (ValidationException e) {
      throw new IngeServiceException(e);
    }
  }

  private SearchRetrieveResponseVO<PubItemVO> getAllVersions(String objectId)
      throws IngeServiceException {
    QueryBuilder latestReleaseQuery =
        QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID, objectId);
    SearchRetrieveResponseVO<PubItemVO> resp =
        executeSearchSortByVersion(latestReleaseQuery, 10000, 0);

    return resp;
  }

  private SearchRetrieveResponseVO<PubItemVO> executeSearchSortByVersion(QueryBuilder query,
      int limit, int offset) throws IngeServiceException {

    SearchSortCriteria sortByVersion =
        new SearchSortCriteria(PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID, SortOrder.DESC);
    SearchRetrieveRequestVO<QueryBuilder> srr =
        new SearchRetrieveRequestVO<QueryBuilder>(query, limit, offset, sortByVersion);
    return pubItemDao.search(srr);
  }



  public void reindex() {

    Query<de.mpg.mpdl.inge.db.model.valueobjects.PubItemObjectDbVO> query =
        (Query<de.mpg.mpdl.inge.db.model.valueobjects.PubItemObjectDbVO>) entityManager
            .createQuery("SELECT itemObject FROM PubItemObjectVO itemObject");
    query.setReadOnly(true);
    query.setFetchSize(5000);
    query.setCacheable(false);
    ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);

    while (results.next()) {
      try {
        de.mpg.mpdl.inge.db.model.valueobjects.PubItemObjectDbVO object =
            (de.mpg.mpdl.inge.db.model.valueobjects.PubItemObjectDbVO) results.get(0);
        PubItemVO latestVersion =
            EntityTransformer.transformToOld((PubItemVersionDbVO) object.getLatestVersion());
        logger.info("Reindexing item latest version "
            + latestVersion.getVersion().getObjectIdAndVersion());
        pubItemDao.createNotImmediately(latestVersion.getVersion().getObjectId() + "_"
            + latestVersion.getVersion().getVersionNumber(), latestVersion);
        if (object.getLatestRelease() != null
            && object.getLatestRelease().getVersionNumber() != object.getLatestVersion()
                .getVersionNumber()) {
          PubItemVO latestRelease =
              EntityTransformer.transformToOld((PubItemVersionDbVO) object.getLatestRelease());
          logger.info("Reindexing item latest release "
              + latestRelease.getVersion().getObjectIdAndVersion());
          pubItemDao.createNotImmediately(latestRelease.getVersion().getObjectId() + "_"
              + latestRelease.getVersion().getVersionNumber(), latestRelease);
        }

      } catch (Exception e) {
        logger.error("Error while reindexing ", e);
      }


    }

  }

  @Override
  @Transactional(readOnly = true)
  public List<VersionHistoryEntryVO> getVersionHistory(String pubItemId, String authenticationToken)
      throws IngeServiceException, AaException {

    List<AuditDbVO> list =
        auditRepository.findDistinctAuditByPubItemObjectIdOrderByModificationDateDesc(pubItemId);

    return EntityTransformer.transformToVersionHistory(list);
  }


  private void checkPubItemAa(PubItemVO item, ContextVO context, AccountUserVO userAccount,
      String method) throws AaException, IngeServiceException {
    aaService.checkAuthorization(this.getClass().getCanonicalName(), method, item, context,
        userAccount);
  }

}
