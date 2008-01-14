package test.framework.sm;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * test the service SM (Statistic manager)  of the eSciDoc-Framework.
 *
 * @author Peter Broszeit (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({TestScopeHandler.class
                   , TestAggregationDefinitionHandler.class
//                   , TestStatisticDataHandler.class
                   , TestReportDefinitionHandler.class
                   , TestReportHandler.class
                   })
public class TestStatisticManager
{
}
