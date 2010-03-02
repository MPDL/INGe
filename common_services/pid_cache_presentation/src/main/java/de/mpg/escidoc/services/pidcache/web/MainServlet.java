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

import org.apache.commons.httpclient.HttpClient;

import de.mpg.escidoc.services.pidcache.xmltransforming;
import de.mpg.escidoc.services.pidcache.cache.PidCache;
import de.mpg.escidoc.services.pidcache.gwdg.PidHandler;
import de.mpg.escidoc.services.pidcache.util.DatabaseHelper;

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
        // TODO Auto-generated method stub
        super.doDelete(req, resp);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
    	PidHandler handler = new PidHandler();
    	
    	if (req.getParameter("pid") == null) 
        {
           	throw new RuntimeException("No 'pid' parameter!");
   		}
    	   
    	try 
    	{
    		resp.getWriter().append(handler.retrievePid(req.getParameter("pid")));
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
        	throw new RuntimeException("No 'url' parameter!");
		}
    	
    	PidCache cache = new PidCache();
    	
        try 
        {
        	if (PidHandler.GWDG_PIDSERVICE_CREATE.equals(req.getPathInfo())) 
            {
        		resp.getWriter().append(cache.assignPid(req.getParameter("url")));
    		}
        	else if (PidHandler.GWDG_PIDSERVICE_EDIT.equals(req.getPathInfo())) 
        	{
        		resp.getWriter().append(cache.editPid(req.getParameter("pid"), req.getParameter("url")));
    		}
        	else 
        	{
        		throw new ServletException("This path '" + req.getPathInfo() + "' is not valid for POST method");
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
        super.doPut(req, resp);
    }
    
}
