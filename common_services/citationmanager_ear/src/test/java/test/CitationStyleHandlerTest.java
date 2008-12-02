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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.InitialContext;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRXmlUtils;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import de.mpg.escidoc.services.citationmanager.CitationStyleHandler;
import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.citationmanager.ProcessCitationStyles;
import de.mpg.escidoc.services.citationmanager.ProcessCitationStyles.OutFormats;
import de.mpg.escidoc.services.citationmanager.utils.ResourceUtil;
import de.mpg.escidoc.services.citationmanager.utils.Utils;
import de.mpg.escidoc.services.citationmanager.utils.XmlHelper;

public class CitationStyleHandlerTest {

    private String itemList;
	private CitationStyleHandler pcs;


    private Logger logger = Logger.getLogger(getClass());

    /**
     * Init  CitMan bean.
     * @throws Exception Any Exception.
     */
    @Before
    public final void getCitationStyleManager() throws Exception
    {
        InitialContext ctx = new InitialContext();
        pcs = (CitationStyleHandler) ctx.lookup(CitationStyleHandler.SERVICE_NAME);
    }

    /**
     * Get test item list from XML 
     * @throws Exception
     */
    @Before
    public final void getItemList() throws Exception
    {
//    	String dsName = ResourceUtil.getPathToDataSources() + "item-list-inga.xml"; 
//    	logger.info("Data Source:" + dsName);
//    			
//        itemList = ResourceUtil.getResourceAsString(dsName);
    	
        itemList = TestHelper.getItemListFromFramework();
		assertFalse("item list from framework is empty", itemList == null || itemList.trim().equals("") );
//		logger.info("item list from framework:\n" + itemList);
        
    }
    

    /**
     * Test list of styles
     * @throws Exception Any exception.
     */
    @Test
    public final void testExplainStuff() throws Exception {
    	
    	String explain = pcs.explainStyles();
    	assertTrue("Empty explain xml", Utils.checkVal(explain) );
    	logger.info("Explain file:" + explain);
    	
    	logger.info("List of citation styles with output formats: " );
    	for (String s : pcs.getStyles() )
    	{
    		logger.info("Citation Style: " + s);
    		for(String of : pcs.getOutputFormats(s))
    		{
        		logger.info("--Output Format: " + of);
        		logger.info("--Mime Type: " + pcs.getMimeType(s, of));
    		}
    		
    	}	
    	
    	
    	
    }  
    /**
     * Test service for all citation styles and all output formats 
     * @throws Exception Any exception.
     */
    @Test
    public final void testCitManOutput() throws Exception {
    	
    	for (String cs : pcs.getStyles() )
    	{
    		long start;
        	byte[] result;
    		for ( String format : pcs.getOutputFormats(cs) ) {
        		logger.info("Test Citation Style: " + cs);
    			
//    		for ( String ouf : new String[]{"snippet","html"} ) {
    	    	start = System.currentTimeMillis();
    	    	result = pcs.getOutput(cs, format, itemList);
    	    	
//        		logger.info("ItemList\n: " + itemList);
//        		logger.info("Result\n: " + new String(result));
        		
    	    	
    	    	logger.info("Output to " + format + ", time: " + (System.currentTimeMillis() - start));
    	    	assertTrue(format + " output should not be empty", result.length > 0);
    	    	
    	    	
        		logger.info("Number of items to proceed: " + TestHelper.ITEMS_LIMIT);
    	        logger.info(format + " length: " + result.length);
    	        logger.info(format + " is OK");
    	        
    			
    		}
    		
    	}
    }

    
    /**
     * Test service with a wrong Citation Style 
     * @throws Exception Any exception.
     */
    
    @Test(expected = CitationStyleManagerException.class) 
    public final void testWrongStyleCitManOutput() throws Exception  {
    	byte[] result = pcs.getOutput("XYZ", "pdf", itemList);
    }
    
    /**
     * Test service with a wrong output format 
     * @throws Exception Any exception.
     */
    
    @Test(expected = CitationStyleManagerException.class) 
    public final void testWrongFormatCitManOutput() throws Exception {
    	byte[] result = pcs.getOutput("APA", "xyz", itemList);
    }

		
}
