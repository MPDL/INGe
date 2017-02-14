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

package de.mpg.mpdl.inge.pubman.web.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.mpg.mpdl.inge.es.handler.OrganizationServiceHandler;
import de.mpg.mpdl.inge.model.referenceobjects.AccountUserRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.UserAttributeVO;
import de.mpg.mpdl.inge.model.valueobjects.UserGroupVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pubman.web.appbase.FacesBean;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.depositorWS.DepositorWSSessionBean;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * LoginHelper.java Class for providing helper methods for login / logout mechanism
 * 
 * @author: Tobias Schraut, created 07.03.2007
 * @version: $Revision$ $LastChangedDate$ Revised by ScT: 21.08.2007
 */
public class LoginHelper extends FacesBean {

  private static Logger logger = Logger.getLogger(LoginHelper.class);

  public static final String PARAMETERNAME_USERHANDLE = "authenticationToken";
  public final static String BEAN_NAME = "LoginHelper";
  private String eSciDocUserHandle = null;

  public String getESciDocUserHandle() {
    return eSciDocUserHandle;
  }

  public void setESciDocUserHandle(String eSciDocUserHandle) {
    this.eSciDocUserHandle = eSciDocUserHandle;
  }

  private String authenticationToken = null;
  private String btnLoginLogout = "login_btLogin";
  private String displayUserName = "";
  private String username = "";
  private String password = "";
  private boolean loggedIn = false;
  private boolean wasLoggedIn = false;
  private AccountUserVO accountUser = new AccountUserVO();
  private List<AffiliationVOPresentation> userAccountAffiliations;

  private List<UserGroupVO> userAccountUserGroups;
  private List<GrantVO> userGrants;
  private boolean detailedMode = false;


  private OrganizationServiceHandler organizationServiceHandler;

  /**
   * Public constructor.
   */
  public LoginHelper() {}


  /**
   * Method checks if the user is already logged in and inserts the escidoc user handle.
   * 
   * @return String empty navigation string for reloading the current page
   * @throws IOException IOException
   * @throws ServiceException ServiceException
   * @throws TechnicalException TechnicalException
   */
  public String insertLogin()
      throws IOException, ServiceException, TechnicalException, URISyntaxException {
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
    String token = this.obtainToken();
    if (this.authenticationToken == null || this.authenticationToken.equals("")) {
      if (token != null) {
        this.authenticationToken = token;
        this.loggedIn = true;
        this.wasLoggedIn = true;
        this.setDetailedMode(true);

      }
    }
    if (this.authenticationToken != null && !this.authenticationToken.equals("")
        && this.wasLoggedIn) {
      fetchAccountUser(this.authenticationToken);
      this.btnLoginLogout = "login_btLogout";
      // reinitialize ContextList
      ((ContextListSessionBean) getSessionBean(ContextListSessionBean.class)).init();
    }

    // enable the depositor links if necessary
    if (this.accountUser.isDepositor()) {
      DepositorWSSessionBean depWSSessionBean =
          (DepositorWSSessionBean) getSessionBean(DepositorWSSessionBean.class);

      depWSSessionBean.setMyWorkspace(true); // getLabel("mainMenu_lblMyWorkspace")
      depWSSessionBean.setDepositorWS(true); // getLabel("mainMenu_lnkDepositor")
      depWSSessionBean.setNewSubmission(true); // getLabel("actionMenu_lnkNewSubmission")
    }
    return "";

  }

