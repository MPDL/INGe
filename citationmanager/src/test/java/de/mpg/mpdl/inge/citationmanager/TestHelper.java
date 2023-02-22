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

package de.mpg.mpdl.inge.citationmanager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.citationmanager.utils.CitationUtil;
import de.mpg.mpdl.inge.util.ResourceUtil;

/**
 * Helper class for all test classes.
 * 
 * @author Johannes M&uuml;ller (initial)
 * @author $Author$ (last change)
 * @version $Revision$ $LastChangedDate$
 */
public class TestHelper {
  private static final Logger logger = Logger.getLogger(TestHelper.class);

  public static Properties getTestProperties(String cs) throws FileNotFoundException, IOException {
    String path_to_props = CitationUtil.getPathToCitationStyleTestResources(cs) + "test.properties";
    InputStream is = ResourceUtil.getResourceAsStream(path_to_props, TestHelper.class.getClassLoader());
    Properties props = new Properties();
    props.load(is);

    return props;
  }

  public static String getCitationStyleTestXmlAsString(String fileName) throws IOException {
    return getFileAsString(CitationUtil.CITATIONSTYLES_DIRECTORY + fileName);
  }

  private static String getFileAsString(String fileName) throws IOException {
    logger.info("test XML" + CitationUtil.getPathToTestResources() + fileName);
    return ResourceUtil.getResourceAsString(CitationUtil.getPathToTestResources() + fileName, TestHelper.class.getClassLoader());
  }
}
