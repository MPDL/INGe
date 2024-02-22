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

package de.mpg.mpdl.inge.model.db.valueobjects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.interfaces.Searchable;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

/**
 * Special type of container of data with specific workflow (i.e. the publication management
 * workflow). A set of publication objects which have some common denominator. Collection may
 * contain one or more subcollections.
 *
 * @revised by MuJ: 28.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 11:14:08
 */
@SuppressWarnings("serial")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@Entity
@Table(name = "context")
//@TypeDef(name = "SubjectClassificationListJsonUserType", typeClass = SubjectClassificationListJsonUserType.class)
//@TypeDef(name = "GenreListJsonUserType", typeClass = GenreListJsonUserType.class)
public class ContextDbVO extends ContextDbRO implements Searchable {
  /**
   * The possible states of a collection.
   *
   * @updated 05-Sep-2007 11:14:08
   */
  public enum State
  {
    CLOSED,
    OPENED
  }


  public enum Workflow
  {
    STANDARD,
    SIMPLE
  }

  //@Type(type = "GenreListJsonUserType")
  @JdbcTypeCode(SqlTypes.JSON)
  private List<MdsPublicationVO.Genre> allowedGenres = new ArrayList<>();

  //@Type(type = "SubjectClassificationListJsonUserType")
  @JdbcTypeCode(SqlTypes.JSON)
  private List<MdsPublicationVO.SubjectClassification> allowedSubjectClassifications = new ArrayList<>();

  @Enumerated(EnumType.STRING)
  private Workflow workflow;

  private String contactEmail;

  /**
   * The state of the PubCollection.
   */
  @Enumerated(EnumType.STRING)
  private ContextDbVO.State state;
  /**
   * A short description of the collection and the collection policy.
   */

  @Column(columnDefinition = "TEXT")
  private String description;

  /**
   * The list of responsible affiliations for this collection.
   */
  @ManyToMany(fetch = FetchType.EAGER, targetEntity=AffiliationDbVO.class)
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "organization")
  @JsonSerialize(contentAs=AffiliationDbRO.class)
  @JoinTable(name = "context_organization")
  private java.util.List<AffiliationDbRO> responsibleAffiliations = new java.util.ArrayList<>();


  /**
   * Default constructor.
   */
  public ContextDbVO() {}

  public ContextDbVO(ContextDbVO other) {

    MapperFactory.STRUCT_MAP_MAPPER.updateContextDbVO(other, this);
  }

  /**
   * Delivers the description of the collection, i. e. a short description of the collection and the
   * collection policy.
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Delivers the state of the collection.
   */
  public ContextDbVO.State getState() {
    return this.state;
  }

  /**
   * Sets the description of the collection, i. e. a short description of the collection and the
   * collection policy.
   *
   * @param newVal
   */
  public void setDescription(String newVal) {
    this.description = newVal;
  }

  /**
   * Sets the state of the collection.
   *
   * @param newVal
   */
  public void setState(ContextDbVO.State newVal) {
    this.state = newVal;
  }

  /**
   * Delivers the list of affiliations which are responsible for this collection.
   */
  public java.util.List<AffiliationDbRO> getResponsibleAffiliations() {
    return this.responsibleAffiliations;
  }

  public void setResponsibleAffiliations(java.util.List<AffiliationDbRO> responsibleAffiliations) {
    this.responsibleAffiliations = responsibleAffiliations;
  }

  public List<MdsPublicationVO.Genre> getAllowedGenres() {
    return this.allowedGenres;
  }

  public void setAllowedGenres(List<MdsPublicationVO.Genre> allowedGenres) {
    this.allowedGenres = allowedGenres;
  }

  public List<MdsPublicationVO.SubjectClassification> getAllowedSubjectClassifications() {
    return this.allowedSubjectClassifications;
  }

  public void setAllowedSubjectClassifications(List<MdsPublicationVO.SubjectClassification> allowedSubjectClassifications) {
    this.allowedSubjectClassifications = allowedSubjectClassifications;
  }

  public Workflow getWorkflow() {
    return this.workflow;
  }

  public void setWorkflow(Workflow workflow) {
    this.workflow = workflow;
  }

  public String getContactEmail() {
    return this.contactEmail;
  }

  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((null == this.allowedGenres) ? 0 : this.allowedGenres.hashCode());
    result = prime * result + ((null == this.allowedSubjectClassifications) ? 0 : this.allowedSubjectClassifications.hashCode());
    result = prime * result + ((null == this.contactEmail) ? 0 : this.contactEmail.hashCode());
    result = prime * result + ((null == this.description) ? 0 : this.description.hashCode());
    result = prime * result + ((null == this.responsibleAffiliations) ? 0 : this.responsibleAffiliations.hashCode());
    result = prime * result + ((null == this.state) ? 0 : this.state.hashCode());
    result = prime * result + ((null == this.workflow) ? 0 : this.workflow.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    ContextDbVO other = (ContextDbVO) obj;
    if (null == this.allowedGenres) {
      if (null != other.allowedGenres)
        return false;
    } else if (!this.allowedGenres.equals(other.allowedGenres))
      return false;
    if (null == this.allowedSubjectClassifications) {
      if (null != other.allowedSubjectClassifications)
        return false;
    } else if (!this.allowedSubjectClassifications.equals(other.allowedSubjectClassifications))
      return false;
    if (null == this.contactEmail) {
      if (null != other.contactEmail)
        return false;
    } else if (!this.contactEmail.equals(other.contactEmail))
      return false;
    if (null == this.description) {
      if (null != other.description)
        return false;
    } else if (!this.description.equals(other.description))
      return false;
    if (null == this.responsibleAffiliations) {
      if (null != other.responsibleAffiliations)
        return false;
    } else if (!this.responsibleAffiliations.equals(other.responsibleAffiliations))
      return false;
    if (this.state != other.state)
      return false;
    if (this.workflow != other.workflow)
      return false;
    return true;
  }
}
