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

package de.mpg.mpdl.inge.model.valueobjects;

import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;

/**
 * An affiliation path is a list of affiliations representing the path between two affiliations
 * (normally beween an affiliation and a top-level affiliation).
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 10:30:46
 */
public class AffiliationPathVO extends ValueObject {
  /**
   * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
   * 'de.mpg.mpdl.inge.model.valueobjects.ItemVO; local class incompatible: stream classdesc
   * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286'
   * that occur after JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to
   * be compiled twice for testing (once for the Application Server, once for the local test).
   * 
   * @author Johannes Mueller
   */

  /**
   * The list of affiliations representing the affiliation path.
   */
  private java.util.List<AffiliationRO> affiliationList = new java.util.ArrayList<AffiliationRO>();

  /**
   * Delivers the list of affiliations representing the affiliation path.
   */
  public java.util.List<AffiliationRO> getAffiliationList() {
    return affiliationList;
  }
}