  /**
   * retrieves the account user with the user handle
   * 
   * @param token user handle that is given back from FIZ framework (is needed here to call
   *        framework methods)
   * @throws ServletException, ServiceException, TechnicalException
   */
  public void fetchAccountUser(String token)
      throws WebserverSystemException, SqlDatabaseSystemException, RemoteException,
      MalformedURLException, ServiceException, TechnicalException, URISyntaxException {

    JsonNode rawUser = null;
    rawUser = this.obtainUser();
    AccountUserRO userRO = new AccountUserRO();
    userRO.setObjectId("/aa/user-account/escidoc:" + rawUser.path("exid").asText());
    userRO.setTitle(rawUser.path("lastName").asText() + ", " + rawUser.path("firstName").asText());
    this.accountUser = new AccountUserVO();

    this.accountUser.setReference(userRO);
    List<UserAttributeVO> attributes = new ArrayList<UserAttributeVO>();
    UserAttributeVO email = new UserAttributeVO();
    email.setName("email");
    email.setValue(rawUser.path("email").asText());
    UserAttributeVO ou = new UserAttributeVO();
    ou.setName("o");
    ou.setValue(rawUser.path("ouid").asText());
    attributes.add(email);
    attributes.add(ou);
    this.accountUser.setAttributes(attributes);
    this.accountUser.setActive(rawUser.path("active").asBoolean());
    this.accountUser
        .setName(rawUser.path("lastName").asText() + ", " + rawUser.path("firstName").asText());
    this.setAuthenticationToken(token);
    this.setLoggedIn(true);
    this.setWasLoggedIn(true);
    this.userGrants = new ArrayList<GrantVO>();

    // get all user-grants

    JsonNode grants = rawUser.path("grants");

    if (grants.isArray()) {
      for (JsonNode grant : grants) {

        GrantVO grantVo = new GrantVO();
        grantVo.setGrantedTo("/aa/user-account/escidoc:" + rawUser.path("exid").asText());
        grantVo.setGrantType("");
        if (grant.path("targetId").asText().contains("all")) {

        } else {
          grantVo.setObjectRef(grant.path("targetId").asText());
          String roleName = grant.path("role").path("name").asText();
          grantVo.setRole((String) "escidoc:role-" + roleName.toLowerCase());
          this.userGrants.add(grantVo);
        }

      }
    }


    // NOTE: The block below must not be removed, as it sets the this.accountUser grants
    List<GrantVO> setterGrants = this.accountUser.getGrants();
    if (this.userGrants != null && !this.userGrants.isEmpty()) {
      for (GrantVO userGrant : this.userGrants) {
        setterGrants.add(userGrant);
        this.accountUser.getGrantsWithoutAudienceGrants().add(userGrant);
      }
    }
  }

  /**
   * changes the language in the navigation menu (according to login state)
   * 
   * @param bundle the resource bundle of the currently selected language
   */
  public void changeLanguage(ResourceBundle bundle) {
    // change the language for the Depositor WS navigation info
    DepositorWSSessionBean depWSSessionBean =
        (DepositorWSSessionBean) getSessionBean(DepositorWSSessionBean.class);
    // change the button language

    if (this.authenticationToken == null || this.authenticationToken.equals("")) {
      this.btnLoginLogout = "login_btLogin";
    } else {
      this.btnLoginLogout = "login_btLogout";
      if (this.accountUser != null) {
        if (this.accountUser.isDepositor()) {
          depWSSessionBean.setMyWorkspace(true); // getLabel("mainMenu_lblMyWorkspace")
          depWSSessionBean.setDepositorWS(true); // getLabel("mainMenu_lnkDepositor")
          depWSSessionBean.setNewSubmission(true); // getLabel("actionMenu_lnkNewSubmission")
        }
      }
    }
  }

  // Getters and Setters
  public void login(String userHandle) {
    this.authenticationToken = userHandle;
  }

  public void logout(String userHandle) {
    this.authenticationToken = null;
  }

  public String getAuthenticationToken() {
    return authenticationToken;
  }

  public void setAuthenticationToken(String authenticationToken) {
    this.authenticationToken = authenticationToken;
  }

  public AccountUserVO getAccountUser() {
    return accountUser;
  }

  public void setAccountUser(AccountUserVO accountUser) {
    this.accountUser = accountUser;
  }

  public String getBtnLoginLogout() {
    return btnLoginLogout;
  }

  public void setBtnLoginLogout(String btnLoginLogout) {
    this.btnLoginLogout = btnLoginLogout;
  }

  public boolean isWasLoggedIn() {
    return wasLoggedIn;
  }

