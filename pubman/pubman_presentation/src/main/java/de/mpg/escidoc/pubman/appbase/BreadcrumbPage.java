package de.mpg.escidoc.pubman.appbase;

import java.util.List;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.webapp.UIComponentELTag;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.breadcrumb.BreadcrumbItem;
import de.mpg.escidoc.pubman.breadcrumb.BreadcrumbItemHistorySessionBean;

/**
 *
 * TODO Abstract class that defines a page as usable for the breadcrumb navigation.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public abstract class BreadcrumbPage extends FacesBean
{

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(BreadcrumbPage.class);

    /**
     * Add an entry to the breadcrumb navigation.
     */
    protected void init()
    {
        
        super.init();
        
        logger.debug("PAGE: " + FacesContext.getCurrentInstance().getViewRoot().getViewId());
        
        FacesContext fc = FacesContext.getCurrentInstance();
        String page = fc.getViewRoot().getViewId().substring(1);
        String pageName = page.substring(0, page.lastIndexOf("."));
        
        BreadcrumbItemHistorySessionBean breadcrumbItemHistorySessionBean = (BreadcrumbItemHistorySessionBean) getSessionBean(BreadcrumbItemHistorySessionBean.class);
        breadcrumbItemHistorySessionBean.push(new BreadcrumbItem(pageName, page));
        
        UIComponent bcComponent = FacesContext.getCurrentInstance().getViewRoot().findComponent("form1:Breadcrumb:BreadcrumbNavigation");
        if (bcComponent == null)
        {
        	bcComponent = FacesContext.getCurrentInstance().getViewRoot().findComponent("Breadcrumb:BreadcrumbNavigation");
        }
        if (bcComponent != null)
        {
            ValueExpression value = FacesContext
                .getCurrentInstance()
                .getApplication()
                .getExpressionFactory()
                .createValueExpression(FacesContext.getCurrentInstance().getELContext(), "#{BreadcrumbItemHistoryRequestBean.navigation}", List.class);
            bcComponent.setValueExpression("value", value);
        }
    }

}
