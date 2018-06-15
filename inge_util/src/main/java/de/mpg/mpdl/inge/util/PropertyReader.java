/*
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */
package de.mpg.mpdl.inge.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Helper class for reading properties from the global escidoc property file.
 * 
 * This class tries to locate the properties in various ways. Once the properties file has been read
 * it is cached. The following steps are executed to find a properties file:
 * <ul>
 * <li>First the location of the properties file is determined by looking for the system property
 * <code>pubman.properties.file</code>. This property can be used to set the path to the properties
 * file that should be used. If this property is not set the default file path
 * <code>pubman.properties</code> is used.
 * <li>Second step is to read the properties file: First we try to read the properties file from the
 * local file system. If it cannot be found, it is searched in the classpath.
 * </ul>
 * 
 * @author Peter Broszeit (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate: 2011-09-30 11:15:01 +0200 (Fri, 30 Sep 2011) $
 * @revised by BrP: 03.09.2007
 */
public class PropertyReader {
  private static Logger logger = Logger.getLogger(PropertyReader.class);

  private static final String DEFAULT_PROPERTY_FILE = "pubman.properties";

  private static Properties properties;
  private static URL solution;
  private static String fileLocation = "";
  private static int counterForLoadingProperties = 0;

  private PropertyReader() {
    loadProperties();
  }

  private static PropertyReader getInstance() {
    return PropertyReaderHolder.instance;
  }

  public static String getProperty(String key) {
    return PropertyReader.getInstance().doGetProperty(key);
  }

  public static String getProperty(String key, String defaultValue) {
    return PropertyReader.getInstance().doGetProperty(key) != null ? PropertyReader.getInstance().doGetProperty(key) : defaultValue;
  }

  public static Properties getProperties() {
    PropertyReader.getInstance();

    return PropertyReader.properties;
  }

  /**
   * Get the configured URL of the running framework instance.
   * 
   * @return The url as a String.
   */
  public static String getFrameworkUrl() {
    return getProperty("escidoc.framework_access.framework.url");
  }

  public static String getFrameworkAdminUsername() {
    return getProperty("framework.admin.username");
  }

  public static String getFrameworkAdminPassword() {
    return getProperty("framework.admin.password");
  }


  //  public static String getLoginUrl() {
  //    return getProperty("escidoc.framework_access.login.url");
  //  }

  /**
   * Force the property file to be reloaded into the Properties object
   */
  public static void forceReloadProperties() {
    new PropertyReader();
  }

  /**
   * Gets the value of a property for the given key from the system properties or the PubMan
   * property file. It is always tried to get the requested property value from the system
   * properties. This option gives the opportunity to set a specific property temporary using the
   * system properties. If the requested property could not be obtained from the system properties
   * the PubMan property file is accessed. (For details on access to the properties file see class
   * description.)
   * 
   * @param key The key of the property.
   * @param callingClass Class of the calling class
   * @return The value of the property.
   */
  private String doGetProperty(String key) {
    // First check system properties
    String value = System.getProperty(key);
    if (value != null) {
      return value;
    }

    // Get the property
    value = properties.getProperty(key);

    return value;
  }

  /**
   * Load the properties from the location defined by the system property
   * <code>pubman.properties.file</code>. If this property is not set the default file path
   * <code>pubman.properties</code> is used. If no properties can be loaded, the jvm is terminated.
   */
  private static void loadProperties() {
    counterForLoadingProperties++;

    String propertiesFile = "";
    Properties solProperties = new Properties();

    solution = PropertyReader.class.getClassLoader().getResource("solution.properties");

    if (solution != null) {
      logger.info("Solution URI is <" + solution.toString() + ">");

      try {
        InputStream in = getInputStream("solution.properties");
        solProperties.load(in);
        in.close();

        String appname = solProperties.getProperty("appname");
        propertiesFile = appname + ".properties";
      } catch (IOException e) {
        logger.warn("Could not read properties from solution.properties file.");
      }

    } else {
      // Use Default location of properties file
      propertiesFile = DEFAULT_PROPERTY_FILE;
      logger.info("Trying default property file: <" + DEFAULT_PROPERTY_FILE + ">");
    }

    properties = new Properties();
    try {
      InputStream instream = getInputStream(propertiesFile);
      properties.load(instream);
      properties.putAll(solProperties);
      instream.close();
    } catch (IOException e) {
      logger.fatal("Got no properties to load...<" + propertiesFile + ">", e);
      throw new ExceptionInInitializerError(e);
    }

    logger.info("Properties loaded successfully from " + fileLocation);
  }

  /**
   * Retrieves the Inputstream of the given file path. First the resource is searched in the file
   * system, if this fails it is searched using the classpath.
   * 
   * @param filepath The path of the file to open.
   * @return The inputstream of the given file path.
   * @throws IOException If the file could not be found neither in the file system nor in the
   *         classpath.
   */
  private static InputStream getInputStream(String filepath) throws IOException {
    return getInputStream(filepath, PropertyReader.class);
  }

  /**
   * Retrieves the Inputstream of the given file path. First the resource is searched in the file
   * system, if this fails it is searched using the classpath.
   * 
   * @param filepath The path of the file to open.
   * @param callingClass Class of the calling class
   * @return The inputstream of the given file path.
   * @throws IOException If the file could not be found neither in the file system nor in the
   *         classpath.
   */
  private static InputStream getInputStream(String filepath, Class<PropertyReader> callingClass) throws IOException {
    InputStream instream = null;
    // First try to search in file system
    try {
      instream = new FileInputStream(filepath);
      fileLocation = (new File(filepath)).getAbsolutePath();
    } catch (Exception e) {
      // try to get resource from classpath
      URL url = callingClass.getClassLoader().getResource(filepath);
      if (url != null) {
        instream = url.openStream();
        fileLocation = url.getFile();
      }
    }
    if (instream == null) {
      throw new FileNotFoundException(filepath);
    }
    return instream;
  }

  // only for test purpose
  static int getCounter() {
    return counterForLoadingProperties;
  }

  private static class PropertyReaderHolder {
    private static final PropertyReader instance = new PropertyReader();
  }
}
