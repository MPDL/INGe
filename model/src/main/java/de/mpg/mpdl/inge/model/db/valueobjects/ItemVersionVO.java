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
package de.mpg.mpdl.inge.model.db.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.model.db.hibernate.MdsPublicationVOJsonUserType;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;


/**
 * Item object which consists of descriptive metadata and may have one or more files associated.
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 21-Nov-2007 11:52:58
 */
@JsonInclude(value = Include.NON_EMPTY)
@Entity(name = "PubItemVersionVO")
@Table(name = "item_version")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "item")
@Access(AccessType.FIELD)
@TypeDef(name = "MdsPublicationVOJsonUserType", typeClass = MdsPublicationVOJsonUserType.class)
public class ItemVersionVO extends ItemVersionRO implements Serializable {

  /**
   * The version number of the referenced item. This attribute is optional.
   */


  /**
   * The message of the last action event of this item.
   */
  @Column(columnDefinition = "TEXT")
  private String message;

  @MapsId("objectId")
  @JoinColumn(name = "objectId", referencedColumnName = "objectId")
  @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @OnDelete(action = OnDeleteAction.CASCADE)
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "item")
  ItemRootVO object = new ItemRootVO();

  @Column
  @Type(type = "MdsPublicationVOJsonUserType")
  private MdsPublicationVO metadata = new MdsPublicationVO();

  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @OrderColumn(name = "creationDate")
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "item")
  private List<FileDbVO> files = new ArrayList<FileDbVO>();

  public ItemRootVO getObject() {
    return object;
  }

  public void setObject(ItemRootVO object) {
    this.object = object;
  }

  public MdsPublicationVO getMetadata() {
    return metadata;
  }

  public void setMetadata(MdsPublicationVO metadata) {
    this.metadata = metadata;
  }

  public void setFiles(List<FileDbVO> files) {
    this.files = files;
  }

  /**
   * Public constructor.
   * 
   * @author Thomas Diebaecker
   */
  public ItemVersionVO() {}


  public ItemVersionVO(ItemVersionVO other) {
    MapperFactory.getDozerMapper().map(other, this);

  }

  /**
   * {@inheritDoc}
   * 
   * @author Thomas Diebaecker
   */

  /**
   * Helper method for JiBX transformations. This method helps JiBX to determine if a "components"
   * XML structure has to be created during marshalling.
   * 
   * @return true, if the item contains one or more files.
   */
  boolean hasFiles() {
    return (this.files.size() >= 1);
  }


  /**
   * Delivers the list of files in this item.
   */
  public java.util.List<FileDbVO> getFiles() {
    return this.files;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String lastMessage) {
    this.message = lastMessage;
  }

  @JsonIgnore
  public String getLastMessageForXml() {
    if (message == null) {
      return "";
    } else {
      return message;
    }
  }
}
