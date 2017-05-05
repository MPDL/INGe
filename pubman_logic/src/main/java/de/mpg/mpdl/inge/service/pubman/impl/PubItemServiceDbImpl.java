package de.mpg.mpdl.inge.service.pubman.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.swing.Scrollable;
import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.jpa.HibernateEntityManager;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.dao.ContextDao;
import de.mpg.mpdl.inge.dao.PubItemDao;
import de.mpg.mpdl.inge.db.model.valueobjects.PubItemObjectDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.PubItemDbRO;
import de.mpg.mpdl.inge.db.model.valueobjects.PubItemVersionDbVO;
import de.mpg.mpdl.inge.db.model.valueobjects.VersionableId;
import de.mpg.mpdl.inge.db.model.valueobjects.PubItemDbRO.State;
import de.mpg.mpdl.inge.db.repository.ContextRepository;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl.ID_PREFIX;
import de.mpg.mpdl.inge.db.repository.ItemObjectRepository;
import de.mpg.mpdl.inge.db.repository.ItemRepository;
import de.mpg.mpdl.inge.inge_validation.ItemValidatingService;
import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.model.referenceobjects.AccountUserRO;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria.SortOrder;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.util.EntityTransformer;
import de.mpg.mpdl.inge.service.util.PubItemUtil;
import de.mpg.mpdl.inge.services.IngeServiceException;

@Service
@Primary
public class PubItemServiceDbImpl implements PubItemService {

  private final static Logger logger = Logger.getLogger(PubItemServiceDbImpl.class);

  public static String INDEX_VERSION_OBJECT_ID = "version.objectId.keyword";
  public static String INDEX_VERSION_STATE = "version.state.keyword";
  public static String INDEX_PUBLIC_STATE = "publicStatus.keyword";
  public static String INDEX_OWNER_OBJECT_ID = "owner.objectId.keyword";
  public static String INDEX_CONTEXT_OBEJCT_ID = "context.objectId.keyword";
  public static String INDEX_LOCAL_TAGS = "localTags";

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
  private PubItemDao<QueryBuilder> pubItemDao;

  @PersistenceContext
  EntityManager entityManager;

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

    aaService.checkPubItemAa(pubItemToCreateOld, contextOld, userAccount, "create");

    logger.info("Before cleanup");
    PubItemUtil.cleanUpItem(pubItemToCreateOld);
    validate(pubItemToCreateOld, ValidationPoint.SAVE);


    String id = idProviderService.getNewId(ID_PREFIX.ITEM);
    String fullId = id + "_1";
    pubItemToCreate.setObjectId(id);
    pubItemToCreate.getObject().setObjectId(id);


    pubItemToCreate = itemRepository.save(pubItemToCreate);
    PubItemVO itemToReturn = EntityTransformer.transformToOld(pubItemToCreate);
    reindex(pubItemToCreate);

    long time = System.currentTimeMillis() - start;
    logger.info("PubItem " + fullId + " successfully created in " + time + " ms");

    return itemToReturn;
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

  /*
   * 
   * private PubItemVO buildPubItemToCreate(PubItemVO templateVO, PubItemVO latestVersion,
   * AccountUserVO userAccount, ContextVO context, int versionNumber, State versionState, State
   * publicState) { PubItemVO pubItemToCreate = new PubItemVO(templateVO);
   * 
   * Date currentDate = new Date(); pubItemToCreate.setContentModel(null);
   * pubItemToCreate.setContentModelHref(null); pubItemToCreate.setLatestRelease(null);
   * pubItemToCreate.setLatestVersion(null); pubItemToCreate.setLockStatus(null);
   * 
   * pubItemToCreate.setContext(context.getReference());
   * pubItemToCreate.setPublicStatus(publicState);
   * 
   * ItemRO itemRO = new ItemRO(); pubItemToCreate.setVersion(itemRO);
   * itemRO.setVersionNumber(versionNumber); itemRO.setModifiedByRO(userAccount.getReference());
   * itemRO.setModificationDate(currentDate); itemRO.setState(versionState);
   * 
   * 
   * 
   * if (latestVersion == null) { pubItemToCreate.setOwner(userAccount.getReference());
   * pubItemToCreate.setCreationDate(currentDate);
   * 
   * } else { itemRO.setObjectId(latestVersion.getVersion().getObjectId());
   * pubItemToCreate.setOwner(latestVersion.getOwner());
   * pubItemToCreate.setCreationDate(latestVersion.getCreationDate());
   * itemRO.setPid(latestVersion.getPid());
   * itemRO.setLastMessage(latestVersion.getVersion().getObjectId()); } return pubItemToCreate; }
   */

