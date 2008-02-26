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

package de.mpg.escidoc.pubman.createItem;

import java.util.List;

import javax.faces.component.html.HtmlPanelGroup;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.ItemListSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.collectionList.CollectionListSessionBean;
import de.mpg.escidoc.pubman.collectionList.PubCollectionVOWrapper;
import de.mpg.escidoc.pubman.collectionList.ui.CollectionListUI;
import de.mpg.escidoc.pubman.editItem.EditItem;
import de.mpg.escidoc.pubman.home.Home;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.PubCollectionVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.PubCollectionVO;

/**
 * Fragment class for CreateItem.
 * 
 * @author: Thomas Diebäcker, created 11.10.2007
 * @author: $Author: mfranke $ last modification
 * @version: $Revision: 1683 $ $LastChangedDate: 2007-12-17 10:30:45 +0100 (Mo, 17 Dez 2007) $ 
 */
public class CreateItem extends FacesBean
{
    public static final String BEAN_NAME = "CreateItem";
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(CreateItem.class);
    
    // Faces navigation string
    public final static String LOAD_CREATEITEM = "loadCreateItem";

    /**
     * Public constructor.
     */
    public CreateItem()
    {
        this.init();
    }
    
    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation. 
     */
    public void init()
    {
        super.init();

    }
    
    public String confirmSelection()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("confirmSelection()");
        }
        
        PubCollectionVO selectedCollection = this.getSessionBean().getCollectionListUI().getSelectedCollection();
        
        if (selectedCollection != null)
        {            
            return this.getItemControllerSessionBean().createNewPubItem(EditItem.LOAD_EDITITEM, selectedCollection.getReference());
        }
        else
        {
            return null;
        }
    }
    
    public String cancel()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("cancel()");
        }

        return Home.LOAD_HOME;
    }
    
    /**
     * Creates the panel newly according to the values in the FacesBean.
     */
    protected void createDynamicItemList()
    {

    }

    /**
     * Starts a new submission.
     *
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String newSubmission()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("New Submission");
        }

        // if there is only one collection for this user we can skip the CreateItem-Dialog and
        // create the new item directly
        if (this.getCollectionListSessionBean().getCollectionList().size() == 0)
        {
            logger.warn("The user does not have privileges for any collection.");
            return null;
        }
        if (this.getCollectionListSessionBean().getCollectionList().size() == 1)
        {
            PubCollectionVO pubCollectionVO = this.getCollectionListSessionBean().getCollectionList().get(0);
            if (logger.isDebugEnabled())
            {
                logger.debug("The user has only privileges for one collection (ID: "
                        + pubCollectionVO.getReference().getObjectId() + ")");
            }
            return this.getItemControllerSessionBean().createNewPubItem(EditItem.LOAD_EDITITEM,
                    pubCollectionVO.getReference());
        }
        else
        {
            // more than one collection exists for this user; let him choose the right one
            if (logger.isDebugEnabled())
            {
                logger.debug("The user has privileges for "
                        + this.getCollectionListSessionBean().getCollectionList().size() + " different collections.");
            }

            return CreateItem.LOAD_CREATEITEM;
        }
    }

    /**
     * Returns the CollectionListSessionBean.
     *
     * @return a reference to the scoped data bean (CollectionListSessionBean)
     */
    protected CollectionListSessionBean getCollectionListSessionBean()
    {
        return (CollectionListSessionBean) getBean(CollectionListSessionBean.class);
    }

    /**
     * Returns the CollectionListSessionBean.
     *
     * @return a reference to the scoped data bean (CollectionListSessionBean)
     */
    protected CollectionListSessionBean getSessionBean()
    {
        return (CollectionListSessionBean) getSessionBean(CollectionListSessionBean.class);
    }

    /**
     * Returns the ItemListSessionBean.
     * @return a reference to the scoped data bean (ItemListSessionBean)
     */
    protected ItemListSessionBean getItemListSessionBean()
    {
        return (ItemListSessionBean) getSessionBean(ItemListSessionBean.class);
    }

    /**
     * Returns a reference to the scoped data bean (the ItemControllerSessionBean). 
     * @return a reference to the scoped data bean
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getBean(ItemControllerSessionBean.class);
    }

	public List<PubCollectionVOPresentation> getCurrentCollectionList() {
		return getSessionBean().getCollectionList();
	}

}
