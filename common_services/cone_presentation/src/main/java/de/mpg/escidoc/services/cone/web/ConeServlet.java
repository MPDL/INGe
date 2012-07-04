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

package de.mpg.escidoc.services.cone.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.aa.Aa;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.cone.ModelList;
import de.mpg.escidoc.services.cone.ModelList.Model;
import de.mpg.escidoc.services.cone.Querier;
import de.mpg.escidoc.services.cone.QuerierFactory;
import de.mpg.escidoc.services.cone.formatter.Formatter;
import de.mpg.escidoc.services.cone.util.Describable;
import de.mpg.escidoc.services.cone.util.Pair;
import de.mpg.escidoc.services.cone.util.Rdfs;
import de.mpg.escidoc.services.cone.util.TreeFragment;
import de.mpg.escidoc.services.cone.util.UrlHelper;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * Servlet to answer calls from various calls.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ConeServlet extends HttpServlet
{

    private static final Logger logger = Logger.getLogger(ConeServlet.class);
    private static final String DB_ERROR_MESSAGE = "Error querying database.";
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String DEFAULT_FORMAT = "html";

    private static final Set<String> RESERVED_PARAMETERS = new HashSet<String>()
    {
    	{
            this.add("f");
            this.add("format");
            this.add("m");
            this.add("mode");
    		this.add("n");
    		this.add("number");
    		this.add("l");
    		this.add("lang");
    		this.add("r");
    		this.add("redirect");
    		this.add("q");
    		this.add("query");
    		this.add("h");
    		this.add("eSciDocUserHandle");
    	}
    };
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        request.setCharacterEncoding(DEFAULT_ENCODING);
        response.setCharacterEncoding(DEFAULT_ENCODING);
        
        Formatter formatter;
        
        PrintWriter out = response.getWriter();
        
        logger.debug("request.getPathInfo() " + request.getPathInfo());
        logger.debug("getPathTranslated() " + request.getPathTranslated());
        logger.debug("getRequestURI() " + request.getRequestURI());
        logger.debug("getServletPath() " + request.getServletPath());
        logger.debug("getLocalAddr() " + request.getLocalAddr());
        logger.debug("getLocalName() " + request.getLocalName());
        logger.debug("getLocalPort() " + request.getLocalPort());
        
        // Read the model name and action from the URL
        String[] path = request.getServletPath().split("/", 4);
        
        String model = null;
        String action = null;
        String format = DEFAULT_FORMAT;
        String lang = (request.getParameter("lang") != null ? request.getParameter("lang") : request.getParameter("l"));
        boolean loggedIn = false;
        if (request.getSession().getAttribute("logged_in") != null)
        {
            Login.checkLogin(request, false);
            loggedIn = getLoggedIn(request);
        }
        
        if (!loggedIn && ((request.getParameter("eSciDocUserHandle") != null) || "true".equals((request.getParameter("redirect") != null ? request.getParameter("redirect") : request.getParameter("r")))))
        {
            try
            {
                response.sendRedirect(Aa.getLoginLink(request) + "&" + request.getQueryString());
            }
            catch (Exception e)
            {
                throw new ServletException("Error redirecting to Login", e);
            }
        }
        
        if (path.length == 3 && "".equals(path[2]))
        {
            action = path[1];
        }
        else if (path.length > 2)
        {
            model = path[1];
            if (path.length >= 3)
            {
                action = path[2];
            }
        }
        
        if (request.getParameter("format") != null || request.getParameter("f") != null)
        {
            format = (request.getParameter("format") != null ? request.getParameter("format") : request.getParameter("f"));
        }
        else 
        {
            ModelList modelList;
            try
            {
                modelList =  ModelList.getInstance();
            }
            catch (Exception e) {
                throw new ServletException(e);
            }
            boolean found = false;
            String acceptHeader = request.getHeader("Accept");
            if (acceptHeader != null)
            {
                String[] types = acceptHeader.split(",");
                for (String type : types)
                {
                    for (String key : modelList.getFormatMimetypes().keySet())
                    {
                        Set<String> formatTypes = modelList.getFormatMimetypes().get(key);
                        if (formatTypes.contains(type))
                        {
                            format = key;
                            found = true;
                            break;
                        }
                    }
                }
            }
            if (!found)
            {
                format = DEFAULT_FORMAT;
            }
        }
        
        formatter = Formatter.getFormatter(format);

        logger.info("Formatter is '" + formatter + "'");
        logger.info("Querying for '" + model + "'");
        
//        if ("explain".equals(model))
//        {
//            explain(response);
//        }
        
        if ("query".equals(action))
        {
            String query = UrlHelper.fixURLEncoding(request.getParameter("query") != null ? request.getParameter("query") : request.getParameter("q"));
            int limit = -1;
            String mode = (request.getParameter("mode") != null ? request.getParameter("mode") : request.getParameter("m"));
            Querier.ModeType modeType = Querier.ModeType.FAST;
            
            if (mode != null && "full".equals(mode.toLowerCase()))
            {
                modeType = Querier.ModeType.FULL;
            }
            try
            {
                limit = Integer.parseInt((request.getParameter("number") != null ? request.getParameter("number") : request.getParameter("n")));
            }
            catch (Exception e)
            {
                // Ignore n(umber) parameter as it is no number.
            }
            
            try
            {
                if (query != null)
                {
                    queryAction(query, limit, lang, modeType, response, formatter, model, loggedIn);
                }
                else
                {
                    ArrayList<Pair<String>> searchFields = new ArrayList<Pair<String>>();
                    for (Object key : request.getParameterMap().keySet())
                    {
                        if (!RESERVED_PARAMETERS.contains(key))
                        {
                            searchFields.add(new Pair<String>(key.toString(), UrlHelper.fixURLEncoding(request.getParameter(key.toString()))));
                        }
                    }
                    queryFieldsAction(searchFields.toArray(new Pair[]{}), limit, lang, modeType, response, formatter, model, loggedIn);
                }
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
                allAction(request, response, formatter, model, loggedIn);
            }
            catch (Exception e)
            {
                throw new ServletException(e);
            }
        }
        else if ("resource".equals(action))
        {
            String id = null;
            
            if (path.length >= 4)
            {
                id = path[1] + "/" + path[2] + "/" + path[3];
            }
            
            try
            {
                detailAction(id, lang, response, formatter, out, model, loggedIn);
            }
            catch (Exception e)
            {
                throw new ServletException(e);
            }
        }
        else if ("explain".equals(action))
        {
            response.setContentType("text/xml");
            try
            {
                out.print(ResourceUtil.getResourceAsString(PropertyReader.getProperty("escidoc.cone.modelsxml.path")));
            }
            catch (Exception e) {
                throw new ServletException(e);
            }
        }
        else if ("rdfs".equals(action))
        {
            response.setContentType("text/xml");
            try
            {
                out.print(Rdfs.getModelAsRdfs(null).toString());
            }
            catch (Exception e) {
                throw new ServletException(e);
            }
        }
        
        response.setHeader("Connection", "close");
        
    }
    
    /**
     * Retrieve the whole list of entities.
     * 
     * @param request
     * @param response
     * @param model
     * @throws IOException
     */
    private void allAction(HttpServletRequest request, HttpServletResponse response, Formatter formatter, String modelName, boolean loggedIn) throws Exception
    {
        Model model = ModelList.getInstance().getModelByAlias(modelName);

        if (model != null)
        {
            response.setContentType(formatter.getContentType());
            String lang = request.getParameter("lang");

            Querier querier = QuerierFactory.newQuerier(loggedIn);
            
            logger.debug("Querier is " + querier);
                
            if (querier == null)
            {
                reportMissingQuerier(response);
            }
            else
            {
                List<? extends Describable> result = null;
                
                try
                {
                    result = querier.query(model.getName(), "*", lang, Querier.ModeType.FAST, 0);
                }
                catch (Exception e)
                {
                    logger.error(DB_ERROR_MESSAGE, e);
                }
   
                response.getWriter().print(formatter.formatQuery(result));
            }
            querier.release();
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
            String id,
            String lang,
            HttpServletResponse response,
            Formatter formatter,
            PrintWriter out,
            String modelName,
            boolean loggedIn)
        throws Exception
    {
        Model model = ModelList.getInstance().getModelByAlias(modelName);

        if (model != null)
        {
            response.setContentType(formatter.getContentType());

            try
            {
                URI uri = new URI(id);
            }
            catch (URISyntaxException e)
            {

                id = model.getSubjectPrefix() + id;
                
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
            
                Querier querier = QuerierFactory.newQuerier(loggedIn);
                
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
   
                    out.print(formatter.formatDetails(id, model, result, lang));
                }
                querier.release();
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
    private void queryAction(String query, int limit, String lang, Querier.ModeType modeType, HttpServletResponse response, Formatter formatter, String modelName, boolean loggedIn)
        throws Exception
    {
        Model model = ModelList.getInstance().getModelByAlias(modelName);

        if (model != null)
        {
            response.setContentType(formatter.getContentType());
            
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
            
                Querier querier = QuerierFactory.newQuerier(loggedIn);
                
                logger.debug("Querier is " + querier);
                
                if (querier == null)
                {
                    reportMissingQuerier(response);
                }
                else
                {
                    List<? extends Describable> result = null;
                    
                    try
                    {
                        if (limit >= 0)
                        {
                            result = querier.query(model.getName(), query, lang, modeType, limit);
                        }
                        else
                        {
                            result = querier.query(model.getName(), query, lang, modeType);
                        }
                    }
                    catch (Exception e)
                    {
                        logger.error(DB_ERROR_MESSAGE, e);
                    }
   
                    response.getWriter().print(formatter.formatQuery(result));
                }
                querier.release();
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
    private void queryFieldsAction(Pair[] searchFields, int limit, String lang, Querier.ModeType modeType, HttpServletResponse response, Formatter formatter, String modelName, boolean loggedIn)
        throws Exception
    {
        Model model = ModelList.getInstance().getModelByAlias(modelName);

        if (model != null)
        {
            response.setContentType(formatter.getContentType());

            Querier querier = QuerierFactory.newQuerier(loggedIn);
            
            logger.debug("Querier is " + querier);
                
                if (querier == null)
                {
                    reportMissingQuerier(response);
                }
                else
                {
                    List<? extends Describable> result = null;
                    
                    try
                    {
                        if (limit >= 0)
                        {
                            result = querier.query(model.getName(), searchFields, lang, modeType, limit);
                        }
                        else
                        {
                            result = querier.query(model.getName(), searchFields, lang, modeType);
                        }
                    }
                    catch (Exception e)
                    {
                        logger.error(DB_ERROR_MESSAGE, e);
                    }
   
                    response.getWriter().print(formatter.formatQuery(result));
                }
                querier.release();

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

    private boolean getLoggedIn(HttpServletRequest request)
    {
        return (request.getSession().getAttribute("logged_in") != null && ((Boolean) request.getSession().getAttribute("logged_in")).booleanValue());
    }

}
