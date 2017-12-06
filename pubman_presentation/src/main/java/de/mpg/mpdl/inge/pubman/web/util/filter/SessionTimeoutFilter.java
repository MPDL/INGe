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

package de.mpg.mpdl.inge.pubman.web.util.filter;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * TODO Description
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class SessionTimeoutFilter implements Filter {
  public static final String LOGOUT_URL = "/aa/logout/clear.jsp";

  @Override
  public void destroy() {}

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
    if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
      final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
      final HttpServletResponse httpServletResponse = (HttpServletResponse) response;

      try {
        final String homePage =
            PropertyReader.getProperty("inge.pubman.instance.url")
                + PropertyReader.getProperty("inge.pubman.instance.context.path");
        // define some exceptions (pages that don't require a logged in user)
        if (!"/ViewItemFullPage.jsp".equals(httpServletRequest.getPathInfo())
            && !"/HomePage.jsp".equals(httpServletRequest.getPathInfo())
            && !"/AffiliationTreePage.jsp".equals(httpServletRequest.getPathInfo())
            && !"/AdvancedSearchPage.jsp".equals(httpServletRequest.getPathInfo())
            && !"/SearchResultListPage.jsp".equals(httpServletRequest.getPathInfo())
            && !"/ViewItemReleaseHistoryPage.jsp".equals(httpServletRequest.getPathInfo())
            && !"/ViewItemRevisionsPage.jsp".equals(httpServletRequest.getPathInfo())
            && !"/ViewItemStatisticsPage.jsp".equals(httpServletRequest.getPathInfo())
            && !"/CartItemsPage.jsp".equals(httpServletRequest.getPathInfo())
            && !"/AffiliationDetailPage.jsp".equals(httpServletRequest.getPathInfo())
            && !"/ExportEmailPage.jsp".equals(httpServletRequest.getPathInfo())
            && !"/OrganizationSuggest.jsp".equals(httpServletRequest.getPathInfo())
            && !"/ViewItemOverviewPage.jsp".equals(httpServletRequest.getPathInfo())
            && httpServletRequest.getRequestedSessionId() != null
            && httpServletRequest.getParameter("expired") == null
            && httpServletRequest.getParameter("logout") == null
            && !httpServletRequest.isRequestedSessionIdValid()) {
          // Deactivated because of import tool.
          httpServletResponse.sendRedirect(PropertyReader.getLoginUrl()
              + SessionTimeoutFilter.LOGOUT_URL + "?target="
              + URLEncoder.encode(homePage + "?expired=true", "UTF-8"));
          // httpServletResponse.sendRedirect(homePage + "?expired=true");
          return;
        }
      } catch (final Exception e) {
        throw new ServletException("Error logging out", e);
      }
    }

    filterChain.doFilter(request, response);
  }

  @Override
  public void init(FilterConfig arg0) throws ServletException {}
}
