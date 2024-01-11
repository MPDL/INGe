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

package de.mpg.mpdl.inge.pubman.web.util.servlet;

import java.io.InputStream;
import java.util.ResourceBundle;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.helpers.DefaultHandler;

import de.mpg.mpdl.inge.pubman.web.util.handler.GenreHandler;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ResourceUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;

@SuppressWarnings("serial")
public class GenreServlet extends HttpServlet {

  @Override
  public void init() throws ServletException {
    try {
      final InputStream file = ResourceUtil.getResourceAsStream(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_GENRES_CONFIGURATION),
          GenreServlet.class.getClassLoader());

      final SAXParserFactory factory = SAXParserFactory.newInstance();
      final SAXParser parser = factory.newSAXParser();

      final String jbossHomeDir = System.getProperty(PropertyReader.JBOSS_HOME_DIR);
      final DefaultHandler handler = new GenreHandler(jbossHomeDir + "/modules/pubman/main");

      parser.parse(file, handler);

      // Clear cache of resource bundles in order to load the newly created ones
      ResourceBundle.clearCache();
    } catch (final Exception e) {
      throw new ServletException(e);
    }
  }
}
