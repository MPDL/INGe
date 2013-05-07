package de.mpg.escidoc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import de.mpg.escidoc.handler.AssertionHandlerTest;
import de.mpg.escidoc.handler.PIDHandlerTest;
import de.mpg.escidoc.handler.PIDProviderTest;
import de.mpg.escidoc.handler.PreHandlerTest;
import de.mpg.escidoc.main.PIDMigrationManagerTest;
import de.mpg.escidoc.util.SQLQuerierTest;



@RunWith(Suite.class)
@Suite.SuiteClasses({ 
                    PIDProviderTest.class,
                    PreHandlerTest.class, 
                    SQLQuerierTest.class,
                    AssertionHandlerTest.class,
                    PIDHandlerTest.class,
                    PIDProviderTest.class,
                    PIDMigrationManagerTest.class                    
                    })
public class AllTests
{
}
