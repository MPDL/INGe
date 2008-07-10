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

package test.common.xmltransforming.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;

import test.common.AffiliationCreator;
import test.common.TestBase;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.types.Coordinates;
import de.mpg.escidoc.services.common.util.ObjectComparator;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.AffiliationRefFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Test of {@link XmlTransforming} methods for Affiliation transforming.
 * 
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $
 * @revised by MuJ: 03.09.2007
 */
public class TransformAffiliationIntegrationTest extends TestBase
{
    private static XmlTransforming xmlTransforming;

    /**
     * This Map contains a mapping between abbrevations and object ids. See the {@link test.common.AffiliationCreator}
     * class for details.
     */
    private static Map<String, String> abbreviationToObjIdMapping;

    private Logger logger = Logger.getLogger(getClass());

    /**
     * Get an XmlTransforming instance once for all tests.
     * 
     * @throws Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        // get a XmlTransforming instance
        xmlTransforming = (XmlTransforming) getService(XmlTransforming.SERVICE_NAME);
    }

    /**
     * Test method for
     * {@link de.mpg.escidoc.services.common.XmlTransforming#transformToOrganizationalUnit(java.lang.String)} in
     * interaction with the framework.
     * 
     * @throws Exception 
     */
    @Test
    public final void testTransformToOrganizationalUnitCreate() throws Exception
    {
        logger.info("### testTransformToOrganizationalUnitCreate ###");

        // create a new organizational unit (=affiliation) using framework_access directly

        // log in as system administrator
        String systemAdministratorUserHandle = loginSystemAdministrator();
        logger.debug("testTransformToOrganizationalUnitCreate() - String systemAdministratorUserHandle=" + systemAdministratorUserHandle);

        // create new AffiliationVO
        AffiliationVO affiliationVOPreCreate = AffiliationCreator.getTopLevelAffiliationMPG();
        long uniquer = System.currentTimeMillis();
        
        if (affiliationVOPreCreate.getMetadataSets().size() > 0 && affiliationVOPreCreate.getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO)
        {
            MdsOrganizationalUnitDetailsVO detailsVO = (MdsOrganizationalUnitDetailsVO) affiliationVOPreCreate.getMetadataSets().get(0);

            detailsVO.setName(detailsVO.getName() + " Nr." + uniquer + "***");
            // fill in some special characters to check their treatment by the framework
            detailsVO.getAlternativeNames().add("These tokens are escaped and must stay escaped: \"&amp;\", \"&gt;\", \"&lt;\", \"&quot;\", \"&apos;\"");
            detailsVO.setCoordinates(new Coordinates(1.45246462436, 2.34673657346));
            detailsVO.getDescriptions().add("These tokens are escaped and must stay escaped, too: &auml; &Auml; &szlig;");
        }
        
        // transform the AffiliationVO into an organizational unit (for create)
        long zeit = -System.currentTimeMillis();
        String organizationalUnitPreCreate = xmlTransforming.transformToOrganizationalUnit(affiliationVOPreCreate);
        zeit += System.currentTimeMillis();
        logger.info("transformToOrganizationalUnit() ->" + zeit + "ms\n" + "AffiliationVO transformed to organizational unit[XML] for create.");
        logger.debug("organizational unit[XML] after transformation from AffiliationVO =\n" + toString(getDocument(organizationalUnitPreCreate, false), false));

        // create the organizational unit in the framework
        String organizationalUnitPostCreate = ServiceLocator.getOrganizationalUnitHandler(systemAdministratorUserHandle).create(organizationalUnitPreCreate);
        assertNotNull(organizationalUnitPostCreate);
        logger.info("organizational unit[XML] created in the framework.");

        Document creationResponseDocument = getDocument(organizationalUnitPostCreate, false);
        String affiliationObjectIdMPG = getRootElementAttributeValue(creationResponseDocument, "objid");

        logger.debug("organizational unit objid: " + affiliationObjectIdMPG + "\n" + "Response from framework =\n" + toString(creationResponseDocument, false));

        // transform the returned organizational unit back into an affiliation
        zeit = -System.currentTimeMillis();
        AffiliationVO affiliationVOPostCreate = xmlTransforming.transformToAffiliation(organizationalUnitPostCreate);
        zeit += System.currentTimeMillis();
        logger.info("transformToAffiliation() ->" + zeit + "ms\n" + "organizational unit[XML] transformed to AffiliationVO after create.");

        // check the differences of the VOs
        ObjectComparator oc = new ObjectComparator(affiliationVOPreCreate, affiliationVOPostCreate);
        List<String> difflist = oc.getDiffs();
        if (logger.isDebugEnabled())
        {
            
            logger.debug("affiliationVOPreCreate.creationDate: " + affiliationVOPreCreate.getCreationDate());
            logger.debug("affiliationVOPostCreate.creationDate: " + affiliationVOPostCreate.getCreationDate());
            
            StringBuffer sb = new StringBuffer();
            sb.append("List of differences between affiliationVOPreCreate and affiliationVOPostCreate: \n");
            for (String diff : difflist)
            {
                sb.append(diff + "\n");
            }
            logger.debug(sb.toString());
        }
        // There are 4 fields that are must have changed:
        // 4: reference, creationDate, creator and publicStatus
        assertEquals(5, difflist.size());

        // delete the created affiliation
        ServiceLocator.getOrganizationalUnitHandler(systemAdministratorUserHandle).delete(affiliationVOPostCreate.getReference().getObjectId());
        
        // log out
        logout(systemAdministratorUserHandle);
    }

