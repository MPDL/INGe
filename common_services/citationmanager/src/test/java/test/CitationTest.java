/**
 * 
 */
package test;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.citationmanager.ResourceUtil;
import de.mpg.escidoc.services.citationmanager.XmlHelper;

import static org.junit.Assert.*;

/**
 * @author endres 
 *
 */
public class CitationTest {
	
	private Logger logger = Logger.getLogger(getClass());
	
	private XmlHelper xh = new XmlHelper();
	
	/**
     * Tests CitationStyle.xml (APA by default)
     * TODO: At the moment only the Validation method is being tested, 
     * 		 Citation Style Processing will tested by ProcessCitationStyleTest later  
     * TODO endres: unittest is ignored because of com.topologi.schematron.SchtrnValidator's unusual
     *              relative path behavior. maven resource paths are not recognized.      
     * @throws IOException 
     */
	
    @Test
    public final void testCitationStyleXMLValidation() throws IOException{
    	
    	long start = 0;
    	try {
    		start = System.currentTimeMillis();
    		xh.validateCitationStyleXML(
    				ResourceUtil.getPathToCitationStyles()
    				+ "APA/CitationStyle.xml"	
    		);
        	logger.info("Citation Style XML file for APA is valid.");
    		
    	}catch (CitationStyleManagerException e){
    		logger.info("Citation Style XML file for APA style is not valid.\n" + e.toString());
    		fail();
    	}
    	logger.info("Citation Style Validation time : " + (System.currentTimeMillis() - start));
    }
    
    /**
     * Validates DataSource against XML Schema  
     * @throws IOException 
     */
    @Test
    public final void testDataSourceValidation() throws IOException{
    	
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
     */
    @Test
    public final void testCitationStyleValidation() throws IOException{
    	
    	long start = 0;
    	String csName = 
    		ResourceUtil.getUriToResources()
    		+ ResourceUtil.CITATIONSTYLES_DIRECTORY
    		+ "APA/CitationStyle.xml";
    	logger.info("CitationStyle URI: " + csName);
    	try {
    		start = System.currentTimeMillis();
    		xh.validateCitationStyleXML(csName);
    		logger.info("CitationStyle XML file: " + csName + " is valid.");
    		
    	}catch (CitationStyleManagerException e){ 
    		logger.info("CitationStyle XML file: " + csName + " is not valid.\n", e);
    		fail();
    	}
    	logger.info("Data Source Validation time : " + (System.currentTimeMillis() - start));
    }

}
