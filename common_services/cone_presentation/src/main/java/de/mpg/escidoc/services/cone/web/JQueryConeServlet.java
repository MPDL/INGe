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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.cone.util.ResourceUtil;
import de.mpg.escidoc.services.cone.Querier;
import de.mpg.escidoc.services.cone.QuerierFactory;

public class JQueryConeServlet extends HttpServlet
{

    private static final Logger logger = Logger.getLogger(JQueryConeServlet.class);
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        PrintWriter out = response.getWriter();
        
        String model = request.getPathInfo();
        if (model != null && !"".equals(model))
        {
            model = model.substring(1);
        }
        
        logger.debug("Querying for '" + model + "'");
        
        if ("explain".equals(model))
        {
            response.setContentType("text/xml");
            out.println(ResourceUtil.getResourceAsString("explain/jquery_explain.xml"));
        }
        else
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
                
                Map<String, String> result = null;
                
                try
                {
                    result = querier.query(model, query);
                }
                catch (Exception e) {
                    logger.error("Error querying database.", e);
                }
                logger.debug("XML: " + result);
                
                out.println(format(result));
            }
        }
    }

    private void reportEmptyParameter(String string, HttpServletResponse response)
    {
        // do not report empty parameters, just return nothing.
    }

    private void reportMissingParameter(String param, HttpServletResponse response) throws IOException
    {
        response.setStatus(500);
        response.getWriter().println("Parameter '" + param + "' is missing.");        
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
    private String format(Map<String, String> map) throws IOException
    {
        
        StringWriter result = new StringWriter();
        
        for (String id : map.keySet())
        {
            String value = map.get(id);
            result.append(value);
            result.append("|");
            result.append(id);
            result.append("\n");
        }
        
        return result.toString();
    }
    
}
