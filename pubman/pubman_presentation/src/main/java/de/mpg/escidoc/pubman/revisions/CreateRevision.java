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
import de.mpg.escidoc.pubman.editItem.EditItem;
import de.mpg.escidoc.pubman.revisions.ui.RevisionListUI;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.pubman.viewItem.ViewItemFull;
import de.mpg.escidoc.services.common.valueobjects.PubCollectionVO;

/**
 * Fragment class for CreateRevision.
 * 
 * @author: Thomas Diebäcker, created 22.10.2007
 * @version: $Revision: 1683 $ $LastChangedDate: 2007-12-17 10:30:45 +0100 (Mo, 17 Dez 2007) $ 
 */
public class CreateRevision extends FacesBean
{
    public static final String BEAN_NAME = "CreateRevision";
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(CreateRevision.class);
    
    // Faces navigation string
    public final static String LOAD_CREATEREVISION = "loadCreateRevision";
    public final static String LOAD_CHOOSECOLLECTION = "loadChooseCollection";

    // panel for dynamic components
    HtmlPanelGroup panDynamicRevisionList = new HtmlPanelGroup();  
    HtmlPanelGroup panDynamicCollectionList = new HtmlPanelGroup();        

    /**
     * Public constructor.
     */
    public CreateRevision()
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
        
        if (logger.isDebugEnabled())
        {
            logger.debug("CreateRevision.init()");
        }
        
        if (this.getSessionBean().getRevisionListUI() == null)
        {
            this.createDynamicItemList();
        }
    }
    
    public String confirm()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Creating new revision for item with ID: " + this.getSessionBean().getPubItemVO().getReference().getObjectId() + ", description: " + this.getSessionBean().getRevisionDescription());
        }
        
        // re-init the collectionList at this time if it's allready there
        this.createDynamicCollectionList();
        
        return CreateRevision.LOAD_CHOOSECOLLECTION;
    }
    
    public String cancel()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("cancel()");
        }

        return ViewItemFull.LOAD_VIEWITEM;
    }
    
    public String confirmCollectionChoose()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("confirmCollectionChoose()");
        }
        
        PubCollectionVO selectedCollection = this.getCollectionListSessionBean().getCollectionListUI().getSelectedCollection();
        
        if (selectedCollection != null)
        {            
            return this.getItemControllerSessionBean().createNewRevision(EditItem.LOAD_EDITITEM, selectedCollection.getReference(), this.getSessionBean().getPubItemVO(), this.getSessionBean().getRevisionDescription());
        }
        else
        {
            return null;
        }
    }
    
    public String cancelCollectionChoose()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("cancelCollectionChoose()");
        }
        
        // re-init RevisionList
        this.getSessionBean().setPubItemVO(this.getSessionBean().getPubItemVO());
        this.init();

        return CreateRevision.LOAD_CREATEREVISION;
    }
    
    /**
     * Creates the item panel newly according to the values in the FacesBean.
     */
    protected void createDynamicItemList()
    {
        this.panDynamicRevisionList.getChildren().clear();
        
        if (this.getSessionBean().getRelationVOWrapperList() != null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Creating dynamic revision list with " + this.getSessionBean().getRelationVOWrapperList().size() + " entries.");
            }
            
            // create a RevisionListUI for all Relations
            List<RelationVOWrapper> relationVOWrapperList = this.getSessionBean().getRelationVOWrapperList();
            this.getSessionBean().setRevisisonListUI(new RevisionListUI(relationVOWrapperList));
            
            // add the UI to the dynamic panel
            this.getPanDynamicRevisionList().getChildren().add(this.getSessionBean().getRevisionListUI());
        }
    }

    /**
     * Creates the collection panel newly according to the values in the FacesBean.
     */
    protected void createDynamicCollectionList()
    {
        this.panDynamicCollectionList.getChildren().clear();
        
        if (this.getCollectionListSessionBean().getCollectionList() != null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Creating dynamic collection list with " + this.getCollectionListSessionBean().getCollectionList().size() + " entries.");
            }
            
            // create a CollectionListUI for all PubCollections
            List<PubCollectionVO> pubCollectionList = this.getCollectionListSessionBean().getCollectionList();
            List<PubCollectionVOWrapper> pubCollectionWrapperList = CommonUtils.convertToPubCollectionVOWrapperList(pubCollectionList);
            this.getCollectionListSessionBean().setCollectionListUI(new CollectionListUI(pubCollectionWrapperList));
            
            // add the UI to the dynamic panel
            this.getPanDynamicCollectionList().getChildren().add(this.getCollectionListSessionBean().getCollectionListUI());
        }
    }

    /**
     * Returns the RevisionListSessionBean.
     * 
     * @return a reference to the scoped data bean (RevisionListSessionBean)
     */
    protected RevisionListSessionBean getSessionBean()
    {
        return (RevisionListSessionBean)getBean(RevisionListSessionBean.class);
    }

    /**
     * Returns a reference to the scoped data bean (the ItemControllerSessionBean). 
     * @return a reference to the scoped data bean
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getBean(ItemControllerSessionBean.class);
    }

    /**
     * Returns the CollectionListSessionBean.
     * 
     * @return a reference to the scoped data bean (CollectionListSessionBean)
     */
    protected CollectionListSessionBean getCollectionListSessionBean()
    {
        return (CollectionListSessionBean)getBean(CollectionListSessionBean.class);
    }

    public HtmlPanelGroup getPanDynamicRevisionList()
    {
        return panDynamicRevisionList;
    }

    public void setPanDynamicRevisionList(HtmlPanelGroup panDynamicRevisionList)
    {
        this.panDynamicRevisionList = panDynamicRevisionList;
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
