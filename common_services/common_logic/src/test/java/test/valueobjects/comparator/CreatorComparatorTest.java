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

package test.valueobjects.comparator;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.mpg.escidoc.services.common.valueobjects.comparator.PubItemVOComparator;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;

/**
 * Test cases for PubItemVOComparator with criterion CREATOR.
 *
 * @author Peter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * Revised by BrP: 03.09.2007
 */
public class CreatorComparatorTest extends ComparatorTestBase
{
    private static Logger logger = Logger.getLogger(CreatorComparatorTest.class); 

    private String getCreatorName(PubItemVO pubItem)
    {
        String creatorname = null;
        CreatorVO creator = pubItem.getMetadata().getCreators().get(0);
        if(creator.getPerson() != null)
        {
            creatorname = creator.getPerson().getFamilyName();
        }
        else //if(creator.getOrganization() != null)
        {
            if (creator.getOrganization().getName() != null)
            {
                creatorname = creator.getOrganization().getName().getValue();
            }
        }
        return creatorname;
    }

    /**
     * Test for sorting ascending.
     */
    @Test
    public void sortCreatorAscending()
    {
        ArrayList<PubItemVO> list = getPubItemList();
        Collections.sort(list, new PubItemVOComparator(PubItemVOComparator.Criteria.CREATOR)) ;
        for (PubItemVO itemVO : list)
        {
            logger.debug(getCreatorName(itemVO )+ " ("+itemVO.getVersion().getObjectId()+")");
        }
        String[] expectedIdOrder = new String[]{"3","2","1","1","4"}; 
        assertObjectIdOrder(list, expectedIdOrder);
    }
    
    /**
     * Test for sorting descending.
     */
    @Test
    public void sortCreatorDescending()
    {
        ArrayList<PubItemVO> list = getPubItemList();
        Collections.sort(list, Collections.reverseOrder(new PubItemVOComparator(PubItemVOComparator.Criteria.CREATOR))) ;
        for (PubItemVO itemVO : list)
        {
            logger.debug(getCreatorName(itemVO) + " ("+itemVO.getVersion().getObjectId()+")");
        }
        String[] expectedIdOrder = new String[]{"4","1","1","2","3"}; 
        assertObjectIdOrder(list, expectedIdOrder);
    }
}
