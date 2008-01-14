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

package de.mpg.escidoc.services.validation.webservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.validation.ItemValidating;
import de.mpg.escidoc.services.validation.ValidationSchemaNotFoundException;

/**
 * Servlet for the REST interface.
 *
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 131 $ $LastChangedDate: 2007-11-21 18:53:43 +0100 (Wed, 21 Nov 2007) $
 */
public class RestServlet extends HttpServlet
{

    private static final Logger LOGGER = Logger.getLogger(RestServlet.class);

    @EJB
    private ItemValidating itemValidating;

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
        try
        {
            try
            {
                // Retrieve the command from the location path
                String command = req.getPathInfo();
                if (command != null && command.length() > 0)
                {
                    command = command.substring(1);
                }

                LOGGER.debug("Command: " + command);
                
                // Init validation service
                InitialContext ctx = new InitialContext();
                itemValidating = (ItemValidating) ctx.lookup(ItemValidating.SERVICE_NAME);
                PrintWriter out = resp.getWriter();

                // validateItemXml
                if ("POST".equals(req.getMethod()) && "validateItemXml".equals(command))
                {
                    String content = getBodyContent(req);
                    String validationPoint = req.getParameter("validation-point");
                    if (validationPoint == null)
                    {
                        validationPoint = "default";
                    }
                    out.println(itemValidating.validateItemXml(content, validationPoint));
                }
                
                // refreshValidationSchemaCache
                else if ("GET".equals(req.getMethod()) && "refreshValidationSchemaCache".equals(command))
                {
                    itemValidating.refreshValidationSchemaCache();
                }
                
                // When someone arrives here with an unknown request, dispatch him to the info page.
                else
                {
                    RequestDispatcher dispatcher = req.getRequestDispatcher("/rest.jsp");
                    dispatcher.forward(req, resp);
                }

            }
            catch (NamingException ne)
            {
                handleException(ne, resp);
            }
            catch (TechnicalException te)
            {
                handleException(te, resp);
            }
            catch (ValidationSchemaNotFoundException nfe)
            {
                handleException(nfe, resp);
            }
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }
    }

    // Read the request body into a string
    private String getBodyContent(final HttpServletRequest req) throws IOException
    {
        String content = "";
        InputStream inStream = req.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
        String line = null;
        while ((line = br.readLine()) != null)
        {
            content += line + "\n";
        }
        return content;
    }

    private void handleException(final Exception e, final HttpServletResponse resp) throws Exception
    {
        PrintWriter out = resp.getWriter();
        out.println("Error: " + e.getMessage());
        out.println();
        StackTraceElement[] stacktrace = e.getStackTrace();
        for (int i = 0; i < stacktrace.length; i++)
        {
            StackTraceElement element = stacktrace[i];
            out.println("at "
                    + element.getClassName()
                    + "."
                    + element.getMethodName()
                    + "("
                    + element.getLineNumber()
                    + ")");
        }
        resp.setStatus(500);
    }
}
