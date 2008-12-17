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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.viewItem.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.appbase.InternationalizedImpl;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.ObjectFormatter;
import de.mpg.escidoc.pubman.viewItem.ViewItemCreatorOrganization;
import de.mpg.escidoc.pubman.viewItem.ViewItemOrganization;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;

/**
 * Bean for creating the source section of a pubitem to be used in the ViewItemFullUI.
 * 
 * @author: Tobias Schraut, created 25.03.2008
 * @version: $Revision$ $LastChangedDate$
 */
public class SourceBean extends FacesBean
{
    private SourceVO source;
    /**
     * The list of formatted organzations in an ArrayList.
     */
    private ArrayList<String> sourceOrganizationArray;

    /**
     * The list of affiliated organizations as VO List.
     */
    private ArrayList<ViewItemOrganization> sourceOrganizationList;

    /**
     * The list of affiliated organizations in a list.
     */
    private List<OrganizationVO> sourceAffiliatedOrganizationsList;

    /**
     * The list of formatted creators in an ArrayList.
     */
    private ArrayList<String> sourceCreatorArray;

    /**
     * The list of formatted creators which are organizations in an ArrayList.
     */
    private ArrayList<ViewItemCreatorOrganization> sourceCreatorOrganizationsArray;
    
    private String identifiers;
    
    private String startEndPage;
    
    private String publishingInfo;

    
    public SourceBean(SourceVO source)
	{
		this.source = source;
		initialize(source);
	}

    /**
     * Initializes the UI and sets all attributes of the GUI components.
     *
     * @param pubItemVO a pubitem
     */
    protected void initialize(SourceVO source)
    {
        // Prerequisites
        // the list of numbered affiliated organizations
        createAffiliatedOrganizationList(source);

        // the list of creators (persons and organizations)
        createCreatorList(source);
        
        this.identifiers = getIdentifiers(source);
        
        this.startEndPage = getStartEndPage(source);
        
        this.publishingInfo = getPublishingInfo(source);
    }
    
