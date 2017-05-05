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

package de.mpg.mpdl.inge.model_new.valueobjects;

import java.util.ArrayList;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;
import de.mpg.mpdl.inge.model_new.hibernate.MdsOrganizationalUnitVOJsonUserType;

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
@JsonInclude(value = Include.NON_NULL)
@Entity
@Table(name = "organization")
@TypeDef(name = "MdsOrganizationalUnitVOJsonUserType",
    typeClass = MdsOrganizationalUnitVOJsonUserType.class)
public class AffiliationVO extends AffiliationRO {
  public enum State {
    CREATED, CLOSED, OPENED, DELETED
  }

  @Type(type = "MdsOrganizationalUnitVOJsonUserType")
  @Column
  private MdsOrganizationalUnitDetailsVO metadata = new MdsOrganizationalUnitDetailsVO();

  // private List<MetadataSetVO> metadataSets = new ArrayList<MetadataSetVO>();

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "organization_parent")
  private java.util.List<AffiliationRO> parentAffiliations = new ArrayList<AffiliationRO>();

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "organization_predecessor")
  private java.util.List<AffiliationRO> predecessorAffiliations = new ArrayList<AffiliationRO>();

  @Enumerated(EnumType.STRING)
  private State publicStatus;

  /**
   * Default constructor.
   */
  public AffiliationVO() {

  }

  /**
   * Clone constructor.
   */
  /*
   * public AffiliationVO(AffiliationVO affiliation) {
   * 
   * this.childAffiliations = affiliation.childAffiliations;
   * 
   * this.parentAffiliations = affiliation.parentAffiliations; this.objectId = affiliation.objectId;
   * this.creationDate = affiliation.creationDate; this.lastModificationDate =
   * affiliation.lastModificationDate; this.creator = affiliation.creator; this.modifiedBy =
   * affiliation.modifiedBy; this.hasChildren = affiliation.hasChildren; this.publicStatus =
   * affiliation.publicStatus; this.metadata = affiliation.metadata; this.predecessorAffiliations =
   * affiliation.predecessorAffiliations; }
   * 
   * 
   * @Override protected Object clone() throws CloneNotSupportedException { return new
   * AffiliationVO(this); }
   */


  /**
   * Helper method for JiBX transformations. This method helps JiBX to determine if a "parents" XML
   * structure has to be created during marshalling.
   */
  boolean hasParentAffiliations() {
    return (this.parentAffiliations.size() >= 1);
  }


  /**
   * Convenience method to retrieve escidoc metadat set.
   * 
   * 
   * @return A {@link MdsOrganizationalUnitDetailsVO}.
   */
  /*
   * public MdsOrganizationalUnitDetailsVO getDefaultMetadata() { if (metadataSets.size() > 0 &&
   * metadataSets.get(0) instanceof MdsOrganizationalUnitDetailsVO) { return
   * (MdsOrganizationalUnitDetailsVO) metadataSets.get(0); } else { return null; } }
   */

  /**
   * Convenience method to set escidoc metadata set.
   * 
   * @param detailsVO A {@link MdsOrganizationalUnitDetailsVO} containing the default metadata.
   */
  /*
   * public void setDefaultMetadata(MdsOrganizationalUnitDetailsVO detailsVO) { if
   * (metadataSets.size() == 0) { metadataSets.add(detailsVO); } else { metadataSets.set(0,
   * detailsVO); } }
   */

  /**
   * Delivers the list of the affiliations' parent affiliations.
   */
  public java.util.List<AffiliationRO> getParentAffiliations() {
    return parentAffiliations;
  }



  /**
   * Delivers the publicly visible status of the affiliation. The public status can only be changed
   * by the system.
   */
  public State getPublicStatus() {
    return publicStatus;
  }

  /**
   * Sets the publicly visible status of the affiliation. The public status can only be changed by
   * the system.
   * 
   * @param newVal
   */
  public void setPublicStatus(State newVal) {
    publicStatus = newVal;
  }

  /**
   * Helper method for JiBX transformations. This method helps JiBX to determine if a "parents" XML
   * structure has to be created during marshalling.
   */
  boolean hasParents() {
    return (this.parentAffiliations.size() >= 1);
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

  public MdsOrganizationalUnitDetailsVO getMetadata() {
    return metadata;
  }

  public void setMetadata(MdsOrganizationalUnitDetailsVO metadata) {
    this.metadata = metadata;
  }

  /*
   * @Override public int hashCode() { final int prime = 31; int result = 1; result = prime * result
   * + ((childAffiliations == null) ? 0 : childAffiliations.hashCode()); result = prime * result +
   * ((creationDate == null) ? 0 : creationDate.hashCode()); result = prime * result + ((creator ==
   * null) ? 0 : creator.hashCode()); result = prime * result + (hasChildren ? 1231 : 1237); result
   * = prime * result + ((lastModificationDate == null) ? 0 : lastModificationDate.hashCode());
   * result = prime * result + ((metadata == null) ? 0 : metadata.hashCode()); result = prime *
   * result + ((modifiedBy == null) ? 0 : modifiedBy.hashCode()); result = prime * result +
   * ((parentAffiliations == null) ? 0 : parentAffiliations.hashCode()); result = prime * result +
   * ((predecessorAffiliations == null) ? 0 : predecessorAffiliations.hashCode()); result = prime *
   * result + ((publicStatus == null) ? 0 : publicStatus.hashCode()); result = prime * result +
   * ((objectId == null) ? 0 : objectId.hashCode()); return result; }
   * 
   * @Override public boolean equals(Object obj) { if (this == obj) return true;
   * 
   * if (obj == null) return false;
   * 
   * if (getClass() != obj.getClass()) return false;
   * 
   * AffiliationVO other = (AffiliationVO) obj;
   * 
   * if (childAffiliations == null) { if (other.childAffiliations != null) return false; } else if
   * (other.childAffiliations == null) return false; else if
   * (!childAffiliations.containsAll(other.childAffiliations) // ||
   * !other.childAffiliations.containsAll(childAffiliations)) { return false; }
   * 
   * if (creationDate == null) { if (other.creationDate != null) return false; } else if
   * (!creationDate.equals(other.creationDate)) return false;
   * 
   * if (creator == null) { if (other.creator != null) return false; } else if
   * (!creator.equals(other.creator)) return false;
   * 
   * if (hasChildren != other.hasChildren) return false;
   * 
   * if (lastModificationDate == null) { if (other.lastModificationDate != null) return false; }
   * else if (!lastModificationDate.equals(other.lastModificationDate)) return false;
   * 
   * if (metadata == null) { if (other.metadata != null) return false; } else if (other.metadata ==
   * null) return false; else if (!metadata.equals(other.metadata)) { return false; }
   * 
   * if (modifiedBy == null) { if (other.modifiedBy != null) return false; } else if
   * (!modifiedBy.equals(other.modifiedBy)) return false;
   * 
   * if (parentAffiliations == null) { if (other.parentAffiliations != null) return false; } else if
   * (other.parentAffiliations == null) return false; else if
   * (!parentAffiliations.containsAll(other.parentAffiliations) // ||
   * !other.parentAffiliations.containsAll(parentAffiliations)) { return false; }
   * 
   * if (predecessorAffiliations == null) { if (other.predecessorAffiliations != null) return false;
   * } else if (other.predecessorAffiliations == null) return false; else if
   * (!predecessorAffiliations.containsAll(other.predecessorAffiliations) // ||
   * !other.predecessorAffiliations.containsAll(predecessorAffiliations)) { return false; }
   * 
   * if (publicStatus == null) { if (other.publicStatus != null) return false; } else if
   * (!publicStatus.equals(other.publicStatus)) return false;
   * 
   * if (objectId == null) { if (other.objectId != null) return false; } else if
   * (!objectId.equals(other.objectId)) return false;
   * 
   * return true; }
   */
}
