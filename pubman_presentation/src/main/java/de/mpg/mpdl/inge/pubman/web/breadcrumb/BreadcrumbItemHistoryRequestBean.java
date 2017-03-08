package de.mpg.mpdl.inge.pubman.web.breadcrumb;

import java.util.List;

import de.mpg.mpdl.inge.pubman.web.appbase.FacesBean;

@SuppressWarnings("serial")
public class BreadcrumbItemHistoryRequestBean extends FacesBean {
  public BreadcrumbItemHistoryRequestBean() {}

  public List<BreadcrumbItem> getNavigation() {
    BreadcrumbItemHistorySessionBean bcHistory =
        (BreadcrumbItemHistorySessionBean) getSessionBean(BreadcrumbItemHistorySessionBean.class);
    
    return bcHistory.getBreadcrumbItemHistory();
  }
}
