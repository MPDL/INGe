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
import org.apache.log4j.Logger;
import com.sun.rave.web.ui.appbase.AbstractSessionBean;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.releases.ui.ReleaseListUI;
import de.mpg.escidoc.services.common.valueobjects.PubItemVersionVO;

/**
 * Keeps all attributes that are used for the whole session by the ReleaseHistory.
 * @author:  Tobias Schraut, created 18.10.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Tue, 20 Nov 2007) $
 */
public class ReleasesSessionBean extends AbstractSessionBean
{
    public static final String BEAN_NAME = "releases$ReleasesSessionBean";
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ReleasesSessionBean.class);
    
    private List<PubItemVersionVO> releaseList = new ArrayList<PubItemVersionVO>();
    private ReleaseListUI releaseListUI = null;
    
    /**
     * Public constructor.
     */
    public ReleasesSessionBean()
    {
    }
    
    /**
     * Retrieves all releases for the current pubitem.
     * @param itemID the  id of the item for which the releases should be retrieved
     * @return the list of PubItemVersionVOs
     */
    public List<PubItemVersionVO> getReleaseHistory(String itemID)
    {
        List<PubItemVersionVO> allReleases = new ArrayList<PubItemVersionVO>();
        
        try
        {
            allReleases = this.getItemControllerSessionBean().retrieveReleasesForItem(itemID);
        }
        catch (Exception e)
        {
            logger.error("Could not retrieve release list for Item " + itemID, e);
        }
        
        return allReleases;
    }
    
    /**
     * Returns a reference to the scoped data bean (the ItemControllerSessionBean). 
     * @return a reference to the scoped data bean
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getBean(ItemControllerSessionBean.BEAN_NAME);
    }

    public ReleaseListUI getReleaseListUI()
    {
        return releaseListUI;
    }

    public void setReleaseListUI(ReleaseListUI releaseListUI)
    {
        this.releaseListUI = releaseListUI;
    }

    public List<PubItemVersionVO> getReleaseList()
    {
        return releaseList;
    }

    public void setReleaseList(List<PubItemVersionVO> releaseList)
    {
        this.releaseList = releaseList;
    }

    
}
