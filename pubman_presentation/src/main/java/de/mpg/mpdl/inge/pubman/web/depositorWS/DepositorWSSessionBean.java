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

package de.mpg.mpdl.inge.pubman.web.depositorWS;

import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.bean.SessionScoped;

/**
 * Keeps all attributes that are used for the whole session by the DepositorWS.
 * 
 * @author: Thomas Diebäcker, created 10.01.2007
 * @version: $Revision$ $LastChangedDate$ Revised by DiT: 09.08.2007
 */
@ManagedBean(name = "DepositorWSSessionBean")
@SessionScoped
@SuppressWarnings("serial")
public class DepositorWSSessionBean extends FacesBean {
  /** default value for the selected item state */
  private String selectedItemState = "PENDING";

  /**
   * ScT: the main menu topics on the left side.
   */
  private boolean myWorkspace = false;
  private boolean depositorWS = false;
  private boolean newSubmission = false;

  public DepositorWSSessionBean() {}

  public String getSelectedItemState() {
    return this.selectedItemState;
  }

  public void setSelectedItemState(String selectedItemState) {
    this.selectedItemState = selectedItemState;
  }

  public boolean getDepositorWS() {
    return this.depositorWS;
  }

  public void setDepositorWS(boolean depositorWS) {
    this.depositorWS = depositorWS;
  }

  public boolean getMyWorkspace() {
    return this.myWorkspace;
  }

  public void setMyWorkspace(boolean myWorkspace) {
    this.myWorkspace = myWorkspace;
  }

  public boolean getNewSubmission() {
    return this.newSubmission;
  }

  public void setNewSubmission(boolean newSubmission) {
    this.newSubmission = newSubmission;
  }
}
