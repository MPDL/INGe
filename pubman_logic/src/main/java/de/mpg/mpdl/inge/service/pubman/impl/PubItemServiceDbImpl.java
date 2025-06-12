package de.mpg.mpdl.inge.service.pubman.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.mpg.mpdl.inge.db.repository.AuditRepository;
import de.mpg.mpdl.inge.db.repository.ContextRepository;
import de.mpg.mpdl.inge.db.repository.FileRepository;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl;
import de.mpg.mpdl.inge.db.repository.ItemObjectRepository;
import de.mpg.mpdl.inge.db.repository.ItemRepository;
import de.mpg.mpdl.inge.es.dao.GenericDaoEs;
import de.mpg.mpdl.inge.es.dao.PubItemDaoEs;
import de.mpg.mpdl.inge.inge_validation.ItemValidatingService;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.AuditDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemRootVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.db.valueobjects.VersionableId;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.aa.IpListProvider;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.FileService;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.pubman.PidService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.ReindexListener;
import de.mpg.mpdl.inge.service.util.GrantUtil;
import de.mpg.mpdl.inge.service.util.PubItemUtil;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.UriBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.PersistenceContext;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
public class PubItemServiceDbImpl extends GenericServiceBaseImpl<ItemVersionVO> implements PubItemService, ReindexListener {

  public static final String INDEX_CONTEXT_OBJECT_ID = "context.objectId";
  public static final String INDEX_CONTEXT_TITLE = "context.name";
  public static final String INDEX_CREATION_DATE = "creationDate";
  public static final String INDEX_FILE_CONTENTCATEGORY = "files.metadata.contentCategory";
  public static final String INDEX_FILE_METADATA_EMBARGO_UNTIL = "files.metadata.embargoUntil";
  public static final String INDEX_FILE_NAME = "files.name";
  public static final String INDEX_FILE_OA_STATUS = "files.metadata.oaStatus";
  public static final String INDEX_FILE_OBJECT_ID = "files.objectId";
  public static final String INDEX_FILE_STORAGE = "files.storage";
  public static final String INDEX_FILE_VISIBILITY = "files.visibility";
  public static final String INDEX_FULLTEXT_CONTENT = "fileData.attachment.content";
  public static final String INDEX_FULLTEXT_FILE_ID = "fileData.fileId";
  public static final String INDEX_FULLTEXT_ITEM_ID = "fileData.itemId";
  public static final String INDEX_LATESTRELEASE_DATE = "latestRelease.modificationDate";
  public static final String INDEX_LATESTVERSION_STATE = "latestVersion.versionState";
  public static final String INDEX_LATESTVERSION_VERSIONNUMBER = "latestVersion.versionNumber";
  public static final String INDEX_LOCAL_TAGS = "localTags";
  public static final String INDEX_METADATA_ALTERNATIVETITLE_VALUE = "metadata.alternativeTitles.value";
  public static final String INDEX_METADATA_CREATOR_ORGANIZATION_IDENTIFIER = "metadata.creators.organization.identifier";
  public static final String INDEX_METADATA_CREATOR_ORGANIZATION_IDENTIFIERPATH = "metadata.creators.organization.identifierPath";
  public static final String INDEX_METADATA_CREATOR_ORGANIZATION_NAME = "metadata.creators.organization.name";
  public static final String INDEX_METADATA_CREATOR_PERSON_FAMILYNAME = "metadata.creators.person.familyName";
  public static final String INDEX_METADATA_CREATOR_PERSON_GIVENNAME = "metadata.creators.person.givenName";
  public static final String INDEX_METADATA_CREATOR_PERSON_IDENTIFIER_ID = "metadata.creators.person.identifier.id";
  public static final String INDEX_METADATA_CREATOR_PERSON_ORCID = "metadata.creators.person.orcid";
  public static final String INDEX_METADATA_CREATOR_PERSON_ORGANIZATION_IDENTIFIER = "metadata.creators.person.organizations.identifier";
  public static final String INDEX_METADATA_CREATOR_PERSON_ORGANIZATION_IDENTIFIERPATH =
          "metadata.creators.person.organizations.identifierPath";
  public static final String INDEX_METADATA_CREATOR_PERSON_ORGANIZATION_NAME = "metadata.creators.person.organizations.name";
  public static final String INDEX_METADATA_CREATOR_ROLE = "metadata.creators.role";
  public static final String INDEX_METADATA_CREATOR_SORT = "sort-metadata-creators-compound";
  public static final String INDEX_METADATA_DATE_ACCEPTED = "metadata.dateAccepted";
  public static final String INDEX_METADATA_DATE_ANY = "metadata.anyDates";
  public static final String INDEX_METADATA_DATE_CATEGORY_SORT = "sort-metadata-dates-by-category";
  public static final String INDEX_METADATA_DATE_CATEGORY_YEAR_SORT = "sort-metadata-dates-by-category-year";
  public static final String INDEX_METADATA_DATE_CREATED = "metadata.dateCreated";
  public static final String INDEX_METADATA_DATE_MODIFIED = "metadata.dateModified";
  public static final String INDEX_METADATA_DATE_PUBLISHED_IN_PRINT = "metadata.datePublishedInPrint";
  public static final String INDEX_METADATA_DATE_PUBLISHED_ONLINE = "metadata.datePublishedOnline";
  public static final String INDEX_METADATA_DATE_SUBMITTED = "metadata.dateSubmitted";
  public static final String INDEX_METADATA_DEGREE = "metadata.degree";
  public static final String INDEX_METADATA_EVENT_ENDDATE = "metadata.event.endDate";
  public static final String INDEX_METADATA_EVENT_INVITATION_STATUS = "metadata.event.invitationStatus";
  public static final String INDEX_METADATA_EVENT_STARTDATE = "metadata.event.startDate";
  public static final String INDEX_METADATA_EVENT_TITLE = "metadata.event.title";
  public static final String INDEX_METADATA_FREEKEYWORDS = "metadata.freeKeywords";
  public static final String INDEX_METADATA_GENRE = "metadata.genre";
  public static final String INDEX_METADATA_IDENTIFIERS_ID = "metadata.identifiers.id";
  public static final String INDEX_METADATA_IDENTIFIERS_TYPE = "metadata.identifiers.type";
  public static final String INDEX_METADATA_LANGUAGES = "metadata.languages";
  public static final String INDEX_METADATA_PROJECTINFO_FUNDING_ORGANIZATION_IDENTIFIERS_ID =
          "metadata.projectInfo.fundingInfo.fundingOrganization.identifiers.id";
  public static final String INDEX_METADATA_PROJECTINFO_FUNDING_ORGANIZATION_TITLE =
          "metadata.projectInfo.fundingInfo.fundingOrganization.title";
  public static final String INDEX_METADATA_PROJECTINFO_FUNDING_PROGRAM_IDENTIFIERS_ID =
          "metadata.projectInfo.fundingInfo.fundingProgram.identifiers.id";
  public static final String INDEX_METADATA_PROJECTINFO_FUNDING_PROGRAM_TITLE = "metadata.projectInfo.fundingInfo.fundingProgram.title";
  public static final String INDEX_METADATA_PROJECTINFO_GRANT_IDENTIFIER_ID = "metadata.projectInfo.grantIdentifier.id";
  public static final String INDEX_METADATA_PROJECTINFO_TITLE = "metadata.projectInfo.title";
  public static final String INDEX_METADATA_PUBLISHINGINFO_EDITION = "metadata.publishingInfo.edition";
  public static final String INDEX_METADATA_PUBLISHINGINFO_PLACE = "metadata.publishingInfo.place";
  public static final String INDEX_METADATA_PUBLISHINGINFO_PUBLISHER_ID = "metadata.publishingInfo.publisher";
  public static final String INDEX_METADATA_REVIEW_METHOD = "metadata.reviewMethod";
  public static final String INDEX_METADATA_SOURCES_ALTERNATIVE_TITLE = "metadata.sources.alternativeTitles.value";
  public static final String INDEX_METADATA_SOURCES_CREATOR_PERSON_FAMILYNAME = "metadata.sources.creators.person.familyName";
  public static final String INDEX_METADATA_SOURCES_CREATOR_PERSON_GIVENNAME = "metadata.sources.creators.person.givenName";
  public static final String INDEX_METADATA_SOURCES_CREATOR_PERSON_ORCID = "metadata.sources.creators.person.orcid";
  public static final String INDEX_METADATA_SOURCES_CREATOR_PERSON_ORGANIZATIONS_IDENTIFIER =
          "metadata.sources.creators.person.organizations.identifier";
  public static final String INDEX_METADATA_SOURCES_CREATOR_PERSON_ORGANIZATIONS_IDENTIFIERPATH =
          "metadata.sources.creators.person.organizations.identifierPath";
  public static final String INDEX_METADATA_SOURCES_IDENTIFIERS_ID = "metadata.sources.identifiers.id";
  public static final String INDEX_METADATA_SOURCES_IDENTIFIERS_TYPE = "metadata.sources.identifiers.type";
  public static final String INDEX_METADATA_SOURCES_TITLE = "metadata.sources.title";
  public static final String INDEX_METADATA_SUBJECTS_TYPE = "metadata.subjects.type";
  public static final String INDEX_METADATA_SUBJECTS_VALUE = "metadata.subjects.value";
  public static final String INDEX_METADATA_TITLE = "metadata.title";
  public static final String INDEX_MODIFICATION_DATE = "modificationDate";
  public static final String INDEX_OWNER_OBJECT_ID = "creator.objectId";
  public static final String INDEX_OWNER_TITLE = "creator.name";
  public static final String INDEX_PID = "objectPid";
  public static final String INDEX_PUBLIC_STATE = "publicState";
  public static final String INDEX_VERSION_OBJECT_ID = "objectId";
  public static final String INDEX_VERSION_PID = "versionPid";
  public static final String INDEX_VERSION_STATE = "versionState";
  public static final String INDEX_VERSION_VERSIONNUMBER = "versionNumber";

