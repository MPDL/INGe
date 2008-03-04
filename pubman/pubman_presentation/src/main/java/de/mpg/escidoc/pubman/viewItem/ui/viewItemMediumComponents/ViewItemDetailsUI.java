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

package de.mpg.escidoc.pubman.viewItem.ui.viewItemMediumComponents;

import java.util.ResourceBundle;

import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;

import de.mpg.escidoc.pubman.ui.HTMLElementUI;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;

/**
 * UI for creating the details section of a pubitem to be used in the ViewItemMediumUI.
 * 
 * @author: Tobias Schraut, created 26.09.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Di, 20 Nov 2007) $
 */
public class ViewItemDetailsUI extends HtmlPanelGroup
{
    private PubItemVO pubItem;
    private HtmlGraphicImage image;
    private HTMLElementUI htmlElement = new HTMLElementUI();
    
    // get the selected language...
    private InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance()
            .getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                    InternationalizationHelper.BEAN_NAME);
    // ... and set the refering resource bundle
    private ResourceBundle bundleLabel = ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle());
    
    public ViewItemDetailsUI()
    {     
    	
    }
    
    public Object processSaveState(FacesContext context) 
    {
        Object superState = super.processSaveState(context);
        return new Object[] {superState, new Integer(getChildCount())};
    }
    
    public void processRestoreState(FacesContext context, Object state) 
    {
        // At this point in time the tree has already been restored, but not before our ctor added the default children.
        // Since we saved the number of children in processSaveState, we know how many children should remain within
        // this component. We assume that the saved tree will have been restored 'behind' the children we put into it
        // from within the ctor.
        Object[] values = (Object[]) state;
        Integer savedChildCount = (Integer) values[1];
        for (int i = getChildCount() - savedChildCount.intValue(); i > 0; i--) 
        {
            getChildren().remove(0);
        }
        super.processRestoreState(context, values[0]);
    }
    
    /**
     * Public constructor.
     */
    public ViewItemDetailsUI(PubItemVO pubItemVO)
    {
        initialize(pubItemVO);
    }
    
    /**
     * Initializes the UI and sets all attributes of the GUI components.
     * 
     * @param pubItemVO a pubitem
     */
    protected void initialize(PubItemVO pubItemVO)
    {
        this.pubItem = pubItemVO;
        
        // get the selected language...
        this.i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance()
                .getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                        InternationalizationHelper.BEAN_NAME);
        // ... and set the refering resource bundle
        this.bundleLabel = ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle());
        
        this.getChildren().clear();
        this.setId(CommonUtils.createUniqueId(this));
        
        // *** HEADER ***
        // add an image to the page
        this.getChildren().add(htmlElement.getStartTag("h2"));
        this.image = new HtmlGraphicImage();
        this.image.setId(CommonUtils.createUniqueId(this.image));
        this.image.setUrl("./images/bt_nb1_22xy22_cont.gif");
        this.image.setWidth("21");
        this.image.setHeight("25");
        this.getChildren().add(this.image);
        
        // add the subheader
        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemMedium_lblSubHeaderDetails")));
        this.getChildren().add(htmlElement.getEndTag("h2"));
        
        if(this.pubItem.getMetadata() != null)
        {
            // *** PUBLISHING INFO ***
            if(this.pubItem.getMetadata().getPublishingInfo() != null)
            {
                // label
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemMedium_lblPublishingInfo")));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // value
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getPublishingInfo()));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
            }
            
            // *** PAGES ***
            if(this.pubItem.getMetadata().getTotalNumberOfPages() != null)
            {
                // label
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemMedium_lblPages")));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // value
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getMetadata().getTotalNumberOfPages()));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
            }
        }
    }
    
    /**
     * Returns the formatted Publishing Info according to filled elements
     * @return String the formatted Publishing Info
     */
    private String getPublishingInfo()
    {
        StringBuffer publishingInfo = new StringBuffer();
        publishingInfo.append("");
        if(this.pubItem.getMetadata().getPublishingInfo() != null)
        {
            if(this.pubItem.getMetadata().getPublishingInfo().getEdition() != null)
            {
                publishingInfo.append(this.pubItem.getMetadata().getPublishingInfo().getEdition());
            }
            
            if(this.pubItem.getMetadata().getPublishingInfo().getEdition() != null && this.pubItem.getMetadata().getPublishingInfo().getPlace() != null)
            {
                publishingInfo.append(", ");
                publishingInfo.append(this.pubItem.getMetadata().getPublishingInfo().getPlace());
            }
            
            if(this.pubItem.getMetadata().getPublishingInfo().getPublisher() != null)
            {
                publishingInfo.append(": ");
                publishingInfo.append(this.pubItem.getMetadata().getPublishingInfo().getPublisher());
            }
        }
        return publishingInfo.toString();
    }
}
