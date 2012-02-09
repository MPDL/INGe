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
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package test.valueobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.services.common.referenceobjects.AccountUserRO;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.ItemRefFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.LimitFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.OffsetFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.OrderFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.RoleFilter;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;

/**
 * Test cases for FilterTaskParamVO. 
 *
 * @author Miriam Doelle (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
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
    
    @Test
    public void testToMapOneItem()
    {
        FilterTaskParamVO filter = new FilterTaskParamVO();
        
        ItemRefFilter itemFilter = filter.new ItemRefFilter();
        itemFilter.getIdList().add(new ItemRO("escidoc:item3"));
        
        filter.getFilterList().add(itemFilter);

        assertEquals(1,filter.getFilterList().size());
        
        HashMap<String, String[]> map = filter.toMap();
        String[] q = map.get("query");
        assertTrue(q[0].trim().length() == new String("{ \"/id\"=escidoc:item3 }").length());
        assertTrue(q[0].trim().equals("( \"/id\"=escidoc:item3 )"));
    }
    
    @Test
    public void testToMapTwoItemsAndRoleFilter()
    {
        FilterTaskParamVO filter = new FilterTaskParamVO();
        
        FilterTaskParamVO.Filter f1 = filter.new RoleFilter("Depositor", new AccountUserRO("objectId911"));
        ItemRefFilter f2 = filter.new ItemRefFilter();
        f2.getIdList().add(new ItemRO("escidoc:item3"));
        f2.getIdList().add(new ItemRO("escidoc:item4"));
        
        filter.getFilterList().add(f1);
        filter.getFilterList().add(f2); 
        
        HashMap<String, String[]> map = filter.toMap();
        String[] q = map.get("query");
        String s = q[0].trim();
        System.out.println(s);
        assertTrue(s.equals("( \"/role\"=Depositor and \"/user\"=objectId911 )  and  ( \"/id\"=escidoc:item3 or \"/id\"=escidoc:item4 )"));
    }

    @Test
    public void testToMapOrderAndLimitFilter()
    {
        FilterTaskParamVO filter = new FilterTaskParamVO();
        
        ItemRefFilter f1 = filter.new ItemRefFilter();
        f1.getIdList().add(new ItemRO("escidoc:item3"));
        
        filter.getFilterList().add(f1);
        
        Filter f2 = filter.new OrderFilter("/properties/context/title", OrderFilter.ORDER_ASCENDING);
        filter.getFilterList().add(f2);
        
        Filter f3 = filter.new LimitFilter(String.valueOf("20"));
        filter.getFilterList().add(f3);
        
        Filter f4 = filter.new OffsetFilter(String.valueOf("5"));
        filter.getFilterList().add(f4);
        
        HashMap<String, String[]> map = filter.toMap();
        
        String[] q1 = map.get("query");
        String s = q1[0].trim();
        assertTrue(s.contains("\"/id\"=escidoc:item3"));
        
        String[] q2 = map.get("query");
        s = q2[0].trim();
        assertTrue(s.contains("\"/properties/context/title\"/sort.ascending"));
        
        String[] q3 = map.get("maximumRecords");
        s = q3[0].trim();
        assertTrue(s.equals("20"));
        
        String[] q4 = map.get("startRecord");
        s = q4[0].trim();
        assertTrue(s.equals("6"));
    }
    
    @Test
    public void testToMapSeveralFilters()
    {
        FilterTaskParamVO filter = new FilterTaskParamVO();
        
        Filter f1 = filter.new OwnerFilter(new AccountUserRO("max"));
        filter.getFilterList().add(0, f1);
        
        Filter f2 = filter.new FrameworkItemTypeFilter("escidoc:1001");
        filter.getFilterList().add(f2);
        
        // all public status except withdrawn
        Filter f4 = filter.new ItemPublicStatusFilter(PubItemVO.State.IN_REVISION);
        filter.getFilterList().add(0, f4);
        Filter f5 = filter.new ItemPublicStatusFilter(PubItemVO.State.PENDING);
        filter.getFilterList().add(0, f5);
        Filter f6 = filter.new ItemPublicStatusFilter(PubItemVO.State.SUBMITTED);
        filter.getFilterList().add(0, f6);
        Filter f7 = filter.new ItemPublicStatusFilter(PubItemVO.State.RELEASED);
        filter.getFilterList().add(0, f7);
        
        
        Filter f8 = filter.new LimitFilter("10");
        filter.getFilterList().add(f8);
        
        Filter f9 = filter.new OffsetFilter("0");
        filter.getFilterList().add(f9);
        
        Filter f10 = filter.new OrderFilter("/properties/context/title", OrderFilter.ORDER_ASCENDING);
        filter.getFilterList().add(f10);
        
        HashMap<String, String[]> map = filter.toMap();
        
        String[] q1 = map.get("query");
        String s = q1[0].trim();

        assertTrue(s.contains("( \"/properties/created-by/id\"=max )  and"));
        assertTrue(s.contains("\"/properties/public-status\"=released or "));
        assertTrue(s.contains("\"/properties/public-status\"=submitted"));
        
        String[] q3 = map.get("maximumRecords");
        s = q3[0].trim();

        assertTrue(s.equals("10"));
        
        String[] q4 = map.get("startRecord");
        s = q4[0].trim();

        assertTrue(s.equals("1"));

    }
   
    @Test
    public void testRefIdWithSortingFilter()
    {
        FilterTaskParamVO filter = new FilterTaskParamVO();
        
        ItemRefFilter f1 = filter.new ItemRefFilter();

        f1.getIdList().add(new ItemRO("escidoc:item1"));
        f1.getIdList().add(new ItemRO("escidoc:item2"));
        filter.getFilterList().add(0, f1);
        
        Filter f10 = filter.new OrderFilter("/properties/context/title", OrderFilter.ORDER_DESCENDING);
        filter.getFilterList().add(f10);
        Filter f8 = filter.new LimitFilter(String.valueOf(10));
        filter.getFilterList().add(f8);
        Filter f9 = filter.new OffsetFilter(String.valueOf(0));
        filter.getFilterList().add(f9);
        
        HashMap<String, String[]> map = filter.toMap();
        
        String[] q1 = map.get("query");
        String s = q1[0].trim();
        
        assertTrue(s.startsWith("("));
    }
}
