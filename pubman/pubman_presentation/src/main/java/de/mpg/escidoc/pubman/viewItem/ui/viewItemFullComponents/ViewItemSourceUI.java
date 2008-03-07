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
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;

/**
 * UI for creating the source section of a pubitem to be used in the ViewItemFullUI.
 * 
 * @author: Tobias Schraut, created 26.09.2007
 * @version: $Revision: 1609 $ $LastChangedDate: 2007-11-26 18:21:32 +0100 (Mo, 26 Nov 2007) $
 */
public class ViewItemSourceUI extends ContainerPanelUI
{
    private PubItemVO pubItem;
    private HtmlGraphicImage image;
    private HTMLElementUI htmlElement = new HTMLElementUI();

    /**
     * Variables for changing the style sheet class according to line counts on the html page
     */
    String divClassTitle= "";
    String divClassText= "";

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

    public ViewItemSourceUI()
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
    public ViewItemSourceUI(PubItemVO pubItemVO)
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
        ApplicationBean applicationBean = (ApplicationBean) FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .getApplicationMap()
                .get(ApplicationBean.BEAN_NAME);

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
        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblSubHeaderSource")));
        this.getChildren().add(htmlElement.getEndTag("h2"));

        if (this.pubItem.getMetadata() != null)
        {
            if (this.pubItem.getMetadata().getSources() != null && this.pubItem.getMetadata().getSources().size() > 0)
            {
                for (int i = 0; i < this.pubItem.getMetadata().getSources().size(); i++)
                {

                    // Prerequisites
                    // the list of numbered affiliated organizations
                    createAffiliatedOrganizationList(i);

                    // the list of creators (persons and organizations)
                    createCreatorList(i);

                    // *** SOURCE TITLE ***
                    // label
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));

                    if(this.pubItem.getMetadata().getSources().get(i).getTitle() != null && this.pubItem.getMetadata().getSources().get(i).getTitle().getLanguage() != null && !this.pubItem.getMetadata().getSources().get(i).getTitle().getLanguage().trim().equals(""))
                    {
                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblSourceTitle") + " <" + this.pubItem.getMetadata().getSources().get(i).getTitle().getLanguage() + ">"));
                    }
                    else
                    {
                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblSourceTitle")));
                    }

                    this.getChildren().add(htmlElement.getEndTag("div"));

                    // value
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));

                    if(this.pubItem.getMetadata().getSources().get(i).getTitle() != null)
                    {
                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getMetadata().getSources().get(i).getTitle().getValue()));
                    }
                    else
                    {
                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
                    }
                    
                    this.getChildren().add(htmlElement.getEndTag("div"));
                    
                    
                    // *** SOURCE ALTERNATIVE TITLES ***
                    if(this.pubItem.getMetadata().getSources().get(i).getAlternativeTitles() != null)
                    {
                        if(this.pubItem.getMetadata().getSources().get(i).getAlternativeTitles().size() > 0)
                        {
                            for(int j = 0; j < this.pubItem.getMetadata().getSources().get(i).getAlternativeTitles().size(); j++)
                            {
                                // label
                                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
                                
                                if(this.pubItem.getMetadata().getSources().get(i).getAlternativeTitles().get(j) != null 
                                        && this.pubItem.getMetadata().getSources().get(i).getAlternativeTitles().get(j).getLanguage() != null 
                                        && !this.pubItem.getMetadata().getSources().get(i).getAlternativeTitles().get(j).getLanguage().trim().equals(""))
                                {
                                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblSourceAlternativeTitle") + " <" + this.pubItem.getMetadata().getSources().get(i).getAlternativeTitles().get(j).getLanguage() + ">"));
                                }
                                else
                                {
                                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblSourceAlternativeTitle")));
                                }
                                
                                this.getChildren().add(htmlElement.getEndTag("div"));
                                
                                // value
                                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
                                
                                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getMetadata().getSources().get(i).getAlternativeTitles().get(j).getValue()));
                                
                                this.getChildren().add(htmlElement.getEndTag("div"));
                            }
                        }
                    }
                    
                    
                    // *** SOURCE GENRE ***
                    // label
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
                    
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblSourceGenre")));
                    
                    this.getChildren().add(htmlElement.getEndTag("div"));
                    
                    // value
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
                    
                    if(this.pubItem.getMetadata().getSources().get(i).getGenre() != null)
                    {
                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel(applicationBean.convertEnumToString(this.pubItem.getMetadata().getSources().get(i).getGenre()))));
                    }
                    else
                    {
                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
                    }
                    
                    this.getChildren().add(htmlElement.getEndTag("div"));
                    
                    
                    // *** CREATORS (PERSONS AND ORGANIZATIONS) ***
                    // label
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
                    
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblSourceCreators")));
                    
                    this.getChildren().add(htmlElement.getEndTag("div"));
                    
                    // value (inserted by the called method)
                    addCreatorsToPage();
                    
                    
                    // the affiliated organizations of the creators above 
                    // label
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
                    
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblSourceAffiliations")));
                    
                    this.getChildren().add(htmlElement.getEndTag("div"));
                    
                    // value (inserted by the called method)
                    addAffiliationsToPage();
                    
                    
                    // *** VOLUME / ISSUE *** 
                    // label
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
                    
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblSourceVolumeIssue")));
                    
                    this.getChildren().add(htmlElement.getEndTag("div"));
                    
                    // value
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
                    
                    if(this.pubItem.getMetadata().getSources().get(i).getVolume() != null && this.pubItem.getMetadata().getSources().get(i).getIssue() != null
                    		&& !this.pubItem.getMetadata().getSources().get(i).getVolume().trim().equals("") && !this.pubItem.getMetadata().getSources().get(i).getIssue().trim().equals(""))
                    {
                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getMetadata().getSources().get(i).getVolume() + " (" + this.pubItem.getMetadata().getSources().get(i).getIssue() + ")"));
                    }
                    // TODO: FrM: I sometimes get a NPE here. I think it is because of lacking parentheses, but I am not into the semantics. Have to clarify with ScT.
                    else if(this.pubItem.getMetadata().getSources().get(i).getVolume() != null && this.pubItem.getMetadata().getSources().get(i).getIssue() == null
                    		|| !this.pubItem.getMetadata().getSources().get(i).getVolume().trim().equals("") && this.pubItem.getMetadata().getSources().get(i).getIssue().trim().equals(""))
                    {
                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getMetadata().getSources().get(i).getVolume()));
                    }
                    else if(this.pubItem.getMetadata().getSources().get(i).getVolume() == null && this.pubItem.getMetadata().getSources().get(i).getIssue() != null
                    		|| this.pubItem.getMetadata().getSources().get(i).getVolume().trim().equals("") && !this.pubItem.getMetadata().getSources().get(i).getIssue().trim().equals(""))
                    {
                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(" " + " (" + this.pubItem.getMetadata().getSources().get(i).getIssue() + ")"));
                    }
                    else
                    {
                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
                    }
                    
                    this.getChildren().add(htmlElement.getEndTag("div"));
                    
                    
                    // *** SEQUENCE NUMBER *** 
                    // label
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
                    
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblSourceSequenceNo")));
                    
                    this.getChildren().add(htmlElement.getEndTag("div"));
                    
                    // value
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
                    
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getMetadata().getSources().get(i).getSequenceNumber()));
                    
                    this.getChildren().add(htmlElement.getEndTag("div"));
                    
                    
                    // *** IDENTIFIERS *** 
                    // label
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
                    
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblSourceIdentifier")));
                    
                    this.getChildren().add(htmlElement.getEndTag("div"));
                    
                    // value
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
                    
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getIdentifiers(i)));
                    
                    this.getChildren().add(htmlElement.getEndTag("div"));
                    
                    
                    // *** EDITION *** 
                    // label
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
                    
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblSourceEdition")));
                    
                    this.getChildren().add(htmlElement.getEndTag("div"));
                    
                    // value
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
                    
                    if(this.pubItem.getMetadata().getSources().get(i).getPublishingInfo() != null)
                    {
                        if(this.pubItem.getMetadata().getSources().get(i).getPublishingInfo().getEdition() != null)
                        {
                            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getMetadata().getSources().get(i).getPublishingInfo().getEdition()));
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
                    
                    
                    // *** PUBLISHING INFO *** 
                    // label
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
                    
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblSourcePubInfo")));
                    
                    this.getChildren().add(htmlElement.getEndTag("div"));
                    
                    // value
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
                    
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getPublishingInfo(i)));
                    
                    this.getChildren().add(htmlElement.getEndTag("div"));
                    
                    // *** START / END PAGE *** 
                    // label
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
                    
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblSourceStartEndPage")));
                    
                    this.getChildren().add(htmlElement.getEndTag("div"));
                    
                    // value
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
                    
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getStartEndPage(i)));
                    
                    this.getChildren().add(htmlElement.getEndTag("div"));
                    
                    
                    //  add some empty rows
                    this.getChildren().add(htmlElement.getStartTag("br"));
                    this.getChildren().add(htmlElement.getStartTag("br"));
                    this.getChildren().add(htmlElement.getStartTag("br"));
                    this.getChildren().add(htmlElement.getStartTag("br"));
                }
            }
            else
            {
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(getLabel("ViewItemFull_lblNoEntries")));
                this.getChildren().add(htmlElement.getEndTag("div"));
            }
        }
    }
    
    /**
     * Generates the affiliated organization list as one string for presenting it in the jsp via the dynamic html component.
     * Doubled organizations will be detected and merged. All organizzations will be numbered. 
     */
    private void createAffiliatedOrganizationList(int sourceNumber)
    {
        String formattedOrganization = "";
        List<CreatorVO> tempCreatorList;
        List<OrganizationVO> tempOrganizationList = null;
        List<OrganizationVO> sortOrganizationList = null;
        this.organizationArray = new ArrayList<String>();
        this.organizationList = new ArrayList<ViewItemOrganization>();
        tempOrganizationList = new ArrayList<OrganizationVO>();
        sortOrganizationList = new ArrayList<OrganizationVO>();
        int affiliationPosition = 0;
        if(this.pubItem.getMetadata().getSources() != null)
        {
            tempCreatorList = this.pubItem.getMetadata().getSources().get(sourceNumber).getCreators();
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
                formattedOrganization = "<p>"+(k + 1) + ": " + sortOrganizationList.get(k).getName()+"</p>" + "<p>" + sortOrganizationList.get(k).getAddress() + "</p>" + "<p>" + sortOrganizationList.get(k).getIdentifier() + "</p>";
                this.organizationArray.add(formattedOrganization);
            }
        }
        
    }
    
    /**
     * Generates the creator list as list of formatted Strings.
     * 
     * @return String formatted creator list as string
     */
    private void createCreatorList(int sourceNumber)
    {
        StringBuffer creatorList = new StringBuffer();
        String formattedCreator = "";
        this.creatorArray = new ArrayList<String>();
        this.creatorOrganizationsArray = new ArrayList<ViewItemCreatorOrganization>();
        // counter for organization array
        int counterOrganization = 0;
        StringBuffer annotation;
        ObjectFormatter formatter = new ObjectFormatter();
        
        if(this.pubItem.getMetadata().getSources() != null)
        {
            for (int i = 0; i < this.pubItem.getMetadata().getSources().get(sourceNumber).getCreators().size(); i++)
            {
                CreatorVO creator = new CreatorVO();
                creator = this.pubItem.getMetadata().getSources().get(sourceNumber).getCreators().get(i);
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
        }
        // if there are no affiliations at all, an empty html element has to be inserted
        if(this.organizationList.size() < 1)
        {
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
            
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
            
            this.getChildren().add(htmlElement.getEndTag("div"));
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
                        this.divClassTitle = "itemTitle";
                        this.divClassText = "itemText";
                    }
                    else
                    {
                        this.divClassTitle = "itemTitle odd";
                        this.divClassText = "itemText odd";
                    }
                    if(i > 0)
                    {
                        this.getChildren().add(this.htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
                        
                        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
                        
                        this.getChildren().add(this.htmlElement.getEndTag("div"));
                    }
                    this.getChildren().add(this.htmlElement.getStartTagWithStyleClass("div", this.divClassText));
                    
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
                    if(this.divClassTitle.equals("itemTitle odd"))
                    {
                        this.divClassTitle = "itemTitle";
                        this.divClassText = "itemText";
                    }
                    else
                    {
                        this.divClassTitle = "itemTitle odd";
                        this.divClassText = "itemText odd";
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
        // if there are no creators at all, an empty html element has to be inserted
        if(this.creatorArray.size() < 1 && this.creatorOrganizationsArray.size() < 1)
        {
            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
            
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
            
            this.getChildren().add(htmlElement.getEndTag("div"));
        }
    }
    
    /**
     * Returns all Identifiers as formatted String
     * @return String the formatted Identifiers
     */
    private String getIdentifiers(int sourceNumber)
    {
        StringBuffer identifiers = new StringBuffer();
        if(this.pubItem.getMetadata().getSources().get(sourceNumber).getIdentifiers() != null)
        {
            for(int i = 0; i < this.pubItem.getMetadata().getSources().get(sourceNumber).getIdentifiers().size(); i++)
            {
                identifiers.append(this.pubItem.getMetadata().getSources().get(sourceNumber).getIdentifiers().get(i).getTypeString());
                identifiers.append(": ");
                identifiers.append(this.pubItem.getMetadata().getSources().get(sourceNumber).getIdentifiers().get(i).getId());
                if(i < this.pubItem.getMetadata().getSources().get(sourceNumber).getIdentifiers().size() - 1)
                {
                    identifiers.append(", ");
                }
            }
        }
        return identifiers.toString();
    }
    
    /**
     * Returns the formatted Publishing Info according to filled elements
     * @return String the formatted Publishing Info
     */
    private String getPublishingInfo(int sourceNumber)
    {
        StringBuffer publishingInfo = new StringBuffer();
        if(this.pubItem.getMetadata().getSources().get(sourceNumber).getPublishingInfo() != null)
        {
            if(this.pubItem.getMetadata().getSources().get(sourceNumber).getPublishingInfo().getEdition() != null)
            {
                publishingInfo.append(this.pubItem.getMetadata().getSources().get(sourceNumber).getPublishingInfo().getEdition());
            }
            if(this.pubItem.getMetadata().getSources().get(sourceNumber).getPublishingInfo().getEdition() != null && this.pubItem.getMetadata().getSources().get(sourceNumber).getPublishingInfo().getPlace() != null)
            {
                publishingInfo.append(", ");
                publishingInfo.append(this.pubItem.getMetadata().getSources().get(sourceNumber).getPublishingInfo().getPlace());
            }
            if(this.pubItem.getMetadata().getSources().get(sourceNumber).getPublishingInfo().getPublisher() != null)
            {
                publishingInfo.append(": ");
                publishingInfo.append(this.pubItem.getMetadata().getSources().get(sourceNumber).getPublishingInfo().getPublisher());
            }
        }
        return publishingInfo.toString();
    }
    
    /**
     * Returns a formatted String containing the start and the end page of the source
     * @return String the formatted start and end page
     */
    private String getStartEndPage(int sourceNumber)
    {
        StringBuffer startEndPage = new StringBuffer();
        
        if(this.pubItem.getMetadata().getSources().get(sourceNumber).getStartPage() != null)
        {
            startEndPage.append(this.pubItem.getMetadata().getSources().get(sourceNumber).getStartPage());
        }
        
        if(this.pubItem.getMetadata().getSources().get(sourceNumber).getEndPage() != null)
        {
            startEndPage.append(" - ");
            startEndPage.append(this.pubItem.getMetadata().getSources().get(sourceNumber).getEndPage());
        }
        return startEndPage.toString();
    }
}