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

package de.mpg.mpdl.inge.pubman.web.acceptItem;

import java.io.IOException;

import javax.faces.bean.ManagedBean;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Visibility;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.pubman.web.DepositorWSPage;
import de.mpg.mpdl.inge.pubman.web.ErrorPage;
import de.mpg.mpdl.inge.pubman.web.depositorWS.MyItemsRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.viewItem.ViewItemFull;

/**
 * Fragment class for editing PubItems. This class provides all functionality for accepting a
 * PubItem including methods for depending dynamic UI components.
 * 
 * @author: Michael Franke, 2007-10-31
 * @author: $Author$
 * @version: $Revision$ $LastChangedDate$ Revised by FrM: 09.08.2007 * Checkstyled, commented,
 *           cleaned.
 */
@ManagedBean(name = "AcceptItem")
@SuppressWarnings("serial")
public class AcceptItem extends FacesBean {
  private static final Logger logger = Logger.getLogger(AcceptItem.class);

  public static final String LOAD_ACCEPTITEM = "loadAcceptItem";

  private String acceptanceComment = null;
  private String creators;

  public AcceptItem() {
    this.init();
  }

  /**
   * Callback method that is called whenever a page containing this page fragment is navigated to,
   * either directly via a URL, or indirectly via page navigation. Creators handling added by FrM.
   */
  public void init() {
    // Fill creators property.
    StringBuffer creators = new StringBuffer();
    for (CreatorVO creator : this.getPubItem().getMetadata().getCreators()) {
      if (creators.length() > 0) {
        creators.append("; ");
      }
      if (creator.getType() == CreatorVO.CreatorType.PERSON) {
        creators.append(creator.getPerson().getFamilyName());
        if (creator.getPerson().getGivenName() != null) {
          creators.append(", ");
          creators.append(creator.getPerson().getGivenName());
        }
      } else if (creator.getType() == CreatorVO.CreatorType.ORGANIZATION) {
        if (creator.getOrganization().getName() != null) {
          creators.append(creator.getOrganization().getName());
        }
      }
    }

    this.creators = creators.toString();
  }

  /**
   * Deliveres a reference to the currently edited item. This is a shortCut for the method in the
   * ItemController.
   * 
   * @return the item that is currently edited
   */
  public PubItemVO getPubItem() {
    return this.getItemControllerSessionBean().getCurrentPubItem();
  }

  public String accept() {
    // TODO: siehe SubmitItem
    String navigateTo = getAcceptItemSessionBean().getNavigationStringToGoBack();
    if (navigateTo == null) {
      navigateTo = ViewItemFull.LOAD_VIEWITEM;
    }

    String retVal =
        this.getItemControllerSessionBean().acceptCurrentPubItem(acceptanceComment, navigateTo);

    if (retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0) {
      info(getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_ACCEPTED));
    }

    if (retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0) {
      try {
        FacesTools.getExternalContext().redirect(
            FacesTools.getRequest().getContextPath()
                + "/faces/ViewItemFullPage.jsp?itemId="
                + this.getItemControllerSessionBean().getCurrentPubItem().getVersion()
                    .getObjectId());
      } catch (IOException e) {
        logger.error("Could not redirect to View Item Page", e);
      }
    }

    return retVal;
  }

  public String cancel() {
    try {
      FacesTools.getExternalContext().redirect(
          FacesTools.getRequest().getContextPath() + "/faces/ViewItemFullPage.jsp?itemId="
              + this.getPubItem().getVersion().getObjectId());
    } catch (IOException e) {
      logger.error("Could not redirect to View Item Page", e);
    }

    return MyItemsRetrieverRequestBean.LOAD_DEPOSITORWS;
  }

  /**
   * Checks is the current item has at least one rights information field filled.
   * 
   * @return true if at least one rights information field filled
   */
  public boolean getHasRightsInformation() {
    for (FileVO file : this.getPubItem().getFiles()) {
      if ((file.getDefaultMetadata().getCopyrightDate() != null && !"".equals(file
          .getDefaultMetadata().getCopyrightDate()))
          || (file.getDefaultMetadata().getLicense() != null && !"".equals(file
              .getDefaultMetadata().getLicense()))
          || (file.getDefaultMetadata().getRights() != null && !"".equals(file.getDefaultMetadata()
              .getRights()))) {
        return true;
      }
    }

    return false;
  }

  public String getAcceptanceComment() {
    return this.acceptanceComment;
  }

  public void setAcceptanceComment(String acceptanceComment) {
    this.acceptanceComment = acceptanceComment;
  }

  public String getCreators() {
    return this.creators;
  }

  public void setCreators(String creators) {
    this.creators = creators;
  }

  public boolean getHasAudienceFiles() {
    for (FileVO file : this.getPubItem().getFiles()) {
      if (file.getVisibility() != null && file.getVisibility().equals(Visibility.AUDIENCE)) {
        return true;
      }
    }

    return false;
  }

  private ItemControllerSessionBean getItemControllerSessionBean() {
    return (ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean");
  }

  private AcceptItemSessionBean getAcceptItemSessionBean() {
    return (AcceptItemSessionBean) FacesTools.findBean("AcceptItemSessionBean");
  }
}
