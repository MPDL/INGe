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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;


/**
 * Utility class to deal with resources such as files and directories. Either on the file system or
 * in jar files.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ResourceUtil {
  private static final Logger logger = Logger.getLogger(ResourceUtil.class);

  /**
   * Gets a resource as InputStream.
   *
   * @param fileName The path and name of the file relative from the working directory.
   * @return The resource as InputStream.
   * @throws FileNotFoundException Thrown if the resource cannot be located.
   */
  public static File getResourceAsFile(final String fileName, ClassLoader classLoader) throws FileNotFoundException {
    URL url = classLoader.getResource(resolveFileName(fileName));

    // Maybe it's in a WAR file
    if (url == null) {
      url = classLoader.getResource(resolveFileName("WEB-INF/classes/" + fileName));
    }

    File file = null;
    if (url != null) {
      logger.debug("Resource found: " + url.getFile());
      try {
        // Decode necessary for windows paths
        file = new File(URLDecoder.decode(url.getFile(), "cp1253"));
      } catch (UnsupportedEncodingException e) {
        logger.warn(e);
      }
    }

    if (file == null) {

      logger.debug("Resource not found, getting file.");

      file = new File(fileName);
      if (!file.exists()) {
        throw new FileNotFoundException("File '" + fileName + "' not found.");
      }
    }

    return file;
  }

  /**
   * Gets a resource as InputStream.
   *
   * @param fileName The path and name of the file relative from the working directory.
   * @return The resource as InputStream.
   * @throws FileNotFoundException Thrown if the resource cannot be located.
   */
  public static InputStream getResourceAsStream(final String fileName, ClassLoader classLoader) throws FileNotFoundException {

    InputStream fileIn;
    fileIn = classLoader.getResourceAsStream(resolveFileName(fileName));

    // Maybe it's in a WAR file
    if (fileIn == null) {
      fileIn = classLoader.getResourceAsStream(resolveFileName("WEB-INF/classes/" + fileName));
    }

    if (fileIn == null) {
      fileIn = new FileInputStream(resolveFileName(fileName));
    }

    return fileIn;
  }

  /**
   * Gets a resource as String.
   *
   * @param fileName The path and name of the file relative from the working directory.
   * @return The resource as String.
   * @throws IOException Thrown if the resource cannot be located.
   */
  public static String getResourceAsString(final String fileName, ClassLoader classLoader) throws IOException {
    InputStream fileIn = getResourceAsStream(fileName, classLoader);
    BufferedReader br = new BufferedReader(new InputStreamReader(fileIn, StandardCharsets.UTF_8));
    String line = null;
    StringBuilder result = new StringBuilder();

    while ((line = br.readLine()) != null) {
      result.append(line);
      result.append("\n");
    }

    return result.toString();
  }

  /**
   * Gets a stream as String.
   *
   * @param stream The input stream
   * @return The resource as String.
   * @throws IOException Thrown if the resource cannot be located.
   */
  public static String getStreamAsString(final InputStream stream) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
    String line = null;
    StringBuilder result = new StringBuilder();

    while ((line = br.readLine()) != null) {
      result.append(line);
      result.append("\n");
    }
    stream.close();

    return result.toString();
  }

  /**
   * Gets a resource as String.
   *
   * @param fileName The path and name of the file relative from the working directory.
   * @return The resource as String.
   * @throws IOException Thrown if the resource cannot be located.
   */
  public static byte[] getResourceAsBytes(final String fileName, ClassLoader classLoader) throws IOException {
    InputStream fileIn = getResourceAsStream(fileName, classLoader);

    byte[] buffer = new byte[2048];
    int read;
    ByteArrayOutputStream result = new ByteArrayOutputStream();

    while ((read = fileIn.read(buffer)) != -1) {
      result.write(buffer, 0, read);
    }

    return result.toByteArray();
  }

  /**
   * Gets an array of files containing the files in a given directory.
   *
   * @param dir The name of the directory.
   * @return Array of files.
   * @throws IOException Thrown if file is not found.
   */
  public static File[] getFilenamesInDirectory(String dir, ClassLoader classLoader) throws IOException {
    File dirFile = getResourceAsFile(resolveFileName(dir), classLoader);

    if (dirFile == null) {
      return null;
    }

    if (!dirFile.isDirectory()) {
      throw new IOException("The given path is not a directory.");
    }

    ArrayList<File> fileArray = new ArrayList<>();
    String[] fileNames = dirFile.list();

    for (String fileName : fileNames) {
      File file = new File(dirFile.getAbsolutePath() + "/" + fileName);

      if (file.isFile()) {
        fileArray.add(file);
      } else {
        fileArray.addAll(Arrays.asList(getFilenamesInDirectory(file.getAbsolutePath(), classLoader)));
      }
    }

    return fileArray.toArray(new File[] {});
  }

  /**
   * This method resolves /.. in uris
   *
   * @param name
   * @return
   */
  public static String resolveFileName(String name) {
    if (name != null && (name.contains("/..") || name.contains("\\.."))) {
      Pattern pattern1 = Pattern.compile("(\\\\|/)\\.\\.");
      Matcher matcher1 = pattern1.matcher(name);
      if (matcher1.find()) {
        int pos1 = matcher1.start();
        Pattern pattern2 = Pattern.compile("(\\\\|/)[^\\\\/]*$");
        Matcher matcher2 = pattern2.matcher(name.substring(0, pos1));
        if (matcher2.find()) {
          int pos2 = matcher2.start();
          return resolveFileName(name.substring(0, pos2) + name.substring(pos1 + 3));
        }
      }
    }

    return name;
  }

  public static void main(String[] args) {
    System.out.println(resolveFileName("transformations\\commonPublicationFormats\\xslt\\..\\..\\vocabulary-mappings.xsl"));
  }
}
