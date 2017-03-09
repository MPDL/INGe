package de.mpg.mpdl.inge.pubman.web.util;

import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.pubman.web.appbase.FacesBean;
import de.mpg.mpdl.inge.pubman.web.audience.AudienceSessionBean;

@SuppressWarnings("serial")
public class GrantVOPresentation extends FacesBean {
  public static final String GRANT_TYPE_USER_GROUP = "user-group";

  private GrantVO grant;
  private int index;
  private int fileIndex;

  public GrantVOPresentation() {}

  /**
   * Public constructor with parameters
   * 
   * @param grant the grant
   * @param index the index of the grant within the file
   */
  public GrantVOPresentation(GrantVO grant, int index) {
    this.grant = grant;
    this.index = index;
  }

  /**
   * 
   * @param grant the grant
   * @param index the index of the grant within the file
   * @param fileIndex the index of the file in the item
   */
  public GrantVOPresentation(GrantVO grant, int index, int fileIndex) {
    this.grant = new GrantVO(grant);
    this.index = index;
    this.fileIndex = fileIndex;
  }

  public void remove() {
    AudienceSessionBean asb = this.getAudienceSessionBean();
    asb.getFileListNew().get(this.fileIndex).getGrantList().remove(this);
    if (asb.getFileListNew().get(this.fileIndex).getGrantList().size() < 1) {
      asb.getFileListNew()
          .get(this.fileIndex)
          .getGrantList()
          .add(
              new GrantVOPresentation(new GrantVO(), asb.getFileListNew().get(this.fileIndex)
                  .getGrantList().size(), this.fileIndex));
    }
  }

  public void removeGrantForAllFiles() {
    this.getAudienceSessionBean().getGrantsForAllFiles().remove(this);
    if (this.getAudienceSessionBean().getGrantsForAllFiles().size() < 1) {
      this.getAudienceSessionBean()
          .getGrantsForAllFiles()
          .add(
              new GrantVOPresentation(new GrantVO(), this.getAudienceSessionBean()
                  .getGrantsForAllFiles().size()));
    }
  }

  private AudienceSessionBean getAudienceSessionBean() {
    return (AudienceSessionBean) getSessionBean(AudienceSessionBean.class);
  }

  public GrantVO getGrant() {
    return grant;
  }

  public void setGrant(GrantVO grant) {
    this.grant = grant;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }
}
