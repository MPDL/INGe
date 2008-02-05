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
import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.collectionList.CollectionListSessionBean;
import de.mpg.escidoc.pubman.collectionList.PubCollectionVOWrapper;
import de.mpg.escidoc.pubman.collectionList.ui.CollectionListUI;
import de.mpg.escidoc.pubman.depositorWS.DepositorWSSessionBean;
import de.mpg.escidoc.pubman.editItem.EditItem;
import de.mpg.escidoc.pubman.home.Home;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.PubCollectionVO;

/**
 * Fragment class for CreateItem.
 * 
 * @author: Thomas Diebäcker, created 11.10.2007
 * @version: $Revision: 1683 $ $LastChangedDate: 2007-12-17 10:30:45 +0100 (Mo, 17 Dez 2007) $ 
 */
public class CreateItem extends FacesBean
{
    public static final String BEAN_NAME = "CreateItem";
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(CreateItem.class);
    
    // Faces navigation string
    public final static String LOAD_CREATEITEM = "loadCreateItem";

    // panel for dynamic components
    HtmlPanelGroup panDynamicCollectionList = new HtmlPanelGroup();        

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
        
        if (this.getSessionBean().getCollectionListUI() == null)
        {
            this.createDynamicItemList();
        }
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
        this.panDynamicCollectionList.getChildren().clear();
        
        if (this.getSessionBean().getCollectionList() != null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Creating dynamic collection list with " + this.getSessionBean().getCollectionList().size() + " entries.");
            }
            
            // create a CollectionListUI for all PubCollections
            List<PubCollectionVO> pubCollectionList = this.getSessionBean().getCollectionList();
            List<PubCollectionVOWrapper> pubCollectionWrapperList = CommonUtils.convertToPubCollectionVOWrapperList(pubCollectionList);
            this.getSessionBean().setCollectionListUI(new CollectionListUI(pubCollectionWrapperList));
            
            // add the UI to the dynamic panel
            this.getPanDynamicCollectionList().getChildren().add(this.getSessionBean().getCollectionListUI());
        }
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
        // force reload of list next time this page is navigated to
        this.getDepositorWSSessionBean().setListDirty(true);
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
            // refresh ListUI
            this.getCollectionListSessionBean().setCollectionListUI(null);
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
        return (CollectionListSessionBean) getBean(CollectionListSessionBean.class);
    }

    /**
     * Returns the DepositorWSSessionBean.
     * @return a reference to the scoped data bean (DepositorWSSessionBean)
     */
    protected DepositorWSSessionBean getDepositorWSSessionBean()
    {
        return (DepositorWSSessionBean) getBean(DepositorWSSessionBean.class);
    }

    /**
     * Returns a reference to the scoped data bean (the ItemControllerSessionBean). 
     * @return a reference to the scoped data bean
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getBean(ItemControllerSessionBean.class);
    }

    public HtmlPanelGroup getPanDynamicCollectionList()
    {
        return panDynamicCollectionList;
    }

    public void setPanDynamicCollectionList(HtmlPanelGroup panDynamicCollectionList)
    {
        this.panDynamicCollectionList = panDynamicCollectionList;
    }
}
