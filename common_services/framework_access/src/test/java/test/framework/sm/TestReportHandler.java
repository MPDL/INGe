package test.framework.sm;

import static org.junit.Assert.assertNotNull;

import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.junit.Test;
import test.framework.TestBase;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Test cases for the basic service ReportHandler.
 *
 * @author Peter Broszeit (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class TestReportHandler extends TestBase
{
    private static final String PLACEHOLDER = "#id#";
    private static final String REPORT_PARAMETERS = "<report-parameters>"
                                                  +     "<report-definition>" + PLACEHOLDER + "</report-definition>"
                                                  + "</report-parameters>"
                                                  ;
    private Logger logger = Logger.getLogger(getClass());

    /**
     * Test method for {@link de.fiz.escidoc.sm.ReportHandlerLocal#retrieve(java.lang.String)}.
     */
    @Test
    public void retrieveReportDefinitionForNumberOfRetrievals() throws Exception
    {
        String param = REPORT_PARAMETERS.replaceFirst(PLACEHOLDER, "4");
        logger.debug("param=" + param);
        long zeit = -System.currentTimeMillis();
        String report = ServiceLocator.getReportHandler(userHandle).retrieve(param);
        zeit += System.currentTimeMillis();
        logger.info("retrieveReportDefinitionForNumberOfRetrievals()->" + zeit + "ms");
        assertNotNull(report);
        logger.debug("Report()=" + report);
    }

    /**
     * Test method for {@link de.fiz.escidoc.sm.ReportHandlerLocal#retrieve(java.lang.String)}.
     */
    @Test
    public void retrieveReportDefinitionForNumberOfDownloadsForItem() throws Exception
    {
        String param = REPORT_PARAMETERS.replaceFirst(PLACEHOLDER, "5");
        logger.debug("param=" + param);
        long zeit = -System.currentTimeMillis();
        String report = ServiceLocator.getReportHandler(userHandle).retrieve(param);
        zeit += System.currentTimeMillis();
        logger.info("retrieveReportDefinitionForNumberOfDownloadsForItem()->" + zeit + "ms");
        assertNotNull(report);
        logger.debug("Report()=" + report);
    }

    /**
     * Test method for {@link de.fiz.escidoc.sm.ReportHandlerLocal#retrieve(java.lang.String)}.
     */
    @Test
    public void retrieveReportDefinitionForNumberOfDownloadsForFile() throws Exception
    {
        String param = REPORT_PARAMETERS.replaceFirst(PLACEHOLDER, "6");
        logger.debug("param=" + param);
        long zeit = -System.currentTimeMillis();
        String report = ServiceLocator.getReportHandler(userHandle).retrieve(param);
        zeit += System.currentTimeMillis();
        logger.info("retrieveReportDefinitionForNumberOfDownloadsForFile()->" + zeit + "ms");
        assertNotNull(report);
        logger.debug("Report()=" + report);
    }

}
