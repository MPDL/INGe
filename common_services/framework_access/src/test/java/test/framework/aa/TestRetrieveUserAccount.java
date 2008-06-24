/*
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
package test.framework.aa;

import static org.junit.Assert.assertNotNull;
import org.apache.log4j.Logger;
import org.junit.Test;
import test.framework.TestBase;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Testcases for the basic service UserAccountHandler.
 * 
 * @author Peter Broszeit (initial creation)
 * @author $Author: wfrank $ (last modification)
 * @version $Revision: 314 $ $LastChangedDate: 2007-11-07 13:12:14 +0100 (Wed, 07 Nov 2007) $
 * @revised by BrP: 11.03.2008
 */
public class TestRetrieveUserAccount extends TestBase
{
    private Logger logger = Logger.getLogger(getClass());

    /**
     * Test method for {@link de.escidoc.www.services.aa.UserAccountHandler#retrieve(java.lang.String)}.
     */
    @Test
    public void retrieveUserAcountById() throws Exception
    {
        String id = USERID;
        long zeit = -System.currentTimeMillis();
        String user = ServiceLocator.getUserAccountHandler(userHandle).retrieve(id);
        zeit += System.currentTimeMillis();
        logger.info("retrieveUserAcountById(" + id + ")->" + zeit + "ms");
        logger.debug("UserAccount(" + id + ")=" + user);
        assertNotNull(user);
    }

    /**
     * Test method for {@link de.escidoc.www.services.aa.UserAccountHandler#retrieve(java.lang.String)}.
     */
    @Test
    public void retrieveUserAcountByHandle() throws Exception
    {
        long zeit = -System.currentTimeMillis();
        String user = ServiceLocator.getUserAccountHandler(userHandle).retrieve(userHandle);
        zeit += System.currentTimeMillis();
        logger.info("retrieveUserAcountByHandle(" + userHandle + ")->" + zeit + "ms");
        logger.debug("UserAccount(" + userHandle + ")=" + user);
        assertNotNull(user);
    }

    /**
     * Test method for {@link de.escidoc.www.services.aa.UserAccountHandler#retrieve(java.lang.String)}.
     */
    @Test
    public void retrieveUserAcountByLoginName() throws Exception
    {
        String id = "test_dep_scientist";
        long zeit = -System.currentTimeMillis();
        String user = ServiceLocator.getUserAccountHandler(userHandle).retrieve(id);
        zeit += System.currentTimeMillis();
        logger.info("retrieveUserAcountByUserId(" + id + ")->" + zeit + "ms");
        logger.debug("UserAccount(" + id + ")=" + user);
        assertNotNull(user);
    }

    /**
     * Test method for {@link de.escidoc.www.services.aa.UserAccountHandler#retrieveCurrentGrants(java.lang.String)}.
     */
    @Test
    public void retrieveCurrentGrants() throws Exception
    {
        String id = USERID;
        long zeit = -System.currentTimeMillis();
        String grants = ServiceLocator.getUserAccountHandler(userHandle).retrieveCurrentGrants(id);
        zeit += System.currentTimeMillis();
        logger.info("retrieveCurrentGrants(" + id + ")->" + zeit + "ms");
        logger.debug("Grants(" + id + ")=" + grants);
        assertNotNull(grants);
    }
}
