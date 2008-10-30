package test.framework.sm;

import static org.junit.Assert.assertNotNull;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Test cases for the basic service ScopeHandler.
 *
 * @author Peter Broszeit (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class TestScopeHandler
{
    private Logger logger = Logger.getLogger(getClass());

    /**
     * Test method for {@link de.fiz.escidoc.sm.ScopeHandlerLocal#retrieveScopes(java.lang.String)}.
     */
    @Test
    public void retrieveScopes() throws Exception
    {
        long zeit = -System.currentTimeMillis();
        String scopes = ServiceLocator.getScopeHandler().retrieveScopes( null );
        zeit += System.currentTimeMillis();
        logger.info("retrieveScopes()->" + zeit + "ms");
        assertNotNull(scopes);
        logger.debug("Scopes()=" + scopes);
    }

    /**
     * Test method for {@link de.fiz.escidoc.sm.ScopeHandlerLocal#retrieve(java.lang.String)}.
     */
    @Test
    public void retrieveAdminScopeScope() throws Exception
    {
        String id = "2";
        long zeit = -System.currentTimeMillis();
        String scope = ServiceLocator.getScopeHandler().retrieve(id);
        zeit += System.currentTimeMillis();
        logger.info("retrieveScope()->" + zeit + "ms");
        assertNotNull(scope);
        logger.debug("Scope()=" + scope);
    }
}