    /**
     * Test method for
     * {@link de.mpg.escidoc.services.common.XmlTransforming#transformToOrganizationalUnit(java.lang.String)} in
     * interaction with the framework.
     * 
     * @throws Exception 
     */
    @Ignore
    public final void testTransformToOrganizationalUnitUpdate() throws Exception
    {
        logger.info("### testTransformToOrganizationalUnitUpdate ###");

        // create a new organizational unit (=affiliation) using framework_access directly

        // log in as system administrator
        String systemAdministratorUserHandle = loginSystemAdministrator();
        logger.debug("testTransformToOrganizationalUnitCreate() - String systemAdministratorUserHandle=" + systemAdministratorUserHandle);

        // create new AffiliationVO
        AffiliationVO affiliationVOPreCreate = AffiliationCreator.getTopLevelAffiliationMPG();
        long uniquer = System.currentTimeMillis();
        if (affiliationVOPreCreate.getMetadataSets().size() > 0 && affiliationVOPreCreate.getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO)
        {
            MdsOrganizationalUnitDetailsVO detailsVO = (MdsOrganizationalUnitDetailsVO) affiliationVOPreCreate.getMetadataSets().get(0);
            detailsVO.setName(detailsVO.getName() + " Nr." + uniquer + "***");
        }
        
        // transform the AffiliationVO into an organizational unit (for create)
        long zeit = -System.currentTimeMillis();
        String organizationalUnitPreCreate = xmlTransforming.transformToOrganizationalUnit(affiliationVOPreCreate);
        zeit += System.currentTimeMillis();
        logger.info("transformToOrganizationalUnit() ->" + zeit + "ms\n" + "AffiliationVO transformed to organizational unit[XML] for create.");
        logger.debug("organizational unit[XML] after transformation from AffiliationVO =\n" + toString(getDocument(organizationalUnitPreCreate, false), false));

        // create the organizational unit in the framework
        String organizationalUnitPostCreate = ServiceLocator.getOrganizationalUnitHandler(systemAdministratorUserHandle).create(organizationalUnitPreCreate);
        assertNotNull(organizationalUnitPostCreate);
        logger.info("organizational unit[XML] created in the framework.");

        Document creationResponseDocument = getDocument(organizationalUnitPostCreate, false);
        String affiliationObjectIdMPG = getRootElementAttributeValue(creationResponseDocument, "objid");

        logger.debug("organizational unit objid: " + affiliationObjectIdMPG + "\n" + "Response from framework =\n" + toString(creationResponseDocument, false));

        // transform the returned organizational unit back into an affiliation
        zeit = -System.currentTimeMillis();
        AffiliationVO affiliationVOPostCreate = xmlTransforming.transformToAffiliation(organizationalUnitPostCreate);
        zeit += System.currentTimeMillis();
        logger.info("transformToAffiliation() ->" + zeit + "ms\n" + "organizational unit[XML] transformed to AffiliationVO after create.");

        // check the differences of the VOs
        ObjectComparator oc = new ObjectComparator(affiliationVOPreCreate, affiliationVOPostCreate);
        List<String> difflist = oc.getDiffs();
        if (logger.isDebugEnabled())
        {
            StringBuffer sb = new StringBuffer();
            sb.append("List of differences between affiliationVOPreCreate and affiliationVOPostCreate: \n");
            for (String diff : difflist)
            {
                sb.append(diff + "\n");
            }
            logger.debug(sb.toString());
        }
        // There are 5 fields that are must have changed:
        // 5: reference, creationDate, lastModificationDate, creator and status
        assertEquals(5, difflist.size());

        // alter the fields of the affiliation
        // Property name can not be altered!
        // Property abbreviation can not be altered!
        AffiliationVO affiliationVOPreUpdate = affiliationVOPostCreate;
        if (affiliationVOPreUpdate.getMetadataSets().size() > 0 && affiliationVOPreUpdate.getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO)
        {
            MdsOrganizationalUnitDetailsVO detailsVO = (MdsOrganizationalUnitDetailsVO) affiliationVOPreUpdate.getMetadataSets().get(0);
            detailsVO.setCity("Freising");
    
            detailsVO.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.URI, "http://www.mpg.de/updated"));
            detailsVO.setCountryCode("DE");
            detailsVO.getDescriptions().add("The description has been changed.");
            detailsVO.getIdentifiers().add(new IdentifierVO(IdentifierVO.IdType.ESCIDOC, "4712"));
        }
        
        // transform the AffiliationVO into an organizational unit (for update)
        zeit = -System.currentTimeMillis();
        String organizationalUnitPreUpdate = xmlTransforming.transformToOrganizationalUnit(affiliationVOPreUpdate);
        zeit += System.currentTimeMillis();
        logger.info("transformToOrganizationalUnit() ->" + zeit + "ms\n" + "AffiliationVO transformed to organizational unit[XML] for update.");
        if (logger.isDebugEnabled())
        {
            logger.debug("organizational unit[XML] after transformation from AffiliationVO =\n" + toString(getDocument(organizationalUnitPreUpdate, false), false));
        }

        // update the organizational unit in the framework
        String organizationalUnitPostUpdate = ServiceLocator.getOrganizationalUnitHandler(systemAdministratorUserHandle).update(affiliationObjectIdMPG, organizationalUnitPreUpdate);
        assertNotNull(organizationalUnitPostUpdate);
        logger.info("organizational unit[XML] updated in the framework.");
        if (logger.isDebugEnabled())
        {
            logger.debug("Response from framework =\n" + toString(getDocument(organizationalUnitPostUpdate, false), false));
        }
        
        // delete the created affiliation
        ServiceLocator.getOrganizationalUnitHandler(systemAdministratorUserHandle).delete(affiliationVOPostCreate.getReference().getObjectId());

        // log out
        logout(systemAdministratorUserHandle);
    }

    /**
     * This test creates and retrieves three top-level affiliations (Max Planck Society (MPG), Helmholtz Association and
     * Fraunhofer Society) and some sub-affiliations. Along the way it tests if the child-parent-relationships of the
     * created affiliations in the frameworks are correct. The created structure is as follows:
     * MPG->(MPIFG,MPI-G,FML,MPIMF->(ZEL,MPH-HD->(ZEL_2->(FU-BERLIN_2))),FU-BERLIN // HG->FU-BERLIN // FG->FU-BERLIN
     * 
     * @throws Exception 
     */
    @Ignore("Takes very long. Enable only if necessary.")
    public final void testRetrieveAffiliationStructure() throws Exception
    {
        logger.info("### testRetrieveAffiliationStructure ###");

        // log in as system administrator
        String systemAdministratorUserHandle = loginSystemAdministrator();
        logger.debug("testCreateAndRetrieveAffiliationStructure() - String systemAdministratorUserHandle=" + systemAdministratorUserHandle);

        // create a new, distinct affiliation structure
        TransformAffiliationIntegrationTest.abbreviationToObjIdMapping = AffiliationCreator.createAffiliationStructure(systemAdministratorUserHandle);

        // check number of childs of MPG (5: MPIFG, MPI-G, FML, MPIMF, FU-BERLIN)
        String mpgChilds = ServiceLocator.getOrganizationalUnitHandler(systemAdministratorUserHandle).retrieveChildObjects(abbreviationToObjIdMapping.get("objectIdMPG"));
        List<AffiliationVO> childVOList = xmlTransforming.transformToAffiliationList(mpgChilds);
        logger.info("MPG: child affiliations retrieved.");
        logger.debug("MPG: list of retrieved child affiliations =\n" + toString(getDocument(mpgChilds, false), false));
        assertEquals(5, childVOList.size());

        // check number of childs of MPIMF (2: ZEL, MPH-HD)
        String mpimfChilds = ServiceLocator.getOrganizationalUnitHandler(systemAdministratorUserHandle).retrieveChildObjects(abbreviationToObjIdMapping.get("objectIdMPIMF"));
        childVOList = xmlTransforming.transformToAffiliationList(mpimfChilds);
        logger.info("MPIMF: child affiliations retrieved.");
        logger.debug("MPIMF: list of retrieved child affiliations =\n" + toString(getDocument(mpimfChilds, false), false));
        assertEquals(2, childVOList.size());

        // check number of childs of MPH-HD (1: ZEL_2)
        String mphhdChilds = ServiceLocator.getOrganizationalUnitHandler(systemAdministratorUserHandle).retrieveChildObjects(abbreviationToObjIdMapping.get("objectIdMPH_HD"));
        childVOList = xmlTransforming.transformToAffiliationList(mphhdChilds);
        logger.info("MPH-HD: child affiliations retrieved.");
        logger.debug("MPH-HD: list of retrieved child affiliations =\n" + toString(getDocument(mphhdChilds, false), false));
        assertEquals(1, childVOList.size());

        // check number of childs of ZEL_2 (1: FU_BERLIN_2)
        String zel2Childs = ServiceLocator.getOrganizationalUnitHandler(systemAdministratorUserHandle).retrieveChildObjects(abbreviationToObjIdMapping.get("objectIdZEL2"));
        childVOList = xmlTransforming.transformToAffiliationList(zel2Childs);
        logger.info("ZEL_2: child affiliations retrieved.");
        logger.debug("ZEL_2: list of retrieved child affiliations =\n" + toString(getDocument(zel2Childs, false), false));
        assertEquals(1, childVOList.size());

        // log out
        logout(systemAdministratorUserHandle);

        // test if the affiliation structure can be retrieved correctly from the framework
        //

        // log in as system administrator again
        systemAdministratorUserHandle = loginSystemAdministrator();
        logger.debug("testCreateAndRetrieveAffiliationStructure() - String systemAdministratorUserHandle=" + systemAdministratorUserHandle);

        // retrieve top level affiliations and transform them to a list of AffiliationVOs
        FilterTaskParamVO filter = new FilterTaskParamVO();
        Filter f1 = filter.new TopLevelAffiliationFilter();
        filter.getFilterList().add(f1);
        String xmlparam = xmlTransforming.transformToFilterTaskParam(filter);
        logger.debug("testCreateAndRetrieveAffiliationStructure() - String xmlparam=\n" + toString(getDocument(xmlparam, false), false));

        String topLevelAffiliations = ServiceLocator.getOrganizationalUnitHandler(systemAdministratorUserHandle).retrieveOrganizationalUnits(xmlparam);
        logger.info("top level affiliations retrieved.");
        logger.error("testCreateAndRetrieveAffiliationStructure() - String topLevelAffiliations=\n" + toString(getDocument(topLevelAffiliations, false), false));

        // check if all of the three created top level affiliations (MPG, FG, HG) are among them
        List<AffiliationVO> topLevelAffiliationList = xmlTransforming.transformToAffiliationList(topLevelAffiliations);
        int counter = 0;
        String affiliationObjid = null;
        for (AffiliationVO affiliation : topLevelAffiliationList)
        {
            affiliationObjid = affiliation.getReference().getObjectId();
            if (affiliationObjid.equals(abbreviationToObjIdMapping.get("objectIdMPG")))
            {
                counter++;
                ObjectComparator oc = new ObjectComparator(affiliation, AffiliationCreator.getTopLevelAffiliationMPG());
                List<String> difflist = oc.getDiffs();
                if (logger.isDebugEnabled())
                {
                    StringBuffer sb = new StringBuffer();
                    sb.append("List of differences between affiliationVO(MPG) before and after creation: \n");
                    for (String diff : difflist)
                    {
                        sb.append(diff + "\n");
                    }
                    logger.debug(sb.toString());
                }
                // There are 6 fields that must have changed:
                // 6: name, reference, creationDate, lastModificationDate, creator and status
                assertEquals(6, difflist.size());
            }
            if ((affiliationObjid.equals(abbreviationToObjIdMapping.get("objectIdFG"))) || (affiliationObjid.equals(abbreviationToObjIdMapping.get("objectIdHG"))))
            {
                counter++;
            }
        }
        assertEquals("", 3, counter);
        logger.info("all created top level affiliations found.");

        // retrieve children of MPG
        String mpgChildAffiliations = ServiceLocator.getOrganizationalUnitHandler(systemAdministratorUserHandle).retrieveChildObjects(abbreviationToObjIdMapping.get("objectIdMPG"));
        logger.info("child affiliations of MPG retrieved.");
        logger.debug("child affiliations of MPG:\n" + toString(getDocument(mpgChildAffiliations, false), false));
        List<AffiliationVO> mpgChildAffiliationList = xmlTransforming.transformToAffiliationList(mpgChildAffiliations);
        if (logger.isDebugEnabled())
        {
            StringBuffer sb = new StringBuffer("Abbreviations of MPG child affiliations:\n");
            for (AffiliationVO childAffiliation : mpgChildAffiliationList)
            {
                if (childAffiliation.getMetadataSets().size() > 0 && childAffiliation.getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO)
                {
                    MdsOrganizationalUnitDetailsVO detailsVO = (MdsOrganizationalUnitDetailsVO) childAffiliation.getMetadataSets().get(0);
                    if (detailsVO.getAlternativeNames().size() > 0)
                    {
                        sb.append(detailsVO.getAlternativeNames().get(0) + "\n");
                    }
                }
            }
            logger.debug(sb.toString());
        }
        // check number of children
        assertEquals("MPG must have 5 child affiliations!", 5, mpgChildAffiliationList.size());

        // test if MPIMF is among them
        counter = 0;
        for (AffiliationVO childAffiliation : mpgChildAffiliationList)
        {
            if (childAffiliation.getReference().getObjectId().equals(abbreviationToObjIdMapping.get("objectIdMPIMF")))
            {
                counter++;
                ObjectComparator oc = new ObjectComparator(AffiliationCreator.getAffiliationMPIMF(), childAffiliation);
                List<String> difflist = oc.getDiffs();
                if (logger.isDebugEnabled())
                {
                    StringBuffer sb = new StringBuffer();
                    sb.append("List of differences between affiliationVO(MPIMF) before and after creation: \n");
                    for (String diff : difflist)
                    {
                        sb.append(diff + "\n");
                    }
                    logger.debug(sb.toString());
                }
                // There are 7 fields that must have changed:
                // 7: name, parentAffiliations, reference, creationDate, lastModificationDate, creator and status
                assertEquals(7, difflist.size());
                // check if MPG is (the only affiliation) in the list of parentAffiliations of MPIMF
                assertEquals(1, childAffiliation.getParentAffiliations().size());
                assertEquals(childAffiliation.getParentAffiliations().get(0).getObjectId(), abbreviationToObjIdMapping.get("objectIdMPG"));
            }
        }
        assertEquals(1, counter);

        // get childs of MPIMF
        String mpimfChildAffiliations = ServiceLocator.getOrganizationalUnitHandler(systemAdministratorUserHandle).retrieveChildObjects(abbreviationToObjIdMapping.get("objectIdMPIMF"));
        logger.info("child affiliations of MPIMF retrieved.");
        List<AffiliationVO> mpimfChildAffiliationList = xmlTransforming.transformToAffiliationList(mpimfChildAffiliations);
        // check number of children
        assertEquals("MPIMF must have 2 child affiliations!", 2, mpimfChildAffiliationList.size());

        // retrieve specific affiliations:
        logger.info("Trying to retrieve specific affiliations (by objectId)...");
        filter = new FilterTaskParamVO();
        AffiliationRefFilter arf1 = filter.new AffiliationRefFilter();
        List<AffiliationRO> idList = arf1.getIdList();
        idList.add(new AffiliationRO(abbreviationToObjIdMapping.get("objectIdHG")));
        idList.add(new AffiliationRO(abbreviationToObjIdMapping.get("objectIdMPH_HD")));
        idList.add(new AffiliationRO(abbreviationToObjIdMapping.get("objectIdMPIMF")));
        idList.add(new AffiliationRO(abbreviationToObjIdMapping.get("objectIdFML")));
        filter.getFilterList().add(arf1);
        xmlparam = xmlTransforming.transformToFilterTaskParam(filter);
        logger.debug("testCreateAndRetrieveAffiliationStructure() - String xmlparam=\n" + toString(getDocument(xmlparam, false), false));
        String specificAffiliations = ServiceLocator.getOrganizationalUnitHandler(systemAdministratorUserHandle).retrieveOrganizationalUnits(xmlparam);
        logger.info("specific affiliations retrieved.");
        List<AffiliationVO> specificAffiliationList = xmlTransforming.transformToAffiliationList(specificAffiliations);
        if (logger.isDebugEnabled())
        {
            StringBuffer sb = new StringBuffer("Abbreviations of retrieved specific affiliations:\n");
            for (AffiliationVO childAffiliation : specificAffiliationList)
            {
                if (childAffiliation.getMetadataSets().size() > 0 && childAffiliation.getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO)
                {
                    MdsOrganizationalUnitDetailsVO detailsVO = (MdsOrganizationalUnitDetailsVO) childAffiliation.getMetadataSets().get(0);
                    if (detailsVO.getAlternativeNames().size() > 0)
                    {
                        sb.append(detailsVO.getAlternativeNames().get(0) + "\n");
                    }
                }
            }
            logger.debug(sb.toString());
        }
        // check number of children
        assertEquals("Not all of the specific affiliations could be found!", 4, specificAffiliationList.size());

        // Remove all affiliations that were created before        
        AffiliationCreator.deleteAllAffiliationsContainingThreeAsteriskes();

        // log out
        logout(systemAdministratorUserHandle);

    }

}
