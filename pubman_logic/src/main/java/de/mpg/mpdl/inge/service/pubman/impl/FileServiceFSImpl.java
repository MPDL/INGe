package de.mpg.mpdl.inge.service.pubman.impl;

import de.mpg.mpdl.inge.db.repository.FileRepository;
import de.mpg.mpdl.inge.db.repository.StagedFileRepository;
import de.mpg.mpdl.inge.filestorage.FileStorageInterface;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.db.valueobjects.StagedFileDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.FileService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.util.ThumbnailCreationService;
import de.mpg.mpdl.inge.util.PropertyReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import net.arnx.wmf2svg.util.Base64;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

/**
 * FileService implementation using the file system to store staged files
 *
 * @author walter
 *
 */
@Service
@Primary
public class FileServiceFSImpl implements FileService {
  private static final Logger logger = LogManager.getLogger(FileServiceFSImpl.class);

  private static final String TMP_FILE_ROOT_PATH = System.getProperty(PropertyReader.JBOSS_HOME_DIR)
      + PropertyReader.getProperty(PropertyReader.INGE_LOGIC_TEMPORARY_FILESYSTEM_ROOT_PATH);

  @Autowired
  //@Qualifier("postgresDbFileServiceBean")
  @Qualifier("fileSystemServiceBean")
  private FileStorageInterface fsi;

  @Autowired
  private FileRepository fr;

  @Autowired
  private StagedFileRepository stagedFileRepository;

  @Autowired
  private AuthorizationService aaService;

  @Autowired
  private PubItemService pubItemService;

  @Autowired
  private ThumbnailCreationService thumbnailCreationService;

  @Autowired
  @Qualifier("queueJmsTemplate")
  private JmsTemplate queueJmsTemplate;

  public FileServiceFSImpl() throws IngeTechnicalException {
    Path rootDirectory = Paths.get(TMP_FILE_ROOT_PATH);
    if (Files.notExists(rootDirectory)) {
      logger.info("trying to create directory [ " + rootDirectory + "]");
      try {
        Files.createDirectories(rootDirectory);
      } catch (IOException e) {
        logger.error("An error occoured, when trying to create directory [" + rootDirectory + "]", e);
        throw new IngeTechnicalException("An error occoured, when trying to create directory [" + rootDirectory + "]", e);
      }
    }
  }


  /*
   * (non-Javadoc)
   *
   * @see de.mpg.mpdl.inge.filestorage.FileStorageInterface#readFile(java.lang.String,
   * java.io.OutputStream)
   */
  @Override
  @Transactional(readOnly = true)
  public FileVOWrapper readFile(String itemId, String fileId, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    logger.info("Trying to read file " + fileId + " in item " + itemId + " with authenticationToken " + authenticationToken);
    // Item-based aa covered by this method
    ItemVersionVO item = this.pubItemService.get(itemId, authenticationToken);

    if (null == item) {
      throw new IngeApplicationException("File with id [" + fileId + "] not found, because itemId [" + itemId + "] not found.");
    }

    FileDbVO selectedFile = null;
    for (FileDbVO file : item.getFiles()) {
      if (file.getObjectId().equals(fileId)) {
        selectedFile = file;
        break;
      }

    }

    FileDbVO fileDbVO = this.fr.findById(fileId).orElse(null);

    if (null == selectedFile || null == fileDbVO || null == fileDbVO.getLocalFileIdentifier()) {
      throw new IngeApplicationException("File with id [" + fileId + "] not found in item [ " + itemId + "].");
    }
    Principal user = null;
    if (null != authenticationToken) {
      user = this.aaService.checkLoginRequired(authenticationToken);
    }
    checkAa("readFile", user, selectedFile, item);

    FileVOWrapper wrapper = new FileVOWrapper(fileDbVO.getLocalFileIdentifier(), selectedFile, this.fsi);

    //thumbnail
    String thumbnailIdentifier = ThumbnailCreationService.createThumbnailFileIdentifier(fileDbVO.getLocalFileIdentifier());
    if (fsi.fileExists(thumbnailIdentifier)) {
      wrapper.setThumbnailFileId(thumbnailIdentifier);
    }
    return wrapper;
  }


  @Override
  public void deleteFile(String filePath) throws IngeTechnicalException {
    this.fsi.deleteFile(filePath);
    this.fsi.deleteFile(ThumbnailCreationService.createThumbnailFileIdentifier(filePath));
  }

