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

package test.referenceobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.mpg.escidoc.services.common.referenceobjects.AccountUserRO;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;

/**
 * Test for class ReferenceObject.
 *
 * @author Miriam Doelle (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $20.09.2007
 * @revised by MuJ: 20.09.2007
 */
public class ReferenceObjectTest
{
    /**
     * Test method for {@link de.mpg.escidoc.services.common.referenceobjects.ReferenceObject#equals(java.lang.Object)}.
     */
    @Test
    public void testEqualsObject()
    {
        AccountUserRO userRef1 = new AccountUserRO();
        userRef1.setObjectId("4711");
        AccountUserRO userRef2 = new AccountUserRO();
        userRef2.setObjectId("4711");
        assertEquals(userRef1, userRef2);
    }
    
    /**
     * Test method for {@link de.mpg.escidoc.services.common.referenceobjects.ReferenceObject#equals(java.lang.Object)}.
     */
    @Test
    public void testEqualsObjectDifferentROs()
    {
        AccountUserRO userRef = new AccountUserRO();
        userRef.setObjectId("4711");
        AffiliationRO affRef = new AffiliationRO();
        affRef.setObjectId("4711");
        assertTrue(!userRef.equals(affRef)); 
    }
}
