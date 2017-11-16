package de.mpg.mpdl.inge.service.pubman.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import de.mpg.mpdl.inge.db.repository.FileRepository;
import de.mpg.mpdl.inge.db.repository.StagedFileRepository;
import de.mpg.mpdl.inge.filestorage.FileStorageInterface;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.StagedFileDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.FileService;
import de.mpg.mpdl.inge.service.pubman.FileServiceExternal;
import de.mpg.mpdl.inge.service.util.EntityTransformer;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * FileService implementation using the file system to store staged files
 * 
 * @author walter
 * 
 */
@Service
@Primary
public class FileServiceFSImpl implements FileService, FileServiceExternal {
  private final static Logger logger = Logger.getLogger(FileServiceFSImpl.class);

  private final static String TMP_FILE_ROOT_PATH = System.getProperty("jboss.home.dir")
      + PropertyReader.getProperty("inge.logic.temporary_filesystem_root_path");


  @Autowired
  @Qualifier("seaweedFileServiceBean")
  private FileStorageInterface fsi;

  @Autowired
  private FileRepository fr;

  @Autowired
  private StagedFileRepository stagedFileRepository;

  @Autowired
  private AuthorizationService aaService;

  public FileServiceFSImpl() throws IngeTechnicalException {
    Path rootDirectory = Paths.get(TMP_FILE_ROOT_PATH);
    if (Files.notExists(rootDirectory)) {
      logger.info("trying to create directory [ " + rootDirectory.toString() + "]");
      try {
        Files.createDirectories(rootDirectory);
      } catch (IOException e) {
        logger.error("An error occoured, when trying to create directory [" + rootDirectory + "]",
            e);
        throw new IngeTechnicalException("An error occoured, when trying to create directory ["
            + rootDirectory + "]", e);
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
  public void readFile(String fileId, OutputStream out, String authentitationToken)
      throws IngeTechnicalException {

    // TODO auth
    logger.info("readFile Token: " + authentitationToken);
    FileDbVO fileDbVO = fr.findOne(fileId);
    System.out.println(fileDbVO.getLocalFileIdentifier());
    fsi.readFile(fileDbVO.getLocalFileIdentifier(), out);
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
  public StagedFileDbVO createStageFile(InputStream fileInputStream, String fileName,
      String authenticationToken) throws IngeTechnicalException, IngeApplicationException,
      AuthorizationException, AuthenticationException {

    AccountUserVO user = aaService.checkLoginRequired(authenticationToken);
    if (fileName == null || fileName.trim().isEmpty()) {
      throw new IngeTechnicalException("No filename defined.");
    }

    StagedFileDbVO stagedFileVo = new StagedFileDbVO();
    stagedFileVo.setFilename(fileName);
    stagedFileVo = stagedFileRepository.save(stagedFileVo);
    stagedFileVo.setCreatorId(user.getReference().getObjectId());

    try {

      Path tmpFilePath =
          Paths.get(TMP_FILE_ROOT_PATH,
              String.valueOf(stagedFileVo.getId() + "_" + UUID.randomUUID()));
      Files.copy(fileInputStream, tmpFilePath);

      stagedFileVo.setPath(tmpFilePath.toString());
    } catch (IOException e) {
      throw new IngeTechnicalException("Could not write staged file for file [" + fileName + "].",
          e);
    } finally {
      try {
        fileInputStream.close();
      } catch (IOException e) {
        throw new IngeTechnicalException("Error while closing stream", e);
      }
    }

    return stagedFileVo;

  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public String createFileFromStagedFile(int stagedFileId, String fileName,
      AccountUserVO userAccount) throws IngeTechnicalException, IngeApplicationException {



    StagedFileDbVO stagedFileVo = stagedFileRepository.findOne(stagedFileId);

    if (!stagedFileVo.getCreatorId().equals(userAccount.getReference().getObjectId())) {
      throw new IngeTechnicalException("Staged file is read by another user than its creator");

    }

    File stagedFile;
    String relativePath;
    try {
      stagedFile = new File(stagedFileVo.getPath());
      relativePath = fsi.createFile(new FileInputStream(stagedFile), fileName);
    } catch (FileNotFoundException e) {
      String msg =
          "Staged file with path [" + stagedFileVo.getPath() + "] and name ["
              + stagedFileVo.getFilename() + "] does not exist.";
      logger.error(msg);
      throw new IngeTechnicalException(msg);
    }

    deleteStageFile(stagedFileVo);
    return relativePath;


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

    stagedFileRepository.delete(stagedFileVO);
    try {

      Files.deleteIfExists(Paths.get(stagedFileVO.getPath()));
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
  public String getFileMetadata(String componentId, String authenticationToken)
      throws IngeTechnicalException, IngeApplicationException, AuthorizationException,
      AuthenticationException {

    // Auth is covered by readFile method

    final StringBuffer b = new StringBuffer(2048);
    final Metadata metadata = new Metadata();
    final AutoDetectParser parser = new AutoDetectParser();
    final BodyContentHandler handler = new BodyContentHandler();
    ParseContext context = new ParseContext();

    ByteArrayOutputStream fileOutput = new ByteArrayOutputStream();
    try {
      this.readFile(componentId, fileOutput, authenticationToken);
      final TikaInputStream input =
          TikaInputStream.get(new ByteArrayInputStream(fileOutput.toByteArray()));
      parser.parse(input, handler, metadata, context);
      fileOutput.close();
      input.close();
    } catch (IOException | SAXException | TikaException e) {
      logger.error("could not read file [" + componentId + "] for Metadata extraction");
      throw new IngeTechnicalException("could not read file [" + componentId
          + "] for Metadata extraction", e);
    }

    for (final String name : metadata.names()) {
      b.append(name).append(": ").append(metadata.get(name))
          .append(System.getProperty("line.separator"));
    }
    return b.toString();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.service.pubman.FileService#getFileType(java.lang.String)
   */
  @Override
  public String getFileType(String fileId) {
    return fr.findOne(fileId).getMimeType();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.service.pubman.FileService#getFileName(java.lang.String)
   */
  @Override
  public String getFileName(String fileName) {
    return fr.findOne(fileName).getName();
  }

  protected void checkAa(String method, AccountUserVO userAccount, Object... objects)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException {
    if (objects == null) {
      objects = new Object[0];
    }
    objects =
        Stream.concat(Arrays.stream(new Object[] {userAccount}), Arrays.stream(objects)).toArray();
    aaService.checkAuthorization(this.getClass().getCanonicalName(), method, objects);
  }

}
