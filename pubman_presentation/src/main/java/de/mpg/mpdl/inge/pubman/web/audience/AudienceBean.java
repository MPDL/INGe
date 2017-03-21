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

package de.mpg.mpdl.inge.pubman.web.audience;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.FileVO.Visibility;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.UserGroupVO;
import de.mpg.mpdl.inge.pubman.web.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.vos.GrantVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubFileVOPresentation;
import de.mpg.mpdl.inge.pubman.web.viewItem.ViewItemFull;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Fragment class for editing the audience grants of files. This class provides all functionality
 * for giving and revoking user group grants for files in request scope.
 * 
 * @author: Tobias Schraut, 2009-05-20
 */
@ManagedBean(name = "AudienceBean")
@SuppressWarnings("serial")
public class AudienceBean extends FacesBean {
  private static final Logger logger = Logger.getLogger(AudienceBean.class);

  public static final String LOAD_AUDIENCEPAGE = "loadAudiencePage";

  public static final String DUMMY_REVOKE_COMMENT = "grant revoked";
  public static final String DUMMY_CREATE_COMMENT = "grant created";

  public AudienceBean() {
    this.init();
  }

  /**
   * Callback method that is called whenever a page containing this page fragment is navigated to,
   * either directly via a URL, or indirectly via page navigation.
   */
  public final void init() {
    // Perform initializations inherited from our superclass
    // super.init();
    // AudienceSessionBean asb = this.getAudienceSessionBean();
    // fill the file list in the session bean
    if (this.getAudienceSessionBean().getFileListNew() == null
        || this.getAudienceSessionBean().getFileListNew().size() == 0) {
      if (this.getItemControllerSessionBean().getCurrentPubItem().getFiles() != null) {
        // LoginHelper loginHelper = (LoginHelper) FacesTools.findBean(LoginHelper.class);
        int fileIndex = 0;
        for (int i = 0; i < this.getItemControllerSessionBean().getCurrentPubItem().getFiles()
            .size(); i++) {
          // only take files with visibility audience
          if (this.getItemControllerSessionBean().getCurrentPubItem().getFiles().get(i)
              .getVisibility().equals(Visibility.AUDIENCE)) {
            PubFileVOPresentation fileForNewList =
                new PubFileVOPresentation(fileIndex, this.getItemControllerSessionBean()
                    .getCurrentPubItem().getFiles().get(i));
            PubFileVOPresentation fileForOldList =
                new PubFileVOPresentation(fileIndex, this.getItemControllerSessionBean()
                    .getCurrentPubItem().getFiles().get(i));

            // add the grants
            List<GrantVO> grantList = new ArrayList<GrantVO>();
            try {
              // TODO INGe connection
              // grantList =
              // GrantList.Factory
              // .retrieveGrantsForObject(loginHelper.getESciDocUserHandle(), this
              // .getItemControllerSessionBean().getCurrentPubItem().getFiles().get(i)
              // .getReference().getObjectId(), Grant.CoreserviceRole.AUDIENCE.getRoleId());
            } catch (Exception e) {
              logger.error("could not retrieve audience grants for files: ", e);
            }

            for (int j = 0; j < grantList.size(); j++) {
              fileForNewList.getGrantList().add(
                  new GrantVOPresentation(grantList.get(j), j, fileIndex));
              fileForOldList.getGrantList().add(
                  new GrantVOPresentation(grantList.get(j), j, fileIndex));
            }

            // ensure that at least one grant is in the list (for presentation)
            if (fileForNewList.getGrantList().size() == 0) {
              GrantVO newGrant = new GrantVO();
              newGrant.setObjectRef(this.getItemControllerSessionBean().getCurrentPubItem()
                  .getFiles().get(i).getReference().getObjectId());
              newGrant.setGrantType(GrantVOPresentation.GRANT_TYPE_USER_GROUP);
              // TODO set role for INGe
              // newGrant.setRole(Grant.CoreserviceRole.AUDIENCE.getRoleId());
              fileForNewList.getGrantList().add(new GrantVOPresentation(newGrant, 0, 0));
            }

            this.getAudienceSessionBean().getFileListOld().add(fileForOldList);
            this.getAudienceSessionBean().getFileListNew().add(fileForNewList);

            fileIndex++;
          }
        }
      }

      // fill the user group list
      if (this.getAudienceSessionBean().getUgl() == null) {
        // LoginHelper loginHelper = (LoginHelper) FacesTools.findBean(LoginHelper.class);
        try {
          this.getAudienceSessionBean().setUgl(
          // TODO INGe connection
          // UserGroupList.Factory.retrieveActiveUserGroups(loginHelper.getESciDocUserHandle())
              null);
        } catch (Exception e) {
          logger.error("could not retrieve user groups for audience management: ", e);
        }
      }

      // ensure that there is at least one grant for all files (for display purpose)
      if (this.getAudienceSessionBean().getGrantsForAllFiles() != null
          && this.getAudienceSessionBean().getGrantsForAllFiles().size() == 0) {
        GrantVO newGrant = new GrantVO();
        // newGrant.setObjid("");
        newGrant.setGrantType(GrantVOPresentation.GRANT_TYPE_USER_GROUP);
        // TODO set role for INGe
        // newGrant.setRole(Grant.CoreserviceRole.AUDIENCE.getRoleId());
        this.getAudienceSessionBean()
            .getGrantsForAllFiles()
            .add(
                new GrantVOPresentation(newGrant, this.getAudienceSessionBean()
                    .getGrantsForAllFiles().size()));
      }
    }
  }

