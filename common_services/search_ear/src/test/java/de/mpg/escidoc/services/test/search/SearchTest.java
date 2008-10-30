/**
 * 
 */
package de.mpg.escidoc.services.test.search;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/**
 * Integration test suite for service "Search".
 * @author endres
 *
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({ 
                    TestMetadataSearch.class,
                    TestSearchAndExport.class
//					 BracketsTest.class
//                     ,ConstantsTest.class
//                     ,ExpressionsTest.class                    
//                     ,OperationsTest.class
//                     ,PhrasesTest.class
//                     ,QueryParserTest.class                    
//                     ,SearchPubItemsByAffiliationTest.class
//                     SimpleSearchTest.class
//                     ,AdvancedSearchTest.class
//                     ,SearchAndOutputTest.class
                    })
public class SearchTest {

}
