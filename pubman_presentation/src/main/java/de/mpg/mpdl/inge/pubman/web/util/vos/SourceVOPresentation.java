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
package de.mpg.mpdl.inge.pubman.web.util.vos;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;

import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;

/**
 * SourceVOPresentation defines some presentation specific methods expanding the extended SourceVO
 * class
 *
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@SuppressWarnings("serial")
public class SourceVOPresentation extends SourceVO {
  private static Properties properties;

  /**
   * get the negative list of source genres as Map for this (server-) instance, depending on the
   * source_genres.properties definitions
   *
   * @return Map filled with all source genres which will be excluded
   */
  public static Map<String, String> getExcludedSourceGenreMap() {
    if (null == SourceVOPresentation.properties || SourceVOPresentation.properties.isEmpty()) {
      SourceVOPresentation.properties = SourceVOPresentation.loadExcludedSourceGenreProperties();
    }
    @SuppressWarnings({"unchecked", "rawtypes"})
    final Map<String, String> propertiesMap = new HashMap<String, String>((Map) SourceVOPresentation.properties);
    return propertiesMap;
  }

  /**
   * get the negative list of source genres as properties for this (server-) instance, depending on
   * the source_genres.properties definitions
   *
   * @return Properties filled with all source genres which will be excluded
   */
  private static Properties loadExcludedSourceGenreProperties() {
    SourceVOPresentation.properties = new Properties();
    URL contentCategoryURI = null;
    try {
      contentCategoryURI = SourceVOPresentation.class.getClassLoader().getResource("source_genres.properties");
      if (null != contentCategoryURI) {
        LogManager.getLogger(SourceVOPresentation.class).info("Source genre properties URI is " + contentCategoryURI);
        final InputStream in = contentCategoryURI.openStream();
        SourceVOPresentation.properties.load(in);
        SourceVOPresentation.properties.putAll(SourceVOPresentation.properties);
        in.close();
        LogManager.getLogger(SourceVOPresentation.class).info("Source genre properties loaded from " + contentCategoryURI);
      } else {
        LogManager.getLogger(SourceVOPresentation.class).debug("Source genre properties file not found.");
      }
    } catch (final Exception e) {
      LogManager.getLogger(SourceVOPresentation.class).warn("WARNING: Source genre properties not found: " + e.getMessage());
    }
    return SourceVOPresentation.properties;
  }
}
