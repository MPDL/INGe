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

package de.mpg.mpdl.inge.model.valueobjects.publication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.model.valueobjects.MetadataSetVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AbstractVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AlternativeTitleVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.LegalCaseVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.ProjectInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PublishingInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SubjectVO;

/**
 * The metadata of a Publication.
 *
 * @revised by MuJ: 28.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 21-Nov-2007 11:48:44
 */
@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_EMPTY)
public class MdsPublicationVO extends MetadataSetVO implements Cloneable {
  /**
   * The possible degree types for an item.
   *
   * @updated 21-Nov-2007 11:48:44
   */
  public enum DegreeType
  {
    BACHELOR("http://purl.org/escidoc/metadata/ves/academic-degrees/bachelor"), //
    DIPLOMA("http://purl.org/escidoc/metadata/ves/academic-degrees/diploma"), //
    HABILITATION("http://purl.org/escidoc/metadata/ves/academic-degrees/habilitation"), //
    MAGISTER("http://purl.org/escidoc/metadata/ves/academic-degrees/magister"), //
    MASTER("http://purl.org/escidoc/metadata/ves/academic-degrees/master"), //
    PHD("http://purl.org/escidoc/metadata/ves/academic-degrees/phd"), //
    STAATSEXAMEN("http://purl.org/escidoc/metadata/ves/academic-degrees/staatsexamen");

  private final String uri;

  DegreeType(String uri) {
      this.uri = uri;
    }

  public String getUri() {
    return uri;
  }}

  /**
   * The possible review methods for an item.
   *
   * @updated 21-Nov-2007 11:48:44
   */
  public enum ReviewMethod{

  INTERNAL("http://purl.org/escidoc/metadata/ves/review-methods/internal"), //
    NO_REVIEW("http://purl.org/escidoc/metadata/ves/review-methods/no-review"), //
    PEER("http://purl.org/eprint/status/PeerReviewed");

  private final String uri;

  ReviewMethod(String uri) {
      this.uri = uri;
    }

  public String getUri() {
    return uri;
  }}

  /**
   * The possible genres for an item.
   */
  public enum Genre{

