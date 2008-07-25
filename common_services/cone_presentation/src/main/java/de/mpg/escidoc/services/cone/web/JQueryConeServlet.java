package de.mpg.escidoc.services.cone.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
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

import de.mpg.escidoc.services.cone.ServiceList.Service;
import de.mpg.escidoc.services.cone.util.ResourceUtil;
import de.mpg.escidoc.services.cone.Querier;
import de.mpg.escidoc.services.cone.QuerierFactory;
import de.mpg.escidoc.services.cone.ServiceList;

public class JQueryConeServlet extends HttpServlet
{

    private static final Logger logger = Logger.getLogger(JQueryConeServlet.class);
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

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
            
            InputStream source = ResourceUtil.getResourceAsStream("explain/services.xml");
            InputStream template = ResourceUtil.getResourceAsStream("explain/jquery_explain.xsl");
            
            try
            {
                Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(template));
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                transformer.transform(new StreamSource(source), new StreamResult(out));
            }
            catch (Exception e) {
                logger.error("Error transforming result", e);
                throw new IOException(e.getMessage());
            }
        }
        else if ("query".equals(action))
        {
            queryAction(request, response, out, model);
        }
        else if ("details".equals(action))
        {
            detailAction(request, response, out, model);
        }
    }

    private void detailAction(HttpServletRequest request, HttpServletResponse response, PrintWriter out, String model) throws IOException
    {
        Service service = ServiceList.getInstance().new Service(model);

        if (ServiceList.getInstance().getList().contains(service))
        {
            response.setContentType("text/plain");
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
                
                logger.debug("Querier is " + querier);
                
                if (querier == null)
                {
                    reportMissingQuerier(response);
                }
                else
                {
                    Map<String, String> result = null;
                    
                    try
                    {
                        result = querier.details(model, id);
                    }
                    catch (Exception e) {
                        logger.error("Error querying database.", e);
                    }
   
                    out.println(formatDetails(result));
                }
            }
        }
        else
        {
            reportUnknownModel(model, response);
        }
    }

    /**
     * @param request
     * @param response
     * @param out
     * @param model
     * @throws IOException
     */
    private void queryAction(HttpServletRequest request, HttpServletResponse response, PrintWriter out, String model)
            throws IOException
    {
        Service service = ServiceList.getInstance().new Service(model);

        if (ServiceList.getInstance().getList().contains(service))
        {
            response.setContentType("text/plain");
            String query = request.getParameter("q");
            
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
                    Map<String, String> result = null;
                    
                    try
                    {
                        result = querier.query(model, query);
                    }
                    catch (Exception e) {
                        logger.error("Error querying database.", e);
                    }
   
                    out.println(formatQuery(result));
                }
            }
        }
        else
        {
            reportUnknownModel(model, response);
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
        response.getWriter().println("Error: Service " + model + " is not known.");
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
        catch (Exception e) {
            logger.error("Error transforming result", e);
            throw new IOException(e.getMessage());
        }
        return result;
    }

    /**
     * Formats an Map<String, String> into a JQuery readable list.
     * 
     * @param result The RDF.
     * @return A String formatted  in a JQuery readable format.
     */
    private String formatQuery(Map<String, String> map) throws IOException
    {
        
        StringWriter result = new StringWriter();
        
        if (map != null)
        {
            for (String id : map.keySet())
            {
                String value = map.get(id);
                result.append(value);
                result.append("|");
                result.append(id);
                result.append("\n");
            }
        }
        
        return result.toString();
    }

    /**
     * Formats an Map<String, String> into a JQuery readable list.
     * 
     * @param result The RDF.
     * @return A String formatted  in a JQuery readable format.
     */
    private String formatDetails(Map<String, String> map) throws IOException
    {
        
        StringWriter result = new StringWriter();
        
        result.append("{\n");
        
        for (String id : map.keySet())
        {
            String value = map.get(id);
            
            result.append("'");
            result.append(id.substring(id.lastIndexOf("/")).replace("'", "\\'"));
            result.append("' : '");
            result.append(value.replace("'", "\\'"));
            result.append("'\n");
        }
        result.append("}");
        return result.toString();
    }
    
}
