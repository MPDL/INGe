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

import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubFileVOPresentation;
import de.mpg.mpdl.inge.service.pubman.impl.MatomoStatisticsService;

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
  /** The object Id of the current item */
  private String itemId;

  /** A List with all PubFileVOPresentation objects representing all files of the current item. */
  private List<PubFileVOPresentation> fileList;

  /** The current pub item */
  private ItemVersionVO pubItem;

  public ViewItemStatistics() {
    this.init();
  }

  public void init() {
    this.pubItem = this.getItemControllerSessionBean().getCurrentPubItem();
    this.itemId = this.pubItem.getObjectIdAndVersion();

    // get all files, remove Locators, convert to presentation objects and add them to the list
    final List<FileDbVO> files = this.pubItem.getFiles();
    final List<FileDbVO> realFiles = new ArrayList<FileDbVO>();

    for (final FileDbVO fileVO : files) {
      if (fileVO.getStorage() == FileDbVO.Storage.INTERNAL_MANAGED) {
        realFiles.add(fileVO);
      }
    }

    this.fileList = CommonUtils.convertToPubFileVOPresentationList(realFiles);
  }

  public String getNumberOfItemRetrievalsAllUsers() throws Exception {
    return Integer.toString(MatomoStatisticsService.getTotal4Item(pubItem.getObjectId()));
  }


  public String getNumberOfFileDownloadsPerFileAllUsers(String fileId, String name) throws Exception {
    final String result = MatomoStatisticsService.getNumberOfFileDownloads(itemId, fileId, name);
    return result;
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

  public ItemVersionVO getPubItem() {
    return this.pubItem;
  }

  public void setPubItem(ItemVersionVO pubItem) {
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

}
