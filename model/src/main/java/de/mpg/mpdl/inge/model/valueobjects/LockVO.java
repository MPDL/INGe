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

package de.mpg.mpdl.inge.model.valueobjects;

import de.mpg.mpdl.inge.model.referenceobjects.AccountUserRO;
import de.mpg.mpdl.inge.model.referenceobjects.LockRO;

/**
 * This class wraps information about a the locking of a resource.
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 10:48:16
 */
@SuppressWarnings("serial")
public class LockVO extends ValueObject {
  private java.util.Date lockDate;
  private LockRO reference;
  private AccountUserRO user;

  /**
   * Delivers the locking date.
   */
  public java.util.Date getLockDate() {
    return lockDate;
  }

  /**
   * Delivers the locks' reference.
   * 
   * @see de.mpg.mpdl.inge.model.referenceobjects.ReferenceObject
   */
  public LockRO getReference() {
    return reference;
  }

  /**
   * Delivers the reference of the user who owns the lock.
   */
  public AccountUserRO getUser() {
    return user;
  }

  /**
   * Sets the locking date.
   * 
   * @param newVal
   */
  public void setLockDate(java.util.Date newVal) {
    this.lockDate = newVal;
  }

  /**
   * Sets the locks' reference.
   * 
   * @see de.mpg.mpdl.inge.model.referenceobjects.ReferenceObject
   * @param newVal
   */
  public void setReference(LockRO newVal) {
    this.reference = newVal;
  }

  /**
   * Sets the reference of the user who owns the lock.
   * 
   * @param newVal
   */
  public void setUser(AccountUserRO newVal) {
    this.user = newVal;
  }
}
