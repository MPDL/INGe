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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.inge.model.referenceobjects.AccountUserRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.UserAttributeVO;
import de.mpg.mpdl.inge.model.valueobjects.UserGroupVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.depositorWS.DepositorWSSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.vos.AffiliationVOPresentation;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.services.IngeServiceException;
import de.mpg.mpdl.inge.util.PropertyReader;

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
  private static final Logger logger = Logger.getLogger(LoginHelper.class);

  public static final String PARAMETERNAME_USERHANDLE = "authenticationToken";

  private AccountUserVO accountUser = new AccountUserVO();

  private List<AffiliationVOPresentation> userAccountAffiliations;
  private List<GrantVO> userGrants;
  private List<UserGroupVO> userAccountUserGroups;

  private String authenticationToken = null;
  private String displayUserName = "";
  private String eSciDocUserHandle = null;
  private String password = "";
  private String username = "";

  private boolean detailedMode = false;
  private boolean loggedIn = false;

  public LoginHelper() {}

  public String getESciDocUserHandle() {
    return this.eSciDocUserHandle;
  }

  public void setESciDocUserHandle(String eSciDocUserHandle) {
    this.eSciDocUserHandle = eSciDocUserHandle;
  }

  /**
   * Method checks if the user is already logged in and inserts the escidoc user handle.
   * 
   * @return String empty navigation string for reloading the current page
   * @throws IOException IOException
   * @throws ServiceException ServiceException
   * @throws TechnicalException TechnicalException
   */
  public void login() {


    try {
      final String token =
          ApplicationBean.INSTANCE.getUserAccountService().login(getUsername(), getPassword());
      this.accountUser = ApplicationBean.INSTANCE.getUserAccountService().get(token);
      if (token != null) {
        this.authenticationToken = token;
        this.loggedIn = true;
        this.detailedMode = true;
        ((ContextListSessionBean) FacesTools.findBean("ContextListSessionBean")).init();
        // reinitialize ContextList
        if (this.accountUser.isDepositor()) {
          final DepositorWSSessionBean depWSSessionBean =
              (DepositorWSSessionBean) FacesTools.findBean("DepositorWSSessionBean");
          // enable the depositor links if necessary
          depWSSessionBean.setMyWorkspace(true);
          depWSSessionBean.setDepositorWS(true);
          depWSSessionBean.setNewSubmission(true);
        }

      }
    } catch (AaException e) {
      logger.error("Error while logging in", e);
      error("Username and/or password not correct");
    } catch (Exception e) {
      logger.error("Error while logging in", e);
      error("Technical error while logging in.");
    }

  }


  public void logout() {

    final HttpSession session = (HttpSession) FacesTools.getExternalContext().getSession(false);
    session.invalidate();

  }

  public void logoutCallBySessionListener() {
    this.authenticationToken = null;
    this.loggedIn = false;
    this.detailedMode = false;
  }


  // /**
  // * changes the language in the navigation menu (according to login state)
  // *
  // * @param bundle the resource bundle of the currently selected language
  // */
  // public void changeLanguage(ResourceBundle bundle) {
  // // change the language for the Depositor WS navigation info
  // DepositorWSSessionBean depWSSessionBean =
  // (DepositorWSSessionBean) FacesTools.findBean(DepositorWSSessionBean.class);
  // // change the button language
  //
  // if (this.authenticationToken == null || this.authenticationToken.equals("")) {
  // this.btnLoginLogout = "login_btLogin";
  // } else {
  // this.btnLoginLogout = "login_btLogout";
  // if (this.accountUser != null) {
  // if (this.accountUser.isDepositor()) {
  // depWSSessionBean.setMyWorkspace(true); // getLabel("mainMenu_lblMyWorkspace")
  // depWSSessionBean.setDepositorWS(true); // getLabel("mainMenu_lnkDepositor")
  // depWSSessionBean.setNewSubmission(true); // getLabel("actionMenu_lnkNewSubmission")
  // }
  // }
  // }
  // }



  public String getAuthenticationToken() {
    return this.authenticationToken;
  }

  public void setAuthenticationToken(String authenticationToken) {
    this.authenticationToken = authenticationToken;
  }

  public AccountUserVO getAccountUser() {
    return this.accountUser;
  }

  public void setAccountUser(AccountUserVO accountUser) {
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
    return "[Login: "
        + (this.loggedIn ? "User " + this.authenticationToken + "(" + this.accountUser
            + ") is logged in]" : "No user is logged in (" + this.accountUser + ")]");
  }

  /**
   * JSF Wrapper for isModerator()
   * 
   * @return
   */
  public boolean getIsModerator() {
    return this.isLoggedIn() && this.getAccountUser().isModerator();
  }

  /**
   * JSF Wrapper for isDepositor()
   * 
   * @return
   */
  public boolean getIsDepositor() {
    return this.isLoggedIn() && this.getAccountUser().isDepositor();
  }

  /**
   * JSF Wrapper for isReporter()
   * 
   * @return
   */
  public boolean getIsReporter() {
    return this.isLoggedIn() && this.getAccountUser().isReporter();
  }

  public List<AffiliationVOPresentation> getAccountUsersAffiliations() throws Exception {
    if (this.userAccountAffiliations == null) {
      this.userAccountAffiliations = new ArrayList<AffiliationVOPresentation>();
      for (final UserAttributeVO ua : this.getAccountUser().getAttributes()) {
        if ("o".equals(ua.getName())) {
          final AffiliationVO orgUnit =
              ApplicationBean.INSTANCE.getOrganizationService().get(ua.getValue(), null);
          this.userAccountAffiliations.add(new AffiliationVOPresentation(orgUnit));
        }
      }
    }
    return this.userAccountAffiliations;

  }

  // only active UserGroups!
  public List<UserGroupVO> getAccountUsersUserGroups() {
    if (this.userAccountUserGroups == null && this.getAccountUser() != null
        && this.getAccountUser().getReference() != null) {
      final HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
      filterParams.put("operation", new String[] {"searchRetrieve"});
      filterParams.put("version", new String[] {"1.1"});
      // String orgId = "escidoc:persistent25";
      filterParams.put("query", new String[] {"\"/structural-relations/user/id\"="
          + this.getAccountUser().getReference().getObjectId() + " and "
          + "\"/properties/active\"=\"true\""});
      // filterParams.put("query", new String[] {"\"http://escidoc.de/core/01/properties/user\"=" +
      // getAccountUser().getReference().getObjectId() + " and " +
      // "\"http://escidoc.de/core/01/properties/active\"=\"true\""});

      /*
       * UserGroupList ugl = new UserGroupList(filterParams, getESciDocUserHandle());
       * userAccountUserGroups = ugl.getUserGroupLists();
       */
    }
    return this.userAccountUserGroups;
  }

  public boolean getIsYearbookEditor() {
    // toDo: find better way how to do this
    final ContextListSessionBean clsb =
        (ContextListSessionBean) FacesTools.findBean("ContextListSessionBean");
    if (this.getIsDepositor() && clsb.getYearbookContextListSize() > 0) {
      return true;
    }

    if (this.getAccountUsersUserGroups() != null) {
      for (final UserGroupVO ug : this.getAccountUsersUserGroups()) {
        if (ug.getLabel().matches("\\d*? - Yearbook User Group for.*?")) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * @return the userGrants (with inherited grants)
   */
  public List<GrantVO> getUserGrants() {
    return this.accountUser.getGrants();
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



}
