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

import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;

/**
 * Class for single breadcrumbs. Each breadcrumb is represented with this class.
 *
 * @author: Tobias Schraut, created 30.05.2007
 * @version: $Revision$ $LastChangedDate$ Revised by ScT: 16.08.2007
 */
@SuppressWarnings("serial")
public class BreadcrumbItem extends FacesBean {
  private static final Logger logger = LogManager.getLogger(BreadcrumbItem.class);

  // The String that should be displayed in the breadcrumb menu, e.g. "ViewItem"
  private String displayValue;

  // The jsp page that should be addressed when the link in the breadcrumb
  // navigation is clicked, e.g. "ViewItem.jsp"
  private String page;

  // Flag indicating that this item is the last one.
  private boolean isLast = false;

  // Method for default action.
  private Method defaultAction;

  /**
   * Flag for marking the page as directly related to a single item (e.g. EditItemPage). These pages
   * must be handled a little bit different in some cases (e.g. deletion of an item)
   */
  private boolean isItemSpecific = false;

  public BreadcrumbItem() {}

  /**
   * Public constructor(with two parameters, the value to display and the page name that should be
   * displayed). You may only use one of the public static final BreadcrumbItem's defined above.
   */
  public BreadcrumbItem(String displayValue, String page, Method defaultAction, boolean isItemSpecific) {
    this.displayValue = displayValue;
    this.page = page;
    this.defaultAction = defaultAction;
    this.isItemSpecific = isItemSpecific;
  }

  /**
   * Internationalization is supported by this getter.
   *
   * @return displayValue to label this BreadcrumbItem
   */
  public String getPageLabel() {
    return this.getLabel(this.displayValue);
  }

  public String getDisplayValue() {
    return this.displayValue;
  }

  public void setDisplayValue(String displayValue) {
    this.displayValue = displayValue;
  }

  public String getPage() {
    return this.page;
  }

  public void setPage(String page) {
    this.page = page;
  }

  public Method getDefaultAction() {
    return this.defaultAction;
  }

  public void setDefaultAction(Method defaultAction) {
    this.defaultAction = defaultAction;
  }

  @Override
  public String toString() {
    return "BreadcrumbItem [displayValue=" + this.displayValue + ", page=" + this.page + ", isLast=" + this.isLast + ", defaultAction="
        + this.defaultAction + ", isItemSpecific=" + this.isItemSpecific + "]";
  }

  @Override
  public boolean equals(final Object other) {
    if (null == this.page || !(other instanceof BreadcrumbItem)) {
      return false;
    }

    return (this.displayValue.equals(((BreadcrumbItem) other).getDisplayValue()));
  }

  public boolean getIsLast() {
    return this.isLast;
  }

  public void setIsLast(boolean isLast) {
    this.isLast = isLast;
  }

  public String executeDefaultAction() {
    if (null != this.defaultAction) {
      try {
        final Class<?> beanClass = this.defaultAction.getDeclaringClass();
        final Object bean = FacesTools.findBean(beanClass.getName());
        return this.defaultAction.invoke(bean, (Object[]) null).toString();
      } catch (final Exception e) {
        logger.error("Error executing default action", e);
      }
    }

    return null;
  }

  public boolean isItemSpecific() {
    return this.isItemSpecific;
  }

  public void setItemSpecific(boolean isItemSpecific) {
    this.isItemSpecific = isItemSpecific;
  }
}
