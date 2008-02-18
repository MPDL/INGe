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
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionListener;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.affiliation.AffiliationSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;

/**
 * UI tree component for browsing by affiliation. 
 * 
 * @author: Hugo Niedermaier, Basics by Thomas Dieb�cker, created 31.05.2007
 * @version: $Revision: 14 $ $LastChangedDate: 2007-11-27 19:14:41 +0100 (Di, 27 Nov 2007) $
 * Revised by NiH: 09.08.2007
 */
public class AffiliationTreeNodeUI extends FacesBean implements ActionListener
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
        UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
        
        this.affiliation = affiliation;
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
        
        // this.affiliationTreeNode.setImageURL("/images/affiliations_tree.gif");                        
        this.affiliationTreeNode.setId(viewRoot.createUniqueId() + Calendar.getInstance().getTimeInMillis());
        this.affiliationTreeNode.setAffiliationVO(affiliation);
        this.affiliationTreeNode.setValue(affiliation.getName());

        HtmlGraphicImage image = (HtmlGraphicImage)getApplication().createComponent(HtmlGraphicImage.COMPONENT_TYPE);
        image.setUrl("/images/affiliations_tree.gif");
        HtmlCommandLink commandLink = (HtmlCommandLink)getApplication().createComponent(HtmlCommandLink.COMPONENT_TYPE);
        commandLink.setValue(affiliation.getName());
        HtmlPanelGroup label = (HtmlPanelGroup)getApplication().createComponent(HtmlPanelGroup.COMPONENT_TYPE);
        label.getChildren().add(image);
        label.getChildren().add(commandLink);
        this.affiliationTreeNode.getFacets().put("label", label); 
        
        if (this.getAffiliationSessionBean().isBrowseByAffiliation() == false)
        {
            commandLink.setAction(application.createMethodBinding("#{AffiliationTree.showAffiliationDetail}", null));
        }
        else
        {
            commandLink.setAction(null);
        }
        commandLink.addActionListener(this);

        // create one dummyChildNode for every ChildRO of this affiliation
        // this dummyChildNode is cleared when the node is filled with the existing affiliations
        // in the action handler when the user opens the appropriate tree node
        if (affiliation.getChildAffiliations().size() > 0)
        {
            AffiliationTreeNode dummyChildNode = new AffiliationTreeNode(this);
            dummyChildNode.setId(viewRoot.createUniqueId() + Calendar.getInstance().getTimeInMillis());
            dummyChildNode.setValue("DummyChildNode");
            
            HtmlGraphicImage dummyImage = (HtmlGraphicImage)getApplication().createComponent(HtmlGraphicImage.COMPONENT_TYPE);
            dummyImage.setUrl("/images/affiliations_tree.gif");
            HtmlCommandLink dummyLink = (HtmlCommandLink)getApplication().createComponent(HtmlCommandLink.COMPONENT_TYPE);
            dummyLink.setValue("DummyChildNode");
            HtmlPanelGroup dummyLabel = (HtmlPanelGroup)getApplication().createComponent(HtmlPanelGroup.COMPONENT_TYPE);
            dummyLabel.getChildren().add(dummyImage);
            dummyLabel.getChildren().add(dummyLink);
            dummyChildNode.getFacets().put("label", dummyLabel); 
            
            if (this.getAffiliationSessionBean().isBrowseByAffiliation() == false)
                dummyLink.setAction(application.createMethodBinding("#{AffiliationTree.showAffiliationDetail}", null));
            else
                dummyLink.setAction(null);
            dummyLink.addActionListener(this);
            
            this.affiliationTreeNode.getChildren().add(dummyChildNode);
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
               
            ItemControllerSessionBean bean = (ItemControllerSessionBean)getBean(ItemControllerSessionBean.class);
            try
            {
                //get all children of this node
                ArrayList<AffiliationVO> nodeAffiliations = null; //bean.retrieveChildAffiliations(this.affiliation);
                
                for (int i = 0; i < nodeAffiliations.size(); i++)
                {
                    AffiliationTreeNode node = new AffiliationTreeNode(this);
                    node.setId(viewRoot.createUniqueId() + Calendar.getInstance().getTimeInMillis());
                    node.setValue(nodeAffiliations.get(i).getName());
                    node.setAffiliationVO(nodeAffiliations.get(i));

                    HtmlGraphicImage nodeImage = (HtmlGraphicImage)getApplication().createComponent(HtmlGraphicImage.COMPONENT_TYPE);
                    nodeImage.setUrl("/images/documents.gif");
                    HtmlCommandLink nodeLink = (HtmlCommandLink)getApplication().createComponent(HtmlCommandLink.COMPONENT_TYPE);
                    nodeLink.setValue(nodeAffiliations.get(i).getName());
                    HtmlPanelGroup nodeLabel = (HtmlPanelGroup)getApplication().createComponent(HtmlPanelGroup.COMPONENT_TYPE);
                    nodeLabel.getChildren().add(nodeImage);
                    nodeLabel.getChildren().add(nodeLink);
                    node.getFacets().put("label", nodeLabel); 

                    if (this.getAffiliationSessionBean().isBrowseByAffiliation() == false)
                    {
                        nodeLink.setAction(application.createMethodBinding("#{AffiliationTree.showAffiliationDetail}", null));
                    }
                    else
                    {
                        nodeLink.setAction(null);
                    }
                    nodeLink.addActionListener(node);
                      
                    //add the childnodes to the tree
                    // if (opend == false)
                    {
                        this.affiliationTreeNode.getChildren().add(node);
                    }
                    // create dummychildnodes for nodes which has children
                    ArrayList<AffiliationVO> nodeAff = null; //bean.retrieveChildAffiliations(node.getAffiliationVO());
                    if ( nodeAff.size() > 0 )
                    {
                        AffiliationTreeNode dummyChildNode = new AffiliationTreeNode(this);
                        dummyChildNode.setId(viewRoot.createUniqueId() + Calendar.getInstance().getTimeInMillis());
                        dummyChildNode.setValue("DummyChildNode");

                        HtmlGraphicImage dummyImage = (HtmlGraphicImage)getApplication().createComponent(HtmlGraphicImage.COMPONENT_TYPE);
                        dummyImage.setUrl("/images/documents.gif");
                        HtmlCommandLink dummyLink = (HtmlCommandLink)getApplication().createComponent(HtmlCommandLink.COMPONENT_TYPE);
                        dummyLink.setValue("DummyChildNode");
                        HtmlPanelGroup dummyLabel = (HtmlPanelGroup)getApplication().createComponent(HtmlPanelGroup.COMPONENT_TYPE);
                        dummyLabel.getChildren().add(dummyImage);
                        dummyLabel.getChildren().add(dummyLink);
                        dummyChildNode.getFacets().put("label", dummyLabel); 
                        
                        if (this.getAffiliationSessionBean().isBrowseByAffiliation() == false)
                            dummyLink.setAction(application.createMethodBinding("#{AffiliationTree.showAffiliationDetail}", null));
                        else
                            dummyLink.setAction(null);
                        dummyLink.addActionListener(null);
                        
                        node.getChildren().add(dummyChildNode);
                    }
                    
                    // addChildren(node, nodeAffiliations.get(i), bean);                    
                }
            }
            catch (Exception e)
            {
                logger.error("Could not create affiliation list." + "\n" + e.toString());
                
                ((ErrorPage)this.getBean(ErrorPage.class)).setException(e);            
                return;
            }  
        }
    }
    
    /**
     * add the children of the given affiliation to the AffiliationTreeNode
     * this function is implemented as a recursion and is called as long as
     * there are more nodes 
     * @param AffiliationTreeNode node
     * @param AffiliationVO affiliation
     * @param ItemControllerSessionBean bean
     */
    public void addChildren(AffiliationTreeNode node)
    {
        
        ItemControllerSessionBean bean = (ItemControllerSessionBean)getBean(ItemControllerSessionBean.class);
        
        // remove the dummyChildNode; should always be on first position
        node.getChildren().remove(0);
        
        try
        {
            ArrayList<AffiliationVO> childAffiliations = null; //bean.retrieveChildAffiliations(node.getAffiliationVO());
            for (int i = 0; i < childAffiliations.size(); i++)
            {
                AffiliationTreeNode childNode = new AffiliationTreeNode(this);
                childNode.setId(viewRoot.createUniqueId() + Calendar.getInstance().getTimeInMillis());
                childNode.setValue(childAffiliations.get(i).getName());
                childNode.setAffiliationVO(childAffiliations.get(i));

                HtmlGraphicImage nodeImage = (HtmlGraphicImage)getApplication().createComponent(HtmlGraphicImage.COMPONENT_TYPE);
                nodeImage.setUrl("/images/documents.gif");
                HtmlCommandLink nodeLink = (HtmlCommandLink)getApplication().createComponent(HtmlCommandLink.COMPONENT_TYPE);
                nodeLink.setValue(childAffiliations.get(i).getName());
                HtmlPanelGroup nodeLabel = (HtmlPanelGroup)getApplication().createComponent(HtmlPanelGroup.COMPONENT_TYPE);
                nodeLabel.getChildren().add(nodeImage);
                nodeLabel.getChildren().add(nodeLink);
                node.getFacets().put("label", nodeLabel); 

                if (this.getAffiliationSessionBean().isBrowseByAffiliation() == false)
                {
                    nodeLink.setAction(application.createMethodBinding("#{AffiliationTree.showAffiliationDetail}", null));
                }
                else
                {
                    nodeLink.setAction(null);
                }
                nodeLink.addActionListener(childNode);
                
                //add the node to the tree
                node.getChildren().add(childNode);

               // if the child node has itself children add a dummy node to get the collapse button
                ArrayList<AffiliationVO> nodeAff = null; //bean.retrieveChildAffiliations(childNode.getAffiliationVO());
                if ( nodeAff.size() > 0 )
                {
                    AffiliationTreeNode dummyChildNode = new AffiliationTreeNode(this);
                    dummyChildNode.setId(viewRoot.createUniqueId() + Calendar.getInstance().getTimeInMillis());
                    dummyChildNode.setValue("DummyChildNode");

                    HtmlGraphicImage dummyImage = (HtmlGraphicImage)getApplication().createComponent(HtmlGraphicImage.COMPONENT_TYPE);
                    dummyImage.setUrl("/images/documents.gif");
                    HtmlCommandLink dummyLink = (HtmlCommandLink)getApplication().createComponent(HtmlCommandLink.COMPONENT_TYPE);
                    dummyLink.setValue("DummyChildNode");
                    HtmlPanelGroup dummyLabel = (HtmlPanelGroup)getApplication().createComponent(HtmlPanelGroup.COMPONENT_TYPE);
                    dummyLabel.getChildren().add(dummyImage);
                    dummyLabel.getChildren().add(dummyLink);
                    dummyChildNode.getFacets().put("label", dummyLabel); 

                    if (this.getAffiliationSessionBean().isBrowseByAffiliation() == false)
                        dummyLink.setAction(application.createMethodBinding("#{AffiliationTree.showAffiliationDetail}", null));
                    else
                        dummyLink.setAction(null);
                    
                    childNode.getChildren().add(dummyChildNode);
                }
            }
        }
        catch (Exception e)
        {
            logger.error("Could not create affiliation list." + "\n" + e.toString());
            
            ((ErrorPage)this.getBean(ErrorPage.class)).setException(e);            
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
        return (AffiliationSessionBean)getBean(AffiliationSessionBean.class);
    }
}
