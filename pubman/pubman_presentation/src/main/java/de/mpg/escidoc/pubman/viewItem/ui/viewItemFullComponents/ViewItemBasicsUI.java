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

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.context.FacesContext;

import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.ui.ContainerPanelUI;
import de.mpg.escidoc.pubman.ui.HTMLElementUI;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.ObjectFormatter;
import de.mpg.escidoc.pubman.viewItem.ViewItemCreatorOrganization;
import de.mpg.escidoc.pubman.viewItem.ViewItemOrganization;
import de.mpg.escidoc.pubman.viewItem.ui.COinSUI;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;

/**
 * UI for creating the basic section of a pubitem to be used in the ViewItemFullUI.
 * 
 * @author: Tobias Schraut, created 10.09.2007
 * @version: $Revision: 1676 $ $LastChangedDate: 2007-12-14 14:03:23 +0100 (Fr, 14 Dez 2007) $
 */
public class ViewItemBasicsUI extends ContainerPanelUI
{
    private PubItemVO pubItem;
    private HtmlGraphicImage image;
    private HTMLElementUI htmlElement = new HTMLElementUI();
    private COinSUI coins = new COinSUI();
    
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

    public ViewItemBasicsUI()
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

        ApplicationBean applicationBean = (ApplicationBean) FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .getApplicationMap()
                .get(ApplicationBean.BEAN_NAME);
        
        this.pubItem = pubItemVO;
        this.htmlElement = new HTMLElementUI();
        this.coins = new COinSUI();
        
        this.getChildren().clear();
        this.setId(CommonUtils.createUniqueId(this));
        
        // Prerequisites
        // the list of numbered affiliated organizations 
        createAffiliatedOrganizationList();
        
        // the list of creators (persons and organizations)
        createCreatorList();
        
        
        this.setId(CommonUtils.createUniqueId(this));

        // *** HEADER ***
        // add an image to the page
        this.getChildren().add(htmlElement.getStartTag("h2"));
        this.image = new HtmlGraphicImage();
        this.image.setId(CommonUtils.createUniqueId(this.image));
        this.image.setUrl("./images/document_icon.png");
        this.image.setWidth("21");
        this.image.setHeight("25");
        this.getChildren().add(this.image);
        
        // add the subheader
        if(this.pubItem.getMetadata().getGenre() != null)
        {
            this.getChildren().add(
                    CommonUtils.getTextElementConsideringEmpty(
                            getLabel("ViewItemFull_lblSubHeaderItem")
                                + " " + getLabel(applicationBean.convertEnumToString(this.pubItem.getMetadata().getGenre()))));
        }
        else
        {
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblSubHeaderItem")));
        }
        
        
        this.getChildren().add(htmlElement.getEndTag("h2"));
        
