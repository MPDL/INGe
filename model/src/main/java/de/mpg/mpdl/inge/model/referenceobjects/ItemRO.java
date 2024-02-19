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

package de.mpg.mpdl.inge.model.referenceobjects;

import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * The class for item references.
 *
 * @revised by MuJ: 27.08.2007
 * @version 1.0
 * @updated 21-Nov-2007 12:37:07
 */
@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_EMPTY)
public class ItemRO extends ReferenceObject implements Cloneable {
  /**
   * The version number of the referenced item. This attribute is optional.
   */
  private int versionNumber;

  /**
   * The date of the last modification of the referenced item.
   */
  private Date modificationDate;

  /**
   * The message of the last action event of this item.
   */
  private String lastMessage;

  /**
   * The state of the item.
   */
  private ItemVO.State state;

  /**
   * The eSciDoc ID of the user that modified that version.
   */
  private AccountUserRO modifiedByRO;

  /**
   * The version PID of the item.
   */
  private String pid;

  /**
   * Creates a new instance.
   */
  public ItemRO() {}

  /**
   * Creates a new instance with the given objectId.
   *
   * @param objectId The id of the object.
   */
  public ItemRO(String objectId) {
    super(objectId);
  }

  /**
   * Copy constructor.
   *
   * @author Thomas Diebaecker
   * @param other The instance to copy.
   */
  public ItemRO(ItemRO other) {
    super(other);
    this.versionNumber = other.versionNumber;
    this.lastMessage = other.lastMessage;
    this.state = other.state;
    this.modifiedByRO = other.modifiedByRO;
    this.modificationDate = other.modificationDate;
    this.pid = other.pid;
  }

  /**
   *
   *
   * @author Thomas Diebaecker
   */
  @Override
  public ItemRO clone() {
    return new ItemRO(this);
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

  /**
   * Set the full identification of an item version.
   *
   * @param idString A String in the form objid:versionNumber e.g. "escidoc:345:2"
   */
  @JsonIgnore
  public void setObjectIdAndVersion(String idString) {
    int ix = idString.lastIndexOf(":");
    if (ix == -1) {
      setObjectId(idString);
      versionNumber = 0;
    } else {
      setObjectId(idString.substring(0, ix));
      versionNumber = Integer.parseInt(idString.substring(ix + 1));
    }
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
  public ItemVO.State getState() {
    return state;
  }

  /**
   * Sets the state of the item.
   *
   * @param newVal The new state.
   */
  public void setState(ItemVO.State newVal) {
    state = newVal;
  }

  @JsonProperty("modifiedBy")
  public AccountUserRO getModifiedByRO() {
    return modifiedByRO;
  }

  @JsonProperty("modifiedBy")
  public void setModifiedByRO(AccountUserRO modifiedByRO) {
    this.modifiedByRO = modifiedByRO;
  }

  public String getPid() {
    return pid;
  }

  // remove "hdl:" if possible (needed for URLs including a handle-resolver)
  @JsonIgnore
  public String getPidWithoutPrefix() {
    if (pid.startsWith(PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_SHORT))) {
      return pid.substring(PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_SHORT).length());
    } else {
      return pid;
    }
  }

  public void setPid(String pid) {
    this.pid = pid;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((lastMessage == null) ? 0 : lastMessage.hashCode());
    result = prime * result + ((modificationDate == null) ? 0 : modificationDate.hashCode());
    result = prime * result + ((modifiedByRO == null) ? 0 : modifiedByRO.hashCode());
    result = prime * result + ((pid == null) ? 0 : pid.hashCode());
    result = prime * result + ((state == null) ? 0 : state.hashCode());
    result = prime * result + versionNumber;
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

    ItemRO other = (ItemRO) obj;

    if (lastMessage == null) {
      if (other.lastMessage != null)
        return false;
    } else if (!lastMessage.equals(other.lastMessage))
      return false;

    if (modificationDate == null) {
      if (other.modificationDate != null)
        return false;
    } else if (!modificationDate.equals(other.modificationDate))
      return false;

    if (modifiedByRO == null) {
      if (other.modifiedByRO != null)
        return false;
    } else if (!modifiedByRO.equals(other.modifiedByRO))
      return false;

    if (pid == null) {
      if (other.pid != null)
        return false;
    } else if (!pid.equals(other.pid))
      return false;

    if (state != other.state)
      return false;

    if (versionNumber != other.versionNumber)
      return false;

    return true;
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
    return Objects.requireNonNullElseGet(modificationDate, Date::new);
  }

  @JsonIgnore
  public ItemVO.State getStateForXml() {
    if (state == null) {
      return ItemVO.State.PENDING;
    } else {
      return state;
    }
  }

  @JsonIgnore
  public AccountUserRO getModifiedByForXml() {
    return Objects.requireNonNullElseGet(modifiedByRO, AccountUserRO::new);
  }

  @JsonIgnore
  public String getLastMessageForXml() {
    return Objects.requireNonNullElse(lastMessage, "");
  }

  @Override
  public void setObjectId(String objectId) {
    if (objectId != null && objectId.contains(":") && objectId.substring(objectId.indexOf(":") + 1).contains(":")) {
      super.setObjectId(objectId.substring(0, objectId.lastIndexOf(":")));
      setVersionNumber(Integer.parseInt(objectId.substring(objectId.lastIndexOf(":") + 1)));
    } else if (objectId != null && objectId.contains("_") && objectId.substring(objectId.indexOf("_") + 1).contains("_")) {
      super.setObjectId(objectId.substring(0, objectId.lastIndexOf("_")));
      setVersionNumber(Integer.parseInt(objectId.substring(objectId.lastIndexOf("_") + 1)));
    } else {
      super.setObjectId(objectId);
    }
  }

  public void setHref(String href) {
    if (href == null) {
      return;
    }
    if (href.contains("/")) {
      href = href.substring(href.lastIndexOf("/") + 1);
    }
    this.setObjectId(href);
  }

  // just a dummy, as href is needed for jibx-input only
  public String getHref() {
    return null;
  }

}
