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

package de.mpg.mpdl.inge.model.valueobjects.metadata;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

/**
 * @revised by MuJ: 27.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 22-Okt-2007 15:27:10
 */
@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_EMPTY)
public class OrganizationVO extends ValueObject implements Cloneable {
  private String address;
  private String identifier;
  private String name;
  private String[] identifierPath;

  /**
   * Delivers the address of the organization as used in the item.
   */
  public String getAddress() {
    return address;
  }

  /**
   * Delivers the id of the corresponding affiliation in the system.
   */
  public String getIdentifier() {
    return identifier;
  }

  /**
   * Delivers the name of the organization as used in the item.
   */

  public String getName() {
    return name;
  }

  /**
   * Sets the address of the organization as used in the item.
   * 
   * @param newVal
   */
  public void setAddress(String newVal) {
    address = newVal;
  }

  /**
   * Sets the name of the organization as used in the item.
   * 
   * @param newVal
   */
  public void setName(String newVal) {
    name = newVal;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#clone()
   */
  @Override
  public Object clone() {
    OrganizationVO clone = new OrganizationVO();
    clone.setAddress(getAddress());
    if (getIdentifier() != null) {
      clone.setIdentifier(getIdentifier());
    }
    if (getName() != null) {
      clone.setName(getName());
    }
    if ((getIdentifierPath() != null)) {
      clone.setIdentifierPath(Arrays.copyOf(getIdentifierPath(), getIdentifierPath().length));
    }
    return clone;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((address == null) ? 0 : address.hashCode());
    result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
    result = prime * result + Arrays.hashCode(identifierPath);
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;

    // Enabling comparison with super classes like OrganizationVOPresentation
    if (!getClass().isAssignableFrom(obj.getClass()))
      return false;

    OrganizationVO other = (OrganizationVO) obj;
    if (address == null) {
      if (other.address != null)
        return false;
    } else if (!address.equals(other.address))
      return false;
    if (identifier == null) {
      if (other.identifier != null)
        return false;
    } else if (!identifier.equals(other.identifier))
      return false;
    if (!Arrays.equals(identifierPath, other.identifierPath))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

  /**
   * Sets the id of the corresponding affiliation in the system.
   * 
   * @param newVal
   */
  public void setIdentifier(String newVal) {
    identifier = newVal;
  }

  public String[] getIdentifierPath() {
    return identifierPath;
  }

  public void setIdentifierPath(String[] identifierPath) {
    this.identifierPath = identifierPath;
  }
}
