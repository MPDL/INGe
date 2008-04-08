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

package test.common.valueobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.mpg.escidoc.services.common.referenceobjects.AccountUserRO;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.ItemRefFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.RoleFilter;

/**
 * Test cases for FilterTaskParamVO. 
 *
 * @author Miriam Doelle (initial creation)
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $
 * Revised by BrP: 03.09.2007
 */
public class FilterTaskParamVOTest
{
    /**
     * Test method for {@link de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO#getFilterList()}.
     */
    @Test
    public void testGetFilter()
    {
        FilterTaskParamVO filter = new FilterTaskParamVO();
        FilterTaskParamVO.Filter f1 = filter.new RoleFilter("Depositor", new AccountUserRO("objectId911"));
        ItemRefFilter f2 = filter.new ItemRefFilter();
        f2.getIdList().add(new ItemRO("escidoc:item3"));
        f2.getIdList().add(new ItemRO("escidoc:item4"));
        filter.getFilterList().add(f1);
        filter.getFilterList().add(f2);
        assertEquals(2,filter.getFilterList().size());
        FilterTaskParamVO.Filter f1b = filter.getFilterList().get(0);
        assertTrue(RoleFilter.class.isAssignableFrom(f1b.getClass()));
        assertEquals("Depositor", ((RoleFilter)f1b).getRole());
        FilterTaskParamVO.Filter f2b = filter.getFilterList().get(1);
        assertTrue(ItemRefFilter.class.isAssignableFrom(f2b.getClass()));
        assertEquals(2, ((ItemRefFilter)f2b).getIdList().size());
    }
}
