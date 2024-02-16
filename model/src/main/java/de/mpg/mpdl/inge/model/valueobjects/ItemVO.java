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
package de.mpg.mpdl.inge.model.valueobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.model.referenceobjects.AccountUserRO;
import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.interfaces.Searchable;

/**
 * Item object which consists of descriptive metadata and may have one or more files associated.
 *
 * @revised by MuJ: 28.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 21-Nov-2007 11:52:58
 */
@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_EMPTY)
public class ItemVO extends ValueObject implements Searchable {
  public enum ItemAction
  {
    RETRIEVE,
    SUBMIT,
    RELEASE,
    EXPORT
  }

  public enum State
  {
    PENDING,
    SUBMITTED,
    RELEASED,
    WITHDRAWN,
    IN_REVISION
  }

  public enum LockStatus
  {
    LOCKED,
    UNLOCKED
  }

  private AccountUserRO owner;
  private ContextRO contextRO;
  private Date creationDate;
  private ItemRO latestRelease = new ItemRO();
  private ItemRO latestVersion = new ItemRO();
  private ItemRO version = new ItemRO();
  private ItemVO.LockStatus lockStatus;
  private ItemVO.State publicStatus;
  private List<FileVO> files = new ArrayList<>();
  private List<MetadataSetVO> metadataSets = new ArrayList<>();
  private List<String> localTags = new ArrayList<>();
  @JsonIgnore
  private String baseUrl;
  private String contentModel;
  private String pid;
  @JsonIgnore
  private String publicStatusComment;

  /**
   * This list of relations is a quickfix and cannot be found in the model yet. The reason for this
   * is that the relations are delivered with every item retrieval from the framework, and they get
   * deleted when they are note provided on updates. TODO MuJ or BrP: model and implement correctly,
   * transforming too. Remove quickfix-VO ("ItemRelationVO").
   */
  private final List<ItemRelationVO> relations = new java.util.ArrayList<>();

  /**
   * Public constructor.
   *
   * @author Thomas Diebaecker
   */
  public ItemVO() {}

  /**
   * Copy constructor.
   *
   * @author Thomas Diebaecker
   * @param other The instance to copy.
   */
  public ItemVO(ItemVO other) {
    this.setCreationDate(other.getCreationDate());
    this.setBaseUrl(other.getBaseUrl());

    for (FileVO file : other.getFiles()) {
      this.getFiles().add((FileVO) file.clone());
    }

    this.setLockStatus(other.getLockStatus());
    this.setPublicStatus(other.getPublicStatus());
    this.setPublicStatusComment(other.getPublicStatusComment());

    for (MetadataSetVO mds : other.getMetadataSets()) {
      this.getMetadataSets().add(mds.clone());
    }

    if (other.getOwner() != null) {
      this.setOwner(other.getOwner().clone());
    }

    this.setPid(other.getPid());

    if (other.getContext() != null) {
      this.setContext(other.getContext().clone());
    }

    if (other.getContentModel() != null) {
      this.setContentModel(other.getContentModel());
    }

    if (other.getVersion() != null) {
      this.setVersion(other.getVersion().clone());
    }

    if (other.getLatestVersion() != null) {
      this.setLatestVersion(other.getLatestVersion().clone());
    }

    if (other.getLatestRelease() != null) {
      this.setLatestRelease(other.getLatestRelease().clone());
    }

    for (ItemRelationVO relation : other.getRelations()) {
      this.getRelations().add(relation.clone());
    }

    this.localTags.addAll(other.getLocalTags());
  }

  public String getPublicStatusComment() {
    return this.publicStatusComment;
  }

  public void setPublicStatusComment(String comment) {
    this.publicStatusComment = comment;
  }

  /**
   * Helper method for JiBX transformations. This method helps JiBX to determine if this is a
   * 'create' or an 'update' transformation.
   *
   * @return true, if this item already has a version object.
   */
  boolean alreadyExistsInFramework() {
    return (this.version != null);
  }

  /**
   * Helper method for JiBX transformations. This method helps JiBX to determine if a "components"
   * XML structure has to be created during marshalling.
   *
   * @return true, if the item contains one or more files.
   */
  boolean hasFiles() {
    return (!this.files.isEmpty());
  }

