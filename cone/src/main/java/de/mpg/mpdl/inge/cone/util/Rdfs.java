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

package de.mpg.mpdl.inge.cone.util;

import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ResourceUtil;
import net.sf.saxon.TransformerFactoryImpl;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class Rdfs {

  /**
   * Create an RDFS out of a model definition from the models.xml.
   *
   * @param modelname The name of the model
   * @return An output stream holding the RDFS of the model
   */
  public static Writer getModelAsRdfs(String modelname) throws Exception {
    Source source = new StreamSource(
        ResourceUtil.getResourceAsStream(PropertyReader.getProperty(PropertyReader.INGE_CONE_RDFS_TEMPLATE), Rdfs.class.getClassLoader()));
    Transformer transformer = new TransformerFactoryImpl().newTransformer(source);
    if (modelname != null) {
      transformer.setParameter("model", modelname);
    }
    Writer writer = new StringWriter();
    Result result = new StreamResult(writer);
    transformer.transform(new StreamSource(
        ResourceUtil.getResourceAsStream(PropertyReader.getProperty(PropertyReader.INGE_CONE_MODELSXML_PATH), Rdfs.class.getClassLoader())),
        result);
    return writer;
  }

  /**
   * @param args
   */
  public static void main(String[] args) throws Exception {

    Writer writer = getModelAsRdfs(args.length == 0 ? null : args[0]);
    System.out.println(writer);
  }
}
