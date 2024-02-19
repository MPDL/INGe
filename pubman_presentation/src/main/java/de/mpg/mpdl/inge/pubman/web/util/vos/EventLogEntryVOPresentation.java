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
 * Wissenschaft e.V. All rights reserved.
 */

package de.mpg.mpdl.inge.pubman.web.util.vos;

import de.mpg.mpdl.inge.model.valueobjects.EventLogEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.VersionHistoryEntryVO;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.InternationalizationHelper;

/**
 * TODO Description
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@SuppressWarnings("serial")
public class EventLogEntryVOPresentation extends EventLogEntryVO {
  private VersionHistoryEntryVO versionHistoryVO;

  private final InternationalizationHelper i18nHelper = FacesTools.findBean("InternationalizationHelper");

  public EventLogEntryVOPresentation(EventLogEntryVO eventLogVO, VersionHistoryEntryVO versionHistoryVO) {
    this.setComment(eventLogVO.getComment());
    this.setDate(eventLogVO.getDate());
    this.setType(eventLogVO.getType());
    this.versionHistoryVO = versionHistoryVO;
  }

  public String getCurrentTypeLabel() {
    if (null != this.getType()) {
      return switch (this.getType()) {
        case CREATE -> this.i18nHelper.getLabel("ViewItemLog_lblCreate");
        case RELEASE -> this.i18nHelper.getLabel("ViewItemLog_lblRelease");
        case SUBMIT -> this.i18nHelper.getLabel("ViewItemLog_lblSubmit");
        case UPDATE -> this.i18nHelper.getLabel("ViewItemLog_lblUpdate");
        case WITHDRAW -> this.i18nHelper.getLabel("ViewItemLog_lblWithdraw");
        case IN_REVISION -> this.i18nHelper.getLabel("ViewItemLog_lblInRevision");
        case ASSIGN_VERSION_PID -> this.i18nHelper.getLabel("ViewItemLog_lblAssignVersionPid");
      };
    }

    return "";
  }

  /** Returns all comments except for update, because update-comments are not possible */
  public String getFormattedComment() {
    if (EventLogEntryVO.EventType.UPDATE.equals(this.getType())) {
      return "Item updated";
    } else {
      return this.getComment();
    }
  }

  public String getFormattedDate() {
    return CommonUtils.formatTimestamp(this.getDate());
  }

  public VersionHistoryEntryVO getVersionHistoryVO() {
    return this.versionHistoryVO;
  }

  public void setVersionHistoryVO(VersionHistoryEntryVO versionHistoryVO) {
    this.versionHistoryVO = versionHistoryVO;
  }
}
