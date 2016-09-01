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

package de.mpg.mpdl.inge.xmltransforming.xmltransforming;

import java.io.StringWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

import de.mpg.mpdl.inge.xmltransforming.TestBase;


/**
 * This class enriches the TestBase class with XML-specific methods.
 * 
 * @author Johannes Mueller (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$ @revised by MuJ: 28.08.2007
 */
public class XmlTransformingTestBase extends TestBase {

  /**
   * Flag to determine whether this class has been initialized (by the init() method).
   */
  private static boolean initialized = false;

  /**
   * Initialize the class, i.e. set the member variables so that they can be shared by different
   * methods.
   * 
   * @throws ParserConfigurationException
   */
  protected static void init() throws ParserConfigurationException {
    setInitialized(true);
  }

  /**
   * Serialize the given Dom Object to a String.
   * 
   * @param xml The Xml Node to serialize.
   * @param omitXMLDeclaration Indicates if XML declaration will be omitted.
   * @return The String representation of the Xml Node.
   * @throws Exception If anything fails.
   */
  protected static String toString(final Node xml, final boolean omitXMLDeclaration)
      throws Exception {
    Transformer transformer =
        TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null)
            .newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");

    // initialize StreamResult with File object to save to file
    StreamResult result = new StreamResult(new StringWriter());
    DOMSource source = new DOMSource(xml);
    transformer.transform(source, result);

    String xmlString = result.getWriter().toString();
    return xmlString;
  }

  /**
   * Delivers the flag to determine whether this class has been initialized yet.
   * 
   * @return true if this class has been initialized yet.
   */
  public static boolean isInitialized() {
    return initialized;
  }

  /**
   * Sets the flag to determine whether this class has been initialized yet.
   * 
   * @param initialized
   */
  private static void setInitialized(boolean initialized) {
    XmlTransformingTestBase.initialized = initialized;
  }
}
