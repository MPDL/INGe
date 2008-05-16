package de.mpg.escidoc.pubman.appbase;

import java.util.ResourceBundle;

import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.util.InternationalizationHelper;

import javax.faces.model.SelectItem;

/**
 * 
 * Implementation of the Internationalized interface. For convenience use.
 * @see de.mpg.escidoc.pubman.appbase.Internationalized
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class InternationalizedImpl implements Internationalized
{
    private static Logger logger = Logger.getLogger(InternationalizedImpl.class);
	
    //For handling the resource bundles (i18n)
    protected Application application = FacesContext.getCurrentInstance().getApplication();
    //get the selected language...
    protected InternationalizationHelper i18nHelper;

    public InternationalizedImpl()
    {
    	i18nHelper = (InternationalizationHelper)getSessionBean(InternationalizationHelper.class);
    }
    /*
     * (non-Javadoc)
     * @see de.mpg.escidoc.pubman.appbase.Internationalized#getLabel(java.lang.String)
     */
    public String getLabel(String placeholder)
    {
        return ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle()).getString(placeholder);
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.escidoc.pubman.appbase.Internationalized#getMessage(java.lang.String)
     */
    public String getMessage(String placeholder)
    {
        return ResourceBundle.getBundle(i18nHelper.getSelectedMessagesBundle()).getString(placeholder);
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.escidoc.pubman.appbase.Internationalized#bindComponentLabel(javax.faces.component.UIComponent, java.lang.String)
     */
    public void bindComponentLabel(UIComponent component, String placeholder)
    {
        ValueExpression value = FacesContext
            .getCurrentInstance()
            .getApplication()
            .getExpressionFactory()
            .createValueExpression(FacesContext.getCurrentInstance().getELContext(), "#{lbl." + placeholder + "}", String.class);
        component.setValueExpression("value", value); 
    }

    /**
     * Return any bean stored in request scope under the specified name.
     * @param cls The bean class.
     * @return the actual or new bean instance
     */
    public static synchronized Object getRequestBean(final Class<?> cls)
    {
        String name = null;

        try
        {
            name = (String) cls.getField("BEAN_NAME").get(new String());
            if (FacesBean.class.getName().equals(name))
            {
            	logger.warn("Bean class " + cls.getName() + " appears to have no individual BEAN_NAME.");
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error getting bean name of " + cls, e);
        }
        
        Object result = FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .getRequestMap()
                .get(name);
        
        logger.debug("Getting bean " + name + ": " + result);

        if (result == null)
        {
            try
            {
            	logger.debug("Creating new request bean: " + name);
                Object newBean = cls.newInstance();
                FacesContext
                        .getCurrentInstance()
                        .getExternalContext()
                        .getRequestMap()
                        .put(name, newBean);
                return newBean;
            }
            catch (Exception e)
            {
                throw new RuntimeException("Error creating new bean of type " + cls, e);
            }
        }
        else
        {
            return result;
        }
    }

    /**
     * Return any bean stored in session scope under the specified name.
     * @param cls The bean class.
     * @return the actual or new bean instance
     */
    public static synchronized Object getSessionBean(final Class<?> cls)
    {

        String name = null;

        try
        {
            name = (String) cls.getField("BEAN_NAME").get(new String());
            if (FacesBean.class.getName().equals(name))
            {
            	logger.warn("Bean class " + cls.getName() + " appears to have no individual BEAN_NAME.");
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error getting bean name of " + cls, e);
        }
        Object result = FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .getSessionMap()
                .get(name);
        
        logger.debug("Getting bean " + name + ": " + result);

        if (result == null)
        {
            try
            {
                logger.debug("Creating new session bean: " + name);
                Object newBean = cls.newInstance();
                FacesContext
                        .getCurrentInstance()
                        .getExternalContext()
                        .getSessionMap()
                        .put(name, newBean);
                return newBean;
            }
            catch (Exception e)
            {
                throw new RuntimeException("Error creating new bean of type " + cls, e);
            }
        }
        else
        {
            return result;
        }
    }

    /**
     * Return any bean stored in application scope under the specified name.
     * @param cls The bean class.
     * @return the actual or new bean instance
     */
    public static synchronized Object getApplicationBean(final Class<?> cls)
    {
        String name = null;

        try
        {
            name = (String) cls.getField("BEAN_NAME").get(new String());
            if (FacesBean.class.getName().equals(name))
            {
            	logger.warn("Bean class " + cls.getName() + " appears to have no individual BEAN_NAME.");
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error getting bean name of " + cls, e);
        }
        Object result = FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .getApplicationMap()
                .get(name);
        
        logger.debug("Getting bean " + name + ": " + result);

        if (result == null)
        {
            try
            {
            	 logger.debug("Creating new application bean: " + name);
                Object newBean = cls.newInstance();
                FacesContext
                        .getCurrentInstance()
                        .getExternalContext()
                        .getApplicationMap()
                        .put(name, newBean);
                return newBean;
            }
            catch (Exception e)
            {
                throw new RuntimeException("Error creating new bean of type " + cls, e);
            }
        }
        else
        {
            return result;
        }
    }
    
}
