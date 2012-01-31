/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.escidoc.services.fledgeddata.webservice;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 
 * @author kleinfe1
 *
 */
public class SitemapServlet extends HttpServlet {

    public void init(ServletConfig config) throws ServletException 
    {
        
    }    

    /**
     * 
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException 
    {
     
        request.setCharacterEncoding("UTF-8");

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
