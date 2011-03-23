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
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */

package de.mpg.escidoc.pubman.sword;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.purl.sword.base.Deposit;
import org.purl.sword.base.DepositResponse;
import org.purl.sword.base.SWORDAuthenticationException;
import org.purl.sword.base.SWORDContentTypeException;

import de.escidoc.core.common.exceptions.application.notfound.ContentStreamNotFoundException;
import de.mpg.escidoc.pubman.sword.PubManSwordErrorDocument.swordError;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.pubman.exceptions.PubItemStatusInvalidException;
import de.mpg.escidoc.services.validation.ItemInvalidException;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportItemVO;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportVO;

/**
 * DepositServlet for the PubMan SWORD interface.
 * @author Friederike Kleinfercher
 */
public class PubManDepositServlet extends HttpServlet
{

    private static final long serialVersionUID = 1L;
    private final Logger logger = Logger.getLogger(PubManDepositServlet.class);
    private String collection;
    PubManSwordServer pubMan;
    private String error = "";
    private PubManSwordErrorDocument errorDoc;
    private boolean validDeposit = true;
    private final String md5Header = "";

    /**
     * Process the GET request. This will return an unimplemented response.
     */
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
    throws ServletException, IOException
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
    @Override
    protected void doPut(HttpServletRequest request,
            HttpServletResponse response)
    throws ServletException, IOException
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
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
    throws ServletException, IOException
    {
        this.pubMan = new PubManSwordServer();
        SwordUtil util = new SwordUtil();
        Deposit deposit = new Deposit();
        AccountUserVO user = null;
        this.errorDoc = new PubManSwordErrorDocument();
        DepositResponse dr = null;

        // Authentification ---------------------------------------------
        String usernamePassword = this.getUsernamePassword(request);
        if (usernamePassword == null)
        {
            this.errorDoc.setSummary("No user credentials provided.");
            this.errorDoc.setErrorDesc(swordError.ErrorBadRequest);
            this.validDeposit = false;
        }
        else
        {
            int p = usernamePassword.indexOf(":");
            if (p != -1)
            {
                deposit.setUsername(usernamePassword.substring(0, p));
                deposit.setPassword(usernamePassword.substring(p + 1));
                user = util.getAccountUser(deposit.getUsername(), deposit.getPassword());
                this.pubMan.setCurrentUser(user);
            }
        }

        try
        {
            // Deposit --------------------------------------------------
            //Check if login was successfull
            if (this.pubMan.getCurrentUser() == null && this.validDeposit)
            {
                this.errorDoc.setSummary("Login user: "+deposit.getUsername()+" failed.");
                this.errorDoc.setErrorDesc(swordError.AuthentificationFailure);
                this.validDeposit = false;
            }

            //Check if collection was provided
            this.collection = request.getParameter("collection");
            if ((this.collection == null || this.collection.equals("")) && this.validDeposit)
            {
                this.collection = request.getParameter("collection");
                this.errorDoc.setSummary("No collection provided in request.");
                this.errorDoc.setErrorDesc(swordError.ErrorBadRequest);
                this.validDeposit = false;
            }

            //Check if user has depositing rights for this collection
            else
            {
                if (!util.checkCollection(this.collection, user) && this.validDeposit)
                {
                    this.errorDoc.setSummary("User: " + deposit.getUsername()
                            +" does not have depositing rights for collection " + this.collection +".");
                    this.errorDoc.setErrorDesc(swordError.AuthorisationFailure);
                    this.validDeposit = false;
                }
            }

            deposit.setFile(request.getInputStream());
            deposit = this.readHttpHeader(deposit, request);

            //Check if metadata format is supported
            if (!util.checkMetadatFormat(deposit.getFormatNamespace()))
            {
                throw new SWORDContentTypeException();
            }

            if (this.validDeposit)
            {
                // Get the DepositResponse
                dr = this.pubMan.doDeposit(deposit, this.collection);
            }
        }
        catch (SWORDAuthenticationException sae)
        {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, this.getError());
            this.logger.error(sae.toString());
            this.validDeposit = false;

        }
        catch (SWORDContentTypeException e)
        {
            this.errorDoc.setSummary("File format not supported.");
            this.errorDoc.setErrorDesc(swordError.ErrorContent);
            this.validDeposit = false;
        }
        catch (ContentStreamNotFoundException e)
        {
            this.errorDoc.setSummary("No metadata File was found.");
            this.errorDoc.setErrorDesc(swordError.ErrorBadRequest);
            this.validDeposit = false;
        }
        catch (ItemInvalidException e)
        {
            ValidationReportItemVO itemReport = null;
            ValidationReportVO report = e.getReport();
            String error = "";
            for (int i = 0; i < report.getItems().size(); i++)
            {
                itemReport = report.getItems().get(i);
                error +=  itemReport.getContent() + "\n";
            }
            this.errorDoc.setSummary(error);
            this.errorDoc.setErrorDesc(swordError.ValidationFailure);
            this.validDeposit = false;
        }
        catch (PubItemStatusInvalidException e)
        {
            logger.error("Error in sword processing", e);
            this.errorDoc.setSummary("Provided item has wrong status.");
            this.errorDoc.setErrorDesc(swordError.ErrorBadRequest);
            this.validDeposit = false;
        }
        catch (Exception ioe)
        {
            logger.error("Error in sword processing", ioe);
            this.errorDoc.setSummary("An internal server error occurred.");
            this.errorDoc.setErrorDesc(swordError.InternalError);
            this.validDeposit = false;
        }
        try
        {
            //Write response atom
            if (this.validDeposit)
            {
                response.setStatus(dr.getHttpResponse());
                response.setContentType("application/xml");
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Location", dr.getEntry().getContent().getSource());
                PrintWriter out = response.getWriter();
                out.write(dr.marshall());
                out.flush();
            }
            //Write error document
            else
            {
                String errorXml = this.errorDoc.createErrorDoc();
                response.setStatus(this.errorDoc.getStatus());
                response.setContentType("application/xml");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                out.write(errorXml);
                out.flush();
            }
        }
        catch (Exception e)
        {
            this.logger.error("Error document could not be created.", e);
            throw new RuntimeException();
        }

