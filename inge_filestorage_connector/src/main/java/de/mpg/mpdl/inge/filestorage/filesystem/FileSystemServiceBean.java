package de.mpg.mpdl.inge.filestorage.filesystem;

import de.mpg.mpdl.inge.filestorage.FileStorageInterface;
import de.mpg.mpdl.inge.filestorage.Range;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.util.PropertyReader;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Calendar;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

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

  private static final Logger logger = LogManager.getLogger(FileSystemServiceBean.class);

  private static final String FILESYSTEM_ROOT_PATH =
      System.getProperty(PropertyReader.JBOSS_HOME_DIR) + PropertyReader.getProperty(PropertyReader.INGE_FILESTORAGE_FILESYSTEM_PATH);

  /*
   *
   *
   * (non-Javadoc)
   *
   * @see de.mpg.mpdl.inge.services.FileStorageInterface#createFile(java.io. InputStream,
   * java.lang.String)
   */

  @Override
  public String createFile(InputStream fileInputStream, String fileId) throws IngeTechnicalException {

    Path fileIdPath = Path.of(fileId);
    String newFileName = fileIdPath.getFileName().toString();

    //if fileName contains a relative path, use it. Else create a path with a date
    Path relativeDirectoryPath = fileIdPath.getParent();
    if (relativeDirectoryPath == null) {
      Calendar calendar = Calendar.getInstance();
      int year = calendar.get(Calendar.YEAR);
      int month = calendar.get(Calendar.MONTH) + 1; // plus one cause calendar month starts with 0
      int day = calendar.get(Calendar.DAY_OF_MONTH);
      // Path starting after the the defined filesystemRootPath
      relativeDirectoryPath = Path.of(String.valueOf(year), String.valueOf(month), String.valueOf(day));
    }


    Path directoryPath = FileSystems.getDefault().getPath(FILESYSTEM_ROOT_PATH, relativeDirectoryPath.toString());
    Path filePath = FileSystems.getDefault().getPath(directoryPath + "/" + newFileName);
    try {

      if (Files.notExists(directoryPath)) {
        //        System.out.println("trying to create directory [ " + directoryPath.toString() + "]");
        Files.createDirectories(directoryPath);
      }

      if (Files.notExists(filePath)) {
        //        System.out.println("Trying to copy fileInputStream into new File [" + filePath.toString() + "]");
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
          if (null != fileSuffix) {
            newFileName = nameOfTheFile + "_" + i + fileSuffix;
          } else {
            newFileName = nameOfTheFile + "_" + i;
          }
          filePath = FileSystems.getDefault().getPath(directoryPath + "/" + newFileName);
          i++;
        } while (Files.exists(filePath));
        //        System.out.println("Trying to copy fileInputStream into new File [" + filePath.toString() + "]");
        Files.copy(fileInputStream, filePath);
      }
    } catch (IOException e) {
      logger.error("An error occoured, when trying to create file [" + fileIdPath + "]", e);
      throw new IngeTechnicalException("An error occoured, when trying to create file [" + fileIdPath + "]", e);
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
    this.readFile(fileRelativePath, out, null);
  }

  @Override
  public void readFile(String fileRelativePath, OutputStream out, Range range) throws IngeTechnicalException {

    try {
      if (!"true".equals(PropertyReader.getProperty(PropertyReader.INGE_REST_DEVELOPMENT_ENABLED))) {
        Path filepath = FileSystems.getDefault().getPath(FILESYSTEM_ROOT_PATH + fileRelativePath);
        if (Files.exists(filepath)) {
          readFileFromFileSystem(filepath, out, range);
        } else {
          logger.error("Path " + fileRelativePath + " does not exist");
        }
      } else {
        InputStream is = readInputStreamFromDevRestSystem(fileRelativePath, range);
        IOUtils.copy(is, out);
      }
    } catch (Exception e) {
      logger.error("An error occoured, when trying to retrieve file [" + fileRelativePath + "]", e);
      throw new IngeTechnicalException("An error occoured, when trying to retrieve file[" + fileRelativePath + "]", e);
    }

  }

  @Override
  public InputStream readFile(String fileRelativePath) throws IngeTechnicalException {
    try {
      if (!"true".equals(PropertyReader.getProperty(PropertyReader.INGE_REST_DEVELOPMENT_ENABLED))) {
        Path filepath = FileSystems.getDefault().getPath(FILESYSTEM_ROOT_PATH + fileRelativePath);
        if (Files.exists(filepath)) {
          return Files.newInputStream(filepath);
        } else {
          logger.error("Path " + fileRelativePath + " does not exist");
        }
      } else {
        return readInputStreamFromDevRestSystem(fileRelativePath, null);
        //readFileFromDevRestSystem(fileRelativePath, out, range);
      }
    } catch (Exception e) {
      logger.error("An error occoured, when trying to retrieve file [" + fileRelativePath + "]", e);
      throw new IngeTechnicalException("An error occoured, when trying to retrieve file[" + fileRelativePath + "]", e);
    }
    return null;

  }

  private void readFileFromFileSystem(Path filepath, OutputStream output, Range range) throws IOException {
    Long length = Files.size(filepath);

    if (range == null) {
      range = new Range(0, length - 1, length);
    }
    try (InputStream input = new BufferedInputStream(Files.newInputStream(filepath))) {
      Range.copy(input, output, length, range.getStart(), range.getLength());
    }

  }

  private InputStream readInputStreamFromDevRestSystem(String fileRelativePath, Range range) throws IOException {
    Request request = Request
        .Get(PropertyReader.getProperty(PropertyReader.INGE_REST_DEVELOPMENT_FILE_URL)
            + fileRelativePath.substring(0, fileRelativePath.lastIndexOf("/") + 1)
            + (URLEncoder.encode(fileRelativePath.substring(fileRelativePath.lastIndexOf("/") + 1), StandardCharsets.UTF_8)))
        .addHeader("Authorization",
            "Basic " + Base64.getEncoder().encodeToString((PropertyReader.getProperty(PropertyReader.INGE_REST_DEVELOPMENT_ADMIN_USERNAME)
                + ":" + PropertyReader.getProperty(PropertyReader.INGE_REST_DEVELOPMENT_ADMIN_PASSWORD)).getBytes()));
    if (range != null) {
      request.addHeader("Range", "bytes=" + range.getStart() + "-" + range.getEnd());
    }
    Response response = request.execute();
    return response.returnContent().asStream();
  }



  /*
   * (non-Javadoc)
   *
   * @see de.mpg.mpdl.inge.services.FileStorageInterface#deleteFile(java.lang.String)
   */
  @Override
  public void deleteFile(String fileRelativePath) throws IngeTechnicalException {
    //    System.out.println("Trying to delete File [" + fileRelativePath + "]");
    Path path = FileSystems.getDefault().getPath(FILESYSTEM_ROOT_PATH + fileRelativePath);
    try {
      if (Files.exists(path)) {
        Files.delete(path);
      }
    } catch (IOException e) {
      logger.error("An error occoured, when trying to delete the file [" + path + "]", e);
      throw new IngeTechnicalException("An error occoured, when trying to delete the file [" + path + "]", e);
    }
  }

  @Override
  public boolean fileExists(String filePath) throws IngeTechnicalException {
    Path path = FileSystems.getDefault().getPath(FILESYSTEM_ROOT_PATH + filePath);
    return Files.exists(path);
  }
}
