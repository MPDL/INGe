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

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * The functional specification of the publication and the modification workflow define several
 * validation points relevant for publication items at a specific point in the workflow. A
 * validation point is implemented by a set of validation rules which are associated to the activity
 * (use case) in the workflow, e.g. 'submit item version', 'accept item version'.
 *
 * @revised by MuJ: 28.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 10:30:53
 */
@SuppressWarnings("serial")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ValidationPointVO extends ValueObject {
  private String name;

  /**
   * Delivers the name of the validation point.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Sets the name of the validation point.
   *
   * @param newVal
   */
  public void setName(String newVal) {
    this.name = newVal;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((null == this.name) ? 0 : this.name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;

    if (null == obj)
      return false;

    if (getClass() != obj.getClass())
      return false;

    ValidationPointVO other = (ValidationPointVO) obj;

    if (null == this.name) {
      if (null != other.name)
        return false;
    } else if (!this.name.equals(other.name))
      return false;

    return true;
  }
}
