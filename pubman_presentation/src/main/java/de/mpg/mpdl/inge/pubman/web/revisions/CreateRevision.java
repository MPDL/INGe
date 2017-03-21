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

package de.mpg.mpdl.inge.pubman.web.revisions;

import javax.faces.bean.ManagedBean;
import javax.faces.component.html.HtmlPanelGroup;

import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.pubman.web.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.editItem.EditItem;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.viewItem.ViewItemFull;

/**
 * Fragment class for CreateRevision.
 * 
 * @author: Thomas Diebäcker, created 22.10.2007
 * @version: $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "CreateRevision")
@SuppressWarnings("serial")
public class CreateRevision extends FacesBean {
  public static final String BEAN_NAME = "CreateRevision";

  public static final String LOAD_CREATEREVISION = "loadCreateRevision";
  public static final String LOAD_CHOOSECOLLECTION = "loadChooseCollection";

  HtmlPanelGroup panDynamicRevisionList = new HtmlPanelGroup();
  HtmlPanelGroup panDynamicCollectionList = new HtmlPanelGroup();

  public CreateRevision() {}

  public String confirm() {
    return CreateRevision.LOAD_CHOOSECOLLECTION;
  }

  public String cancel() {
    return ViewItemFull.LOAD_VIEWITEM;
  }

  public String confirmCollectionChoose() {
    ContextVO selectedCollection =
        this.getCollectionListSessionBean().getSelectedDepositorContext();

    if (selectedCollection != null) {
      return this.getItemControllerSessionBean().createNewRevision(EditItem.LOAD_EDITITEM,
          selectedCollection.getReference(), this.getRelationListSessionBean().getPubItemVO(),
          this.getRelationListSessionBean().getRevisionDescription());
    } else {
      return null;
    }
  }

  public String cancelCollectionChoose() {
    this.getRelationListSessionBean()
        .setPubItemVO(this.getRelationListSessionBean().getPubItemVO());

    return CreateRevision.LOAD_CREATEREVISION;
  }

  public HtmlPanelGroup getPanDynamicRevisionList() {
    return this.panDynamicRevisionList;
  }

  public void setPanDynamicRevisionList(HtmlPanelGroup panDynamicRevisionList) {
    this.panDynamicRevisionList = panDynamicRevisionList;
  }

  public HtmlPanelGroup getPanDynamicCollectionList() {
    return this.panDynamicCollectionList;
  }

  public void setPanDynamicCollectionList(HtmlPanelGroup panDynamicCollectionList) {
    this.panDynamicCollectionList = panDynamicCollectionList;
  }

  private RelationListSessionBean getRelationListSessionBean() {
    return (RelationListSessionBean) FacesTools.findBean("RelationListSessionBean");
  }

  private ItemControllerSessionBean getItemControllerSessionBean() {
    return (ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean");
  }

  private ContextListSessionBean getCollectionListSessionBean() {
    return (ContextListSessionBean) FacesTools.findBean("ContextListSessionBean");
  }
}
