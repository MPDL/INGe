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

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.inge.aa.AuthenticationVO;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.util.PropertyReader;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */

public class IngeAaClientFinish extends FinalClient {

  private static final Logger logger = LogManager.getLogger(IngeAaClientFinish.class);

  @Override
  protected AuthenticationVO finalizeAuthentication(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String token = request.getParameter("token");

    AccountUserDbVO user = getUser(token);

    if (null != user) {
      try {

        AuthenticationVO authenticationVO = new AuthenticationVO();
        authenticationVO.setType(AuthenticationVO.Type.USER);
        authenticationVO.setUsername(user.getLoginname());
        authenticationVO.setUserId(user.getObjectId());
        authenticationVO.setFullName(user.getName());
        authenticationVO.setToken(token);

        for (GrantVO grantVO : user.getGrantList()) {
          if (null == grantVO.getObjectRef()) {
            AuthenticationVO.Role role = authenticationVO.new Role();

            role.setKey(grantVO.getRole());
            authenticationVO.getRoles().add(role);
          } else {
            AuthenticationVO.Grant grant = authenticationVO.new Grant();
            grant.setKey(grantVO.getRole());
            grant.setValue(grantVO.getObjectRef());
            authenticationVO.getGrants().add(grant);
          }
        }
        return authenticationVO;
      } catch (Exception e) {
        throw new ServletException(e);
      }
    }

    return null;
  }

  private static AccountUserDbVO getUser(String token) {

    try {
      Response resp = Request.Get(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL) + "/rest/login/who")
          .addHeader("Authorization", token).execute();
      return MapperFactory.getObjectMapper().readValue(resp.returnContent().asStream(), AccountUserDbVO.class);
    } catch (Exception e) {
      logger.error("Error while parsing AccountUserIbject", e);
      return null;

    }


  }

  public static String loginInInge(String username, String password) throws Exception {
    String pw = username + ":" + password;
    Response resp = Request.Post(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL) + "/rest/login")
        .bodyString(pw, ContentType.TEXT_PLAIN).execute();
    HttpResponse httpResp = resp.returnResponse();

    if (HttpStatus.SC_OK == httpResp.getStatusLine().getStatusCode()) {

      String token = httpResp.getLastHeader("Token").getValue();
      return token;
    }

    return null;


  }

  public static void logoutInInge(String token) throws Exception {
    Response resp = Request.Get(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL) + "/rest/logout")
        .addHeader("Authorization", token).execute();
    HttpResponse httpResp = resp.returnResponse();

    if (HttpStatus.SC_OK != httpResp.getStatusLine().getStatusCode()) {
      throw new RuntimeException("Error while logging out");

    }


  }
}
