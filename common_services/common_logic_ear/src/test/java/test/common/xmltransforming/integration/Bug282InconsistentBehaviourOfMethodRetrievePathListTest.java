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

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import test.common.AffiliationCreator;
import test.common.xmltransforming.XmlTransformingTestBase;
import de.fiz.escidoc.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.fiz.escidoc.common.exceptions.application.security.AuthenticationException;
import de.fiz.escidoc.common.exceptions.application.security.AuthorizationException;
import de.fiz.escidoc.oum.OrganizationalUnitHandlerRemote;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.AffiliationNotFoundException;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationPathVO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Test class for framework bug #213
 * 
 * @author Johannes M&uuml;ller (initial creation)
 * @author $Author: jmueller $ (last change)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $ 
 */
public class Bug282InconsistentBehaviourOfMethodRetrievePathListTest extends XmlTransformingTestBase
{
    private Logger logger = Logger.getLogger(getClass());

    /**
     * Separator used for concatenation of affiliation names to one organization name.
     */
    public static final String ORGANIZATION_NAME_SEPARATOR = ", ";
    private static XmlTransforming xmlTransforming;

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception
    {
        xmlTransforming = (XmlTransforming) getService(XmlTransforming.SERVICE_NAME);
    }

    /**
     * Checks if FIZ bugzilla issue #213 still exists (When you add components to an item and update it, the framework
     * sets the component-type of the added components to "null".) The test directly accessed the framework (i.e. does
     * not use the intermediate services in the logic layer.)
     * 
     * @throws Exception 
     */
    @Test
    public void testBug282InconsistentBehaviourOfMethodRetrievePathList() throws Exception
    {
        logger.info("### testBug282InconsistentBehaviourOfMethodRetrievePathList ###");

        // log in as system administrator
        String systemAdministratorUserHandle = loginSystemAdministrator();

        // create an top-level affiliation and a child affiliation
        // create MPG (top level) affiliation
        AffiliationVO affiliationVOMPG = AffiliationCreator.getTopLevelAffiliationMPG();
        long uniquer = System.currentTimeMillis();
        String objectIdMPG = AffiliationCreator.createTopLevelAffiliation(affiliationVOMPG, systemAdministratorUserHandle, uniquer);
        affiliationVOMPG.setReference(new AffiliationRO(objectIdMPG));
        // create MPIFG as a sub-affiliation of MPG
        AffiliationVO affiliationVOMPIFG = AffiliationCreator.getAffiliationMPIFG();
        List<String> parentObjIds = new ArrayList<String>();
        parentObjIds.add(objectIdMPG);
        String objectIdMPIFG = AffiliationCreator.createSubAffiliation(affiliationVOMPIFG, parentObjIds, systemAdministratorUserHandle, uniquer);
        affiliationVOMPIFG.setReference(new AffiliationRO(objectIdMPIFG));

        // log in as librarian
        String librarianUserHandle = loginLibrarian();

        // createOrganizationListFromAffiliation for MPIFG
        List<OrganizationVO> organizationListMPIFG = createOrganizationListFromAffiliation(librarianUserHandle, affiliationVOMPIFG);
        assertEquals(1, organizationListMPIFG.size());
        String mpifgAffiliationPath = organizationListMPIFG.get(0).getName().getValue();
        logger.info("MPIFG affiliation path: " + mpifgAffiliationPath);
        assertEquals(mpifgAffiliationPath, affiliationVOMPIFG.getName() + ORGANIZATION_NAME_SEPARATOR + affiliationVOMPG.getName());

        // createOrganizationListFromAffiliation for MPG
        List<OrganizationVO> organizationListMPG = createOrganizationListFromAffiliation(librarianUserHandle, affiliationVOMPG);
        assertEquals(1, organizationListMPG.size());
        String mpgAffiliationPath = organizationListMPG.get(0).getName().getValue();
        logger.info("MPG affiliation path: " + mpgAffiliationPath);
        assertEquals(mpgAffiliationPath, affiliationVOMPG.getName());
    }

    /**
     * @param userHandle
     * @param affiliation
     * @return List of organizations.
     * @throws TechnicalException
     * @throws AffiliationNotFoundException
     */
    public List<OrganizationVO> createOrganizationListFromAffiliation(final String userHandle, final AffiliationVO affiliation)
            throws TechnicalException, AffiliationNotFoundException
    {
        logger.debug("createOrganizationListFromAffiliation(AffiliationVO)");
        if (affiliation == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":createOrganizationListFromAffiliation:affiliation is null");
        }

