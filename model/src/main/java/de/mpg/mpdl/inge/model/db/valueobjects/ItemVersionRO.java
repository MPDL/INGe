package de.mpg.mpdl.inge.model.db.valueobjects;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;


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
    if (versionPid.startsWith("hdl:")) {
      return versionPid.substring(4);
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


}
