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

package de.mpg.mpdl.inge.model.valueobjects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.model.referenceobjects.AccountUserRO;
import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.valueobjects.interfaces.Searchable;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
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
@JsonInclude(value = Include.NON_EMPTY)
public class ContextVO extends ValueObject implements Searchable {
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

  /**
   * The reference object identifying this pubCollection.
   */
  private ContextRO reference;
  /**
   * A unique name of the collection within the system.
   */
  private String name;

  private String type;
  /**
   * The state of the PubCollection.
   */
  private ContextVO.State state;
  /**
   * A short description of the collection and the collection policy.
   */
  private String description;
  /**
   * The default metadata.
   */
  private MetadataSetVO defaultMetadata;
  /**
   * The creator of the collection.
   */
  private AccountUserRO creator;
  private java.util.Date creationDate;
  private java.util.Date lastModificationDate;
  private AccountUserRO modifiedBy;
  /**
   * The set union of validation points for items in this collection.
   */
  private final java.util.List<ValidationPointVO> validationPoints = new java.util.ArrayList<>();
  /**
   * The list of responsible affiliations for this collection.
   */
  private final java.util.List<AffiliationRO> responsibleAffiliations = new java.util.ArrayList<>();

  private final List<AdminDescriptorVO> adminDescriptors = new ArrayList<>();

  /**
   * Helper method for JiBX transformations. This method helps JiBX to determine if this is a
   * 'create' or an 'update' transformation.
   */
  public boolean alreadyExistsInFramework() {
    return (this.reference != null);
  }

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
  public ContextVO.State getState() {
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
  public void setState(ContextVO.State newVal) {
    state = newVal;
  }

  /**
   * Delivers the name of the collection, i. e. a unique name of the collection within the system.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the collection, i. e. a unique name of the collection within the system.
   *
   * @param newVal
   */
  public void setName(String newVal) {
    name = newVal;
  }

  /**
   * Delivers the refence object identifying this pubCollection.
   *
   * @see de.mpg.mpdl.inge.model.referenceobjects.ReferenceObject
   */
  public ContextRO getReference() {
    return reference;
  }

  /**
   * Sets the refence object identifying this pubCollection.
   *
   * @see de.mpg.mpdl.inge.model.referenceobjects.ReferenceObject
   * @param newVal
   */
  public void setReference(ContextRO newVal) {
    reference = newVal;
  }

  /**
   * Delivers the default metadata for items of the collection.
   */
  public MetadataSetVO getDefaultMetadata() {
    return defaultMetadata;
  }

  /**
   * Sets the default metadata for items of the collection.
   *
   * @param newVal
   */
  public void setDefaultMetadata(MdsPublicationVO newVal) {
    defaultMetadata = newVal;
  }

  /**
   * Delivers the validation points of this collection.
   */
  public java.util.List<ValidationPointVO> getValidationPoints() {
    return validationPoints;
  }

  /**
   * Delivers the reference of the creator of the collection.
   */
  public AccountUserRO getCreator() {
    return creator;
  }

  /**
   * Sets the reference of the creator of the collection.
   *
   * @param newVal
   */
  public void setCreator(AccountUserRO newVal) {
    creator = newVal;
  }

  /**
   * Delivers the list of affiliations which are responsible for this collection.
   */
  public java.util.List<AffiliationRO> getResponsibleAffiliations() {
    return responsibleAffiliations;
  }

  @JsonIgnore
  public List<AdminDescriptorVO> getAdminDescriptors() {
    return adminDescriptors;
  }

  public PublicationAdminDescriptorVO getAdminDescriptor() {
    if (!getAdminDescriptors().isEmpty() && getAdminDescriptors().get(0) instanceof PublicationAdminDescriptorVO) {
      return (PublicationAdminDescriptorVO) getAdminDescriptors().get(0);
    } else {
      return null;
    }
  }

  public void setAdminDescriptor(PublicationAdminDescriptorVO adminDescriptorVO) {
    if (!getAdminDescriptors().isEmpty()) {
      getAdminDescriptors().set(0, adminDescriptorVO);
    } else {
      getAdminDescriptors().add(adminDescriptorVO);
    }
  }

  public java.util.Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(java.util.Date creationDate) {
    this.creationDate = creationDate;
  }

  public java.util.Date getLastModificationDate() {
    return lastModificationDate;
  }

  public void setLastModificationDate(java.util.Date lastModificationDate) {
    this.lastModificationDate = lastModificationDate;
  }

  public AccountUserRO getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(AccountUserRO modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((adminDescriptors == null) ? 0 : adminDescriptors.hashCode());
    result = prime * result + ((creator == null) ? 0 : creator.hashCode());
    result = prime * result + ((defaultMetadata == null) ? 0 : defaultMetadata.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((reference == null) ? 0 : reference.hashCode());
    result = prime * result + ((responsibleAffiliations == null) ? 0 : responsibleAffiliations.hashCode());
    result = prime * result + ((state == null) ? 0 : state.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((validationPoints == null) ? 0 : validationPoints.hashCode());
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

    ContextVO other = (ContextVO) obj;

    if (adminDescriptors == null) {
      if (other.adminDescriptors != null)
        return false;
    } else if (other.adminDescriptors == null)
      return false;
    else if (!new HashSet<>(adminDescriptors).containsAll(other.adminDescriptors) //
        || !new HashSet<>(other.adminDescriptors).containsAll(adminDescriptors)) {
      return false;
    }

    if (creator == null) {
      if (other.creator != null)
        return false;
    } else if (!creator.equals(other.creator))
      return false;

    if (defaultMetadata == null) {
      if (other.defaultMetadata != null)
        return false;
    } else if (!defaultMetadata.equals(other.defaultMetadata))
      return false;

    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;

    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;

    if (reference == null) {
      if (other.reference != null)
        return false;
    } else if (!reference.equals(other.reference))
      return false;

    if (responsibleAffiliations == null) {
      if (other.responsibleAffiliations != null)
        return false;
    } else if (other.responsibleAffiliations == null)
      return false;
    else if (!new HashSet<>(responsibleAffiliations).containsAll(other.responsibleAffiliations) //
        || !new HashSet<>(other.responsibleAffiliations).containsAll(responsibleAffiliations)) {
      return false;
    }

    if (state != other.state)
      return false;

    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;

    if (validationPoints == null) {
      if (other.validationPoints != null)
        return false;
    } else if (other.validationPoints == null)
      return false;
    else if (!new HashSet<>(validationPoints).containsAll(other.validationPoints) //
        || !new HashSet<>(other.validationPoints).containsAll(validationPoints)) {
      return false;
    }

    return true;
  }

}
