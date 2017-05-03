package de.mpg.mpdl.inge.util;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PropertyReaderFailureTest {

  @BeforeClass
  public static void removePropertyFile() {
    FileUtils.deleteQuietly(new File("pubman.properties"));
    FileUtils.deleteQuietly(new File("./target/test-classes/pubman.properties"));
  }


  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void testPropertyFileNotExisting() {
    expectedEx.expect(ExceptionInInitializerError.class);
    PropertyReader.getProperty("xx");
  }

}
