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
package de.mpg.mpdl.inge.pubman.web.breadcrumb;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.bean.SessionScoped;

/**
 * BreadcrumbItem history is stored in this session bean for advanced page navigation.
 *
 * @author Mario Wagner
 * @version:
 */
@ManagedBean(name = "BreadcrumbItemHistorySessionBean")
@SessionScoped
@SuppressWarnings("serial")
public class BreadcrumbItemHistorySessionBean extends FacesBean {
  // a List of all pages with item-lists
  private final String[] itemListPages = { //
      "SearchResultListPage", "DepositorWSPage", "QAWSPage", "BatchWorkspacePage", "CartItemsPage"};

  // the List of BreadCrumbs representing JSP's that have been viewed
  private List<BreadcrumbItem> breadcrumbs = new ArrayList<BreadcrumbItem>();

  /**
   * Initializes this BreadcrumbItemHistory.
   */
  public void clear() {
    this.breadcrumbs.clear();
  }

  /**
   * Register will be done smart: if the BreadcrumbItem is already registered, the old
   * BreadcrumbItem will be replaced. AND all following BreadcrumbItem are deleted !!!
   *
   * @param newItem BreadcrumbItem to be added to the history
   */
  public void push(final BreadcrumbItem newItem) {
    if ("HomePage".equals(newItem.getDisplayValue())) {
      this.breadcrumbs.clear();
    }

    BreadcrumbItem lastItem = null;
    boolean keepold = false;

    if (!this.breadcrumbs.isEmpty()) {
      boolean remove = false;

      int position = 0;
      for (int i = 0; i < this.breadcrumbs.size(); i++) {
        lastItem = this.breadcrumbs.get(i);

        lastItem.setIsLast(false);

        if (lastItem.equals(newItem)) {
          // replaces the actual item
          remove = true;
          position = i;

          // this.breadcrumbs.remove(lastItem);

          // in particular for ViewItemFullPage, when an ID is added to the URL
          keepold = lastItem.getPage().startsWith(newItem.getPage()) && !newItem.getPage().contains("itemId=");
        }
      }

      if (remove) {
        lastItem = this.breadcrumbs.get(position);
        boolean specialListTreatment = false;
        // special case for list after watching an item
        if (position < this.breadcrumbs.size() - 1) {
          for (int m = position + 1; m < this.breadcrumbs.size(); m++) {
            for (int k = 0; k < this.itemListPages.length; k++) {

              if (this.breadcrumbs.get(m).getDisplayValue().equals(this.itemListPages[k])
                  && this.breadcrumbs.get(position).getPage().contains("itemId=") && newItem.getPage().contains("itemId=")) {
                specialListTreatment = true;
              }
            }
          }
        }

        if (!specialListTreatment) {
          for (int i = this.breadcrumbs.size() - 1; i >= position; i--) {
            this.breadcrumbs.remove(i);
          }
        } else {
          this.breadcrumbs.remove(position);
          keepold = false;
        }
      }
    }

    if (!keepold) {
      this.breadcrumbs.add(newItem);
    } else {
      this.breadcrumbs.add(lastItem);
    }

    this.breadcrumbs.get(this.breadcrumbs.size() - 1).setIsLast(true);
  }

  /**
   * get and remove the last BreadcrumbItem from history.
   *
   * @return BreadcrumbItem
   */
  public BreadcrumbItem pop() {
    return this.get(true);
  }

  public List<BreadcrumbItem> getBreadcrumbs() {
    return this.breadcrumbs;
  }

  public void setBreadcrumbs(List<BreadcrumbItem> breadcrumbs) {
    this.breadcrumbs = breadcrumbs;
  }

  /**
   * get the last BreadcrumbItem from history
   *
   * @return BreadcrumbItem
   */
  public BreadcrumbItem get() {
    return this.get(false);
  }

  private BreadcrumbItem get(boolean remove) {
    BreadcrumbItem returnItem = null;
    final int index = this.breadcrumbs.size() - 1;
    if (index >= 0) {
      returnItem = this.breadcrumbs.get(index);
      if (remove) {
        this.breadcrumbs.remove(index);
      }
    }

    return returnItem;
  }

  public List<BreadcrumbItem> getBreadcrumbItemHistory() {
    // return only the last 3 items of the breadcrumb list
    if (this.breadcrumbs.size() > 3) {
      final List<BreadcrumbItem> breadcrumbsLimited = new ArrayList<BreadcrumbItem>();
      breadcrumbsLimited.add(this.breadcrumbs.get(this.breadcrumbs.size() - 3));
      breadcrumbsLimited.add(this.breadcrumbs.get(this.breadcrumbs.size() - 2));
      breadcrumbsLimited.add(this.breadcrumbs.get(this.breadcrumbs.size() - 1));
      return breadcrumbsLimited;
    } else {
      return this.breadcrumbs;
    }
  }

  public void setBreadcrumbItemHistory(List<BreadcrumbItem> breadcrumbs) {
    this.breadcrumbs = breadcrumbs;
  }

  public BreadcrumbItem getCurrentItem() {
    if (!this.breadcrumbs.isEmpty()) {
      return this.breadcrumbs.get(this.breadcrumbs.size() - 1);
    } else {
      return new BreadcrumbItem("HomePage", "HomePage", null, false);
    }
  }

  public BreadcrumbItem getPreviousItem() {
    if (this.breadcrumbs.size() > 1) {
      return this.breadcrumbs.get(this.breadcrumbs.size() - 2);
    } else {
      return new BreadcrumbItem("HomePage", "HomePage", null, false);
    }
  }

  /**
   * Returns the display value of the last breadcrumb entry. If the breadcrumbs are empty, the
   * 'Homepage' value is returned.
   *
   * @return display value of the last breadcrumb entry
   */
  public String getLastPageIdentifier() {
    if (!this.breadcrumbs.isEmpty()) {
      return this.breadcrumbs.get(this.breadcrumbs.size() - 1).getDisplayValue();
    } else {
      return new BreadcrumbItem("HomePage", "HomePage", null, false).getDisplayValue();
    }
  }

  /**
   * Returns the display value of the last breadcrumb entry. If the breadcrumbs are empty, the
   * 'Homepage' value is returned.
   *
   * @return display value of the last breadcrumb entry
   */
  public boolean getPreviousPageIsListPage() {
    if (this.breadcrumbs.size() > 1) {
      for (int i = 0; i < this.itemListPages.length; i++) {
        if (this.itemListPages[i].equals(this.breadcrumbs.get(this.breadcrumbs.size() - 2).getDisplayValue())) {
          return true;
        } else if ((this.breadcrumbs.size() > 2
            && this.itemListPages[i].equals(this.breadcrumbs.get(this.breadcrumbs.size() - 3).getDisplayValue()))
            && ("ViewItemFullPage".equals(this.breadcrumbs.get(this.breadcrumbs.size() - 2).getDisplayValue())
                || "ViewItemOverviewPage".equals(this.breadcrumbs.get(this.breadcrumbs.size() - 2).getDisplayValue()))
            && ("ViewItemFullPage".equals(this.breadcrumbs.get(this.breadcrumbs.size() - 1).getDisplayValue())
                || "ViewItemOverviewPage".equals(this.breadcrumbs.get(this.breadcrumbs.size() - 1).getDisplayValue()))) {
          return true;
        }
      }
    }

    return false;
  }
}
