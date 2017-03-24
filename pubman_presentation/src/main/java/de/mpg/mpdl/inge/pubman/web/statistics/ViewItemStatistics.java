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

package de.mpg.mpdl.inge.pubman.web.statistics;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.pubman.SimpleStatisticsService;
import de.mpg.mpdl.inge.pubman.web.ViewItemStatisticsPage;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubFileVOPresentation;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Backing Bean for viewItemStatistics.jspf
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@ManagedBean(name = "ViewItemStatistics")
@SuppressWarnings("serial")
public class ViewItemStatistics extends FacesBean {
  private static final Logger logger = Logger.getLogger(ViewItemStatisticsPage.class);

  /** The object Id of the current item */
  private String itemId;

  /** A List with all PubFileVOPresentation objects representing all files of the current item. */
  private List<PubFileVOPresentation> fileList;

  /** The current pub item */
  private PubItemVO pubItem;

  public ViewItemStatistics() {
    this.init();
  }

  public void init() {
    this.pubItem = getItemControllerSessionBean().getCurrentPubItem();
    this.itemId = pubItem.getVersion().getObjectId();

    // get all files, remove Locators, convert to presentation objects and add them to the list
    List<FileVO> files = this.pubItem.getFiles();
    List<FileVO> realFiles = new ArrayList<FileVO>();

    for (FileVO fileVO : files) {
      if (fileVO.getStorage() == FileVO.Storage.INTERNAL_MANAGED)
        realFiles.add(fileVO);
    }

    this.fileList = CommonUtils.convertToPubFileVOPresentationList(realFiles);
  }

  public String getNumberOfItemRetrievalsAllUsers() throws Exception {
    return getItemControllerSessionBean().getStatisticValue(
        SimpleStatisticsService.REPORTDEFINITION_NUMBER_OF_ITEM_RETRIEVALS_ALL_USERS);
  }

  public String getNumberOfItemRetrievalsAnonymousUsers() throws Exception {
    return getItemControllerSessionBean().getStatisticValue(
        SimpleStatisticsService.REPORTDEFINITION_NUMBER_OF_ITEM_RETRIEVALS_ANONYMOUS);
  }

  public String getNumberOfFileDownloadsPerItemAllUsers() throws Exception {
    return getItemControllerSessionBean().getStatisticValue(
        SimpleStatisticsService.REPORTDEFINITION_FILE_DOWNLOADS_PER_ITEM_ALL_USERS);
  }

  public String getNumberOfFileDownloadsPerItemAnonymousUsers() throws Exception {
    return getItemControllerSessionBean().getStatisticValue(
        SimpleStatisticsService.REPORTDEFINITION_FILE_DOWNLOADS_PER_ITEM_ANONYMOUS);
  }

  public List<PubFileVOPresentation> getFileList() {
    return this.fileList;
  }

  public void setFileList(List<PubFileVOPresentation> fileList) {
    this.fileList = fileList;
  }

  private ItemControllerSessionBean getItemControllerSessionBean() {
    return (ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean");
  }

  public PubItemVO getPubItem() {
    return this.pubItem;
  }

  public void setPubItem(PubItemVO pubItem) {
    this.pubItem = pubItem;
  }

  public String getItemID() {
    return this.itemId;
  }

  public void setItemID(String itemID) {
    this.itemId = itemID;
  }

  public boolean getFilesAvailable() {
    return this.fileList.size() > 0;
  }

  public String getNimsLink() {
    try {
      return PropertyReader.getProperty("escidoc.pubman.statistics.nims.link") + getItemID();
    } catch (Exception e) {
      logger.error("Could not read escidoc.pubman.statistics.nims.link from properties");
      return null;
    }
  }

  /**
   * Gets context ids from properties and checks if this item belons to it
   * 
   * @return
   */
  public boolean getShowNIMSLink() {
    try {
      String contexts = PropertyReader.getProperty("escidoc.pubman.statistics.nims.context.ids");
      ItemControllerSessionBean icsb =
          (ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean");
      ContextVO currentContext = icsb.getCurrentContext();
      // logger.info(currentContext.getReference().getObjectId());
      if (contexts != null) {
        String[] contextArray = contexts.split(",");
        for (String contextId : contextArray) {
          if (contextId.trim().equals(currentContext.getReference().getObjectId())) {
            return true;
          }
        }
      }
    } catch (Exception e) {
      logger.error("Could not read escidoc.pubman.statistics.nims.contexts");
    }

    return false;
  }
}
