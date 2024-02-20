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

package de.mpg.mpdl.inge.pubman.web.util.vos;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;

import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.FormatVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;
import de.mpg.mpdl.inge.pubman.web.easySubmission.EasySubmission;
import de.mpg.mpdl.inge.pubman.web.easySubmission.EasySubmissionSessionBean;
import de.mpg.mpdl.inge.pubman.web.editItem.EditItemSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import jakarta.faces.event.AjaxBehaviorEvent;

/**
 * Presentation wrapper for {@link FileDbVO}.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
@SuppressWarnings("serial")
public class PubFileVOPresentation extends FacesBean {
  private static Properties properties;

  private FileDbVO file;
  //  private List<GrantVOPresentation> grantList = new ArrayList<GrantVOPresentation>();
  private String fileType;
  private boolean isLocator = false;
  private int index;

  public PubFileVOPresentation() {
    this.file = new FileDbVO();
    this.file.setStorage(FileDbVO.Storage.INTERNAL_MANAGED);
    this.init();
  }

  public PubFileVOPresentation(int fileIndex, boolean isLocator) {
    this.file = new FileDbVO();
    this.index = fileIndex;
    this.isLocator = isLocator;
    if (isLocator) {
      this.file.setStorage(FileDbVO.Storage.EXTERNAL_URL);
    } else {
      this.file.setStorage(FileDbVO.Storage.INTERNAL_MANAGED);
    }
    this.init();
  }

  public PubFileVOPresentation(int fileIndex, FileDbVO file) {
    this.index = fileIndex;
    this.file = file;

    this.init();
  }

  public PubFileVOPresentation(int fileIndex, FileDbVO file, boolean isLocator) {
    this.index = fileIndex;
    this.file = file;
    this.isLocator = isLocator;

    this.init();
  }

  public void init() {
    this.setVisibility();
    this.setOaStatus();
  }

  /**
   * get all available content categories as Map for this (server-) instance, depending on the
   * content_categories.properties definitions
   *
   * @return Map filled with all content Categories
   */
  public static Map<String, String> getContentCategoryMap() {
    if (null == PubFileVOPresentation.properties || PubFileVOPresentation.properties.isEmpty()) {
      PubFileVOPresentation.properties = PubFileVOPresentation.loadContentCategoryProperties();
    }
    @SuppressWarnings({"unchecked", "rawtypes"})
    final Map<String, String> propertiesMap = new HashMap<String, String>((Map) PubFileVOPresentation.properties);

    return propertiesMap;
  }

  /**
   *
   * @param key for which the content category URI will be returned
   * @return URI depending on the key of the content category
   */
  public static String getContentCategoryUri(String key) {
    if (null == PubFileVOPresentation.properties || PubFileVOPresentation.properties.isEmpty()) {
      PubFileVOPresentation.properties = PubFileVOPresentation.loadContentCategoryProperties();
    }

    final String value = PubFileVOPresentation.properties.getProperty(key.toLowerCase());
    if (null != value) {
      return value;
    }

    // this.error("There is no such content category defined (" + key + ")");
    LogManager.getLogger(PubFileVOPresentation.class)
        .warn("WARNING: content-category \"" + key + "\" has not been defined valid in Genres.xml");

    return null;
  }

  /**
   * get all available content categories as properties for this (server-) instance, depending on
   * the content_categories.properties definitions
   *
   * @return Properties filled with all content Categories
   */
  private static Properties loadContentCategoryProperties() {
    PubFileVOPresentation.properties = new Properties();
    URL contentCategoryURI = null;
    try {
      contentCategoryURI = PubFileVOPresentation.class.getClassLoader().getResource("content_categories.properties");
      if (null != contentCategoryURI) {
        LogManager.getLogger(PubFileVOPresentation.class).info("Content-category properties URI is " + contentCategoryURI);
        final InputStream in = contentCategoryURI.openStream();
        PubFileVOPresentation.properties.load(in);
        in.close();

        LogManager.getLogger(PubFileVOPresentation.class).info("Content-category properties loaded from " + contentCategoryURI);
      } else {
        LogManager.getLogger(PubFileVOPresentation.class).debug("Content-category properties file not found.");
      }
    } catch (final Exception e) {
      LogManager.getLogger(PubFileVOPresentation.class).warn("WARNING: content-category properties not found: " + e.getMessage());
    }
    return PubFileVOPresentation.properties;
  }

  public int getIndex() {
    return this.index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public FileDbVO getFile() {
    return this.file;
  }

  public void setFile(FileDbVO file) {
    this.file = file;
  }

  public boolean getIsLocator() {
    return this.isLocator;
  }

  public void setLocator(boolean isLocator) {
    this.isLocator = isLocator;
  }

  public String getFileType() {
    return this.fileType;
  }

  /**
   * Returns the content category.
   *
   * @return The internationalized content-category.
   */
  public String getContentCategory() {
    if (null != this.file.getMetadata().getContentCategory()) {
      return this.file.getMetadata().getContentCategory();
    }
    return "";
  }

  /**
   * Returns an internationalized String for the file's content category.
   *
   * @return The internationalized content-category.
   */
  public String getContentCategoryLabel() {
    if (null != this.file.getMetadata().getContentCategory()) {
      return this.getLabel("ENUM_CONTENTCATEGORY_" + this.file.getMetadata().getContentCategory().toLowerCase().replace("_", "-"));
      /*
       * @SuppressWarnings({"unchecked", "rawtypes"}) final Map<String, String> propertiesMap = new
       * HashMap<String, String>((Map) PubFileVOPresentation.properties); for (final
       * Map.Entry<String, String> entry : propertiesMap.entrySet()) { if
       * (entry.getValue().equals(this.file.getContentCategory())) { return
       * this.getLabel("ENUM_CONTENTCATEGORY_" + entry.getKey().toLowerCase().replace("_", "-")); }
       * }
       */
    }

    return "";
  }

  public String getOaStatus() {
    String oaStatus = "";
    if (null == this.file.getMetadata().getOaStatus()) {
      this.file.getMetadata().setOaStatus(MdsFileVO.OA_STATUS.NOT_SPECIFIED);
    }
    oaStatus = this.getLabel(this.getI18nHelper().convertEnumToString(this.file.getMetadata().getOaStatus()));

    return oaStatus;
  }

  private void setOaStatus() {
    if (null == this.file.getMetadata().getOaStatus() && FileDbVO.Visibility.PUBLIC.equals(this.file.getVisibility())) {
      this.file.getMetadata().setOaStatus(MdsFileVO.OA_STATUS.NOT_SPECIFIED);
    } else if (!FileDbVO.Visibility.PUBLIC.equals(this.file.getVisibility())) {
      this.file.getMetadata().setOaStatus(null);
    }
  }

  /**
   * Returns an string according to XML conventions.
   *
   * @return The content category of the file.
   */
  public String getContentCategoryAsXmlString() {
    return this.file.getMetadata().getContentCategory();
  }

  /**
   * Sets the content category of the file.
   *
   * @param category The content category as a string according to XML conventions.
   */
  public void setContentCategoryAsXmlString(String category) {
    this.file.getMetadata().setContentCategory(category);
  }

  /**
   * Return the file size.
   *
   * @return The number of bytes.
   */
  public int getSize() {
    if (null != this.file.getMetadata()) {
      return this.file.getMetadata().getSize();
    }

    return 0;
  }

  public String getDescription() {
    if (null != this.file.getMetadata()) {
      return this.file.getMetadata().getDescription();
    }

    return "";
  }

  public void setDescription(String description) {
    if (null != this.file.getMetadata()) {
      this.file.getMetadata().setDescription(description);
    } else {
      this.file.setMetadata(new MdsFileVO());
      this.file.getMetadata().setDescription(description);
    }
  }

  public String getVisibility() {
    String visibility = "";
    if (null == this.file.getVisibility()) {
      this.file.setVisibility(FileDbVO.Visibility.PUBLIC);
    }
    visibility = this.getLabel(this.getI18nHelper().convertEnumToString(this.file.getVisibility()));

    return visibility;
  }

  private void setVisibility() {
    if (null == this.file.getVisibility()) {
      this.file.setVisibility(FileDbVO.Visibility.PUBLIC);
    }
  }

  public void setMimeType(String mimeType) {
    if (null == this.file.getMetadata()) {
      this.file.setMetadata(new MdsFileVO());
    }

    // set in properties
    this.file.setMimeType(mimeType);

    final List<FormatVO> formats = this.file.getMetadata().getFormats();
    boolean found = false;
    for (final FormatVO formatVO : formats) {
      if ("dcterms:IMT".equals(formatVO.getType())) {
        formatVO.setValue(mimeType);
        found = true;
        break;
      }
    }
    if (!found) {
      final FormatVO formatVO = new FormatVO();
      formatVO.setType("dcterms:IMT");
      formatVO.setValue(mimeType);
      formats.add(formatVO);
    }
  }

  public String getMimeType() {
    if (null == this.file.getMetadata()) {
      return null;
    }

    final List<FormatVO> formats = this.file.getMetadata().getFormats();
    for (final FormatVO formatVO : formats) {
      if ("dcterms:IMT".equals(formatVO.getType())) {
        return formatVO.getValue();
      }
    }

    return null;
  }

  public String getLocator() {
    String locator = "";
    if (this.isLocator) {
      locator = this.file.getContent();
    }

    return locator;
  }

  public void setLocator(String locator) {
    this.file.setContent(locator.trim());
  }

  public void setFileType(String fileType) {
    this.fileType = fileType;
  }

  public void removeFile() {
    final EditItemSessionBean editItemSessionBean = FacesTools.findBean("EditItemSessionBean");

    editItemSessionBean.getFiles().remove(this.index);

    //    // ensure that at least one file component is visible
    //    if (editItemSessionBean.getFiles().size() == 0) {
    //      final FileDbVO newFile = new FileDbVO();
    //      newFile.setMetadata(new MdsFileVO());
    //      newFile.setStorage(FileDbVO.Storage.INTERNAL_MANAGED);
    //      editItemSessionBean.getFiles().add(0, new PubFileVOPresentation(0, newFile, false));
    //    }

    editItemSessionBean.reorganizeFileIndexes();
  }

  public String removeLocatorEditItem() {
    final EditItemSessionBean editItemSessionBean = FacesTools.findBean("EditItemSessionBean");

    editItemSessionBean.getLocators().remove(this.index);

    // ensure that at least one locator component is visible
    if (editItemSessionBean.getLocators().isEmpty()) {
      final FileDbVO newLocator = new FileDbVO();
      newLocator.setMetadata(new MdsFileVO());
      newLocator.setStorage(FileDbVO.Storage.EXTERNAL_URL);
      editItemSessionBean.getLocators().add(0, new PubFileVOPresentation(0, newLocator, true));
    }

    editItemSessionBean.reorganizeLocatorIndexes();

    return "loadEditItem";
  }

  public String removeFileEasySubmission() {
    this.getEasySubmissionSessionBean().getFiles().remove(this.index);
    this.getEasySubmission().reorganizeFileIndexes();
    this.getEasySubmission().init();

    return "loadNewEasySubmission";
  }

  public String removeLocatorEasySubmission() {
    this.getEasySubmissionSessionBean().getLocators().remove(this.index);
    this.getEasySubmission().reorganizeLocatorIndexes();
    this.getEasySubmission().init();

    return "loadNewEasySubmission";
  }

  /**
   * This Method evaluates if the embargo date input filed has to be displayed or not (yes, if
   * visibility is set to private or restricted)
   *
   * @return boolean flag if embargo date input field should be displayed or not
   */
  public boolean getShowEmbargoDate() {
    boolean showEmbargoDate = false;
    if (FileDbVO.Visibility.PRIVATE.equals(this.file.getVisibility()) || FileDbVO.Visibility.AUDIENCE.equals(this.file.getVisibility())) {
      showEmbargoDate = true;
    } else {
      this.file.getMetadata().setEmbargoUntil(null);
      showEmbargoDate = false;
    }

    return showEmbargoDate;
  }

  /**
   * public String addGrant() { GrantVO newGrant = new GrantVO(); newGrant.setObjectRef("");
   * newGrant.setGrantType(GrantVOPresentation.GRANT_TYPE_USER_GROUP);
   * newGrant.setRole(Grant.CoreserviceRole.AUDIENCE.getRoleId());
   * newGrant.setAssignedOn(this.file.getReference().getObjectId()); this.getGrantList().add( new
   * GrantVOPresentation(newGrant, this.getGrantList().size(), this.index)); return
   * AudienceBean.LOAD_AUDIENCEPAGE; }
   */

  /**
   * This method triggers an update on the OA status after the visibility was changed
   *
   * @param event The value change event
   */
  public void visibilityUpdateEvent(AjaxBehaviorEvent event) {
    setOaStatus();
  }

  //  public List<GrantVOPresentation> getGrantList() {
  //    // ensure that at least one grant is in the list (for presentation)
  //    /*
  //     * if(this.grantList.size() == 0) { this.grantList.add(new GrantVOPresentation(new Grant(),
  //     * this.grantList.size(), this.index)); }
  //     */
  //    return this.grantList;
  //  }
  //
  //  public void setGrantList(List<GrantVOPresentation> grantList) {
  //    this.grantList = grantList;
  //  }

  protected EasySubmission getEasySubmission() {
    return FacesTools.findBean("EasySubmission");
  }

  private EasySubmissionSessionBean getEasySubmissionSessionBean() {
    return FacesTools.findBean("EasySubmissionSessionBean");
  }
}
