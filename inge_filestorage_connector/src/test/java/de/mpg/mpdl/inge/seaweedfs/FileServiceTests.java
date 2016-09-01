package de.mpg.mpdl.inge.seaweedfs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
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

  public static final String[] FILE_NAMES = {"test100k.db", "test1Mb.db"};
  public static final String FILE2_NAME = "test1Mb.db";

  @Autowired
  SeaweedFileServiceBean seaweedFileServiceBean;

  @Autowired
  FileSystemServiceBean fileSystemServiceBean;

  /**
   * Test for creating, reading and deleting a file from seaweedfs
   * 
   * @throws IOException
   */
  @Test
  public void testSeaweedfsCreateReadDelete() throws Exception {
    for (String fileName : FILE_NAMES) {
      InputStream fileInputStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
      String fileId = seaweedFileServiceBean.createFile(fileInputStream, fileName);
      OutputStream retrievedFileOutputStream = new ByteArrayOutputStream();
      seaweedFileServiceBean.readFile(fileId, retrievedFileOutputStream);
      System.out.println(((ByteArrayOutputStream) retrievedFileOutputStream).toString());
      seaweedFileServiceBean.deleteFile(fileId);
      System.out.println("--------------------");
    }
  }

  /**
   * Test for creating, reading and deleting a file from the filesystem
   * 
   * @throws IOException
   */
  @Test
  public void testFilesystemCreateReadDelete() throws Exception {
    for (String fileName : FILE_NAMES) {
      InputStream fileInputStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
      String fileRelativePath = fileSystemServiceBean.createFile(fileInputStream, fileName);
      OutputStream retrievedFileOutputStream = new ByteArrayOutputStream();
      fileSystemServiceBean.readFile(fileRelativePath, retrievedFileOutputStream);
      System.out.println(((ByteArrayOutputStream) retrievedFileOutputStream).toString());
      fileSystemServiceBean.deleteFile(fileRelativePath);
      System.out.println("--------------------");
    }
  }
}
