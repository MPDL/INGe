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

package de.mpg.mpdl.inge.cone.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.aa.TanStore;
import de.mpg.mpdl.inge.cone.ConeException;
import de.mpg.mpdl.inge.cone.Describable;
import de.mpg.mpdl.inge.cone.ModelList;
import de.mpg.mpdl.inge.cone.ModelList.Model;
import de.mpg.mpdl.inge.cone.Pair;
import de.mpg.mpdl.inge.cone.Querier;
import de.mpg.mpdl.inge.cone.QuerierFactory;
import de.mpg.mpdl.inge.cone.TreeFragment;
import de.mpg.mpdl.inge.cone.formatter.AbstractFormatter;
import de.mpg.mpdl.inge.cone.util.Rdfs;
import de.mpg.mpdl.inge.util.ConeUtils;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ResourceUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet to answer calls from various calls.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
@SuppressWarnings("serial")
public class ConeServlet extends HttpServlet {

  private static final Logger logger = Logger.getLogger(ConeServlet.class);
  private static final String DB_ERROR_MESSAGE = "Error querying database.";
  private static final String DEFAULT_ENCODING = "UTF-8";
  private static final String DEFAULT_FORMAT = "html";

  private static final Set<String> RESERVED_PARAMETERS = new HashSet<>() {
    {
      this.add("f");
      this.add("format");
      this.add("m");
      this.add("mode");
      this.add("n");
      this.add("number");
      this.add("l");
      this.add("lang");
      this.add("q");
      this.add("query");
      this.add("tan4directLogin");
    }
  };

