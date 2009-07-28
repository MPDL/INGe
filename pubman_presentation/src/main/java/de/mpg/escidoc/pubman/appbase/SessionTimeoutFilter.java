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

package de.mpg.escidoc.pubman.appbase;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class SessionTimeoutFilter implements Filter
{
    private static final Logger logger = Logger.getLogger(SessionTimeoutFilter.class);
    
    public static String LOGOUT_URL = "/aa/logout/clear.jsp";
    
    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy()
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
            ServletException
    {
        if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse))
        {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            
            try
            {
                String homePage = PropertyReader.getProperty("escidoc.pubman.instance.url") + PropertyReader.getProperty("escidoc.pubman.instance.context.path");
                
                if (!"/viewItemFullPage.jsp".equals(httpServletRequest.getPathInfo())
                        && httpServletRequest.getRequestedSessionId() != null
                        && httpServletRequest.getParameter("expired") == null
                        && httpServletRequest.getParameter("logout") == null
                        && !httpServletRequest.isRequestedSessionIdValid())
                {
                    // Deactivated because of import tool.
                    httpServletResponse.sendRedirect(ServiceLocator.getFrameworkUrl() + LOGOUT_URL + "?target=" + URLEncoder.encode(homePage + "?expired=true", "UTF-8"));
                    //httpServletResponse.sendRedirect(homePage + "?expired=true");
                    return;
                    
                }
            }
            catch (Exception e)
            {
                throw new ServletException("Error logging out", e);
            }
        }
        filterChain.doFilter(request, response);
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig arg0) throws ServletException
    {
        // TODO Auto-generated method stub
    }
}
