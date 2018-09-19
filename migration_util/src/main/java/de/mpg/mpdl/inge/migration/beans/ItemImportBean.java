package de.mpg.mpdl.inge.migration.beans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.inge.db.repository.ItemObjectRepository;
import de.mpg.mpdl.inge.db.repository.ItemRepository;
import de.mpg.mpdl.inge.db.repository.UserAccountRepository;
import de.mpg.mpdl.inge.db.repository.AuditRepository;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl.ID_PREFIX;
import de.mpg.mpdl.inge.filestorage.filesystem.FileSystemServiceBean;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.AuditDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemRootVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.db.valueobjects.VersionableId;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO.ChecksumAlgorithm;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO.Storage;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO.Visibility;
import de.mpg.mpdl.inge.model.valueobjects.EventLogEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.VersionHistoryEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.FileService;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.pubman.impl.GenericServiceImpl;
import de.mpg.mpdl.inge.service.util.PubItemUtil;
import de.mpg.mpdl.inge.util.ResourceUtil;

@Component
public class ItemImportBean {

  static Logger log = Logger.getLogger(Migration.class.getName());

  @Value("${escidoc.url}")
  private String escidocUrl;
  @Value("${items.path}")
  private String itemsPath;
  @Value("${item.path}")
  private String itemPath;
  @Value("${maximum.records}")
  private int limit;
  @Autowired
  private ItemRepository itemRepository;
  @Autowired
  private AuditRepository auditRepository;
  @Autowired
  private ItemObjectRepository itemObjectRepository;
  @Autowired
  private UserAccountRepository userRepository;
  @Autowired
  private FileSystemServiceBean fssb;
  @Autowired
  private FileService fs;
  @Autowired
  private MigrationUtilBean utils;
  @Autowired
  private OrganizationService organizationService;


  public void importPubItems() throws Exception {

    String contentModelId = "escidoc:persistent4";
    HttpClient client = utils.setup();

    int startRecord = 1;
    int allRecords = Integer.MAX_VALUE;

    while (allRecords > startRecord + limit) {
      log.info("Searching from " + startRecord + " to " + (startRecord + limit));
      URI uri = new URIBuilder(escidocUrl + itemsPath).addParameter("query", "\"/properties/content-model/id\"=\"" + contentModelId + "\"")
          .addParameter("maximumRecords", String.valueOf(limit)).addParameter("startRecord", String.valueOf(startRecord)).build();
      final HttpGet request = new HttpGet(uri);
      HttpResponse response = client.execute(request);
      String xml = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

      SearchRetrieveResponseVO<PubItemVO> pubItemList = XmlTransformingService.transformToSearchRetrieveResponse(xml);

      allRecords = pubItemList.getNumberOfRecords();
      startRecord = startRecord + limit;
      log.info("Found " + allRecords + "items.");

      pubItemList.getRecords().parallelStream().forEach(i -> saveAllVersionsOfPubItem(i.getData()));

    }
  }

