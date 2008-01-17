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

package test.common.datagathering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import test.common.AffiliationCreator;
import test.common.TestBase;
import de.mpg.escidoc.services.common.DataGathering;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.datagathering.DataGatheringBean;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Test class for {@link de.mpg.escidoc.services.common.datagathering.DataGatheringBean}.
 * 
 * @author Johannes Mueller (initial creation)
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $
 * @revised by MuJ: 03.09.2007
 */
public class CreateOrganizationsTest extends TestBase
{
    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(CreateOrganizationsTest.class);

    /**
     * This Map contains a mapping between abbrevations and object ids. See the {@link test.common.AffiliationCreator}
     * class for details.
     */
    private static Map<String, String> abbreviationToObjIdMapping;

    /**
     * @throws Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        // create the affiliation structure (as the structure is only read in the test methods, this can/should be done
        // here)
        String systemAdministratorUserHandle = loginSystemAdministrator();
        CreateOrganizationsTest.abbreviationToObjIdMapping = AffiliationCreator.createAffiliationStructure(systemAdministratorUserHandle);
        logout(systemAdministratorUserHandle);
    }

    /**
     * @throws Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception
    { 
        // Remove all affiliations that were created before the tests
        AffiliationCreator.deleteAllAffiliationsContainingThreeAsteriskes();
    }

    /**
     * Test method for
     * {@link de.mpg.escidoc.services.common.datagathering.DataGatheringBean#createOrganizationListFromAffiliation(de.mpg.escidoc.services.common.valueobjects.AffiliationVO)}.
     * 
     * @throws Exception 
     */
    @Test
    public void testCreateOrganizationListFromAffiliationHG() throws Exception
    {
        final String methodName = "testCreateOrganizationListFromAffiliationHG";
        List<OrganizationVO> orgList;
        String affPath;
        String[] affPathNames;

        // log in as system administrator
        String systemAdministratorUserHandle = loginSystemAdministrator();
        logger.debug(methodName + "() - String systemAdministratorUserHandle=" + systemAdministratorUserHandle);

        // retrieve affiliation 'HG' from the framework
        String hg2XML = ServiceLocator.getOrganizationalUnitHandler(systemAdministratorUserHandle).retrieve(abbreviationToObjIdMapping.get("objectIdHG"));
        AffiliationVO hg = ((XmlTransforming) getService(XmlTransforming.SERVICE_NAME)).transformToAffiliation(hg2XML);
        // get the organization list for HG:
        orgList = ((DataGathering) getService(DataGathering.SERVICE_NAME)).createOrganizationListFromAffiliation(systemAdministratorUserHandle, hg);
        // check orgList for HG:
        // only HG
        assertEquals(orgList.size(), 1);
        affPath = orgList.get(0).getName().getValue();
        logger.debug(methodName + "() - HG: String orgList.get(0).getName()=\n" + affPath);
        affPathNames = affPath.split(DataGatheringBean.ORGANIZATION_NAME_SEPARATOR);
        assertEquals(1, affPathNames.length);
        assertTrue(affPathNames[0].startsWith("Helmholtz-Gemeinschaft"));

        // logout
        logout(systemAdministratorUserHandle);
    }

