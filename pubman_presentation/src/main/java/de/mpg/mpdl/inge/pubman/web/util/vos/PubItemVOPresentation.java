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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.InnerHitsResult;
import co.elastic.clients.json.JsonData;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AbstractVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AlternativeTitleVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SubjectVO;
import de.mpg.mpdl.inge.model.xmltransforming.util.HtmlUtils;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.InternationalizationHelper;
import de.mpg.mpdl.inge.pubman.web.viewItem.FileBean;
import de.mpg.mpdl.inge.pubman.web.viewItem.ViewItemCreatorOrganization;
import de.mpg.mpdl.inge.pubman.web.viewItem.ViewItemOrganization;
import de.mpg.mpdl.inge.util.PropertyReader;
import jakarta.faces.event.ValueChangeEvent;
import jakarta.faces.model.SelectItem;

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

  private Hit searchHit;

  private Map<String, List<String>> highlightMap = new HashMap<>();

  public PubItemVOPresentation(ItemVersionVO item) {
    this(item, null);
  }

  public PubItemVOPresentation(ItemVersionVO item, Hit searchHit) {
    super(item);
    if (null != this && null != this.getVersionState()) {
      this.released = ItemVersionRO.State.RELEASED.equals(this.getVersionState());
    }

    // get the first source of the item (if available)
    if (null != item.getMetadata() && null != item.getMetadata().getSources() && !item.getMetadata().getSources().isEmpty()) {
      this.firstSource = new SourceVO();
      this.firstSource = item.getMetadata().getSources().get(0);
    }

    // Check the local tags
    if (this.getObject().getLocalTags().isEmpty()) {
      this.getObject().getLocalTags().add("");
    }

    this.wrappedLocalTags = new ArrayList<>();
    for (int i = 0; i < this.getObject().getLocalTags().size(); i++) {
      final WrappedLocalTag wrappedLocalTag = new WrappedLocalTag();
      wrappedLocalTag.setParent(this);
      wrappedLocalTag.setValue(this.getObject().getLocalTags().get(i));
      if (!wrappedLocalTag.getValue().isEmpty() || this.wrappedLocalTags.isEmpty()) {
        this.wrappedLocalTags.add(wrappedLocalTag);
      }
    }

    if (null != searchHit) {
      this.initSearchHits(searchHit);
    }
    this.initFileBeans();
  }

  public void initSearchHits(Hit<Object> hit) {
    this.searchHit = hit;
    this.isSearchResult = true;
    this.score = this.searchHit.score().floatValue();


    if (null != this.searchHit && null != this.searchHit.innerHits() && null != this.searchHit.innerHits().get("file")) {

      InnerHitsResult ihs = hit.innerHits().get("file");

      for (Hit<JsonData> innerHit : hit.innerHits().get("file").hits().hits()) {

        List<String> highlights = new ArrayList<>();
        for (Map.Entry<String, List<String>> highlight : innerHit.highlight().entrySet()) {
          if (null != highlight.getValue() && !highlight.getValue().isEmpty()) {
            highlights.addAll(highlight.getValue());
          }
        }
        String fileId = innerHit.source().toJson().asJsonObject().get("fileData").asJsonObject().getJsonString("fileId").getString();
        getHighlightMap().put(fileId, highlights);

      }

    }


  }

  public void initFileBeans() {
    if (this.getFiles().isEmpty()) {
      return;
    }

    this.fileBeanList = new ArrayList<>();
    this.locatorBeanList = new ArrayList<>();

    for (final FileDbVO file : this.getFiles()) {
      // add locators
      if (FileDbVO.Storage.EXTERNAL_URL == file.getStorage()) {
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
    if (null != this.getMetadata().getAbstracts() && !this.getMetadata().getAbstracts().isEmpty()) {
      this.getMetadata().getAbstracts().add((index + 1), new AbstractVO());
    }
  }

  /**
   * Removes an abstract title from the current position
   */
  public void removeAbstractAtIndex(int index) {
    if (null != this.getMetadata().getAbstracts() && !this.getMetadata().getAbstracts().isEmpty()) {
      this.getMetadata().getAbstracts().remove(index);
    }
  }

  /**
   * Adds the first alternative title with no content
   */
  public void addAlternativeTitle() {
    if (null == this.getMetadata().getAlternativeTitles() || this.getMetadata().getAlternativeTitles().isEmpty()) {
      this.getMetadata().getAlternativeTitles().add(new AlternativeTitleVO());
    }
  }

  /**
   * Adds an empty alternative title after the current one
   */
  public void addAlternativeTitleAtIndex(int index) {
    if (null != this.getMetadata().getAlternativeTitles() && !this.getMetadata().getAlternativeTitles().isEmpty()) {
      this.getMetadata().getAlternativeTitles().add((index + 1), new AlternativeTitleVO());
    }
  }

  /**
   * Removes an alternative title form the current position
   */
  public void removeAlternativeTitleAtIndex(int index) {
    if (null != this.getMetadata().getAlternativeTitles() && !this.getMetadata().getAlternativeTitles().isEmpty()) {
      this.getMetadata().getAlternativeTitles().remove(index);
    }
  }

  /**
   * localized creation of SelectItems for the identifier types available
   *
   * @return SelectItem[] with Strings representing identifier types
   */
  public SelectItem[] getAlternativeTitleTypes() {
    final ArrayList<SelectItem> selectItemList = new ArrayList<>();

    // constants for comboBoxes
    selectItemList.add(new SelectItem("", this.getLabel("EditItem_NO_ITEM_SET")));

    for (final SourceVO.AlternativeTitleType type : SourceVO.AlternativeTitleType.values()) {
      selectItemList.add(new SelectItem(type.toString(), this.getLabel(("ENUM_ALTERNATIVETITLETYPE_" + type))));
    }

    return selectItemList.toArray(new SelectItem[] {});
  }

  /**
   * Adds an empty subject after the current one
   */
  public void addSubjectAtIndex(int index) {
    if (null != this.getMetadata().getSubjects() && !this.getMetadata().getSubjects().isEmpty()) {
      this.getMetadata().getSubjects().add((index + 1), new SubjectVO());
    }
  }

  /**
   * Removes an alternative title from the current position
   */
  public void removeSubjectAtIndex(int index) {
    if (null != this.getMetadata().getSubjects() && !this.getMetadata().getSubjects().isEmpty()) {
      this.getMetadata().getSubjects().remove(index);
    }
  }

  /**
   * Distinguish between Persons and organization as creators and returns them formatted as string.
   *
   * @return String the formatted creators
   */
  public String getCreators() {
    if (null != this.getMetadata()) {
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
    final StringBuilder creators = new StringBuilder();
    for (int i = 0; i < creatorMaximum; i++) {
      if (null != this.getMetadata()) {
        if (null != this.getMetadata().getCreators().get(i).getPerson()) {
          if (null != this.getMetadata().getCreators().get(i).getPerson().getFamilyName()) {
            creators.append(this.getMetadata().getCreators().get(i).getPerson().getFamilyName());
            if (null != this.getMetadata().getCreators().get(i).getPerson().getGivenName()) {
              creators.append(", ");
              creators.append(this.getMetadata().getCreators().get(i).getPerson().getGivenName());
            }
          }
        } else if (null != this.getMetadata().getCreators().get(i).getOrganization()) {
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

    if (null != this.getMetadata()) {
      creatorsNo = this.getMetadata().getCreators().size();
    }

    String creators;
    if (creatorsMax >= creatorsNo) {
      creators = this.getCreators(creatorsNo);
    } else {
      creators = this.getCreators(creatorsMax);
      creators = creators + " ...";
    }

    return creators;
  }

  /**
   * Delivers all creators, which are part of the OU given in the properties
   */
  public List<CreatorVO> getOrganizationsAuthors() {
    final List<CreatorVO> creators = this.getMetadata().getCreators();
    final List<CreatorVO> mpgCreators = new ArrayList<>();
    final String rootOrganization = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_ROOT_ORGANIZATION_NAME);
    boolean isPartOfTheOrganization = false;

    if (null != rootOrganization && !rootOrganization.isEmpty()) {
      for (final CreatorVO creator : creators) {
        if (CreatorVO.CreatorType.PERSON.equals(creator.getType()) && null != creator.getPerson().getOrganizations()
            && !CreatorVO.CreatorRole.REFEREE.equals(creator.getRole()) && !CreatorVO.CreatorRole.ADVISOR.equals(creator.getRole())
            && !CreatorVO.CreatorRole.HONOREE.equals(creator.getRole())) {
          for (final OrganizationVO organization : creator.getPerson().getOrganizations()) {
            if (organization.getName().contains(rootOrganization)) {
              isPartOfTheOrganization = true;
              break;
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
    if (null != this.getMetadata().getDatePublishedInPrint() && !"".equals(this.getMetadata().getDatePublishedInPrint())) {
      return this.getMetadata().getDatePublishedInPrint() + ", " + this.getLabel("ViewItem_lblDatePublishedInPrint");
    }

    if (null != this.getMetadata().getDatePublishedOnline() && !"".equals(this.getMetadata().getDatePublishedOnline())) {
      return this.getMetadata().getDatePublishedOnline() + ", " + this.getLabel("ViewItem_lblDatePublishedOnline");
    }

    if (null != this.getMetadata().getDateAccepted() && !"".equals(this.getMetadata().getDateAccepted())) {
      return this.getMetadata().getDateAccepted() + ", " + this.getLabel("ViewItem_lblDateAccepted");
    }

    if (null != this.getMetadata().getDateSubmitted() && !"".equals(this.getMetadata().getDateSubmitted())) {
      return this.getMetadata().getDateSubmitted() + ", " + this.getLabel("ViewItem_lblDateSubmitted");
    }

    if (null != this.getMetadata().getDateModified() && !"".equals(this.getMetadata().getDateModified())) {
      return this.getMetadata().getDateModified() + ", " + this.getLabel("ViewItem_lblDateModified");
    }

    if (null != this.getMetadata().getDateCreated() && !"".equals(this.getMetadata().getDateCreated())) {
      return this.getMetadata().getDateCreated() + ", " + this.getLabel("ViewItem_lblDateCreated");
    }

    return null;
  }

  public String getDatesAsString() {
    if ((null == this.getMetadata().getDateAccepted()) && (null == this.getMetadata().getDateCreated())
        && (null == this.getMetadata().getDateModified()) && (null == this.getMetadata().getDatePublishedInPrint())
        && (null == this.getMetadata().getDatePublishedOnline()) && (null == this.getMetadata().getDateSubmitted())) {
      return "";
    }

    final ArrayList<String> dates = new ArrayList<>();

    if (null != this.getMetadata().getDateCreated() && !this.getMetadata().getDateCreated().isEmpty()) {
      dates.add(this.getLabel("ViewItem_lblDateCreated") + ": " + this.getMetadata().getDateCreated());
    }
    if (null != this.getMetadata().getDateModified() && !this.getMetadata().getDateModified().isEmpty()) {
      dates.add(this.getLabel("ViewItem_lblDateModified") + ": " + this.getMetadata().getDateModified());
    }
    if (null != this.getMetadata().getDateSubmitted() && !this.getMetadata().getDateSubmitted().isEmpty()) {
      dates.add(this.getLabel("ViewItem_lblDateSubmitted") + ": " + this.getMetadata().getDateSubmitted());
    }
    if (null != this.getMetadata().getDateAccepted() && !this.getMetadata().getDateAccepted().isEmpty()) {
      dates.add(this.getLabel("ViewItem_lblDateAccepted") + ": " + this.getMetadata().getDateAccepted());
    }
    if (null != this.getMetadata().getDatePublishedOnline() && !this.getMetadata().getDatePublishedOnline().isEmpty()) {
      dates.add(this.getLabel("ViewItem_lblDatePublishedOnline") + ": " + this.getMetadata().getDatePublishedOnline());
    }

    if (null != this.getMetadata().getDatePublishedInPrint() && !this.getMetadata().getDatePublishedInPrint().isEmpty()) {
      dates.add(this.getLabel("ViewItem_lblDatePublishedInPrint") + ": " + this.getMetadata().getDatePublishedInPrint());
    }

    String allDates = "";

    for (final String date : dates) {
      allDates = allDates + date + " | ";
    }

    // remove last two signs
    if (2 < allDates.length()) {
      allDates = allDates.substring(0, allDates.length() - 2);
    }

    return allDates;
  }

  public String getFormattedLatestReleaseModificationDate() {
    if (null != this.getObject().getLatestRelease().getModificationDate()) {
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
    if (null != this.getMetadata().getGenre()) {
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
    if (null != this.firstSource && null != this.firstSource.getGenre()) {
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
    if (null == this.firstSource) {
      return "";
    }

    final StringBuilder startEndPage = new StringBuilder();
    if (null != this.firstSource.getStartPage()) {
      startEndPage.append(this.firstSource.getStartPage());
    }

    if (null != this.firstSource.getEndPage() && !this.firstSource.getEndPage().trim().isEmpty()) {
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
    if (null == this.getMetadata().getPublishingInfo()) {
      return "";
    }

    final StringBuilder publishingInfo = new StringBuilder();

    // Place
    if (null != this.getMetadata().getPublishingInfo().getPlace()) {
      publishingInfo.append(this.getMetadata().getPublishingInfo().getPlace().trim());
    }

    // colon
    if (null != this.getMetadata().getPublishingInfo().getPublisher()
        && !this.getMetadata().getPublishingInfo().getPublisher().trim().isEmpty()
        && null != this.getMetadata().getPublishingInfo().getPlace()
        && !this.getMetadata().getPublishingInfo().getPlace().trim().isEmpty()) {
      publishingInfo.append(" : ");
    }

    // Publisher
    if (null != this.getMetadata().getPublishingInfo().getPublisher()) {
      publishingInfo.append(this.getMetadata().getPublishingInfo().getPublisher().trim());
    }

    // Comma
    if ((null != this.getMetadata().getPublishingInfo().getEdition()
        && !this.getMetadata().getPublishingInfo().getEdition().trim().isEmpty())
        && ((null != this.getMetadata().getPublishingInfo().getPlace()
            && !this.getMetadata().getPublishingInfo().getPlace().trim().isEmpty())
            || (null != this.getMetadata().getPublishingInfo().getPublisher()
                && !this.getMetadata().getPublishingInfo().getPublisher().trim().isEmpty()))) {
      publishingInfo.append(", ");
    }

    // Edition
    if (null != this.getMetadata().getPublishingInfo().getEdition()) {
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
    if (null == this.firstSource) {
      return "";
    }

    final StringBuilder publishingInfoSource = new StringBuilder();

    if (null != this.firstSource.getPublishingInfo()) {
      // Place
      if (null != this.firstSource.getPublishingInfo().getPlace()) {
        publishingInfoSource.append(this.firstSource.getPublishingInfo().getPlace().trim());
      }

      // colon
      if (null != this.firstSource.getPublishingInfo().getPublisher()
          && !this.firstSource.getPublishingInfo().getPublisher().trim().isEmpty()
          && null != this.firstSource.getPublishingInfo().getPlace() && !this.firstSource.getPublishingInfo().getPlace().trim().isEmpty()) {
        publishingInfoSource.append(" : ");
      }

      // Publisher
      if (null != this.firstSource.getPublishingInfo().getPublisher()) {
        publishingInfoSource.append(this.firstSource.getPublishingInfo().getPublisher().trim());
      }

      // Comma
      if ((null != this.firstSource.getPublishingInfo().getEdition() && !this.firstSource.getPublishingInfo().getEdition().trim().isEmpty())
          && ((null != this.firstSource.getPublishingInfo().getPlace() && !this.firstSource.getPublishingInfo().getPlace().trim().isEmpty())
              || (null != this.firstSource.getPublishingInfo().getPublisher()
                  && !this.firstSource.getPublishingInfo().getPublisher().trim().isEmpty()))) {
        publishingInfoSource.append(", ");
      }

      // Edition
      if (null != this.firstSource.getPublishingInfo().getEdition()) {
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
    if (null != this.getMetadata().getEvent() && null != this.getMetadata().getEvent().getTitle()
        && !this.getMetadata().getEvent().getTitle().trim().isEmpty()) {
      if (50 < this.getMetadata().getEvent().getTitle().length()) {
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
    if (null != this.getMetadata().getTitle() && !this.getMetadata().getTitle().trim().isEmpty()) {
      if (80 < this.getMetadata().getTitle().length()) {
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
    if (!this.getMetadata().getAbstracts().isEmpty()) {
      if (null != this.getMetadata().getAbstracts().get(0) && null != this.getMetadata().getAbstracts().get(0).getValue()
          && 150 < this.getMetadata().getAbstracts().get(0).getValue().length()) {
        return this.getMetadata().getAbstracts().get(0).getValue().substring(0, 149) + "...";
      }

      else if (null != this.getMetadata().getAbstracts().get(0) && null != this.getMetadata().getAbstracts().get(0).getValue())
        return this.getMetadata().getAbstracts().get(0).getValue();
    }

    return null;
  }

  /**
   * @return String the title
   */
  public String getFullTitle() {
    if (null != this.getMetadata() && null != this.getMetadata().getTitle()) {
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
    if (null != this.firstSource && null != this.firstSource.getTitle() && !this.firstSource.getTitle().trim().isEmpty()) {
      if (50 < this.firstSource.getTitle().length()) {
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
    if (null == this.getFileList()) {
      return "";
    }

    final StringBuilder files = new StringBuilder();
    files.append(this.getFileList().size());

    // if there is only 1 file, display "File attached", otherwise display "Files attached" (plural)
    if (1 == this.getFileList().size()) {
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
    if (null == this.getLocatorList()) {
      return "";
    }

    final StringBuilder locators = new StringBuilder();
    locators.append(this.getLocatorList().size());

    // if there is only 1 locator, display "Locator", otherwise display "Locators" (plural)
    if (1 == this.getLocatorList().size()) {
      locators.append(" " + this.getLabel("ViewItemShort_lblLocatorAttached"));
    } else {
      locators.append(" " + this.getLabel("ViewItemShort_lblLocatorsAttached"));
    }

    return locators.toString();
  }

  private List<FileDbVO> getFileList() {
    List<FileDbVO> fileList = null;
    if (null != this.getFiles()) {
      fileList = new ArrayList<>();
      for (int i = 0; i < this.getFiles().size(); i++) {
        if (FileDbVO.Storage.INTERNAL_MANAGED == this.getFiles().get(i).getStorage()) {
          fileList.add(this.getFiles().get(i));
        }
      }
    }

    return fileList;
  }

  public int getNumberOfFiles() {
    return this.getFileList().size();
  }

  private List<FileDbVO> getLocatorList() {
    List<FileDbVO> locatorList = null;
    if (null != this.getFiles()) {
      locatorList = new ArrayList<>();
      for (int i = 0; i < this.getFiles().size(); i++) {
        if (FileDbVO.Storage.EXTERNAL_URL == this.getFiles().get(i).getStorage()) {
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
    if (null != this.getFileList()) {
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
    if (null != this.getLocatorList()) {
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
    if (null != this.getMetadata().getSources() && 1 < this.getMetadata().getSources().size()) {
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
    if (null != this.creatorArray) {
      creators = creators + this.creatorArray.size();
    }
    if (null != this.creatorOrganizationsArray) {
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
    if (null != this.affiliatedOrganizationsList) {
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
    this.selected = (Boolean) event.getNewValue();
  }

  public String getLink() {
    if (null != this && null != this.getObjectId()) {
      return CommonUtils.getGenericItemLink(this.getObjectId(), this.getVersionNumber());
    }

    return null;
  }

  public String getLinkLatestRelease() {
    if (null != this.getObject().getLatestRelease() && null != this.getObject().getLatestRelease().getObjectId()) {
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
    if (null != this.getObject().getPublicState()) {
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
    if (null != this.getVersionState()) {
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

  public String getIdentifier() {
    final String id = this.getLink();
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

  public List<FileBean> getFulltextFileBeanList() {
    final List<FileBean> fulltexts = new ArrayList<>();
    if (null != this.fileBeanList) {
      for (final FileBean file : this.fileBeanList) {
        if ("any-fulltext".equals(file.getContentCategory())) {
          fulltexts.add(file);
        }
      }
    }

    return fulltexts;
  }

  public List<FileBean> getPubliclyAccessibleFulltextFileBeanList() {
    final List<FileBean> fulltexts = new ArrayList<>();
    if (null != this.fileBeanList) {
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

  public List<FileBean> getSupplementaryMaterialFileBeanList() {
    final List<FileBean> supplementaryMaterial = new ArrayList<>();
    if (null != this.fileBeanList) {
      for (final FileBean file : this.fileBeanList) {
        if ("supplementary-material".equals(file.getContentCategory()) || "multimedia".equals(file.getContentCategory())
            || "research-data".equals(file.getContentCategory()) || "code".equals(file.getContentCategory())) {
          supplementaryMaterial.add(file);
        }
      }
    }

    return supplementaryMaterial;
  }

  public List<FileBean> getRestrictedAccessibleFulltextFileBeanList() {
    final List<FileBean> fulltexts = new ArrayList<>();
    if (null != this.fileBeanList) {
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

  public List<FileBean> getPubliclyAccessibleSupplementaryMaterialFileBeanList() {
    final List<FileBean> supplementaryMaterial = new ArrayList<>();
    if (null != this.fileBeanList) {
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

    if (!creators.isEmpty()) {
      this.descriptionMetaTag = this.getLabel("ENUM_CREATORROLE_" + creators.get(0).getRoleString()) + ": ";
      if (null != creators.get(0).getPerson()) {
        this.descriptionMetaTag += creators.get(0).getPerson().getFamilyName() + ", " + creators.get(0).getPerson().getGivenName();
      } else {
        this.descriptionMetaTag += creators.get(0).getOrganization().getName();
      }
    }

    if (1 < creators.size()) {
      this.descriptionMetaTag += " et al.";
    }

    // add genre information
    this.descriptionMetaTag +=
        "; " + this.getLabel("ViewItemFull_lblGenre") + ": " + this.getLabel("ENUM_GENRE_" + this.getMetadata().getGenre());

    // add published print date
    if (null != this.getMetadata().getDatePublishedInPrint() && !"".equals(this.getMetadata().getDatePublishedInPrint())) {
      this.descriptionMetaTag +=
          "; " + this.getLabel("ViewItemShort_lblDatePublishedInPrint") + ": " + this.getMetadata().getDatePublishedInPrint();
    }
    // add published online date if no publisched print date
    else if (null != this.getMetadata().getDatePublishedOnline() && !"".equals(this.getMetadata().getDatePublishedOnline())) {
      this.descriptionMetaTag +=
          "; " + this.getLabel("ViewItemShort_lblDatePublishedOnline") + ": " + this.getMetadata().getDatePublishedOnline();
    }

    // add open access component
    if (null != this.getFileBeanList() && !this.getFileBeanList().isEmpty()) {
      for (final FileBean file : this.getFileBeanList()) {
        if (true == file.getIsVisible()) {
          this.descriptionMetaTag += "; Open Access";
          break;
        }
      }
    }

    // add keywords
    if (null != this.getMetadata().getFreeKeywords() && !"".equals(this.getMetadata().getFreeKeywords())) {
      this.descriptionMetaTag += "; Keywords: " + this.getMetadata().getFreeKeywords();
    }

    // add title at the end of description meta tag
    if (null != this.getMetadata().getTitle() && !"".equals(this.getMetadata().getTitle())) {
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
    if (null != this.getMetadata().getDatePublishedInPrint() && !this.getMetadata().getDatePublishedInPrint().isEmpty()) {
      return this.getLabel("ViewItem_lblPublicationState_published-in-print");
    } else if (null != this.getMetadata().getDatePublishedOnline() && !this.getMetadata().getDatePublishedOnline().isEmpty()) {
      return this.getLabel("ViewItem_lblPublicationState_published-online");
    } else if (null != this.getMetadata().getDateAccepted() && !this.getMetadata().getDateAccepted().isEmpty()) {
      return this.getLabel("ViewItem_lblPublicationState_accepted");
    } else if (null != this.getMetadata().getDateSubmitted() && !this.getMetadata().getDateSubmitted().isEmpty()) {
      return this.getLabel("ViewItem_lblPublicationState_submitted");
    } else {
      return this.getLabel("ViewItem_lblPublicationState_not-specified");
    }
  }

  private String getLabel(String placeholder) {
    return this.i18nHelper.getLabel(placeholder);
  }

  public Map<String, List<String>> getHighlightMap() {
    return this.highlightMap;
  }

  public void setHighlightMap(Map<String, List<String>> highlightMap) {
    this.highlightMap = highlightMap;
  }

  public Hit getSearchHit() {
    return this.searchHit;
  }

  public void setSearchHit(Hit searchHit) {
    this.searchHit = searchHit;
  }
}
