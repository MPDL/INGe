package de.mpg.mpdl.inge.migration.beans;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.inge.db.repository.ContextRepository;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl.ID_PREFIX;
import de.mpg.mpdl.inge.db.repository.ItemObjectRepository;
import de.mpg.mpdl.inge.db.repository.ItemRepository;
import de.mpg.mpdl.inge.db.repository.OrganizationRepository;
import de.mpg.mpdl.inge.db.repository.UserAccountRepository;
import de.mpg.mpdl.inge.db.repository.UserLoginRepository;
import de.mpg.mpdl.inge.db.repository.YearbookRepository;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO.ChecksumAlgorithm;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO.Storage;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO.Visibility;
import de.mpg.mpdl.inge.model.db.valueobjects.PubItemDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.PubItemObjectDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.PubItemVersionDbVO;
import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.UserAttributeVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.util.AdminHelper;
import de.mpg.mpdl.inge.util.PropertyReader;

@Component
public class Migration {

  static Logger log = Logger.getLogger(Migration.class.getName());

  @Value("${escidoc.url}")
  private String escidocUrl;

  @Value("${db.pwd}")
  private String dbpwd;

  @Value("${contexts.path}")
  private String contextsPath;
  @Value("${items.path}")
  private String itemsPath;
  @Value("${item.path}")
  private String itemPath;
  @Value("${ous.path}")
  private String ousPath;
  @Value("${users.path}")
  private String usersPath;
  @Value("${user.path}")
  private String userPath;

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private ItemObjectRepository itemObjectRepository;

  @Autowired
  private OrganizationRepository orgRepository;

  @Autowired
  private ContextRepository contextRepository;

  @Autowired
  private UserAccountRepository userRepository;

  @Autowired
  private UserLoginRepository userLoginRepository;

  @Autowired
  private IdentifierProviderServiceImpl idProvider;

  @Autowired
  private YearbookRepository yearbookRepository;

  @Autowired
  private Reindexing reIndexing;

  private Queue<AffiliationVO> updateLaterAffs = new LinkedList<AffiliationVO>();

  private HttpClient httpClientWithEscidocCookie;

  public void sayHello(String name) {
    log.info("Starting to migrate " + name);
  }

  public HttpClient setup() {
    String userHandle = AdminHelper.getAdminUserHandle();
    BasicCookieStore cookieStore = new BasicCookieStore();
    BasicClientCookie cookie = new BasicClientCookie("escidocCookie", userHandle);
    cookie.setDomain("qa-coreservice.mpdl.mpg.de");
    cookie.setPath("/");
    cookieStore.addCookie(cookie);
    httpClientWithEscidocCookie = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
    return httpClientWithEscidocCookie;
  }

  public void run(String what) throws Exception {
    switch (what) {
      case "ctx":
        importContexts();
        break;
      case "ctx_reindex":
        reIndexing.reindexContexts();
        break;
      case "ous":
        importAffs();
        break;
      case "ous_reindex":
        reIndexing.reindexOus();
        break;
      case "items":
        importPubItems();
        break;
      case "items_reindex":
        reIndexing.reindexItems();
        break;
      case "users":
        importUsers();
        break;
      case "users_reindex":
        reIndexing.reindexUsers();
        break;
      case "logins":
        importLogins();
        break;
      case "all":
        importAffs();
        importContexts();
        importUsers();
        importLogins();
        importPubItems();
        break;
      default:
        log.info("user doesn't really know, what exactly he wants to do!!!");
    }
  }

