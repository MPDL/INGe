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

import javax.faces.component.html.HtmlPanelGroup;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.editItem.EditItem;
import de.mpg.escidoc.pubman.viewItem.ViewItemFull;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;

/**
 * Fragment class for CreateRevision.
 * 
 * @author: Thomas Diebäcker, created 22.10.2007
 * @version: $Revision$ $LastChangedDate$ 
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

    }
    
    public String confirm()
    {
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
        
        ContextVO selectedCollection = this.getCollectionListSessionBean().getSelectedDepositorContext();
        
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
     * Returns the RevisionListSessionBean.
     * 
     * @return a reference to the scoped data bean (RevisionListSessionBean)
     */
    protected RelationListSessionBean getSessionBean()
    {
        return (RelationListSessionBean)getSessionBean(RelationListSessionBean.class);
    }

    /**
     * Returns a reference to the scoped data bean (the ItemControllerSessionBean). 
     * @return a reference to the scoped data bean
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getSessionBean(ItemControllerSessionBean.class);
    }

    /**
     * Returns the ContextListSessionBean.
     * 
     * @return a reference to the scoped data bean (ContextListSessionBean)
     */
    protected ContextListSessionBean getCollectionListSessionBean()
    {
        return (ContextListSessionBean)getSessionBean(ContextListSessionBean.class);
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
