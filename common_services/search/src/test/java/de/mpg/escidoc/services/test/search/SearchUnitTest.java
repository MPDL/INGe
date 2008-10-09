package de.mpg.escidoc.services.test.search;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * 
 * Test suite for unit test of search service
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ 
					TestMetadataSearchQuery.class,
					TestQueryParser.class
                    })
                    
                    
public class SearchUnitTest {

}
