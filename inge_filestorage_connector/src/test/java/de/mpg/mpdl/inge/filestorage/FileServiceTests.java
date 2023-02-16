package de.mpg.mpdl.inge.filestorage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.mpdl.inge.filestorage.filesystem.FileSystemServiceBean;
import de.mpg.mpdl.inge.filestorage.seaweedfs.SeaweedFileServiceBean;
import de.mpg.mpdl.inge.filestorage.spring.FileStorageConnectorConfiguration;

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

  private static final Logger logger = Logger.getLogger(FileServiceTests.class);

  public static final String[] FILE_NAMES = {"test100k.db", "test1Mb.db"};

  @Value("${filesystem_path}")
  private String filesystemRootPath;

  @Value("${gluster_path}")
  private String glusterRootPath;

  @Autowired
  SeaweedFileServiceBean seaweedFileServiceBean;

  @Autowired
  FileSystemServiceBean fileSystemServiceBean;

  @BeforeClass
  public static void setUpClass() {
    // Initialize stuff once for ALL tests (run once)
    try {
      System.out.println("\nStarte SeaweadServer...");
      Runtime.getRuntime().exec("c:/Tools/SeaweedFS/start_seaweed.bat > c:/Tools/SeaweedFS/start.log");
      Thread.currentThread();
      System.out.println("\nwarte...");
      Thread.sleep(40000);
    } catch (Exception e) {
      System.out.println("\nException" + e);
    }
  }

  @AfterClass
  public static void tearDownClass() {
    // Do something after ALL tests have been run (run once)
    try {
      System.out.println("\nBeende SeaweadServer...");
      Runtime.getRuntime().exec("c:/Tools/SeaweedFS/kill_seaweed.bat > c:/Tools/SeaweedFS/kill.log");
    } catch (Exception e) {
      System.out.println("\nException" + e);
    }
  }

  /**
   * Test for creating, reading and deleting a file from seaweedfs
   * 
   * @throws IOException
   */
  @Test
  public void testSeaweedfsCreateReadDelete() throws Exception {
    System.out.println("\nSEAWEAD\n");
    logger.info("--------------------");
    logger.info("Starting Seaweed test " + this.getClass().getName());
    for (String fileName : FILE_NAMES) {
      System.out.println("Datei [" + fileName + "]");
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
    System.out.println("\nFILESYSTEM\n");
    logger.info("--------------------");
    logger.info("Starting Filesystem test " + this.getClass().getName());
    for (String fileName : FILE_NAMES) {
      System.out.println("Datei [" + fileName + "]");
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
