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

package de.mpg.mpdl.inge.db.model.valueobjects;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.db.model.hibernate.ContextAdminDescriptorJsonUserType;
import de.mpg.mpdl.inge.model.valueobjects.interfaces.Searchable;
import de.mpg.mpdl.inge.model.valueobjects.publication.PublicationAdminDescriptorVO;

/**
 * Special type of container of data with specific workflow (i.e. the publication management
 * workflow). A set of publication objects which have some common denominator. Collection may
 * contain one or more subcollections.
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 11:14:08
 */
@JsonInclude(value = Include.NON_NULL)
@Entity(name = "ContextVO")
@Table(name = "context")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "context")
@TypeDef(name = "ContextAdminDescriptorJsonUserType",
    typeClass = ContextAdminDescriptorJsonUserType.class)
public class ContextDbVO extends ContextDbRO implements Searchable {
  /**
   * The possible states of a collection.
   * 
   * @updated 05-Sep-2007 11:14:08
   */
  public enum State {
    CREATED, CLOSED, OPENED, DELETED
  }

  private String type;
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
  @ManyToMany(fetch = FetchType.EAGER)
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "organization")
  private java.util.List<AffiliationDbRO> responsibleAffiliations =
      new java.util.ArrayList<AffiliationDbRO>();

  @Type(type = "ContextAdminDescriptorJsonUserType")
  private PublicationAdminDescriptorVO adminDescriptor;

  /**
   * Default constructor.
   */
  public ContextDbVO() {}

  /**
   * Delivers the description of the collection, i. e. a short description of the collection and the
   * collection policy.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Delivers the state of the collection.
   */
  public ContextDbVO.State getState() {
    return state;
  }

  /**
   * Sets the description of the collection, i. e. a short description of the collection and the
   * collection policy.
   * 
   * @param newVal
   */
  public void setDescription(String newVal) {
    description = newVal;
  }

  /**
   * Sets the state of the collection.
   * 
   * @param newVal
   */
  public void setState(ContextDbVO.State newVal) {
    state = newVal;
  }

  /**
   * Delivers the list of affiliations which are responsible for this collection.
   */
  public java.util.List<AffiliationDbRO> getResponsibleAffiliations() {
    return responsibleAffiliations;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public PublicationAdminDescriptorVO getAdminDescriptor() {
    return adminDescriptor;
  }

  public void setAdminDescriptor(PublicationAdminDescriptorVO adminDescriptor) {
    this.adminDescriptor = adminDescriptor;
  }

  public void setResponsibleAffiliations(java.util.List<AffiliationDbRO> responsibleAffiliations) {
    this.responsibleAffiliations = responsibleAffiliations;
  }
}
