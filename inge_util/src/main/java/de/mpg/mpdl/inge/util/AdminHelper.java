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

package de.mpg.mpdl.inge.util;

/**
 *
 * Utility class for pubman logic.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class AdminHelper {
  //  private static final Logger logger = LogManager.getLogger(AdminHelper.class);
  //
  //  private static String adminUserHandle = null;
  //  private static Date loginTime = null;
  //
  //  /**
  //   * Logs in the given user with the given password.
  //   *
  //   * @param userid The id of the user to log in.
  //   * @param password The password of the user to log in.
  //   * @return The handle for the logged in user.
  //   * @throws HttpException
  //   * @throws IOException
  //   * @throws ServiceException
  //   * @throws URISyntaxException
  //   */
  //  public static String loginUser(String userid, String password) throws HttpException, IOException, ServiceException, URISyntaxException {
  //    String frameworkUrl = PropertyReader.getProperty(PropertyReader.ESCIDOC_FRAMEWORK_ACCESS_LOGIN_URL); // nur noch fuer Migration
  //
  //    int delim1 = frameworkUrl.indexOf("//");
  //    int delim2 = frameworkUrl.indexOf(":", delim1);
  //
  //    String host;
  //    int port;
  //
  //    if (delim2 > 0) {
  //      host = frameworkUrl.substring(delim1 + 2, delim2);
  //      port = Integer.parseInt(frameworkUrl.substring(delim2 + 1));
  //    } else {
  //      host = frameworkUrl.substring(delim1 + 2);
  //      port = 80;
  //    }
  //
  //    HttpClient client = new HttpClient();
  //    client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
  //
  //    PostMethod login = new PostMethod(frameworkUrl + "/aa/j_spring_security_check");
  //    login.addParameter("j_username", userid);
  //    login.addParameter("j_password", password);
  //
  //    //    ProxyHelper.executeMethod(client, login);
  //    client.executeMethod(login);
  //
  //    login.releaseConnection();
  //    CookieSpec cookiespec = CookiePolicy.getDefaultSpec();
  //    Cookie[] logoncookies = cookiespec.match(host, port, "/", false, client.getState().getCookies());
  //
  //    Cookie sessionCookie = logoncookies[0];
  //
  //    PostMethod postMethod = new PostMethod(frameworkUrl + "/aa/login");
  //    postMethod.addParameter("target", frameworkUrl);
  //    client.getState().addCookie(sessionCookie);
  //    //    ProxyHelper.executeMethod(client, postMethod);
  //    client.executeMethod(postMethod);
  //
  //    if (HttpServletResponse.SC_SEE_OTHER != postMethod.getStatusCode()) {
  //      throw new HttpException("Wrong status code: " + login.getStatusCode());
  //    }
  //
  //    String userHandle = null;
  //    Header headers[] = postMethod.getResponseHeaders();
  //    for (int i = 0; i < headers.length; ++i) {
  //      if ("Location".equals(headers[i].getName())) {
  //        String location = headers[i].getValue();
  //        int index = location.indexOf('=');
  //        userHandle = new String(Base64.getDecoder().decode(location.substring(index + 1, location.length())));
  //      }
  //    }
  //
  //    if (userHandle == null) {
  //      throw new ServiceException("User not logged in.");
  //    }
  //
  //    return userHandle;
  //  }
  //
  //  /**
  //   * Gets the admin users user handle.
  //   *
  //   * @return The admin's user handle.
  //   */
  //  public static String getAdminUserHandle() {
  //    Date now = new Date();
  //
  //    // Renew every hour
  //    if (adminUserHandle == null || loginTime == null || loginTime.getTime() < now.getTime() - 1 * 60 * 60 * 1000) {
  //      try {
  //        loginTime = new Date();
  //        adminUserHandle = loginUser(PropertyReader.getProperty(PropertyReader.ESCIDOC_FRAMEWORK_ADMIN_USERNAME),
  //            PropertyReader.getProperty(PropertyReader.ESCIDOC_FRAMEWORK_ADMIN_PASSWORD));
  //      } catch (Exception e) {
  //        logger.error("Exception logging on admin user.", e);
  //      }
  //    }
  //
  //    return adminUserHandle;
  //  }
}
