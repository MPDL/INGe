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

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * The class for AccountUser references.
 * 
 * @revised by MuJ: 27.08.2007
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @updated 21-Nov-2007 12:08:27
 */
@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_EMPTY)
@MappedSuperclass
public class BasicDbRO implements Serializable {
  @Id
  private String objectId;

  @Column(columnDefinition = "TEXT")
  private String name;

  private java.util.Date lastModificationDate;

  private java.util.Date creationDate;



  @Embedded
  @AttributeOverrides({@AttributeOverride(name = "objectId", column = @Column(name = "creator_objectId")),
      @AttributeOverride(name = "name", column = @Column(name = "creator_name"))})
  private AccountUserDbRO creator;

  @Embedded
  @AttributeOverrides({@AttributeOverride(name = "objectId", column = @Column(name = "modifier_objectId")),
      @AttributeOverride(name = "name", column = @Column(name = "modifier_name"))})
  private AccountUserDbRO modifier;

  /**
   * Creates a new instance.
   */
  public BasicDbRO() {
    super();
  }



  public String getObjectId() {
    return objectId;
  }



  public void setObjectId(String objectId) {
    this.objectId = objectId;
  }



  public String getName() {
    return name;
  }



  public void setName(String name) {
    this.name = name;
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



  public AccountUserDbRO getCreator() {
    return creator;
  }



  public void setCreator(AccountUserDbRO creator) {
    this.creator = creator;
  }



  public AccountUserDbRO getModifier() {
    return modifier;
  }



  public void setModifier(AccountUserDbRO modifier) {
    this.modifier = modifier;
  }



  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
    result = prime * result + ((creator == null) ? 0 : creator.hashCode());
    result = prime * result + ((lastModificationDate == null) ? 0 : lastModificationDate.hashCode());
    result = prime * result + ((modifier == null) ? 0 : modifier.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((objectId == null) ? 0 : objectId.hashCode());
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
    BasicDbRO other = (BasicDbRO) obj;
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
    if (lastModificationDate == null) {
      if (other.lastModificationDate != null)
        return false;
    } else if (!lastModificationDate.equals(other.lastModificationDate))
      return false;
    if (modifier == null) {
      if (other.modifier != null)
        return false;
    } else if (!modifier.equals(other.modifier))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (objectId == null) {
      if (other.objectId != null)
        return false;
    } else if (!objectId.equals(other.objectId))
      return false;
    return true;
  }



}
