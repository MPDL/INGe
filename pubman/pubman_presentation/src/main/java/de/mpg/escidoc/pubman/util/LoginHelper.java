/*
 *
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License"). You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */

package de.mpg.escidoc.pubman.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.rpc.ServiceException;

import org.apache.axis.encoding.Base64;
import org.apache.log4j.Logger;

import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.www.services.aa.UserAccountHandler;
import de.escidoc.www.services.aa.UserGroupHandler;
import de.escidoc.www.services.oum.OrganizationalUnitHandler;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.depositorWS.DepositorWSSessionBean;
import de.mpg.escidoc.pubman.desktop.Login;
import de.mpg.escidoc.pubman.qaws.QAWSSessionBean;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.GrantVO;
import de.mpg.escidoc.services.common.valueobjects.UserAttributeVO;
import de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup.UserGroup;
import de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup.UserGroupList;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * LoginHelper.java Class for providing helper methods for login / logout mechanism
 * 
 * @author: Tobias Schraut, created 07.03.2007
 * @version: $Revision$ $LastChangedDate$ Revised by ScT:
 *           21.08.2007
 */
public class LoginHelper extends FacesBean
{

    private static Logger logger = Logger.getLogger(LoginHelper.class);

    public static final String PARAMETERNAME_USERHANDLE = "eSciDocUserHandle";
    public final  static String BEAN_NAME = "LoginHelper";
    private String eSciDocUserHandle = null;
    private String btnLoginLogout = "login_btLogin";
    private boolean loggedIn = false;
    // a flag for showing if the user has been logged in once. If yes, the user
    // will be redirected to the home page
    // after log out.
    private boolean wasLoggedIn = false;
    private AccountUserVO accountUser = new AccountUserVO();
    private List<AffiliationVOPresentation> userAccountAffiliations;

    private List<UserGroup> userAccountUserGroups;
    private List<GrantVO>   userGrants;
    private List<GrantVO> userGrantsWithoutAudience;

    /**
     * Public constructor.
     */
    public LoginHelper()
    {
    }

    /**
     * Method checks if the user is already logged in and inserts the escidoc user handle. If not it redirects to the
     * login page.
     *
     * @return String empty navigation string for reloading the current page
     * @throws IOException IOException
     * @throws ServletException ServletException
     * @throws ServiceException ServiceException
     * @throws TechnicalException TechnicalException
     */
    public String checkLogin() throws IOException, ServletException, ServiceException, TechnicalException, URISyntaxException
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
        String userHandle = request.getParameter(LoginHelper.PARAMETERNAME_USERHANDLE);
        Login login = (Login) getRequestBean(Login.class);

        //check if the user is not logged in at all
        if (this.eSciDocUserHandle == null || this.eSciDocUserHandle.equals(""))
        {
            //if the request handle is null or empty
            if (userHandle == null || userHandle.equals(""))
            {
                //if ths user was not logged in then log-in the user
                if (!wasLoggedIn)
                {
                    if (login == null)
                    {
                        login = new Login();
                        login.loginLogout();
                    }
                }
                else
                {
                    //if the user was logged in, then redirect to where it was asked during the login
                    this.wasLoggedIn = false;
                    fc.getExternalContext().redirect(request.getContextPath());
                }
            }
            else
            {
                //if the user is logged in, encode the Handle and prepare some private attributes
                this.eSciDocUserHandle = new String(Base64.decode(userHandle));
                this.loggedIn = true;
                this.wasLoggedIn = false;
            }
        }
        //if the user is logged-in successfully, then
        if (this.eSciDocUserHandle != null && !this.eSciDocUserHandle.equals("") && !this.wasLoggedIn)
        {
            fetchAccountUser(this.eSciDocUserHandle);
            this.btnLoginLogout = "login_btLogout";
            this.wasLoggedIn = true;
        }

        logger.debug("this.accountUser.isDepositor(): " + this.accountUser.isDepositor());
        logger.debug("getLabel(\"mainMenu_lnkDepositor\"): " + getLabel("mainMenu_lnkDepositor"));

        // enable the depositor links if necessary
        if (this.accountUser.isDepositor())
        {
            DepositorWSSessionBean depWSSessionBean
            = (DepositorWSSessionBean) getSessionBean(DepositorWSSessionBean.class);

            depWSSessionBean.setMyWorkspace(true); // getLabel("mainMenu_lblMyWorkspace")
            depWSSessionBean.setDepositorWS(true); // getLabel("mainMenu_lnkDepositor")
            depWSSessionBean.setNewSubmission(true); // getLabel("actionMenu_lnkNewSubmission")
        }

        if (this.accountUser.isModerator())
        {
            QAWSSessionBean qaWSSessionBean=(QAWSSessionBean) getSessionBean(QAWSSessionBean.class);
            qaWSSessionBean.init();
        }

