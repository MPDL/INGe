package de.mpg.mpdl.inge.pubman.web.breadcrumb;

import java.util.List;

import javax.faces.bean.ManagedBean;

import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;

@ManagedBean(name = "BreadcrumbItemHistoryRequestBean")
@SuppressWarnings("serial")
public class BreadcrumbItemHistoryRequestBean extends FacesBean {
  public BreadcrumbItemHistoryRequestBean() {}

  public List<BreadcrumbItem> getNavigation() {
    BreadcrumbItemHistorySessionBean bcHistory =
        (BreadcrumbItemHistorySessionBean) FacesTools.findBean("BreadcrumbItemHistorySessionBean");

    return bcHistory.getBreadcrumbItemHistory();
  }
}
