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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * JUnit test class for Structured Export component   
 * @author Author: Vlad Makarenko (initial creation) 
 * @author $Author: vdm $ (last modification) 
 * @version $Revision: 68 $ $LastChangedDate: 2007-12-11 12:41:20 +0100 (Tue, 11 Dec 2007) $
 */
import net.sf.saxon.om.SiblingCountingNode;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import test.TestHelper;

import de.mpg.escidoc.services.exportmanager.Export;
import de.mpg.escidoc.services.exportmanager.ExportHandler;
import de.mpg.escidoc.services.exportmanager.ExportManagerException;
import de.mpg.escidoc.services.exportmanager.Export.ArchiveFormats;



public class ExportTest 
{
		private ExportHandler export = new Export();
	    private String pubManItemList;
	    private String facesItemList;
	    private long start = 0;

	    private Logger logger = Logger.getLogger(ExportTest.class);

	    /**
	     * Get test item list from XML 
	     * @throws Exception
	     */
	    @Before
	    public final void getItemLists() throws Exception
	    {
	    	FileOutputStream fos;
	    	
	    	pubManItemList = TestHelper.getItemListFromFramework(TestHelper.CONTENT_MODEL_PUBMAN, "5");
			assertFalse("PubMan item list from framework is empty", pubManItemList == null || pubManItemList.trim().equals("") );
			//logger.info("PubMan item list from framework:\n" + pubManItemList);
			
			facesItemList = TestHelper.getItemListFromFramework(TestHelper.CONTENT_MODEL_FACES, "5");
			assertFalse("Faces item list from framework is empty", facesItemList == null || facesItemList.trim().equals("") );
			logger.info("Faces item list from framework:\n" + facesItemList);
			
			
//			fos = new FileOutputStream("facesItemListSmall.xml");
//			fos.write(facesItemListSmall.getBytes());				
//			fos.close();	        
			
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
	     * Test calculate XML file
	     * @throws Exception Any exception.
	     */
	    @Test
	    public final void testCalculateItemListFileSizes() throws Exception
	    {
	    	long size = export.calculateItemListFileSizes(facesItemList);
	        assertTrue("Sum of the file sizes should be > 0", size > 0);
	        logger.info("Entire size of the components' files: " + size);
	    }
	    
	    
	    
	    /**
	     * Test generate output into archive.
	     * @throws Exception Any exception.
	     */
	    @Test 
	    public final void testExportsToArchives() throws Exception
	    {
	    	logger.info("heapMaxSize = " + Runtime.getRuntime().maxMemory());

	    	logger.info("Exports to the archive file:");    
	    	File f; 
	    	for (ArchiveFormats af : ArchiveFormats.values())
	    	{
	    		String afString = af.toString();
	    		start = -System.currentTimeMillis();
	    		f = export.getOutputFile("CSV", null, afString, facesItemList); 
	    		start += System.currentTimeMillis();
	    		assertFalse(afString + " generation failed", f == null || f.length() == 0);
	    		logger.info(afString + " generation is OK (" + (start) + "ms), " +
	    				"file name:" + f.getCanonicalPath() + 
	    				", file size:" + f.length() 
	    		);
	    		f.delete();
	    	}
	    	logger.info("End of the exports to the archive files.");    

	    	logger.info("Exports to the byte[]:");    
	    	byte [] ba;
	    	//		FileOutputStream fos;
	    	for (ArchiveFormats af : ArchiveFormats.values())
	    	{
	    		String afString = af.toString();
	    		start = -System.currentTimeMillis();
	    		ba = export.getOutput("CSV", null, afString, facesItemList); 
	    		start += System.currentTimeMillis();
	    		assertFalse(afString + " generation failed", ba == null || ba.length == 0);
	    		logger.info(afString + " generation is OK (" + (start) + "ms), " +
	    				"byte array size:" + ba.length 
	    		);
	    		//			afString = afString.equals(ArchiveFormats.gzip.toString()) ? "tar.gz" : afString; 
	    		//			fos = new FileOutputStream("output." + afString);
	    		//			fos.write(ba);				
	    		//			fos.close();	        
	    	}
	    	logger.info("End of the exports to the byte[].");    


	    }	   

	    /**
	     * Test generate output.
	     * @throws Exception Any exception.
	     */
	    @Test 
	    public final void testExports() throws Exception
	    {
	    	
	    	byte[] result; 
	    	for ( String ef : new String[] { "ENDNOTE", "BIBTEX", "APA" })
	    	{
		    	logger.info("start " + ef + " export ");
		        start = -System.currentTimeMillis();
		    	result = export.getOutput(
		    			ef,
		    			ef.equals("APA") ? "snippet" : null,	
		    			null, 
		    			pubManItemList
		    	);
		    	start += System.currentTimeMillis();
		    	assertFalse(ef + " export failed", result == null || result.length == 0);
		    	logger.info(ef + " export (" + start + "ms):\n" + new String(result));
	    		
	    	}
	    	
	    }
	    

}
