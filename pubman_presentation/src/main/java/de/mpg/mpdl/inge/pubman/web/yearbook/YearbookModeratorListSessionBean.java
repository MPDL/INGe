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

package de.mpg.mpdl.inge.pubman.web.yearbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.db.valueobjects.YearbookDbVO;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.FileFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO.OrderFilter;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pubman.web.ErrorPage;
import de.mpg.mpdl.inge.pubman.web.basket.PubItemStorageSessionBean;
import de.mpg.mpdl.inge.pubman.web.common_presentation.BasePaginatorListSessionBean;
import de.mpg.mpdl.inge.pubman.web.export.ExportItems;
import de.mpg.mpdl.inge.pubman.web.export.ExportItemsSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;

/**
 * This session bean implements the BasePaginatorListSessionBean for sortable lists of PubItems.
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@ManagedBean(name = "YearbookModeratorListSessionBean")
@SessionScoped
@SuppressWarnings("serial")
public class YearbookModeratorListSessionBean extends
    BasePaginatorListSessionBean<YearbookDbVO, YearbookModeratorListSessionBean.SORT_CRITERIA> {
  private static final Logger logger = Logger.getLogger(YearbookModeratorListSessionBean.class);

  /**
   * An enumeration that contains the index for the search service and the sorting filter for the
   * eSciDoc ItemHandler for the offered sorting criterias. TODO Description
   * 
   * @author Markus Haarlaender (initial creation)
   * @author $Author$ (last modification)
   * @version $Revision$ $LastChangedDate$
   * 
   */
  public static enum SORT_CRITERIA {
    // Use dummy value "score" for default sorting
    RELEVANCE(null, "", OrderFilter.ORDER_DESCENDING), //
    TITLE("sort.escidoc.publication.title", "/sort/md-records/md-record/publication/title",
        OrderFilter.ORDER_ASCENDING), //
    GENRE("sort.escidoc.genre-without-uri sort.escidoc.publication.degree",
        "/sort/genre-without-uri /sort/md-records/md-record/publication/degree",
        OrderFilter.ORDER_ASCENDING), //
    DATE("sort.escidoc.any-dates", "/sort/any-dates", OrderFilter.ORDER_DESCENDING), //
    CREATOR("sort.escidoc.publication.compound.publication-creator-names",
        "/sort/md-records/md-record/publication/creator/person/family-name",
        OrderFilter.ORDER_ASCENDING), // TODO: Change back to sort.escidoc.complete-name when
                                      // complete name is filled!!
    PUBLISHING_INFO("sort.escidoc.publication.publishing-info.publisher",
        "/sort/md-records/md-record/publication/source/publishing-info/publisher",
        OrderFilter.ORDER_ASCENDING), //
    MODIFICATION_DATE("sort.escidoc.last-modification-date", "/sort/last-modification-date",
        OrderFilter.ORDER_DESCENDING), //
    EVENT_TITLE("sort.escidoc.publication.event.title",
        "/sort/md-records/md-record/publication/event/title", OrderFilter.ORDER_ASCENDING), //
    SOURCE_TITLE("", "/sort/md-records/md-record/publication/source/title",
        OrderFilter.ORDER_ASCENDING), //
    SOURCE_CREATOR("", "/sort/md-records/md-record/publication/source/creator/person/family-name",
        OrderFilter.ORDER_ASCENDING), //
    REVIEW_METHOD("", "/sort/md-records/md-record/publication/review-method",
        OrderFilter.ORDER_ASCENDING), //
    FILE("", "", OrderFilter.ORDER_ASCENDING), //
    CREATION_DATE("sort.escidoc.property.creation-date", "/sort/properties/creation-date",
        OrderFilter.ORDER_ASCENDING), //
    STATE("sort.escidoc.property.version.status", "/sort/properties/version/status",
        OrderFilter.ORDER_ASCENDING), //
    OWNER("sort.escidoc.property.created-by.title", "/properties/created-by/xLinkTitle",
        OrderFilter.ORDER_ASCENDING), //
    COLLECTION("sort.escidoc.context.objid", "/sort/properties/context/xLinkTitle",
        OrderFilter.ORDER_ASCENDING);

    /**
     * The search sorting index
     */
    private String index;

    /**
     * The path to the xml by which a list should be sorted
     */
    private String sortPath;

    /**
     * An additional attribute indicating the default sort order ("ascending" or "descending")
     */
    private String sortOrder;

    SORT_CRITERIA(String index, String sortPath, String sortOrder) {
      this.setIndex(index);
      this.setSortPath(sortPath);
      this.sortOrder = sortOrder;
    }

    /**
     * Sets the sorting search index
     * 
     * @param index
     */
    public void setIndex(String index) {
      this.index = index;
    }

    /**
     * Returns the sorting search index
     * 
     * @return
     */
    public String getIndex() {
      return this.index;
    }

    /**
     * Sets the path to the xml tag by which the list should be sorted. Used in filter of
     * ItemHandler
     * 
     * @param sortPath
     */
    public void setSortPath(String sortPath) {
      this.sortPath = sortPath;
    }

    /**
     * Sets the path to the xml tag by which the list should be sorted. Used in filter of
     * ItemHandler
     * 
     * @return
     */
    public String getSortPath() {
      return this.sortPath;
    }

    /**
     * Sets the sort order. "ascending" or "descending"
     * 
     * @param sortOrder
     */
    public void setSortOrder(String sortOrder) {
      this.sortOrder = sortOrder;
    }

    /**
     * Returns the sort order. "ascending" or "descending"
     * 
     * @param sortOrder
     */
    public String getSortOrder() {
      return this.sortOrder;
    }
  }

  /**
   * The HTTP GET parameter name for the sorting criteria.
   */
  public static String parameterSelectedSortBy = "sortBy";

  /**
   * The HTTP GET parameter name for the sorting order
   */
  public static String parameterSelectedSortOrder = "sortOrder";

  /**
   * A list containing the menu entries of the sorting criteria menu.
   */
  private List<SelectItem> sortBySelectItems;

  /**
   * The currently selected sorting criteria.
   */
  private String selectedSortBy;

  /**
   * The currently selected sort order
   */
  private String selectedSortOrder;

  /**
   * A string indicating the currently selected submenu of a PubItem list.
   */
  private String subMenu = "EXPORT";

  /**
   * A string indicating the currently selected list type of a Pub Item list.
   */
  private String listType = "BIB";

  /**
   * A map containing the references of the currently selected pub items of one page. Used to reset
   * selections after a redirect.
   */
  private final Map<String, String> selectedItemRefs = new HashMap<String, String>();


  public YearbookModeratorListSessionBean() {}

  /**
   * Called by JSF when the items should be sorted by their state. Redirects to the same page with
   * updated GET parameter for sorting.
   * 
   * @return
   */
  public void changeToSortByState() {
    try {
      this.setSelectedSortBy("STATE");
      this.setCurrentPageNumber(1);
      this.setSelectedSortOrder(SORT_CRITERIA.valueOf(this.getSelectedSortBy()).getSortOrder());
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }

  /**
   * Called by JSF when the items should be sorted by their title. Redirects to the same page with
   * updated GET parameter for sorting.
   * 
   * @return
   */
  public void changeToSortByTitle() {
    try {
      this.setSelectedSortBy("TITLE");
      this.setCurrentPageNumber(1);
      this.setSelectedSortOrder(SORT_CRITERIA.valueOf(this.getSelectedSortBy()).getSortOrder());
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }

  /**
   * Called by JSF when the items should be sorted by their genre. Redirects to the same page with
   * updated GET parameter for sorting.
   * 
   * @return
   */
  public void changeToSortByGenre() {
    try {
      this.setSelectedSortBy("GENRE");
      this.setCurrentPageNumber(1);
      this.setSelectedSortOrder(SORT_CRITERIA.valueOf(this.getSelectedSortBy()).getSortOrder());
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }

  /**
   * Called by JSF when the items should be sorted by their date. Redirects to the same page with
   * updated GET parameter for sorting.
   * 
   * @return
   */
  public void changeToSortByDate() {
    try {
      this.setSelectedSortBy("DATE");
      this.setCurrentPageNumber(1);
      this.setSelectedSortOrder(SORT_CRITERIA.valueOf(this.getSelectedSortBy()).getSortOrder());
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }

  /**
   * Called by JSF when the items should be sorted by their creators. Redirects to the same page
   * with updated GET parameter for sorting.
   * 
   * @return
   */
  public void changeToSortByCreator() {
    try {
      this.setSelectedSortBy("CREATOR");
      this.setCurrentPageNumber(1);
      this.setSelectedSortOrder(SORT_CRITERIA.valueOf(this.getSelectedSortBy()).getSortOrder());
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }

  /**
   * Called by JSF when the items should be sorted by their files. Redirects to the same page with
   * updated GET parameter for sorting.
   * 
   * @return
   */
  public void changeToSortByFile() {
    try {
      this.setSelectedSortBy("FILE");
      this.setCurrentPageNumber(1);
      this.setSelectedSortOrder(SORT_CRITERIA.valueOf(this.getSelectedSortBy()).getSortOrder());
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }

  /**
   * Called by JSF when the items should be sorted by their genre. Redirects to the same page with
   * updated GET parameter for sorting.
   * 
   * @return
   */
  public void changeToSortByCreationDate() {
    try {
      this.setSelectedSortBy("CREATION_DATE");
      this.setCurrentPageNumber(1);
      this.setSelectedSortOrder(SORT_CRITERIA.valueOf(this.getSelectedSortBy()).getSortOrder());
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }

  /**
   * Called by JSF when the sort order should be changed from "ascending" to "descending" or vice
   * versa.
   * 
   * @return
   */
  public void changeSortOrder() {
    if (this.selectedSortOrder.equals(OrderFilter.ORDER_ASCENDING)) {
      this.setSelectedSortOrder(OrderFilter.ORDER_DESCENDING);
    } else {
      this.setSelectedSortOrder(OrderFilter.ORDER_ASCENDING);
    }
    try {
      this.setSelectedSortOrder(this.selectedSortOrder);
      this.setCurrentPageNumber(1);
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }

  /**
   * Called by JSF when the sorting criteria should be changed.
   * 
   * @return
   */
  public void changeSortBy() {
    try {
      this.setCurrentPageNumber(1);
      this.setSelectedSortOrder(SORT_CRITERIA.valueOf(this.getSelectedSortBy()).getSortOrder());
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }

  /**
   * Called by JSF when submenu should be changed to the VIEW part
   * 
   * @return
   */
  public void changeSubmenuToView() {
    try {
      this.setSubMenu("VIEW");
      this.setListUpdate(false);
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }

  /**
   * Called by JSF when the submenu should be changed to the EXPORT part
   * 
   * @return
   */
  public void changeSubmenuToExport() {
    try {
      this.setSubMenu("EXPORT");
      this.setListUpdate(false);
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }

  /**
   * Called by JSF when submenu should be changed to the FILTER part
   * 
   * @return
   */
  public void changeSubmenuToFilter() {
    try {
      this.setSubMenu("FILTER");
      this.setListUpdate(false);
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }

  /**
   * Called by JSF when submenu should be changed to the SORTING part
   * 
   * @return
   */
  public void changeSubmenuToSorting() {
    try {
      this.setSubMenu("SORTING");
      this.setListUpdate(false);
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }


  /**
   * Returns true if the current sort order is ascending, false if "descending
   * 
   * @return
   */
  public boolean getIsAscending() {
    return this.selectedSortOrder.equals(OrderFilter.ORDER_ASCENDING);
  }

  /**
   * Sets the menu entries for the sorting criteria menu
   * 
   * @param sortBySelectItems
   */
  public void setSortBySelectItems(List<SelectItem> sortBySelectItems) {
    this.sortBySelectItems = sortBySelectItems;
  }

  /**
   * Returns the menu entries for the sorting criteria menu
   */
  public List<SelectItem> getSortBySelectItems() {
    this.sortBySelectItems = new ArrayList<SelectItem>();

    // the last three should not be in if not logged in
    if (!this.getLoginHelper().isLoggedIn()) {
      for (int i = 0; i < SORT_CRITERIA.values().length - 3; i++) {
        final SORT_CRITERIA sc = SORT_CRITERIA.values()[i];

        // only add if index/sorting path is available
        if ((this.getPageType().equals("SearchResult") && (sc.getIndex() == null || !sc.getIndex()
            .equals("")))
            || (!this.getPageType().equals("SearchResult") && !sc.getSortPath().equals(""))) {
          this.sortBySelectItems.add(new SelectItem(sc.name(), this.getLabel("ENUM_CRITERIA_"
              + sc.name())));
        }
      }
    } else {
      for (int i = 0; i < SORT_CRITERIA.values().length; i++) {
        final SORT_CRITERIA sc = SORT_CRITERIA.values()[i];
        // only add if index/sorting path is available
        if ((this.getPageType().equals("SearchResult") && (sc.getIndex() == null || !sc.getIndex()
            .equals("")))
            || (!this.getPageType().equals("SearchResult") && !sc.getSortPath().equals(""))) {
          this.sortBySelectItems.add(new SelectItem(sc.name(), this.getLabel("ENUM_CRITERIA_"
              + sc.name())));
        }
      }
    }

    return this.sortBySelectItems;
  }


  /**
   * Sets the current sorting criteria
   * 
   * @param selectedSortBy
   */
  public void setSelectedSortBy(String selectedSortBy) {
    this.selectedSortBy = selectedSortBy;
    this.getParameterMap().put(YearbookModeratorListSessionBean.parameterSelectedSortBy,
        selectedSortBy);
  }

  /**
   * Returns the currently selected sorting criteria
   * 
   * @return
   */
  public String getSelectedSortBy() {
    return this.selectedSortBy;
  }

  /**
   * Retu´rns the label in the selected language for the currrently selected sorting criteria
   * 
   * @return
   */
  public String getSelectedSortByLabel() {
    String returnString = "";
    if (!this.getSelectedSortBy().equals("all")) {
      returnString = this.getLabel("ENUM_CRITERIA_" + this.getSelectedSortBy());
    }

    return returnString;
  }

  /**
   * Returns the current sort order ("ascending" or "descending")
   */
  public String getSelectedSortOrder() {
    return this.selectedSortOrder;
  }

  /**
   * Sets the current sort order ("ascending" or "descending")
   * 
   * @param selectedSortOrder
   */
  public void setSelectedSortOrder(String selectedSortOrder) {
    this.selectedSortOrder = selectedSortOrder;
    this.getParameterMap().put(YearbookModeratorListSessionBean.parameterSelectedSortOrder,
        selectedSortOrder);
  }

  /**
   * Reads out additional parmaeters from GET request for sorting criteria and sort order and sets
   * their default values if they are null
   */
  @Override
  protected void readOutParameters() {
    final String sortBy =
        FacesTools.getExternalContext().getRequestParameterMap()
            .get(YearbookModeratorListSessionBean.parameterSelectedSortBy);

    if (sortBy != null) {
      this.setSelectedSortBy(sortBy);
    } else if (this.getSelectedSortBy() != null) {
      // do nothing
    } else {
      // This is commented out due to PUBMAN-1907
      // if(getPageType().equals("SearchResult"))
      // {
      // setSelectedSortBy(SORT_CRITERIA.RELEVANCE.name());
      // }
      // else
      // {
      this.setSelectedSortBy(SORT_CRITERIA.MODIFICATION_DATE.name());
      // }
    }

    final String sortOrder =
        FacesTools.getExternalContext().getRequestParameterMap()
            .get(YearbookModeratorListSessionBean.parameterSelectedSortOrder);
    if (sortOrder != null) {
      this.setSelectedSortOrder(sortOrder);
    } else if (this.getSelectedSortOrder() != null) {
      // do nothing
    } else {
      this.setSelectedSortOrder(OrderFilter.ORDER_DESCENDING);
    }
  }

  /**
   * Returns the currently selected sorting criteria which is used as an additional filter
   */
  @Override
  public SORT_CRITERIA getSortCriteria() {
    final SORT_CRITERIA sc = SORT_CRITERIA.valueOf(this.getSelectedSortBy());
    sc.setSortOrder(this.getSelectedSortOrder());

    return sc;
  }

  /**
   * Sets the submenu
   * 
   * @param subMenu
   */
  public void setSubMenu(String subMenu) {
    this.subMenu = subMenu;
  }

  /**
   * Returns a string describing the curently selected submenu
   * 
   * @return
   */
  public String getSubMenu() {
    return this.subMenu;
  }

  /**
   * Resets the submenus, clears parameters from the map
   */
  @Override
  protected void pageTypeChanged() {

  }



  /**
   * Before any redirect, the references of the currently selected publication items are stored in
   * this session in order to reselct them after the redirect Thus, the selection is not lost.
   */
  @Override
  protected void beforeRedirect() {

  }



  /*
   * @Override protected void saveState() { //saveSelections(); }
   */

  /**
   * Updates the checkboxes of the items on the page after a new list is displayed.
   */
  @Override
  protected void listUpdated() {

  }



  public boolean getDisplaySortOrder() {
    if (SORT_CRITERIA.RELEVANCE.name().equals(this.getSelectedSortBy())) {
      return false;
    }

    return true;
  }

}