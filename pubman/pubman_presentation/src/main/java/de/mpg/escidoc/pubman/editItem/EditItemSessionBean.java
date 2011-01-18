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
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */

package de.mpg.escidoc.pubman.editItem;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.EditItemBean;
import de.mpg.escidoc.pubman.editItem.bean.CreatorBean;
import de.mpg.escidoc.pubman.editItem.bean.SourceBean;
import de.mpg.escidoc.pubman.util.PubFileVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsFileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;

/**
 * Keeps all attributes that are used for the whole session by the EditItem.
 * 
 * @author: Tobias Schraut, created 26.02.2007
 * @version: $Revision$ $LastChangedDate: 2007-11-13 10:54:07 +0100 (Di, 13
 *           Nov 2007) $
 */
public class EditItemSessionBean extends EditItemBean 
{
    public static final String BEAN_NAME = "EditItemSessionBean";
    
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(EditItemSessionBean.class);

    private List<PubFileVOPresentation> files = new ArrayList<PubFileVOPresentation>();
    
    private List<PubFileVOPresentation> locators = new ArrayList<PubFileVOPresentation>();
    
    private String genreBundle = "Genre_ARTICLE";
    
     /**The offset of the page where to jump back*/
    private String offset;
    
    /**
     * A creator bean that holds the data from the author copy&paste organizations
     */
    private CreatorBean authorCopyPasteOrganizationsCreatorBean;
    
    private List<SourceBean> sources = new ArrayList<SourceBean>();

    public static final String SUBMISSION_METHOD_FULL_SUBMISSION = "FULL_SUBMISSION";
    public static final String SUBMISSION_METHOD_EASY_SUBMISSION = "EASY_SUBMISSION";
    public static final String SUBMISSION_METHOD_IMPORT = "IMPORT";
    
    /**
     * Flag for the GUI to detect if the edit item page is called for a submission or for an editing process
     */
    private String currentSubmission = "";

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
    public void initEmptyComponents()
    {
        clean();
        
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
     * 
     */
    public void clean()
    {
        super.clean(); 
        
        this.files.clear();
        this.locators.clear();
        this.sources.clear();
        this.genreBundle = "";
        this.offset="";
    }
    
    public void bindSourcesToBean(List<SourceVO> sourceList)
    {
        for (SourceVO sourceVO : sourceList)
        {
            this.sources.add(new SourceBean(sourceVO, this.sources));
        }
    }

    public void bindSourcesToVO(List<SourceVO> sourceList)
    {
        sourceList.clear();
        for (SourceBean sourceBean : getSources())
        {
            SourceVO sourceVO = sourceBean.getSource();

            sourceList.add(sourceVO);
        }
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

    public List<PubFileVOPresentation> getLocators()
    {
        return locators;
    }

    public void setLocators(List<PubFileVOPresentation> locators)
    {
        this.locators = locators;
    }

    public String getGenreBundle() {
        return genreBundle;
    }

    public void setGenreBundle(String genreBundle)
    {
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
    
    public List<SourceBean> getSources()
    {
        return sources;
    }

    public void setSources(List<SourceBean> sources)
    {
        this.sources = sources;
    }

    /**
     * (Re)-initializes the PersonOPrganisationManager that manages the author copy&paste organizations.
     */
    public void initAuthorCopyPasteCreatorBean()
    {
        CreatorVO newVO = new CreatorVO();
       
        // TODO MF.
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

    public String getCurrentSubmission()
    {
        return currentSubmission;
    }

    public void setCurrentSubmission(String currentSubmission)
    {
        this.currentSubmission = currentSubmission;
    }
}
