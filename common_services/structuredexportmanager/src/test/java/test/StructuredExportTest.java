/*
roject* CDDL HEADER START
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

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * JUnit test class for Structured Export component   
 * @author Author: Vlad Makarenko (initial creation) 
 * @author $Author: vdm $ (last modification) 
 * @version $Revision: 68 $ $LastChangedDate: 2007-12-11 12:41:20 +0100 (Tue, 11 Dec 2007) $
 */
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test; 

import de.mpg.escidoc.services.structuredexportmanager.StructuredExport;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExportHandler;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExportManagerException;
 

import test.TestHelper;

public class StructuredExportTest 
{
		private StructuredExportHandler export = new StructuredExport();
	    private String endNoteTestOutput;

	    private Logger logger = Logger.getLogger(StructuredExportTest.class);
	    
	    private HashMap<String, String> itemLists;
	    
	    public static final Map<String, String> ITEM_LISTS_FILE_MAMES =   
	    	new HashMap<String, String>()   
	    	{  
				{  
//		    		put("ENDNOTE", "src/test/resources/item_publication_item6_0.xml");  
//		    		put("BIBTEX", "src/test/resources/item_test_bibtex.xml");  
//		    		put("CSV", "src/test/resources/faces_item-list.xml");  
		    		put("BAD_ITEM_LIST", "src/test/resources/item_publication_bad.xml");  
		    	}  
	    	};
	    
	    private String fwItemList;	
	    	

	    /**
	     * Get test item list from XML 
	     * @throws Exception
	     */
	    @Before
	    public final void getItemLists() throws Exception
	    {
	    	itemLists = new HashMap<String, String>();
	    	for ( String key : ITEM_LISTS_FILE_MAMES.keySet() )
	    	{
	    		String itemList =  TestHelper.readFile(ITEM_LISTS_FILE_MAMES.get(key), "UTF-8");
	    		assertNotNull("Item list xml is not found", itemList);
	    		itemLists.put(key, itemList);
	    	}
	    	
	    	fwItemList = TestHelper.getItemListFromFramework();
    		assertFalse("item list from framework is empty", fwItemList == null || fwItemList.trim().equals("") );
    		logger.info("item list from framework:\n" + fwItemList);
	    	
//	    	FileOutputStream fos = new FileOutputStream("fwItemList.xml");
//	    	fos.write(fwItemList.getBytes());
//	    	fos.close();
	    	
	    }

	    
	    /**
	     * Get EndNote output test 
	     * @throws Exception
	     */
//	    @Before
//	    @Ignore
//	    public final void getStructuredTestOutput() throws Exception
//	    {
//	    	endNoteTestOutput = new String(TestHelper.readBinFile("src/test/resources/EndNoteTestOutput.txt"));
//	    	assertNotNull("EndNote output is not found", endNoteTestOutput);
//	    }

 
	    /**
	     * Test explainExport XML file
	     * @throws Exception Any exception.
	     */
	    @Test
	    public final void testExplainExport() throws Exception
	    {
	    	String result = export.explainFormats();
	        assertNotNull("explain formats file is null", result);
	        logger.info("explain formats: " + result);
	    }
	    
	    /**
	     * Test list of export formats
	     * @throws Exception Any exception.
	     */
	    @Test
	    public final void testFormatList() throws Exception
	    {
	    	String[] fl = export.getFormatsList();
	    	assertTrue("The list of export formats is empty", fl.length>0);
	    	for (String f : fl)
	    		logger.info("Export format: " + f);
	    }
	    
   
	    
	    /**
	     * Test service with a item list XML.
	     * @throws Exception Any exception.
	     */
	    // FIXME tendres: vlad has to take a look on the xslt transformation
	    @Test
	    @Ignore
	    public final void testStructuredExports() throws Exception
	    {
	    	long start;
	    	String[] fl = export.getFormatsList();
	    	
    		String itemList = fwItemList;
	    	
	    	for (String f : new String[]{"ENDNOTE","BIBTEX" })
	    	{
	    		logger.info("Export format: " + f);
	    		logger.info("Number of items to proceed: " + TestHelper.ITEMS_LIMIT);
//	    		String itemList = itemLists.get(f);
//	    		logger.info("Test item list:\n" + itemList);
		    	start = System.currentTimeMillis();
		    	String result = new String(export.getOutput(itemList, f));
	    		logger.info("Processing time: " + (System.currentTimeMillis() - start) );
		    	logger.info("---------------------------------------------------");
		    	logger.info(f + " export result:\n" + result);
		    	assertFalse(f + " output is empty", result == null || result.trim().equals("") );
		    	// assertTrue("Export is not equal to test output", result.equals(endNoteTestOutput));
	    	}
	    	
	    }

	    
	    
	    /**
	     * Test service with a non-valid item list XML.
	     * @throws Exception 
	     * @throws Exception Any exception.
	     */
	    @Test(expected = StructuredExportManagerException.class)
	    @Ignore
	    public final void testBadItemsListEndNoteExport() throws Exception
	    {
	    	byte[] result = export.getOutput(itemLists.get("BAD_ITEM_LIST"), "ENDNOTE");
	    }
	    

}
