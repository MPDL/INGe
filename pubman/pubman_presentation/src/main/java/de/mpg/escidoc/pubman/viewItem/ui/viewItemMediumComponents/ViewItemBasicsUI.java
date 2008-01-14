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

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.ui.HTMLElementUI;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.pubman.util.ObjectFormatter;
import de.mpg.escidoc.pubman.viewItem.ViewItemCreatorOrganization;
import de.mpg.escidoc.pubman.viewItem.ViewItemOrganization;
import de.mpg.escidoc.pubman.viewItem.ui.COinSUI;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;

/**
 * UI for creating the basic section of a pubitem to be used in the ViewItemMediumUI.
 * 
 * @author: Tobias Schraut, created 26.09.2007
 * @version: $Revision: 1683 $ $LastChangedDate: 2007-12-17 10:30:45 +0100 (Mon, 17 Dec 2007) $
 */
public class ViewItemBasicsUI extends HtmlPanelGroup
{
    private PubItemVO pubItem;
    private HTMLElementUI htmlElement = new HTMLElementUI();
    private COinSUI coins = new COinSUI();
//  get the selected language...
    private InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance()
            .getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                    InternationalizationHelper.BEAN_NAME);
    // ... and set the refering resource bundle
    private ResourceBundle bundleLabel = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());

    
    /**
     * Variables for changing the style sheet class according to line counts on the html page
     */
    String DivClassTitle= "";
    String DivClassText= "";
    
    /**
     * The list of formatted organzations in an ArrayList.
     */
    private ArrayList<String> organizationArray;
    
    /**
     * The list of affiliated organizations as VO List.
     */
    private ArrayList<ViewItemOrganization> organizationList;
    
    /**
     * The list of affiliated organizations in a list.
     */
    private List<OrganizationVO> affiliatedOrganizationsList;
    
    /**
     * The list of formatted creators in an ArrayList.
     */
    private ArrayList<String> creatorArray;
    
    /**
     * The list of formatted creators which are organizations in an ArrayList.
     */
    private ArrayList<ViewItemCreatorOrganization> creatorOrganizationsArray;
    
    /**
     * Public constructor.
     */
    public ViewItemBasicsUI(PubItemVO pubItemVO)
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
        this.htmlElement = new HTMLElementUI();
        
        ApplicationBean applicationBean = (ApplicationBean)FacesContext.getCurrentInstance()
        .getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                ApplicationBean.BEAN_NAME);
        
        this.getChildren().clear();
        this.setId(CommonUtils.createUniqueId(this));
        
        if(this.pubItem.getMetadata() != null)
        {
            // Prerequisites
            // the list of numbered affiliated organizations 
            createAffiliatedOrganizationList();
            
            // the list of creators (persons and organizations)
            createCreatorList();
            
            
            // *** CREATORS (PERSONS AND ORGANIZATIONS) ***
            // label
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
            
            this.getChildren().add(coins.getCOinSTag(pubItemVO));
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemMedium_lblCreators")));
            
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // value (inserted by the called method)
            addCreatorsToPage();
            
            
            // the affiliated organizations of the creators above 
            // label
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
            
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemMedium_lblAffiliations")));
            
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // value (inserted by the called method)
            addAffiliationsToPage();
            
            // *** DATES ***
            // label
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
            
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemMedium_lblDates")));
            
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // values
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
            
            this.getChildren().add(htmlElement.getStartTag("p"));
            
            boolean empty = true;
            empty = displayDate(this.pubItem.getMetadata().getDatePublishedInPrint(), "ViewItem_lblDatePublishedInPrint", empty);
            empty = displayDate(this.pubItem.getMetadata().getDatePublishedOnline(), "ViewItem_lblDatePublishedOnline", empty);
            empty = displayDate(this.pubItem.getMetadata().getDateAccepted(), "ViewItem_lblDateAccepted", empty);
            empty = displayDate(this.pubItem.getMetadata().getDateSubmitted(), "ViewItem_lblDateSubmitted", empty);
            empty = displayDate(this.pubItem.getMetadata().getDateModified(), "ViewItem_lblDateModified", empty);
            empty = displayDate(this.pubItem.getMetadata().getDateCreated(), "ViewItem_lblDateCreated", empty);

            this.getChildren().add(htmlElement.getEndTag("p"));
            
            this.getChildren().add(htmlElement.getEndTag("div"));

            
            
            // *** GENRE ***
            // label
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
            
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemMedium_lblGenre")));
            
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // value
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
            
            if(this.pubItem.getMetadata().getGenre() != null)
            {
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(applicationBean.convertEnumToString(this.pubItem.getMetadata().getGenre())));
            }
            else
            {
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
            }
            
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // *** DEGREE ***
            // label
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
            
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemMedium_lblDegree")));
            
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
        }
    }

    private boolean displayDate(String date, String label, boolean empty)
    {
        if(empty && date != null && !date.trim().equals(""))
        {
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString(label) + ": " + date));
            return false;
        }
        else
        {
            return empty;
        }

    }

    /**
     * Generates the affiliated organization list as one string for presenting it in the jsp via the dynamic html component.
     * Doubled organizations will be detected and merged. All organizzations will be numbered. 
     */
    private void createAffiliatedOrganizationList()
    {
        String formattedOrganization = "";
        List<CreatorVO> tempCreatorList;
        List<OrganizationVO> tempOrganizationList = null;
        List<OrganizationVO> sortOrganizationList = null;
        this.organizationArray = new ArrayList<String>();
        this.organizationList = new ArrayList<ViewItemOrganization>();
        tempOrganizationList = new ArrayList<OrganizationVO>();
        sortOrganizationList = new ArrayList<OrganizationVO>();
        tempCreatorList = this.pubItem.getMetadata().getCreators();
        int affiliationPosition = 0;
        for (int i = 0; i < tempCreatorList.size(); i++)
        {
            CreatorVO creator = new CreatorVO();
            creator = tempCreatorList.get(i);
            if (creator.getPerson() != null)
            {
                if (creator.getPerson().getOrganizations().size() > 0)
                {
                    for (int listSize = 0; listSize < creator.getPerson().getOrganizations().size(); listSize++)
                    {
                        tempOrganizationList.add(creator.getPerson().getOrganizations().get(listSize));
                    }
                    for (int j = 0; j < tempOrganizationList.size(); j++)
                    {
                        // if the organization is not in the list already, put
                        // it in.
                        if (!sortOrganizationList.contains(tempOrganizationList.get(j)))
                        {
                            affiliationPosition++;
                            sortOrganizationList.add(tempOrganizationList.get(j));
                            ViewItemOrganization viewOrganization = new ViewItemOrganization();
                            if(tempOrganizationList.get(j).getName() != null)
                            {
                                viewOrganization.setOrganizationName(tempOrganizationList.get(j).getName().getValue());
                            }
                            viewOrganization.setOrganizationAddress(tempOrganizationList.get(j).getAddress());
                            viewOrganization.setOrganizationIdentifier(tempOrganizationList.get(j).getIdentifier());
                            viewOrganization.setPosition(new Integer(affiliationPosition).toString());
                            if(tempOrganizationList.get(j).getName() != null)
                            {
                                viewOrganization.setOrganizationInfoPage(tempOrganizationList.get(j).getName().getValue(),
                                        tempOrganizationList.get(j).getAddress());
                            }
                            this.organizationList.add(viewOrganization);
                        }
                    }
                }
            }
        }
        // save the List in the backing bean for later use.
        this.affiliatedOrganizationsList = sortOrganizationList;
        // generate a 'well-formed' list for presentation in the jsp
        for (int k = 0; k < sortOrganizationList.size(); k++)
        {
        	String name = sortOrganizationList.get(k).getName() != null ? sortOrganizationList.get(k).getName().getValue() : "";
        	formattedOrganization = "<p>"+(k + 1) + ": " + name +"</p>" + "<p>" + sortOrganizationList.get(k).getAddress() + "</p>" + "<p>" + sortOrganizationList.get(k).getIdentifier() + "</p>";
            this.organizationArray.add(formattedOrganization);
        }
    }
    
    /**
     * Generates the creator list as list of formatted Strings.
     * 
     * @return String formatted creator list as string
     */
    private void createCreatorList()
    {
        StringBuffer creatorList = new StringBuffer();
        String formattedCreator = "";
        this.creatorArray = new ArrayList<String>();
        this.creatorOrganizationsArray = new ArrayList<ViewItemCreatorOrganization>();
        // counter for organization array
        int counterOrganization = 0;
        StringBuffer annotation;
        ObjectFormatter formatter = new ObjectFormatter();
        
        if(this.pubItem.getMetadata().getCreators() != null)
        {
            for (int i = 0; i < this.pubItem.getMetadata().getCreators().size(); i++)
            {
                CreatorVO creator = new CreatorVO();
                creator = this.pubItem.getMetadata().getCreators().get(i);
                annotation = new StringBuffer();
                int organizationsFound = 0;
                for (int j = 0; j < this.affiliatedOrganizationsList.size(); j++)
                {
                    if (creator.getPerson() != null)
                    {
                        if (creator.getPerson().getOrganizations().contains(this.affiliatedOrganizationsList.get(j)))
                        {
                            if (organizationsFound == 0)
                            {
                                annotation.append("   [");
                            }
                            if (organizationsFound > 0 && j < this.affiliatedOrganizationsList.size())
                            {
                                annotation.append(",");
                            }
                            annotation.append(new Integer(j + 1).toString());
                            organizationsFound++;
                        }
                    }
                }
                if (annotation.length() > 0)
                {
                    annotation.append("]");
                }
                formattedCreator = formatter.formatCreator(creator) + annotation.toString();
                if (creator.getPerson() != null)
                {
                    this.creatorArray.add(formattedCreator);
                }
                if (creator.getOrganization() != null)
                {
                    ViewItemCreatorOrganization creatorOrganization = new ViewItemCreatorOrganization();
                    creatorOrganization.setOrganizationName(formattedCreator);
                    creatorOrganization.setPosition(new Integer(counterOrganization).toString());
                    creatorOrganization.setOrganizationAddress(creator.getOrganization().getAddress());
                    creatorOrganization.setOrganizationInfoPage(formattedCreator, creator.getOrganization()
                            .getAddress());
                    this.creatorOrganizationsArray.add(creatorOrganization);
                    counterOrganization++;
                }
                creatorList.append(formattedCreator);
            }
        }
    }
    
    /**
     * Adds the affiliated organizations to the html page based on the entries in the organizationArray.
     *
     */
    private void addAffiliationsToPage()
    {
        if(this.organizationList != null )
        {
            if(this.organizationList.size() > 0)
            {
                this.getChildren().add(this.htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
                
                for(int i = 0; i < organizationList.size(); i++)
                {
                    this.getChildren().add(this.htmlElement.getStartTagWithStyleClass("div", "itemAffiliations"));
                    
                    // Wrap this text elemet with an html <p> tag
                    this.getChildren().add(this.htmlElement.getStartTag("p"));
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(organizationList.get(i).getPosition() + ": " +organizationList.get(i).getOrganizationName()));
                    this.getChildren().add(this.htmlElement.getEndTag("p"));
                    
                    this.getChildren().add(this.htmlElement.getStartTag("p"));
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(organizationList.get(i).getOrganizationAddress()));
                    this.getChildren().add(this.htmlElement.getEndTag("p"));
                    
                    this.getChildren().add(this.htmlElement.getStartTag("p"));
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(organizationList.get(i).getOrganizationIdentifier()));
                    this.getChildren().add(this.htmlElement.getEndTag("p"));
                    
                    this.getChildren().add(this.htmlElement.getEndTag("div"));
                }
                
                
                this.getChildren().add(this.htmlElement.getEndTag("div"));
            }
            else
            {
                this.getChildren().add(this.htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
                
                this.getChildren().add(this.htmlElement.getStartTag("p"));
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
                this.getChildren().add(this.htmlElement.getEndTag("p"));
                
                this.getChildren().add(this.htmlElement.getEndTag("div"));
            }
        }
        else
        {
            this.getChildren().add(this.htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
            
            this.getChildren().add(this.htmlElement.getStartTag("p"));
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
            this.getChildren().add(this.htmlElement.getEndTag("p"));
            
            this.getChildren().add(this.htmlElement.getEndTag("div"));
        }
    }
    
    /**
     * Adds the creators (persons and organizations) to the html page based on the entries in the creatorArray.
     *
     */
    private void addCreatorsToPage()
    {
        // add the persons first
        if(this.creatorArray != null )
        {
            if(this.creatorArray.size() > 0)
            {
                for(int i = 0; i < creatorArray.size(); i++)
                {
                    //switch the style classes
                    if(new Integer(i/2)*2 == i)
                    {
                        this.DivClassTitle = "itemTitle odd";
                        this.DivClassText = "itemText odd";
                    }
                    else
                    {
                        this.DivClassTitle = "itemTitle";
                        this.DivClassText = "itemText";
                    }
                    if(i > 0)
                    {
                        this.getChildren().add(this.htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
                        
                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
                        
                        this.getChildren().add(this.htmlElement.getEndTag("div"));
                    }
                    this.getChildren().add(this.htmlElement.getStartTagWithStyleClass("div", this.DivClassText));
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(creatorArray.get(i)));
                    this.getChildren().add(this.htmlElement.getEndTag("div"));
                }
            }
        }
        
        // then add the organizations which are creators
        if(this.creatorOrganizationsArray != null )
        {
            if(this.creatorOrganizationsArray.size() > 0)
            {
                for(int i = 0; i < creatorOrganizationsArray.size(); i++)
                {
                    // switch the style classes
                    if(this.DivClassTitle.equals("itemTitle odd"))
                    {
                        this.DivClassTitle = "itemTitle";
                        this.DivClassText = "itemText";
                    }
                    else
                    {
                        this.DivClassTitle = "itemTitle odd";
                        this.DivClassText = "itemText odd";
                    }
                    if(this.creatorArray != null )
                    {
                        if(this.creatorArray.size() > 0)
                        {
                            this.getChildren().add(this.htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
                            
                            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
                            
                            this.getChildren().add(this.htmlElement.getEndTag("div"));
                        }
                    }
                    else
                    {
                        if(i > 0)
                        {
                            this.getChildren().add(this.htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
                            
                            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
                            
                            this.getChildren().add(this.htmlElement.getEndTag("div"));
                        }
                    }
                    this.getChildren().add(this.htmlElement.getStartTagWithStyleClass("div", this.DivClassText));
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(creatorOrganizationsArray.get(i).getOrganizationName()));
                    this.getChildren().add(this.htmlElement.getEndTag("div"));
                }
                
            }
        }
    }
        
    /**
     * This method examines if the date given as parameter is emty or not.
     * If it is not empty a delimiter will be added to the date.
     * 
     * @param date the date that should be examined and delimitered
     * @return String the delimitered date
     */
    private String getDelimiteredDate (String date, boolean delimiter)
    {
        String delimiteredDate = "";
        
        if(date != null)
        {
            if (!date.trim().equals(""))
            {
                if(delimiter == true)
                {
                    delimiteredDate = date + " | ";
                }
                else
                {
                    delimiteredDate = date + " ";
                }
            }
        }
        return delimiteredDate;
    }
}
