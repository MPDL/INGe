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

package de.mpg.mpdl.inge.pubman.web.releases;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.model.valueobjects.EventLogEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.VersionHistoryEntryVO;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.EventLogEntryVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.VersionHistoryVOPresentation;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.bean.SessionScoped;

/**
 * Keeps all attributes that are used for the whole session by the ReleaseHistory, the Item Log and
 * the Revision History.
 *
 * @author: Tobias Schraut, created 18.10.2007
 * @version: $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "ItemVersionListSessionBean")
@SessionScoped
@SuppressWarnings("serial")
public class ItemVersionListSessionBean extends FacesBean {
  private List<VersionHistoryVOPresentation> versionList = new ArrayList<>();

  private List<EventLogEntryVOPresentation> releaseList = new ArrayList<>();

  private List<EventLogEntryVOPresentation> eventLogList = new ArrayList<>();

  public ItemVersionListSessionBean() {}

  public void initLists(List<VersionHistoryEntryVO> vList) {
    this.versionList = new ArrayList<>();
    this.releaseList = new ArrayList<>();
    this.eventLogList = new ArrayList<>();

    for (final VersionHistoryEntryVO vEntry : vList) {
      this.versionList.add(new VersionHistoryVOPresentation(vEntry));
    }

    for (final VersionHistoryVOPresentation vEntry : this.versionList) {
      final List<EventLogEntryVO> eventList = vEntry.getEvents();
      for (final EventLogEntryVO eEntry : eventList) {
        if (EventLogEntryVO.EventType.RELEASE.equals(eEntry.getType())) {
          this.releaseList.add(new EventLogEntryVOPresentation(eEntry, vEntry));
        }

        this.eventLogList.add(new EventLogEntryVOPresentation(eEntry, vEntry));
      }
    }
  }

  public void resetVersionLists() {
    this.versionList = null;
    this.releaseList = null;
    this.eventLogList = null;
  }

  public List<VersionHistoryVOPresentation> getVersionList() {
    return this.versionList;
  }

  public List<EventLogEntryVOPresentation> getReleaseList() {
    return this.releaseList;
  }

  public List<EventLogEntryVOPresentation> getEventLogList() {
    return this.eventLogList;
  }

  public void setVersionList(List<VersionHistoryVOPresentation> versionList) {
    this.versionList = versionList;
  }

  public void setReleaseList(List<EventLogEntryVOPresentation> releaseList) {
    this.releaseList = releaseList;
  }

  public void setEventLogList(List<EventLogEntryVOPresentation> eventLogList) {
    this.eventLogList = eventLogList;
  }
}
