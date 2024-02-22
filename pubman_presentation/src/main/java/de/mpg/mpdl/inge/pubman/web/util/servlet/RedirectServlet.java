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

import java.io.IOException;

import de.mpg.mpdl.inge.pubman.web.util.ServletTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.LoginHelper;
import de.mpg.mpdl.inge.util.PropertyReader;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A servlet for retrieving and redirecting the content objects urls. /pubman/item/escidoc:12345 for
 * items and /pubman/item/escidoc:12345/component/escidoc:23456/name.txt for components.
 *
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
@SuppressWarnings("serial")
public class RedirectServlet extends HttpServlet {
  private static final String INSTANCE_CONTEXT_PATH = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_CONTEXT_PATH);
  private static final String INSTANCE_URL = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL);

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String id = req.getPathInfo().substring(1);
    boolean download = ("download".equals(req.getParameter("mode")));
    boolean tme = ("tme".equals(req.getParameter("mode")));

    String userHandle = req.getParameter(LoginHelper.PARAMETERNAME_USERHANDLE);

    StringBuilder redirectUrl = new StringBuilder();

    // no component -> ViewItemOverviewPage
    if (!id.contains("/component/")) {
      LoginHelper loginHelper = ServletTools.findSessionBean(req, "LoginHelper");

      if (null != loginHelper && loginHelper.isDetailedMode()) {
        redirectUrl.append(RedirectServlet.INSTANCE_CONTEXT_PATH + "/faces/ViewItemFullPage.jsp?itemId=" + id);
      } else {
        redirectUrl.append(RedirectServlet.INSTANCE_CONTEXT_PATH + "/faces/ViewItemOverviewPage.jsp?itemId=" + id);
      }

      if (null != userHandle) {
        redirectUrl.append("&" + LoginHelper.PARAMETERNAME_USERHANDLE + "=" + userHandle);
      }

      resp.sendRedirect(redirectUrl.toString());

      return;
    }

    // is component
    if (id.contains("/component/")) {
      String[] pieces = id.split("/");
      if (4 != pieces.length) {
        resp.sendError(404, "File not found");
      }

      if (INSTANCE_URL.startsWith("https")) {
        redirectUrl.append("https://" + req.getServerName());
      }

      redirectUrl.append("/rest/items/");
      redirectUrl.append(pieces[0]);
      redirectUrl.append("/component/");
      redirectUrl.append(pieces[2]);

      // open component or download it
      if (null == req.getParameter("mode") || download) {
        redirectUrl.append("/content");
        if (download)
          redirectUrl.append("?download=true");
      }

      // view technical metadata
      if (tme) {
        redirectUrl.append("/metadata");
      }

      resp.sendRedirect(redirectUrl.toString());
    }

  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
    // No post action
  }
}
