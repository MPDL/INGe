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

import org.apache.commons.discovery.tools.ResourceUtils;
import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.cone.Querier;
import de.mpg.escidoc.services.cone.QuerierFactory;

public class JQueryConeServlet extends HttpServlet
{

    private static final Logger logger = Logger.getLogger(JQueryConeServlet.class);
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        PrintWriter out = response.getWriter();
        response.setContentType("text/plain");
        
        String model = request.getPathInfo();
        String query = request.getParameter("q");
        
        Querier querier = QuerierFactory.newQuerier();
        
        Map<String, String> result = null;
        
        try
        {
            result = querier.query(model, query);
        }
        catch (Exception e) {
            logger.error("Error querying database.", e);
        }
        logger.info("XML: " + result);
        
        out.println(format(result));
        
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
            result.append("id");
            result.append("\n");
        }
        
        return result.toString();
    }
    
}
