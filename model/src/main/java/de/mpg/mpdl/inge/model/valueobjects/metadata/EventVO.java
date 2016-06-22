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

import de.mpg.mpdl.inge.model.valueobjects.IgnoreForCleanup;
import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

/**
 * @revised by MuJ: 29.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 22-Okt-2007 15:26:37
 */
@JsonInclude(value = Include.NON_NULL)
public class EventVO extends ValueObject implements Cloneable {
  /**
   * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
   * 'de.mpg.mpdl.inge.model.valueobjects.ItemVO; local class incompatible: stream classdesc
   * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286'
   * that occur after JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to
   * be compiled twice for testing (once for the Application Server, once for the local test).
   * 
   * @author Johannes Mueller
   */
  private static final long serialVersionUID = 1L;

  /**
   * The possible invitation status of the event.
   * 
   * @updated 22-Okt-2007 15:26:37
   */
  public enum InvitationStatus {
    INVITED
  }

  private java.util.List<AlternativeTitleVO> alternativeTitles =
      new java.util.ArrayList<AlternativeTitleVO>();
  private String endDate;

  @IgnoreForCleanup
  private InvitationStatus invitationStatus;

  private String place;
  private String startDate;
  private String title;

  /**
   * Get the alternativeTitles. The event may have one or several alternative forms of the title
   * (e.g. an abbreviated title).
   */
  public java.util.List<AlternativeTitleVO> getAlternativeTitles() {
    return alternativeTitles;
  }

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

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#clone()
   */
  @Override
  public Object clone() {
    EventVO clone = new EventVO();
    if (getTitle() != null) {
      clone.setTitle(getTitle());
    }
    for (AlternativeTitleVO altTitle : getAlternativeTitles()) {
      clone.getAlternativeTitles().add((AlternativeTitleVO) altTitle.clone());
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

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(getClass().isAssignableFrom(obj.getClass()))) {
      return false;
    }
    EventVO other = (EventVO) obj;
    return equals(getTitle(), other.getTitle()) && equals(getStartDate(), other.getStartDate())
        && equals(getEndDate(), other.getEndDate()) && equals(getPlace(), other.getPlace())
        && equals(getInvitationStatus(), other.getInvitationStatus())
        && equals(getAlternativeTitles(), other.getAlternativeTitles());
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