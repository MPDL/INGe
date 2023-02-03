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

package de.mpg.mpdl.inge.pubman.web.util.vos;

import co.elastic.clients.elasticsearch.core.search.Hit;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.*;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorType;
import de.mpg.mpdl.inge.model.xmltransforming.util.HtmlUtils;
import de.mpg.mpdl.inge.pubman.web.GFZConeBean;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.InternationalizationHelper;
import de.mpg.mpdl.inge.pubman.web.viewItem.FileBean;
import de.mpg.mpdl.inge.pubman.web.viewItem.ViewItemCreatorOrganization;
import de.mpg.mpdl.inge.pubman.web.viewItem.ViewItemOrganization;
import de.mpg.mpdl.inge.util.PropertyReader;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;

/**
 * Wrapper class for items to be used in the presentation.
 * 
 * @author franke
 * @author $Author$
 * @version: $Revision$ $LastChangedDate: 2007-12-04 16:52:04 +0100 (Di, 04 Dez 2007)$
 */
@SuppressWarnings("serial")
public class PubItemVOPresentation extends ItemVersionVO {
  private final InternationalizationHelper i18nHelper = FacesTools.findBean("InternationalizationHelper");

  private boolean selected = false;
  private boolean shortView = true;
  private boolean released = false;

  private List<FileBean> fileBeanList;
  private List<FileBean> locatorBeanList;
  private String descriptionMetaTag;

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
   * The list of formatted creators (persons) in an ArrayList.
   */
  private ArrayList<String> creatorArray;

  /**
   * The list of formatted creators which are organizations in an ArrayList.
   */
  private ArrayList<ViewItemCreatorOrganization> creatorOrganizationsArray;

  /**
   * The list of formatted creators (persons AND organizations) in an ArrayList.
   */
  private ArrayList<String> allCreatorsList;

  /**
   * the first source of the item (for display in the medium view)
   */
  private SourceVO firstSource;

  private boolean isSearchResult = false;

  private boolean isFromEasySubmission;

  private List<WrappedLocalTag> wrappedLocalTags;

  /**
   * Validation messages that should be displayed in item list
   */
  private ValidationReportVO validationReport;

  private float score;

  private Hit<ItemVersionVO> searchHit;

  private Map<String, List<String>> highlightMap = new HashMap<>();

  public PubItemVOPresentation(ItemVersionVO item) {
    this(item, null);
  }

  public PubItemVOPresentation(ItemVersionVO item, Hit<ItemVersionVO> searchHit) {
    super(item);
    if (this != null && this.getVersionState() != null) {
      this.released = ItemVersionRO.State.RELEASED.equals(this.getVersionState());
    }

    // get the first source of the item (if available)
    if (item.getMetadata() != null && item.getMetadata().getSources() != null && item.getMetadata().getSources().size() > 0) {
      this.firstSource = new SourceVO();
      this.firstSource = item.getMetadata().getSources().get(0);
    }

    // Check the local tags
    if (this.getObject().getLocalTags().isEmpty()) {
      this.getObject().getLocalTags().add("");
    }

    this.wrappedLocalTags = new ArrayList<WrappedLocalTag>();
    for (int i = 0; i < this.getObject().getLocalTags().size(); i++) {
      final WrappedLocalTag wrappedLocalTag = new WrappedLocalTag();
      wrappedLocalTag.setParent(this);
      wrappedLocalTag.setValue(this.getObject().getLocalTags().get(i));
      if (wrappedLocalTag.getValue().length() > 0 || this.wrappedLocalTags.size() == 0) {
        this.wrappedLocalTags.add(wrappedLocalTag);
      }
    }

    if (searchHit != null) {
      this.initSearchHits(searchHit);
    }
    this.initFileBeans();
  }

  public void initSearchHits(Hit<ItemVersionVO> hit) {
    this.searchHit = hit;
    this.isSearchResult = true;
    this.score = searchHit.score().floatValue();

    /*

    if (searchHit != null && searchHit.highlight()!=null && searchHit.highlight().get("file")!=null) {
      String fileId = ((Map<String, Object>) innerhit.getSourceAsMap().get("fileData")).get("fileId").toString();


      for (String highlights : searchHit.highlight().get("file")) {
        List<String> highlights = new ArrayList<>();
        for (Entry<String, HighlightField> highlight : innerhit.getHighlightFields().entrySet()) {
          if (highlight.getValue() != null && highlight.getValue().getFragments() != null) {
            for (Text t : highlight.getValue().getFragments()) {
              highlights.add(t.toString());
            }
          }
        }
        String fileId = ((Map<String, Object>) innerhit.getSourceAsMap().get("fileData")).get("fileId").toString();
        getHighlightMap().put(fileId, highlights);
      }
    }

    if (searchHit != null && searchHit.innerHits() != null && searchHit.innerHits().get("file") != null) {
      for (InnerHitsResult innerhit : searchHit.innerHits().get("file").) {
        List<String> highlights = new ArrayList<>();
        innerhit.hits().hits().
        for (Entry<String, HighlightField> highlight : innerhit.getHighlightFields().entrySet()) {
          if (highlight.getValue() != null && highlight.getValue().getFragments() != null) {
            for (Text t : highlight.getValue().getFragments()) {
              highlights.add(t.toString());
            }
          }
        }
        String fileId = ((Map<String, Object>) innerhit.getSourceAsMap().get("fileData")).get("fileId").toString();
        getHighlightMap().put(fileId, highlights);
      }
    }

     */
  }

