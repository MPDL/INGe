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

package de.mpg.mpdl.inge.model.valueobjects;

import java.util.List;

import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;

/**
 * Version information of a publication item.
 *
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @created 17-Okt-2007 18:51:45
 */
@SuppressWarnings("serial")
public class VersionHistoryEntryVO extends ValueObject {
  /**
   * Reference to the according item.
   */
  private ItemRO reference;
  /**
   * The modification date of the version.
   */
  private java.util.Date modificationDate;
  /**
   * The state of the item version.
   */
  private ItemVO.State state;
  /**
   * The list of events for this history entry.
   */
  private List<EventLogEntryVO> events;

  /**
   * @return Reference to the according item.
   */
  public ItemRO getReference() {
    return this.reference;
  }

  /**
   * @return The modification date of the version.
   */
  public java.util.Date getModificationDate() {
    return this.modificationDate;
  }

  /**
   * @return The state of the item version.
   */
  public ItemVO.State getState() {
    return this.state;
  }

  /**
   * Reference to the according item.
   *
   * @param newVal
   */
  public void setReference(ItemRO newVal) {
    this.reference = newVal;
  }

  /**
   * The modification date of the version.
   *
   * @param newVal
   */
  public void setModificationDate(java.util.Date newVal) {
    this.modificationDate = newVal;
  }

  /**
   * Sets the state of the item version.
   *
   * @param newVal
   */
  public void setState(ItemVO.State newVal) {
    this.state = newVal;
  }

  public List<EventLogEntryVO> getEvents() {
    return this.events;
  }

  public void setEvents(List<EventLogEntryVO> events) {
    this.events = events;
  }

}
