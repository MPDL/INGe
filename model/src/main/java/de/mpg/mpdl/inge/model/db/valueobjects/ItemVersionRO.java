package de.mpg.mpdl.inge.model.db.valueobjects;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.mpg.mpdl.inge.util.PropertyReader;
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


@SuppressWarnings("serial")
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
    if (0 != this.versionNumber) {
      return this.objectId + "_" + this.versionNumber;
    } else {
      return this.objectId;
    }

  }

  public String getObjectId() {
    return this.objectId;
  }

  public void setObjectId(String objectId) {
    this.objectId = objectId;
  }

  public AccountUserDbRO getModifier() {
    return this.modifier;
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
    return this.versionNumber;
  }

  /**
   * The version number of the referenced item. This attribute is optional.
   *
   * @param newVal
   */
  public void setVersionNumber(int newVal) {
    this.versionNumber = newVal;
  }

  public Date getModificationDate() {
    return this.modificationDate;
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
    this.versionState = newVal;
  }

  public String getVersionPid() {
    return this.versionPid;
  }

  @JsonIgnore
  public String getVersionPidWithoutPrefix() {
    if (this.versionPid.startsWith(PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_SHORT))) {
      return this.versionPid.substring(PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_SHORT).length());
    } else {
      return this.versionPid;
    }
  }

  public void getVersionPid(String pid) {
    this.versionPid = pid;
  }

  @JsonIgnore
  public int getVersionNumberForXml() {
    if (0 < this.versionNumber) {
      return this.versionNumber;
    } else {
      return 1;
    }
  }

  @JsonIgnore
  public Date getModificationDateForXml() {
    return Objects.requireNonNullElseGet(this.modificationDate, Date::new);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((null == this.modificationDate) ? 0 : this.modificationDate.hashCode());
    result = prime * result + ((null == this.modifier) ? 0 : this.modifier.hashCode());
    result = prime * result + ((null == this.objectId) ? 0 : this.objectId.hashCode());
    result = prime * result + this.versionNumber;
    result = prime * result + ((null == this.versionPid) ? 0 : this.versionPid.hashCode());
    result = prime * result + ((null == this.versionState) ? 0 : this.versionState.hashCode());
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
    ItemVersionRO other = (ItemVersionRO) obj;
    if (null == this.modificationDate) {
      if (null != other.modificationDate)
        return false;
    } else if (!this.modificationDate.equals(other.modificationDate))
      return false;
    if (null == this.modifier) {
      if (null != other.modifier)
        return false;
    } else if (!this.modifier.equals(other.modifier))
      return false;
    if (null == this.objectId) {
      if (null != other.objectId)
        return false;
    } else if (!this.objectId.equals(other.objectId))
      return false;
    if (this.versionNumber != other.versionNumber)
      return false;
    if (null == this.versionPid) {
      if (null != other.versionPid)
        return false;
    } else if (!this.versionPid.equals(other.versionPid))
      return false;
    if (this.versionState != other.versionState)
      return false;
    return true;
  }


}
