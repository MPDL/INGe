package de.mpg.escidoc.pubman.editItem.bean;

import java.util.List;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PersonVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;

/**
 * POJO bean to deal with one creator. This can either be a person or a organisation.
 * Only for creators of type person there is a list of assigned organisations included.
 *
 * @author Mario Wagner
 */
public class CreatorBean extends FacesBean
{
    private CreatorVO creator;
    private PersonOrganisationManager personOrganisationManager;

    private boolean personType, organisationType;
    private OrganizationVO currentOrgaForSelection;

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
        if (CreatorVO.CreatorType.PERSON.equals(creator.getType()) && creator.getPerson() == null)
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

        // FIXME remeber this VO and initialize external OrganisationVO selection

        return null;
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

        // FIXME remeber this VO and initialize external OrganisationVO selection

        return null;
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
        return ((ApplicationBean) getApplicationBean(ApplicationBean.class)).getSelectItemsCreatorRole(true);
    }

    /**
     * localized creation of SelectItems for the creator types available.
     * @return SelectItem[] with Strings representing creator types.
     */
    public SelectItem[] getCreatorTypes()
    {

        return ((ApplicationBean) getBean(ApplicationBean.class)).getSelectItemsCreatorType(false);
    }

}
