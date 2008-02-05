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
import java.util.List;
import javax.faces.component.html.HtmlPanelGrid;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;

/**
 * Keeps all attributes that are used for the whole session by the Affiliation components.
 * @author:  Hugo Niedermaier, Basics by Thomas Diebäcker, created 30.05.2007
 * @version: $Revision: 1604 $ $LastChangedDate: 2007-11-26 11:04:12 +0100 (Mo, 26 Nov 2007) $
 * Revised by NiH: 13.08.2007
 */
public class AffiliationSessionBean extends FacesBean
{   
    static final long serialVersionUID = 1L;
    
    //NiH: store different modes for the affiliation selection
    //to distinguish between the actions add and select in the edit item mask
    private boolean add = false;
    //to distinguish between the use case browse by affiliation and add/select in edit item 
    private boolean browseByAffiliation = false;

    public static final String BEAN_NAME = "AffiliationSessionBean";
    
    //instnace of the Affiliation Tree
    private YuiTree treeAffiliation = new YuiTree();
    //flag to control the dynamic creation of the tree from outside
    private boolean wasInit = false;
    private ArrayList<AffiliationVO> currentAffiliationList = new ArrayList<AffiliationVO>();
    protected HtmlPanelGrid organizationPanDynamicParentPanel = new HtmlPanelGrid();
    
    //NiH: list of OrganizationVO's selected in EditItem page
    protected List<OrganizationVO> organizationParentVO = new ArrayList<OrganizationVO>();
    
    protected String organizationParentValueBinding = new String();
    protected int indexComponent;    
    
    /**
     * Public constructor.
     */
    public AffiliationSessionBean()
    {
        this.init();
    }

    /**
     * This method is called when this bean is initially added to session scope. Typically, this occurs as a result of
     * evaluating a value binding or method binding expression, which utilizes the managed bean facility to instantiate
     * this bean and store it into session scope.
     */    
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
    }

    public ArrayList<AffiliationVO> getCurrentAffiliationList()
    {
        return currentAffiliationList;
    }

    public void setCurrentAffiliationList(ArrayList<AffiliationVO> list)
    {
        this.currentAffiliationList = list;
    }

    public boolean isBrowseByAffiliation()
    {
        return browseByAffiliation;
    }

    public void setBrowseByAffiliation(boolean browseByAffiliation)
    {
        this.browseByAffiliation = browseByAffiliation;
    }

    public HtmlPanelGrid getOrganizationPanDynamicParentPanel()
    {
        return organizationPanDynamicParentPanel;
    }

    public void setOrganizationPanDynamicParentPanel(HtmlPanelGrid panDynamicParentPanel)
    {
        this.organizationPanDynamicParentPanel = panDynamicParentPanel;
    }

    /**
     * NiH: returns the list of OrganizationVO's selected in EditItem page
     * @return List<OrganizationVO>
     */
    public List<OrganizationVO> getOrganizationParentVO()
    {
        return organizationParentVO;
    }

    public void setOrganizationParentVO(List<OrganizationVO> organizationParentVO)
    {
        this.organizationParentVO = organizationParentVO;
    }

    public String getOrganizationParentValueBinding()
    {
        return organizationParentValueBinding;
    }

    public void setOrganizationParentValueBinding(String organizationParentValueBinding)
    {
        this.organizationParentValueBinding = organizationParentValueBinding;
    }

    public int getIndexComponent()
    {
        return indexComponent;
    }

    public void setIndexComponent(int indexComponent)
    {
        this.indexComponent = indexComponent;
    }

    public boolean isAdd()
    {
        return add;
    }

    public void setAdd(boolean add)
    {
        this.add = add;
    }

    public YuiTree getTreeAffiliation()
    {
        return treeAffiliation;
    }

    public void setTreeAffiliation(YuiTree treeAffiliation)
    {
        this.treeAffiliation = treeAffiliation;
    }

    public boolean isWasInit()
    {
        return wasInit;
    }

    public void setWasInit(boolean wasInit)
    {
        this.wasInit = wasInit;
    }

}