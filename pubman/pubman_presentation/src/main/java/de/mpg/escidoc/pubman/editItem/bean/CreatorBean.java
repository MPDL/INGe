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

package de.mpg.escidoc.pubman.editItem.bean;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.affiliation.AffiliationBean;
import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.easySubmission.EasySubmissionSessionBean;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PersonVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO.IdType;

/**
 * POJO bean to deal with one creator. This can either be a person or a organisation.
 * Only for creators of type person there is a list of assigned organisations included.
 *
 * @author Mario Wagner
 */
public class CreatorBean extends FacesBean
{
	private static Logger logger = Logger.getLogger(CreatorBean.class);
	
    private CreatorVO creator = null;
    private PersonOrganisationManager personOrganisationManager = null;

    private boolean personType, organisationType;
    private OrganizationVO currentOrgaForSelection = null;

    public CreatorBean()
    {
        // ensure the parentVO is never null;
        this(new CreatorVO());
    }

    public CreatorBean(CreatorVO creator)
    {
        setCreator(creator);
        
    }

    public CreatorVO getCreator()
    {
        return creator;
    }

    public void setCreator(CreatorVO creator)
    {
        if (creator.getType() == null)
        {
            creator.setType(CreatorVO.CreatorType.PERSON);
        }
        if (CreatorVO.CreatorType.PERSON.equals(creator.getType()) && (creator.getPerson() == null || creator.getPerson().getOrganizations().size() == 0))
        {
            if (creator.getPerson() == null)
            {
                creator.setPerson(new PersonVO());
            }
            if (creator.getPerson().getOrganizations().size() == 0)
            {
                // create a new Organization for this person
                OrganizationVO newPersonOrganization = new OrganizationVO();
                newPersonOrganization.setName(new TextVO());                
                creator.getPerson().getOrganizations().add(newPersonOrganization);
            }
        }
        else if (CreatorVO.CreatorType.ORGANIZATION.equals(creator.getType()))
        {
            if (creator.getOrganization() == null)
            {
                OrganizationVO newOrga = new OrganizationVO();
                newOrga.setName(new TextVO());
                
                creator.setOrganization(newOrga);
            }
            if (creator.getOrganization() != null && creator.getOrganization().getName() == null)
            {
                creator.getOrganization().setName(new TextVO());
            }
        }
        this.creator = creator;
        personType = CreatorVO.CreatorType.PERSON.equals(creator.getType());
        organisationType = CreatorVO.CreatorType.ORGANIZATION.equals(creator.getType());
        currentOrgaForSelection = null;
        // ensure proper initialization of our DataModelManager
        if (personType)
        {
            personOrganisationManager = new PersonOrganisationManager(creator.getPerson());
        }
        else
        {
            personOrganisationManager = null;
        }
    }

    /**
     * Action navigation call to select one persons organisation
     * @return
     */
    public String selectPersonOrganisation()
    {
        currentOrgaForSelection = (OrganizationVO) personOrganisationManager.getObjectDM().getRowData();

        // Set this value to let the affiliation tree know where to jump after selection.
        ((AffiliationBean)getSessionBean(AffiliationBean.class)).setSource("EditItem");
        ((AffiliationBean)getSessionBean(AffiliationBean.class)).setCache(currentOrgaForSelection);
        
        // affiliation tree
        return "loadAffiliationTree";
    }

    /**
     * @return true if this element is selecting the organisation.
     */
    public boolean getSelecting()
    {
        currentOrgaForSelection = (OrganizationVO) personOrganisationManager.getObjectDM().getRowData();

        // Set this value to let the affiliation tree know where to jump after selection.
        OrganizationVO selecting = ((EasySubmissionSessionBean)getSessionBean(EasySubmissionSessionBean.class)).getCurrentlySelecting();

        return (currentOrgaForSelection.equals(selecting));
    }
    
    /**
     * Action navigation call to select one persons organisation for easy submission
     * @return
     */
    public String selectPersonOrganisationEasySubmission()
    {
        currentOrgaForSelection = (OrganizationVO) personOrganisationManager.getObjectDM().getRowData();

        // Set this value to let the affiliation tree know where to jump after selection.
        ((AffiliationBean)getSessionBean(AffiliationBean.class)).setSource("EasySubmission");
        ((AffiliationBean)getSessionBean(AffiliationBean.class)).setCache(currentOrgaForSelection);
        ((EasySubmissionSessionBean)getSessionBean(EasySubmissionSessionBean.class)).setCurrentlySelecting(currentOrgaForSelection);
        
        // affiliation tree
        return "loadAffiliationTree";
    }

