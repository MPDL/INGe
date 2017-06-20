/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.pubman.web.sword;

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
import org.purl.sword.base.SWORDContentTypeException;

import de.escidoc.core.common.exceptions.application.notfound.ContentStreamNotFoundException;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportItemVO;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationServiceException;
import de.mpg.mpdl.inge.model.exception.IngeServiceException;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.pubman.web.sword.PubManSwordErrorDocument.swordError;

/**
 * DepositServlet for the PubMan SWORD interface.
 * 
 * @author Friederike Kleinfercher
 */
@SuppressWarnings("serial")
public class PubManDepositServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(PubManDepositServlet.class);

  private PubManSwordErrorDocument errorDoc;
  private boolean validDeposit = true;

  /**
   * Process the GET request. This will return an unimplemented response.
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
  }

  /**
   * Process the PUT request.
   * 
   * @param HttpServletRequest
   * @param HttpServletResponse
   * @throws ServletException
   * @throws IOException
   */
  @Override
  protected void doPut(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    this.doPost(request, response);
  }

  /**
   * Process a POST request.
   * 
   * @param HttpServletRequest
   * @param HttpServletResponse
   * @throws ServletException
   * @throws IOException
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    final PubManSwordServer pubManSwordServer = new PubManSwordServer();
    final SwordUtil util = new SwordUtil();
    Deposit deposit = new Deposit();
    AccountUserVO user = null;
    this.errorDoc = new PubManSwordErrorDocument();
    DepositResponse dr = null;

    // Authentification ---------------------------------------------
    final String usernamePassword = this.getUsernamePassword(request);
    if (usernamePassword == null) {
      this.errorDoc.setSummary("No user credentials provided.");
      this.errorDoc.setErrorDesc(swordError.ErrorBadRequest);
      this.validDeposit = false;
    } else {
      final int p = usernamePassword.indexOf(":");
      if (p != -1) {
        deposit.setUsername(usernamePassword.substring(0, p));
        deposit.setPassword(usernamePassword.substring(p + 1));
        user = util.getAccountUser(deposit.getUsername(), deposit.getPassword());
        pubManSwordServer.setCurrentUser(user);
      }
    }

    try {
      // Deposit --------------------------------------------------
      // Check if login was successfull
      if (pubManSwordServer.getCurrentUser() == null && this.validDeposit) {
        this.errorDoc.setSummary("Login user: " + deposit.getUsername() + " failed.");
        this.errorDoc.setErrorDesc(swordError.AuthentificationFailure);
        this.validDeposit = false;
      }

      // Check if collection was provided
      String collection = request.getParameter("collection");
      if ((collection == null || collection.equals("")) && this.validDeposit) {
        collection = request.getParameter("collection");
        this.errorDoc.setSummary("No collection provided in request.");
        this.errorDoc.setErrorDesc(swordError.ErrorBadRequest);
        this.validDeposit = false;
      }

      // Check if user has depositing rights for this collection
      else {
        if (!util.checkCollection(collection, user) && this.validDeposit) {
          this.errorDoc.setSummary("User: " + deposit.getUsername()
              + " does not have depositing rights for collection " + collection + ".");
          this.errorDoc.setErrorDesc(swordError.AuthorisationFailure);
          this.validDeposit = false;
        }
      }

      deposit.setFile(request.getInputStream());
      deposit = this.readHttpHeader(deposit, request);

      // Check if metadata format is supported
      if (!util.checkMetadatFormat(deposit.getFormatNamespace())) {
        throw new SWORDContentTypeException();
      }

      if (this.validDeposit) {
        // Get the DepositResponse
        dr = pubManSwordServer.doDeposit(deposit, collection);
      }
    } catch (final SWORDContentTypeException e) {
      this.errorDoc.setSummary("File format not supported.");
      this.errorDoc.setErrorDesc(swordError.ErrorContent);
      this.validDeposit = false;
    } catch (final ContentStreamNotFoundException e) {
      this.errorDoc.setSummary("No metadata File was found.");
      this.errorDoc.setErrorDesc(swordError.ErrorBadRequest);
      this.validDeposit = false;
    } catch (final ValidationException e) {
      ValidationReportItemVO itemReport = null;
      final ValidationReportVO report = e.getReport();
      String error = "";
      for (int i = 0; i < report.getItems().size(); i++) {
        itemReport = report.getItems().get(i);
        error += itemReport.getContent() + "\n";
      }
      this.errorDoc.setSummary(error);
      this.errorDoc.setErrorDesc(swordError.ValidationFailure);
      this.validDeposit = false;
    } catch (final ValidationServiceException e) {
      PubManDepositServlet.logger.error("Error in Validation", e);
      this.errorDoc.setSummary(e.getMessage());
      this.errorDoc.setErrorDesc(swordError.ValidationFailure);
      this.validDeposit = false;
    } catch (final IngeServiceException e) {
      PubManDepositServlet.logger.error("Error in sword processing", e);
      this.errorDoc.setSummary("Provided item has wrong status.");
      this.errorDoc.setErrorDesc(swordError.ErrorBadRequest);
      this.validDeposit = false;
    } catch (final Exception ioe) {
      PubManDepositServlet.logger.error("Error in sword processing", ioe);
      this.errorDoc.setSummary("An internal server error occurred.");
      this.errorDoc.setErrorDesc(swordError.InternalError);
      this.validDeposit = false;
    }
    try {
      // Write response atom
      if (this.validDeposit) {
        response.setStatus(dr.getHttpResponse());
        response.setContentType("application/xml");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Location", dr.getEntry().getContent().getSource());
        final PrintWriter out = response.getWriter();
        out.write(dr.marshall());
        out.flush();
      }
      // Write error document
      else {
        final String errorXml = this.errorDoc.createErrorDoc();
        response.setStatus(this.errorDoc.getStatus());
        response.setContentType("application/xml");
        response.setCharacterEncoding("UTF-8");
        final PrintWriter out = response.getWriter();
        out.write(errorXml);
        out.flush();
      }
    } catch (final Exception e) {
      PubManDepositServlet.logger.error("Error document could not be created.", e);
      throw new RuntimeException();
    }

    pubManSwordServer.setCurrentUser(null);
    this.validDeposit = true;
  }

  /**
   * Utiliy method to return the username and password (separated by a colon).
   * 
   * @param request
   * @return The username and password combination
   */
  private String getUsernamePassword(HttpServletRequest request) {
    try {
      final String authHeader = request.getHeader("Authorization");
      if (authHeader != null) {
        final StringTokenizer st = new StringTokenizer(authHeader);
        if (st.hasMoreTokens()) {
          final String basic = st.nextToken();
          if (basic.equalsIgnoreCase("Basic")) {
            final String credentials = st.nextToken();
            final String userPass = new String(Base64.decodeBase64(credentials.getBytes()));
            return userPass;
          }
        }
      }
    } catch (final Exception e) {
      PubManDepositServlet.logger.debug(e.toString());
    }

    return null;
  }

  /**
   * Reads out the values in the http header and sets the Deposit object.
   * 
   * @param deposit
   * @return
   */
  private Deposit readHttpHeader(Deposit deposit, HttpServletRequest request)
      throws SWORDContentTypeException {
    // Set the X-No-Op header
    final String noop = request.getHeader("X-No-Op");
    if ((noop != null) && (noop.equals("true"))) {
      deposit.setNoOp(true);
    } else {
      deposit.setNoOp(false);
    }

    // Set the X-Verbose header
    final String verbose = request.getHeader("X-Verbose");
    if ((verbose != null) && (verbose.equals("true"))) {
      deposit.setVerbose(true);
    } else {
      deposit.setVerbose(false);
    }

    // Check X-On-Behalf-Of header
    final String mediation = request.getHeader("X-On-Behalf-Of");
    if ((mediation != null) && (!mediation.equals(""))) {
      this.errorDoc.setSummary("Mediation not supported.");
      this.errorDoc.setErrorDesc(swordError.MediationNotAllowed);
      this.validDeposit = false;
    }

    // Check X-Packaging header
    final String packaging = request.getHeader("X-Packaging");
    if ((packaging != null) && (!packaging.equals(""))) {
      deposit.setFormatNamespace(packaging);
    } else {
      throw new SWORDContentTypeException();
    }

    // Check Content-deposition header
    final String filename = request.getHeader("Content-Disposition:filename");
    if ((filename != null) && (filename.equals(""))) {
      deposit.setContentDisposition(filename);
    }

    // Check ContentType header
    final String contentType = request.getHeader("Content-Type");
    if ((contentType != null) && (contentType.equals(""))) {
      deposit.setContentType(contentType);
    }

    return deposit;
  }
}
