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

package de.mpg.mpdl.inge.pubman.web;

import de.mpg.mpdl.inge.pubman.web.appbase.BreadcrumbPage;

/**
 * viewItemPage.java Backing bean for viewItemPage.jsp Created on 24. Januar 2007, 18:15 Copyright
 * Tobias Schraut Revised by ScT: 23.08.2007
 */
@SuppressWarnings("serial")
public class ViewItemPage extends BreadcrumbPage {
  public static final String BEAN_NAME = "ViewItemPage";
  
  // The referring GUI Tool Page
//  public final static String GT_VIEW_ITEM_PAGE = "GTviewItemPage.jsp";
  
//  private static final String PARAMETERNAME_ITEM_ID = "itemId";

  /**
   * Public constructor.
   */
  public ViewItemPage() {
    this.init();
  }

  /**
   * Callback method that is called whenever a page containing this page fragment is navigated to,
   * either directly via a URL, or indirectly via page navigation.
   */
  public void init() {
    // Perform initializations inherited from our superclass
    super.init();
    // Try to get the request parameter (item ID) out of the faces context
//    FacesContext fc = FacesContext.getCurrentInstance();
//    HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
//    String itemID = request.getParameter(ViewItemPage.PARAMETERNAME_ITEM_ID);
    // initialize viewItem

//    // insert the itemID into the view item session bean
//    if (itemID != null && !itemID.equals("")) {
//      this.getViewItemSessionBean().setItemIdViaURLParam(itemID);
//    }
  }

//  /**
//   * Redirets to the referring GUI Tool page.
//   * 
//   * @return a navigation string
//   */
//  protected String redirectToGUITool() {
//    FacesContext fc = FacesContext.getCurrentInstance();
//    try {
//      fc.getExternalContext().redirect(GT_VIEW_ITEM_PAGE);
//    } catch (IOException e) {
//      logger.error("Could not redirect to GUI Tool View item page." + "\n" + e.toString());
//    }
//    return "";
//  }

//  /**
//   * Returns a reference to the scoped data bean (the ViewItemSessionBean).
//   * 
//   * @return a reference to the scoped data bean
//   */
//  protected ViewItemSessionBean getViewItemSessionBean() {
//    return (ViewItemSessionBean) getSessionBean(ViewItemSessionBean.class);
//  }

  @Override
  public boolean isItemSpecific() {
    return true;
  }
}
