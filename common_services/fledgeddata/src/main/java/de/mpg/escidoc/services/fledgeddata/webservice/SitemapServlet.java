/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.escidoc.services.fledgeddata.webservice;

import java.io.FileNotFoundException;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.fledgeddata.oai.OAIUtil;
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
    public void init(ServletConfig config) throws ServletException 
    {
        super.init(config);
        
        try 
        {
        	LOGGER.info("[FDS] Initialize sitemap servlet.");
        	
        	LOGGER.info("[FDS] Read out properties file.");
            this.properties = OAIUtil.loadProperties();
            
            LOGGER.info("[FDS] Create initial sitemap entries.");
            Sitemap sitemap = new Sitemap();
            Sitemap.setProperties(properties);
            sitemap.start();
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new ServletException(e.getMessage());
        } catch (Throwable e) {
            e.printStackTrace();
            throw new ServletException(e.getMessage());
        }
    }   
}
