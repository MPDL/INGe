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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.mpg.escidoc.services.cone.util.LocalizedString;
import de.mpg.escidoc.services.cone.util.LocalizedTripleObject;
import de.mpg.escidoc.services.cone.util.TreeFragment;

/**
 * Tests for the Util classes.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class UtilTest
{
    @Test
    public void testLocalizedTripleObjectEquals()
    {
        LocalizedString localizedString1 = new LocalizedString("Test $tring1", "en");
        LocalizedString localizedString2 = new LocalizedString("Test $tring1", "en");
        
        TreeFragment treeFragment1 = new TreeFragment("id1");
        List<LocalizedTripleObject> list1 = new ArrayList<LocalizedTripleObject>();
        list1.add(localizedString1);
        treeFragment1.put("property1", list1);
        
        TreeFragment treeFragment2 = new TreeFragment("id1");
        List<LocalizedTripleObject> list2 = new ArrayList<LocalizedTripleObject>();
        list2.add(localizedString2);
        treeFragment2.put("property1", list2);
        
        assertEquals(localizedString1, localizedString2);
        assertEquals(treeFragment1, treeFragment2);
        
        localizedString2.setLanguage("de");
        
        assertFalse(localizedString1.equals(localizedString2));
        assertFalse(treeFragment1.equals(treeFragment2));
        
        localizedString2.setLanguage("en");
        localizedString2.setValue("Test $tring2");
        
        assertFalse(localizedString1.equals(localizedString2));
        assertFalse(treeFragment1.equals(treeFragment2));
        
        localizedString2.setValue("Test $tring1");
        treeFragment2.setLanguage("en");
        
        assertEquals(localizedString1, localizedString2);
        assertFalse(treeFragment1.equals(treeFragment2));
        
        treeFragment2.setLanguage(null);
        treeFragment2.setSubject("id2");
        
        assertEquals(localizedString1, localizedString2);
        assertFalse(treeFragment1.equals(treeFragment2));
        
        treeFragment2.setSubject("id1");
        
        assertEquals(localizedString1, localizedString2);
        assertEquals(treeFragment1, treeFragment2);
        
        treeFragment2.remove("property1");
        treeFragment2.put("property2", list2);
        
        assertEquals(localizedString1, localizedString2);
        assertFalse(treeFragment1.equals(treeFragment2));
    }
    
}
