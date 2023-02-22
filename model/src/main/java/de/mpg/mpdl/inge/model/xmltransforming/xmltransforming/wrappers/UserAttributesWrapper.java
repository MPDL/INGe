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

package de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.wrappers;

import java.io.Serializable;
import java.util.List;

import de.mpg.mpdl.inge.model.valueobjects.UserAttributeVO;

/**
 * This class is used by the XML transforming classes to wrap a list of UserAttributeVOs. The reason
 * for this is that JiBX cannot bind directly to ArrayLists.
 * 
 * @author Markus Haarlaender (initial creation)
 * 
 */
@SuppressWarnings("serial")
public class UserAttributesWrapper implements Serializable {
  /**
   * The wrapped list of UserAtributeVOs.
   */
  private List<UserAttributeVO> userAttributes;

  public void setUserAttributes(List<UserAttributeVO> userAttributes) {
    this.userAttributes = userAttributes;
  }

  public List<UserAttributeVO> getUserAttributes() {
    return userAttributes;
  }



}
