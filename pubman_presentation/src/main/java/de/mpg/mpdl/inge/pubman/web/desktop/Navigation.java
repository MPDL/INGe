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

package de.mpg.mpdl.inge.pubman.web.desktop;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.pubman.web.ViewItemRevisionsPage;
import de.mpg.mpdl.inge.pubman.web.affiliation.AffiliationBean;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.createItem.CreateItem;
import de.mpg.mpdl.inge.pubman.web.depositorWS.MyItemsRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.easySubmission.EasySubmission;
import de.mpg.mpdl.inge.pubman.web.editItem.EditItem;
import de.mpg.mpdl.inge.pubman.web.home.Home;
import de.mpg.mpdl.inge.pubman.web.itemLog.ViewItemLog;
import de.mpg.mpdl.inge.pubman.web.releases.ItemVersionListSessionBean;
import de.mpg.mpdl.inge.pubman.web.releases.ReleaseHistory;
import de.mpg.mpdl.inge.pubman.web.search.AdvancedSearchEdit;
import de.mpg.mpdl.inge.pubman.web.search.SearchRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.NavigationRule;
import de.mpg.mpdl.inge.pubman.web.util.beans.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.viewItem.ViewItemFull;

/**
 * Navigation.java Backing Bean for the Navigation side bar of pubman. Additionally there is some
 * internationalization functionality (language switching).
 * 
 * @author: Tobias Schraut, created 30.05.2007
 * @version: $Revision$ $LastChangedDate$ Revised by ScT: 16.08.2007
 */
@ManagedBean(name = "Navigation")
@SuppressWarnings("serial")
public class Navigation extends FacesBean {
  private static final Logger logger = Logger.getLogger(Navigation.class);

  private List<NavigationRule> navRules;

  // private boolean showExportMenuOption;

  public Navigation() {
    this.init();
  }

  public void init() {
    // initially sets the navigation rules for redirecting after changing the language
    this.navRules = new ArrayList<NavigationRule>();
    this.navRules.add(new NavigationRule("/faces/HomePage.jsp", Home.LOAD_HOME));
    this.navRules.add(new NavigationRule("/faces/DepositorWSPage.jsp",
        MyItemsRetrieverRequestBean.LOAD_DEPOSITORWS));
    this.navRules.add(new NavigationRule("/faces/EditItemPage.jsp", EditItem.LOAD_EDITITEM));
    this.navRules
        .add(new NavigationRule("/faces/ViewItemFullPage.jsp", ViewItemFull.LOAD_VIEWITEM));
    this.navRules.add(new NavigationRule("/faces/SearchResultListPage.jsp",
        SearchRetrieverRequestBean.LOAD_SEARCHRESULTLIST));
    this.navRules.add(new NavigationRule("/faces/AffiliationTreePage.jsp",
        AffiliationBean.LOAD_AFFILIATION_TREE));
    this.navRules.add(new NavigationRule("/faces/ViewItemRevisionsPage.jsp",
        ViewItemRevisionsPage.LOAD_VIEWREVISIONS));
    this.navRules.add(new NavigationRule("/faces/ViewItemReleaseHistoryPage.jsp",
        ReleaseHistory.LOAD_RELEASE_HISTORY));
    this.navRules.add(new NavigationRule("/faces/AdvancedSearchPage.jsp",
        AdvancedSearchEdit.LOAD_SEARCHPAGE));
    this.navRules.add(new NavigationRule("/faces/EasySubmissionPage.jsp",
        EasySubmission.LOAD_EASYSUBMISSION));
  }

  // /**
  // * loads the home page.
  // *
  // * @return String navigation string (JSF navigation) to load the home page.
  // */
  // public String loadHome() {
  // return Home.LOAD_HOME;
  // }

  /**
   * loads the help page.
   * 
   * @return String navigation string (JSF navigation) to load the help page.
   */
  public String loadHelp() {
    return "loadHelp";
  }

  /**
   * Changes the language within the application. Some classes have to be treated especially.
   * 
   * @return String navigation string (JSF navigation) to reload the page the user has been when
   *         changing the language
   */
  public String changeLanguage() {
    // initialize the nav string with null. if it won't be changed the page would just be reloaded
    String navigationString = "";

    // special re-initializaion for pages with dynamic page elements which
    // must be re-inited

    String requestURI = FacesTools.getRequest().getRequestURI();

    if (requestURI.startsWith("/pubman")) {
      requestURI = requestURI.substring("/pubman".length());
    }

    for (int i = 0; i < this.navRules.size(); i++) {
      if (requestURI.equals(this.navRules.get(i).getRequestURL())) {
        navigationString = this.navRules.get(i).getNavigationString();
        break;
      }
    }

    if (navigationString.equals(EditItem.LOAD_EDITITEM)) {
      ((EditItem) FacesTools.findBean("EditItem")).init();
    } else if (navigationString.equals(ViewItemFull.LOAD_VIEWITEM)) {
      ((ViewItemFull) FacesTools.findBean("ViewItemFull")).init();
      // } else if (navigationString.equals(ViewItemRevisionsPage.LOAD_VIEWREVISIONS)) {
      // createRevision = (CreateRevision) getRequestBean(CreateRevision.class);
      // createRevision.init();
    } else if (navigationString.equals(ReleaseHistory.LOAD_RELEASE_HISTORY)) {
      this.getItemVersionSessionBean().resetVersionLists();
      ((ReleaseHistory) FacesTools.findBean("ReleaseHistory")).init();
    } else if (navigationString.equals(ViewItemLog.LOAD_ITEM_LOG)) {
      this.getItemVersionSessionBean().resetVersionLists();
      ((ViewItemLog) FacesTools.findBean("ViewItemLog")).init();
    } else if (navigationString.equals(EasySubmission.LOAD_EASYSUBMISSION)) {
      ((EasySubmission) FacesTools.findBean("EasySubmission")).init();
    } else {
      navigationString = null;
    }

    return navigationString;
  }

  /**
   * Starts a new submission.
   * 
   * @return string, identifying the page that should be navigated to after this methodcall
   */
  public String newSubmission() {
    // if there is only one context for this user we can skip the
    // CreateItem-Dialog and create the new item directly
    if (this.getCollectionListSessionBean().getDepositorContextList().size() == 0) {
      Navigation.logger.warn("The user does not have privileges for any context.");
      return null;
    }

    if (this.getCollectionListSessionBean().getDepositorContextList().size() == 1) {
      final ContextVO contextVO =
          this.getCollectionListSessionBean().getDepositorContextList().get(0);
      return this.getItemControllerSessionBean().createNewPubItem(EditItem.LOAD_EDITITEM,
          contextVO.getReference());
    }

    // more than one context exists for this user; let him choose the right one
    return CreateItem.LOAD_CREATEITEM;
  }

  // public String getFacesParamValue(final String name) {
  // return (String) FacesTools.getExternalContext().getRequestParameterMap().get(name);
  // }

  // public void setShowExportMenuOption(boolean showExportMenuOption) {
  // this.showExportMenuOption = showExportMenuOption;
  // }

  // public boolean getShowExportMenuOption() {
  // return this.showExportMenuOption;
  // }

  private ItemVersionListSessionBean getItemVersionSessionBean() {
    return (ItemVersionListSessionBean) FacesTools.findBean("ItemVersionListSessionBean");
  }

  private ContextListSessionBean getCollectionListSessionBean() {
    return (ContextListSessionBean) FacesTools.findBean("ContextListSessionBean");
  }

  private ItemControllerSessionBean getItemControllerSessionBean() {
    return (ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean");
  }
}
