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
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
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
  public BasicDbRO() {}



  public String getObjectId() {
    return this.objectId;
  }



  public void setObjectId(String objectId) {
    this.objectId = objectId;
  }



  public String getName() {
    return this.name;
  }



  public void setName(String name) {
    this.name = name;
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



  public AccountUserDbRO getCreator() {
    return this.creator;
  }



  public void setCreator(AccountUserDbRO creator) {
    this.creator = creator;
  }



  public AccountUserDbRO getModifier() {
    return this.modifier;
  }



  public void setModifier(AccountUserDbRO modifier) {
    this.modifier = modifier;
  }



  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((null == this.creationDate) ? 0 : this.creationDate.hashCode());
    result = prime * result + ((null == this.creator) ? 0 : this.creator.hashCode());
    result = prime * result + ((null == this.lastModificationDate) ? 0 : this.lastModificationDate.hashCode());
    result = prime * result + ((null == this.modifier) ? 0 : this.modifier.hashCode());
    result = prime * result + ((null == this.name) ? 0 : this.name.hashCode());
    result = prime * result + ((null == this.objectId) ? 0 : this.objectId.hashCode());
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
    BasicDbRO other = (BasicDbRO) obj;
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
    if (null == this.lastModificationDate) {
      if (null != other.lastModificationDate)
        return false;
    } else if (!this.lastModificationDate.equals(other.lastModificationDate))
      return false;
    if (null == this.modifier) {
      if (null != other.modifier)
        return false;
    } else if (!this.modifier.equals(other.modifier))
      return false;
    if (null == this.name) {
      if (null != other.name)
        return false;
    } else if (!this.name.equals(other.name))
      return false;
    if (null == this.objectId) {
      if (null != other.objectId)
        return false;
    } else if (!this.objectId.equals(other.objectId))
      return false;
    return true;
  }



}
