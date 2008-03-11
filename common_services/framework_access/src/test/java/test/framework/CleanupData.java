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

import test.framework.aa.CleanupUserData;
import test.framework.om.CleanupContextData;
import test.framework.oum.CleanupOrgUnitData;

/**
 * Test of the eSciDoc-Framework services.
 * 
 * @author Peter Broszeit (initial creation)
 * @author $Author: pbroszei $ (last modification)
 * @version $Revision: 281 $ $LastChangedDate: 2007-09-04 11:35:30 +0200 (Di, 04 Sep 2007) $
 * @revised by BrP: 03.09.2007
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({CleanupUserData.class
                    ,CleanupOrgUnitData.class
                    ,CleanupContextData.class
                    })
public class CleanupData
{
}