  private void importContexts() throws Exception {
    URI uri = new URIBuilder(escidocUrl + contextsPath).addParameter("maximumRecords", "5000").build();
    log.info(uri.toString());
    String contextXml = Request.Get(uri).execute().returnContent().asString(StandardCharsets.UTF_8);

    try {
      SearchRetrieveResponseVO<de.mpg.mpdl.inge.model.valueobjects.ContextVO> contextList =
          XmlTransformingService.transformToSearchRetrieveResponse(contextXml);

      for (SearchRetrieveRecordVO<de.mpg.mpdl.inge.model.valueobjects.ContextVO> rec : contextList.getRecords()) {
        saveContext(rec.getData());
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void saveContext(de.mpg.mpdl.inge.model.valueobjects.ContextVO context) throws Exception {
    try {
      ContextDbVO newVo = transformToNew(context);
      log.info("Saving " + newVo.getObjectId());
      contextRepository.save(newVo);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static ContextDbVO transformToNew(ContextVO contextVo) {
    AccountUserDbRO owner = new AccountUserDbRO();
    AccountUserDbRO modifier = new AccountUserDbRO();

    owner.setObjectId(changeId("user", contextVo.getCreator().getObjectId()));
    owner.setName(contextVo.getCreator().getTitle());

    modifier.setObjectId(changeId("user", contextVo.getModifiedBy().getObjectId()));
    modifier.setName(contextVo.getModifiedBy().getTitle());

    ContextDbVO newContext = new ContextDbVO();
    newContext.setCreator(owner);
    newContext.setCreationDate(contextVo.getCreationDate());
    newContext.setLastModificationDate(contextVo.getLastModificationDate());
    newContext.setModifier(modifier);
    newContext.setDescription(contextVo.getDescription());
    newContext.setName(contextVo.getName());
    newContext.setObjectId(changeId("ctx", contextVo.getReference().getObjectId()));

    newContext.setState(ContextDbVO.State.valueOf(contextVo.getState().name()));

    newContext.setType(contextVo.getType());

    newContext.setAdminDescriptor(contextVo.getAdminDescriptor());

    for (AffiliationRO oldAffRo : contextVo.getResponsibleAffiliations()) {
      AffiliationDbRO newAffRo = new AffiliationDbRO();
      newAffRo.setObjectId(changeId("ou", changeId("ou", oldAffRo.getObjectId())));
      newAffRo.setName(oldAffRo.getTitle());
      newContext.getResponsibleAffiliations().add(newAffRo);
    }

    return newContext;
  }

  private void importAffs() throws Exception {
    URI uri = new URIBuilder(escidocUrl + ousPath).addParameter("query", "\"/id\"=\"e*\" not \"/parents/parent/id\">\"''\"").build();
    log.info(uri.toString());
    String ouXml = Request.Get(uri).execute().returnContent().asString(StandardCharsets.UTF_8);

    try {
      SearchRetrieveResponseVO<AffiliationVO> ouList = XmlTransformingService.transformToSearchRetrieveResponseOrganizationVO(ouXml);

      log.info(ouList.getNumberOfRecords() + " ous were found");
      saveOuList(ouList);

      uri = new URIBuilder(escidocUrl + ousPath).build();
      log.info(uri.toString());
      ouXml = Request.Get(uri).execute().returnContent().asString(StandardCharsets.UTF_8);
      ouList = XmlTransformingService.transformToSearchRetrieveResponseOrganizationVO(ouXml);

      for (SearchRetrieveRecordVO<AffiliationVO> affRecord : ouList.getRecords()) {
        updateOUWithPredecessors(affRecord.getData());
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void importUsers() throws Exception {
    httpClientWithEscidocCookie = setup();
    URI uri = new URIBuilder(escidocUrl + usersPath).addParameter("maximumRecords", String.valueOf(5000))
        .addParameter("startRecord", String.valueOf(1)).build();
    final HttpGet request = new HttpGet(uri);
    HttpResponse response = httpClientWithEscidocCookie.execute(request);
    String xml = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

    SearchRetrieveResponseVO<AccountUserVO> userList = XmlTransformingService.transformToSearchRetrieveResponseAccountUser(xml);
    for (SearchRetrieveRecordVO<AccountUserVO> accountUser : userList.getRecords()) {

      String objectId = accountUser.getData().getReference().getObjectId();
      objectId = objectId.substring(objectId.lastIndexOf("/") + 1, objectId.length());

      uri = new URIBuilder(escidocUrl + userPath + "/" + objectId + "/resources/current-grants")
          .addParameter("maximumRecords", String.valueOf(5000)).addParameter("startRecord", String.valueOf(1)).build();
      final HttpGet requestGrant = new HttpGet(uri);
      HttpResponse responseGrant = httpClientWithEscidocCookie.execute(requestGrant);
      String grantXml = EntityUtils.toString(responseGrant.getEntity(), StandardCharsets.UTF_8);
      List<GrantVO> grantList = XmlTransformingService.transformToGrantVOList(grantXml);

      uri = new URIBuilder(escidocUrl + userPath + "/" + objectId + "/resources/attributes")
          .addParameter("maximumRecords", String.valueOf(5000)).addParameter("startRecord", String.valueOf(1)).build();
      final HttpGet requestAttrs = new HttpGet(uri);
      HttpResponse responseAttrs = httpClientWithEscidocCookie.execute(requestAttrs);
      String attrXml = EntityUtils.toString(responseAttrs.getEntity(), StandardCharsets.UTF_8);
      List<UserAttributeVO> userAttrList = XmlTransformingService.transformToUserAttributesList(attrXml);

      log.info("Saving user" + accountUser.getData().getName() + " - " + accountUser.getData().getReference().getObjectId());
      try {
        userRepository.save(transformToNew(accountUser.getData(), grantList, userAttrList));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void saveOuList(SearchRetrieveResponseVO<AffiliationVO> srr) throws Exception {
    if (srr.getNumberOfRecords() > 0) {
      for (SearchRetrieveRecordVO<AffiliationVO> affRecord : srr.getRecords()) {

        String href = affRecord.getData().getReference().getObjectId();
        String objectId = href.substring(href.lastIndexOf("/") + 1, href.length());

        saveOuWithoutPredecessor(affRecord.getData());

        URI uri = new URIBuilder(escidocUrl + ousPath).addParameter("query", "\"/parents/parent/id\"=\"" + objectId + "\"").build();
        String ouXml = Request.Get(uri).execute().returnContent().asString(StandardCharsets.UTF_8);

        SearchRetrieveResponseVO<AffiliationVO> ouList = XmlTransformingService.transformToSearchRetrieveResponseOrganizationVO(ouXml);
        saveOuList(ouList);

      }
    }
  }

  private void saveOuWithoutPredecessor(AffiliationVO affVo) throws Exception {

    AffiliationDbVO newVo = transformToNew(affVo);
    newVo.getPredecessorAffiliations().clear();
    log.info("Saving " + newVo.getObjectId());
    orgRepository.save(newVo);
    updateLaterAffs.add(affVo);
  }

  private void updateOUWithPredecessors(AffiliationVO affVo) {
    String id = changeId("ou", affVo.getReference().getObjectId());
    if (!affVo.getPredecessorAffiliations().isEmpty()) {
      AffiliationDbVO newVo = orgRepository.findOne(id);
      for (AffiliationRO oldAffRo : affVo.getPredecessorAffiliations()) {
        AffiliationDbRO newAffRo = new AffiliationDbRO();
        newAffRo.setObjectId(changeId("ou", oldAffRo.getObjectId()));
        newAffRo.setName(oldAffRo.getTitle());

        newVo.getPredecessorAffiliations().add(newAffRo);
      }
      log.info("Updating " + newVo.getObjectId());
      orgRepository.save(newVo);
    }
  }

  private void importPubItems() throws Exception {
    String contentModelId = "escidoc:persistent4";

    int limit = 5000;
    int startRecord = 1;
    int allRecords = Integer.MAX_VALUE;

    while (allRecords > startRecord + limit) {
      log.info("Searching from " + startRecord + " to " + (startRecord + limit));
      URI uri = new URIBuilder(escidocUrl + itemsPath).addParameter("query", "\"/properties/content-model/id\"=\"" + contentModelId + "\"")
          .addParameter("maximumRecords", String.valueOf(limit)).addParameter("startRecord", String.valueOf(startRecord)).build();
      final HttpGet request = new HttpGet(uri);
      HttpResponse response = httpClientWithEscidocCookie.execute(request);
      String xml = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

      SearchRetrieveResponseVO<PubItemVO> pubItemList = XmlTransformingService.transformToSearchRetrieveResponse(xml);

      allRecords = pubItemList.getNumberOfRecords();
      startRecord = startRecord + limit;
      log.info("Found " + allRecords + "items.");

      pubItemList.getRecords().parallelStream().forEach(i -> saveAllVersionsOfPubItem(i.getData()));

    }
  }

  private void saveAllVersionsOfPubItem(PubItemVO pubItemVo) {
    int versionNumber = pubItemVo.getLatestVersion().getVersionNumber();

    for (int i = versionNumber; i > 0; i--) {
      try {
        String href = pubItemVo.getVersion().getObjectId() + ":" + i;
        String objectId = href.substring(href.lastIndexOf("/") + 1, href.length());
        log.info("Getting " + objectId);
        URI itemUri = new URIBuilder(escidocUrl + itemPath + "/" + objectId).build();
        final HttpGet requestItem = new HttpGet(itemUri);
        HttpResponse itemResponse = httpClientWithEscidocCookie.execute(requestItem);
        String itemXml = EntityUtils.toString(itemResponse.getEntity(), StandardCharsets.UTF_8);

        PubItemVO item = XmlTransformingService.transformToPubItem(itemXml);

        savePubItem(item);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void savePubItem(PubItemVO pubItem) throws Exception {
    try {
      PubItemVersionDbVO newVo = transformToNew(pubItem);
      log.info("Saving " + newVo.getObjectId() + "_" + newVo.getVersionNumber());
      itemRepository.save(newVo);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static AffiliationDbVO transformToNew(de.mpg.mpdl.inge.model.valueobjects.AffiliationVO affVo) {
    AccountUserDbRO owner = new AccountUserDbRO();
    AccountUserDbRO modifier = new AccountUserDbRO();

    owner.setObjectId(changeId("user", affVo.getCreator().getObjectId()));
    owner.setName(affVo.getCreator().getTitle());
    modifier.setObjectId(changeId("user", affVo.getModifiedBy().getObjectId()));
    modifier.setName(affVo.getModifiedBy().getTitle());

    AffiliationDbVO newAff = new AffiliationDbVO();
    newAff.setCreationDate(affVo.getCreationDate());

    newAff.setCreator(owner);
    newAff.setHasChildren(affVo.getHasChildren());
    newAff.setLastModificationDate(affVo.getLastModificationDate());
    newAff.setMetadata(affVo.getDefaultMetadata());
    newAff.setModifier(modifier);
    newAff.setName(affVo.getDefaultMetadata().getName());
    newAff.setObjectId(changeId("ou", affVo.getReference().getObjectId()));

    for (AffiliationRO oldAffRo : affVo.getPredecessorAffiliations()) {
      AffiliationDbRO newAffRo = new AffiliationDbRO();
      newAffRo.setObjectId(changeId("ou", oldAffRo.getObjectId()));
      newAffRo.setName(oldAffRo.getTitle());
      newAff.getPredecessorAffiliations().add(newAffRo);
    }
    for (AffiliationRO oldAffRo : affVo.getParentAffiliations()) {
      AffiliationDbRO newAffRo = new AffiliationDbRO();
      newAffRo.setObjectId(changeId("ou", oldAffRo.getObjectId()));
      newAffRo.setName(oldAffRo.getTitle());
      newAff.setParentAffiliation(newAffRo);
    }

    newAff.setPublicStatus(AffiliationDbVO.State.valueOf(affVo.getPublicStatus().toUpperCase()));
    return newAff;

  }

  private static PubItemVersionDbVO transformToNew(de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO itemVo) {
    AccountUserDbRO owner = new AccountUserDbRO();
    AccountUserDbRO modifier = new AccountUserDbRO();

    owner.setObjectId(changeId("user", itemVo.getOwner().getObjectId()));
    owner.setName(itemVo.getOwner().getTitle());

    modifier.setObjectId(changeId("user", itemVo.getVersion().getModifiedByRO().getObjectId()));
    modifier.setName(itemVo.getVersion().getModifiedByRO().getTitle());

    PubItemVersionDbVO newPubItem = new PubItemVersionDbVO();
    for (de.mpg.mpdl.inge.model.valueobjects.FileVO oldFile : itemVo.getFiles()) {

      AccountUserDbRO fileOwner = new AccountUserDbRO();

      fileOwner.setObjectId(changeId("user", oldFile.getCreatedByRO().getObjectId()));
      fileOwner.setName(oldFile.getCreatedByRO().getTitle());

      FileDbVO file = new FileDbVO();
      file.setChecksum(oldFile.getChecksum());
      file.setChecksumAlgorithm(ChecksumAlgorithm.valueOf(oldFile.getChecksumAlgorithm().name()));
      file.setContent(oldFile.getContent());
      file.setContentCategory(oldFile.getContentCategory());
      file.setCreationDate(oldFile.getCreationDate());
      file.setCreator(fileOwner);
      file.setDescription(oldFile.getDescription());
      file.setLastModificationDate(oldFile.getLastModificationDate());
      file.setMetadata(oldFile.getDefaultMetadata());
      file.setMimeType(oldFile.getMimeType());
      // file.setModifier(oldFile.getM);
      file.setName(oldFile.getName());
      file.setObjectId(changeId("file", oldFile.getReference().getObjectId()));
      file.setPid(oldFile.getPid());
      file.setStorage(Storage.valueOf(oldFile.getStorage().name()));
      file.setVisibility(Visibility.valueOf(oldFile.getVisibility().name()));

      newPubItem.getFiles().add(file);
    }

    newPubItem.setLastMessage(itemVo.getVersion().getLastMessage());
    newPubItem.setMetadata(itemVo.getMetadata());
    newPubItem.setModificationDate(itemVo.getVersion().getModificationDate());
    newPubItem.setModifiedBy(owner);
    newPubItem.setObjectId(changeId("item", itemVo.getVersion().getObjectId()));
    newPubItem.setState(PubItemVersionDbVO.State.valueOf(itemVo.getVersion().getState().name()));
    newPubItem.setVersionNumber(itemVo.getVersion().getVersionNumber());
    newPubItem.setVersionPid(itemVo.getVersion().getPid());

    PubItemObjectDbVO pubItemObject = new PubItemObjectDbVO();
    newPubItem.setObject(pubItemObject);

    ContextDbRO context = new ContextDbRO();
    context.setObjectId(changeId("ctx", itemVo.getContext().getObjectId()));
    pubItemObject.setContext(context);

    pubItemObject.setCreationDate(itemVo.getCreationDate());
    pubItemObject.setLastModificationDate(itemVo.getLatestVersion().getModificationDate());

    if (itemVo.getLatestRelease() != null) {
      if (itemVo.getLatestRelease().getVersionNumber() == itemVo.getVersion().getVersionNumber()) {
        pubItemObject.setLatestRelease(newPubItem);
      } else if (itemVo.getLatestRelease().getVersionNumber() > itemVo.getVersion().getVersionNumber()) {
        PubItemDbRO latestRelease = new PubItemDbRO();
        latestRelease.setObjectId(changeId(ID_PREFIX.ITEM.getPrefix(), itemVo.getLatestRelease().getObjectId()));
        latestRelease.setVersionNumber(itemVo.getLatestRelease().getVersionNumber());
        pubItemObject.setLatestRelease(latestRelease);
      }
    }

    if (itemVo.getLatestVersion().getVersionNumber() == itemVo.getVersion().getVersionNumber()) {
      pubItemObject.setLatestVersion(newPubItem);
    } else {
      PubItemDbRO latestVersion = new PubItemDbRO();
      latestVersion.setObjectId(changeId(ID_PREFIX.ITEM.getPrefix(), itemVo.getLatestVersion().getObjectId()));
      latestVersion.setVersionNumber(itemVo.getLatestVersion().getVersionNumber());
      pubItemObject.setLatestVersion(latestVersion);
    }

    pubItemObject.setLocalTags(itemVo.getLocalTags());
    pubItemObject.setObjectId(changeId("item", itemVo.getVersion().getObjectId()));
    pubItemObject.setOwner(owner);
    pubItemObject.setPid(itemVo.getPid());
    pubItemObject.setPublicStatus(PubItemVersionDbVO.State.valueOf(itemVo.getPublicStatus().name()));
    pubItemObject.setPublicStatusComment(itemVo.getPublicStatusComment());

    return newPubItem;
  }

  private AccountUserDbVO transformToNew(AccountUserVO oldAccountUserVO, List<GrantVO> grants, List<UserAttributeVO> attributes) {

    AccountUserDbRO owner = new AccountUserDbRO();
    AccountUserDbRO modifier = new AccountUserDbRO();

    owner.setObjectId(changeId("user", oldAccountUserVO.getCreator().getObjectId()));
    owner.setName(oldAccountUserVO.getCreator().getTitle());

    modifier.setObjectId(changeId("user", oldAccountUserVO.getModifiedBy().getObjectId()));
    modifier.setName(oldAccountUserVO.getModifiedBy().getTitle());

    AccountUserDbVO newAccountUser = new AccountUserDbVO();

    newAccountUser.setActive(oldAccountUserVO.isActive());
    if (oldAccountUserVO.getAffiliations() != null && oldAccountUserVO.getAffiliations().size() > 0) {
      AffiliationDbRO affRO = new AffiliationDbRO();
      affRO.setObjectId(changeId("ou", oldAccountUserVO.getAffiliations().get(0).getObjectId()));
      newAccountUser.setAffiliation(affRO);
    }

    newAccountUser.setCreationDate(oldAccountUserVO.getCreationDate());
    newAccountUser.setCreator(owner);
    newAccountUser.setEmail(oldAccountUserVO.getEmail());

    if (grants != null) {

      for (GrantVO grant : grants) {
        grant.setGrantedTo(null);
        grant.setLastModificationDate(null);
        grant.setReference(null);
        if (grant.getObjectRef() != null) {
          if (grant.getObjectRef().contains("context")) {
            grant.setObjectRef(changeId("ctx", grant.getObjectRef()));
          } else if (grant.getObjectRef().contains("organizational-unit")) {
            grant.setObjectRef(changeId("ou", grant.getObjectRef()));
          } else {
            log.info("Unknown grant object: " + grant.getObjectRef());
          }
        }

        if (grant.getRole().contains("depositor")) {
          grant.setRole("DEPOSITOR");
        } else if (grant.getRole().contains("moderator")) {
          grant.setRole("MODERATOR");
        } else if (grant.getRole().contains("system-administrator")) {
          grant.setRole("SYSADMIN");
        } else if (grant.getRole().contains("reporter")) {
          grant.setRole("REPORTER");
        } else if (grant.getRole().contains("cone-open")) {
          grant.setRole("CONE_OPEN_VOCABULARY_EDITOR");
        } else if (grant.getRole().contains("cone-closed")) {
          grant.setRole("CONE_CLOSED_VOCABULARY_EDITOR");
        } else {
          log.info("Unknown role: " + grant.getRole());
        }
      }
    }

    if (attributes != null) {
      for (UserAttributeVO attr : attributes) {

        if (attr.getName().equals("o")) {
          AffiliationDbRO affRO = new AffiliationDbRO();
          affRO.setObjectId(changeId("ou", attr.getValue()));
          newAccountUser.setAffiliation(affRO);
        }

        if (attr.getName().equals("email")) {
          if (attr.getValue().isEmpty()) {
            newAccountUser.setEmail(null);
          } else {
            newAccountUser.setEmail(attr.getValue());
          }
        }
      }
    }

    newAccountUser.setGrantList(grants);
    newAccountUser.setLastModificationDate(oldAccountUserVO.getLastModificationDate());
    newAccountUser.setLoginname(oldAccountUserVO.getUserid());
    newAccountUser.setModifier(modifier);
    newAccountUser.setName(oldAccountUserVO.getName());
    newAccountUser.setObjectId(changeId("user", oldAccountUserVO.getReference().getObjectId()));

    return newAccountUser;
  }

  private void importLogins() throws Exception {

    PGSimpleDataSource dataSource = new PGSimpleDataSource();
    dataSource.setUser("postgres");
    dataSource.setPassword(dbpwd);
    dataSource.setDatabaseName("escidoc-core");
    dataSource.setServerName("srv02.mpdl.mpg.de");
    dataSource.setPortNumber(5432);

    Connection conn = dataSource.getConnection();

    ResultSet res = conn.createStatement().executeQuery("SELECT loginname,password FROM aa.user_account;");

    PasswordEncoder pe = new BCryptPasswordEncoder();

    while (res.next()) {

      log.info("Saving " + res.getString(1));
      try {
        userLoginRepository.insertLogin(res.getString(1), pe.encode(res.getString(2)));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }

  private static String changeId(String prefix, String href) {
    return href.substring(href.lastIndexOf("/") + 1, href.length()).replaceAll("escidoc:", prefix + "_").replaceAll(":", "_");
  }

}
