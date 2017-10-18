package de.mpg.mpdl.inge.service.pubman.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;

import org.apache.http.client.fluent.Request;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.hibernate.CacheMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.inge.db.repository.AuditRepository;
import de.mpg.mpdl.inge.db.repository.ContextRepository;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl.ID_PREFIX;
import de.mpg.mpdl.inge.db.repository.ItemObjectRepository;
import de.mpg.mpdl.inge.db.repository.ItemRepository;
import de.mpg.mpdl.inge.es.dao.GenericDaoEs;
import de.mpg.mpdl.inge.es.dao.PubItemDaoEs;
import de.mpg.mpdl.inge.inge_validation.ItemValidatingService;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.AuditDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.AuditDbVO.EventType;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.PubItemDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.PubItemObjectDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.PubItemVersionDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.VersionableId;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.referenceobjects.AccountUserRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Storage;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria.SortOrder;
import de.mpg.mpdl.inge.model.valueobjects.VersionHistoryEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.FileService;
import de.mpg.mpdl.inge.service.pubman.PidService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.util.EntityTransformer;
import de.mpg.mpdl.inge.service.util.OaiFileTools;
import de.mpg.mpdl.inge.service.util.PubItemUtil;
import de.mpg.mpdl.inge.util.PropertyReader;

