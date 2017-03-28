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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Class for providing login / logout functionality and coresponds to the Login.jspf.
 * 
 * @author: Tobias Schraut, created 31.01.2007
 * @version: $Revision$ $LastChangedDate$ Revised by ScT: 20.08.2007
 */
@ManagedBean(name = "Login")
@SessionScoped
@SuppressWarnings("serial")
public class Login extends FacesBean {
  private static final Logger logger = Logger.getLogger(Login.class);

  public static String LOGIN_URL = "/aa/login";
  // public static String LOGOUT_URL = "/aa/logout/clear.jsp";

  private String btnLoginLogout = "login_btLogin";
  private String displayUserName = "";
  private String password = "";
  private String username = "";

  private boolean loggedIn = false;

  public Login() {}

  /**
   * one method for login and logout according to the current login state
   * 
   * @return String empty navigation string for reloading the page
   */
  public void loginLogout() throws ServletException, IOException, ServiceException,
      URISyntaxException {
    String token = getLoginHelper().getAuthenticationToken();

    if (getLoginHelper().isLoggedIn() && getLoginHelper().getAuthenticationToken() != null) {
      // logout mechanism
      getLoginHelper().setBtnLoginLogout("login_btLogin");
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
        HttpSession session = (HttpSession) FacesTools.getExternalContext().getSession(false);
        session.invalidate();
      }
    } else {
      login();
    }
  }

  public void login() {
    String token = getLoginHelper().obtainToken();
    if (token != null) {
      this.loggedIn = true;
      try {
        getLoginHelper().insertLogin();
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
    this.loggedIn = false;
    getLoginHelper().logout("");
  }

  /**
   * method for brutal logout if authentication errors occur in the framework
   * 
   * @return String navigation string for loading the login error page
   */
  public void forceLogout() {
    try {
      FacesTools.getExternalContext().redirect(
          PropertyReader.getLoginUrl() + LOGIN_URL + "?target="
              + FacesTools.getRequest().getRequestURL().toString());
    } catch (IOException e) {
      logger.error("Could not redirect to Fremework login page in forceLogout", e);
    }
  }

  /**
   * method for brutal logout if authantication errors occur in the framework
   * 
   * @return String navigation string for loading the login error page
   */
  public void forceLogout(String itemID) {
    try {
      String targetUrl = CommonUtils.getGenericItemLink(itemID);
      FacesTools.getExternalContext().redirect(
          PropertyReader.getLoginUrl() + LOGIN_URL + "?target="
              + URLEncoder.encode(targetUrl, "UTF-8"));
    } catch (Exception e) {
      logger.error("Could not redirect to Fremework login page", e);
    }
  }

  public String getBtnLoginLogout() {
    return this.btnLoginLogout;
  }

  public void setBtnLoginLogout(String btnLoginLogout) {
    this.btnLoginLogout = btnLoginLogout;
  }

  public boolean isLoggedIn() {
    return this.loggedIn;
  }

  public boolean getLoggedIn() {
    return this.loggedIn;
  }

  public void setLoggedIn(boolean loggedIn) {
    this.loggedIn = loggedIn;
  }

  public String getDisplayUserName() {
    return this.displayUserName;
  }

  public void setDisplayUserName(String displayUserName) {
    this.displayUserName = displayUserName;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