    /**
     * Action navigation call to select the creator organisation
     * @return
     */
    public String selectOrganisation()
    {
        if (creator.getOrganization() == null)
        {
            creator.setOrganization(new OrganizationVO());
        }
        currentOrgaForSelection = creator.getOrganization();

        // Set this value to let the affiliation tree know where to jump after selection.
        ((AffiliationBean)getSessionBean(AffiliationBean.class)).setSource("EditItem");
        ((AffiliationBean)getSessionBean(AffiliationBean.class)).setCache(currentOrgaForSelection);
        
        return "loadAffiliationTree";
    }

    /**
     * Action navigation call to select the creator organisation for Easy Submission
     * @return
     */
    public String selectOrganisationEasySubmission()
    {
        if (creator.getOrganization() == null)
        {
            creator.setOrganization(new OrganizationVO());
        }
        currentOrgaForSelection = creator.getOrganization();

        // Set this value to let the affiliation tree know where to jump after selection.
        ((AffiliationBean)getSessionBean(AffiliationBean.class)).setSource("EasySubmission");
        ((AffiliationBean)getSessionBean(AffiliationBean.class)).setCache(currentOrgaForSelection);
        
        return "loadAffiliationTree";
    }
    
    /**
     * ValueChangeListener method to handle changes in the creatorType. 
     * @param event
     * @throws AbortProcessingException
     */
    public void processCreatorTypeChanged(ValueChangeEvent event) throws AbortProcessingException
    {
        // Reinitialize this POJO, because the creator.getType() has been changed.
        String newVal = (String) event.getNewValue();
        creator.setTypeString(newVal);
        setCreator(creator);
        // enforce rendering of the response
//        FacesContext context = FacesContext.getCurrentInstance();
//        context.renderResponse();
    }

    /**
     * Specialized DataModelManager to deal with objects of type OrganizationVO
     * @author Mario Wagner
     */
    public class PersonOrganisationManager extends DataModelManager<OrganizationVO>
    {
        PersonVO parentVO;

        public PersonOrganisationManager(PersonVO parentVO)
        {
            setParentVO(parentVO);
        }

        public OrganizationVO createNewObject()
        {
            OrganizationVO newOrga = new OrganizationVO();
 
            newOrga.setName(new TextVO());
            return newOrga;
        }

        public List<OrganizationVO> getDataListFromVO()
        {
            if (parentVO == null) return null;
            return parentVO.getOrganizations();
        }

        public void setParentVO(PersonVO parentVO)
        {
            this.parentVO = parentVO;
            for (OrganizationVO orgaVO : parentVO.getOrganizations())
            {
                if (orgaVO.getName() == null)
                {
                    orgaVO.setName(new TextVO());
                }
            }
            setObjectList(parentVO.getOrganizations());
        }

        public int getSize()
        {
            return getObjectDM().getRowCount();
        }
    }


    public PersonOrganisationManager getPersonOrganisationManager()
    {
        return personOrganisationManager;
    }

    public void setContentAbstractManager(final PersonOrganisationManager personOrganisationManager)
    {
        this.personOrganisationManager = personOrganisationManager;
    }

    public boolean isPersonType()
    {
        return personType;
    }

    public void setPersonType(final boolean personType)
    {
        this.personType = personType;
    }

    public OrganizationVO getCurrentOrgaForSelection()
    {
        return currentOrgaForSelection;
    }

    public void setCurrentOrgaForSelection(final OrganizationVO currentOrgaForSelection)
    {
        this.currentOrgaForSelection = currentOrgaForSelection;
    }

    public boolean isOrganisationType()
    {
        return organisationType;
    }

    public void setOrganisationType(final boolean organisationType)
    {
        this.organisationType = organisationType;
    }

    /**
     * localized creation of SelectItems for the creator roles available.
     * @return SelectItem[] with Strings representing creator roles.
     */
    public SelectItem[] getCreatorRoles()
    {
        return this.i18nHelper.getSelectItemsCreatorRole(true);
    }

    /**
     * localized creation of SelectItems for the creator types available.
     * @return SelectItem[] with Strings representing creator types.
     */
    public SelectItem[] getCreatorTypes()
    {

        return this.i18nHelper.getSelectItemsCreatorType(false);
    }

    public String getIdentifierValue()
    {
        if (isPersonType() && getCreator() != null && getCreator().getPerson() != null && getCreator().getPerson().getIdentifier() != null)
        {
            return getCreator().getPerson().getIdentifier().getId();
        }
        else
        {
            return null;
        }
    }
    
    public void setIdentifierValue(String newValue)
    {
        if (newValue != null && !"".equals(newValue))
        {
            if (isPersonType() && getCreator() != null && getCreator().getPerson() != null)
            {
                if (getCreator().getPerson().getIdentifier() == null)
                {
                    getCreator().getPerson().setIdentifier(new IdentifierVO());
                }
                getCreator().getPerson().getIdentifier().setId(newValue);
                getCreator().getPerson().getIdentifier().setType(IdType.CONE);
            }
        }
        else
        {
            getCreator().getPerson().setIdentifier(null);
        }
    }
}
