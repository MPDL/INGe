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
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.mpg.mpdl.inge.model.db.hibernate.MdsFileVOJsonUserType;
import de.mpg.mpdl.inge.model.db.hibernate.StringListJsonUserType;
import de.mpg.mpdl.inge.model.util.MapperFactory;
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
@Entity(name = "FileVO")
@Table(name = "file")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "item")
@Access(AccessType.FIELD)
@TypeDef(name = "MdsFileVOJsonUserType", typeClass = MdsFileVOJsonUserType.class)
@TypeDef(name = "StringListJsonUserType", typeClass = StringListJsonUserType.class)
public class FileDbVO extends FileDbRO implements Serializable {
  /**
   * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
   * 'de.mpg.mpdl.inge.model.valueobjects.ItemVO; local class incompatible: stream classdesc
   * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286'
   * that occur after JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to
   * be compiled twice for testing (once for the Application Server, once for the local test).
   * 
   * @author Johannes Mueller
   */

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
    EXTERNAL_URL
  }
  /**
   * The possible storage of a file.
   */
  public enum ChecksumAlgorithm
  {
    MD5
  }


  /*
   * @Embedded private FileRO reference;
   * 
   * @AttributeOverrides({@AttributeOverride(name = "objectId", column = @Column( name =
   * "reference_objectId"))})
   */



  /**
   * The visibility of the file for users of the system.
   */
  @Enumerated(EnumType.STRING)
  private Visibility visibility;

  /**
   * The persistent identifier of the file if the item is released.
   */
  private String pid;

  /**
   * A reference to the content of the file.
   */
  @Column(columnDefinition = "TEXT")
  private String content;

  /**
   * A reference to the storage attribute of the file.
   */
  @Enumerated(EnumType.STRING)
  private Storage storage;


  private String checksum;

  @Enumerated(EnumType.STRING)
  private ChecksumAlgorithm checksumAlgorithm;

  /**
   * The size of the file in Bytes. Has to be zero if no content is given.
   */
  // private long size;

  /**
   * The MIME-type of this format. Valid values see http://www.iana.org/assignments/media-types/
   */
  private String mimeType;


  /**
   * Size of the file. Use this field instead of metadata.size
   */
  private long size;


  @Column
  @Type(type = "MdsFileVOJsonUserType")
  private MdsFileVO metadata;

  @JsonIgnore
  private String localFileIdentifier;

  @Type(type= "StringListJsonUserType")
  private List<String> allowedAudienceIds = new ArrayList<>();

  /**
   * Public contructor.
   * 
   * @author Thomas Diebaecker
   */
  public FileDbVO() {}



  /**
   * Copy constructor.
   * 
   * @author Thomas Diebaecker
   * @param other The instance to copy.
   */
  public FileDbVO(FileDbVO other) {
    MapperFactory.getDozerMapper().map(other, this);
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
   * Delivers the visibility of the file.
   */
  public Visibility getVisibility() {
    return visibility;
  }

  /**
   * Sets the visibility of the file.
   * 
   * @param newVal
   */
  public void setVisibility(Visibility newVal) {
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



  public Storage getStorage() {
    return storage;
  }

  public void setStorage(Storage storage) {
    this.storage = storage;
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

  /*
   * @Override public int hashCode() { final int prime = 31; int result = 1; result = prime * result
   * + ((checksum == null) ? 0 : checksum.hashCode()); result = prime * result + ((checksumAlgorithm
   * == null) ? 0 : checksumAlgorithm.hashCode()); result = prime * result + ((content == null) ? 0
   * : content.hashCode()); result = prime * result + ((contentCategory == null) ? 0 :
   * contentCategory.hashCode()); result = prime * result + ((createdByRO == null) ? 0 :
   * createdByRO.hashCode()); result = prime * result + ((creationDate == null) ? 0 :
   * creationDate.hashCode()); result = prime * result + ((description == null) ? 0 :
   * description.hashCode()); result = prime * result + ((lastModificationDate == null) ? 0 :
   * lastModificationDate.hashCode()); result = prime * result + ((metadataSets == null) ? 0 :
   * metadataSets.hashCode()); result = prime * result + ((mimeType == null) ? 0 :
   * mimeType.hashCode()); result = prime * result + ((name == null) ? 0 : name.hashCode()); result
   * = prime * result + ((pid == null) ? 0 : pid.hashCode()); //result = prime * result +
   * ((reference == null) ? 0 : reference.hashCode()); result = prime * result + ((storage == null)
   * ? 0 : storage.hashCode()); result = prime * result + ((visibility == null) ? 0 :
   * visibility.hashCode()); return result; }
   * 
   * 
   * @Override public boolean equals(Object obj) { if (this == obj) return true;
   * 
   * if (obj == null) return false;
   * 
   * if (getClass() != obj.getClass()) return false;
   * 
   * FileVO other = (FileVO) obj;
   * 
   * if (checksum == null) { if (other.checksum != null) return false; } else if
   * (!checksum.equals(other.checksum)) return false;
   * 
   * if (checksumAlgorithm != other.checksumAlgorithm) return false;
   * 
   * if (content == null) { if (other.content != null) return false; } else if
   * (!content.equals(other.content)) return false;
   * 
   * if (contentCategory == null) { if (other.contentCategory != null) return false; } else if
   * (!contentCategory.equals(other.contentCategory)) return false;
   * 
   * if (createdByRO == null) { if (other.createdByRO != null) return false; } else if
   * (!createdByRO.equals(other.createdByRO)) return false;
   * 
   * if (creationDate == null) { if (other.creationDate != null) return false; } else if
   * (!creationDate.equals(other.creationDate)) return false;
   * 
   * if (description == null) { if (other.description != null) return false; } else if
   * (!description.equals(other.description)) return false;
   * 
   * if (lastModificationDate == null) { if (other.lastModificationDate != null) return false; }
   * else if (!lastModificationDate.equals(other.lastModificationDate)) return false;
   * 
   * if (metadataSets == null) { if (other.metadataSets != null) return false; } else if
   * (other.metadataSets == null) return false; else if
   * (!metadataSets.containsAll(other.metadataSets) // ||
   * !other.metadataSets.containsAll(metadataSets)) { return false; }
   * 
   * if (mimeType == null) { if (other.mimeType != null) return false; } else if
   * (!mimeType.equals(other.mimeType)) return false;
   * 
   * if (name == null) { if (other.name != null) return false; } else if (!name.equals(other.name))
   * return false;
   * 
   * if (pid == null) { if (other.pid != null) return false; } else if (!pid.equals(other.pid))
   * return false;
   * 
   * 
   * if (reference == null) { if (other.reference != null) return false; } else if
   * (!reference.equals(other.reference)) return false;
   * 
   * 
   * 
   * if (storage != other.storage) return false;
   * 
   * if (visibility != other.visibility) return false;
   * 
   * return true; }
   */



  public MdsFileVO getMetadata() {
    return metadata;
  }

  public void setMetadata(MdsFileVO metadata) {
    this.metadata = metadata;
  }

  public String getLocalFileIdentifier() {
    return localFileIdentifier;
  }

  public void setLocalFileIdentifier(String localFileIdentifier) {
    this.localFileIdentifier = localFileIdentifier;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }



  public List<String> getAllowedAudienceIds() {
    return allowedAudienceIds;
  }



  public void setAllowedAudienceIds(List<String> allowedAudienceIds) {
    this.allowedAudienceIds = allowedAudienceIds;
  }



  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((allowedAudienceIds == null) ? 0 : allowedAudienceIds.hashCode());
    result = prime * result + ((checksum == null) ? 0 : checksum.hashCode());
    result = prime * result + ((checksumAlgorithm == null) ? 0 : checksumAlgorithm.hashCode());
    result = prime * result + ((content == null) ? 0 : content.hashCode());
    result = prime * result + ((localFileIdentifier == null) ? 0 : localFileIdentifier.hashCode());
    result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
    result = prime * result + ((mimeType == null) ? 0 : mimeType.hashCode());
    result = prime * result + ((pid == null) ? 0 : pid.hashCode());
    result = prime * result + (int) (size ^ (size >>> 32));
    result = prime * result + ((storage == null) ? 0 : storage.hashCode());
    result = prime * result + ((visibility == null) ? 0 : visibility.hashCode());
    return result;
  }



  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    FileDbVO other = (FileDbVO) obj;
    if (allowedAudienceIds == null) {
      if (other.allowedAudienceIds != null)
        return false;
    } else if (!allowedAudienceIds.equals(other.allowedAudienceIds))
      return false;
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
    if (localFileIdentifier == null) {
      if (other.localFileIdentifier != null)
        return false;
    } else if (!localFileIdentifier.equals(other.localFileIdentifier))
      return false;
    if (metadata == null) {
      if (other.metadata != null)
        return false;
    } else if (!metadata.equals(other.metadata))
      return false;
    if (mimeType == null) {
      if (other.mimeType != null)
        return false;
    } else if (!mimeType.equals(other.mimeType))
      return false;
    if (pid == null) {
      if (other.pid != null)
        return false;
    } else if (!pid.equals(other.pid))
      return false;
    if (size != other.size)
      return false;
    if (storage != other.storage)
      return false;
    if (visibility != other.visibility)
      return false;
    return true;
  }



}
