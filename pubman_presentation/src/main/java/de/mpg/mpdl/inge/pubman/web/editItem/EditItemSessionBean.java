/*
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
package de.mpg.mpdl.inge.pubman.web.editItem;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubFileVOPresentation;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.bean.SessionScoped;

/**
 * Keeps all attributes that are used for the whole session by the EditItem.
 *
 * @author: Tobias Schraut, created 26.02.2007
 * @version: $Revision$ $LastChangedDate: 2007-11-13 10:54:07 +0100 (Di, 13 Nov 2007) $
 */
@ManagedBean(name = "EditItemSessionBean")
@SessionScoped
@SuppressWarnings("serial")
public class EditItemSessionBean extends EditItemBean {
  public static final String SUBMISSION_METHOD_FULL_SUBMISSION = "FULL_SUBMISSION";
  public static final String SUBMISSION_METHOD_EASY_SUBMISSION = "EASY_SUBMISSION";
  public static final String SUBMISSION_METHOD_IMPORT = "IMPORT";

  private List<PubFileVOPresentation> files = new ArrayList<>();

  private boolean filesInitialized = false;

  private List<PubFileVOPresentation> locators = new ArrayList<>();

  private String genreBundle = "Genre_ARTICLE";

  /** The offset of the page where to jump back */
  private String offset;

  private List<SourceBean> sources = new ArrayList<>();

  /**
   * Flag for the GUI to detect if the edit item page is called for a submission or for an editing
   * process
   */
  private String currentSubmission = "";

  public EditItemSessionBean() {}

  /**
   * This method clears the file and the locator list
   */
  public void initEmptyComponents() {
    this.clean();
    this.checkMinAnzLocators();
  }

  public void checkMinAnzLocators() {
    if (this.getLocators().isEmpty()
        || !this.getLocators().isEmpty() && 0 < this.getLocators().get(this.getLocators().size() - 1).getFile().getSize()) {
      final FileDbVO newLocator = new FileDbVO();
      newLocator.setMetadata(new MdsFileVO());
      newLocator.setStorage(FileDbVO.Storage.EXTERNAL_URL);
      this.getLocators().add(new PubFileVOPresentation(0, newLocator, true));
    }
  }

  @Override
  public void clean() {
    super.clean();
    this.files.clear();
    this.locators.clear();
    this.sources.clear();
    this.genreBundle = "";
    this.offset = "";
    this.filesInitialized = false;
  }

  public void bindSourcesToBean(List<SourceVO> sourceList) {
    for (final SourceVO sourceVO : sourceList) {
      this.sources.add(new SourceBean(sourceVO, this.sources));
    }
  }

  public void bindSourcesToVO(List<SourceVO> sourceList) {
    sourceList.clear();
    for (final SourceBean sourceBean : this.getSources()) {
      final SourceVO sourceVO = sourceBean.getSource();
      sourceList.add(sourceVO);
    }
  }

  /**
   * This method reorganizes the index property in PubFileVOPresentation after removing one element
   * of the list.
   */
  public void reorganizeFileIndexes() {
    if (null != this.files) {
      for (int i = 0; i < this.files.size(); i++) {
        this.files.get(i).setIndex(i);
      }
    }
  }

  /**
   * This method reorganizes the index property in PubFileVOPresentation after removing one element
   * of the list.
   */
  public void reorganizeLocatorIndexes() {
    if (null != this.locators) {
      for (int i = 0; i < this.locators.size(); i++) {
        this.locators.get(i).setIndex(i);
      }
    }
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

  public String getGenreBundle() {
    return this.genreBundle;
  }

  public void setGenreBundle(String genreBundle) {
    this.genreBundle = genreBundle;
  }

  public void setOffset(String offset) {
    this.offset = offset;
  }

  public String getOffset() {
    return this.offset;
  }

  public List<SourceBean> getSources() {
    return this.sources;
  }

  public void setSources(List<SourceBean> sources) {
    this.sources = sources;
  }

  public String getCurrentSubmission() {
    return this.currentSubmission;
  }

  public void setCurrentSubmission(String currentSubmission) {
    this.currentSubmission = currentSubmission;
  }

  public boolean isFilesInitialized() {
    return this.filesInitialized;
  }

  public void setFilesInitialized(boolean filesInitialized) {
    this.filesInitialized = filesInitialized;
  }
}
