/**
 * 
 */
package test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.citationmanager.utils.ResourceUtil;
import de.mpg.escidoc.services.citationmanager.utils.Utils;
import de.mpg.escidoc.services.citationmanager.utils.XmlHelper;
import de.mpg.escidoc.services.citationmanager.xslt.CitationStyleExecutor;
import de.mpg.escidoc.services.citationmanager.xslt.CitationStyleManagerImpl;
import de.mpg.escidoc.services.transformation.TransformationBean;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/**
 * @author endres 
 *
 */
public class TestCitationManager {
    
    private static Logger logger = Logger.getLogger(TestCitationManager.class);
    
    private XmlHelper xh = new XmlHelper(); 
    
    private static HashMap<String, String> itemLists;
    
     
    private static CitationStyleExecutor cse = new CitationStyleExecutor();
    private CitationStyleManagerImpl csm = new CitationStyleManagerImpl();

    /**
     * Tests CitationStyle.xml (APA by default)
     * TODO: At the moment only the Validation method is being tested, 
     *       Citation Style Processing will tested by TestCitationStylesSubstantial later  
     * TODO endres: unittest is ignored because of com.topologi.schematron.SchtrnValidator's unusual
     *              relative path behavior. maven resource paths are not recognized.      
     * @throws IOException 
     */ 

    /**
     * Get test item list from XML 
     * @throws Exception 
     */
    @BeforeClass 
    public static void getItemLists() throws Exception
    {
    	
    	itemLists = new HashMap<String, String>();
    	
        for ( String cs: cse.getStyles() ) 
        {
        	String itemList =  TestHelper.getCitationStyleTestXmlAsString(
        			TestHelper.getTestProperties(cs).getProperty("plain.test.xml")   
        	); 
        	assertNotNull("Item list xml is not found", itemList);
        	itemLists.put(cs, itemList);
        }

    }   
    
    /**
     * Test list of styles
     * @throws Exception Any exception.
     */
    @Test
//    @Ignore
    public final void testGetStyles() throws Exception {
        logger.info("List of citation styles: " );
        for (String s : cse.getStyles() )
            logger.info("Citation Style: " + s);
    } 
     
    /**
     * Test list of styles
     * @throws Exception Any exception.
     */
    @Test
//    @Ignore
    public final void testExplainStuff() throws Exception 
    {
        String explain = cse.explainStyles();
        assertTrue("Empty explain xml", Utils.checkVal(explain) );
        logger.info("Explain file:" + explain);
        
        logger.info("List of citation styles with output formats: " );
        for (String s : cse.getStyles() )
        {
            logger.info("Citation Style: " + s);
            for(String of : cse.getOutputFormats(s))
            {
                logger.info("--Output Format: " + of);
                logger.info("--Mime Type: " + cse.getMimeType(s, of));
            }
            
        }   
    }       
    

    /**
     * Test Citation Style Test
     * @throws Exception
     */
//    @Test
//    @Ignore
    public final void testTestCitationStyle() throws Exception
    { 
    	testValidation("Test");
    	testCompilation("Test");
    	testOutput("Test", "pdf", "");
    	testOutput("Test", "escidoc_snippet", "");
    }
    
    
    /**
     * Test Citation Style Test
     * @throws Exception
     */
    @Test
    @Ignore
    public final void testDefaultCitationStyle() throws Exception
    {
    	testValidation("Default");
    	testCompilation("Default");
    	testOutput("Default", "pdf", "");
    	testOutput("Default", "escidoc_snippet", "");
    }

    /**
     * Test complete scope of Citation Styles
     * @throws Exception
     */
    
    @Test
    @Ignore
    public final void testCitationStyles() throws Exception
    {
        for (String cs: cse.getStyles() )
        {
	    	testValidation(cs);
	    	testCompilation(cs);
	    	testOutput(cs);
        }
    }
    
