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

package de.mpg.mpdl.inge.db.model.valueobjects;

import java.util.ArrayList;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.db.model.hibernate.MdsOrganizationalUnitVOJsonUserType;
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
@JsonInclude(value = Include.NON_NULL)
@Entity(name = "AffiliationVO")
@Table(name = "organization")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "organization")
@TypeDef(name = "MdsOrganizationalUnitVOJsonUserType",
    typeClass = MdsOrganizationalUnitVOJsonUserType.class)
public class AffiliationDbVO extends AffiliationDbRO {
  public enum State {
    CREATED, CLOSED, OPENED, DELETED
  }

  @Type(type = "MdsOrganizationalUnitVOJsonUserType")
  @Column
  private MdsOrganizationalUnitDetailsVO metadata = new MdsOrganizationalUnitDetailsVO();

  // private List<MetadataSetVO> metadataSets = new ArrayList<MetadataSetVO>();

  @ManyToOne(fetch = FetchType.EAGER)
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "organization")
  private AffiliationDbRO parentAffiliation;



  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "organization_predecessor")
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "organization")
  private java.util.List<AffiliationDbRO> predecessorAffiliations =
      new ArrayList<AffiliationDbRO>();

  @Enumerated(EnumType.STRING)
  private AffiliationDbVO.State publicStatus;


  @Formula("(select count(*)>0 from organization op WHERE op.parentAffiliation_objectid=objectId)")
  private boolean hasChildren;

  /**
   * Default constructor.
   */
  public AffiliationDbVO() {

  }



  /**
   * Delivers the list of the affiliations' parent affiliations.
   */
  public AffiliationDbRO getParentAffiliation() {
    return parentAffiliation;
  }

  public void setParentAffiliation(AffiliationDbRO parentAffiliation) {
    this.parentAffiliation = parentAffiliation;
  }


  /**
   * Delivers the publicly visible status of the affiliation. The public status can only be changed
   * by the system.
   */
  public AffiliationDbVO.State getPublicStatus() {
    return publicStatus;
  }

  /**
   * Sets the publicly visible status of the affiliation. The public status can only be changed by
   * the system.
   * 
   * @param newVal
   */
  public void setPublicStatus(AffiliationDbVO.State newVal) {
    publicStatus = newVal;
  }



  /**
   * @return the predecessorAffiliations
   */
  public java.util.List<AffiliationDbRO> getPredecessorAffiliations() {
    return predecessorAffiliations;
  }

  /**
   * @param predecessorAffiliations the predecessorAffiliations to set
   */
  public void setPredecessorAffiliations(java.util.List<AffiliationDbRO> predecessorAffiliations) {
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

  public boolean getHasChildren() {
    return hasChildren;
  }

  public void setHasChildren(boolean hasChildren) {
    this.hasChildren = hasChildren;
  }
}
