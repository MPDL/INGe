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

package de.mpg.escidoc.pubman.releases;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.releases.ui.ReleaseListUI;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemVersionVO;

/**
 * Fragment class for Releasy history.
 * 
 * @author: Tobias Schraut, created 18.10.2007
 * @version: $Revision: 1687 $ $LastChangedDate: 2007-12-17 15:29:08 +0100 (Mo, 17 Dez 2007) $ 
 */
public class ReleaseHistory extends FacesBean
{
    public static final String BEAN_NAME = "ReleaseHistory";
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ReleaseHistory.class);
    
    // Faces navigation string
    public final static String LOAD_RELEASE_HISTORY = "loadReleaseHistory";

    // panel for dynamic components
    private HtmlPanelGroup panDynamicReleases = new HtmlPanelGroup();        

    /**
     * Public constructor.
     */
    public ReleaseHistory()
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
        
        if (this.getSessionBean().getReleaseListUI() == null)
        {
            this.createDynamicItemList();
        }
    }
    
    /**
     * Creates the panel newly according to the values in the FacesBean.
     */
    protected void createDynamicItemList()
    {
        this.panDynamicReleases.getChildren().clear();
        this.getSessionBean().setReleaseList(new ArrayList<PubItemVersionVO>());
        
        if (this.getSessionBean().getReleaseHistory(this.getItemControllerSessionBean().getCurrentPubItem().getReference().getObjectId()) != null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Creating dynamic release list with " + this.getSessionBean().getReleaseHistory(this.getItemControllerSessionBean().getCurrentPubItem().getReference().getObjectId()).size() + " entries.");
            }
            
            // create a CollectionListUI for all PubCollections
            List<PubItemVersionVO> releaseList = this.getSessionBean().getReleaseHistory(this.getItemControllerSessionBean().getCurrentPubItem().getReference().getObjectId());
            List<PubItemVersionVOWrapper> releaseWrapperList = new ArrayList<PubItemVersionVOWrapper>();
            if(releaseList != null)
            {
                for(int i = 0; i < releaseList.size(); i++)
                {
                    if(releaseList.get(i).getState() != null)
                    {
                        if(releaseList.get(i).getState().equals(PubItemVO.State.RELEASED))
                        {
                            releaseWrapperList.add(new PubItemVersionVOWrapper(releaseList.get(i)));
                            this.getSessionBean().getReleaseList().add(releaseList.get(i));
                        }
                    }
                }
            }
            
            this.getSessionBean().setReleaseListUI(new ReleaseListUI(releaseWrapperList));
            
            // add the UI to the dynamic panel
            this.getPanDynamicReleases().getChildren().add(this.getSessionBean().getReleaseListUI());
        }
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
     * Returns the ReleasesSessionBean.
     * 
     * @return a reference to the scoped data bean (ReleasesSessionBean)
     */
    protected ReleasesSessionBean getSessionBean()
    {
        return (ReleasesSessionBean)getBean(ReleasesSessionBean.class);
    }

    public HtmlPanelGroup getPanDynamicReleases()
    {
        return panDynamicReleases;
    }

    public void setPanDynamicReleases(HtmlPanelGroup panDynamicReleases)
    {
        this.panDynamicReleases = panDynamicReleases;
    }
    
    
}
