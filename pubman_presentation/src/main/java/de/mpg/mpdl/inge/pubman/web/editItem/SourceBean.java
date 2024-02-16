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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.mpg.mpdl.inge.model.valueobjects.metadata.AlternativeTitleVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PublishingInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO.Genre;
import de.mpg.mpdl.inge.pubman.web.editItem.IdentifierCollection.IdentifierManager;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.CreatorVOPresentation;
import jakarta.faces.model.SelectItem;

/**
 * POJO bean to deal with one source.
 *
 * @author Mario Wagner
 */
@SuppressWarnings("serial")
public class SourceBean extends EditItemBean {
  public static final String HIDDEN_DELIMITER = " \\|\\|##\\|\\| ";
  public static final String HIDDEN_INNER_DELIMITER = " @@~~@@ ";
  public static final String HIDDEN_IDTYPE_DELIMITER = "\\|";

  //  private boolean autosuggestJournals = false;
  private String hiddenIdsField;
  private String hiddenAlternativeTitlesField;
  private SourceVO source;
  private List<SourceBean> list;
  private IdentifierCollection identifierCollection;
  private CreatorCollection creatorCollection;

  /**
   * Create a source bean using a given {@link SourceVO}.
   *
   * @param source The original source vo.
   */
  public SourceBean(SourceVO source, List<SourceBean> list) {
    this.list = list;
    this.setSource(source);
    // this.btnChooseCollection.setId("Source1");
    //    if (source.getGenre() != null && source.getGenre().equals(SourceVO.Genre.JOURNAL)) {
    //      this.autosuggestJournals = true;
    //    }
  }

  public SourceVO getSource() {
    return this.source;
  }

  /**
   * Set the source and initialize collections.
   *
   * @param source The original source vo.
   */
  public void setSource(SourceVO source) {
    this.source = source;
    // initialize embedded collections
    if (this.getCreators().isEmpty()) {
      this.bindCreatorsToBean(source.getCreators());
    }
    this.identifierCollection = new IdentifierCollection(source.getIdentifiers());
    if (source.getPublishingInfo() == null) {
      source.setPublishingInfo(new PublishingInfoVO());
    }
    //    if (source.getGenre() != null && source.getGenre().equals(SourceVO.Genre.JOURNAL)) {
    //      this.autosuggestJournals = true;
    //    }
  }

  //  public void chooseSourceGenre(ValueChangeEvent event) {
  //    if (event.getNewValue() != null) {
  //
  //
  //      final String sourceGenre = event.getNewValue().toString();
  ////      if (sourceGenre.equals(SourceVO.Genre.JOURNAL.toString())) {
  ////        this.autosuggestJournals = true;
  ////      }
  //    }
  //  }

  /**
   * Adds the first alternative title for the source with no content
   */
  public void addSourceAlternativeTitle() {
    if (this.getSource() != null) {
      if (this.getSource().getAlternativeTitles() == null || this.getSource().getAlternativeTitles().isEmpty()) {
        this.getSource().getAlternativeTitles().add(new AlternativeTitleVO());
      }
    }
  }

  /**
   * Adds an empty alternative title for the source after the current one
   */
  public void addSourceAlternativeTitleAtIndex(int index) {
    if (this.getSource() != null && this.getSource().getAlternativeTitles() != null && !this.getSource().getAlternativeTitles().isEmpty()) {
      this.getSource().getAlternativeTitles().add((index + 1), new AlternativeTitleVO());
    }
  }

  /**
   * Removes an alternative title from the current position of the source
   */
  public void removeSourceAlternativeTitleAtIndex(int index) {
    if (this.getSource() != null && this.getSource().getAlternativeTitles() != null && !this.getSource().getAlternativeTitles().isEmpty()) {
      this.getSource().getAlternativeTitles().remove(index);
    }
  }

  /**
   * If genre is journal, activate auto suggest.
   *
   * @return EditItem page.
   */
  public String chooseGenre() {
    //    if (this.source.getGenre() != null && this.source.getGenre().equals(SourceVO.Genre.JOURNAL)) {
    //      this.autosuggestJournals = true;
    //    }
    return EditItem.LOAD_EDITITEM;
  }

  public CreatorCollection getCreatorCollection() {
    return this.creatorCollection;
  }

  public void setCreatorCollection(CreatorCollection creatorCollection) {
    this.creatorCollection = creatorCollection;
  }

  public IdentifierCollection getIdentifierCollection() {
    return this.identifierCollection;
  }

  public void setIdentifierCollection(IdentifierCollection identifierCollection) {
    this.identifierCollection = identifierCollection;
  }

  /**
   * localized creation of SelectItems for the source genres available.
   *
   * @return SelectItem[] with Strings representing source genres
   */
  public SelectItem[] getSourceGenreOptions() {
    final Map<String, String> excludedSourceGenres = ApplicationBean.INSTANCE.getExcludedSourceGenreMap();
    final List<SelectItem> sourceGenres = new ArrayList<SelectItem>();
    sourceGenres.add(new SelectItem("", this.getLabel("EditItem_NO_ITEM_SET")));
    for (final SourceVO.Genre value : SourceVO.Genre.values()) {
      sourceGenres.add(new SelectItem(value, this.getLabel("ENUM_GENRE_" + value.name())));
    }

    String uri = "";
    int i = 0;
    while (i < sourceGenres.size()) {
      if (sourceGenres.get(i).getValue() != null && !("").equals(sourceGenres.get(i).getValue())) {
        uri = ((SourceVO.Genre) sourceGenres.get(i).getValue()).getUri();
      }

      if (excludedSourceGenres.containsValue(uri)) {
        sourceGenres.remove(i);
      } else {
        i++;
      }
    }

    return sourceGenres.toArray(new SelectItem[sourceGenres.size()]);
  }

