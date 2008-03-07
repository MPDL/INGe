package de.mpg.escidoc.pubman.appbase;

import java.io.IOException;
import java.util.List;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ApplicationBean;
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

    private BreadcrumbItem previousItem = null;
    
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
        previousItem = breadcrumbItemHistorySessionBean.getPreviousItem();
        
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

    public String getPreviousPageURI()
    {
    	return previousItem.getPage();
    }

    public String getPreviousPageName()
    {
    	return previousItem.getPageLabel();
    }
    
    public String cancel()
    {
    	String result = previousItem.getPage();
    	try
    	{
    		FacesContext.getCurrentInstance().getExternalContext().redirect(((ApplicationBean) getApplicationBean(ApplicationBean.class)).getAppContext() + result);
    	}
    	catch (IOException e) {
			logger.error("Error redirecting to previous page", e);
		}
    	return null;
    }
}
