package de.mpg.mpdl.inge.dataacquisition;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import de.mpg.mpdl.inge.dataacquisition.valueobjects.DataSourceVO;

/**
 * 
 * Unit testing class for DataSourceHandler.
 * 
 * @author Friederike Kleinfercher (initial creation)
 */
public class DataSourceHandlerTest {
  private static final Logger logger = Logger.getLogger(DataSourceHandlerTest.class);

  /**
   * This method tests the retrieval of sources from the sources xml.
   * 
   * @throws Exception
   */
  @Test
  public void sourceRetrivalTest() throws Exception {
    this.logger.info("Testing processing of sources.xml");
    DataSourceHandlerService sourceHandler = new DataSourceHandlerService();
    DataSourceVO test = null;
    List<DataSourceVO> testV = null;

    testV = sourceHandler.getSources(null, DataSourceHandlerService.PUBLISHED);
    Assert.assertNotNull(testV);
    this.logger.info("Retrieval of all sources successful");

    // Get source by identifier
    test = sourceHandler.getSourceByIdentifier("arXiv");
    Assert.assertNotNull(test);
    this.logger.info("Retrieval of source by identifier (arXiv) successful");

    // Get source by name
    test = sourceHandler.getSourceByName("arxiv");
    Assert.assertNotNull(test);
    this.logger.info("Retrieval of source by name (arxiv) successful");
  }

}
