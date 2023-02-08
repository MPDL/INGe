package de.mpg.mpdl.inge.service.pubman.impl;

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

import jakarta.annotation.PostConstruct;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.log4j.Logger;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import de.mpg.mpdl.inge.db.repository.FileRepository;
import de.mpg.mpdl.inge.db.repository.StagedFileRepository;
import de.mpg.mpdl.inge.filestorage.FileStorageInterface;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO.ChecksumAlgorithm;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.db.valueobjects.StagedFileDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.aa.AuthorizationService.AccessType;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.FileService;
import de.mpg.mpdl.inge.service.pubman.FileServiceExternal;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.util.PropertyReader;
import net.arnx.wmf2svg.util.Base64;

/**
 * FileService implementation using the file system to store staged files
 * 
 * @author walter
 * 
 */
@Service
@Primary
public class FileServiceFSImpl implements FileService, FileServiceExternal {
  private static final Logger logger = Logger.getLogger(FileServiceFSImpl.class);

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

  public FileServiceFSImpl() throws IngeTechnicalException {
    Path rootDirectory = Paths.get(TMP_FILE_ROOT_PATH);
    if (Files.notExists(rootDirectory)) {
      logger.info("trying to create directory [ " + rootDirectory.toString() + "]");
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
    ItemVersionVO item = pubItemService.get(itemId, authenticationToken);

    if (item == null) {
      throw new IngeApplicationException("File with id [" + fileId + "] not found, because itemId [" + itemId + "] not found.");
    }

    FileDbVO selectedFile = null;
    for (FileDbVO file : item.getFiles()) {
      if (file.getObjectId().equals(fileId)) {
        selectedFile = file;
        break;
      }

    }

    FileDbVO fileDbVO = fr.findById(fileId).orElse(null);

    if (selectedFile == null || fileDbVO == null || fileDbVO.getLocalFileIdentifier() == null) {
      throw new IngeApplicationException("File with id [" + fileId + "] not found in item [ " + itemId + "].");
    }
    Principal user = null;
    if (authenticationToken != null) {
      user = aaService.checkLoginRequired(authenticationToken);
    }
    checkAa("readFile", user, selectedFile, item);


    // fsi.readFile(fileDbVO.getLocalFileIdentifier(), out);

    return new FileVOWrapper(fileDbVO.getLocalFileIdentifier(), selectedFile, fsi);
  }

  @Override
  public void deleteFile(String filePath) throws IngeTechnicalException {
    fsi.deleteFile(filePath);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.service.pubman.FileService#createStageFile(java.io.InputStream,
   * java.lang.String)
   */
  @Override
  @Transactional(rollbackFor = Throwable.class)
  public StagedFileDbVO createStageFile(InputStream fileInputStream, String fileName, String authenticationToken)
      throws IngeTechnicalException, IngeApplicationException, AuthorizationException, AuthenticationException {

    Principal user = aaService.checkLoginRequired(authenticationToken);
    if (fileName == null || fileName.trim().isEmpty()) {
      throw new IngeTechnicalException("No filename defined.");
    }

    StagedFileDbVO stagedFileVo = new StagedFileDbVO();
    stagedFileVo.setFilename(fileName);

    stagedFileVo.setCreatorId(user.getUserAccount().getObjectId());

    try {

      Path tmpFilePath = Paths.get(TMP_FILE_ROOT_PATH, String.valueOf(stagedFileVo.getId() + "_" + UUID.randomUUID()));
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

    stagedFileVo = stagedFileRepository.save(stagedFileVo);
    return stagedFileVo;


  }



  @Override
  public void createFileFromStagedFile(FileDbVO fileVO, Principal user) throws IngeTechnicalException, IngeApplicationException {
    createFileFromStagedFile(fileVO, user, null);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void createFileFromStagedFile(FileDbVO fileVO, Principal user, String forcedFileName)
      throws IngeTechnicalException, IngeApplicationException {

    if (fileVO.getContent() == null || fileVO.getContent().trim().isEmpty()) {
      throw new IngeApplicationException("A file content containing the id of the staged file has to be provided");
    }

    StagedFileDbVO stagedFileVo;

    try {

      //if content is an url, download content and create staged file  
      if (fileVO.getContent().startsWith("http")) {
        HttpResponse resp = Request.Get(fileVO.getContent()).execute().returnResponse();
        if (resp.getStatusLine().getStatusCode() != 200) {
          throw new IngeApplicationException(
              "Could not download file from " + fileVO.getContent() + ". Status " + resp.getStatusLine().getStatusCode());
        } else {
          try (InputStream is = resp.getEntity().getContent()) {

            String filename = null;
            //First try to get filename as Content-Disposition header

            Header header = resp.getFirstHeader("Content-Disposition");
            if (header != null) {
              for (HeaderElement e : header.getElements()) {

                try {
                  if (e.getParameterByName("filename*") != null) {
                    String[] utf8filename = e.getParameterByName("filename*").getValue().split("''");
                    filename = URLDecoder.decode(utf8filename[1], utf8filename[0]);
                    break;
                  }
                } catch (Exception e1) {
                  logger.warn("Could not read 'filename*' HTTP Content-Dispositon header from " + fileVO.getContent(), e1);
                }

                if (e.getParameterByName("filename") != null) {
                  filename = e.getParameterByName("filename").getValue();
                }
              }

            }

            //If no header was found, use last part of url as filename
            if (filename == null || filename.trim().isEmpty()) {
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
          stagedFileVo = stagedFileRepository.findById(Integer.parseInt(fileVO.getContent())).orElse(null);
        } catch (Exception e) {
          throw new IngeApplicationException("Given file id " + fileVO.getContent() + " is invalid!");
        }
      }



      if (stagedFileVo == null) {
        throw new IngeApplicationException("No staged file with the given id " + fileVO.getContent() + " was found in the database");
      }

      if (!stagedFileVo.getCreatorId().equals(user.getUserAccount().getObjectId())) {
        throw new IngeApplicationException("Staged file is tried to be read by another user than its creator");

      }



      File stagedFile = new File(stagedFileVo.getPath());
      fileVO.setSize((int) stagedFile.length());

      //Detecting MimeType
      try (FileInputStream stagedFileStream = new FileInputStream(stagedFile)) {
        final Tika tika = new Tika();
        fileVO.setMimeType(tika.detect(stagedFileStream, stagedFileVo.getFilename()));
        stagedFileStream.close();
      } catch (final Exception e) {
        logger.info("Error while trying to detect mimetype of staged file " + stagedFileVo.getId(), e);
      }

      //Uploading file
      try (FileInputStream stagedFileStream = new FileInputStream(stagedFile)) {

        if (forcedFileName == null) {
          forcedFileName = stagedFileVo.getFilename();
        }

        if (!"true".equals(PropertyReader.getProperty(PropertyReader.INGE_REST_DEVELOPMENT_ENABLED))) {
          String relativePath = fsi.createFile(stagedFileStream, forcedFileName);
          fileVO.setLocalFileIdentifier(relativePath);
        } else {
          Request request = Request
              .Post(PropertyReader.getProperty(PropertyReader.INGE_REST_DEVELOPMENT_FILE_URL)
                  + URLEncoder.encode(forcedFileName, StandardCharsets.UTF_8.name()))
              .addHeader("Authorization",
                  "Basic " + Base64.encode((PropertyReader.getProperty(PropertyReader.INGE_REST_DEVELOPMENT_ADMIN_USERNAME) + ":"
                      + PropertyReader.getProperty(PropertyReader.INGE_REST_DEVELOPMENT_ADMIN_PASSWORD)).getBytes()))
              .bodyStream(stagedFileStream);
          Response response = request.execute();
          fileVO.setLocalFileIdentifier(response.returnContent().asString());
        }

      }


      fileVO.setName(stagedFileVo.getFilename());
      fileVO.setChecksumAlgorithm(ChecksumAlgorithm.MD5);
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
  private void deleteStageFile(StagedFileDbVO stagedFileVO) throws IngeTechnicalException {

    logger.info("Trying to delete staged file " + stagedFileVO.getId() + " / Name: " + stagedFileVO.getFilename() + " / Path: "
        + stagedFileVO.getPath());
    try {
      if (Files.exists(Paths.get(stagedFileVO.getPath()))) {
        Files.deleteIfExists(Paths.get(stagedFileVO.getPath()));
        stagedFileRepository.delete(stagedFileVO);
      } else {
        logger.warn("Staged File " + stagedFileVO.getId() + " / Name: " + stagedFileVO.getFilename() + " / Path: " + stagedFileVO.getPath()
            + " does not exist");
      }
    } catch (IOException e) {
      logger.error("Could not delete staged file [" + stagedFileVO.getPath() + "]", e);
      throw new IngeTechnicalException("Could not delete staged file", e);
    }

  }



  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.service.pubman.FileService#indexFile(java.io.InputStream)
   */
  @Override
  public void indexFile(InputStream fileInputStream) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.service.pubman.FileService#getFileMetadata(java.lang.String)
   */
  @Override
  public String getFileMetadata(String itemId, String componentId, String authenticationToken)
      throws IngeTechnicalException, IngeApplicationException, AuthorizationException, AuthenticationException {

    // Auth is covered by readFile method


    final Metadata metadata = new Metadata();

    //    ByteArrayOutputStream fileOutput = new ByteArrayOutputStream();
    //    try {
    //      FileVOWrapper wrapper = this.readFile(itemId, componentId, authenticationToken);
    //      wrapper.readFile(fileOutput);
    //      final TikaInputStream input = TikaInputStream.get(new ByteArrayInputStream(fileOutput.toByteArray()));
    //      final AutoDetectParser parser = new AutoDetectParser();
    //      final BodyContentHandler handler = new BodyContentHandler(-1);
    //      ParseContext context = new ParseContext();
    //      parser.parse(input, handler, metadata, context);
    //      fileOutput.close();
    //      input.close();
    //    } catch (IOException | SAXException | TikaException e) {
    //      logger.error("could not read file [" + componentId + "] for Metadata extraction");
    //      throw new IngeTechnicalException("could not read file [" + componentId + "] for Metadata extraction", e);
    //    } finally {
    //      try {
    //        fileOutput.close();
    //      } catch (IOException e) {
    //        logger.error("Could not close output stream", e);
    //      }
    //    }

    try (ByteArrayOutputStream fileOutput = new ByteArrayOutputStream()) {
      FileVOWrapper wrapper = this.readFile(itemId, componentId, authenticationToken);
      wrapper.readFile(fileOutput);
      try (final TikaInputStream input = TikaInputStream.get(new ByteArrayInputStream(fileOutput.toByteArray()))) {
        final AutoDetectParser parser = new AutoDetectParser();
        final BodyContentHandler handler = new BodyContentHandler(-1);
        ParseContext context = new ParseContext();
        parser.parse(input, handler, metadata, context);
      }
    } catch (IOException | SAXException | TikaException e) {
      logger.error("could not read file [" + componentId + "] for Metadata extraction");
      throw new IngeTechnicalException("could not read file [" + componentId + "] for Metadata extraction", e);
    }

    final StringBuffer b = new StringBuffer(2048);
    for (final String name : metadata.names()) {
      b.append(name).append(": ").append(metadata.get(name)).append(System.getProperty(PropertyReader.LINE_SEPARATOR));
    }
    return b.toString();
  }



  protected void checkAa(String method, Principal userAccount, Object... objects)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    if (objects == null) {
      objects = new Object[0];
    }
    objects = Stream.concat(Arrays.stream(new Object[] {userAccount}), Arrays.stream(objects)).toArray();
    aaService.checkAuthorization(this.getClass().getCanonicalName(), method, objects);
  }


  public boolean checkAccess(AccessType at, Principal principal, ItemVersionVO item, FileDbVO file)
      throws IngeApplicationException, IngeTechnicalException {
    if (pubItemService.checkAccess(AccessType.GET, principal, item)) {
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
      while ((bytesCount = fis.read(byteArray)) != -1) {
        digest.update(byteArray, 0, bytesCount);
      } ;

      // close the stream; We don't need it now.
      fis.close();
    }

    // Get the hash's bytes
    byte[] bytes = digest.digest();

    // This bytes[] has bytes in decimal format;
    // Convert it to hexadecimal format
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < bytes.length; i++) {
      sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
    }

    // return complete hash
    return sb.toString();
  }

  @Scheduled(cron = "${inge.cron.cleanup_staging_files}")
  @PostConstruct
  public void deleteOldStagingFiles() {

    Date old = Date.from(ZonedDateTime.now().minusHours(6).toInstant());
    logger.info("CRON: Deleting unused staging files since " + old);
    List<StagedFileDbVO> fileList = stagedFileRepository.findByCreationDateBefore(old);
    for (StagedFileDbVO stagedFile : fileList) {

      try {
        deleteStageFile(stagedFile);
      } catch (IngeTechnicalException e) {
        logger.error("Error deleting stage file" + e);
      }
    }


  }

}
