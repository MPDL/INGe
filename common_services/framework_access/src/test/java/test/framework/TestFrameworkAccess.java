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
package test.framework;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.framework.aa.TestUserManagement;
import test.framework.cmm.TestContentTypeModeller;
import test.framework.om.TestObjectManager;
import test.framework.oum.TestOrganizationalUnitManager;
import test.framework.sb.TestSearch;
import test.framework.st.TestStagingService;

/**
 * Test of the eSciDoc-Framework services.
 * 
 * @author Peter Broszeit (initial creation)
 * @author $Author: pbroszei $ (last modification)
 * @version $Revision: 319 $ $LastChangedDate: 2007-11-14 14:48:40 +0100 (Wed, 14 Nov 2007) $
 * @revised by BrP: 03.09.2007
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({test.framework.aa.TestUserManagement.class
                    ,test.framework.oum.TestOrganizationalUnitManager.class
                    ,test.framework.cmm.TestContentTypeModeller.class
                    ,test.framework.om.TestObjectManager.class
                    ,test.framework.st.TestStagingService.class
                    ,test.framework.sb.TestSearch.class
                    })
public class TestFrameworkAccess
{
}