  private void saveAllVersionsOfPubItem(PubItemVO pubItemVo) {
    HttpClient client = null;
    try {
      client = utils.setup();
    } catch (URISyntaxException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    int versionNumber = pubItemVo.getLatestVersion().getVersionNumber();
    Map<String, String[]> fileMap = new HashMap<String, String[]>();

    for (int i = 1; i <= versionNumber; i++) {
      try {
        String href = pubItemVo.getVersion().getObjectId() + ":" + i;
        String objectId = href.substring(href.lastIndexOf("/") + 1, href.length());
        log.info("Getting " + objectId);
        URI itemUri = new URIBuilder(escidocUrl + itemPath + "/" + objectId).build();
        final HttpGet requestItem = new HttpGet(itemUri);
        HttpResponse itemResponse = client.execute(requestItem);
        String itemXml = EntityUtils.toString(itemResponse.getEntity(), StandardCharsets.UTF_8);
        PubItemVO item = XmlTransformingService.transformToPubItem(itemXml);
        savePubItem(item, fileMap);
      } catch (Exception e) {
        log.error("FAILED Getting " + pubItemVo.getVersion().getObjectIdAndVersion(), e);
      }
    }
    try {
      URI versionHistoryUri =
          new URIBuilder(escidocUrl + itemPath + "/" + pubItemVo.getVersion().getObjectId() + "/resources/version-history").build();
      final HttpGet versionHistoryRequest = new HttpGet(versionHistoryUri);
      HttpResponse versionHistoryResponse = client.execute(versionHistoryRequest);
      String versionHostoryXml = EntityUtils.toString(versionHistoryResponse.getEntity(), StandardCharsets.UTF_8);
      List<VersionHistoryEntryVO> versionHistory = XmlTransformingService.transformToEventVOList(versionHostoryXml);
      log.info("Starting to generate audit log 4 " + pubItemVo.getVersion().getObjectId());
      saveAuditLog(versionHistory, pubItemVo.getVersion().getObjectId());
    } catch (Exception e) {
      log.error("FAILED logging audit records 4 " + pubItemVo.getVersion().getObjectId(), e);
    }
  }

  private void savePubItem(PubItemVO pubItem, Map<String, String[]> fileMap) {
    ItemVersionVO newVo = null;
    try {
      newVo = transformToNew(pubItem, fileMap);
      log.info("Saving " + newVo.getObjectId() + "_" + newVo.getVersionNumber());
      itemRepository.save(newVo);
    } catch (Exception e) {
      log.info("FAILED Saving " + newVo.getObjectId() + "_" + newVo.getVersionNumber());
      e.printStackTrace();
    }
  }

  private ItemVersionVO transformToNew(de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO itemVo, Map<String, String[]> fileMap)
      throws Exception {
    AccountUserDbRO owner = new AccountUserDbRO();
    AccountUserDbRO modifier = new AccountUserDbRO();

    owner.setObjectId(utils.changeId("user", itemVo.getOwner().getObjectId()));
    /*
     * if (itemVo.getOwner().getTitle().length() > 255) {
     * owner.setName(itemVo.getOwner().getTitle().substring(0, 254)); } else {
     * owner.setName(itemVo.getOwner().getTitle()); }
     */

    modifier.setObjectId(utils.changeId("user", itemVo.getVersion().getModifiedByRO().getObjectId()));
    // modifier.setName(itemVo.getVersion().getModifiedByRO().getTitle());

    ItemVersionVO newPubItem = new ItemVersionVO();
    String currentFileId = "none";

    for (de.mpg.mpdl.inge.model.valueobjects.FileVO oldFile : itemVo.getFiles()) {
      currentFileId = oldFile.getReference().getObjectId();
      currentFileId = currentFileId.substring(currentFileId.lastIndexOf(":") + 1);
      log.info("processing file with id " + currentFileId);

      AccountUserDbRO fileOwner = new AccountUserDbRO();
      MdsFileVO metadata = oldFile.getDefaultMetadata();
      String contentCategory = oldFile.getContentCategory().substring(oldFile.getContentCategory().lastIndexOf("/") + 1);
      metadata.setContentCategory(contentCategory);

      fileOwner.setObjectId(utils.changeId("user", oldFile.getCreatedByRO().getObjectId()));
      // fileOwner.setName(oldFile.getCreatedByRO().getTitle());

      FileDbVO file = new FileDbVO();
      file.setChecksum(oldFile.getChecksum());
      if (oldFile.getChecksumAlgorithm() != null) {
        file.setChecksumAlgorithm(ChecksumAlgorithm.valueOf(oldFile.getChecksumAlgorithm().name()));
      }
      file.setCreationDate(oldFile.getCreationDate());
      file.setCreator(fileOwner);
      file.setLastModificationDate(oldFile.getLastModificationDate());
      file.setMetadata(metadata);
      file.setMimeType(oldFile.getMimeType());
      file.setSize(oldFile.getDefaultMetadata().getSize());
      if (FileVO.Storage.INTERNAL_MANAGED.equals(oldFile.getStorage())) {
        file.setName(oldFile.getName().replace("/", "_"));
      } else {
        file.setName(oldFile.getName());
      }
      file.setObjectId(utils.changeId("file", oldFile.getReference().getObjectId()));
      file.setPid(oldFile.getPid());
      file.setStorage(Storage.valueOf(oldFile.getStorage().name()));
      file.setVisibility(Visibility.valueOf(oldFile.getVisibility().name()));
      if (file.getVisibility().equals(FileDbVO.Visibility.AUDIENCE)) {
        Path path;
        ArrayList<String> audienceIds = new ArrayList<String>();
        try
        // path = Paths.get(getClass().getClassLoader().getResource("Kontext_MPI-ID.txt").toURI());
        (InputStream file_content = ResourceUtil.getResourceAsStream("Kontext_MPI-ID.txt", getClass().getClassLoader())) {
          BufferedReader reader = new BufferedReader(new InputStreamReader(file_content, "UTF-8"));
          Stream<String> lines = reader.lines();

          String ctx_id = itemVo.getContext().getObjectId().substring(itemVo.getContext().getObjectId().lastIndexOf("/") + 1);

          lines.filter(line -> line.startsWith(ctx_id)).map(line -> line.split(", ")[1]).forEach(id -> audienceIds.add(id));
          file.setAllowedAudienceIds(audienceIds);
        } catch (IOException e) {
          log.error("pech", e);
        }
      }

      if (fileMap.containsKey(currentFileId)) {
        log.info("no need for upload, using existing localId " + fileMap.get(currentFileId));
        file.setLocalFileIdentifier(fileMap.get(currentFileId)[0]);
        if (oldFile.getStorage().equals(FileVO.Storage.INTERNAL_MANAGED)) {
          String old_content = oldFile.getContent();
          String[] pieces = old_content.split("/");
          String new_content = "/rest/items/" + pieces[3].replaceAll("escidoc:", "item_") + "_" + fileMap.get(currentFileId)[1]
              + "/component/" + pieces[6].replaceAll("escidoc:", "file_") + "/content";
          file.setContent(new_content);
        } else {
          file.setContent(oldFile.getContent());
        }
      } else {
        if (oldFile.getStorage().equals(FileVO.Storage.INTERNAL_MANAGED)) {
          String localId;
          try {
            localId = upload(oldFile.getContent(), oldFile.getName().replace("/", "_"));
            file.setLocalFileIdentifier(localId);
            String[] values = {localId, Integer.toString(itemVo.getVersion().getVersionNumber())};
            fileMap.put(currentFileId, values);
            if (oldFile.getStorage().equals(FileVO.Storage.INTERNAL_MANAGED)) {
              String old_content = oldFile.getContent();
              String[] pieces = old_content.split("/");
              String new_content = "/rest/items/" + pieces[3].replaceAll("escidoc:", "item_") + "_" + fileMap.get(currentFileId)[1]
                  + "/component/" + pieces[6].replaceAll("escidoc:", "file_") + "/content";
              file.setContent(new_content);
            } else {
              file.setContent(oldFile.getContent());
            }

          } catch (Exception e) {
            log.error("ERROR uploading" + oldFile.getContent(), e);
          }
        }
      }
      newPubItem.getFiles().add(file);
    }

    newPubItem.setMessage(itemVo.getVersion().getLastMessage());
    MdsPublicationVO itemMetaData = itemVo.getMetadata();
    itemMetaData = MetadataCleanup.purge(itemMetaData);
    newPubItem.setMetadata(itemMetaData);
    newPubItem.setModificationDate(itemVo.getVersion().getModificationDate());
    newPubItem.setModifier(modifier);
    newPubItem.setObjectId(utils.changeId("item", itemVo.getVersion().getObjectId()));
    newPubItem.setVersionState(ItemVersionVO.State.valueOf(itemVo.getVersion().getState().name()));
    newPubItem.setVersionNumber(itemVo.getVersion().getVersionNumber());
    newPubItem.setVersionPid(itemVo.getVersion().getPid());

    ItemRootVO pubItemObject = new ItemRootVO();
    newPubItem.setObject(pubItemObject);

    ContextDbRO context = new ContextDbRO();
    context.setObjectId(utils.changeId("ctx", itemVo.getContext().getObjectId()));
    pubItemObject.setContext(context);

    pubItemObject.setCreationDate(itemVo.getCreationDate());
    pubItemObject.setLastModificationDate(itemVo.getLatestVersion().getModificationDate());

    if (itemVo.getLatestRelease() != null && itemVo.getLatestRelease().getVersionNumber() != 0) {
      if (itemVo.getLatestRelease().getVersionNumber() == itemVo.getVersion().getVersionNumber()) {
        pubItemObject.setLatestRelease(newPubItem);
      } else if (itemVo.getLatestRelease().getVersionNumber() < itemVo.getVersion().getVersionNumber()) {
        ItemVersionRO latestRelease = new ItemVersionRO();
        latestRelease.setObjectId(utils.changeId(ID_PREFIX.ITEM.getPrefix(), itemVo.getLatestRelease().getObjectId()));
        latestRelease.setVersionNumber(itemVo.getLatestRelease().getVersionNumber());
        pubItemObject.setLatestRelease(latestRelease);
      }
    }

    if (itemVo.getLatestVersion().getVersionNumber() == itemVo.getVersion().getVersionNumber()) {
      pubItemObject.setLatestVersion(newPubItem);
    } /*
       * else { ItemVersionRO latestVersion = new ItemVersionRO();
       * latestVersion.setObjectId(utils.changeId(ID_PREFIX.ITEM.getPrefix(),
       * itemVo.getLatestVersion().getObjectId()));
       * latestVersion.setVersionNumber(itemVo.getLatestVersion().getVersionNumber());
       * pubItemObject.setLatestVersion(latestVersion); }
       */

    pubItemObject.setLocalTags(itemVo.getLocalTags());
    pubItemObject.setObjectId(utils.changeId("item", itemVo.getVersion().getObjectId()));
    pubItemObject.setCreator(owner);
    pubItemObject.setObjectPid(itemVo.getPid());
    pubItemObject.setPublicState(ItemVersionVO.State.valueOf(itemVo.getPublicStatus().name()));
    try {
      PubItemUtil.setOrganizationIdPathInItem(newPubItem, organizationService);
    } catch (Exception e) {
      log.error("ERROR creating OU path");
    }

    return newPubItem;
  }

  private String upload(String contentUri, String fileName) throws Exception {

    HttpClient client = utils.setup();

    URI uri = new URIBuilder(escidocUrl + contentUri).build();
    final HttpGet request = new HttpGet(uri);
    HttpResponse response = client.execute(request);

    String fileId = fssb.createFile(response.getEntity().getContent(), fileName);
    return fileId;
  }

  public void importSinglePubItem(String id) {

    PubItemVO theItem = null;

    try {
      HttpClient client = utils.setup();

      URI uri = new URIBuilder(escidocUrl + itemPath + "/" + id).build();
      final HttpGet request = new HttpGet(uri);
      HttpResponse response = client.execute(request);
      String xml = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
      log.info("Getting " + id);

      theItem = XmlTransformingService.transformToPubItem(xml);
      saveAllVersionsOfPubItem(theItem);
    } catch (Exception e) {
      log.error("FAILED Getting " + id, e);
    }

  }

  private void saveAuditLog(List<VersionHistoryEntryVO> versionHistory, String objectId) {

    versionHistory.forEach(eventList -> {

      ItemVersionVO item =
          itemRepository.findOne(new VersionableId(objectId.replace("escidoc:", "item_"), eventList.getReference().getVersionNumber()));
      eventList.getEvents().forEach(event -> {

        if (event.getType() != null && !event.getType().equals(EventLogEntryVO.EventType.ASSIGN_VERSION_PID)) {
          AuditDbVO audit = new AuditDbVO();
          audit.setComment(event.getComment());
          audit.setModificationDate(event.getDate());
          audit.setModifier(item.getModifier());
          audit.setPubItem(item);

          switch (event.getType()) {
            case CREATE: {
              audit.setEvent(AuditDbVO.EventType.CREATE);
              break;
            }
            case RELEASE: {
              audit.setEvent(AuditDbVO.EventType.RELEASE);
              break;
            }
            case SUBMIT: {
              audit.setEvent(AuditDbVO.EventType.SUBMIT);
              break;
            }
            case IN_REVISION: {
              audit.setEvent(AuditDbVO.EventType.REVISE);
              break;
            }
            case UPDATE: {
              audit.setEvent(AuditDbVO.EventType.UPDATE);
              break;
            }
            case WITHDRAW: {
              audit.setEvent(AuditDbVO.EventType.WITHDRAW);
              break;
            }
            default:
              break;
          }
          auditRepository.saveAndFlush(audit);
        }
      });
    });
  }
}