  ARTICLE("http://purl.org/escidoc/metadata/ves/publication-types/article"), //
    BLOG_POST("http://purl.org/eprint/type/blog-post"), //
    BOOK("http://purl.org/eprint/type/Book"), //
    BOOK_ITEM("http://purl.org/eprint/type/BookItem"), //
    BOOK_REVIEW("http://purl.org/escidoc/metadata/ves/publication-types/book-review"), //
    CASE_NOTE("http://purl.org/escidoc/metadata/ves/publication-types/case-note"), //
    CASE_STUDY("http://purl.org/escidoc/metadata/ves/publication-types/case-study"), //
    COLLECTED_EDITION("http://purl.org/escidoc/metadata/ves/publication-types/collected-edition"), //
    COMMENTARY("http://purl.org/escidoc/metadata/ves/publication-types/commentary"), //
    CONFERENCE_PAPER("http://purl.org/eprint/type/ConferencePaper"), //
    CONFERENCE_REPORT("http://purl.org/escidoc/metadata/ves/publication-types/conference-report"), //
    CONTRIBUTION_TO_COLLECTED_EDITION("http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-collected-edition"), //
    CONTRIBUTION_TO_COMMENTARY("http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-commentary"), //
    CONTRIBUTION_TO_ENCYCLOPEDIA("http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-encyclopedia"), //
    CONTRIBUTION_TO_FESTSCHRIFT("http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-festschrift"), //
    CONTRIBUTION_TO_HANDBOOK("http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-handbook"), //
    COURSEWARE_LECTURE("http://purl.org/escidoc/metadata/ves/publication-types/courseware-lecture"), //
    DATA_PUBLICATION("http://purl.org/escidoc/metadata/ves/publication-types/data-publication"), //
    EDITORIAL("http://purl.org/escidoc/metadata/ves/publication-types/editorial"), //
    ENCYCLOPEDIA("http://purl.org/escidoc/metadata/ves/publication-types/encyclopedia"), //
    FESTSCHRIFT("http://purl.org/escidoc/metadata/ves/publication-types/festschrift"), //
    FILM("http://purl.org/escidoc/metadata/ves/publication-types/film"), //
    HANDBOOK("http://purl.org/escidoc/metadata/ves/publication-types/handbook"), //
    INTERVIEW("http://purl.org/escidoc/metadata/ves/publication-types/interview"), //
    ISSUE("http://purl.org/escidoc/metadata/ves/publication-types/issue"), //
    JOURNAL("http://purl.org/escidoc/metadata/ves/publication-types/journal"), //
    MAGAZINE_ARTICLE("http://purl.org/escidoc/metadata/ves/publication-types/magazine-article"), //
    MANUAL("http://purl.org/escidoc/metadata/ves/publication-types/manual"), //
    MANUSCRIPT("http://purl.org/escidoc/metadata/ves/publication-types/manuscript"), //
    MEETING_ABSTRACT("http://purl.org/escidoc/metadata/ves/publication-types/meeting-abstract"), //
    MONOGRAPH("http://purl.org/escidoc/metadata/ves/publication-types/monograph"), //
    MULTI_VOLUME("http://purl.org/escidoc/metadata/ves/publication-types/multi-volume"), //
    NEWSPAPER("http://purl.org/escidoc/metadata/ves/publication-types/newspaper"), //
    NEWSPAPER_ARTICLE("http://purl.org/escidoc/metadata/ves/publication-types/newspaper-article"), //
    OPINION("http://purl.org/escidoc/metadata/ves/publication-types/opinion"), //
    OTHER("http://purl.org/escidoc/metadata/ves/publication-types/other"), //
    PAPER("http://purl.org/escidoc/metadata/ves/publication-types/paper"), //
    PATENT("http://purl.org/eprint/type/Patent"), //
    POSTER("http://purl.org/eprint/type/ConferencePoster"), //
    PRE_REGISTRATION_PAPER("http://purl.org/eprint/type/pre-registration-paper"), //
    PREPRINT("http://purl.org/eprint/type/preprint"), //
    PROCEEDINGS("http://purl.org/escidoc/metadata/ves/publication-types/proceedings"), //
    REGISTERED_REPORT("http://purl.org/eprint/type/registered-report"), //
    REPORT("http://purl.org/eprint/type/Report"), //
    REVIEW_ARTICLE("http://purl.org/eprint/type/review-article"), //
    SERIES("http://purl.org/escidoc/metadata/ves/publication-types/series"), //
    SOFTWARE("http://purl.org/escidoc/metadata/ves/publication-types/software"), //
    TALK_AT_EVENT("http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event"), //
    THESIS("http://purl.org/eprint/type/Thesis");

  private final String uri;

  Genre(String uri) {
      this.uri = uri;
    }

  public String getUri() {
    return uri;
  }}

  public enum SubjectClassification{

  DDC("http://purl.org/escidoc/metadata/terms/0.1/DDC"), //
    ISO639_3("http://purl.org/escidoc/metadata/terms/0.1/ISO639-3"), //
    JEL("http://purl.org/escidoc/metadata/terms/0.1/JEL"), //
    JUS("http://purl.org/escidoc/metadata/terms/0.1/JUS"), //
    MPINP("http://purl.org/escidoc/metadata/terms/0.1/MPINP"), //
    MPIPKS("http://purl.org/escidoc/metadata/terms/0.1/MPIPKS"), //
    MPIRG("http://purl.org/escidoc/metadata/terms/0.1/MPIRG"), //
    MPIS_GROUPS("http://purl.org/escidoc/metadata/terms/0.1/MPIS_GROUPS"), //
    MPIS_PROJECTS("http://purl.org/escidoc/metadata/terms/0.1/MPIS_PROJECTS"), //
    MPIWG_PROJECTS("http://purl.org/escidoc/metadata/terms/0.1/MPIWG_PROJECTS"), //
    MPICC_PROJECTS("http://purl.org/escidoc/metadata/terms/0.1/MPICC_PROJECTS"),
    PACS("http://purl.org/escidoc/metadata/terms/0.1/PACS");

