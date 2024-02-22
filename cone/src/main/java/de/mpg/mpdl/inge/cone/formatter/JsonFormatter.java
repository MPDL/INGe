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

package de.mpg.mpdl.inge.cone.formatter;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.inge.cone.ConeException;
import de.mpg.mpdl.inge.cone.Describable;
import de.mpg.mpdl.inge.cone.LocalizedString;
import de.mpg.mpdl.inge.cone.ModelList;
import de.mpg.mpdl.inge.cone.Pair;
import de.mpg.mpdl.inge.cone.ResultEntry;
import de.mpg.mpdl.inge.cone.TreeFragment;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ResourceUtil;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet to answer calls from the JQuery Javascript API.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class JsonFormatter extends AbstractFormatter {

  private static final Logger logger = LogManager.getLogger(JsonFormatter.class);
  private static final String ERROR_TRANSFORMING_RESULT = "Error transforming result";
  private static final String DEFAULT_ENCODING = "UTF-8";

  @Override
  public String getContentType() {
    return "application/json;charset=" + DEFAULT_ENCODING;
  }

  /**
   * Send explain output to client.
   *
   * @param response
   *
   * @throws FileNotFoundException
   * @throws TransformerFactoryConfigurationError
   * @throws IOException
   */
  public void explain(HttpServletResponse response) throws FileNotFoundException, TransformerFactoryConfigurationError, IOException {
    response.setContentType("text/xml");

    InputStream source = ResourceUtil.getResourceAsStream(PropertyReader.getProperty(PropertyReader.INGE_CONE_MODELSXML_PATH),
        JsonFormatter.class.getClassLoader());
    InputStream template = ResourceUtil.getResourceAsStream("explain/json_explain.xsl", JsonFormatter.class.getClassLoader());

    try {
      Transformer transformer =
          TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null).newTransformer(new StreamSource(template));
      transformer.setOutputProperty(OutputKeys.ENCODING, DEFAULT_ENCODING);
      transformer.transform(new StreamSource(source), new StreamResult(response.getWriter()));
    } catch (Exception e) {
      logger.error(ERROR_TRANSFORMING_RESULT, e);
      throw new IOException(e.getMessage());
    }
  }

  public OutputStream format(String source) throws IOException {

    InputStream template = ResourceUtil.getResourceAsStream("xslt/rdf2jquery.xsl", JsonFormatter.class.getClassLoader());
    OutputStream result = new ByteArrayOutputStream();

    try {
      Transformer transformer =
          TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null).newTransformer(new StreamSource(template));
      transformer.transform(new StreamSource(new StringReader(source)), new StreamResult(result));
    } catch (Exception e) {
      logger.error(ERROR_TRANSFORMING_RESULT, e);
      throw new IOException(e.getMessage());
    }
    return result;
  }

  /**
   * Formats RDF descriptions into a JSON list.
   *
   * @param pairs The RDF.
   * @return A String formatted in JSON format.
   */
  public String formatQuery(List<? extends Describable> pairs, ModelList.Model model) throws ConeException {

    StringWriter result = new StringWriter();

    result.append("[\n");

    if (null != pairs) {
      for (Describable pair : pairs) {
        if (pair instanceof Pair) {
          result.append("\t{\n");
          String key = ((Pair) pair).getKey();
          Object value = ((Pair) pair).getValue();

          result.append("\t\t\"id\" : \"");
          try {
            result.append(PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + key.replace("\"", "\\\""));
          } catch (Exception e) {
            throw new ConeException(e);
          }
          result.append("\",\n");

          if (value instanceof LocalizedString && null != ((LocalizedString) value).getLanguage()) {
            result.append("\t\t\"language\" : \"");
            result.append(((LocalizedString) value).getLanguage().replace("\"", "\\\""));
            result.append("\",\n");
          }

          if (value instanceof ResultEntry && null != ((ResultEntry) value).getType()) {
            result.append("\t\t\"type\" : \"");
            result.append(((ResultEntry) value).getType().replace("\"", "\\\""));
            result.append("\",\n");
          }

          result.append("\t\t\"value\" : \"");
          result.append(value.toString().replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t"));
          result.append("\"\n");

          result.append("\t}");

        } else if (pair instanceof TreeFragment) {
          result.append(((TreeFragment) pair).toJson());
        }

        if (pair instanceof ResultEntry) {
          result.append("\t{\n");

          String key = ((Pair) pair).getKey();
          Object value = ((Pair) pair).getValue();

          result.append("\t\t\"id\" : \"");
          try {
            result.append(PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + key.replace("\"", "\\\""));
          } catch (Exception e) {
            throw new ConeException(e);
          }
          result.append("\",\n");

          if (value instanceof LocalizedString && null != ((LocalizedString) value).getLanguage()) {
            result.append("\t\t\"language\" : \"");
            result.append(((LocalizedString) value).getLanguage().replace("\"", "\\\""));
            result.append("\",\n");
          }

          result.append("\t\t\"value\" : \"");
          result.append(value.toString().replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t"));
          result.append("\"\n");

          result.append("\t}");

        }



        if (pair != pairs.get(pairs.size() - 1)) {
          result.append(",");
        }
        result.append("\n");
      }
    }

    result.append("]\n");

    return result.toString();
  }

  public String formatDetails(String id, ModelList.Model model, TreeFragment triples, String lang) {
    return triples.toJson();
  }

}
