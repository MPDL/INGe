package de.mpg.mpdl.inge.filestorage.glusterfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.services.FileStorageInterface;

/**
 * File storage service direct on the file system
 * 
 * @author przibylla (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@Service
public class GlusterFileServiceBean implements FileStorageInterface {

  @Value("${gluster_path}")
  private String glusterRootPath;

  /*
   * 
   * 
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.FileStorageInterface#createFile(java.io.InputStream,
   * java.lang.String)
   */
  @Override
  public String createFile(InputStream fileInputStream, String fileName) throws IOException {
    String newFileName = fileName;
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH) + 1; // plus one cause calendar month starts with 0
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    // Path starting after the the defined filesystemRootPath
    String relativeDirectoryPath = year + "/" + month + "/" + day;
    Path directoryPath =
        FileSystems.getDefault().getPath(glusterRootPath + relativeDirectoryPath);
    if (Files.notExists(directoryPath)) {
      System.out.println("trying to create directory [ " + directoryPath.toString() + "]");
      Files.createDirectories(directoryPath);
    }
    Path filePath = FileSystems.getDefault().getPath(directoryPath + "/" + newFileName);
    if (Files.notExists(filePath)) {
      System.out.println("Trying to copy fileInputStream into new File [" + filePath.toString()
          + "]");
      Files.copy(fileInputStream, filePath);
    } else {
      int i = 1;
      // Split fileName to name and suffix
      String nameOfTheFile = newFileName.substring(0, newFileName.lastIndexOf("."));
      String fileSuffix = newFileName.substring(newFileName.lastIndexOf("."));
      do {
        newFileName = nameOfTheFile + "_" + i + fileSuffix;
        filePath = FileSystems.getDefault().getPath(directoryPath + "/" + newFileName);
        i++;
      } while (Files.exists(filePath));
      System.out.println("Trying to copy fileInputStream into new File [" + filePath.toString()
          + "]");
      Files.copy(fileInputStream, filePath);
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
  public void readFile(String fileRelativePath, OutputStream out) throws IOException {
    Path path = FileSystems.getDefault().getPath(glusterRootPath + fileRelativePath);
    if (Files.exists(path)) {
      Files.copy(path, out);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.FileStorageInterface#deleteFile(java.lang.String)
   */
  @Override
  public void deleteFile(String fileRelativePath) throws Exception {
    System.out.println("Trying to delete File [" + fileRelativePath + "]");
    Path path = FileSystems.getDefault().getPath(glusterRootPath + fileRelativePath);
    if (Files.exists(path)) {
      Files.delete(path);
    }
  }
}
