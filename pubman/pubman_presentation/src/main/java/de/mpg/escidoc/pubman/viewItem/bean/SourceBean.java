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
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
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
import de.mpg.escidoc.pubman.util.CreatorDisplay;
import de.mpg.escidoc.pubman.util.ObjectFormatter;
import de.mpg.escidoc.pubman.viewItem.ViewItemCreatorOrganization;
import de.mpg.escidoc.pubman.viewItem.ViewItemCreators;
import de.mpg.escidoc.pubman.viewItem.ViewItemFull;
import de.mpg.escidoc.pubman.viewItem.ViewItemOrganization;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO.IdType;
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
	private ArrayList<CreatorDisplay> sourceCreatorArray;

	/**
	 * The list of formatted creators which are organizations in an ArrayList.
	 */
	private ArrayList<ViewItemCreatorOrganization> sourceCreatorOrganizationsArray;

	/**
	 * The list of source creators as VO List.
	 */
	private ArrayList<ViewItemCreators> creators;

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

		if (source.getCreators().size()>0)
		{
			createCreatorsList();
		}

		this.startEndPage = getStartEndPage(source);
		this.publishingInfo = getPublishingInfo(source);
		if (source.getIdentifiers().size() >0 ) {
			this.identifiers = ViewItemFull.getIdentifierHtmlString(source.getIdentifiers());
		}

	}

	private void createCreatorsList()
	{

		List<CreatorVO> tempCreatorList;
		List<OrganizationVO> tempOrganizationList = null;
		List<OrganizationVO> sortOrganizationList = null;
		sortOrganizationList = new ArrayList<OrganizationVO>();

		String formattedCreator = "";
		String formattedOrganization = "";

		this.setSourceOrganizationList(new ArrayList<ViewItemOrganization>());
		this.setSourceCreatorOrganizationsArray(new ArrayList<ViewItemCreatorOrganization>());
		this.setSourceOrganizationArray(new ArrayList<String>());

		// counter for organization array
		int counterOrganization = 0;
		ObjectFormatter formatter = new ObjectFormatter();

		//temporary list of All creators, retrieved directly from the metadata
		tempCreatorList = this.getSource().getCreators();
		//the list of creators is initialized to a new array list
		this.setSourceCreatorArray(new ArrayList<CreatorDisplay>());
		int affiliationPosition = 0;

		//for each creator in the list
		for (int i = 0; i < tempCreatorList.size(); i++)
		{

			//temporary organization list is matched against the sorted for each separate creator
			//therefore for each creator is newly re-set
			tempOrganizationList = new ArrayList<OrganizationVO>();

			CreatorVO creator1 = new CreatorVO();
			creator1 = tempCreatorList.get(i);

			CreatorDisplay creatorDisplay = new CreatorDisplay();

			//if the creator is a person add his organization to the sorted organization list
			if (creator1.getPerson() != null)
			{
				//if there is affiliated organization for this creator
				if (creator1.getPerson().getOrganizations().size() > 0)
				{
					//add each affiliated organization of the creator to the temporary organization list
					for (int listSize = 0; listSize < creator1.getPerson().getOrganizations().size(); listSize++)
					{
						tempOrganizationList.add(creator1.getPerson().getOrganizations().get(listSize));
					}

					//for each organizations in the temporary organization list
					for (int j = 0; j < tempOrganizationList.size(); j++)
					{
						// check if the organization in the list is in the sorted organization list
						if (!sortOrganizationList.contains(tempOrganizationList.get(j)))
						{
							affiliationPosition++;
							//if the temporary organization is to be added to the sorted set of organizations
							sortOrganizationList.add(tempOrganizationList.get(j));
							//create new Organization view object
							this.getSourceOrganizationList().add(ViewItemFull.formatCreatorOrganization(tempOrganizationList.get(j), affiliationPosition));
						}
					}
				}

				formattedCreator=formatter.formatCreator(creator1,ViewItemFull.formatCreatorOrganizationIndex(creator1,sortOrganizationList));
				creatorDisplay.setFormattedDisplay(formattedCreator);

				if (creator1.getPerson().getIdentifier() != null
						&& (creator1.getPerson().getIdentifier().getType() == IdType.CONE))
				{
					try
					{
						creatorDisplay.setPortfolioLink(creator1.getPerson().getIdentifier().getId());
					}
					catch (Exception e)
					{
						throw new RuntimeException(e);
					}
				}

				this.sourceCreatorArray.add(creatorDisplay);
			} //end if creator is a person

			if (creator1.getOrganization() != null)
			{
				formattedCreator=formatter.formatCreator(creator1,"");
				creatorDisplay.setFormattedDisplay(formattedCreator);
				ViewItemCreatorOrganization creatorOrganization = new ViewItemCreatorOrganization();
				creatorOrganization.setOrganizationName(formattedCreator);
				creatorOrganization.setPosition(new Integer(counterOrganization).toString());
				creatorOrganization.setOrganizationAddress(creator1.getOrganization().getAddress());
				creatorOrganization.setOrganizationInfoPage(formattedCreator, creator1.getOrganization().getAddress());
				creatorOrganization.setIdentifier(creator1.getOrganization().getIdentifier());
				this.sourceCreatorOrganizationsArray.add(creatorOrganization);
			}

			counterOrganization++;
			this.setSourceAffiliatedOrganizationsList(sortOrganizationList);
			// generate a 'well-formed' list for presentation in the jsp
			for (int k = 0; k < sortOrganizationList.size(); k++)
			{
				String name = sortOrganizationList.get(k).getName() != null ? sortOrganizationList.get(k).getName()
						.getValue() : "";
						formattedOrganization = "<p>" + (k + 1) + ": " + name + "</p>" + "<p>"
						+ sortOrganizationList.get(k).getAddress() + "</p>" + "<p>"
						+ sortOrganizationList.get(k).getIdentifier() + "</p>";
						this.sourceOrganizationArray.add(formattedOrganization);
			}
		} //end for each creator in the list
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

	public ArrayList<CreatorDisplay> getSourceCreatorArray()
	{
		return sourceCreatorArray;
	}

	public void setSourceCreatorArray(ArrayList<CreatorDisplay> sourceCreatorArray)
	{
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

	public ArrayList<ViewItemCreators> getCreators() {
		return creators;
	}

	public void setCreators(ArrayList<ViewItemCreators> creators) {
		this.creators = creators;
	}
}