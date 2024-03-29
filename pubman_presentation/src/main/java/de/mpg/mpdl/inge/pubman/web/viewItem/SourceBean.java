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

package de.mpg.mpdl.inge.pubman.web.viewItem;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.ObjectFormatter;
import de.mpg.mpdl.inge.pubman.web.util.vos.CreatorDisplay;
import de.mpg.mpdl.inge.util.ConeUtils;

/**
 * Bean for creating the source section of a pubitem to be used in the ViewItemFullUI.
 * 
 * @author: Tobias Schraut, created 25.03.2008
 * @version: $Revision$ $LastChangedDate$
 */
@SuppressWarnings("serial")
public class SourceBean extends FacesBean {
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
  private ArrayList<ViewItemCreators> sourceCreatorArray;

  /**
   * The list of formatted creators which are organizations in an ArrayList.
   */
  private ArrayList<ViewItemCreatorOrganization> sourceCreatorOrganizationsArray;

  private SourceVO source;
  private String identifiers;
  private String publishingInfo;
  private String startEndPage;

  public SourceBean(SourceVO source) {
    this.source = source;
    this.initialize(source);
  }

  /**
   * Initializes the UI and sets all attributes of the GUI components.
   * 
   * @param pubItemVO a pubitem
   */
  protected void initialize(SourceVO source) {
    if (source.getCreators().size() > 0) {
      this.createCreatorsList();
    }

    this.startEndPage = this.getStartEndPage(source);
    this.publishingInfo = this.getPublishingInfo(source);

    if (source.getIdentifiers().size() > 0) {
      this.identifiers = ViewItemFull.getIdentifierHtmlString(source.getIdentifiers());
    }
  }

  private void createCreatorsList() {
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
    final ObjectFormatter formatter = new ObjectFormatter();

    // temporary list of All creators, retrieved directly from the metadata
    tempCreatorList = this.getSource().getCreators();
    // the list of creators is initialized to a new array list
    this.setSourceCreatorArray(new ArrayList<ViewItemCreators>());
    int affiliationPosition = 0;

    // for each creator in the list
    for (int i = 0; i < tempCreatorList.size(); i++) {

      // temporary organization list is matched against the sorted for each separate creator
      // therefore for each creator is newly re-set
      tempOrganizationList = new ArrayList<OrganizationVO>();

      CreatorVO creator1 = new CreatorVO();
      creator1 = tempCreatorList.get(i);

      final CreatorDisplay creatorDisplay = new CreatorDisplay();
      final ViewItemCreators creator = new ViewItemCreators();

      // if the creator is a person add his organization to the sorted organization list
      if (creator1.getPerson() != null) {
        // if there is affiliated organization for this creator
        if (creator1.getPerson().getOrganizations().size() > 0) {
          // add each affiliated organization of the creator to the temporary organization list
          for (int listSize = 0; listSize < creator1.getPerson().getOrganizations().size(); listSize++) {
            tempOrganizationList.add(creator1.getPerson().getOrganizations().get(listSize));
          }

          // for each organizations in the temporary organization list
          for (int j = 0; j < tempOrganizationList.size(); j++) {
            // check if the organization in the list is in the sorted organization list
            if (!sortOrganizationList.contains(tempOrganizationList.get(j))) {
              affiliationPosition++;
              // if the temporary organization is to be added to the sorted set of organizations
              sortOrganizationList.add(tempOrganizationList.get(j));
              // create new Organization view object
              this.getSourceOrganizationList()
                  .add(ViewItemFull.formatCreatorOrganization(tempOrganizationList.get(j), affiliationPosition));
            }
          }
        }

        formattedCreator = formatter.formatCreator(creator1, ViewItemFull.formatCreatorOrganizationIndex(creator1, sortOrganizationList));
        creatorDisplay.setFormattedDisplay(formattedCreator);

        if (creator1.getPerson().getIdentifier() != null && (creator1.getPerson().getIdentifier().getType() == IdType.CONE)) {
          try {
            creatorDisplay.setPortfolioLink(ConeUtils.makeConePersonsLinkFull(creator1.getPerson().getIdentifier().getId()));
          } catch (final Exception e) {
            throw new RuntimeException(e);
          }
        }

        if (creator1.getPerson().getOrcid() != null) {
          try {
            creatorDisplay.setOrcid(creator1.getPerson().getOrcid());
          } catch (final Exception e) {
            throw new RuntimeException(e);
          }
        }

        // this.sourceCreatorArray.add(creatorDisplay);
        creator.setCreatorType(ViewItemCreators.Type.PERSON.toString());
        creator.setCreatorObj(creatorDisplay);
        creator.setCreatorRole(creator1.getRoleString());
        this.sourceCreatorArray.add(creator);
      } // end if creator is a person

      if (creator1.getOrganization() != null) {
        formattedCreator = formatter.formatCreator(creator1, "");
        creatorDisplay.setFormattedDisplay(formattedCreator);
        final ViewItemCreatorOrganization creatorOrganization = new ViewItemCreatorOrganization();
        creatorOrganization.setOrganizationName(formattedCreator);
        creatorOrganization.setPosition(String.valueOf(counterOrganization));
        creatorOrganization.setOrganizationAddress(creator1.getOrganization().getAddress());
        creatorOrganization.setOrganizationInfoPage(formattedCreator, creator1.getOrganization().getAddress());
        creatorOrganization.setIdentifier(creator1.getOrganization().getIdentifier());
        this.sourceCreatorOrganizationsArray.add(creatorOrganization);
        creator.setCreatorType(ViewItemCreators.Type.ORGANIZATION.toString());
        creator.setCreatorObj(creatorOrganization);
        creator.setCreatorRole(creator1.getRoleString());
        this.sourceCreatorArray.add(creator);
      }

      counterOrganization++;
      this.setSourceAffiliatedOrganizationsList(sortOrganizationList);
      // generate a 'well-formed' list for presentation in the jsp
      for (int k = 0; k < sortOrganizationList.size(); k++) {
        final String name = sortOrganizationList.get(k).getName() != null ? sortOrganizationList.get(k).getName() : "";
        formattedOrganization = "<p>" + (k + 1) + ": " + name + "</p>" + "<p>" + sortOrganizationList.get(k).getAddress() + "</p>" + "<p>"
            + sortOrganizationList.get(k).getIdentifier() + "</p>";
        this.sourceOrganizationArray.add(formattedOrganization);
      }
    } // end for each creator in the list
  }