    /**
     * Generates the affiliated organization list as one string for presenting it in the jsp via the dynamic html component.
     * Doubled organizations will be detected and merged. All organizzations will be numbered. 
     */
    private void createAffiliatedOrganizationList(SourceVO source)
    {
        String formattedOrganization = "";
        List<CreatorVO> tempCreatorList;
        List<OrganizationVO> tempOrganizationList = null;
        List<OrganizationVO> sortOrganizationList = null;
        this.sourceOrganizationArray = new ArrayList<String>();
        this.sourceOrganizationList = new ArrayList<ViewItemOrganization>();
        tempOrganizationList = new ArrayList<OrganizationVO>();
        sortOrganizationList = new ArrayList<OrganizationVO>();
        int affiliationPosition = 0;
        if(source != null)
        {
            tempCreatorList = source.getCreators();
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
                                this.sourceOrganizationList.add(viewOrganization);
                            }
                        }
                    }
                }
            }
            // save the List in the backing bean for later use.
            this.sourceAffiliatedOrganizationsList = sortOrganizationList;
        }
        
    }
    
    /**
     * Generates the creator list as list of formatted Strings.
     * 
     * @return String formatted creator list as string
     */
    private void createCreatorList(SourceVO source)
    {
        StringBuffer creatorList = new StringBuffer();
        String formattedCreator = "";
        this.sourceCreatorArray = new ArrayList<String>();
        this.sourceCreatorOrganizationsArray = new ArrayList<ViewItemCreatorOrganization>();
        // counter for organization array
        int counterOrganization = 0;
        StringBuffer annotation;
        ObjectFormatter formatter = new ObjectFormatter();
        
        if(source != null)
        {
            for (int i = 0; i < source.getCreators().size(); i++)
            {
                CreatorVO creator = new CreatorVO();
                creator = source.getCreators().get(i);
                annotation = new StringBuffer();
                int organizationsFound = 0;
                for (int j = 0; j < this.sourceAffiliatedOrganizationsList.size(); j++)
                {
                    if (creator.getPerson() != null)
                    {
                        if (creator.getPerson().getOrganizations().contains(this.sourceAffiliatedOrganizationsList.get(j)))
                        {
                            if (organizationsFound == 0)
                            {
                                annotation.append("<sup>");
                            }
                            if (organizationsFound > 0 && j < this.sourceAffiliatedOrganizationsList.size())
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
                    annotation.append("</sup>");
                }
                formattedCreator = formatter.formatCreator(creator, annotation.toString());
                if (creator.getPerson() != null)
                {
                    this.sourceCreatorArray.add(formattedCreator);
                }
                if (creator.getOrganization() != null)
                {
                    ViewItemCreatorOrganization creatorOrganization = new ViewItemCreatorOrganization();
                    creatorOrganization.setOrganizationName(formattedCreator);
                    creatorOrganization.setPosition(new Integer(counterOrganization).toString());
                    creatorOrganization.setOrganizationAddress(creator.getOrganization().getAddress());
                    creatorOrganization.setOrganizationInfoPage(formattedCreator, creator.getOrganization()
                            .getAddress());
                    this.sourceCreatorOrganizationsArray.add(creatorOrganization);
                    counterOrganization++;
                }
                creatorList.append(formattedCreator);
            }
        }
    }
    
    
    
    /**
     * Returns all Identifiers as formatted String
     * @return String the formatted Identifiers
     */
    private String getIdentifiers(SourceVO source)
    {
        StringBuffer identifiers = new StringBuffer();
        if(source.getIdentifiers() != null)
        {
            for(int i = 0; i < source.getIdentifiers().size(); i++)
            {
                identifiers.append(source.getIdentifiers().get(i).getTypeString());
                identifiers.append(": ");
                if (CommonUtils.getisUriValidUrl(source.getIdentifiers().get(i)))
                {
                    identifiers.append("<a href='"+source.getIdentifiers().get(i).getId()+"'>"+source.getIdentifiers().get(i).getId()+"</a>"); 

                }
                else
                {
                    identifiers.append(source.getIdentifiers().get(i).getId());
                }
                if(i < source.getIdentifiers().size() - 1)
                {
                    identifiers.append("<br/>");
                }
            }
        }
        return identifiers.toString();
    }

    
    /**
     * Returns the formatted Publishing Info according to filled elements
     * @return String the formatted Publishing Info
     */
    private String getPublishingInfo(SourceVO source)
    { 
        
        
        StringBuffer publishingInfo = new StringBuffer();
        publishingInfo.append("");
        if(source.getPublishingInfo() != null)
        {
           
            // Place
            if(source.getPublishingInfo().getPlace() != null && !source.getPublishingInfo().getPlace().equals(""))
            {
                publishingInfo.append(source.getPublishingInfo().getPlace().trim());
            }
            
            // colon
            if(source.getPublishingInfo().getPublisher() != null && !source.getPublishingInfo().getPublisher().trim().equals("") && source.getPublishingInfo().getPlace() != null && !source.getPublishingInfo().getPlace().trim().equals(""))
            {
                    publishingInfo.append(" : ");
            }
            
            // Publisher
            if(source.getPublishingInfo().getPublisher() != null && !source.getPublishingInfo().getPublisher().equals(""))
            {
                publishingInfo.append(source.getPublishingInfo().getPublisher().trim());
            }
            
            // Comma
            if((source.getPublishingInfo().getEdition() != null && !source.getPublishingInfo().getEdition().trim().equals("")) && ((source.getPublishingInfo().getPlace() != null && !source.getPublishingInfo().getPlace().trim().equals("")) || (source.getPublishingInfo().getPublisher() != null && !source.getPublishingInfo().getPublisher().trim().equals(""))))
            {
                    publishingInfo.append(", ");
            }
            
            // Edition
            if(source.getPublishingInfo().getEdition() != null)
            {
                publishingInfo.append(source.getPublishingInfo().getEdition());
            }
            
        }
        return publishingInfo.toString();
    }
    
    /**
     * Returns a formatted String containing the start and the end page of the source
     * @return String the formatted start and end page
     */
    private String getStartEndPage(SourceVO source)
    {
        StringBuffer startEndPage = new StringBuffer();
        
        if(source.getStartPage() != null)
        {
            startEndPage.append(source.getStartPage());
        }
        
        if(source.getEndPage() != null)
        {
            startEndPage.append(" - ");
            startEndPage.append(source.getEndPage());
        }
        
        if (startEndPage.toString().equals(" - "))
        {
            return "";
        }
        else return startEndPage.toString();
    }
    
    /**
     * Returns the ApplicationBean.
     * 
     * @return a reference to the scoped data bean (ApplicationBean)
     */
    protected ApplicationBean getApplicationBean()
    {
        return (ApplicationBean) FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), ApplicationBean.BEAN_NAME);
        
    }
    
    public String getGenre()
    {
    	InternationalizedImpl internationalized = new InternationalizedImpl();
    	return internationalized.getLabel(this.i18nHelper.convertEnumToString(this.source.getGenre()));
    }

	public String getIdentifiers() {
		return this.identifiers;
	}

	public void setIdentifiers(String identifiers) {
		this.identifiers = identifiers;
	}

	public String getStartEndPage() {
		return this.startEndPage;
	}

	public void setStartEndPage(String startEndPage) {
		this.startEndPage = startEndPage;
	}

	public String getPublishingInfo() {
		return this.publishingInfo;
	}

	public void setPublishingInfo(String publishingInfo) {
		this.publishingInfo = publishingInfo;
	}

	public SourceVO getSource() {
		return this.source;
	}

	public void setSource(SourceVO source) {
		this.source = source;
	}

	public ArrayList<String> getSourceOrganizationArray() {
		return this.sourceOrganizationArray;
	}

	public void setSourceOrganizationArray(ArrayList<String> sourceOrganizationArray) {
		this.sourceOrganizationArray = sourceOrganizationArray;
	}

	public ArrayList<ViewItemOrganization> getSourceOrganizationList() {
		return this.sourceOrganizationList;
	}

	public void setSourceOrganizationList(
			ArrayList<ViewItemOrganization> sourceOrganizationList) {
		this.sourceOrganizationList = sourceOrganizationList;
	}

	public List<OrganizationVO> getSourceAffiliatedOrganizationsList() {
		return this.sourceAffiliatedOrganizationsList;
	}

	public void setSourceAffiliatedOrganizationsList(
			List<OrganizationVO> sourceAffiliatedOrganizationsList) {
		this.sourceAffiliatedOrganizationsList = sourceAffiliatedOrganizationsList;
	}

	public ArrayList<String> getSourceCreatorArray() {
		return this.sourceCreatorArray;
	}

	public void setSourceCreatorArray(ArrayList<String> sourceCreatorArray) {
		this.sourceCreatorArray = sourceCreatorArray;
	}

	public ArrayList<ViewItemCreatorOrganization> getSourceCreatorOrganizationsArray() {
		return this.sourceCreatorOrganizationsArray;
	}

	public void setSourceCreatorOrganizationsArray(
			ArrayList<ViewItemCreatorOrganization> sourceCreatorOrganizationsArray) {
		this.sourceCreatorOrganizationsArray = sourceCreatorOrganizationsArray;
	}
	
	public boolean getHasCreator()
	{
	    if (this.sourceCreatorArray.size() > 0 && this.sourceCreatorOrganizationsArray.size() > 0)
	    {
	        return true;
	    }
	    return false;
	}
    
    public boolean getHasAffiliation()
    {
        if (this.sourceOrganizationArray.size() > 0 )
        {
            return true;
        }
        return false;
    }
}