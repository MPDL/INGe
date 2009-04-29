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

package de.mpg.escidoc.pubman.viewItem;

import org.apache.myfaces.trinidad.component.UIXIterator;

import de.mpg.escidoc.pubman.appbase.FacesBean;

/**
 * Backing bean for ViewItemMedium.jspf (for viewing items in a medium context).
 * 
 * @author Tobias Schraut, created 28.03.2008
 * @version: $Revision$ $LastChangedDate$
 */
public class ViewItemMedium extends FacesBean
{
    
	private UIXIterator creatorPersonsIterator = new UIXIterator();
    
    private UIXIterator creatorAffiliationsIterator = new UIXIterator();	
    
    private UIXIterator fileSearchHitIterator = new UIXIterator();
    
    /**
     * Public constructor.
     */
    public ViewItemMedium()
    {
        
    }

	public UIXIterator getCreatorPersonsIterator() {
		return creatorPersonsIterator;
	}

	public void setCreatorPersonsIterator(UIXIterator creatorPersonsIterator) {
		this.creatorPersonsIterator = creatorPersonsIterator;
	}

	public UIXIterator getCreatorAffiliationsIterator() {
		return creatorAffiliationsIterator;
	}

	public void setCreatorAffiliationsIterator(
			UIXIterator creatorAffiliationsIterator) {
		this.creatorAffiliationsIterator = creatorAffiliationsIterator;
	}

    public UIXIterator getFileSearchHitIterator()
    {
        return fileSearchHitIterator;
    }

    public void setFileSearchHitIterator(UIXIterator fileSearchHitIterator)
    {
        this.fileSearchHitIterator = fileSearchHitIterator;
    }

    
    
}