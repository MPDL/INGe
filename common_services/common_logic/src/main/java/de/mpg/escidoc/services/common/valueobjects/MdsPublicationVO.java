/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.common.valueobjects;

import de.mpg.escidoc.services.common.valueobjects.interfaces.TitleIF;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PublishingInfoVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;

/**
 * The metadata of a Publication.
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision: 632 $ $LastChangedDate: 2007-11-21 12:45:14 +0100 (Wed, 21 Nov 2007) $ by $Author: pbroszei $
 * @updated 21-Nov-2007 11:48:44
 */
public class MdsPublicationVO extends MetadataSetVO implements Cloneable, TitleIF
{
    /**
     * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
     * 'de.mpg.escidoc.services.common.valueobjects.ItemVO; local class incompatible: stream classdesc
     * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286' that occur after
     * JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to be compiled twice for testing (once
     * for the Application Server, once for the local test).
     * 
     * @author Johannes Mueller
     */
    

    	/**
	 * The possible degree types for an item.
	 * @updated 21-Nov-2007 11:48:44
	 */
    public enum DegreeType
    {
        MASTER, DIPLOMA, MAGISTER, PHD, STAATSEXAMEN, HABILITATION
    }

    	/**
	 * The possible review methods for an item.
	 * @updated 21-Nov-2007 11:48:44
	 */
    public enum ReviewMethod
    {
        INTERNAL, PEER, NO_REVIEW
    }

    /**
     * The possible genres for an item.
     */
    public enum Genre
    {
        ARTICLE, BOOK, BOOK_ITEM, PROCEEDINGS, CONFERENCE_PAPER, TALK_AT_EVENT, CONFERENCE_REPORT, POSTER, COURSEWARE_LECTURE, THESIS, PAPER, REPORT, ISSUE, JOURNAL, MANUSCRIPT, SERIES, OTHER
    }

    /**
     * Alternative titles of the publication, e.g. translations of original title or sub-titles.
     */
    private java.util.List<TextVO> alternativeTitles = new java.util.ArrayList<TextVO>();
    /**
     * Persons and organizations who essentially participated in creating the content with a specific task, e.g. author,
     * translator, editor.
     */
    private java.util.List<CreatorVO> creators = new java.util.ArrayList<CreatorVO>();
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
     * The genre of a publication describes the type of the publication.
     */
    private Genre genre;
    /**
     * Identifiers referencing the described item, e.g. the ISBN, Report-Number.
     */
    private java.util.List<IdentifierVO> identifiers = new java.util.ArrayList<IdentifierVO>();
    /**
	 * The language attribute is a valid ISO Language Code.  These codes are the lower-
	 * case, two-letter codes as defined by ISO-639. You can find a full list of these
	 * codes at a number of sites, such as: http://www.loc.gov/standards/iso639-
	 * 2/englangn.html
	 */
    private java.util.List<String> languages = new java.util.ArrayList<String>();
    /**
     * The name of the library where the item is currently located.
     */
    private String location;
    /**
	 * The institution which published the item and additional information, e.g. the
	 * publisher name and place of a book, or the university where a theses has been
	 * created. 
	 */
    private PublishingInfoVO publishingInfo;
    /**
     * The type of the scientific review process for the described item.
     */
    private ReviewMethod reviewMethod;
    /**
     * The bundles in which the item has been published, e.g. journals, books, series or databases.
     */
    private java.util.List<SourceVO> sources = new java.util.ArrayList<SourceVO>();
    /**
     * Free keywords.
     */
    private TextVO subject;
    private TextVO tableOfContents;
    /**
     * The number of pages of the described item. Note: The pages of an item published in a bundle is part of the source
     * container.
     */
    private String totalNumberOfPages;
    /**
     * Abstracts or short descriptions of the item.
     */
    private java.util.List<TextVO> abstracts = new java.util.ArrayList<TextVO>();

    /**
     * Creates a new instance.
     */
    public MdsPublicationVO()
    {
        super();
    }

