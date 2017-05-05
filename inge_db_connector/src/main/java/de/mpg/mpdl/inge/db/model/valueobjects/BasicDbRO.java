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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The class for AccountUser references.
 * 
 * @revised by MuJ: 27.08.2007
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @updated 21-Nov-2007 12:08:27
 */
@JsonInclude(value = Include.NON_NULL)
@MappedSuperclass
public class BasicDbRO implements Cloneable {
  /**
   * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
   * 'de.mpg.mpdl.inge.model.valueobjects.ItemVO; local class incompatible: stream classdesc
   * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286'
   * that occur after JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to
   * be compiled twice for testing (once for the Application Server, once for the local test).
   * 
   * @author Johannes Mueller
   */
  private static final long serialVersionUID = 1L;


  @Id
  private String objectId;

  @Column(columnDefinition = "TEXT")
  private String name;

  private java.util.Date creationDate;

  private java.util.Date lastModificationDate;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "objectId", column = @Column(name = "owner_objectId")),
      @AttributeOverride(name = "name", column = @Column(name = "owner_name"))})
  private AccountUserDbRO creator;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "objectId", column = @Column(name = "modifier_objectId")),
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



}
