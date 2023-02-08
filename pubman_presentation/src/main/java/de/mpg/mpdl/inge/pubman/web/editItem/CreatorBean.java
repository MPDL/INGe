/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.pubman.web.editItem;

import java.util.List;

import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.ValueChangeEvent;
import jakarta.faces.model.SelectItem;

import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.pubman.web.affiliation.AffiliationBean;
import de.mpg.mpdl.inge.pubman.web.easySubmission.EasySubmissionSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.DataModelManager;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.vos.OrganizationVOPresentation;

/**
 * POJO bean to deal with one creator. This can either be a person or a organisation. Only for
 * creators of type person there is a list of assigned organisations included.
 * 
 * @author Mario Wagner
 */
@SuppressWarnings("serial")
public class CreatorBean extends FacesBean {
  private CreatorVO creator = null;
  private OrganizationVO currentOrgaForSelection = null;
  private PersonOrganisationManager personOrganisationManager = null;
  private String ouNumber = null;
  private boolean personType;

  public CreatorBean() {
    // ensure the parentVO is never null;
    this(new CreatorVO());
  }

  public CreatorBean(CreatorVO creator) {
    this.setCreator(creator);
  }

  public CreatorVO getCreator() {
    return this.creator;
  }

  public void setCreator(CreatorVO creator) {
    if (this.creator.getType() == null) {
      this.creator.setType(CreatorVO.CreatorType.PERSON);
    }
    if (CreatorVO.CreatorType.PERSON.equals(creator.getType())
        && (this.creator.getPerson() == null || this.creator.getPerson().getOrganizations().size() == 0)) {
      if (this.creator.getPerson() == null) {
        this.creator.setPerson(new PersonVO());
      }
      if (this.creator.getPerson().getOrganizations().size() == 0) {
        // create a new Organization for this person
        final OrganizationVO newPersonOrganization = new OrganizationVO();
        newPersonOrganization.setName("");
        this.creator.getPerson().getOrganizations().add(newPersonOrganization);
      }
    } else if (CreatorVO.CreatorType.ORGANIZATION.equals(this.creator.getType())) {
      if (this.creator.getOrganization() == null) {
        final OrganizationVO newOrga = new OrganizationVO();
        newOrga.setName("");

        this.creator.setOrganization(newOrga);
      }
      if (this.creator.getOrganization() != null && this.creator.getOrganization().getName() == null) {
        this.creator.getOrganization().setName("");
      }
    }
    this.creator = creator;
    this.personType = CreatorVO.CreatorType.PERSON.equals(creator.getType());
    // organisationType = CreatorVO.CreatorType.ORGANIZATION.equals(creator.getType());
    this.currentOrgaForSelection = null;
    // ensure proper initialization of our DataModelManager
    if (this.personType) {
      this.personOrganisationManager = new PersonOrganisationManager(creator.getPerson());
    } else {
      this.personOrganisationManager = null;
    }
  }

  /**
   * Action navigation call to select one persons organisation
   * 
   * @return
   */
  public String selectPersonOrganisation() {
    this.currentOrgaForSelection = (OrganizationVO) this.personOrganisationManager.getObjectDM().getRowData();

    // Set this value to let the affiliation tree know where to jump after selection.
    final AffiliationBean affiliationBean = (AffiliationBean) FacesTools.findBean("AffiliationBean");
    affiliationBean.setSource("EditItem");
    affiliationBean.setCache(this.currentOrgaForSelection);

    // affiliation tree
    return "loadAffiliationTree";
  }

  /**
   * @return true if this element is selecting the organisation.
   */
  public boolean getSelecting() {
    this.currentOrgaForSelection = (OrganizationVO) this.personOrganisationManager.getObjectDM().getRowData();

    // Set this value to let the affiliation tree know where to jump after selection.
    final OrganizationVO selecting = ((EasySubmissionSessionBean) FacesTools.findBean("EasySubmissionSessionBean")).getCurrentlySelecting();

    return (this.currentOrgaForSelection.equals(selecting));
  }

