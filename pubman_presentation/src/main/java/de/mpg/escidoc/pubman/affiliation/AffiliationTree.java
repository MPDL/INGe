/*
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

package de.mpg.escidoc.pubman.affiliation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.AffiliationVOPresentation;
import de.mpg.escidoc.pubman.util.CommonUtils;

/**
 * Request bean to handle the organizational unit tree.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class AffiliationTree extends FacesBean
{
    public static final String BEAN_NAME = "AffiliationTree";
    private List<AffiliationVOPresentation> affiliations;
    private long timestamp;
    private List<SelectItem> affiliationSelectItems;
    private Map<String, AffiliationVOPresentation> affiliationMap;

    /**
     * Default constructor.
     * 
     * @throws Exception Any exception
     */
    public AffiliationTree() throws Exception
    {
        affiliationMap = new HashMap<String, AffiliationVOPresentation>();
        affiliations = CommonUtils.convertToAffiliationVOPresentationList(getItemControllerSessionBean()
                .searchTopLevelAffiliations());
        timestamp = new Date().getTime();
    }

    public List<AffiliationVOPresentation> getAffiliations()
    {
        return affiliations;
    }

    public void setAffiliations(List<AffiliationVOPresentation> affiliations)
    {
        this.affiliations = affiliations;
    }

    private ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean) getSessionBean(ItemControllerSessionBean.class);
    }

    /**
     * Is called from JSF to reload the ou data.
     * 
     * @return Just a dummy message
     * @throws Exception Any exception
     */
    public String getResetMessage() throws Exception
    {
        affiliations = CommonUtils.convertToAffiliationVOPresentationList(getItemControllerSessionBean()
                .searchTopLevelAffiliations());
        affiliationSelectItems = null;
        timestamp = new Date().getTime();
        return getMessage("Affiliations_reloaded");
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public void setAffiliationSelectItems(List<SelectItem> affiliationsSelectItem)
    {
        this.affiliationSelectItems = affiliationsSelectItem;
    }

    /**
     * Returns SelectItems for a menu with all organizational units.
     * @return
     * @throws Exception
     */
    public List<SelectItem> getAffiliationSelectItems() throws Exception
    {
        if (affiliationSelectItems == null)
        {
            affiliationSelectItems = new ArrayList<SelectItem>();
            affiliationSelectItems.add(new SelectItem("all", getLabel("EditItem_NO_ITEM_SET")));
            
            
            List<AffiliationVOPresentation> topLevelAffs = getAffiliations();
            addChildAffiliationsToMenu(topLevelAffs, affiliationSelectItems, 0);
            
         
        }
        return affiliationSelectItems;
    }
    
    /**
     * Adds the list of the given affiliations to the filter select
     * @param affs
     * @param affSelectItems
     * @param level
     * @throws Exception
     */
    private void addChildAffiliationsToMenu(List<AffiliationVOPresentation> affs, List<SelectItem> affSelectItems, int level) throws Exception
    {
        String prefix = "";
        for (int i = 0; i < level; i++)
        {
            //2 save blanks
            prefix += '\u00A0';
            prefix += '\u00A0';
            prefix += '\u00A0';
        }
        //1 right angle
        prefix+='\u2514';
        for(AffiliationVOPresentation aff : affs){
            affSelectItems.add(new SelectItem(aff.getReference().getObjectId(), prefix+" "+aff.getName()));
            affiliationMap.put(aff.getReference().getObjectId(), aff);
            addChildAffiliationsToMenu(aff.getChildren(), affSelectItems, level+1);
        }
    }

    public void setAffiliationMap(Map<String, AffiliationVOPresentation> affiliationMap)
    {
        this.affiliationMap = affiliationMap;
    }

    /**
     * Returns a Map that contains all affiliations with their id as key. Only fully available if getAffiliationSelectItems() is called before.
     * @return
     */
    public Map<String, AffiliationVOPresentation> getAffiliationMap()
    {
        return affiliationMap;
    }
}
