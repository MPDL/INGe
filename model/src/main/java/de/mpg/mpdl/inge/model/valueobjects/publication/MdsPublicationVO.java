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
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
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
    return this.uri;
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
    return this.uri;
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
    return this.uri;
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
    return this.uri;
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
    for (AlternativeTitleVO altTitle : other.alternativeTitles) {
      this.alternativeTitles.add(altTitle.clone());
    }
    for (CreatorVO creator : other.creators) {
      this.creators.add(creator.clone());
    }
    if (null != other.dateAccepted) {
      setDateAccepted(other.dateAccepted);
    }
    if (null != other.dateCreated) {
      setDateCreated(other.dateCreated);
    }
    if (null != other.dateModified) {
      setDateModified(other.dateModified);
    }
    if (null != other.datePublishedInPrint) {
      setDatePublishedInPrint(other.datePublishedInPrint);
    }
    // DiT, 14.11.2007: added DatePublishedOnline
    if (null != other.datePublishedOnline) {
      setDatePublishedOnline(other.datePublishedOnline);
    }
    if (null != other.dateSubmitted) {
      setDateSubmitted(other.dateSubmitted);
    }
    this.degree = other.degree;
    if (null != other.event) {
      this.event = other.event.clone();
    }
    // JUS BEGIN
    if (null != other.legalCase) {
      this.legalCase = other.legalCase.clone();
    }
    // JUS END
    this.genre = other.genre;
    for (IdentifierVO identifier : other.identifiers) {
      this.identifiers.add(identifier.clone());
    }
    this.languages.addAll(other.languages);
    this.location = other.location;
    if (null != other.publishingInfo) {
      this.publishingInfo = other.publishingInfo.clone();
    }
    this.reviewMethod = other.reviewMethod;
    for (SourceVO source : other.sources) {
      this.sources.add(source.clone());
    }

    if (null != other.freeKeywords) {
      this.freeKeywords = other.freeKeywords;
    }

    for (SubjectVO subject : other.subjects) {
      this.subjects.add(subject.clone());
    }

    for (AbstractVO summary : other.abstracts) {
      this.abstracts.add(summary.clone());
    }
    if (null != other.tableOfContents) {
      this.tableOfContents = other.tableOfContents;
    }

    this.totalNumberOfPages = other.totalNumberOfPages;

    if (null != other.projectInfo) {
      for (ProjectInfoVO pi : other.projectInfo) {
        this.projectInfo.add(pi.clone());
      }
    }

  }

  /**
   * Delivers the list of alternative titles of the item, e.g. translations of original title or
   * sub-titles.
   */
  public java.util.List<AlternativeTitleVO> getAlternativeTitles() {
    return this.alternativeTitles;
  }

  /**
   * Delivers the list of creators of the item, i. e. any person or organization who essentially
   * participated in creating the content with a specific task, e. g. author, translator, editor.
   */
  public java.util.List<CreatorVO> getCreators() {
    return this.creators;
  }

  /**
   * Delivers the degree of the item, i. e. the type of degree which is received with this type of
   * publication
   */
  public DegreeType getDegree() {
    return this.degree;
  }

  /**
   * Delivers the event of the item. Some items are related to an event, e.g. a conference or a
   * lecture series.
   */
  public EventVO getEvent() {
    return this.event;
  }

  /**
   * JUS Delivers the legal case of the item. Items of genre types case note has mandatory legal
   * case information.
   */
  public LegalCaseVO getLegalCase() {
    return this.legalCase;
  }

  /**
   * Delivers the genre of the item, i. e. the type of the publication (e. g. article, book,
   * conference paper).
   */
  public MdsPublicationVO.Genre getGenre() {
    return this.genre;
  }

  /**
   * Delivers the list of identifiers of the item, i. e. identifiers referencing the described item,
   * e. g. the ISBN, report number.
   */
  public java.util.List<IdentifierVO> getIdentifiers() {
    return this.identifiers;
  }

  /**
   * Delivers the location of the item, i. e. the name of the library where the item is currently
   * located.
   */
  public String getLocation() {
    return this.location;
  }

  /**
   * Delivers the publication info of the item, i. e. the institution which published the item and
   * additional information, e. g. the publisher name and place of a book, or the university where a
   * thesis has been created.
   */
  public PublishingInfoVO getPublishingInfo() {
    return this.publishingInfo;
  }

  /**
   * Delivers the review method of the item, i. e. the type of the scientific review process for the
   * described item.
   */
  public ReviewMethod getReviewMethod() {
    return this.reviewMethod;
  }

  /**
   * Delivers the list of sources of the item, i. e. the bundles in which the item has been
   * published, e. g. journals, books, series or databases.
   */
  public java.util.List<SourceVO> getSources() {
    return this.sources;
  }

  /**
   * DDC keywords.
   */
  public List<SubjectVO> getSubjects() {
    return this.subjects;
  }

  /**
   * Delivers the table of contents of the item.
   */
  public String getTableOfContents() {
    return this.tableOfContents;
  }

  /**
   * Delivers the number of pages of the item. Note: The pages of an item published in a bundle is
   * part of the source container.
   */
  public String getTotalNumberOfPages() {
    return this.totalNumberOfPages;
  }

  /**
   * Sets the degree of the item, i. e. the type of degree which is received with this type of
   * publication
   *
   * @param newVal newVal
   */
  public void setDegree(DegreeType newVal) {
    this.degree = newVal;
  }

  /**
   * Sets the event of the item. Some items are related to an event, e.g. a conference or a lecture
   * series.
   *
   * @param newVal newVal
   */
  public void setEvent(EventVO newVal) {
    this.event = newVal;
  }

  /**
   * JUS Sets the legal case of the item. Items of genre types case note has mandatory legal case
   * information.
   */
  public void setLegalCase(LegalCaseVO newVal) {
    this.legalCase = newVal;
  }

  /**
   * Sets the genre of the item, i. e. the type of the publication (e. g. article, book, conference
   * paper).
   *
   * @param newVal newVal
   */
  public void setGenre(MdsPublicationVO.Genre newVal) {
    this.genre = newVal;
  }

  /**
   * Sets the location of the item, i. e. the name of the library where the item is currently
   * located.
   *
   * @param newVal newVal
   */
  public void setLocation(String newVal) {
    this.location = newVal;
  }

  /**
   * Sets the publication info of the item, i. e. the institution which published the item and
   * additional information, e. g. the publisher name and place of a book, or the university where a
   * thesis has been created.
   *
   * @param newVal newVal
   */
  public void setPublishingInfo(PublishingInfoVO newVal) {
    this.publishingInfo = newVal;
  }

  /**
   * Sets the review method of the item, i. e. the type of the scientific review process for the
   * described item.
   *
   * @param newVal newVal
   */
  public void setReviewMethod(ReviewMethod newVal) {
    this.reviewMethod = newVal;
  }

  /**
   * Sets the table of contents of the item.
   *
   * @param newVal
   */
  public void setTableOfContents(String newVal) {
    this.tableOfContents = newVal;
  }

  /**
   * Sets the number of pages of the item. Note: The pages of an item published in a bundle is part
   * of the source container.
   *
   * @param newVal newVal
   */
  public void setTotalNumberOfPages(String newVal) {
    this.totalNumberOfPages = newVal;
  }

  public final MdsPublicationVO clone() {
    return new MdsPublicationVO(this);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((null == this.abstracts) ? 0 : this.abstracts.hashCode());
    result = prime * result + ((null == this.alternativeTitles) ? 0 : this.alternativeTitles.hashCode());
    result = prime * result + ((null == this.creators) ? 0 : this.creators.hashCode());
    result = prime * result + ((null == this.dateAccepted) ? 0 : this.dateAccepted.hashCode());
    result = prime * result + ((null == this.dateCreated) ? 0 : this.dateCreated.hashCode());
    result = prime * result + ((null == this.dateModified) ? 0 : this.dateModified.hashCode());
    result = prime * result + ((null == this.datePublishedInPrint) ? 0 : this.datePublishedInPrint.hashCode());
    result = prime * result + ((null == this.datePublishedOnline) ? 0 : this.datePublishedOnline.hashCode());
    result = prime * result + ((null == this.dateSubmitted) ? 0 : this.dateSubmitted.hashCode());
    result = prime * result + ((null == this.degree) ? 0 : this.degree.hashCode());
    result = prime * result + ((null == this.event) ? 0 : this.event.hashCode());
    result = prime * result + ((null == this.freeKeywords) ? 0 : this.freeKeywords.hashCode());
    result = prime * result + ((null == this.genre) ? 0 : this.genre.hashCode());
    result = prime * result + ((null == this.identifiers) ? 0 : this.identifiers.hashCode());
    result = prime * result + ((null == this.languages) ? 0 : this.languages.hashCode());
    result = prime * result + ((null == this.legalCase) ? 0 : this.legalCase.hashCode());
    result = prime * result + ((null == this.location) ? 0 : this.location.hashCode());
    result = prime * result + ((null == this.projectInfo) ? 0 : this.projectInfo.hashCode());
    result = prime * result + ((null == this.publishingInfo) ? 0 : this.publishingInfo.hashCode());
    result = prime * result + ((null == this.reviewMethod) ? 0 : this.reviewMethod.hashCode());
    result = prime * result + ((null == this.sources) ? 0 : this.sources.hashCode());
    result = prime * result + ((null == this.subjects) ? 0 : this.subjects.hashCode());
    result = prime * result + ((null == this.tableOfContents) ? 0 : this.tableOfContents.hashCode());
    result = prime * result + ((null == this.totalNumberOfPages) ? 0 : this.totalNumberOfPages.hashCode());
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

    if (null == this.abstracts) {
      if (null != other.abstracts)
        return false;
    } else if (null == other.abstracts)
      return false;
    else if (!new HashSet<>(this.abstracts).containsAll(other.abstracts) //
        || !new HashSet<>(other.abstracts).containsAll(this.abstracts)) {
      return false;
    }

    if (null == this.alternativeTitles) {
      if (null != other.alternativeTitles)
        return false;
    } else if (null == other.alternativeTitles)
      return false;
    else if (!new HashSet<>(this.alternativeTitles).containsAll(other.alternativeTitles) //
        || !new HashSet<>(other.alternativeTitles).containsAll(this.alternativeTitles)) {
      return false;
    }

    if (null == this.creators) {
      if (null != other.creators)
        return false;
    } else if (null == other.creators)
      return false;
    else if (!new HashSet<>(this.creators).containsAll(other.creators) //
        || !new HashSet<>(other.creators).containsAll(this.creators)) {
      return false;
    }

    if (null == this.dateAccepted) {
      if (null != other.dateAccepted)
        return false;
    } else if (!this.dateAccepted.equals(other.dateAccepted))
      return false;

    if (null == this.dateCreated) {
      if (null != other.dateCreated)
        return false;
    } else if (!this.dateCreated.equals(other.dateCreated))
      return false;

    if (null == this.dateModified) {
      if (null != other.dateModified)
        return false;
    } else if (!this.dateModified.equals(other.dateModified))
      return false;

    if (null == this.datePublishedInPrint) {
      if (null != other.datePublishedInPrint)
        return false;
    } else if (!this.datePublishedInPrint.equals(other.datePublishedInPrint))
      return false;

    if (null == this.datePublishedOnline) {
      if (null != other.datePublishedOnline)
        return false;
    } else if (!this.datePublishedOnline.equals(other.datePublishedOnline))
      return false;

    if (null == this.dateSubmitted) {
      if (null != other.dateSubmitted)
        return false;
    } else if (!this.dateSubmitted.equals(other.dateSubmitted))
      return false;

    if (this.degree != other.degree)
      return false;

    if (null == this.event) {
      if (null != other.event)
        return false;
    } else if (!this.event.equals(other.event))
      return false;

    if (null == this.freeKeywords) {
      if (null != other.freeKeywords)
        return false;
    } else if (!this.freeKeywords.equals(other.freeKeywords))
      return false;

    if (this.genre != other.genre)
      return false;

    if (null == this.identifiers) {
      if (null != other.identifiers)
        return false;
    } else if (null == other.identifiers)
      return false;
    else if (!new HashSet<>(this.identifiers).containsAll(other.identifiers)
        || !new HashSet<>(other.identifiers).containsAll(this.identifiers)) {
      return false;
    }

    if (null == this.languages) {
      if (null != other.languages)
        return false;
    } else if (null == other.languages)
      return false;
    else if (!new HashSet<>(this.languages).containsAll(other.languages) //
        || !new HashSet<>(other.languages).containsAll(this.languages)) {
      return false;
    }

    if (null == this.legalCase) {
      if (null != other.legalCase)
        return false;
    } else if (!this.legalCase.equals(other.legalCase))
      return false;

    if (null == this.location) {
      if (null != other.location)
        return false;
    } else if (!this.location.equals(other.location))
      return false;

    if (null == this.projectInfo) {
      if (null != other.projectInfo)
        return false;
    } else if (!this.projectInfo.equals(other.projectInfo))
      return false;

    if (null == this.publishingInfo) {
      if (null != other.publishingInfo)
        return false;
    } else if (!this.publishingInfo.equals(other.publishingInfo))
      return false;

    if (this.reviewMethod != other.reviewMethod)
      return false;

    if (null == this.sources) {
      if (null != other.sources)
        return false;
    } else if (null == other.sources)
      return false;
    else if (!new HashSet<>(this.sources).containsAll(other.sources) //
        || !new HashSet<>(other.sources).containsAll(this.sources)) {
      return false;
    }

    if (null == this.subjects) {
      if (null != other.subjects)
        return false;
    } else if (null == other.subjects)
      return false;
    else if (!new HashSet<>(this.subjects).containsAll(other.subjects) //
        || !new HashSet<>(other.subjects).containsAll(this.subjects)) {
      return false;
    }

    if (null == this.tableOfContents) {
      if (null != other.tableOfContents)
        return false;
    } else if (!this.tableOfContents.equals(other.tableOfContents))
      return false;

    if (null == this.totalNumberOfPages) {
      if (null != other.totalNumberOfPages)
        return false;
    } else if (!this.totalNumberOfPages.equals(other.totalNumberOfPages))
      return false;

    return true;
  }

  /**
   * Delivers the date when the item was accepted (for scientific check).
   */
  public String getDateAccepted() {
    return this.dateAccepted;
  }

  /**
   * Delivers the date when the item was created.
   */
  public String getDateCreated() {
    return this.dateCreated;
  }

  /**
   * Delivers the date when the item was modified.
   */
  public String getDateModified() {
    return this.dateModified;
  }

  /**
   * Delivers the date when the item was published.
   */
  public String getDatePublishedInPrint() {
    return this.datePublishedInPrint;
  }

  /**
   * Delivers the date when the item was submitted.
   */
  public String getDateSubmitted() {
    return this.dateSubmitted;
  }

  /**
   * Delivers the list of languages of the item. Every language attribute is a valid ISO Language
   * Code. These codes are the lower- case, two-letter codes as defined by ISO-639. You can find a
   * full list of these codes at a number of sites, such as:
   * http://www.loc.gov/standards/iso639-2/englangn.html
   */
  public java.util.List<String> getLanguages() {
    return this.languages;
  }

  /**
   * Sets the date when the item was accepted (for scientific check).
   *
   * @param newVal
   */
  public void setDateAccepted(String newVal) {
    if (null == newVal || newVal.isEmpty()) {
      this.dateAccepted = null;
    } else {
      this.dateAccepted = newVal;
    }

  }

  /**
   * Sets the date when the item was created.
   *
   * @param newVal
   */
  public void setDateCreated(String newVal) {
    if (null == newVal || newVal.isEmpty()) {
      this.dateCreated = null;
    } else {
      this.dateCreated = newVal;
    }

  }

  /**
   * Sets the date when the item was modified.
   *
   * @param newVal
   */
  public void setDateModified(String newVal) {
    if (null == newVal || newVal.isEmpty()) {
      this.dateModified = null;
    } else {
      this.dateModified = newVal;
    }

  }

  /**
   * Sets the date when the item was published.
   *
   * @param newVal
   */
  public void setDatePublishedInPrint(String newVal) {
    if (null == newVal || newVal.isEmpty()) {
      this.datePublishedInPrint = null;
    } else {
      this.datePublishedInPrint = newVal;
    }
  }

  /**
   * Sets the date when the item was submitted.
   *
   * @param newVal
   */
  public void setDateSubmitted(String newVal) {
    if (null == newVal || newVal.isEmpty()) {
      this.dateSubmitted = null;
    } else {
      this.dateSubmitted = newVal;
    }
  }

  /**
   * Delivers the list of abstracts or short descriptions of the item.
   */
  public java.util.List<AbstractVO> getAbstracts() {
    return this.abstracts;
  }

  public String getFreeKeywords() {
    return this.freeKeywords;
  }

  public void setFreeKeywords(String freeKeywords) {
    this.freeKeywords = freeKeywords;
  }

  /**
   * @return the datePublishedOnline
   */
  public String getDatePublishedOnline() {
    return this.datePublishedOnline;
  }

  /**
   *
   * @param newVal
   */
  public void setDatePublishedOnline(String newVal) {
    if (null == newVal || newVal.isEmpty()) {
      this.datePublishedOnline = null;
    } else {
      this.datePublishedOnline = newVal;
    }
  }

  public List<ProjectInfoVO> getProjectInfo() {
    return this.projectInfo;
  }

  public void setProjectInfo(List<ProjectInfoVO> projectInfo) {
    this.projectInfo = projectInfo;
  }
}
