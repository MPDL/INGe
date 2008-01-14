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

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIParameter;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;
import com.sun.rave.web.ui.component.Hyperlink;
import de.mpg.escidoc.pubman.util.CommonUtils;

/**
 * Panel that works as a container for other contents (e.g. item lists, search criteria). 
 *
 * @author: Thomas Diebäcker, created 29.08.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Tue, 20 Nov 2007) $
 */
public class ContainerPanelUI extends HtmlPanelGroup
{
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ContainerPanelUI.class);

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
        
        HtmlOutputText txtTitle = new HtmlOutputText();
        txtTitle.setId(CommonUtils.createUniqueId(txtTitle));
        txtTitle.setValue(CommonUtils.limitString(title, 100));
        
        Hyperlink lnkTitle = new Hyperlink();
        lnkTitle.setId(CommonUtils.createUniqueId(lnkTitle));
        lnkTitle.setAction(application.createMethodBinding(actionMethod, null));
     
        UIParameter uiParameter = new UIParameter();
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
}
