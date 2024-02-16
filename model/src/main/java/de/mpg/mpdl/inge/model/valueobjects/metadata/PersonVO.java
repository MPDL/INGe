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
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import de.mpg.mpdl.inge.model.valueobjects.ValueObject;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * @revised by MuJ: 27.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 12:48:57
 */
@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_EMPTY)
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
    return completeName;
  }

  /**
   * Sets the complete name of the person, usually a concatenation of given names and family name.
   *
   * @param newVal
   */
  public void setCompleteName(String newVal) {
    completeName = newVal;
  }

  /**
   * Delivers the given name of the person.
   */
  public String getGivenName() {
    return givenName;
  }

  /**
   * Sets the given name of the person.
   *
   * @param newVal
   */
  public void setGivenName(String newVal) {
    givenName = newVal;
  }

  /**
   * Delivers the family name of the person.
   */
  public String getFamilyName() {
    return familyName;
  }

  /**
   * Sets the family name of the person.
   *
   * @param newVal
   */
  public void setFamilyName(String newVal) {
    familyName = newVal;
  }

  /**
   * Delivers the list of organizational units the person was affiliated to when creating the item.
   */
  public java.util.List<OrganizationVO> getOrganizations() {
    return organizations;
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
    return organizations.size();
  }

  /**
   * Delivers the identifier in the Personennormdatei, provided by the Deutsche Nationalbibliothek.
   */
  public IdentifierVO getIdentifier() {
    return identifier;
  }

  /**
   * Delivers the list of or stage names of the person.
   */
  public java.util.List<String> getPseudonyms() {
    return pseudonyms;
  }

  /**
   * Sets the identifier in the Personennormdatei, provided by the Deutsche Nationalbibliothek.
   *
   * @param newVal
   */
  public void setIdentifier(IdentifierVO newVal) {
    identifier = newVal;
  }

  /**
   * Delivers the list of alternative names used for the person.
   */
  public java.util.List<String> getAlternativeNames() {
    return alternativeNames;
  }

  /**
   * Delivers the list of titles of the person.
   */
  public java.util.List<String> getTitles() {
    return titles;
  }

  public PersonVO clone() {
    try {
      PersonVO clone = (PersonVO) super.clone();
      if (this.identifier != null) {
        clone.identifier = this.identifier.clone();
      }
      clone.alternativeNames = new ArrayList<>(this.alternativeNames);
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
    result = prime * result + ((alternativeNames == null) ? 0 : alternativeNames.hashCode());
    result = prime * result + ((completeName == null) ? 0 : completeName.hashCode());
    result = prime * result + ((familyName == null) ? 0 : familyName.hashCode());
    result = prime * result + ((givenName == null) ? 0 : givenName.hashCode());
    result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
    result = prime * result + ((organizations == null) ? 0 : organizations.hashCode());
    result = prime * result + ((pseudonyms == null) ? 0 : pseudonyms.hashCode());
    result = prime * result + ((titles == null) ? 0 : titles.hashCode());
    result = prime * result + ((orcid == null) ? 0 : orcid.hashCode());
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

    PersonVO other = (PersonVO) obj;

    if (alternativeNames == null) {
      if (other.alternativeNames != null)
        return false;
    } else if (other.alternativeNames == null)
      return false;
    else if (!new HashSet<>(alternativeNames).containsAll(other.alternativeNames) //
        || !new HashSet<>(other.alternativeNames).containsAll(alternativeNames)) {
      return false;
    }

    if (completeName == null) {
      if (other.completeName != null)
        return false;
    } else if (!completeName.equals(other.completeName))
      return false;

    if (familyName == null) {
      if (other.familyName != null)
        return false;
    } else if (!familyName.equals(other.familyName))
      return false;

    if (givenName == null) {
      if (other.givenName != null)
        return false;
    } else if (!givenName.equals(other.givenName))
      return false;

    if (identifier == null) {
      if (other.identifier != null)
        return false;
    } else if (!identifier.equals(other.identifier))
      return false;

    if (organizations == null) {
      if (other.organizations != null)
        return false;
    } else if (other.organizations == null)
      return false;
    else if (!new HashSet<>(organizations).containsAll(other.organizations) //
        || !new HashSet<>(other.organizations).containsAll(organizations)) {
      return false;
    }

    if (pseudonyms == null) {
      if (other.pseudonyms != null)
        return false;
    } else if (other.pseudonyms == null)
      return false;
    else if (!new HashSet<>(pseudonyms).containsAll(other.pseudonyms) //
        || !new HashSet<>(other.pseudonyms).containsAll(pseudonyms)) {
      return false;
    }

    if (titles == null) {
      if (other.titles != null)
        return false;
    } else if (other.titles == null)
      return false;
    else if (!new HashSet<>(titles).containsAll(other.titles) //
        || !new HashSet<>(other.titles).containsAll(titles)) {
      return false;
    }

    if (orcid == null) {
      if (other.orcid != null)
        return false;
    } else if (!orcid.equals(other.orcid))
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
