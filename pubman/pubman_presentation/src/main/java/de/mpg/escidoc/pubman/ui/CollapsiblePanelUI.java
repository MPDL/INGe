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

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.util.CommonUtils;

/**
 * Panel that provides functionality for collapsing or expanding its content.
 *
 * @author: Thomas Diebäcker, created 29.08.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Di, 20 Nov 2007) $
 */
public class CollapsiblePanelUI extends ContainerPanelUI implements ActionListener
{
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(CollapsiblePanelUI.class);
    private static final String IMAGE_CONTAINER_IS_VISIBLE = "images/up.gif";
    private static final String IMAGE_CONTAINER_IS_NOT_VISIBLE = "images/down.gif";

    protected HTMLElementUI htmlElementUI = new HTMLElementUI();
    protected HtmlCommandButton btCollapse = new HtmlCommandButton();
    protected boolean isContainerVisible = true;

    /**
     * Public constructor.
     */
    public CollapsiblePanelUI()
    {
        super();
        
        this.btCollapse.setId(CommonUtils.createUniqueId(this.btCollapse));
        this.btCollapse.setImage(this.isContainerVisible ? CollapsiblePanelUI.IMAGE_CONTAINER_IS_VISIBLE : CollapsiblePanelUI.IMAGE_CONTAINER_IS_NOT_VISIBLE);
        this.btCollapse.setImmediate(true);
        this.btCollapse.addActionListener(this);
        super.addToControls(this.btCollapse);
    }

    /**
     * Adds a new UIComponent to the title bar horizontally.
     * @param newComponent the component to add to the title bar
     */
    protected void addToTitleBar(UIComponent newComponent)
    {
        // add the new component to the title bar
        this.panTitleBar.getChildren().add(this.panTitleBar.getChildCount() - 1, newComponent);
    }

    /**
     * Adds a new UIComponent to the controls section in the title bar horizontally.
     * @param newComponent the component to add to the controls section in the title bar
     */
    @Override
    protected void addToControls(UIComponent newComponent)
    {
        // add the new component to the controls section in the title bar
        this.panControls.getChildren().add(this.panControls.getChildCount() - 2, newComponent);
    }

    /**
     * Toggles the visibility of the container.
     */
    protected void toggleContainerVisibility()
    {
        // toggle variable
        this.isContainerVisible = !this.isContainerVisible;

        // refresh the display of the panel
        this.refreshPanelVisibility();

        if (logger.isDebugEnabled())
        {
            logger.debug("ContainerVisibility has been changed from "
                    + !this.isContainerVisible + " to "
                    + this.isContainerVisible + ".");
        }
    }

    /**
     * Refreshes the display of the container depending on the current value of its visibility.
     */
    protected void refreshPanelVisibility()
    {
        // set rendered attribute to make container visible/invisible
        this.panContainer.setRendered(this.isContainerVisible);

        // change graphics for toggle button
        this.btCollapse.setImage(this.isContainerVisible ? CollapsiblePanelUI.IMAGE_CONTAINER_IS_VISIBLE : CollapsiblePanelUI.IMAGE_CONTAINER_IS_NOT_VISIBLE);
    }

    /**
     * Action handler for user actions.
     * @param event the event of the action
     */
    public void processAction(ActionEvent event)
    {
        if (event.getComponent() == this.btCollapse)
        {
            this.toggleContainerVisibility();
        }
    }
    
}
