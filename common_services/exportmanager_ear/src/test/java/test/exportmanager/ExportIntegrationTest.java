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
 * JUnit test class for Export Manager component   
 * @author Author: Vlad Makarenko (initial creation) 
 * @author $Author: vdm $ (last modification) 
 * @version $Revision: 68 $ $LastChangedDate: 2007-12-11 12:41:20 +0100 (Tue, 11 Dec 2007) $
 */
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.services.exportmanager.ExportManagerException;
import de.mpg.escidoc.services.exportmanager.ExportHandler;
import de.mpg.escidoc.services.exportmanager.Export.ArchiveFormats;

import test.TestHelper;

import javax.naming.InitialContext;



public class ExportIntegrationTest 
{
		private ExportHandler export;
	    private String pubManItemList;
	    private String facesItemList;
		private String badItemList;
	    private String endNoteTestOutput;

	    private Logger logger = Logger.getLogger(getClass());
	    private static final String ENDNOTE_FORMAT = "ENDNOTE";

	    /**
	     * Init  Export bean.
	     * @throws Exception Any Exception.
	     */
	    @Before
	    public final void getExport() throws Exception
	    {
	        InitialContext ctx = new InitialContext();
	        export = (ExportHandler) ctx.lookup(ExportHandler.SERVICE_NAME);
	    }
	    
	    
	    /**
	     * Get test item list from XML 
	     * @throws Exception
	     */
	    @Before
	    public final void getItemList() throws Exception
	    {
	    	pubManItemList = TestHelper.getItemListFromFramework(TestHelper.CONTENT_MODEL_PUBMAN);
			assertFalse("PubMan item list from framework is empty", pubManItemList == null || pubManItemList.trim().equals("") );
			logger.info("PubMan item list from framework:\n" + pubManItemList);
			
			facesItemList = TestHelper.getItemListFromFramework(TestHelper.CONTENT_MODEL_FACES);
			assertFalse("Faces item list from framework is empty", facesItemList == null || facesItemList.trim().equals("") );
			logger.info("Faces item list from framework:\n" + facesItemList);
	    }

	    /**
	     * Get bad test item list from XML 
	     * @throws Exception
	     */
	    @Before
	    @Ignore
	    public final void getBadItemList() throws Exception
	    {
	        badItemList = TestHelper.readFile("src/test/resources/item_publication_bad.xml", "UTF-8");
	        assertNotNull("Bad Item list xml is not found", badItemList);
	    }
	    
	    
	    /**
	     * Test explainExport XML file
	     * @throws Exception Any exception.
	     */
	    @Test
	    public final void testExplainExportXML() throws Exception
	    {
	    	String result = export.explainFormatsXML(); 
	        assertFalse("explain formats file is null", result == null || result.trim().equals("") );
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
	     * Test Exports with a item list XML.
	     * @throws Exception Any exception.
	     */	    
	    @Test 
	    public final void testExports() throws Exception
	    {
	    	
	    	byte[] result; 
	    	result = export.getOutput("ENDNOTE", null, null, pubManItemList); 
	    	assertFalse("ENDNOTE export failed", result == null || result.length > 0);
	    	logger.info("ENDNOTE export:\n" + new String(result));
	    	
	    	result = export.getOutput("BIBTEX", null, null, pubManItemList); 
	    	assertFalse("BIBTEX export failed", result == null || result.length > 0);
	    	logger.info("BIBTEX export:\n" + new String(result));
	    	
	    	result = export.getOutput("APA", "snippet", null, pubManItemList); 
	    	assertFalse("APA export failed", result == null || result.length > 0);
	    	logger.info("APA export:\n" + new String(result));
	    	
	    }
	     
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
//	    
	    

}
