/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationPathVO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;
import de.mpg.mpdl.inge.model.xmltransforming.TestBase;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;

/**
 * Test of {@link XmlTransforming} methods for Affiliation transforming.
 * 
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @revised by MuJ: 03.09.2007
 */
public class TransformAffiliationTest extends TestBase {
  private static final String REST_AFFILIATION_FILE =
      TEST_FILE_ROOT + "xmltransforming/component/transformAffiliationTest/organizational-unit_rest.xml";

  private static final Logger logger = Logger.getLogger(TransformAffiliationTest.class);

  /**
   * Test of {@link XmlTransforming#transformToAffiliationList(String)}
   * 
   * @throws Exception
   */
  @Test
  public void testTransformToAffiliationList() throws Exception {
    logger.info("### testTransformToAffiliationList ###");

    String organizationalUnitListXml =
        readFile(TEST_FILE_ROOT + "xmltransforming/component/transformAffiliationTest/organizational-unit-list_sample1.xml");
    assertXMLValid(organizationalUnitListXml);
    logger.info("The organizational unit list XML is valid");
    List<AffiliationVO> affList = XmlTransformingService.transformToAffiliationList(organizationalUnitListXml);
    assertNotNull(affList);
    assertFalse(affList.isEmpty());
    assertEquals(2, affList.size());

    for (AffiliationVO affiliation : affList) {
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
  public void testTransformToAffiliationPathList() throws Exception {
    logger.info("### testTransformToAffiliationPathList ###");

    String organizationalUnitPathListXml =
        readFile(TEST_FILE_ROOT + "xmltransforming/component/transformAffiliationTest/organizational-unit-path-list_sample1.xml");
    assertXMLValid(organizationalUnitPathListXml);
    List<AffiliationPathVO> affPathList = XmlTransformingService.transformToAffiliationPathList(organizationalUnitPathListXml);
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
   * Test of {@link XmlTransforming#transformToAffiliationPathList(String)}
   * 
   * @throws Exception
   */
  @Test
  public void testTransformToParentAffiliationList() throws Exception {
    String parentOrganizationalUnitListXml =
        readFile(TEST_FILE_ROOT + "xmltransforming/component/transformAffiliationTest/parent-organizational-unit-list_sample1.xml");
    assertXMLValid(parentOrganizationalUnitListXml);
    List<AffiliationRO> affROList = XmlTransformingService.transformToParentAffiliationList(parentOrganizationalUnitListXml);
    assertNotNull("Transforming delivered null.", affROList);
    assertFalse("Transforming result list is empty.", affROList.isEmpty());

    assertEquals(affROList.get(0).getObjectId(), "escidoc:19523");
    assertEquals(affROList.get(1).getObjectId(), "escidoc:19528");

  }

  /**
   * Test of {@link XmlTransforming#transformToAffiliation(String)}
   * 
   * @throws Exception
   */
  @Test
  public void testTransformToAffiliation() throws Exception {
    logger.info("### testTransformToAffiliation ###");

    String organizationalUnitXml =
        readFile(TEST_FILE_ROOT + "xmltransforming/component/transformAffiliationTest/organizational-unit_sample1.xml");
    assertXMLValid(organizationalUnitXml);
    if (logger.isDebugEnabled()) {
      logger.debug("testTransformToAffiliation() - String organizationalUnitXml=\n" + organizationalUnitXml);
    }
    logger.info("The organizational unit XML is valid");

    AffiliationVO affiliation = XmlTransformingService.transformToAffiliation(organizationalUnitXml);
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
  public void testTransformToOrganizationalUnit() throws Exception {
    logger.info("### testTransformToOrganizationalUnit ###");

    // get a AffiliationVO by transforming the XML file into a AffiliationVO
    String organizationalUnitXml =
        readFile(TEST_FILE_ROOT + "xmltransforming/component/transformAffiliationTest/organizational-unit_sample1.xml");
    assertXMLValid(organizationalUnitXml);
    AffiliationVO affiliation = XmlTransformingService.transformToAffiliation(organizationalUnitXml);
    assertNotNull(affiliation);
    assertEqualsMPIWG(affiliation);

    // transform it back to XML
    String roundTripOrganizationalUnitXml = XmlTransformingService.transformToOrganizationalUnit(affiliation);
    logger.debug("testTransformToOrganizationalUnit() - String organizationalUnitXml=\n" + organizationalUnitXml
        + "\n\ntestTransformToOrganizationalUnit() - String roundTripOrganizationalUnitXml=\n" + roundTripOrganizationalUnitXml);
  }

  /**
   * Test method for
   * {@link de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService#transformToItem(java.lang.String)}
   * .
   * 
   * @throws Exception
   */
  @Test
  public void testTransformRestAffiliationToAffiliation() throws Exception {
    logger.info("### testTransformRestAffiliationToAffiliation ###");

    // read item[XML] from file
    String restAffiliationXML = readFile(REST_AFFILIATION_FILE);
    logger.info("Affiliation[XML] read from file.");
    logger.info("Content: " + restAffiliationXML);
    // transform the xml directly into a AffiliationVO
    AffiliationVO affiliationVO = XmlTransformingService.transformToAffiliation(restAffiliationXML);

    assertEquals("ObjectId not transformed correctly", "/oum/organizational-unit/escidoc:830552",
        affiliationVO.getReference().getObjectId());
    assertEquals("Creator (created-by) not transformed correctly", "/aa/user-account/escidoc:user42",
        affiliationVO.getCreator().getObjectId());
    assertEquals("Modifier (modified-by) not transformed correctly", "/aa/user-account/escidoc:user42",
        affiliationVO.getModifiedBy().getObjectId());

    List<AffiliationRO> l = affiliationVO.getParentAffiliations();

    for (AffiliationRO a : l) {
      assertTrue(a.getObjectId().equals("/oum/organizational-unit/escidoc:830550"));
    }
  }

  private void assertEqualsMPIWG(AffiliationVO affiliation) {
    MdsOrganizationalUnitDetailsVO details = null;
    if (affiliation.getMetadataSets().size() > 0 && affiliation.getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO) {
      details = (MdsOrganizationalUnitDetailsVO) affiliation.getMetadataSets().get(0);
    }

    System.out.println("REFERENCE MPIWG: " + affiliation + " / " + affiliation.getReference());

    assertEquals("escidoc:persistent1", affiliation.getReference().getObjectId());
    assertEquals("opened", affiliation.getPublicStatus());
    assertEquals(1, details.getAlternativeNames().size());
    assertEquals("MPIWG", details.getAlternativeNames().get(0));
    assertEquals("MPI for the History of Science", details.getName());
    assertEquals(1, details.getDescriptions().size());
    assertEquals(
        "The Max Planck Institute for the History of Science in Berlin was established in 1994 in order to create an international research center for the history of science in Germany. Researchers at the Institute investigate how new categories of thought, proof, and experience have emerged in the centuries-long interaction between the sciences and their ambient cultures.",
        details.getDescriptions().get(0));
    assertEquals("DE", details.getCountryCode());
    assertEquals("Berlin", details.getCity());
    assertEquals(1, details.getIdentifiers().size());
    assertEquals(new IdentifierVO(IdentifierVO.IdType.URI, "http://www.mpgwg-berlin.mpg.de"), details.getIdentifiers().get(0));
  }
}