  //  public boolean getAutosuggestJournals() {
  //    return this.autosuggestJournals;
  //  }
  //
  //  public void setAutosuggestJournals(boolean autosuggestJournals) {
  //    this.autosuggestJournals = autosuggestJournals;
  //  }

  /**
   * Takes the text from the hidden input fields, splits it using the delimiter and adds them to the
   * model. Format of alternative titles: alt title 1 ||##|| alt title 2 ||##|| alt title 3 Format
   * of ids: URN|urn:221441 ||##|| URL|http://www.xwdc.de ||##|| ESCIDOC|escidoc:21431
   *
   * @return
   */
  public String parseAndSetAlternativeTitlesAndIds() {
    // clear old alternative titles
    final List<AlternativeTitleVO> altTitleList = this.getSource().getAlternativeTitles();
    altTitleList.clear();

    // clear old identifiers
    final IdentifierManager idManager = this.getIdentifierCollection().getIdentifierManager();
    idManager.getObjectList().clear();

    if (!this.getHiddenAlternativeTitlesField().trim().isEmpty()) {
      altTitleList.addAll(SourceBean.parseAlternativeTitles(this.getHiddenAlternativeTitlesField()));
    }
    if (!this.getHiddenIdsField().trim().isEmpty()) {
      // idManager.getDataListFromVO().clear();
      idManager.getObjectList().addAll(SourceBean.parseIdentifiers(this.getHiddenIdsField()));
    }
    return "";
  }

  public static List<AlternativeTitleVO> parseAlternativeTitles(String titleList) {
    final List<AlternativeTitleVO> list = new ArrayList<AlternativeTitleVO>();
    final String[] alternativeTitles = titleList.split(SourceBean.HIDDEN_DELIMITER);
    for (int i = 0; i < alternativeTitles.length; i++) {
      final String[] parts = alternativeTitles[i].trim().split(SourceBean.HIDDEN_INNER_DELIMITER);
      final String alternativeTitleType = parts[0].trim();
      final String alternativeTitle = parts[1].trim();
      if (!alternativeTitle.isEmpty()) {
        final AlternativeTitleVO textVO = new AlternativeTitleVO(alternativeTitle);
        textVO.setType(alternativeTitleType);
        list.add(textVO);
      }
    }
    return list;
  }

  public static List<IdentifierVO> parseIdentifiers(String idList) {
    final List<IdentifierVO> list = new ArrayList<IdentifierVO>();
    final String[] ids = idList.split(SourceBean.HIDDEN_DELIMITER);
    for (int i = 0; i < ids.length; i++) {
      final String idComplete = ids[i].trim();
      final String[] idParts = idComplete.split(SourceBean.HIDDEN_IDTYPE_DELIMITER);
      // id has no type, use type 'other'
      if (idParts.length == 1 && !idParts[0].isEmpty()) {
        final IdentifierVO idVO = new IdentifierVO(IdType.OTHER, idParts[0].trim());
        list.add(idVO);
      }
      // Id has a type
      else if (idParts.length == 2) {
        IdType idType = IdType.OTHER;

        for (final IdType id : IdType.values()) {
          if (id.getUri().equals(idParts[0]) || id.name().toLowerCase().equals(idParts[0].toLowerCase())) {
            idType = id;
          }
        }

        final IdentifierVO idVO = new IdentifierVO(idType, idParts[1].trim());
        list.add(idVO);
      }
    }
    return list;
  }

  public int getPosition() {
    for (int i = 0; i < this.list.size(); i++) {
      if (this.list.get(i) == this) {
        return i;
      }
    }
    return -1;
  }

  public String add() {

    final SourceVO sourceVO = new SourceVO();
    if (sourceVO.getIdentifiers().isEmpty()) {
      sourceVO.getIdentifiers().add(new IdentifierVO());
    }

    final SourceBean newSourceBean = new SourceBean(sourceVO, this.list);
    final CreatorVOPresentation newSourceCreator = new CreatorVOPresentation(newSourceBean.getCreators(), newSourceBean);
    newSourceCreator.setType(CreatorType.PERSON);
    newSourceCreator.setPerson(new PersonVO());
    newSourceCreator.getPerson().setIdentifier(new IdentifierVO());
    newSourceCreator.getPerson().setOrganizations(new ArrayList<OrganizationVO>());
    final OrganizationVO newCreatorOrganization = new OrganizationVO();
    newCreatorOrganization.setName("");
    newSourceCreator.getPerson().getOrganizations().add(newCreatorOrganization);
    newSourceBean.getCreators().add(newSourceCreator);
    this.list.add(this.getPosition() + 1, newSourceBean);
    newSourceBean.initOrganizationsFromCreators();
    return "";
  }

  public String remove() {
    this.list.remove(this);
    return "";
  }

  public boolean isSingleElement() {
    return (this.list.size() == 1);
  }

  public String getJournalSuggestClass() {
    if (this.source.getGenre() == Genre.JOURNAL) {
      return " journalSuggest";
    } else {
      return "";
    }
  }

  public void setHiddenIdsField(String hiddenIdsField) {
    this.hiddenIdsField = hiddenIdsField;
  }

  public String getHiddenIdsField() {
    return this.hiddenIdsField;
  }

  public void setHiddenAlternativeTitlesField(String hiddenAlternativeTitlesField) {
    this.hiddenAlternativeTitlesField = hiddenAlternativeTitlesField;
  }

  public String getHiddenAlternativeTitlesField() {
    return this.hiddenAlternativeTitlesField;
  }

  public List<SourceBean> getList() {
    return this.list;
  }

  public void setList(List<SourceBean> list) {
    this.list = list;
  }
}
