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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.services.framework.PropertyReader;

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
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String id = req.getPathInfo().substring(1);
        if (id != null && id.contains("/component/"))
        {
            String[] pieces = id.split("/");
            if (pieces.length != 4)
            {
                resp.sendError(404, "File not found");
            }
            else
            {
                try
                {
                    String frameworkUrl = PropertyReader.getProperty("escidoc.framework_access.framework.url");
                    String url = null;
                    try
                    {
                        url = frameworkUrl + "/ir/item/" + pieces[0] + "/components/component/" + pieces[2]
                                + "/content";
                    }
                    catch (Exception e)
                    {
                        throw new ServletException("Error getting framework url", e);
                    }
                    // test new method
                    // String contentType = mimeType; // For dialog, try
                    // resp.setContentType(contentType);
                    byte[] buffer = null;
                    GetMethod method = new GetMethod(url);
                    method.setFollowRedirects(false);
                    LoginHelper loginHelper = (LoginHelper) req.getSession().getAttribute("LoginHelper");
                    if (loginHelper.getESciDocUserHandle() != null)
                    {
                        method.addRequestHeader("Cookie", "escidocCookie=" + loginHelper.getESciDocUserHandle());
                    }
                    // Execute the method with HttpClient.
                    HttpClient client = new HttpClient();
                    client.executeMethod(method);
                    InputStream input;
                    OutputStream out = resp.getOutputStream();
                    if (method.getStatusCode() == 302)
                    {
                        String servletUrl = PropertyReader.getProperty("escidoc.pubman.instance.url")
                                + PropertyReader.getProperty("escidoc.pubman.instance.context.path")
                                + PropertyReader.getProperty("escidoc.pubman.item.pattern");
                        servletUrl = servletUrl.replace("$1", "");
                        
                        String loginUrl = frameworkUrl + "/aa/login?target="
                                + URLEncoder.encode(url, "ASCII");
                        resp.sendRedirect(loginUrl);
                        return;
                    }
                    else if (method.getStatusCode() != 200)
                    {
                        throw new RuntimeException("error code " + method.getStatusCode());
                    }
                    else
                    {
                        for (Header header : method.getResponseHeaders())
                        {
                            resp.setHeader(header.getName(), header.getValue());
                        }
                        input = method.getResponseBodyAsStream();
                    }
                    buffer = new byte[2048];
                    int numRead;
                    long numWritten = 0;
                    while ((numRead = input.read(buffer)) != -1)
                    {
                        out.write(buffer, 0, numRead);
                        out.flush();
                        numWritten += numRead;
                    }
                    input.close();
                }
                catch (URISyntaxException e)
                {
                    throw new ServletException(e);
                }
            }
        }
        else
        {
            resp.sendRedirect("/pubman/faces/viewItemFullPage.jsp?itemId=" + id);
        }
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
