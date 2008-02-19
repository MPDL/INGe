/**
 * 
 */
package test;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.citationmanager.XmlHelper;

import static org.junit.Assert.*;

/**
 * @author endres
 *
 */
public class CitationTest {
	
	private Logger logger = Logger.getLogger(getClass());
	
	/**
     * Tests CitationStyle.xml (APA by default)
     * TODO: At the moment only the Validation method is being tested, 
     * 		 Citation Style Processing will tested by ProcessCitationStyleTest later  
     * TODO endres: unittest is ignored because of com.topologi.schematron.SchtrnValidator's unusual
     *              relative path behavior. maven resource paths are not recognized.      
     * @throws IOException 
     */
    @Test
    @Ignore
    public final void testCitationStyleXMLValidation() throws IOException{
    	
    	long start = 0;
    	XmlHelper xh = new XmlHelper();
    	try {
    		start = System.currentTimeMillis();
    		xh.validateCitationStyleXML(
    				"CitationStyles/APA/CitationStyle.xml"	
    		);
        	logger.info("Citation Style XML file for APA is valid.");
    		
    	}catch (CitationStyleManagerException e){
    		logger.error("Citation Style XML file for APA style is not valid.\n" + e.toString());
    		fail();
    	}
    	logger.error("Citation Style Validation time : " + (System.currentTimeMillis() - start));
    }

}
