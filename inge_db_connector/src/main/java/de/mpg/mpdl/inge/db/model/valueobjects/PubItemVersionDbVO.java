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
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
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

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.db.model.hibernate.MdsPublicationVOJsonUserType;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;


/**
 * Item object which consists of descriptive metadata and may have one or more files associated.
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 21-Nov-2007 11:52:58
 */
@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_NULL)
@Entity(name = "PubItemVersionVO")
@Table(name = "item_version")
@Access(AccessType.FIELD)
@TypeDef(name = "MdsPublicationVOJsonUserType", typeClass = MdsPublicationVOJsonUserType.class)
public class PubItemVersionDbVO extends PubItemDbRO {

  /**
   * The version number of the referenced item. This attribute is optional.
   */

  @MapsId("objectId")
  @JoinColumn(name = "objectId", referencedColumnName = "objectId")
  @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE,
      CascadeType.PERSIST, CascadeType.REFRESH})
  @OnDelete(action = OnDeleteAction.CASCADE)
  PubItemObjectDbVO object;


  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @OrderColumn(name = "creationDate")
  private List<FileDbVO> files = new ArrayList<FileDbVO>();


  @Column
  @Type(type = "MdsPublicationVOJsonUserType")
  private MdsPublicationVO metadata = new MdsPublicationVO();


  public PubItemObjectDbVO getObject() {
    return object;
  }

  public void setObject(PubItemObjectDbVO object) {
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
  public PubItemVersionDbVO() {}

  /**
   * Copy constructor.
   * 
   * @author Thomas Diebaecker
   * @param other The instance to copy.
   */
  /*
   * public ItemVO(ItemVO other) { this.setCreationDate(other.getCreationDate());
   * this.setBaseUrl(other.getBaseUrl());
   * 
   * for (FileVO file : other.getFiles()) { this.getFiles().add((FileVO) file.clone()); }
   * 
   * this.setLockStatus(other.getLockStatus()); this.setPublicStatus(other.getPublicStatus());
   * this.setPublicStatusComment(other.getPublicStatusComment());
   * 
   * for (MetadataSetVO mds : other.getMetadataSets()) { this.getMetadataSets().add(mds.clone()); }
   * 
   * if (other.getOwner() != null) { this.setOwner((AccountUserRO) other.getOwner().clone()); }
   * 
   * this.setPid(other.getPid());
   * 
   * if (other.getContext() != null) { this.setContext((ContextRO) other.getContext().clone()); }
   * 
   * if (other.getContentModel() != null) { this.setContentModel(other.getContentModel()); }
   * 
   * try {
   * 
   * if (other.getVersion() != null) { this.setVersion((VersionRO) other.getVersion().clone()); }
   * 
   * 
   * if (other.getLatestVersion() != null) { this.setLatestVersion((PubItemVO)
   * other.getLatestVersion().clone()); }
   * 
   * if (other.getLatestRelease() != null) { this.setLatestRelease((PubItemVO)
   * other.getLatestRelease().clone()); }
   * 
   * for (ItemRelationVO relation : other.getRelations()) { this.getRelations().add((ItemRelationVO)
   * relation.clone()); } } catch (Exception cnse) { throw new RuntimeException(cnse); }
   * 
   * 
   * for (String localTag : other.getLocalTags()) { this.localTags.add(localTag); }
   * 
   * }
   */

  /**
   * {@inheritDoc}
   * 
   * @author Thomas Diebaecker
   */



  /*
   * @Override public boolean equals(Object obj) { if (this == obj) return true;
   * 
   * if (!super.equals(obj)) return false;
   * 
   * if (getClass() != obj.getClass()) return false;
   * 
   * ItemRO other = (ItemRO) obj;
   * 
   * if (lastMessage == null) { if (other.lastMessage != null) return false; } else if
   * (!lastMessage.equals(other.lastMessage)) return false;
   * 
   * if (modificationDate == null) { if (other.modificationDate != null) return false; } else if
   * (!modificationDate.equals(other.modificationDate)) return false;
   * 
   * if (modifiedByRO == null) { if (other.modifiedByRO != null) return false; } else if
   * (!modifiedByRO.equals(other.modifiedByRO)) return false;
   * 
   * if (pid == null) { if (other.pid != null) return false; } else if (!pid.equals(other.pid))
   * return false;
   * 
   * if (state != other.state) return false;
   * 
   * if (versionNumber != other.versionNumber) return false;
   * 
   * return true; }
   */



  /*
   * @Override
   * 
   * public void setObjectId(String objectId) { if (objectId != null && objectId.contains(":") &&
   * objectId.substring(objectId.indexOf(":") + 1).contains(":")) {
   * super.setObjectId(objectId.substring(0, objectId.lastIndexOf(":")));
   * setVersionNumber(Integer.parseInt(objectId.substring(objectId.lastIndexOf(":") + 1))); } else {
   * super.setObjectId(objectId); } }
   * 
   * 
   * public void setHref(String href) { if (href == null) { return; } if (href.contains("/")) { href
   * = href.substring(href.lastIndexOf("/") + 1); } this.setObjectId(href); }
   */

  // just a dummy, as href is needed for jibx-input only
  /*
   * public String getHref() { return null; }
   * 
   * 
   * @Override public Object clone() { return new ItemVO(this); }
   */



  /**
   * Helper method for JiBX transformations. This method helps JiBX to determine if this is a
   * 'create' or an 'update' transformation.
   * 
   * @return true, if this item already has a version object.
   */
  /*
   * boolean alreadyExistsInFramework() { return (this.version != null); }
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



  /*
   * @Override public int hashCode() { final int prime = 31; int result = 1; result = prime * result
   * + ((baseUrl == null) ? 0 : baseUrl.hashCode()); result = prime * result + ((contentModel ==
   * null) ? 0 : contentModel.hashCode()); result = prime * result + ((contextRO == null) ? 0 :
   * contextRO.hashCode()); result = prime * result + ((creationDate == null) ? 0 :
   * creationDate.hashCode()); result = prime * result + ((files == null) ? 0 : files.hashCode());
   * result = prime * result + ((latestRelease == null) ? 0 : latestRelease.hashCode()); result =
   * prime * result + ((latestVersion == null) ? 0 : latestVersion.hashCode()); result = prime *
   * result + ((localTags == null) ? 0 : localTags.hashCode()); result = prime * result +
   * ((lockStatus == null) ? 0 : lockStatus.hashCode()); result = prime * result + ((metadataSets ==
   * null) ? 0 : metadataSets.hashCode()); result = prime * result + ((owner == null) ? 0 :
   * owner.hashCode()); result = prime * result + ((pid == null) ? 0 : pid.hashCode()); result =
   * prime * result + ((publicStatus == null) ? 0 : publicStatus.hashCode()); result = prime *
   * result + ((publicStatusComment == null) ? 0 : publicStatusComment.hashCode()); result = prime *
   * result + ((relations == null) ? 0 : relations.hashCode()); result = prime * result + ((version
   * == null) ? 0 : version.hashCode()); result = prime * result + ((lastMessage == null) ? 0 :
   * lastMessage.hashCode()); result = prime * result + ((modificationDate == null) ? 0 :
   * modificationDate.hashCode()); result = prime * result + ((modifiedByRO == null) ? 0 :
   * modifiedByRO.hashCode()); result = prime * result + ((versionPid == null) ? 0 :
   * versionPid.hashCode()); result = prime * result + ((state == null) ? 0 : state.hashCode());
   * result = prime * result + versionNumber; return result; }
   * 
   * @Override public boolean equals(Object obj) { if (this == obj) return true;
   * 
   * if (obj == null) return false;
   * 
   * if (getClass() != obj.getClass()) return false;
   * 
   * ItemVO other = (ItemVO) obj;
   * 
   * if (baseUrl == null) { if (other.baseUrl != null) return false; } else if
   * (!baseUrl.equals(other.baseUrl)) return false;
   * 
   * if (contentModel == null) { if (other.contentModel != null) return false; } else if
   * (!contentModel.equals(other.contentModel)) return false;
   * 
   * if (contextRO == null) { if (other.contextRO != null) return false; } else if
   * (!contextRO.equals(other.contextRO)) return false;
   * 
   * if (creationDate == null) { if (other.creationDate != null) return false; } else if
   * (!creationDate.equals(other.creationDate)) return false;
   * 
   * if (files == null) { if (other.files != null) return false; } else if (other.files == null)
   * return false; else if (!files.containsAll(other.files) // || !other.files.containsAll(files)) {
   * return false; }
   * 
   * if (latestRelease == null) { if (other.latestRelease != null) return false; } else if
   * (!latestRelease.equals(other.latestRelease)) return false;
   * 
   * if (latestVersion == null) { if (other.latestVersion != null) return false; } else if
   * (!latestVersion.equals(other.latestVersion)) return false;
   * 
   * if (localTags == null) { if (other.localTags != null) return false; } else if (other.localTags
   * == null) return false; else if (!localTags.containsAll(other.localTags) // ||
   * !other.localTags.containsAll(localTags)) { return false; }
   * 
   * if (lockStatus != other.lockStatus) return false;
   * 
   * if (metadataSets == null) { if (other.metadataSets != null) return false; } else if
   * (other.metadataSets == null) return false; else if
   * (!metadataSets.containsAll(other.metadataSets) // ||
   * !other.metadataSets.containsAll(metadataSets)) { return false; }
   * 
   * if (owner == null) { if (other.owner != null) return false; } else if
   * (!owner.equals(other.owner)) return false;
   * 
   * if (pid == null) { if (other.pid != null) return false; } else if (!pid.equals(other.pid))
   * return false;
   * 
   * if (publicStatus != other.publicStatus) return false;
   * 
   * if (publicStatusComment == null) { if (other.publicStatusComment != null) return false; } else
   * if (!publicStatusComment.equals(other.publicStatusComment)) return false;
   * 
   * if (relations == null) { if (other.relations != null) return false; } else if (other.relations
   * == null) return false; else if (!relations.containsAll(other.relations) // ||
   * !other.relations.containsAll(relations)) { return false; }
   * 
   * if (version == null) { if (other.version != null) return false; } else if
   * (!version.equals(other.version)) return false;
   * 
   * return true; }
   */
}
