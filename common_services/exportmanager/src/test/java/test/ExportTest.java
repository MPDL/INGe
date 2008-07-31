/*
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

package test;


import static org.junit.Assert.*;

/**
 * JUnit test class for Structured Export component   
 * @author Author: Vlad Makarenko (initial creation) 
 * @author $Author: vdm $ (last modification) 
 * @version $Revision: 68 $ $LastChangedDate: 2007-12-11 12:41:20 +0100 (Tue, 11 Dec 2007) $
 */
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import de.mpg.escidoc.services.exportmanager.Export;
import de.mpg.escidoc.services.exportmanager.ExportHandler;
import de.mpg.escidoc.services.exportmanager.ExportManagerException;


import test.TestHelper;

public class ExportTest 
{
		private ExportHandler export = new Export();
	    private String itemList; 
		private String badItemList;
	    private String endNoteTestOutput;

	    private Logger logger = Logger.getLogger(ExportTest.class);
	    private static final String ENDNOTE_FORMAT = "ENDNOTE";


	    /**
	     * Get test item list from XML 
	     * @throws Exception
	     */
	    @Before
	    public final void getItemList() throws Exception
	    {
	        itemList = TestHelper.readFile("src/test/resources/item_publication.xml", "UTF-8");
	        assertNotNull("Item list xml is not found", itemList);
	    }

	    /**
	     * Get bad test item list from XML 
	     * @throws Exception
	     */
	    @Before
	    public final void getBadItemList() throws Exception
	    {
	        badItemList = TestHelper.readFile("src/test/resources/item_publication_bad.xml", "UTF-8");
	        assertNotNull("Bad Item list xml is not found", badItemList);
	    }
	    
	    
	    /**
	     * Get EndNote output test 
	     * @throws Exception
	     */
	    @Before
	    public final void getStructuredTestOutput() throws Exception
	    {
	    	endNoteTestOutput = new String(TestHelper.readBinFile("src/test/resources/EndNoteTestOutput.txt"));
	    	assertNotNull("EndNote output is not found", endNoteTestOutput);
	    }

 
	    /**
	     * Test explainExport XML file
	     * @throws Exception Any exception.
	     */
//	    @Test
//	    public final void testExplainExport() throws Exception
//	    {
//	    	String result = export.explainFormatsXML();  
//	        assertNotNull("explain formats file is null", result);
//	        logger.info("explain formats: " + result);
//	    }
	    
	    
//	    /**
//	     * Test service with a item list XML.
//	     * @throws Exception Any exception.
//	     */
//	    @Test 
//	    public final void testItemsListEndNoteExport() throws Exception
//	    {
////	    	String result = new String(export.getOutput(itemList), "UTF-8");
//	    	String result = new String(export.getOutput(itemList, ENDNOTE_FORMAT));
//	        logger.info("Test item list:\n" + itemList);
//	        logger.info("EndNote test output:\n" + endNoteTestOutput);
//	        logger.info("---------------------------------------------------");
//	        logger.info("EndNote export result:\n" + result);
//	        assertNotNull("EndNote output is null", result);
//	        assertTrue("Export is not equal to test output", result.equals(endNoteTestOutput));
//	    }
//
//	    
//	    /**
//	     * Test service with a non-valid item list XML.
//	     * @throws Exception 
//	     * @throws Exception Any exception.
//	     */
//	    @Test(expected = ExportManagerException.class) 	    
//	    public final void testBadItemsListEndNoteExport() throws Exception
//	    {
//	    	byte[] result = export.getOutput(badItemList, ENDNOTE_FORMAT);
//	    }
	    

}
