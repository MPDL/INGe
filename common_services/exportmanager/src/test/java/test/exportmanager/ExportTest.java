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

package test.exportmanager;


import static org.junit.Assert.*;

import java.io.FileOutputStream;

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

import de.mpg.escidoc.services.exportmanager.Export;
import de.mpg.escidoc.services.exportmanager.ExportHandler;
import de.mpg.escidoc.services.exportmanager.ExportManagerException;
import de.mpg.escidoc.services.exportmanager.Export.ArchiveFormats;


import test.TestHelper;

public class ExportTest 
{
		private ExportHandler export = new Export();
	    private String facesItemList; 
	    private String eSciDocItemList; 

	    private Logger logger = Logger.getLogger(ExportTest.class);

	    /**
	     * Get test item list from XML 
	     * @throws Exception
	     */
	    @Before
	    public final void getItemLists() throws Exception
	    {
	        facesItemList = TestHelper.readFile("src/test/resources/search-results.xml", "UTF-8");
	        assertNotNull("facesItemList list xml is not found", facesItemList);
//	        eSciDocItemList = TestHelper.readFile("src/test/resources/item_publication_item6_0.xml", "UTF-8");
	        eSciDocItemList = TestHelper.readFile("src/test/resources/item_test_bibtex.xml", "UTF-8");
	        assertNotNull("eSciDocItemList list xml is not found", eSciDocItemList);
	    }

	    
 
	    /**
	     * Test explainExport XML file
	     * @throws Exception Any exception.
	     */
	    @Test
	    public final void testExplainExport() throws Exception
	    {
	    	String result = export.explainFormatsXML();  
	        assertNotNull("explain formats file is null", result);
	        logger.info("explain formats: " + result);
	    }
	    
	    
	    /**
	     * Test service with a item list XML.
	     * @throws Exception Any exception.
	     */
	    @Test 
	    //@Ignore
	    public final void testExportToArchives() throws Exception
	    {
	        
	    	byte[] result = export.getOutput("CSV", null, ArchiveFormats.tar.toString(), facesItemList); 
	    	assertNotNull("tar generation failed", result);
			FileOutputStream fos = new FileOutputStream("file.tar");
			fos.write(result);				
			fos.close();	    
			
			result = export.getOutput("CSV", null, ArchiveFormats.gzip.toString(), facesItemList); 
			assertNotNull("gzip generation failed", result);
			fos = new FileOutputStream("file.tar.gz");
			fos.write(result);				
			fos.close();
			
			result = export.getOutput("CSV", null, ArchiveFormats.zip.toString(), facesItemList); 
			assertNotNull("zip generation failed", result);
			fos = new FileOutputStream("file.zip");
			fos.write(result);				
			fos.close();	        
	    }
	    
	    /**
	     * Test service with a item list XML.
	     * @throws Exception Any exception.
	     */
	    @Test 
	    public final void testExports() throws Exception
	    {
	    	
	    	byte[] result; 
//	    	result = export.getOutput("ENDNOTE", null, null, eSciDocItemList); 
//	    	assertTrue("ENDNOTE export failed", result != null && result.length > 0);
//	    	logger.info("ENDNOTE export:\n" + new String(result));
	    	
	    	result = export.getOutput("BIBTEX", null, null, eSciDocItemList); 
	    	assertNotNull("BIBTEX export failed", result);
	    	logger.info("BIBTEX export:\n" + new String(result));
	    	
	    	result = export.getOutput("APA", "snippet", null, eSciDocItemList); 
	    	assertTrue("APA export failed", result != null && result.length > 0);
	    	logger.info("APA export:\n" + new String(result));
	    	
	    	
	    }
	    

}
