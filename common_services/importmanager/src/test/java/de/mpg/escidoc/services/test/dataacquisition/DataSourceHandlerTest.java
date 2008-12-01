package de.mpg.escidoc.services.test.dataacquisition;

import org.apache.log4j.Logger;
import org.junit.Test;
import de.mpg.escidoc.services.dataacquisition.DataSourceHandlerBean;



/**
 * 
 * Unit testing class for DataSourceHandler.
 *
 * @author Friederike Kleinfercher (initial creation)
 */
public class DataSourceHandlerTest {

    
    private Logger LOGGER = Logger.getLogger(DataSourceHandlerTest.class);

    @Test
    public void SourcesTest() throws Exception
    {
        this.LOGGER.info("Testing processing of sources.xml");
        
        DataSourceHandlerBean sourceHandler = new DataSourceHandlerBean();
        sourceHandler.getSourceByIdentifier("arXiv");
        this.LOGGER.info("Retrieval of source by identifier (arXiv) successful");
        sourceHandler.getSourceByName("eSciDoc");
        this.LOGGER.info("Retrieval of source by name (eSciDoc) successful");
        
    }
    
}
