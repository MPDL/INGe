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

package de.mpg.mpdl.inge.pubman.web.withdrawItem;

import java.io.IOException;

import javax.faces.bean.ManagedBean;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.pubman.web.DepositorWSPage;
import de.mpg.mpdl.inge.pubman.web.depositorWS.MyItemsRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ItemControllerSessionBean;
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
@ManagedBean(name = "WithdrawItem")
@SuppressWarnings("serial")
public class WithdrawItem extends FacesBean {
  private static final Logger logger = Logger.getLogger(WithdrawItem.class);

  public static final String LOAD_WITHDRAWITEM = "loadWithdrawItem";

  private String withdrawalComment;
  private String creators;

  public WithdrawItem() {
    this.init();
  }

  public void init() {
    final StringBuffer creators = new StringBuffer();
    for (final CreatorVO creator : this.getPubItem().getMetadata().getCreators()) {
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
        final String name =
            creator.getOrganization().getName() != null ? creator.getOrganization().getName() : "";
        creators.append(name);
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

  public String withdraw() {
    if (this.withdrawalComment == null || "".equals(this.withdrawalComment.trim())) {
      FacesBean.error(this.getMessage(DepositorWSPage.NO_WITHDRAWAL_COMMENT_GIVEN));
      return null;
    }

    final String navigateTo = ViewItemFull.LOAD_VIEWITEM;

    final String retVal =
        this.getItemControllerSessionBean().withdrawCurrentPubItem(navigateTo,
            this.withdrawalComment);

    if (navigateTo.equals(retVal)) {
      this.info(this.getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_WITHDRAWN));
      this.getPubItemListSessionBean().update();

      try {
        FacesTools.getExternalContext().redirect(
            FacesTools.getRequest().getContextPath()
                + "/faces/ViewItemFullPage.jsp?itemId="
                + this.getItemControllerSessionBean().getCurrentPubItem().getVersion()
                    .getObjectId());
      } catch (final IOException e) {
        WithdrawItem.logger.error("Could not redirect to View Item Page", e);
      }
    }

    return retVal;
  }

  /**
   * Cancels the editing.
   * 
   * @return string, identifying the page that should be navigated to after this methodcall
   */
  public String cancel() {
    try {
      FacesTools.getExternalContext().redirect(
          FacesTools.getRequest().getContextPath() + "/faces/ViewItemFullPage.jsp?itemId="
              + this.getItemControllerSessionBean().getCurrentPubItem().getVersion().getObjectId());
    } catch (final IOException e) {
      WithdrawItem.logger.error("Could not redirect to View Item Page", e);
    }

    return MyItemsRetrieverRequestBean.LOAD_DEPOSITORWS;
  }

  public String getWithdrawalComment() {
    return this.withdrawalComment;
  }

  public void setWithdrawalComment(String withdrawalComment) {
    this.withdrawalComment = withdrawalComment;
  }

  public String getCreators() {
    return this.creators;
  }

  public void setCreators(String creators) {
    this.creators = creators;
  }

  private ItemControllerSessionBean getItemControllerSessionBean() {
    return (ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean");
  }

  private PubItemListSessionBean getPubItemListSessionBean() {
    return (PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean");
  }
}
