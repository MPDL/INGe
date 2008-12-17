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

package de.mpg.escidoc.pubman.releases.ui;

import javax.faces.component.UIParameter;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.releases.PubItemVersionVOWrapper;
import de.mpg.escidoc.pubman.ui.ContainerPanelUI;
import de.mpg.escidoc.pubman.ui.HTMLElementUI;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.pubman.viewItem.ViewItemFull;
import de.mpg.escidoc.pubman.viewItem.ViewItemSessionBean;
import de.mpg.escidoc.services.common.valueobjects.VersionHistoryEntryVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;

/**
 * ContainerPanelUI for keeping ViewCollectionUIs.
 * 
 * @author: Thomas Diebäcker, created 12.10.2007
 * @version: $Revision$ $LastChangedDate$
 */
public class ViewReleasePanelUI extends ContainerPanelUI implements ActionListener, ValueChangeListener
{
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ViewReleasePanelUI.class);
    
    private VersionHistoryEntryVO pubItemVersionVO = null;
    private HtmlCommandLink viewItemLink = new HtmlCommandLink();
    private HtmlOutputText viewItemText = new HtmlOutputText();
    private UIParameter version = new UIParameter();
    
    // item list controls
    private HtmlPanelGroup panViewItemPanelControls = new HtmlPanelGroup();

    /**
     * Public constructor.
     * @param pubItemVersionVOWrapper the wrapper with the ValueObject that should be displayed
     * @param releaseListUI the parent ListUI, to be able to call the selectOneItem-method to deselect all other
     *        objects when one gets selected (emulate the radioButtonGroup)
     */
    public ViewReleasePanelUI(PubItemVersionVOWrapper pubItemVersionVOWrapper, ReleaseListUI releaseListUI, int position)
    {
        // call constructor of super class
        super();
        
        this.pubItemVersionVO = pubItemVersionVOWrapper.getVersion();
        
        HTMLElementUI htmlElement = new HTMLElementUI();
        
        this.panControls.setId(CommonUtils.createUniqueId(this.panControls));
        
        this.viewItemText = new HtmlOutputText();
        this.viewItemText.setId(CommonUtils.createUniqueId(this.viewItemText));
        this.viewItemText.setValue(getLabel("ViewItemReleaseHistory_lblVersion") + " " 
                        + new Integer(this.pubItemVersionVO.getReference().getVersionNumber()).toString());
        
        this.version = new UIParameter();
        this.version.setId(CommonUtils.createUniqueId(this.version));
        this.version.setName("itemID");
        this.version.setValue("_lnkViewItem_" + (this.pubItemVersionVO.getReference().getObjectId()).replace(":", "-"));
        
        this.viewItemLink = new HtmlCommandLink();
        this.viewItemLink.setId("_lnkViewItem_" + (this.pubItemVersionVO.getReference().getObjectId()).replace(":", "-"));
        this.viewItemLink.setAction(application.createMethodBinding("#{ViewItemSessionBean.viewRelease}", null));
        this.viewItemLink.setValue(getLabel("ViewItemReleaseHistory_lblVersion") + " " 
                + new Integer(this.pubItemVersionVO.getReference().getVersionNumber()).toString());
        this.viewItemLink.setDisabled(true);
        this.viewItemLink.getChildren().add(this.version);
        
        this.panControls.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
        this.panControls.getChildren().add(this.viewItemLink);
        this.panControls.getChildren().add(htmlElement.getEndTag("div"));
        
        this.panControls.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
        this.panControls.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemReleaseHistory_lblReleaseDate") 
                        + " " + CommonUtils.format(this.pubItemVersionVO.getModificationDate())));
        this.panControls.getChildren().add(htmlElement.getEndTag("div"));
        
        this.panControls.getChildren().add(htmlElement.getEndTag("div"));
        this.panTitleBar.getChildren().add(this.panControls);
        
        this.panControls.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "clear"));
        this.panViewItemPanelControls.setId(CommonUtils.createUniqueId(this.panViewItemPanelControls));
        
        this.addToTitleBar(panViewItemPanelControls);
    }

    /**
     * Action handler for user actions.
     * @param ActionEvent event
     */
    public void processAction(ActionEvent event)
    {
        ItemControllerSessionBean itemControllerSessionBean = (ItemControllerSessionBean)FacesContext.getCurrentInstance()
                .getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                ItemControllerSessionBean.BEAN_NAME);
        
        String itemID = "";
        PubItemVOPresentation pubItemVO = null;
        HtmlCommandLink link = (HtmlCommandLink) (event.getSource());
        
        // then find the item ID delivered by the link´s ID
        if (link != null)
        {
            itemID = link.getId().substring(13).replace("-", ":");
        }
        
        if (itemID != null)
        {
            try
            {
                pubItemVO = itemControllerSessionBean.retrieveItem(itemID);
            }
            catch (Exception e)
            {
                logger.error("Could not retrioeve item by ID: " + itemID + "! ", e);
            }
        }
       
       // set this pub item in the itemcontroller session bean
       itemControllerSessionBean.setCurrentPubItem(pubItemVO);
       
       viewItem(itemID);
    }

    public void processValueChange(ValueChangeEvent arg0) throws AbortProcessingException
    {
        
    }
    
    /**
     * View the selected item. 
     * 
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String viewItem(String itemID)
    {
        // set the reload flag to false to force a redirecting to get a proper URL
        this.getViewItemSessionBean().setHasBeenRedirected(true);
        
        ViewItemFull viewItemFull = (ViewItemFull)FacesContext.getCurrentInstance()
        .getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                ViewItemFull.BEAN_NAME);

        viewItemFull.getPanelItemFull().getChildren().clear();
        viewItemFull.init();
        /*try
        {
            fc.getExternalContext().redirect("viewItemFullPage.jsp?itemID=" + itemID);
        }
        catch (IOException e)
        {
            logger.error("Could not redirect to viewItemFullPage.jsp", e);
        }*/
        return ViewItemFull.LOAD_VIEWITEM;
    }
    
    /**
     * View the selected item. 
     * 
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String viewRelease()
    {
        // set the reload flag to false to force a redirecting to get a proper URL
        this.getViewItemSessionBean().setHasBeenRedirected(true);
        
        ViewItemFull viewItemFull = (ViewItemFull)FacesContext.getCurrentInstance()
        .getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                ViewItemFull.BEAN_NAME);

        viewItemFull.getPanelItemFull().getChildren().clear();
        viewItemFull.init();
        
        return ViewItemFull.LOAD_VIEWITEM;
    }
    
    /**
     * Returns the ViewItemSessionBean.
     * 
     * @return a reference to the scoped data bean (ViewItemSessionBean)
     */
    protected ViewItemSessionBean getViewItemSessionBean()
    {
        ViewItemSessionBean viewItemSessionBean = (ViewItemSessionBean)FacesContext.getCurrentInstance()
        .getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                ViewItemSessionBean.BEAN_NAME);
        
        return viewItemSessionBean;
    }
}

