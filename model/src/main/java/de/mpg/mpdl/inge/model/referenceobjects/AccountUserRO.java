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

package de.mpg.mpdl.inge.model.referenceobjects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The class for AccountUser references.
 * 
 * @revised by MuJ: 27.08.2007
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @updated 21-Nov-2007 12:08:27
 */
@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_EMPTY)
public class AccountUserRO extends ReferenceObject implements Cloneable {
  /**
   * Creates a new instance.
   */
  public AccountUserRO() {
    super();
  }

  /**
   * Copy constructor.
   * 
   * @author Thomas Diebaecker
   * @param other The instance to copy.
   */
  public AccountUserRO(AccountUserRO other) {
    super(other);
  }

  /**
   * {@inheritDoc}
   * 
   * @author Thomas Diebaecker
   */
  @Override
  public Object clone() {
    return new AccountUserRO(this);
  }

  /**
   * Helper method for JiBX transformations. This method helps JiBX to determine if this is a
   * 'create' or an 'update' transformation.
   * 
   * @return boolean true if the reference of this AffiliationVO is set
   */
  public boolean alreadyExistsInFramework() {
    return (getObjectId() != null);
  }

  /**
   * Creates a new instance with the given objectId.
   * 
   * @param objectId The id of the object.
   */
  public AccountUserRO(String objectId) {
    super(objectId);
  }

}
