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
package test.framework.om;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test the Service OM (Objectmanager) of the eSciDoc-Framework.
 * 
 * @author Peter Broszeit (initial creation)
 * @author $Author: wfrank $ (last modification)
 * @version $Revision: 329 $ $LastChangedDate: 2007-12-06 09:44:45 +0100 (Thu, 06 Dec 2007) $
 * @revised by FrW: 11.03.2008
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({TestContext.class
                    ,TestItem.class
                    ,TestRelation.class
                    ,TestItemRetrieve.class
                    ,TestContainer.class
                    // ,TestToc.class
                    })
public class TestObjectManager
{
}
