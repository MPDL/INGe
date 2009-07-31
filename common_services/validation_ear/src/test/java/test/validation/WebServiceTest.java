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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import test.TestHelper;
import de.mpg.escidoc.services.common.util.ResourceUtil;

/**
 * Test class for the SOAP andc REST interface.
 *
 * @author mfranke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 131 $ $LastChangedDate: 2007-11-21 18:53:43 +0100 (Wed, 21 Nov 2007) $
 */
public class WebServiceTest
{

    private String validXml;
    private String semiValidXml;
    private String invalidXml;

    private Logger logger = Logger.getLogger(getClass());

    /**
     * Get a valid item xml from ressources.
     * @throws Exception Any Exception.
     */
    @Before
    public final void getValidItemXml() throws Exception
    {
        validXml = ResourceUtil.getResourceAsString("validation/validItem.xml");
        assertNotNull("Valid item xml not found", validXml);
    }

    /**
     * Get a semi valid item xml from ressources.
     * @throws Exception Any Exception.
     */
    @Before
    public final void getSemiValidItemXml() throws Exception
    {
        semiValidXml = ResourceUtil.getResourceAsString("validation/semiValidItem.xml");
        assertNotNull("Semi valid item xml not found", semiValidXml);
    }

    /**
     * Get an invalid item xml from ressources.
     * @throws Exception Any Exception.
     */
    @Before
    public final void getInvalidItemXml() throws Exception
    {
        invalidXml = ResourceUtil.getResourceAsString("validation/invalidItem.xml");
        assertNotNull("Invalid item xml not found", invalidXml);
    }

    /**
     * Test REST web service with a valid item object.
     * @throws Exception Any exception.
     */
    @Test
    public final void testValidItemRestValidation() throws Exception
    {
        String itemXml = validXml;
        TestHelper.initTimeLog("Start REST call");
        String report = TestHelper.callRestWebservice(null, itemXml);
        TestHelper.logTime("End REST call", 200);

        logger.debug("Report: " + report);

        assertTrue(!report.contains("<failure"));
    }

    /**
     * Test REST web service with a valid item object.
     * @throws Exception Any exception.
     */
    @Test
    public final void testInvalidItemRestValidation() throws Exception
    {
        String itemXml = invalidXml;
        String report = TestHelper.callRestWebservice(null, itemXml);

        logger.debug("Report InvalidItemRestValidation: " + report);

        assertNotNull( report );
        assertTrue(report.contains("<failure"));
    }

    /**
     * Test SOAP web service with a valid item object.
     * @throws Exception Any exception.
     */
    // FIXME tendres: "This test has to be made runnnable"
    @Test
    @Ignore( "This test has to be made runnnable" )
    public final void testValidItemSoapValidation() throws Exception
    {
        String itemXml = validXml;

        TestHelper.initTimeLog("Start SOAP call");
        String report = TestHelper.callSoapWebservice(new Object[]{itemXml});
        TestHelper.logTime("End SOAP call", 750);

        logger.debug("Report: " + report);

        assertTrue(!report.contains("<failure"));
    }

    /**
     * Test SOAP web service with a semi valid item object.
     * @throws Exception Any exception.
     */
    @Ignore
    @Test
    public final void testSemiValidItemSoapValidation() throws Exception
    {
        String itemXml = semiValidXml;

        TestHelper.initTimeLog("Start SOAP call 1");
        String report = TestHelper.callSoapWebservice(new Object[]{itemXml, "default"});
        TestHelper.logTime("End SOAP call 1", 750);

        logger.debug("Report: " + report);

        // Validation point default should validate
        assertTrue(!report.contains("<failure"));
        report = TestHelper.callSoapWebservice(new Object[]{itemXml, "submit_item"});
        TestHelper.logTime("End SOAP call 2", 750);

        logger.info("Report: " + report);

        // Validation point submit_item should not validate
        assertTrue(report.contains("<failure"));
    }
}