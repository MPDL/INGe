/*
 *
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

package de.mpg.mpdl.inge.pubman.web.util.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO.PredefinedRoles;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pubman.web.HomePage;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.depositorWS.DepositorWSSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.vos.AffiliationVOPresentation;
import de.mpg.mpdl.inge.service.aa.IpListProvider.IpRange;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.pubman.impl.UserAccountServiceImpl;
import de.mpg.mpdl.inge.service.util.GrantUtil;
import de.mpg.mpdl.inge.util.PropertyReader;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.bean.SessionScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * LoginHelper.java Class for providing helper methods for login / logout mechanism
 *
 * @author: Tobias Schraut, created 07.03.2007
 * @version: $Revision$ $LastChangedDate$ Revised by ScT: 21.08.2007
 */
@ManagedBean(name = "LoginHelper")
@SessionScoped
@SuppressWarnings("serial")
public class LoginHelper extends FacesBean {
  private static final Logger logger = LogManager.getLogger(LoginHelper.class);

  public static final String PARAMETERNAME_USERHANDLE = "authenticationToken";

  private AccountUserDbVO accountUser;

  private List<AffiliationVOPresentation> userAccountAffiliations;
  // private List<UserGroupVO> userAccountUserGroups;

  private String authenticationToken;
  private String displayUserName;
  // private String eSciDocUserHandle;
  private String password;
  private String username;

  private Principal principal;

  private boolean detailedMode;
  private boolean loggedIn;

  private IpRange currentIp;
  private String userIp;

  public LoginHelper() {
    this.init();
  }

  private void init() {
    this.authenticationToken = null;
    this.displayUserName = null;
    this.password = null;
    this.username = null;

    this.detailedMode = false;
    this.loggedIn = false;

    ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();

    // Delete old cookie, if there
    UserAccountServiceImpl.removeTokenCookie((HttpServletRequest) ec.getRequest(), (HttpServletResponse) ec.getResponse());


    userIp = ec.getRequestHeaderMap().get("X-Forwarded-For");
    // try to fallback to getRemoteAddr(), if Proxy doesn't fill X-Forwarded-For header
    if (userIp == null) {
      HttpServletRequest request = (HttpServletRequest) ec.getRequest();
      userIp = request.getRemoteAddr();
    }

    logger.info("Init LoginHelper with IP " + userIp);

    if (userIp != null) {
      try {
        currentIp = ApplicationBean.INSTANCE.getIpListProvider().getMatch(userIp);
        principal = ApplicationBean.INSTANCE.getUserAccountService().login((HttpServletRequest) ec.getRequest(),
            (HttpServletResponse) ec.getResponse());

      } catch (Exception e) {
        logger.error("Error logging in anonymous user", e);
      }
    }
  }

  /**
   * Method checks if the user is already logged in and inserts the escidoc user handle.
   *
   * @return String empty navigation string for reloading the current page
   * @throws IOException IOException
   * @throws TechnicalException TechnicalException
   */
  public String login() {
    try {
      logger.info("Try to login: " + this.getUsername());
      HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
      HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
      this.principal = ApplicationBean.INSTANCE.getUserAccountService().login(this.getUsername(), this.getPassword(), request, response);

      if (principal != null) {
        this.accountUser = getPrincipal().getUserAccount();
        this.authenticationToken = getPrincipal().getJwToken();
        this.loggedIn = true;
        this.detailedMode = true;

        logger.info("Login succeeded: " + this.getUsername());

        ((ContextListSessionBean) FacesTools.findBean("ContextListSessionBean")).init();
        // reinitialize ContextList
        if (GrantUtil.hasRole(accountUser, PredefinedRoles.DEPOSITOR)) {
          final DepositorWSSessionBean depWSSessionBean = FacesTools.findBean("DepositorWSSessionBean");
          // enable the depositor links if necessary
          depWSSessionBean.setMyWorkspace(true);
          depWSSessionBean.setDepositorWS(true);
          depWSSessionBean.setNewSubmission(true);
        }
      }
    } catch (final AuthenticationException e) {
      logger.error("Error while logging in", e);
      if (e.getMessage().contains("blocked")) {
        this.error(this.getMessage("LoginBlocked"));
      }
      if (e.getMessage().contains("change password")) {
        this.error("<a href=\"" + ApplicationBean.INSTANCE.getPubmanInstanceUrl() + ApplicationBean.INSTANCE.getInstanceContextPath()
            + "/faces/UserAccountOptions.jsp\" style=\"color:red\">" + this.getMessage("LoginPasswordChangeRequired") + "</a>");
      } else {
        this.error(this.getMessage("LoginError"));
      }
    } catch (final Exception e) {
      logger.error("Error while logging in", e);
      this.error(this.getMessage("LoginTechnicalError"));
    }

    return "";
  }

