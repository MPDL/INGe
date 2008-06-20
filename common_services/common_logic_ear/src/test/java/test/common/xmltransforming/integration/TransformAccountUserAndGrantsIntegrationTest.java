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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.List;

import javax.xml.rpc.ServiceException;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.common.TestBase;

import de.escidoc.www.services.aa.UserAccountHandler;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.GrantVO;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Test cases for the transformToAccountUser method of the XmlTransforming interface.
 * 
 * @author Peter Broszeit (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 651 $ $LastChangedDate: 2007-12-04 14:16:08 +0100 (Tue, 04 Dec 2007) $
 * @revised by MuJ: 03.09.2007
 */
public class TransformAccountUserAndGrantsIntegrationTest extends TestBase
{
    private static final String ACCOUNT_USER_SCHEMA_FILE = "xsd/soap/user-account/0.5/user-account.xsd";
    private static final String PUBMAN_COLLECTION_ID = "escidoc:persistent3";
    private static final String TEST_DEP_SCIENTIST_LOGIN_NAME = "test_dep_scientist";
    private static final String TEST_DEP_SCIENTIST_ID = "escidoc:user1";
    private static final String TEST_DEP_LIBRARIAN_LOGIN_NAME = "test_dep_lib";
    private static final String TEST_DEP_LIBRARIAN_ID = "escidoc:user2";
    private static XmlTransforming xmlTransforming;
    private static Logger logger = Logger.getLogger(TransformAccountUserAndGrantsIntegrationTest.class);

    private static String userHandle;

    /**
     * @throws Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        xmlTransforming = (XmlTransforming)getService(XmlTransforming.SERVICE_NAME);
    }

    /**
     * @throws HttpException
     * @throws ServiceException
     * @throws IOException
     */
    @Before
    public void setUp() throws HttpException, ServiceException, IOException, URISyntaxException
    {
        userHandle = loginScientist();
    }

    /**
     * @throws WebserverSystemException
     * @throws SqlDatabaseSystemException
     * @throws AuthenticationException
     * @throws RemoteException
     * @throws ServiceException
     */
    @After
    public void tearDown() throws RemoteException, ServiceException, URISyntaxException
    {
        logout(userHandle);
    }

    /**
     * Retrieve the predefined framework user 'test_dep_scientist' and check transforming to AccountUserVO using
     * {@link XmlTransforming#transformToAccountUser(String)}.
     * 
     * @throws Exception
     */
    @Test
    public void transformTestDepScientist() throws Exception
    {
        logger.info("### transformTestDepScientist ###");

        UserAccountHandler uaHandler = ServiceLocator.getUserAccountHandler(userHandle);
        String userid = TEST_DEP_SCIENTIST_LOGIN_NAME;
        String user = uaHandler.retrieve(userid);
        assertXMLValid(user);
        logger.info("The account user XML retrieved from the framework is valid to the schema in " + ACCOUNT_USER_SCHEMA_FILE);

        logger.debug("UserAccount(" + userid + ")=" + user);
        AccountUserVO accountUser = xmlTransforming.transformToAccountUser(user);
        assertNotNull(accountUser);
        assertEquals("escidoc:user1", accountUser.getReference().getObjectId());
        assertEquals(userid, accountUser.getUserid());
        assertTrue(accountUser.isActive());
        assertEquals("Test Depositor Scientist", accountUser.getName());
        // assertEquals("", accountUser.getEmail());
        assertTrue(0 == accountUser.getAffiliations().size());
        logger.info("The account user XML has successfully been transformed into an AffilitationVO.");
    }

    /**
     * Retrieve the grants of the predefined framework user 'test_dep_scientist' and check transforming of the grants.
     * 
     * @throws Exception
     */
    @Test
    public void transformTestDepScientistGrants() throws Exception
    {
        logger.info("### transformTestDepScientistGrants ###");
        logger.info("Framework-URL: " + ServiceLocator.getFrameworkUrl());

        // retrieve account user and transform
        UserAccountHandler uaHandler = ServiceLocator.getUserAccountHandler(userHandle);
        String userid = TEST_DEP_SCIENTIST_LOGIN_NAME;
        String user = uaHandler.retrieve(userid);
        logger.debug("UserAccount(" + userid + ")=" + user);
        assertXMLValid(user);
        AccountUserVO accountUser = xmlTransforming.transformToAccountUser(user);
        assertNotNull(accountUser);
        assertEquals(TEST_DEP_SCIENTIST_ID, accountUser.getReference().getObjectId());

        // retrieve grants and transform
        String grantsXML = uaHandler.retrieveCurrentGrants(accountUser.getReference().getObjectId());
        List<GrantVO> grants = xmlTransforming.transformToGrantVOList(grantsXML);

        // check results
        assertTrue(grants.size() > 1);
        List<GrantVO> accountUserGrants = accountUser.getGrants();
        // check whether test_dep_scientist is 'Depositor' and 'System Administrator'
        int allExpectedGrantsPresent = 0;
        for (GrantVO grant : grants)
        {
            accountUserGrants.add(grant);
            if (grant.getRole().equals(GrantVO.PredefinedRoles.DEPOSITOR.frameworkValue()))
            {
                allExpectedGrantsPresent |= 1; // this is allExpectedGrantsPresent = allExpectedGrantsPresent OR 1;
            }
            if (grant.getRole().equals("escidoc:role-administrator"))
            {
                allExpectedGrantsPresent |= 2; // this is allExpectedGrantsPresent = allExpectedGrantsPresent OR 2;
            }
        }
        assertEquals(3, allExpectedGrantsPresent);
        assertTrue(accountUser.isDepositor());
    }

    /**
     * Retrieve the predefined framework user 'test_dep_lib' and check transforming to AccountUserVO using
     * {@link XmlTransforming#transformToAccountUser(String)}.
     * 
     * @throws Exception
     */
    @Test
    public void transformTestDepLibrarian() throws Exception
    {
        logger.info("### transformTestDepLibrarian ###");

        UserAccountHandler uaHandler = ServiceLocator.getUserAccountHandler(userHandle);
        String userid = TEST_DEP_LIBRARIAN_LOGIN_NAME;
        String user = uaHandler.retrieve(userid);
        logger.debug("UserAccount(" + userid + ")=" + user);
        assertXMLValid(user);
        logger.info("The account user XML retrieved from the framework is valid to the schema in " + ACCOUNT_USER_SCHEMA_FILE);

        AccountUserVO accountUser = xmlTransforming.transformToAccountUser(user);
        assertNotNull(accountUser);
        assertEquals("escidoc:user2", accountUser.getReference().getObjectId());
        assertEquals(userid, accountUser.getUserid());
        // assertEquals("pubman",accountUser.getPassword());
        assertTrue(accountUser.isActive());
        assertEquals("Test Depositor Library", accountUser.getName());
        // assertEquals("", accountUser.getEmail());
        assertTrue(0 == accountUser.getAffiliations().size());
        logger.info("The account user XML has successfully been transformed into an AffilitationVO.");
    }

    /**
     * Retrieve the grants of the predefined framework user 'test_dep_scientist' and check transforming of the grants.
     * 
     * @throws Exception
     */
    @Test
    public void transformTestDepLibrarianGrants() throws Exception
    {
        logger.info("### transformTestDepLibrarianGrants ###");

        // retrieve account user and transform
        UserAccountHandler uaHandler = ServiceLocator.getUserAccountHandler(userHandle);
        String userid = TEST_DEP_LIBRARIAN_LOGIN_NAME;
        String user = uaHandler.retrieve(userid);
        logger.debug("UserAccount(" + userid + ")=" + user);
        assertXMLValid(user);
        AccountUserVO accountUser = xmlTransforming.transformToAccountUser(user);
        assertNotNull(accountUser);
        assertEquals(TEST_DEP_LIBRARIAN_ID, accountUser.getReference().getObjectId());

        // retrieve grants and transform
        String grantsXML = uaHandler.retrieveCurrentGrants(accountUser.getReference().getObjectId());

        logger.info("Grants: " + grantsXML);

        List<GrantVO> grants = xmlTransforming.transformToGrantVOList(grantsXML);

        // check results
        assertTrue(grants.size() > 1);
        List<GrantVO> accountUserGrants = accountUser.getGrants();
        // check whether test_dep_scientist is 'Depositor' and 'Moderator of PubCollection escidoc:persistent3' and 'System Administrator'
        int allExpectedGrantsPresent = 0;
        for (GrantVO grant : grants)
        {
            logger.info("-->" + grant.getRole());
            accountUserGrants.add(grant);
            if (grant.getRole().equals(GrantVO.PredefinedRoles.DEPOSITOR.frameworkValue()))
            {
                allExpectedGrantsPresent |= 1; // this is allExpectedGrantsPresent = allExpectedGrantsPresent OR 1;
            }
            if (grant.getRole().equals(GrantVO.PredefinedRoles.MODERATOR.frameworkValue()))
            {
                // assertEquals(grant.getObjectRef(), PUBMAN_COLLECTION_ID);
                allExpectedGrantsPresent |= 2; // this is allExpectedGrantsPresent = allExpectedGrantsPresent OR 2;
            }
            if (grant.getRole().equals("escidoc:role-administrator"))
            {
                allExpectedGrantsPresent |= 4; // this is allExpectedGrantsPresent = allExpectedGrantsPresent OR 4;
            }
        }
        assertEquals(7, allExpectedGrantsPresent);
        assertTrue(accountUser.isDepositor());
        assertTrue(accountUser.isModerator(new ContextRO(PUBMAN_COLLECTION_ID)));
    }

    /**
     * Retrieve the predefined framework user 'test_editor' and check transforming to AccountUserVO using
     * {@link XmlTransforming#transformToAccountUser(String)}.
     * 
     * @throws Exception
     */
    @Test
    public void transformTestEditor() throws Exception
    {
        logger.info("### transformTestEditor ###");

        UserAccountHandler uaHandler = ServiceLocator.getUserAccountHandler(userHandle);
        String userid = "test_editor";
        String user = uaHandler.retrieve(userid);
        logger.debug("UserAccount(" + userid + ")=" + user);
        assertXMLValid(user);
        logger.info("The account user XML retrieved from the framework is valid to the schema in " + ACCOUNT_USER_SCHEMA_FILE);

        AccountUserVO accountUser = xmlTransforming.transformToAccountUser(user);
        assertNotNull(accountUser);
        assertEquals("escidoc:user3", accountUser.getReference().getObjectId());
        assertEquals(userid, accountUser.getUserid());
        // assertEquals("escidoc",accountUser.getPassword());
        assertTrue(accountUser.isActive());
        assertEquals("Test Editor", accountUser.getName());
        // assertEquals("", accountUser.getEmail());
        assertTrue(0 == accountUser.getAffiliations().size());
        logger.info("The account user XML has successfully been transformed into an AffilitationVO.");
    }

    /**
     * Retrieve the predefined framework user 'test_author' and check transforming to AccountUserVO using
     * {@link XmlTransforming#transformToAccountUser(String)}.
     * 
     * @throws Exception
     */
    @Test
    public void transformTestDepAuthor() throws Exception
    {
        logger.info("### transformTestDepAuthor ###");

        UserAccountHandler uaHandler = ServiceLocator.getUserAccountHandler(userHandle);
        String userid = "test_author";
        String user = uaHandler.retrieve(userid);
        logger.debug("UserAccount(" + userid + ")=" + user);
        assertXMLValid(user);
        logger.info("The account user XML retrieved from the framework is valid to the schema in " + ACCOUNT_USER_SCHEMA_FILE);

        AccountUserVO accountUser = xmlTransforming.transformToAccountUser(user);
        assertNotNull(accountUser);
        assertEquals("escidoc:user4", accountUser.getReference().getObjectId());
        assertEquals(userid, accountUser.getUserid());
        // assertEquals("escidoc",accountUser.getPassword());
        assertTrue(accountUser.isActive());
        assertEquals("Test Author", accountUser.getName());
        // assertEquals("", accountUser.getEmail());
        assertTrue(0 == accountUser.getAffiliations().size());
        assertXMLValid(user);
        logger.info("The account user XML has successfully been transformed into an AffilitationVO.");
    }

    /**
     * Retrieve the predefined framework user 'roland' (system administrator) and check transforming to AccountUserVO
     * using {@link XmlTransforming#transformToAccountUser(String)}.
     * 
     * @throws Exception
     */
    @Test
    public void transformSystemAdministrator() throws Exception
    {
        logger.info("### transformSystemAdministrator ###");

        UserAccountHandler uaHandler = ServiceLocator.getUserAccountHandler(userHandle);
        String userid = "roland";
        String user = uaHandler.retrieve(userid);
        logger.debug("UserAccount(" + userid + ")=" + user);
        assertXMLValid(user);
        logger.info("The account user XML retrieved from the framework is valid to the schema in " + ACCOUNT_USER_SCHEMA_FILE);

        AccountUserVO accountUser = xmlTransforming.transformToAccountUser(user);
        assertNotNull(accountUser);
        assertEquals("escidoc:user42", accountUser.getReference().getObjectId());
        assertEquals(userid, accountUser.getUserid());
        assertTrue(accountUser.isActive());
        assertEquals("roland", accountUser.getName());
        assertEquals("roland@roland", accountUser.getEmail());
        assertTrue(0 == accountUser.getAffiliations().size());
        logger.info("The account user XML has successfully been transformed into an AffilitationVO.");
    }
}
