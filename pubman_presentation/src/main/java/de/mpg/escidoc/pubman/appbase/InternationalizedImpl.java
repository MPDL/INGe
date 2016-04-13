package de.mpg.escidoc.pubman.appbase;

import java.util.ResourceBundle;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.util.InternationalizationHelper;

/**
 * 
 * Implementation of the Internationalized interface. For convenience use.
 * 
 * @see de.mpg.escidoc.pubman.appbase.Internationalized
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class InternationalizedImpl implements Internationalized {
  private static Logger logger = Logger.getLogger(InternationalizedImpl.class);

  // get the selected language...
  private InternationalizationHelper i18nHelper;

  public InternationalizedImpl() {
    i18nHelper = (InternationalizationHelper) getSessionBean(InternationalizationHelper.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.escidoc.pubman.appbase.Internationalized#getLabel(java.lang.String)
   */
  public String getLabel(String placeholder) {

    return ResourceBundle.getBundle(getI18nHelper().getSelectedLabelBundle())
        .getString(placeholder);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.escidoc.pubman.appbase.Internationalized#getMessage(java.lang.String)
   */
  public String getMessage(String placeholder) {

    return ResourceBundle.getBundle(getI18nHelper().getSelectedMessagesBundle()).getString(
        placeholder);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.escidoc.pubman.appbase.Internationalized#bindComponentLabel(javax.faces.component.
   * UIComponent, java.lang.String)
   */
  public void bindComponentLabel(UIComponent component, String placeholder) {
    ValueExpression value =
        FacesContext
            .getCurrentInstance()
            .getApplication()
            .getExpressionFactory()
            .createValueExpression(FacesContext.getCurrentInstance().getELContext(),
                "#{lbl." + placeholder + "}", String.class);
    component.setValueExpression("value", value);
  }

  /**
   * Return any bean stored in request scope under the specified name.
   * 
   * @param cls The bean class.
   * @return the actual or new bean instance
   */
  public static Object getRequestBean(final Class<?> cls) {
    String name = null;

    try {
      name = (String) cls.getField("BEAN_NAME").get(new String());
      if (FacesBean.class.getName().equals(name)) {
        logger.warn("Bean class " + cls.getName() + " appears to have no individual BEAN_NAME.");
      }
    } catch (Exception e) {
      throw new RuntimeException("Error getting bean name of " + cls, e);
    }

    FacesContext context = FacesContext.getCurrentInstance();
    return cls
        .cast(context.getApplication().evaluateExpressionGet(context, "#{" + name + "}", cls));
  }

  /**
   * Return any bean stored in session scope under the specified name.
   * 
   * @param cls The bean class.
   * @return the actual or new bean instance
   */
  public static Object getSessionBean(final Class<?> cls) {


    String name = null;

    try {
      name = (String) cls.getField("BEAN_NAME").get(new String());
      if (FacesBean.class.getName().equals(name)) {
        logger.warn("Bean class " + cls.getName() + " appears to have no individual BEAN_NAME.");
      }
    } catch (Exception e) {
      throw new RuntimeException("Error getting bean name of " + cls, e);
    }


    FacesContext context = FacesContext.getCurrentInstance();
    Object bean =
        cls.cast(context.getApplication().evaluateExpressionGet(context, "#{" + name + "}", cls));

    return bean;
  }

  /**
   * Return any bean stored in application scope under the specified name.
   * 
   * @param cls The bean class.
   * @return the actual or new bean instance
   */
  public static Object getApplicationBean(final Class<?> cls) {
    String name = null;

    try {
      name = (String) cls.getField("BEAN_NAME").get(new String());
      if (FacesBean.class.getName().equals(name)) {
        logger.warn("Bean class " + cls.getName() + " appears to have no individual BEAN_NAME.");
      }
    } catch (Exception e) {
      throw new RuntimeException("Error getting bean name of " + cls, e);
    }

    FacesContext context = FacesContext.getCurrentInstance();
    return cls
        .cast(context.getApplication().evaluateExpressionGet(context, "#{" + name + "}", cls));

  }

  public InternationalizationHelper getI18nHelper() {
    return i18nHelper;
  }

  public void setI18nHelper(InternationalizationHelper i18nHelper) {
    this.i18nHelper = i18nHelper;
  }

}
