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

package de.mpg.escidoc.pubman.viewItem.ui.viewItemFullComponents;

import java.util.ResourceBundle;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import de.mpg.escidoc.pubman.ui.HTMLElementUI;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO;

/**
 * UI for creating the event section of a pubitem to be used in the ViewItemFullUI.
 * 
 * @author: Tobias Schraut, created 26.09.2007
 * @version: $Revision: 1588 $ $LastChangedDate: 2007-11-20 13:18:36 +0100 (Tue, 20 Nov 2007) $
 */
public class ViewItemEventUI extends HtmlPanelGroup
{
    private PubItemVO pubItem;
    private HtmlGraphicImage image;
    private HTMLElementUI htmlElement = new HTMLElementUI();
    
    // get the selected language...
    private InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance()
            .getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                    InternationalizationHelper.BEAN_NAME);
    // ... and set the refering resource bundle
    private ResourceBundle bundleLabel = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
    
    /**
     * Public constructor.
     */
    public ViewItemEventUI(PubItemVO pubItemVO)
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
        this.bundleLabel = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
        
        this.getChildren().clear();
        this.setId(CommonUtils.createUniqueId(this));
        
        // *** HEADER ***
        // add an image to the page
        this.getChildren().add(htmlElement.getStartTag("h2"));
        this.image = new HtmlGraphicImage();
        this.image.setId(CommonUtils.createUniqueId(this.image));
        this.image.setUrl("./images/eseminar_icon.png");
        this.image.setWidth("21");
        this.image.setHeight("25");
        this.getChildren().add(this.image);
        
        // add the subheader
        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblSubHeaderEvent")));
        this.getChildren().add(htmlElement.getEndTag("h2"));
        
        if(this.pubItem.getMetadata() != null)
        {
            if(this.pubItem.getMetadata().getEvent() != null)
            {
                // *** EVENT TITLE ***
                // label
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
                
                if(this.pubItem.getMetadata().getEvent().getTitle() != null 
                        && this.pubItem.getMetadata().getEvent().getTitle().getLanguage() != null 
                        && !this.pubItem.getMetadata().getEvent().getTitle().getLanguage().trim().equals(""))
                {
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblEventTitle") + " <" + this.pubItem.getMetadata().getEvent().getTitle().getLanguage() + ">"));
                }
                else
                {
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblEventTitle")));
                }
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // value
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
                
                if(this.pubItem.getMetadata().getEvent().getTitle() != null)
                {
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getMetadata().getEvent().getTitle().getValue()));
                }
                else
                {
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
                }
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                
                // *** ALTERNATIVE EVENT TITLES ***
                if(this.pubItem.getMetadata().getEvent().getAlternativeTitles() != null)
                {
                    for(int i = 0; i < this.pubItem.getMetadata().getEvent().getAlternativeTitles().size(); i++)
                    {
                        // label
                        this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
                        
                        if(this.pubItem.getMetadata().getEvent().getAlternativeTitles().get(i).getLanguage() != null && !this.pubItem.getMetadata().getEvent().getAlternativeTitles().get(i).getLanguage().trim().equals(""))
                        {
                            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblEventAlternativeTitle") + " <" + this.pubItem.getMetadata().getEvent().getAlternativeTitles().get(i).getLanguage() + ">"));
                        }
                        else
                        {
                            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblEventAlternativeTitle")));
                        }
                        
                        this.getChildren().add(htmlElement.getEndTag("div"));
                        
                        // value
                        this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
                        
                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getMetadata().getEvent().getAlternativeTitles().get(i).getValue()));
                        
                        this.getChildren().add(htmlElement.getEndTag("div"));
                    }
                }
                
                // *** EVENT PLACE ***
                // label
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
                
                if(this.pubItem.getMetadata().getEvent().getPlace() != null 
                        && this.pubItem.getMetadata().getEvent().getPlace().getLanguage() != null 
                        && !this.pubItem.getMetadata().getEvent().getPlace().getLanguage().trim().equals(""))
                {
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblEventPlace")+ " <" + this.pubItem.getMetadata().getEvent().getPlace().getLanguage() + ">"));
                }
                else
                {
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblEventPlace")));
                }
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // value
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
                
                if(this.pubItem.getMetadata().getEvent().getPlace() != null)
                {
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getMetadata().getEvent().getPlace().getValue()));
                }
                else
                {
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
                }
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                
                // *** START / END DATE ***
                // label
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblEventStartEndDate")));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // value
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getStartEndDate()));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                
                // *** INVITED LABEL ***
                if(this.pubItem.getMetadata().getEvent().getInvitationStatus() != null)
                {
                    if(this.pubItem.getMetadata().getEvent().getInvitationStatus().equals(EventVO.InvitationStatus.INVITED))
                    {
                        this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
                        
                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemMedium_lblEventInvited")));
                        
                        this.getChildren().add(htmlElement.getEndTag("div"));
                    }
                    else
                    {
                        this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
                        
                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
                        
                        this.getChildren().add(htmlElement.getEndTag("div"));
                    }
                }
                else
                {
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
                    
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
                    
                    this.getChildren().add(htmlElement.getEndTag("div"));
                }
            }
            else
            {
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblNoEntries")));
                this.getChildren().add(htmlElement.getEndTag("div"));
            }
        }
        else
        {
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblNoEntries")));
            this.getChildren().add(htmlElement.getEndTag("div"));
        }
    }
    
    /**
     * Returns a formatted String including the start and the end date of the event
     * @return String the formatted date string
     */
    private String getStartEndDate()
    {
        StringBuffer date = new StringBuffer();
        
        if(this.pubItem.getMetadata().getEvent().getStartDate() != null)
        {
            date.append(this.pubItem.getMetadata().getEvent().getStartDate());
        }
        
        if(this.pubItem.getMetadata().getEvent().getEndDate() != null)
        {
            date.append(" - ");
            date.append(this.pubItem.getMetadata().getEvent().getEndDate());
        }
        return date.toString();
    }
}
