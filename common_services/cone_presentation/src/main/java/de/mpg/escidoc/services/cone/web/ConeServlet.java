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

package de.mpg.escidoc.services.cone.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.cone.ModelList;
import de.mpg.escidoc.services.cone.Querier;
import de.mpg.escidoc.services.cone.QuerierFactory;
import de.mpg.escidoc.services.cone.ModelList.Model;
import de.mpg.escidoc.services.cone.util.Pair;
import de.mpg.escidoc.services.cone.util.TreeFragment;

/**
 * Servlet to answer calls from various calls.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public abstract class ConeServlet extends HttpServlet
{

    private static final Logger logger = Logger.getLogger(ConeServlet.class);
    private static final String DB_ERROR_MESSAGE = "Error querying database.";
    private static final String DEFAULT_ENCODING = "UTF-8";
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        request.setCharacterEncoding(DEFAULT_ENCODING);
        response.setCharacterEncoding(DEFAULT_ENCODING);
        
        PrintWriter out = response.getWriter();
        
        // Read the model name and action from the URL
        String[] path = request.getPathInfo().split("/");
        
        String model = null;
        String action = null;
        
        if (path.length >= 2)
        {
            model = path[1];
        }
        
        if (path.length >= 3)
        {
            action = path[2];
        }

        logger.debug("Querying for '" + model + "'");
        
        if ("explain".equals(model))
        {
            explain(response);
        }
        else if ("query".equals(action))
        {
            try
            {
                queryAction(request, response, model);
            }
            catch (Exception e)
            {
                throw new ServletException(e);
            }
        }
        else if ("all".equals(action))
        {
            try
            {
                allAction(request, response, model);
            }
            catch (Exception e)
            {
                throw new ServletException(e);
            }
        }
        else if ("details".equals(action))
        {
            try
            {
                detailAction(request, response, out, model);
            }
            catch (Exception e)
            {
                throw new ServletException(e);
            }
        }
    }

    /**
     * Retrieve the whole list of entities.
     * 
     * @param request
     * @param response
     * @param model
     * @throws IOException
     */
    private void allAction(HttpServletRequest request, HttpServletResponse response, String modelName) throws Exception
    {
        Model model = ModelList.getInstance().getModelByAlias(modelName);

        if (model != null)
        {
            String lang = request.getParameter("lang");
            Querier querier = QuerierFactory.newQuerier();
            
            logger.debug("Querier is " + querier);
                
            if (querier == null)
            {
                reportMissingQuerier(response);
            }
            else
            {
                List<Pair> result = null;
                
                try
                {
                    result = querier.query(model.getName(), "*", lang, 0);
                }
                catch (Exception e)
                {
                    logger.error(DB_ERROR_MESSAGE, e);
                }
   
                response.getWriter().println(formatQuery(result));
            }

        }
        else
        {
            reportUnknownModel(modelName, response);
        }
    }
    
    /**
     * Retrieve the details for a given id.
     * 
     * @param request Just to use it in the method.
     * @param response Just to use it in the method.
     * @param out Just to use it in the method.
     * @param model The requested type of data, e.g. "journals", "languages"
     * @throws IOException
     */
    private void detailAction(
            HttpServletRequest request,
            HttpServletResponse response,
            PrintWriter out,
            String modelName)
        throws Exception
    {
        Model model = ModelList.getInstance().getModelByAlias(modelName);

        if (model != null)
        {
            String[] path = request.getPathInfo().split("/");
            response.setContentType(getContentType());
            String id = null;
            String lang = request.getParameter("lang");
            
            if (path.length > 3)
            {
                int startPos = path[0].length() + path[1].length() + path[2].length() + 3;
                id = request.getPathInfo().substring(startPos);
            }

            try
            {
                URI uri = new URI(id);
                if (!uri.isAbsolute())
                {
                    throw new URISyntaxException(id, "no urn");
                }
            }
            catch (URISyntaxException e)
            {

                id = model.getIdentifierPrefix() + id;
                
                try
                {
                    new URI(id);
                }
                catch (URISyntaxException e2)
                {
                    reportMissingParameter("id", response);
                }
            }
            
            if (id == null)
            {
                reportMissingParameter("id", response);
            }
            else if ("".equals(id))
            {
                reportEmptyParameter("id", response);
            }
            else
            {
            
                Querier querier = QuerierFactory.newQuerier();

                if (querier == null)
                {
                    reportMissingQuerier(response);
                }
                else
                {
                    TreeFragment result = null;
                    
                    try
                    {
                        result = querier.details(modelName, id, lang);
                    }
                    catch (Exception e)
                    {
                        logger.error(DB_ERROR_MESSAGE, e);
                    }
   
                    out.println(formatDetails(id, model, result, lang));
                }
            }
        }
        else
        {
            reportUnknownModel(modelName, response);
        }
    }

    /**
     * Retrieve a list of matching entities.
     * 
     * @param request
     * @param response
     * @param model
     * @throws IOException
     */
    private void queryAction(HttpServletRequest request, HttpServletResponse response, String modelName)
        throws Exception
    {
        Model model = ModelList.getInstance().getModelByAlias(modelName);

        if (model != null)
        {
            response.setContentType(getContentType());
            String query = request.getParameter("q");
            String lang = request.getParameter("lang");
            
            if (query == null)
            {
                reportMissingParameter("q", response);
            }
            else if ("".equals(query))
            {
                reportEmptyParameter("q", response);
            }
            else
            {
            
                Querier querier = QuerierFactory.newQuerier();
                
                logger.debug("Querier is " + querier);
                
                if (querier == null)
                {
                    reportMissingQuerier(response);
                }
                else
                {
                    List<Pair> result = null;
                    
                    try
                    {
                        result = querier.query(model.getName(), query, lang);
                    }
                    catch (Exception e)
                    {
                        logger.error(DB_ERROR_MESSAGE, e);
                    }
   
                    response.getWriter().println(formatQuery(result));
                }
            }
        }
        else
        {
            reportUnknownModel(modelName, response);
        }
    }

    private void reportMissingQuerier(HttpServletResponse response) throws IOException
    {
        response.setStatus(500);
        response.getWriter().println("Error: Querier implementation not set in propertyfile.");
    }

    private void reportUnknownModel(String model, HttpServletResponse response) throws IOException
    {
        response.setStatus(500);
        response.getWriter().println("Error: Model " + model + " is not known.");
    }

    private void reportEmptyParameter(String string, HttpServletResponse response)
    {
        // do not report empty parameters, just return nothing.
    }

    private void reportMissingParameter(String param, HttpServletResponse response) throws IOException
    {
        response.setStatus(500);
        response.getWriter().println("Error: Parameter '" + param + "' is missing.");
    }

    /**
     * Explain action to be implemented by a format servlet.
     * 
     * @param response The HTTP response piped through.
     * @throws FileNotFoundException From XSLT transformation.
     * @throws TransformerFactoryConfigurationError From XSLT transformation.
     * @throws IOException From XSLT transformation.
     */
    protected abstract void explain(HttpServletResponse response)
        throws FileNotFoundException, TransformerFactoryConfigurationError, IOException;

    /**
     * Format the results of the query action.
     * 
     * @param pairs The results
     * @return A string that displays the given results in the current format.
     * @throws IOException From XSLT transformation.
     */
    protected abstract String formatQuery(List<Pair> pairs) throws IOException;

    /**
     * Format the results of the details action.
     * 
     * @param id The id of the object.
     * @param model The current model.
     * @param triples The structure of the current object.
     * @param lang The selected language.
     * 
     * @return A string that displays the given results in the current format.
     * @throws IOException From XSLT transformation.
     */
    protected abstract String formatDetails(String id, Model model, TreeFragment triples, String lang)
        throws IOException;
    
    /**
     * An implementing servlet should return the "Content-Type" header value of its format (e.g. "text/html"). 
     * 
     * @return The content type as string.
     */
    protected abstract String getContentType();
}
