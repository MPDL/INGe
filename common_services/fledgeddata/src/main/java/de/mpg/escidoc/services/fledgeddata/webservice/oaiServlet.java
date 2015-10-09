/**
 * Copyright 2006 OCLC Online Computer Library Center Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or
 * agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mpg.escidoc.services.fledgeddata.webservice;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Properties;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import de.mpg.escidoc.services.fledgeddata.FetchImeji;
import de.mpg.escidoc.services.fledgeddata.oai.OAIUtil;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.OAIInternalServerError;
import de.mpg.escidoc.services.fledgeddata.oai.verb.ServerVerb;


/**
 * OAIHandler is the primary Servlet.
 *
 * @author Jeffrey A. Young, OCLC Online Computer Library Center
 * @author Friederike Kleinfercher, MPDL
 */
public class oaiServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;      
    private static final String VERSION = "1.5.59";
    private static final Logger LOGGER = Logger.getLogger(oaiServlet.class);
    private Properties properties = new Properties();
    
    /**
     * Get the VERSION number
     */
    public static String getVERSION() { return VERSION; }
    
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
     * Peform the http GET action. Note that POST is shunted to here as well.
     * The verb widget is taken from the request and used to invoke an
     * OAIVerb object of the corresponding kind to do the actual work of the verb.
     *
     * @param request the servlet's request information
     * @param response the servlet's response information
     * @exception IOException an I/O error occurred
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException 
    {
        boolean serviceUnavailable = isServiceUnavailable();   
        HashMap serverVerbs = ServerVerb.getVerbs();       
        request.setCharacterEncoding("UTF-8");
            
        if (serviceUnavailable) 
        {
        	LOGGER.info("[FDS] oai servcice set to 'unavailable' in properties file.");
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
            "[FDS] Sorry. This server is down for maintenance");
        } 
        else 
        {
            try 
            {               
                String result = getResult(request, response, serverVerbs);
                Writer out = getWriter(request, response);
                out.write(result);
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
    }
    
    protected boolean isServiceUnavailable() 
    {
        if (this.properties.getProperty("oai.serviceUnavailable") != null 
        		&& this.properties.getProperty("oai.serviceUnavailable").equals("true")) 
        {
            return true;
        }
        return false;
    }

    public String getResult(HttpServletRequest request, HttpServletResponse response, HashMap serverVerbs) 
    		throws Throwable 
    {
        try 
        {
            String verb = request.getParameter("verb");
            String result;
            Class verbClass = null;
            verbClass = (Class)serverVerbs.get(verb);
            if (verbClass == null)
            {
            	verbClass = (Class)serverVerbs.get("BadVerb");
            }

            Method construct = verbClass.getMethod("construct", new Class[] {Properties.class,
                    HttpServletRequest.class, HttpServletResponse.class}); 
            try 
            {
                result = (String)construct.invoke(null, new Object[] {properties, request, response});
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
            return result;
            
        } catch (NoSuchMethodException e) {
            throw new OAIInternalServerError(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new OAIInternalServerError(e.getMessage());
        }
    }
    
    /**
     * Get a response Writer depending on acceptable encodings
     * @param request the servlet's request information
     * @param response the servlet's response information
     * @exception IOException an I/O error occurred
     */
    public static Writer getWriter(HttpServletRequest request, HttpServletResponse response) 
    		throws IOException 
    {
        Writer out;
        String encodings = request.getHeader("Accept-Encoding");

        if (encodings != null && encodings.indexOf("gzip") != -1) 
        {
            response.setHeader("Content-Encoding", "gzip");
            out = new OutputStreamWriter(new GZIPOutputStream(response.getOutputStream()),
            "UTF-8");
        } 
        else 
        	if (encodings != null && encodings.indexOf("deflate") != -1) 
        	{
	            response.setHeader("Content-Encoding", "deflate");
	            out = new OutputStreamWriter(new DeflaterOutputStream(response.getOutputStream()),
	            "UTF-8");
        	} 
        	else 
        	{
        		out = response.getWriter();
        	}
        return out;
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
