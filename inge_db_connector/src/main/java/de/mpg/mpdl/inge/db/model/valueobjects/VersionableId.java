package de.mpg.mpdl.inge.db.model.valueobjects;

import java.io.Serializable;

import javax.persistence.Embeddable;

@SuppressWarnings("serial")
@Embeddable
public class VersionableId implements Serializable {

  private String objectId;

  private int versionNumber;

  public VersionableId() {}

  public VersionableId(String objectId, int versionNumber) {
    this.objectId = objectId;
    this.versionNumber = versionNumber;
  }

  public String getObjectId() {
    return objectId;
  }

  public void setObjectId(String objectId) {
    this.objectId = objectId;
  }

  public int getVersionNumber() {
    return versionNumber;
  }

  public void setVersionNumber(int versionNumber) {
    this.versionNumber = versionNumber;
  }

  @Override
  public String toString() {
    return objectId + "_" + versionNumber;
  }



}
