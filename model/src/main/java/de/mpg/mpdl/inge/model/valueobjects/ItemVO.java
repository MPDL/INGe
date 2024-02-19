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
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
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
      this.getFiles().add(file.clone());
    }

    this.setLockStatus(other.getLockStatus());
    this.setPublicStatus(other.getPublicStatus());
    this.setPublicStatusComment(other.getPublicStatusComment());

    for (MetadataSetVO mds : other.getMetadataSets()) {
      this.getMetadataSets().add(mds.clone());
    }

    if (null != other.getOwner()) {
      this.setOwner(other.getOwner().clone());
    }

    this.setPid(other.getPid());

    if (null != other.getContext()) {
      this.setContext(other.getContext().clone());
    }

    if (null != other.getContentModel()) {
      this.setContentModel(other.getContentModel());
    }

    if (null != other.getVersion()) {
      this.setVersion(other.getVersion().clone());
    }

    if (null != other.getLatestVersion()) {
      this.setLatestVersion(other.getLatestVersion().clone());
    }

    if (null != other.getLatestRelease()) {
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
    return (null != this.version);
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
    return (null != this.pid);
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
    if (null != this.version) {
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
    if (null == contentModelHref) {
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
    result = prime * result + ((null == this.baseUrl) ? 0 : this.baseUrl.hashCode());
    result = prime * result + ((null == this.contentModel) ? 0 : this.contentModel.hashCode());
    result = prime * result + ((null == this.contextRO) ? 0 : this.contextRO.hashCode());
    result = prime * result + ((null == this.creationDate) ? 0 : this.creationDate.hashCode());
    result = prime * result + ((null == this.files) ? 0 : this.files.hashCode());
    result = prime * result + ((null == this.latestRelease) ? 0 : this.latestRelease.hashCode());
    result = prime * result + ((null == this.latestVersion) ? 0 : this.latestVersion.hashCode());
    result = prime * result + ((null == this.localTags) ? 0 : this.localTags.hashCode());
    result = prime * result + ((null == this.lockStatus) ? 0 : this.lockStatus.hashCode());
    result = prime * result + ((null == this.metadataSets) ? 0 : this.metadataSets.hashCode());
    result = prime * result + ((null == this.owner) ? 0 : this.owner.hashCode());
    result = prime * result + ((null == this.pid) ? 0 : this.pid.hashCode());
    result = prime * result + ((null == this.publicStatus) ? 0 : this.publicStatus.hashCode());
    result = prime * result + ((null == this.publicStatusComment) ? 0 : this.publicStatusComment.hashCode());
    result = prime * result + ((null == this.relations) ? 0 : this.relations.hashCode());
    result = prime * result + ((null == this.version) ? 0 : this.version.hashCode());
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

    ItemVO other = (ItemVO) obj;

    if (null == this.baseUrl) {
      if (null != other.baseUrl)
        return false;
    } else if (!this.baseUrl.equals(other.baseUrl))
      return false;

    if (null == this.contentModel) {
      if (null != other.contentModel)
        return false;
    } else if (!this.contentModel.equals(other.contentModel))
      return false;

    if (null == this.contextRO) {
      if (null != other.contextRO)
        return false;
    } else if (!this.contextRO.equals(other.contextRO))
      return false;

    if (null == this.creationDate) {
      if (null != other.creationDate)
        return false;
    } else if (!this.creationDate.equals(other.creationDate))
      return false;

    if (null == this.files) {
      if (null != other.files)
        return false;
    } else if (null == other.files)
      return false;
    else if (!new HashSet<>(this.files).containsAll(other.files) //
        || !new HashSet<>(other.files).containsAll(this.files)) {
      return false;
    }

    if (null == this.latestRelease) {
      if (null != other.latestRelease)
        return false;
    } else if (!this.latestRelease.equals(other.latestRelease))
      return false;

    if (null == this.latestVersion) {
      if (null != other.latestVersion)
        return false;
    } else if (!this.latestVersion.equals(other.latestVersion))
      return false;

    if (null == this.localTags) {
      if (null != other.localTags)
        return false;
    } else if (null == other.localTags)
      return false;
    else if (!new HashSet<>(this.localTags).containsAll(other.localTags) //
        || !new HashSet<>(other.localTags).containsAll(this.localTags)) {
      return false;
    }

    if (this.lockStatus != other.lockStatus)
      return false;

    if (null == this.metadataSets) {
      if (null != other.metadataSets)
        return false;
    } else if (null == other.metadataSets)
      return false;
    else if (!new HashSet<>(this.metadataSets).containsAll(other.metadataSets) //
        || !new HashSet<>(other.metadataSets).containsAll(this.metadataSets)) {
      return false;
    }

    if (null == this.owner) {
      if (null != other.owner)
        return false;
    } else if (!this.owner.equals(other.owner))
      return false;

    if (null == this.pid) {
      if (null != other.pid)
        return false;
    } else if (!this.pid.equals(other.pid))
      return false;

    if (this.publicStatus != other.publicStatus)
      return false;

    if (null == this.publicStatusComment) {
      if (null != other.publicStatusComment)
        return false;
    } else if (!this.publicStatusComment.equals(other.publicStatusComment))
      return false;

    if (null == this.relations) {
      if (null != other.relations)
        return false;
    } else if (null == other.relations)
      return false;
    else if (!new HashSet<>(this.relations).containsAll(other.relations) //
        || !new HashSet<>(other.relations).containsAll(this.relations)) {
      return false;
    }

    if (null == this.version) {
      if (null != other.version)
        return false;
    } else if (!this.version.equals(other.version))
      return false;

    return true;
  }
}