  private final String uri;

  SubjectClassification(String uri) {
      this.uri = uri;
    }

  public String getUri() {
    return uri;
  }

  public String toString() {
    return name();
  }}

  /**
   * Alternative titles of the publication, e.g. translations of original title or sub-titles.
   */
  private final java.util.List<AlternativeTitleVO> alternativeTitles = new java.util.ArrayList<>();
  /**
   * Persons and organizations who essentially participated in creating the content with a specific
   * task, e.g. author, translator, editor.
   */
  private final java.util.List<CreatorVO> creators = new java.util.ArrayList<>();
  private String dateAccepted;
  private String dateCreated;
  private String dateModified;
  private String datePublishedInPrint;
  /**
   * The date the item was published online.
   */
  private String datePublishedOnline;
  private String dateSubmitted;
  /**
   * The type of degree which is received with this type of publication.
   */
  private DegreeType degree;
  /**
   * Some items are related to an event, e.g. a conference or a lecture series.
   */
  private EventVO event;
  /**
   * JUS The information about the legal case of case note publication.
   */
  private LegalCaseVO legalCase;
  /**
   * The genre of a publication describes the type of the publication.
   */
  private Genre genre;
  /**
   * Identifiers referencing the described item, e.g. the ISBN, Report-Number.
   */
  private final java.util.List<IdentifierVO> identifiers = new java.util.ArrayList<>();
  /**
   * The language attribute is a valid ISO Language Code. These codes are the lower- case,
   * two-letter codes as defined by ISO-639. You can find a full list of these codes at a number of
   * sites, such as: http://www.loc.gov/standards/iso639- 2/englangn.html
   */
  private final java.util.List<String> languages = new java.util.ArrayList<>();
  /**
   * The name of the library where the item is currently located.
   */
  private String location;
  /**
   * The institution which published the item and additional information, e.g. the publisher name
   * and place of a book, or the university where a theses has been created.
   */
  private PublishingInfoVO publishingInfo;
  /**
   * The type of the scientific review process for the described item.
   */
  private ReviewMethod reviewMethod;
  /**
   * The bundles in which the item has been published, e.g. journals, books, series or databases.
   */
  private final java.util.List<SourceVO> sources = new java.util.ArrayList<>();
  /**
   * Free keywords.
   */
  private String freeKeywords;

  private final List<SubjectVO> subjects = new ArrayList<>();

  private String tableOfContents;
  /**
   * The number of pages of the described item. Note: The pages of an item published in a bundle is
   * part of the source container.
   */
  private String totalNumberOfPages;
  /**
   * Abstracts or short descriptions of the item.
   */
  private final java.util.List<AbstractVO> abstracts = new java.util.ArrayList<>();

  /**
   * Information about project and funding
   */
  private List<ProjectInfoVO> projectInfo = new ArrayList<>();

  /**
   * Creates a new instance.
   */
  public MdsPublicationVO() {
  }

