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

package de.mpg.mpdl.inge.aa.web.client;

import de.mpg.mpdl.inge.util.PropertyReader;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
@SuppressWarnings("serial")
public class FinalClientServlet extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
    doPost(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
    try {
      String clientClassName = PropertyReader.getProperty(PropertyReader.INGE_AA_CLIENT_CLASS);
      if (null == clientClassName || clientClassName.isEmpty()) {
        clientClassName = PropertyReader.getProperty(PropertyReader.INGE_AA_CLIENT_FINISH_CLASS);
        Class<?> clientClass = Class.forName(clientClassName);
        FinalClient client = (FinalClient) clientClass.newInstance();
        client.process(req, resp);
      } else {
        Class<?> clientClass = Class.forName(clientClassName);
        FinalClient client = (FinalClient) clientClass.newInstance();
        client.process(req, resp);
      }
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
}
