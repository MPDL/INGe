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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.cone.Querier;
import de.mpg.escidoc.services.cone.QuerierFactory;
import de.mpg.escidoc.services.cone.ModelList;
import de.mpg.escidoc.services.cone.ModelList.Model;
import de.mpg.escidoc.services.cone.util.Pair;

/**
 * Servlet to answer calls from the JQuery Javascript API.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class JQueryConeServlet extends HttpServlet
{

    private static final Logger logger = Logger.getLogger(JQueryConeServlet.class);
    private static final String ERROR_TRANSFORMING_RESULT = "Error transforming result";
    private static final String DB_ERROR_MESSAGE = "Error querying database.";
    private static final String REGEX_PREDICATE_REPLACE = ":/\\-\\.";
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String CONTENT_TYPE = "text/plain";
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        request.setCharacterEncoding(DEFAULT_ENCODING);
        
        PrintWriter out = response.getWriter();
        
        // Read the service name and action from the URL
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
            response.setContentType("text/xml");
            
            InputStream source = ResourceUtil.getResourceAsStream("explain/models.xml");
            InputStream template = ResourceUtil.getResourceAsStream("explain/jquery_explain.xsl");
            
            try
            {
                Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(template));
                transformer.setOutputProperty(OutputKeys.ENCODING, DEFAULT_ENCODING);
                transformer.transform(new StreamSource(source), new StreamResult(out));
            }
            catch (Exception e)
            {
                logger.error(ERROR_TRANSFORMING_RESULT, e);
                throw new IOException(e.getMessage());
            }
        }
        else if ("query".equals(action))
        {
            try
            {
                queryAction(request, response, out, model);
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
     * Retrieve the details for a given id.
     * 
     * @param request Just to use it in the method.
     * @param response Just to use it in the method.
     * @param out Just to use it in the method.
     * @param model The requested type of data, e.g. "jnar", "lang"
     * @throws IOException
     */
    private void detailAction(HttpServletRequest request, HttpServletResponse response, PrintWriter out, String modelName)
        throws Exception
    {
        Model model = ModelList.getInstance().new Model(modelName);

        if (ModelList.getInstance().getList().contains(model))
        {
            response.setContentType(CONTENT_TYPE);
            String id = request.getParameter("id");
            
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
                    Map<String, List<String>> result = null;
                    
                    try
                    {
                        result = querier.details(modelName, id);
                    }
                    catch (Exception e)
                    {
                        logger.error(DB_ERROR_MESSAGE, e);
                    }
   
                    out.println(formatDetails(result));
                }
            }
        }
        else
        {
            reportUnknownModel(modelName, response);
        }
    }

    /**
     * @param request
     * @param response
     * @param out
     * @param model
     * @throws IOException
     */
    private void queryAction(HttpServletRequest request, HttpServletResponse response, PrintWriter out, String modelName)
        throws Exception
    {
        Model model = ModelList.getInstance().new Model(modelName);

        if (ModelList.getInstance().getList().contains(model))
        {
            response.setContentType(CONTENT_TYPE);
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
                        result = querier.query(modelName, query, lang);
                    }
                    catch (Exception e)
                    {
                        logger.error(DB_ERROR_MESSAGE, e);
                    }
   
                    out.println(formatQuery(result));
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
     * Formats an RDF XML String into a JQuery readable list.
     * 
     * @param result The RDF.
     * @return A String formatted  in a JQuery readable format.
     */
    @SuppressWarnings("unused")
    private OutputStream format(String source) throws IOException
    {
        
     // Use Saxon for XPath2.0 support
        System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");

        InputStream template = ResourceUtil.getResourceAsStream("xslt/rdf2jquery.xsl");
        OutputStream result = new ByteArrayOutputStream();
        
        try
        {
            Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(template));
            transformer.transform(new StreamSource(new StringReader(source)), new StreamResult(result));
        }
        catch (Exception e)
        {
            logger.error(ERROR_TRANSFORMING_RESULT, e);
            throw new IOException(e.getMessage());
        }
        return result;
    }

    /**
     * Formats an Map&lt;String, String> into a JQuery readable list.
     * 
     * @param result The RDF.
     * @return A String formatted  in a JQuery readable format.
     */
    private String formatQuery(List<Pair> pairs) throws IOException
    {
        
        StringWriter result = new StringWriter();
        
        if (pairs != null)
        {
            for (Pair pair : pairs)
            {
                String key = pair.getKey();
                String value = pair.getValue();
                result.append(value);
                result.append("|");
                result.append(key);
                result.append("\n");
            }
        }
        
        return result.toString();
    }

    /**
     * Formats an Map&lt;String, String> into a JQuery readable list.
     * 
     * @param result The RDF.
     * @return A String formatted  in a JQuery readable format.
     */
    private String formatDetails(Map<String, List<String>> triples) throws IOException
    {
        
        StringWriter result = new StringWriter();
        
        result.append("{\n");
        for (Iterator<String> iterator = triples.keySet().iterator(); iterator.hasNext();)
        {
            String predicate = (String) iterator.next();
            List<String> objects = triples.get(predicate);
            
            result.append("\"");
            result.append(predicate.replaceAll("[" + REGEX_PREDICATE_REPLACE + "]+", "_").replace("'", "\\'"));
            result.append("\" : \"");
            if (objects.size() == 1)
            {
                result.append(objects.get(0).replace("'", "\\'"));
            }
            else
            {
                result.append("{\n");
                for (Iterator<String> iterator2 = objects.iterator(); iterator2.hasNext();)
                {
                    String object = (String) iterator2.next();
                    result.append("\"");
                    result.append(object.replace("'", "\\'"));
                    result.append("\"");
                    if (iterator2.hasNext())
                    {
                        result.append(",");
                    }
                    result.append("\n");
                }
                result.append("}");
            }
            result.append("\"");
            if (iterator.hasNext())
            {
                result.append(",");
            }
            result.append("\n");
        }
        result.append("}");
        return result.toString();
    }
    
}
