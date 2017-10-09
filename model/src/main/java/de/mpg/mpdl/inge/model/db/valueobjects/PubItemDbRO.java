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

import com.fasterxml.jackson.annotation.JsonIgnore;


@MappedSuperclass
@IdClass(VersionableId.class)
public class PubItemDbRO implements Serializable {

  public enum State {
    PENDING, SUBMITTED, RELEASED, WITHDRAWN, IN_REVISION
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
   * The message of the last action event of this item.
   */
  @Column(columnDefinition = "TEXT")
  private String lastMessage;
  /**
   * The state of the item.
   */
  @Enumerated(EnumType.STRING)
  private PubItemDbRO.State state;
  /**
   * The eSciDoc ID of the user that modified that version.
   */
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "objectId", column = @Column(name = "modifier_objectId")),
      @AttributeOverride(name = "name", column = @Column(name = "modifier_name"))})
  private AccountUserDbRO modifiedBy;
  /**
   * The version PID of the item.
   */
  private String versionPid;

  public String getObjectId() {
    return objectId;
  }

  public void setObjectId(String objectId) {
    this.objectId = objectId;
  }

  public AccountUserDbRO getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(AccountUserDbRO modifiedBy) {
    this.modifiedBy = modifiedBy;
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

  public String getLastMessage() {
    return lastMessage;
  }

  public void setLastMessage(String lastMessage) {
    this.lastMessage = lastMessage;
  }

  /**
   * Delivers the state of the item.
   * 
   * @return The current State.
   */
  public PubItemDbRO.State getState() {
    return this.state;
  }

  /**
   * Sets the state of the item.
   * 
   * @param newVal The new state.
   */
  public void setState(PubItemDbRO.State newVal) {
    state = newVal;
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

  @JsonIgnore
  public PubItemDbRO.State getStateForXml() {
    if (state == null) {
      return PubItemDbRO.State.PENDING;
    } else {
      return state;
    }
  }

  @JsonIgnore
  public String getLastMessageForXml() {
    if (lastMessage == null) {
      return "";
    } else {
      return lastMessage;
    }
  }

  /**
   * Get the full identification of an item version.
   * 
   * @return A String in the form objid:versionNumber e.g. "escidoc:345:2"
   */
  public String getObjectIdAndVersion() {
    if (versionNumber != 0) {
      return getObjectId() + "_" + versionNumber;
    } else {
      return getObjectId();
    }

  }

}
