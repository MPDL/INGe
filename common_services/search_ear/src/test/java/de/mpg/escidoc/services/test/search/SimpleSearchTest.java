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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.util.ObjectComparator;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;

/**
 * Test class for simple search.
 * 
 * @author Miriam Doelle (initial creation)
 * @author $Author: pbroszei $ (last modification)
 * @version $Revision: 451 $ $LastChangedDate: 2007-11-29 13:58:00 +0100 (Thu, 29 Nov 2007) $
 * Revised by NiH: 22.08.2007
 */
public class SimpleSearchTest extends TestBase
{
	
	@Test
	public void dummyTest() {
		
	}
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
//     * {@link de.mpg.escidoc.services.pubman.searching.PubItemSearchingBean#search(java.lang.String, boolean)}.
//     * 
//     * @throws Exception
//     */
////    @Ignore("See FIZ Bugzilla #370")
//    @Test
//    public void testSearch() throws Exception
//    {   
//        AccountUserVO user = getUserTestDepScientistWithHandle();
//        
//        // new item
//        PubItemVO myItem = getNewPubItemWithoutFiles();
//        String title = "Der kleine Prinz"+System.nanoTime();
//        myItem.getMetadata().getTitle().setValue(title);
//           
//        
//        // create PubItem and submit (automatically releases the pubItem)
//        ItemRO myItemRef = pubItemDepositing.submitPubItem(myItem, "Test Submit", user).getVersion();
//        assertNotNull(myItemRef);
//        
//        // wait a little bit for indexing...
//        // if test fails, the time given for indexing might be too short
//        // (with Thread.sleep(2000) the test sometimes failed.
//        Thread.sleep(5000);
//        
//        // search the item      
//        String query = "\""+title+"\"";
//        List<PubItemResultVO> searchResultList = pubSearching.search(query, false);
//        assertNotNull(searchResultList);
//        assertEquals("Wrong number of search results",1, searchResultList.size());
//        PubItemResultVO result = searchResultList.get(0);
//        
//        PubItemVO item = getPubItemFromFramework(result.getVersion(), user);
//        ObjectComparator oc = new ObjectComparator(item,result);
//        assertTrue( oc.toString(),oc.isEqual());
//    }
//    
//    
//    /**
//     * Test method for
//     * {@link de.mpg.escidoc.services.pubman.searching.PubItemSearchingBean#search(java.lang.String, boolean)}.
//     * 
//     * @throws Exception
//     */
////    @Ignore("See FIZ Bugzilla #370")
//    @Test
//    public void testSearchWithFile() throws Exception
//    {   
//        AccountUserVO user = getUserTestDepScientistWithHandle();
//        
//        // new item
//        PubItemVO myItem = getNewPubItemWithoutFiles();
//        String title = "Der kleine Prinz"+System.nanoTime();
//        myItem.getMetadata().getTitle().setValue(title);
//        // Add file to item
//        FileVO file = new FileVO();
//        String testfile = "src/test/resources/searching/searchTest/Der_kleine_Prinz_Auszug.pdf";
//        file.setContent(uploadFile(testfile, "application/pdf", user.getHandle()).toString());
//        file.setContentType(ContentType.PUBLISHER_VERSION);
//        file.setVisibility(Visibility.PUBLIC);
//        file.setName("Der_kleine_Prinz_Auszug.pdf");
//        file.setMimeType("application/pdf");
//        file.setDescription("Auszug aus \"Der kleine Prinz\" von Antoine de Saint-Exupery.");
//        myItem.getFiles().add(file);
//        
//        
//        // create PubItem and submit (automatically releases the pubItem)
//        ItemRO myItemRef = pubItemDepositing.submitPubItem(myItem, "Test Submit", user).getVersion();
//        getPubItemFromFramework(myItemRef, user);
//        
//        // wait a little bit for indexing...
//        // if test fails, the time given for indexing might be too short
//        // (with Thread.sleep(2000) the test sometimes failed.
//        Thread.sleep(5000);
//        
//        // search the item      
//        String query = "\""+title+"\" AND \"Antoine de Saint-Exupery\"";
//        List<PubItemResultVO> searchResultList = pubSearching.search(query, true);
//        assertNotNull(searchResultList);
//        assertEquals("Wrong number of search results",1, searchResultList.size());
//        PubItemResultVO result = searchResultList.get(0);
//        
//        PubItemVO item = getPubItemFromFramework(result.getVersion(), user);        
//        ObjectComparator oc = new ObjectComparator(item,result);
//        assertTrue( oc.toString(),oc.isEqual());
//    }
}
