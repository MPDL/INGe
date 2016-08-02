package de.mpg.mpdl.inge.seaweedfs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.JsonNode;

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
@ContextConfiguration(classes = SeaweedConnectorConfiguration.class)
public class FileServiceTest {

  public static final String [] FILE_NAMES = {"test100k.db", "test1Mb.db"};
  public static final String FILE2_NAME = "test1Mb.db";

  @Autowired
  SeaweedFileServiceBean seaweedFileServiceBean;

  /**
   * Test for creating, reading and deleting a file
   * 
   * @throws IOException
   */
  @Test
  public void testCreateReadDelete() throws Exception {
    for (String filename : FILE_NAMES) {
      File file = new File(this.getClass().getClassLoader().getResource(filename).toURI());
      JsonNode jsonObject = seaweedFileServiceBean.createFile(file);
      List<String> fileId = jsonObject.findValuesAsText("fid");
      System.out.println("Created File: [" + fileId.get(0) + "]");
      OutputStream retrievedFileOutputStream = new ByteArrayOutputStream();
      seaweedFileServiceBean.readFile(fileId.get(0), retrievedFileOutputStream);
      System.out.println(((ByteArrayOutputStream) retrievedFileOutputStream).toString());
      seaweedFileServiceBean.deleteFile(fileId.get(0));
      System.out.println("--------------------");
    }
  }
}