        return "";
    }

    /**
     * Method checks if the user is already logged in and inserts the escidoc user handle.
     *
     * @return String empty navigation string for reloading the current page
     * @throws IOException IOException
     * @throws ServiceException ServiceException
     * @throws TechnicalException TechnicalException
     */
    public String insertLogin() throws IOException, ServiceException, TechnicalException, URISyntaxException
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
        String userHandle = request.getParameter(LoginHelper.PARAMETERNAME_USERHANDLE);
        if (this.eSciDocUserHandle == null || this.eSciDocUserHandle.equals(""))
        {
            if (userHandle != null)
            {
                this.eSciDocUserHandle = new String(Base64.decode(userHandle));
                this.loggedIn = true;
                this.wasLoggedIn = true;

            }
        }
        if (this.eSciDocUserHandle != null && !this.eSciDocUserHandle.equals("") && this.wasLoggedIn)
        {
            fetchAccountUser(this.eSciDocUserHandle);
            this.btnLoginLogout = "login_btLogout";
            //reinitialize ContextList
            ((ContextListSessionBean)getSessionBean(ContextListSessionBean.class)).init();
            this.wasLoggedIn = false;
        }

        // enable the depositor links if necessary
        if (this.accountUser.isDepositor())
        {
            DepositorWSSessionBean depWSSessionBean
            = (DepositorWSSessionBean) getSessionBean(DepositorWSSessionBean.class);

            depWSSessionBean.setMyWorkspace(true); // getLabel("mainMenu_lblMyWorkspace")
            depWSSessionBean.setDepositorWS(true); // getLabel("mainMenu_lnkDepositor")
            depWSSessionBean.setNewSubmission(true); // getLabel("actionMenu_lnkNewSubmission")
        }
        return "";

    }

    /**
     * retrieves the account user with the user handle
     *
     * @param userHandle user handle that is given back from FIZ framework (is needed here to call framework methods)
     * @throws ServletException, ServiceException, TechnicalException
     */
    public void fetchAccountUser(String userHandle) throws WebserverSystemException, SqlDatabaseSystemException, RemoteException, MalformedURLException, ServiceException, TechnicalException, URISyntaxException
    {
        // Call FrameWork method
        XmlTransformingBean transforming = new XmlTransformingBean();
        Login login = (Login)getRequestBean(Login.class);
        String xmlUser = "";
        try
        {
            UserAccountHandler uah = ServiceLocator.getUserAccountHandler(userHandle);
            xmlUser = uah.retrieve(userHandle);
            this.accountUser = transforming.transformToAccountUser(xmlUser);
            String attributesXml = uah.retrieveAttributes(accountUser.getReference().getObjectId());
            this.accountUser.setAttributes(transforming.transformToUserAttributesList(attributesXml));
            // add the user handle to the transformed account user
            this.accountUser.setHandle(userHandle);
            this.setESciDocUserHandle(userHandle);
            this.setLoggedIn(true);
            this.setWasLoggedIn(true);
            
            // get all user-grants
            String userGrantXML = uah.retrieveCurrentGrants(this.accountUser.getReference().getObjectId());
            this.userGrants = transforming.transformToGrantVOList(userGrantXML);
            
            // get all user-group-grants
//            UserGroupHandler ugh = ServiceLocator.getUserGroupHandler(userHandle);
//            List<GrantVO> allUserGroupGrants = new ArrayList<GrantVO> ();
//            List<UserGroup> userAccountUserGroups = this.getAccountUsersUserGroups();
//            
//            for (UserGroup userGroup : userAccountUserGroups)
//            {
//                String userGroupGrants = ugh.retrieveCurrentGrants(userGroup.getObjid());
//                allUserGroupGrants.addAll(transforming.transformToGrantVOList(userGroupGrants));
//            }
            
            //NOTE: The block below must not be removed, as it sets the this.accountUser grants
            List<GrantVO> setterGrants = this.accountUser.getGrants();
            if (this.userGrants!=null)
            {
                for (GrantVO userGrant : this.userGrants)
                {
                    setterGrants.add(userGrant);
                    this.accountUser.getGrantsWithoutAudienceGrants().add(userGrant);
                }
            }
//            if (allUserGroupGrants != null && !allUserGroupGrants.isEmpty())
//            {
//                for (GrantVO userGroupGrant : allUserGroupGrants)
//                {
//                    if(!userGroupGrant.getRole().equals(GrantVO.PredefinedRoles.AUDIENCE.frameworkValue()))
//                    {
//                        this.accountUser.getGrantsWithoutAudienceGrants().add(userGroupGrant);
//                    }
//                    setterGrants.add(userGroupGrant);
//                }
//            }
            throw new AuthenticationException();
        }
        catch (AuthenticationException e)
        {
            login.forceLogout();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            login.forceLogout();
        }
    }

    /**
     * changes the language in the navigation menu (according to login state)
     * 
     * @param bundle the resource bundle of the currently selected language
     */
    public void changeLanguage(ResourceBundle bundle)
    {
        // change the language for the Depositor WS navigation info
        DepositorWSSessionBean depWSSessionBean
        = (DepositorWSSessionBean) getSessionBean(DepositorWSSessionBean.class);
        // change the button language

        if (this.eSciDocUserHandle == null || this.eSciDocUserHandle.equals(""))
        {
            this.btnLoginLogout = "login_btLogin";
        }
        else
        {
            this.btnLoginLogout = "login_btLogout";
            if (this.accountUser != null)
            {
                if (this.accountUser.isDepositor())
                {
                    depWSSessionBean.setMyWorkspace(true); // getLabel("mainMenu_lblMyWorkspace")
                    depWSSessionBean.setDepositorWS(true); // getLabel("mainMenu_lnkDepositor")
                    depWSSessionBean.setNewSubmission(true); // getLabel("actionMenu_lnkNewSubmission")
                }
            }
        }
    }

    // Getters and Setters
    public void login(String userHandle)
    {
        this.eSciDocUserHandle = userHandle;
    }

    public void logout(String userHandle)
    {
        this.eSciDocUserHandle = null;
    }

    public String getESciDocUserHandle()
    {
        return eSciDocUserHandle;
    }

    public void setESciDocUserHandle(String eSciDocUserHandle)
    {
        this.eSciDocUserHandle = eSciDocUserHandle;
    }

    public AccountUserVO getAccountUser()
    {
        return accountUser;
    }

    public void setAccountUser(AccountUserVO accountUser)
    {
        this.accountUser = accountUser;
    }

    public String getBtnLoginLogout()
    {
        return btnLoginLogout;
    }

    public void setBtnLoginLogout(String btnLoginLogout)
    {
        this.btnLoginLogout = btnLoginLogout;
    }

    public boolean isWasLoggedIn()
    {
        return wasLoggedIn;
    }

    public void setWasLoggedIn(boolean wasLoggedIn)
    {
        this.wasLoggedIn = wasLoggedIn;
    }

    public boolean isLoggedIn()
    {
        return loggedIn;
    }

    public boolean getLoggedIn()
    {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn)
    {
        this.loggedIn = loggedIn;
    }

    public String getUser()
    {
        return this.eSciDocUserHandle;
    }

    public String getLoginLogoutLabel()
    {
        return getLabel(btnLoginLogout);
    }

    @Override
    public String toString()
    {
        return "[Login: "
        + (loggedIn
                ? "User " + eSciDocUserHandle + "(" + accountUser + ") is logged in]"
                        : "No user is logged in (" + accountUser + ")]");
    }

    /**
     * JSF Wrapper for isModerator()
     * @return
     */
    public boolean getIsModerator()
    {
        return isLoggedIn() && getAccountUser().isModerator();
    }

    /**
     * JSF Wrapper for isDepositor()
     * @return
     */
    public boolean getIsDepositor()
    {
        return isLoggedIn() && getAccountUser().isDepositor();
    }

    /**
     * JSF Wrapper for isReporter()
     * @return
     */
    public boolean getIsReporter()
    {
        return isLoggedIn() && getAccountUser().isReporter();
    }

    public List<AffiliationVOPresentation> getAccountUsersAffiliations() throws Exception
    {
        if(this.userAccountAffiliations == null)
        {
            XmlTransformingBean transforming = new XmlTransformingBean();
            OrganizationalUnitHandler ouh = ServiceLocator.getOrganizationalUnitHandler(getESciDocUserHandle());
            userAccountAffiliations = new ArrayList<AffiliationVOPresentation>();
            for(UserAttributeVO ua : getAccountUser().getAttributes())
            {
                if("o".equals(ua.getName()))
                {
                    String orgUnitXml = ouh.retrieve(ua.getValue());
                    userAccountAffiliations.add(new AffiliationVOPresentation(transforming.transformToAffiliation(orgUnitXml)));
                }
            }
        }
        return userAccountAffiliations;

    }

    // only active UserGroups!
    public List<UserGroup> getAccountUsersUserGroups()
    {
        if(userAccountUserGroups == null && getAccountUser()!=null && getAccountUser().getReference()!=null)
        {
            HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
            filterParams.put("operation", new String[] {"searchRetrieve"});
            filterParams.put("version", new String[] {"1.1"});
            //String orgId = "escidoc:persistent25";
            filterParams.put("query", new String[] {"\"/structural-relations/user/id\"=" + getAccountUser().getReference().getObjectId() + " and " + "\"/properties/active\"=\"true\""});
//            filterParams.put("query", new String[] {"\"http://escidoc.de/core/01/properties/user\"=" + getAccountUser().getReference().getObjectId() + " and " + "\"http://escidoc.de/core/01/properties/active\"=\"true\""});

            UserGroupList ugl = new UserGroupList(filterParams, getESciDocUserHandle());
            userAccountUserGroups = ugl.getUserGroupLists();
        }
        return userAccountUserGroups;
    }

    public boolean getIsYearbookEditor()
    {

        //toDo: find better way how to do this
        ContextListSessionBean clsb = (ContextListSessionBean)getSessionBean(ContextListSessionBean.class);
        if(this.getIsDepositor() && clsb.getYearbookContextListSize()>0)
        {
            return true;
        }

        if(getAccountUsersUserGroups()!=null)
        {
            for(UserGroup ug : getAccountUsersUserGroups())
            {
                if(ug.getLabel().matches("\\d*? - Yearbook User Group for.*?"))
                {
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
    
}
