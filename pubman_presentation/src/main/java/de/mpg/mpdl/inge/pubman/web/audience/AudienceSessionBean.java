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

import de.mpg.mpdl.inge.model.valueobjects.UserGroupVO;
import de.mpg.mpdl.inge.pubman.web.appbase.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.GrantVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.PubFileVOPresentation;

/**
 * Fragment class for editing the audience grants of files. This class provides all functionality
 * for giving and revoking user group grants for files in session scope.
 * 
 * @author: Tobias Schraut, 2009-05-20
 */
@SuppressWarnings("serial")
public class AudienceSessionBean extends FacesBean {
  public static final String BEAN_NAME = "AudienceSessionBean";
  
  // the original file list with the original grants retrieved from core-service and which will be
  // applied if user clicks on cancel
  private List<PubFileVOPresentation> fileListOld = new ArrayList<PubFileVOPresentation>();
  // the file list where changes are made and which will be applied if user clicks on save
  private List<PubFileVOPresentation> fileListNew = new ArrayList<PubFileVOPresentation>();

  private List<UserGroupVO> ugl;

  private List<GrantVOPresentation> grantsForAllFiles = new ArrayList<GrantVOPresentation>();

  public AudienceSessionBean() {}

  // /**
  // * Callback method that is called whenever a page containing this page fragment is navigated to,
  // * either directly via a URL, or indirectly via page navigation.
  // */
  // public void init() {
  // // Perform initializations inherited from our superclass
  // //super.init();
  // }

  public void cleanUp() {
    this.fileListNew = new ArrayList<PubFileVOPresentation>();
    this.fileListOld = new ArrayList<PubFileVOPresentation>();
    this.grantsForAllFiles = new ArrayList<GrantVOPresentation>();
    this.ugl = null;
  }

  public List<PubFileVOPresentation> getFileListOld() {
    return fileListOld;
  }

  public void setFileListOld(List<PubFileVOPresentation> fileListOld) {
    this.fileListOld = fileListOld;
  }

  public List<PubFileVOPresentation> getFileListNew() {
    return fileListNew;
  }

  public void setFileListNew(List<PubFileVOPresentation> fileListNew) {
    this.fileListNew = fileListNew;
  }

  public List<UserGroupVO> getUgl() {
    return ugl;
  }

  public void setUgl(List<UserGroupVO> ugl) {
    this.ugl = ugl;
  }

  public List<GrantVOPresentation> getGrantsForAllFiles() {
    return grantsForAllFiles;
  }

  public void setGrantsForAllFiles(List<GrantVOPresentation> grantsForAllFiles) {
    this.grantsForAllFiles = grantsForAllFiles;
  }
}
