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
import com.fasterxml.jackson.annotation.JsonProperty;

import de.mpg.mpdl.inge.model.referenceobjects.AccountUserRO;
import de.mpg.mpdl.inge.model.referenceobjects.FileRO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * A file that is contained in an item.
 *
 * @revised by MuJ: 28.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 21-Nov-2007 12:05:47
 */
@SuppressWarnings("serial")
public class FileVO extends ValueObject implements Cloneable {
  /**
   * The possible visibility of a file.
   *
   * @updated 21-Nov-2007 12:05:47
   */
  public enum Visibility
  {
    PUBLIC,
    PRIVATE,
    AUDIENCE
  }

  /**
   * The possible storage of a file.
   */
  public enum Storage
  {
    INTERNAL_MANAGED,
    EXTERNAL_URL,
    EXTERNAL_MANAGED
  }
  /**
   * The possible storage of a file.
   */
  public enum ChecksumAlgorithm
  {
    MD5,
    SHA1
  }

  private FileRO reference;

  /**
   * The name of the file including the extension.
   */
  private String name;

  /**
   * The visibility of the file for users of the system.
   */
  private FileVO.Visibility visibility;

  /**
   * A short description of the file.
   */
  private String description;

  @JsonProperty("createdBy")
  private AccountUserRO createdByRO;

  /**
   * This date gives the moment in time the file was created.
   */
  private java.util.Date creationDate;

  /**
   * This date is updated whenever the file is stored.
   */
  private java.util.Date lastModificationDate;

  /**
   * The persistent identifier of the file if the item is released.
   */
  private String pid;

  /**
   * A reference to the content of the file.
   */
  private String content;

  /**
   * A reference to the storage attribute of the file.
   */
  private FileVO.Storage storage;

  /**
   * The content type of the file.
   */
  private String contentCategory;

  private String checksum;

  private ChecksumAlgorithm checksumAlgorithm;

  /**
   * The size of the file in Bytes. Has to be zero if no content is given.
   */
  // private long size;

  /**
   * The MIME-type of this format. Valid values see http://www.iana.org/assignments/media-types/
   */
  private String mimeType;

  private List<MetadataSetVO> metadataSets = new ArrayList<>();


  // Internal reference to the files content
  @JsonIgnore
  private String localFileIdentifier;

  /**
   * Public contructor.
   *
   * @author Thomas Diebaecker
   */
  public FileVO() {}

  /**
   * Copy constructor.
   *
   * @author Thomas Diebaecker
   * @param other The instance to copy.
   */
  public FileVO(FileVO other) {
    this.content = other.content;
    this.contentCategory = other.contentCategory;
    this.creationDate = other.creationDate;
    this.createdByRO = other.createdByRO;
    this.description = other.description;
    this.lastModificationDate = other.lastModificationDate;
    this.mimeType = other.mimeType;
    this.name = other.name;
    this.pid = other.pid;
    this.reference = other.reference;
    // size = other.size;
    this.visibility = other.visibility;
    this.storage = other.storage;
    this.metadataSets = other.metadataSets;
    this.checksum = other.checksum;
    this.checksumAlgorithm = other.checksumAlgorithm;
  }

  public FileVO clone()
  {
    return new FileVO(this);
  }

  /**
   * Helper method for JiBX transformations. This method helps JiBX to determine if this is a
   * 'create' or an 'update' transformation. (visibility restricted to package)
   *
   * @return boolean true if this file already exists in the framework (creation date is already
   *         set)
   */
  boolean alreadyExistsInFramework() {
    return (null != this.creationDate);
  }

  @JsonProperty("metadata")
  public MdsFileVO getDefaultMetadata() {
    if (!this.metadataSets.isEmpty() && this.metadataSets.get(0) instanceof MdsFileVO) {
      return (MdsFileVO) this.metadataSets.get(0);
    } else {
      return null;
    }
  }

  @JsonProperty("metadata")
  public void setDefaultMetadata(MdsFileVO mdsFileVO) {
    if (this.metadataSets.isEmpty()) {
      this.metadataSets.add(mdsFileVO);
    } else {
      this.metadataSets.set(0, mdsFileVO);
    }
  }

  /**
   * Delivers the files' reference.
   *
   * @see de.mpg.mpdl.inge.model.referenceobjects.ReferenceObject
   */
  public FileRO getReference() {
    return this.reference;
  }

  /**
   * Sets the files' reference.
   *
   * @see de.mpg.mpdl.inge.model.referenceobjects.ReferenceObject
   *
   * @param newVal
   */
  public void setReference(FileRO newVal) {
    this.reference = newVal;
  }

