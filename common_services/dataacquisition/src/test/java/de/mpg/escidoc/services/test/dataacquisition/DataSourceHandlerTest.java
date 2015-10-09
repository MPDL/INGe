package de.mpg.escidoc.services.test.dataacquisition;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import de.mpg.escidoc.services.dataacquisition.DataSourceHandlerBean;
import de.mpg.escidoc.services.dataacquisition.valueobjects.DataSourceVO;



/**
 * 
 * Unit testing class for DataSourceHandler.
 *
 * @author Friederike Kleinfercher (initial creation)
 */
public class DataSourceHandlerTest 
{

    
    private Logger logger = Logger.getLogger(DataSourceHandlerTest.class);

    
    /**
     * This method tests the retrieval of sources from the sources xml.
     * @throws Exception
     */
    @Test
    public void sourceRetrivalTest() throws Exception
    {
        this.logger.info("Testing processing of sources.xml");
        DataSourceHandlerBean sourceHandler = new DataSourceHandlerBean();
        DataSourceVO test = null;
        List<DataSourceVO> testV = null;
        
        testV = sourceHandler.getSources();
        Assert.assertNotNull(testV);
        this.logger.info("Retrieval of all sources successful");
        
        //Get source by identifier
        test = sourceHandler.getSourceByIdentifier("arXiv");
        Assert.assertNotNull(test);
        this.logger.info("Retrieval of source by identifier (arXiv) successful");
        test = sourceHandler.getSourceByIdentifier("pmc");
        Assert.assertNotNull(test);
        this.logger.info("Retrieval of source by identifier (pmc) successful");
        test = sourceHandler.getSourceByIdentifier("bmc");
        Assert.assertNotNull(test);
        this.logger.info("Retrieval of source by identifier (bmc) successful");
        test = sourceHandler.getSourceByIdentifier("spires");
        Assert.assertNotNull(test);
        this.logger.info("Retrieval of source by identifier (spires) successful");
        test = sourceHandler.getSourceByIdentifier("eSciDoc");
        Assert.assertNotNull(test);
        this.logger.info("Retrieval of source by identifier (eSciDoc) successful");
        
        //Get source by name
        test = sourceHandler.getSourceByName("eSciDoc");
        Assert.assertNotNull(test);
        this.logger.info("Retrieval of source by name (eSciDoc) successful");
        test = sourceHandler.getSourceByName("arxiv");
        Assert.assertNotNull(test);
        this.logger.info("Retrieval of source by name (arxiv) successful");
        test = sourceHandler.getSourceByName("PubMedCentral");
        Assert.assertNotNull(test);
        this.logger.info("Retrieval of source by name (PubMedCentral) successful");
        test = sourceHandler.getSourceByName("BioMed Central");
        Assert.assertNotNull(test);
        this.logger.info("Retrieval of source by name (BioMed Central) successful");
        test = sourceHandler.getSourceByName("spires");
        Assert.assertNotNull(test);
        this.logger.info("Retrieval of source by name (spires) successful");
        
    }
    
}