  public void initFileBeans() {
    if (this.getFiles().isEmpty()) {
      return;
    }

    this.fileBeanList = new ArrayList<FileBean>();
    this.locatorBeanList = new ArrayList<FileBean>();

    for (final FileDbVO file : this.getFiles()) {
      // add locators
      if (file.getStorage() == FileDbVO.Storage.EXTERNAL_URL) {
        this.locatorBeanList.add(new FileBean(file, this));
      }
      // add files
      else {
        if (this.getHighlightMap().containsKey(file.getObjectId())) {
          this.fileBeanList.add(new FileBean(file, this, getHighlightMap().get(file.getObjectId())));
        } else {
          this.fileBeanList.add(new FileBean(file, this));
        }
      }
    }
  }

  public boolean getSelected() {
    return this.selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public boolean getShortView() {
    return this.shortView;
  }

  public void setShortView(boolean shortView) {
    this.shortView = shortView;
  }

  public boolean getMediumView() {
    return !this.shortView;
  }

  public void setMediumView(boolean mediumView) {
    this.shortView = !mediumView;
  }

  /**
   * Adds an empty abstract after the current one
   */
  public void addAbstractAtIndex(int index) {
    if (this.getMetadata().getAbstracts() != null && !this.getMetadata().getAbstracts().isEmpty()) {
      this.getMetadata().getAbstracts().add((index + 1), new AbstractVO());
    }
  }

  /**
   * Removes an abstract title from the current position
   */
  public void removeAbstractAtIndex(int index) {
    if (this.getMetadata().getAbstracts() != null && !this.getMetadata().getAbstracts().isEmpty()) {
      this.getMetadata().getAbstracts().remove(index);
    }
  }

  /**
   * Adds the first alternative title with no content
   */
  public void addAlternativeTitle() {
    if (this.getMetadata().getAlternativeTitles() == null || this.getMetadata().getAlternativeTitles().isEmpty()) {
      this.getMetadata().getAlternativeTitles().add(new AlternativeTitleVO());
    }
  }

  /**
   * Adds an empty alternative title after the current one
   */
  public void addAlternativeTitleAtIndex(int index) {
    if (this.getMetadata().getAlternativeTitles() != null && !this.getMetadata().getAlternativeTitles().isEmpty()) {
      this.getMetadata().getAlternativeTitles().add((index + 1), new AlternativeTitleVO());
    }
  }

  /**
   * Removes an alternative title form the current position
   */
  public void removeAlternativeTitleAtIndex(int index) {
    if (this.getMetadata().getAlternativeTitles() != null && !this.getMetadata().getAlternativeTitles().isEmpty()) {
      this.getMetadata().getAlternativeTitles().remove(index);
    }
  }

  /**
   * localized creation of SelectItems for the identifier types available
   * 
   * @return SelectItem[] with Strings representing identifier types
   */
  public SelectItem[] getAlternativeTitleTypes() {
    final ArrayList<SelectItem> selectItemList = new ArrayList<SelectItem>();

    // constants for comboBoxes
    selectItemList.add(new SelectItem("", this.getLabel("EditItem_NO_ITEM_SET")));

    for (final SourceVO.AlternativeTitleType type : SourceVO.AlternativeTitleType.values()) {
      selectItemList.add(new SelectItem(type.toString(), this.getLabel(("ENUM_ALTERNATIVETITLETYPE_" + type.toString()))));
    }

    return selectItemList.toArray(new SelectItem[] {});
  }

  /**
   * Adds an empty subject after the current one
   */
  public void addSubjectAtIndex(int index) {
    if (this.getMetadata().getSubjects() != null && !this.getMetadata().getSubjects().isEmpty()) {
      this.getMetadata().getSubjects().add((index + 1), new SubjectVO());
    }
  }

  /**
   * Removes an alternative title from the current position
   */
  public void removeSubjectAtIndex(int index) {
    if (this.getMetadata().getSubjects() != null && !this.getMetadata().getSubjects().isEmpty()) {
      this.getMetadata().getSubjects().remove(index);
    }
  }

  /**
   * Distinguish between Persons and organization as creators and returns them formatted as string.
   * 
   * @return String the formatted creators
   */
  public String getCreators() {
    if (this.getMetadata() != null) {
      final int creatorsNo = this.getMetadata().getCreators().size();
      return this.getCreators(creatorsNo);
    }

    return "";
  }

  /**
   * Formats the display of the creators (internal use only, used for different views)
   * 
   * @return String the formatted creators
   */

  private String getCreators(int creatorMaximum) {
    final StringBuffer creators = new StringBuffer();
    for (int i = 0; i < creatorMaximum; i++) {
      if (this.getMetadata() != null) {
        if (this.getMetadata().getCreators().get(i).getPerson() != null) {
          if (this.getMetadata().getCreators().get(i).getPerson().getFamilyName() != null) {
            creators.append(this.getMetadata().getCreators().get(i).getPerson().getFamilyName());
            if (this.getMetadata().getCreators().get(i).getPerson().getGivenName() != null) {
              creators.append(", ");
              creators.append(this.getMetadata().getCreators().get(i).getPerson().getGivenName());
            }
          }
        } else if (this.getMetadata().getCreators().get(i).getOrganization() != null) {
          creators.append(this.getMetadata().getCreators().get(i).getOrganization().getName());
        }
      }

      if (i < creatorMaximum - 1) {
        creators.append("; ");
      }
    }

    return creators.toString();
  }


  public String getCreatorsShort() {
    final int creatorsMax = 4;
    int creatorsNo = 0;

    if (this.getMetadata() != null) {
      creatorsNo = this.getMetadata().getCreators().size();
    }

    String creators;
    if (creatorsNo <= creatorsMax) {
      creators = this.getCreators(creatorsNo);
    } else {
      creators = this.getCreators(creatorsMax);
      creators = creators.toString() + " ...";
    }

    return creators;
  }

  /**
   * Delivers all creators, which are part of the OU given in the properties
   */
  public List<CreatorVO> getOrganizationsAuthors() {
    final List<CreatorVO> creators = this.getMetadata().getCreators();
    final List<CreatorVO> mpgCreators = new ArrayList<CreatorVO>();
    final String rootOrganization = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_ROOT_ORGANIZATION_NAME);
    boolean isPartOfTheOrganization = false;

    if (rootOrganization != null && !rootOrganization.isEmpty()) {
      for (final CreatorVO creator : creators) {
        if (CreatorType.PERSON.equals(creator.getType()) && creator.getPerson().getOrganizations() != null
            && !CreatorRole.REFEREE.equals(creator.getRole()) && !CreatorRole.ADVISOR.equals(creator.getRole())
            && !CreatorRole.HONOREE.equals(creator.getRole())) {
          for (final OrganizationVO organization : creator.getPerson().getOrganizations()) {
            if (organization.getName().toString().contains(rootOrganization)) {
              isPartOfTheOrganization = true;
            }
          }
          if (isPartOfTheOrganization) {
            mpgCreators.add(creator);
            isPartOfTheOrganization = false;
          }
        }
      }
      return mpgCreators;
    } else {
      return creators;
    }
  }


