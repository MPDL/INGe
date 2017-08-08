package de.mpg.mpdl.inge.service.pubman.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.filestorage.FileStorageInterface;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.pubman.FileService;

@Service
@Primary
public class FileServiceFSImpl implements FileService {
  private static Logger logger = Logger.getLogger(FileServiceFSImpl.class);

  @Autowired
  private FileStorageInterface fsi;

  @Override
  public String createFile(InputStream fileInputStream, String fileName)
      throws IngeTechnicalException {
    return fsi.createFile(fileInputStream, fileName);
  }

  @Override
  public void readFile(String filePath, OutputStream out) throws IngeTechnicalException {
    fsi.readFile(filePath, out);
  }

  @Override
  public void deleteFile(String filePath) throws IngeTechnicalException {
    fsi.deleteFile(filePath);
  }

  @Override
  public Path createStageFile(InputStream fileInputStream, String fileName)
      throws IngeTechnicalException {
    String[] fileNameParts = fileName.split("\\.");
    Path tmpFilePath = null;
    if (fileNameParts[0] != null && !("".equals(fileNameParts[0])) && fileNameParts[1] != null
        && !("".equals(fileNameParts[1]))) {
      int fileHashValue = (fileNameParts[0] + System.currentTimeMillis()).hashCode();
      tmpFilePath = Paths.get(TEMP_FILE_PATH + fileHashValue + "." + fileNameParts[1]);
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

  @Override
  public void deleteStageFile(Path path) throws IngeTechnicalException {
    try {
      Files.deleteIfExists(path);
    } catch (IOException e) {
      logger.error("Could not delete staged file [" + path + "]", e);
      throw new IngeTechnicalException("Could not delete staged file", e);
    }
  }

  @Override
  public void indexFile(InputStream fileInputStream) {
    // TODO Auto-generated method stub

  }

}
