package de.mpg.mpdl.inge.seaweedfs;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.mpdl.inge.filestorage.FileStorageConnectorConfiguration;
import de.mpg.mpdl.inge.filestorage.filesystem.FileSystemServiceBean;
import de.mpg.mpdl.inge.filestorage.seaweedfs.SeaweedFileServiceBean;

/**
 * Unit test FileServiceBean
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ContextConfiguration(classes = FileStorageConnectorConfiguration.class)
public class FileServiceTests {
  
  private static Logger logger = Logger.getLogger(FileServiceTests.class);

  public static final String[] FILE_NAMES = {"test100k.db", "test1Mb.db"};
  public static final String FILE2_NAME = "test1Mb.db";

  @Value("${filesystem_path}")
  private String filesystemRootPath;

  @Autowired
  SeaweedFileServiceBean seaweedFileServiceBean;

  @Autowired
  FileSystemServiceBean fileSystemServiceBean;

  /**
   * Test for creating, reading and deleting a file from seaweedfs
   * 
   * @throws IOException
   */
  @Ignore
  @Test
  public void testSeaweedfsCreateReadDelete() throws Exception {
    logger.info("--------------------");
    logger.info("Starting Seaweed test " + this.getClass().getName());
    for (String fileName : FILE_NAMES) {
      InputStream fileInputStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
      String fileId = seaweedFileServiceBean.createFile(fileInputStream, fileName);
      OutputStream retrievedFileOutputStream = new ByteArrayOutputStream();
      seaweedFileServiceBean.readFile(fileId, retrievedFileOutputStream);
      assertNotNull(retrievedFileOutputStream);
      seaweedFileServiceBean.deleteFile(fileId);
      retrievedFileOutputStream = new ByteArrayOutputStream();
      seaweedFileServiceBean.readFile(fileId, retrievedFileOutputStream);
      assertEquals("", new String(((ByteArrayOutputStream) retrievedFileOutputStream).toByteArray()));
      logger.info("--------------------");
    }
  }

  /**
   * Test for creating, reading and deleting a file from the filesystem
   * 
   * @throws IOException
   */
  @Test
  public void testFilesystemCreateReadDelete() throws Exception {
    logger.info("--------------------");
    logger.info("Starting Filesystem test " + this.getClass().getName());
    for (String fileName : FILE_NAMES) {
      InputStream fileInputStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
      String fileRelativePath = fileSystemServiceBean.createFile(fileInputStream, fileName);
      OutputStream retrievedFileOutputStream = new ByteArrayOutputStream();
      fileSystemServiceBean.readFile(fileRelativePath, retrievedFileOutputStream);
      assertNotNull(retrievedFileOutputStream);
      fileSystemServiceBean.deleteFile(fileRelativePath);
      Path path = FileSystems.getDefault().getPath(filesystemRootPath + fileRelativePath);
      assertFalse(Files.exists(path));
      logger.info("--------------------");
    }
  }
}
