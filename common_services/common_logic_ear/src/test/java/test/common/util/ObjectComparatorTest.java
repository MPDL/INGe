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

package test.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.apache.log4j.Logger;
import org.junit.Test;

import test.common.TestBase;
import de.mpg.escidoc.services.common.util.ObjectComparator;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;

/**
 * Test class for {@link de.mpg.escidoc.services.common.util.ObjectComparator}
 * 
 * @author Johannes Mueller (initial creation)
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $
 * @revised by MuJ: 03.09.2007
 */
public class ObjectComparatorTest extends TestBase
{
    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(ObjectComparatorTest.class);

    /**
     * @throws Exception
     */
    @Test
    public void testCompare() throws Exception
    {
        MdsPublicationVO o1 = getMdsPublication1();
        MdsPublicationVO o2 = getMdsPublication1();
        o2.getTitle().setValue("This is a real new title.");
        o2.setEvent(null);
        o1.setDatePublishedInPrint(new Date().toString());
        o1.getSources().get(0).getCreators().get(0).setRole(CreatorRole.COMMENTATOR);
        ObjectComparator oc = new ObjectComparator(o1, o2);
        logger.debug("testCompare() - " + oc.toString());
        assertEquals(4, oc.getDiffs().size());
    }

    /**
     * @throws Exception
     */
    @Test
    public void testCompareStrings() throws Exception
    {
        ObjectComparator oc = new ObjectComparator("This is a real new title.", "Das is was andres");
        logger.debug("testCompareStrings() - " + oc.toString());
        assertEquals(1, oc.getDiffs().size());
    }

    /**
     * @throws Exception
     */
    @Test
    public void testCompareNull() throws Exception
    {
        ObjectComparator oc = new ObjectComparator(null, null);
        logger.debug("testCompareNull() - " + oc.toString());
        assertTrue(oc.toString(), oc.isEqual());
    }
}
