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

package de.mpg.escidoc.pubman.itemList;

import javax.faces.component.html.HtmlPanelGroup;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.PubItemVOWrapper;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;

/**
 * Fragment class for lists of items. 
 * 
 * @author: Thomas Diebäcker, created 29.08.2007
 * @version: $Revision$ $LastChangedDate$
 */
public class ItemList extends FacesBean
{
    public static final String BEAN_NAME = "itemListItemList";
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ItemList.class);
    
    // panels for dynamic components
    private HtmlPanelGroup panDynamicItemList = new HtmlPanelGroup();
        
    /**
     * This is just for testing purpose!
     */
    public ItemList()
    {

        this.init();
        
    }
    
    /**
     * Creates an item for testing purpose.
     * @param titleValue the title of the test item
     * @return a new test item
     */
    private PubItemVOWrapper createTestItem(String titleValue)
    {
        PubItemVOWrapper pubItemVOWrapper = new PubItemVOWrapper();
        PubItemVO pubItemVO = new PubItemVO();
        TextVO textVO = new TextVO();
        textVO.setValue(titleValue);
        MdsPublicationVO mdsPublicationVO = new MdsPublicationVO();
        mdsPublicationVO.setTitle(textVO);
        mdsPublicationVO.setGenre(MdsPublicationVO.Genre.BOOK);
        mdsPublicationVO.setDatePublishedInPrint("2007-12-21");
        mdsPublicationVO.setDatePublishedOnline("2007-12-22");
        pubItemVO.setMetadata(mdsPublicationVO);
        pubItemVOWrapper.setValueObject(pubItemVO);
        
        return pubItemVOWrapper;
    }

    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation. 
     */
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
    }

    public HtmlPanelGroup getPanDynamicItemList()
    {
        return panDynamicItemList;
    }

    public void setPanDynamicItemList(HtmlPanelGroup panDynamicItemList)
    {
        this.panDynamicItemList = panDynamicItemList;
    }
}
