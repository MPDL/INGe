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
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft fr wissenschaftlich-technische
 * Information mbH and Max-Planck- Gesellschaft zur Fderung der Wissenschaft e.V. All rights
 * reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.util;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class handle URIs in XSLT stylesheets such as xsl:import. In a jar the stylesheet can only
 * be loaded as InputStream. Without this URIResolver it is not possible to work with import
 * statements.
 *
 * @author mfranke
 * @author $Author$
 * @version $Revision$$LastChangedDate$
 */
public class LocalUriResolver implements URIResolver {
  private static final Logger logger = LogManager.getLogger(LocalUriResolver.class);

  private static final String TRANS_PATH = "transformations/";

  private String base = "";

  public LocalUriResolver() {}

  /**
   * Field-based constructor.
   *
   * @param base The base URI.
   */
  public LocalUriResolver(String base) {
    this.base = base;
  }

  @Override
  public final Source resolve(String href, String altBase) throws TransformerException {

    logger.debug("Trying to resolve <" + href + "> with base <" + altBase + ">");
    String path = null;

    if (null == altBase) {
      altBase = "";
    }

    try {

      if ("ves-mapping.xml".equals(href) || "vocabulary-mappings.xsl".equals(href)) {
        path = TRANS_PATH + href;
      } else if (null != href && href.matches("^https?://.*")) {
        HttpClient client = new HttpClient();
        GetMethod getMethod = new GetMethod(href);
        //        ProxyHelper.executeMethod(client, getMethod);
        client.executeMethod(getMethod);
        return new StreamSource(getMethod.getResponseBodyAsStream());
      } else {
        path = this.base + altBase + "/" + href;
      }

      return new StreamSource(ResourceUtil.getResourceAsStream(path, LocalUriResolver.class.getClassLoader()));
    } catch (FileNotFoundException e) {
      // throw new TransformerException("Cannot resolve URI: " + href);
      throw new TransformerException("Cannot resolve URI: " + path, e);
    } catch (HttpException e) {
      throw new TransformerException("Cannot connect to URI: " + path, e);
    } catch (IOException e) {
      throw new TransformerException("Cannot get content from URI: " + path, e);
    }
  }
}
