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
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Tue, 20 Nov 2007) $
 */
public class ViewItemMediumUI extends HtmlPanelGroup
{
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ViewItemMediumUI.class);
    
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
