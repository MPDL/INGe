package de.mpg.mpdl.inge.model.db.valueobjects;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.mpg.mpdl.inge.util.PropertyReader;


@MappedSuperclass
@IdClass(VersionableId.class)
public class ItemVersionRO implements Serializable {

  public enum State
  {
    PENDING,
    SUBMITTED,
    RELEASED,
    WITHDRAWN,
    IN_REVISION
  }

  @Id
  private String objectId;

  @Id
  protected int versionNumber;
  /**
   * The date of the last modification of the referenced item.
   */
  private Date modificationDate;

  /**
   * The state of the item.
   */
  @Enumerated(EnumType.STRING)
  private ItemVersionRO.State versionState;

  /**
   * The version PID of the item.
   */
  private String versionPid;


  /**
   * The eSciDoc ID of the user that modified that version.
   */
  @Embedded
  @AttributeOverrides({@AttributeOverride(name = "objectId", column = @Column(name = "modifier_objectId")),
      @AttributeOverride(name = "name", column = @Column(name = "modifier_name"))})
  private AccountUserDbRO modifier;



  /**
   * Get the full identification of an item version.
   * 
   * @return A String in the form objid:versionNumber e.g. "escidoc:345:2"
   */
  @Transient
  @JsonIgnore
  public String getObjectIdAndVersion() {
    if (versionNumber != 0) {
      return getObjectId() + "_" + versionNumber;
    } else {
      return getObjectId();
    }

  }

  public String getObjectId() {
    return objectId;
  }

  public void setObjectId(String objectId) {
    this.objectId = objectId;
  }

  public AccountUserDbRO getModifier() {
    return modifier;
  }

  public void setModifier(AccountUserDbRO modifier) {
    this.modifier = modifier;
  }

  public void setVersionPid(String versionPid) {
    this.versionPid = versionPid;
  }

  /**
   * The version number of the referenced item. This attribute is optional.
   */
  public int getVersionNumber() {
    return versionNumber;
  }

  /**
   * The version number of the referenced item. This attribute is optional.
   * 
   * @param newVal
   */
  public void setVersionNumber(int newVal) {
    versionNumber = newVal;
  }

  public Date getModificationDate() {
    return modificationDate;
  }

  public void setModificationDate(Date modificationDate) {
    this.modificationDate = modificationDate;
  }



  /**
   * Delivers the state of the item.
   * 
   * @return The current State.
   */
  public ItemVersionRO.State getVersionState() {
    return this.versionState;
  }

  /**
   * Sets the state of the item.
   * 
   * @param newVal The new state.
   */
  public void setVersionState(ItemVersionRO.State newVal) {
    versionState = newVal;
  }

  public String getVersionPid() {
    return versionPid;
  }

  @JsonIgnore
  public String getVersionPidWithoutPrefix() {
    if (versionPid.startsWith(PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_SHORT))) {
      return versionPid.substring(PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_SHORT).length());
    } else {
      return versionPid;
    }
  }

  public void getVersionPid(String pid) {
    this.versionPid = pid;
  }

  @JsonIgnore
  public int getVersionNumberForXml() {
    if (versionNumber > 0) {
      return versionNumber;
    } else {
      return 1;
    }
  }

  @JsonIgnore
  public Date getModificationDateForXml() {
    if (modificationDate == null) {
      return new Date();
    } else {
      return modificationDate;
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((modificationDate == null) ? 0 : modificationDate.hashCode());
    result = prime * result + ((modifier == null) ? 0 : modifier.hashCode());
    result = prime * result + ((objectId == null) ? 0 : objectId.hashCode());
    result = prime * result + versionNumber;
    result = prime * result + ((versionPid == null) ? 0 : versionPid.hashCode());
    result = prime * result + ((versionState == null) ? 0 : versionState.hashCode());
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
    ItemVersionRO other = (ItemVersionRO) obj;
    if (modificationDate == null) {
      if (other.modificationDate != null)
        return false;
    } else if (!modificationDate.equals(other.modificationDate))
      return false;
    if (modifier == null) {
      if (other.modifier != null)
        return false;
    } else if (!modifier.equals(other.modifier))
      return false;
    if (objectId == null) {
      if (other.objectId != null)
        return false;
    } else if (!objectId.equals(other.objectId))
      return false;
    if (versionNumber != other.versionNumber)
      return false;
    if (versionPid == null) {
      if (other.versionPid != null)
        return false;
    } else if (!versionPid.equals(other.versionPid))
      return false;
    if (versionState != other.versionState)
      return false;
    return true;
  }


}
