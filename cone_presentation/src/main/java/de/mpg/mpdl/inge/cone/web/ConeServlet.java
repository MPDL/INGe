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

import de.mpg.mpdl.inge.aa.TanStore;
import de.mpg.mpdl.inge.cone.*;
import de.mpg.mpdl.inge.cone.formatter.AbstractFormatter;
import de.mpg.mpdl.inge.cone.util.Rdfs;
import de.mpg.mpdl.inge.util.ConeUtils;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ResourceUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

  private static final Logger logger = LogManager.getLogger(ConeServlet.class);
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

  public void init() {

  }

  /**
   *
   *
   * @throws IOException
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    StringBuilder sb = new StringBuilder();
    sb.append("CoNE request: ");
    sb.append(req);
    sb.append(" Params: ");
    for (Object key : req.getParameterMap().keySet()) {
      sb.append(key.toString());
      sb.append("=");
      sb.append(req.getParameter(key.toString()));
      sb.append(" / ");
    }
    logger.info(sb.toString());

    req.setCharacterEncoding(DEFAULT_ENCODING);
    resp.setCharacterEncoding(DEFAULT_ENCODING);

    // LoggedIn
    boolean loggedIn = false;
    if (null == req.getSession().getAttribute("logged_in") || !(Boolean) req.getSession().getAttribute("logged_in")) {
      Login.checkLogin(req, false);
      loggedIn = getLoggedIn(req);
    } else {
      loggedIn = (Boolean) req.getSession().getAttribute("logged_in");
    }

    // CONE Zugriff im LoggedIn Modus (obwohl nicht eingelogged)
    if (!loggedIn) {
      String tan = req.getParameter("tan4directLogin");
      if (null != tan && TanStore.checkTan(tan)) {
        loggedIn = true;
      }
    }

    // Action, model
    String action = null;
    String modelName = null;
    String[] path = req.getServletPath().split("/", 4);
    if (3 == path.length && "".equals(path[2])) {
      action = path[1];
    } else if (2 < path.length) {
      modelName = path[1];
      if (3 <= path.length) {
        action = path[2];
      }
    }

    // Format
    String format = DEFAULT_FORMAT;
    if (null != req.getParameter("format") || null != req.getParameter("f")) {
      format = (null != req.getParameter("format") ? req.getParameter("format") : req.getParameter("f"));
    } else {
      ModelList modelList;
      try {
        modelList = ModelList.getInstance();
      } catch (Exception e) {
        throw new ServletException(e);
      }
      boolean found = false;
      String acceptHeader = req.getHeader("Accept");
      if (null != acceptHeader) {
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
    if (null != req.getParameter("mode") && "full".equalsIgnoreCase(req.getParameter("mode"))
        || null != req.getParameter("m") && "full".equalsIgnoreCase(req.getParameter("m"))) {
      modeType = Querier.ModeType.FULL;
    } else {
      modeType = Querier.ModeType.FAST;
    }

    // Lang
    String language;
    if (null != req.getParameter("language")) {
      language = req.getParameter("language");
    } else if (null != req.getParameter("l")) {
      language = req.getParameter("l");
    } else {
      language = PropertyReader.getProperty(PropertyReader.INGE_CONE_LANGUAGE_DEFAULT);
    }

    // Query
    PrintWriter out = resp.getWriter();
    if ("query".equals(action)) {
      String queryString;
      queryString = UrlHelper.fixURLEncoding(null != req.getParameter("query") ? req.getParameter("query") : req.getParameter("q"));

      // Limit
      int limit;
      try {
        if (null != req.getParameter("number")) {
          limit = Integer.parseInt(req.getParameter("number"));
        } else if (null != req.getParameter("n")) {
          limit = Integer.parseInt(req.getParameter("n"));
        } else {
          limit = Integer.parseInt(PropertyReader.getProperty(PropertyReader.INGE_CONE_RESULTS_DEFAULT));
        }
      } catch (Exception e) {
        throw new ServletException(e);
      }

      try {
        if (null != queryString) {
          queryAction(queryString, limit, language, modeType, resp, formatter, modelName, loggedIn);
        } else {
          ArrayList<Pair<String>> searchFields = new ArrayList<>();
          for (Object key : req.getParameterMap().keySet()) {
            if (!RESERVED_PARAMETERS.contains(key)) {
              searchFields.add(new Pair<>(key.toString(), UrlHelper.fixURLEncoding(req.getParameter(key.toString()))));
            }
          }
          queryFieldsAction(searchFields.toArray(new Pair[] {}), limit, language, modeType, resp, formatter, modelName, loggedIn);
        }
      } catch (Exception e) {
        throw new ServletException(e);
      }

    } else if ("all".equals(action)) {
      try {
        allAction(language, modeType, resp, formatter, modelName, loggedIn);
      } catch (Exception e) {
        throw new ServletException(e);
      }

    } else if ("resource".equals(action)) {
      String id = null;
      if (4 <= path.length) {
        id = ConeUtils.makeConePersonsLinkRelative(path);
      }
      try {
        detailAction(id, language, resp, formatter, out, modelName, loggedIn);
      } catch (Exception e) {
        throw new ServletException(e);
      }

    } else if ("explain".equals(action)) {
      resp.setContentType("text/xml");
      try {
        out.print(ResourceUtil.getResourceAsString(PropertyReader.getProperty(PropertyReader.INGE_CONE_MODELSXML_PATH),
            ConeServlet.class.getClassLoader()));
      } catch (Exception e) {
        throw new ServletException(e);
      }

    } else if ("rdfs".equals(action)) {
      resp.setContentType("text/xml");
      try {
        out.print(Rdfs.getModelAsRdfs(null));
      } catch (Exception e) {
        throw new ServletException(e);
      }
    } else if ("search-index".equals(action)) {
      try {
        resp.setContentType("application/json");
        SearchEngineIndexer se = SearchIndexerFactory.createSearchEngineIndexer();
        String query = req.getParameter("q");
        String fromParam = req.getParameter("from");
        int from = 0;
        if (fromParam != null) {
          from = Integer.parseInt(fromParam);
        }
        String sizeParam = req.getParameter("size");
        int size = 10;
        if (sizeParam != null) {
          size = Integer.parseInt(sizeParam);
        }
        String res = se.simpleSearch(modelName, query, from, size);
        resp.getWriter().print(res);
        resp.getWriter().flush();

      } catch (Exception e) {
        throw new ServletException(e);
      }
    }

    resp.setHeader("Connection", "close");
  }

  private void allAction(String language, Querier.ModeType modeType, HttpServletResponse response, AbstractFormatter formatter,
      String modelName, boolean loggedIn) throws Exception {
    ModelList.Model model = ModelList.getInstance().getModelByAlias(modelName);
    response.setContentType(formatter.getContentType());
    Querier querier = QuerierFactory.newQuerier(loggedIn);

    if (null == querier) {
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
    ModelList.Model model = ModelList.getInstance().getModelByAlias(modelName);
    response.setContentType(formatter.getContentType());

    if (null == id) {
      reportMissingParameter("id", response);
    } else {
      Querier querier = QuerierFactory.newQuerier(loggedIn);
      if (null == querier) {
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
    ModelList.Model model = ModelList.getInstance().getModelByAlias(modelName);

    try {
      response.setContentType(formatter.getContentType());

      if (null == queryString) {
        reportMissingParameter("q", response);
      } else {
        Querier querier = QuerierFactory.newQuerier(loggedIn);

        if (null == querier) {
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
        if (null != querier) {
          querier.release();
        }
      }
    } catch (IOException e) {
      throw new ConeException(e);
    }
  }

  private void queryFieldsAction(Pair[] searchFields, int limit, String language, Querier.ModeType modeType, HttpServletResponse response,
      AbstractFormatter formatter, String modelName, boolean loggedIn) throws ConeException {
    ModelList.Model model = ModelList.getInstance().getModelByAlias(modelName);

    try {
      response.setContentType(formatter.getContentType());

      Querier querier = QuerierFactory.newQuerier(loggedIn);

      if (null == querier) {
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
    return (null != request.getSession().getAttribute("logged_in") && (Boolean) request.getSession().getAttribute("logged_in"));
  }
}
