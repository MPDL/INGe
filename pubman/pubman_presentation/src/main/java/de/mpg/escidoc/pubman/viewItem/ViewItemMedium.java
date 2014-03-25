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
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.viewItem;



import org.ajax4jsf.component.html.HtmlAjaxRepeat;

import de.mpg.escidoc.pubman.appbase.FacesBean;

/**
 * Backing bean for ViewItemMedium.jspf (for viewing items in a medium context).
 * 
 * @author Tobias Schraut, created 28.03.2008
 * @version: $Revision$ $LastChangedDate$
 */
public class ViewItemMedium extends FacesBean
{
    
    private HtmlAjaxRepeat creatorPersonsIterator = new HtmlAjaxRepeat();
    
    private HtmlAjaxRepeat creatorAffiliationsIterator = new HtmlAjaxRepeat();    
    
    private HtmlAjaxRepeat fileSearchHitIterator = new HtmlAjaxRepeat();
    
    /**
     * Public constructor.
     */
    public ViewItemMedium()
    {
        
    }

    public HtmlAjaxRepeat getCreatorPersonsIterator() {
        return creatorPersonsIterator;
    }

    public void setCreatorPersonsIterator(HtmlAjaxRepeat creatorPersonsIterator) {
        this.creatorPersonsIterator = creatorPersonsIterator;
    }

    public HtmlAjaxRepeat getCreatorAffiliationsIterator() {
        return creatorAffiliationsIterator;
    }

    public void setCreatorAffiliationsIterator(
            HtmlAjaxRepeat creatorAffiliationsIterator) {
        this.creatorAffiliationsIterator = creatorAffiliationsIterator;
    }

    public HtmlAjaxRepeat getFileSearchHitIterator()
    {
        return fileSearchHitIterator;
    }

    public void setFileSearchHitIterator(HtmlAjaxRepeat fileSearchHitIterator)
    {
        this.fileSearchHitIterator = fileSearchHitIterator;
    }

    
    
}