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

package de.mpg.escidoc.services.pubman.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
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

import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 *
 * Utility class for pubman logic.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class AdminHelper
{
    private static String adminUserHandle = null;
    private static Date loginTime = null;
    
    private static final int NUMBER_OF_URL_TOKENS = 2;
    

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(AdminHelper.class);

    /**
     * Hide the constructor.
     */
    protected AdminHelper()
    { }

    /**
     * Logs in the given user with the given password.
     * 
     * @param userid The id of the user to log in.
     * @param password The password of the user to log in.
     * @return The handle for the logged in user.
     * @throws HttpException
     * @throws IOException
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    protected static String loginUser(String userid, String password) throws HttpException, IOException, ServiceException, URISyntaxException
    {
        String frameworkUrl = ServiceLocator.getFrameworkUrl();
        StringTokenizer tokens = new StringTokenizer( frameworkUrl, "//" );
        if( tokens.countTokens() != NUMBER_OF_URL_TOKENS ) {
            throw new IOException( "Url in the config file is in the wrong format, needs to be http://<host>:<port>" );
        }
        tokens.nextToken();
        StringTokenizer hostPort = new StringTokenizer(tokens.nextToken(), ":");
        
        if( hostPort.countTokens() != NUMBER_OF_URL_TOKENS ) {
            throw new IOException( "Url in the config file is in the wrong format, needs to be http://<host>:<port>" );
        }
        String host = hostPort.nextToken();
        int port = Integer.parseInt( hostPort.nextToken() );
        
        HttpClient client = new HttpClient();
        String proxyHost = System.getProperty("http.proxyHost");
        String proxyPortS = System.getProperty("http.proxyPort");
        if (proxyHost != null && proxyPortS != null)
        {
                int proxyPort = Integer.valueOf(proxyPortS);
                client.getHostConfiguration().setProxy(proxyHost, proxyPort);
        }
        client.getHostConfiguration().setHost( host, port, "http");
        client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        
        PostMethod login = new PostMethod( frameworkUrl + "/aa/j_spring_security_check");
        login.addParameter("j_username", userid);
        login.addParameter("j_password", password);
        
        client.executeMethod(login);
                
        login.releaseConnection();
        CookieSpec cookiespec = CookiePolicy.getDefaultSpec();
        Cookie[] logoncookies = cookiespec.match(
                host, port, "/", false, 
                client.getState().getCookies());
        
        Cookie sessionCookie = logoncookies[0];
        
        PostMethod postMethod = new PostMethod("/aa/login");
        postMethod.addParameter("target", frameworkUrl);
        client.getState().addCookie(sessionCookie);
        client.executeMethod(postMethod);
      
        if (HttpServletResponse.SC_SEE_OTHER != postMethod.getStatusCode())
        {
            throw new HttpException("Wrong status code: " + login.getStatusCode());
        }
        
        String userHandle = null;
        Header headers[] = postMethod.getResponseHeaders();
        for (int i = 0; i < headers.length; ++i)
        {
            if ("Location".equals(headers[i].getName()))
            {
                String location = headers[i].getValue();
                int index = location.indexOf('=');
                userHandle = new String(Base64.decode(location.substring(index + 1, location.length())));
                //System.out.println("location: "+location);
                //System.out.println("handle: "+userHandle);
            }
        }
        
        if (userHandle == null)
        {
            throw new ServiceException("User not logged in.");
        }
        return userHandle;
    }

    /**
     * Gets the admin users user handle.
     *
     * @return The admin's user handle.
     */
    public static String getAdminUserHandle()
    {
        Date now = new Date();
        if (adminUserHandle == null || loginTime == null || loginTime.getTime() < now.getTime() - 12 * 60 * 60 * 1000)
        {
            try
            {
                loginTime = new Date();
                adminUserHandle = loginUser(PropertyReader.getProperty("framework.admin.username"), PropertyReader.getProperty("framework.admin.password"));
            }
            catch (Exception e)
            {
                LOGGER.error("Exception logging on admin user.", e);
            }
        }
        return adminUserHandle;
    }
}
