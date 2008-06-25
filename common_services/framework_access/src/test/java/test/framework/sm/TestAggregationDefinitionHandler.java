package test.framework.sm;

import static org.junit.Assert.assertNotNull;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Test cases for the basic service AggregationDefinitionHandler.
 *
 * @author Peter Broszeit (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class TestAggregationDefinitionHandler
{
    private Logger logger = Logger.getLogger(getClass());

    /**
     */
    @Test
    public void retrieveAggregationDefinitions() throws Exception
    {
        long zeit = -System.currentTimeMillis();
        String definitions = ServiceLocator.getAggregationDefinitionHandler().retrieveAggregationDefinitions();
        zeit += System.currentTimeMillis();
        logger.info("retrieveAggregationDefinitions()->" + zeit + "ms");
        assertNotNull(definitions);
        logger.debug("AggregationDefinitions()=" + definitions);
    }

}
