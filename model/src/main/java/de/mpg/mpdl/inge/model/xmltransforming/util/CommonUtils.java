/*
 * 
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

package de.mpg.mpdl.inge.model.xmltransforming.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;

/**
 * Useful common functionalities.
 * 
 * @author Miriam Doelle (initial creation)
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @revised by MuJ: 03.09.2007
 */
public class CommonUtils {
  /**
   * Searches for a file name in the classpath and gives back the URL.
   * 
   * @param fileName The file name
   * @return The URL
   * @throws FileNotFoundException
   */
  public static URL findURLInClasspath(String fileName) throws FileNotFoundException {
    URL url = CommonUtils.class.getClassLoader().getResource(fileName);
    if (url == null) {
      throw new FileNotFoundException(fileName);
    }
    return url;
  }

  /**
   * Reads a property list (key and element pairs) from the given property file and delivers the
   * properties.
   * 
   * @param propertyFileName The name of the property file
   * @return The properties
   * @throws IOException
   */
  private static Properties getProperties(String propertyFileName) throws IOException {
    URL url = findURLInClasspath(propertyFileName);
    Properties properties = new Properties();
    properties.load(url.openStream());
    return properties;
  }

  /**
   * Encodes a String according to RFC3986. Difference to URIEncoder is that a blank is not replaced
   * by a plus (+), but by %20
   * 
   * @param urlPart
   * @return
   */
  public static String urlEncode(String urlPart) {
    try {
      return URLEncoder.encode(urlPart, "UTF-8").replace("+", "%20");
    } catch (Exception e) {
      return urlPart;
    }
  }
}
