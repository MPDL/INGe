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
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
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
    return (null != this.reference);
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
  public ContextVO.State getState() {
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
  public void setState(ContextVO.State newVal) {
    this.state = newVal;
  }

  /**
   * Delivers the name of the collection, i. e. a unique name of the collection within the system.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Sets the name of the collection, i. e. a unique name of the collection within the system.
   *
   * @param newVal
   */
  public void setName(String newVal) {
    this.name = newVal;
  }

  /**
   * Delivers the refence object identifying this pubCollection.
   *
   * @see de.mpg.mpdl.inge.model.referenceobjects.ReferenceObject
   */
  public ContextRO getReference() {
    return this.reference;
  }

  /**
   * Sets the refence object identifying this pubCollection.
   *
   * @see de.mpg.mpdl.inge.model.referenceobjects.ReferenceObject
   * @param newVal
   */
  public void setReference(ContextRO newVal) {
    this.reference = newVal;
  }

  /**
   * Delivers the default metadata for items of the collection.
   */
  public MetadataSetVO getDefaultMetadata() {
    return this.defaultMetadata;
  }

  /**
   * Sets the default metadata for items of the collection.
   *
   * @param newVal
   */
  public void setDefaultMetadata(MdsPublicationVO newVal) {
    this.defaultMetadata = newVal;
  }

  /**
   * Delivers the validation points of this collection.
   */
  public java.util.List<ValidationPointVO> getValidationPoints() {
    return this.validationPoints;
  }

  /**
   * Delivers the reference of the creator of the collection.
   */
  public AccountUserRO getCreator() {
    return this.creator;
  }

  /**
   * Sets the reference of the creator of the collection.
   *
   * @param newVal
   */
  public void setCreator(AccountUserRO newVal) {
    this.creator = newVal;
  }

  /**
   * Delivers the list of affiliations which are responsible for this collection.
   */
  public java.util.List<AffiliationRO> getResponsibleAffiliations() {
    return this.responsibleAffiliations;
  }

  @JsonIgnore
  public List<AdminDescriptorVO> getAdminDescriptors() {
    return this.adminDescriptors;
  }

  public PublicationAdminDescriptorVO getAdminDescriptor() {
    if (!this.adminDescriptors.isEmpty() && this.adminDescriptors.get(0) instanceof PublicationAdminDescriptorVO) {
      return (PublicationAdminDescriptorVO) this.adminDescriptors.get(0);
    } else {
      return null;
    }
  }

  public void setAdminDescriptor(PublicationAdminDescriptorVO adminDescriptorVO) {
    if (!this.adminDescriptors.isEmpty()) {
      this.adminDescriptors.set(0, adminDescriptorVO);
    } else {
      this.adminDescriptors.add(adminDescriptorVO);
    }
  }

  public java.util.Date getCreationDate() {
    return this.creationDate;
  }

  public void setCreationDate(java.util.Date creationDate) {
    this.creationDate = creationDate;
  }

  public java.util.Date getLastModificationDate() {
    return this.lastModificationDate;
  }

  public void setLastModificationDate(java.util.Date lastModificationDate) {
    this.lastModificationDate = lastModificationDate;
  }

  public AccountUserRO getModifiedBy() {
    return this.modifiedBy;
  }

  public void setModifiedBy(AccountUserRO modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((null == this.adminDescriptors) ? 0 : this.adminDescriptors.hashCode());
    result = prime * result + ((null == this.creator) ? 0 : this.creator.hashCode());
    result = prime * result + ((null == this.defaultMetadata) ? 0 : this.defaultMetadata.hashCode());
    result = prime * result + ((null == this.description) ? 0 : this.description.hashCode());
    result = prime * result + ((null == this.name) ? 0 : this.name.hashCode());
    result = prime * result + ((null == this.reference) ? 0 : this.reference.hashCode());
    result = prime * result + ((null == this.responsibleAffiliations) ? 0 : this.responsibleAffiliations.hashCode());
    result = prime * result + ((null == this.state) ? 0 : this.state.hashCode());
    result = prime * result + ((null == this.type) ? 0 : this.type.hashCode());
    result = prime * result + ((null == this.validationPoints) ? 0 : this.validationPoints.hashCode());
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

    ContextVO other = (ContextVO) obj;

    if (null == this.adminDescriptors) {
      if (null != other.adminDescriptors)
        return false;
    } else if (null == other.adminDescriptors)
      return false;
    else if (!new HashSet<>(this.adminDescriptors).containsAll(other.adminDescriptors) //
        || !new HashSet<>(other.adminDescriptors).containsAll(this.adminDescriptors)) {
      return false;
    }

    if (null == this.creator) {
      if (null != other.creator)
        return false;
    } else if (!this.creator.equals(other.creator))
      return false;

    if (null == this.defaultMetadata) {
      if (null != other.defaultMetadata)
        return false;
    } else if (!this.defaultMetadata.equals(other.defaultMetadata))
      return false;

    if (null == this.description) {
      if (null != other.description)
        return false;
    } else if (!this.description.equals(other.description))
      return false;

    if (null == this.name) {
      if (null != other.name)
        return false;
    } else if (!this.name.equals(other.name))
      return false;

    if (null == this.reference) {
      if (null != other.reference)
        return false;
    } else if (!this.reference.equals(other.reference))
      return false;

    if (null == this.responsibleAffiliations) {
      if (null != other.responsibleAffiliations)
        return false;
    } else if (null == other.responsibleAffiliations)
      return false;
    else if (!new HashSet<>(this.responsibleAffiliations).containsAll(other.responsibleAffiliations) //
        || !new HashSet<>(other.responsibleAffiliations).containsAll(this.responsibleAffiliations)) {
      return false;
    }

    if (this.state != other.state)
      return false;

    if (null == this.type) {
      if (null != other.type)
        return false;
    } else if (!this.type.equals(other.type))
      return false;

    if (null == this.validationPoints) {
      if (null != other.validationPoints)
        return false;
    } else if (null == other.validationPoints)
      return false;
    else if (!new HashSet<>(this.validationPoints).containsAll(other.validationPoints) //
        || !new HashSet<>(other.validationPoints).containsAll(this.validationPoints)) {
      return false;
    }

    return true;
  }

}
