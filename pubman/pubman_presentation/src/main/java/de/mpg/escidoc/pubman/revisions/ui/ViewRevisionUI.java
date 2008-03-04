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

package de.mpg.escidoc.pubman.revisions.ui;

import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.revisions.RelationVOWrapper;
import de.mpg.escidoc.pubman.ui.HTMLElementUI;
import de.mpg.escidoc.pubman.util.CommonUtils;

/**
 * UI for viewing revisions. 
 * 
 * @author: Thomas Diebaecker, created 22.10.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Di, 20 Nov 2007) $
 */
public class ViewRevisionUI extends HtmlPanelGroup
{
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ViewRevisionUI.class);
    
    private HTMLElementUI htmlElement = new HTMLElementUI();
    private HtmlOutputText lblDescription = new HtmlOutputText();
    
    /**
     * Public constructor.
     */
    public ViewRevisionUI(RelationVOWrapper relationVOWrapper)
    {
        this.setId(CommonUtils.createUniqueId(this));
        
        this.getChildren().add(htmlElement.getStartTag("div"));
        
        this.lblDescription.setId(CommonUtils.createUniqueId(this.lblDescription));
        this.lblDescription.setValue(relationVOWrapper.getValueObject().getDescription());
        this.getChildren().add(this.lblDescription);

        this.getChildren().add(htmlElement.getEndTag("div"));
    }    
}
