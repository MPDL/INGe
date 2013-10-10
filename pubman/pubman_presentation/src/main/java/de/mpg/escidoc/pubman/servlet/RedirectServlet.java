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
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.rmi.RemoteException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import de.escidoc.core.common.exceptions.application.invalid.TmeException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.www.services.tme.JhoveHandler;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.viewItem.ViewItemSessionBean;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ProxyHelper;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * A servlet for retrieving and redirecting the content objects urls.
 *     /pubman/item/escidoc:12345 for items
 * and
 *     /pubman/item/escidoc:12345/component/escidoc:23456/name.txt for components.
 *     
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class RedirectServlet extends HttpServlet
{
	private static final Logger logger = Logger.getLogger(RedirectServlet.class);
	
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String id = req.getPathInfo().substring(1);
        boolean download = ("download".equals(req.getParameter("mode")));
        boolean tme = ("tme".equals(req.getParameter("mode")));
        
        // no component -> viewItemOverviewPage
        if (!id.contains("/component/"))
        {
            LoginHelper loginHelper = (LoginHelper) req.getSession().getAttribute("LoginHelper");
            ViewItemSessionBean visb = (ViewItemSessionBean) req.getSession().getAttribute("ViewItemSessionBean");
            if ("detail".equals(req.getParameter("mode")) || 
                    (!("overview".equals(req.getParameter("mode"))) &&
                            ((loginHelper != null && loginHelper.isLoggedIn()) || 
                                    (visb != null && visb.isDetailedMode()))))
            {
                visb.setDetailedMode(true);
                resp.sendRedirect("/pubman/faces/viewItemFullPage.jsp?itemId=" + id);
            }
            else 
            {
                if(visb != null) 
                {
                    visb.setDetailedMode(false);
                }
                resp.sendRedirect("/pubman/faces/viewItemOverviewPage.jsp?itemId=" + id);
            }
            return;
        }
        
        // is component
        if (id.contains("/component/"))
        {
            String[] pieces = id.split("/");
            if (pieces.length != 4)
            {
                resp.sendError(404, "File not found");
            }
            
            // open component or download it
            if (req.getParameter("mode") == null || download)
            {
                try
                {
                    InputStream input = getContentAsInputStream(req, resp, download, pieces);
                    
                    if(input == null)
                    {
                        return;
                    }
                   
                    byte[] buffer = new byte[2048];
                    int numRead;
                    long numWritten = 0;
                    OutputStream out = resp.getOutputStream();
                    while ((numRead = input.read(buffer)) != -1)
                    {
                    	logger.debug(numRead + " bytes read.");
                        out.write(buffer, 0, numRead);
                        resp.flushBuffer();
                        numWritten += numRead;
                      
                    }
                    
                    input.close();
                    out.close();
                }
                catch (URISyntaxException e)
                {
                    throw new ServletException(e);
                }
            }
            // view technical metadata
            if (tme)
            {
                try
                {
                    if ("jhove".equals(PropertyReader.getProperty("escidoc.pubman.tme.configuration")))
                    {
                        String technicalMetadata = getTechnicalMetadataByJhove(pieces);
                        resp.setHeader("Content-Type", "text/xml");
                        OutputStream out = resp.getOutputStream();
                        out.write(technicalMetadata.getBytes());
                    }
                    else 
                    {
                        InputStream input = getContentAsInputStream(req, resp, false, pieces);
                        if(input == null)
                        {
                            return;
                        }
                        String b = new String();
                        
                        try
                        {
                            b = getTechnicalMetadataByTika(input);
                        }
                        catch (TikaException e)
                        {
                            logger.warn("TikaException when parsing " + pieces[3], e);
                        }
                        catch (SAXException e)
                        {
                            logger.warn("SAXException when parsing " + pieces[3], e);
                        }
                        OutputStream out = resp.getOutputStream();
                        resp.setHeader("Content-Type", "text/plain");
                        out.write(b.toString().getBytes());
                        return;
                    }
                }
                catch (Exception e)
                {
                    throw new ServletException(e);
                }
            }
        }
    }

    private InputStream getContentAsInputStream(HttpServletRequest req, HttpServletResponse resp, boolean download,
            String[] pieces) throws IOException, URISyntaxException, ServletException, HttpException,
            UnsupportedEncodingException
    {
        String frameworkUrl = PropertyReader.getProperty("escidoc.framework_access.framework.url");
        String url = null;
        try
        {
            url = frameworkUrl + "/ir/item/" + pieces[0] + "/components/component/" + pieces[2]
                    + "/content";
            logger.debug("Calling " + url);
        }
        catch (Exception e)
        {
            throw new ServletException("Error getting framework url", e);
        }
        
        GetMethod method = new GetMethod(url);
        method.setFollowRedirects(false);
        LoginHelper loginHelper = (LoginHelper) req.getSession().getAttribute("LoginHelper");
        if (loginHelper != null && loginHelper.getESciDocUserHandle() != null)
        {
            method.addRequestHeader("Cookie", "escidocCookie=" + loginHelper.getESciDocUserHandle());
        }
        // Execute the method with HttpClient.
        HttpClient client = new HttpClient();
        ProxyHelper.setProxy(client, frameworkUrl);
        ProxyHelper.executeMethod(client, method);
        logger.debug("...executed");
        InputStream input;
        
        if (method.getStatusCode() == 302)
        {
            String servletUrl = PropertyReader.getProperty("escidoc.pubman.instance.url")
                    + PropertyReader.getProperty("escidoc.pubman.instance.context.path")
                    + PropertyReader.getProperty("escidoc.pubman.item.pattern");
            servletUrl = servletUrl.replace("$1", "");
            
            String loginUrl = frameworkUrl + "/aa/login?target="
                    + URLEncoder.encode(url, "ASCII");
            resp.sendRedirect(loginUrl);
            return null;
        }
        else if (method.getStatusCode() != 200)
        {
            throw new RuntimeException("error code " + method.getStatusCode());
        }
        else
        {
            for (Header header : method.getResponseHeaders())
            {
            	if (!"Transfer-Encoding".equals(header.getName()))
            	{
                	logger.debug("Setting header: " + header.getName() + ": " + header.getValue());
                    resp.setHeader(header.getName(), header.getValue());
            	}
            	else
            	{
            		logger.info("Ignoring " + header.getName() + ": " + header.getValue());
            	}
            }
            if (download)
            {
                resp.setHeader("Content-Disposition", "attachment");
            }
            input = method.getResponseBodyAsStream();
        }
        return input;
    }


    private String getTechnicalMetadataByTika(InputStream input) throws IOException, SAXException, TikaException
    {
        StringBuffer b = new StringBuffer(2048);
        Metadata metadata = new Metadata();
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        
        parser.parse(input, handler, metadata);
        
        for (String name : metadata.names())
        {
            b.append(name).append(": ").append(metadata.get(name))
                    .append(System.getProperty("line.separator"));
        }
        return b.toString();
    }

    private String getTechnicalMetadataByJhove(String[] pieces) throws IOException, URISyntaxException,
            ServiceException, RemoteException, XmlSchemaValidationException, SystemException, TmeException,
            XmlCorruptedException, AuthorizationException, AuthenticationException, MissingMethodParameterException
    {
        String componentPattern = PropertyReader.getProperty("escidoc.pubman.component.pattern");
        String componentUrl = componentPattern.replace("$1", pieces[0]).replace("$2", pieces[2]).replace("$3", pieces[3]);
                
        JhoveHandler jhoveHandler = ServiceLocator.getJhoveHandler(AdminHelper.getAdminUserHandle());
        
        StringBuffer b = new StringBuffer(2048);
        b.append(
                "<request xmlns:xlink=\"http://www.w3.org/1999/xlink\">"
                        + "<file xlink:type=\"simple\" xlink:title=\"\" xlink:href=\"")
                .append(PropertyReader.getProperty("escidoc.pubman.instance.url"))
                .append(PropertyReader.getProperty("escidoc.pubman.instance.context.path"))
                .append(componentUrl);
        b.append("\"");
        b.append("/>");
        b.append("</request>");
        
        String technicalMetadata = jhoveHandler.extract(b.toString());
        return technicalMetadata;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        // No post action
        return;
    }
}
