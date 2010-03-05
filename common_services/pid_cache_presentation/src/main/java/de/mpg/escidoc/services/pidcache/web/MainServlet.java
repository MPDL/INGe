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

package de.mpg.escidoc.services.pidcache.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;

import de.mpg.escidoc.services.pidcache.PidCacheService;
import de.mpg.escidoc.services.pidcache.gwdg.GwdgPidService;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class MainServlet extends HttpServlet
{

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
    	resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "DELETE method not supported for this service.");
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
    	try 
    	{
    		PidCacheService pidCacheService = new PidCacheService();
    		if (GwdgPidService.GWDG_PIDSERVICE_VIEW.equals(req.getPathInfo())
    				|| GwdgPidService.GWDG_PIDSERVICE_VIEW.concat("/").equals(req.getPathInfo())) 
            {
        		if (req.getParameter("pid") == null) 
                {
        			resp.sendError(HttpServletResponse.SC_NO_CONTENT, "PID parameter failed.");
           		}
        		resp.getWriter().append(pidCacheService.retrieve(req.getParameter("pid")));
    		}
        	else if (GwdgPidService.GWDG_PIDSERVICE_FIND.equals(req.getPathInfo())
        			|| GwdgPidService.GWDG_PIDSERVICE_FIND.concat("/").equals(req.getPathInfo())) 
        	{
        		if (req.getParameter("url") == null) 
                {
        			resp.sendError(HttpServletResponse.SC_NO_CONTENT, "URL parameter failed.");
           		}
        		resp.getWriter().append(pidCacheService.search(req.getParameter("url")));
    		}
        	else 
        	{
        		resp.sendError(HttpServletResponse.SC_NOT_FOUND, req.getPathInfo());
			}
		} 
    	catch (Exception e) 
    	{
    		throw new RuntimeException(e);
		}
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {        
    	if (req.getParameter("url") == null) 
        {
        	resp.sendError(HttpServletResponse.SC_NO_CONTENT, "URL parameter failed.");
		}
        try 
        {
        	PidCacheService cacheService = new PidCacheService();
        	if (GwdgPidService.GWDG_PIDSERVICE_CREATE.equals(req.getPathInfo())) 
            {
        		resp.getWriter().append(cacheService.create((req.getParameter("url"))));
    		}
        	else if (GwdgPidService.GWDG_PIDSERVICE_EDIT.equals(req.getPathInfo())) 
        	{
        		if (req.getParameter("pid") == null) 
        		{
        			resp.sendError(HttpServletResponse.SC_NO_CONTENT, "PID parameter failed.");
    			}
        		resp.getWriter().append(cacheService.update(req.getParameter("pid"), req.getParameter("url")));
    		}
        	else 
        	{
        		resp.sendError(HttpServletResponse.SC_NOT_FOUND, req.getPathInfo());
			}
		} 
        catch (Exception e) 
        {
        	throw new RuntimeException(e);
		}
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
    	resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "PUT method not supported for this service");
    }
    
}