  /**
   *
   *
   * @throws IOException
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    StringBuilder sb = new StringBuilder();
    sb.append("CoNE request: ");
    sb.append(request);
    sb.append(" Params: ");
    for (Object key : request.getParameterMap().keySet()) {
      sb.append(key.toString());
      sb.append("=");
      sb.append(request.getParameter(key.toString()));
      sb.append(" / ");
    }
    logger.info(sb.toString());

    request.setCharacterEncoding(DEFAULT_ENCODING);
    response.setCharacterEncoding(DEFAULT_ENCODING);

    // LoggedIn
    boolean loggedIn = false;
    if (request.getSession().getAttribute("logged_in") == null || !(Boolean) request.getSession().getAttribute("logged_in")) {
      Login.checkLogin(request, false);
      loggedIn = getLoggedIn(request);
    } else {
      loggedIn = (Boolean) request.getSession().getAttribute("logged_in");
    }

    // CONE Zugriff im LoggedIn Modus (obwohl nicht eingelogged)
    if (!loggedIn) {
      String tan = request.getParameter("tan4directLogin");
      if (tan != null && TanStore.checkTan(tan)) {
        loggedIn = true;
      }
    }

    // Action, model
    String action = null;
    String modelName = null;
    String[] path = request.getServletPath().split("/", 4);
    if (path.length == 3 && "".equals(path[2])) {
      action = path[1];
    } else if (path.length > 2) {
      modelName = path[1];
      if (path.length >= 3) {
        action = path[2];
      }
    }

    // Format
    String format = DEFAULT_FORMAT;
    if (request.getParameter("format") != null || request.getParameter("f") != null) {
      format = (request.getParameter("format") != null ? request.getParameter("format") : request.getParameter("f"));
    } else {
      ModelList modelList;
      try {
        modelList = ModelList.getInstance();
      } catch (Exception e) {
        throw new ServletException(e);
      }
      boolean found = false;
      String acceptHeader = request.getHeader("Accept");
      if (acceptHeader != null) {
        String[] types = acceptHeader.split(",");
        for (String type : types) {
          for (String key : modelList.getFormatMimetypes().keySet()) {
            Set<String> formatTypes = modelList.getFormatMimetypes().get(key);
            if (formatTypes.contains(type)) {
              format = key;
              found = true;
              break;
            }
          }
        }
      }
      if (!found) {
        format = DEFAULT_FORMAT;
      }
    }

    // Formatter
    AbstractFormatter formatter = null;
    try {
      formatter = AbstractFormatter.getFormatter(format);
    } catch (ConeException e) {
      throw new ServletException(e);
    }

    // Mode
    Querier.ModeType modeType;
    if (request.getParameter("mode") != null && "full".equalsIgnoreCase(request.getParameter("mode"))
        || request.getParameter("m") != null && "full".equalsIgnoreCase(request.getParameter("m"))) {
      modeType = Querier.ModeType.FULL;
    } else {
      modeType = Querier.ModeType.FAST;
    }

    // Lang
    String language;
    if (request.getParameter("language") != null) {
      language = request.getParameter("language");
    } else if (request.getParameter("l") != null) {
      language = request.getParameter("l");
    } else {
      language = PropertyReader.getProperty(PropertyReader.INGE_CONE_LANGUAGE_DEFAULT);
    }

    // Query
    PrintWriter out = response.getWriter();
    if ("query".equals(action)) {
      String queryString;
      queryString =
          UrlHelper.fixURLEncoding(request.getParameter("query") != null ? request.getParameter("query") : request.getParameter("q"));

      // Limit
      int limit;
      try {
        if (request.getParameter("number") != null) {
          limit = Integer.parseInt(request.getParameter("number"));
        } else if (request.getParameter("n") != null) {
          limit = Integer.parseInt(request.getParameter("n"));
        } else {
          limit = Integer.parseInt(PropertyReader.getProperty(PropertyReader.INGE_CONE_RESULTS_DEFAULT));
        }
      } catch (Exception e) {
        throw new ServletException(e);
      }

      try {
        if (queryString != null) {
          queryAction(queryString, limit, language, modeType, response, formatter, modelName, loggedIn);
        } else {
          ArrayList<Pair<String>> searchFields = new ArrayList<>();
          for (Object key : request.getParameterMap().keySet()) {
            if (!RESERVED_PARAMETERS.contains(key)) {
              searchFields.add(new Pair<>(key.toString(), UrlHelper.fixURLEncoding(request.getParameter(key.toString()))));
            }
          }
          queryFieldsAction(searchFields.toArray(new Pair[] {}), limit, language, modeType, response, formatter, modelName, loggedIn);
        }
      } catch (Exception e) {
        throw new ServletException(e);
      }

    } else if ("all".equals(action)) {
      try {
        allAction(language, modeType, response, formatter, modelName, loggedIn);
      } catch (Exception e) {
        throw new ServletException(e);
      }

    } else if ("resource".equals(action)) {
      String id = null;
      if (path.length >= 4) {
        id = ConeUtils.makeConePersonsLinkRelative(path);
      }
      try {
        detailAction(id, language, response, formatter, out, modelName, loggedIn);
      } catch (Exception e) {
        throw new ServletException(e);
      }

    } else if ("explain".equals(action)) {
      response.setContentType("text/xml");
      try {
        out.print(ResourceUtil.getResourceAsString(PropertyReader.getProperty(PropertyReader.INGE_CONE_MODELSXML_PATH),
            ConeServlet.class.getClassLoader()));
      } catch (Exception e) {
        throw new ServletException(e);
      }

    } else if ("rdfs".equals(action)) {
      response.setContentType("text/xml");
      try {
        out.print(Rdfs.getModelAsRdfs(null));
      } catch (Exception e) {
        throw new ServletException(e);
      }
    }

    response.setHeader("Connection", "close");
  }

  private void allAction(String language, Querier.ModeType modeType, HttpServletResponse response, AbstractFormatter formatter,
      String modelName, boolean loggedIn) throws Exception {
    Model model = ModelList.getInstance().getModelByAlias(modelName);
    response.setContentType(formatter.getContentType());
    Querier querier = QuerierFactory.newQuerier(loggedIn);

    if (querier == null) {
      reportMissingQuerier(response);
    } else {
      List<? extends Describable> result = null;

      try {
        result = querier.query(model.getName(), "*", language, modeType,
            Integer.parseInt(PropertyReader.getProperty(PropertyReader.INGE_CONE_RESULTS_MAXIMUM)));
      } catch (Exception e) {
        logger.error(DB_ERROR_MESSAGE, e);
      }

      response.getWriter().print(formatter.formatQuery(result, model));
    }

    querier.release();
  }

  private void detailAction(String id, String language, HttpServletResponse response, AbstractFormatter formatter, PrintWriter out,
      String modelName, boolean loggedIn) throws Exception {
    Model model = ModelList.getInstance().getModelByAlias(modelName);
    response.setContentType(formatter.getContentType());

    if (id == null) {
      reportMissingParameter("id", response);
    } else {
      Querier querier = QuerierFactory.newQuerier(loggedIn);
      if (querier == null) {
        reportMissingQuerier(response);
      } else {
        TreeFragment result = null;
        try {
          result = querier.details(modelName, id, language);
        } catch (Exception e) {
          logger.error(DB_ERROR_MESSAGE, e);
        }
        out.print(formatter.formatDetails(id, model, result, language));
      }
      querier.release();
    }
  }

  private void queryAction(String queryString, int limit, String language, Querier.ModeType modeType, HttpServletResponse response,
      AbstractFormatter formatter, String modelName, boolean loggedIn) throws ConeException {
    Model model = ModelList.getInstance().getModelByAlias(modelName);

    try {
      response.setContentType(formatter.getContentType());

      if (queryString == null) {
        reportMissingParameter("q", response);
      } else {
        Querier querier = QuerierFactory.newQuerier(loggedIn);

        if (querier == null) {
          reportMissingQuerier(response);
        } else {
          List<? extends Describable> result = null;

          try {
            result = querier.query(model.getName(), queryString, language, modeType, limit);
          } catch (Exception e) {
            logger.error(DB_ERROR_MESSAGE, e);
          }

          response.getWriter().print(formatter.formatQuery(result, model));
        }
        if (querier != null) {
          querier.release();
        }
      }
    } catch (IOException e) {
      throw new ConeException(e);
    }
  }

  private void queryFieldsAction(Pair[] searchFields, int limit, String language, Querier.ModeType modeType, HttpServletResponse response,
      AbstractFormatter formatter, String modelName, boolean loggedIn) throws ConeException {
    Model model = ModelList.getInstance().getModelByAlias(modelName);

    try {
      response.setContentType(formatter.getContentType());

      Querier querier = QuerierFactory.newQuerier(loggedIn);

      if (querier == null) {
        reportMissingQuerier(response);
      } else {
        List<? extends Describable> result = null;

        try {
          result = querier.query(model.getName(), searchFields, language, modeType, limit);
        } catch (Exception e) {
          logger.error(DB_ERROR_MESSAGE, e);
        }

        response.getWriter().print(formatter.formatQuery(result, model));
      }
      querier.release();
    } catch (IOException e) {
      throw new ConeException(e);
    }
  }

  private void reportMissingQuerier(HttpServletResponse response) throws IOException {
    response.setStatus(500);
    response.getWriter().println("Error: Querier implementation not set in propertyfile.");
  }

  private void reportMissingParameter(String param, HttpServletResponse response) throws IOException {
    response.setStatus(500);
    response.getWriter().println("Error: Parameter '" + param + "' is missing.");
  }

  private boolean getLoggedIn(HttpServletRequest request) {
    return (request.getSession().getAttribute("logged_in") != null && (Boolean) request.getSession().getAttribute("logged_in"));
  }
}
