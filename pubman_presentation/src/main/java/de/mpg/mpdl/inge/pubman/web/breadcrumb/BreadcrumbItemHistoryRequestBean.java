package de.mpg.mpdl.inge.pubman.web.breadcrumb;

import java.util.List;

import javax.faces.bean.ManagedBean;

import de.mpg.mpdl.inge.pubman.web.util.FacesBean;

@ManagedBean(name = "BreadcrumbItemHistoryRequestBean")
@SuppressWarnings("serial")
public class BreadcrumbItemHistoryRequestBean extends FacesBean {
  public BreadcrumbItemHistoryRequestBean() {}

  public List<BreadcrumbItem> getNavigation() {
    BreadcrumbItemHistorySessionBean bcHistory =
        (BreadcrumbItemHistorySessionBean) getSessionBean(BreadcrumbItemHistorySessionBean.class);

    return bcHistory.getBreadcrumbItemHistory();
  }
}