  /**
   * Cleans AudienceSessionBean and re-initializes the audience related beans
   * 
   * @return String navigation String
   */
  public String manageAudience() {
    this.getAudienceSessionBean().cleanUp();
    this.init();
    return AudienceBean.LOAD_AUDIENCEPAGE;
  }

  /**
   * Returns the number of files with visibility audience (for presentation purpose)
   * 
   * @return number of files with visibility audience
   */
  public int getNumberOfFiles() {
    int numberOfFiles = 0;
    numberOfFiles = this.getAudienceSessionBean().getFileListNew().size();
    return numberOfFiles;
  }


  private ItemControllerSessionBean getItemControllerSessionBean() {
    return (ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean");
  }

  /**
   * Returns all user groups
   * 
   * @return all user groups
   */
  public SelectItem[] getUserGroups() {
    SelectItem[] selectItems = null;
    List<SelectItem> selectItemsList = new ArrayList<SelectItem>();

    if (this.getUserGroupList() != null) {
      // the first and empty list entry
      SelectItem selectItem = new SelectItem("", "-");
      selectItemsList.add(selectItem);
      for (int i = 0; i < this.getUserGroupList().size(); i++) {
        if (this.getUserGroupList().get(i) != null
            && this.getUserGroupList().get(i).getName() != null
            && !this.getUserGroupList().get(i).getName().contains("Yearbook User Group for")) {
          selectItem =
              new SelectItem(this.getUserGroupList().get(i).getObjid(), this.getUserGroupList()
                  .get(i).getName());
          selectItemsList.add(selectItem);
        }
      }
      selectItems = new SelectItem[selectItemsList.size()];
      for (int k = 0; k < selectItemsList.size(); k++) {
        selectItems[k] = selectItemsList.get(k);
      }
    } else {
      selectItems = new SelectItem[1];
      // the first and empty list entry
      SelectItem selectItem = new SelectItem("", "-");
      selectItems[0] = selectItem;
    }
    return selectItems;
  }

  public String addGrantForAllFiles() {
    GrantVO newGrant = new GrantVO();
    // newGrant.setObjid("");
    newGrant.setGrantType(GrantVOPresentation.GRANT_TYPE_USER_GROUP);
    // TODO set role for INGe
    // newGrant.setRole(Grant.CoreserviceRole.AUDIENCE.getRoleId());
    this.getGrantsForAllFiles().add(
        new GrantVOPresentation(newGrant, this.getAudienceSessionBean().getGrantsForAllFiles()
            .size()));
    return AudienceBean.LOAD_AUDIENCEPAGE;
  }

  /**
   * This method applies all grants to every file listed
   * 
   * @return String navigation string
   */
  public String applyForAll() {
    // AudienceSessionBean asb = this.getAudienceSessionBean();
    // if(this.getAudienceSessionBean().getGrantsForAllFiles().size() > 0 &&
    // !this.getAudienceSessionBean().getGrantsForAllFiles().get(0).getGrant().getGrantedTo().trim().equals(""))
    // {
    for (int i = 0; i < this.getAudienceSessionBean().getFileListNew().size(); i++) {
      // first remove all existing grants
      this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().clear();
      // then add the new grants
      for (int j = 0; j < this.getAudienceSessionBean().getGrantsForAllFiles().size(); j++) {
        if (this.getAudienceSessionBean().getGrantsForAllFiles().get(j).getGrant().getGrantedTo() != null
            && !this.getAudienceSessionBean().getGrantsForAllFiles().get(j).getGrant()
                .getGrantedTo().trim().equals("")) {
          GrantVO newGrant = new GrantVO();
          newGrant.setObjectRef(this.getAudienceSessionBean().getFileListNew().get(i).getFile()
              .getReference().getObjectId());
          newGrant.setGrantedTo(this.getAudienceSessionBean().getGrantsForAllFiles().get(j)
              .getGrant().getGrantedTo());
          newGrant.setGrantType(this.getAudienceSessionBean().getGrantsForAllFiles().get(j)
              .getGrant().getGrantType());
          newGrant.setRole(this.getAudienceSessionBean().getGrantsForAllFiles().get(j).getGrant()
              .getRole());
          newGrant.setGrantedTo(this.getAudienceSessionBean().getGrantsForAllFiles().get(j)
              .getGrant().getGrantedTo());
          this.getAudienceSessionBean()
              .getFileListNew()
              .get(i)
              .getGrantList()
              .add(
                  new GrantVOPresentation(newGrant, this.getAudienceSessionBean().getFileListNew()
                      .get(i).getGrantList().size(), i));
        } else {
          // ensure that at least one grant is in the list (for presentation)
          if (this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().size() == 0) {
            this.getAudienceSessionBean()
                .getFileListNew()
                .get(i)
                .getGrantList()
                .add(
                    new GrantVOPresentation(new GrantVO(), this.getAudienceSessionBean()
                        .getFileListNew().get(i).getGrantList().size(), i));
          }
        }
      }
    }
    // }
    return AudienceBean.LOAD_AUDIENCEPAGE;
  }

  /**
   * This method saves new grants and revokes grants according to the changes made by the user
   * 
   * @return String navigation string to the View item page
   */
  public String save() {
    boolean error = false;
    // LoginHelper loginHelper = (LoginHelper) FacesTools.findBean(LoginHelper.class);
    // AudienceSessionBean asb = this.getAudienceSessionBean();

    // first clean up unnecessary grants (empty grants for presentation)
    // old list
    for (int i = 0; i < this.getAudienceSessionBean().getFileListOld().size(); i++) {
      for (int j = this.getAudienceSessionBean().getFileListOld().get(i).getGrantList().size(); j > 0; j--) {
        if (this.getAudienceSessionBean().getFileListOld().get(i).getGrantList().get(j - 1)
            .getGrant().getObjectRef() == null
            || this.getAudienceSessionBean().getFileListOld().get(i).getGrantList().get(j - 1)
                .getGrant().getObjectRef().trim().equals("")) {
          this.getAudienceSessionBean().getFileListOld().get(i).getGrantList().remove(j - 1);
        }
      }
    }

    // new list
    for (int i = 0; i < this.getAudienceSessionBean().getFileListNew().size(); i++) {
      for (int j = this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().size(); j > 0; j--) {
        if (this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().get(j - 1)
            .getGrant().getObjectRef() == null
            || this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().get(j - 1)
                .getGrant().getObjectRef().trim().equals("")) {
          this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().remove(j - 1);
        }
      }
    }

    // First look for grants to be revoked (which are available in the old list but do not exist in
    // the new list anymore) or changed
    for (int i = 0; i < this.getAudienceSessionBean().getFileListOld().size(); i++) {
      // List<GrantVOPresentation> grants =
      // this.getAudienceSessionBean().getFileListOld().get(i).getGrantList();
      List<GrantVOPresentation> grantsToRevoke =
          this.getAudienceSessionBean().getFileListOld().get(i).getGrantList();
      List<GrantVOPresentation> grantsToCreate = new ArrayList<GrantVOPresentation>();
      // go through the grants
      for (int j = 0; j < this.getAudienceSessionBean().getFileListNew().get(i).getGrantList()
          .size(); j++) {
        // check if there is an object id (if not, the grant MUST be completely new!)
        // TODO realize for INGe
        /*
         * if
         * (this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().get(j).getGrant()
         * .getObjid() != null &&
         * !this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().get(j)
         * .getGrant().getObjid().trim().equals("")) { // compare with the grants in the new list
         * (corresponding file) for (int k = 0; k < grants.size(); k++) { if (grants .get(k)
         * .getGrant() .getObjid() .equals(
         * this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().get(j)
         * .getGrant().getObjid())) { // check if user group has been changed if (grants .get(k)
         * .getGrant() .getGrantedTo() .equals(
         * this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().get(j)
         * .getGrant().getGrantedTo())) { // If user group has NOT changed, remove Grant from grants
         * to be revoked grantsToRevoke.remove(k); } // If user group has changed, revoke old grant
         * and add new grant, except if no user // group is selected for new grant else if
         * (this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().get(j)
         * .getGrant().getGrantedTo() != null &&
         * !this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().get(j)
         * .getGrant().getGrantedTo().equals("")) { // grantsToRevoke.remove(k);
         * grantsToCreate.add(this.getAudienceSessionBean().getFileListNew().get(i)
         * .getGrantList().get(j)); } } } }else { if
         * (this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().get(j)
         * .getGrant().getGrantedTo() != null &&
         * !this.getAudienceSessionBean().getFileListNew().get(i).getGrantList().get(j)
         * .getGrant().getGrantedTo().equals("")) {
         * grantsToCreate.add(this.getAudienceSessionBean().getFileListNew().get(i).getGrantList()
         * .get(j)); }
         * 
         * }
         */
      }

      // revoke the grants to be revoked
      if (grantsToRevoke != null) {
        for (int l = 0; l < grantsToRevoke.size(); l++) {
          try {
            // TODO INGe connection
            // grantsToRevoke.get(l).getGrant()
            // .revokeInCoreservice(loginHelper.getESciDocUserHandle(), DUMMY_REVOKE_COMMENT);

          } catch (RuntimeException e) {
            logger.error("Error while revoking grant: ", e);
            error(getMessage("AudienceErrorRevokingGrant"));
            error = true;
          }
        }
      }
      // create grants that have been changed
      if (grantsToCreate != null && !error) {
        for (int m = 0; m < grantsToCreate.size(); m++) {
          try {
            // TODO INGe connection
            // grantsToCreate.get(m).getGrant()
            // .createInCoreservice(loginHelper.getESciDocUserHandle(), DUMMY_CREATE_COMMENT);
          } catch (RuntimeException rE) {
            logger.error("Error while creating grant: ", rE);
            error(getMessage("AudienceErrorAssigningGrant"));
            error = true;
          }
        }
      }
    }

    this.getAudienceSessionBean().cleanUp();
    if (!error) {
      info(getMessage("AudienceSuccessfullyChanged"));
    }
    return ViewItemFull.LOAD_VIEWITEM;
  }

  public String cancel() {
    this.getAudienceSessionBean().cleanUp();
    return ViewItemFull.LOAD_VIEWITEM;
  }

  /**
   * Returns the URL of the coreservice this PubMan instance is currently working with
   * 
   * @return String URL of the coreservice
   */
  public String getFwUrl() {
    return PropertyReader.getProperty("escidoc.framework_access.login.url");
  }

  public String getItemPattern() {
    String itemPattern = "";

    String pubmanUrl =
        PropertyReader.getProperty("escidoc.pubman.instance.url")
            + PropertyReader.getProperty("escidoc.pubman.instance.context.path");

    itemPattern =
        PropertyReader.getProperty("escidoc.pubman.item.pattern").replaceAll(
            "\\$1",
            this.getItemControllerSessionBean().getCurrentPubItem().getVersion()
                .getObjectIdAndVersion());

    if (!pubmanUrl.endsWith("/"))
      pubmanUrl = pubmanUrl + "/";
    if (itemPattern.startsWith("/"))
      itemPattern = itemPattern.substring(1, itemPattern.length());

    return itemPattern;
  }


  /**
   * Returns the AudienceSessionBean.
   * 
   * @return a reference to the scoped data bean (AudienceSessionBean)
   */
  private AudienceSessionBean getAudienceSessionBean() {
    return (AudienceSessionBean) FacesTools.findBean("AudienceSessionBean");
  }

  public List<UserGroupVO> getUserGroupList() {
    return this.getAudienceSessionBean().getUgl();
  }

  public void setUserGroupList(List<UserGroupVO> ugl) {
    this.getAudienceSessionBean().setUgl(ugl);
  }

  public List<PubFileVOPresentation> getFileList() {
    return this.getAudienceSessionBean().getFileListNew();
  }

  public void setFileList(List<PubFileVOPresentation> fileList) {
    this.getAudienceSessionBean().setFileListNew(fileList);
  }

  public List<GrantVOPresentation> getGrantsForAllFiles() {
    return this.getAudienceSessionBean().getGrantsForAllFiles();
  }

  public void setGrantsForAllFiles(List<GrantVOPresentation> grantsForAllFiles) {
    this.getAudienceSessionBean().setGrantsForAllFiles(grantsForAllFiles);
  }
}
