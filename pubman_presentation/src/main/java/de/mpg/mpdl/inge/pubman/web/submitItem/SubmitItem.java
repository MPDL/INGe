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

import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Visibility;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO.State;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.pubman.web.DepositorWSPage;
import de.mpg.mpdl.inge.pubman.web.depositorWS.MyItemsRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.viewItem.ViewItemFull;
import de.mpg.mpdl.inge.service.pubman.PubItemService;

/**
 * Fragment class for editing PubItems. This class provides all functionality for editing, saving
 * and submitting a PubItem including methods for depending dynamic UI components.
 * 
 * @author: Thomas Diebäcker, created 10.01.2007
 * @author: $Author$
 * @version: $Revision$ $LastChangedDate$ Revised by FrM: 09.08.2007 * Checkstyled, commented,
 *           cleaned.
 */
@ManagedBean(name = "SubmitItem")
@SuppressWarnings("serial")
public class SubmitItem extends FacesBean {
  private static final Logger logger = Logger.getLogger(SubmitItem.class);

  public static final String LOAD_SUBMITITEM = "loadSubmitItem";

  private String submissionComment;
  private String creators;

  public SubmitItem() {
    this.init();
  }

  public void init() {
    // Fill creators property.
    final StringBuffer creators = new StringBuffer();
    for (final CreatorVO creator : this.getCurrentPubItem().getMetadata().getCreators()) {
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
    String navigateTo = ViewItemFull.LOAD_VIEWITEM;
    String message;

    if (this.getCurrentPubItem().getVersion().getState() == State.SUBMITTED) {
      message = this.getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_RELEASED);
      navigateTo =
          this.getItemControllerSessionBean().releaseCurrentPubItem(this.submissionComment,
              navigateTo);
    } else {
      navigateTo =
          this.getItemControllerSessionBean().submitCurrentPubItem(this.submissionComment,
              navigateTo);
      if (PubItemService.WORKFLOW_SIMPLE.equals(this.getItemControllerSessionBean()
          .getCurrentWorkflow())) {
        message = this.getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_RELEASED);
        navigateTo =
            this.getItemControllerSessionBean().releaseCurrentPubItem(this.submissionComment,
                navigateTo);
      } else {
        message = this.getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_SUBMITTED);
      }
    }

    if (ViewItemFull.LOAD_VIEWITEM.equals(navigateTo)) {
      this.info(this.getMessage(message));
    }

    ((PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean")).update();

    return navigateTo;
  }

  public String cancel() {
    final ExternalContext extContext = FacesTools.getExternalContext();
    final HttpServletRequest request = FacesTools.getRequest();
    try {
      extContext.redirect(request.getContextPath() + "/faces/ViewItemFullPage.jsp?itemId="
          + this.getCurrentPubItem().getVersion().getObjectId());
    } catch (final IOException e) {
      SubmitItem.logger.error("Could not redirect to View Item Page", e);
    }

    return MyItemsRetrieverRequestBean.LOAD_DEPOSITORWS;
  }

  public boolean getHasRightsInformation() {
    for (final FileVO file : this.getCurrentPubItem().getFiles()) {
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
    for (final FileVO file : this.getCurrentPubItem().getFiles()) {
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
    return (ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean");
  }
}
