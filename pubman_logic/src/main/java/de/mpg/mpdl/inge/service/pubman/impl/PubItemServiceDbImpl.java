package de.mpg.mpdl.inge.service.pubman.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.hibernate.CacheMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
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
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO.Visibility;
import de.mpg.mpdl.inge.model.db.valueobjects.PubItemDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.PubItemObjectDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.PubItemVersionDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.VersionableId;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
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
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.FileService;
import de.mpg.mpdl.inge.service.pubman.PidService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.ReindexListener;
import de.mpg.mpdl.inge.service.util.EntityTransformer;
import de.mpg.mpdl.inge.service.util.PubItemUtil;
import de.mpg.mpdl.inge.util.PropertyReader;

@Service
@Primary
public class PubItemServiceDbImpl extends GenericServiceBaseImpl<PubItemVO> implements
    PubItemService, ReindexListener {

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

  @Autowired
  private ItemValidatingService itemValidatingService;

  @PersistenceContext
  EntityManager entityManager;

  @Autowired
  @Qualifier("queueJmsTemplate")
  private JmsTemplate queueJmsTemplate;

  @Autowired
  @Qualifier("topicJmsTemplate")
  private JmsTemplate topicJmsTemplate;


  public static String INDEX_MODIFICATION_DATE = "version.modificationDate";
  public static String INDEX_CREATION_DATE = "creationDate";
  public static String INDEX_LOCAL_TAGS = "localTags";
  public static String INDEX_CONTEXT_OBJECT_ID = "context.objectId";
  public static String INDEX_CONTEXT_TITLE = "context.title";
  public static String INDEX_OWNER_OBJECT_ID = "owner.objectId";
  public static String INDEX_OWNER_TITLE = "owner.title";
  public static String INDEX_PUBLIC_STATE = "publicStatus";
  public static String INDEX_PID = "pid";

  public static String INDEX_VERSION_STATE = "version.state";
  public static String INDEX_LATESTVERSION_VERSIONNUMBER = "latestVersion.versionNumber";
  public static String INDEX_LATESTVERSION_STATE = "latestVersion.state";
  public static String INDEX_LATESTRELEASE_DATE = "latestRelease.modificationDate";
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
      "metadata.creators.organization.identifier";
  public static String INDEX_METADATA_CREATOR_PERSON_ORGANIZATION_NAME =
      "metadata.creators.person.organizations.name";
  public static String INDEX_METADATA_CREATOR_ORGANIZATION_NAME =
      "metadata.creators.organization.name";
  public static String INDEX_METADATA_CREATOR_ROLE = "metadata.creators.role";

  public static String INDEX_METADATA_TITLE = "metadata.title";
  public static String INDEX_METADATA_ALTERNATIVETITLE_VALUE = "metadata.alternativeTitles.value";
  public static String INDEX_METADATA_DATE_ANY = "metadata.anyDates";
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

  public static String INDEX_METADATA_PUBLISHINGINFO_PUBLISHER_ID =
      "metadata.publishingInfo.publisher";

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

  public static String INDEX_METADATA_SOURCES_CREATOR_PERSON_FAMILYNAME =
      "metadata.sources.creators.person.familyName";

  public static String INDEX_METADATA_SOURCES_CREATOR_PERSON_GIVENNAME =
      "metadata.sources.creators.person.givenName";

  public static String INDEX_METADATA_SOURCES_CREATOR_PERSON_ORGANIZATION_IDENTIFIER =
      "metadata.sources.creators.person.organization.identifier";

  public static String INDEX_FILE_METADATA_EMBARGO_UNTIL = "files.metadata.embargoUntil";

  public static String INDEX_FILE_VISIBILITY = "files.visibility";

  public static String INDEX_FILE_CONTENTCATEGORY = "files.contentCategory";

  public static String INDEX_FILE_STORAGE = "files.storage";

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



    PubItemUtil.cleanUpItem(pubItemVO);

    PubItemVersionDbVO pubItemToCreate =
        buildPubItemToCreate("dummyId", contextNew, pubItemVO.getMetadata(),
            pubItemVO.getLocalTags(), userAccount.getReference().getTitle(), userAccount
                .getReference().getObjectId());

    pubItemToCreate.setFiles(handleFiles(pubItemVO, null, userAccount));
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
    sendEventTopic(itemToReturn, "create");
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
      List<String> localTags, String modifierName, String modifierId) {
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

    pubItem.setObject(pubItemObject);
    return pubItem;
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

    List<FileDbVO> fileDbVOList = handleFiles(pubItemVO, latestVersion, userAccount);
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
    sendEventTopic(itemToReturn, "update");
    logger.info("PubItem " + latestVersion.getObjectIdAndVersion() + " successfully updated in "
        + (System.currentTimeMillis() - start) + " ms");
    return itemToReturn;
  }


  private List<FileDbVO> handleFiles(PubItemVO newPubItemVO, PubItemVersionDbVO currentPubItemVO,
      AccountUserVO userAccount) throws IngeApplicationException, IngeTechnicalException {

    List<FileDbVO> updatedFileList = new ArrayList<>();

    Map<String, FileDbVO> currentFiles = new HashMap<>();
    if (currentPubItemVO != null) {
      for (FileDbVO file : currentPubItemVO.getFiles()) {
        currentFiles.put(file.getObjectId(), file);
      }
    }

    Date currentDate = new Date();
    for (FileVO fileVo : newPubItemVO.getFiles()) {

      if (fileVo.getReference() != null && fileVo.getReference().getObjectId() != null) {

      }

      FileDbVO currentFileDbVO;

      if (fileVo.getReference() != null && fileVo.getReference().getObjectId() != null) {


        if (!currentFiles.containsKey(fileVo.getReference().getObjectId())) {
          throw new IngeApplicationException("File with id [" + fileVo.getReference().getObjectId()
              + "] does not exist for this item. Please remove identifier to create as new file");
        }

        // Already existing file, just update some fields
        currentFileDbVO = currentFiles.remove(fileVo.getReference().getObjectId());


      } else {

        // New file
        currentFileDbVO = new FileDbVO();
        if ((Storage.INTERNAL_MANAGED).equals(fileVo.getStorage())) {
          String stagedFileId = fileVo.getContent();
          String persistentPath =
              fileService.createFileFromStagedFile(Integer.parseInt(stagedFileId),
                  fileVo.getName(), userAccount);
          currentFileDbVO.setLocalFileIdentifier(persistentPath);
          // TODO Set content to a REST path
          fileVo.setContent(null);
        }


        currentFileDbVO.setObjectId(idProviderService.getNewId(ID_PREFIX.FILES));
        currentFileDbVO.setStorage(FileDbVO.Storage.valueOf(fileVo.getStorage().name()));
        // TODO Checksum
        currentFileDbVO.setChecksum(fileVo.getChecksum());
        // oldFileVo.setChecksumAlgorithm(FileVO.ChecksumAlgorithm.valueOf(newFileVo
        // .getChecksumAlgorithm().name()));
        currentFileDbVO.setContent(fileVo.getContent());
        currentFileDbVO.setCreationDate(currentDate);
        AccountUserDbRO creator = new AccountUserDbRO();
        creator.setObjectId(userAccount.getUserid());
        creator.setName(userAccount.getName());
        currentFileDbVO.setCreator(creator);

        // TODO Pid ?
        currentFileDbVO.setPid(fileVo.getPid());

      }

      currentFileDbVO.setLastModificationDate(currentDate);
      currentFileDbVO.setMetadata(fileVo.getDefaultMetadata());
      currentFileDbVO.setName(fileVo.getDefaultMetadata().getTitle());
      currentFileDbVO.setDescription(fileVo.getDefaultMetadata().getDescription());
      currentFileDbVO.setContentCategory(fileVo.getDefaultMetadata().getContentCategory());
      currentFileDbVO.setMimeType(fileVo.getMimeType());
      currentFileDbVO.setVisibility(Visibility.valueOf(fileVo.getVisibility().name()));


      updatedFileList.add(currentFileDbVO);
    }
    return updatedFileList;
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

    // Delete reference to Object in latestRelease and latestVersion. Otherwise the object is not
    // deleted by EntityManager.
    // See http://www.baeldung.com/delete-with-hibernate or JPA spec section 3.2.2
    PubItemObjectDbVO pubItemObjectToDelete =
        itemObjectRepository.findOne(latestPubItemDbVersion.getObject().getObjectId());
    ((PubItemVersionDbVO) pubItemObjectToDelete.getLatestVersion()).setObject(null);
    if (pubItemObjectToDelete.getLatestRelease() != null) {
      ((PubItemVersionDbVO) pubItemObjectToDelete.getLatestRelease()).setObject(null);
    }


    itemObjectRepository.delete(pubItemObjectToDelete);

    SearchRetrieveResponseVO<PubItemVO> resp = getAllVersions(id);
    for (SearchRetrieveRecordVO<PubItemVO> rec : resp.getRecords()) {
      pubItemDao.delete(rec.getPersistenceId());
    }
    sendEventTopic(latestPubItem, "delete");

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
      logger.info("Item " + id + " not found");
      return null;
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
        URI url =
            new URI(PropertyReader.getProperty("escidoc.pubman.instance.url")
                + PropertyReader.getProperty("escidoc.pubman.instance.context.path")
                + PropertyReader.getProperty("escidoc.pubman.item.pattern").replaceAll("\\$1",
                    latestVersion.getObjectIdAndVersion()));
        latestVersion.setVersionPid(pidService.createPid(url).getIdentifier());
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
            URI uri =
                new URI(REST_SERVICE_URL + REST_COMPONENT_PATH + "/" + fileDbVO.getObjectId());
            fileDbVO.setPid(pidService.createPid(uri).getIdentifier());
          }
        } catch (URISyntaxException | TechnicalException e) {
          logger.error("Error creating PID for file [" + fileDbVO.getObjectId()
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



    reindex(latestVersion);
    sendEventTopic(itemToReturn, aaMethod);
    return itemToReturn;
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
      this.itemValidatingService.validate(pubItem, vp);
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

  @Override
  @Transactional(readOnly = true)
  public void reindexAll(String authenticationToken) throws IngeTechnicalException,
      AuthenticationException, AuthorizationException, IngeApplicationException {
    Query<String> query =
        (Query<String>) entityManager
            .createQuery("SELECT itemObject.objectId FROM PubItemObjectVO itemObject");
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
        String id = (String) results.get(0);
        queueJmsTemplate.convertAndSend("reindex", id);

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
  @JmsListener(containerFactory = "queueContainerFactory", destination = "reindex-PubItemVO")
  public void reindexListener(String id) throws IngeTechnicalException {
    reindex(id, false);
  }


  private void reindex(PubItemObjectDbVO object, boolean immediate) throws IngeTechnicalException {

    PubItemVersionDbVO latestVersion = (PubItemVersionDbVO) object.getLatestVersion();
    PubItemVO latestVersionOld =
        EntityTransformer.transformToOld((PubItemVersionDbVO) object.getLatestVersion());
    // First try to delete the old version from index
    pubItemDao.delete(new VersionableId(latestVersion.getObjectId(), latestVersion
        .getVersionNumber() - 1).toString());
    logger.info("Reindexing item latest version "
        + latestVersionOld.getVersion().getObjectIdAndVersion());

    if (immediate) {
      pubItemDao.createImmediately(latestVersionOld.getVersion().getObjectId() + "_"
          + latestVersionOld.getVersion().getVersionNumber(), latestVersionOld);
    } else {
      pubItemDao.create(latestVersionOld.getVersion().getObjectId() + "_"
          + latestVersionOld.getVersion().getVersionNumber(), latestVersionOld);
    }

    if (object.getLatestRelease() != null
        && object.getLatestRelease().getVersionNumber() != object.getLatestVersion()
            .getVersionNumber()) {
      PubItemVO latestRelease =
          EntityTransformer.transformToOld((PubItemVersionDbVO) object.getLatestRelease());
      logger.info("Reindexing item latest release "
          + latestRelease.getVersion().getObjectIdAndVersion());
      if (immediate) {
        pubItemDao.createImmediately(latestRelease.getVersion().getObjectId() + "_"
            + latestRelease.getVersion().getVersionNumber(), latestRelease);
      } else {
        pubItemDao.create(latestRelease.getVersion().getObjectId() + "_"
            + latestRelease.getVersion().getVersionNumber(), latestRelease);
      }

    }
  }

  private void reindex(String objectId, boolean immediate) throws IngeTechnicalException {
    de.mpg.mpdl.inge.model.db.valueobjects.PubItemObjectDbVO object =
        itemObjectRepository.findOne(objectId);
    reindex(object, immediate);

  }

  private void reindex(PubItemVersionDbVO item) throws IngeTechnicalException {
    reindex(item.getObject(), true);
  }

  @Override
  public void reindex(String id, String authenticationToken) throws IngeTechnicalException,
      AuthenticationException, AuthorizationException, IngeApplicationException {
    // TODO AA
    reindex(id, false);
  }


  @Override
  @Transactional(readOnly = true)
  public List<VersionHistoryEntryVO> getVersionHistory(String pubItemId, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException {

    List<AuditDbVO> list =
        auditRepository.findDistinctAuditByPubItemObjectIdOrderByModificationDateDesc(pubItemId);

    return EntityTransformer.transformToVersionHistory(list);
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


  private void sendEventTopic(PubItemVO item, String method) {
    topicJmsTemplate.convertAndSend(item, new MessagePostProcessor() {
      @Override
      public Message postProcessMessage(Message message) throws JMSException {
        message.setStringProperty("method", method);
        return message;
      }
    });
  }



}
