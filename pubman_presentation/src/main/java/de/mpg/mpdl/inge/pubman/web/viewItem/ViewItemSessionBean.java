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

package de.mpg.mpdl.inge.pubman.web.viewItem;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.pubman.web.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.appbase.FacesBean;
import de.mpg.mpdl.inge.pubman.web.desktop.Login;
import de.mpg.mpdl.inge.pubman.web.util.PubItemVOPresentation;

/**
 * Keeps all attributes that are used for the whole session by ViewItem.
 * 
 * @author: Thomas Diebäcker, created 30.05.2007
 * @version: $Revision$ $LastChangedDate$ Revised by ScT: 22.08.2007
 */
@SuppressWarnings("serial")
public class ViewItemSessionBean extends FacesBean {
  public static final String BEAN_NAME = "ViewItemSessionBean";

  private static Logger logger = Logger.getLogger(ViewItemSessionBean.class);

  // navigationString to go back to the list where viewItem has been called
  // from
  private String navigationStringToGoBack = null;
  // // navigationString to go back to the list where viewItem has been called
  // // from
  // private String itemIdViaURLParam = null;

  // Flag if view item has already been redirected
  private boolean hasBeenRedirected = false;

  private String subMenu;
  private boolean detailedMode = false;

  /**
   * Public constructor.
   */
  public ViewItemSessionBean() {
    this.subMenu = "ACTIONS";
  }

//  /**
//   * Callback method that is called whenever a page is navigated to, either directly via a URL, or
//   * indirectly via page navigation.
//   */
//  public void init() {
//    // Perform initializations inherited from our superclass
//    //super.init();
//  }

  /**
   * View the selected item.
   * 
   * @return string, identifying the page that should be navigated to after this methodcall
   */
  public String viewRelease() {
    String itemID = getFacesParamValue("itemID").substring(13).replace("-", ":");

    PubItemVOPresentation pubItemVO = null;

    // set the reload flag to false to force a redirecting to get a proper URL
    this.hasBeenRedirected = false;

    try {
      pubItemVO = this.getItemControllerSessionBean().retrieveItem(itemID);
    } catch (Exception e) {
      logger.error("Could not retrieve release with id " + itemID, e);
      Login login =
          (Login) FacesContext.getCurrentInstance().getApplication().getVariableResolver()
              .resolveVariable(FacesContext.getCurrentInstance(), "Login");
      try {
        login.forceLogout();
      } catch (Exception e2) {
        logger.error("Error logging out user", e2);
      }
      return "";
    }

    this.getItemControllerSessionBean().setCurrentPubItem(pubItemVO);

    return ViewItemFull.LOAD_VIEWITEM;
  }

  /**
   * gets the parameters out of the faces context
   * 
   * @param name name of the parameter in the faces context
   * @return the value of the parameter as string
   */
  public static String getFacesParamValue(String name) {
    return (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
        .get(name);
  }

  /**
   * Returns the ViewItemFull bean.
   * 
   * @return a reference to the scoped data bean (ViewItemFull)
   */
  protected ViewItemFull getViewItemFull() {
    return (ViewItemFull) getBean(ViewItemFull.class);
  }

  /**
   * Returns the ItemControllerSessionBean.
   * 
   * @return a reference to the scoped data bean (ItemControllerSessionBean)
   */
  protected ItemControllerSessionBean getItemControllerSessionBean() {
    return (ItemControllerSessionBean) getSessionBean(ItemControllerSessionBean.class);
  }

  // Getters and Setters
  public String getNavigationStringToGoBack() {
    return navigationStringToGoBack;
  }

  public void setNavigationStringToGoBack(String navigationStringToGoBack) {
    this.navigationStringToGoBack = navigationStringToGoBack;
  }

  // public String getItemIdViaURLParam() {
  // return itemIdViaURLParam;
  // }
  //
  // public void setItemIdViaURLParam(String itemIdViaURLParam) {
  // this.itemIdViaURLParam = itemIdViaURLParam;
  // }


  public boolean isHasBeenRedirected() {
    return hasBeenRedirected;
  }

  public void setHasBeenRedirected(boolean hasBeenRedirected) {
    this.hasBeenRedirected = hasBeenRedirected;
  }

  public void setSubMenu(String subMenu) {
    this.subMenu = subMenu;
  }

  public String getSubMenu() {
    return subMenu;

  }

  public void itemChanged() {
    subMenu = "ACTIONS";

  }

  public void setDetailedMode(boolean detailedMode) {
    this.detailedMode = detailedMode;
  }

  public boolean isDetailedMode() {
    return this.detailedMode;
  }
}
