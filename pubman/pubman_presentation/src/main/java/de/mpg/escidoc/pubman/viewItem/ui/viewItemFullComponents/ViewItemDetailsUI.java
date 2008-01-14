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
import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.ui.HTMLElementUI;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;

/**
 * UI for creating the details section of a pubitem to be used in the ViewItemFullUI.
 * 
 * @author: Tobias Schraut, created 11.09.2007
 * @version: $Revision: 1688 $ $LastChangedDate: 2007-12-17 15:30:02 +0100 (Mon, 17 Dec 2007) $
 */
public class ViewItemDetailsUI extends HtmlPanelGroup
{
    private PubItemVO pubItem;
    private HtmlGraphicImage image;
    private HTMLElementUI htmlElement = new HTMLElementUI();
    
    /**
     * Variables for changing the style sheet class according to line counts on the html page
     */
    String DivClassTitle= "";
    String DivClassText= "";
    
    // get the selected language...
    private InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance()
            .getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                    InternationalizationHelper.BEAN_NAME);
    // ... and set the refering resource bundle
    private ResourceBundle bundleLabel = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
    
    
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
        this.bundleLabel = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
        ApplicationBean applicationBean = (ApplicationBean)FacesContext.getCurrentInstance()
        .getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                ApplicationBean.BEAN_NAME);
        
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
        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblSubHeaderDetails")));
        this.getChildren().add(htmlElement.getEndTag("h2"));
        
        if(this.pubItem.getMetadata() != null)
        {
            if((this.pubItem.getMetadata().getAbstracts() != null && this.pubItem.getMetadata().getAbstracts().size() > 0)
                    || (this.pubItem.getMetadata().getTableOfContents() != null && this.pubItem.getMetadata().getTableOfContents().getValue() != null && !this.pubItem.getMetadata().getTableOfContents().getValue().trim().equals(""))
                    || this.pubItem.getMetadata().getPublishingInfo() != null
                    || (this.pubItem.getMetadata().getTotalNumberOfPages() != null && !this.pubItem.getMetadata().getTotalNumberOfPages().trim().equals(""))
                    || this.pubItem.getMetadata().getDegree() != null
                    || (this.pubItem.getMetadata().getLocation() != null && !this.pubItem.getMetadata().getLocation().trim().equals(""))
                    || (this.pubItem.getMetadata().getIdentifiers() != null && this.pubItem.getMetadata().getIdentifiers().size() > 0))
            {
                // *** ABSTRACT(S) ***
                if(this.pubItem.getMetadata().getAbstracts() != null)
                {
                    if(this.pubItem.getMetadata().getAbstracts().size() > 0)
                    {
                        for(int i = 0; i < this.pubItem.getMetadata().getAbstracts().size(); i++)
                        {
                            // switch the style classes
                            if(new Integer(i/2)*2 == i)
                            {
                                this.DivClassTitle = "itemTitle";
                                this.DivClassText = "itemText";
                            }
                            else
                            {
                                this.DivClassTitle = "itemTitle odd";
                                this.DivClassText = "itemText odd";
                            }
                            // label
                            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", this.DivClassTitle));
                            
                            if(this.pubItem.getMetadata().getAbstracts().get(i).getLanguage() != null && !this.pubItem.getMetadata().getAbstracts().get(i).getLanguage().trim().equals(""))
                            {
                                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblAbstract") + " <"  + this.pubItem.getMetadata().getAbstracts().get(i).getLanguage() + ">"));
                            }
                            else
                            {
                                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblAbstract")));
                            }
                            
                            this.getChildren().add(htmlElement.getEndTag("div"));
                            
                            // value
                            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", this.DivClassText));
                            
                            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getMetadata().getAbstracts().get(i).getValue()));
                            
                            this.getChildren().add(htmlElement.getEndTag("div"));
                        }
                    }
                }
                
                // *** TABLE OF CONTENTS (TOC)
                // label
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
                
                if (this.pubItem.getMetadata().getTableOfContents() != null 
                        && this.pubItem.getMetadata().getTableOfContents().getValue() != null 
                        && this.pubItem.getMetadata().getTableOfContents().getLanguage() != null 
                        && !this.pubItem.getMetadata().getTableOfContents().getLanguage().trim().equals(""))
                {
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblTOC") + " <" + this.pubItem.getMetadata().getTableOfContents().getLanguage() + ">"));
                }
                else
                {
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblTOC")));
                }
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // value
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
                
                if(this.pubItem.getMetadata().getTableOfContents() != null)
                {
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getMetadata().getTableOfContents().getValue()));
                }
                else
                {
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
                }
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // *** PUBLISHING INFO ***
                // label
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblPublishingInfo")));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // value
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getPublishingInfo()));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // *** PAGES ***
                //label
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblPages")));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // value
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getMetadata().getTotalNumberOfPages()));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // *** DEGREE TYPE ***
                // label
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblDegreeType")));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // value
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
                
                if(this.pubItem.getMetadata().getDegree() != null)
                {
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(applicationBean.convertEnumToString(this.pubItem.getMetadata().getDegree())));
                }
                else
                {
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
                }
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // *** PUBLICATION LOCATION ***
                // label
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblPublicationLocation")));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // value
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getMetadata().getLocation()));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // *** IDENTIFIERS ***
                //label
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblIdentifiers")));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // value
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getIdentifiers()));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // *** EDITION ***
                // label
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblEdition")));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // value
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
                
                if(this.pubItem.getMetadata().getPublishingInfo() != null)
                {
                    if(this.pubItem.getMetadata().getPublishingInfo().getEdition() != null)
                    {
                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getMetadata().getPublishingInfo().getEdition()));
                    }
                    else
                    {
                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
                    }
                }
                else
                {
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
                }
                
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // *** RELATIONS ***
                addRelationsToPage();
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
            // Edition
            if(this.pubItem.getMetadata().getPublishingInfo().getEdition() != null)
            {
                publishingInfo.append(this.pubItem.getMetadata().getPublishingInfo().getEdition());
            }
            
            // Comma
            if((this.pubItem.getMetadata().getPublishingInfo().getEdition() != null && !this.pubItem.getMetadata().getPublishingInfo().getEdition().trim().equals("")) && ((this.pubItem.getMetadata().getPublishingInfo().getPlace() != null && !this.pubItem.getMetadata().getPublishingInfo().getPlace().trim().equals("")) || (this.pubItem.getMetadata().getPublishingInfo().getPublisher() != null && !this.pubItem.getMetadata().getPublishingInfo().getPublisher().trim().equals(""))))
            {
                    publishingInfo.append(". ");
            }
            
            // Place
            if(this.pubItem.getMetadata().getPublishingInfo().getPlace() != null)
            {
                publishingInfo.append(this.pubItem.getMetadata().getPublishingInfo().getPlace().trim());
            }
            
            // colon
            if(this.pubItem.getMetadata().getPublishingInfo().getPublisher() != null && !this.pubItem.getMetadata().getPublishingInfo().getPublisher().trim().equals("") && this.pubItem.getMetadata().getPublishingInfo().getPlace() != null && !this.pubItem.getMetadata().getPublishingInfo().getPlace().trim().equals(""))
            {
                    publishingInfo.append(" : ");
            }
            
            // Publisher
            if(this.pubItem.getMetadata().getPublishingInfo().getPublisher() != null)
            {
                publishingInfo.append(this.pubItem.getMetadata().getPublishingInfo().getPublisher().trim());
            }
        }
        return publishingInfo.toString();
    }
    
    /**
     * Returns all Identifiers as formatted String
     * @return String the formatted Identifiers
     */
    private String getIdentifiers()
    {
        StringBuffer identifiers = new StringBuffer();
        
        if(this.pubItem.getMetadata().getIdentifiers() != null)
        {
            for(int i = 0; i < this.pubItem.getMetadata().getIdentifiers().size(); i++)
            {
                identifiers.append(this.pubItem.getMetadata().getIdentifiers().get(i).getTypeString());
                identifiers.append(": ");
                identifiers.append(this.pubItem.getMetadata().getIdentifiers().get(i).getId());
                if(i < this.pubItem.getMetadata().getIdentifiers().size() - 1)
                {
                    identifiers.append(", ");
                }
            }
        }
        return identifiers.toString();
    }
    
    /**
     * adds Relations as HTML elements to the current page
     */
    private void addRelationsToPage()
    {
        //TODO ScT: Comment in when relations of items come back....
        /*String DivClassTitle= "";
        String DivClassText= "";
        for(int i = 0; i < this.pubItem.getMetadata().getRelations().size(); i++)
        {
            StringBuffer relation = new StringBuffer();
            if(this.pubItem.getMetadata().getRelations().get(i).getType().name() != null)
            {
                relation.append(this.pubItem.getMetadata().getRelations().get(i).getType().name());
                relation.append(": ");
            }
            if (this.pubItem.getMetadata().getRelations().get(i).getIdentifier().getType().name() != null)
            {
                relation.append(this.pubItem.getMetadata().getRelations().get(i).getIdentifier().getType().name());
                relation.append(": ");
            }
            if(this.pubItem.getMetadata().getRelations().get(i).getIdentifier().getId() != null)
            {
                relation.append(this.pubItem.getMetadata().getRelations().get(i).getIdentifier().getId());
            }
            if(this.pubItem.getMetadata().getRelations().get(i).getShortDescriptions() != null)
            {
                relation.append(" (");
                for(int j = 0; j < this.pubItem.getMetadata().getRelations().get(i).getShortDescriptions().size(); j++)
                {
                    relation.append(this.pubItem.getMetadata().getRelations().get(i).getShortDescriptions().get(j).getValue());
                    if (j < this.pubItem.getMetadata().getRelations().get(i).getShortDescriptions().size() - 1)
                    {
                        relation.append(", ");
                    }
                }
                relation.append(")");
                
            }
            
            // label
            if(new Integer(i/2)*2 == i)
            {
                DivClassTitle = "itemTitle";
                DivClassText = "itemText";
            }
            else
            {
                DivClassTitle = "itemTitle odd";
                DivClassText = "itemText odd";
            }
            
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", DivClassTitle));
            
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblRelation")));
            
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // value
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", DivClassText));
            
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(relation.toString()));
            
            this.getChildren().add(htmlElement.getEndTag("div"));
        }*/
        
    }
}
