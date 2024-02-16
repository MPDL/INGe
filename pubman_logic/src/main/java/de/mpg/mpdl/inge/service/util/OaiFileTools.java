package de.mpg.mpdl.inge.service.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.util.PropertyReader;

public class OaiFileTools {

  private static final Logger logger = Logger.getLogger(OaiFileTools.class);

  private static final String OAI_FILESYSTEM_ROOT_PATH =
      System.getProperty(PropertyReader.JBOSS_HOME_DIR) + PropertyReader.getProperty(PropertyReader.INGE_FILESTORAGE_OAI_FILESYSTEM_PATH);

  public static void createFile(InputStream fileInputStream, String fileName) throws IngeTechnicalException {
    logger.info("OAI: Trying to create File [" + fileName + "]");
    Path directoryPath = Paths.get(OAI_FILESYSTEM_ROOT_PATH);
    if (Files.notExists(directoryPath)) {
      logger.info("OAI: trying to create directory [ " + directoryPath + "]");
      try {
        Files.createDirectories(directoryPath);
      } catch (IOException e) {
        logger.error("OAI: An error occoured, when trying to create directory [" + directoryPath + "]", e);
        throw new IngeTechnicalException("OAI: An error occoured, when trying to create directory [" + directoryPath + "]", e);
      }
    }

    Path filePath = FileSystems.getDefault().getPath(directoryPath + "/" + fileName);
    try {
      CopyOption[] options = new CopyOption[] {StandardCopyOption.REPLACE_EXISTING,};
      Files.copy(fileInputStream, filePath, options);
      logger.info("OAI: File [" + filePath + "] created.");
    } catch (IOException e) {
      logger.error("OAI: An error occoured, when trying to create file [" + filePath + "]", e);
      throw new IngeTechnicalException("OAI: An error occoured, when trying to create file [" + filePath + "]", e);
    }

  }

  public static void deleteFile(String fileName) throws IngeTechnicalException {
    logger.info("OAI: Trying to delete File [" + fileName + "]");
    Path filePath = FileSystems.getDefault().getPath(OAI_FILESYSTEM_ROOT_PATH + fileName);
    try {
      if (Files.exists(filePath)) {
        Files.delete(filePath);
        logger.info("OAI: File [" + filePath + "] deleted.");
      } else {
        logger.info("OAI: File [" + filePath + "] does not exist.");
      }
    } catch (IOException e) {
      logger.error("OAI: An error occoured, when trying to delete the file [" + filePath + "]", e);
      throw new IngeTechnicalException("OAI: An error occoured, when trying to delete the file [" + filePath + "]", e);
    }
  }
}
