/*
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import de.mpg.mpdl.inge.model.referenceobjects.AccountUserRO;
import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.valueobjects.interfaces.Searchable;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;

/**
 * A MPG unit or lower level of organizational unit within an MPG unit; includes also external
 * affiliations. (Dependent on internal organizational structure: Institute, Department, project
 * groups, working groups, temporary working groups, etc.)
 *
 * @revised by MuJ: 28.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 07-Sep-2007 13:27:29
 */
@SuppressWarnings("serial")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class AffiliationVO extends ValueObject implements Searchable {
  private final java.util.List<AffiliationRO> childAffiliations = new java.util.ArrayList<>();

  private final List<MetadataSetVO> metadataSets = new ArrayList<>();

  private java.util.List<AffiliationRO> parentAffiliations = new ArrayList<>();

  private java.util.List<AffiliationRO> predecessorAffiliations = new ArrayList<>();

  protected AffiliationRO reference;

  private java.util.Date creationDate;
  private java.util.Date lastModificationDate;
  private AccountUserRO creator;
  private AccountUserRO modifiedBy;
  private boolean hasChildren;
  private String publicStatus;

  /**
   * Default constructor.
   */
  public AffiliationVO() {

  }

  /**
   * Helper method for JiBX transformations. This method helps JiBX to determine if this is a
   * 'create' or an 'update' transformation.
   */
  boolean alreadyExistsInFramework() {
    return (null != this.reference);
  }

  /**
   * Helper method for JiBX transformations. This method helps JiBX to determine if a "parents" XML
   * structure has to be created during marshalling.
   */
  boolean hasParentAffiliations() {
    return (!this.parentAffiliations.isEmpty());
  }

  /**
   * Delivers the list of the affiliations' child affiliations.
   */
  public java.util.List<AffiliationRO> getChildAffiliations() {
    return this.childAffiliations;
  }

  /**
   * Convenience method to retrieve escidoc metadat set.
   *
   *
   * @return A {@link MdsOrganizationalUnitDetailsVO}.
   */
  public MdsOrganizationalUnitDetailsVO getDefaultMetadata() {
    if (!this.metadataSets.isEmpty() && this.metadataSets.get(0) instanceof MdsOrganizationalUnitDetailsVO) {
      return (MdsOrganizationalUnitDetailsVO) this.metadataSets.get(0);
    } else {
      return null;
    }
  }

  /**
   * Convenience method to set escidoc metadata set.
   *
   * @param detailsVO A {@link MdsOrganizationalUnitDetailsVO} containing the default metadata.
   */
  public void setDefaultMetadata(MdsOrganizationalUnitDetailsVO detailsVO) {
    if (this.metadataSets.isEmpty()) {
      this.metadataSets.add(detailsVO);
    } else {
      this.metadataSets.set(0, detailsVO);
    }
  }

  /**
   * Delivers the creation date of the affiliation, i. e. a timestamp from the system when the
   * organizational unit is created.
   */
  public java.util.Date getCreationDate() {
    return this.creationDate;
  }

  /**
   * Delivers the creator of the affiliation, i. e. the account user that created the affiliation in
   * the system.
   */
  public AccountUserRO getCreator() {
    return this.creator;
  }

  /**
   * Delivers the date if the last modification of the affiliation in the system.
   */
  public java.util.Date getLastModificationDate() {
    return this.lastModificationDate;
  }

  /**
   * Delivers the list of the affiliations' parent affiliations.
   */
  public java.util.List<AffiliationRO> getParentAffiliations() {
    return this.parentAffiliations;
  }

  public void setParentAffiliations(java.util.List<AffiliationRO> parentAffiliations) {
    this.parentAffiliations = parentAffiliations;
  }

  /**
   * Delivers the affiliations' reference.
   *
   * @see de.mpg.mpdl.inge.model.referenceobjects.ReferenceObject
   */
  public AffiliationRO getReference() {
    return this.reference;
  }

  /**
   * Sets the creation date of the affiliation, i. e. a timestamp from the system when the
   * organizational unit is created.
   *
   * @param newVal
   */
  public void setCreationDate(java.util.Date newVal) {
    this.creationDate = newVal;
  }

  /**
   * Sets the creator of the affiliation, i. e. the account user that created the affiliation in the
   * system.
   *
   * @param newVal
   */
  public void setCreator(AccountUserRO newVal) {
    this.creator = newVal;
  }

  /**
   * Sets the date if the last modification of the affiliation in the system.
   *
   * @param newVal
   */
  public void setLastModificationDate(java.util.Date newVal) {
    this.lastModificationDate = newVal;
  }

  /**
   * Sets the affiliations' reference.
   *
   * @see de.mpg.mpdl.inge.model.referenceobjects.ReferenceObject
   * @param newVal newVal
   */
  public void setReference(AffiliationRO newVal) {
    this.reference = newVal;
  }

  /**
   * Delivers the publicly visible status of the affiliation. The public status can only be changed
   * by the system.
   */
  public String getPublicStatus() {
    return this.publicStatus;
  }

  /**
   * Sets the publicly visible status of the affiliation. The public status can only be changed by
   * the system.
   *
   * @param newVal
   */
  public void setPublicStatus(String newVal) {
    this.publicStatus = newVal;
  }

  /**
   * Sets the flag indicating whether the affiliation has child affiliations or not.
   *
   * @param newVal
   */
  public void setHasChildren(boolean newVal) {
    this.hasChildren = newVal;
  }

  /**
   * Helper method for JiBX transformations. This method helps JiBX to determine if a "parents" XML
   * structure has to be created during marshalling.
   */
  boolean hasParents() {
    return (!this.parentAffiliations.isEmpty());
  }

  /**
   * Delivers true if the affiliation has child affiliations. The idiosyncratic method name is
   * chosen to support JSF backing beans.
   *
   * @return true if the affiliation has child affiliations.
   */
  public boolean getHasChildren() {
    return this.hasChildren;
  }

  @JsonIgnore
  public List<MetadataSetVO> getMetadataSets() {
    return this.metadataSets;
  }

  public AccountUserRO getModifiedBy() {
    return this.modifiedBy;
  }

  public void setModifiedBy(AccountUserRO modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  /**
   * @return the predecessorAffiliations
   */
  public java.util.List<AffiliationRO> getPredecessorAffiliations() {
    return this.predecessorAffiliations;
  }

  /**
   * @param predecessorAffiliations the predecessorAffiliations to set
   */
  public void setPredecessorAffiliations(java.util.List<AffiliationRO> predecessorAffiliations) {
    this.predecessorAffiliations = predecessorAffiliations;
  }

  /**
   * Are predecessors available.
   *
   * @return true if predecessors are available
   */
  public boolean getHasPredecessors() {
    return (!this.predecessorAffiliations.isEmpty());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((null == this.childAffiliations) ? 0 : this.childAffiliations.hashCode());
    result = prime * result + ((null == this.creationDate) ? 0 : this.creationDate.hashCode());
    result = prime * result + ((null == this.creator) ? 0 : this.creator.hashCode());
    result = prime * result + (this.hasChildren ? 1231 : 1237);
    result = prime * result + ((null == this.lastModificationDate) ? 0 : this.lastModificationDate.hashCode());
    result = prime * result + ((null == this.metadataSets) ? 0 : this.metadataSets.hashCode());
    result = prime * result + ((null == this.modifiedBy) ? 0 : this.modifiedBy.hashCode());
    result = prime * result + ((null == this.parentAffiliations) ? 0 : this.parentAffiliations.hashCode());
    result = prime * result + ((null == this.predecessorAffiliations) ? 0 : this.predecessorAffiliations.hashCode());
    result = prime * result + ((null == this.publicStatus) ? 0 : this.publicStatus.hashCode());
    result = prime * result + ((null == this.reference) ? 0 : this.reference.hashCode());
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

    AffiliationVO other = (AffiliationVO) obj;

    if (null == this.childAffiliations) {
      if (null != other.childAffiliations)
        return false;
    } else if (null == other.childAffiliations)
      return false;
    else if (!new HashSet<>(this.childAffiliations).containsAll(other.childAffiliations) //
        || !new HashSet<>(other.childAffiliations).containsAll(this.childAffiliations)) {
      return false;
    }

    if (null == this.creationDate) {
      if (null != other.creationDate)
        return false;
    } else if (!this.creationDate.equals(other.creationDate))
      return false;

    if (null == this.creator) {
      if (null != other.creator)
        return false;
    } else if (!this.creator.equals(other.creator))
      return false;

    if (this.hasChildren != other.hasChildren)
      return false;

    if (null == this.lastModificationDate) {
      if (null != other.lastModificationDate)
        return false;
    } else if (!this.lastModificationDate.equals(other.lastModificationDate))
      return false;

    if (null == this.metadataSets) {
      if (null != other.metadataSets)
        return false;
    } else if (null == other.metadataSets)
      return false;
    else if (!new HashSet<>(this.metadataSets).containsAll(other.metadataSets) //
        || !new HashSet<>(other.metadataSets).containsAll(this.metadataSets)) {
      return false;
    }

    if (null == this.modifiedBy) {
      if (null != other.modifiedBy)
        return false;
    } else if (!this.modifiedBy.equals(other.modifiedBy))
      return false;

    if (null == this.parentAffiliations) {
      if (null != other.parentAffiliations)
        return false;
    } else if (null == other.parentAffiliations)
      return false;
    else if (!new HashSet<>(this.parentAffiliations).containsAll(other.parentAffiliations) //
        || !new HashSet<>(other.parentAffiliations).containsAll(this.parentAffiliations)) {
      return false;
    }

    if (null == this.predecessorAffiliations) {
      if (null != other.predecessorAffiliations)
        return false;
    } else if (null == other.predecessorAffiliations)
      return false;
    else if (!new HashSet<>(this.predecessorAffiliations).containsAll(other.predecessorAffiliations) //
        || !new HashSet<>(other.predecessorAffiliations).containsAll(this.predecessorAffiliations)) {
      return false;
    }

    if (null == this.publicStatus) {
      if (null != other.publicStatus)
        return false;
    } else if (!this.publicStatus.equals(other.publicStatus))
      return false;

    if (null == this.reference) {
      if (null != other.reference)
        return false;
    } else if (!this.reference.equals(other.reference))
      return false;

    return true;
  }
}
