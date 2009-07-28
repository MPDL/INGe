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

package de.mpg.escidoc.pubman.util;

import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;

/**
 * Wrapper for PubItemVOs that provides additional attributes for the presentation layer. 
 * 
 * @author: Thomas Diebäcker, created 29.08.2007
 * @version: $Revision$ $LastChangedDate$
 */
public class PubItemVOWrapper extends ValueObjectWrapper
{
    /** Constant for showing a short view of this item */
    public static final int SHOW_AS_SHORT = 0;
    /** Constant for showing a medium view of this item */
    public static final int SHOW_AS_MEDIUM = 1;
    
    private int itemView = PubItemVOWrapper.SHOW_AS_SHORT;    
    private boolean expanded = true;
    
    /**
     * Public constructor.
     */
    public PubItemVOWrapper()
    {        
        super();
    }
    
    public PubItemVOWrapper(PubItemVO pubItemVO)
    {
        super(pubItemVO);
    }

    public PubItemVO getValueObject()
    {
        return (PubItemVO)this.valueObject;
    }

    public void setValueObject(PubItemVO pubItemVO)
    {
        this.valueObject = pubItemVO;
    }

    public int getItemView()
    {
        return itemView;
    }

    public void setItemView(int itemView)
    {
        this.itemView = itemView;
    }

    public boolean isExpanded()
    {
        return expanded;
    }

    public void setExpanded(boolean expanded)
    {
        this.expanded = expanded;
    }
}
