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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.inge.aa.AuthenticationVO;
import de.mpg.mpdl.inge.aa.crypto.RSAEncoder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public abstract class FinalClient extends Client {

  private static final Logger logger = LogManager.getLogger(FinalClient.class);

  protected void process(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String tan = request.getParameter("tan");
    String target = request.getParameter("target");
    String uri = request.getParameter("uri");
    String model = request.getParameter("model");

    try {
      AuthenticationVO authenticationVO = finalizeAuthentication(request, response);
      authenticationVO.setTan(tan);

      String xml = authenticationVO.toXml();
      String encodedXml = RSAEncoder.rsaEncrypt(xml);
      String separator = "?";

      if (target.contains("?")) {
        separator = "&";
      } // 24.08.2023
      try { // SP: the original target got lost (and I can't find out where...)
            //     now it contains only model and not uri or searchterm and not model
            //     in the first case you have to deliver uri and in second case model
        if (null != uri) { // first case
          response.sendRedirect(target + "&uri=" + uri + separator + encodedXml);
        } else if (null != model) { // second case
          response.sendRedirect(target + "&model=" + model + separator + encodedXml);
        } else {
          response.sendRedirect(target + separator + encodedXml);
        }
      } catch (IllegalStateException ise) {
        logger.warn("Caught IllegalStateException: DEBUG for more info");
        logger.debug("FinalClient tried to send a redirect, but there was probably already a header defined.");
      }

    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  protected abstract AuthenticationVO finalizeAuthentication(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
