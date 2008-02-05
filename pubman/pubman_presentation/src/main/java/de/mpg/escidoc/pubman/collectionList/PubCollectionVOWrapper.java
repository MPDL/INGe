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

package de.mpg.escidoc.pubman.collectionList;

import de.mpg.escidoc.pubman.util.ValueObjectWrapper;
import de.mpg.escidoc.services.common.valueobjects.PubCollectionVO;

/**
 * Wrapper for PubCollectionVOs that provides additional attributes for the presentation layer. 
 * 
 * @author: Thomas Diebäcker, created 29.08.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Di, 20 Nov 2007) $
 */
public class PubCollectionVOWrapper extends ValueObjectWrapper
{
    private boolean expanded = false;
    
    /**
     * Public constructor.
     */
    public PubCollectionVOWrapper()
    {        
        super();
    }
    
    public PubCollectionVOWrapper(PubCollectionVO pubCollectionVO)
    {
        super(pubCollectionVO);
    }

    public PubCollectionVO getValueObject()
    {
        return (PubCollectionVO)this.valueObject;
    }

    public void setValueObject(PubCollectionVO pubItemVO)
    {
        this.valueObject = pubItemVO;
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
