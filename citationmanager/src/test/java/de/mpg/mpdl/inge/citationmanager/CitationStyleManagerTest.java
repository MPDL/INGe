/**
 * 
 */
package de.mpg.mpdl.inge.citationmanager;

import static org.junit.Assert.fail;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import de.mpg.mpdl.inge.citationmanager.xslt.CitationStyleManagerImpl;

/**
 * @author endres
 * 
 */
public class CitationStyleManagerTest {

  private static final Logger logger = Logger.getLogger(CitationStyleManagerTest.class);


  private CitationStyleManagerImpl csm = new CitationStyleManagerImpl();


  /**
   * Tests CitationStyle.xml (APA by default) TODO: At the moment only the Validation method is
   * being tested, Citation Style Processing will tested by TestCitationStylesSubstantial later TODO
   * endres: unittest is ignored because of com.topologi.schematron.SchtrnValidator's unusual
   * relative path behavior. maven resource paths are not recognized.
   * 
   * @throws IOException
   */


  /**
   * Validates CitationStyle against XML Schema and Schematron Schema
   * 
   * @throws IOException
   * @throws SAXException
   * @throws ParserConfigurationException
   * @throws CitationStyleManagerException
   */
  public final void testValidation(String cs)
      throws IOException, CitationStyleManagerException, ParserConfigurationException, SAXException {

    logger.info("Validate Citation Style: " + cs);
    long start = -System.currentTimeMillis();
    String report = csm.validate(cs);
    logger.info("Citation Style XML Validation time : " + (start + System.currentTimeMillis()));
    if (report == null)
      logger.info("CitationStyle XML file: " + cs + " is valid.");
    else {
      logger.info("CitationStyle XML file: " + cs + " is not valid:\n" + report + "\n");
      fail();
    }
  }


  /**
   * Test service for citation style compilation
   * 
   * @throws Exception Any exception.
   */
  public final void testCompilation(String cs) throws Exception {
    logger.info("Compilation of citation style: " + cs + "...");
    csm.compile(cs);
    logger.info("OK");
  }
}
