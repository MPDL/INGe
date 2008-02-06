package de.mpg.escidoc.pubman.breadcrumb;

import java.util.List;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;

public class BreadcrumbItemHistoryRequestBean extends FacesBean
{
    
    private static Logger logger = Logger.getLogger(BreadcrumbItemHistoryRequestBean.class);
    
    private BreadcrumbItem breadCrumbItem = null;
    
    public BreadcrumbItemHistoryRequestBean()
    {
        
        logger.debug("INIT");
        
        BreadcrumbItemHistorySessionBean bcHistory = (BreadcrumbItemHistorySessionBean)getSessionBean(BreadcrumbItemHistorySessionBean.class);
        FacesContext fc = FacesContext.getCurrentInstance();
        String page = fc.getViewRoot().getViewId().substring(1);
        String pageName = page.substring(0, page.lastIndexOf("."));
        
        if ("HomePage".equals(pageName))
        {
            bcHistory.clear();
        }
        
        breadCrumbItem = new BreadcrumbItem(pageName, page);
        bcHistory.setCurrentItem(breadCrumbItem);
        bcHistory.push(breadCrumbItem);
    }
    
    public List<BreadcrumbItem> getNavigation()
    {
        
        logger.debug("GETTING");
        
        BreadcrumbItemHistorySessionBean bcHistory = (BreadcrumbItemHistorySessionBean)getSessionBean(BreadcrumbItemHistorySessionBean.class);
        return bcHistory.getBreadcrumbItemHistory();
    }
}