@Service
@Primary
public class PubItemServiceDbImpl extends GenericServiceBaseImpl<PubItemVO> implements
    PubItemService {

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
  private PubItemDaoEs pubItemDao;

  @Autowired
  private FileService fileService;

  @Autowired
  private PidService pidService;

  @PersistenceContext
  EntityManager entityManager;

  public static String INDEX_MODIFICATION_DATE = "version.modificationDate";
  public static String INDEX_CREATION_DATE = "creationDate";
  public static String INDEX_LOCAL_TAGS = "localTags";
  public static String INDEX_CONTEXT_OBJECT_ID = "context.objectId";
  public static String INDEX_OWNER_OBJECT_ID = "owner.objectId";
  public static String INDEX_PUBLIC_STATE = "publicStatus";
  public static String INDEX_PID = "pid";

  public static String INDEX_VERSION_STATE = "version.state";
  public static String INDEX_LATESTVERSION_VERSIONNUMBER = "latestVersion.versionNumber";
  public static String INDEX_VERSION_VERSIONNUMBER = "version.versionNumber";
  public static String INDEX_VERSION_OBJECT_ID = "version.objectId";
  public static String INDEX_VERSION_PID = "version.pid";

  public static String INDEX_METADATA_CREATOR_PERSON_IDENTIFIER_ID =
      "metadata.creators.person.identifier.id";
  public static String INDEX_METADATA_CREATOR_PERSON_FAMILYNAME =
      "metadata.creators.person.familyName";
  public static String INDEX_METADATA_CREATOR_PERSON_GIVENNAME =
      "metadata.creators.person.givenName";

  public static String INDEX_METADATA_CREATOR_PERSON_ORGANIZATION_IDENTIFIER =
      "metadata.creators.person.organizations.identifier";
  public static String INDEX_METADATA_CREATOR_ORGANIZATION_IDENTIFIER =
      "metadata.creators.organizations.identifier";
  public static String INDEX_METADATA_CREATOR_PERSON_ORGANIZATION_NAME =
      "metadata.creators.person.organizations.name";
  public static String INDEX_METADATA_CREATOR_ORGANIZATION_NAME =
      "metadata.creators.organizations.name";
  public static String INDEX_METADATA_CREATOR_ROLE = "metadata.creators.role";

  public static String INDEX_METADATA_TITLE = "metadata.title";
  public static String INDEX_METADATA_DATE_PUBLISHED_IN_PRINT = "metadata.datePublishedInPrint";
  public static String INDEX_METADATA_DATE_PUBLISHED_ONLINE = "metadata.datePublishedOnline";
  public static String INDEX_METADATA_DATE_ACCEPTED = "metadata.dateAccepted";
  public static String INDEX_METADATA_DATE_CREATED = "metadata.dateCreated";
  public static String INDEX_METADATA_DATE_MODIFIED = "metadata.dateModified";
  public static String INDEX_METADATA_DATE_SUBMITTED = "metadata.dateSubmitted";
  public static String INDEX_METADATA_EVENT_TITLE = "metadata.event.title";
  public static String INDEX_METADATA_EVENT_STARTDATE = "metadata.event.startDate";
  public static String INDEX_METADATA_EVENT_ENDDATE = "metadata.event.endDate";
  public static String INDEX_METADATA_EVENT_INVITATION_STATUS = "metadata.event.invitationStatus";
  public static String INDEX_METADATA_GENRE = "metadata.genre";
  public static String INDEX_METADATA_REVIEW_METHOD = "metadata.reviewMethod";
  public static String INDEX_METADATA_SUBJECTS = "metadata.subjects.value";
  public static String INDEX_METADATA_DEGREE = "metadata.degree";
  public static String INDEX_METADATA_LANGUAGES = "metadata.languages";
  public static String INDEX_METADATA_IDENTIFIERS_ID = "metadata.identifiers.id";

  public static String INDEX_METADATA_PROJECTINFO_TITLE = "metadata.projectInfo.title";
  public static String INDEX_METADATA_PROJECTINFO_FUNDING_ORGANIZATION_TITLE =
      "metadata.projectInfo.fundingOrganization.title";
  public static String INDEX_METADATA_PROJECTINFO_FUNDING_ORGANIZATION_IDENTIFIERS_ID =
      "metadata.projectInfo.fundingOrganization.identifiers.id";
  public static String INDEX_METADATA_PROJECTINFO_FUNDING_PROGRAM_TITLE =
      "metadata.projectInfo.fundingProgram.title";
  public static String INDEX_METADATA_PROJECTINFO_FUNDING_PROGRAM_IDENTIFIERS_ID =
      "metadata.projectInfo.fundingProgram.identifiers.id";
  public static String INDEX_METADATA_PROJECTINFO_GRANT_IDENTIFIER_ID =
      "metadata.projectInfo.grantIdentifier.id";

  public static String INDEX_METADATA_SOURCES_TITLE = "metadata.sources.title";
  public static String INDEX_METADATA_SOURCES_ALTERNATIVE_TITLE =
      "metadata.sources.alternativeTitles.value";
  public static String INDEX_METADATA_SOURCES_CREATOR_PERSON_ORGANIZATION_IDENTIFIER =
      "metadata.sources.creators.person.organization.identifier";

  public static String INDEX_FILE_METADATA_EMBARGO_UNTIL = "file.metadata.embargoUntil";

  public static final String REST_SERVICE_URL = PropertyReader.getProperty("inge.rest.service_url");
  public static final String REST_COMPONENT_PATH = PropertyReader
      .getProperty("inge.rest.file_path");

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public PubItemVO create(PubItemVO pubItemVO, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException {
    long start = System.currentTimeMillis();
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);

    de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO contextNew =
        contextRepository.findOne(pubItemVO.getContext().getObjectId());
    ContextVO contextOld = EntityTransformer.transformToOld(contextNew);

    uploadStagedFiles(pubItemVO);

    PubItemUtil.cleanUpItem(pubItemVO);

    PubItemVersionDbVO pubItemToCreate =
        buildPubItemToCreate("dummyId", contextNew, pubItemVO.getMetadata(), pubItemVO.getFiles(),
            pubItemVO.getLocalTags(), userAccount.getReference().getTitle(), userAccount
                .getReference().getObjectId());

    PubItemVO pubItemToCreateOld = EntityTransformer.transformToOld(pubItemToCreate);

    checkAa("create", userAccount, pubItemToCreateOld, contextOld);

    validate(pubItemToCreateOld, ValidationPoint.SAVE);

    String id = idProviderService.getNewId(ID_PREFIX.ITEM);
    String fullId = id + "_1";
    pubItemToCreate.setObjectId(id);
    pubItemToCreate.getObject().setObjectId(id);

    try {
      pubItemToCreate = itemRepository.saveAndFlush(pubItemToCreate);
    } catch (DataAccessException e) {
      rollbackSavedFiles(pubItemVO);
      GenericServiceImpl.handleDBException(e);
    }
    PubItemVO itemToReturn = EntityTransformer.transformToOld(pubItemToCreate);

    createAuditEntry(pubItemToCreate, EventType.CREATE);
    reindex(pubItemToCreate);
    long time = System.currentTimeMillis() - start;
    logger.info("PubItem " + fullId + " successfully created in " + time + " ms");

    return itemToReturn;
  }

  private void createAuditEntry(PubItemVersionDbVO pubItem, EventType event)
      throws IngeApplicationException, IngeTechnicalException {
    AuditDbVO audit = new AuditDbVO();
    audit.setEvent(event);
    audit.setComment(pubItem.getLastMessage());
    audit.setModificationDate(pubItem.getModificationDate());
    audit.setModifier(pubItem.getModifiedBy());
    audit.setPubItem(pubItem);
    try {
      auditRepository.saveAndFlush(audit);
    } catch (DataAccessException e) {
      GenericServiceImpl.handleDBException(e);
    }
  }

  private PubItemVersionDbVO buildPubItemToCreate(String objectId,
      de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO context, MdsPublicationVO md,
      List<FileVO> files, List<String> localTags, String modifierName, String modifierId) {
    Date currentDate = new Date();

    PubItemVersionDbVO pubItem = new PubItemVersionDbVO();

    pubItem.setMetadata(md);
    pubItem.setLastMessage(null);
    pubItem.setModificationDate(currentDate);
    AccountUserDbRO mod = new AccountUserDbRO();
    mod.setName(modifierName);
    mod.setObjectId(modifierId);
    pubItem.setModifiedBy(mod);
    pubItem.setObjectId(objectId);
    pubItem.setState(PubItemDbRO.State.PENDING);
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
    pubItemObject.setPublicStatus(PubItemDbRO.State.PENDING);
    pubItemObject.setPublicStatusComment(null);

    pubItem.getFiles().clear();
    for (FileVO fileVO : files) {
      pubItem.getFiles().add(generateFileDbVOFromFileVO(fileVO, mod, currentDate));
    }

    pubItem.setObject(pubItemObject);
    return pubItem;
  }

  private FileDbVO generateFileDbVOFromFileVO(FileVO fileVO, AccountUserDbRO accountUser,
      Date currentDate) {
    FileDbVO fileDbVO = new FileDbVO();
    fileDbVO.setChecksum(fileVO.getChecksum());
    // fileDbVO.setChecksumAlgorithm(FileDbVO.ChecksumAlgorithm.valueOf(fileVO.getChecksumAlgorithm()
    // .name()));
    fileDbVO.setContent(fileVO.getContent());
    fileDbVO.setContentCategory(fileVO.getContentCategory());
    fileDbVO.setCreationDate(fileVO.getCreationDate());
    fileDbVO.setCreator(accountUser);
    fileDbVO.setDescription(fileVO.getDescription());
    fileDbVO.setLastModificationDate(fileVO.getLastModificationDate());
    fileDbVO.setMetadata(fileVO.getDefaultMetadata());
    fileDbVO.setMimeType(fileVO.getMimeType());
    fileDbVO.setName(fileVO.getName());
    fileDbVO.setObjectId(idProviderService.getNewId(ID_PREFIX.FILES)); // TODO check if no
    // transformation is
    // needed
    fileDbVO.setPid(fileVO.getPid());
    fileDbVO.setStorage(FileDbVO.Storage.valueOf(fileVO.getStorage().name()));
    fileDbVO.setVisibility(FileDbVO.Visibility.valueOf(fileVO.getVisibility().name()));

    return fileDbVO;
  }

  private PubItemDbRO updatePubItemWithTechnicalMd(PubItemVersionDbVO latestVersion,
      String modifierName, String modifierId) {
    Date currentDate = new Date();

    latestVersion.setModificationDate(currentDate);
    de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbRO mod =
        new de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbRO();
    mod.setName(modifierName);
    mod.setObjectId(modifierId);
    latestVersion.setModifiedBy(mod);
    latestVersion.getObject().setLastModificationDate(currentDate);

    return latestVersion;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public PubItemVO update(PubItemVO pubItemVO, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException {
    long start = System.currentTimeMillis();
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    PubItemUtil.cleanUpItem(pubItemVO);
    PubItemVersionDbVO latestVersion =
        itemRepository.findLatestVersion(pubItemVO.getVersion().getObjectId());
    if (latestVersion == null) {
      throw new IngeApplicationException("Object with given id not found.");
    }
    PubItemVO latestVersionOld = EntityTransformer.transformToOld(latestVersion);
    checkEqualModificationDate(pubItemVO.getVersion().getModificationDate(), latestVersionOld
        .getVersion().getModificationDate());

    ContextVO context =
        EntityTransformer.transformToOld(contextRepository.findOne(pubItemVO.getContext()
            .getObjectId()));

    checkAa("update", userAccount, latestVersionOld, context);

    if (PubItemDbRO.State.RELEASED.equals(latestVersion.getState())) {
      entityManager.detach(latestVersion);
      // Reset latestRelase reference because it is the same object as latest version
      PubItemDbRO latestReleaseDbRO = new PubItemDbRO();
      latestReleaseDbRO.setObjectId(latestVersion.getObject().getLatestRelease().getObjectId());
      latestReleaseDbRO.setVersionNumber(latestVersion.getObject().getLatestRelease()
          .getVersionNumber());
      latestVersion.getObject().setLatestRelease(latestReleaseDbRO);

      // if current user is owner, set to status pending. Else, set to status
      // submitted

      if (userAccount.isModerator(context.getReference())) {
        latestVersion.setState(PubItemDbRO.State.SUBMITTED);
      } else {
        latestVersion.setState(PubItemDbRO.State.PENDING);
      }

      latestVersion.setVersionNumber(latestVersion.getVersionNumber() + 1);
      latestVersion.getObject().setLatestVersion(latestVersion);
    }

    updatePubItemWithTechnicalMd(latestVersion, userAccount.getName(), userAccount.getReference()
        .getObjectId());
    latestVersion.setMetadata(pubItemVO.getMetadata());
    ArrayList<FileDbVO> fileDbVOList = new ArrayList<FileDbVO>();
    for (FileVO fileVo : pubItemVO.getFiles()) {
      if ((fileVo.getReference() == null || fileVo.getReference().getObjectId() == null)
          && (fileVo.getContent() == null || fileVo.getContent().length() == 0)) {
        break;
      }

      FileDbVO newFileDbVo = new FileDbVO();
      newFileDbVo.setChecksum(fileVo.getChecksum());
      // TODO
      // oldFileVo.setChecksumAlgorithm(FileVO.ChecksumAlgorithm.valueOf(newFileVo
      // .getChecksumAlgorithm().name()));
      newFileDbVo.setContent(fileVo.getContent());
      newFileDbVo.setContentCategory(fileVo.getContentCategory());

      AccountUserRO accountUserRo = fileVo.getCreatedByRO();
      AccountUserDbRO modifier = new AccountUserDbRO();
      if (accountUserRo != null) {
        modifier.setObjectId(accountUserRo.getObjectId());
        modifier.setName(accountUserRo.getTitle());
        newFileDbVo.setCreator(modifier);
      } else {
        modifier.setObjectId(userAccount.getUserid());
        modifier.setName(userAccount.getName());
      }

      if (fileVo.getCreationDate() != null) {
        newFileDbVo.setCreationDate(fileVo.getCreationDate());
      } else {
        Date currentDate = new Date();
        newFileDbVo.setCreationDate(currentDate);
      }

      newFileDbVo.setMetadata(fileVo.getDefaultMetadata());
      newFileDbVo.setDescription(fileVo.getDescription());
      newFileDbVo.setLastModificationDate(fileVo.getLastModificationDate());
      newFileDbVo.setMimeType(fileVo.getMimeType());
      newFileDbVo.setName(fileVo.getName());
      newFileDbVo.setPid(fileVo.getPid());

      if (fileVo.getReference() != null) {
        newFileDbVo.setObjectId(fileVo.getReference().getObjectId());
        newFileDbVo.setName(fileVo.getName());
      } else {
        newFileDbVo.setObjectId(idProviderService.getNewId(ID_PREFIX.FILES));
      }

      newFileDbVo.setStorage(FileDbVO.Storage.valueOf(fileVo.getStorage().name()));
      newFileDbVo.setVisibility(FileDbVO.Visibility.valueOf(fileVo.getVisibility().name()));

      fileDbVOList.add(newFileDbVo);
    }
    latestVersion.setFiles(fileDbVOList);
    latestVersion.getObject().setLocalTags(pubItemVO.getLocalTags());

    latestVersionOld = EntityTransformer.transformToOld(latestVersion);
    validate(latestVersionOld);

    try {
      latestVersion = itemRepository.saveAndFlush(latestVersion);
    } catch (DataAccessException e) {
      GenericServiceImpl.handleDBException(e);
    }
    PubItemVO itemToReturn = EntityTransformer.transformToOld(latestVersion);
    createAuditEntry(latestVersion, EventType.UPDATE);
    reindex(latestVersion);
    logger.info("PubItem " + latestVersion.getObjectIdAndVersion() + " successfully updated in "
        + (System.currentTimeMillis() - start) + " ms");
    return itemToReturn;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void delete(String id, String authenticationToken) throws IngeTechnicalException,
      AuthenticationException, AuthorizationException, IngeApplicationException {

    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);

    PubItemVersionDbVO latestPubItemDbVersion = itemRepository.findLatestVersion(id);
    if (latestPubItemDbVersion == null) {
      throw new IngeApplicationException("Item " + id + " not found");
    }

    PubItemVO latestPubItem = EntityTransformer.transformToOld(latestPubItemDbVersion);

    ContextVO context =
        EntityTransformer.transformToOld(contextRepository.findOne(latestPubItem.getContext()
            .getObjectId()));
    checkAa("delete", userAccount, latestPubItem, context);

    itemObjectRepository.delete(latestPubItemDbVersion.getObject());

    SearchRetrieveResponseVO<PubItemVO> resp = getAllVersions(id);
    for (SearchRetrieveRecordVO<PubItemVO> rec : resp.getRecords()) {
      pubItemDao.delete(rec.getPersistenceId());
    }

    logger.info("PubItem " + id + " successfully deleted");

  }

  @Override
  @Transactional(readOnly = true)
  public PubItemVO get(String id, String authenticationToken) throws IngeTechnicalException,
      AuthenticationException, AuthorizationException, IngeApplicationException {
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
        checkAa("get", userAccount, requestedItem, context);
      } catch (AuthenticationException e) {
        if (version == null) {
          requestedItem =
              EntityTransformer.transformToOld(itemRepository.findLatestRelease(objectId));
        }
      }
    }

    if (requestedItem == null) {
      throw new IngeApplicationException("Item " + id + " not found");
    }

    long time = System.currentTimeMillis() - start;
    logger.info("PubItem " + id + " successfully retrieved in " + time + " ms");

    return requestedItem;
  }



  @Override
  @Transactional(rollbackFor = Throwable.class)
  public PubItemVO submitPubItem(String pubItemId, Date modificationDate, String message,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException {
    return changeState(pubItemId, modificationDate, PubItemDbRO.State.SUBMITTED, message, "submit",
        authenticationToken, EventType.SUBMIT);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public PubItemVO revisePubItem(String pubItemId, Date modificationDate, String message,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException {
    return changeState(pubItemId, modificationDate, PubItemDbRO.State.IN_REVISION, message,
        "revise", authenticationToken, EventType.REVISE);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public PubItemVO releasePubItem(String pubItemId, Date modificationDate, String message,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException {
    return changeState(pubItemId, modificationDate, PubItemDbRO.State.RELEASED, message, "release",
        authenticationToken, EventType.RELEASE);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public PubItemVO withdrawPubItem(String pubItemId, Date modificationDate, String message,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException {
    return changeState(pubItemId, modificationDate, PubItemDbRO.State.WITHDRAWN, message,
        "withdraw", authenticationToken, EventType.WITHDRAW);
  }

  private PubItemVO changeState(String id, Date modificationDate, PubItemDbRO.State state,
      String message, String aaMethod, String authenticationToken, EventType auditEventType)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException {
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);

    PubItemVersionDbVO latestVersion = itemRepository.findLatestVersion(id);

    if (latestVersion == null) {
      throw new IngeApplicationException("Object with given id not found.");
    }

    PubItemVO latestVersionOld = EntityTransformer.transformToOld(latestVersion);

    checkEqualModificationDate(modificationDate, latestVersionOld.getModificationDate());

    ContextVO context =
        EntityTransformer.transformToOld(contextRepository.findOne(latestVersion.getObject()
            .getContext().getObjectId()));

    checkAa(aaMethod, userAccount, latestVersionOld, context);

    if (PubItemDbRO.State.SUBMITTED.equals(state)
        && !PubItemDbRO.State.RELEASED.equals(latestVersion.getObject().getPublicStatus())) {
      latestVersion.getObject().setPublicStatus(PubItemDbRO.State.SUBMITTED);
    }

    if (PubItemDbRO.State.RELEASED.equals(state)) {
      latestVersion.getObject().setPublicStatus(PubItemDbRO.State.RELEASED);
      latestVersion.getObject().setLatestRelease(latestVersion);
      PubItemObjectDbVO pubItemObject = latestVersion.getObject();
      try {
        if (pubItemObject.getPid() == null) {
          URI url =
              new URI(PropertyReader.getProperty("escidoc.pubman.instance.url")
                  + PropertyReader.getProperty("escidoc.pubman.instance.context.path")
                  + PropertyReader.getProperty("escidoc.pubman.item.pattern").replaceAll("\\$1",
                      latestVersion.getObjectId()));
          pubItemObject.setPid(pidService.createPid(url).getIdentifier());
        }
        if (latestVersion.getVersionPid() == null) {
          URI url =
              new URI(PropertyReader.getProperty("escidoc.pubman.instance.url")
                  + PropertyReader.getProperty("escidoc.pubman.instance.context.path")
                  + PropertyReader.getProperty("escidoc.pubman.item.pattern").replaceAll("\\$1",
                      latestVersion.getObjectIdAndVersion()));
          latestVersion.setVersionPid(pidService.createPid(url).getIdentifier());
        }
      } catch (URISyntaxException | TechnicalException e) {
        logger.error("Error creating PID for item [" + latestVersion.getObjectIdAndVersion() + "]",
            e);
        throw new IngeTechnicalException("Error creating PID for item ["
            + latestVersion.getObjectIdAndVersion() + "]", e);
      }


      for (FileDbVO fileDbVO : latestVersion.getFiles()) {
        try {
          if ((FileDbVO.Storage.INTERNAL_MANAGED).equals(fileDbVO.getStorage())
              && fileDbVO.getPid() == null) {
            URI uri = new URI(REST_SERVICE_URL + REST_COMPONENT_PATH + "/" + fileDbVO.getName());
            fileDbVO.setPid(pidService.createPid(uri).getIdentifier());
          }
        } catch (URISyntaxException | TechnicalException e) {
          logger.error("Error creating PID for file [" + fileDbVO.getName()
              + "] part of the item [" + latestVersion.getObjectIdAndVersion() + "]", e);
          throw new IngeTechnicalException("Error creating PID for item ["
              + latestVersion.getObjectIdAndVersion() + "]", e);
        }
      }

    }

    if (PubItemDbRO.State.WITHDRAWN.equals(state)) {
      // change public state to withdrawn, leave version state as is
      latestVersion.getObject().setPublicStatus(PubItemDbRO.State.WITHDRAWN);
      latestVersion.getObject().setPublicStatusComment(message);
    } else {
      latestVersion.setState(state);
    }

    updatePubItemWithTechnicalMd(latestVersion, userAccount.getName(), userAccount.getReference()
        .getObjectId());

    latestVersion.setLastMessage(message);

    validate(EntityTransformer.transformToOld(latestVersion));

    try {
      latestVersion = itemRepository.saveAndFlush(latestVersion);
    } catch (DataAccessException e) {
      GenericServiceImpl.handleDBException(e);
    }

    PubItemVO itemToReturn = EntityTransformer.transformToOld(latestVersion);

    createAuditEntry(latestVersion, auditEventType);



    // TODO: - fuer OPEN_AIRE ins Repository Verzeichnis schreiben
    // - beliebigen Pfad vorgeben ermöglichen
    // - vorhandene Datei ueberschreiben
    // - Namespaces in XML Datei anpassen
    // - Datei löschen ermöglichen
    if (PubItemDbRO.State.RELEASED.equals(state)) {
      try {
        String s = XmlTransformingService.transformToItem(itemToReturn);
        OaiFileTools.createFile(new ByteArrayInputStream(s.getBytes()), itemToReturn.getVersion()
            .getObjectIdAndVersion() + ".xml");
      } catch (TechnicalException e) {
        throw new IngeTechnicalException(e);
      }
    } else if (PubItemDbRO.State.WITHDRAWN.equals(state)) {
      try {
        OaiFileTools.deleteFile(itemToReturn.getVersion().getObjectIdAndVersion() + ".xml");
      } catch (Exception e) {
        throw new IngeTechnicalException(e);
      }
    }

    reindex(latestVersion);
    return itemToReturn;
  }

  private void reindex(PubItemVersionDbVO item) throws IngeTechnicalException {
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

  private void validate(PubItemVO pubItem) throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException {
    ValidationPoint vp = ValidationPoint.STANDARD;

    if (pubItem.getPublicStatus() != null && ItemVO.State.PENDING.equals(pubItem.getPublicStatus())) {
      vp = ValidationPoint.SAVE;
    }

    validate(pubItem, vp);
  }

  private void validate(PubItemVO pubItem, ValidationPoint vp) throws IngeTechnicalException,
      AuthenticationException, AuthorizationException, IngeApplicationException {
    try {
      ItemValidatingService.validate(pubItem, vp);
    } catch (ValidationException e) {
      throw new IngeApplicationException("Invalid metadata", e);
    } catch (Exception e) {
      throw new IngeTechnicalException(e.getMessage(), e);
    }
  }

  private SearchRetrieveResponseVO<PubItemVO> getAllVersions(String objectId)
      throws IngeTechnicalException {
    QueryBuilder latestReleaseQuery =
        QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID, objectId);
    SearchRetrieveResponseVO<PubItemVO> resp =
        executeSearchSortByVersion(latestReleaseQuery, 10000, 0);

    return resp;
  }

  private SearchRetrieveResponseVO<PubItemVO> executeSearchSortByVersion(QueryBuilder query,
      int limit, int offset) throws IngeTechnicalException {

    SearchSortCriteria sortByVersion =
        new SearchSortCriteria(PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID, SortOrder.DESC);
    SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(query, limit, offset, sortByVersion);
    return pubItemDao.search(srr);
  }

  @Transactional(readOnly = true)
  public void reindex() {

    Query<de.mpg.mpdl.inge.model.db.valueobjects.PubItemObjectDbVO> query =
        (Query<de.mpg.mpdl.inge.model.db.valueobjects.PubItemObjectDbVO>) entityManager
            .createQuery("SELECT itemObject FROM PubItemObjectVO itemObject");
    query.setReadOnly(true);
    query.setFetchSize(500);
    query.setCacheMode(CacheMode.IGNORE);
    query.setFlushMode(FlushModeType.COMMIT);
    query.setCacheable(false);
    ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);

    int count = 0;
    while (results.next()) {
      try {
        count++;
        de.mpg.mpdl.inge.model.db.valueobjects.PubItemObjectDbVO object =
            (de.mpg.mpdl.inge.model.db.valueobjects.PubItemObjectDbVO) results.get(0);
        PubItemVO latestVersion =
            EntityTransformer.transformToOld((PubItemVersionDbVO) object.getLatestVersion());
        logger.info("(" + count + ") Reindexing item latest version "
            + latestVersion.getVersion().getObjectIdAndVersion());
        pubItemDao.create(latestVersion.getVersion().getObjectId() + "_"
            + latestVersion.getVersion().getVersionNumber(), latestVersion);
        if (object.getLatestRelease() != null
            && object.getLatestRelease().getVersionNumber() != object.getLatestVersion()
                .getVersionNumber()) {
          PubItemVO latestRelease =
              EntityTransformer.transformToOld((PubItemVersionDbVO) object.getLatestRelease());
          logger.info("(" + count + ") Reindexing item latest release "
              + latestRelease.getVersion().getObjectIdAndVersion());
          pubItemDao.create(latestRelease.getVersion().getObjectId() + "_"
              + latestRelease.getVersion().getVersionNumber(), latestRelease);
        }

        // Clear entity manager after every 1000 items, otherwise OutOfMemory can occur
        if (count % 1000 == 0) {
          logger.info("Clearing entity manager");
          entityManager.flush();
          entityManager.clear();
        }

      } catch (Exception e) {
        logger.error("Error while reindexing ", e);
      }

    }

  }

  @Override
  @Transactional(readOnly = true)
  public List<VersionHistoryEntryVO> getVersionHistory(String pubItemId, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException {

    List<AuditDbVO> list =
        auditRepository.findDistinctAuditByPubItemObjectIdOrderByModificationDateDesc(pubItemId);

    return EntityTransformer.transformToVersionHistory(list);
  }



  private void mapFileVOToFileDbVO(PubItemVO pubItemVO) {

  }

  private void uploadStagedFiles(PubItemVO pubItemVO) throws IngeTechnicalException {
    for (FileVO fileVO : pubItemVO.getFiles()) {
      if ((Storage.INTERNAL_MANAGED).equals(fileVO.getStorage())) {
        String stagedPath = fileVO.getContent();
        try {
          String persistentPath =
              Request.Put(REST_SERVICE_URL + REST_COMPONENT_PATH + "/" + fileVO.getName())
                  .bodyStream(fileService.readStageFile(Paths.get(stagedPath))).execute()
                  .returnContent().asString();
          fileVO.setContent(persistentPath);
        } catch (IOException e) {
          logger.error("Could not upload staged file [" + stagedPath + "]", e);
          throw new IngeTechnicalException("Could not upload staged file [" + stagedPath + "]", e);
        }
      }
    }
  }

  private void rollbackSavedFiles(PubItemVO pubItemVO) throws IngeTechnicalException {
    for (FileVO fileVO : pubItemVO.getFiles()) {
      if ((Storage.INTERNAL_MANAGED).equals(fileVO.getStorage())) {
        String relativePath = fileVO.getContent();
        try {
          fileService.deleteFile(relativePath);
        } catch (IngeTechnicalException e) {
          logger.error("Could not upload staged file [" + relativePath + "]", e);
          throw new IngeTechnicalException("Could not upload staged file [" + relativePath + "]", e);
        }
      }
    }
  }

  @Override
  protected GenericDaoEs<PubItemVO> getElasticDao() {
    return pubItemDao;
  }

}
