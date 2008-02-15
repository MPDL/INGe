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

import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.apache.myfaces.trinidad.model.TreeModel;

import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.affiliation.ui.AffiliationTreeNode;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.editItem.ui.OrganizationUI;
import de.mpg.escidoc.pubman.search.AffiliationDetail;
import de.mpg.escidoc.pubman.search.SearchResultList;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.services.common.DataGathering;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;

/**
 * Fragment class for the AffiliationTree. This class provides all functionality for displaying all affiliations and its
 * subcomponents.
 * 
 * @author: Hugo Niedermaier, Basics by Thomas Diebäcker, created 30.05.2007
 * @version: $Revision: 1615 $ $LastChangedDate: 2007-11-27 12:43:17 +0100 (Di, 27 Nov 2007) $
 * Revised by NiH: 09.08.2007
 */
public class AffiliationTree extends FacesBean
{
    public static final String BEAN_NAME = "AffiliationTree";
    private static Logger logger = Logger.getLogger(AffiliationTree.class);
    // Faces navigation string
    public final static String LOAD_AFFILIATIONTREE = "loadAffiliationTree";
    // binded components in JSP
    private TreeModel treeAffiliation;
    private HtmlCommandLink lnkSelect = new HtmlCommandLink();
    private String text = new String();
    
    /**
     * Public constructor.
     */
    public AffiliationTree()
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
        
