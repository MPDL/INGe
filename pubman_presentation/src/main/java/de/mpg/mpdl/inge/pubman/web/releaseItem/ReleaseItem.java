package de.mpg.mpdl.inge.pubman.web.releaseItem;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
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

@ManagedBean(name = "ReleaseItem")
@SuppressWarnings("serial")
public class ReleaseItem extends FacesBean {
  private static final Logger logger = LogManager.getLogger(ReleaseItem.class);

  public static final String LOAD_RELEASEITEM = "loadReleaseItem";

  private String releaseComment = null;
  private String creators;

  public ReleaseItem() {
    this.init();
  }

  public void init() {
    final StringBuilder creators = new StringBuilder();
    for (final CreatorVO creator : this.getPubItem().getMetadata().getCreators()) {
      if (!creators.isEmpty()) {
        creators.append("; ");
      }

      if (CreatorVO.CreatorType.PERSON == creator.getType()) {
        creators.append(creator.getPerson().getFamilyName());
        if (null != creator.getPerson().getGivenName()) {
          creators.append(", ");
          creators.append(creator.getPerson().getGivenName());
        }
      } else if (CreatorVO.CreatorType.ORGANIZATION == creator.getType() && null != creator.getOrganization().getName()) {
        creators.append(creator.getOrganization().getName());
      }
    }

    this.creators = creators.toString();
  }

  public String cancel() {
    try {
      FacesTools.getExternalContext()
          .redirect(FacesTools.getRequest().getContextPath() + "/faces/ViewItemFullPage.jsp?itemId=" + this.getPubItem().getObjectId());
    } catch (final IOException e) {
      logger.error("Could not redirect to View Item Page", e);
    }

    return MyItemsRetrieverRequestBean.LOAD_DEPOSITORWS;
  }

  public boolean getHasAudienceFiles() {
    for (final FileDbVO file : this.getPubItem().getFiles()) {
      if (null != file.getVisibility() && file.getVisibility().equals(FileDbVO.Visibility.AUDIENCE)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Checks is the current item has at least one rights information field filled.
   *
   * @return true if at least one rights information field filled
   */
  public boolean getHasRightsInformation() {
    for (final FileDbVO file : this.getPubItem().getFiles()) {
      if ((null != file.getMetadata().getCopyrightDate() && !"".equals(file.getMetadata().getCopyrightDate()))
          || (null != file.getMetadata().getLicense() && !"".equals(file.getMetadata().getLicense()))
          || (null != file.getMetadata().getRights() && !"".equals(file.getMetadata().getRights()))) {
        return true;
      }
    }

    return false;
  }

  private ItemControllerSessionBean getItemControllerSessionBean() {
    return FacesTools.findBean("ItemControllerSessionBean");
  }

  public ItemVersionVO getPubItem() {
    return this.getItemControllerSessionBean().getCurrentPubItem();
  }

  private PubItemListSessionBean getPubItemListSessionBean() {
    return FacesTools.findBean("PubItemListSessionBean");
  }

  public void setReleaseComment(String releaseComment) {
    this.releaseComment = releaseComment;
  }

  public String getReleaseComment() {
    return this.releaseComment;
  }

  public void setCreators(String creators) {
    this.creators = creators;
  }

  public String getCreators() {
    return this.creators;
  }

  public String release() {
    final String navigateTo = ViewItemFull.LOAD_VIEWITEM;

    final String retVal = this.getItemControllerSessionBean().releaseCurrentPubItem(navigateTo, this.releaseComment);

    if (navigateTo.equals(retVal)) {
      this.info(this.getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_RELEASED));
      this.getPubItemListSessionBean().update();
    }

    return retVal;
  }
}
