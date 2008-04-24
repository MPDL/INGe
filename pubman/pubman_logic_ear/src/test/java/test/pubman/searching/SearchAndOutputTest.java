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

package test.pubman.searching;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import test.pubman.TestBase; 
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.pubman.PubItemDepositing;
import de.mpg.escidoc.services.pubman.PubItemSearching;

/**
 * Test class for simple search.
 * 
 * @author Vladislav Makarenko (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SearchAndOutputTest extends TestBase
{
	private Logger logger = Logger.getLogger(getClass());
	
    private PubItemSearching pubSearching;
    private PubItemDepositing pubItemDepositing;
    
    

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception
    {
        pubSearching = (PubItemSearching)getService(PubItemSearching.SERVICE_NAME);
        pubItemDepositing = (PubItemDepositing)getService(PubItemDepositing.SERVICE_NAME);
    }

    /**
     * Test method for
     * {@link de.mpg.escidoc.services.pubman.searching.PubItemSearchingBean#search(java.lang.String, boolean)}.
     * 
     * @throws Exception
     */
//    @Ignore("See FIZ Bugzilla #370")
    @Test
    public void testSearchAndOutput() throws Exception
    {   
        AccountUserVO user = getUserTestDepScientistWithHandle();
        
        // new item
        PubItemVO myItem = getNewPubItemWithoutFiles();
        String title = "Der kleine Prinz"+System.nanoTime();
        myItem.getMetadata().getTitle().setValue(title);
           
        
        // create PubItem and submit (automatically releases the pubItem)
        ItemRO myItemRef = pubItemDepositing.submitPubItem(myItem, "Test Submit", user).getVersion();
        assertNotNull(myItemRef);
        
        // wait a little bit for indexing...
        // if test fails, the time given for indexing might be too short
        // (with Thread.sleep(2000) the test sometimes failed.
        Thread.sleep(5000);
        
        // search the item      
        String query = "escidoc.title=\""+title+"\"";
        //String query = "escidoc.title=\"test\"";
        
        logger.info("search for " + query);
        
        logger.info("exportFormat = APA, outputFormat = pdf");
        byte[] searchResult = pubSearching.searchAndOutput(query, null, "APA", "pdf"); 
        assertNotNull(searchResult);
        String searchResultString =  new String(searchResult);
        assertTrue("Empty output", ! searchResultString.trim().equals("") );
        logger.debug("output: " + searchResultString);
        
        logger.info("exportFormat = ENDNOTE");
        searchResult = pubSearching.searchAndOutput(query, null, "ENDNOTE", null); 
        assertNotNull(searchResult);
        searchResultString =  new String(searchResult);
        assertTrue("Empty output", ! searchResultString.trim().equals("") );
        logger.debug("output: " + searchResultString);

        pubItemDepositing.deletePubItem(myItemRef, user);
//        ObjectComparator oc = new ObjectComparator(item,result);
//        assertTrue( oc.toString(), oc.isEqual());
    }
    
    
}
