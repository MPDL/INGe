package de.mpg.mpdl.inge.model.db.valueobjects;

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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.objectId == null) ? 0 : this.objectId.hashCode());
    result = prime * result + this.versionNumber;
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
    VersionableId other = (VersionableId) obj;
    if (this.objectId == null) {
      if (other.objectId != null)
        return false;
    } else if (!this.objectId.equals(other.objectId))
      return false;
    if (this.versionNumber != other.versionNumber)
      return false;
    return true;
  }
}
