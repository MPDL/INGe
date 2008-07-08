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

package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import test.validation.ValidationCacheTest;
import test.validation.ValidationReportTransformingTest;
import test.validation.ValidatorTest;
import test.validation.WebServiceTest;

/**
 * Component test suite for common_logic.
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 113 $ $LastChangedDate: 2007-11-08 18:23:27 +0100 (Thu, 08 Nov 2007) $
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
                    ValidationCacheTest.class,
                    ValidationReportTransformingTest.class,
                    ValidatorTest.class,
                    WebServiceTest.class
                    })
public class ValidationServiceTest
{

}
