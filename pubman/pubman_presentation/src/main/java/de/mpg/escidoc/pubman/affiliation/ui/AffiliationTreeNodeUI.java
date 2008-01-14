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

package de.mpg.escidoc.pubman.affiliation.ui;

import java.util.ArrayList;
import java.util.Calendar;
import javax.faces.application.Application;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionListener;
import org.apache.log4j.Logger;
import com.sun.rave.web.ui.appbase.AbstractFragmentBean;
import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.affiliation.AffiliationSessionBean;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;

/**
 * UI tree component for browsing by affiliation. 
 * 
 * @author: Hugo Niedermaier, Basics by Thomas Dieb�cker, created 31.05.2007
 * @version: $Revision: 1652 $ $LastChangedDate: 2007-12-10 17:54:37 +0100 (Mon, 10 Dec 2007) $
 * Revised by NiH: 09.08.2007
 */
public class AffiliationTreeNodeUI extends AbstractFragmentBean implements ActionListener
{
    // GUI components
    AffiliationTreeNode affiliationTreeNode = new AffiliationTreeNode(this);
    
    private static Logger logger = Logger.getLogger(AffiliationTreeNodeUI.class);
    private AffiliationVO affiliation = null;
    private UIViewRoot viewRoot = null;
    private Application application = null;
    
    boolean hasChildrenFetched;
    
    /**
     * Public constructor.
     * Initializes the UI.
     * @param affiliation the affiliation that should be represented by this UI
     */
    public AffiliationTreeNodeUI(AffiliationVO affiliation)
    {
        hasChildrenFetched = false;
        initialize(affiliation);
    }
    
    /**
     * Initializes the UI and sets all attributes of the GUI components.
     * @param item the pubItem that is being edited
     * @param position index of the item in the corresponding itemList
     */
    protected void initialize(AffiliationVO affiliation)
    {
        Application application = FacesContext.getCurrentInstance().getApplication();
        UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
        
        this.affiliation = affiliation;
        this.application = application;
        this.viewRoot = viewRoot;
           
        // set attributes for all GUI components
        // NiH: show different images depending on if the affiliation has childAffiliations or not
        // please toggle comment when this is needed
//        if (affiliation.getChildAffiliations().size() > 0)
//        {
//            this.affiliationTreeNode.setImageURL("/images/affiliations_tree.gif");                        
//        }
//        else
//        {
//            // set another image for leafs here if there are no children
//            this.affiliationTreeNode.setImageURL("/images/affiliations_tree.gif");
//        }
        this.affiliationTreeNode.setImageURL("/images/affiliations_tree.gif");                        
        
        this.affiliationTreeNode.setId(viewRoot.createUniqueId() + Calendar.getInstance().getTimeInMillis());
        this.affiliationTreeNode.setAffiliationVO(affiliation);
        this.affiliationTreeNode.setText(affiliation.getName());
        if (this.getAffiliationSessionBean().isBrowseByAffiliation() == false)
        {
            this.affiliationTreeNode.setAction(application.createMethodBinding("#{affiliation$AffiliationTree.showAffiliationDetail}", null));
        }
        else
        {
            this.affiliationTreeNode.setAction(null);
        }
        this.affiliationTreeNode.addActionListener(this);

        // create one dummyChildNode for every ChildRO of this affiliation
        // this dummyChildNode is cleared when the node is filled with the existing affiliations
        // in the action handler when the user opens the appropriate tree node
        if (affiliation.getChildAffiliations().size() > 0)
        {
            AffiliationTreeNode dummyChildNode = new AffiliationTreeNode(this);
            dummyChildNode.setId(viewRoot.createUniqueId() + Calendar.getInstance().getTimeInMillis());
            dummyChildNode.setText("DummyChildNode");
            if (this.getAffiliationSessionBean().isBrowseByAffiliation() == false)
                dummyChildNode.setAction(application.createMethodBinding("#{affiliation$AffiliationTree.showAffiliationDetail}", null));
            else
                this.affiliationTreeNode.setAction(null);
            dummyChildNode.addActionListener(this);
            
            this.affiliationTreeNode.getChildren().add(dummyChildNode);
        }
        
//      create dummychildnodes for nodes which has children
        ItemControllerSessionBean bean = (ItemControllerSessionBean)getBean(ItemControllerSessionBean.BEAN_NAME);
        ArrayList<AffiliationVO> nodeAff = null;
        try
        {
            nodeAff = bean.retrieveChildAffiliations(this.affiliationTreeNode.getAffiliationVO());  
        }
        catch (Exception e)
        {
            logger.error("Could not create affiliation list." + "\n" + e.toString());
            
            ((ErrorPage)this.getBean(ErrorPage.BEAN_NAME)).setException(e);            
            return;
        } 
        
        if ( nodeAff.size() > 0 )
        {
            AffiliationTreeNode dummyChildNode = new AffiliationTreeNode(this);
            dummyChildNode.setId(viewRoot.createUniqueId() + Calendar.getInstance().getTimeInMillis());
            dummyChildNode.setText("DummyChildNode");
            if (this.getAffiliationSessionBean().isBrowseByAffiliation() == false)
                dummyChildNode.setAction(application.createMethodBinding("#{affiliation$AffiliationTree.showAffiliationDetail}", null));
            else
                this.affiliationTreeNode.setAction(null);
            // dummyChildNode.addActionListener(null);
            
            affiliationTreeNode.getChildren().add(dummyChildNode);
        }
        
    }

