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

package test.common.valueobjects.comparator;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.mpg.escidoc.services.common.valueobjects.comparator.PubItemVOComparator;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;

/**
 * Test cases for PubItemVOComparator with criterion EVENT_TITLE.
 *
 * @author Peter (initial creation)
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $
 * Revised by BrP: 03.09.2007
 */
public class EventTitleComparatorTest extends ComparatorTestBase
{
    private static Logger logger = Logger.getLogger(EventTitleComparatorTest.class); 

    /**
     * Test for sorting ascending.
     */
    @Test
    public void sortEventTitleAscending()
    {
        ArrayList<PubItemVO> list = getPubItemList();
        Collections.sort(list, new PubItemVOComparator(PubItemVOComparator.Criteria.EVENT_TITLE)) ;
        for (PubItemVO itemVO : list)
        {
            EventVO event = null;
            if(itemVO.getMetadata().getEvent() != null)
            {
                event = itemVO.getMetadata().getEvent();
            }
            
            logger.debug((event!=null?event.getTitle().getValue():"null") + " ("+itemVO.getVersion().getObjectId()+")");
        }
        String[] expectedIdOrder = new String[]{"2","1","1","3","4"}; 
        assertObjectIdOrder(list, expectedIdOrder);
    }
    
    /**
     * Test for sorting descending.
     */
    @Test
    public void sortEventTitleDescending()
    {
        ArrayList<PubItemVO> list = getPubItemList();
        Collections.sort(list, Collections.reverseOrder(new PubItemVOComparator(PubItemVOComparator.Criteria.EVENT_TITLE))) ;
        for (PubItemVO itemVO : list)
        {
            EventVO event = null;
            if(itemVO.getMetadata().getEvent() != null)
            {
                event = itemVO.getMetadata().getEvent();
            }
            
            logger.debug((event!=null?event.getTitle().getValue():"null") + " ("+itemVO.getVersion().getObjectId()+")");
        }
        String[] expectedIdOrder = new String[]{"4","3","1","1","2"}; 
        assertObjectIdOrder(list, expectedIdOrder);
    }
    
    /**
     * Test for comoparing two null values.
     */
     @Test
    public void compareTwoNullValues()
    {
        int rc = new PubItemVOComparator(PubItemVOComparator.Criteria.EVENT_TITLE).compare(getPubItemVO4(), getPubItemVO4());
        assertEquals(0, rc);
    }
}