        if(this.pubItem.getMetadata() != null)
        {
            // Check if this item is withdrawn
            if(this.pubItem.getState() != null && this.pubItem.getState().equals(PubItemVO.State.WITHDRAWN))
            {
                // *** DATE OF WITHDRAWAL ***
                // label
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd withdrawn"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblWithdrawalDate")));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // text
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd withdrawn"));

                if(this.pubItem.getModificationDate() != null)
                {
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(CommonUtils.format(this.pubItem.getModificationDate())));
                }
                else
                {
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
                }

                this.getChildren().add(htmlElement.getEndTag("div"));

                // *** WITHDRAWAL COMMENT***
                // label
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle withdrawn"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblWithdrawalComment")));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // text
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText withdrawn"));
                
                if(this.pubItem.getWithdrawalComment() != null)
                {
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getWithdrawalComment()));
                }
                else
                {
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
                }
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
            }
            
            // *** TITLE *** 
            // label
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
            
            if(this.pubItem.getMetadata().getTitle() != null)
            {
                if(this.pubItem.getMetadata().getTitle().getLanguage() != null)
                {
                    if(!this.pubItem.getMetadata().getTitle().getLanguage().trim().equals(""))
                    {
                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblTitle") + " <" +  this.pubItem.getMetadata().getTitle().getLanguage() + ">"));
                    }
                    else
                    {
                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblTitle")));
                    }
                }
                else
                {
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblTitle")));
                }
            }
            else
            {
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblTitle")));
            }
            
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // value
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
            
            if(this.pubItem.getMetadata().getTitle() != null)
            {
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getMetadata().getTitle().getValue()));
            }
            else
            {
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
            }
            
            this.getChildren().add(coins.getCOinSTag(pubItemVO));
            
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // *** ALTERNATIVE TITLE(S) *** 
            // label
            if(this.pubItem.getMetadata().getAlternativeTitles() != null)
            {
                if(this.pubItem.getMetadata().getAlternativeTitles().size() > 0)
                {
                    for(int i = 0; i < this.pubItem.getMetadata().getAlternativeTitles().size(); i++)
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
                        this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", this.DivClassTitle));
                        
                        if(this.pubItem.getMetadata().getAlternativeTitles().get(i).getLanguage() != null && !this.pubItem.getMetadata().getAlternativeTitles().get(i).getLanguage().trim().equals(""))
                        {
                            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblAlternativeTitle") + " <" + this.pubItem.getMetadata().getAlternativeTitles().get(i).getLanguage() + ">"));
                        }
                        else
                        {
                            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblAlternativeTitle")));
                        }
                        
                        this.getChildren().add(htmlElement.getEndTag("div"));
                        
                        // value
                        this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", this.DivClassText));
                        
                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getMetadata().getAlternativeTitles().get(i).getValue()));
                        
                        this.getChildren().add(htmlElement.getEndTag("div"));
                    }
                }
            }
            
            
            
            // *** CREATORS (PERSONS AND ORGANIZATIONS) ***
            // label
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
            
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblCreators")));
            
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // value (inserted by the called method)
            addCreatorsToPage();
            
            
            // the affiliated organizations of the creators above 
            // label
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
            
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblAffiliations")));
            
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // value (inserted by the called method)
            addAffiliationsToPage();
            
            // Dates
            // label
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
            
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblDates")));
            
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // values
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
            
            this.getChildren().add(htmlElement.getStartTag("p"));
            
            boolean empty = true;
            empty = displayDate(this.pubItem.getMetadata().getDateCreated(), "ViewItem_lblDateCreated", empty);
            empty = displayDate(this.pubItem.getMetadata().getDateModified(), "ViewItem_lblDateModified", empty);
            empty = displayDate(this.pubItem.getMetadata().getDateSubmitted(), "ViewItem_lblDateSubmitted", empty);
            empty = displayDate(this.pubItem.getMetadata().getDateAccepted(), "ViewItem_lblDateAccepted", empty);
            empty = displayDate(this.pubItem.getMetadata().getDatePublishedOnline(), "ViewItem_lblDatePublishedOnline", empty);
            empty = displayDate(this.pubItem.getMetadata().getDatePublishedInPrint(), "ViewItem_lblDatePublishedInPrint", empty);
            
            
//            
//            if(this.pubItem.getMetadata().getDateCreated() != null)
//            {
//                if(!this.pubItem.getMetadata().getDateCreated().trim().equals(""))
//                {
//                    if(this.pubItem.getMetadata().getDateModified() != null || this.pubItem.getMetadata().getDateSubmitted() != null)
//                    {
//                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemMedium_lblDateCreated") + " " + getDelimiteredDate(this.pubItem.getMetadata().getDateCreated(), true)));
//                    }
//                    else
//                    {
//                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemMedium_lblDateCreated") + " " + getDelimiteredDate(this.pubItem.getMetadata().getDateCreated(), false)));
//                    }
//                }
//                else
//                {
//                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
//                }
//            }
//            else
//            {
//                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
//            }
//            if(this.pubItem.getMetadata().getDateModified() != null)
//            {
//                if(!this.pubItem.getMetadata().getDateModified().trim().equals(""))
//                {
//                    if(this.pubItem.getMetadata().getDateSubmitted() != null)
//                    {
//                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemMedium_lblDateModified") + " " + getDelimiteredDate(this.pubItem.getMetadata().getDateModified(), true)));
//                    }
//                    else
//                    {
//                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemMedium_lblDateModified") + " " + getDelimiteredDate(this.pubItem.getMetadata().getDateModified(), false)));
//                    }
//                }
//                else
//                {
//                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
//                }
//            }
//            else
//            {
//                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
//            }
//            if(this.pubItem.getMetadata().getDateSubmitted() != null)
//            {
//                if(!this.pubItem.getMetadata().getDateSubmitted().trim().equals(""))
//                {
//                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemMedium_lblDateSubmitted") + " " + getDelimiteredDate(this.pubItem.getMetadata().getDateSubmitted(), false)));
//                }
//                else
//                {
//                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
//                }
//            }
//            else
//            {
//                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
//            }
            this.getChildren().add(htmlElement.getEndTag("p"));
            
            this.getChildren().add(htmlElement.getEndTag("div"));
