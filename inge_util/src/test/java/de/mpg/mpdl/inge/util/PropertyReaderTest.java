package de.mpg.mpdl.inge.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class PropertyReaderTest {

  static File propertiesFile = new File("pubman.properties");

  @BeforeClass
  public static void createPropertiesFile() throws IOException {
    FileUtils.deleteQuietly(propertiesFile);

    FileUtils.writeStringToFile(propertiesFile, "escidoc.framework_access.framework.url = http://dev-pubman.mpdl.mpg.de", true);
    FileUtils.writeStringToFile(propertiesFile, System.getProperty("line.separator"), true);
    //    FileUtils.writeStringToFile(propertiesFile, "escidoc.framework_access.login.url = http://localhost:8080", true);
    //    FileUtils.writeStringToFile(propertiesFile, System.getProperty("line.separator"), true);

    Logger.getLogger(PropertyReaderTest.class).info("pubman.properties created for testing in <" + propertiesFile.getAbsolutePath() + ">");
  }

  @Ignore
  @Test
  public void testGetProperty() {
    String frameworkUrl = PropertyReader.getFrameworkUrl();
    assertTrue(frameworkUrl != null);
    assertTrue(frameworkUrl.equals("http://dev-pubman.mpdl.mpg.de"));

    //    String loginUrl = PropertyReader.getLoginUrl();
    String loginUrl = PropertyReader.getFrameworkUrl();
    assertTrue(loginUrl != null);
    assertTrue(loginUrl.equals("http://localhost:8080"));

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

    // check if old properties are already here
    String frameworkUrl = PropertyReader.getFrameworkUrl();
    assertTrue(frameworkUrl != null);
    assertTrue(frameworkUrl.equals("http://dev-pubman.mpdl.mpg.de"));

    //    String loginUrl = PropertyReader.getLoginUrl();
    //    assertTrue(loginUrl != null);
    //    assertTrue(loginUrl.equals("http://localhost:8080"));

    assertTrue("Is <" + PropertyReader.getCounter() + "> expected 1", PropertyReader.getCounter() == 1);

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
