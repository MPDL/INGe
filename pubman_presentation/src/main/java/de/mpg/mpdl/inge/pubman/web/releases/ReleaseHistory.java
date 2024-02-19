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

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.VersionHistoryEntryVO;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ItemControllerSessionBean;
import jakarta.faces.bean.ManagedBean;

/**
 * Fragment class for Releasy history.
 *
 * @author: Tobias Schraut, created 18.10.2007
 * @version: $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "ReleaseHistory")
@SuppressWarnings("serial")
public class ReleaseHistory extends FacesBean {
  private static final Logger logger = LogManager.getLogger(ReleaseHistory.class);

  public static final String LOAD_RELEASE_HISTORY = "loadReleaseHistory";

  public ReleaseHistory() {
    this.init();
  }

  public void init() {
    final ItemVersionListSessionBean ivlsb = FacesTools.findBean("ItemVersionListSessionBean");
    if (null == ivlsb.getVersionList()) {
      ivlsb.initLists(this.getVersionHistory(
          ((ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean")).getCurrentPubItem().getObjectId()));
    }
  }

  /**
   * Retrieves all releases for the current pubitem.
   *
   * @param itemID the id of the item for which the releases should be retrieved
   * @return the list of VersionHistoryEntryVOs
   */
  public List<VersionHistoryEntryVO> getVersionHistory(String itemID) {
    try {
      return ((ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean")).retrieveVersionHistoryForItem(itemID);
    } catch (final Exception e) {
      logger.error("Could not retrieve release list for Item " + itemID, e);
    }

    return null;
  }
}
