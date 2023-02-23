/*
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
package de.mpg.mpdl.inge.dataacquisition;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.mpg.mpdl.inge.util.DOMUtilities;

/**
 * 
 * The ProtocolHandler contains methods for checking responses for various protocols.
 * 
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class ProtocolHandler {
  /**
   * Public constructor for the protocolHandler class.
   */
  public ProtocolHandler() {}

  /**
   * Checks a OAI Record for error codes and throws corresponding exceptions.
   * 
   * @param record
   * @throws BadArgumentException
   * @throws IdentifierNotRecognisedException
   * @throws FormatNotRecognisedException
   * @throws RuntimeException
   */
  public void checkOAIRecord(String record) throws DataacquisitionException {

    Document recordDOM;

    // Possible error codes:
    String error1 = "badArgument";
    String error2 = "cannotDisseminateFormat";
    String error3 = "idDoesNotExist";

    try {
      recordDOM = DOMUtilities.createDocument(record, true);
    } catch (Exception e) {
      throw new RuntimeException("An error occurred while checking the OAI Record: " + record);
    }

    NodeList errorList = recordDOM.getElementsByTagName("error");

    for (int i = 0; i < errorList.getLength(); i++) {
      Node errorNode = errorList.item(i);
      NamedNodeMap attr = errorNode.getAttributes();
      Node errorCode = attr.getNamedItem("code");

      if (errorCode.getTextContent().equals(error1)) {
        throw new DataacquisitionException("OAI-Record returned with error Code " + "'badArgument "
            + "(The request includes illegal arguments or is missing required arguments)'.");
      }

      if (errorCode.getTextContent().equals(error2)) {
        throw new DataacquisitionException("OAI-Record returned with error Code " + "'cannotDisseminateFormat "
            + "(The value of the metadataPrefix argument is not supported by the item "
            + "identified by the value of the identifier argument)'.");
      }

      if (errorCode.getTextContent().equals(error3)) {
        throw new DataacquisitionException("OAI-Record returned with error Code " + "'idDoesNotExist "
            + "(The value of the identifier argument is unknown or illegal in this repository)'.");
      }
    }
  }
}