  /**
   * Helper method for JiBX transformations.
   */
  boolean hasPID() {
    return (this.pid != null);
  }

  /**
   * Helper method for JiBX transformations. This method helps JiBX to determine if a "relations"
   * XML structure has to be created during marshalling.
   */
  boolean hasRelations() {
    return (!this.relations.isEmpty());
  }

  /**
   * Delivers the list of files in this item.
   */
  public java.util.List<FileVO> getFiles() {
    return this.files;
  }

  public void setFiles(List<FileVO> files) {
    this.files = files;
  }

  /**
   * Delivers the metadata sets of the item.
   */
  @JsonIgnore
  public List<MetadataSetVO> getMetadataSets() {
    return this.metadataSets;
  }

  public void setMetadataSets(List<MetadataSetVO> metadataSets) {
    this.metadataSets = metadataSets;
  }

  /**
   * Delivers the owner of the item.
   */
  public AccountUserRO getOwner() {
    return this.owner;
  }

  /**
   * Delivers the persistent identifier of the item.
   */
  public String getPid() {
    return this.pid;
  }

  /**
   * Delivers the reference of the collection the item is contained in.
   */
  public ContextRO getContext() {
    return this.contextRO;
  }

  /**
   * Delivers the reference of the item.
   */
  public ItemRO getVersion() {
    return this.version;
  }

  /**
   * Delivers the list of relations in this item.
   */
  public java.util.List<ItemRelationVO> getRelations() {
    return this.relations;
  }

  /**
   * Sets the owner of the item.
   *
   * @param newVal
   */
  public void setOwner(AccountUserRO newVal) {
    this.owner = newVal;
  }

  /**
   * Sets the persistent identifier of the item.
   *
   * @param newVal
   */
  public void setPid(String newVal) {
    this.pid = newVal;
  }

  /**
   * Sets the reference of the collection the item is contained in.
   *
   * @param newVal
   */
  public void setContext(ContextRO newVal) {
    this.contextRO = newVal;
  }

  /**
   * Sets the reference of the item.
   *
   * @param newVal
   */
  public void setVersion(ItemRO newVal) {
    this.version = newVal;
  }

  /**
   * Delivers the date when the item was created.
   */
  public java.util.Date getCreationDate() {
    return this.creationDate;
  }

  /**
   * Delivers the lock status of the item.
   */
  public LockStatus getLockStatus() {
    return this.lockStatus;
  }

  /**
   * Sets the date when the item was created.
   *
   * @param newVal
   */
  public void setCreationDate(java.util.Date newVal) {
    this.creationDate = newVal;
  }

  /**
   * Sets the lock status of the item.
   *
   * @param newVal
   */
  public void setLockStatus(LockStatus newVal) {
    this.lockStatus = newVal;
  }

  /**
   * Delivers the comment which has to be given when an item is withdrawn.
   */
  public String getWithdrawalComment() {
    if (ItemVO.State.WITHDRAWN.equals(this.publicStatus)) {
      return this.publicStatusComment;
    }

    return null;
  }

  /**
   * Delivers the comment which has to be given when an item is withdrawn.
   *
   * @return The modification date as {@link Date}.
   */
  public Date getModificationDate() {
    if (this.version != null) {
      return this.version.getModificationDate();
    }

    return null;
  }

  public ItemRO getLatestVersion() {
    return this.latestVersion;
  }

  public void setLatestVersion(ItemRO latestVersion) {
    this.latestVersion = latestVersion;
  }

  public ItemRO getLatestRelease() {
    return this.latestRelease;
  }

  public void setLatestRelease(ItemRO latestRelease) {
    this.latestRelease = latestRelease;
  }

  public String getContentModel() {
    return this.contentModel;
  }

  public void setContentModel(String contentModel) {
    this.contentModel = contentModel;
  }

  public void setContentModelHref(String contentModelHref) {
    if (contentModelHref == null) {
      return;
    }

    if (contentModelHref.contains("/")) {
      contentModelHref = contentModelHref.substring(contentModelHref.lastIndexOf("/") + 1);
    }

    this.setContentModel(contentModelHref);
  }

  public ItemVO.State getPublicStatus() {
    return this.publicStatus;
  }

  public void setPublicStatus(ItemVO.State publicStatus) {
    this.publicStatus = publicStatus;
  }