  /**
   * Delivers the name of the file including the extension.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Sets the name of the file including the extension.
   *
   * @param newVal
   */
  public void setName(String newVal) {
    this.name = newVal;
  }

  /**
   * Delivers the persistent identifier of the file.
   */
  public String getPid() {
    return this.pid;
  }

  /**
   * remove "hdl:" if possible (needed for URLs including a handle-resolver)
   */
  @JsonIgnore
  public String getPidWithoutPrefix() {
    if (this.pid.startsWith(PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_SHORT))) {
      return this.pid.substring(PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_SHORT).length());
    } else {
      return this.pid;
    }
  }

  /**
   * Sets the persistent identifier of the file.
   *
   * @param newVal
   */
  public void setPid(String newVal) {
    this.pid = newVal;
  }

  /**
   * Delivers the description of the file, i. e. a short description of the file.
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Sets the description of the file, i. e. a short description of the file.
   *
   * @param newVal
   */
  public void setDescription(String newVal) {
    this.description = newVal;
  }

  public AccountUserRO getCreatedByRO() {
    return this.createdByRO;
  }

  public void setCreatedByRO(AccountUserRO createdByRO) {
    this.createdByRO = createdByRO;
  }

  /**
   * Delivers a reference to the content of the file, i. e. to the data of the file.
   */
  public String getContent() {
    return this.content;
  }

  /**
   * Sets a reference to the content of the file, i. e. to the data of the file.
   *
   * @param newVal
   */
  public void setContent(String newVal) {
    this.content = newVal;
  }

  /**
   * Delivers the content type of the file.
   */
  public String getContentCategory() {
    return this.contentCategory;
  }

  /**
   * Sets the content type of the file.
   *
   * @param newVal
   */
  public void setContentCategory(String newVal) {
    this.contentCategory = newVal;
  }

  /**
   * Delivers the visibility of the file.
   */
  public FileVO.Visibility getVisibility() {
    return this.visibility;
  }

  /**
   * Sets the visibility of the file.
   *
   * @param newVal
   */
  public void setVisibility(FileVO.Visibility newVal) {
    this.visibility = newVal;
  }

  /**
   * Delivers the MIME-type of the file. For valid values see
   * http://www.iana.org/assignments/media-types/
   */
  public String getMimeType() {
    return this.mimeType;
  }

  /**
   * Sets the MIME-type of the file. For valid values see
   * http://www.iana.org/assignments/media-types/
   *
   * @param newVal
   */
  public void setMimeType(String newVal) {
    this.mimeType = newVal;
  }

  /**
   * Delivers the creation date of the file.
   */
  public java.util.Date getCreationDate() {
    return this.creationDate;
  }

  /**
   * Sets the creation date of the file.
   *
   * @param newVal
   */
  public void setCreationDate(java.util.Date newVal) {
    this.creationDate = newVal;
  }

  /**
   * Delivers the date of the last modification of the file.
   */
  public java.util.Date getLastModificationDate() {
    return this.lastModificationDate;
  }

  /**
   * Sets the date of the last modification of the file.
   *
   * @param newVal
   */
  public void setLastModificationDate(java.util.Date newVal) {
    this.lastModificationDate = newVal;
  }

  /**
   * Delivers the value of the contentCategory Enum as a String. If the Enum is not set, an empty
   * String is returned.
   */
  @JsonIgnore
  public String getContentCategoryString() {
    if (null == this.contentCategory) {
      return "";
    }
    return this.contentCategory;
  }

  /**
   * Sets the value of the contentCategory Enum by a String.
   *
   * @param newValString
   */
  @JsonIgnore
  public void setContentCategoryString(String newValString) {
    this.contentCategory = newValString;
  }

  /**
   * Delivers the value of the visibility Enum as a String. If the enum is not set, an empty String
   * is returned.
   */
  @JsonIgnore
  public String getVisibilityString() {
    if (null == this.visibility || null == this.visibility.toString()) {
      return "";
    }
    return this.visibility.toString();
  }

  /**
   * Sets the value of the visibility Enum by a String.
   *
   * @param newValString
   */
  @JsonIgnore
  public void setVisibilityString(String newValString) {
    if (null == newValString || newValString.isEmpty()) {
      this.visibility = null;
    } else {
      FileVO.Visibility newVal = FileVO.Visibility.valueOf(newValString);
      this.visibility = newVal;
    }
  }

  public FileVO.Storage getStorage() {
    return this.storage;
  }

  public void setStorage(FileVO.Storage storage) {
    this.storage = storage;
  }

  @JsonIgnore
  public String getStorageString() {
    if (null == this.storage || null == this.storage.toString()) {
      return "";
    }
    return this.storage.toString();
  }

  @JsonIgnore
  public void setStorageString(String newValString) {
    if (null == newValString || newValString.isEmpty()) {
      this.storage = null;
    } else {
      FileVO.Storage newVal = FileVO.Storage.valueOf(newValString);
      this.storage = newVal;
    }
  }

  @JsonIgnore
  public List<MetadataSetVO> getMetadataSets() {
    return this.metadataSets;
  }

  public String getChecksum() {
    return this.checksum;
  }

  public void setChecksum(String checksum) {
    this.checksum = checksum;
  }

  public ChecksumAlgorithm getChecksumAlgorithm() {
    return this.checksumAlgorithm;
  }

  public void setChecksumAlgorithm(ChecksumAlgorithm checksumAlgorithm) {
    this.checksumAlgorithm = checksumAlgorithm;
  }

  public String getLocalFileIdentifier() {
    return this.localFileIdentifier;
  }

  public void setLocalFileIdentifier(String localFileIdentifier) {
    this.localFileIdentifier = localFileIdentifier;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((null == this.checksum) ? 0 : this.checksum.hashCode());
    result = prime * result + ((null == this.checksumAlgorithm) ? 0 : this.checksumAlgorithm.hashCode());
    result = prime * result + ((null == this.content) ? 0 : this.content.hashCode());
    result = prime * result + ((null == this.contentCategory) ? 0 : this.contentCategory.hashCode());
    result = prime * result + ((null == this.createdByRO) ? 0 : this.createdByRO.hashCode());
    result = prime * result + ((null == this.creationDate) ? 0 : this.creationDate.hashCode());
    result = prime * result + ((null == this.description) ? 0 : this.description.hashCode());
    result = prime * result + ((null == this.lastModificationDate) ? 0 : this.lastModificationDate.hashCode());
    result = prime * result + ((null == this.metadataSets) ? 0 : this.metadataSets.hashCode());
    result = prime * result + ((null == this.mimeType) ? 0 : this.mimeType.hashCode());
    result = prime * result + ((null == this.name) ? 0 : this.name.hashCode());
    result = prime * result + ((null == this.pid) ? 0 : this.pid.hashCode());
    result = prime * result + ((null == this.reference) ? 0 : this.reference.hashCode());
    result = prime * result + ((null == this.storage) ? 0 : this.storage.hashCode());
    result = prime * result + ((null == this.visibility) ? 0 : this.visibility.hashCode());
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

    FileVO other = (FileVO) obj;

    if (null == this.checksum) {
      if (null != other.checksum)
        return false;
    } else if (!this.checksum.equals(other.checksum))
      return false;

    if (this.checksumAlgorithm != other.checksumAlgorithm)
      return false;

    if (null == this.content) {
      if (null != other.content)
        return false;
    } else if (!this.content.equals(other.content))
      return false;

    if (null == this.contentCategory) {
      if (null != other.contentCategory)
        return false;
    } else if (!this.contentCategory.equals(other.contentCategory))
      return false;

    if (null == this.createdByRO) {
      if (null != other.createdByRO)
        return false;
    } else if (!this.createdByRO.equals(other.createdByRO))
      return false;

    if (null == this.creationDate) {
      if (null != other.creationDate)
        return false;
    } else if (!this.creationDate.equals(other.creationDate))
      return false;

    if (null == this.description) {
      if (null != other.description)
        return false;
    } else if (!this.description.equals(other.description))
      return false;

    if (null == this.lastModificationDate) {
      if (null != other.lastModificationDate)
        return false;
    } else if (!this.lastModificationDate.equals(other.lastModificationDate))
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

    if (null == this.mimeType) {
      if (null != other.mimeType)
        return false;
    } else if (!this.mimeType.equals(other.mimeType))
      return false;

    if (null == this.name) {
      if (null != other.name)
        return false;
    } else if (!this.name.equals(other.name))
      return false;

    if (null == this.pid) {
      if (null != other.pid)
        return false;
    } else if (!this.pid.equals(other.pid))
      return false;

    if (null == this.reference) {
      if (null != other.reference)
        return false;
    } else if (!this.reference.equals(other.reference))
      return false;

    if (this.storage != other.storage)
      return false;

    if (this.visibility != other.visibility)
      return false;

    return true;
  }

}
