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

package de.mpg.mpdl.inge.pubman.web.qaws;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.bean.SessionScoped;
import jakarta.faces.model.SelectItem;

/**
 * TODO Session Bean for the Quality Assurance Workspace, keeps all attributes
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
@ManagedBean(name = "QAWSSessionBean")
@SessionScoped
@SuppressWarnings("serial")
public class QAWSSessionBean extends FacesBean {
  /** value for the selected collection */
  private String selectedContextId = null;

  // private AffiliationDbVO selectedAffiliationVO;

  /** value for the selected organizational unit */
  private String selectedOUId = null;

  private String selectedItemState = "SUBMITTED";

  private List<ItemVersionVO> pubItemList = new ArrayList<>();

  /**
   * The currently selected context filter.
   */
  private String selectedContext;

  /**
   * The currently selected org unit.
   */
  private String selectedOrgUnit;

  /**
   * A list with the menu entries for the org units filter menu.
   */
  private List<SelectItem> orgUnitSelectItems;


  public QAWSSessionBean() {}

  public List<ItemVersionVO> getPubItemList() {
    return this.pubItemList;
  }

  public void setPubItemList(List<ItemVersionVO> pubItemList) {
    this.pubItemList = pubItemList;
  }

  public String getSelectedContextId() {
    return this.selectedContextId;
  }

  public void setSelectedContextId(String selectedContextId) {
    this.selectedContextId = selectedContextId;
  }

  public String getSelectedOUId() {
    return this.selectedOUId;
  }

  public void setSelectedOUId(String selectedOUId) {
    this.selectedOUId = selectedOUId;
  }

  public String getSelectedItemState() {
    return this.selectedItemState;
  }

  public void setSelectedItemState(String selectedItemState) {
    this.selectedItemState = selectedItemState;
  }

  public String getSelectedContext() {
    return this.selectedContext;
  }

  public void setSelectedContext(String selectedContext) {
    this.selectedContext = selectedContext;
  }

  public String getSelectedOrgUnit() {
    return this.selectedOrgUnit;
  }

  public void setSelectedOrgUnit(String selectedOrgUnit) {
    this.selectedOrgUnit = selectedOrgUnit;
  }

  public List<SelectItem> getOrgUnitSelectItems() {
    return this.orgUnitSelectItems;
  }

  public void setOrgUnitSelectItems(List<SelectItem> orgUnitSelectItems) {
    this.orgUnitSelectItems = orgUnitSelectItems;
  }
}
