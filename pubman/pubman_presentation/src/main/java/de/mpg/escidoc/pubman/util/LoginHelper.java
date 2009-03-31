/*
*
* CDDL HEADER START
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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
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
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.depositorWS.DepositorWSSessionBean;
import de.mpg.escidoc.pubman.desktop.Login;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.GrantVO;
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
        DepositorWSSessionBean depWSSessionBean
            = (DepositorWSSessionBean) getSessionBean(DepositorWSSessionBean.class);
        if (this.eSciDocUserHandle == null || this.eSciDocUserHandle.equals(""))
        {
            if (userHandle == null || userHandle.equals(""))
            {
                if (!wasLoggedIn)
                {
                    // fc.getExternalContext().redirect(SERVER_URL + LOGIN_URL
                    // +"?target=" +
                    // request.getRequestURL().toString());
                    if (login == null)
                    {
                        login = new Login();
                        login.loginLogout();
                    }
                }
                else
                {
                    this.wasLoggedIn = false;
                    fc.getExternalContext().redirect(request.getContextPath());
                }
            }
            else
            {
                this.eSciDocUserHandle = new String(Base64.decode(userHandle));
                this.loggedIn = true;
                this.wasLoggedIn = false;
            }
        }
        if (this.eSciDocUserHandle != null && !this.eSciDocUserHandle.equals(""))
        {
            fetchAccountUser(this.eSciDocUserHandle);
            this.btnLoginLogout = "login_btLogout";
        }

        logger.debug("this.accountUser.isDepositor(): " + this.accountUser.isDepositor());
        logger.debug("getLabel(\"mainMenu_lnkDepositor\"): " + getLabel("mainMenu_lnkDepositor"));

        // enable the depositor links if necessary
        if (this.accountUser.isDepositor())
        {
            depWSSessionBean.setMyWorkspace(true); // getLabel("mainMenu_lblMyWorkspace")
            depWSSessionBean.setDepositorWS(true); // getLabel("mainMenu_lnkDepositor")
            depWSSessionBean.setNewSubmission(true); // getLabel("actionMenu_lnkNewSubmission")
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
        DepositorWSSessionBean depWSSessionBean
            = (DepositorWSSessionBean) getSessionBean(DepositorWSSessionBean.class);
        if (this.eSciDocUserHandle == null || this.eSciDocUserHandle.equals(""))
        {
            if (userHandle != null)
            {
                this.eSciDocUserHandle = new String(Base64.decode(userHandle));
                this.loggedIn = true;
                this.wasLoggedIn = false;
              
            }
        }
        if (this.eSciDocUserHandle != null && !this.eSciDocUserHandle.equals(""))
        {
            fetchAccountUser(this.eSciDocUserHandle);
            this.btnLoginLogout = "login_btLogout";
          //reinitialize ContextList
            ((ContextListSessionBean)getSessionBean(ContextListSessionBean.class)).init();
            
        }

        // enable the depositor links if necessary
        if (this.accountUser.isDepositor())
        {
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
            xmlUser = ServiceLocator.getUserAccountHandler(userHandle).retrieve(userHandle);
            this.accountUser = transforming.transformToAccountUser(xmlUser);
            // add the user handle to the transformed account user
            this.accountUser.setHandle(userHandle);
            String userGrantXML = ServiceLocator.getUserAccountHandler(userHandle).retrieveCurrentGrants(this.accountUser.getReference().getObjectId());
            List<GrantVO> grants = transforming.transformToGrantVOList(userGrantXML);
            List<GrantVO> userGrants = this.accountUser.getGrants();
            if (grants!=null)
            {
                for (GrantVO grant : grants)
                {
                    userGrants.add(grant);
                }
            }

        }
        catch (AuthenticationException e)
        {
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
}
