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
public class DataSourceHandlerTest 
{

    
    private Logger logger = Logger.getLogger(DataSourceHandlerTest.class);

    @Test
    /**
     * This method tests the retrieval of sources from the sources xml.
     * @throws Exception
     */
    public void sourcesTest() throws Exception
    {
        this.logger.info("Testing processing of sources.xml");
        
        DataSourceHandlerBean sourceHandler = new DataSourceHandlerBean();
        sourceHandler.getSources();
        this.logger.info("Retrieval of all sources successful");
        sourceHandler.getSourceByIdentifier("arXiv");
        this.logger.info("Retrieval of source by identifier (arXiv) successful");
        sourceHandler.getSourceByName("eSciDoc");
        this.logger.info("Retrieval of source by name (eSciDoc) successful");
        
    }
    
}