  private static final Logger logger = LogManager.getLogger(PubItemServiceDbImpl.class);

  @PersistenceContext
  EntityManager entityManager;
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
  @Autowired
  private OrganizationService organizationService;
  @Autowired
  @Qualifier("queueJmsTemplate")
  private JmsTemplate queueJmsTemplate;
  @Autowired
  @Qualifier("topicJmsTemplate")
  private JmsTemplate topicJmsTemplate;
  @Autowired
  private FileRepository fileRepository;
  @Autowired
  @Qualifier("mpgJsonIpListProvider")
  private IpListProvider ipListProvider;

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public ItemVersionVO addNewDoi(String itemId, String authenticationToken)
          throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    Principal principal = this.aaService.checkLoginRequired(authenticationToken);

    ValidId validId = getValidId(itemId);

    ItemVersionVO latestVersion = this.itemRepository.findLatestVersion(validId.objectId);
    if (null == latestVersion) {
      throw new IngeApplicationException("Object with given id not found.");
    }
    ContextDbVO context = this.contextRepository.findById(latestVersion.getObject().getContext().getObjectId()).orElse(null);
    checkAa("addNewDoi", principal, latestVersion, context);

    String doi = DoiRestService.getNewDoi(latestVersion);
    latestVersion.getMetadata().getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.DOI, doi));
    ItemVersionVO updatedPubItem = update(latestVersion, authenticationToken);
    ItemVersionVO releasedPubItem =
            releasePubItem(updatedPubItem.getObjectId(), updatedPubItem.getModificationDate(), "DOI added", authenticationToken);

    return releasedPubItem;
  }

  public boolean checkAccess(AuthorizationService.AccessType at, Principal principal, ItemVersionVO item)
          throws IngeApplicationException, IngeTechnicalException {
    try {
      checkAa(at.getMethodName(), principal, item, item.getObject().getContext());
    } catch (AuthenticationException | AuthorizationException e) {
      return false;
    } catch (IngeTechnicalException | IngeApplicationException e) {
      throw e;
    } catch (Exception e) {
      throw new IngeTechnicalException("", e);
    }
    return true;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public ItemVersionVO create(ItemVersionVO pubItemVO, String authenticationToken)
          throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    long start = System.currentTimeMillis();
    Principal principal = this.aaService.checkLoginRequired(authenticationToken);

    de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO contextNew =
            this.contextRepository.findById(pubItemVO.getObject().getContext().getObjectId()).orElse(null);

    if (null == contextNew) {
      throw new IngeApplicationException("Context with id " + pubItemVO.getObject().getContext().getObjectId() + "not found");
    }

    PubItemUtil.cleanUpItem(pubItemVO);

    // Remove old file ids and add absolute path to content - used e.g. for import
    if (null != pubItemVO.getFiles()) {
      for (FileDbVO file : pubItemVO.getFiles()) {
        file.setObjectId(null);
        if (null != file.getContent() && FileDbVO.Storage.INTERNAL_MANAGED.equals(file.getStorage())
                && file.getContent().startsWith("/rest/items")) {
          file.setContent(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL) + file.getContent());
        }

      }
    }

    PubItemUtil.setOrganizationIdPathInItem(pubItemVO, this.organizationService);

    ItemVersionVO pubItemToCreate = buildPubItemToCreate("dummyId", contextNew, pubItemVO, principal.getUserAccount());

    pubItemToCreate.setFiles(pubItemVO.getFiles());

    checkAa("create", principal, pubItemToCreate, contextNew);

    validate(pubItemToCreate, ValidationPoint.SAVE);

    String id = this.idProviderService.getNewId(IdentifierProviderServiceImpl.ID_PREFIX.ITEM);
    VersionableId newId = new VersionableId(id, 1);
    pubItemToCreate.setObjectId(id);
    pubItemToCreate.getObject().setObjectId(id);

    pubItemToCreate.setFiles(handleFiles(pubItemVO, null, principal, newId));

    try {
      pubItemToCreate = this.itemRepository.saveAndFlush(pubItemToCreate);
    } catch (DataAccessException e) {
      rollbackSavedFiles(pubItemVO);
      GenericServiceImpl.handleDBException(e);
    }


    createAuditEntry(pubItemToCreate, AuditDbVO.EventType.CREATE);
    reindex(pubItemToCreate);
    sendEventTopic(pubItemToCreate, "create");
    long time = System.currentTimeMillis() - start;
    logger.info("PubItem " + newId + " successfully created in " + time + " ms");

    return pubItemToCreate;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void delete(String id, String authenticationToken)
          throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    Principal principal = this.aaService.checkLoginRequired(authenticationToken);

    ItemVersionVO latestPubItemDbVersion = this.itemRepository.findLatestVersion(id);
    if (null == latestPubItemDbVersion) {
      throw new IngeApplicationException("Item " + id + " not found");
    }


    ContextDbVO context = this.contextRepository.findById(latestPubItemDbVersion.getObject().getContext().getObjectId()).orElse(null);
    checkAa("delete", principal, latestPubItemDbVersion, context);

    List<String> deletedFiles = new ArrayList<>();

    // Delete reference to Object in latestRelease and latestVersion. Otherwise the object is not
    // deleted by EntityManager.
    // See http://www.baeldung.com/delete-with-hibernate or JPA spec section 3.2.2
    ItemRootVO pubItemObjectToDelete = this.itemObjectRepository.findById(latestPubItemDbVersion.getObject().getObjectId()).orElse(null);
    ((ItemVersionVO) pubItemObjectToDelete.getLatestVersion()).setObject(null);
    if (null != pubItemObjectToDelete.getLatestRelease()) {
      ((ItemVersionVO) pubItemObjectToDelete.getLatestRelease()).setObject(null);
    }


    // Delete all files and versions
    for (int i = 1; i <= latestPubItemDbVersion.getVersionNumber(); i++) {
      ItemVersionVO item = this.itemRepository.findById(new VersionableId(latestPubItemDbVersion.getObjectId(), i)).orElse(null);
      for (FileDbVO file : item.getFiles()) {
        if (!deletedFiles.contains(file.getObjectId())) {
          this.fileRepository.delete(file);
          deletedFiles.add(file.getObjectId());
          if (FileDbVO.Storage.INTERNAL_MANAGED.equals(file.getStorage())) {
            fileService.deleteFile(file.getLocalFileIdentifier());
          }
        }


      }
      item.setFiles(null);
      this.itemRepository.delete(item);
    }

    // Delete Object
    this.itemObjectRepository.delete(pubItemObjectToDelete);

    SearchRetrieveResponseVO<ItemVersionVO> resp = getAllVersions(id);
    for (SearchRetrieveRecordVO<ItemVersionVO> rec : resp.getRecords()) {
      this.pubItemDao.deleteImmediatly(rec.getPersistenceId());
      this.pubItemDao.deleteByQuery(TermQuery.of(t -> t.field(INDEX_FULLTEXT_ITEM_ID).value(rec.getPersistenceId()))._toQuery(), 1000);
    }
    sendEventTopic(latestPubItemDbVersion, "delete");

    logger.info("PubItem " + id + " successfully deleted");

  }

  @Override
  @Transactional(readOnly = true)
  public ItemVersionVO get(String id, String authenticationToken)
          throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    long start = System.currentTimeMillis();

    ValidId validId = getValidId(id);

    //    String[] splittedId = id.split("_");
    //    String objectId = splittedId[0] + "_" + splittedId[1];
    //    String version = null;
    //    if (splittedId.length == 3) {
    //      version = splittedId[2];
    //    }

    ItemVersionVO requestedItem = null;
    Principal principal = null;

    if (null != authenticationToken) {
      principal = this.aaService.checkLoginRequired(authenticationToken);
    }

    if (null == validId.version) {
      if (null == authenticationToken) {
        // Return latest release
        requestedItem = this.itemRepository.findLatestRelease(validId.objectId);
      } else {
        // Check if user is allowed to see latest version
        requestedItem = this.itemRepository.findLatestVersion(validId.objectId);
        if (null != requestedItem) {

          ContextDbVO context = this.contextRepository.findById(requestedItem.getObject().getContext().getObjectId()).orElse(null);
          try {
            checkAa("get", principal, requestedItem, context);
          } catch (AuthenticationException | AuthorizationException e) {
            requestedItem = this.itemRepository.findLatestRelease(validId.objectId);
          }
        }
      }
    } else {
      requestedItem = this.itemRepository.findById(new VersionableId(validId.objectId, validId.version)).orElse(null);
      if (null != requestedItem) {
        ContextDbVO context = this.contextRepository.findById(requestedItem.getObject().getContext().getObjectId()).orElse(null);
        checkAa("get", principal, requestedItem, context);
      }
    }

    if (null == requestedItem) {
      logger.error("Item " + id + " not found");
      return null;
    }

    requestedItem.setFileLinks();
    long time = System.currentTimeMillis() - start;
    logger.info("PubItem " + id + " successfully retrieved in " + time + " ms");

    return requestedItem;
  }

  public Map<AuthorizationService.AccessType, Boolean> getAuthorizationInfo(String itemId, String authenticationToken)
          throws IngeApplicationException, IngeTechnicalException {
    Principal principal = null;
    ItemVersionVO item = null;
    if (authenticationToken != null) {
      try {
        principal = this.aaService.checkLoginRequired(authenticationToken);
        item = get(itemId, authenticationToken);
        //item not found, return null
        if (item == null) {
          return null;
        }

      } catch (AuthenticationException | AuthorizationException e) {

      }
    }
    Map<AuthorizationService.AccessType, Boolean> authMap = new LinkedHashMap<>();

    for (AuthorizationService.AccessType at : AuthorizationService.AccessType.values()) {
      boolean isAuthorized = false;
      if (principal != null && item != null) {
        if (AuthorizationService.AccessType.ADD_NEW_DOI.equals(at)) {
          isAuthorized = checkAccess(at, principal, item) && DoiRestService.isItemDoiReady(item);
        } else {
          isAuthorized = checkAccess(at, principal, item);
        }

      }
      authMap.put(at, isAuthorized);

    }
    return authMap;
  }

  public JsonNode getAuthorizationInfoForFile(String itemId, String fileId, String authenticationToken)
          throws IngeApplicationException, IngeTechnicalException {
    Principal principal = null;
    ItemVersionVO item = null;
    if (authenticationToken != null) {
      try {
        principal = this.aaService.checkLoginRequired(authenticationToken);
        item = get(itemId, authenticationToken);
        //item not found, return null
        if (item == null) {
          return null;
        }

      } catch (AuthenticationException | AuthorizationException e) {

      }
    }

    FileDbVO file = item.getFiles().stream().filter(f -> fileId.equals(f.getObjectId())).findFirst().get();
    if (file == null) {
      return null;
    }


    ObjectNode returnNode = objectMapper.createObjectNode();

    JsonNode readFileNode = objectMapper.createObjectNode().put(AuthorizationService.AccessType.READ_FILE.name(),
            fileService.checkAccess(AuthorizationService.AccessType.READ_FILE, principal, item, file));
    returnNode.set("actions", readFileNode);

    if (file.getVisibility().equals(FileDbVO.Visibility.AUDIENCE)) {

      ObjectNode ipNameObject = objectMapper.createObjectNode();
      returnNode.set("ipInfo", ipNameObject);
      for (String audienceId : file.getAllowedAudienceIds()) {
        IpListProvider.IpRange ipRange = this.ipListProvider.get(audienceId);
        if (ipRange != null) {
          ipNameObject.put(ipRange.getId(), ipRange.getName());
        }
      }
    }
    return returnNode;

  }

  @Override
  @Transactional(readOnly = true)
  public List<AuditDbVO> getVersionHistory(String pubItemId, String authenticationToken) {

    List<Object[]> results = this.auditRepository.findDistinctAuditByPubItemObjectIdOrderByModificationDateDesc(pubItemId);

    List<AuditDbVO> versionHistory = new ArrayList<>();
    // a.id, a.comment, a.event, a.modificationdate, a.modifier_name, a.modifier_objectid, a.pubitem_objectid, a.pubitem_versionnumber
    for (Object[] result : results) {
      AuditDbVO auditDbVO = new AuditDbVO();
      auditDbVO.setId(((int) result[0]));
      auditDbVO.setComment((String) result[1]);
      auditDbVO.setEvent(AuditDbVO.EventType.valueOf((String) result[2]));
      auditDbVO.setModificationDate((Date) result[3]);
      AccountUserDbRO accountUserDbRO = new AccountUserDbRO();
      accountUserDbRO.setName((String) result[4]);
      accountUserDbRO.setObjectId((String) result[5]);
      auditDbVO.setModifier(accountUserDbRO);
      ItemVersionVO itemVersionVO = new ItemVersionVO();
      itemVersionVO.setObjectId((String) result[6]);
      itemVersionVO.setVersionNumber((int) result[7]);
      auditDbVO.setPubItem(itemVersionVO);
      versionHistory.add(auditDbVO);
    }

    return versionHistory;
  }

  @Override
  public void reindex(String id, String authenticationToken) throws IngeTechnicalException {
    // TODO AA
    reindex(id, false, true);
  }

  @Override
  public void reindex(String id, boolean includeFulltext, String authenticationToken) throws IngeTechnicalException {
    // TODO AA
    reindex(id, false, includeFulltext);
  }

  @Override
  @Transactional(readOnly = true)
  public void reindexAll(String authenticationToken) {
    Query<String> query = (Query<String>) this.entityManager.createQuery("SELECT itemObject.objectId FROM ItemRootVO itemObject");
    query.setReadOnly(true);
    query.setFetchSize(500);
    query.setCacheMode(CacheMode.IGNORE);
    query.setFlushMode(FlushModeType.COMMIT);
    query.setCacheable(false);

    try (ScrollableResults<String> results = query.scroll(ScrollMode.FORWARD_ONLY)) {
      int count = 0;
      while (results.next()) {
        try {
          count++;
          String id = results.get();
          this.queueJmsTemplate.convertAndSend("reindex-ItemVersionVO", id);

          // Clear entity manager after every 1000 items, otherwise OutOfMemory can occur
          if (0 == count % 1000) {
            logger.info("Clearing entity manager");
            this.entityManager.flush();
            this.entityManager.clear();
          }

        } catch (Exception e) {
          logger.error("Error while reindexing ", e);
        }
      }
    }
  }

  @Override
  @JmsListener(containerFactory = "queueContainerFactory", destination = "reindex-ItemVersionVO")
  public void reindexListener(String id) throws IngeTechnicalException {
    reindex(id, false, true);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public ItemVersionVO releasePubItem(String pubItemId, Date modificationDate, String message, String authenticationToken)
          throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    return changeState(pubItemId, modificationDate, ItemVersionRO.State.RELEASED, message, "release", authenticationToken,
            AuditDbVO.EventType.RELEASE);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public ItemVersionVO revisePubItem(String pubItemId, Date modificationDate, String message, String authenticationToken)
          throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    return changeState(pubItemId, modificationDate, ItemVersionRO.State.IN_REVISION, message, "revise", authenticationToken,
            AuditDbVO.EventType.REVISE);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public ItemVersionVO rollbackToVersion(String itemId, String authenticationToken)
          throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    Principal principal = this.aaService.checkLoginRequired(authenticationToken);

    ValidId validId = getValidId(itemId);

    if (validId.version == null) {
      throw new IngeApplicationException("No version given.");
    }

    ItemVersionVO latestVersion = this.itemRepository.findLatestVersion(validId.objectId);
    if (null == latestVersion) {
      throw new IngeApplicationException("Object with given id not found.");
    }

    if (latestVersion.getVersionNumber() == validId.version.intValue()) {
      throw new IngeApplicationException("Versionnumber given is the latest version.");
    }

    ContextDbVO context = this.contextRepository.findById(latestVersion.getObject().getContext().getObjectId()).orElse(null);
    checkAa("rollbackToVersion", principal, latestVersion, context);

    ItemVersionVO rollbackVersion = this.get(itemId, authenticationToken);

    // Now copy the old stuff into the current item
    latestVersion.setMetadata(rollbackVersion.getMetadata());

    // Do not forget the files and locators
    latestVersion.getFiles().clear();
    for (FileDbVO fileVO : rollbackVersion.getFiles()) {
      FileDbVO clonedFile = new FileDbVO(fileVO);
      latestVersion.getFiles().add(clonedFile);
    }

    ItemVersionVO newVersion = this.update(latestVersion, authenticationToken, false);
    createAuditEntry(newVersion, AuditDbVO.EventType.UPDATE, "Rollback to version " + validId.version);

    return newVersion;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public ItemVersionVO submitPubItem(String pubItemId, Date modificationDate, String message, String authenticationToken)
          throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    return changeState(pubItemId, modificationDate, ItemVersionRO.State.SUBMITTED, message, "submit", authenticationToken,
            AuditDbVO.EventType.SUBMIT);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public ItemVersionVO update(ItemVersionVO pubItemVO, String authenticationToken) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException{
    return update(pubItemVO, authenticationToken, true);
  }


  private ItemVersionVO update(ItemVersionVO pubItemVO, String authenticationToken, boolean createAuditEntry)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    ContextDbVO contextNew = null;
    long start = System.currentTimeMillis();
    Principal principal = this.aaService.checkLoginRequired(authenticationToken);
    PubItemUtil.cleanUpItem(pubItemVO);
    ItemVersionVO latestVersion = this.itemRepository.findLatestVersion(pubItemVO.getObjectId());
    if (null == latestVersion) {
      throw new IngeApplicationException("Object with given id not found.");
    }

    checkEqualModificationDate(pubItemVO.getModificationDate(), latestVersion.getModificationDate());

    ContextDbVO context = this.contextRepository.findById(latestVersion.getObject().getContext().getObjectId()).orElse(null);

    checkAa("update", principal, latestVersion, context);

    if (null != pubItemVO.getObject().getContext() && !(pubItemVO.getObject().getContext().getObjectId().equals(context.getObjectId()))) {
      contextNew = this.contextRepository.findById(pubItemVO.getObject().getContext().getObjectId()).orElse(null);
      checkAa("update", principal, latestVersion, contextNew);
    }



    if (ItemVersionRO.State.RELEASED.equals(latestVersion.getVersionState())) {
      this.entityManager.detach(latestVersion);
      // Reset latestRelase reference because it is the same object as latest version
      ItemVersionRO latestReleaseDbRO = new ItemVersionRO();
      latestReleaseDbRO.setObjectId(latestVersion.getObject().getLatestRelease().getObjectId());
      latestReleaseDbRO.setVersionNumber(latestVersion.getObject().getLatestRelease().getVersionNumber());
      latestVersion.getObject().setLatestRelease(latestReleaseDbRO);

      // if current user is owner, set to status pending. Else, set to status submitted

      if (GrantUtil.hasRole(principal.getUserAccount(), GrantVO.PredefinedRoles.MODERATOR,
          null != contextNew ? contextNew.getObjectId() : context.getObjectId())) {
        latestVersion.setVersionState(ItemVersionRO.State.SUBMITTED);
      } else {
        latestVersion.setVersionState(ItemVersionRO.State.PENDING);
      }

      latestVersion.setVersionNumber(latestVersion.getVersionNumber() + 1);
      latestVersion.getObject().setLatestVersion(latestVersion);
    }

    if (null != contextNew) {
      latestVersion.getObject().setContext(contextNew);
    }
    updatePubItemWithTechnicalMd(latestVersion, principal.getUserAccount().getName(), principal.getUserAccount().getObjectId());
    latestVersion.setMetadata(pubItemVO.getMetadata());

    PubItemUtil.setOrganizationIdPathInItem(latestVersion, this.organizationService);



    latestVersion.getObject().setLocalTags(pubItemVO.getObject().getLocalTags());

    // Set Files and keep old file list
    List<FileDbVO> oldFiles = latestVersion.getFiles();
    latestVersion.setFiles(pubItemVO.getFiles());

    validate(latestVersion);

    // Reset files to old files and handle them
    latestVersion.setFiles(oldFiles);
    latestVersion.setFiles(handleFiles(pubItemVO, latestVersion, principal, new VersionableId(latestVersion.getObjectIdAndVersion())));

    try {
      latestVersion = this.itemRepository.saveAndFlush(latestVersion);
    } catch (DataAccessException e) {
      GenericServiceImpl.handleDBException(e);
    }
    if(createAuditEntry)
    {
      createAuditEntry(latestVersion, AuditDbVO.EventType.UPDATE, null);
    }

    reindex(latestVersion);
    sendEventTopic(latestVersion, "update");
    logger.info(
        "PubItem " + latestVersion.getObjectIdAndVersion() + " successfully updated in " + (System.currentTimeMillis() - start) + " ms");
    return latestVersion;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public ItemVersionVO withdrawPubItem(String pubItemId, Date modificationDate, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    return changeState(pubItemId, modificationDate, ItemVersionRO.State.WITHDRAWN, message, "withdraw", authenticationToken,
        AuditDbVO.EventType.WITHDRAW);
  }

  private ItemVersionVO buildPubItemToCreate(String objectId, de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO context,
      ItemVersionVO givenPubItemVO, AccountUserDbVO userAccount) {
    Date currentDate = new Date();

    ItemVersionVO pubItem = new ItemVersionVO();

    pubItem.setMetadata(givenPubItemVO.getMetadata());
    pubItem.setMessage(null);
    pubItem.setModificationDate(currentDate);
    AccountUserDbRO mod = new AccountUserDbRO();
    // Moved out due to DSGVO
    // mod.setName(userAccount.getName());
    mod.setObjectId(userAccount.getObjectId());
    pubItem.setModifier(mod);
    pubItem.setObjectId(objectId);
    pubItem.setVersionState(ItemVersionRO.State.PENDING);
    pubItem.setVersionNumber(1);
    pubItem.setVersionPid(null);

    ItemRootVO pubItemObject = new ItemRootVO();
    pubItemObject.setContext(context);
    pubItemObject.setCreationDate(currentDate);
    pubItemObject.setLastModificationDate(currentDate);
    pubItemObject.setLatestVersion(pubItem);
    pubItemObject.setLocalTags(givenPubItemVO.getObject().getLocalTags());
    pubItemObject.setObjectId(objectId);
    pubItemObject.setCreator(mod);
    pubItemObject.setObjectPid(null);
    pubItemObject.setPublicState(ItemVersionRO.State.PENDING);

    pubItem.setObject(pubItemObject);

    return pubItem;
  }

  private ItemVersionVO changeState(String id, Date modificationDate, ItemVersionRO.State state, String message, String aaMethod,
      String authenticationToken, AuditDbVO.EventType auditEventType)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Principal principal = this.aaService.checkLoginRequired(authenticationToken);

    ItemVersionVO latestVersion = this.itemRepository.findLatestVersion(id);

    if (null == latestVersion) {
      throw new IngeApplicationException("Object with given id not found.");
    }


    checkEqualModificationDate(modificationDate, latestVersion.getModificationDate());

    ContextDbVO context = this.contextRepository.findById(latestVersion.getObject().getContext().getObjectId()).orElse(null);

    checkAa(aaMethod, principal, latestVersion, context);

    if (ItemVersionRO.State.SUBMITTED.equals(state) && !ItemVersionRO.State.RELEASED.equals(latestVersion.getObject().getPublicState())) {
      latestVersion.getObject().setPublicState(ItemVersionRO.State.SUBMITTED);
    }

    if (ItemVersionRO.State.RELEASED.equals(state)) {
      latestVersion.getObject().setPublicState(ItemVersionRO.State.RELEASED);
      latestVersion.getObject().setLatestRelease(latestVersion);
    }

    if (ItemVersionRO.State.WITHDRAWN.equals(state)) {
      // change public state to withdrawn, leave version state as is
      latestVersion.getObject().setPublicState(ItemVersionRO.State.WITHDRAWN);
      // latestVersion.getObject().setWithdrawComment(message);
    } else {
      latestVersion.setVersionState(state);
    }

    updatePubItemWithTechnicalMd(latestVersion, principal.getUserAccount().getName(), principal.getUserAccount().getObjectId());

    latestVersion.setMessage(message);

    if (!ItemVersionRO.State.WITHDRAWN.equals(state)) {
      validate(latestVersion);
    }

    // vorherige Validierung notwendig, da sonst PID unnötigerweise angelegt wird (kein 2 Phase
    // Commit!)
    // PID Generierung
    if (ItemVersionRO.State.RELEASED.equals(state)) {
      ItemRootVO pubItemObject = latestVersion.getObject();
      try {
        if (null == pubItemObject.getObjectPid()) {
          URI url = UriBuilder.getItemObjectLink(latestVersion.getObjectId());
          if ("true".equalsIgnoreCase(PropertyReader.getProperty(PropertyReader.INGE_PID_SERVICE_USE))) {
            pubItemObject.setObjectPid(
                PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_SHORT) + this.pidService.createPid(url).getIdentifier());
          } else {
            pubItemObject.setObjectPid(url.toString().replace(PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_URL),
                PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_SHORT)));
          }
        }
        URI url = UriBuilder.getItemObjectAndVersionLink(latestVersion.getObjectId(), latestVersion.getVersionNumber());
        if ("true".equalsIgnoreCase(PropertyReader.getProperty(PropertyReader.INGE_PID_SERVICE_USE))) {
          latestVersion.setVersionPid(
              PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_SHORT) + this.pidService.createPid(url).getIdentifier());
        } else {
          latestVersion.setVersionPid(url.toString().replace(PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_URL),
              PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_SHORT)));
        }
      } catch (URISyntaxException | TechnicalException e) {
        logger.error("Error creating PID for item [" + latestVersion.getObjectIdAndVersion() + "]", e);
        throw new IngeTechnicalException("Error creating PID for item [" + latestVersion.getObjectIdAndVersion() + "]", e);
      }

      for (FileDbVO fileDbVO : latestVersion.getFiles()) {
        try {
          if ((FileDbVO.Storage.INTERNAL_MANAGED).equals(fileDbVO.getStorage()) && null == fileDbVO.getPid()) {
            URI url = UriBuilder.getItemComponentLink(latestVersion.getObjectId(), latestVersion.getVersionNumber(), fileDbVO.getObjectId(),
                fileDbVO.getName());
            if ("true".equalsIgnoreCase(PropertyReader.getProperty(PropertyReader.INGE_PID_SERVICE_USE))) {
              fileDbVO.setPid(
                  PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_SHORT) + this.pidService.createPid(url).getIdentifier());
            } else {
              fileDbVO.setPid(url.toString().replace(PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_URL),
                  PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_SHORT)));
            }
          }
        } catch (URISyntaxException e) {
          logger.error("Error creating PID for file [" + fileDbVO.getObjectId() + "] part of the item ["
              + latestVersion.getObjectIdAndVersion() + "]", e);
          throw new IngeTechnicalException("Error creating PID for item [" + latestVersion.getObjectIdAndVersion() + "]", e);
        } catch (TechnicalException e) {
          throw new RuntimeException(e);
        }
      }
    }

    try {
      latestVersion = this.itemRepository.saveAndFlush(latestVersion);
    } catch (DataAccessException e) {
      GenericServiceImpl.handleDBException(e);
    }

    createAuditEntry(latestVersion, auditEventType);

    reindex(latestVersion);
    sendEventTopic(latestVersion, aaMethod);

    return latestVersion;
  }

  private void createAuditEntry (ItemVersionVO pubItem, AuditDbVO.EventType event, String message) throws IngeApplicationException {
    AuditDbVO audit = new AuditDbVO();
    audit.setEvent(event);
    audit.setComment(message);
    audit.setModificationDate(pubItem.getModificationDate());
    audit.setModifier(pubItem.getModifier());
    audit.setPubItem(pubItem);
    try {
      this.auditRepository.saveAndFlush(audit);
    } catch (DataAccessException e) {
      GenericServiceImpl.handleDBException(e);
    }
  }

  private void createAuditEntry(ItemVersionVO pubItem, AuditDbVO.EventType event) throws IngeApplicationException {
   createAuditEntry(pubItem, event, pubItem.getMessage());
  }

  private SearchRetrieveResponseVO<ItemVersionVO> executeSearchSortByVersion(co.elastic.clients.elasticsearch._types.query_dsl.Query query,
      int limit, int offset) throws IngeTechnicalException {

    SearchSortCriteria sortByVersion =
        new SearchSortCriteria(PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID, SearchSortCriteria.SortOrder.DESC);
    SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(query, limit, offset, sortByVersion);
    return this.pubItemDao.search(this.getElasticSearchIndexFields(), srr);
  }

  private SearchRetrieveResponseVO<ItemVersionVO> getAllVersions(String objectId) throws IngeTechnicalException {
    co.elastic.clients.elasticsearch._types.query_dsl.Query latestReleaseQuery =
        TermQuery.of(t -> t.field(PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID).value(objectId))._toQuery();
    SearchRetrieveResponseVO<ItemVersionVO> resp = executeSearchSortByVersion(latestReleaseQuery, -2, 0); // unbegrenzte Suche

    return resp;
  }

  private ValidId getValidId(String id) {
    String pattern = "(item_)(\\d+)(_(\\d+))?$";
    Pattern r = Pattern.compile(pattern);
    Matcher m = r.matcher(id);

    if (!m.find()) {
      String error = "ItemId " + id + " not valid";
      logger.error(error);
      throw new RuntimeException(error);
    }

    return new ValidId(m.group(1) + m.group(2), null != m.group(4) ? Integer.parseInt(m.group(4)) : null);
  }

  private List<FileDbVO> handleFiles(ItemVersionVO newPubItemVO, ItemVersionVO currentPubItemVO, Principal principal, VersionableId itemId)
      throws IngeApplicationException, IngeTechnicalException {

    List<FileDbVO> updatedFileList = new ArrayList<>();

    Map<String, FileDbVO> currentFiles = new HashMap<>();
    if (null != currentPubItemVO) {
      for (FileDbVO file : currentPubItemVO.getFiles()) {
        currentFiles.put(file.getObjectId(), file);
      }
    }

    Date currentDate = new Date();
    for (FileDbVO fileVo : newPubItemVO.getFiles()) {

      FileDbVO currentFileDbVO;

      if (null != fileVo.getObjectId()) {

        // Check if this file exists for the given item id
        String errorMessage =
            "File with id [" + fileVo.getObjectId() + "] does not exist for this item. Please remove identifier to create as new file";

        List<String> items = this.itemRepository.findItemsForFile(fileVo.getObjectId());
        if (null == items || items.isEmpty() || !items.contains(itemId.getObjectId())) {
          throw new IngeApplicationException(errorMessage);
        }
        // Already existing file, just update some fields
        // currentFileDbVO = currentFiles.remove(fileVo.getObjectId());
        currentFileDbVO = this.fileRepository.findById(fileVo.getObjectId()).orElse(null);
        this.setVisibility(currentFileDbVO, fileVo);

      } else {

        // New file or locator
        currentFileDbVO = new FileDbVO();

        if (null == fileVo.getContent() || fileVo.getContent().trim().isEmpty()) {
          throw new IngeApplicationException(
              "A file content has to be provided containing the identifier of the staged file or the url to an external reference.");
        }

        if (null == fileVo.getStorage()) {
          throw new IngeApplicationException(
              "A file storage type has to be provided. Use 'INTERNAL_MANAGED' for uploading from staging area or an URL, use 'EXTERNAL_URL' for an external reference without uploading");
        }

        currentFileDbVO.setObjectId(this.idProviderService.getNewId(IdentifierProviderServiceImpl.ID_PREFIX.FILES));

        // New real file
        if (FileDbVO.Storage.INTERNAL_MANAGED.equals(fileVo.getStorage())) {

          this.setVisibility(currentFileDbVO, fileVo);

          this.fileService.createFileFromStagedFile(fileVo, principal, currentFileDbVO.getObjectId());
          currentFileDbVO.setLocalFileIdentifier(fileVo.getLocalFileIdentifier());
          // TODO Set content to a REST path

          fileVo.setContent("/rest/items/" + itemId.toString() + "/component/" + currentFileDbVO.getObjectId() + "/content");

          currentFileDbVO.setChecksum(fileVo.getChecksum());
          currentFileDbVO.setChecksumAlgorithm(FileDbVO.ChecksumAlgorithm.valueOf(fileVo.getChecksumAlgorithm().name()));
          currentFileDbVO.setSize(fileVo.getSize());
          currentFileDbVO.setMimeType(fileVo.getMimeType());
          currentFileDbVO.setName(fileVo.getName());

          try {
            fileService.generateThumbnail(currentFileDbVO);
          } catch (Exception e) {
            logger.warn("Could not create thumbnail for " + currentFileDbVO.getObjectId(), e);
          }

        }



        currentFileDbVO.setStorage(FileDbVO.Storage.valueOf(fileVo.getStorage().name()));

        // oldFileVo.setChecksumAlgorithm(FileVO.ChecksumAlgorithm.valueOf(newFileVo
        // .getChecksumAlgorithm().name()));

        // Content still links to older version which is ok, because the old version might already
        // been
        currentFileDbVO.setContent(fileVo.getContent());
        currentFileDbVO.setCreationDate(currentDate);
        AccountUserDbRO creator = new AccountUserDbRO();
        creator.setObjectId(principal.getUserAccount().getObjectId());
        // Remove name due to DSGVO
        // creator.setName(principal.getUserAccount().getName());
        currentFileDbVO.setCreator(creator);
        currentFileDbVO.setPid(fileVo.getPid());
      }

      currentFileDbVO.setLastModificationDate(currentDate);
      currentFileDbVO.setMetadata(fileVo.getMetadata());

      currentFileDbVO.setVisibility(FileDbVO.Visibility.valueOf(fileVo.getVisibility().name()));

      // currentFileDbVO.setAllowedAudienceIds(null);
      //
      // if (Visibility.AUDIENCE.equals(fileVo.getVisibility())) {
      // if (fileVo.getAllowedAudienceIds() != null) {
      // for (String audienceId : fileVo.getAllowedAudienceIds()) {
      // if (ipListProvider.get(audienceId) == null) {
      // throw new IngeApplicationException("Audience id " + audienceId + " is unknown");
      // }
      // }
      // }
      // currentFileDbVO.setAllowedAudienceIds(fileVo.getAllowedAudienceIds());
      // }

      updatedFileList.add(currentFileDbVO);
    }

    // TODO
    // Delete files which are left in currentFiles Map if they are not part of an released item

    return updatedFileList;
  }

  private void reindex(ItemRootVO object, boolean immediate, boolean includeFulltext) throws IngeTechnicalException {

    logger.info("Reindexing object " + object.getObjectId());

    ItemVersionVO latestVersion = (ItemVersionVO) object.getLatestVersion();
    // First try to delete the old version from index
    String oldVersion = new VersionableId(latestVersion.getObjectId(), latestVersion.getVersionNumber() - 1).toString();
    this.pubItemDao.delete(oldVersion);

    // Delete all fulltexts of old version from index
    if (includeFulltext) {
      this.pubItemDao.deleteByQuery(TermQuery.of(t -> t.field(INDEX_FULLTEXT_ITEM_ID).value(oldVersion))._toQuery(), 1000);
    }


    logger.info("Reindexing item latest version " + latestVersion.getObjectIdAndVersion());

    if (immediate) {
      this.pubItemDao.createImmediately(latestVersion.getObjectId() + "_" + latestVersion.getVersionNumber(), latestVersion);
    } else {
      this.pubItemDao.create(latestVersion.getObjectId() + "_" + latestVersion.getVersionNumber(), latestVersion);
    }

    if (includeFulltext) {
      this.queueJmsTemplate.convertAndSend("reindex-fulltext", latestVersion);
    }

    if (null != object.getLatestRelease() && object.getLatestRelease().getVersionNumber() != object.getLatestVersion().getVersionNumber()) {
      ItemVersionVO latestRelease = (ItemVersionVO) object.getLatestRelease();
      logger.info("Reindexing item latest release " + latestRelease.getObjectIdAndVersion());
      if (immediate) {
        this.pubItemDao.createImmediately(latestRelease.getObjectId() + "_" + latestRelease.getVersionNumber(), latestRelease);
      } else {
        this.pubItemDao.create(latestRelease.getObjectId() + "_" + latestRelease.getVersionNumber(), latestRelease);
      }
      if (includeFulltext) {
        this.queueJmsTemplate.convertAndSend("reindex-fulltext", latestRelease);
      }
    }

  }

  private void reindex(String objectId, boolean immediate, boolean includeFulltexts) throws IngeTechnicalException {
    de.mpg.mpdl.inge.model.db.valueobjects.ItemRootVO object = this.itemObjectRepository.findById(objectId).orElse(null);
    reindex(object, immediate, includeFulltexts);

  }

  private void reindex(ItemVersionVO item) throws IngeTechnicalException {
    reindex(item.getObject(), true, true);
  }

  private void rollbackSavedFiles(ItemVersionVO pubItemVO) throws IngeTechnicalException {
    for (FileDbVO fileVO : pubItemVO.getFiles()) {
      if ((FileDbVO.Storage.INTERNAL_MANAGED).equals(fileVO.getStorage())) {
        String relativePath = fileVO.getContent();
        try {
          this.fileService.deleteFile(relativePath);
        } catch (IngeTechnicalException e) {
          logger.error("Could not upload staged file [" + relativePath + "]", e);
          throw new IngeTechnicalException("Could not upload staged file [" + relativePath + "]", e);
        }
      }
    }
  }

  private void sendEventTopic(ItemVersionVO item, String method) {
    this.topicJmsTemplate.convertAndSend(item, message -> {
      message.setStringProperty("method", method);
      return message;
    });
  }

  private void setVisibility(FileDbVO currentFileDbVO, FileDbVO fileVo) throws IngeApplicationException {
    currentFileDbVO.setAllowedAudienceIds(null);

    if (FileDbVO.Visibility.AUDIENCE.equals(fileVo.getVisibility())) {
      if (null != fileVo.getAllowedAudienceIds()) {
        for (String audienceId : fileVo.getAllowedAudienceIds()) {
          if (null == this.ipListProvider.get(audienceId)) {
            throw new IngeApplicationException("Audience id " + audienceId + " is unknown");
          }
        }
      }
      currentFileDbVO.setAllowedAudienceIds(fileVo.getAllowedAudienceIds());
    }
  }

  private void updatePubItemWithTechnicalMd(ItemVersionVO latestVersion, String modifierName, String modifierId) {
    Date currentDate = new Date();

    latestVersion.setModificationDate(currentDate);
    de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbRO mod = new de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbRO();
    // Moved out due do DSGVO
    // mod.setName(modifierName);
    mod.setObjectId(modifierId);
    latestVersion.setModifier(mod);
    latestVersion.getObject().setLastModificationDate(currentDate);

  }

  private void validate(ItemVersionVO pubItem) throws IngeTechnicalException, IngeApplicationException {
    ValidationPoint vp = ValidationPoint.STANDARD;

    if (null != pubItem.getObject().getPublicState() && ItemVersionRO.State.PENDING.equals(pubItem.getObject().getPublicState())) {
      vp = ValidationPoint.SAVE;
    }

    validate(pubItem, vp);
  }

  private void validate(ItemVersionVO pubItem, ValidationPoint vp) throws IngeTechnicalException, IngeApplicationException {
    try {
      this.itemValidatingService.validate(pubItem, vp);
    } catch (ValidationException e) {
      throw new IngeApplicationException("Invalid metadata", e);
    } catch (Exception e) {
      throw new IngeTechnicalException(e.getMessage(), e);
    }
  }


  private static class ValidId {
    private final String objectId;
    private final Integer version;

    private ValidId(String _objectId, Integer _version) {
      this.objectId = _objectId;
      this.version = _version;
    }
  }

  @Override
  protected GenericDaoEs<ItemVersionVO> getElasticDao() {
    return this.pubItemDao;
  }


  /*
   * public ItemVersionVO enrichItem(ItemVersionVO item) {
   *
   * if (item != null) { try { entityManager.detach(item); } catch (Exception e) {
   *
   * } if (item.getFiles() != null) { for (FileDbVO file : item.getFiles()) { if
   * (Storage.INTERNAL_MANAGED.equals(file.getStorage())) { file.setContent("/rest/items/" +
   * item.getObjectIdAndVersion() + "/component/" + file.getObjectId() + "/content"); } } } } return
   * item; }
   */


}
