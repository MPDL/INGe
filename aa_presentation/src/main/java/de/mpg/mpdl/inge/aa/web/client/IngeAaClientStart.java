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

import java.net.URLEncoder;

import de.mpg.mpdl.inge.util.PropertyReader;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class IngeAaClientStart extends StartClient {

  @Override
  protected String startAuthentication(HttpServletRequest request, HttpServletResponse response) {
    String tan = request.getParameter("tan");
    String from = request.getParameter("target");
    String aaInstanceUrl = PropertyReader.getProperty(PropertyReader.INGE_AA_INSTANCE_URL);

    return "/auth/login_inge.jsp?target=" + aaInstanceUrl + "clientReturn"
        + URLEncoder.encode("?target=" + from + "&tan=" + tan, StandardCharsets.ISO_8859_1);
  }

}
