package test.framework.aa;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.Test;

import test.framework.TestBase;
import de.escidoc.www.services.aa.RoleHandler;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class TestRoleHandlerRetrieve extends TestBase
{
    private static RoleHandler roleHandler = null;
    
    private Logger logger = Logger.getLogger(getClass());

    @Test
    public void retrieve()
    {
        try
        {
            roleHandler = ServiceLocator.getRoleHandler(loginSystemAdministrator());
            String xml = roleHandler.retrieve("escidoc:role-moderator");
            assertTrue(xml != null);
            logger.info(xml);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