    /**
     * action handler when the user opens the appropriate tree node
     * @param javax.faces.event.ActionEvent event
     */
    public synchronized void processAction(javax.faces.event.ActionEvent event)
    {
        if( hasChildrenFetched == false )
        {
            hasChildrenFetched = true;
            
        // remove the dummyChildNode; should always be on first position
            this.affiliationTreeNode.getChildren().remove(0);
               
            ItemControllerSessionBean bean = (ItemControllerSessionBean)getBean(ItemControllerSessionBean.BEAN_NAME);
            try
            {
                //get all children of this node
                ArrayList<AffiliationVO> nodeAffiliations = bean.retrieveChildAffiliations(this.affiliation);
                
                for (int i = 0; i < nodeAffiliations.size(); i++)
                {
                    AffiliationTreeNode node = new AffiliationTreeNode(this);
                    node.setId(viewRoot.createUniqueId() + Calendar.getInstance().getTimeInMillis());
                    node.setText(nodeAffiliations.get(i).getName());
                    node.setAffiliationVO(nodeAffiliations.get(i));
                    if (this.getAffiliationSessionBean().isBrowseByAffiliation() == false)
                    {
                        node.setAction(application.createMethodBinding("#{affiliation$AffiliationTree.showAffiliationDetail}", null));
                    }
                    else
                    {
                        this.affiliationTreeNode.setAction(null);
                    }
                    node.setImageURL("/images/documents.gif");
                    node.addActionListener(node);
                      
                    //add the childnodes to the tree
                    // if (opend == false)
                    {
                        this.affiliationTreeNode.getChildren().add(node);
                    }

                    if ( node.hasAffiliationChildren() == true )
                    {
                        AffiliationTreeNode dummyChildNode = new AffiliationTreeNode(this);
                        dummyChildNode.setId(viewRoot.createUniqueId() + Calendar.getInstance().getTimeInMillis());
                        dummyChildNode.setText("DummyChildNode");
                        if (this.getAffiliationSessionBean().isBrowseByAffiliation() == false)
                            dummyChildNode.setAction(application.createMethodBinding("#{affiliation$AffiliationTree.showAffiliationDetail}", null));
                        else
                            this.affiliationTreeNode.setAction(null);
                        // dummyChildNode.addActionListener(null);
                        
                        node.getChildren().add(dummyChildNode);
                    }
                    
                    // addChildren(node, nodeAffiliations.get(i), bean);                    
                }
            }
            catch (Exception e)
            {
                logger.error("Could not create affiliation list." + "\n" + e.toString());
                
                ((ErrorPage)this.getBean(ErrorPage.BEAN_NAME)).setException(e);            
                return;
            }  
        }
    }
    
    /**
     * add the children of the given affiliation to the AffiliationTreeNode
     * @param AffiliationTreeNode node
     */
    public void addChildren(AffiliationTreeNode node)
    {
        
        ItemControllerSessionBean bean = (ItemControllerSessionBean)getBean(ItemControllerSessionBean.BEAN_NAME);
        
        // remove the dummyChildNode; should always be on first position
        node.getChildren().remove(0);
        
        try
        {
            ArrayList<AffiliationVO> childAffiliations = bean.retrieveChildAffiliations(node.getAffiliationVO());
            for (int i = 0; i < childAffiliations.size(); i++)
            {
                AffiliationTreeNode childNode = new AffiliationTreeNode(this);
                childNode.setId(viewRoot.createUniqueId() + Calendar.getInstance().getTimeInMillis());
                childNode.setText(childAffiliations.get(i).getName());
                childNode.setAffiliationVO(childAffiliations.get(i));
                if (this.getAffiliationSessionBean().isBrowseByAffiliation() == false)
                {
                    childNode.setAction(application.createMethodBinding("#{affiliation$AffiliationTree.showAffiliationDetail}", null));
                }
                else
                {
                    this.affiliationTreeNode.setAction(null);
                }
                childNode.setImageURL("/images/documents.gif");
                childNode.addActionListener(childNode);
                
                //add the node to the tree
                node.getChildren().add(childNode);

               // if the child node has itself children add a dummy node to get the collapse button            
                if ( childNode.hasAffiliationChildren() == true )
                {
                    AffiliationTreeNode dummyChildNode = new AffiliationTreeNode(this);
                    dummyChildNode.setId(viewRoot.createUniqueId() + Calendar.getInstance().getTimeInMillis());
                    dummyChildNode.setText("DummyChildNode");
                    if (this.getAffiliationSessionBean().isBrowseByAffiliation() == false)
                        dummyChildNode.setAction(application.createMethodBinding("#{affiliation$AffiliationTree.showAffiliationDetail}", null));
                    else
                        this.affiliationTreeNode.setAction(null);
                    //dummyChildNode.addActionListener(null);
                    
                    childNode.getChildren().add(dummyChildNode);
                }
            }
        }
        catch (Exception e)
        {
            logger.error("Could not create affiliation list." + "\n" + e.toString());
            
            ((ErrorPage)this.getBean(ErrorPage.BEAN_NAME)).setException(e);            
            return;
        }
    }
    
    /**
     * Returns the UIComponent of this UI that can be added to other UI containers.
     * @return the AffiliationTreeNode
     */
    public AffiliationTreeNode getUIComponent()
    {
        return this.affiliationTreeNode;
    }    

    /**
     * Returns the AffiliationSessionBean.
     * 
     * @return a reference to the scoped data bean (AffiliationSessionBean)
     */
    protected AffiliationSessionBean getAffiliationSessionBean()
    {
        return (AffiliationSessionBean)getBean(AffiliationSessionBean.BEAN_NAME);
    }
}
