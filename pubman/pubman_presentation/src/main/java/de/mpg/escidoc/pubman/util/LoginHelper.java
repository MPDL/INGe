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
import java.rmi.RemoteException;
import java.util.List;
import java.util.ResourceBundle;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.rpc.ServiceException;
import org.apache.axis.encoding.Base64;
import de.fiz.escidoc.common.exceptions.application.security.AuthenticationException;
import de.fiz.escidoc.common.exceptions.system.SqlDatabaseSystemException;
import de.fiz.escidoc.common.exceptions.system.WebserverSystemException;
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
 * @version: $Revision: 1641 $ $LastChangedDate: 2007-12-04 16:52:04 +0100 (Tue, 04 Dec 2007) $ Revised by ScT:
 *           21.08.2007
 */
public class LoginHelper
{
    // For handling the resource bundles (i18n)
    ResourceBundle bundleLabel = ResourceBundle.getBundle(InternationalizationHelper.LABLE_BUNDLE_EN);
    public static final String PARAMETERNAME_USERHANDLE = "eSciDocUserHandle";
    final public static String BEAN_NAME = "LoginHelper";
    private String eSciDocUserHandle = null;
    private String btnLoginLogout = "Login";
    private boolean loggedIn = false;
    // a flag for showing if the user has been logged in once. If yes, the user
    // will be redirected to the home page
    // after log out.
    private boolean wasLoggedIn = false;
    private AccountUserVO accountUser = new AccountUserVO();

    /**
     * Public constructor
     */
    public LoginHelper()
    {
    }

    /**
     * Method checks if the user is already logged in and inserts the escidoc user handle. If not it redirects to the
     * login page.
     * 
     * @return String empty navigation string for reloading the current page
     * @throws IOException, ServletException, ServiceException, TechnicalException
     */
    public String checkLogin() throws IOException, ServletException, ServiceException, TechnicalException
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest)fc.getExternalContext().getRequest();
        String userHandle = request.getParameter(LoginHelper.PARAMETERNAME_USERHANDLE);
        Login login = (Login)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), "Login");
        DepositorWSSessionBean depWSSessionBean = (DepositorWSSessionBean)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                DepositorWSSessionBean.BEAN_NAME);
        if (this.eSciDocUserHandle == null || this.eSciDocUserHandle.equals(""))
        {
            if (userHandle == null || userHandle.equals(""))
            {
                if (this.wasLoggedIn == false)
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
            this.btnLoginLogout = bundleLabel.getString("login_btLogout");
        }
        // enable the depositor links if necessary
        if (this.accountUser.isDepositor() == true)
        {
            depWSSessionBean.setMyWorkspace(bundleLabel.getString("mainMenu_lblMyWorkspace"));
            depWSSessionBean.setDepositorWS(bundleLabel.getString("mainMenu_lnkDepositor"));
            depWSSessionBean.setNewSubmission(bundleLabel.getString("actionMenu_lnkNewSubmission"));
        }
        return "";
    }

    /**
     * Method checks if the user is already logged in and inserts the escidoc user handle.
     * 
     * @return String empty navigation string for reloading the current page
     * @throws IOException, ServletException, ServiceException, TechnicalException
     */
    public String insertLogin() throws IOException, ServiceException, TechnicalException
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest)fc.getExternalContext().getRequest();
        String userHandle = request.getParameter(LoginHelper.PARAMETERNAME_USERHANDLE);
        DepositorWSSessionBean depWSSessionBean = (DepositorWSSessionBean)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                DepositorWSSessionBean.BEAN_NAME);
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
            this.btnLoginLogout = bundleLabel.getString("login_btLogout");
        }
        // enable the depositor links if necessary
        if (this.accountUser.isDepositor() == true)
        {
            depWSSessionBean.setMyWorkspace(bundleLabel.getString("mainMenu_lblMyWorkspace"));
            depWSSessionBean.setDepositorWS(bundleLabel.getString("mainMenu_lnkDepositor"));
            depWSSessionBean.setNewSubmission(bundleLabel.getString("actionMenu_lnkNewSubmission"));
        }
        return "";
    }

    /**
     * retrieves the account user with the user handle
     * 
     * @param userHandle user handle that is given back from FIZ framework (is needed here to call framework methods)
     * @throws ServletException, ServiceException, TechnicalException
     */
    private void fetchAccountUser(String userHandle) throws WebserverSystemException, SqlDatabaseSystemException, RemoteException, MalformedURLException, ServiceException, TechnicalException
    {
        // Call FrameWork method
        XmlTransformingBean transforming = new XmlTransformingBean();
        Login login = (Login)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), "desktop$Login");
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
            for (GrantVO grant : grants)
            {
                userGrants.add(grant);
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
        DepositorWSSessionBean depWSSessionBean = (DepositorWSSessionBean)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                DepositorWSSessionBean.BEAN_NAME);
        // change the button language
        this.bundleLabel = bundle;
        if (this.eSciDocUserHandle == null || this.eSciDocUserHandle.equals(""))
        {
            this.btnLoginLogout = bundleLabel.getString("login_btLogin");
        }
        else
        {
            this.btnLoginLogout = bundleLabel.getString("login_btLogout");
            if (this.accountUser != null)
            {
                if (this.accountUser.isDepositor() == true)
                {
                    depWSSessionBean.setMyWorkspace(bundleLabel.getString("mainMenu_lblMyWorkspace"));
                    depWSSessionBean.setDepositorWS(bundleLabel.getString("mainMenu_lnkDepositor"));
                    depWSSessionBean.setNewSubmission(bundleLabel.getString("actionMenu_lnkNewSubmission"));
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

    public void setLoggedIn(boolean loggedIn)
    {
        this.loggedIn = loggedIn;
    }

    public String getUser()
    {
        return this.eSciDocUserHandle;
    }

    public ResourceBundle getBundleLabel()
    {
        return bundleLabel;
    }

    public void setBundleLabel(ResourceBundle bundleLabel)
    {
        this.bundleLabel = bundleLabel;
    }
}