    @Test
//    @Ignore
    public final void testOutputs() throws Exception {
        
     for ( String cs: cse.getStyles() ) 
     {
    	 for ( String format: cse.getOutputFormats(cs) ) 
            {
                testOutput(cs, format);                
            }
            
        }
    }
    
    
    /**
     * Test Sengbusch Collection output  
     * @throws Exception Any exception.
     */
//    @Test
    public final void testSengbuschCollectionOutput() throws Exception {
    	

    	//It is in old MD set
    	Format out = new Format("escidoc-publication-item-list-v2", "application/xml", "UTF-8");
    	Format in = new Format("escidoc-publication-item-list-v1", "application/xml", "UTF-8");
    	TransformationBean trans = ResourceUtil.getTransformationBean();

    	byte[] v2 = trans.transform(
    			ResourceUtil.getResourceAsString("target/test-classes/testFiles/Sengbusch.xml")
    			.getBytes("UTF-8"), in, out, "escidoc"
    	);
    	
   	
    	testOutput("APA", "pdf", "Sengbusch", new String(v2, "UTF-8"));
    	
    }
    
//    @Test
    public final void testArxiv() throws Exception {
    	
    	
    	
    	testOutput("APA", "snippet", "arxiv", 
    			ResourceUtil.getResourceAsString("src/test/resources/testFiles/arXiv:0904-2.3933.xml")
    		);
    	
    }
    
    
    
    /**
     * Validates CitationStyle against XML Schema and Schematron Schema  
     * @throws IOException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     * @throws CitationStyleManagerException 
     */
    public final void testValidation(String cs) throws IOException, CitationStyleManagerException, ParserConfigurationException, SAXException
    {

		logger.info("Validate Citation Style: " + cs);
		long start = -System.currentTimeMillis();
		String report = csm.validate(cs);
		logger.info("Citation Style XML Validation time : " + (start + System.currentTimeMillis()));
		if (report == null)
			logger.info("CitationStyle XML file: " + cs + " is valid.");
		else
		{
			logger.info("CitationStyle XML file: " + cs + " is not valid:\n" + report + "\n");
			fail();
		}    
    }


    /**
     * Test service for citation style compilation 
     * @throws Exception Any exception.
     */
    public final void testCompilation(String cs) throws Exception {
       logger.info("Compilation of citation style: " + cs + "...");
       csm.compile(cs);
       logger.info("OK");
    } 
    
    /**
     * Test service for all citation styles and all output formats 
     * @throws Exception Any exception.
     */


    
    /*
     * outPrefix == null:  omit output file generation
     * outPrefix == "": generate output file, file name by default  
     * outPrefix.length>0 == "": generate output file, use outPrefix as file name prefix   
     * */
    public final void testOutput(String cs, String ouf, String outPrefix, String il) throws Exception 
    {

    	long start;
    	byte[] result;
    	logger.info("Test Citation Style: " + cs);

    	start = System.currentTimeMillis();
    	result = cse.getOutput(cs, ouf, il);

    	logger.info("Output to " + ouf + ", time: " + (System.currentTimeMillis() - start));
    	assertTrue(ouf + " output should not be empty", result.length > 0);

    	logger.info(ouf + " length: " + result.length);
    	logger.info(ouf + " is OK");

    	if ( outPrefix != null)
    		TestHelper.writeToFile("target/" 
    				+ ( ! outPrefix.equals("") ? outPrefix + "_" : "")
    				+ cs + "_" + ouf + "." + XmlHelper.getExtensionByName(ouf), result); 

    }
    
    public final void testOutput(String cs, String ouf, String outPrefix) throws Exception
    {
    	testOutput(cs, ouf, outPrefix, itemLists.get(cs));
    }
    
    public final void testOutput(String cs, String ouf) throws Exception
    {
    	testOutput(cs, ouf, null);
    }
    
    public final void testOutput(String cs) throws Exception
    {
    	for ( String format: cse.getOutputFormats(cs) ) 
        {
            testOutput(cs, format);                
        }
    	
    }
    
}
