package de.mpg.escidoc.pubman.appbase;

import java.util.ResourceBundle;

import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import de.mpg.escidoc.pubman.util.InternationalizationHelper;

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
    
    //For handling the resource bundles (i18n)
    protected Application application = FacesContext.getCurrentInstance().getApplication();
    //get the selected language...
    protected InternationalizationHelper i18nHelper = (InternationalizationHelper)application
    .getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);

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
    
}
