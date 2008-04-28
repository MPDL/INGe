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

package de.mpg.escidoc.services.common.valueobjects;

import java.util.List;

/**
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $ by $Author: jmueller $
 * @created 09-Okt-2007 16:17:57
 * @revised by MuJ: 09.10.2007
 */
public class ItemFilterParamVO extends FilterParamVO
{
    
    

    /**
     * Object ids of creators as filter criteria.
     */
    private List<String> creatorIds = new java.util.ArrayList<String>();
    /**
     * Object ids of items as filter criteria.
     */
    private List<String> itemIds = new java.util.ArrayList<String>();
    /**
     * The item state to filter for.
     */
    private ItemVO.State state;

    /**
     * @return Object ids of creators as filter criteria.
     */
    public List<String> getCreatorIds()
    {
        return creatorIds;
    }

    /**
     * @return Object ids of items as filter criteria.
     */
    public List<String> getItemIds()
    {
        return itemIds;
    }

    /**
     * @return The item state to filter for.
     */
    public ItemVO.State getState()
    {
        return state;
    }

    /**
     * @param newVal The item state to filter for.
     */
    public void setState(ItemVO.State newVal)
    {
        state = newVal;
    }

}