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

package de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.exceptions;

/**
 * This exception occurs whenever something goes wrong during unmarshalling. The member variable
 * <i>m_xml</i> contains information about the XML that should have been unmarshalled.
 *
 * @author Johannes Mueller (initial creation)
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @revised by MuJ: 03.09.2007
 */
@SuppressWarnings("serial")
public class UnmarshallingException extends TransformingException {
  /**
   * The XML that should have been unmarshalled and that caused the exception.
   */
  private final String m_xml;

  /**
   * Constructor forwarding a given exception message and a cause to the upper exception chain. The
   * XML that should have been unmarshalled is stored in the according member variable.
   *
   * @param xml The XML that should have been unmarshalled.
   * @param cause The Throwable.
   */
  public UnmarshallingException(String xml, Throwable cause) {
    super("The affected XML is as follows:\n" + xml, cause);
    this.m_xml = xml;
  }

  /**
   * Delivers the XML that should have been unmarshalled and that caused the exception.
   *
   * @return The xml
   */
  public String getXml() {
    return this.m_xml;
  }
}
