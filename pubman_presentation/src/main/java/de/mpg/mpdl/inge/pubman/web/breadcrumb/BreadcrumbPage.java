package de.mpg.mpdl.inge.pubman.web.breadcrumb;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;

/**
 * 
 * TODO Abstract class that defines a page as usable for the breadcrumb navigation.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@SuppressWarnings("serial")
public abstract class BreadcrumbPage extends FacesBean {
  private static final Logger logger = Logger.getLogger(BreadcrumbPage.class);

  private BreadcrumbItem previousItem = null;

  /**
   * Add an entry to the breadcrumb navigation.
   */
  protected void init() {
    FacesContext fc = FacesTools.getCurrentInstance();
    String page = fc.getViewRoot().getViewId().substring(1);
    String pageName = page.substring(0, page.lastIndexOf("."));

    // Add get parameters to page, but not if homepage (in order to avoid "expired=true" parameter)
    if (FacesTools.getRequest().getQueryString() != null && !pageName.equals("HomePage")) {
      page += "?" + FacesTools.getRequest().getQueryString();
    }

    Method defaultAction = null;
    try {
      defaultAction = getDefaultAction();
    } catch (NoSuchMethodException e) {
      logger.error("Error getting default action", e);
    }

    BreadcrumbItemHistorySessionBean breadcrumbItemHistorySessionBean =
        (BreadcrumbItemHistorySessionBean) FacesTools.findBean("BreadcrumbItemHistorySessionBean");
    breadcrumbItemHistorySessionBean.push(new BreadcrumbItem(pageName, page, defaultAction,
        isItemSpecific()));
    this.previousItem = breadcrumbItemHistorySessionBean.getPreviousItem();
  }

  public String getPreviousPageURI() {
    return this.previousItem.getPage();
  }

  public String getPreviousPageName() {
    return this.previousItem.getPageLabel();
  }

  public String cancel() {
    String result = this.previousItem.getPage();
    try {
      FacesTools.getExternalContext().redirect(
          ((ApplicationBean) FacesTools.findBean("ApplicationBean")).getAppContext() + result);
    } catch (IOException e) {
      logger.error("Error redirecting to previous page", e);
    }

    return null;
  }

  protected Method getDefaultAction() throws NoSuchMethodException {
    return null;
  }

  public abstract boolean isItemSpecific();
}
