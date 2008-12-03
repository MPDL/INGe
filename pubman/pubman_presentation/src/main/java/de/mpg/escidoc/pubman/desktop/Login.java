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

package de.mpg.escidoc.pubman.desktop;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.breadcrumb.BreadcrumbItemHistorySessionBean;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Class for providing login / logout functionality and coresponds to the Login.jspf.
 * 
 * @author: Tobias Schraut, created 31.01.2007
 * @version: $Revision: 1687 $ $LastChangedDate: 2007-12-17 15:29:08 +0100 (Mo, 17 Dez 2007) $ Revised by ScT: 20.08.2007
 */
public class Login extends FacesBean
{
    public static String LOGIN_URL = "/aa/login";
    public static String LOGOUT_URL = "/aa/logout";
    final public static String BEAN_NAME = "Login";
    private String btnLoginLogout = "login_btLogin";
    private String displayUserName = "";
    private boolean loggedIn = false;
    private static Logger logger = Logger.getLogger(Login.class);
    private HtmlInputText txtLogin = new HtmlInputText();
    private HtmlInputText txtPassword = new HtmlInputText();

    /**
     * public constructor
     */
    public Login()
    {
        this.init();
    }

    /**
     * Callback method that is called whenever a page is navigated to, either directly via a URL, or indirectly via page
     * navigation.
     */
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
    }

    /**
     * gets the parameters out of the faces context
     * 
     * @param name Name of the parameter that should be found in the faces context
     * @return String value of the faces context parameter
     */
    public String getFacesParamValue(String name)
    {
        return (String)FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(name);
    }

    /**
     * one method for login and logout according to the current login state
     * 
     * @return String empty navigation string for reloading the page
     */
    public String loginLogout() throws ServletException, IOException, ServiceException, URISyntaxException
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
        LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);

        String userHandle = loginHelper.getESciDocUserHandle();
        if (loginHelper.isLoggedIn() && loginHelper.getESciDocUserHandle() != null)
        {
            // logout mechanism
            loginHelper.setBtnLoginLogout("login_btLogin");
            if (userHandle != null)
            {
                long zeit = -System.currentTimeMillis();

                zeit += System.currentTimeMillis();
                logger.info("logout->" + zeit + "ms");
//                loginHelper.setLoggedIn(false);
//                loginHelper.getAccountUser().setName("");
//                loginHelper.setESciDocUserHandle(null);
//                depWSSessionBean.setMyWorkspace(false);
//                depWSSessionBean.setDepositorWS(false);
//                depWSSessionBean.setNewSubmission(false);
                HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
                session.invalidate();
             // Logout mechanism
                logout();
            }
        }
        else
        {
            //login mechanism
            BreadcrumbItemHistorySessionBean breadCrumbHistory = (BreadcrumbItemHistorySessionBean)getSessionBean(BreadcrumbItemHistorySessionBean.class);
            
            String pubmanUrl = PropertyReader.getProperty("escidoc.pubman.instance.url") + PropertyReader.getProperty("escidoc.pubman.instance.context.path");
            if(!pubmanUrl.endsWith("/")) pubmanUrl = pubmanUrl + "/";
            
            String url =  ServiceLocator.getFrameworkUrl() + LOGIN_URL + "?target=" + pubmanUrl + "faces/" + breadCrumbHistory.getCurrentItem().getPage();
            fc.getExternalContext().redirect(url);
            
        }
        return "";
    }

    /**
     * @param fc
     * @throws IOException
     * @throws ServiceException
     * @throws URISyntaxException
     */
    public void logout() throws IOException, ServiceException, URISyntaxException
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.getExternalContext().redirect(
                ServiceLocator.getFrameworkUrl() + LOGOUT_URL + "?target=" + URLEncoder.encode(PropertyReader.getProperty("escidoc.pubman.instance.url") + PropertyReader.getProperty("escidoc.pubman.instance.context.path") + "?logout=true", "UTF-8"));
    }

    /**
     * method for brutal logout if authantication errors occur in the framework
     *
     * @return String navigation string for loading the login error page
     */
    public String forceLogout() throws URISyntaxException
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
        try
        {
            fc.getExternalContext().redirect(
                    ServiceLocator.getFrameworkUrl() + LOGIN_URL + "?target=" + request.getRequestURL().toString());
        }
        catch (IOException e)
        {
            logger.error("Could not redirect to Fremework login page", e);
        }
        catch (ServiceException e)
        {
            logger.error("Could not redirect to Fremework login page", e);
        }

        return "";
    }

    // Getters and Setters
    public String getBtnLoginLogout()
    {
        return btnLoginLogout;
    }

    public void setBtnLoginLogout(String btnLoginLogout)
    {
        this.btnLoginLogout = btnLoginLogout;
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

    public String getDisplayUserName()
    {
        return displayUserName;
    }

    public void setDisplayUserName(String displayUserName)
    {
        this.displayUserName = displayUserName;
    }

    public HtmlInputText getTxtLogin()
    {
        return txtLogin;
    }

    public void setTxtLogin(HtmlInputText txtLogin)
    {
        this.txtLogin = txtLogin;
    }

    public HtmlInputText getTxtPassword()
    {
        return txtPassword;
    }

    public void setTxtPassword(HtmlInputText txtPassword)
    {
        this.txtPassword = txtPassword;
    }
}
