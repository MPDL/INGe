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

package de.mpg.escidoc.services.common.valueobjects.comparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.MdsPublicationVO.Genre;
import de.mpg.escidoc.services.common.valueobjects.MdsPublicationVO.ReviewMethod;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;

/**
 * The class implements the comparison of two PubItemVOs for the criteria which
 * has to be given in the constructor. Note: this comparator imposes orderings
 * that are inconsistent with equals.
 * @revised by MuJ: 27.08.2007
 * @author Peter Broszeit (initial creation)
 * @version $Revision: 665 $ $LastChangedDate: 2007-07-09 16:4
 * @updated 04-Sep-2007 11:45:03
 */
public class PubItemVOComparator implements Comparator<PubItemVO>
{
    public enum Criteria
    {
        TITLE, EVENT_TITLE, SOURCE_TITLE, GENRE, DATE, CREATOR, SOURCE_CREATOR, PUBLISHING_INFO, REVIEW_METHOD
    }

    private static final int LESS = -1;
    private static final int EQUAL = 0;
    private static final int GREATER = 1;

    private Criteria criteria;

    /**
     * Creates a new instance with the given criteria.
     * 
     * @param criteria    criteria
     */
    public PubItemVOComparator(Criteria criteria)
    {
        if (criteria == null)
        {
            throw new IllegalArgumentException("Criteria must not be null.");
        }
        this.criteria = criteria;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(PubItemVO pubItem1, PubItemVO pubItem2)
    {
        if (pubItem1 == null)
        {
            throw new IllegalArgumentException("First argument is null.");
        }
        if (pubItem2 == null)
        {
            throw new IllegalArgumentException("Second argument is null.");
        }
        int rc = 0;
        switch (criteria)
        {
            case TITLE:
                rc = compareTitle(pubItem1, pubItem2);
                break;
            case EVENT_TITLE:
                rc = compareEventTitle(pubItem1, pubItem2);
                break;
            case SOURCE_TITLE:
                rc = compareSourceTitle(pubItem1, pubItem2);
                break;
            case GENRE:
                rc = compareGenre(pubItem1, pubItem2);
                break;
            case DATE:
                rc = compareDate(pubItem1, pubItem2);
                break;
            case CREATOR:
                rc = compareCreator(pubItem1, pubItem2);
                break;
            case SOURCE_CREATOR:
                rc = compareSourceCreator(pubItem1, pubItem2);
                break;
            case PUBLISHING_INFO:
                rc = comparePublishingInfo(pubItem1, pubItem2);
                break;
            case REVIEW_METHOD:
                rc = compareReviewMethod(pubItem1, pubItem2);
                break;
            default:
                assert false : "illegal criteria";
        }
        return rc;
    }

    /**
     * This method is for the comparison of the titles of the sources of two PubItemVOs. Both items must not be null. If
     * no source is given (is null) it is greater than a given source.
     * 
     * @param pubItem1 The first publication item.
     * @param pubItem2 The second publication item.
     * @return 0 if the items are equal, -1 if the first argument is less than the second, 1 otherwise.
     */
    private int compareSourceTitle(PubItemVO pubItem1, PubItemVO pubItem2)
    {
        SourceVO source1 = null;
        SourceVO source2 = null;
        if (pubItem1.getMetadata().getSources().size() > 0)
        {
            source1 = pubItem1.getMetadata().getSources().get(0);
        }
        if (pubItem2.getMetadata().getSources().size() > 0)
        {
            source2 = pubItem2.getMetadata().getSources().get(0);
        }
        if (source1 == null && source2 == null)
        {
            return EQUAL;
        }
        if (source1 == null)
        {
            return GREATER;
        }
        if (source2 == null)
        {
            return LESS;
        }
        TextVO title1 = source1.getTitle();
        TextVO title2 = source2.getTitle();
        String value1 = title1.getValue();
        String value2 = title2.getValue();
        return value1.compareToIgnoreCase(value2);
    }

    /**
     * This method compares the publisher names of the publihing info metadata of two PubItemVOs. Both pubItems must not
     * be null. If no publisher name is given (is null) it is greater than a given publisher name.
     * 
     * @param pubItem1 The first publication item.
     * @param pubItem2 The second publication item.
     * @return 0 if the items are equal, -1 if the first argument is less than the second, 1 otherwise.
     */
    private int comparePublishingInfo(PubItemVO pubItem1, PubItemVO pubItem2)
    {
        // get publisher names
        String publisherName1 = null;
        if (pubItem1.getMetadata().getPublishingInfo() != null)
        {
            publisherName1 = pubItem1.getMetadata().getPublishingInfo().getPublisher();
        }
        String publisherName2 = null;
        if (pubItem2.getMetadata().getPublishingInfo() != null)
        {
            publisherName2 = pubItem2.getMetadata().getPublishingInfo().getPublisher();
        }
        if (publisherName1 == null && publisherName2 == null)
        {
            return EQUAL;
        }
        if (publisherName1 == null)
        {
            return GREATER;
        }
        if (publisherName2 == null)
        {
            return LESS;
        }
        // use string compare for alphabetically order
        return publisherName1.compareToIgnoreCase(publisherName2);
    }

    /**
     * This method compares the title metadata of two PubItemVOs. Both pubItems must not be null.
     * 
     * @param pubItem1 The first publication item.
     * @param pubItem2 The second publication item.
     * @return 0 if the items are equal, -1 if the first argument is less than the second, 1 otherwise.
     */
    private int compareTitle(PubItemVO pubItem1, PubItemVO pubItem2)
    {
        String title1 = pubItem1.getMetadata().getTitle().getValue();
        String title2 = pubItem2.getMetadata().getTitle().getValue();
        return title1.compareToIgnoreCase(title2);
    }

    /**
     * This method compares the creators of the sources of two PubItemVOs. Both items must not be null.
     * If no source is given (is null) it is greater than a given source.
     * 
     * @param pubItem1 The first publication item.
     * @param pubItem2 The second publication item.
     * @return 0 if the items are equal, -1 if the first argument is less than the second, 1 otherwise.
     */
    private int compareSourceCreator(PubItemVO pubItem1, PubItemVO pubItem2)
    {
        SourceVO source1 = null;
        SourceVO source2 = null;
        if (pubItem1.getMetadata().getSources().size() > 0)
        {
            source1 = pubItem1.getMetadata().getSources().get(0);
        }
        if (pubItem2.getMetadata().getSources().size() > 0)
        {
            source2 = pubItem2.getMetadata().getSources().get(0);
        }
        if (source1 == null && source2 == null)
        {
            return EQUAL;
        }
        if (source1 == null)
        {
            return GREATER;
        }
        if (source2 == null)
        {
            return LESS;
        }
        CreatorVO creator1 = null;
        CreatorVO creator2 = null;
        if (source1.getCreators().size() > 0)
        {
            creator1 = source1.getCreators().get(0);
        }
        if (source2.getCreators().size() > 0)
        {
            creator2 = source2.getCreators().get(0);
        }
        if (creator1 == null && creator2 == null)
        {
            return EQUAL;
        }
        if (creator1 == null)
        {
            return GREATER;
        }
        if (creator2 == null)
        {
            return LESS;
        }
        String value1 = null;
        String value2 = null;
        if (creator1.getPerson() != null)
        {
            value1 = creator1.getPerson().getFamilyName();
        }
        else if (creator1.getOrganization() != null)
        {
            if (creator1.getOrganization().getName() != null)
            {
                value1 = creator1.getOrganization().getName().getValue();
            }
        }
        if (creator2.getPerson() != null)
        {
            value2 = creator2.getPerson().getFamilyName();
        }
        else if (creator2.getOrganization() != null)
        {
            if (creator2.getOrganization().getName() != null)
            {
                value2 = creator2.getOrganization().getName().getValue();
            }
        }
        if (value1 == null && value2 == null)
        {
            return EQUAL;
        }
        if (value1 == null)
        {
            return GREATER;
        }
        if (value2 == null)
        {
            return LESS;
        }
        return value1.compareToIgnoreCase(value2);
    }

    /**
     * This methods compares the review methods of two PubItemVOs. Both items must not be null. If no review type is
     * given (is null) it is greater than a review type .
     * 
     * @param pubItem1 The first publication item.
     * @param pubItem2 The second publication item.
     * @return 0 if the items are equal, -1 if the first argument is less than the second, 1 otherwise.
     */
    private int compareReviewMethod(PubItemVO pubItem1, PubItemVO pubItem2)
    {
        ReviewMethod method1 = pubItem1.getMetadata().getReviewMethod();
        ReviewMethod method2 = pubItem2.getMetadata().getReviewMethod();
        if (method1 == null && method2 == null)
        {
            return EQUAL;
        }
        if (method1 == null)
        {
            return GREATER;
        }
        if (method2 == null)
        {
            return LESS;
        }
        String value1 = method1.name();
        String value2 = method2.name();
        return value1.compareToIgnoreCase(value2);
    }

    /**
     * This method compares the genre of two PubItemVOs.
     * 
     * @param pubItem1 The first publication item.
     * @param pubItem2 The second publication item.
     * @return 0 if the items are equal, -1 if the first argument is less than the second, 1 otherwise.
     */
    private int compareGenre(PubItemVO pubItem1, PubItemVO pubItem2)
    {
        Genre genre1 = pubItem1.getMetadata().getGenre();
        Genre genre2 = pubItem2.getMetadata().getGenre();
        // Use string compare for alphabetically order.
        // The use of Enum.compareTo() would result in the order of the genre enum declarations
        return genre1.name().compareToIgnoreCase(genre2.name());
    }

    /**
     * This method compares the titles of the events of two PubItemVOs. Both items must not be null. If no event is
     * given (is null) it is greater than a given event.
     * 
     * @param pubItem1 The first publication item.
     * @param pubItem2 The second publication item.
     * @return 0 if the items are equal, -1 if the first argument is less than the second, 1 otherwise.
     */
    private int compareEventTitle(PubItemVO pubItem1, PubItemVO pubItem2)
    {
        EventVO event1 = pubItem1.getMetadata().getEvent();
        EventVO event2 = pubItem2.getMetadata().getEvent();
        if (event1 == null && event2 == null)
        {
            return EQUAL;
        }
        if (event1 == null)
        {
            return GREATER;
        }
        if (event2 == null)
        {
            return LESS;
        }
        TextVO title1 = event1.getTitle();
        TextVO title2 = event2.getTitle();
        String value1 = title1.getValue();
        String value2 = title2.getValue();
        return value1.compareToIgnoreCase(value2);

    }

    /**
     * This method compares the creators of two PubItemVOs. Both items must not be null.
     * 
     * @param pubItem1 The first publication item.
     * @param pubItem2 The second publication item.
     * @return 0 if the items are equal, -1 if the first argument is less than the second, 1 otherwise.
     */
    private int compareCreator(PubItemVO pubItem1, PubItemVO pubItem2)
    {
        // get creators
        String creatorName1 = getCreatorName(pubItem1);
        String creatorName2 = getCreatorName(pubItem2);
        if (creatorName1 == null && creatorName2 == null)
        {
            return EQUAL;
        }
        if (creatorName1 == null)
        {
            return GREATER;
        }
        if (creatorName2 == null)
        {
            return LESS;
        }
        // use string compare for alphabetically order
        return creatorName1.compareToIgnoreCase(creatorName2);
    }

    /**
     * Retrieves the creator name of an item. Helper method for compareCreator.
     *
     * @param pubItem The ItemVO.
     * @return The name of the first creator.
     */
    private String getCreatorName(PubItemVO pubItem)
    {
        String creatorname;
        CreatorVO creator = pubItem.getMetadata().getCreators().get(0);
        if (creator.getPerson() != null)
        {
            creatorname = creator.getPerson().getFamilyName();
        }
        else
        // if(creator.getOrganization() != null)
        {
            assert creator.getOrganization() != null : "Invalid pubItem: Neither person nor organization is set in creator object.";
            creatorname = creator.getOrganization().getName() == null ? null : creator.getOrganization().getName().getValue();
        }
        return creatorname;
    }

    /**
     * This method compares the dates of two PubItemVOs using the most recent date in the metadata record. Both items
     * must not be null. If no date is given (is null) it is greater than a given date.
     * 
     * @param pubItem1 The first publication item.
     * @param pubItem2 The second publication item.
     * @return 0 if the items are equal, -1 if the first argument is less than the second, 1 otherwise.
     */
    private int compareDate(PubItemVO pubItem1, PubItemVO pubItem2)
    {
        // get all dates
        List<String> datesPubItem1 = getDates(pubItem1);
        List<String> datesPubItem2 = getDates(pubItem2);
        if (datesPubItem1.size() == 0 && datesPubItem2.size() == 0)
        {
            return EQUAL;
        }
        if (datesPubItem1.size() == 0)
        {
            return GREATER;
        }
        if (datesPubItem2.size() == 0)
        {
            return LESS;
        }
        // get most recent date of each pubItem
        Collections.sort(datesPubItem1);
        Collections.sort(datesPubItem2);
        String mostRecentDatePubItem1 = datesPubItem1.get(datesPubItem1.size() - 1);
        String mostRecentDatePubItem2 = datesPubItem2.get(datesPubItem2.size() - 1);
        return mostRecentDatePubItem1.compareTo(mostRecentDatePubItem2);
    }

    /**
     * Retrieves a list of all dates of an item. Helper method for compareDate.
     */
    private List<String> getDates(final PubItemVO pubItem)
    {
        List<String> dates = new ArrayList<String>();

        // Last modified
//        if (pubItem.getModificationDate() != null)
//        {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            dates.add(sdf.format(pubItem.getModificationDate()));
//        }

        // metadata dates
        if (pubItem.getMetadata().getDateAccepted() != null)
        {
            dates.add(pubItem.getMetadata().getDateAccepted());
        }
        if (pubItem.getMetadata().getDateCreated() != null)
        {
            dates.add(pubItem.getMetadata().getDateCreated());
        }
        if (pubItem.getMetadata().getDateModified() != null)
        {
            dates.add(pubItem.getMetadata().getDateModified());
        }
        if (pubItem.getMetadata().getDatePublishedInPrint() != null)
        {
            dates.add(pubItem.getMetadata().getDatePublishedInPrint());
        }
        if (pubItem.getMetadata().getDatePublishedOnline() != null)
        {
            dates.add(pubItem.getMetadata().getDatePublishedOnline());
        }
        if (pubItem.getMetadata().getDateSubmitted() != null)
        {
            dates.add(pubItem.getMetadata().getDateSubmitted());
        }
        return dates;
    }
}
