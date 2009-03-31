package de.mpg.escidoc.pubman.sword;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
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
import org.purl.sword.base.SWORDException;

import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;

/**
 * DepositServlet
 * 
 * @author Friederike Kleinfercher
 */
public class PubManDepositServlet extends HttpServlet 
{

    private static final long serialVersionUID = 1L;
   
    private PubManSwordServer pubMan;
    private String collection;

   private static Logger logger = Logger.getLogger(PubManDepositServlet.class);


   /** 
    * Process the Get request. This will return an unimplemented response.
    */
   protected void doGet(HttpServletRequest request,
         HttpServletResponse response) throws ServletException, IOException
   {
      // Send a '501 Not Implemented'
      response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
   }

   /**
    * Process a post request.
    */
   protected void doPost(HttpServletRequest request,
         HttpServletResponse response) throws ServletException, IOException
  {
      this.pubMan = new PubManSwordServer();
      Deposit deposit = new Deposit();
      Date date = new Date();
      AccountUserVO user = null;
      SwordUtil util = new SwordUtil();
      logger.debug("Starting deposit processing at " + date.toString() + " by " + request.getRemoteAddr());

      // AUTHENTIFICATION -----------------------------------------------------------------
      String usernamePassword = getUsernamePassword(request);
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

            deposit.setFile(request.getInputStream());
          
            // Set the X-Format-Namespace header
            deposit.setFormatNamespace(request.getHeader(HttpHeaders.X_FORMAT_NAMESPACE));

            // Set the X-No-Op header
            String noop = request.getHeader(HttpHeaders.X_NO_OP);
            if ((noop != null) && (noop.equals("true"))) 
            {
               deposit.setNoOp(true);
            } else 
            {
               deposit.setNoOp(false);
            }

//            // Set the X-Verbose header
//            String verbose = request.getHeader(HttpHeaders.X_VERBOSE);
//            if ((verbose != null) && (verbose.equals("true"))) {
//               deposit.setVerbose(true);
//            } else {
//               deposit.setVerbose(false);
//            }

            // Set the slug
            String slug = request.getHeader(HttpHeaders.SLUG);
            if (slug != null) 
            {
               deposit.setSlug(slug);
            }

            // Set the content disposition
            deposit.setContentDisposition(request.getHeader(HttpHeaders.CONTENT_DISPOSITION));

            // Set the IP address
            deposit.setIPAddress(request.getRemoteAddr());

            // Set the deposit location
            // Set link to pubItem
            deposit.setLocation("TODO: Location");

            // Set the content type
            deposit.setContentType(request.getContentType());

            // Set the content length
            String cl = request.getHeader(HttpHeaders.CONTENT_LENGTH);
            if ((cl != null) && (!cl.equals(""))) {
               deposit.setContentLength(Integer.parseInt(cl));	
            }

            // Get the DepositResponse
            DepositResponse dr = this.pubMan.doDeposit(deposit);

            // Print out the Deposit Response
            response.setStatus(dr.getHttpResponse());
            // response.setContentType("application/atomserv+xml");
            response.setContentType("application/xml");
            PrintWriter out = response.getWriter();
            out.write(dr.marshall());
            out.flush();
         
      } 
      catch (SWORDAuthenticationException sae) 
      {
          //TODO
      } 
      catch (SWORDException se) 
      {
         // Throw a HTTP 500
         response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

         // Is there an appropriate error header to return?
         if (se.getErrorCode() != null) 
         {
            response.setHeader(HttpHeaders.X_ERROR_CODE, se.getErrorCode());
         }
         logger.error(se.toString());
      } 
      catch (IOException ioe) 
      {
         response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         logger.error(ioe.toString());
      } 
    }

   /**
    * Utiliy method to return the username and password (separated by a colon ':')
    * 
    * @param request
    * @return The username and password combination
    */
   private String getUsernamePassword(HttpServletRequest request) {
      try 
      {
         String authHeader = request.getHeader("Authorization");
         if (authHeader != null) 
         {
            StringTokenizer st = new StringTokenizer(authHeader);
            if (st.hasMoreTokens()) 
            {
               String basic = st.nextToken();
               if (basic.equalsIgnoreCase("Basic")) 
               {
                  String credentials = st.nextToken();
                  String userPass = new String(Base64.decodeBase64(credentials.getBytes()));
                  return userPass;
               }
            }
         }
      } 
      catch (Exception e) 
      {
         logger.debug(e.toString());
      }
      return null;
   }

}
