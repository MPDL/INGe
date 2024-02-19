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

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import de.mpg.mpdl.inge.model.referenceobjects.GrantRO;

/**
 * A grant wraps a role that is granted to a certain certain object (like an affiliation or a
 * collection).
 *
 * @revised by MuJ: 28.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 10:46:17
 */
@SuppressWarnings("serial")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class GrantVO extends ValueObject {
  /**
   * The role that is granted. The value of this attribute matches the value the framework gives
   * back as role (e. g. "escidoc:role-depositor").
   */
  private String role;

  /**
   * The scope of this grant.
   */
  private String grantType;

  /**
   * The object to which this grant was granted.
   */
  @JsonIgnore
  private String grantedTo;

  /**
   * The reference to the object for which the role is granted. Changed to String by FrM.
   */
  private String objectRef;

  /**
   * The possible predefined roles. Caution: To compare roles to PredefinedRoles, use the according
   * isPredefinedRole() method, or compare the role with the PredefinedRole.value(). It is:
   * user.isModerator <=> frameworkValue="escidoc:role-md-editor" (the framework role
   * "role-moderator" is not the same as the PubMan role MODERATOR!)
   */

  @JsonIgnore
  private GrantRO reference;

  @JsonIgnore
  private Date lastModificationDate;



  public enum PredefinedRoles
  {
    DEPOSITOR("DEPOSITOR"),
    MODERATOR("MODERATOR"),
    SYSADMIN("SYSADMIN"),
    LOCAL_ADMIN("LOCAL_ADMIN"),
    REPORTER("REPORTER");

  private final String frameworkValue;

  PredefinedRoles(String frameworkValue) {
      this.frameworkValue = frameworkValue;
    }

  public String frameworkValue() {
    return this.frameworkValue;
  }

  }

  public GrantVO() {}

  /**
   * Constructor using fields.
   *
   * @param role The granted role.
   * @param object The object the role is granted on.
   */
  public GrantVO(String role, String object) {
    this.role = role;
    this.objectRef = object;
  }

  /**
   * Copyconstructor
   *
   * @param grant The granted which will be copied.
   */
  public GrantVO(GrantVO grant) {
    this.grantedTo = grant.grantedTo;
    this.grantType = grant.grantType;
    this.objectRef = grant.objectRef;
    this.reference = grant.reference;
    this.role = grant.role;
  }

  /**
   * Delivers the object reference of the object the rights are granted for.
   */
  public String getObjectRef() {
    return this.objectRef;
  }

  /**
   * Delivers the role that is granted. The value of this attribute matches the value the framework
   * gives back as role (e. g. "escidoc:role-depositor"). If you want to check if the role matches a
   * predefined role, use the according isPredefinedRole() method instead.
   */
  public String getRole() {
    return this.role;
  }

  /**
   * Sets the object reference of the object the rights are granted for.
   *
   * @param newVal newVal
   */
  public void setObjectRef(String newVal) {
    this.objectRef = newVal;
  }

  /**
   * Sets the role that is granted. The value of this attribute must match the value the framework
   * expects as role (e. g. "escidoc:role-depositor").
   *
   * @param newVal newVal
   */
  public void setRole(String newVal) {
    this.role = newVal;
  }

  public String toString() {
    return "[" + this.objectRef + " : " + this.role + "]";
  }

  public GrantRO getReference() {
    return this.reference;
  }

  public void setReference(GrantRO reference) {
    this.reference = reference;
  }

  public Date getLastModificationDate() {
    return this.lastModificationDate;
  }

  public void setLastModificationDate(Date lastModificationDate) {
    this.lastModificationDate = lastModificationDate;
  }

  public String getGrantedTo() {
    return this.grantedTo;
  }

  public void setGrantedTo(String grantedTo) {
    this.grantedTo = grantedTo;
  }

  public String getGrantType() {
    return this.grantType;
  }

  public void setGrantType(String grantType) {
    this.grantType = grantType;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((null == this.grantType) ? 0 : this.grantType.hashCode());
    result = prime * result + ((null == this.grantedTo) ? 0 : this.grantedTo.hashCode());
    result = prime * result + ((null == this.lastModificationDate) ? 0 : this.lastModificationDate.hashCode());
    result = prime * result + ((null == this.objectRef) ? 0 : this.objectRef.hashCode());
    result = prime * result + ((null == this.reference) ? 0 : this.reference.hashCode());
    result = prime * result + ((null == this.role) ? 0 : this.role.hashCode());
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

    GrantVO other = (GrantVO) obj;

    if (null == this.grantType) {
      if (null != other.grantType)
        return false;
    } else if (!this.grantType.equals(other.grantType))
      return false;

    if (null == this.grantedTo) {
      if (null != other.grantedTo)
        return false;
    } else if (!this.grantedTo.equals(other.grantedTo))
      return false;

    if (null == this.lastModificationDate) {
      if (null != other.lastModificationDate)
        return false;
    } else if (!this.lastModificationDate.equals(other.lastModificationDate))
      return false;

    if (null == this.objectRef) {
      if (null != other.objectRef)
        return false;
    } else if (!this.objectRef.equals(other.objectRef))
      return false;

    if (null == this.reference) {
      if (null != other.reference)
        return false;
    } else if (!this.reference.equals(other.reference))
      return false;

    if (null == this.role) {
      if (null != other.role)
        return false;
    } else if (!this.role.equals(other.role))
      return false;

    return true;
  }

}
