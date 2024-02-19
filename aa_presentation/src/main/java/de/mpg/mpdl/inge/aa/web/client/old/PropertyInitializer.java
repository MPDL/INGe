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

package de.mpg.mpdl.inge.aa.web.client.old;

import jakarta.servlet.http.HttpServlet;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
@SuppressWarnings("serial")
public class PropertyInitializer extends HttpServlet {
  //  private static final Logger logger = LogManager.getLogger(PropertyInitializer.class);
  //
  //  @Override
  //  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
  //    init();
  //    resp.getWriter().write("Properties reloaded!");
  //    // resp.getWriter().write(Config.getProperties().toString());
  //  }
  //
  //  @Override
  //  public void init() throws ServletException {
  //    String context = this.getServletContext().getContextPath();
  //    if (context != null && context.startsWith("/")) {
  //      String propertyFilename = context.substring(1) + ".properties";
  //      logger.info("Loading properties from " + propertyFilename);
  //      try {
  //        InputStream propertyStream = ResourceUtil.getResourceAsStream(propertyFilename, PropertyInitializer.class.getClassLoader());
  //        Config.getProperties().load(propertyStream);
  //        // propertyStream.close();
  //      } catch (Exception e) {
  //        throw new ServletException(e);
  //      }
  //    }
  //  }
}
