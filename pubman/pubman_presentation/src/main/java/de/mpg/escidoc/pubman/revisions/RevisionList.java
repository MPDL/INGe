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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.ItemListSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.pubman.util.RelationVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;

/**
 * Fragment class for Revision list.
 * 
 * @author: Tobias Schraut, created 18.10.2007
 * @version: $Revision: 1687 $ $LastChangedDate: 2007-12-17 15:29:08 +0100 (Mo, 17 Dez 2007) $ 
 */
public class RevisionList extends FacesBean
{
    public static final String BEAN_NAME = "ReleaseHistory";
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(RevisionList.class);
    
    // Faces navigation string
    public final static String LOAD_REVISION_LIST = "loadRevisionList";

    private List<PubItemVOPresentation> revisionList;
    
    /**
     * Public constructor.
     */
    public RevisionList()
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
        
        
        //get Revisions
        List<RelationVOPresentation> relationVOList = retrieveRevisions(getItemControllerSessionBean().getCurrentPubItem());
        
        List<PubItemVO> pubItemVOList = new ArrayList<PubItemVO>();
        
        for (RelationVOPresentation relationVO : relationVOList)
        {
            PubItemVO sourceItem = relationVO.getSourceItem();
            if (sourceItem!=null) pubItemVOList.add(sourceItem);

        }
        
        //get ParentItems
        
        List<RelationVOPresentation> relationVOList2 = retrieveParentItems(getItemControllerSessionBean().getCurrentPubItem());
        
        List<PubItemVO> parentPubItemVOList = new ArrayList<PubItemVO>();
        
        for (RelationVOPresentation relationVO : relationVOList2)
        {
            PubItemVO targetItem = relationVO.getTargetItem();
            if (targetItem!=null) pubItemVOList.add(targetItem);

        }
        
        
        revisionList = CommonUtils.convertToPubItemVOPresentationList(pubItemVOList);
   
        this.getItemListSessionBean().setCurrentPubItemList(revisionList);
        this.getItemListSessionBean().setIsRevisionView(true);
        this.getItemListSessionBean().setListDirty(true);
        this.getItemListSessionBean().setType("RevisionList");
    
    }
    

    /**
     * Retrieves all RevisionWrappers for the current item.
     * @return the list of RelationVOWrappers
     */
    private List<RelationVOPresentation> retrieveRevisions(PubItemVO pubItemVO)
    {
        
        try
        {
            return this.getItemControllerSessionBean().retrieveRevisions(pubItemVO); 
        }
        catch (Exception e)
        {
            logger.error("Could not create revision list.", e);
        }

        return null;
    }
    
    /**
     * If the stated item is a revision, a list with RelationVO wrappers from which this revision was created is returned.
     * @return the list of RelationVOWrappers
     */
    private List<RelationVOPresentation> retrieveParentItems(PubItemVO pubItemVO)
    {
        
        try
        {
            return this.getItemControllerSessionBean().retrieveParentsForRevision(pubItemVO); 
        }
        catch (Exception e)
        {
            logger.error("Could not create revision list.", e);
        }

        return null;
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
     * Returns the ItemVersionListSessionBean.
     * 
     * @return a reference to the scoped data bean (ItemVersionListSessionBean)
     */
    protected RelationListSessionBean getSessionBean()
    {
        return (RelationListSessionBean)getSessionBean(RelationListSessionBean.class);
    }
    
    public String getDummy()
    {
    	return "";
    }
    
    protected ItemListSessionBean getItemListSessionBean() 
    {
        return (ItemListSessionBean)getSessionBean(ItemListSessionBean.class);
    }
    
    public boolean getShowRevisions()
    {
        return revisionList.size() > 0;
    }
}
