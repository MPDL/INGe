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
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;
import de.mpg.mpdl.inge.model.valueobjects.interfaces.IgnoreForCleanup;

/**
 * @revised by MuJ: 29.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 22-Okt-2007 15:26:37
 */
@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_EMPTY)
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
    return endDate;
  }

  /**
   * Delivers the invitations status of the event. The invitation status is the information whether
   * the creator was explicitly invited.
   */
  public InvitationStatus getInvitationStatus() {
    return invitationStatus;
  }

  /**
   * Delivers the place of the event.
   */
  public String getPlace() {
    return place;
  }

  /**
   * Delivers the start date of the event.
   */
  public String getStartDate() {
    return startDate;
  }

  /**
   * Delivers the title of the event.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the invitations status of the event. The invitation status is the information whether the
   * creator was explicitly invited.
   * 
   * @param newVal
   */
  public void setInvitationStatus(InvitationStatus newVal) {
    invitationStatus = newVal;
  }

  /**
   * Sets the place of the event.
   * 
   * @param newVal newVal
   */
  public void setPlace(String newVal) {
    place = newVal;
  }

  /**
   * Sets the title of the event.
   * 
   * @param newVal newVal
   */
  public void setTitle(String newVal) {
    title = newVal;
  }

  public Object clone() {
    EventVO clone = new EventVO();
    if (getTitle() != null) {
      clone.setTitle(getTitle());
    }
    if (getEndDate() != null) {
      clone.setEndDate(getEndDate());
    }
    if (getStartDate() != null) {
      clone.setStartDate(getStartDate());
    }
    clone.setInvitationStatus(getInvitationStatus());
    if (getPlace() != null) {
      clone.setPlace(getPlace());
    }
    return clone;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
    result = prime * result + ((invitationStatus == null) ? 0 : invitationStatus.hashCode());
    result = prime * result + ((place == null) ? 0 : place.hashCode());
    result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
    result = prime * result + ((title == null) ? 0 : title.hashCode());
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

    EventVO other = (EventVO) obj;

    if (endDate == null) {
      if (other.endDate != null)
        return false;
    } else if (!endDate.equals(other.endDate))
      return false;

    if (invitationStatus != other.invitationStatus)
      return false;

    if (place == null) {
      if (other.place != null)
        return false;
    } else if (!place.equals(other.place))
      return false;

    if (startDate == null) {
      if (other.startDate != null)
        return false;
    } else if (!startDate.equals(other.startDate))
      return false;

    if (title == null) {
      if (other.title != null)
        return false;
    } else if (!title.equals(other.title))
      return false;

    return true;
  }

  /**
   * Sets the end date of the event.
   * 
   * @param newVal
   */
  public void setEndDate(String newVal) {
    endDate = newVal;
  }

  /**
   * Sets the start date of the event.
   * 
   * @param newVal
   */
  public void setStartDate(String newVal) {
    startDate = newVal;
  }
}
