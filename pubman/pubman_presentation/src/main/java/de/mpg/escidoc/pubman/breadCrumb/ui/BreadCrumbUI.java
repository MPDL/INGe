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

package de.mpg.escidoc.pubman.breadCrumb.ui;

import java.util.Calendar;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlOutputLink;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;

import de.mpg.escidoc.pubman.breadCrumb.BreadCrumbItem;

/**
 * BreadCrumbUI.java Class for creating the whole breadcrumb navigation bar
 * 
 * @author: Tobias Schraut, created 30.05.2007
 * @version: $Revision: 1653 $ $LastChangedDate: 2007-12-10 17:55:03 +0100 (Mon, 10 Dec 2007) $ Revised by ScT: 16.08.2007
 */
public class BreadCrumbUI
{
    private HtmlPanelGroup panelGroup = new HtmlPanelGroup();
    private HtmlOutputLink lnkCrumb = new HtmlOutputLink();
    private HtmlOutputText txtCrumb = new HtmlOutputText();
    private HtmlOutputText separator = new HtmlOutputText();

    /**
     * public constructor.
     * 
     * @param breadcrumbList List of breadcrumb items.
     */
    public BreadCrumbUI(List<BreadCrumbItem> breadcrumbList)
    {
        initialize(breadcrumbList);
    }

    /**
     * Initialization method that is called every time a breadcrumb element is generated. 
     * Fills the HtmlPanelGroup with a list of BreadCrumbItem elements.
     * 
     * @param breadcrumbList list of bread crumb elements
     */
    protected void initialize(List<BreadCrumbItem> breadcrumbList)
    {
        UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
        this.panelGroup = new HtmlPanelGroup();
        this.panelGroup.setId(viewRoot.createUniqueId() + Calendar.getInstance().getTimeInMillis());
        for (int i = 0; i < breadcrumbList.size(); i++)
        {
            this.txtCrumb = new HtmlOutputText();
            this.txtCrumb.setId(viewRoot.createUniqueId() + Calendar.getInstance().getTimeInMillis());
            this.txtCrumb.setValue(breadcrumbList.get(i).getDisplayValue());
            this.txtCrumb.setStyleClass("breadCrumb");
            // check, if the current UI is the last one in the list. In that
            // case do not present it as link!
            if (i == breadcrumbList.size() - 1)
            {
                this.panelGroup.getChildren().add(txtCrumb);
            }
            else
            {
                this.lnkCrumb = new HtmlOutputLink();
                this.lnkCrumb.setId(viewRoot.createUniqueId() + Calendar.getInstance().getTimeInMillis());
                this.lnkCrumb.setValue(breadcrumbList.get(i).getPage());
                this.lnkCrumb.setStyleClass("breadCrumb");
                this.lnkCrumb.getChildren().add(txtCrumb);
                // This parameter is not needed in the moment.
//              this.param = new UIParameter();
//              this.param.setId(viewRoot.createUniqueId() + Calendar.getInstance().getTimeInMillis());
//              this.param.setValue("xxx");
//              this.param.setName("BCParam");
//              this.lnkCrumb.getChildren().add(param);
                this.panelGroup.getChildren().add(lnkCrumb);
            }
            // insert a separator as long as the breadcrumb is not the last one
            if (i < breadcrumbList.size() - 1)
            {
                this.separator = new HtmlOutputText();
                this.separator.setId(viewRoot.createUniqueId() + Calendar.getInstance().getTimeInMillis());
                this.separator.setValue(" > ");
                this.separator.setStyleClass("breadCrumb");
                this.panelGroup.getChildren().add(separator);
            }
        }
    }

    /**
     * Returns the Panel Grid component
     * 
     * @return UIComponent the panel grid
     */
    public UIComponent getUIComponent()
    {
        return this.panelGroup;
    }
}
