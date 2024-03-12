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

package de.mpg.mpdl.inge.model.valueobjects.metadata;

import java.util.Date;
import java.util.HashSet;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

/**
 * Some items are published as part of a bundle, e.g. a journal, a book, a series or a database. The
 * source container includes descriptive elements of the superordinate element.
 *
 * @revised by MuJ: 27.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 22-Okt-2007 14:35:53
 */
@SuppressWarnings("serial")
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class SourceVO extends ValueObject implements Cloneable {
  private String title;
  private java.util.List<AlternativeTitleVO> alternativeTitles = new java.util.ArrayList<>();
  private java.util.List<CreatorVO> creators = new java.util.ArrayList<>();
  private String volume;
  private String issue;
  private Date datePublishedInPrint;
  private String startPage;
  private String endPage;
  private String sequenceNumber;
  private PublishingInfoVO publishingInfo;
  private java.util.List<IdentifierVO> identifiers = new java.util.ArrayList<>();
  private java.util.List<SourceVO> sources = new java.util.ArrayList<>();
  private Genre genre;
  private String totalNumberOfPages;

  /**
   * The possible genres for an source.
   */
  public enum Genre
  {
    BLOG("http://purl.org/eprint/type/blog"), //
    BOOK("http://purl.org/eprint/type/Book"), //
    PROCEEDINGS("http://purl.org/escidoc/metadata/ves/publication-types/proceedings"), //
    JOURNAL("http://purl.org/escidoc/metadata/ves/publication-types/journal"), //
    ISSUE("http://purl.org/escidoc/metadata/ves/publication-types/issue"), //
    RADIO_BROADCAST("http://purl.org/escidoc/metadata/ves/publication-types/radio-broadcast"),
    SERIES("http://purl.org/escidoc/metadata/ves/publication-types/series"),
    TV_BROADCAST("http://purl.org/escidoc/metadata/ves/publication-types/tv-broadcast"),
    WEB_PAGE("http://purl.org/escidoc/metadata/ves/publication-types/web-page"),

    // JUS
    NEWSPAPER("http://purl.org/escidoc/metadata/ves/publication-types/newspaper"), //
    ENCYCLOPEDIA("http://purl.org/escidoc/metadata/ves/publication-types/encyclopedia"), //
    MULTI_VOLUME("http://purl.org/escidoc/metadata/ves/publication-types/multi-volume"), //
    COMMENTARY("http://purl.org/escidoc/metadata/ves/publication-types/commentary"), //
    HANDBOOK("http://purl.org/escidoc/metadata/ves/publication-types/handbook"), //
    COLLECTED_EDITION("http://purl.org/escidoc/metadata/ves/publication-types/collected-edition"), //
    FESTSCHRIFT("http://purl.org/escidoc/metadata/ves/publication-types/festschrift");

  private final String uri;

  Genre(String uri) {
      this.uri = uri;
    }

  public String getUri() {
    return this.uri;
  }}

  /**
   * The possible genres for an source.
   */
  public enum AlternativeTitleType{

  ABBREVIATION("http://purl.org/escidoc/metadata/terms/0.1/ABBREVIATION"), //
    HTML("http://purl.org/escidoc/metadata/terms/0.1/HTML"), //
    LATEX("http://purl.org/escidoc/metadata/terms/0.1/LATEX"), //
    MATHML("http://purl.org/escidoc/metadata/terms/0.1/MATHML"), //
    SUBTITLE("http://purl.org/escidoc/metadata/terms/0.1/SUBTITLE"), //
    OTHER("http://purl.org/escidoc/metadata/terms/0.1/OTHER");

  private final String uri;

  AlternativeTitleType(String uri) {
      this.uri = uri;
    }

  public String getUri() {
    return this.uri;
  }

  }

  /**
   * Creates a new instance.
   */
  public SourceVO() {}

  /**
   * Creates a new instance with the given title.
   *
   * @param title
   */
  public SourceVO(String title) {
    this.title = title;
  }

  /**
   * Delivers the title of the source, e.g. the title of the journal or the book.
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * Sets the title of the source, e.g. the title of the journal or the book.
   *
   * @param newVal
   */
  public void setTitle(String newVal) {
    this.title = newVal;
  }

  /**
   * Delivers the list of alternative titles of the source. The source may have one or several other
   * forms of the title.
   */
  public java.util.List<AlternativeTitleVO> getAlternativeTitles() {
    return this.alternativeTitles;
  }

  /**
   * Delivers the volume of the source in which the described item was published in.
   */
  public String getVolume() {
    return this.volume;
  }

  /**
   * Sets the volume of the source in which the described item was published in.
   *
   * @param newVal
   */
  public void setVolume(String newVal) {
    this.volume = newVal;
  }

  /**
   * Delivers the issue of the source in which the described item was published in.
   */
  public String getIssue() {
    return this.issue;
  }

  /**
   * Sets the issue of the source in which the described item was published in.
   *
   * @param newVal
   */
  public void setIssue(String newVal) {
    this.issue = newVal;
  }

  /**
   * Delivers the page where the described item starts.
   */
  public String getStartPage() {
    return this.startPage;
  }

  /**
   * Sets the page where the described item starts.
   *
   * @param newVal
   */
  public void setStartPage(String newVal) {
    this.startPage = newVal;
  }

  /**
   * Delivers the page where the described item ends.
   */
  public String getEndPage() {
    return this.endPage;
  }

  /**
   * Sets the page where the described item ends.
   *
   * @param newVal
   */
  public void setEndPage(String newVal) {
    this.endPage = newVal;
  }

  /**
   * Delivers the sequence number, i. e. the number of the described item within the source.
   */
  public String getSequenceNumber() {
    return this.sequenceNumber;
  }

  /**
   * Sets the sequence number, i. e. the number of the described item within the source.
   *
   * @param newVal
   */
  public void setSequenceNumber(String newVal) {
    this.sequenceNumber = newVal;
  }

  /**
   * Delivers the publishing info, i. e. the institution which published the item and additional
   * information, e.g. the publisher name and place of a book or the university where an theses has
   * been created.
   */
  public PublishingInfoVO getPublishingInfo() {
    return this.publishingInfo;
  }

  /**
   * Sets the publishing info, i. e. the institution which published the item and additional
   * information, e.g. the publisher name and place of a book or the university where an theses has
   * been created.
   *
   * @param newVal
   */
  public void setPublishingInfo(PublishingInfoVO newVal) {
    this.publishingInfo = newVal;
  }

  /**
   * Delivers the list of creators of the source, e.g. the editor of a book or a book series.
   */
  public java.util.List<CreatorVO> getCreators() {
    return this.creators;
  }

  /**
   * Delivers the list of sources, i. e. bundles in which the source has been published, e.g. a
   * series.
   */
  public java.util.List<SourceVO> getSources() {
    return this.sources;
  }

  /**
   * Delivers the genre of the source.
   */
  public Genre getGenre() {
    return this.genre;
  }

  /**
   * Sets the genre of the source.
   *
   * @param newVal
   */
  public void setGenre(Genre newVal) {
    this.genre = newVal;
  }

  /**
   * Delivers the list of external Identifier of the source, e.g. ISSN, ISBN, URI.
   */
  public java.util.List<IdentifierVO> getIdentifiers() {
    return this.identifiers;
  }

  public String getTotalNumberOfPages() {
    return this.totalNumberOfPages;
  }

  public void setTotalNumberOfPages(String totalNumberOfPages) {
    this.totalNumberOfPages = totalNumberOfPages;
  }

  public final SourceVO clone() {
    try {
      SourceVO clone = (SourceVO) super.clone();
      clone.alternativeTitles = new java.util.ArrayList<>();
      for (AlternativeTitleVO title : this.alternativeTitles) {
        clone.alternativeTitles.add(title.clone());
      }
      clone.creators = new java.util.ArrayList<>();
      for (CreatorVO creator : this.creators) {
        clone.creators.add(creator.clone());
      }
      clone.identifiers = new java.util.ArrayList<>();
      for (IdentifierVO identifier : this.identifiers) {
        clone.identifiers.add(identifier.clone());
      }
      clone.sources = new java.util.ArrayList<>();
      for (SourceVO source : this.sources) {
        clone.sources.add(source.clone());
      }
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((null == this.alternativeTitles) ? 0 : this.alternativeTitles.hashCode());
    result = prime * result + ((null == this.creators) ? 0 : this.creators.hashCode());
    result = prime * result + ((null == this.datePublishedInPrint) ? 0 : this.datePublishedInPrint.hashCode());
    result = prime * result + ((null == this.endPage) ? 0 : this.endPage.hashCode());
    result = prime * result + ((null == this.genre) ? 0 : this.genre.hashCode());
    result = prime * result + ((null == this.identifiers) ? 0 : this.identifiers.hashCode());
    result = prime * result + ((null == this.issue) ? 0 : this.issue.hashCode());
    result = prime * result + ((null == this.publishingInfo) ? 0 : this.publishingInfo.hashCode());
    result = prime * result + ((null == this.sequenceNumber) ? 0 : this.sequenceNumber.hashCode());
    result = prime * result + ((null == this.sources) ? 0 : this.sources.hashCode());
    result = prime * result + ((null == this.startPage) ? 0 : this.startPage.hashCode());
    result = prime * result + ((null == this.title) ? 0 : this.title.hashCode());
    result = prime * result + ((null == this.totalNumberOfPages) ? 0 : this.totalNumberOfPages.hashCode());
    result = prime * result + ((null == this.volume) ? 0 : this.volume.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;

    if (null == obj)
      return false;

    if (getClass() != obj.getClass())
      return false;

    SourceVO other = (SourceVO) obj;

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

    if (null == this.datePublishedInPrint) {
      if (null != other.datePublishedInPrint)
        return false;
    } else if (!this.datePublishedInPrint.equals(other.datePublishedInPrint))
      return false;

    if (null == this.endPage) {
      if (null != other.endPage)
        return false;
    } else if (!this.endPage.equals(other.endPage))
      return false;

    if (this.genre != other.genre)
      return false;

    if (null == this.identifiers) {
      if (null != other.identifiers)
        return false;
    } else if (null == other.identifiers)
      return false;
    else if (!new HashSet<>(this.identifiers).containsAll(other.identifiers) //
        || !new HashSet<>(other.identifiers).containsAll(this.identifiers)) {
      return false;
    }

    if (null == this.issue) {
      if (null != other.issue)
        return false;
    } else if (!this.issue.equals(other.issue))
      return false;

    if (null == this.publishingInfo) {
      if (null != other.publishingInfo)
        return false;
    } else if (!this.publishingInfo.equals(other.publishingInfo))
      return false;

    if (null == this.sequenceNumber) {
      if (null != other.sequenceNumber)
        return false;
    } else if (!this.sequenceNumber.equals(other.sequenceNumber))
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

    if (null == this.startPage) {
      if (null != other.startPage)
        return false;
    } else if (!this.startPage.equals(other.startPage))
      return false;

    if (null == this.title) {
      if (null != other.title)
        return false;
    } else if (!this.title.equals(other.title))
      return false;

    if (null == this.totalNumberOfPages) {
      if (null != other.totalNumberOfPages)
        return false;
    } else if (!this.totalNumberOfPages.equals(other.totalNumberOfPages))
      return false;

    if (null == this.volume) {
      if (null != other.volume)
        return false;
    } else if (!this.volume.equals(other.volume))
      return false;

    return true;
  }

  public void setDatePublishedInPrint(Date datePublishedInPrint) {
    this.datePublishedInPrint = datePublishedInPrint;
  }

  public Date getDatePublishedInPrint() {
    return this.datePublishedInPrint;
  }
}
