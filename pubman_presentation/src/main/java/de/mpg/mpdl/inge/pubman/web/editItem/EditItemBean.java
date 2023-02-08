package de.mpg.mpdl.inge.pubman.web.editItem;

import java.util.ArrayList;
import java.util.List;

import jakarta.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.CreatorVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.OrganizationVOPresentation;
import de.mpg.mpdl.inge.transformation.util.creators.Author;
import de.mpg.mpdl.inge.transformation.util.creators.AuthorDecoder;

@SuppressWarnings("serial")
public class EditItemBean extends FacesBean {
  private static final Logger logger = Logger.getLogger(EditItemBean.class);

  /**
   * Stores a string from a hidden input field (set by javascript) that indicates whether the author
   * copy&paste elements are to be displayed or not.
   */
  private String showAuthorCopyPaste;

  /** Checkbox if existing authors should be overwritten with the ones from author copy/paste */
  private boolean overwriteCreators;

  private List<CreatorVOPresentation> creators = new ArrayList<CreatorVOPresentation>();

  private List<OrganizationVOPresentation> creatorOrganizations = new ArrayList<OrganizationVOPresentation>();

  /** The string with authors to parse for author copy&paste. */
  private String creatorParseString;

  private boolean organizationPasted = false;

  /** List containing the ou number of the organizations which are mapped to a creator */
  private final List<Integer> usedOrganizations = new ArrayList<Integer>();

  public List<CreatorVOPresentation> getCreators() {
    return this.creators;
  }

  public void setCreators(List<CreatorVOPresentation> creators) {
    this.creators = creators;
  }

  public List<OrganizationVOPresentation> getCreatorOrganizations() {
    return this.creatorOrganizations;
  }

  public void setCreatorOrganizations(List<OrganizationVOPresentation> creatorOrganizations) {
    this.creatorOrganizations = creatorOrganizations;
  }

  public void initOrganizationsFromCreators() {
    final List<OrganizationVOPresentation> creatorOrganizations = new ArrayList<OrganizationVOPresentation>();
    for (final CreatorVOPresentation creator : this.creators) {
      if (creator.getType() == CreatorType.PERSON) {
        for (final OrganizationVO organization : creator.getPerson().getOrganizations()) {
          if (!creatorOrganizations.contains(organization)) {
            final OrganizationVOPresentation organizationPresentation = new OrganizationVOPresentation(organization);
            if (!organizationPresentation.isEmpty()) {
              organizationPresentation.setBean(this);
              if (organizationPresentation.getName() == null) {
                organizationPresentation.setName("");
              }
              creatorOrganizations.add(organizationPresentation);
            }
          }
        }
      }
    }

    // if there is still no organization add a new one
    if (creatorOrganizations.isEmpty()) {
      final OrganizationVOPresentation org = new OrganizationVOPresentation();
      org.setBean(this);
      creatorOrganizations.add(org);
    }

    this.creatorOrganizations = creatorOrganizations;
  }

  /**
   * Returns the content(set by javascript) from a hidden input field that indicates whether the
   * author copy&paste elements are to be displayed or not.
   */
  public String getShowAuthorCopyPaste() {
    return this.showAuthorCopyPaste;
  }

  /**
   * Sets the content from a hidden input field that indicates whether the author copy&paste
   * elements are to be displayed or not.
   */
  public void setShowAuthorCopyPaste(String showAuthorCopyPaste) {
    this.showAuthorCopyPaste = showAuthorCopyPaste;
  }

  public void setOverwriteCreators(boolean overwriteCreators) {
    this.overwriteCreators = overwriteCreators;
  }