  /**
   * Returns the formatted Publishing Info according to filled elements
   * 
   * @return String the formatted Publishing Info
   */
  private String getPublishingInfo(SourceVO source) {


    final StringBuffer publishingInfo = new StringBuffer();
    publishingInfo.append("");
    if (source.getPublishingInfo() != null) {

      // Place
      if (source.getPublishingInfo().getPlace() != null && !source.getPublishingInfo().getPlace().equals("")) {
        publishingInfo.append(source.getPublishingInfo().getPlace().trim());
      }

      // colon
      if (source.getPublishingInfo().getPublisher() != null && !source.getPublishingInfo().getPublisher().trim().equals("")
          && source.getPublishingInfo().getPlace() != null && !source.getPublishingInfo().getPlace().trim().equals("")) {
        publishingInfo.append(" : ");
      }

      // Publisher
      if (source.getPublishingInfo().getPublisher() != null && !source.getPublishingInfo().getPublisher().equals("")) {
        publishingInfo.append(source.getPublishingInfo().getPublisher().trim());
      }

      // Comma
      if ((source.getPublishingInfo().getEdition() != null && !source.getPublishingInfo().getEdition().trim().equals(""))
          && ((source.getPublishingInfo().getPlace() != null && !source.getPublishingInfo().getPlace().trim().equals(""))
              || (source.getPublishingInfo().getPublisher() != null && !source.getPublishingInfo().getPublisher().trim().equals("")))) {
        publishingInfo.append(", ");
      }

      // Edition
      if (source.getPublishingInfo().getEdition() != null) {
        publishingInfo.append(source.getPublishingInfo().getEdition());
      }

    }
    return publishingInfo.toString();
  }

  /**
   * Returns a formatted String containing the start and the end page of the source
   * 
   * @return String the formatted start and end page
   */
  private String getStartEndPage(SourceVO source) {
    final StringBuffer startEndPage = new StringBuffer();

    if (source.getStartPage() != null) {
      startEndPage.append(source.getStartPage());
    }

    if (source.getEndPage() != null) {
      startEndPage.append(" - ");
      startEndPage.append(source.getEndPage());
    }

    if (startEndPage.toString().equals(" - ")) {
      return "";
    }

    return startEndPage.toString();
  }

  public String getGenre() {
    return this.getLabel(this.getI18nHelper().convertEnumToString(this.source.getGenre()));
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

  public void setSourceOrganizationList(ArrayList<ViewItemOrganization> sourceOrganizationList) {
    this.sourceOrganizationList = sourceOrganizationList;
  }

  public List<OrganizationVO> getSourceAffiliatedOrganizationsList() {
    return this.sourceAffiliatedOrganizationsList;
  }

  public void setSourceAffiliatedOrganizationsList(List<OrganizationVO> sourceAffiliatedOrganizationsList) {
    this.sourceAffiliatedOrganizationsList = sourceAffiliatedOrganizationsList;
  }

  public ArrayList<ViewItemCreators> getSourceCreatorArray() {
    return this.sourceCreatorArray;
  }

  public void setSourceCreatorArray(ArrayList<ViewItemCreators> sourceCreatorArray) {
    this.sourceCreatorArray = sourceCreatorArray;
  }

  public ArrayList<ViewItemCreatorOrganization> getSourceCreatorOrganizationsArray() {
    return this.sourceCreatorOrganizationsArray;
  }

  public void setSourceCreatorOrganizationsArray(ArrayList<ViewItemCreatorOrganization> sourceCreatorOrganizationsArray) {
    this.sourceCreatorOrganizationsArray = sourceCreatorOrganizationsArray;
  }

  public boolean getHasCreator() {
    if (this.sourceCreatorArray.size() > 0) {
      return true;
    }
    return false;
  }

  public boolean getHasAffiliation() {
    if (this.sourceOrganizationArray.size() > 0) {
      return true;
    }
    return false;
  }
}
