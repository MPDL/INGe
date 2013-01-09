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
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package test.pubman.depositing.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import test.pubman.TestBase;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.FrameworkContextTypeFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.ItemRefFilter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.PubCollectionStatusFilter;
import de.mpg.escidoc.services.common.valueobjects.GrantVO;
import de.mpg.escidoc.services.common.valueobjects.GrantVO.PredefinedRoles;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.pubman.exceptions.ExceptionHandler;

/**
 * Test for retrieving PubCollection in different ways.
 * 
 * @author Miriam Doelle (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @revised by MuJ: 19.09.2007
 */
public class RetrievePubCollectionTest extends TestBase
{
    
    private static XmlTransforming xmlTransforming;
    private AccountUserVO user;
    private HashMap<String, String[]> filterMap = new HashMap<String, String[]>();
    
    private static final String SEARCH_RETRIEVE = "searchRetrieve";
    private static final String VERSION = "version";
    private static final String OPERATION = "operation";


    
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
        
        filterMap.clear();
        filterMap.put(OPERATION, new String[]{SEARCH_RETRIEVE});
        filterMap.put(VERSION, new String[]{"1.1"});
    }

    /**
     * Tests retrieving collections by user depositor role. 
     * 
     * @throws Exception
     */
    @Test
    public void getPubCollectionByDepositor() throws Exception
    {

        List<ContextVO> pubCollectionList = this.getPubCollectionListForRole(user, PredefinedRoles.DEPOSITOR.frameworkValue(), "PubMan");
        
        assertEquals(2, pubCollectionList.size());
        ContextVO pubCollection = pubCollectionList.get(0);
        assertNotNull(pubCollection.getReference());
    }
    
    /**
     * Tests retrieving collections by user moderator role. 
     * 
     * @throws Exception
     */
    @Test
    public void getPubCollectionByModerator() throws Exception
    {

        List<ContextVO> pubCollectionList = this.getPubCollectionListForRole(user, PredefinedRoles.MODERATOR.frameworkValue(), "PubMan");
        
        assertEquals(2, pubCollectionList.size());
        ContextVO pubCollection = pubCollectionList.get(0);
        assertNotNull(pubCollection.getReference());
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
        List<ContextVO> pubCollectionList = this.getPubCollectionListForRole(user, null, "PubMan");

        assertNotNull(pubCollectionList);
        assertTrue("At least one (sample) PubMan collection has to exist in the framework!", pubCollectionList.size() >= 1);
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
        assertTrue(pubCollection.getReference().getObjectId().equals(PUBMAN_TEST_COLLECTION_ID));
    }
      
    private List<ContextVO> getPubCollectionListForRole(AccountUserVO user, String role, String type) throws TechnicalException
    {
        if (user == null)
        {
            throw new IllegalArgumentException(getClass() + ".getPubCollectionListForDepositing: user is null.");
        }
        if (user.getReference() == null || user.getReference().getObjectId() == null)
        {
            throw new IllegalArgumentException(getClass() + ".getPubCollectionListForDepositing: user reference does not contain an objectId");
        }

        try
        {
            List<ContextVO> contextList = new ArrayList<ContextVO>();
            String xmlGrants = ServiceLocator.getUserAccountHandler(user.getHandle()).retrieveCurrentGrants(user.getReference().getObjectId());
            
            List<GrantVO> grants = xmlTransforming.transformToGrantVOList(xmlGrants);
            
            if (grants.size() == 0)
            {
                return contextList;
            }

            // Create filter
            FilterTaskParamVO filterParam = new FilterTaskParamVO();
            
            FrameworkContextTypeFilter typeFilter = filterParam.new FrameworkContextTypeFilter(type);
            filterParam.getFilterList().add(typeFilter);
            
            if (role != null)
            {
                ItemRefFilter itmRefFilter = filterParam.new ItemRefFilter();
                filterParam.getFilterList().add(itmRefFilter);
                boolean hasGrants = false;
                for (GrantVO grant : grants)
                {
                    if (role.equals(grant.getRole()))
                    {
                        if (grant.getObjectRef() != null)
                        {
                            itmRefFilter.getIdList().add(new ItemRO(grant.getObjectRef()));
                            hasGrants = true;
                        }
                    }
                }
                if (!hasGrants)
                {
                    return contextList;
                }
            }

            PubCollectionStatusFilter statusFilter = filterParam.new PubCollectionStatusFilter(ContextVO.State.OPENED);
            filterParam.getFilterList().add(statusFilter);

            HashMap<String, String[]> filterMap = filterParam.toMap();
           
            // Get context list
            String xmlContextList = ServiceLocator.getContextHandler(user.getHandle()).retrieveContexts(filterMap);
            contextList = (List<ContextVO>) xmlTransforming.transformSearchRetrieveResponseToContextList(xmlContextList);
            
            return contextList;

        }
        catch (Exception e)
        {
            // No business exceptions expected.
            ExceptionHandler.handleException(e, "getPubCollectionListForDepositing for user <" + user.getUserid() + ">");
            throw new TechnicalException(e);
        }
    }

}
