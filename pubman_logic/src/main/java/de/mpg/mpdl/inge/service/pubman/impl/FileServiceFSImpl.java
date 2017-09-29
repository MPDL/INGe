package de.mpg.mpdl.inge.service.pubman.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import de.mpg.mpdl.inge.db.repository.FileRepository;
import de.mpg.mpdl.inge.filestorage.FileStorageInterface;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.pubman.FileService;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * FileService implementation using the file system to store staged files
 * 
 * @author walter
 * 
 */
@Service
@Primary
public class FileServiceFSImpl implements FileService {
  private final static Logger logger = Logger.getLogger(FileServiceFSImpl.class);

  private final static String TMP_FILE_ROOT_PATH = PropertyReader
      .getProperty("inge.logic.temporary_filesystem_root_path");


  @Autowired
  private FileStorageInterface fsi;

  @Autowired
  private FileRepository fr;

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.filestorage.FileStorageInterface#createFile(java.io.InputStream,
   * java.lang.String)
   */
  @Override
  public String createFile(InputStream fileInputStream, String fileName)
      throws IngeTechnicalException {
    return fsi.createFile(fileInputStream, fileName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.filestorage.FileStorageInterface#readFile(java.lang.String,
   * java.io.OutputStream)
   */
  @Override
  public void readFile(String fileId, OutputStream out) throws IngeTechnicalException {
    FileDbVO fileDbVO = fr.findOne(fileId);
    fsi.readFile(fileDbVO.getContent(), out);
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
  public Path createStageFile(InputStream fileInputStream, String fileName)
      throws IngeTechnicalException {
    String[] fileNameParts = fileName.split("\\.");
    Path tmpFilePath = null;
    if (fileNameParts[0] != null && !("".equals(fileNameParts[0])) && fileNameParts[1] != null
        && !("".equals(fileNameParts[1]))) {
      int fileHashValue = (fileNameParts[0] + System.currentTimeMillis()).hashCode();
      tmpFilePath = Paths.get(TMP_FILE_ROOT_PATH + fileHashValue + "." + fileNameParts[1]);
    }

    try {
      Files.copy(fileInputStream, tmpFilePath);
    } catch (IOException e) {
      logger.error("Could not write staged file [" + tmpFilePath + "] for file [" + fileName + "]",
          e);
      throw new IngeTechnicalException("Could not write staged file [" + tmpFilePath
          + "] for file [" + fileName + "]", e);
    }
    return tmpFilePath;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.service.pubman.FileService#readStageFile(java.nio.file.Path)
   */
  @Override
  public InputStream readStageFile(Path stagedFilePath) throws IngeTechnicalException {
    InputStream in = null;
    try {
      in = Files.newInputStream(stagedFilePath);
    } catch (IOException e) {
      logger.error("Could not read staged file [" + stagedFilePath.toString() + "]", e);
      throw new IngeTechnicalException("Could not read staged file [" + stagedFilePath.toString()
          + "]", e);
    }
    return in;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.service.pubman.FileService#deleteStageFile(java.nio.file.Path)
   */
  @Override
  public void deleteStageFile(Path path) throws IngeTechnicalException {
    try {
      Files.deleteIfExists(path);
    } catch (IOException e) {
      logger.error("Could not delete staged file [" + path + "]", e);
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
  public String getFileMetadata(String componentId) {

    final StringBuffer b = new StringBuffer(2048);
    final Metadata metadata = new Metadata();
    final AutoDetectParser parser = new AutoDetectParser();
    final BodyContentHandler handler = new BodyContentHandler();
    ParseContext context = new ParseContext();

    ByteArrayOutputStream fileOutput = new ByteArrayOutputStream();
    try {
      this.readFile(componentId, fileOutput);
      final TikaInputStream input =
          TikaInputStream.get(new ByteArrayInputStream(fileOutput.toByteArray()));
      parser.parse(input, handler, metadata, context);
      fileOutput.close();
      input.close();
    } catch (IngeTechnicalException | IOException | SAXException | TikaException e) {
      logger.error("could not read file [" + componentId + "] for Metadata extraction");
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

}
