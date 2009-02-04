/**
 * 
 */
package test;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.mpg.escidoc.services.citationmanager.CitationStyleHandler;
import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.citationmanager.ProcessCitationStyles;
import de.mpg.escidoc.services.citationmanager.ProcessCitationStyles.OutFormats;
import de.mpg.escidoc.services.citationmanager.utils.ResourceUtil;
import de.mpg.escidoc.services.citationmanager.utils.Utils;
import de.mpg.escidoc.services.citationmanager.utils.XmlHelper;

import static org.junit.Assert.*;

/**
 * @author endres 
 *
 */
public class CitationTest {
	
	private Logger logger = Logger.getLogger(getClass());
	
	private XmlHelper xh = new XmlHelper();
	
	private final String dsFileName = "APA_revised_item-list.xml";  
//	private final String dsFileName = "item-list-tobias.xml";  
//	private final String dsFileName = "mpi-psl.xml";  
//	private final String dsFileName = "1.xml";  
	
	private String itemList;
	 
	private CitationStyleHandler pcs = new ProcessCitationStyles();
	
	/**
     * Tests CitationStyle.xml (APA by default)
     * TODO: At the moment only the Validation method is being tested, 
     * 		 Citation Style Processing will tested by ProcessCitationStyleTest later  
     * TODO endres: unittest is ignored because of com.topologi.schematron.SchtrnValidator's unusual
     *              relative path behavior. maven resource paths are not recognized.      
     * @throws IOException 
     */ 

    /**
     * Get test item list from XML 
     * @throws Exception 
     */
    @Before
    public final void getItemList() throws Exception
    {
    	String ds = ResourceUtil.getPathToDataSources() + dsFileName; 
    	logger.info("Data Source:" + ds);
    	itemList = ResourceUtil.getResourceAsString(ds);
    	assertNotNull("Item list xml is not found:", ds);
    	
//    	itemList = TestHelper.getItemsFromFramework_APA();
//		assertFalse("item list from framework is empty", itemList == null || itemList.trim().equals("") );
//		logger.info("item list from framework:\n" + itemList);
		   
//		TestHelper.writeToFile("porverka.xml", itemList.getBytes());

    }	
	
    /**
     * Test list of styles
     * @throws Exception Any exception.
     */
    @Test
    @Ignore
    public final void testGetStyles() throws Exception {
    	logger.info("List of citation styles: " );
    	for (String s : pcs.getStyles() )
    		logger.info("Citation Style: " + s);
    }
     
    /**
     * Test list of styles
     * @throws Exception Any exception.
     */
    @Test
    @Ignore
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
     * Validates DataSource against XML Schema  
     * @throws IOException 
     */
    @Test
    @Ignore
    public final void testDataSourceValidation() throws IOException{
    	
    	//TODO: always recent schema should be provided
		long start = 0;
		String dsName = ResourceUtil.getUriToResources() 
			+ ResourceUtil.DATASOURCES_DIRECTORY 
			+ "item-list-inga.xml";
	      
        try {
        	start = System.currentTimeMillis();
        	xh.validateDataSourceXML(dsName);
            logger.info("DataSource file:" + dsName + " is valid.");
            
        }catch (CitationStyleManagerException e){ 
            logger.info("DataSource file:" + dsName + " is not valid.\n", e);
            fail();
        }
        logger.info("Data Source Validation time : " + (System.currentTimeMillis() - start));
    }
    
    /**
     * Validates CitationStyle against XML Schema and Schematron Schema  
     * @throws IOException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     * @throws CitationStyleManagerException 
     */
    @Test
    @Ignore
    public final void testCitationStyleValidation() throws IOException, CitationStyleManagerException, ParserConfigurationException, SAXException
    {
    	
    	for (String cs : pcs.getStyles() )
    	{
    		logger.info("Validate Citation Style: " + cs);
        	String csName = 
        		ResourceUtil.getUriToResources()
        		+ ResourceUtil.CITATIONSTYLES_DIRECTORY
        		+ cs + "/CitationStyle.xml";  
        	logger.info("CitationStyle URI: " + csName);
        	long start = 0;
        	try {
        		start = -System.currentTimeMillis();
        		xh.validateCitationStyleXML(csName);
        		logger.info("CitationStyle XML file: " + csName + " is valid.");
        		
        	}catch (CitationStyleManagerException e){ 
        		logger.info("CitationStyle XML file: " + csName + " is not valid.\n", e);
        		fail();
        	}
        	logger.info("Citation Style XML Validation time : " + (start + System.currentTimeMillis()));
    	}
    		
	
    }


    /**
     * Test service for all citation styles and all output formats 
     * @throws Exception Any exception.
     */
    @Test
    public final void testCitManOutput() throws Exception {
    	
    	
//    	for (String cs : pcs.getStyles() )
    	for (String cs : new String[]{"APA_revised"} )
//    	for (String cs : new String[]{"AJP"} )
    	{
    		long start;
        	byte[] result;
    		for ( String format : 
//    				pcs.getOutputFormats(cs)
    				new String[]{"pdf"}
    		) {
        		logger.info("Test Citation Style: " + cs);
    			
    	    	start = System.currentTimeMillis();
    	    	result = pcs.getOutput(cs, format, itemList);
    	    	
//        		logger.info("ItemList\n: " + itemList);
//        		logger.info("Result\n: " + new String(result));
    	    	
    	    	logger.info("Output to " + format + ", time: " + (System.currentTimeMillis() - start));
    	    	assertTrue(format + " output should not be empty", result.length > 0);
    	    	
        		logger.info("Number of items to proceed: " + TestHelper.ITEMS_LIMIT);
    	        logger.info(format + " length: " + result.length);
    	        logger.info(format + " is OK");
    	        
    	        TestHelper.writeToFile(cs + "." + format, result);
    			
    		}
    		
    	}
    }
    
 
    
}