        final AffiliationRO affiliationRef = affiliation.getReference();
        // initialize the result organization list
        List<OrganizationVO> organizationList = new ArrayList<OrganizationVO>();
        try
        {
            // Get the affiliation paths for the given affiliation from the framework
            OrganizationalUnitHandlerRemote ouHandler = de.mpg.escidoc.services.framework.ServiceLocator.getOrganizationalUnitHandler(userHandle);
            String affObjId = affiliationRef.getObjectId();
            logger.debug("createOrganizationListFromAffiliation(AffiliationVO) - trying to ouHandler.retrievePathList(affObjId) with affObjId="
                    + affObjId);
            String affiliationPathListXML = ouHandler.retrievePathList(affObjId);
            logger.debug("createOrganizationListFromAffiliation() - retrieved path list XML=\n" + affiliationPathListXML);
            List<AffiliationPathVO> affiliationPathVOList = xmlTransforming.transformToAffiliationPathList(affiliationPathListXML);

            // cache already retrieved affiliations
            // every cache entry consists of key:objectId, value: corresponding affiliation
            // initialize cache with given affiliation
            Map<String, AffiliationVO> affiliationCache = new HashMap<String, AffiliationVO>();
            affiliationCache.put(affiliationRef.getObjectId(), affiliation);
            // loop through the list of affiliation paths; every affiliation path yields one OrganizationVO
            for (AffiliationPathVO affPathVO : affiliationPathVOList)
            {
                // create a new OrganizationVO
                OrganizationVO newOrg = new OrganizationVO();

                // create and set organization ADDRESS (to the address of the given affiliation)
                StringBuffer address = new StringBuffer();
                appendAddressPart(affiliation.getAddress(), address);
                appendAddressPart(affiliation.getPostcode(), address);
                appendAddressPart(affiliation.getCity(), address);
                appendAddressPart(affiliation.getCountryCode(), address);
                if (address.length() > 0)
                {
                    newOrg.setAddress(address.toString());
                }

                // create and set organization ID (to the pid of the given affiliation)
                if (affiliation.getReference() != null)
                {
                    newOrg.setIdentifier(affiliation.getReference().getObjectId());
                }

                // create and set organization NAME (to the concatenated list of organization names in the affiliation
                // path)
                // loop through the list of affiliationROs in the affiliation path
                StringBuffer orgName = new StringBuffer();
                for (AffiliationRO affRO : affPathVO.getAffiliationList())
                {
                    String newAffObjId = affRO.getObjectId();
                    AffiliationVO newAff = affiliationCache.get(newAffObjId);
                    // check if affiliation(affRef) is already in the cache
                    if (newAff == null)
                    {
                        // if not, retrieve the affiliation from the framework and put it in the cache
                        String newAffXML = ServiceLocator.getOrganizationalUnitHandler().retrieve(newAffObjId);
                        newAff = xmlTransforming.transformToAffiliation(newAffXML);
                        affiliationCache.put(newAffObjId, newAff);
                    }
                    if (orgName.length() > 0)
                    {
                        orgName.append(ORGANIZATION_NAME_SEPARATOR);
                    }
                    orgName.append(newAff.getName());
                }
                TextVO name = new TextVO();
                name.setValue(orgName.toString());
                newOrg.setName(name);

                // add the new OrganizationVO to the result list
                organizationList.add(newOrg);
            }
        }
        catch (OrganizationalUnitNotFoundException e)
        {
            logger.error("createOrganizationListFromAffiliation(AffiliationVO)", e);
            throw new AffiliationNotFoundException(affiliationRef, e);
        }
        catch (AuthenticationException e)
        {
            logger.error("createOrganizationListFromAffiliation(AffiliationVO)", e);
            throw new TechnicalException(e);
        }
        catch (AuthorizationException e)
        {
            logger.error("createOrganizationListFromAffiliation(AffiliationVO)", e);
            throw new TechnicalException(e);
        }
        catch (RemoteException e)
        {
            logger.error("createOrganizationListFromAffiliation(AffiliationVO)", e);
            throw new TechnicalException(e);
        }
        catch (ServiceException e)
        {
            logger.error("createOrganizationListFromAffiliation(AffiliationVO)", e);
            throw new TechnicalException(e);
        }

        return organizationList;
    }

    private void appendAddressPart(String addressPart, StringBuffer address)
    {

        if (addressPart != null)
        {
            if (address.length() > 0)
            {
                address.append(", ");
            }
            address.append(addressPart);
        }
    }

}