  public java.util.List<String> getLocalTags() {
    return this.localTags;
  }

  public void setLocalTags(List<String> localTags) {
    this.localTags = localTags;
  }

  public String getBaseUrl() {
    return this.baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((baseUrl == null) ? 0 : baseUrl.hashCode());
    result = prime * result + ((contentModel == null) ? 0 : contentModel.hashCode());
    result = prime * result + ((contextRO == null) ? 0 : contextRO.hashCode());
    result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
    result = prime * result + ((files == null) ? 0 : files.hashCode());
    result = prime * result + ((latestRelease == null) ? 0 : latestRelease.hashCode());
    result = prime * result + ((latestVersion == null) ? 0 : latestVersion.hashCode());
    result = prime * result + ((localTags == null) ? 0 : localTags.hashCode());
    result = prime * result + ((lockStatus == null) ? 0 : lockStatus.hashCode());
    result = prime * result + ((metadataSets == null) ? 0 : metadataSets.hashCode());
    result = prime * result + ((owner == null) ? 0 : owner.hashCode());
    result = prime * result + ((pid == null) ? 0 : pid.hashCode());
    result = prime * result + ((publicStatus == null) ? 0 : publicStatus.hashCode());
    result = prime * result + ((publicStatusComment == null) ? 0 : publicStatusComment.hashCode());
    result = prime * result + ((relations == null) ? 0 : relations.hashCode());
    result = prime * result + ((version == null) ? 0 : version.hashCode());
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

    ItemVO other = (ItemVO) obj;

    if (baseUrl == null) {
      if (other.baseUrl != null)
        return false;
    } else if (!baseUrl.equals(other.baseUrl))
      return false;

    if (contentModel == null) {
      if (other.contentModel != null)
        return false;
    } else if (!contentModel.equals(other.contentModel))
      return false;

    if (contextRO == null) {
      if (other.contextRO != null)
        return false;
    } else if (!contextRO.equals(other.contextRO))
      return false;

    if (creationDate == null) {
      if (other.creationDate != null)
        return false;
    } else if (!creationDate.equals(other.creationDate))
      return false;

    if (files == null) {
      if (other.files != null)
        return false;
    } else if (other.files == null)
      return false;
    else if (!new HashSet<>(files).containsAll(other.files) //
        || !new HashSet<>(other.files).containsAll(files)) {
      return false;
    }

    if (latestRelease == null) {
      if (other.latestRelease != null)
        return false;
    } else if (!latestRelease.equals(other.latestRelease))
      return false;

    if (latestVersion == null) {
      if (other.latestVersion != null)
        return false;
    } else if (!latestVersion.equals(other.latestVersion))
      return false;

    if (localTags == null) {
      if (other.localTags != null)
        return false;
    } else if (other.localTags == null)
      return false;
    else if (!new HashSet<>(localTags).containsAll(other.localTags) //
        || !new HashSet<>(other.localTags).containsAll(localTags)) {
      return false;
    }

    if (lockStatus != other.lockStatus)
      return false;

    if (metadataSets == null) {
      if (other.metadataSets != null)
        return false;
    } else if (other.metadataSets == null)
      return false;
    else if (!new HashSet<>(metadataSets).containsAll(other.metadataSets) //
        || !new HashSet<>(other.metadataSets).containsAll(metadataSets)) {
      return false;
    }

    if (owner == null) {
      if (other.owner != null)
        return false;
    } else if (!owner.equals(other.owner))
      return false;

    if (pid == null) {
      if (other.pid != null)
        return false;
    } else if (!pid.equals(other.pid))
      return false;

    if (publicStatus != other.publicStatus)
      return false;

    if (publicStatusComment == null) {
      if (other.publicStatusComment != null)
        return false;
    } else if (!publicStatusComment.equals(other.publicStatusComment))
      return false;

    if (relations == null) {
      if (other.relations != null)
        return false;
    } else if (other.relations == null)
      return false;
    else if (!new HashSet<>(relations).containsAll(other.relations) //
        || !new HashSet<>(other.relations).containsAll(relations)) {
      return false;
    }

    if (version == null) {
      if (other.version != null)
        return false;
    } else if (!version.equals(other.version))
      return false;

    return true;
  }
}
