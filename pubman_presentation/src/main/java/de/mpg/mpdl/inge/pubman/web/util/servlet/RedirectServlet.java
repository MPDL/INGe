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

package de.mpg.mpdl.inge.pubman.web.util.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import de.mpg.mpdl.inge.pubman.web.util.ServletTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.LoginHelper;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ProxyHelper;

/**
 * A servlet for retrieving and redirecting the content objects urls. /pubman/item/escidoc:12345 for
 * items and /pubman/item/escidoc:12345/component/escidoc:23456/name.txt for components.
 * 
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@SuppressWarnings("serial")
public class RedirectServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(RedirectServlet.class);

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {
    final String id = req.getPathInfo().substring(1);
    final boolean download = ("download".equals(req.getParameter("mode")));
    final boolean tme = ("tme".equals(req.getParameter("mode")));

    final String userHandle = req.getParameter(LoginHelper.PARAMETERNAME_USERHANDLE);


    // no component -> ViewItemOverviewPage
    if (!id.contains("/component/")) {
      final StringBuffer redirectUrl = new StringBuffer();
      final LoginHelper loginHelper = (LoginHelper) ServletTools.findSessionBean(req, "LoginHelper");
      if (loginHelper != null && loginHelper.isDetailedMode()) {
        redirectUrl.append("/pubman/faces/ViewItemFullPage.jsp?itemId=" + id);
      } else {
        redirectUrl.append("/pubman/faces/ViewItemOverviewPage.jsp?itemId=" + id);
      }
      if (userHandle != null) {
        redirectUrl.append("&" + LoginHelper.PARAMETERNAME_USERHANDLE + "=" + userHandle);
      }
      resp.sendRedirect(redirectUrl.toString());
      return;
    }

    // is component
    if (id.contains("/component/")) {
      final String[] pieces = id.split("/");
      if (pieces.length != 4) {
        resp.sendError(404, "File not found");
      }

      // open component or download it
      if (req.getParameter("mode") == null || download) {
        try {
          final InputStream input = this.getContentAsInputStream(req, resp, download, pieces);

          if (input == null) {
            return;
          }

          final byte[] buffer = new byte[2048];
          int numRead;
          // long numWritten = 0;
          final OutputStream out = resp.getOutputStream();
          while ((numRead = input.read(buffer)) != -1) {
            RedirectServlet.logger.debug(numRead + " bytes read.");
            out.write(buffer, 0, numRead);
            resp.flushBuffer();
            // numWritten += numRead;

          }

          input.close();
          out.close();
        } catch (final URISyntaxException e) {
          throw new ServletException(e);
        }
      }
      // view technical metadata
      if (tme) {
        final OutputStream out = resp.getOutputStream();
        resp.setCharacterEncoding("UTF-8");

        try {

          final InputStream input = this.getContentAsInputStream(req, resp, false, pieces);
          if (input == null) {
            return;
          }
          String b = new String();

          try {
            b = this.getTechnicalMetadataByTika(input);
          } catch (final TikaException e) {
            RedirectServlet.logger.warn("TikaException when parsing " + pieces[3], e);
          } catch (final SAXException e) {
            RedirectServlet.logger.warn("SAXException when parsing " + pieces[3], e);
          }

          resp.setHeader("Content-Type", "text/plain; charset=UTF-8");

          out.write(b.toString().getBytes());
          return;

        } catch (final Exception e) {
          throw new ServletException(e);
        }
      }
    }
  }

  private InputStream getContentAsInputStream(HttpServletRequest req, HttpServletResponse resp,
      boolean download, String[] pieces) throws IOException, URISyntaxException, ServletException,
      HttpException, UnsupportedEncodingException {
    final String frameworkUrl = PropertyReader.getProperty("escidoc.framework_access.login.url");
    String url = null;
    try {
      url =
          frameworkUrl + "/ir/item/" + pieces[0] + "/components/component/" + pieces[2]
              + "/content";
      RedirectServlet.logger.debug("Calling " + url);
    } catch (final Exception e) {
      throw new ServletException("Error getting framework url", e);
    }

    final GetMethod method = new GetMethod(url);
    method.setFollowRedirects(false);
    final LoginHelper loginHelper = (LoginHelper) ServletTools.findSessionBean(req, "LoginHelper");
    if (loginHelper != null && loginHelper.getESciDocUserHandle() != null) {
      method.addRequestHeader("Cookie", "escidocCookie=" + loginHelper.getESciDocUserHandle());
    }
    // Execute the method with HttpClient.
    final HttpClient client = new HttpClient();
    ProxyHelper.setProxy(client, frameworkUrl);
    ProxyHelper.executeMethod(client, method);
    RedirectServlet.logger.debug("...executed");
    InputStream input;

    if (method.getStatusCode() == 302) {
      String servletUrl =
          PropertyReader.getProperty("escidoc.pubman.instance.url")
              + PropertyReader.getProperty("escidoc.pubman.instance.context.path")
              + PropertyReader.getProperty("escidoc.pubman.item.pattern");
      servletUrl = servletUrl.replace("$1", "");

      final String loginUrl = frameworkUrl + "/aa/login?target=" + URLEncoder.encode(url, "ASCII");
      resp.sendRedirect(loginUrl);
      return null;
    }

    if (method.getStatusCode() != 200) {
      throw new RuntimeException("error code " + method.getStatusCode());
    }

    for (final Header header : method.getResponseHeaders()) {
      if (!"Transfer-Encoding".equals(header.getName())) {
        RedirectServlet.logger.debug("Setting header: " + header.getName() + ": " + header.getValue());
        resp.setHeader(header.getName(), header.getValue());
      } else {
        RedirectServlet.logger.info("Ignoring " + header.getName() + ": " + header.getValue());
      }
    }
    if (download) {
      resp.setHeader("Content-Disposition", "attachment");
    }
    input = method.getResponseBodyAsStream();

    return input;
  }

  private String getTechnicalMetadataByTika(InputStream input) throws IOException, SAXException,
      TikaException {
    final StringBuffer b = new StringBuffer(2048);
    final Metadata metadata = new Metadata();
    final AutoDetectParser parser = new AutoDetectParser();
    final BodyContentHandler handler = new BodyContentHandler(-1);

    parser.parse(input, handler, metadata);

    for (final String name : metadata.names()) {
      b.append(name).append(": ").append(metadata.get(name))
          .append(System.getProperty("line.separator"));
    }
    return b.toString();
  }



  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {
    // No post action
    return;
  }
}
