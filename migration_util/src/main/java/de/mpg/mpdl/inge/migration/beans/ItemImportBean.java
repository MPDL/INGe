package de.mpg.mpdl.inge.migration.beans;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.inge.db.repository.ItemObjectRepository;
import de.mpg.mpdl.inge.db.repository.ItemRepository;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl.ID_PREFIX;
import de.mpg.mpdl.inge.filestorage.filesystem.FileSystemServiceBean;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemRootVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO.ChecksumAlgorithm;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO.Storage;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO.Visibility;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;

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
  private ItemObjectRepository itemObjectRepository;
  @Autowired
  private FileSystemServiceBean fssb;
  @Autowired
  private MigrationUtilBean utils;


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
    Map<String, String> fileMap = new HashMap<String, String>();

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
        log.error("ERROR " + pubItemVo.getVersion().getObjectIdAndVersion(), e);
      }
    }
  }

  private void savePubItem(PubItemVO pubItem, Map<String, String> fileMap) throws Exception {
    try {
      ItemVersionVO newVo = transformToNew(pubItem, fileMap);
      log.info("Saving " + newVo.getObjectId() + "_" + newVo.getVersionNumber());
      itemRepository.save(newVo);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private ItemVersionVO transformToNew(de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO itemVo, Map<String, String> fileMap) {
    AccountUserDbRO owner = new AccountUserDbRO();
    AccountUserDbRO modifier = new AccountUserDbRO();

    owner.setObjectId(utils.changeId("user", itemVo.getOwner().getObjectId()));
    owner.setName(itemVo.getOwner().getTitle());

    modifier.setObjectId(utils.changeId("user", itemVo.getVersion().getModifiedByRO().getObjectId()));
    modifier.setName(itemVo.getVersion().getModifiedByRO().getTitle());

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
      fileOwner.setName(oldFile.getCreatedByRO().getTitle());

      FileDbVO file = new FileDbVO();
      file.setChecksum(oldFile.getChecksum());
      file.setChecksumAlgorithm(ChecksumAlgorithm.valueOf(oldFile.getChecksumAlgorithm().name()));
      file.setContent(oldFile.getContent());
      file.setCreationDate(oldFile.getCreationDate());
      file.setCreator(fileOwner);
      file.setLastModificationDate(oldFile.getLastModificationDate());
      file.setMetadata(metadata);
      file.setMimeType(oldFile.getMimeType());
      file.setSize(oldFile.getDefaultMetadata().getSize());
      file.setName(oldFile.getName());
      file.setObjectId(utils.changeId("file", oldFile.getReference().getObjectId()));
      file.setPid(oldFile.getPid());
      file.setStorage(Storage.valueOf(oldFile.getStorage().name()));
      file.setVisibility(Visibility.valueOf(oldFile.getVisibility().name()));

      if (fileMap.containsKey(currentFileId)) {
        log.info("no need for upload, using existing localId " + fileMap.get(currentFileId));
        file.setLocalFileIdentifier(fileMap.get(currentFileId));
      } else {
        if (oldFile.getStorage().equals(FileVO.Storage.INTERNAL_MANAGED)) {
          String localId;
          try {
            localId = upload(oldFile.getContent(), oldFile.getName());
            file.setLocalFileIdentifier(localId);
            fileMap.put(currentFileId, localId);

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
    newPubItem.setModifier(owner);
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

    if (itemVo.getLatestRelease() != null) {
      if (itemVo.getLatestRelease().getVersionNumber() == itemVo.getVersion().getVersionNumber()) {
        pubItemObject.setLatestRelease(newPubItem);
      } /*else if (itemVo.getLatestRelease().getVersionNumber() > itemVo.getVersion().getVersionNumber()) {
        ItemVersionRO latestRelease = new ItemVersionRO();
        latestRelease.setObjectId(utils.changeId(ID_PREFIX.ITEM.getPrefix(), itemVo.getLatestRelease().getObjectId()));
        latestRelease.setVersionNumber(itemVo.getLatestRelease().getVersionNumber());
        pubItemObject.setLatestRelease(latestRelease);
        }*/
    }

    if (itemVo.getLatestVersion().getVersionNumber() == itemVo.getVersion().getVersionNumber()) {
      pubItemObject.setLatestVersion(newPubItem);
    } /*else {
      ItemVersionRO latestVersion = new ItemVersionRO();
      latestVersion.setObjectId(utils.changeId(ID_PREFIX.ITEM.getPrefix(), itemVo.getLatestVersion().getObjectId()));
      latestVersion.setVersionNumber(itemVo.getLatestVersion().getVersionNumber());
      pubItemObject.setLatestVersion(latestVersion);
      }*/

    pubItemObject.setLocalTags(itemVo.getLocalTags());
    pubItemObject.setObjectId(utils.changeId("item", itemVo.getVersion().getObjectId()));
    pubItemObject.setCreator(owner);
    pubItemObject.setObjectPid(itemVo.getPid());
    pubItemObject.setPublicState(ItemVersionVO.State.valueOf(itemVo.getPublicStatus().name()));

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

  public void importSinglePubItem(String id) throws Exception {

    HttpClient client = utils.setup();

    URI uri = new URIBuilder(escidocUrl + itemPath + "/" + id).build();
    final HttpGet request = new HttpGet(uri);
    HttpResponse response = client.execute(request);
    String xml = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

    PubItemVO theItem = XmlTransformingService.transformToPubItem(xml);
    saveAllVersionsOfPubItem(theItem);

  }

}