  public boolean getOverwriteCreators() {
    return this.overwriteCreators;
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

  public boolean bindOrganizationsToCreators() {
    this.usedOrganizations.clear();
    for (final CreatorVOPresentation creator : this.getCreators()) {
      if (!this.bindOrganizationsToCreator(creator)) {
        return false;
      }
    }

    for (final OrganizationVOPresentation org : this.getCreatorOrganizations()) {

      if (!org.isEmpty() && !this.usedOrganizations.contains(org.getNumber())) {

        this.error(this.getMessage("EntryIsNotBound").replace("$1", String.valueOf(org.getNumber())));
        return false;
      }
    }

    return true;
  }

  public void bindCreatorsToBean(List<CreatorVO> creatorList) {
    final List<CreatorVOPresentation> creators = this.getCreators();
    creators.clear();

    for (final CreatorVO creator : creatorList) {
      final CreatorVOPresentation beanCreator = new CreatorVOPresentation(creators, this, creator);
      if (beanCreator.getPerson() != null && beanCreator.getPerson().getIdentifier() == null) {
        beanCreator.getPerson().setIdentifier(new IdentifierVO());
      }
      creators.add(beanCreator);
    }
  }

  public void bindCreatorsToVO(List<CreatorVO> creators) {
    creators.clear();
    for (final CreatorVOPresentation creatorVOPresentation : this.getCreators()) {
      CreatorVO creatorVO;
      if (CreatorType.ORGANIZATION == creatorVOPresentation.getType()) {
        creatorVO = new CreatorVO(creatorVOPresentation.getOrganization(), creatorVOPresentation.getRole());
      } else {
        creatorVO = new CreatorVO(creatorVOPresentation.getPerson(), creatorVOPresentation.getRole());
      }

      creators.add(creatorVO);
    }
  }

  /**
   * @param creator
   */
  public boolean bindOrganizationsToCreator(CreatorVOPresentation creator) {
    if (creator.isPersonType()) {
      final PersonVO person = creator.getPerson();
      final List<OrganizationVO> personOrgs = person.getOrganizations();
      String[] orgArr = new String[] {};
      if (creator.getOuNumbers() != null) {
        orgArr = creator.getOuNumbers().split(",");
      }
      personOrgs.clear();
      try {
        for (final String org : orgArr) {
          if (!"".equals(org)) {
            final int orgNr = Integer.parseInt(org);
            personOrgs.add(this.getCreatorOrganizations().get(orgNr - 1));
            this.usedOrganizations.add(orgNr);
          }
        }
      } catch (final NumberFormatException nfe) {
        this.error(this.getMessage("EntryIsNotANumber").replace("$1", creator.getOuNumbers()));
        return false;
      } catch (final IndexOutOfBoundsException ioobe) {
        this.error(this.getMessage("EntryIsNotInValidRange").replace("$1", creator.getOuNumbers()));
        return false;
      } catch (final Exception e) {
        EditItemBean.logger.error("Unexpected error evaluation creator organizations", e);
        this.error(this.getMessage("ErrorInOrganizationAssignment").replace("$1", creator.getOuNumbers()));
        return false;
      }
    }

    return true;
  }

  public int getOrganizationCount() {
    return this.getCreatorOrganizations().size();
  }

  public void readPastedOrganizations() {
    EditItemBean.logger.debug("readPastedOrganizations");
    this.organizationPasted = false;
  }

  public boolean isOrganizationPasted() {
    return this.organizationPasted;
  }

  public void setOrganizationPasted(boolean organizationPasted) {
    this.organizationPasted = organizationPasted;
  }

  public void clean() {
    this.getCreatorOrganizations().clear();
    this.getCreators().clear();

    this.setShowAuthorCopyPaste("");
    this.creatorParseString = "";
  }

  public void setCreatorParseString(String creatorParseString) {
    this.creatorParseString = creatorParseString;
  }

  public String getCreatorParseString() {
    return this.creatorParseString;
  }

  /**
   * Parses a string that includes creators in different formats and adds them to the given
   * creatorCollection.
   * 
   * @param creatorString The String to be parsed
   * @param creatorCollection The collection to which the creators should be added
   * @param orgs A list of organizations that should be added to every creator. null if no
   *        organizations should be added.
   * @param overwrite Indicates if the already existing creators should be overwritten
   * @throws Exception
   */
  public void parseCreatorString(String creatorString, List<OrganizationVO> orgs, boolean overwrite) throws Exception {
    final AuthorDecoder authDec = new AuthorDecoder(creatorString);

    final List<Author> authorList = authDec.getBestAuthorList();
    if (authorList == null || authorList.size() == 0) {
      throw new Exception(this.getMessage("EditItemBean_errorParseCreator"));
    }

    if (overwrite) {
      this.getCreators().clear();
    }

    // check if last existing author is empty, then remove it
    if (this.getCreators().size() >= 1) {
      final CreatorVOPresentation creatorVO = this.getCreators().get(this.getCreators().size() - 1);
      // creator is a person
      if (creatorVO.isPersonType() && creatorVO.getPerson() != null && "".equals(creatorVO.getPerson().getFamilyName())
          && "".equals(creatorVO.getPerson().getGivenName())
          && (creatorVO.getPerson().getOrganizations().isEmpty() || creatorVO.getPerson().getOrganizations().get(0).getName() == null
              || "".equals(creatorVO.getPerson().getOrganizations().get(0).getName()))) {
        this.getCreators().remove(creatorVO);
      }
      // creator is an organisation
      else if (creatorVO.isOrganizationType() && creatorVO.getOrganization() != null && "".equals(creatorVO.getOrganization().getName())) {
        this.getCreators().remove(creatorVO);
      }
    }

    // add authors to creator collection
    for (final Author author : authorList) {
      final CreatorVOPresentation creator = new CreatorVOPresentation(this.getCreators(), this);
      creator.setPerson(new PersonVO());
      creator.getPerson().setIdentifier(new IdentifierVO());
      creator.setOuNumbers("");
      this.getCreators().add(creator);

      if (author.getPrefix() != null && !"".equals(author.getPrefix())) {
        creator.getPerson().setFamilyName(author.getPrefix() + " " + author.getSurname());
      } else {
        creator.getPerson().setFamilyName(author.getSurname());
      }
      creator.getPerson().setGivenName(author.getGivenName());

      creator.setRole(CreatorRole.AUTHOR);
      creator.setType(CreatorType.PERSON);
    }
  }
}
