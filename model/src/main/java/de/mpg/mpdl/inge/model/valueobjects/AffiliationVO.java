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
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
@JsonInclude(value = Include.NON_EMPTY)
public class AffiliationVO extends ValueObject implements Searchable {
  private java.util.List<AffiliationRO> childAffiliations = new java.util.ArrayList<AffiliationRO>();

  private List<MetadataSetVO> metadataSets = new ArrayList<MetadataSetVO>();

  private java.util.List<AffiliationRO> parentAffiliations = new ArrayList<AffiliationRO>();

  private java.util.List<AffiliationRO> predecessorAffiliations = new ArrayList<AffiliationRO>();

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
   * Clone constructor.
   */
  public AffiliationVO(AffiliationVO affiliation) {

    this.childAffiliations = affiliation.childAffiliations;

    this.parentAffiliations = affiliation.parentAffiliations;
    this.reference = affiliation.reference;
    this.creationDate = affiliation.creationDate;
    this.lastModificationDate = affiliation.lastModificationDate;
    this.creator = affiliation.creator;
    this.modifiedBy = affiliation.modifiedBy;
    this.hasChildren = affiliation.hasChildren;
    this.publicStatus = affiliation.publicStatus;
    this.metadataSets = affiliation.metadataSets;
    this.predecessorAffiliations = affiliation.predecessorAffiliations;
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    return new AffiliationVO(this);
  }

  /**
   * Helper method for JiBX transformations. This method helps JiBX to determine if this is a
   * 'create' or an 'update' transformation.
   */
  boolean alreadyExistsInFramework() {
    return (this.reference != null);
  }

  /**
   * Helper method for JiBX transformations. This method helps JiBX to determine if a "parents" XML
   * structure has to be created during marshalling.
   */
  boolean hasParentAffiliations() {
    return (this.parentAffiliations.size() >= 1);
  }

  /**
   * Delivers the list of the affiliations' child affiliations.
   */
  public java.util.List<AffiliationRO> getChildAffiliations() {
    return childAffiliations;
  }

