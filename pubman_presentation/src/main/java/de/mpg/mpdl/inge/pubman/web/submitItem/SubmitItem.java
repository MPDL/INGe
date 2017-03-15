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

package de.mpg.mpdl.inge.pubman.web.submitItem;

import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Visibility;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO.State;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.pubman.PubItemService;
import de.mpg.mpdl.inge.pubman.web.DepositorWSPage;
import de.mpg.mpdl.inge.pubman.web.ErrorPage;
import de.mpg.mpdl.inge.pubman.web.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.appbase.FacesBean;
import de.mpg.mpdl.inge.pubman.web.depositorWS.MyItemsRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.viewItem.ViewItemFull;

/**
 * Fragment class for editing PubItems. This class provides all functionality for editing, saving
 * and submitting a PubItem including methods for depending dynamic UI components.
 * 
 * @author: Thomas Diebäcker, created 10.01.2007
 * @author: $Author$
 * @version: $Revision$ $LastChangedDate$ Revised by FrM: 09.08.2007 * Checkstyled, commented,
 *           cleaned.
 */
@SuppressWarnings("serial")
public class SubmitItem extends FacesBean {
  private static final Logger logger = Logger.getLogger(SubmitItem.class);

  public static final String LOAD_SUBMITITEM = "loadSubmitItem";

  private String submissionComment;
  private String creators;

  public SubmitItem() {
    this.init();
  }

  /**
   * Callback method that is called whenever a page containing this page fragment is navigated to,
   * either directly via a URL, or indirectly via page navigation. Creators handling added by FrM.
   */
  public void init() {
    // Fill creators property.
    StringBuffer creators = new StringBuffer();
    for (CreatorVO creator : this.getCurrentPubItem().getMetadata().getCreators()) {
      if (creators.length() > 0) {
        creators.append("; ");
      }
      if (creator.getType() == CreatorVO.CreatorType.PERSON) {
        creators.append(creator.getPerson().getFamilyName());
        if (creator.getPerson().getGivenName() != null) {
          creators.append(", ");
          creators.append(creator.getPerson().getGivenName());
        }
      } else if (creator.getType() == CreatorVO.CreatorType.ORGANIZATION
          && creator.getOrganization().getName() != null) {
        creators.append(creator.getOrganization().getName());
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
  public PubItemVO getCurrentPubItem() {
    return this.getItemControllerSessionBean().getCurrentPubItem();
  }

  /**
   * Submits the item.
   * 
   * @return string, identifying the page that should be navigated to after this methodcall
   */
  public String submit() {
    FacesContext fc = FacesContext.getCurrentInstance();
    ExternalContext extContext = fc.getExternalContext();
    HttpServletRequest request = (HttpServletRequest) extContext.getRequest();

    String navigateTo = ViewItemFull.LOAD_VIEWITEM;
    String retVal = "";
    String message;

    if (this.getCurrentPubItem().getVersion().getState() == State.SUBMITTED) {
      retVal =
          this.getItemControllerSessionBean().releaseCurrentPubItem(submissionComment, navigateTo);
      message = getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_RELEASED);
    } else {
      retVal =
          this.getItemControllerSessionBean().submitCurrentPubItem(submissionComment, navigateTo);
      if (PubItemService.WORKFLOW_SIMPLE.equals(this.getItemControllerSessionBean()
          .getCurrentWorkflow())) {
        retVal =
            this.getItemControllerSessionBean()
                .releaseCurrentPubItem(submissionComment, navigateTo);
        message = getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_RELEASED);
      } else {
        message = getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_SUBMITTED);
      }
    }

    if (retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0) {
      info(message);
    }

    if (ViewItemFull.LOAD_VIEWITEM.equals(retVal)) {
      try {
        extContext.redirect(request.getContextPath() + "/faces/ViewItemFullPage.jsp?itemId="
            + this.getItemControllerSessionBean().getCurrentPubItem().getVersion().getObjectId());
      } catch (IOException e) {
        logger.error("Could not redirect to View Item Page", e);
      }
    }

    PubItemListSessionBean pubItemListSessionBean =
        (PubItemListSessionBean) getSessionBean(PubItemListSessionBean.class);
    if (pubItemListSessionBean != null) {
      pubItemListSessionBean.update();
    }

    return retVal;
  }

  public String cancel() {
    FacesContext fc = FacesContext.getCurrentInstance();
    ExternalContext extContext = fc.getExternalContext();
    HttpServletRequest request = (HttpServletRequest) extContext.getRequest();
    try {
      extContext.redirect(request.getContextPath() + "/faces/ViewItemFullPage.jsp?itemId="
          + this.getCurrentPubItem().getVersion().getObjectId());
    } catch (IOException e) {
      logger.error("Could not redirect to View Item Page", e);
    }

    return MyItemsRetrieverRequestBean.LOAD_DEPOSITORWS;
  }

  public boolean getHasRightsInformation() {
    for (FileVO file : this.getCurrentPubItem().getFiles()) {
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

  public boolean getHasAudienceFiles() {
    for (FileVO file : this.getCurrentPubItem().getFiles()) {
      if (file.getVisibility() != null && file.getVisibility().equals(Visibility.AUDIENCE)) {
        return true;
      }
    }

    return false;
  }

  public String getSubmissionComment() {
    return this.submissionComment;
  }

  public void setSubmissionComment(String submissionComment) {
    this.submissionComment = submissionComment;
  }

  public String getCreators() {
    return this.creators;
  }

  public void setCreators(String creators) {
    this.creators = creators;
  }

  public boolean getIsStandardWorkflow() {
    return this.getItemControllerSessionBean().getCurrentWorkflow()
        .equals(PubItemService.WORKFLOW_STANDARD);
  }

  public boolean getIsSimpleWorkflow() {
    return this.getItemControllerSessionBean().getCurrentWorkflow()
        .equals(PubItemService.WORKFLOW_SIMPLE);
  }

  public boolean getIsSubmitted() {
    return this.getCurrentPubItem().getVersion().getState() == State.SUBMITTED;
  }

  private ItemControllerSessionBean getItemControllerSessionBean() {
    return (ItemControllerSessionBean) getSessionBean(ItemControllerSessionBean.class);
  }
}
