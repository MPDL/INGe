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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;
import de.mpg.mpdl.inge.model.valueobjects.interfaces.IgnoreForCleanup;

/**
 * @revised by MuJ: 29.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 12:48:55
 */
@SuppressWarnings("serial")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class CreatorVO extends ValueObject implements Cloneable {
  /**
   * The possible roles of the creator.
   *
   * @updated 05-Sep-2007 12:48:55
   */
  public enum CreatorRole
  {
    ARTIST("http://www.loc.gov/loc.terms/relators/ART"),
    AUTHOR("http://www.loc.gov/loc.terms/relators/AUT"),
    DEVELOPER("http://www.loc.gov/loc.terms/relators/developer"),
    EDITOR("http://www.loc.gov/loc.terms/relators/EDT"),
    PAINTER("http://purl.org/escidoc/metadata/ves/creator-roles/painter"),
    ILLUSTRATOR("http://www.loc.gov/loc.terms/relators/ILL"),
    PHOTOGRAPHER("http://www.loc.gov/loc.terms/relators/PHT"),
    COMMENTATOR("http://www.loc.gov/loc.terms/relators/CMM"),
    TRANSCRIBER("http://www.loc.gov/loc.terms/relators/TRC"),
    ADVISOR("http://www.loc.gov/loc.terms/relators/SAD"),
    TRANSLATOR("http://www.loc.gov/loc.terms/relators/TRL"),
    CONTRIBUTOR("http://www.loc.gov/loc.terms/relators/CTB"),
    HONOREE("http://www.loc.gov/loc.terms/relators/HNR"),
    REFEREE("http://purl.org/escidoc/metadata/ves/creator-roles/referee"),
    INTERVIEWEE("http://www.loc.gov/loc.terms/relators/interviewee"),
    INTERVIEWER("http://www.loc.gov/loc.terms/relators/interviewer"),
    INVENTOR("http://www.loc.gov/loc.terms/relators/INV"),
    APPLICANT("http://www.loc.gov/loc.terms/relators/APP"),
    DIRECTOR("http://www.loc.gov/loc.terms/relators/DRT"),
    PRODUCER("http://www.loc.gov/loc.terms/relators/PRO"),
    ACTOR("http://www.loc.gov/loc.terms/relators/ACT"),
    CINEMATOGRAPHER("http://www.loc.gov/loc.terms/relators/CNG"),
    SOUND_DESIGNER("http://www.loc.gov/loc.terms/relators/SDS");


  private final String uri;

  CreatorRole(String uri) {
      this.uri = uri;
    }

  public String getUri() {
    return this.uri;
  }}

  /**
   * The possible creator types.
   *
   * @updated 05-Sep-2007 12:48:55
   */
  public enum CreatorType{PERSON,ORGANIZATION}

  private OrganizationVO organization;
  private PersonVO person;

  @IgnoreForCleanup
  private CreatorRole role;

  @IgnoreForCleanup
  private CreatorType type;

  /**
   * Creates a new instance.
   */
  public CreatorVO() {}

  /**
   * Creates a new instance with the given organization and role.
   *
   * @param organization The organization
   * @param role The creator role
   */
  public CreatorVO(OrganizationVO organization, CreatorRole role) {
    // use the setter as the setter does more than just setting the property
    setOrganization(organization);
    this.role = role;
  }

  /**
   * Creates a new instance with the given person and role.
   *
   * @param person The person
   * @param role The creator role
   */
  public CreatorVO(PersonVO person, CreatorRole role) {
    // use the setter as the setter does more than just setting the property
    setPerson(person);
    this.role = role;
  }

  /**
   * Delivers the organization (or null if the creator is not an organization).
   */
  public OrganizationVO getOrganization() {
    return this.organization;
  }

  /**
   * Delivers the person (or null if the creator is not an person).
   */
  public PersonVO getPerson() {
    return this.person;
  }

  /**
   * Delivers the creators' role.
   */
  public CreatorRole getRole() {
    return this.role;
  }

  /**
   * Delivers the creators' type.
   */
  public CreatorType getType() {
    return this.type;
  }

  /**
   * Set the creator to the given organization. Because the creator cannot be an organization and a
   * person at the same time, the person is set to null.
   *
   * @param newVal newVal
   */
  public void setOrganization(OrganizationVO newVal) {
    this.type = CreatorType.ORGANIZATION;
    this.person = null;
    this.organization = newVal;
  }

  /**
   * Set the creator to the given person. Because the creator cannot be a person and an organization
   * at the same time, the organization is set to null.
   *
   * @param newVal newVal
   */
  public void setPerson(PersonVO newVal) {
    this.type = CreatorType.PERSON;
    this.organization = null;
    this.person = newVal;
  }

  /**
   * Set the creators' role.
   *
   * @param newVal newVal
   */
  public void setRole(CreatorRole newVal) {
    this.role = newVal;
  }

  public void setType(CreatorType newVal) {

    this.type = newVal;

  }

  public final CreatorVO clone() {
    try {
      CreatorVO clone = (CreatorVO) super.clone();
      if (null != clone.organization) {
        clone.organization = this.organization.clone();
      }
      if (null != clone.type) {
        clone.type = this.type;
      }
      if (null != clone.person) {
        clone.person = this.person.clone();
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
    result = prime * result + ((null == this.organization) ? 0 : this.organization.hashCode());
    result = prime * result + ((null == this.person) ? 0 : this.person.hashCode());
    result = prime * result + ((null == this.role) ? 0 : this.role.hashCode());
    result = prime * result + ((null == this.type) ? 0 : this.type.hashCode());
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

    CreatorVO other = (CreatorVO) obj;

    if (null == this.organization) {
      if (null != other.organization)
        return false;
    } else if (!this.organization.equals(other.organization))
      return false;

    if (null == this.person) {
      if (null != other.person)
        return false;
    } else if (!this.person.equals(other.person))
      return false;

    if (this.role != other.role)
      return false;

    if (this.type != other.type)
      return false;

    return true;
  }

  /**
   * Delivers the value of the role Enum as a String. If the enum is not set, an empty String is
   * returned.
   *
   * @return the value of the role Enum
   */
  @JsonIgnore
  public String getRoleString() {
    if (null == this.role || null == this.role.toString()) {
      return "";
    }
    return this.role.toString();
  }

  /**
   * Sets the value of the role Enum by a String.
   *
   * @param newValString A string containing the new value.
   */
  @JsonIgnore
  public void setRoleString(String newValString) {
    if (null == newValString || newValString.isEmpty()) {
      this.role = null;
    } else {
      CreatorVO.CreatorRole newVal = CreatorVO.CreatorRole.valueOf(newValString);
      this.role = newVal;
    }
  }

  /**
   * Delivers the value of the type Enum as a String. If the enum is not set, an empty String is
   * returned.
   *
   * @return the value of the type Enum
   */
  @JsonIgnore
  public String getTypeString() {
    if (null == this.type || null == this.type.toString()) {
      return "";
    }
    return this.type.toString();
  }

  /**
   * Sets the value of the type Enum by a String.
   *
   * @param newValString A string containing the new value.
   */
  @JsonIgnore
  public void setTypeString(String newValString) {
    if (null == newValString || newValString.isEmpty()) {
      this.type = null;
    } else {
      CreatorVO.CreatorType newVal = CreatorVO.CreatorType.valueOf(newValString);
      this.type = newVal;
    }
  }
}
