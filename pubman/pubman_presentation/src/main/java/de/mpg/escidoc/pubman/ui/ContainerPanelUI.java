/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.ui;

import java.util.ResourceBundle;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIParameter;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.Internationalized;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;

/**
 * Panel that works as a container for other contents (e.g. item lists, search criteria). 
 *
 * @author: Thomas Diebäcker, created 29.08.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Di, 20 Nov 2007) $
 */
public class ContainerPanelUI extends HtmlPanelGroup implements Internationalized
{
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ContainerPanelUI.class);

    
    //For handling the resource bundles (i18n)
    protected Application application = FacesContext.getCurrentInstance().getApplication();
    //get the selected language...
    protected InternationalizationHelper i18nHelper = (InternationalizationHelper)application
    .getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);

    // UI elements
    private HTMLElementUI htmlElementUI = new HTMLElementUI();
    protected HtmlPanelGroup panTitleBar = new HtmlPanelGroup();
    protected HtmlPanelGroup panControls = new HtmlPanelGroup();
    protected HtmlPanelGroup panContainer = new HtmlPanelGroup();
    protected HtmlPanelGroup panFooter = new HtmlPanelGroup();
    private HtmlPanelGroup panTitelComponent = new HtmlPanelGroup();
    private UIComponentBase titelComponent = new HtmlOutputLabel();
    private HtmlOutputText lblDummy = new HtmlOutputText();

    /**
     * Public constructor.
     */
    public ContainerPanelUI()
    {
        this.setId(CommonUtils.createUniqueId(this));
        
        this.panTitleBar.setId(CommonUtils.createUniqueId(this.panTitleBar));

        this.panTitelComponent.setId(CommonUtils.createUniqueId(this.panTitelComponent));
        this.setTitelComponent(lblDummy);
        this.panTitleBar.getChildren().add(this.panTitelComponent);

        this.panControls.setId(CommonUtils.createUniqueId(this.panControls));
        this.panControls.getChildren().add(this.htmlElementUI.getStartTagWithStyleClass("div", "displayControls")); // add at the right side of the title bar, so use the method of the super class
        this.panControls.getChildren().add(htmlElementUI.getEndTag("div"));
        this.panTitleBar.getChildren().add(this.panControls);        
        this.getChildren().add(this.panTitleBar);
        
        this.panContainer.setId(CommonUtils.createUniqueId(this.panContainer));
        this.getChildren().add(this.panContainer);

        this.panFooter.setId(CommonUtils.createUniqueId(this.panFooter));
        this.getChildren().add(this.panFooter);
    }

    public Object processSaveState(FacesContext context) 
    {
        Object superState = super.processSaveState(context);
        return new Object[] {superState, new Integer(getChildCount())};
    }
    
    public void processRestoreState(FacesContext context, Object state) 
    {
        // At this point in time the tree has already been restored, but not before our ctor added the default children.
        // Since we saved the number of children in processSaveState, we know how many children should remain within this component. 
    	// We assume that the saved tree will have been restored 'behind' the children we put into it from within the ctor.
        Object[] values = (Object[]) state;
        Integer savedChildCount = (Integer) values[1];
        for (int i = getChildCount() - savedChildCount.intValue(); i > 0; i--) 
        {
            getChildren().remove(0);
        }
        super.processRestoreState(context, values[0]);
    }

    /**
     * Adds a new UIComponent to the title bar horizontally.
     * @param newComponent the component to add to the title bar
     */
    protected void addToTitleBar(UIComponent newComponent)
    {
        // add the new component to the title bar
        this.panTitleBar.getChildren().add(this.panTitleBar.getChildCount(), newComponent);
    }

    /**
     * Adds a new UIComponent to the controls section in the title bar horizontally.
     * @param newComponent the component to add to the controls section in the title bar
     */
    protected void addToControls(UIComponent newComponent)
    {
        // add the new component to the controls section in the title bar
        this.panControls.getChildren().add(this.panControls.getChildCount() - 1, newComponent);
    }

    /**
     * Adds a new UIComponent to the container panel vertically.
     * @param newComponent the component to add to the container
     */
    protected void addToContainer(UIComponent newComponent)
    {
        this.panContainer.getChildren().add(newComponent);
    }

    /**
     * Adds a new UIComponent to footer horizontally.
     * @param newComponent the component to add to the title bar
     */
    protected void addToFooter(UIComponent newComponent)
    {
        // add the new component to the title bar
        this.panFooter.getChildren().add(newComponent);
    }

    /**
     * Removes all elements from the container.
     */
    protected void clearContainer()
    {
        this.panContainer.getChildren().clear();
    }

    /**
     * Sets the title in the title bar as label component.
     * @param title the new title
     */
    public void setTitle(String title)
    {
        HtmlOutputText txtTitle = new HtmlOutputText();
        txtTitle.setId(CommonUtils.createUniqueId(txtTitle));
        txtTitle.setValue(CommonUtils.limitString(title, 100));
        
        this.setTitelComponent(txtTitle);
    }

    /**
     * Sets the title in the title bar as link component.
     * @param link the new link
     * @param title the new title
     */
    public void setTitle(String actionMethod, String parameter, String title)
    {
        Application application = FacesContext.getCurrentInstance().getApplication();
        
        HtmlOutputText txtTitle = (HtmlOutputText) application.createComponent(HtmlOutputText.COMPONENT_TYPE);
        txtTitle.setId(CommonUtils.createUniqueId(txtTitle));
        txtTitle.setValue(CommonUtils.limitString(title, 100));
        
        HtmlCommandLink lnkTitle = (HtmlCommandLink) application.createComponent(HtmlCommandLink.COMPONENT_TYPE);
        lnkTitle.setId(CommonUtils.createUniqueId(lnkTitle));
        lnkTitle.setAction(application.createMethodBinding(actionMethod, new Class[0]));
     
        UIParameter uiParameter = (UIParameter) application.createComponent(UIParameter.COMPONENT_TYPE);
        uiParameter.setId(CommonUtils.createUniqueId(uiParameter));
        uiParameter.setName("itemID");
        uiParameter.setValue(parameter);
        
        lnkTitle.getChildren().add(uiParameter);
        lnkTitle.getChildren().add(txtTitle);
        
        this.setTitelComponent(lnkTitle);
    }

    /**
     * Sets a new title component. This can be any UIComponent (e.g. a HTMLCommandLink).
     * The title component will be shown in the titlebar of this container.
     * @param titelComponent the UIComponent to be set as title component
     */
    public void setTitelComponent(UIComponentBase titelComponent)
    {
        this.titelComponent = titelComponent;
        this.titelComponent.setId(CommonUtils.createUniqueId(this.titelComponent));
        
        // delete old children 
        this.panTitelComponent.getChildren().clear();
        
        // add the new component as the only child
        this.panTitelComponent.getChildren().add(this.titelComponent);
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
        bindComponentValue(component, "#{lbl." + placeholder + "}");
    }

    /**
     * Bind a localized string to a JSF component value.
     *
     * @param component The JSF component.
     * @param value The string with internationalized content.
     */
    public void bindComponentValue(final UIComponent component, final String value)
    {
        ValueExpression valueExpression = FacesContext
                .getCurrentInstance()
                .getApplication()
                .getExpressionFactory()
                .createValueExpression(FacesContext.getCurrentInstance().getELContext(), value, String.class);
        component.setValueExpression("value", valueExpression);
    }

    /**
     * Create an el method expression from a given string.
     *
     * @param el The string containing the expression. E.g. "#{MyBean.doSomething}".
     * @return The according el method expression.
     */
    protected MethodExpression getMethodExpression(final String el)
    {
        return FacesContext
                .getCurrentInstance()
                .getApplication()
                .getExpressionFactory()
                .createMethodExpression(
                        FacesContext
                                .getCurrentInstance()
                                .getELContext(),
                        el,
                        null,
                        new Class<?>[0]);
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
        if (result == null)
        {
            try
            {
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

}
