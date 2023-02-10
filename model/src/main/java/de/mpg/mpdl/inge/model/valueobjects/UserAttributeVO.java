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
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Attributes of an user account
 * 
 * @author haarlaender
 * 
 */
@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_EMPTY)
public class UserAttributeVO extends ValueObject {

  /**
   * The id of the attribute.
   */
  private String objectId;

  /**
   * The name of the attribute
   */
  private String name;

  /**
   * The value of the attribute
   */
  private String value;


  public String getObjectId() {
    return objectId;
  }

  public void setObjectId(String objectId) {
    this.objectId = objectId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((objectId == null) ? 0 : objectId.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;

    if (obj == null)
      return false;

    if (getClass() != obj.getClass())
      return false;

    UserAttributeVO other = (UserAttributeVO) obj;

    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;

    if (objectId == null) {
      if (other.objectId != null)
        return false;
    } else if (!objectId.equals(other.objectId))
      return false;

    if (value == null) {
      if (other.value != null)
        return false;
    } else if (!value.equals(other.value))
      return false;

    return true;
  }

}