  /**
   * Copy constructor.
   *
   * @param other The instance to copy.
   */
  public MdsPublicationVO(MdsPublicationVO other) {
    super(other.getTitle());
    for (AlternativeTitleVO altTitle : other.getAlternativeTitles()) {
      getAlternativeTitles().add(altTitle.clone());
    }
    for (CreatorVO creator : other.getCreators()) {
      getCreators().add(creator.clone());
    }
    if (other.getDateAccepted() != null) {
      setDateAccepted(other.getDateAccepted());
    }
    if (other.getDateCreated() != null) {
      setDateCreated(other.getDateCreated());
    }
    if (other.getDateModified() != null) {
      setDateModified(other.getDateModified());
    }
    if (other.getDatePublishedInPrint() != null) {
      setDatePublishedInPrint(other.getDatePublishedInPrint());
    }
    // DiT, 14.11.2007: added DatePublishedOnline
    if (other.getDatePublishedOnline() != null) {
      setDatePublishedOnline(other.getDatePublishedOnline());
    }
    if (other.getDateSubmitted() != null) {
      setDateSubmitted(other.getDateSubmitted());
    }
    setDegree(other.getDegree());
    if (other.getEvent() != null) {
      setEvent(other.getEvent().clone());
    }
    // JUS BEGIN
    if (other.getLegalCase() != null) {
      setLegalCase(other.getLegalCase().clone());
    }
    // JUS END
    setGenre(other.getGenre());
    for (IdentifierVO identifier : other.getIdentifiers()) {
      getIdentifiers().add(identifier.clone());
    }
    getLanguages().addAll(other.getLanguages());
    setLocation(other.getLocation());
    if (other.getPublishingInfo() != null) {
      setPublishingInfo(other.getPublishingInfo().clone());
    }
    setReviewMethod(other.getReviewMethod());
    for (SourceVO source : other.getSources()) {
      getSources().add(source.clone());
    }

    if (other.getFreeKeywords() != null) {
      setFreeKeywords(other.getFreeKeywords());
    }

    for (SubjectVO subject : other.getSubjects()) {
      getSubjects().add(subject.clone());
    }

    for (AbstractVO summary : other.getAbstracts()) {
      getAbstracts().add(summary.clone());
    }
    if (other.getTableOfContents() != null) {
      setTableOfContents(other.getTableOfContents());
    }

    setTotalNumberOfPages(other.getTotalNumberOfPages());

    if (other.getProjectInfo() != null) {
      for (ProjectInfoVO pi : other.getProjectInfo()) {
        getProjectInfo().add(pi.clone());
      }
    }

  }

  /**
   * Delivers the list of alternative titles of the item, e.g. translations of original title or
   * sub-titles.
   */
  public java.util.List<AlternativeTitleVO> getAlternativeTitles() {
    return alternativeTitles;
  }

  /**
   * Delivers the list of creators of the item, i. e. any person or organization who essentially
   * participated in creating the content with a specific task, e. g. author, translator, editor.
   */
  public java.util.List<CreatorVO> getCreators() {
    return creators;
  }

  /**
   * Delivers the degree of the item, i. e. the type of degree which is received with this type of
   * publication
   */
  public DegreeType getDegree() {
    return degree;
  }

  /**
   * Delivers the event of the item. Some items are related to an event, e.g. a conference or a
   * lecture series.
   */
  public EventVO getEvent() {
    return event;
  }

  /**
   * JUS Delivers the legal case of the item. Items of genre types case note has mandatory legal
   * case information.
   */
  public LegalCaseVO getLegalCase() {
    return legalCase;
  }

  /**
   * Delivers the genre of the item, i. e. the type of the publication (e. g. article, book,
   * conference paper).
   */
  public MdsPublicationVO.Genre getGenre() {
    return genre;
  }

  /**
   * Delivers the list of identifiers of the item, i. e. identifiers referencing the described item,
   * e. g. the ISBN, report number.
   */
  public java.util.List<IdentifierVO> getIdentifiers() {
    return identifiers;
  }

  /**
   * Delivers the location of the item, i. e. the name of the library where the item is currently
   * located.
   */
  public String getLocation() {
    return location;
  }

  /**
   * Delivers the publication info of the item, i. e. the institution which published the item and
   * additional information, e. g. the publisher name and place of a book, or the university where a
   * thesis has been created.
   */
  public PublishingInfoVO getPublishingInfo() {
    return publishingInfo;
  }

  /**
   * Delivers the review method of the item, i. e. the type of the scientific review process for the
   * described item.
   */
  public ReviewMethod getReviewMethod() {
    return reviewMethod;
  }

  /**
   * Delivers the list of sources of the item, i. e. the bundles in which the item has been
   * published, e. g. journals, books, series or databases.
   */
  public java.util.List<SourceVO> getSources() {
    return sources;
  }

  /**
   * DDC keywords.
   */
  public List<SubjectVO> getSubjects() {
    return subjects;
  }

  /**
   * Delivers the table of contents of the item.
   */
  public String getTableOfContents() {
    return tableOfContents;
  }

  /**
   * Delivers the number of pages of the item. Note: The pages of an item published in a bundle is
   * part of the source container.
   */
  public String getTotalNumberOfPages() {
    return totalNumberOfPages;
  }

