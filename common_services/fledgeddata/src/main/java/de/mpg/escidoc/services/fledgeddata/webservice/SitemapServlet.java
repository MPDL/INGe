/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.escidoc.services.fledgeddata.webservice;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.SocketException;
import java.util.Properties;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.fledgeddata.oai.OAIUtil;
import de.mpg.escidoc.services.fledgeddata.oai.oaiCatalog;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.OAIInternalServerError;
import de.mpg.escidoc.services.fledgeddata.oai.verb.ListIdentifiers;
import de.mpg.escidoc.services.fledgeddata.sitemap.Sitemap;


/**
 * 
 * @author kleinfe1
 *
 */
public class SitemapServlet extends HttpServlet 
{

	private static final Logger LOGGER = Logger.getLogger(SitemapServlet.class);
	private Properties properties = new Properties();
	
    /**
     * init is called one time when the Servlet is loaded. This is the
     * place where one-time initialization is done. Specifically, we
     * load the properties file for this application.
     *
     * @param config servlet configuration information
     * @exception ServletException there was a problem with initialization
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        try {
        	LOGGER.info("[FDS] Initialize oai servlet.");
            this.properties = OAIUtil.loadProperties();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new ServletException(e.getMessage());
        } catch (Throwable e) {
            e.printStackTrace();
            throw new ServletException(e.getMessage());
        }
    }   

    /**
     * 
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException 
    {
    	try 
        {   System.out.println("CREATE SITEMAP");
    		String listIdentfiersXml = "";
        	listIdentfiersXml = ListIdentifiers.construct(properties, request, response);
            String result = Sitemap.createSitemap(listIdentfiersXml);
            
            response.setStatus(200);
            response.setContentType("application/xml");
            OutputStream out = response.getOutputStream();
            out.write(result.getBytes("UTF-8"));
            out.close();
            
        } catch (OAIInternalServerError e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (SocketException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Throwable e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    /**
     * Peform a POST action. Actually this gets shunted to GET
     *
     * @param request the servlet's request information
     * @param response the servlet's response information
     * @exception IOException an I/O error occurred
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException 
    {
        doGet(request, response);
    }
}
