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

package de.mpg.mpdl.inge.pubman.web.affiliation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import de.mpg.mpdl.inge.pubman.OrganizationalUnitService;
import de.mpg.mpdl.inge.pubman.web.qaws.QAWSSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.AffiliationVOPresentation;

/**
 * Request bean to handle the organizational unit tree.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@ManagedBean(name = "AffiliationTree")
@SessionScoped
@SuppressWarnings("serial")
public class AffiliationTree extends FacesBean {
  private List<AffiliationVOPresentation> affiliations;
  private long timestamp;
  private List<SelectItem> affiliationSelectItems;
  private Map<String, AffiliationVOPresentation> affiliationMap;
  

  boolean started = false;

  public AffiliationTree() throws Exception {
    this.affiliationMap = new HashMap<String, AffiliationVOPresentation>();
    this.affiliations =
        CommonUtils.convertToAffiliationVOPresentationList(OrganizationalUnitService.getInstance().searchTopLevelOrganizations());
    this.timestamp = new Date().getTime();
  }

  public List<AffiliationVOPresentation> getAffiliations() {
    return this.affiliations;
  }

  public void setAffiliations(List<AffiliationVOPresentation> affiliations) {
    this.affiliations = affiliations;
  }

  
  /**
   * Is called from JSF to reload the ou data.
   * 
   * @return Just a dummy message
   * @throws Exception Any exception
   */
  public String getResetMessage() throws Exception {
    this.affiliations =
        CommonUtils.convertToAffiliationVOPresentationList(OrganizationalUnitService.getInstance().searchTopLevelOrganizations());
    this.affiliationSelectItems = null;
    this.timestamp = new Date().getTime();
    return this.getMessage("Affiliations_reloaded");
  }

  public long getTimestamp() {
    return this.timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public void setAffiliationSelectItems(List<SelectItem> affiliationsSelectItem) {
    this.affiliationSelectItems = affiliationsSelectItem;
  }

  /**
   * Returns SelectItems for a menu with all organizational units.
   * 
   * @return
   * @throws Exception
   */
  public List<SelectItem> getAffiliationSelectItems() throws Exception {



    if (this.affiliationSelectItems == null) {

      if (this.started) {
        while (this.affiliationSelectItems == null) {
          Thread.sleep(1000);
        }
      } else {
        this.started = true;

        final List<SelectItem> list = new ArrayList<SelectItem>();
        list.add(new SelectItem("all", this.getLabel("EditItem_NO_ITEM_SET")));

        final List<AffiliationVOPresentation> topLevelAffs = this.getAffiliations();
        this.addChildAffiliationsToMenu(topLevelAffs, list, 0);

        this.affiliationSelectItems = list;

        ((QAWSSessionBean) FacesTools.findBean("QAWSSessionBean"))
            .setOrgUnitSelectItems(this.affiliationSelectItems);
      }
    }

    return this.affiliationSelectItems;
  }

  /**
   * Adds the list of the given affiliations to the filter select.
   * 
   * @param affs
   * @param affSelectItems
   * @param level
   * @throws Exception
   */
  private void addChildAffiliationsToMenu(List<AffiliationVOPresentation> affs,
      List<SelectItem> affSelectItems, int level) throws Exception {
    if (affs == null) {
      return;
    }

    String prefix = "";
    for (int i = 0; i < level; i++) {
      // 2 save blanks
      prefix += '\u00A0';
      prefix += '\u00A0';
      prefix += '\u00A0';
    }
    // 1 right angle
    prefix += '\u2514';
    for (final AffiliationVOPresentation aff : affs) {
      affSelectItems.add(new SelectItem(aff.getReference().getObjectId(), prefix + " "
          + aff.getName()));
      this.affiliationMap.put(aff.getReference().getObjectId(), aff);
      if (aff.getChildren() != null) {
        this.addChildAffiliationsToMenu(aff.getChildren(), affSelectItems, level + 1);
      }
    }
  }

  public void setAffiliationMap(Map<String, AffiliationVOPresentation> affiliationMap) {
    this.affiliationMap = affiliationMap;
  }

  /**
   * Returns a Map that contains all affiliations with their id as key. Only fully available if
   * getAffiliationSelectItems() is called before.
   * 
   * @return
   */
  public Map<String, AffiliationVOPresentation> getAffiliationMap() {
    return this.affiliationMap;
  }
}