  /**
   * Delivers all creators
   */
  public List<CreatorVO> getAllAuthors() {
    final List<CreatorVO> creators = this.getMetadata().getCreators();
    return creators;
  }

  /**
   * Returns the newest date of the metadata date section.
   * 
   * @return the latest date
   */
  public String getLatestDate() {
    if (this.getMetadata().getDatePublishedInPrint() != null && !"".equals(this.getMetadata().getDatePublishedInPrint())) {
      return this.getMetadata().getDatePublishedInPrint() + ", " + this.getLabel("ViewItem_lblDatePublishedInPrint");
    }

    if (this.getMetadata().getDatePublishedOnline() != null && !"".equals(this.getMetadata().getDatePublishedOnline())) {
      return this.getMetadata().getDatePublishedOnline() + ", " + this.getLabel("ViewItem_lblDatePublishedOnline");
    }

    if (this.getMetadata().getDateAccepted() != null && !"".equals(this.getMetadata().getDateAccepted())) {
      return this.getMetadata().getDateAccepted() + ", " + this.getLabel("ViewItem_lblDateAccepted");
    }

    if (this.getMetadata().getDateSubmitted() != null && !"".equals(this.getMetadata().getDateSubmitted())) {
      return this.getMetadata().getDateSubmitted() + ", " + this.getLabel("ViewItem_lblDateSubmitted");
    }

    if (this.getMetadata().getDateModified() != null && !"".equals(this.getMetadata().getDateModified())) {
      return this.getMetadata().getDateModified() + ", " + this.getLabel("ViewItem_lblDateModified");
    }

    if (this.getMetadata().getDateCreated() != null && !"".equals(this.getMetadata().getDateCreated())) {
      return this.getMetadata().getDateCreated() + ", " + this.getLabel("ViewItem_lblDateCreated");
    }

    return null;
  }

  public String getDatesAsString() {
    if ((this.getMetadata().getDateAccepted() == null) && (this.getMetadata().getDateCreated() == null)
        && (this.getMetadata().getDateModified() == null) && (this.getMetadata().getDatePublishedInPrint() == null)
        && (this.getMetadata().getDatePublishedOnline() == null) && (this.getMetadata().getDateSubmitted() == null)) {
      return "";
    }

    final ArrayList<String> dates = new ArrayList<String>();

    if (this.getMetadata().getDateCreated() != null && !this.getMetadata().getDateCreated().equals("")) {
      dates.add(this.getLabel("ViewItem_lblDateCreated") + ": " + this.getMetadata().getDateCreated());
    }
    if (this.getMetadata().getDateModified() != null && !this.getMetadata().getDateModified().equals("")) {
      dates.add(this.getLabel("ViewItem_lblDateModified") + ": " + this.getMetadata().getDateModified());
    }
    if (this.getMetadata().getDateSubmitted() != null && !this.getMetadata().getDateSubmitted().equals("")) {
      dates.add(this.getLabel("ViewItem_lblDateSubmitted") + ": " + this.getMetadata().getDateSubmitted());
    }
    if (this.getMetadata().getDateAccepted() != null && !this.getMetadata().getDateAccepted().equals("")) {
      dates.add(this.getLabel("ViewItem_lblDateAccepted") + ": " + this.getMetadata().getDateAccepted());
    }
    if (this.getMetadata().getDatePublishedOnline() != null && !this.getMetadata().getDatePublishedOnline().equals("")) {
      dates.add(this.getLabel("ViewItem_lblDatePublishedOnline") + ": " + this.getMetadata().getDatePublishedOnline());
    }

    if (this.getMetadata().getDatePublishedInPrint() != null && !this.getMetadata().getDatePublishedInPrint().equals("")) {
      dates.add(this.getLabel("ViewItem_lblDatePublishedInPrint") + ": " + this.getMetadata().getDatePublishedInPrint());
    }

    String allDates = "";

    for (final String date : dates) {
      allDates = allDates + date + " | ";
    }

    // remove last two signs
    if (allDates.length() > 2) {
      allDates = allDates.substring(0, allDates.length() - 2);
    }

    return allDates;
  }

  public String getFormattedLatestReleaseModificationDate() {
    if (this.getObject().getLatestRelease().getModificationDate() != null) {
      return CommonUtils.format(this.getObject().getLatestRelease().getModificationDate());
    }

    return "-";
  }

  /**
   * gets the genre of the item
   * 
   * @return String the genre of the item
   */
  public String getGenre() {
    if (this.getMetadata().getGenre() != null) {
      return this.getLabel(this.i18nHelper.convertEnumToString(this.getMetadata().getGenre()));
    }

    return "";
  }

