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

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

/**
 * @revised by MuJ: 27.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 22-Okt-2007 15:27:10
 */
@SuppressWarnings("serial")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class OrganizationVO extends ValueObject implements Cloneable {
  private String address;
  private String identifier;
  private String name;
  private String[] identifierPath;

  /**
   * Delivers the address of the organization as used in the item.
   */
  public String getAddress() {
    return this.address;
  }

  /**
   * Delivers the id of the corresponding affiliation in the system.
   */
  public String getIdentifier() {
    return this.identifier;
  }

  /**
   * Delivers the name of the organization as used in the item.
   */

  public String getName() {
    return this.name;
  }

  /**
   * Sets the address of the organization as used in the item.
   *
   * @param newVal
   */
  public void setAddress(String newVal) {
    this.address = newVal;
  }

  /**
   * Sets the name of the organization as used in the item.
   *
   * @param newVal
   */
  public void setName(String newVal) {
    this.name = newVal;
  }

  public final OrganizationVO clone() {
    try {
      OrganizationVO clone = (OrganizationVO) super.clone();
      if (null != this.identifierPath) {
        clone.identifierPath = this.identifierPath.clone();
      }
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((null == this.address) ? 0 : this.address.hashCode());
    result = prime * result + ((null == this.identifier) ? 0 : this.identifier.hashCode());
    result = prime * result + Arrays.hashCode(this.identifierPath);
    result = prime * result + ((null == this.name) ? 0 : this.name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (null == obj)
      return false;

    // Enabling comparison with super classes like OrganizationVOPresentation
    if (!getClass().isAssignableFrom(obj.getClass()))
      return false;

    OrganizationVO other = (OrganizationVO) obj;
    if (null == this.address) {
      if (null != other.address)
        return false;
    } else if (!this.address.equals(other.address))
      return false;
    if (null == this.identifier) {
      if (null != other.identifier)
        return false;
    } else if (!this.identifier.equals(other.identifier))
      return false;
    if (!Arrays.equals(this.identifierPath, other.identifierPath))
      return false;
    if (null == this.name) {
      if (null != other.name)
        return false;
    } else if (!this.name.equals(other.name))
      return false;
    return true;
  }

  /**
   * Sets the id of the corresponding affiliation in the system.
   *
   * @param newVal
   */
  public void setIdentifier(String newVal) {
    this.identifier = newVal;
  }

  public String[] getIdentifierPath() {
    return this.identifierPath;
  }

  public void setIdentifierPath(String[] identifierPath) {
    this.identifierPath = identifierPath;
  }
}
