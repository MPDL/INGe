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

package test.pubman.depositing.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import test.pubman.TestBase;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.FrameworkContextTypeFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.RoleFilter;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Test for retrieving PubCollection in different ways.
 * 
 * @author Miriam Doelle (initial creation)
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 422 $ $LastChangedDate: 2007-11-07 12:15:06 +0100 (Wed, 07 Nov 2007) $
 * @revised by MuJ: 19.09.2007
 */
public class RetrievePubCollectionTest extends TestBase
{
    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(RetrievePubCollectionTest.class);
    private static XmlTransforming xmlTransforming;
    private AccountUserVO user;

    /**
     * Get an XmlTransforming instance and log in as depositor.
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception
    {
        xmlTransforming = (XmlTransforming)getService(XmlTransforming.SERVICE_NAME);
        user = getUserTestDepScientistWithHandle();
    }

    /**
     * Tests retrieving collections by user role. 
     * 
     * @throws Exception
     * TODO tendres: test has to be rewritten, the collections returned is not correct
     */
    @Test
    public void getPubCollectionByDepositor() throws Exception
    {

        // Create filter
        FilterTaskParamVO filterParam = new FilterTaskParamVO();
        RoleFilter roleFilter = filterParam.new RoleFilter("Depositor", user.getReference());
        filterParam.getFilterList().add(roleFilter);

        // ... and transform filter to xml
        String filterString = xmlTransforming.transformToFilterTaskParam(filterParam);

        // Get context list
        String contextList = ServiceLocator.getContextHandler(user.getHandle()).retrieveContexts(filterString);
        // ... and transform to PubCollections.
        List<ContextVO> pubCollectionList = xmlTransforming.transformToContextList(contextList);

        assertNotNull(pubCollectionList);
        assertEquals(2, pubCollectionList.size());
        ContextVO pubCollection = pubCollectionList.get(0);
        assertNotNull(pubCollection.getReference());
        //assertEquals(PUBMAN_TEST_COLLECTION_NAME, pubCollection.getName());
        //assertEquals(PUBMAN_TEST_COLLECTION_DESCRIPTION, pubCollection.getDescription());
    }

    /**
     * Tests retrieving collections by collection type. 
     * 
     * 
     * @throws Exception
     * TODO tendres: test has to be rewritten, the collections returned is not correct
     */
    @Test
    public void getPubCollectionByType() throws Exception
    {
        // Create filter
        FilterTaskParamVO filterParam = new FilterTaskParamVO();
        FrameworkContextTypeFilter typeFilter = filterParam.new FrameworkContextTypeFilter("PubMan");
        filterParam.getFilterList().add(typeFilter);

        // ... and transform filter to xml
        String filterString = xmlTransforming.transformToFilterTaskParam(filterParam);
        logger.debug("getPubCollectionByType() - String filterString=" + filterString);

        // Get context list
        String contextList = ServiceLocator.getContextHandler(user.getHandle()).retrieveContexts(filterString);
        //logger.debug("getPubCollectionByType() - retrieved collection XML=" + toString(getDocument(contextList, false), false));
        // ... and transform to PubCollections.
        List<ContextVO> pubCollectionList = xmlTransforming.transformToContextList(contextList);

        assertNotNull(pubCollectionList);
        assertTrue("At least one (sample) PubMan collection has to exist in the framework!", pubCollectionList.size() >= 1);

        for (ContextVO pubCollection : pubCollectionList)
        {
            assertNotNull(pubCollection.getReference());
            if (pubCollection.getReference().getObjectId() == PUBMAN_TEST_COLLECTION_ID)
            {
                // assertEquals(PUBMAN_TEST_COLLECTION_NAME, pubCollection.getName());
                // assertEquals(PUBMAN_TEST_COLLECTION_DESCRIPTION, pubCollection.getDescription());
            }
        }
    }

    /**
     * Tests retrieving collections by user role and collection type. 
     * 
     * @throws Exception
     * TODO tendres: test has to be rewritten, the collections returned is not correct
     */
    @Test
    public void getPubCollectionByRoleAndType() throws Exception
    {

        // Create filter
        FilterTaskParamVO filterParam = new FilterTaskParamVO();
        RoleFilter roleFilter = filterParam.new RoleFilter("Depositor", user.getReference());
        filterParam.getFilterList().add(roleFilter);
        FrameworkContextTypeFilter typeFilter = filterParam.new FrameworkContextTypeFilter("PubMan");
        filterParam.getFilterList().add(typeFilter);

        // ... and transform filter to xml
        String filterString = xmlTransforming.transformToFilterTaskParam(filterParam);
        if (logger.isDebugEnabled())
        {
            logger.debug("getPubCollectionByRoleAndType() - String filterString=" + filterString);
        }

        // Get context list
        String contextList = ServiceLocator.getContextHandler(user.getHandle()).retrieveContexts(filterString);
        // ... and transform to PubCollections.
        List<ContextVO> pubCollectionList = xmlTransforming.transformToContextList(contextList);

        assertNotNull(pubCollectionList);
        assertEquals(2, pubCollectionList.size());
        ContextVO pubCollection = pubCollectionList.get(0);
        assertNotNull(pubCollection.getReference());
        // assertEquals(PUBMAN_TEST_COLLECTION_NAME, pubCollection.getName());
        // assertEquals(PUBMAN_TEST_COLLECTION_DESCRIPTION, pubCollection.getDescription());
    }

    /**
     * Tests retrieving one collection by collection id. 
     * 
     * @throws Exception
     */
    @Test
    public void getPubCollectionById() throws Exception
    {

        // Get context list
        String context = ServiceLocator.getContextHandler(user.getHandle()).retrieve(PUBMAN_TEST_COLLECTION_ID);
        // ... and transform to PubCollections.
        ContextVO pubCollection = xmlTransforming.transformToContext(context);

        assertNotNull(pubCollection);
        assertNotNull(pubCollection.getReference());
        assertEquals(PUBMAN_TEST_COLLECTION_NAME, pubCollection.getName());
        assertEquals(PUBMAN_TEST_COLLECTION_DESCRIPTION, pubCollection.getDescription());
    }
}
