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

package de.mpg.mpdl.inge.citationmanager.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.mpg.mpdl.inge.citationmanager.CitationStyleManagerException;

/**
 * Utility class to deal with resources such as files and directories. Either on the file system or
 * in jar files.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
/**
 * @author vlad
 * 
 */
public class CitationUtil {
  public static final String CITATIONSTYLES_DIRECTORY = "CitationStyles/";
  public static final String CITATION_STYLE_PROCESSING_XSL = "escidoc-cscl2cs-processing.xsl";
  public static final String CITATION_STYLE_XML = "CitationStyle.xml";
  public static final String CITATION_STYLE_XSL = "CitationStyle.xsl";
  public static final String CLASS_DIRECTORY = "target/classes/";
  public static final String DATASOURCES_DIRECTORY = "DataSources/";
  public static final String EXPLAIN_FILE = "explain-styles.xml";
  public static final String FONTSTYLES_FILENAME = "FontStyles";
  public static final String RESOURCES_DIRECTORY_JAR = "";
  public static final String RESOURCES_DIRECTORY_LOCAL = "src/main/resources/";
  public static final String SCHEMAS_DIRECTORY = "Schemas/";
  public static final String SORTINGS_DIRECTORY = "Transformations/";
  public static final String TEST_RESOURCES_DIRECTORY_LOCAL = "target/test-classes/";
  public static final String TRANSFORMATIONS_DIRECTORY = "Transformations/";

  /**
   * Returns path to the directory of the classes depending on the run context (TOBE implemented
   * further)
   * 
   * @return path
   * @throws IOException
   */
  public static String getPathToClasses() throws IOException {
    return "";
  }

  /**
   * Returns path to the resources directory
   * 
   * @return path
   * @throws IOException
   */
  public static String getPathToResources() throws IOException {
    return getPathToClasses().replace(CLASS_DIRECTORY, RESOURCES_DIRECTORY_LOCAL);
  }

  /**
   * Returns path to the test resources directory
   * 
   * @return path
   * @throws IOException
   */
  public static String getPathToTestResources() throws IOException {
    return getPathToClasses().replace(CLASS_DIRECTORY, TEST_RESOURCES_DIRECTORY_LOCAL);
  }

  /**
   * Returns path to the test resources directory of the citation style
   * 
   * @param cs - CItation Style ID
   * @return
   * @throws IOException
   */
  public static String getPathToCitationStyleTestResources(String cs) throws IOException {
    return getPathToTestResources() + CITATIONSTYLES_DIRECTORY + cs + "/";
  }

  /**
   * Returns path to the Citation Styles directory
   * 
   * @return path
   * @throws IOException
   */
  public static String getPathToCitationStyles() throws IOException {
    return getPathToResources() + CITATIONSTYLES_DIRECTORY;
  }

  /**
   * Returns path to the Citation Style directory
   * 
   * @param cs
   * @return
   * @throws IOException
   */
  public static String getPathToCitationStyle(String cs) throws IOException {
    return getPathToCitationStyles() + cs + "/";
  }

  /**
   * Returns path to the Citation Style Definition XML
   * 
   * @param cs
   * @return
   * @throws IOException
   */
  public static String getPathToCitationStyleXML(String cs) throws IOException {
    return getPathToCitationStyle(cs) + CITATION_STYLE_XML;
  }

  /**
   * Returns path to the Citation Style compiled XSL
   * 
   * @param cs
   * @return
   * @throws IOException
   */
  public static String getPathToCitationStyleXSL(String cs) throws IOException {
    return getPathToCitationStyle(cs) + CITATION_STYLE_XSL;
  }

  /**
   * Returns path to the Data Sources directory
   * 
   * @return path
   * @throws IOException
   */
  public static String getPathToDataSources() throws IOException {
    return getPathToResources() + DATASOURCES_DIRECTORY;
  }

  /**
   * Returns path to the Schemas directory
   * 
   * @return path
   * @throws IOException
   */
  public static String getPathToSchemas() throws IOException {
    return getPathToResources() + SCHEMAS_DIRECTORY;
  }

  /**
   * Returns path to the Transformations directory
   * 
   * @return path
   * @throws IOException
   */
  public static String getPathToTransformations() throws IOException {
    return getPathToClasses() + TRANSFORMATIONS_DIRECTORY;
  }

  /**
   * Gets URI to the resources
   * 
   * @return uri to the resources
   * @throws IOException
   */
  public static String getUriToResources() throws IOException {
    return RESOURCES_DIRECTORY_JAR.equals(getPathToClasses()) ? RESOURCES_DIRECTORY_JAR : RESOURCES_DIRECTORY_LOCAL;
  }

  /*
   * Explains citation styles and output types for them
   * 
   * @see de.mpg.mpdl.inge.citationmanager.CitationStyleHandler#explainStyles()
   */
  public static String getExplainStyles() throws CitationStyleManagerException {
    String fileString = null;
    try {
      fileString = de.mpg.mpdl.inge.util.ResourceUtil.getResourceAsString(getPathToSchemas() + CitationUtil.EXPLAIN_FILE,
          CitationUtil.class.getClassLoader());
    } catch (IOException e) {
      throw new CitationStyleManagerException(e);
    }
    return fileString;
  }

  /**
   * Load citman properties
   * 
   * @param path - relative path to the prop file
   * @param fileName - name of the prop file
   * @return properties
   * @throws IOException
   * @throws FileNotFoundException
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static Properties getProperties(String path, String fileName) throws FileNotFoundException, IOException {
    InputStream is = de.mpg.mpdl.inge.util.ResourceUtil.getResourceAsStream(CitationUtil.getPathToResources() + path + fileName,
        CitationUtil.class.getClassLoader());
    Properties props = new Properties();
    props.load(is);

    return props;
  }

  //  /**
  //   * Load citman properties
  //   * 
  //   * @param fileName - file name of the properties
  //   * @return properties
  //   * @throws FileNotFoundException
  //   * @throws IOException
  //   */
  //
  //  public static Properties getProperties(String fileName) throws FileNotFoundException, IOException {
  //    return getProperties("", fileName);
  //  }

  //  /**
  //   * Returns list of Citation Styles according to the content of the <code>CitaionStyles</code>
  //   * directory
  //   * 
  //   * @param root
  //   * @return list of Citation Styles
  //   * @throws IllegalArgumentException
  //   * @throws IOException
  //   */
  //  public static String[] getCitationStylesList() throws IllegalArgumentException, IOException {
  //    // File templPath = new File(getCsPath(root).toString());
  //    File templPath = new File(getPathToCitationStyles());
  //
  //    return templPath.list(new FilenameFilter() {
  //      public boolean accept(File dir, String name) {
  //        return (new File(dir, name)).isDirectory() && !name.startsWith(".");
  //      }
  //    });
  //  }

  //  /**
  //   * Deletes CitationStyleBundle
  //   * 
  //   * @param path
  //   * @param name A name of CitationStyle
  //   * @throws CitationStyleManagerException
  //   * @throws IOException
  //   * @throws IOException
  //   * @throws Exception
  //   */
  //  public static void deleteCitationStyleBundle(String name) throws IllegalArgumentException, CitationStyleManagerException, IOException {
  //
  //    Utils.checkName(name, "Empty name of CitationStyleBundle");
  //
  //    File path = new File(getPathToCitationStyles() + "/" + name);
  //    if (!path.isDirectory()) {
  //      throw new IllegalArgumentException("deleteCitationStyleBundle: cannot find directory:" + path);
  //    }
  //    for (String f : path.list())
  //      new File(path, f).delete();
  //    path.delete();
  //  }
}
