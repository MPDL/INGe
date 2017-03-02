package de.mpg.mpdl.inge.pubman.web.appbase;

import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.pubman.web.util.InternationalizationHelper;

/**
 * 
 * Implementation of the Internationalized interface. For convenience use.
 * 
 * @see de.mpg.mpdl.inge.pubman.web.appbase.Internationalized
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class InternationalizedImpl implements Internationalized {
  private static Logger logger = Logger.getLogger(InternationalizedImpl.class);

  private final InternationalizationHelper i18nHelper =
      (InternationalizationHelper) getSessionBean(InternationalizationHelper.class);;
  private final ResourceBundle labelBundle = ResourceBundle.getBundle(i18nHelper
      .getSelectedLabelBundle());
  private final ResourceBundle messageBundle = ResourceBundle.getBundle(i18nHelper
      .getSelectedMessagesBundle());

  public InternationalizedImpl() {}

  public String getLabel(String placeholder) {
    return this.labelBundle.getString(placeholder);
  }

  public String getMessage(String placeholder) {
    return this.messageBundle.getString(placeholder);
  }

  public InternationalizationHelper getI18nHelper() {
    return this.i18nHelper;
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
}