        //get the instance of the Affiliation Tree from the Session Bean
        this.treeAffiliation = this.getAffiliationSessionBean().getTreeAffiliation();
        
//        if (this.getAffiliationSessionBean().getCurrentAffiliationList().size() == 0)
//        {
//            // initialize and create the tree
//            this.initializeTree();
//        }
//        else
//        {
//            // create the tree
//            this.createDynamicTree();
//        }
        this.initializeTree();
    }

    /**
     * Creates a new tree in the FacesBean and forces the UI component to create a new tree.
     * 
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String initializeTree()
    {
        // clear affiliationList stored in the FacesBean
        this.getAffiliationSessionBean().getCurrentAffiliationList().clear();
        ArrayList<AffiliationVO> topLevelAffiliations = new ArrayList<AffiliationVO>();
        // retrieve the top-level affiliations
        try
        {
            topLevelAffiliations = this.getItemControllerSessionBean().retrieveTopLevelAffiliations();
        }
        catch (Exception e)
        {
            logger.error("Could not create affiliation list." + "\n" + e.toString());
            ((ErrorPage)this.getBean(ErrorPage.class)).setException(e);
            return ErrorPage.LOAD_ERRORPAGE;
        }

        // set new list in FacesBean
        this.getAffiliationSessionBean().setCurrentAffiliationList(topLevelAffiliations);
        // force the UI to update
        this.createDynamicTree();
        return AffiliationTree.LOAD_AFFILIATIONTREE;
    }

    /**
     * Creates the tree newly according to the values in the FacesBean.
     */
    protected void createDynamicTree()
    {
        if (this.getAffiliationSessionBean().isWasInit() == false)   //only if we have to create the tree new!
        {
            this.getAffiliationSessionBean().setWasInit(true);
            //this.getTreeAffiliation().getChildren().clear();
            if (this.getAffiliationSessionBean().getCurrentAffiliationList() != null)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Creating dynamic item list with "
                            + getAffiliationSessionBean().getCurrentAffiliationList().size() + " entries.");
                }
                for (int i = 0; i < this.getAffiliationSessionBean().getCurrentAffiliationList().size(); i++)
                {   
                    //AffiliationTreeNodeUI affiliationTreeNodeUI = new AffiliationTreeNodeUI(
                    //		this.getAffiliationSessionBean().getCurrentAffiliationList().get(i));
                    //treeAffiliation.getChildren().add(affiliationTreeNodeUI.getUIComponent());
                }
            }
        }
        else    //don't expand the nodes of the top level affiliations
        {
            //treeAffiliation.setExpandOnSelect(false);
        }
    }

    /**
     * Shows detailed information on the selected Affiliation and its items in a new page.
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String showAffiliationDetail()
    {
        //static flag workaround ui:tree component (exception on doubleclick selection)
        //commented out until we have a solution for this bug!
//        if (SearchResultList.isInSearch == true)
//        {
//            //the search for affiliation is already started!
//            //immediate displaying the search result list
//            SearchResultList.isInSearch = false;
//            return (SearchResultList.LOAD_AFFILIATIONSEARCHRESULTLIST);
//        }
//        else 
        if (this.getAffiliationSessionBean().isBrowseByAffiliation() == false)
        {
            SearchResultList.isInSearch = true; //the search is started
            UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
            String selectedNodeID = null; //treeAffiliation.getSelected();
            AffiliationTreeNode selectedTreeNode = (AffiliationTreeNode)viewRoot.findComponent(selectedNodeID);
            
            if (selectedTreeNode != null)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Name of selected affiliation: " + selectedTreeNode.getAffiliationVO().getName());
                }
                
                //set the AffiliationVO in the Bean for displaying the appropriate Informations in the Detail jsp
                AffiliationDetail bean = (AffiliationDetail)getBean(AffiliationDetail.class);
                bean.setAffiliationVO(selectedTreeNode.getAffiliationVO());
                
                //start search by affiliation
                SearchResultList list = (SearchResultList)getBean(SearchResultList.class);
                return list.startSearchForAffiliation(selectedTreeNode.getAffiliationVO().getReference());
            }
        }
        return null;
    }
    
    /**
     * create the organization list from the selected affiliation
     * and add it to the the list in the AffiliationSessionBean at the correct position
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String getNode()
    {
        //static flag workaround ui:tree component (exception on doubleclick selection) 
//        if (SearchResultList.isInSearch == true)
//        {
//            //the search for affiliation is already strated!
//            return "loadEditItem";
//        }
//        else
        {
            SearchResultList.isInSearch = true; //the search is started
            UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
            String selectedNodeID = null; //treeAffiliation.getSelected();
            if (selectedNodeID != null)
            {
                //get the selected tree node
                AffiliationTreeNode selectedTreeNode = (AffiliationTreeNode)viewRoot.findComponent(selectedNodeID);
                if (selectedTreeNode != null)
                {
                    try
                    {
                        LoginHelper loginHelper = (LoginHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver()
                        .resolveVariable(FacesContext.getCurrentInstance(), "LoginHelper");
                        String userHandle = loginHelper.getESciDocUserHandle();

                        InitialContext context = new InitialContext();
                        //get the DataGathering service
                        DataGathering dataGathering = (DataGathering)context.lookup(DataGathering.SERVICE_NAME);
                        //retrieve the list of organizations from the selected affiliation
                        List<OrganizationVO> organizationVO = dataGathering.createOrganizationListFromAffiliation(userHandle, selectedTreeNode.getAffiliationVO());
        
                        //get the primary organization list from the edit item mask 
                        AffiliationSessionBean bean = getAffiliationSessionBean();
                        List<OrganizationVO> list = bean.getOrganizationParentVO();
                        
                        //index of the component in the jsf component tree
                        int index = bean.getIndexComponent();
                        
                        //remove the empty vo at index position
                        list.remove(index);            
                        
                        //iterate all the elements from the new OrganizationVO list and add it to
                        //the list in the AffiliationSessionBean
                        OrganizationVO newVo;
                        for (int i = 0; i < organizationVO.size(); i++)
                        {
                            newVo = organizationVO.get(i);
                            list.add(bean.getIndexComponent(), newVo);
                        }
                        bean.setOrganizationParentVO(list);
                        
                        //create the nwe panel in the edit item mask with the changed list of OrganizationVO's
                        OrganizationUI.createDynamicParentPanel(bean.getOrganizationPanDynamicParentPanel(), bean.getOrganizationParentVO(), bean.getOrganizationParentValueBinding());
                        
                    }
                    catch (Exception e) 
                    {
                        logger.error(e.toString());
                    }
                }
            }            
        }

        SearchResultList.isInSearch = false;
        return "loadEditItem";
    }
    
    /**
     * Returns the AffiliationSessionBean.
     * 
     * @return a reference to the scoped data bean (AffiliationSessionBean)
     */
    protected AffiliationSessionBean getAffiliationSessionBean()
    {
        return (AffiliationSessionBean)getBean(AffiliationSessionBean.class);
    }

    /**
     * Returns the DepositorWSSessionBean.
     * 
     * @return a reference to the scoped data bean (DepositorWSSessionBean)
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getBean(ItemControllerSessionBean.class);
    }
    
    public TreeModel getTreeAffiliation()
    {
        return treeAffiliation;
    }

    public void setTreeAffiliation(TreeModel treeAffiliation)
    {
        this.treeAffiliation = treeAffiliation;
    }

    public HtmlCommandLink getLnkSelect()
    {
        return lnkSelect;
    }

    public void setLnkSelect(HtmlCommandLink lnkSelect)
    {
        this.lnkSelect = lnkSelect;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

}
