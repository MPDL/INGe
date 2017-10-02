package de.mpg.mpdl.inge.filestorage.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.filestorage.FileStorageInterface;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * File storage service direct on the file system
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@Service
@Primary
public class FileSystemServiceBean implements FileStorageInterface {

  private static Logger logger = Logger.getLogger(FileSystemServiceBean.class);

  private final static String FILESYSTEM_ROOT_PATH =
      PropertyReader.getProperty("inge.filestorage.filesystem_path");

  /*
   * 
   * 
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.FileStorageInterface#createFile(java.io. InputStream,
   * java.lang.String)
   */
  @Override
  public String createFile(InputStream fileInputStream, String fileName)
      throws IngeTechnicalException {
    String newFileName = fileName;
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH) + 1; // plus one cause calendar month starts with 0
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    // Path starting after the the defined filesystemRootPath
    String relativeDirectoryPath = year + "/" + month + "/" + day;
    Path directoryPath =
        FileSystems.getDefault().getPath(FILESYSTEM_ROOT_PATH + relativeDirectoryPath);
    Path filePath = FileSystems.getDefault().getPath(directoryPath + "/" + newFileName);
    try {

      if (Files.notExists(directoryPath)) {
        System.out.println("trying to create directory [ " + directoryPath.toString() + "]");
        Files.createDirectories(directoryPath);
      }

      if (Files.notExists(filePath)) {
        System.out
            .println("Trying to copy fileInputStream into new File [" + filePath.toString() + "]");
        Files.copy(fileInputStream, filePath);
      } else {
        int i = 1;
        // Split fileName to name and suffix
        String nameOfTheFile = null;
        String fileSuffix = null;
        if (newFileName.contains(".")) {
          nameOfTheFile = newFileName.substring(0, newFileName.lastIndexOf("."));
          fileSuffix = newFileName.substring(newFileName.lastIndexOf("."));
        } else {
          nameOfTheFile = newFileName;
        }

        do {
          if (fileSuffix != null) {
            newFileName = nameOfTheFile + "_" + i + fileSuffix;
          } else {
            newFileName = nameOfTheFile + "_" + i;
          }
          filePath = FileSystems.getDefault().getPath(directoryPath + "/" + newFileName);
          i++;
        } while (Files.exists(filePath));
        System.out
            .println("Trying to copy fileInputStream into new File [" + filePath.toString() + "]");
        Files.copy(fileInputStream, filePath);
      }
    } catch (IOException e) {
      logger.error("An error occoured, when trying to create file [" + fileName + "]", e);
      throw new IngeTechnicalException(
          "An error occoured, when trying to create file [" + fileName + "]", e);
    }
    return relativeDirectoryPath + "/" + newFileName;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.FileStorageInterface#readFile(java.lang.String,
   * java.io.OutputStream)
   */
  @Override
  public void readFile(String fileRelativePath, OutputStream out) throws IngeTechnicalException {
    Path path = FileSystems.getDefault().getPath(FILESYSTEM_ROOT_PATH + fileRelativePath);
    try {
      if (Files.exists(path)) {
        Files.copy(path, out);
      }
    } catch (IOException e) {
      logger.error("An error occoured, when trying to retrieve file [" + path.toString() + "]", e);
      throw new IngeTechnicalException(
          "An error occoured, when trying to retrieve file[" + path.toString() + "]", e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.FileStorageInterface#deleteFile(java.lang.String)
   */
  @Override
  public void deleteFile(String fileRelativePath) throws IngeTechnicalException {
    System.out.println("Trying to delete File [" + fileRelativePath + "]");
    Path path = FileSystems.getDefault().getPath(FILESYSTEM_ROOT_PATH + fileRelativePath);
    try {
      if (Files.exists(path)) {
        Files.delete(path);
      }
    } catch (IOException e) {
      logger.error("An error occoured, when trying to delete the file [" + path.toString() + "]",
          e);
      throw new IngeTechnicalException(
          "An error occoured, when trying to delete the file [" + path.toString() + "]", e);
    }
  }
}
