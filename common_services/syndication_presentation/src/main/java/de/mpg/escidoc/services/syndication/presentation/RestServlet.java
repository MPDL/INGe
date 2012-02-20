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

package de.mpg.escidoc.services.syndication.presentation;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.sun.syndication.io.FeedException;

import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.syndication.Syndication;
import de.mpg.escidoc.services.syndication.SyndicationException;
import de.mpg.escidoc.services.syndication.Utils;
import de.mpg.escidoc.services.syndication.feed.Feed;

/**
 * The servlet takes URL, calls eSciDoc syndication manager and 
 * returns generated RSS/ATOM  feed to the client.  
 *    
 * @author Vlad Makarenko  (initial creation) 
 * @author $Author$ (last modification)
 * $Revision$
 * $LastChangedDate$ 
 */

public class RestServlet extends HttpServlet
{

    /** Serial identifier. */
    private static final long serialVersionUID = 1L;
    /** Logging instance. */
    private static final Logger logger = Logger.getLogger(RestServlet.class);

    /* Syndication Manager Instance  */
	Syndication synd;
    
    public RestServlet() throws IOException, SyndicationException
    {
    	synd = new Syndication();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected final void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
            IOException
    {
        doPost(req, resp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
            IOException
    {
    	//TODO delete logger.info! Included due to logger.debug problems
    	logger.info("RestServlet.doPost() started");
    	logger.debug("RestServlet.doPost() started");
        String url = null;
        try
        {
        	url = PropertyReader.getProperty("escidoc.syndication.service.url") + req.getServletPath() + req.getPathInfo();
        	//TODO delete logger.info! Included due to logger.debug problems
        	logger.info("req.getServletPath(): " + req.getServletPath().toString());
        	//TODO delete logger.info! Included due to logger.debug problems
        	logger.info("req.getPathInfo(): " + req.getPathInfo().toString());
        	//TODO delete logger.info! Included due to logger.debug problems
        	logger.info("URL: " + url);
        	url = url.replace("//", "/");
        	//TODO delete logger.info! Included due to logger.debug problems
        	logger.info("New URL: " + url);
        }
        catch (Exception e)
        {
            handleException(e, resp);
        }
    	String q = req.getQueryString();
    	//TODO delete logger.info! Included due to logger.debug problems
    	logger.info("Request-QueryString: " + q);
    	
    	if (Utils.checkVal(q))
    	{
    		url += "?" + q;
    		//TODO delete logger.info! Included due to logger.debug problems
        	logger.info("Complete URL: " + q);
    	}
    	
    	
    	Feed feed = synd.getFeeds().matchFeedByUri( url );
    	//TODO delete logger.info! Included due to logger.debug problems
    	logger.info("Requested Feed: " + feed.toString());
    	
    	
    	//set correct mime-type
    	resp.setContentType(
    			"application/" 
    			+ ( url.contains( "rss_" ) ? "rss" : "atom" ) 
    			+ "+xml; charset=utf-8"
    	);

    	//cache handling
    	String ttl = feed.getCachingTtl();
    	//TODO delete logger.info! Included due to logger.debug problems
    	logger.info("CachingTtl: " + ttl);
    	
    	if (Utils.checkVal(ttl))
    	{
        	long ttlLong = Long.parseLong(ttl) * 1000L; 
        	resp.setHeader("control-cache",  "max-age=" + ttl + ", must-revalidate");

        	DateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");  
        	df.setTimeZone(TimeZone.getTimeZone("GMT"));
        	resp.setHeader("Expires",  df.format(new Date(System.currentTimeMillis() + ttlLong)) );
    	}
    	
    	try 
    	{
    		synd.getFeed( url, resp.getWriter() );
    	} 
    	catch (SyndicationException e) 
    	{
    		handleException(e, resp);
    	} 
    	catch (URISyntaxException e) 
    	{
    		resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Wrong URI syntax: " + url);
    		return;
    	} 
    	catch (FeedException e) 
    	{
    		handleException(e, resp);
    	}


    }
    
    
    /**
     * Take care on an incoming exception.
     * 
     * @param e
     *            exception
     * @param resp
     *            response stream
     * @throws IOException 
     * @throws Exception
     *             if the handling went wrong on any reason
     */
    private void handleException(final Exception e, final HttpServletResponse resp) throws IOException 
    {
    	logger.error(e.getMessage(), e);
    	
        PrintWriter pw;
        pw = resp.getWriter();
        pw.print("Error: ");
        e.printStackTrace(pw);
        pw.close();
    }

	    
}