  /**
   * Sets the degree of the item, i. e. the type of degree which is received with this type of
   * publication
   *
   * @param newVal newVal
   */
  public void setDegree(DegreeType newVal) {
    degree = newVal;
  }

  /**
   * Sets the event of the item. Some items are related to an event, e.g. a conference or a lecture
   * series.
   *
   * @param newVal newVal
   */
  public void setEvent(EventVO newVal) {
    event = newVal;
  }

  /**
   * JUS Sets the legal case of the item. Items of genre types case note has mandatory legal case
   * information.
   */
  public void setLegalCase(LegalCaseVO newVal) {
    legalCase = newVal;
  }

  /**
   * Sets the genre of the item, i. e. the type of the publication (e. g. article, book, conference
   * paper).
   *
   * @param newVal newVal
   */
  public void setGenre(MdsPublicationVO.Genre newVal) {
    genre = newVal;
  }

  /**
   * Sets the location of the item, i. e. the name of the library where the item is currently
   * located.
   *
   * @param newVal newVal
   */
  public void setLocation(String newVal) {
    location = newVal;
  }

  /**
   * Sets the publication info of the item, i. e. the institution which published the item and
   * additional information, e. g. the publisher name and place of a book, or the university where a
   * thesis has been created.
   *
   * @param newVal newVal
   */
  public void setPublishingInfo(PublishingInfoVO newVal) {
    publishingInfo = newVal;
  }

  /**
   * Sets the review method of the item, i. e. the type of the scientific review process for the
   * described item.
   *
   * @param newVal newVal
   */
  public void setReviewMethod(ReviewMethod newVal) {
    reviewMethod = newVal;
  }

  /**
   * Sets the table of contents of the item.
   *
   * @param newVal
   */
  public void setTableOfContents(String newVal) {
    tableOfContents = newVal;
  }

  /**
   * Sets the number of pages of the item. Note: The pages of an item published in a bundle is part
   * of the source container.
   *
   * @param newVal newVal
   */
  public void setTotalNumberOfPages(String newVal) {
    totalNumberOfPages = newVal;
  }

