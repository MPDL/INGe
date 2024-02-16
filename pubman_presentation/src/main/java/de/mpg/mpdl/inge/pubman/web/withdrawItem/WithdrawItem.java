package de.mpg.mpdl.inge.pubman.web.withdrawItem;

import java.io.IOException;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.pubman.web.DepositorWSPage;
import de.mpg.mpdl.inge.pubman.web.depositorWS.MyItemsRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.viewItem.ViewItemFull;
import jakarta.faces.bean.ManagedBean;

@ManagedBean(name = "WithdrawItem")
@SuppressWarnings("serial")
public class WithdrawItem extends FacesBean {
  private static final Logger logger = Logger.getLogger(WithdrawItem.class);

  public static final String LOAD_WITHDRAWITEM = "loadWithdrawItem";

  private String withdrawalComment = null;
  private String creators;

  public WithdrawItem() {
    this.init();
  }

  public void init() {
    final StringBuffer creators = new StringBuffer();
    for (final CreatorVO creator : this.getPubItem().getMetadata().getCreators()) {
      if (!creators.isEmpty()) {
        creators.append("; ");
      }

      if (creator.getType() == CreatorVO.CreatorType.PERSON) {
        creators.append(creator.getPerson().getFamilyName());
        if (creator.getPerson().getGivenName() != null) {
          creators.append(", ");
          creators.append(creator.getPerson().getGivenName());
        }
      } else if (creator.getType() == CreatorVO.CreatorType.ORGANIZATION && creator.getOrganization().getName() != null) {
        creators.append(creator.getOrganization().getName());
      }
    }

    this.creators = creators.toString();
  }

  public String cancel() {
    try {
      FacesTools.getExternalContext().redirect(FacesTools.getRequest().getContextPath() + "/faces/ViewItemFullPage.jsp?itemId="
          + this.getItemControllerSessionBean().getCurrentPubItem().getObjectId());
    } catch (final IOException e) {
      WithdrawItem.logger.error("Could not redirect to View Item Page", e);
    }

    return MyItemsRetrieverRequestBean.LOAD_DEPOSITORWS;
  }

  private ItemControllerSessionBean getItemControllerSessionBean() {
    return (ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean");
  }

  public ItemVersionVO getPubItem() {
    return this.getItemControllerSessionBean().getCurrentPubItem();
  }

  private PubItemListSessionBean getPubItemListSessionBean() {
    return (PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean");
  }

  public void setWithdrawalComment(String withdrawalComment) {
    this.withdrawalComment = withdrawalComment;
  }

  public String getWithdrawalComment() {
    return this.withdrawalComment;
  }

  public void setCreators(String creators) {
    this.creators = creators;
  }

  public String getCreators() {
    return this.creators;
  }

  public String withdraw() {
    if (this.withdrawalComment == null || this.withdrawalComment.trim().isEmpty()) {
      this.error(this.getMessage(DepositorWSPage.NO_WITHDRAWAL_COMMENT_GIVEN));
      return null;
    }

    final String navigateTo = ViewItemFull.LOAD_VIEWITEM;

    final String retVal = this.getItemControllerSessionBean().withdrawCurrentPubItem(navigateTo, this.withdrawalComment);

    if (navigateTo.equals(retVal)) {
      this.info(this.getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_WITHDRAWN));
      this.getPubItemListSessionBean().update();

      try {
        FacesTools.getExternalContext().redirect(FacesTools.getRequest().getContextPath() + "/faces/ViewItemFullPage.jsp?itemId="
            + this.getItemControllerSessionBean().getCurrentPubItem().getObjectId());
      } catch (final IOException e) {
        WithdrawItem.logger.error("Could not redirect to View Item Page", e);
      }
    }

    return retVal;
  }
}
