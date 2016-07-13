package de.mpg.mpdl.inge.pubman.web.breadcrumb;

import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.pubman.web.appbase.FacesBean;

public class BreadcrumbItemHistoryRequestBean extends FacesBean {

  private static Logger logger = Logger.getLogger(BreadcrumbItemHistoryRequestBean.class);

  public BreadcrumbItemHistoryRequestBean() {

    logger.debug("INIT");

  }

  public List<BreadcrumbItem> getNavigation() {

    logger.debug("GETTING");

    BreadcrumbItemHistorySessionBean bcHistory =
        (BreadcrumbItemHistorySessionBean) getSessionBean(BreadcrumbItemHistorySessionBean.class);
    return bcHistory.getBreadcrumbItemHistory();
  }
}