  /**
   * Convenience method to retrieve escidoc metadat set.
   * 
   * 
   * @return A {@link MdsOrganizationalUnitDetailsVO}.
   */
  public MdsOrganizationalUnitDetailsVO getDefaultMetadata() {
    if (metadataSets.size() > 0 && metadataSets.get(0) instanceof MdsOrganizationalUnitDetailsVO) {
      return (MdsOrganizationalUnitDetailsVO) metadataSets.get(0);
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
    if (metadataSets.size() == 0) {
      metadataSets.add(detailsVO);
    } else {
      metadataSets.set(0, detailsVO);
    }
  }

  /**
   * Delivers the creation date of the affiliation, i. e. a timestamp from the system when the
   * organizational unit is created.
   */
  public java.util.Date getCreationDate() {
    return creationDate;
  }

  /**
   * Delivers the creator of the affiliation, i. e. the account user that created the affiliation in
   * the system.
   */
  public AccountUserRO getCreator() {
    return creator;
  }

  /**
   * Delivers the date if the last modification of the affiliation in the system.
   */
  public java.util.Date getLastModificationDate() {
    return lastModificationDate;
  }

  /**
   * Delivers the list of the affiliations' parent affiliations.
   */
  public java.util.List<AffiliationRO> getParentAffiliations() {
    return parentAffiliations;
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
    return reference;
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
    return publicStatus;
  }

  /**
   * Sets the publicly visible status of the affiliation. The public status can only be changed by
   * the system.
   * 
   * @param newVal
   */
  public void setPublicStatus(String newVal) {
    publicStatus = newVal;
  }

  /**
   * Sets the flag indicating whether the affiliation has child affiliations or not.
   * 
   * @param newVal
   */
  public void setHasChildren(boolean newVal) {
    hasChildren = newVal;
  }

  /**
   * Helper method for JiBX transformations. This method helps JiBX to determine if a "parents" XML
   * structure has to be created during marshalling.
   */
  boolean hasParents() {
    return (this.parentAffiliations.size() >= 1);
  }

  /**
   * Delivers true if the affiliation has child affiliations. The idiosyncratic method name is
   * chosen to support JSF backing beans.
   * 
   * @return true if the affiliation has child affiliations.
   */
  public boolean getHasChildren() {
    return hasChildren;
  }

  @JsonIgnore
  public List<MetadataSetVO> getMetadataSets() {
    return metadataSets;
  }

  public AccountUserRO getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(AccountUserRO modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  /**
   * @return the predecessorAffiliations
   */
  public java.util.List<AffiliationRO> getPredecessorAffiliations() {
    return predecessorAffiliations;
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
    return (this.predecessorAffiliations.size() != 0);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((childAffiliations == null) ? 0 : childAffiliations.hashCode());
    result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
    result = prime * result + ((creator == null) ? 0 : creator.hashCode());
    result = prime * result + (hasChildren ? 1231 : 1237);
    result = prime * result + ((lastModificationDate == null) ? 0 : lastModificationDate.hashCode());
    result = prime * result + ((metadataSets == null) ? 0 : metadataSets.hashCode());
    result = prime * result + ((modifiedBy == null) ? 0 : modifiedBy.hashCode());
    result = prime * result + ((parentAffiliations == null) ? 0 : parentAffiliations.hashCode());
    result = prime * result + ((predecessorAffiliations == null) ? 0 : predecessorAffiliations.hashCode());
    result = prime * result + ((publicStatus == null) ? 0 : publicStatus.hashCode());
    result = prime * result + ((reference == null) ? 0 : reference.hashCode());
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

    AffiliationVO other = (AffiliationVO) obj;

    if (childAffiliations == null) {
      if (other.childAffiliations != null)
        return false;
    } else if (other.childAffiliations == null)
      return false;
    else if (!childAffiliations.containsAll(other.childAffiliations) //
        || !other.childAffiliations.containsAll(childAffiliations)) {
      return false;
    }

    if (creationDate == null) {
      if (other.creationDate != null)
        return false;
    } else if (!creationDate.equals(other.creationDate))
      return false;

    if (creator == null) {
      if (other.creator != null)
        return false;
    } else if (!creator.equals(other.creator))
      return false;

    if (hasChildren != other.hasChildren)
      return false;

    if (lastModificationDate == null) {
      if (other.lastModificationDate != null)
        return false;
    } else if (!lastModificationDate.equals(other.lastModificationDate))
      return false;

    if (metadataSets == null) {
      if (other.metadataSets != null)
        return false;
    } else if (other.metadataSets == null)
      return false;
    else if (!metadataSets.containsAll(other.metadataSets) //
        || !other.metadataSets.containsAll(metadataSets)) {
      return false;
    }

    if (modifiedBy == null) {
      if (other.modifiedBy != null)
        return false;
    } else if (!modifiedBy.equals(other.modifiedBy))
      return false;

    if (parentAffiliations == null) {
      if (other.parentAffiliations != null)
        return false;
    } else if (other.parentAffiliations == null)
      return false;
    else if (!parentAffiliations.containsAll(other.parentAffiliations) //
        || !other.parentAffiliations.containsAll(parentAffiliations)) {
      return false;
    }

    if (predecessorAffiliations == null) {
      if (other.predecessorAffiliations != null)
        return false;
    } else if (other.predecessorAffiliations == null)
      return false;
    else if (!predecessorAffiliations.containsAll(other.predecessorAffiliations) //
        || !other.predecessorAffiliations.containsAll(predecessorAffiliations)) {
      return false;
    }

    if (publicStatus == null) {
      if (other.publicStatus != null)
        return false;
    } else if (!publicStatus.equals(other.publicStatus))
      return false;

    if (reference == null) {
      if (other.reference != null)
        return false;
    } else if (!reference.equals(other.reference))
      return false;

    return true;
  }
}