  /*
   * (non-Javadoc)
   *
   * @see de.mpg.mpdl.inge.service.pubman.FileService#createStageFile(java.io.InputStream,
   * java.lang.String)
   */
  @Override
  //  @Transactional(rollbackFor = Throwable.class)
  public StagedFileDbVO createStageFile(InputStream fileInputStream, String fileName, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException {

    Principal user = this.aaService.checkLoginRequired(authenticationToken);
    if (null == fileName || fileName.trim().isEmpty()) {
      throw new IngeTechnicalException("No filename defined.");
    }

    StagedFileDbVO stagedFileVo = new StagedFileDbVO();
    stagedFileVo.setFilename(fileName);

    stagedFileVo.setCreatorId(user.getUserAccount().getObjectId());

    try {

      Path tmpFilePath = Paths.get(TMP_FILE_ROOT_PATH, stagedFileVo.getId() + "_" + UUID.randomUUID());
      Files.copy(fileInputStream, tmpFilePath);

      stagedFileVo.setPath(tmpFilePath.toString());
    } catch (IOException e) {
      throw new IngeTechnicalException("Could not write staged file for file [" + fileName + "].", e);
    } finally {
      try {
        fileInputStream.close();
      } catch (IOException e) {
        throw new IngeTechnicalException("Error while closing stream", e);
      }
    }

    stagedFileVo = this.stagedFileRepository.save(stagedFileVo);
    return stagedFileVo;


  }



  //  @Override
  //  public void createFileFromStagedFile(FileDbVO fileVO, Principal user) throws IngeTechnicalException, IngeApplicationException {
  //    createFileFromStagedFile(fileVO, user, null);
  //  }

  @Override
//  @Transactional(rollbackFor = Throwable.class)
  public void createFileFromStagedFile(FileDbVO fileVO, Principal user, String forcedFileName)
      throws IngeTechnicalException, IngeApplicationException {

    if (null == fileVO.getContent() || fileVO.getContent().trim().isEmpty()) {
      throw new IngeApplicationException("A file content containing the id of the staged file has to be provided");
    }

    StagedFileDbVO stagedFileVo;

    try {

      //if content is an url, download content and create staged file
      if (fileVO.getContent().startsWith("http")) {
        HttpResponse resp = Request.Get(fileVO.getContent()).execute().returnResponse();
        if (200 != resp.getStatusLine().getStatusCode()) {
          throw new IngeApplicationException(
              "Could not download file from " + fileVO.getContent() + ". Status " + resp.getStatusLine().getStatusCode());
        } else {
          try (InputStream is = resp.getEntity().getContent()) {

            String filename = null;
            //First try to get filename as Content-Disposition header

            Header header = resp.getFirstHeader("Content-Disposition");
            if (null != header) {
              for (HeaderElement e : header.getElements()) {

                try {
                  if (null != e.getParameterByName("filename*")) {
                    String[] utf8filename = e.getParameterByName("filename*").getValue().split("''");
                    filename = URLDecoder.decode(utf8filename[1], utf8filename[0]);
                    break;
                  }
                } catch (Exception e1) {
                  logger.warn("Could not read 'filename*' HTTP Content-Dispositon header from " + fileVO.getContent(), e1);
                }

                if (null != e.getParameterByName("filename")) {
                  filename = e.getParameterByName("filename").getValue();
                }
              }

            }

            //If no header was found, use last part of url as filename
            if (null == filename || filename.trim().isEmpty()) {
              String[] parts = fileVO.getContent().split("/");
              filename = parts[parts.length - 1];
            }


            stagedFileVo = createStageFile(is, filename, user.getJwToken());
          }
        }
      }
      //else get staged file from database
      else {
        try {
          stagedFileVo = this.stagedFileRepository.findById(Integer.parseInt(fileVO.getContent())).orElse(null);
        } catch (Exception e) {
          throw new IngeApplicationException("Given file id " + fileVO.getContent() + " is invalid!");
        }
      }



      if (null == stagedFileVo) {
        throw new IngeApplicationException("No staged file with the given id " + fileVO.getContent() + " was found in the database");
      }

      if (!stagedFileVo.getCreatorId().equals(user.getUserAccount().getObjectId())) {
        throw new IngeApplicationException("Staged file is tried to be read by another user than its creator");

      }



      File stagedFile = new File(stagedFileVo.getPath());
      fileVO.setSize((int) stagedFile.length());

      //Detecting MimeType
      try (FileInputStream stagedFileStream = new FileInputStream(stagedFile)) {
        Tika tika = new Tika();
        fileVO.setMimeType(tika.detect(stagedFileStream, stagedFileVo.getFilename()));
      } catch (Exception e) {
        logger.info("Error while trying to detect mimetype of staged file " + stagedFileVo.getId(), e);
      }

      //Uploading file
      try (FileInputStream stagedFileStream = new FileInputStream(stagedFile)) {

        if (null == forcedFileName) {
          forcedFileName = stagedFileVo.getFilename();
        }

        if (!"true".equals(PropertyReader.getProperty(PropertyReader.INGE_REST_DEVELOPMENT_ENABLED))) {
          String relativePath = this.fsi.createFile(stagedFileStream, forcedFileName);
          fileVO.setLocalFileIdentifier(relativePath);
        } else {
          Request request = Request
              .Post(PropertyReader.getProperty(PropertyReader.INGE_REST_DEVELOPMENT_FILE_URL)
                  + URLEncoder.encode(forcedFileName, StandardCharsets.UTF_8))
              .addHeader("Authorization",
                  "Basic " + Base64.encode((PropertyReader.getProperty(PropertyReader.INGE_REST_DEVELOPMENT_ADMIN_USERNAME) + ":"
                      + PropertyReader.getProperty(PropertyReader.INGE_REST_DEVELOPMENT_ADMIN_PASSWORD)).getBytes()))
              .bodyStream(stagedFileStream);
          Response response = request.execute();
          fileVO.setLocalFileIdentifier(response.returnContent().asString());
        }

      }


      fileVO.setName(stagedFileVo.getFilename());
      fileVO.setChecksumAlgorithm(FileDbVO.ChecksumAlgorithm.MD5);
      fileVO.setChecksum(getFileChecksum(MessageDigest.getInstance("MD5"), stagedFile));

    } catch (FileNotFoundException e) {
      String msg = "Staged file does not exist.";
      logger.error(msg);
      throw new IngeTechnicalException(msg, e);
    } catch (Exception e) {

      logger.error("Error while creating file", e);
      throw new IngeTechnicalException("Error while creating file", e);
    }


    deleteStageFile(stagedFileVo);


    /*
     *
     *
     * String[] fileNameParts = fileName.split("\\."); String hashedFileName = null; Path
     * tmpFilePath = null; if (fileNameParts.length > 1) { if (fileNameParts[0] != null &&
     * !("".equals(fileNameParts[0])) && fileNameParts[1] != null && !("".equals(fileNameParts[1])))
     * { int fileHashValue = (fileNameParts[0] + System.currentTimeMillis()).hashCode();
     * hashedFileName = fileHashValue + "." + fileNameParts[1]; tmpFilePath =
     * Paths.get(TMP_FILE_ROOT_PATH + hashedFileName); } } else if (fileNameParts.length == 1 &&
     * fileNameParts[0] != null && !("".equals(fileNameParts[0]))) { int fileHashValue =
     * (fileNameParts[0] + System.currentTimeMillis()).hashCode(); tmpFilePath =
     * Paths.get(TMP_FILE_ROOT_PATH + fileHashValue); } else { throw new
     * IngeTechnicalException("Could not write staged file [" + tmpFilePath + "] for file [" +
     * fileName + "]. No filename defined"); }
     *
     *
     *
     * try { Files.copy(fileInputStream, tmpFilePath); } catch (IOException e) {
     * logger.error("Could not write staged file [" + tmpFilePath + "] for file [" + fileName + "]",
     * e); throw new IngeTechnicalException("Could not write staged file [" + tmpFilePath +
     * "] for file [" + fileName + "]", e); }
     */


  }


  @Transactional(rollbackFor = Throwable.class)
  protected void deleteStageFile(StagedFileDbVO stagedFileVO) throws IngeTechnicalException {

    logger.info("Trying to delete staged file " + stagedFileVO.getId() + " / Name: " + stagedFileVO.getFilename() + " / Path: "
        + stagedFileVO.getPath());
    try {
      if (Files.exists(Paths.get(stagedFileVO.getPath()))) {
        Files.deleteIfExists(Paths.get(stagedFileVO.getPath()));
        this.stagedFileRepository.delete(stagedFileVO);
      } else {
        logger.warn("Staged File " + stagedFileVO.getId() + " / Name: " + stagedFileVO.getFilename() + " / Path: " + stagedFileVO.getPath()
            + " does not exist");
      }
    } catch (IOException e) {
      logger.error("Could not delete staged file [" + stagedFileVO.getPath() + "]", e);
      throw new IngeTechnicalException("Could not delete staged file", e);
    }

  }

  @Transactional(readOnly = true, rollbackFor = Throwable.class)
  public void regenerateThumbnails(String token) throws IngeTechnicalException, AuthenticationException {
    aaService.checkLoginRequiredWithRole(token, GrantVO.PredefinedRoles.SYSADMIN.frameworkValue());
    List<FileDbVO> allFiles = fr.findAll();
    for (FileDbVO fileDbVO : allFiles) {
      this.queueJmsTemplate.convertAndSend("generate-thumbnail", fileDbVO);
    }
  }

  @Override
  @JmsListener(containerFactory = "queueContainerFactory", destination = "generate-thumbnail")
  public void generateThumbnail(FileDbVO fileDbVO) throws IngeTechnicalException, AuthenticationException {
    try {
      //FileDbVO fileDbVO = (FileDbVO) msg.getObject();
      thumbnailCreationService.createThumbnail(fileDbVO);
    } catch (Exception e) {
      throw new IngeTechnicalException("Could not create thumbnail", e);
    }
  }


  //  /*
  //   * (non-Javadoc)
  //   *
  //   * @see de.mpg.mpdl.inge.service.pubman.FileService#indexFile(java.io.InputStream)
  //   */
  //  @Override
  //  public void indexFile(InputStream fileInputStream) {}

  /*
   * (non-Javadoc)
   *
   * @see de.mpg.mpdl.inge.service.pubman.FileService#getFileMetadata(java.lang.String)
   */
  @Override
  public String getFileMetadata(String itemId, String componentId, String authenticationToken)
      throws IngeTechnicalException, IngeApplicationException, AuthorizationException, AuthenticationException {

    // Auth is covered by readFile method


    Metadata metadata = new Metadata();

    try (ByteArrayOutputStream fileOutput = new ByteArrayOutputStream()) {
      FileVOWrapper wrapper = this.readFile(itemId, componentId, authenticationToken);
      wrapper.readFile(fileOutput);
      try (TikaInputStream input = TikaInputStream.get(new ByteArrayInputStream(fileOutput.toByteArray()))) {
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler(-1);
        ParseContext context = new ParseContext();
        parser.parse(input, handler, metadata, context);
      }
    } catch (IOException | SAXException | TikaException e) {
      logger.error("could not read file [" + componentId + "] for Metadata extraction");
      throw new IngeTechnicalException("could not read file [" + componentId + "] for Metadata extraction", e);
    }

    StringBuilder b = new StringBuilder(2048);
    for (String name : metadata.names()) {
      b.append(name).append(": ").append(metadata.get(name)).append(System.getProperty(PropertyReader.LINE_SEPARATOR));
    }
    return b.toString();
  }



  protected void checkAa(String method, Principal userAccount, Object... objects)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    if (null == objects) {
      objects = new Object[0];
    }
    objects = Stream.concat(Arrays.stream(new Object[] {userAccount}), Arrays.stream(objects)).toArray();
    this.aaService.checkAuthorization(this.getClass().getCanonicalName(), method, objects);
  }