    /**
     * Copy constructor.
     * 
     * @param other The instance to copy.
     */
    public MdsPublicationVO(MdsPublicationVO other)
    {
        super(other);
        for (TextVO altTitle : other.getAlternativeTitles())
        {
            getAlternativeTitles().add((TextVO)altTitle.clone());
        }
        for (CreatorVO creator : other.getCreators())
        {
            getCreators().add((CreatorVO)creator.clone());
        }
        if (other.getDateAccepted() != null)
        {
            setDateAccepted(other.getDateAccepted());
        }
        if (other.getDateCreated() != null)
        {
            setDateCreated(other.getDateCreated());
        }
        if (other.getDateModified() != null)
        {
            setDateModified(other.getDateModified());
        }
        if (other.getDatePublishedInPrint() != null)
        {
            setDatePublishedInPrint(other.getDatePublishedInPrint());
        }
        // DiT, 14.11.2007: added DatePublishedOnline
        if (other.getDatePublishedOnline() != null)
        {
            setDatePublishedOnline(other.getDatePublishedOnline());
        }
        if (other.getDateSubmitted() != null)
        {
            setDateSubmitted(other.getDateSubmitted());
        }
        setDegree(other.getDegree());
        if (other.getEvent() != null)
        {
            setEvent((EventVO)other.getEvent().clone());
        }
        setGenre(other.getGenre());
        for (IdentifierVO identifier : other.getIdentifiers())
        {
            getIdentifiers().add((IdentifierVO)identifier.clone());
        }
        getLanguages().addAll(other.getLanguages());
        setLocation(other.getLocation());
        if (other.getPublishingInfo() != null)
        {
            setPublishingInfo((PublishingInfoVO)other.getPublishingInfo().clone());
        }
        setReviewMethod(other.getReviewMethod());
        for (SourceVO source : other.getSources())
        {
            getSources().add((SourceVO)source.clone());
        }
        if (other.getSubject() != null)
        {
            setSubject((TextVO)other.getSubject().clone());
        }
        for (TextVO summary : other.getAbstracts())
        {
            getAbstracts().add((TextVO)summary.clone());
        }
        if (other.getTableOfContents() != null)
        {
            setTableOfContents((TextVO)other.getTableOfContents().clone());
        }
        setTotalNumberOfPages(other.getTotalNumberOfPages());
    }

    /**
     * Delivers the list of alternative titles of the item, e.g. translations of original title or sub-titles.
     */
    public java.util.List<TextVO> getAlternativeTitles()
    {
        return alternativeTitles;
    }

    /**
     * Delivers the list of creators of the item, i. e. any person or organization who essentially participated in
     * creating the content with a specific task, e. g. author, translator, editor.
     */
    public java.util.List<CreatorVO> getCreators()
    {
        return creators;
    }

    /**
     * Delivers the degree of the item, i. e. the type of degree which is received with this type of publication
     */
    public DegreeType getDegree()
    {
        return degree;
    }

    /**
     * Delivers the event of the item. Some items are related to an event, e.g. a conference or a lecture series.
     */
    public EventVO getEvent()
    {
        return event;
    }

    /**
     * Delivers the genre of the item, i. e. the type of the publication (e. g. article, book, conference paper).
     */
    public MdsPublicationVO.Genre getGenre()
    {
        return genre;
    }

    /**
     * Delivers the list of identifiers of the item, i. e. identifiers referencing the described item, e. g. the ISBN,
     * report number.
     */
    public java.util.List<IdentifierVO> getIdentifiers()
    {
        return identifiers;
    }

    /**
     * Delivers the location of the item, i. e. the name of the library where the item is currently located.
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * Delivers the publication info of the item, i. e. the institution which published the item and additional
     * information, e. g. the publisher name and place of a book, or the university where a thesis has been created.
     */
    public PublishingInfoVO getPublishingInfo()
    {
        return publishingInfo;
    }

    /**
     * Delivers the review method of the item, i. e. the type of the scientific review process for the described item.
     */
    public ReviewMethod getReviewMethod()
    {
        return reviewMethod;
    }

    /**
     * Delivers the list of sources of the item, i. e. the bundles in which the item has been published, e. g. journals,
     * books, series or databases.
     */
    public java.util.List<SourceVO> getSources()
    {
        return sources;
    }

    /**
	 * Free keywords.
	 */
    public TextVO getSubject()
    {
        return subject;
    }

    /**
     * Delivers the table of contents of the item.
     */
    public TextVO getTableOfContents()
    {
        return tableOfContents;
    }

    /**
     * Delivers the number of pages of the item. Note: The pages of an item published in a bundle is part of the source
     * container.
     */
    public String getTotalNumberOfPages()
    {
        return totalNumberOfPages;
    }

    /**
     * Sets the degree of the item, i. e. the type of degree which is received with this type of publication
     * 
     * @param newVal newVal
     */
    public void setDegree(DegreeType newVal)
    {
        degree = newVal;
    }

    /**
     * Sets the event of the item. Some items are related to an event, e.g. a conference or a lecture series.
     * 
     * @param newVal newVal
     */
    public void setEvent(EventVO newVal)
    {
        event = newVal;
    }

    /**
     * Sets the genre of the item, i. e. the type of the publication (e. g. article, book, conference paper).
     * 
     * @param newVal newVal
     */
    public void setGenre(MdsPublicationVO.Genre newVal)
    {
        genre = newVal;
    }

    /**
     * Sets the location of the item, i. e. the name of the library where the item is currently located.
     * 
     * @param newVal newVal
     */
    public void setLocation(String newVal)
    {
        location = newVal;
    }

    /**
     * Sets the publication info of the item, i. e. the institution which published the item and additional information,
     * e. g. the publisher name and place of a book, or the university where a thesis has been created.
     * 
     * @param newVal newVal
     */
    public void setPublishingInfo(PublishingInfoVO newVal)
    {
        publishingInfo = newVal;
    }

