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


/**
 * The class for PubCollection references.
 * 
 * @revised by MuJ: 27.08.2007
 * @version 1.0
 * @updated 21-Nov-2007 12:38:41
 */
@SuppressWarnings("serial")
public class ContextRO extends ReferenceObject implements Cloneable {
  /**
   * Creates a new instance.
   */
  public ContextRO() {
    super();
  }

  /**
   * Creates a new instance with the given objectId.
   * 
   * @param objectId The id of the object.
   */
  public ContextRO(String objectId) {
    super(objectId);
  }

  /**
   * Copy constructor.
   * 
   * @author Thomas Diebaecker
   * @param other The instance to copy.
   */
  public ContextRO(ContextRO other) {
    super(other);
  }

  /**
   * {@inheritDoc}
   * 
   * @author Thomas Diebaecker
   */
  public Object clone() {
    return new ContextRO(this);
  }
}
