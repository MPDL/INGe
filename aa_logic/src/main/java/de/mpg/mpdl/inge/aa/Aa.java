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

package de.mpg.mpdl.inge.aa;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import de.mpg.mpdl.inge.util.PropertyReader;
import jakarta.servlet.http.HttpServletRequest;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class Aa {
  //  private static final Logger logger = LogManager.getLogger(Aa.class);

  private AuthenticationVO authenticationVO = null;

  public Aa(HttpServletRequest request) throws Exception {
    //    if (!Config.isLoaded()) {
    //      initConfig(request);
    //    }
    String[] encodedXml = request.getParameterValues("auth");
    if (encodedXml != null) {
      String xml = de.mpg.mpdl.inge.aa.crypto.RSAEncoder.rsaDecrypt(encodedXml);
      AuthenticationVO authenticationVO = new AuthenticationVO(xml);

      String tan = authenticationVO.getTan();
      if (TanStore.checkTan(tan)) {
        request.getSession().setAttribute("authentication", authenticationVO);
        this.authenticationVO = authenticationVO;
      } else {
        request.getSession().removeAttribute("authentication");
      }
    }
  }

  public AuthenticationVO getAuthenticationVO() {
    return authenticationVO;
  }

  //  public static void initConfig(HttpServletRequest request) throws ServletException {
  //    if (Config.getProperties().isEmpty()) {
  //      String context = request.getContextPath();
  //      if (context != null && context.startsWith("/")) {
  //        String propertyFilename = context.substring(1) + ".properties";
  //        logger.info("Loading properties from " + propertyFilename);
  //        try {
  //          InputStream propertyStream = ResourceUtil.getResourceAsStream(propertyFilename, Aa.class.getClassLoader());
  //          Config.getProperties().load(propertyStream);
  //          Config.setLoaded(true);
  //          // propertyStream.close();
  //        } catch (Exception e) {
  //          throw new ServletException(e);
  //        }
  //      }
  //    }
  //  }

  /**
   * Create a link to the login page with the referer information and a random TAN.
   *
   * @param request the servlet request.
   * @return a URL
   */
  public static String getLoginLink(HttpServletRequest request) {
    //    if (!Config.isLoaded()) {
    //      initConfig(request);
    //    }

    //    String tan;
    //    do {
    //      tan = TanStore.createTan();
    //    } while (!TanStore.storeTan(tan));

    String from = request.getRequestURI();

    String page = PropertyReader.getProperty(PropertyReader.INGE_AA_INSTANCE_URL);

    String query = request.getQueryString();
    if (query != null && !query.isEmpty()) {
      query = "?" + query;
    } else {
      query = "";
    }

    String tan = TanStore.getNewTan();

    return page + "login.jsp?from=" + URLEncoder.encode(from + query, StandardCharsets.UTF_8) + "&tan="
        + URLEncoder.encode(tan, StandardCharsets.UTF_8);
  }


}