    /**
     * Sets the review method of the item, i. e. the type of the scientific review process for the described item.
     * 
     * @param newVal newVal
     */
    public void setReviewMethod(ReviewMethod newVal)
    {
        reviewMethod = newVal;
    }

    /**
	 * Free keywords.
	 * 
	 * @param newVal
	 */
    public void setSubject(TextVO newVal)
    {
        subject = newVal;
    }

    /**
	 * Sets the table of contents of the item.
	 * 
	 * @param newVal
	 */
    public void setTableOfContents(TextVO newVal)
    {
        tableOfContents = newVal;
    }

    /**
     * Sets the number of pages of the item. Note: The pages of an item published in a bundle is part of the source
     * container.
     * 
     * @param newVal newVal
     */
    public void setTotalNumberOfPages(String newVal)
    {
        totalNumberOfPages = newVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MdsPublicationVO clone()
    {
        return new MdsPublicationVO(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(getClass().isAssignableFrom(obj.getClass())))
        {
            return false;
        }
        MdsPublicationVO other = (MdsPublicationVO)obj;
        return equals(getTitle(), other.getTitle()) && equals(getAlternativeTitles(), other.getAlternativeTitles())
                && equals(getCreators(), other.getCreators()) && equals(getDateAccepted(), other.getDateAccepted())
                && equals(getDateCreated(), other.getDateCreated())
                && equals(getDateModified(), other.getDateModified())
                && equals(getDatePublishedInPrint(), other.getDatePublishedInPrint())
                // DiT, 14.11.2007: added DatePublishedOnline
                && equals(getDatePublishedOnline(), other.getDatePublishedOnline())
                && equals(getDateSubmitted(), other.getDateSubmitted()) && equals(getDegree(), other.getDegree())
                && equals(getEvent(), other.getEvent()) && equals(getGenre(), other.getGenre())
                && equals(getIdentifiers(), other.getIdentifiers()) && equals(getLanguages(), other.getLanguages())
                && equals(getLocation(), other.getLocation()) && equals(getPublishingInfo(), other.getPublishingInfo())
                && equals(getReviewMethod(), other.getReviewMethod()) && equals(getSources(), other.getSources())
                && equals(getSubject(), other.getSubject()) && equals(getAbstracts(), other.getAbstracts())
                && equals(getTableOfContents(), other.getTableOfContents())
                && equals(getTotalNumberOfPages(), other.getTotalNumberOfPages());
    }

    /**
     * Delivers the date when the item was accepted (for scientific check).
     */
    public String getDateAccepted()
    {
        return dateAccepted;
    }

    /**
     * Delivers the date when the item was created.
     */
    public String getDateCreated()
    {
        return dateCreated;
    }

    /**
     * Delivers the date when the item was modified.
     */
    public String getDateModified()
    {
        return dateModified;
    }

    /**
     * Delivers the date when the item was published.
     */
    public String getDatePublishedInPrint()
    {
        return datePublishedInPrint;
    }

    /**
     * Delivers the date when the item was submitted.
     */
    public String getDateSubmitted()
    {
        return dateSubmitted;
    }

    /**
	 * Delivers the list of languages of the item. Every language attribute is a valid
	 * ISO Language Code. These codes are the lower- case, two-letter codes as defined
	 * by ISO-639. You can find a full list of these codes at a number of sites, such
	 * as:
	 * http://www.loc.gov/standards/iso639-2/englangn.html
	 */
    public java.util.List<String> getLanguages()
    {
        return languages;
    }

    /**
	 * Sets the date when the item was accepted (for scientific check).
	 * 
	 * @param newVal
	 */
    public void setDateAccepted(String newVal)
    {
        dateAccepted = newVal;
    }

    /**
	 * Sets the date when the item was created.
	 * 
	 * @param newVal
	 */
    public void setDateCreated(String newVal)
    {
        dateCreated = newVal;
    }

    /**
	 * Sets the date when the item was modified.
	 * 
	 * @param newVal
	 */
    public void setDateModified(String newVal)
    {
        dateModified = newVal;
    }

    /**
	 * Sets the date when the item was published.
	 * 
	 * @param newVal
	 */
    public void setDatePublishedInPrint(String newVal)
    {
        datePublishedInPrint = newVal;
    }

    /**
	 * Sets the date when the item was submitted.
	 * 
	 * @param newVal
	 */
    public void setDateSubmitted(String newVal)
    {
        dateSubmitted = newVal;
    }

    /**
     * Delivers the list of abstracts or short descriptions of the item.
     */
    public java.util.List<TextVO> getAbstracts()
    {
        return abstracts;
    }

    /**
     * @return the datePublishedOnline
     */
    public String getDatePublishedOnline()
    {
        return datePublishedOnline;
    }

    /**
	 * 
	 * @param newVal
	 */
    public void setDatePublishedOnline(String newVal)
    {
        this.datePublishedOnline = newVal;
    }
}