  /**
   * gets the genre group of the item
   * 
   * @return String the genre group of the item
   */
  public String getGenreGroup() {
    return ResourceBundle.getBundle("Genre_" + this.getMetadata().getGenre()).getString("genre_group_value");
  }

  /**
   * gets the genre of the first source of the item
   * 
   * @return String the genre of the source
   */
  public String getSourceGenre() {
    if (this.firstSource != null && this.firstSource.getGenre() != null) {
      return this.getLabel(this.i18nHelper.convertEnumToString(this.firstSource.getGenre()));
    }

    return "";
  }

  /**
   * Returns a formatted String containing the start and the end page of the source
   * 
   * @return String the formatted start and end page
   */
  public String getStartEndPageSource() {
    if (this.firstSource == null) {
      return "";
    }

    final StringBuffer startEndPage = new StringBuffer();
    if (this.firstSource.getStartPage() != null) {
      startEndPage.append(this.firstSource.getStartPage());
    }

    if (this.firstSource.getEndPage() != null && !this.firstSource.getEndPage().trim().equals("")) {
      startEndPage.append(" - ");
      startEndPage.append(this.firstSource.getEndPage());
    }

    return startEndPage.toString();
  }

  /**
   * Returns the formatted object PID for presentation (without leading "hdl:")
   * 
   * @return pid (String) the object PID without leading "hdl:"
   */
  public String getObjectPidWithoutPrefix() {
    final String pid = this.getVersionPid();
    if (pid.startsWith(PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_SHORT))) {
      return pid.substring(PropertyReader.getProperty(PropertyReader.INGE_PID_HANDLE_SHORT).length());
    } else {
      return pid;
    }
  }

  /**
   * Returns the formatted Publishing Info according to filled elements
   * 
   * @return String the formatted Publishing Info
   */
  public String getPublishingInfo() {
    if (this.getMetadata().getPublishingInfo() == null) {
      return "";
    }

    final StringBuffer publishingInfo = new StringBuffer();
    publishingInfo.append("");

    // Place
    if (this.getMetadata().getPublishingInfo().getPlace() != null) {
      publishingInfo.append(this.getMetadata().getPublishingInfo().getPlace().trim());
    }

    // colon
    if (this.getMetadata().getPublishingInfo().getPublisher() != null
        && !this.getMetadata().getPublishingInfo().getPublisher().trim().equals("")
        && this.getMetadata().getPublishingInfo().getPlace() != null
        && !this.getMetadata().getPublishingInfo().getPlace().trim().equals("")) {
      publishingInfo.append(" : ");
    }

    // Publisher
    if (this.getMetadata().getPublishingInfo().getPublisher() != null) {
      publishingInfo.append(this.getMetadata().getPublishingInfo().getPublisher().trim());
    }

    // Comma
    if ((this.getMetadata().getPublishingInfo().getEdition() != null
        && !this.getMetadata().getPublishingInfo().getEdition().trim().equals(""))
        && ((this.getMetadata().getPublishingInfo().getPlace() != null
            && !this.getMetadata().getPublishingInfo().getPlace().trim().equals(""))
            || (this.getMetadata().getPublishingInfo().getPublisher() != null
                && !this.getMetadata().getPublishingInfo().getPublisher().trim().equals("")))) {
      publishingInfo.append(", ");
    }

    // Edition
    if (this.getMetadata().getPublishingInfo().getEdition() != null) {
      publishingInfo.append(this.getMetadata().getPublishingInfo().getEdition());
    }

    return publishingInfo.toString();
  }

  /**
   * Returns the formatted Publishing Info of the source(!!!) according to filled elements
   * 
   * @return String the formatted Publishing Info of the source
   */
  public String getPublishingInfoSource() {
    if (this.firstSource == null) {
      return "";
    }

    final StringBuffer publishingInfoSource = new StringBuffer();

    publishingInfoSource.append("");
    if (this.firstSource.getPublishingInfo() != null) {
      // Place
      if (this.firstSource.getPublishingInfo().getPlace() != null) {
        publishingInfoSource.append(this.firstSource.getPublishingInfo().getPlace().trim());
      }

      // colon
      if (this.firstSource.getPublishingInfo().getPublisher() != null
          && !this.firstSource.getPublishingInfo().getPublisher().trim().equals("")
          && this.firstSource.getPublishingInfo().getPlace() != null
          && !this.firstSource.getPublishingInfo().getPlace().trim().equals("")) {
        publishingInfoSource.append(" : ");
      }

      // Publisher
      if (this.firstSource.getPublishingInfo().getPublisher() != null) {
        publishingInfoSource.append(this.firstSource.getPublishingInfo().getPublisher().trim());
      }

      // Comma
      if ((this.firstSource.getPublishingInfo().getEdition() != null
          && !this.firstSource.getPublishingInfo().getEdition().trim().equals(""))
          && ((this.firstSource.getPublishingInfo().getPlace() != null
              && !this.firstSource.getPublishingInfo().getPlace().trim().equals(""))
              || (this.firstSource.getPublishingInfo().getPublisher() != null
                  && !this.firstSource.getPublishingInfo().getPublisher().trim().equals("")))) {
        publishingInfoSource.append(", ");
      }

      // Edition
      if (this.firstSource.getPublishingInfo().getEdition() != null) {
        publishingInfoSource.append(this.firstSource.getPublishingInfo().getEdition());
      }
    }

    return publishingInfoSource.toString();
  }

  /**
   * Returns the event title (50 Chars) and crops the last characters
   * 
   * @return String the event title
   */
  public String getEventTitle() {
    String eventTitle = "";
    if (this.getMetadata().getEvent() != null && this.getMetadata().getEvent().getTitle() != null
        && !this.getMetadata().getEvent().getTitle().trim().equals("")) {
      if (this.getMetadata().getEvent().getTitle().length() > 50) {
        eventTitle = this.getMetadata().getEvent().getTitle().substring(0, 49) + "...";
      } else {
        eventTitle = this.getMetadata().getEvent().getTitle();
      }
    }

    return eventTitle;
  }

  /**
   * Returns the title (80 Chars) and crops the last characters. Specification says 100 chars, but
   * this is too long, 50 is too short.
   * 
   * @return String the title
   */
  public String getShortTitle() {
    if (this.getMetadata().getTitle() != null && !this.getMetadata().getTitle().trim().equals("")) {
      if (this.getMetadata().getTitle().length() > 80) {
        return this.getMetadata().getTitle().substring(0, 79) + "...";
      }

      return this.getMetadata().getTitle();
    }

    return null;
  }

  /**
   * Returns the first abstract (150 Chars) and crops the last characters. Specification not
   * available!
   * 
   * @return String the title
   */
  public String getShortAbstract() {
    if (this.getMetadata().getAbstracts().size() > 0) {
      if (this.getMetadata().getAbstracts().get(0) != null && this.getMetadata().getAbstracts().get(0).getValue() != null
          && this.getMetadata().getAbstracts().get(0).getValue().length() > 150) {
        return this.getMetadata().getAbstracts().get(0).getValue().substring(0, 149) + "...";
      }

      else if (this.getMetadata().getAbstracts().get(0) != null && this.getMetadata().getAbstracts().get(0).getValue() != null)
        return this.getMetadata().getAbstracts().get(0).getValue();
    }

    return null;
  }

  /**
   * @return String the title
   */
  public String getFullTitle() {
    if (this.getMetadata() != null && this.getMetadata().getTitle() != null) {
      return this.getMetadata().getTitle();
    }

    return "#### NO TITLE!!! ####";
  }


  /**
   * Returns the source title (50 Chars) of the first source and crops the last characters
   * 
   * @return String the event title
   */
  public String getSourceTitle() {
    String sourceTitle = "";
    if (this.firstSource != null && this.firstSource.getTitle() != null && !this.firstSource.getTitle().trim().equals("")) {
      if (this.firstSource.getTitle().length() > 50) {
        sourceTitle = this.firstSource.getTitle().substring(0, 49) + "...";
      } else {
        sourceTitle = this.firstSource.getTitle();
      }
    }

    return sourceTitle;
  }

  /**
   * This method examines the pubitem concerning its files and generates a display string for the
   * page according to the number of files detected.
   *
   * @return String the formatted String to display the occurencies of files
   */
  public String getFileInfo() {
    if (this.getFileList() == null) {
      return "";
    }

    final StringBuffer files = new StringBuffer();
    files.append(this.getFileList().size());

    // if there is only 1 file, display "File attached", otherwise display "Files attached" (plural)
    if (this.getFileList().size() == 1) {
      files.append(" " + this.getLabel("ViewItemShort_lblFileAttached"));
    } else {
      files.append(" " + this.getLabel("ViewItemShort_lblFilesAttached"));
    }

    return files.toString();
  }

  /**
   * This method examines the pubitem concerning its locators and generates a display string for the
   * page according to the number of locators detected.
   *
   * @return String the formatted String to display the occurencies of locators
   */
  public String getLocatorInfo() {
    if (this.getLocatorList() == null) {
      return "";
    }

    final StringBuffer locators = new StringBuffer();
    locators.append(this.getLocatorList().size());

    // if there is only 1 locator, display "Locator", otherwise display "Locators" (plural)
    if (this.getLocatorList().size() == 1) {
      locators.append(" " + this.getLabel("ViewItemShort_lblLocatorAttached"));
    } else {
      locators.append(" " + this.getLabel("ViewItemShort_lblLocatorsAttached"));
    }

    return locators.toString();
  }

  /**
   * This method examines which file is really a file and not a locator and returns a list of native
   * files
   * 
   * @return List<FileDbVO> file list
   */
  private List<FileDbVO> getFileList() {
    List<FileDbVO> fileList = null;
    if (this.getFiles() != null) {
      fileList = new ArrayList<FileDbVO>();
      for (int i = 0; i < this.getFiles().size(); i++) {
        if (this.getFiles().get(i).getStorage() == FileDbVO.Storage.INTERNAL_MANAGED) {
          fileList.add(this.getFiles().get(i));
        }
      }
    }

    return fileList;
  }

  public int getNumberOfFiles() {
    return this.getFileList().size();
  }

  /**
   * This method examines which file is a locator and not a file and returns a list of locators
   * 
   * @return List<FileDbVO> locator list
   */
  private List<FileDbVO> getLocatorList() {
    List<FileDbVO> locatorList = null;
    if (this.getFiles() != null) {
      locatorList = new ArrayList<FileDbVO>();
      for (int i = 0; i < this.getFiles().size(); i++) {
        if (this.getFiles().get(i).getStorage() == FileDbVO.Storage.EXTERNAL_URL) {
          locatorList.add(this.getFiles().get(i));
        }
      }
    }

    return locatorList;
  }

  public int getNumberOfLocators() {
    return this.getLocatorList().size();
  }

  /**
   * Counts the files and gives info back as int
   * 
   * @return int the amount of files belonging to this item
   */
  public int getAmountOfFiles() {
    if (this.getFileList() != null) {
      return this.getFileList().size();
    }

    return 0;
  }

  /**
   * Counts the locators and gives info back as int
   * 
   * @return int the amount of locators belonging to this item
   */
  public int getAmountOfLocators() {
    if (this.getLocatorList() != null) {
      return this.getLocatorList().size();
    }

    return 0;
  }

  /**
   * Counts the sources of the current item
   * 
   * @return int number of sources
   */
  public int getFurtherSources() {
    // get the number of sources (if bigger than 1) minus the first one
    if (this.getMetadata().getSources() != null && this.getMetadata().getSources().size() > 1) {
      return this.getMetadata().getSources().size() - 1;
    }

    return 0;
  }

  /**
   * Counts the creators and returns the number as int (inportant for rendering in )
   * 
   * @return int number of creators
   */
  public int getCountCreators() {
    int creators = 0;
    if (this.creatorArray != null) {
      creators = creators + this.creatorArray.size();
    }
    if (this.creatorOrganizationsArray != null) {
      creators = creators + this.creatorOrganizationsArray.size();
    }

    return creators;
  }

  /**
   * Counts the affiliated organizations and returns the number as int (inportant for rendering in )
   * 
   * @return int number of organiozations
   */
  public int getCountAffiliatedOrganizations() {
    if (this.affiliatedOrganizationsList != null) {
      return this.affiliatedOrganizationsList.size();
    }

    return 0;
  }


  public void switchToMediumView() {
    this.shortView = false;
  }

  public void switchToShortView() {
    this.shortView = true;
  }

  public void select(ValueChangeEvent event) {
    this.selected = ((Boolean) event.getNewValue()).booleanValue();
  }

  public String getLink() throws Exception {
    if (this != null && this.getObjectId() != null) {
      return CommonUtils.getGenericItemLink(this.getObjectId(), this.getVersionNumber());
    }

    return null;
  }

  public String getLinkLatestRelease() throws Exception {
    if (this.getObject().getLatestRelease() != null && this.getObject().getLatestRelease().getObjectId() != null) {
      return CommonUtils.getGenericItemLink(this.getObject().getLatestRelease().getObjectId(),
          this.getObject().getLatestRelease().getVersionNumber());
    }

    return null;
  }

  public boolean getShowCheckbox() {
    return true;
  }

  public void writeBackLocalTags() {
    this.getObject().getLocalTags().clear();
    for (final WrappedLocalTag wrappedLocalTag : this.getWrappedLocalTags()) {
      this.getObject().getLocalTags().add(wrappedLocalTag.getValue());
    }
  }

  /**
   * This method return the public state of the current item
   * 
   * @author Tobias Schraut
   * @return String public state of the current item
   */
  public String getItemPublicState() {
    if (this.getObject().getPublicState() != null) {
      return this.getLabel(this.i18nHelper.convertEnumToString(this.getObject().getPublicState()));
    }

    return "";
  }

  /**
   * This method return the state of the current item version
   * 
   * @author Tobias Schraut
   * @return String state of the current item version
   */
  public String getItemState() {
    if (this.getVersionState() != null) {
      return this.getLabel(this.i18nHelper.convertEnumToString(this.getVersionState()));
    }

    return "";
  }

  /**
   * This method return true if the item is withdrawn, otherwise false
   * 
   * @author Tobias Schraut
   * @return Boolean true if item is withdrawn
   */
  public boolean getIsStateWithdrawn() {
    return ItemVersionRO.State.WITHDRAWN.equals(this.getObject().getPublicState());
  }

  /**
   * This method return true if the item is submitted, otherwise false
   * 
   * @author Tobias Schraut
   * @return Boolean true if item is submitted
   */
  public boolean getIsStateSubmitted() {
    return ItemVersionRO.State.SUBMITTED.equals(this.getVersionState());
  }

  /**
   * This method return true if the item is released, otherwise false
   * 
   * @author Tobias Schraut
   * @return Boolean true if item is released
   */
  public boolean getIsStateReleased() {
    return ItemVersionRO.State.RELEASED.equals(this.getVersionState());
  }

  /**
   * This method return true if the item is pending, otherwise false
   * 
   * @author Tobias Schraut
   * @return Boolean true if item is pending
   */
  public boolean getIsStatePending() {
    return ItemVersionRO.State.PENDING.equals(this.getVersionState());
  }

  /**
   * This method return true if the item is in revision, otherwise false
   * 
   * @author Tobias Schraut
   * @return Boolean true if item is in revision
   */
  public boolean getIsStateInRevision() {
    return ItemVersionRO.State.IN_REVISION.equals(this.getVersionState());
  }

  public ArrayList<String> getOrganizationArray() {
    return this.organizationArray;
  }

  public void setOrganizationArray(ArrayList<String> organizationArray) {
    this.organizationArray = organizationArray;
  }

  public ArrayList<ViewItemOrganization> getOrganizationList() {
    return this.organizationList;
  }

  public void setOrganizationList(ArrayList<ViewItemOrganization> organizationList) {
    this.organizationList = organizationList;
  }

  public List<OrganizationVO> getAffiliatedOrganizationsList() {
    return this.affiliatedOrganizationsList;
  }

  public void setAffiliatedOrganizationsList(List<OrganizationVO> affiliatedOrganizationsList) {
    this.affiliatedOrganizationsList = affiliatedOrganizationsList;
  }

  public ArrayList<String> getCreatorArray() {
    return this.creatorArray;
  }

  public void setCreatorArray(ArrayList<String> creatorArray) {
    this.creatorArray = creatorArray;
  }

  public ArrayList<ViewItemCreatorOrganization> getCreatorOrganizationsArray() {
    return this.creatorOrganizationsArray;
  }

  public void setCreatorOrganizationsArray(ArrayList<ViewItemCreatorOrganization> creatorOrganizationsArray) {
    this.creatorOrganizationsArray = creatorOrganizationsArray;
  }

  public SourceVO getFirstSource() {
    return this.firstSource;
  }

  public void setFirstSource(SourceVO firstSource) {
    this.firstSource = firstSource;
  }

  public ArrayList<String> getAllCreatorsList() {
    return this.allCreatorsList;
  }

  public void setAllCreatorsList(ArrayList<String> allCreatorsList) {
    this.allCreatorsList = allCreatorsList;
  }

  public boolean isSearchResult() {
    return this.isSearchResult;
  }

  public void setSearchResult(boolean isSearchResult) {
    this.isSearchResult = isSearchResult;
  }

  public boolean getIsFromEasySubmission() {
    return this.isFromEasySubmission;
  }

  public void setFromEasySubmission(boolean isFromEasySubmission) {
    this.isFromEasySubmission = isFromEasySubmission;
  }

  public boolean getIsReleased() {
    return this.released;
  }

  public void setReleased(boolean released) {
    this.released = released;
  }

  public List<WrappedLocalTag> getWrappedLocalTags() {
    return this.wrappedLocalTags;
  }

  public int getNumberOfWrappedLocalTags() {
    return this.wrappedLocalTags.size();
  }

  public void setWrappedLocalTags(List<WrappedLocalTag> wrappedLocalTags) {
    this.wrappedLocalTags = wrappedLocalTags;
  }

  public class WrappedLocalTag implements Serializable {
    private String value;
    private PubItemVOPresentation parent;

    public String getValue() {
      return this.value;
    }

    public void setValue(String value) {
      this.value = value;
    }

    public PubItemVOPresentation getParent() {
      return this.parent;
    }

    public void setParent(PubItemVOPresentation parent) {
      this.parent = parent;
    }

    public void removeLocalTag() {
      this.parent.getWrappedLocalTags().remove(this);
      this.parent.writeBackLocalTags();
    }

    public boolean getIsLast() {
      return (this == this.parent.getWrappedLocalTags().get(this.parent.getWrappedLocalTags().size() - 1));
    }

    public int getNumberOfAllTags() {
      return (this.parent.getWrappedLocalTags().size());
    }
  }

  public String getIdentifier() throws Exception {
    final String id = this.getLink().toString();
    final String[] idSplit = id.split("/");

    return idSplit[idSplit.length - 1];
  }

  public void setScore(float score) {
    this.score = score;
  }

  public float getScore() {
    return this.score;
  }

  public void setFileBeanList(List<FileBean> fileBeanList) {
    this.fileBeanList = fileBeanList;
  }

  public List<FileBean> getFileBeanList() {
    return this.fileBeanList;
  }

  /**
   * Delivers the FileBeans for all Files which have the content-category fulltext
   * 
   * @return List<FileBeans> which have the content-category fulltext
   */
  public List<FileBean> getFulltextFileBeanList() {
    final List<FileBean> fulltexts = new ArrayList<FileBean>();
    if (this.fileBeanList != null) {
      for (final FileBean file : this.fileBeanList) {
        if ("any-fulltext".equals(file.getContentCategory())) {
          fulltexts.add(file);
        }
      }
    }

    return fulltexts;
  }

  /**
   * Delivers the FileBeans for all files which are publicly accessible and have content category
   * "any-fulltext" / "postprint" / "preprint" / "publisher-version"
   * 
   * @return List<FileBeans> which are public accessible and have content category "any-fulltext" /
   *         "postprint" / "preprint" / "publisher-version"
   */
  public List<FileBean> getPubliclyAccessibleFulltextFileBeanList() {
    final List<FileBean> fulltexts = new ArrayList<FileBean>();
    if (this.fileBeanList != null) {
      for (final FileBean file : this.fileBeanList) {
        if (FileDbVO.Visibility.PUBLIC.equals(file.getFile().getVisibility())
            && ("any-fulltext".equals(file.getContentCategory()) || "pre-print".equals(file.getContentCategory())
                || "post-print".equals(file.getContentCategory()) || "publisher-version".equals(file.getContentCategory()))) {
          fulltexts.add(file);
        }
      }
    }

    return fulltexts;
  }

  /**
   * Delivers the FileBeans for all Files which have the content-category supplementary material
   * 
   * @return List<FileBeans> which have the content-category supplementary material
   */
  public List<FileBean> getSupplementaryMaterialFileBeanList() {
    final List<FileBean> supplementaryMaterial = new ArrayList<FileBean>();
    if (this.fileBeanList != null) {
      for (final FileBean file : this.fileBeanList) {
        if ("supplementary-material".equals(file.getContentCategory()) || "multimedia".equals(file.getContentCategory())
            || "research-data".equals(file.getContentCategory()) || "code".equals(file.getContentCategory())) {
          supplementaryMaterial.add(file);
        }
      }
    }

    return supplementaryMaterial;
  }

  /**
   * Delivers the FileBeans for all files which are restriced for the current user accessible and
   * have content category "any-fulltext" / "postprint" / "preprint" / "publisher-version"
   * 
   * @return List<FileBeans> which are restriced accessible for the current user and have content
   *         category "any-fulltext" / "postprint" / "preprint" / "publisher-version"
   */
  public List<FileBean> getRestrictedAccessibleFulltextFileBeanList() {
    final List<FileBean> fulltexts = new ArrayList<FileBean>();
    if (this.fileBeanList != null) {
      for (final FileBean file : this.fileBeanList) {
        if (FileDbVO.Visibility.AUDIENCE.equals(file.getFile().getVisibility())
            && ("any-fulltext".equals(file.getContentCategory()) || "pre-print".equals(file.getContentCategory())
                || "post-print".equals(file.getContentCategory()) || "publisher-version".equals(file.getContentCategory()))) {
          if (file.isFileAccessGranted()) {
            fulltexts.add(file);
          }
        }
      }
    }

    return fulltexts;
  }

  /**
   * Delivers the FileBeans for all files which are publicly accessible and have content category
   * "supplementary-material"
   * 
   * @return List<FileBeans> which are public accessible and have content category "any-fulltext" /
   *         "postprint" / "preprint" / "publisher-version"
   */
  public List<FileBean> getPubliclyAccessibleSupplementaryMaterialFileBeanList() {
    final List<FileBean> supplementaryMaterial = new ArrayList<FileBean>();
    if (this.fileBeanList != null) {
      for (final FileBean file : this.fileBeanList) {
        if (FileDbVO.Visibility.PUBLIC.equals(file.getFile().getVisibility())
            && ("supplementary-material".equals(file.getContentCategory()) || "multimedia".equals(file.getContentCategory())
                || "research-data".equals(file.getContentCategory()) || "code".equals(file.getContentCategory()))) {
          supplementaryMaterial.add(file);
        }
      }
    }

    return supplementaryMaterial;
  }

  public void setLocatorBeanList(List<FileBean> locatorBeanList) {
    this.locatorBeanList = locatorBeanList;
  }

  public List<FileBean> getLocatorBeanList() {
    return this.locatorBeanList;
  }

  public String getDescriptionMetaTag() {
    final List<CreatorVO> creators = this.getMetadata().getCreators();

    if (creators.size() > 0) {
      this.descriptionMetaTag = this.getLabel("ENUM_CREATORROLE_" + creators.get(0).getRoleString()) + ": ";
      if (creators.get(0).getPerson() != null) {
        this.descriptionMetaTag += creators.get(0).getPerson().getFamilyName() + ", " + creators.get(0).getPerson().getGivenName();
      } else {
        this.descriptionMetaTag += creators.get(0).getOrganization().getName();
      }
    }

    if (creators.size() > 1) {
      this.descriptionMetaTag += " et al.";
    }

    // add genre information
    this.descriptionMetaTag +=
        "; " + this.getLabel("ViewItemFull_lblGenre") + ": " + this.getLabel("ENUM_GENRE_" + this.getMetadata().getGenre());

    // add published print date
    if (this.getMetadata().getDatePublishedInPrint() != null && this.getMetadata().getDatePublishedInPrint() != "") {
      this.descriptionMetaTag +=
          "; " + this.getLabel("ViewItemShort_lblDatePublishedInPrint") + ": " + this.getMetadata().getDatePublishedInPrint();
    }
    // add published online date if no publisched print date
    else if (this.getMetadata().getDatePublishedOnline() != null && this.getMetadata().getDatePublishedOnline() != "") {
      this.descriptionMetaTag +=
          "; " + this.getLabel("ViewItemShort_lblDatePublishedOnline") + ": " + this.getMetadata().getDatePublishedOnline();
    }

    // add open access component
    if (this.getFileBeanList() != null && this.getFileBeanList().size() > 0) {
      for (final FileBean file : this.getFileBeanList()) {
        if (file.getIsVisible() == true) {
          this.descriptionMetaTag += "; Open Access";
          break;
        }
      }
    }

    // add keywords
    if (this.getMetadata().getFreeKeywords() != null && this.getMetadata().getFreeKeywords() != "") {
      this.descriptionMetaTag += "; Keywords: " + this.getMetadata().getFreeKeywords();
    }

    // add title at the end of description meta tag
    if (this.getMetadata().getTitle() != null && this.getMetadata().getTitle() != "") {
      this.descriptionMetaTag += "; " + this.getLabel("ViewItemFull_lblTitle") + ": " + this.getMetadata().getTitle();
    }

    this.descriptionMetaTag = HtmlUtils.removeSubSupIfBalanced(this.descriptionMetaTag);
    this.descriptionMetaTag = CommonUtils.htmlEscape(this.descriptionMetaTag);

    return this.descriptionMetaTag;
  }

  public void setValidationReport(ValidationReportVO validationReport) {
    this.validationReport = validationReport;
  }

  public ValidationReportVO getValidationReport() {
    return this.validationReport;
  }

  public String getPublicationStatus() {
    if (this.getMetadata().getDatePublishedInPrint() != null && !this.getMetadata().getDatePublishedInPrint().isEmpty()) {
      return this.getLabel("ViewItem_lblPublicationState_published-in-print");
    } else if (this.getMetadata().getDatePublishedOnline() != null && !this.getMetadata().getDatePublishedOnline().isEmpty()) {
      return this.getLabel("ViewItem_lblPublicationState_published-online");
    } else if (this.getMetadata().getDateAccepted() != null && !this.getMetadata().getDateAccepted().isEmpty()) {
      return this.getLabel("ViewItem_lblPublicationState_accepted");
    } else if (this.getMetadata().getDateSubmitted() != null && !this.getMetadata().getDateSubmitted().isEmpty()) {
      return this.getLabel("ViewItem_lblPublicationState_submitted");
    } else {
      return this.getLabel("ViewItem_lblPublicationState_not-specified");
    }
  }

  private String getLabel(String placeholder) {
    return this.i18nHelper.getLabel(placeholder);
  }

  public Map<String, List<String>> getHighlightMap() {
    return highlightMap;
  }

  public void setHighlightMap(Map<String, List<String>> highlightMap) {
    this.highlightMap = highlightMap;
  }

  public Hit getSearchHit() {
    return searchHit;
  }

  public void setSearchHit(Hit searchHit) {
    this.searchHit = searchHit;
  }

  public String getJournalMetaData() throws Exception {
    GFZConeBean gfzConeBean = (GFZConeBean) FacesTools.findBean("GFZConeBean");
    return gfzConeBean.getJournalMetaData(this);
  }

  public void setJournalMetaData(String value) {}
}
