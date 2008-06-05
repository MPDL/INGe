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

package de.mpg.escidoc.services.test.search;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO.CreatorRole;

/**
 * Test class for {@link PubItemSearching#searchPubItemsByAffiliation(AffiliationRO)}
 * 
 * @author Miriam Doelle (initial creation)
 * @author $Author: pbroszei $ (last modification)
 * @version $Revision: 451 $ $LastChangedDate: 2007-11-29 13:58:00 +0100 (Thu, 29 Nov 2007) $
 * Revised by NiH: 22.08.2007
 */
public class SearchPubItemsByAffiliationTest extends TestBase
{
//    /**
//     * Logger for this class
//     */
//    private static final Logger logger = Logger.getLogger(SearchPubItemsByAffiliationTest.class);
//
//    private PubItemSearching pubSearching;
//    private PubItemDepositing pubItemDepositing;
//
//    /**
//     * @throws Exception
//     */
//    @Before
//    public void setUp() throws Exception
//    {
//        pubSearching = (PubItemSearching)getService(PubItemSearching.SERVICE_NAME);
//        pubItemDepositing = (PubItemDepositing)getService(PubItemDepositing.SERVICE_NAME);
//    }
//
//    /**
//     * Test method for
//     * {@link de.mpg.escidoc.services.pubman.searching.PubItemSearchingBean#searchPubItemsByAffiliation(de.mpg.escidoc.services.common.referenceobjects.AffiliationRO)}.
//     * 
//     * @throws Exception
//     */
//    @Test
//    @Ignore("The searchhandler is unable to retrieve more than 100 items")
//    public void testSearchPubItemsByAffiliation() throws Exception
//    {
//        AccountUserVO user = getUserTestDepScientistWithHandle();
//
//        // create PubItem and submit (automatically releases the pubItem)
//        PubItemVO myItem = getNewPubItemWithoutFiles();
//        OrganizationVO creatorOrg = new OrganizationVO();
//        TextVO textVO = new TextVO( "Max Planck Society" );
//        creatorOrg.setAddress("Max-Planck-Str. 1");
//        creatorOrg.setName(textVO);
//        creatorOrg.setIdentifier("escidoc:persistent26");
//        myItem.getMetadata().getCreators().add(new CreatorVO(creatorOrg, CreatorRole.COMMENTATOR));
//        ItemRO myItemRef = pubItemDepositing.submitPubItem(myItem, "Test Submit", user).getVersion();
//        logger.info("Item '" + myItemRef.getObjectId() + "' submitted.");
//
//        // wait a little bit for indexing...
//        logger.debug("Waiting 15 seconds to let the framework indexing happen...");
//        Thread.sleep(15000);
//
//        AffiliationRO affiliationRO = new AffiliationRO("escidoc:persistent1");
//        AffiliationVO affiliationVO = new AffiliationVO();
//        affiliationVO.setReference(affiliationRO);
//        
//        // search the item on the same organizational level where the item was created
//        List<PubItemVO> searchResultList = pubSearching.searchPubItemsByAffiliation(affiliationVO);   
//        assertNotNull(searchResultList);
//        assertTrue("No items could be found!", searchResultList.size() != 0 );
//        boolean itemFound = false;
//        logger.info( "Found " + searchResultList.size() + " Items.");
//        logger.info("Searching for object id '"+myItemRef.getObjectId()+"'.");
//        for (PubItemVO item:searchResultList)            
//        {
//            logger.info("Found item '"+item.getVersion().getObjectId()+"'.");
//            if (item.getVersion().equals(myItemRef))
//            {
//                itemFound = true;
//            }
//        }
//        assertTrue("Could not find the created item!", itemFound);
//     
//        // search the item on one organizational level above where the item was created
//        searchResultList = pubSearching.searchPubItemsByAffiliation(affiliationVO);   
//        assertNotNull(searchResultList);
//        assertTrue("No items could be found!", searchResultList.size() != 0 );
//        itemFound = false;
//        logger.info("Searching for object id '"+myItemRef.getObjectId()+"'.");
//        for (PubItemVO item:searchResultList)            
//        {
//            logger.info("Found item '"+item.getVersion().getObjectId()+"'.");
//            if (item.getVersion().equals(myItemRef))
//            {
//                itemFound = true;
//            }
//        }
//        assertTrue("Could not find the created item!", itemFound);
//    }
}
