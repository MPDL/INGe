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

import java.io.File;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRXmlUtils;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.w3c.dom.Document;

import de.mpg.escidoc.services.citationmanager.ProcessCitationStyles;
import de.mpg.escidoc.services.citationmanager.ResourceUtil;

/**
 * Test class for citation manager processing component
 * Can be started from eclipse and ant ask. Can be started from the command line 
 * as ant task: <code>ant test-apa</code>. 
 * The system property <code>citation.style</code> is used to define 
 * citation style to test. 
 * See <code>resource/CitationStyles/APA/test.properties</code> for definition of the tests. 
 * The following fields are obligatory to define the correct test:
 * <li><code>data.source</code>     
 * <li><code>test.[i].data.source.item.id</code>     
 * <li><code>test.[i].result</code>     
 *    
 * @author Vladislav Makarenko (initial)
 * @author $Author: vdm $ (last change)
 * @version $Revision: 146 $ $LastChangedDate: 2007-11-12 20:58:08 +0100 (Mon, 12 Nov 2007) $
 */ 

public class ProcessCitationStyleTest extends TestCase {

    private Logger logger = Logger.getLogger(getClass());
    
    private final String DEFAULT_CITSTYLE = "APA";
	
    @Test
    public final void testCitationStyleProcessing() throws Exception  {
    	
    	long start;
    	long exec_time = 0;
    	int FAILED = 0;
    	String cs = System.getProperty("citation.style", DEFAULT_CITSTYLE);
    	Properties tp = TestHelper.getTestProperties(cs); 
    	String ds = tp.getProperty("data.source");
    	boolean IS_IGNORE_MULTIPLY_SPACES = tp.getProperty("ignore.multiply.spaces").equals("yes");
    	
    	//Sort test properties
    	Object[] keys = tp.keySet().toArray();
        Arrays.sort(keys);
        
        // Get number of tests
        String lastKey = (String)keys[keys.length-1];
        Pattern r = Pattern.compile("^test\\.(\\d+)\\..*");
        Matcher m = r.matcher(lastKey);
        if (!m.matches( )) 
            throw new IllegalArgumentException("Bad last key: " + lastKey);
        
        int testsNumber = Integer.valueOf(m.group(1));
        
        logger.info("Number of tests: " + testsNumber);

        //get DataSource
		Document document = JRXmlUtils.parse(ResourceUtil.DATASOURCES_DIRECTORY + ds + ".xml");
        
		ProcessCitationStyles p = new ProcessCitationStyles();
		File csPath = new File(ResourceUtil.CITATIONSTYLES_DIRECTORY);
		
		//get xmls
        p.loadFontStylesFromXml(csPath);
        p.loadCitationStyleFromXml(csPath, cs);
		
    	//get Default/citation-style-test.jrxml
    	p.loadCitationStyleTestJRXml();
    	
    	//populate some of report parameters
    	p.setJasperDesignDefaultProperties(cs);
        
    	//create Test Report
    	p.parseAll(csPath, cs);
    	JasperReport jr = JasperCompileManager.compileReport(p.getJasperDesign());
       
		// go through all tests
        for ( int i = 1; i <= testsNumber; i++  ) {
        	
        	//get input data of test
        	String name = tp.getProperty("test." + i + ".name");
        	String rule = tp.getProperty("test." + i + ".rule");
        	String id = tp.getProperty("test." + i + ".data.source.item.id");
        	String result = tp.getProperty("test." + i + ".result");
        	String testHeader = "Test number: " + i;
        	
        	//check whether they are complete
/*        	if (name==null)
        		throw new IllegalArgumentException ("name is not defined for " + testHeader);
        	else if (rule==null) 
        		throw new IllegalArgumentException ("rule is not defined for " + testHeader);
       		else */
            
        	// test output of props
        	logger.info("-----------------------------------------------");
        	logger.info(testHeader);
        	logger.info("Name: " + name);
        	logger.info("Rule: " + rule);
        	// process the next test if...
        	if ( id == null || result == null ) {
        		logger.info("Id and Result should be defined both. " + testHeader + " is skipped");
        		continue;
        	} 
        	logger.info("id: " + id);
        	
        	String[] ids = id.split(",");
        	String xpath = "";
        	for ( String tmp : ids ) 
        		xpath += "@objid='" + tmp + "' or " ;
        	xpath = xpath.length()>0 ?  xpath.substring(0, xpath.length() - 4) : xpath;
        	
        	xpath = "/item-list/item[" + xpath + "]/md-records/md-record/publication";
        	logger.info("xpath: " + xpath);
        		
        	// getOutput for special text report with DataSource defined with id
	    	start = -System.currentTimeMillis();
//        	String out = new String(p.getTextOutput(jr, document, xpath), "UTF-8");
        	String out = new String(p.getTextOutput(jr, document, xpath));
        	start += System.currentTimeMillis();
        	exec_time +=  start; 
        	logger.info("filling time: " + start);
        	
        	if (IS_IGNORE_MULTIPLY_SPACES)
        		out = TestHelper.cleanCit(out);
        	
        	logger.info("Asserted result: " + result);
        	logger.info("Processed result: " + out);
        	
        	//assertEquals(testHeader + ",", result, out);
        	if (!result.equals(out)) {
        		FAILED++;
            	logger.info("Asserted and Processed results are different");
            	logger.info(testHeader + " is failed");
        	}
        	
        }
    	logger.info("-----------------------------------------------");
    	logger.info("Complete execution time: " + exec_time);
        if (FAILED > 0 ) {
        	logger.info(FAILED + " of " + testsNumber + " tests are failed");
        	fail("FAILED");
        } else {
        	logger.info(testsNumber + " of " + testsNumber + " tests are passed successfully!");
        }
    }	
}
