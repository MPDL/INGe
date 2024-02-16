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

package de.mpg.mpdl.inge.pubman.web.easySubmission;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.pubman.web.editItem.CreatorBean;
import de.mpg.mpdl.inge.pubman.web.editItem.EditItemBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubFileVOPresentation;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.bean.SessionScoped;
import jakarta.faces.model.SelectItem;

/**
 * Fragment class for the easy submission. This class provides all functionality for editing, saving
 * and submitting a PubItem within the easy submission process.
 *
 * @author: Tobias Schraut, created 04.04.2008
 * @version: $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "EasySubmissionSessionBean")
@SessionScoped
@SuppressWarnings("serial")
public class EasySubmissionSessionBean extends EditItemBean {
  public static final String SUBMISSION_METHOD_MANUAL = "MANUAL";
  public static final String SUBMISSION_METHOD_FETCH_IMPORT = "FETCH_IMPORT";

  public static final String ES_STEP1 = "STEP1";
  public static final String ES_STEP2 = "STEP2";
  public static final String ES_STEP3 = "STEP3";
  public static final String ES_STEP4 = "STEP4";
  public static final String ES_STEP5 = "STEP5";

  public static final String FULLTEXT_NONE = "NONE";
  public static final String FULLTEXT_ALL = "ALL";
  public static final String FULLTEXT_DEFAULT = "FORMAT";

  private static final String DATE_PUBLISHED_IN_PRINT = "DATE_PUBLISHED_IN_PRINT";

  private String currentSubmissionMethod = EasySubmissionSessionBean.SUBMISSION_METHOD_MANUAL;
  private String currentSubmissionStep = EasySubmissionSessionBean.ES_STEP1;
  private String currentDateType = EasySubmissionSessionBean.DATE_PUBLISHED_IN_PRINT;

  private OrganizationVO currentlySelecting = null;
  private ContextDbVO context;

  private List<PubFileVOPresentation> files = new ArrayList<>();
  private List<PubFileVOPresentation> locators = new ArrayList<>();

  private String selectedDate;

  private String currentExternalServiceType = "";
  private String currentFTLabel = "";
  private boolean fulltext = true;
  private String radioSelectFulltext;
  private boolean importSourceRefresh = false;

  private SelectItem[] FULLTEXT_OPTIONS;
  private SelectItem[] EXTERNAL_SERVICE_OPTIONS;

  private String genreBundle = "Genre_ARTICLE";

  /**
   * A creator bean that holds the data from the author copy&paste organizations
   */
  private CreatorBean authorCopyPasteOrganizationsCreatorBean;

  /**
   * Stores a string from a hidden input field (set by javascript) that indicates whether the author
   * copy&paste elements are to be displayed or not.
   */
  private String showAuthorCopyPaste;
  private String creatorParseString;

  public EasySubmissionSessionBean() {
    this.currentSubmissionStep = EasySubmissionSessionBean.ES_STEP1;
    this.importSourceRefresh = false;
    this.initAuthorCopyPasteCreatorBean();
  }

  /**
   * This method cleans up the EasySubmissionSessionBean
   *
   */
  public void cleanup() {
    super.clean();

    this.getFiles().clear();
    this.getLocators().clear();
    this.setGenreBundle("Genre_ARTICLE");
    this.setSelectedDate("");
    this.initAuthorCopyPasteCreatorBean();
    this.setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP3);
  }

  public void checkMinAnzLocators() {
    if (this.getLocators().isEmpty()
        || !this.getLocators().isEmpty() && this.getLocators().get(this.getLocators().size() - 1).getFile().getSize() > 0) {
      final FileDbVO newLocator = new FileDbVO();
      newLocator.setMetadata(new MdsFileVO());
      newLocator.setStorage(FileDbVO.Storage.EXTERNAL_URL);
      this.getLocators().add(new PubFileVOPresentation(0, newLocator, true));
    }
  }

  public String getCurrentSubmissionMethod() {
    return this.currentSubmissionMethod;
  }

  public void setCurrentSubmissionMethod(String currentSubmissionMethod) {
    this.currentSubmissionMethod = currentSubmissionMethod;
  }

  public String getCurrentSubmissionStep() {
    return this.currentSubmissionStep;
  }

  public void setCurrentSubmissionStep(String currentSubmissionStep) {
    this.currentSubmissionStep = currentSubmissionStep;
  }

  public ContextDbVO getContext() {
    return this.context;
  }

  public void setContext(ContextDbVO context) {
    this.context = context;
  }

  public List<PubFileVOPresentation> getFiles() {
    return this.files;
  }

  public void setFiles(List<PubFileVOPresentation> files) {
    this.files = files;
  }

  public List<PubFileVOPresentation> getLocators() {
    return this.locators;
  }

  public void setLocators(List<PubFileVOPresentation> locators) {
    this.locators = locators;
  }

  public String getCurrentDateType() {
    return this.currentDateType;
  }

  public void setCurrentDateType(String currentDateType) {
    this.currentDateType = currentDateType;
  }

  public String getCurrentExternalServiceType() {
    return this.currentExternalServiceType;
  }

  public void setCurrentExternalServiceType(String currentExternalServiceType) {
    this.currentExternalServiceType = currentExternalServiceType;
  }

  public OrganizationVO getCurrentlySelecting() {
    return this.currentlySelecting;
  }

  public void setCurrentlySelecting(OrganizationVO currentlySelecting) {
    this.currentlySelecting = currentlySelecting;
  }

  public String getSelectedDate() {
    return this.selectedDate;
  }

  public void setSelectedDate(String selectedDate) {
    this.selectedDate = selectedDate;
  }

  public String getCurrentFTLabel() {
    return this.currentFTLabel;
  }

  public void setCurrentFTLabel(String currentFTLabel) {
    this.currentFTLabel = currentFTLabel;
  }

  public boolean isImportSourceRefresh() {
    return this.importSourceRefresh;
  }

  public void setImportSourceRefresh(boolean importSourceRefresh) {
    this.importSourceRefresh = importSourceRefresh;
  }

  public SelectItem[] getFULLTEXT_OPTIONS() {
    // TODO Workaround cause Labels are set before the language change is done. Could be done better
    // if there are performance issues (see EasySubmission.java)
    for (SelectItem fulltextOption : this.FULLTEXT_OPTIONS) {
      if (fulltextOption.getValue().equals(EasySubmissionSessionBean.FULLTEXT_ALL)) {
        fulltextOption.setLabel(this.getLabel("easy_submission_lblFulltext_all"));
      } else if (fulltextOption.getValue().equals(EasySubmissionSessionBean.FULLTEXT_NONE)) {
        fulltextOption.setLabel(this.getLabel("easy_submission_lblFulltext_none"));
      } else {
        fulltextOption.setLabel(this.getCurrentFTLabel());
      }
    }
    return this.FULLTEXT_OPTIONS;
  }

  public void setFULLTEXT_OPTIONS(SelectItem[] fulltext_options) {
    this.FULLTEXT_OPTIONS = fulltext_options;
  }

  public SelectItem[] getEXTERNAL_SERVICE_OPTIONS() {
    return this.EXTERNAL_SERVICE_OPTIONS;
  }

  public void setEXTERNAL_SERVICE_OPTIONS(SelectItem[] external_service_options) {
    this.EXTERNAL_SERVICE_OPTIONS = external_service_options;
  }

  public String getRadioSelectFulltext() {
    return this.radioSelectFulltext;
  }

  public void setRadioSelectFulltext(String radioSelectFulltext) {
    this.radioSelectFulltext = radioSelectFulltext;
  }

  public String getGenreBundle() {
    return this.genreBundle;
  }

  public void setGenreBundle(String genreBundle) {
    this.genreBundle = genreBundle;
  }

  public void setAuthorCopyPasteOrganizationsCreatorBean(CreatorBean authorCopyPasteOrganizationsCreatorBean) {
    this.authorCopyPasteOrganizationsCreatorBean = authorCopyPasteOrganizationsCreatorBean;
  }

  public CreatorBean getAuthorCopyPasteOrganizationsCreatorBean() {
    return this.authorCopyPasteOrganizationsCreatorBean;
  }

  @Override
  public void setShowAuthorCopyPaste(String showAuthorCopyPaste) {
    this.showAuthorCopyPaste = showAuthorCopyPaste;
  }

  @Override
  public String getShowAuthorCopyPaste() {
    return this.showAuthorCopyPaste;
  }

  @Override
  public String getCreatorParseString() {
    return this.creatorParseString;
  }

  @Override
  public void setCreatorParseString(String creatorParseString) {
    this.creatorParseString = creatorParseString;
  }

  public boolean isFulltext() {
    return this.fulltext;
  }

  public void setFulltext(boolean fulltext) {
    this.fulltext = fulltext;
  }

  /**
   * (Re)-initializes the PersonOPrganisationManager that manages the author copy&paste
   * organizations.
   */
  public void initAuthorCopyPasteCreatorBean() {
    this.setShowAuthorCopyPaste("");
  }
}