//            
//            // empty label (only set this if at least one of the dates is populated)
//            if(!(this.pubItem.getMetadata().getDateAccepted() == null && this.pubItem.getMetadata().getDatePublishedOnline() == null && this.pubItem.getMetadata().getDatePublishedInPrint() == null))
//            {
//                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
//                
//                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
//                
//                this.getChildren().add(htmlElement.getEndTag("div"));
//                
//                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
//                
//                this.getChildren().add(htmlElement.getStartTag("p"));
//                if(this.pubItem.getMetadata().getDateAccepted() != null)
//                {
//                    if(!this.pubItem.getMetadata().getDateAccepted().trim().equals(""))
//                    {
//                        if(this.pubItem.getMetadata().getDatePublishedOnline() != null || this.pubItem.getMetadata().getDatePublishedInPrint() != null)
//                        {
//                            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemMedium_lblDateAccepted") + " " + getDelimiteredDate(this.pubItem.getMetadata().getDateAccepted(), true)));
//                        }
//                        else
//                        {
//                            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemMedium_lblDateAccepted") + " " + getDelimiteredDate(this.pubItem.getMetadata().getDateAccepted(), false)));
//                        }
//                    }
//                    else
//                    {
//                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
//                    }
//                }
//                else
//                {
//                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
//                }
//                if(this.pubItem.getMetadata().getDatePublishedOnline() != null)
//                {
//                    if(!this.pubItem.getMetadata().getDatePublishedOnline().trim().equals(""))
//                    {
//                        if(this.pubItem.getMetadata().getDatePublishedInPrint() != null)
//                        {
//                            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemMedium_lblDatePublishedOnline") + " " + getDelimiteredDate(this.pubItem.getMetadata().getDatePublishedOnline(), true)));
//                        }
//                        else
//                        {
//                            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemMedium_lblDatePublishedOnline") + " " + getDelimiteredDate(this.pubItem.getMetadata().getDatePublishedOnline(), false)));
//                        }
//                    }
//                    else
//                    {
//                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
//                    }
//                }
//                else
//                {
//                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
//                }
//                if(this.pubItem.getMetadata().getDatePublishedInPrint() != null)
//                {
//                    if(!this.pubItem.getMetadata().getDatePublishedInPrint().trim().equals(""))
//                    {
//                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemMedium_lblDatePublishedInPrint") + " " + getDelimiteredDate(this.pubItem.getMetadata().getDatePublishedInPrint(), false)));
//                    }
//                    else
//                    {
//                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
//                    }
//                }
//                else
//                {
//                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
//                }
//                this.getChildren().add(htmlElement.getEndTag("p"));
//                
//                this.getChildren().add(htmlElement.getEndTag("div"));
//            }
            
            // The  Subject
            // label
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
            
            if (this.pubItem.getMetadata().getSubject() != null && this.pubItem.getMetadata().getSubject().getLanguage() != null && !this.pubItem.getMetadata().getSubject().getLanguage().trim().equals(""))
            {
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblSubject")+ " <" + this.pubItem.getMetadata().getSubject().getLanguage() + ">"));
            }
            else
            {
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblSubject")));
            }
            
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // value
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
            
            if(this.pubItem.getMetadata().getSubject() != null)
            {
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getMetadata().getSubject().getValue()));
            }
            else
            {
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
            }
            
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // The  Languages of the Publication
            // label
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
            
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblLanguages")));
            
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // value
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
            
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLanguages()));
            
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // The  Review method
            // label
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
            
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblRevisionMethod")));
            
            this.getChildren().add(htmlElement.getEndTag("div"));
            
            // value
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
            
            if(this.pubItem.getMetadata().getReviewMethod() != null)
            {
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel(applicationBean.convertEnumToString(this.pubItem.getMetadata().getReviewMethod()))));
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
        if(date != null && !date.trim().equals(""))
        {
            if(!empty)
            {
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(" | "));
            }
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel(label) + ": " + date));
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
                        this.DivClassTitle = "itemTitle";
                        this.DivClassText = "itemText";
                    }
                    else
                    {
                        this.DivClassTitle = "itemTitle odd";
                        this.DivClassText = "itemText odd";
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
                this.getChildren().add(this.htmlElement.getStartTagWithStyleClass("div", "itemText"));
                
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
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(creatorOrganizationsArray.get(i).getOrganizationName()));
                }
                this.getChildren().add(this.htmlElement.getEndTag("div"));
            }
        }
    }

    /**
     * Gets language(s) of the item' s content.
     * 
     * @return String formatted languages
     */
    private String getLanguages()
    {
        StringBuffer languages = new StringBuffer();
        if (this.pubItem.getMetadata().getLanguages() != null)
        {
            for (int i = 0; i < this.pubItem.getMetadata().getLanguages().size(); i++)
            {
                languages.append(this.pubItem.getMetadata().getLanguages().get(i));
                if (i < this.pubItem.getMetadata().getLanguages().size() - 1)
                {
                    languages.append(", ");
                }
            }
        }
        return languages.toString();
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
