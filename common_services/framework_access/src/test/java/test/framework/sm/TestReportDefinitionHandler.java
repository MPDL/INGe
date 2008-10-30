package test.framework.sm;

import static org.junit.Assert.assertNotNull;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Test cases for the basic service ReportDefinitionHandler.
 *
 * @author Peter Broszeit (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class TestReportDefinitionHandler
{
    private Logger logger = Logger.getLogger(getClass());

    /**
     * Test method for {@link de.fiz.escidoc.sm.ReportDefinitionHandlerLocal#retrieveReportDefinitions(java.lang.String)}.
     */
    @Test
    public void retrieveReportDefinitions() throws Exception
    {
        long zeit = -System.currentTimeMillis();
        String definitions = ServiceLocator.getReportDefinitionHandler().retrieveReportDefinitions( null );
        zeit += System.currentTimeMillis();
        logger.info("retrieveReportDefinitions()->" + zeit + "ms");
        assertNotNull(definitions);
        logger.debug("ReportDefinition()=" + definitions);
    }

    /**
     * Test method for {@link de.fiz.escidoc.sm.ReportDefinitionHandlerLocal#retrieve(java.lang.String)}.
     */
    @Test
    public void retrieveReportDefinitionForNumberOfRetrievals() throws Exception
    {
        String id = "4";
        long zeit = -System.currentTimeMillis();
        String reportDefinition = ServiceLocator.getReportDefinitionHandler().retrieve(id);
        zeit += System.currentTimeMillis();
        logger.info("retrieveReportDefinitionForNumberOfRetrievals()->" + zeit + "ms");
        assertNotNull(reportDefinition);
        logger.debug("ReportDefinition()=" + reportDefinition);
    }

    /**
     * Test method for {@link de.fiz.escidoc.sm.ReportDefinitionHandlerLocal#retrieve(java.lang.String)}.
     */
    @Test
    public void retrieveReportDefinitionForNumberOfDownloadsForItem() throws Exception
    {
        String id = "5";
        long zeit = -System.currentTimeMillis();
        String reportDefinition = ServiceLocator.getReportDefinitionHandler().retrieve(id);
        zeit += System.currentTimeMillis();
        logger.info("retrieveReportDefinitionForNumberOfDownloadsForItem()->" + zeit + "ms");
        assertNotNull(reportDefinition);
        logger.debug("ReportDefinition()=" + reportDefinition);
    }

    /**
     * Test method for {@link de.fiz.escidoc.sm.ReportDefinitionHandlerLocal#retrieve(java.lang.String)}.
     */
    @Test
    public void retrieveReportDefinitionForNumberOfDownloadsForFile() throws Exception
    {
        String id = "6";
        long zeit = -System.currentTimeMillis();
        String reportDefinition = ServiceLocator.getReportDefinitionHandler().retrieve(id);
        zeit += System.currentTimeMillis();
        logger.info("retrieveReportDefinitionForNumberOfDownloadsForFile()->" + zeit + "ms");
        assertNotNull(reportDefinition);
        logger.debug("ReportDefinition()=" + reportDefinition);
    }
}
