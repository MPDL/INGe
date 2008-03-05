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

import java.util.List;

import javax.faces.component.html.HtmlPanelGroup;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
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
        
        if (this.getSessionBean().getVersionList() == null)
        {
        	this.getSessionBean().setVersionList(getReleaseHistory(this.getItemControllerSessionBean().getCurrentPubItem().getReference().getObjectId()));
        }
    }
    
    /**
     * Retrieves all releases for the current pubitem.
     * @param itemID the  id of the item for which the releases should be retrieved
     * @return the list of PubItemVersionVOs
     */
    public List<PubItemVersionVO> getReleaseHistory(String itemID)
    {

        try
        {
        	return this.getItemControllerSessionBean().retrieveReleasesForItem(itemID);
        }
        catch (Exception e)
        {
            logger.error("Could not retrieve release list for Item " + itemID, e);
        }
        
        return null;
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
     * Returns the ItemVersionListSessionBean.
     * 
     * @return a reference to the scoped data bean (ItemVersionListSessionBean)
     */
    protected ItemVersionListSessionBean getSessionBean()
    {
        return (ItemVersionListSessionBean)getBean(ItemVersionListSessionBean.class);
    }

    public HtmlPanelGroup getPanDynamicReleases()
    {
        return panDynamicReleases;
    }

    public void setPanDynamicReleases(HtmlPanelGroup panDynamicReleases)
    {
        this.panDynamicReleases = panDynamicReleases;
    }
    
    public String getDummy()
    {
    	return "";
    }
}
