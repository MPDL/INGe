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

package de.mpg.escidoc.pubman.viewItem.ui;

import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.PubItemVOWrapper;
import de.mpg.escidoc.pubman.viewItem.ui.viewItemMediumComponents.ViewItemBasicsUI;
import de.mpg.escidoc.pubman.viewItem.ui.viewItemMediumComponents.ViewItemDetailsUI;
import de.mpg.escidoc.pubman.viewItem.ui.viewItemMediumComponents.ViewItemEventUI;
import de.mpg.escidoc.pubman.viewItem.ui.viewItemMediumComponents.ViewItemFileUI;
import de.mpg.escidoc.pubman.viewItem.ui.viewItemMediumComponents.ViewItemSourceUI;

/**
 * UI for viewing items in a medium context. 
 * 
 * @author: Thomas Diebäcker, created 30.08.2007
 * @version: $Revision$ $LastChangedDate$
 */
public class ViewItemMediumUI extends HtmlPanelGroup
{
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ViewItemMediumUI.class);
    
    public ViewItemMediumUI()
    {     
    	
    }
    
    public Object processSaveState(FacesContext context) 
    {
        Object superState = super.processSaveState(context);
        return new Object[] {superState, new Integer(getChildCount())};
    }
    
    public void processRestoreState(FacesContext context, Object state) 
    {
        // At this point in time the tree has already been restored, but not before our ctor added the default children.
        // Since we saved the number of children in processSaveState, we know how many children should remain within
        // this component. We assume that the saved tree will have been restored 'behind' the children we put into it
        // from within the ctor.
        Object[] values = (Object[]) state;
        Integer savedChildCount = (Integer) values[1];
        for (int i = getChildCount() - savedChildCount.intValue(); i > 0; i--) 
        {
            getChildren().remove(0);
        }
        super.processRestoreState(context, values[0]);
    }
    
    /**
     * Public constructor.
     */
    public ViewItemMediumUI(PubItemVOWrapper pubItemVOWrapper)
    {     
        this.getChildren().clear();
        this.setId(CommonUtils.createUniqueId(this));
        
        // plug all involved components together
        this.getChildren().add(new ViewItemBasicsUI(pubItemVOWrapper.getValueObject()));
        this.getChildren().add(new ViewItemDetailsUI(pubItemVOWrapper.getValueObject()));
        this.getChildren().add(new ViewItemEventUI(pubItemVOWrapper.getValueObject()));
        this.getChildren().add(new ViewItemSourceUI(pubItemVOWrapper.getValueObject()));
        this.getChildren().add(new ViewItemFileUI(pubItemVOWrapper.getValueObject()));
    }
}
