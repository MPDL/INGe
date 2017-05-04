package de.mpg.mpdl.inge.util;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class PropertyReaderFailureTest {

  @BeforeClass
  public static void removePropertyFile() {
    FileUtils.deleteQuietly(new File("pubman.properties"));
    FileUtils.deleteQuietly(new File("./target/test-classes/pubman.properties"));
  }

  @Test(expected = ExceptionInInitializerError.class)
  public void testPropertyFileNotExisting() {

    PropertyReader.getProperty("xx");
  }

}
