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
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */ 
package test.framework.aa;

import org.apache.log4j.Logger;
import org.junit.Test;

import test.framework.TestBase;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Test cases for the authentification service of the framework.
 *
 * @author Peter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @revised by BrP: 04.09.2007
 */
public class TestLoginLogout
{    
    private static Logger logger = Logger.getLogger(TestLoginLogout.class);
    

    /**
     * Logs the default user in.
     */
    @Test
    public void login() throws Exception
    {
        long zeit = -System.currentTimeMillis();
        AdminHelper.loginUser(PropertyReader.getProperty(TestBase.PROPERTY_USERNAME_SCIENTIST), 
                PropertyReader.getProperty(TestBase.PROPERTY_PASSWORD_SCIENTIST));
        zeit += System.currentTimeMillis(); 
        logger.info("login->" + zeit + "ms");
    }

    /**
     * Logs the default user in twice.
     */
    @Test
    public void loginTwice() throws Exception
    {
        String scientistUserId = PropertyReader.getProperty(TestBase.PROPERTY_ID_SCIENTIST);
        String handle1 = AdminHelper.loginUser(PropertyReader.getProperty(TestBase.PROPERTY_USERNAME_SCIENTIST), 
                PropertyReader.getProperty(TestBase.PROPERTY_PASSWORD_SCIENTIST));
        String user = ServiceLocator.getUserAccountHandler(handle1).retrieve(
                scientistUserId); 
        String handle2 = AdminHelper.loginUser(PropertyReader.getProperty(TestBase.PROPERTY_USERNAME_SCIENTIST), 
                PropertyReader.getProperty(TestBase.PROPERTY_PASSWORD_SCIENTIST));
        
        user = ServiceLocator.getUserAccountHandler(handle2).retrieve(scientistUserId); 
        // handle1 must still be valid
        user = ServiceLocator.getUserAccountHandler(handle1).retrieve(scientistUserId); 
        // make handle2 invalid
        ServiceLocator.getUserManagementWrapper(handle2).logout();
        // handle1 must still be valid
        user = ServiceLocator.getUserAccountHandler(handle1).retrieve(scientistUserId); 
    }

    /**
     * Logs the default user in and out.
     */
    @Test
    public void logout() throws Exception
    {
        String userHandle = AdminHelper.loginUser(PropertyReader.getProperty(TestBase.PROPERTY_USERNAME_SCIENTIST), 
                PropertyReader.getProperty(TestBase.PROPERTY_PASSWORD_SCIENTIST));
        long zeit = -System.currentTimeMillis();
        ServiceLocator.getUserManagementWrapper(userHandle).logout();
        zeit += System.currentTimeMillis(); 
        logger.info("logout->" + zeit + "ms");
    }
}
