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
    AUDIENCE;
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

  private List<MetadataSetVO> metadataSets = new ArrayList<MetadataSetVO>();


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
    content = other.content;
    contentCategory = other.contentCategory;
    creationDate = other.creationDate;
    createdByRO = other.createdByRO;
    description = other.description;
    lastModificationDate = other.lastModificationDate;
    mimeType = other.mimeType;
    name = other.name;
    pid = other.pid;
    reference = other.reference;
    // size = other.size;
    visibility = other.visibility;
    storage = other.storage;
    metadataSets = other.metadataSets;
    checksum = other.checksum;
    checksumAlgorithm = other.checksumAlgorithm;
  }

  public Object clone() {
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
    return (this.creationDate != null);
  }

  @JsonProperty("metadata")
  public MdsFileVO getDefaultMetadata() {
    if (metadataSets.size() > 0 && metadataSets.get(0) instanceof MdsFileVO) {
      return (MdsFileVO) metadataSets.get(0);
    } else {
      return null;
    }
  }

  @JsonProperty("metadata")
  public void setDefaultMetadata(MdsFileVO mdsFileVO) {
    if (metadataSets.size() == 0) {
      metadataSets.add(mdsFileVO);
    } else {
      metadataSets.set(0, mdsFileVO);
    }
  }

  /**
   * Delivers the files' reference.
   * 
   * @see de.mpg.mpdl.inge.model.referenceobjects.ReferenceObject
   */
  public FileRO getReference() {
    return reference;
  }

  /**
   * Sets the files' reference.
   * 
   * @see de.mpg.mpdl.inge.model.referenceobjects.ReferenceObject
   * 
   * @param newVal
   */
  public void setReference(FileRO newVal) {
    reference = newVal;
  }

  /**
   * Delivers the name of the file including the extension.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the file including the extension.
   * 
   * @param newVal
   */
  public void setName(String newVal) {
    name = newVal;
  }

  /**
   * Delivers the persistent identifier of the file.
   */
  public String getPid() {
    return pid;
  }

  /**
   * remove "hdl:" if possible (needed for URLs including a handle-resolver)
   */
  @JsonIgnore
  public String getPidWithoutPrefix() {
    if (pid.startsWith(PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_SHORT))) {
      return pid.substring(PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_SHORT).length());
    } else {
      return pid;
    }
  }

  /**
   * Sets the persistent identifier of the file.
   * 
   * @param newVal
   */
  public void setPid(String newVal) {
    pid = newVal;
  }

  /**
   * Delivers the description of the file, i. e. a short description of the file.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description of the file, i. e. a short description of the file.
   * 
   * @param newVal
   */
  public void setDescription(String newVal) {
    description = newVal;
  }

  public AccountUserRO getCreatedByRO() {
    return createdByRO;
  }

  public void setCreatedByRO(AccountUserRO createdByRO) {
    this.createdByRO = createdByRO;
  }

  /**
   * Delivers a reference to the content of the file, i. e. to the data of the file.
   */
  public String getContent() {
    return content;
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
    return contentCategory;
  }

  /**
   * Sets the content type of the file.
   * 
   * @param newVal
   */
  public void setContentCategory(String newVal) {
    contentCategory = newVal;
  }

  /**
   * Delivers the visibility of the file.
   */
  public FileVO.Visibility getVisibility() {
    return visibility;
  }

  /**
   * Sets the visibility of the file.
   * 
   * @param newVal
   */
  public void setVisibility(FileVO.Visibility newVal) {
    visibility = newVal;
  }

  /**
   * Delivers the MIME-type of the file. For valid values see
   * http://www.iana.org/assignments/media-types/
   */
  public String getMimeType() {
    return mimeType;
  }

  /**
   * Sets the MIME-type of the file. For valid values see
   * http://www.iana.org/assignments/media-types/
   * 
   * @param newVal
   */
  public void setMimeType(String newVal) {
    mimeType = newVal;
  }

  /**
   * Delivers the creation date of the file.
   */
  public java.util.Date getCreationDate() {
    return creationDate;
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
    return lastModificationDate;
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
    if (contentCategory == null || contentCategory.toString() == null) {
      return "";
    }
    return contentCategory.toString();
  }

  /**
   * Sets the value of the contentCategory Enum by a String.
   * 
   * @param newValString
   */
  @JsonIgnore
  public void setContentCategoryString(String newValString) {
    contentCategory = newValString;
  }

  /**
   * Delivers the value of the visibility Enum as a String. If the enum is not set, an empty String
   * is returned.
   */
  @JsonIgnore
  public String getVisibilityString() {
    if (visibility == null || visibility.toString() == null) {
      return "";
    }
    return visibility.toString();
  }

  /**
   * Sets the value of the visibility Enum by a String.
   * 
   * @param newValString
   */
  @JsonIgnore
  public void setVisibilityString(String newValString) {
    if (newValString == null || newValString.length() == 0) {
      visibility = null;
    } else {
      FileVO.Visibility newVal = FileVO.Visibility.valueOf(newValString);
      visibility = newVal;
    }
  }

  public FileVO.Storage getStorage() {
    return storage;
  }

  public void setStorage(FileVO.Storage storage) {
    this.storage = storage;
  }

  @JsonIgnore
  public String getStorageString() {
    if (storage == null || storage.toString() == null) {
      return "";
    }
    return storage.toString();
  }

  @JsonIgnore
  public void setStorageString(String newValString) {
    if (newValString == null || newValString.length() == 0) {
      storage = null;
    } else {
      FileVO.Storage newVal = FileVO.Storage.valueOf(newValString);
      storage = newVal;
    }
  }

  @JsonIgnore
  public List<MetadataSetVO> getMetadataSets() {
    return metadataSets;
  }

  public String getChecksum() {
    return checksum;
  }

  public void setChecksum(String checksum) {
    this.checksum = checksum;
  }

  public ChecksumAlgorithm getChecksumAlgorithm() {
    return checksumAlgorithm;
  }

  public void setChecksumAlgorithm(ChecksumAlgorithm checksumAlgorithm) {
    this.checksumAlgorithm = checksumAlgorithm;
  }

  public String getLocalFileIdentifier() {
    return localFileIdentifier;
  }

  public void setLocalFileIdentifier(String localFileIdentifier) {
    this.localFileIdentifier = localFileIdentifier;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((checksum == null) ? 0 : checksum.hashCode());
    result = prime * result + ((checksumAlgorithm == null) ? 0 : checksumAlgorithm.hashCode());
    result = prime * result + ((content == null) ? 0 : content.hashCode());
    result = prime * result + ((contentCategory == null) ? 0 : contentCategory.hashCode());
    result = prime * result + ((createdByRO == null) ? 0 : createdByRO.hashCode());
    result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((lastModificationDate == null) ? 0 : lastModificationDate.hashCode());
    result = prime * result + ((metadataSets == null) ? 0 : metadataSets.hashCode());
    result = prime * result + ((mimeType == null) ? 0 : mimeType.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((pid == null) ? 0 : pid.hashCode());
    result = prime * result + ((reference == null) ? 0 : reference.hashCode());
    result = prime * result + ((storage == null) ? 0 : storage.hashCode());
    result = prime * result + ((visibility == null) ? 0 : visibility.hashCode());
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

    FileVO other = (FileVO) obj;

    if (checksum == null) {
      if (other.checksum != null)
        return false;
    } else if (!checksum.equals(other.checksum))
      return false;

    if (checksumAlgorithm != other.checksumAlgorithm)
      return false;

    if (content == null) {
      if (other.content != null)
        return false;
    } else if (!content.equals(other.content))
      return false;

    if (contentCategory == null) {
      if (other.contentCategory != null)
        return false;
    } else if (!contentCategory.equals(other.contentCategory))
      return false;

    if (createdByRO == null) {
      if (other.createdByRO != null)
        return false;
    } else if (!createdByRO.equals(other.createdByRO))
      return false;

    if (creationDate == null) {
      if (other.creationDate != null)
        return false;
    } else if (!creationDate.equals(other.creationDate))
      return false;

    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;

    if (lastModificationDate == null) {
      if (other.lastModificationDate != null)
        return false;
    } else if (!lastModificationDate.equals(other.lastModificationDate))
      return false;

    if (metadataSets == null) {
      if (other.metadataSets != null)
        return false;
    } else if (other.metadataSets == null)
      return false;
    else if (!metadataSets.containsAll(other.metadataSets) //
        || !other.metadataSets.containsAll(metadataSets)) {
      return false;
    }

    if (mimeType == null) {
      if (other.mimeType != null)
        return false;
    } else if (!mimeType.equals(other.mimeType))
      return false;

    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;

    if (pid == null) {
      if (other.pid != null)
        return false;
    } else if (!pid.equals(other.pid))
      return false;

    if (reference == null) {
      if (other.reference != null)
        return false;
    } else if (!reference.equals(other.reference))
      return false;

    if (storage != other.storage)
      return false;

    if (visibility != other.visibility)
      return false;

    return true;
  }

}
