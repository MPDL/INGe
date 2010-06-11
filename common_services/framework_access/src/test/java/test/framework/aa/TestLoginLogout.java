/*
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
 * Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */ 
package test.framework.aa;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;

import org.apache.axis.encoding.Base64;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.junit.Test;

import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Testcases for the authentification service of the framework.
 *
 * @author Peter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @revised by BrP: 04.09.2007
 */
public class TestLoginLogout
{    
    private static final String LOGIN_URL ="/aa/j_spring_security_check";
    protected static final String PROPERTY_USERNAME_SCIENTIST = "framework.scientist.username";
    protected static final String PROPERTY_PASSWORD_SCIENTIST = "framework.scientist.password";
    protected static final String PROPERTY_USERNAME_LIBRARIAN = "framework.librarian.username";
    protected static final String PROPERTY_PASSWORD_LIBRARIAN = "framework.librarian.password";
    protected static final String PROPERTY_USERNAME_AUTHOR = "framework.author.username";
    protected static final String PROPERTY_PASSWORD_AUTHOR = "framework.author.password";
    protected static final String PROPERTY_USERNAME_ADMIN = "framework.admin.username";
    protected static final String PROPERTY_PASSWORD_ADMIN = "framework.admin.password";
    
    private static final int NUMBER_OF_URL_TOKENS = 2;

    private static Logger logger = Logger.getLogger(TestLoginLogout.class);
    

    /**
     * Logs the default user in.
     */
    @Test
    public void login() throws Exception
    {
        long zeit = -System.currentTimeMillis();
        AdminHelper.loginUser(PropertyReader.getProperty(PROPERTY_USERNAME_SCIENTIST), PropertyReader.getProperty(PROPERTY_PASSWORD_SCIENTIST));
        zeit += System.currentTimeMillis(); 
        logger.info("login->" + zeit + "ms");
    }

    /**
     * Logs the default user in twice.
     */
    @Test
    public void loginTwice() throws Exception
    {
        String handle1 = AdminHelper.loginUser(PropertyReader.getProperty(PROPERTY_USERNAME_SCIENTIST), PropertyReader.getProperty(PROPERTY_PASSWORD_SCIENTIST));
        String user = ServiceLocator.getUserAccountHandler(handle1).retrieve("escidoc:user1"); 
        String handle2 = AdminHelper.loginUser(PropertyReader.getProperty(PROPERTY_USERNAME_SCIENTIST), PropertyReader.getProperty(PROPERTY_PASSWORD_SCIENTIST));
        
        user = ServiceLocator.getUserAccountHandler(handle2).retrieve("escidoc:user1"); 
        // handle1 must still be valid
        user = ServiceLocator.getUserAccountHandler(handle1).retrieve("escidoc:user1"); 
        // make handle2 invalid
        ServiceLocator.getUserManagementWrapper(handle2).logout();
        // handle1 must still be valid
        user = ServiceLocator.getUserAccountHandler(handle1).retrieve("escidoc:user1"); 
    }

    /**
     * Logs the default user in and out.
     */
    @Test
    public void logout() throws Exception
    {
        String userHandle = AdminHelper.loginUser(PropertyReader.getProperty(PROPERTY_USERNAME_SCIENTIST), PropertyReader.getProperty(PROPERTY_PASSWORD_SCIENTIST));
        long zeit = -System.currentTimeMillis();
        ServiceLocator.getUserManagementWrapper(userHandle).logout();
        zeit += System.currentTimeMillis(); 
        logger.info("logout->" + zeit + "ms");
    }
}
