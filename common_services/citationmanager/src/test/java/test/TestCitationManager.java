/**
 * 
 */
package test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.mpg.escidoc.services.citationmanager.CitationStyleHandler;
import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.citationmanager.utils.ResourceUtil;
import de.mpg.escidoc.services.citationmanager.utils.Utils;
import de.mpg.escidoc.services.citationmanager.utils.XmlHelper;
import de.mpg.escidoc.services.citationmanager.xslt.CitationStyleExecutor;

/**
 * @author endres 
 *
 */
public class TestCitationManager {
    
    private static Logger logger = Logger.getLogger(TestCitationManager.class);
    
    private XmlHelper xh = new XmlHelper();
    
    private final static String dsFileName = "target/test-classes/testFiles/1_JournalArticle.xml"; 
    // 2_ContrToCollectedEdition.xml
    // 
    //target/test-classes/backup/CitationStyleTestCollection.xml
    private static String itemList;
     
    private CitationStyleHandler cse = new CitationStyleExecutor();

    private static int itemsNumber;
    
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
    public static void getItemList() throws Exception
    {
        String ds = dsFileName; 
        itemList = ResourceUtil.getResourceAsString(ds);
        assertNotNull("Item list xml is not found:", ds);
        itemsNumber =  TestHelper.getItemsNumber(ds);
        
////        itemList = TestHelper.getItemsFromFramework_APA();
//      itemList = TestHelper.getTestItemListFromFramework();
//      assertTrue("item list from framework is empty", Utils.checkVal(itemList) );
//      logger.info("item list from framework:\n" + itemList);
           
        
//      TestHelper.writeToFile("porverka.xml", itemList.getBytes());

    }   
    
    /**
     * Test list of styles
     * @throws Exception Any exception.
     */
    @Test
    public final void testGetStyles() throws Exception {
        logger.info("List of citation styles: " );
        for (String s : cse.getStyles() )
            logger.info("Citation Style: " + s);
    } 
     
    /**
     * Test list of styles
     * @throws Exception Any exception.
     */
  //  @Test
    
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
     * Validates DataSource against XML Schema  
     * @throws IOException 
     */
   // @Test
    public final void testDataSourceValidation() throws IOException{
        
        //TODO: always recent schema should be provided
        long start = 0;
        String dsName = dsFileName;
          
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
//    @Test
    public final void testCitationStyleValidation() throws IOException, CitationStyleManagerException, ParserConfigurationException, SAXException
    {
        
//      for (String cs : cse.getStyles() )
        for (String cs : new String[]{"APA","AJP"} )
        {
            logger.info("Validate Citation Style: " + cs);
            String csName = 
                 ResourceUtil.getPathToCitationStyles() + cs + "/CitationStyle.xml";;
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
   // @Test
    public final void testCitManOutput() throws Exception {
        
        
   //  for (String cs : cse.getStyles() )
     for (String cs : new String[]{"APA","AJP"} )
//            for (String cs : new String[]{"APA"} )
        {
            long start;
            byte[] result;
            for ( String format : 
                  cse.getOutputFormats(cs)
//                    new String[]{"snippet", "escidoc_snippet"}
//            new String[]{"pdf"}
//          new String[]{"escidoc_snippet"}
            ) {
                logger.info("Test Citation Style: " + cs);
                
                start = System.currentTimeMillis();
                result = cse.getOutput(cs, format, itemList);
                
//              logger.info("ItemList\n: " + itemList);
//              logger.info("Result\n: " + new String(result));
                
                logger.info("Output to " + format + ", time: " + (System.currentTimeMillis() - start));
                assertTrue(format + " output should not be empty", result.length > 0);
                
                logger.info("Number of proceeded items: " + itemsNumber);
                logger.info(format + " length: " + result.length);
                logger.info(format + " is OK");
                
                TestHelper.writeToFile("target/" + cs + "." + format, result);
                
            }
            
        }
    }
    
   // @Test
    // @Ignore
     public final void testJusOutput() throws Exception {
       	
       	CitationStyleExecutor cse = new CitationStyleExecutor();
         
//       for (String cs : pcs.getStyles() )
             byte[] result;
         	
         	result = cse.getOutput("JUS", "snippet", itemList);
         	
         	System.out.println(new String(result, "UTF-8"));
             
//         TestHelper.writeToFile(cs + "." + format, result);

         	
             
     }
    
 
    
}
