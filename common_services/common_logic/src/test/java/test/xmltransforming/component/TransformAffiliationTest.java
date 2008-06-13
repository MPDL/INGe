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

package test.xmltransforming.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.w3c.dom.Document;

import test.TestBase;
import test.XpathPrefixResolver;

import com.sun.org.apache.xpath.internal.XPathAPI;
import com.sun.org.apache.xpath.internal.objects.XObject;

import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationPathVO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;

/**
 * Test of {@link XmlTransforming} methods for Affiliation transforming.
 * 
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $
 * @revised by MuJ: 03.09.2007
 */
public class TransformAffiliationTest extends TestBase
{
    private static XmlTransforming xmlTransforming = new XmlTransformingBean();

    private Logger logger = Logger.getLogger(getClass());

    /**
     * Test of {@link XmlTransforming#transformToAffiliationList(String)}
     * 
     * @throws Exception 
     */
    @Test
    public void testTransformToAffiliationList() throws Exception
    {
        logger.info("### testTransformToAffiliationList ###");

        String organizationalUnitListXml = readFile(TEST_FILE_ROOT + "xmltransforming/component/transformAffiliationTest/organizational-unit-list_sample1.xml");
        assertXMLValid(organizationalUnitListXml);
        logger.info("The organizational unit list XML is valid");
        List<AffiliationVO> affList = xmlTransforming.transformToAffiliationList(organizationalUnitListXml);
        assertNotNull(affList);
        assertFalse(affList.isEmpty());
        assertEquals(2, affList.size());

        for (AffiliationVO affiliation : affList)
        {
            assertEqualsMPIWG(affiliation);
        }
        logger.info("The organizational unit list XML has successfully been transformed into an List<AffilitationVO>.");
    }

    /**
     * Test of {@link XmlTransforming#transformToAffiliationPathList(String)}
     * 
     * @throws Exception 
     */
    @Test
    public void testTransformToAffiliationPathList() throws Exception
    {
        String organizationalUnitPathListXml = readFile(TEST_FILE_ROOT + "xmltransforming/component/transformAffiliationTest/organizational-unit-path-list_sample1.xml");
        assertXMLValid(organizationalUnitPathListXml);
        List<AffiliationPathVO> affPathList = xmlTransforming.transformToAffiliationPathList(organizationalUnitPathListXml);
        assertNotNull("Transforming delivered null.", affPathList);
        assertFalse("Transforming result list is empty.", affPathList.isEmpty());
        List<AffiliationRO> affPath = null;
        affPath = affPathList.get(0).getAffiliationList();
        assertEquals(affPath.get(0).getObjectId(), "escidoc:1234");
        assertEquals(affPath.get(1).getObjectId(), "escidoc:5678");
        affPath = affPathList.get(1).getAffiliationList();
        assertEquals(affPath.get(0).getObjectId(), "escidoc:9101");
        assertEquals(affPath.get(1).getObjectId(), "escidoc:1121");
        affPath = affPathList.get(2).getAffiliationList();
        assertEquals(affPath.get(0).getObjectId(), "escidoc:3141");
        assertEquals(affPath.get(1).getObjectId(), "escidoc:5161");
    }

    /**
     * Test of {@link XmlTransforming#transformToAffiliation(String)}
     * 
     * @throws Exception 
     */
    @Test
    public void testTransformToAffiliation() throws Exception
    {
        logger.info("### testTransformToAffiliation ###");

        String organizationalUnitXml = readFile(TEST_FILE_ROOT + "xmltransforming/component/transformAffiliationTest/organizational-unit_sample1.xml");
        assertXMLValid(organizationalUnitXml);
        if (logger.isDebugEnabled())
        {
            logger.debug("testTransformToAffiliation() - String organizationalUnitXml=\n" + organizationalUnitXml);
        }
        logger.info("The organizational unit XML is valid");

        AffiliationVO affiliation = xmlTransforming.transformToAffiliation(organizationalUnitXml);
        assertNotNull(affiliation);
        assertEqualsMPIWG(affiliation);
        logger.info("The organizational unit XML has successfully been transformed into an AffilitationVO.");
    }

    /**
     * Test of {@link XmlTransforming#transformToAffiliation(String)}
     * 
     * @throws Exception 
     */
    @Test
    public void testTransformToOrganizationalUnit() throws Exception
    {
        logger.info("### testTransformToOrganizationalUnit ###");

        // get a AffiliationVO by transforming the XML file into a AffiliationVO
        String organizationalUnitXml = readFile(TEST_FILE_ROOT + "xmltransforming/component/transformAffiliationTest/organizational-unit_sample1.xml");
        assertXMLValid(organizationalUnitXml);
        AffiliationVO affiliation = xmlTransforming.transformToAffiliation(organizationalUnitXml);
        assertNotNull(affiliation);
        assertEqualsMPIWG(affiliation);

        // transform it back to XML
        String roundTripOrganizationalUnitXml = xmlTransforming.transformToOrganizationalUnit(affiliation);
        logger.debug("testTransformToOrganizationalUnit() - String organizationalUnitXml=\n" + organizationalUnitXml
                + "\n\ntestTransformToOrganizationalUnit() - String roundTripOrganizationalUnitXml=\n" + roundTripOrganizationalUnitXml);

        // compare round tripped XML with original XML...
        Document organizationalUnitDoc = getDocument(organizationalUnitXml, true);
        Document roundTripOrganizationalUnitDoc = getDocument(roundTripOrganizationalUnitXml, true);
        // comapre objectIds
        XObject xObject;
        xObject = XPathAPI.eval(organizationalUnitDoc, "//escidocOrganizationalUnit:organizational-unit/@objid", new XpathPrefixResolver());
        String objIdBefore = xObject.toString();
        xObject = XPathAPI.eval(roundTripOrganizationalUnitDoc, "//escidocOrganizationalUnit:organizational-unit/@objid", new XpathPrefixResolver());
        String objIdAfter = xObject.toString();
        assertTrue(objIdBefore.length() > 0);
        assertEquals(objIdBefore, objIdAfter);
    }

    private void assertEqualsMPIWG(AffiliationVO affiliation)
    {
        assertEquals("escidoc:persistent1", affiliation.getReference().getObjectId());
        assertEquals("opened", affiliation.getPublicStatus());
        assertEquals(1, affiliation.getAlternativeNames().size());
        assertEquals("MPIWG", affiliation.getAlternativeNames().get(0));
        assertEquals("MPI for the History of Science", affiliation.getName());
        assertEquals(1, affiliation.getDescriptions().size());
        assertEquals(
                "The Max Planck Institute for the History of Science in Berlin was established in 1994 in order to create an international research center for the history of science in Germany. Researchers at the Institute investigate how new categories of thought, proof, and experience have emerged in the centuries-long interaction between the sciences and their ambient cultures.",
                affiliation.getDescriptions().get(0));
        assertEquals("DE", affiliation.getCountryCode());
        assertEquals("Berlin", affiliation.getCity());
        assertEquals(2, affiliation.getIdentifiers().size());
        assertEquals("http://www.mpgwg-berlin.mpg.de", affiliation.getIdentifiers().get(0));
    }
}
