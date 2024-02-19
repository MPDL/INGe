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

/**
 * The class for Affiliation references.
 *
 * @revised by MuJ: 27.08.2007
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @updated 04-Sep-2007 11:43:18
 */
@SuppressWarnings("serial")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class AffiliationRO extends ReferenceObject {
  private String form;

  /**
   * Creates a new instance.
   */
  public AffiliationRO() {}

  /**
   * Creates a new instance with the given objectId.
   *
   * @param objectId The id of the object.
   */
  public AffiliationRO(String objectId) {
    super(objectId);
  }

  /**
   * @return the form
   */
  public String getForm() {
    return this.form;
  }

  /**
   * @param form the form to set
   */
  public void setForm(String form) {
    this.form = form;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((null == this.form) ? 0 : this.form.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;

    if (!super.equals(obj))
      return false;

    if (getClass() != obj.getClass())
      return false;

    AffiliationRO other = (AffiliationRO) obj;

    if (null == this.form) {
      if (null != other.form)
        return false;
    } else if (!this.form.equals(other.form))
      return false;

    return true;
  }
}