  public void setWasLoggedIn(boolean wasLoggedIn) {
    this.wasLoggedIn = wasLoggedIn;
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

  public String getUser() {
    return this.authenticationToken;
  }

  public String getLoginLogoutLabel() {
    return getLabel(btnLoginLogout);
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

  @Override
  public String toString() {
    return "[Login: "
        + (loggedIn ? "User " + authenticationToken + "(" + accountUser + ") is logged in]"
            : "No user is logged in (" + accountUser + ")]");
  }

  /**
   * JSF Wrapper for isModerator()
   * 
   * @return
   */
  public boolean getIsModerator() {
    return isLoggedIn() && getAccountUser().isModerator();
  }

  /**
   * JSF Wrapper for isDepositor()
   * 
   * @return
   */
  public boolean getIsDepositor() {
    return isLoggedIn() && getAccountUser().isDepositor();
  }

  /**
   * JSF Wrapper for isReporter()
   * 
   * @return
   */
  public boolean getIsReporter() {
    return isLoggedIn() && getAccountUser().isReporter();
  }

  public List<AffiliationVOPresentation> getAccountUsersAffiliations() throws Exception {
    if (this.userAccountAffiliations == null) {
      organizationServiceHandler = new OrganizationServiceHandler();
      userAccountAffiliations = new ArrayList<AffiliationVOPresentation>();
      for (UserAttributeVO ua : getAccountUser().getAttributes()) {
        if ("o".equals(ua.getName())) {
          AffiliationVO orgUnit = organizationServiceHandler.readOrganization(ua.getValue());
          userAccountAffiliations.add(new AffiliationVOPresentation(orgUnit));
        }
      }
    }
    return userAccountAffiliations;

  }

  // only active UserGroups!
  public List<UserGroupVO> getAccountUsersUserGroups() {
    if (userAccountUserGroups == null && getAccountUser() != null
        && getAccountUser().getReference() != null) {
      HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
      filterParams.put("operation", new String[] {"searchRetrieve"});
      filterParams.put("version", new String[] {"1.1"});
      // String orgId = "escidoc:persistent25";
      filterParams
          .put("query",
              new String[] {"\"/structural-relations/user/id\"="
                  + getAccountUser().getReference().getObjectId() + " and "
                  + "\"/properties/active\"=\"true\""});
      // filterParams.put("query", new String[] {"\"http://escidoc.de/core/01/properties/user\"=" +
      // getAccountUser().getReference().getObjectId() + " and " +
      // "\"http://escidoc.de/core/01/properties/active\"=\"true\""});

      /*
       * UserGroupList ugl = new UserGroupList(filterParams, getESciDocUserHandle());
       * userAccountUserGroups = ugl.getUserGroupLists();
       */
    }
    return userAccountUserGroups;
  }

  public boolean getIsYearbookEditor() {

    // toDo: find better way how to do this
    ContextListSessionBean clsb =
        (ContextListSessionBean) getSessionBean(ContextListSessionBean.class);
    if (this.getIsDepositor() && clsb.getYearbookContextListSize() > 0) {
      return true;
    }

    if (getAccountUsersUserGroups() != null) {
      for (UserGroupVO ug : getAccountUsersUserGroups()) {
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
    return this.getAccountUser().getGrants();
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

  public String obtainToken() {

    try {

      URL url = new URL(PropertyReader.getProperty("auth.token.url"));
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");

      String input =
          "{\"userid\":\"" + this.getUsername() + "\",\"password\":\"" + this.getPassword() + "\"}";

      OutputStream os = conn.getOutputStream();
      os.write(input.getBytes());
      os.flush();

      System.out.println(conn.getResponseCode());
      String token = conn.getHeaderField("Token");
      System.out.println(token);

      conn.disconnect();
      return token;

    } catch (MalformedURLException e) {

      e.printStackTrace();

    } catch (IOException e) {

      e.printStackTrace();

    }
    return null;

  }

  public JsonNode obtainUser() {

    try {

      URL url = new URL(PropertyReader.getProperty("auth.users.url") + "/" + this.getUsername());
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setDoOutput(true);
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Authorization", this.getAuthenticationToken());

      ObjectMapper mapper = new ObjectMapper();
      JsonNode rawUser = mapper.readTree(conn.getInputStream());
      conn.disconnect();

      // rawUser.forEach((k, v) -> System.out.println("user map. " + k + " " + v));

      return rawUser;

    } catch (MalformedURLException e) {

      e.printStackTrace();

    } catch (JsonParseException e) {

      e.printStackTrace();

    } catch (IOException e) {

      e.printStackTrace();

    }
    return null;
  }

}