  public boolean checkAccess(AuthorizationService.AccessType at, Principal principal, ItemVersionVO item, FileDbVO file)
      throws IngeApplicationException, IngeTechnicalException {
    if (this.pubItemService.checkAccess(AuthorizationService.AccessType.GET, principal, item)) {
      try {
        checkAa(at.getMethodName(), principal, file, item);
      } catch (AuthenticationException | AuthorizationException e) {
        return false;
      } catch (IngeTechnicalException | IngeApplicationException e) {
        throw e;
      } catch (Exception e) {
        throw new IngeTechnicalException("", e);
      }
      return true;
    }
    return false;
  }

  private static String getFileChecksum(MessageDigest digest, File file) throws IOException {
    // Get file input stream for reading the file content

    try (FileInputStream fis = new FileInputStream(file)) {

      // Create byte array to read data in chunks
      byte[] byteArray = new byte[1024];
      int bytesCount = 0;

      // Read file data and update in message digest
      while (-1 != (bytesCount = fis.read(byteArray))) {
        digest.update(byteArray, 0, bytesCount);
      }

      // close the stream; We don't need it now.
    }

    // Get the hash's bytes
    byte[] bytes = digest.digest();

    // This bytes[] has bytes in decimal format;
    // Convert it to hexadecimal format
    StringBuilder sb = new StringBuilder();
    for (byte aByte : bytes) {
      sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
    }

    // return complete hash
    return sb.toString();
  }

  @Scheduled(cron = "${inge.cron.cleanup_staging_files}")
  public void deleteOldStagingFiles() {

    Date criticalDate = Date.from(ZonedDateTime.now()
        .minusHours(Integer.parseInt(PropertyReader.getProperty(PropertyReader.INGE_CRON_CLEANUP_STAGING_FILES_HOURS))).toInstant());
    logger.info("*** CRON (" + PropertyReader.getProperty(PropertyReader.INGE_CRON_CLEANUP_STAGING_FILES)
        + "): deleteOldStagingFiles() since " + criticalDate);

    List<StagedFileDbVO> fileList = this.stagedFileRepository.findByCreationDateBefore(criticalDate);
    for (StagedFileDbVO stagedFile : fileList) {
      try {
        deleteStageFile(stagedFile);
      } catch (IngeTechnicalException e) {
        logger.error("*** CRON: Error deleteOldStagingFiles() " + e);
      }
    }

    logger.info("*** CRON: deleteOldStagingFiles() finished.");
  }


}
