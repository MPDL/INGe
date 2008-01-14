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

package de.mpg.escidoc.pubman.revisions;

import de.mpg.escidoc.pubman.util.ValueObjectWrapper;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.RelationVO;

/**
 * Wrapper for RelationVOs that provides additional attributes for the presentation layer. 
 * 
 * @author: Thomas Diebäcker, created 22.10.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Tue, 20 Nov 2007) $
 */
public class RelationVOWrapper extends ValueObjectWrapper
{
    private boolean expanded = false;
    private PubItemVO sourceItem = null;
    
    /**
     * Public constructor.
     */
    public RelationVOWrapper()
    {        
        super();
    }
    
    public RelationVOWrapper(RelationVO relationVO, PubItemVO sourceItem)
    {
        super(relationVO);
        this.sourceItem = sourceItem;
    }

    public RelationVO getValueObject()
    {
        return (RelationVO)this.valueObject;
    }

    public void setValueObject(RelationVO relationVO)
    {
        this.valueObject = relationVO;
    }

    public boolean isExpanded()
    {
        return expanded;
    }

    public void setExpanded(boolean expanded)
    {
        this.expanded = expanded;
    }

    public PubItemVO getSourceItem()
    {
        return sourceItem;
    }

    public void setSourceItem(PubItemVO sourceItem)
    {
        this.sourceItem = sourceItem;
    }
}
