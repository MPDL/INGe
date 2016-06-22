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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

/**
 * Helper class for reading properties from the global escidoc property file.
 * 
 * This class tries to locate the properties in various ways. Once the properties file has been read
 * it is cached. The following steps are executed to find a properties file:
 * <ul>
 * <li>First the location of the properties file is dertermined by looking for the system property
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
  private static Properties properties;

  private static final String DEFAULT_PROPERTY_FILE = "pubman.properties";

  private static final String PROPERTY_FILE_KEY = "pubman.properties.file";

  private static URL solution;

  private static String fileLocation = null;

  /**
   * Gets the value of a property for the given key from the system properties or the escidoc
   * property file. It is always tried to get the requested property value from the system
   * properties. This option gives the opportunity to set a specific property temporary using the
   * system properties. If the requested property could not be obtained from the system properties
   * the escidoc property file is accessed. (For details on access to the properties file see class
   * description.)
   * 
   * @param key The key of the property.
   * @param callingClass Class of the calling class
   * @return The value of the property.
   * @throws IOException
   * @throws URISyntaxException
   */
  public static String getProperty(String key) throws IOException, URISyntaxException {
    // First check system properties
    String value = System.getProperty(key);
    if (value != null) {
      return value;
    }
    // Check properties file
    if (properties == null) {
      loadProperties();
    }
    // Get the property
    value = properties.getProperty(key);
    // Logger.getLogger(PropertyReader.class).info("framework URL: "+value);
    return value;
  }

  public static Properties getProperties() throws IOException, URISyntaxException {
    if (properties == null) {
      loadProperties();
    }

    return properties;
  }

  /**
   * Load the properties from the location defined by the system property
   * <code>pubman.properties.file</code>. If this property is not set the default file path
   * <code>pubman.properties</code> is used.
   * 
   * @param callingClass Class of the calling class
   * @throws IOException If the properties file could not be found neither in the file system nor in
   *         the classpath.
   * @throws URISyntaxException
   */
  public static void loadProperties() throws IOException, URISyntaxException {
    String propertiesFile = null;
    Properties solProperties = new Properties();
    try {
      solution = PropertyReader.class.getClassLoader().getResource("solution.properties");
    } catch (Exception e) {
      Logger.getLogger(PropertyReader.class).warn(
          "WARNING: solution.properties not found: " + e.getMessage());
    }
    if (solution != null) {
      Logger.getLogger(PropertyReader.class).info("Solution URI is " + solution.toString());
      InputStream in = getInputStream("solution.properties");
      solProperties.load(in);
      in.close();
      String appname = solProperties.getProperty("appname");
      propertiesFile = appname + ".properties";
    } else {
      // Use Default location of properties file
      propertiesFile = DEFAULT_PROPERTY_FILE;
      Logger.getLogger(PropertyReader.class).debug(
          "solution.properties file not found. Trying default.");
    }

    InputStream instream = getInputStream(propertiesFile);
    properties = new Properties();
    properties.load(instream);
    properties.putAll(solProperties);
    instream.close();

    Logger.getLogger(PropertyReader.class).info("Properties loaded from " + fileLocation);
    // Logger.getLogger(PropertyReader.class).info(properties.toString());
  }

  public static void setProperty(String key, String value) throws IOException, URISyntaxException {
    if (properties == null) {
      loadProperties();
    }

    Object object = null;
    if ((object = properties.getProperty(key)) != null) {
      Logger.getLogger(PropertyReader.class).debug(
          "Overwriting property (" + key + ", " + object.toString() + ")" + " with " + value);
    }
    properties.setProperty(key, value);
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
  public static InputStream getInputStream(String filepath, Class callingClass) throws IOException {
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

  /**
   * Get the configured URL of the running framework instance.
   * 
   * @return The url as a String.
   * @throws ServiceException
   * @throws URISyntaxException
   */
  public static String getFrameworkUrl() throws ServiceException, URISyntaxException {
    String url;
    try {
      url = getProperty("escidoc.framework_access.framework.url");
    } catch (IOException e) {
      throw new ServiceException(e);
    }
    return url;
  }

  public static String getLoginUrl() throws ServiceException, URISyntaxException {
    String url;
    try {
      url = getProperty("escidoc.framework_access.login.url");
    } catch (IOException e) {
      throw new ServiceException(e);
    }
    return url;
  }
}