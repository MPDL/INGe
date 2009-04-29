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
import org.purl.sword.base.Deposit;
import org.purl.sword.base.DepositResponse;
import org.purl.sword.base.HttpHeaders;
import org.purl.sword.base.SWORDAuthenticationException;
import org.purl.sword.base.SWORDContentTypeException;

import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;

/**
 * DepositServlet for the PubMan SWORD interface.
 * @author Friederike Kleinfercher
 */
public class PubManDepositServlet extends HttpServlet 
{

    private static final long serialVersionUID = 1L;
    private Logger logger = Logger.getLogger(PubManDepositServlet.class);
    private String collection;
    PubManSwordServer pubMan;
    private String error = "";

/** 
    * Process the GET request. This will return an unimplemented response.
    */
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
   }
   
   /** 
    * Process the PUT request. 
    * @param HttpServletRequest
    * @param HttpServletResponse
    * @throws ServletException
    * @throws IOException
    */
   protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      this.doPost(request, response);
   }

   /**
    * Process a POST request.
    * @param HttpServletRequest
    * @param HttpServletResponse
    * @throws ServletException
    * @throws IOException
    */
   protected void doPost(HttpServletRequest request,
         HttpServletResponse response) throws ServletException, IOException
  {
      this.pubMan = new PubManSwordServer();
      SwordUtil util = new SwordUtil();
      Deposit deposit = new Deposit();
      AccountUserVO user = null;

      this.logger.debug("Starting deposit processing by " + request.getRemoteAddr());

      // AUTHENTIFICATION -----------------------------------------------------------------
      String usernamePassword = this.getUsernamePassword(request);
      if ((usernamePassword != null) && (!usernamePassword.equals(""))) 
      {
         int p = usernamePassword.indexOf(":");
         if (p != -1) 
         {
            deposit.setUsername(usernamePassword.substring(0, p));
            deposit.setPassword(usernamePassword.substring(p+1));
            user = util.getAccountUser(deposit.getUsername(), deposit.getPassword());
            this.pubMan.setCurrentUser(user);
         } 
      } 
      else 
      {
          String s = "Basic realm=\"SWORD\"";
          response.setHeader("WWW-Authenticate", s);
          response.setStatus(401);
          return;
      }

      // DEPOSIT --------------------------------------------------------------------------
      try {

            //Check if login was successfull
            if (this.pubMan.getCurrentUser() == null)
            {
                this.logger.info("User: " + deposit.getUsername() + " not recognized.");
                response.sendError(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION, "User: " + deposit.getUsername() + " not recognized.");
                this.pubMan.setCurrentUser(null);
                return;
            }
          
            this.collection = request.getParameter("collection");
            //Check if collection was provided
            if (this.collection == null || this.collection.equals(""))
            {
                this.logger.info("No collection provided in request.");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No collection provided in request.");
                this.pubMan.setCurrentUser(null);
                return;
            }
            //Check if user has depositing rights for this collection
            else
            {
                if (!util.checkCollection(this.collection, user))
                {
                   this.logger.error("User: " + deposit.getUsername() + 
                            " does not have depositing rights for collection " + this.collection +".");
                   response.sendError(HttpServletResponse.SC_FORBIDDEN, "User: " + deposit.getUsername() + 
                           " does not have depositing rights for collection " + this.collection +".");
                   this.pubMan.setCurrentUser(null);
                   return;
                }
            }
            
            deposit.setFile(request.getInputStream());

            // Set the X-No-Op header
            String noop = request.getHeader(HttpHeaders.X_NO_OP);
            if ((noop != null) && (noop.equals("true"))) 
            {
               deposit.setNoOp(true);
            } else 
            {
               deposit.setNoOp(false);
            }

            // Set the X-Verbose header
//            String verbose = request.getHeader(HttpHeaders.X_VERBOSE);
//            if ((verbose != null) && (verbose.equals("true"))) 
//            {
//               deposit.setVerbose(true);
//            } 
//            else 
//            {
//               deposit.setVerbose(false);
//            }
            
            // Get the DepositResponse
            DepositResponse dr = this.pubMan.doDeposit(deposit, this.collection);
            
            // Print out the Deposit Response
            response.setStatus(dr.getHttpResponse());
            response.setContentType("application/xml");
            PrintWriter out = response.getWriter();
            out.write(dr.marshall());
            out.flush();
          
      } 
      catch (SWORDAuthenticationException sae) 
      {
          response.sendError(HttpServletResponse.SC_FORBIDDEN, this.getError());
          this.logger.error(sae.toString());
      } 
      catch (SWORDContentTypeException e)
      {
          response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, this.getError());
          this.logger.error("Internal error.", e);
      }
      catch (Exception ioe) 
      {
         response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, this.getError());
         this.logger.error(ioe.toString());
      } 
      this.pubMan.setCurrentUser(null);
    }
   
   /**
    * Utiliy method to return the username and password (separated by a colon ':')
    * 
    * @param request
    * @return The username and password combination
    */
   private String getUsernamePassword(HttpServletRequest request) {
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
         this.logger.debug(e.toString());
      }
      return null;
   }
   

   public PubManSwordServer getPubMan()
    {
        return this.pubMan;
    }

    public void setPubMan(PubManSwordServer pubMan)
    {
        this.pubMan = pubMan;
    }

    public String getError()
    {
        return this.error;
    }

    public void setError(String error)
    {
        this.error = error;
    }
}
