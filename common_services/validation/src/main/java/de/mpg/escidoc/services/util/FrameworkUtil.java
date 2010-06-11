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

package de.mpg.escidoc.services.util;

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

import de.escidoc.www.services.om.ContextHandler;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Utility class to deal with the framework interfaces.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class FrameworkUtil
{
    // Admin user handle
    private static String adminUserHandle = null;
    
    private static final int NUMBER_OF_URL_TOKENS = 2;

    /**
     * Hidden constructor.
     */
    protected FrameworkUtil()
    {

    }

    /**
     * Retrieves all contexts visible to the logged in admin user.
     * @return An XML containing a list of the retrieved contexts.
     */
    public static String getAllContexts()
    {
        if (adminUserHandle == null)
        {
            loginAdminUser();
        }
        try
        {
            ContextHandler cHandler = ServiceLocator.getContextHandler(adminUserHandle);
            return cHandler.retrieveContexts("<param></param>");
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error", e);
        }
    }

    /**
     * Login the admin user.
     */
    private static void loginAdminUser()
    {
        try
        {
            String adminLogin = PropertyReader.getProperty("framework.admin.username");
            String adminPasword = PropertyReader.getProperty("framework.admin.password");
            adminUserHandle = AdminHelper.loginUser(adminLogin, adminPasword);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error", e);
        }
    }
 

}
