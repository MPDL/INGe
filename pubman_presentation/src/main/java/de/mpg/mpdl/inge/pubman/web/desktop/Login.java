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

package de.mpg.mpdl.inge.pubman.web.desktop;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pubman.web.appbase.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.LoginHelper;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Class for providing login / logout functionality and coresponds to the Login.jspf.
 * 
 * @author: Tobias Schraut, created 31.01.2007
 * @version: $Revision$ $LastChangedDate$ Revised by ScT: 20.08.2007
 */
@SuppressWarnings("serial")
public class Login extends FacesBean {
  public static String LOGIN_URL = "/aa/login";
  public static String LOGOUT_URL = "/aa/logout/clear.jsp";
  final public static String BEAN_NAME = "Login";
  private String btnLoginLogout = "login_btLogin";
  private String displayUserName = "";
  private boolean loggedIn = false;
  private static Logger logger = Logger.getLogger(Login.class);
  // Username and password for login form
  private String username = "";
  private String password = "";


  /**
   * public constructor
   */
  public Login() {
    this.init();
  }

  /**
   * Callback method that is called whenever a page is navigated to, either directly via a URL, or
   * indirectly via page navigation.
   */
  @Override
  public void init() {
    // Perform initializations inherited from our superclass
    super.init();
  }

  /**
   * gets the parameters out of the faces context
   * 
   * @param name Name of the parameter that should be found in the faces context
   * @return String value of the faces context parameter
   */
  public String getFacesParamValue(String name) {
    return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
        .get(name);
  }

  /**
   * one method for login and logout according to the current login state
   * 
   * @return String empty navigation string for reloading the page
   */
  public String loginLogout() throws ServletException, IOException, ServiceException,
      URISyntaxException {
    FacesContext fc = FacesContext.getCurrentInstance();
    LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
    String token = loginHelper.getAuthenticationToken();

    if (loginHelper.isLoggedIn() && loginHelper.getAuthenticationToken() != null) {
      // logout mechanism
      loginHelper.setBtnLoginLogout("login_btLogin");
      if (token != null) {
        long zeit = -System.currentTimeMillis();

        zeit += System.currentTimeMillis();
        logger.info("logout->" + zeit + "ms");
        // loginHelper.setLoggedIn(false);
        // loginHelper.getAccountUser().setName("");
        // loginHelper.setESciDocUserHandle(null);
        // depWSSessionBean.setMyWorkspace(false);
        // depWSSessionBean.setDepositorWS(false);
        // depWSSessionBean.setNewSubmission(false);

        // Logout mechanism

        logout();
        HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
        session.invalidate();
      }
    } else {
      login(loginHelper);
    }
    return "";
  }

  public void login(LoginHelper loginHelper) {
    String token = loginHelper.obtainToken();
    if (token != null) {
      this.loggedIn = true;
      try {
        loginHelper.insertLogin();
      } catch (IOException | ServiceException | TechnicalException | URISyntaxException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  /**
   * @param loginHelper
   * @param fc
   * @throws IOException
   * @throws ServiceException
   * @throws URISyntaxException
   */
  public void logout() throws IOException, ServiceException, URISyntaxException {
    LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);

    this.loggedIn = false;
    loginHelper.logout("");

  }

  /**
   * method for brutal logout if authentication errors occur in the framework
   * 
   * @return String navigation string for loading the login error page
   */
  public String forceLogout() {
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();

    try {
      fc.getExternalContext().redirect(
          PropertyReader.getLoginUrl() + LOGIN_URL + "?target="
              + request.getRequestURL().toString());
    } catch (IOException e) {
      logger.error("Could not redirect to Fremework login page in forceLogout", e);
    }

    return "";
  }

  /**
   * method for brutal logout if authantication errors occur in the framework
   * 
   * @return String navigation string for loading the login error page
   */
  public String forceLogout(String itemID) {
    FacesContext fc = FacesContext.getCurrentInstance();
//    HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
    try {
      String targetUrl = CommonUtils.getGenericItemLink(itemID);
      fc.getExternalContext().redirect(
          PropertyReader.getLoginUrl() + LOGIN_URL + "?target="
              + URLEncoder.encode(targetUrl, "UTF-8"));
      // fc.getExternalContext().redirect(getLoginUrlFromCurrentBreadcrumb());
    } catch (Exception e) {
      logger.error("Could not redirect to Fremework login page", e);
    }



    return "";
  }

  // Getters and Setters
  public String getBtnLoginLogout() {
    return btnLoginLogout;
  }

  public void setBtnLoginLogout(String btnLoginLogout) {
    this.btnLoginLogout = btnLoginLogout;
  }

  public boolean isLoggedIn() {
    return loggedIn;
  }

  public boolean getLoggedIn() {
    return loggedIn;
  }

  public void setLoggedIn(boolean loggedIn) {
    this.loggedIn = loggedIn;
  }

  public String getDisplayUserName() {
    return displayUserName;
  }

  public void setDisplayUserName(String displayUserName) {
    this.displayUserName = displayUserName;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

//  private String getLoginUrlFromCurrentBreadcrumb() throws IOException, URISyntaxException,
//      ServiceException {
//    BreadcrumbItemHistorySessionBean breadCrumbHistory =
//        (BreadcrumbItemHistorySessionBean) getSessionBean(BreadcrumbItemHistorySessionBean.class);
//
//    String pubmanUrl =
//        PropertyReader.getProperty("escidoc.pubman.instance.url")
//            + PropertyReader.getProperty("escidoc.pubman.instance.context.path");
//    if (!pubmanUrl.endsWith("/"))
//      pubmanUrl = pubmanUrl + "/";
//
//    // Use double URL encoding here because the login mechanism gives back the decoded URL
//    // parameters.
//    String url =
//        PropertyReader.getLoginUrl()
//            + LOGIN_URL
//            + "?target="
//            + pubmanUrl
//            + "faces/"
//            + URLEncoder.encode(
//                URLEncoder.encode(breadCrumbHistory.getCurrentItem().getPage(), "UTF-8"), "UTF-8");
//    return url;
//  }
}
