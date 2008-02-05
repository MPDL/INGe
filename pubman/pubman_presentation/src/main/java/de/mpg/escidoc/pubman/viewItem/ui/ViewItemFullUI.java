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

import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.viewItem.ui.viewItemFullComponents.ViewItemBasicsUI;
import de.mpg.escidoc.pubman.viewItem.ui.viewItemFullComponents.ViewItemDetailsUI;
import de.mpg.escidoc.pubman.viewItem.ui.viewItemFullComponents.ViewItemEventUI;
import de.mpg.escidoc.pubman.viewItem.ui.viewItemFullComponents.ViewItemFileUI;
import de.mpg.escidoc.pubman.viewItem.ui.viewItemFullComponents.ViewItemSourceUI;
import de.mpg.escidoc.pubman.viewItem.ui.viewItemFullComponents.ViewItemSystemDetailsUI;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;

/**
 * UI for viewing items in full length.
 * 
 * @author: Tobias Schraut, created 30.08.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Di, 20 Nov 2007) $
 */
public class ViewItemFullUI extends HtmlPanelGroup
{
    public ViewItemFullUI()
    {
        // ? initialize(new PubItemVO());
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
    public ViewItemFullUI(PubItemVO pubItemVO)
    {
        initialize(pubItemVO);
    }

    /**
     * Initializes the UI and sets all attributes of the GUI components.
     * 
     * @param pubItemVO a pubitem
     */
    protected void initialize(PubItemVO pubItemVO)
    {
        // Set up the main panel group which holds the html components
        this.setId(CommonUtils.createUniqueId(this));
                
        this.getChildren().clear();
        
        // plug all involved components together
        this.getChildren().add(new ViewItemBasicsUI(pubItemVO));
        this.getChildren().add(new ViewItemDetailsUI(pubItemVO));
        this.getChildren().add(new ViewItemSystemDetailsUI(pubItemVO));
        this.getChildren().add(new ViewItemEventUI(pubItemVO));
        this.getChildren().add(new ViewItemSourceUI(pubItemVO));
        this.getChildren().add(new ViewItemFileUI(pubItemVO));
    }
}