  /**
   * Action navigation call to select one persons organisation for easy submission
   * 
   * @return
   */
  public String selectPersonOrganisationEasySubmission() {
    this.currentOrgaForSelection = (OrganizationVO) this.personOrganisationManager.getObjectDM().getRowData();

    // Set this value to let the affiliation tree know where to jump after selection.
    final AffiliationBean affiliationBean = FacesTools.findBean("AffiliationBean");
    affiliationBean.setSource("EasySubmission");
    affiliationBean.setCache(this.currentOrgaForSelection);
    ((EasySubmissionSessionBean) FacesTools.findBean("EasySubmissionSessionBean")).setCurrentlySelecting(this.currentOrgaForSelection);

    // affiliation tree
    return "loadAffiliationTree";
  }

  /**
   * Action navigation call to select the creator organisation
   * 
   * @return
   */
  public String selectOrganisation() {
    if (this.creator.getOrganization() == null) {
      this.creator.setOrganization(new OrganizationVO());
    }

    this.currentOrgaForSelection = this.creator.getOrganization();

    // Set this value to let the affiliation tree know where to jump after selection.
    final AffiliationBean affiliationBean = FacesTools.findBean("AffiliationBean");
    affiliationBean.setSource("EditItem");
    affiliationBean.setCache(this.currentOrgaForSelection);

    return "loadAffiliationTree";
  }

  /**
   * Action navigation call to select the creator organisation for Easy Submission
   * 
   * @return
   */
  public String selectOrganisationEasySubmission() {
    if (this.creator.getOrganization() == null) {
      this.creator.setOrganization(new OrganizationVO());
    }

    this.currentOrgaForSelection = this.creator.getOrganization();

    // Set this value to let the affiliation tree know where to jump after selection.
    final AffiliationBean affiliationBean = FacesTools.findBean("AffiliationBean");
    affiliationBean.setSource("EasySubmission");
    affiliationBean.setCache(this.currentOrgaForSelection);

    return "loadAffiliationTree";
  }

  /**
   * ValueChangeListener method to handle changes in the creatorType.
   * 
   * @param event
   * @throws AbortProcessingException
   */
  public void processCreatorTypeChanged(ValueChangeEvent event) throws AbortProcessingException {
    // Reinitialize this POJO, because the creator.getType() has been changed.
    final String newVal = (String) event.getNewValue();
    this.creator.setTypeString(newVal);
    this.setCreator(this.creator);
    // enforce rendering of the response
    // FacesContext context = FacesTools.getCurrentInstance();
    // context.renderResponse();
  }

  /**
   * Specialized DataModelManager to deal with objects of type OrganizationVO
   * 
   * @author Mario Wagner
   */
  public class PersonOrganisationManager extends DataModelManager<OrganizationVO> {
    PersonVO parentVO;

    public PersonOrganisationManager(PersonVO parentVO) {
      this.setParentVO(parentVO);
    }

    @Override
    public OrganizationVO createNewObject() {
      final OrganizationVO newOrga = new OrganizationVO();

      newOrga.setName("");
      return newOrga;
    }

    public List<OrganizationVO> getDataListFromVO() {
      if (this.parentVO == null) {
        return null;
      }

      return this.parentVO.getOrganizations();
    }

    public void setParentVO(PersonVO parentVO) {
      this.parentVO = parentVO;
      for (final OrganizationVO orgaVO : parentVO.getOrganizations()) {
        if (orgaVO.getName() == null) {
          orgaVO.setName("");
        }
      }
      this.setObjectList(parentVO.getOrganizations());
    }

    public int getSize() {
      return this.getObjectDM().getRowCount();
    }
  }


  public PersonOrganisationManager getPersonOrganisationManager() {
    return this.personOrganisationManager;
  }

  public void setContentAbstractManager(final PersonOrganisationManager personOrganisationManager) {
    this.personOrganisationManager = personOrganisationManager;
  }

  public boolean isPersonType() {
    return (this.creator.getType() == CreatorType.PERSON);
  }

  public void setPersonType(final boolean personType) {
    this.creator.setType(CreatorType.PERSON);
  }

