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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
@JsonInclude(value = Include.NON_EMPTY)
public class SourceVO extends ValueObject implements Cloneable {
  private String title;
  private final java.util.List<AlternativeTitleVO> alternativeTitles = new java.util.ArrayList<>();
  private final java.util.List<CreatorVO> creators = new java.util.ArrayList<>();
  private String volume;
  private String issue;
  private Date datePublishedInPrint;
  private String startPage;
  private String endPage;
  private String sequenceNumber;
  private PublishingInfoVO publishingInfo;
  private final java.util.List<IdentifierVO> identifiers = new java.util.ArrayList<>();
  private final java.util.List<SourceVO> sources = new java.util.ArrayList<>();
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
    return uri;
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
    return uri;
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
    return title;
  }

  /**
   * Sets the title of the source, e.g. the title of the journal or the book.
   *
   * @param newVal
   */
  public void setTitle(String newVal) {
    title = newVal;
  }

  /**
   * Delivers the list of alternative titles of the source. The source may have one or several other
   * forms of the title.
   */
  public java.util.List<AlternativeTitleVO> getAlternativeTitles() {
    return alternativeTitles;
  }

  /**
   * Delivers the volume of the source in which the described item was published in.
   */
  public String getVolume() {
    return volume;
  }

  /**
   * Sets the volume of the source in which the described item was published in.
   *
   * @param newVal
   */
  public void setVolume(String newVal) {
    volume = newVal;
  }

  /**
   * Delivers the issue of the source in which the described item was published in.
   */
  public String getIssue() {
    return issue;
  }

  /**
   * Sets the issue of the source in which the described item was published in.
   *
   * @param newVal
   */
  public void setIssue(String newVal) {
    issue = newVal;
  }

  /**
   * Delivers the page where the described item starts.
   */
  public String getStartPage() {
    return startPage;
  }

  /**
   * Sets the page where the described item starts.
   *
   * @param newVal
   */
  public void setStartPage(String newVal) {
    startPage = newVal;
  }

  /**
   * Delivers the page where the described item ends.
   */
  public String getEndPage() {
    return endPage;
  }

  /**
   * Sets the page where the described item ends.
   *
   * @param newVal
   */
  public void setEndPage(String newVal) {
    endPage = newVal;
  }

  /**
   * Delivers the sequence number, i. e. the number of the described item within the source.
   */
  public String getSequenceNumber() {
    return sequenceNumber;
  }

  /**
   * Sets the sequence number, i. e. the number of the described item within the source.
   *
   * @param newVal
   */
  public void setSequenceNumber(String newVal) {
    sequenceNumber = newVal;
  }

  /**
   * Delivers the publishing info, i. e. the institution which published the item and additional
   * information, e.g. the publisher name and place of a book or the university where an theses has
   * been created.
   */
  public PublishingInfoVO getPublishingInfo() {
    return publishingInfo;
  }

  /**
   * Sets the publishing info, i. e. the institution which published the item and additional
   * information, e.g. the publisher name and place of a book or the university where an theses has
   * been created.
   *
   * @param newVal
   */
  public void setPublishingInfo(PublishingInfoVO newVal) {
    publishingInfo = newVal;
  }

  /**
   * Delivers the list of creators of the source, e.g. the editor of a book or a book series.
   */
  public java.util.List<CreatorVO> getCreators() {
    return creators;
  }

  /**
   * Delivers the list of sources, i. e. bundles in which the source has been published, e.g. a
   * series.
   */
  public java.util.List<SourceVO> getSources() {
    return sources;
  }

  /**
   * Delivers the genre of the source.
   */
  public Genre getGenre() {
    return genre;
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

  public SourceVO clone() {
    try {
      SourceVO clone = (SourceVO) super.clone();
      for (AlternativeTitleVO title : this.alternativeTitles) {
        clone.alternativeTitles.add((AlternativeTitleVO) title.clone());
      }
      for (CreatorVO creator : this.creators) {
        clone.creators.add((CreatorVO) creator.clone());
      }
      for (IdentifierVO identifier : this.identifiers) {
        clone.identifiers.add((IdentifierVO) identifier.clone());
      }
      for (SourceVO source : this.sources) {
        clone.sources.add((SourceVO) source.clone());
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
    result = prime * result + ((alternativeTitles == null) ? 0 : alternativeTitles.hashCode());
    result = prime * result + ((creators == null) ? 0 : creators.hashCode());
    result = prime * result + ((datePublishedInPrint == null) ? 0 : datePublishedInPrint.hashCode());
    result = prime * result + ((endPage == null) ? 0 : endPage.hashCode());
    result = prime * result + ((genre == null) ? 0 : genre.hashCode());
    result = prime * result + ((identifiers == null) ? 0 : identifiers.hashCode());
    result = prime * result + ((issue == null) ? 0 : issue.hashCode());
    result = prime * result + ((publishingInfo == null) ? 0 : publishingInfo.hashCode());
    result = prime * result + ((sequenceNumber == null) ? 0 : sequenceNumber.hashCode());
    result = prime * result + ((sources == null) ? 0 : sources.hashCode());
    result = prime * result + ((startPage == null) ? 0 : startPage.hashCode());
    result = prime * result + ((title == null) ? 0 : title.hashCode());
    result = prime * result + ((totalNumberOfPages == null) ? 0 : totalNumberOfPages.hashCode());
    result = prime * result + ((volume == null) ? 0 : volume.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;

    if (obj == null)
      return false;

    if (getClass() != obj.getClass())
      return false;

    SourceVO other = (SourceVO) obj;

    if (alternativeTitles == null) {
      if (other.alternativeTitles != null)
        return false;
    } else if (other.alternativeTitles == null)
      return false;
    else if (!alternativeTitles.containsAll(other.alternativeTitles) //
        || !other.alternativeTitles.containsAll(alternativeTitles)) {
      return false;
    }

    if (creators == null) {
      if (other.creators != null)
        return false;
    } else if (other.creators == null)
      return false;
    else if (!creators.containsAll(other.creators) //
        || !other.creators.containsAll(creators)) {
      return false;
    }

    if (datePublishedInPrint == null) {
      if (other.datePublishedInPrint != null)
        return false;
    } else if (!datePublishedInPrint.equals(other.datePublishedInPrint))
      return false;

    if (endPage == null) {
      if (other.endPage != null)
        return false;
    } else if (!endPage.equals(other.endPage))
      return false;

    if (genre != other.genre)
      return false;

    if (identifiers == null) {
      if (other.identifiers != null)
        return false;
    } else if (other.identifiers == null)
      return false;
    else if (!identifiers.containsAll(other.identifiers) //
        || !other.identifiers.containsAll(identifiers)) {
      return false;
    }

    if (issue == null) {
      if (other.issue != null)
        return false;
    } else if (!issue.equals(other.issue))
      return false;

    if (publishingInfo == null) {
      if (other.publishingInfo != null)
        return false;
    } else if (!publishingInfo.equals(other.publishingInfo))
      return false;

    if (sequenceNumber == null) {
      if (other.sequenceNumber != null)
        return false;
    } else if (!sequenceNumber.equals(other.sequenceNumber))
      return false;

    if (sources == null) {
      if (other.sources != null)
        return false;
    } else if (other.sources == null)
      return false;
    else if (!sources.containsAll(other.sources) //
        || !other.sources.containsAll(sources)) {
      return false;
    }

    if (startPage == null) {
      if (other.startPage != null)
        return false;
    } else if (!startPage.equals(other.startPage))
      return false;

    if (title == null) {
      if (other.title != null)
        return false;
    } else if (!title.equals(other.title))
      return false;

    if (totalNumberOfPages == null) {
      if (other.totalNumberOfPages != null)
        return false;
    } else if (!totalNumberOfPages.equals(other.totalNumberOfPages))
      return false;

    if (volume == null) {
      if (other.volume != null)
        return false;
    } else if (!volume.equals(other.volume))
      return false;

    return true;
  }

  public void setDatePublishedInPrint(Date datePublishedInPrint) {
    this.datePublishedInPrint = datePublishedInPrint;
  }

  public Date getDatePublishedInPrint() {
    return datePublishedInPrint;
  }
}
