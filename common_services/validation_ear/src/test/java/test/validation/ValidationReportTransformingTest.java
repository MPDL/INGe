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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package test.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.TestHelper;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportItemVO;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportVO;
import de.mpg.escidoc.services.validation.xmltransforming.ValidationTransforming;

/**
 *
 * @author Michael Franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 119 $ $LastChangedDate: 2007-11-09 17:37:29 +0100 (Fri, 09 Nov 2007) $
 */
public class ValidationReportTransformingTest
{
    private static ValidationTransforming vtransforming;

    private Logger logger = Logger.getLogger(getClass());

    /**
     * @throws java.lang.Exception Any exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        XMLUnit.setIgnoreWhitespace(true);
        vtransforming = TestHelper.getValidationTransforming();
    }

    /**
     * @throws java.lang.Exception Any exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
    }

    /**
     * Tests the transformation of a report xml without messages.
     * @throws Exception Any exception.
     */
    @Test
    public final void transformEmptyXmlReportToObject() throws Exception
    {

        String xmlReport = ResourceUtil.getResourceAsString("src/test/resources/xmltransforming/validationreport/empty.xml");
        logger.debug("Creating ValidationReportVO from: " + xmlReport);

        ValidationReportVO reportVO = vtransforming.transformToValidationReport(xmlReport);

        assertNotNull(reportVO);
        assertFalse(reportVO.hasItems());

    }
    /**
     * Tests the transformation of a report xml with informative and restrictive messages.
     * @throws Exception Any exception.
     */
    @Test
    public final void transformFullXmlReportToObject() throws Exception
    {

        String xmlReport = ResourceUtil.getResourceAsString("src/test/resources/xmltransforming/validationreport/full.xml");
        logger.debug("Creating ValidationReportVO from: " + xmlReport);

        ValidationReportVO reportVO = vtransforming.transformToValidationReport(xmlReport);

        assertNotNull(reportVO);
        assertTrue(reportVO.hasItems());

        List<ValidationReportItemVO> items = reportVO.getItems();
        assertNotNull(items);

    }

}