  public OrganizationVO getCurrentOrgaForSelection() {
    return this.currentOrgaForSelection;
  }

  public void setCurrentOrgaForSelection(final OrganizationVO currentOrgaForSelection) {
    this.currentOrgaForSelection = currentOrgaForSelection;
  }

  public boolean isOrganisationType() {
    return (this.creator.getType() == CreatorType.ORGANIZATION);
  }

  public void setOrganisationType(final boolean organisationType) {
    this.creator.setType(CreatorType.ORGANIZATION);
  }

  /**
   * localized creation of SelectItems for the creator roles available.
   * 
   * @return SelectItem[] with Strings representing creator roles.
   */
  public SelectItem[] getCreatorRoles() {
    return this.getI18nHelper().getSelectItemsCreatorRole(true);
  }

  /**
   * localized creation of SelectItems for the creator types available.
   * 
   * @return SelectItem[] with Strings representing creator types.
   */
  public SelectItem[] getCreatorTypes() {
    return this.getI18nHelper().getSelectItemsCreatorType(false);
  }

  public String getIdentifierValue() {
    if (this.isPersonType() && this.getCreator() != null && this.creator.getPerson() != null
        && this.creator.getPerson().getIdentifier() != null) {
      return this.creator.getPerson().getIdentifier().getId();
    }

    return null;
  }

  public void setIdentifierValue(String newValue) {
    if (newValue != null && !"".equals(newValue)) {
      if (this.isPersonType() && this.getCreator() != null && this.creator.getPerson() != null) {
        if (this.creator.getPerson().getIdentifier() == null) {
          this.creator.getPerson().setIdentifier(new IdentifierVO());
        }
        this.creator.getPerson().getIdentifier().setId(newValue);
        this.creator.getPerson().getIdentifier().setType(IdType.CONE);
      }
    } else {
      this.creator.getPerson().setIdentifier(null);
    }
  }

  public String getOuNumbers() {
    if (this.personType && this.ouNumber == null) {
      final EditItemSessionBean editItemSessionBean = (EditItemSessionBean) FacesTools.findBean("EditItemSessionBean");
      final List<OrganizationVOPresentation> creatorOrganizations = editItemSessionBean.getCreatorOrganizations();
      for (final OrganizationVO organization : this.creator.getPerson().getOrganizations()) {
        if (this.ouNumber == null) {
          this.ouNumber = "";
        } else {
          this.ouNumber += ",";
        }
        if (creatorOrganizations.indexOf(organization) >= 0) {
          this.ouNumber += creatorOrganizations.indexOf(organization) + 1;
        }
      }
    }

    return this.ouNumber;
  }

  /**
   * @param ouNumber the ouNumber to set
   */
  public void setOuNumbers(String ouNumber) {
    this.ouNumber = ouNumber;
  }

  public String getAutoPasteValue() {
    return "";
  }

  public void setAutoPasteValue(String value) {
    if (!"".equals(value)) {
      final String[] values = value.split(EditItem.AUTOPASTE_INNER_DELIMITER);
      final EditItemSessionBean editItemSessionBean = (EditItemSessionBean) FacesTools.findBean("EditItemSessionBean");
      final List<OrganizationVOPresentation> creatorOrganizations = editItemSessionBean.getCreatorOrganizations();
      final OrganizationVOPresentation newOrg = new OrganizationVOPresentation();
      newOrg.setName(values[1]);
      newOrg.setIdentifier(values[0]);
      newOrg.setBean(editItemSessionBean);
      creatorOrganizations.add(newOrg);
      this.ouNumber = creatorOrganizations.size() + "";
    }
  }

  public String getCreatorPersonFamilyName() {
    return this.creator.getPerson().getFamilyName();
  }

  public void setCreatorPersonFamilyName(String name) {
    this.creator.getPerson().setFamilyName(name);
  }

  public String getCreatorPersonGivenName() {
    return this.creator.getPerson().getGivenName();
  }

  public void setCreatorPersonGivenName(String name) {
    this.creator.getPerson().setGivenName(name);
  }
}
