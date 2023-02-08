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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.cone.ConeException;
import de.mpg.mpdl.inge.cone.Describable;
import de.mpg.mpdl.inge.cone.ModelList.Model;
import de.mpg.mpdl.inge.cone.Pair;
import de.mpg.mpdl.inge.cone.TreeFragment;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ResourceUtil;

/**
 * Servlet to answer calls from PubMan for options generation.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class OptionsFormatter extends AbstractFormatter {

  private static final Logger logger = Logger.getLogger(OptionsFormatter.class);
  private static final String ERROR_TRANSFORMING_RESULT = "Error transforming result";
  private static final String DEFAULT_ENCODING = "UTF-8";

  @Override
  public String getContentType() {
    return "text/plain;charset=" + DEFAULT_ENCODING;
  }

  /**
   * Send explain output to client.
   * 
   * @param response
   * 
   * @throws FileNotFoundException
   * @throws TransformerFactoryConfigurationError
   * @throws IOException
   * @throws URISyntaxException
   */
  public void explain(HttpServletResponse response)
      throws FileNotFoundException, TransformerFactoryConfigurationError, IOException, URISyntaxException {
    response.setContentType("text/xml");

    InputStream source = ResourceUtil.getResourceAsStream(PropertyReader.getProperty(PropertyReader.INGE_CONE_MODELSXML_PATH),
        OptionsFormatter.class.getClassLoader());
    InputStream template = ResourceUtil.getResourceAsStream("explain/options_explain.xsl", OptionsFormatter.class.getClassLoader());

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

  /**
   * Formats an Map&lt;String, String> into a simple |-separated list.
   * 
   * @param pairs The list.
   * @return A String formatted in a JQuery readable format.
   */
  public String formatQuery(List<? extends Describable> pairs, Model model) throws ConeException {

    StringWriter result = new StringWriter();

    if (pairs != null) {
      for (Describable pair : pairs) {
        if (pair instanceof Pair) {
          String key = ((Pair) pair).getKey();
          Object value = ((Pair) pair).getValue();
          try {
            result.append(key.substring(key.lastIndexOf("/") + 1));
          } catch (Exception e) {
            throw new ConeException(e);
          }
          result.append("|");
          result.append(value.toString());
        } else if (pair instanceof TreeFragment) {
          result.append(((TreeFragment) pair).toJson());
        }
        result.append("\n");
      }
    }

    return result.toString();
  }

  /**
   * Formats an TreeFragment into a JSON object.
   * 
   * @param result The JSON.
   * @return A String formatted in a JQuery readable format.
   */
  public String formatDetails(String id, Model model, TreeFragment triples, String lang) throws ConeException {
    return triples.toJson();
  }

}
