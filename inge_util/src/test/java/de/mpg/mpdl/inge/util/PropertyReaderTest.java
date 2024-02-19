package de.mpg.mpdl.inge.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class PropertyReaderTest {

  static File propertiesFile = new File("pubman.properties");

  @BeforeClass
  public static void createPropertiesFile() throws IOException {
    FileUtils.deleteQuietly(propertiesFile);

    FileUtils.writeStringToFile(propertiesFile, "test = http://dev-pubman.mpdl.mpg.de", true);
    FileUtils.writeStringToFile(propertiesFile, System.getProperty(PropertyReader.LINE_SEPARATOR), true);

    LogManager.getLogger(PropertyReaderTest.class)
        .info("pubman.properties created for testing in <" + propertiesFile.getAbsolutePath() + ">");
  }

  @Ignore
  @Test
  public void testGetProperty() {
    String notDefinedProperty = PropertyReader.getProperty("notdefined", "defaultValue");
    assertTrue(notDefinedProperty != null);
    assertTrue(notDefinedProperty.equals("defaultValue"));

    String notExistingProperty = PropertyReader.getProperty("notExistingProperty");
    assertTrue(notExistingProperty == null);
  }

  @Ignore
  @Test
  public void testForceReloadProperties() throws IOException {

    // add additional property
    FileUtils.writeStringToFile(new File("pubman.properties"), "xxx = yyy", true);

    PropertyReader.forceReloadProperties();
    String xxxProperty = PropertyReader.getProperty("xxx");

    assertTrue(xxxProperty.equals("yyy"));
  }

  @Ignore
  @Test
  public void testGetProperties() {
    Properties p = PropertyReader.getProperties();
    assertTrue(p != null);
    assertTrue(p.size() > 0);
  }

  @AfterClass
  public static void removePropertyFile() {
    FileUtils.deleteQuietly(propertiesFile);
  }

}