        this.pubMan.setCurrentUser(null);
        this.validDeposit = true;
    }


    /**
     * Utiliy method to return the username and password
     * (separated by a colon).
     * 
     * @param request
     * @return The username and password combination
     */
    private String getUsernamePassword(HttpServletRequest request)
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
            this.logger.debug(e.toString());
        }
        return null;
    }

    /**
     * Reads out the values in the http header and sets the
     * Deposit object.
     * @param deposit
     * @return
     */
    private Deposit readHttpHeader(Deposit deposit, HttpServletRequest request) throws SWORDContentTypeException
    {
        // Set the X-No-Op header
        String noop = request.getHeader("X-No-Op");
        if ((noop != null) && (noop.equals("true")))
        {
            deposit.setNoOp(true);
        }
        else
        {
            deposit.setNoOp(false);
        }

        // Set the X-Verbose header
        String verbose = request.getHeader("X-Verbose");
        if ((verbose != null) && (verbose.equals("true")))
        {
            deposit.setVerbose(true);
        }
        else
        {
            deposit.setVerbose(false);
        }

        //       // Set the MD5 Checksum header
        //       String checksum = request.getHeader("Content-MD5");
        //       if ((checksum != null) && (!checksum.equals("")))
        //       {
        //           this.md5Header = checksum;
        //       }

        //Check X-On-Behalf-Of header
        String mediation = request.getHeader("X-On-Behalf-Of");
        if ((mediation != null) && (!mediation.equals("")))
        {
            this.errorDoc.setSummary("Mediation not supported.");
            this.errorDoc.setErrorDesc(swordError.MediationNotAllowed);
            this.validDeposit = false;
        }

        //Check X-Packaging header
        String packaging = request.getHeader("X-Packaging");
        if ((packaging != null) && (!packaging.equals("")))
        {
            deposit.setFormatNamespace(packaging);
        }
        else
        {
            throw new SWORDContentTypeException();
        }

        //Check Content-deposition  header
        String filename = request.getHeader("Content-Disposition:filename");
        if ((filename != null) && (filename.equals("")))
        {
            deposit.setContentDisposition(filename);
        }

        //Check ContentType  header
        String contentType = request.getHeader("Content-Type");
        if ((contentType != null) && (contentType.equals("")))
        {
            deposit.setContentType(contentType);
        }

        return deposit;
    }

    public boolean checkChecksum(InputStream fis, String md5) throws NoSuchAlgorithmException, IOException
    {
        boolean check = false;
        byte[] buffer = new byte[1024];
        String checkCalc="";
        MessageDigest md = MessageDigest.getInstance("MD5");
        int numRead;

        do
        {
            numRead = fis.read(buffer);
            if (numRead > 0)
            {
                md.update(buffer, 0, numRead);
            }
        }
        while (numRead != -1);
        fis.close();

        byte[] digest = md.digest();
        BigInteger bigInt = new BigInteger(1,digest);
        checkCalc = bigInt.toString(16);
        while(checkCalc.length() < 32 )
        {
            checkCalc = "0"+checkCalc;
        }

        if (md5.equals(checkCalc))
        {
            check = true;
        }

        return check;
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

    public PubManSwordErrorDocument getErrorDoc()
    {
        return this.errorDoc;
    }

    public void setErrorDoc(PubManSwordErrorDocument errorDoc)
    {
        this.errorDoc = errorDoc;
    }
}