  @Override
  @Transactional
  public PubItemVO update(PubItemVO pubItemVO, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException {

    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);


    PubItemVersionDbVO latestVersion =
        itemRepository.findLatestVersion(pubItemVO.getVersion().getObjectId());
    PubItemVO latestVersionOld = EntityTransformer.transformToOld(latestVersion);

    ContextVO context =
        EntityTransformer.transformToOld(contextRepository.findOne(pubItemVO.getContext()
            .getObjectId()));
    aaService.checkPubItemAa(latestVersionOld, context, userAccount, "update");

    if (State.RELEASED.equals(latestVersion.getState())) {
      entityManager.detach(latestVersion);
      // Reset latestRelase reference because it is the same object as latest version
      PubItemDbRO latestReleaseRO = new PubItemDbRO();
      latestReleaseRO.setObjectId(latestVersion.getObject().getLatestRelease().getObjectId());
      latestReleaseRO.setVersionNumber(latestVersion.getObject().getLatestRelease()
          .getVersionNumber());
      latestVersion.getObject().setLatestRelease(latestReleaseRO);

      latestVersion.setState(de.mpg.mpdl.inge.db.model.valueobjects.PubItemDbRO.State.PENDING);
      latestVersion.setVersionNumber(latestVersion.getVersionNumber() + 1);
      latestVersion.getObject().setLatestVersion(latestVersion);
    }

    updatePubItemWithTechnicalMd(latestVersion, userAccount.getName(), userAccount.getReference()
        .getObjectId());
    latestVersion.setMetadata(pubItemVO.getMetadata());
    latestVersion.getObject().setLocalTags(pubItemVO.getLocalTags());



    validate(latestVersionOld);
    PubItemUtil.cleanUpItem(latestVersionOld);


    String newFullId = latestVersion.getObjectIdAndVersion();

    latestVersion = itemRepository.save(latestVersion);
    PubItemVO itemToReturn = EntityTransformer.transformToOld(latestVersion);
    reindex(latestVersion);

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
    aaService.checkPubItemAa(latestPubItem, context, userAccount, "delete");

    itemObjectRepository.delete(latestPubItemVersion.getObject());

    SearchRetrieveResponseVO<PubItemVO> resp = getAllVersions(id);
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

    PubItemVO requestedItem = null;
    if (authenticationToken == null) {
      // Return latest Release
      requestedItem = EntityTransformer.transformToOld(itemRepository.findLatestRelease(objectId));
    } else {
      AccountUserVO userAccount = null;
      if (authenticationToken != null) {
        userAccount = aaService.checkLoginRequired(authenticationToken);
      }


      if (version != null) {
        requestedItem =
            EntityTransformer.transformToOld(itemRepository.findOne(new VersionableId(objectId,
                Integer.parseInt(version))));
        ContextVO context =
            EntityTransformer.transformToOld(contextRepository.findOne(requestedItem.getContext()
                .getObjectId()));
        aaService.checkPubItemAa(requestedItem, context, userAccount, "get");
        return requestedItem;
      } else {
        requestedItem =
            EntityTransformer.transformToOld(itemRepository.findLatestVersion(objectId));
        ContextVO context =
            EntityTransformer.transformToOld(contextRepository.findOne(requestedItem.getContext()
                .getObjectId()));
        try {
          aaService.checkPubItemAa(requestedItem, context, userAccount, "get");
        } catch (AaException e) {
          requestedItem =
              EntityTransformer.transformToOld(itemRepository.findLatestRelease(objectId));
        }
      }


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
  @Transactional
  public PubItemVO submitPubItem(String pubItemId, String message, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException {
    return changeState(pubItemId, State.SUBMITTED, message, "submit", authenticationToken);
  }

  @Override
  @Transactional
  public PubItemVO revisePubItem(String pubItemId, String message, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException {
    return changeState(pubItemId, State.IN_REVISION, message, "revise", authenticationToken);
  }

  @Override
  @Transactional
  public PubItemVO releasePubItem(String pubItemId, String message, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException {
    return changeState(pubItemId, State.RELEASED, message, "release", authenticationToken);
  }

  @Override
  @Transactional
  public PubItemVO withdrawPubItem(String pubItemId, String message, String authenticationToken)
      throws IngeServiceException, AaException, ItemInvalidException {
    return changeState(pubItemId, State.WITHDRAWN, message, "withdraw", authenticationToken);
  }


  private PubItemVO changeState(String id, State state, String message, String aaMethod,
      String authenticationToken) throws IngeServiceException, AaException, ItemInvalidException {
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);

    PubItemVersionDbVO latestVersion = itemRepository.findLatestVersion(id);

    PubItemVO latestVersionOld = EntityTransformer.transformToOld(latestVersion);

    ContextVO context =
        EntityTransformer.transformToOld(contextRepository.findOne(latestVersion.getObject()
            .getContext().getObjectId()));


    aaService.checkPubItemAa(latestVersionOld, context, userAccount, aaMethod);


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
      ItemValidatingService.validate(pubItem, vp);
    } catch (ValidationException e) {
      throw new IngeServiceException(e);
    }
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
        pubItemDao.create(latestVersion.getVersion().getObjectId() + "_"
            + latestVersion.getVersion().getVersionNumber(), latestVersion);
        if (object.getLatestRelease() != null) {
          PubItemVO latestRelease =
              EntityTransformer.transformToOld((PubItemVersionDbVO) object.getLatestRelease());
          logger.info("Reindexing item latest release "
              + latestRelease.getVersion().getObjectIdAndVersion());
          pubItemDao.create(latestRelease.getVersion().getObjectId() + "_"
              + latestRelease.getVersion().getVersionNumber(), latestRelease);
        }

      } catch (Exception e) {
        logger.error("Error while reindexing ", e);
      }


    }

  }

}
