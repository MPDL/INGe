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

import java.util.ArrayList;
import java.util.List;
import javax.faces.component.html.HtmlPanelGroup;
import org.apache.log4j.Logger;
import com.sun.rave.web.ui.appbase.AbstractFragmentBean;
import de.mpg.escidoc.pubman.itemList.ui.ItemListUI;
import de.mpg.escidoc.pubman.util.PubItemVOWrapper;
import de.mpg.escidoc.services.common.valueobjects.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;

/**
 * Fragment class for lists of items. 
 * 
 * @author: Thomas Diebäcker, created 29.08.2007
 * @version: $Revision: 1632 $ $LastChangedDate: 2007-11-29 15:01:44 +0100 (Thu, 29 Nov 2007) $
 */
public class ItemList extends AbstractFragmentBean
{
    public static final String BEAN_NAME = "itemList$ItemList";
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ItemList.class);
    
    // panels for dynamic components
    private HtmlPanelGroup panDynamicItemList = new HtmlPanelGroup();
        
    /**
     * This is just for testing purpose!
     */
    public ItemList()
    {
        List<PubItemVOWrapper> pubItemList = new ArrayList<PubItemVOWrapper>();
        pubItemList.add(createTestItem("Title of Generic Test Item 01"));
        pubItemList.add(createTestItem("Title of Generic Test Item 02"));
        pubItemList.add(createTestItem("Title of Generic Test Item 03"));
        pubItemList.add(createTestItem("Title of Generic Test Item 04"));
        pubItemList.add(createTestItem("Title of Generic Test Item 05"));
        pubItemList.add(createTestItem("Title of Generic Test Item 06"));
        pubItemList.add(createTestItem("Title of Generic Test Item 07"));
        pubItemList.add(createTestItem("Title of Generic Test Item 08"));
        pubItemList.add(createTestItem("Title of Generic Test Item 09"));
        pubItemList.add(createTestItem("Title of Generic Test Item 10"));
        pubItemList.add(createTestItem("Title of Generic Test Item 11"));
        pubItemList.add(createTestItem("Title of Generic Test Item 12"));
        pubItemList.add(createTestItem("Title of Generic Test Item 13"));
        pubItemList.add(createTestItem("Title of Generic Test Item 14"));
        pubItemList.add(createTestItem("Title of Generic Test Item 15"));
        pubItemList.add(createTestItem("Title of Generic Test Item 16"));
        pubItemList.add(createTestItem("Title of Generic Test Item 17"));
        pubItemList.add(createTestItem("Title of Generic Test Item 18"));
        pubItemList.add(createTestItem("Title of Generic Test Item 19"));
        pubItemList.add(createTestItem("Title of Generic Test Item 20"));
        pubItemList.add(createTestItem("Title of Generic Test Item 21"));
        pubItemList.add(createTestItem("Title of Generic Test Item 22"));
        
        // create an ItemListUI for these PubItems
        ItemListUI itemListUI = new ItemListUI(pubItemList, "#{depositorWS$DepositorWS.showItem}");
        
        // add the UI to the dynamic panel
        this.panDynamicItemList.getChildren().add(itemListUI);
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
     * Public constructor.
     */
    public ItemList(List<PubItemVOWrapper> allPubItems)
    {        
        // create an ItemListUI for these PubItems
        ItemListUI itemListUI = new ItemListUI(allPubItems, "#{depositorWS$DepositorWS.showItem}");
        
        // add the UI to the dynamic panel
        this.panDynamicItemList.getChildren().add(itemListUI);
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
