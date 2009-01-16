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

package de.mpg.escidoc.pubman.editItem;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.editItem.bean.CreatorBean;
import de.mpg.escidoc.pubman.editItem.bean.CreatorBean.PersonOrganisationManager;
import de.mpg.escidoc.pubman.util.PubFileVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsFileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PersonVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;

/**
 * Keeps all attributes that are used for the whole session by the EditItem.
 * 
 * @author: Tobias Schraut, created 26.02.2007
 * @version: $Revision$ $LastChangedDate: 2007-11-13 10:54:07 +0100 (Di, 13
 *           Nov 2007) $
 */
public class EditItemSessionBean extends FacesBean 
{
	public static final String BEAN_NAME = "EditItemSessionBean";
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(EditItemSessionBean.class);

	private List<PubFileVOPresentation> files = new ArrayList<PubFileVOPresentation>();
	
	private List<PubFileVOPresentation> locators = new ArrayList<PubFileVOPresentation>();
	
	private String genreBundle = "Genre_ARTICLE";
	
	 /**The offset of the page where to jump back*/
    private String offset;
    
    /**The string with authors to parse for author copy&paste.*/
    private String creatorParseString;
    
    /**Checkbox if existing authors should be overwritten with the ones from author copy/paste*/
    private boolean overwriteCreators;
    
    /**
     * A creator bean that holds the data from the author copy&paste organizations
     */
    private CreatorBean authorCopyPasteOrganizationsCreatorBean;
    


	/**
	 * Public constructor.
	 */
	public EditItemSessionBean() 
	{
		this.init();
	}

	/**
	 * This method is called when this bean is initially added to session scope.
	 * Typically, this occurs as a result of evaluating a value binding or
	 * method binding expression, which utilizes the managed bean facility to
	 * instantiate this bean and store it into session scope.
	 */
	public void init() 
	{
		// Perform initializations inherited from our superclass
		super.init();
		initAuthorCopyPasteCreatorBean();
	}
	
	/**
	 * This method clears the file and the locator list
	 */
	public void clean()
	{
		this.files.clear();
		this.locators.clear();
		this.genreBundle = null;
		this.offset="";
		
		// make sure that at least one locator and one file is stored in the  EditItemSessionBean
    	if(this.getFiles().size() < 1)
    	{
    	    FileVO newFile = new FileVO();
    	    newFile.getMetadataSets().add(new MdsFileVO());
    	    newFile.setStorage(FileVO.Storage.INTERNAL_MANAGED);
    		this.getFiles().add(new PubFileVOPresentation(this.getFiles().size(), newFile, false));
    	}
    	if(this.getLocators().size() < 1)
    	{
    		FileVO newLocator = new FileVO();
    		newLocator.getMetadataSets().add(new MdsFileVO());
    		newLocator.setStorage(FileVO.Storage.EXTERNAL_URL);
    		this.getLocators().add(new PubFileVOPresentation(0, newLocator, true));
    	}
    	
    	initAuthorCopyPasteCreatorBean();
	}
	
	/**
     * This method reorganizes the index property in PubFileVOPresentation after removing one element of the list.
     */
    public void reorganizeFileIndexes()
    {
    	if(this.files != null)
    	{
    		for(int i = 0; i < this.files.size(); i++)
        	{
        		this.files.get(i).setIndex(i);
        	}
    	}
    }
    
    /**
     * This method reorganizes the index property in PubFileVOPresentation after removing one element of the list.
     */
    public void reorganizeLocatorIndexes()
    {
    	if(this.locators != null)
    	{
    		for(int i = 0; i < this.locators.size(); i++)
        	{
        		this.locators.get(i).setIndex(i);
        	}
    	}
    }

	public List<PubFileVOPresentation> getFiles() 
	{
		return files;
	}

	public void setFiles(List<PubFileVOPresentation> files) 
	{
		this.files = files;
	}

	public List<PubFileVOPresentation> getLocators() {
		return locators;
	}

	public void setLocators(List<PubFileVOPresentation> locators) {
		this.locators = locators;
	}

	public String getGenreBundle() {
		return genreBundle;
	}

	public void setGenreBundle(String genreBundle) {
		this.genreBundle = genreBundle;
	}

    public void setOffset(String offset)
    {
        this.offset = offset;
    }

    public String getOffset()
    {
        return offset;
    }
    
    /**
     * (Re)-initializes the PersonOPrganisationManager that manages the author copy&paste organizations.
     */
    public void initAuthorCopyPasteCreatorBean()
    {
        CreatorVO newVO = new CreatorVO();
        newVO.setPerson(new PersonVO());
        OrganizationVO newPersonOrganization = new OrganizationVO();
        newPersonOrganization.setName(new TextVO());
        newPersonOrganization.setAddress("");
        newPersonOrganization.setIdentifier("");
        newVO.getPerson().getOrganizations().add(newPersonOrganization);
        CreatorBean dummyCreatorBean = new CreatorBean(newVO);
        authorCopyPasteOrganizationsCreatorBean = dummyCreatorBean;
        setCreatorParseString("");
    }


    /**
     * Sets the CreatorBean that manages the author copy&paste organizations.
     * @param authorCopyPasteOrganizationsCreatorBean
     */
    public void setAuthorCopyPasteOrganizationsCreatorBean(CreatorBean authorCopyPasteOrganizationsCreatorBean)
    {
        this.authorCopyPasteOrganizationsCreatorBean = authorCopyPasteOrganizationsCreatorBean;
    }

    /**
     * Returns the PersonOPrganisationManager that manages the author copy&paste organizations.
     * @return
     */
    public CreatorBean getAuthorCopyPasteOrganizationsCreatorBean()
    {
        return authorCopyPasteOrganizationsCreatorBean;
    }

    public void setCreatorParseString(String creatorParseString)
    {
        this.creatorParseString = creatorParseString;
    }

    public String getCreatorParseString()
    {
        return creatorParseString;
    }

    public void setOverwriteCreators(boolean overwriteCreators)
    {
        this.overwriteCreators = overwriteCreators;
    }

    public boolean getOverwriteCreators()
    {
        return overwriteCreators;
    }

	
	
}
