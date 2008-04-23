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

import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;

import org.apache.axis.encoding.Base64;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
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
     *
     * Login a given user to the framework.
     *
     * @param userName The login name of the user.
     * @param password The user's password.
     *
     * @return The user handle returned by the framework.
     *
     * @throws IOException Thrown by the Http call.
     * @throws ServiceException Thrown by the framework.
     */
    public static String loginUser(final String userName, final String password) throws IOException, ServiceException
    {
        // build the postMethod from the given credentials
        PostMethod postMethod = new PostMethod(ServiceLocator.getFrameworkUrl() + "/um/loginResults");
        postMethod.addParameter("survey", "LoginResults");
        postMethod.addParameter("target", "http://localhost:8888");
        postMethod.addParameter("login", userName);
        postMethod.addParameter("password", password);

        HttpClient client = new HttpClient();
        client.executeMethod(postMethod);
        if (HttpServletResponse.SC_SEE_OTHER != postMethod.getStatusCode())
        {
            throw new HttpException("Wrong status code: " + postMethod.getStatusCode());
        }

        String userHandle = null;
        Header[] headers = postMethod.getResponseHeaders();
        for (int i = 0; i < headers.length; ++i)
        {
            if ("Location".equals(headers[i].getName()))
            {
                String location = headers[i].getValue();
                int index = location.indexOf('=');
                userHandle = new String(Base64.decode(location.substring(index + 1, location.length())));
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
        if (adminUserHandle == null)
        {
            try
            {
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
