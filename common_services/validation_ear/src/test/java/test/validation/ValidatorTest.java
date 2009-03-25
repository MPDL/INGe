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

import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import test.TestHelper;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.validation.ItemValidating;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportVO;

/**
 * Test class for the validation package.
 *
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 126 $ $LastChangedDate: 2007-11-15 11:36:15 +0100 (Thu, 15 Nov 2007) $
 *
 */
public class ValidatorTest
{

    private ItemValidating validator;
    private XmlTransforming xmlTransforming;
    private String validXml;
    private String invalidXml;
    private String validationPoint = "submit_item";

    private Logger logger = Logger.getLogger(getClass());

    /**
     * Init validator bean.
     * @throws Exception Any Exception.
     */
    @Before
    public final void getValidator() throws Exception
    {
        InitialContext ctx = new InitialContext();
        validator = (ItemValidating) ctx.lookup(ItemValidating.SERVICE_NAME);
    }

    /**
     * Init transforming bean.
     * @throws Exception Any exception.
     */
    @Before
    public final void getTransformer() throws Exception
    {
        InitialContext ctx = new InitialContext();
        xmlTransforming = (XmlTransforming) ctx.lookup(XmlTransforming.SERVICE_NAME);
    }

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
     * Get an invalid item xml from ressources.
     * @throws Exception Any Exception.
     */
    @Before
    public final void getInvalidItemXml() throws Exception
    {
        invalidXml = ResourceUtil.getResourceAsString("validation/invalidItem.xml");
        assertNotNull("Valid item xml not found", invalidXml);
    }

    /**
     * Get a valid item VO from the ressources.
     * @return Item VO.
     * @throws Exception Any exception.
     */
    private PubItemVO getValidItemVO() throws Exception
    {
        PubItemVO item = xmlTransforming.transformToPubItem(validXml);
        return item;
    }

    /**
     * Get an invalid item VO from the ressources.
     * @return Item VO.
     * @throws Exception Any exception.
     */
    private PubItemVO getInvalidItemVO() throws Exception
    {
        PubItemVO item = xmlTransforming.transformToPubItem(invalidXml);
        return item;
    }

    /**
     * Test service with a valid item XML.
     * @throws Exception Any exception.
     */
    @Test
    public final void testValidItemXmlValidation() throws Exception
    {
        TestHelper.initTimeLog("testValidItemXmlValidation");
        String reportXml = validator.validateItemXml(validXml, validationPoint);
        TestHelper.logTime("done");

        logger.info("report: " + reportXml);
        assertNotNull("Report is null", reportXml);
        assertTrue("Report xml does contain failure messages.", !reportXml.contains("failure"));
    }

    /**
     * Test service with a valid item object.
     * @throws Exception Any exception.
     */
    @Test
    public final void testValidItemVOValidation() throws Exception
    {
        TestHelper.initTimeLog("testValidItemVOValidation");
        PubItemVO itemVO = getValidItemVO();
        TestHelper.logTime("transform to VO");
        ValidationReportVO reportVO = validator.validateItemObject(itemVO, validationPoint);
        TestHelper.logTime("done");

        assertNotNull("Report is null", reportVO);
        assertTrue("Report VO should be valid, but is not.", reportVO.isValid());
    }

    /**
     * Test service with an invalid item object.
     * @throws Exception Any exception.
     */
    @Test
    public final void testInvalidItemVOValidation() throws Exception
    {
        ValidationReportVO reportVO = validator.validateItemObject(getInvalidItemVO(), validationPoint);
        assertNotNull("Report is null", reportVO);
        assertTrue("Report VO should be invalid, but is valid.", !reportVO.isValid());
    }

    /**
     * Test service with an invalid item XML.
     * @throws Exception Any exception.
     */
    @Test
    public final void testInvalidItemXmlValidation() throws Exception
    {
        String reportXml = validator.validateItemXml(invalidXml, validationPoint);
        assertNotNull("Report is null", reportXml);
        assertTrue("Report xml does not contain failure messages, although it should.", reportXml.contains("failure"));
    }

    /**
     * Test service with an invalid item XML.
     * @throws Exception Any exception.
     */
    @Test
    public final void testRefreshValidationCache() throws Exception
    {
        validator.refreshValidationSchemaCache();
    }

}
