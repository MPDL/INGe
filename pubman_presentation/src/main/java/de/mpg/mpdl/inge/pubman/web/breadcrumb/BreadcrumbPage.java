package de.mpg.mpdl.inge.pubman.web.breadcrumb;

import java.io.IOException;
import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import jakarta.faces.context.FacesContext;

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
  private static final Logger logger = LogManager.getLogger(BreadcrumbPage.class);

  private BreadcrumbItem previousItem = null;

  /**
   * Add an entry to the breadcrumb navigation.
   */
  protected void init() {
    final FacesContext fc = FacesTools.getCurrentInstance();
    String page = fc.getViewRoot().getViewId().substring(1);
    final String pageName = page.substring(0, page.lastIndexOf("."));

    // Add get parameters to page, but not if homepage (in order to avoid "expired=true" parameter)
    if (null != FacesTools.getRequest().getQueryString() && !"HomePage".equals(pageName)) {
      page += "?" + FacesTools.getRequest().getQueryString();
    }

    Method defaultAction = null;
    try {
      defaultAction = this.getDefaultAction();
    } catch (final NoSuchMethodException e) {
      logger.error("Error getting default action", e);
    }

    final BreadcrumbItemHistorySessionBean breadcrumbItemHistorySessionBean = FacesTools.findBean("BreadcrumbItemHistorySessionBean");
    breadcrumbItemHistorySessionBean.push(new BreadcrumbItem(pageName, page, defaultAction, this.isItemSpecific()));
    this.previousItem = breadcrumbItemHistorySessionBean.getPreviousItem();
  }

  public String getPreviousPageURI() {
    return this.previousItem.getPage();
  }

  public String getPreviousPageName() {
    return this.previousItem.getPageLabel();
  }

  public void cancel() {
    final String result = this.previousItem.getPage();
    try {
      FacesTools.getExternalContext().redirect(ApplicationBean.INSTANCE.getAppContext() + result);
    } catch (final IOException e) {
      logger.error("Error redirecting to previous page", e);
    }
  }

  protected Method getDefaultAction() throws NoSuchMethodException {
    return null;
  }

  public abstract boolean isItemSpecific();
}
