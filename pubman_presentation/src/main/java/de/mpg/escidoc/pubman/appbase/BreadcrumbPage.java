package de.mpg.escidoc.pubman.appbase;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        
        //-----
        Map<String, String> parameterMap = fc.getExternalContext().getRequestParameterMap();
        
        String itemId = parameterMap.get("itemId");
        if (itemId!=null) 
        {
            page += "?itemId="+itemId;
        }
        
        /*
        String parameters="?";
        for (Iterator<Map.Entry<String, String>> iter = parameterMap.entrySet().iterator(); iter.hasNext();)
        {
            Map.Entry<String, String> parameter = iter.next();
            parameters+=parameter.getKey()+"="+parameter.getValue()+"&";
        }
        
        page+=parameters;
        //----
         
         */
        
        Method defaultAction = null;
        try
        {
        	defaultAction = getDefaultAction();
        }
    	catch (NoSuchMethodException e) {
			logger.error("Error getting default action", e);
		}
        BreadcrumbItemHistorySessionBean breadcrumbItemHistorySessionBean = (BreadcrumbItemHistorySessionBean) getSessionBean(BreadcrumbItemHistorySessionBean.class);
        breadcrumbItemHistorySessionBean.push(new BreadcrumbItem(pageName, page, defaultAction));
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
        else
        {
        	logger.warn("Breadcrumb navigation not found.");
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
    
    protected Method getDefaultAction() throws NoSuchMethodException
    {
    	return null;
    }
}