  public String logout() {
    logger.info("Try to logout: " + this.getUsername());
    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

    try {
      ApplicationBean.INSTANCE.getUserAccountService().logout(this.authenticationToken, request, response);
      logger.info("Logout succeeded: " + this.getUsername());
    } catch (Exception e) {
      logger.error("Error while logging out", e);
    }

    final HttpSession session = (HttpSession) FacesTools.getExternalContext().getSession(false);
    session.invalidate();
    logger.info("Session invalidated: " + this.getUsername());


    this.init();

    return HomePage.LOAD_HOMEPAGE;
  }


  public String getAuthenticationToken() {
    return this.authenticationToken;
  }

  public void setAuthenticationToken(String authenticationToken) {
    this.authenticationToken = authenticationToken;
  }

  public AccountUserDbVO getAccountUser() {
    return this.accountUser;
  }

  public void setAccountUser(AccountUserDbVO accountUser) {
    this.accountUser = accountUser;
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

  public String getUser() {
    return this.authenticationToken;
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

  @Override
  public String toString() {
    return "[Login: " + (this.loggedIn ? "User " + this.authenticationToken + "(" + this.accountUser + ") is logged in]"
        : "No user is logged in (" + this.accountUser + ")]");
  }

  /**
   * JSF Wrapper for isAdmin()
   *
   * @return
   */
  public boolean getIsAdmin() {
    return this.isLoggedIn() && GrantUtil.hasRole(accountUser, PredefinedRoles.SYSADMIN);
  }

  /**
   * JSF Wrapper for isAdmin()
   *
   * @return
   */
  public boolean getIsLocalAdmin() {
    return this.isLoggedIn() && GrantUtil.hasRole(accountUser, PredefinedRoles.LOCAL_ADMIN);
  }

  /**
   * JSF Wrapper for isModerator()
   *
   * @return
   */
  public boolean getIsModerator() {
    return this.isLoggedIn() && GrantUtil.hasRole(accountUser, PredefinedRoles.MODERATOR);
  }

  /**
   * JSF Wrapper for isDepositor()
   *
   * @return
   */
  public boolean getIsDepositor() {
    return this.isLoggedIn() && GrantUtil.hasRole(accountUser, PredefinedRoles.DEPOSITOR);
  }

  /**
   * JSF Wrapper for isReporter()
   *
   * @return
   */
  public boolean getIsReporter() {
    return this.isLoggedIn() && GrantUtil.hasRole(accountUser, PredefinedRoles.REPORTER);
  }

  public List<AffiliationVOPresentation> getAccountUsersAffiliations() throws Exception {
    if (this.userAccountAffiliations == null) {
      this.userAccountAffiliations = new ArrayList<>();
      if (accountUser.getAffiliation() != null) {
        final AffiliationDbVO orgUnit =
            ApplicationBean.INSTANCE.getOrganizationService().get(accountUser.getAffiliation().getObjectId(), null);
        this.userAccountAffiliations.add(new AffiliationVOPresentation(orgUnit));
      }

    }

    return this.userAccountAffiliations;
  }

  // only active UserGroups!
  /*
   * public List<UserGroupVO> getAccountUsersUserGroups() { if (this.userAccountUserGroups == null
   * && this.getAccountUser() != null && this.getAccountUser().getReference() != null) { final
   * HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
   * filterParams.put("operation", new String[] {"searchRetrieve"}); filterParams.put("version", new
   * String[] {"1.1"}); // String orgId = "escidoc:persistent25"; filterParams.put("query", new
   * String[] {"\"/structural-relations/user/id\"=" +
   * this.getAccountUser().getReference().getObjectId() + " and " +
   * "\"/properties/active\"=\"true\""}); // filterParams.put("query", new String[]
   * {"\"http://escidoc.de/core/01/properties/user\"=" + //
   * getAccountUser().getReference().getObjectId() + " and " + //
   * "\"http://escidoc.de/core/01/properties/active\"=\"true\""});
   *
   * }
   *
   * return this.userAccountUserGroups; }
   */

  /**
   * @return the userGrants (with inherited grants)
   */
  public List<GrantVO> getUserGrants() {
    return this.accountUser.getGrantList();
  }

  /**
   * @return the Link to the UserAccountOptions page
   */
  public String getUserAccountOptionsLink() {
    return "loadUserAccountOptionsPage";
  }

  /**
   * sets whether detailedMode is activated or not
   *
   * @param detailedMode the detailedMode to set
   */
  public void setDetailedMode(boolean detailedMode) {
    this.detailedMode = detailedMode;
  }

  /**
   * returns whether detailedMode is activated or not
   *
   * @return detailedMode [boolean]
   */
  public boolean isDetailedMode() {
    return this.detailedMode;
  }

  public IpRange getCurrentIp() {
    return currentIp;

  }

  public Principal getPrincipal() {
    return principal;
  }

  public void setPrincipal(Principal principal) {
    this.principal = principal;
  }

  public String getInstanceAndUserIp() {
    logger.info("Clusternode: " + PropertyReader.getProperty(PropertyReader.INGE_CLUSTER_NODE_NAME) + " / Userip: " + userIp);
    return "Clusternode: " + PropertyReader.getProperty(PropertyReader.INGE_CLUSTER_NODE_NAME) + " / Userip: " + userIp;
  }
}
