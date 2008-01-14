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

package de.mpg.escidoc.pubman;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import com.sun.rave.web.ui.appbase.AbstractSessionBean;
import de.mpg.escidoc.services.common.referenceobjects.PubItemRO;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;

/**
 * Superclass for keeping all attributes that are used for the whole session by ItemLists.
 * @author:  Thomas Diebäcker, created 10.01.2007
 * @version: $Revision: 1629 $ $LastChangedDate: 2007-11-29 12:01:41 +0100 (Thu, 29 Nov 2007) $
 * Revised by DiT: 14.08.2007
 */
public class ItemListSessionBean extends AbstractSessionBean
{
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ItemListSessionBean.class);

    private ArrayList<PubItemVO> currentPubItemList = new ArrayList<PubItemVO>();
    private ArrayList<PubItemVO> selectedPubItems = new ArrayList<PubItemVO>();
    private boolean isListDirty = true;
    private String message = null;
    private String sortBy = "TITLE";
    private String sortOrder = "ASCENDING";

    /**
     * This method is called when this bean is initially added to session scope. Typically, this occurs as a result of
     * evaluating a value binding or method binding expression, which utilizes the managed bean facility to instantiate
     * this bean and store it into session scope.
     */
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
    }

    /**
     * Remove an item by the given ID from the current item list.
     * @param itemToBeRemoved the RO of the item to be removed
     */
    public void removeFromCurrentListByRO(PubItemRO itemToBeRemoved)
    {
        for (int i = 0; i < this.currentPubItemList.size(); i++)
        {
            PubItemVO pubItem = this.currentPubItemList.get(i);
            if (pubItem.getReference().getObjectId().equals(itemToBeRemoved.getObjectId())
                    && pubItem.getReference().getVersionNumber() == itemToBeRemoved.getVersionNumber())
            {
                this.currentPubItemList.remove(pubItem);
            }
        }
    }
    
    public ArrayList<PubItemVO> getCurrentPubItemList()
    {
        return this.currentPubItemList;
    }

    public void setCurrentPubItemList(ArrayList<PubItemVO> currentPubItemList)
    {
        this.currentPubItemList = currentPubItemList;
        
        // clear the selectedList
        this.getSelectedPubItems().clear();
    }

    public ArrayList<PubItemVO> getSelectedPubItems()
    {
        return this.selectedPubItems;
    }

    public void setSelectedPubItems(ArrayList<PubItemVO> selectedPubItems)
    {
        this.selectedPubItems = selectedPubItems;
    }

    public String getMessage()
    {
        return this.message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getSortBy()
    {
        return sortBy;
    }

    public void setSortBy(String sortBy)
    {
        this.sortBy = sortBy;
    }

    public String getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public boolean isListDirty()
    {
        return isListDirty;
    }

    public void setListDirty(boolean isListDirty)
    {
        this.isListDirty = isListDirty;
    }
}
