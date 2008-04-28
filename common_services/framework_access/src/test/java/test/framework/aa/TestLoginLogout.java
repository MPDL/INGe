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
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */ 
package test.framework.aa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;

import org.apache.axis.encoding.Base64;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.junit.Test;

import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Testcases for the authentification service of the framework.
 *
 * @author Peter (initial creation)
 * @author $Author: pbroszei $ (last modification)
 * @version $Revision: 314 $ $LastChangedDate: 2007-11-07 13:12:14 +0100 (Wed, 07 Nov 2007) $
 * @revised by BrP: 04.09.2007
 */
public class TestLoginLogout
{    
    private static final String LOGIN_URL ="/aa/login";
    private static final String USER_NAME = "test_dep_scientist";
    private static final String USER_PASSWORD = "escidoc";

    private static Logger logger = Logger.getLogger(TestLoginLogout.class);

    private static String loginUser() throws ServiceException, HttpException, IOException, URISyntaxException
    {
        // post the login data
        PostMethod postMethod = new PostMethod(ServiceLocator.getFrameworkUrl() + LOGIN_URL);
        postMethod.addParameter("survey", "LoginResults");
        postMethod.addParameter("target", "http://10.20.0.8:8080");
        postMethod.addParameter("login", USER_NAME);
        postMethod.addParameter("password", USER_PASSWORD);
        logger.debug("PostMethod=" + postMethod.getURI());
        HttpClient client = new HttpClient();
        client.executeMethod(postMethod);
        assertEquals(HttpServletResponse.SC_SEE_OTHER,postMethod.getStatusCode());
        String response = postMethod.getResponseBodyAsString();
        logger.debug("Response=" + response);
        String userHandle = null;
        Header headers[] = postMethod.getResponseHeaders();
        for (int i=0; i<headers.length; ++i)
        {
            logger.debug("Header[" + headers[i].getName() + "]=" + headers[i].getValue());
            if ("Location".equals(headers[i].getName()))
            {
                String location = headers[i].getValue();
                int index = location.indexOf('=');
                userHandle = new String(Base64.decode(location.substring(index + 1, location.length())));
            }
        }
        assertNotNull(userHandle);
        logger.debug("userHandle=" + userHandle);
        return userHandle;
    }
    
    /**
     * Logs the default user in.
     */
    @Test
    public void login() throws Exception
    {
        long zeit = -System.currentTimeMillis();
        loginUser();
        zeit += System.currentTimeMillis(); 
        logger.info("login->" + zeit + "ms");
    }

    /**
     * Logs the default user in twice.
     */
    @Test
    public void loginTwice() throws Exception
    {
        String handle1 = loginUser();
        String user = ServiceLocator.getUserAccountHandler(handle1).retrieve("escidoc:user1"); 
        String handle2 = loginUser();
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
        String userHandle = loginUser();
        long zeit = -System.currentTimeMillis();
        ServiceLocator.getUserManagementWrapper(userHandle).logout();
        zeit += System.currentTimeMillis(); 
        logger.info("logout->" + zeit + "ms");
    }
}
