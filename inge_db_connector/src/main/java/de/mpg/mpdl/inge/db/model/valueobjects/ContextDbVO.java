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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

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
@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_NULL)
@Entity(name = "ContextVO")
@Table(name = "context")
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
  private State state;
  /**
   * A short description of the collection and the collection policy.
   */

  @Column(columnDefinition = "TEXT")
  private String description;

  /**
   * The list of responsible affiliations for this collection.
   */
  @ManyToMany(fetch = FetchType.EAGER)
  private java.util.List<AffiliationDbRO> responsibleAffiliations =
      new java.util.ArrayList<AffiliationDbRO>();

  @Type(type = "ContextAdminDescriptorJsonUserType")
  private PublicationAdminDescriptorVO adminDescriptor;



  /**
   * Default constructor.
   */
  public ContextDbVO() {

  }

  /**
   * Clone constructor.
   * 
   * @param context The collection to be cloned.
   */
  /*
   * public ContextVO(ContextVO context) { this.creator = context.creator; this.defaultMetadata =
   * context.defaultMetadata; this.description = context.description; this.name = context.name;
   * this.objectId = context.objectId; this.responsibleAffiliations =
   * context.responsibleAffiliations; this.state = context.state; this.validationPoints =
   * context.validationPoints; this.adminDescriptor = context.adminDescriptor; this.type =
   * context.type; }
   */



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

  /*
   * @Override public int hashCode() { final int prime = 31; int result = 1; result = prime * result
   * + ((adminDescriptor == null) ? 0 : adminDescriptor.hashCode()); result = prime * result +
   * ((creator == null) ? 0 : creator.hashCode()); result = prime * result + ((defaultMetadata ==
   * null) ? 0 : defaultMetadata.hashCode()); result = prime * result + ((description == null) ? 0 :
   * description.hashCode()); result = prime * result + ((name == null) ? 0 : name.hashCode());
   * result = prime * result + ((objectId == null) ? 0 : objectId.hashCode()); result = prime *
   * result + ((responsibleAffiliations == null) ? 0 : responsibleAffiliations.hashCode()); result =
   * prime * result + ((state == null) ? 0 : state.hashCode()); result = prime * result + ((type ==
   * null) ? 0 : type.hashCode()); result = prime * result + ((validationPoints == null) ? 0 :
   * validationPoints.hashCode()); return result; }
   * 
   * @Override public boolean equals(Object obj) { if (this == obj) return true;
   * 
   * if (obj == null) return false;
   * 
   * if (getClass() != obj.getClass()) return false;
   * 
   * ContextVO other = (ContextVO) obj;
   * 
   * if (adminDescriptor == null) { if (other.adminDescriptor != null) return false; } else if
   * (other.adminDescriptor == null) return false; else if
   * (!adminDescriptor.equals(other.adminDescriptor)) { return false; }
   * 
   * if (creator == null) { if (other.creator != null) return false; } else if
   * (!creator.equals(other.creator)) return false;
   * 
   * if (defaultMetadata == null) { if (other.defaultMetadata != null) return false; } else if
   * (!defaultMetadata.equals(other.defaultMetadata)) return false;
   * 
   * if (description == null) { if (other.description != null) return false; } else if
   * (!description.equals(other.description)) return false;
   * 
   * if (name == null) { if (other.name != null) return false; } else if (!name.equals(other.name))
   * return false;
   * 
   * if (objectId == null) { if (other.objectId != null) return false; } else if
   * (!objectId.equals(other.objectId)) return false;
   * 
   * if (responsibleAffiliations == null) { if (other.responsibleAffiliations != null) return false;
   * } else if (other.responsibleAffiliations == null) return false; else if
   * (!responsibleAffiliations.containsAll(other.responsibleAffiliations) // ||
   * !other.responsibleAffiliations.containsAll(responsibleAffiliations)) { return false; }
   * 
   * if (state != other.state) return false;
   * 
   * if (type == null) { if (other.type != null) return false; } else if (!type.equals(other.type))
   * return false;
   * 
   * if (validationPoints == null) { if (other.validationPoints != null) return false; } else if
   * (other.validationPoints == null) return false; else if
   * (!validationPoints.containsAll(other.validationPoints) // ||
   * !other.validationPoints.containsAll(validationPoints)) { return false; }
   * 
   * return true; }
   */

}