    /**
     * Test method for
     * {@link de.mpg.escidoc.services.common.datagathering.DataGatheringBean#createOrganizationListFromAffiliation(de.mpg.escidoc.services.common.valueobjects.AffiliationVO)}.
     * 
     * @throws Exception 
     */
    @Test
    public void testCreateOrganizationListFromAffiliationZEL2() throws Exception
    {
        final String methodName = "testCreateOrganizationListFromAffiliationHG";
        List<OrganizationVO> orgList;
        String affPath;
        String[] affPathNames;

        // log in as system administrator
        String systemAdministratorUserHandle = loginSystemAdministrator();
        logger.debug(methodName + "() - String systemAdministratorUserHandle=" + systemAdministratorUserHandle);

        // retrieve affiliation 'ZEL2' from the framework
        String zel2XML = ServiceLocator.getOrganizationalUnitHandler(systemAdministratorUserHandle).retrieve(abbreviationToObjIdMapping.get("objectIdZEL2"));
        logger.debug(methodName + "() - String zel2XML=\n" + toString(getDocument(zel2XML, false), false));
        AffiliationVO zel2 = ((XmlTransforming) getService(XmlTransforming.SERVICE_NAME)).transformToAffiliation(zel2XML);
        // get the organization list for ZEL2:
        orgList = ((DataGathering) getService(DataGathering.SERVICE_NAME)).createOrganizationListFromAffiliation(systemAdministratorUserHandle, zel2);
        // check orgList for ZEL2:
        // ZEL2 -> MPH-HD -> MPIMF -> MPG
        assertEquals(orgList.size(), 1);
        affPath = orgList.get(0).getName().getValue();
        logger.debug(methodName + "() - ZEL2: String orgList.get(0).getName()=\n" + affPath);
        affPathNames = affPath.split(DataGatheringBean.ORGANIZATION_NAME_SEPARATOR);
        assertEquals(4, affPathNames.length);
        assertTrue(affPathNames[0].startsWith("Zentrale Einrichtung Lichtmikroskopie des MPI für Medizinische Forschung_2"));
        assertTrue(affPathNames[1].startsWith("Max-Planck-Haus Heidelberg"));
        assertTrue(affPathNames[2].startsWith("Max-Planck-Institut für medizinische Forschung"));
        assertTrue(affPathNames[3].startsWith("Max-Planck-Gesellschaft"));

        // logout
        logout(systemAdministratorUserHandle);
    }

    /**
     * Test method for
     * {@link de.mpg.escidoc.services.common.datagathering.DataGatheringBean#createOrganizationListFromAffiliation(de.mpg.escidoc.services.common.valueobjects.AffiliationVO)}.
     * 
     * @throws Exception 
     */
    @Test
    public void testCreateOrganizationListFromAffiliationFuBerlin() throws Exception
    {
        final String methodName = "testCreateOrganizationListFromAffiliationHG";
        List<OrganizationVO> orgList;
        String affPath;
        String[] affPathNames;

        // log in as system administrator
        String systemAdministratorUserHandle = loginSystemAdministrator();
        logger.debug(methodName + "() - String systemAdministratorUserHandle=" + systemAdministratorUserHandle);

        // retrieve affiliation 'FU-BERLIN' from the framework
        String fuBerlinXML = ServiceLocator.getOrganizationalUnitHandler(systemAdministratorUserHandle).retrieve(abbreviationToObjIdMapping.get("objectIdFuBerlin"));
        logger.debug(methodName + "() - String fuBerlinXML=\n" + toString(getDocument(fuBerlinXML, false), false));
        AffiliationVO fuBerlin = ((XmlTransforming) getService(XmlTransforming.SERVICE_NAME)).transformToAffiliation(fuBerlinXML);
        // get the organization list for FU-BERLIN:
        orgList = ((DataGathering) getService(DataGathering.SERVICE_NAME)).createOrganizationListFromAffiliation(systemAdministratorUserHandle, fuBerlin);
        // check orgList size for FU-BERLIN:
        // FU-BERLIN -> MPG, FU-BERLIN -> HG, FU-BERLIN -> FG
        assertEquals(3, orgList.size());
        // check orgList entries (order of list entries is not deterministic!)
        boolean mpgOccured = false, hgOccured = false, fgOccured = false;
        for (OrganizationVO org : orgList)
        {
            affPath = org.getName().getValue();
            affPathNames = affPath.split(DataGatheringBean.ORGANIZATION_NAME_SEPARATOR);
            assertEquals(2, affPathNames.length);
            // First entry of path is always FU-BERLIN
            assertTrue("Unerwartet: affPathNames[0]:" + affPathNames[0], affPathNames[0].startsWith("Freie Universität Berlin"));
            // Second entry is either HG or MPG or FG (order non-deterministic)
            if (affPathNames[1].startsWith("Helmholtz-Gemeinschaft"))
            {
                hgOccured = true;
                logger.debug(methodName + "() - FU-BERLIN: HG occured in one of the affiliation paths.");
            }
            if (affPathNames[1].startsWith("Max-Planck-Gesellschaft"))
            {
                mpgOccured = true;
                logger.debug(methodName + "() - FU-BERLIN: MPG occured in one of the affiliation paths.");
            }
            if (affPathNames[1].startsWith("Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V."))
            {
                fgOccured = true;
                logger.debug(methodName + "() - FU-BERLIN: FG occured in one of the affiliation paths.");
            }
        }
        assertTrue(hgOccured && mpgOccured && fgOccured);

        // logout
        logout(systemAdministratorUserHandle);
    }
}
