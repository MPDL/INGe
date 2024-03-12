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

import java.util.ArrayList;
import java.util.HashSet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

/**
 * @revised by MuJ: 27.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 12:48:57
 */
@SuppressWarnings("serial")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class PersonVO extends ValueObject implements Cloneable {
  private String completeName;
  private String givenName;
  private String familyName;
  private java.util.List<String> alternativeNames = new java.util.ArrayList<>();
  private java.util.List<String> titles = new java.util.ArrayList<>();
  private java.util.List<String> pseudonyms = new java.util.ArrayList<>();
  private java.util.List<OrganizationVO> organizations = new java.util.ArrayList<>();
  private IdentifierVO identifier;
  private String orcid;

  /**
   * Delivers the complete name of the person, usually a concatenation of given names and family
   * name.
   */
  public String getCompleteName() {
    return this.completeName;
  }

  /**
   * Sets the complete name of the person, usually a concatenation of given names and family name.
   *
   * @param newVal
   */
  public void setCompleteName(String newVal) {
    this.completeName = newVal;
  }

  /**
   * Delivers the given name of the person.
   */
  public String getGivenName() {
    return this.givenName;
  }

  /**
   * Sets the given name of the person.
   *
   * @param newVal
   */
  public void setGivenName(String newVal) {
    this.givenName = newVal;
  }

  /**
   * Delivers the family name of the person.
   */
  public String getFamilyName() {
    return this.familyName;
  }

  /**
   * Sets the family name of the person.
   *
   * @param newVal
   */
  public void setFamilyName(String newVal) {
    this.familyName = newVal;
  }

  /**
   * Delivers the list of organizational units the person was affiliated to when creating the item.
   */
  public java.util.List<OrganizationVO> getOrganizations() {
    return this.organizations;
  }

  /**
   * Sets the list of organizational units the person was affiliated to when creating the item.
   *
   * @param organizations
   */
  public void setOrganizations(java.util.List<OrganizationVO> organizations) {
    this.organizations = organizations;
  }

  /**
   * Delivers the size of the organization list
   */
  @JsonIgnore
  public int getOrganizationsSize() {
    return this.organizations.size();
  }

  /**
   * Delivers the identifier in the Personennormdatei, provided by the Deutsche Nationalbibliothek.
   */
  public IdentifierVO getIdentifier() {
    return this.identifier;
  }

  /**
   * Delivers the list of or stage names of the person.
   */
  public java.util.List<String> getPseudonyms() {
    return this.pseudonyms;
  }

  /**
   * Sets the identifier in the Personennormdatei, provided by the Deutsche Nationalbibliothek.
   *
   * @param newVal
   */
  public void setIdentifier(IdentifierVO newVal) {
    this.identifier = newVal;
  }

  /**
   * Delivers the list of alternative names used for the person.
   */
  public java.util.List<String> getAlternativeNames() {
    return this.alternativeNames;
  }

  /**
   * Delivers the list of titles of the person.
   */
  public java.util.List<String> getTitles() {
    return this.titles;
  }

  public final PersonVO clone() {
    try {
      PersonVO clone = (PersonVO) super.clone();
      if (null != this.identifier) {
        clone.identifier = this.identifier.clone();
      }
      clone.alternativeNames = new ArrayList<>(this.alternativeNames);
      clone.organizations = new ArrayList<>(this.organizations);
      for (OrganizationVO organization : this.organizations) {
        clone.organizations.add(organization.clone());
      }
      clone.pseudonyms = new ArrayList<>(this.pseudonyms);
      clone.titles = new ArrayList<>(this.titles);
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((null == this.alternativeNames) ? 0 : this.alternativeNames.hashCode());
    result = prime * result + ((null == this.completeName) ? 0 : this.completeName.hashCode());
    result = prime * result + ((null == this.familyName) ? 0 : this.familyName.hashCode());
    result = prime * result + ((null == this.givenName) ? 0 : this.givenName.hashCode());
    result = prime * result + ((null == this.identifier) ? 0 : this.identifier.hashCode());
    result = prime * result + ((null == this.organizations) ? 0 : this.organizations.hashCode());
    result = prime * result + ((null == this.pseudonyms) ? 0 : this.pseudonyms.hashCode());
    result = prime * result + ((null == this.titles) ? 0 : this.titles.hashCode());
    result = prime * result + ((null == this.orcid) ? 0 : this.orcid.hashCode());
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

    PersonVO other = (PersonVO) obj;

    if (null == this.alternativeNames) {
      if (null != other.alternativeNames)
        return false;
    } else if (null == other.alternativeNames)
      return false;
    else if (!new HashSet<>(this.alternativeNames).containsAll(other.alternativeNames) //
        || !new HashSet<>(other.alternativeNames).containsAll(this.alternativeNames)) {
      return false;
    }

    if (null == this.completeName) {
      if (null != other.completeName)
        return false;
    } else if (!this.completeName.equals(other.completeName))
      return false;

    if (null == this.familyName) {
      if (null != other.familyName)
        return false;
    } else if (!this.familyName.equals(other.familyName))
      return false;

    if (null == this.givenName) {
      if (null != other.givenName)
        return false;
    } else if (!this.givenName.equals(other.givenName))
      return false;

    if (null == this.identifier) {
      if (null != other.identifier)
        return false;
    } else if (!this.identifier.equals(other.identifier))
      return false;

    if (null == this.organizations) {
      if (null != other.organizations)
        return false;
    } else if (null == other.organizations)
      return false;
    else if (!new HashSet<>(this.organizations).containsAll(other.organizations) //
        || !new HashSet<>(other.organizations).containsAll(this.organizations)) {
      return false;
    }

    if (null == this.pseudonyms) {
      if (null != other.pseudonyms)
        return false;
    } else if (null == other.pseudonyms)
      return false;
    else if (!new HashSet<>(this.pseudonyms).containsAll(other.pseudonyms) //
        || !new HashSet<>(other.pseudonyms).containsAll(this.pseudonyms)) {
      return false;
    }

    if (null == this.titles) {
      if (null != other.titles)
        return false;
    } else if (null == other.titles)
      return false;
    else if (!new HashSet<>(this.titles).containsAll(other.titles) //
        || !new HashSet<>(other.titles).containsAll(this.titles)) {
      return false;
    }

    if (null == this.orcid) {
      if (null != other.orcid)
        return false;
    } else if (!this.orcid.equals(other.orcid))
      return false;

    return true;
  }

  public String getOrcid() {
    return this.orcid;
  }

  public void setOrcid(String orcid) {
    this.orcid = orcid;
  }

}