  public MdsPublicationVO clone() {
    return new MdsPublicationVO(this);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((abstracts == null) ? 0 : abstracts.hashCode());
    result = prime * result + ((alternativeTitles == null) ? 0 : alternativeTitles.hashCode());
    result = prime * result + ((creators == null) ? 0 : creators.hashCode());
    result = prime * result + ((dateAccepted == null) ? 0 : dateAccepted.hashCode());
    result = prime * result + ((dateCreated == null) ? 0 : dateCreated.hashCode());
    result = prime * result + ((dateModified == null) ? 0 : dateModified.hashCode());
    result = prime * result + ((datePublishedInPrint == null) ? 0 : datePublishedInPrint.hashCode());
    result = prime * result + ((datePublishedOnline == null) ? 0 : datePublishedOnline.hashCode());
    result = prime * result + ((dateSubmitted == null) ? 0 : dateSubmitted.hashCode());
    result = prime * result + ((degree == null) ? 0 : degree.hashCode());
    result = prime * result + ((event == null) ? 0 : event.hashCode());
    result = prime * result + ((freeKeywords == null) ? 0 : freeKeywords.hashCode());
    result = prime * result + ((genre == null) ? 0 : genre.hashCode());
    result = prime * result + ((identifiers == null) ? 0 : identifiers.hashCode());
    result = prime * result + ((languages == null) ? 0 : languages.hashCode());
    result = prime * result + ((legalCase == null) ? 0 : legalCase.hashCode());
    result = prime * result + ((location == null) ? 0 : location.hashCode());
    result = prime * result + ((projectInfo == null) ? 0 : projectInfo.hashCode());
    result = prime * result + ((publishingInfo == null) ? 0 : publishingInfo.hashCode());
    result = prime * result + ((reviewMethod == null) ? 0 : reviewMethod.hashCode());
    result = prime * result + ((sources == null) ? 0 : sources.hashCode());
    result = prime * result + ((subjects == null) ? 0 : subjects.hashCode());
    result = prime * result + ((tableOfContents == null) ? 0 : tableOfContents.hashCode());
    result = prime * result + ((totalNumberOfPages == null) ? 0 : totalNumberOfPages.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;

    if (!super.equals(obj))
      return false;

    if (getClass() != obj.getClass())
      return false;

    MdsPublicationVO other = (MdsPublicationVO) obj;

    if (abstracts == null) {
      if (other.abstracts != null)
        return false;
    } else if (other.abstracts == null)
      return false;
    else if (!new HashSet<>(abstracts).containsAll(other.abstracts) //
        || !new HashSet<>(other.abstracts).containsAll(abstracts)) {
      return false;
    }

    if (alternativeTitles == null) {
      if (other.alternativeTitles != null)
        return false;
    } else if (other.alternativeTitles == null)
      return false;
    else if (!new HashSet<>(alternativeTitles).containsAll(other.alternativeTitles) //
        || !new HashSet<>(other.alternativeTitles).containsAll(alternativeTitles)) {
      return false;
    }

    if (creators == null) {
      if (other.creators != null)
        return false;
    } else if (other.creators == null)
      return false;
    else if (!new HashSet<>(creators).containsAll(other.creators) //
        || !new HashSet<>(other.creators).containsAll(creators)) {
      return false;
    }

    if (dateAccepted == null) {
      if (other.dateAccepted != null)
        return false;
    } else if (!dateAccepted.equals(other.dateAccepted))
      return false;

    if (dateCreated == null) {
      if (other.dateCreated != null)
        return false;
    } else if (!dateCreated.equals(other.dateCreated))
      return false;

    if (dateModified == null) {
      if (other.dateModified != null)
        return false;
    } else if (!dateModified.equals(other.dateModified))
      return false;

    if (datePublishedInPrint == null) {
      if (other.datePublishedInPrint != null)
        return false;
    } else if (!datePublishedInPrint.equals(other.datePublishedInPrint))
      return false;

    if (datePublishedOnline == null) {
      if (other.datePublishedOnline != null)
        return false;
    } else if (!datePublishedOnline.equals(other.datePublishedOnline))
      return false;

    if (dateSubmitted == null) {
      if (other.dateSubmitted != null)
        return false;
    } else if (!dateSubmitted.equals(other.dateSubmitted))
      return false;

    if (degree != other.degree)
      return false;

    if (event == null) {
      if (other.event != null)
        return false;
    } else if (!event.equals(other.event))
      return false;

    if (freeKeywords == null) {
      if (other.freeKeywords != null)
        return false;
    } else if (!freeKeywords.equals(other.freeKeywords))
      return false;

    if (genre != other.genre)
      return false;

    if (identifiers == null) {
      if (other.identifiers != null)
        return false;
    } else if (other.identifiers == null)
      return false;
    else if (!new HashSet<>(identifiers).containsAll(other.identifiers) || !new HashSet<>(other.identifiers).containsAll(identifiers)) {
      return false;
    }

    if (languages == null) {
      if (other.languages != null)
        return false;
    } else if (other.languages == null)
      return false;
    else if (!new HashSet<>(languages).containsAll(other.languages) //
        || !new HashSet<>(other.languages).containsAll(languages)) {
      return false;
    }

    if (legalCase == null) {
      if (other.legalCase != null)
        return false;
    } else if (!legalCase.equals(other.legalCase))
      return false;

    if (location == null) {
      if (other.location != null)
        return false;
    } else if (!location.equals(other.location))
      return false;

    if (projectInfo == null) {
      if (other.projectInfo != null)
        return false;
    } else if (!projectInfo.equals(other.projectInfo))
      return false;

    if (publishingInfo == null) {
      if (other.publishingInfo != null)
        return false;
    } else if (!publishingInfo.equals(other.publishingInfo))
      return false;

    if (reviewMethod != other.reviewMethod)
      return false;

    if (sources == null) {
      if (other.sources != null)
        return false;
    } else if (other.sources == null)
      return false;
    else if (!new HashSet<>(sources).containsAll(other.sources) //
        || !new HashSet<>(other.sources).containsAll(sources)) {
      return false;
    }

    if (subjects == null) {
      if (other.subjects != null)
        return false;
    } else if (other.subjects == null)
      return false;
    else if (!new HashSet<>(subjects).containsAll(other.subjects) //
        || !new HashSet<>(other.subjects).containsAll(subjects)) {
      return false;
    }

    if (tableOfContents == null) {
      if (other.tableOfContents != null)
        return false;
    } else if (!tableOfContents.equals(other.tableOfContents))
      return false;

    if (totalNumberOfPages == null) {
      if (other.totalNumberOfPages != null)
        return false;
    } else if (!totalNumberOfPages.equals(other.totalNumberOfPages))
      return false;

    return true;
  }

  /**
   * Delivers the date when the item was accepted (for scientific check).
   */
  public String getDateAccepted() {
    return dateAccepted;
  }

  /**
   * Delivers the date when the item was created.
   */
  public String getDateCreated() {
    return dateCreated;
  }

  /**
   * Delivers the date when the item was modified.
   */
  public String getDateModified() {
    return dateModified;
  }

  /**
   * Delivers the date when the item was published.
   */
  public String getDatePublishedInPrint() {
    return datePublishedInPrint;
  }

  /**
   * Delivers the date when the item was submitted.
   */
  public String getDateSubmitted() {
    return dateSubmitted;
  }

  /**
   * Delivers the list of languages of the item. Every language attribute is a valid ISO Language
   * Code. These codes are the lower- case, two-letter codes as defined by ISO-639. You can find a
   * full list of these codes at a number of sites, such as:
   * http://www.loc.gov/standards/iso639-2/englangn.html
   */
  public java.util.List<String> getLanguages() {
    return languages;
  }

  /**
   * Sets the date when the item was accepted (for scientific check).
   *
   * @param newVal
   */
  public void setDateAccepted(String newVal) {
    if (newVal == null || newVal.isEmpty()) {
      dateAccepted = null;
    } else {
      dateAccepted = newVal;
    }

  }

  /**
   * Sets the date when the item was created.
   *
   * @param newVal
   */
  public void setDateCreated(String newVal) {
    if (newVal == null || newVal.isEmpty()) {
      dateCreated = null;
    } else {
      dateCreated = newVal;
    }

  }

  /**
   * Sets the date when the item was modified.
   *
   * @param newVal
   */
  public void setDateModified(String newVal) {
    if (newVal == null || newVal.isEmpty()) {
      dateModified = null;
    } else {
      dateModified = newVal;
    }

  }

  /**
   * Sets the date when the item was published.
   *
   * @param newVal
   */
  public void setDatePublishedInPrint(String newVal) {
    if (newVal == null || newVal.isEmpty()) {
      datePublishedInPrint = null;
    } else {
      datePublishedInPrint = newVal;
    }
  }

  /**
   * Sets the date when the item was submitted.
   *
   * @param newVal
   */
  public void setDateSubmitted(String newVal) {
    if (newVal == null || newVal.isEmpty()) {
      dateSubmitted = null;
    } else {
      dateSubmitted = newVal;
    }
  }

  /**
   * Delivers the list of abstracts or short descriptions of the item.
   */
  public java.util.List<AbstractVO> getAbstracts() {
    return abstracts;
  }

  public String getFreeKeywords() {
    return freeKeywords;
  }

  public void setFreeKeywords(String freeKeywords) {
    this.freeKeywords = freeKeywords;
  }

  /**
   * @return the datePublishedOnline
   */
  public String getDatePublishedOnline() {
    return datePublishedOnline;
  }

  /**
   *
   * @param newVal
   */
  public void setDatePublishedOnline(String newVal) {
    if (newVal == null || newVal.isEmpty()) {
      datePublishedOnline = null;
    } else {
      datePublishedOnline = newVal;
    }
  }

  public List<ProjectInfoVO> getProjectInfo() {
    return projectInfo;
  }

  public void setProjectInfo(List<ProjectInfoVO> projectInfo) {
    this.projectInfo = projectInfo;
  }
}
