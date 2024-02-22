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

package de.mpg.mpdl.inge.model.valueobjects.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;
import de.mpg.mpdl.inge.model.valueobjects.interfaces.IgnoreForCleanup;

/**
 * @revised by MuJ: 29.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 22-Okt-2007 15:26:37
 */
@SuppressWarnings("serial")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class EventVO extends ValueObject implements Cloneable {
  /**
   * The possible invitation status of the event.
   *
   * @updated 22-Okt-2007 15:26:37
   */
  public enum InvitationStatus
  {
    INVITED
  }

  private String endDate;

  @IgnoreForCleanup
  private InvitationStatus invitationStatus;

  private String place;
  private String startDate;
  private String title;

  /**
   * Delivers the end date of the event.
   */
  public String getEndDate() {
    return this.endDate;
  }

  /**
   * Delivers the invitations status of the event. The invitation status is the information whether
   * the creator was explicitly invited.
   */
  public InvitationStatus getInvitationStatus() {
    return this.invitationStatus;
  }

  /**
   * Delivers the place of the event.
   */
  public String getPlace() {
    return this.place;
  }

  /**
   * Delivers the start date of the event.
   */
  public String getStartDate() {
    return this.startDate;
  }

  /**
   * Delivers the title of the event.
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * Sets the invitations status of the event. The invitation status is the information whether the
   * creator was explicitly invited.
   *
   * @param newVal
   */
  public void setInvitationStatus(InvitationStatus newVal) {
    this.invitationStatus = newVal;
  }

  /**
   * Sets the place of the event.
   *
   * @param newVal newVal
   */
  public void setPlace(String newVal) {
    this.place = newVal;
  }

  /**
   * Sets the title of the event.
   *
   * @param newVal newVal
   */
  public void setTitle(String newVal) {
    this.title = newVal;
  }

  public final EventVO clone() {
    try {
      EventVO clone = (EventVO) super.clone();
      if (null != clone.invitationStatus) {
        clone.invitationStatus = this.invitationStatus;
      }
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((null == this.endDate) ? 0 : this.endDate.hashCode());
    result = prime * result + ((null == this.invitationStatus) ? 0 : this.invitationStatus.hashCode());
    result = prime * result + ((null == this.place) ? 0 : this.place.hashCode());
    result = prime * result + ((null == this.startDate) ? 0 : this.startDate.hashCode());
    result = prime * result + ((null == this.title) ? 0 : this.title.hashCode());
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

    EventVO other = (EventVO) obj;

    if (null == this.endDate) {
      if (null != other.endDate)
        return false;
    } else if (!this.endDate.equals(other.endDate))
      return false;

    if (this.invitationStatus != other.invitationStatus)
      return false;

    if (null == this.place) {
      if (null != other.place)
        return false;
    } else if (!this.place.equals(other.place))
      return false;

    if (null == this.startDate) {
      if (null != other.startDate)
        return false;
    } else if (!this.startDate.equals(other.startDate))
      return false;

    if (null == this.title) {
      if (null != other.title)
        return false;
    } else if (!this.title.equals(other.title))
      return false;

    return true;
  }

  /**
   * Sets the end date of the event.
   *
   * @param newVal
   */
  public void setEndDate(String newVal) {
    this.endDate = newVal;
  }

  /**
   * Sets the start date of the event.
   *
   * @param newVal
   */
  public void setStartDate(String newVal) {
    this.startDate = newVal;
  }
}
