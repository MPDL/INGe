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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.sword;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.purl.sword.base.SWORDAuthenticationException;
import org.purl.sword.base.ServiceDocumentRequest;

import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;


/**
 * The servlet to provide a servicedocument for Pubman.
 * @author Friederike Kleinfercher
 */
public class PubManServiceDocumentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Logger log = Logger.getLogger(PubManServiceDocumentServlet.class);

    private AccountUserVO currentUser;
    private PubManSwordServer swordServer = new PubManSwordServer();


    /**
     * Initialise the servlet.
     */
    public void init()
    {
        // Instantiate the correct SWORD Server class
        String className = getServletContext().getInitParameter("server-class");
        if (className == null)
        {
            this.log.fatal("Unable to read value of 'sword-server-class' from Servlet context");
        }
        else
        {
            try
            {
                this.swordServer = (PubManSwordServer)Class.forName(className).newInstance();
            }
            catch (Exception e)
            {
                this.log.fatal("Unable to instantiate class from 'sword-server-class': " + className);
            }
        }
    }

    /**
     * Process the GET request.
     * @param HttpServletRequest
     * @param HttpServletResponse
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) 
                         throws ServletException, IOException
    {
        // Create the ServiceDocumentRequest
        ServiceDocumentRequest sdr = new ServiceDocumentRequest();
        SwordUtil util = new SwordUtil();
        AccountUserVO user = null;

        String usernamePassword = this.getUsernamePassword(request);
        if ((usernamePassword != null) && (!usernamePassword.equals("")))
        {
            int p = usernamePassword.indexOf(":");
            if (p != -1) {
                sdr.setUsername(usernamePassword.substring(0, p));
                sdr.setPassword(usernamePassword.substring(p + 1));
                user = util.getAccountUser(sdr.getUsername(), sdr.getPassword());
                this.currentUser = user;
            }
        }
        else
        {
            String s = "Basic realm=\"SWORD\"";
            response.setHeader("WWW-Authenticate", s);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try
        {
            String doc = swordServer.doServiceDocument(sdr);
            this.currentUser = null;
            
            // Print out the Service Document
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/xml");
            PrintWriter out = response.getWriter();
            out.write(doc);
            out.flush();
        }
        catch (SWORDAuthenticationException sae)
        {
            response.setHeader("WWW-Authenticate", sae.getLocalizedMessage());
            response.setStatus(401);
            response.setCharacterEncoding("UTF-8");
            this.currentUser = null;
        }
        catch (Exception e)
        {
            this.log.error(e);
        }
    }

    /** 
     * Process the POST request. This will return an unimplemented response.
     * @param HttpServletRequest
     * @param HttpServletResponse
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) 
                          throws ServletException, IOException
    {
        response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
    }

    /**
     * Utiliy method to return the username and password
     * (separated by a colon ':').
     * 
     * @param request
     * @return The username and password combination
     */
    private String getUsernamePassword (HttpServletRequest request)
    {
       try {
          String authHeader = request.getHeader("Authorization");
          if (authHeader != null) {
             StringTokenizer st = new StringTokenizer(authHeader);
             if (st.hasMoreTokens()) {
                String basic = st.nextToken();
                if (basic.equalsIgnoreCase("Basic")) {
                   String credentials = st.nextToken();
                   String userPass = new String(Base64.decodeBase64(credentials.getBytes()));
                   return userPass;
                }
             }
          }
       } catch (Exception e) {
          this.log.debug(e.toString());
       }
       return null;
    }
}
