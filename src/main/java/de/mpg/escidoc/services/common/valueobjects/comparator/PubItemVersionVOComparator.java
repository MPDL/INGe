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

import java.util.Comparator;
import java.util.Date;

import de.mpg.escidoc.services.common.valueobjects.PubItemVersionVO;

/**
 * The class implements the comparison of two PubItemVOs for the criteria which
 * has to be given in the constructor. Note: this comparator imposes orderings
 * that are inconsistent with equals.
 * 
 * @revised by MuJ: 27.08.2007
 * @author Johannes Mueller (initial creation)
 * @version $Revision: 611 $ $LastChangedDate: 2007-07-09 16:4
 */
public class PubItemVersionVOComparator implements Comparator<PubItemVersionVO>
{
    /**
     * The possible sorting criteria.
     */
    public enum Criteria
    {
        DATE
    }
    
    /**
     * The possible sorting orders.
     */
    public enum Order
    {
        ASCENDING, DESCENDING
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
    public PubItemVersionVOComparator(Criteria criteria)
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
    public int compare(PubItemVersionVO pubItemVersion1, PubItemVersionVO pubItemVersion2)
    {
        if (pubItemVersion1 == null)
        {
            throw new IllegalArgumentException("First argument is null.");
        }
        if (pubItemVersion2 == null)
        {
            throw new IllegalArgumentException("Second argument is null.");
        }
        int rc = 0;
        switch (criteria)
        {
            case DATE:
                rc = compareDate(pubItemVersion1, pubItemVersion2);
                break;
            default:
                assert false : "illegal criteria";
        }
        return rc;
    }

    
    /**
     * This method compares the dates of two PubItemVersionVOs using the modificationDate attribute. Both itemVersions
     * must not be null. If no date is given (is null) it is greater than a given date.
     * 
     * @param pubItemVersion1 The first pubItem version.
     * @param pubItemVersion2 The second pubItem version.
     * @return 0 if the item versions are equal, -1 if the first argument is less than the second, 1 otherwise.
     */
    private int compareDate(PubItemVersionVO pubItemVersion1, PubItemVersionVO pubItemVersion2)
    {
        Date pubItemVersion1date = pubItemVersion1.getModificationDate();
        Date pubItemVersion2date = pubItemVersion2.getModificationDate();
        if (pubItemVersion1date == null && pubItemVersion2date == null)
        {
            return EQUAL;
        }
        if (pubItemVersion1date == null)
        {
            return GREATER;
        }
        if (pubItemVersion2date == null)
        {
            return LESS;
        }
        return pubItemVersion1date.compareTo(pubItemVersion2date);
    }
}
