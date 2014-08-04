package de.mpg.escidoc.handler;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.naming.NamingException;

import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.handler.PreHandler.Type;
import de.mpg.escidoc.main.PIDProviderIf;

public class PIDProviderTest
{    
    @Test
    @Ignore
    public void getPid()
    {
        PIDProviderIf pidProvider = new PIDProviderMock();

        String pid = null;
        
        
        try
        {
            pid = pidProvider.getPid("escidoc:418001", Type.COMPONENT, "xx");
            fail("PIDProviderException excpectd");
        }
        catch (PIDProviderException e)
        {
            assert(true);
        }
        
        try
        {
            pid = pidProvider.getPid("escidoc:12345", Type.COMPONENT, "xx");
        }
        catch (PIDProviderException e)
        {
            assertTrue(e.getMessage().contains("No item was found"));
        }
        
        
        try
        {
            pid = pidProvider.getPid("escidoc:418001", Type.COMPONENT, null);
        }
        catch (PIDProviderException e)
        {
            assertTrue(true);
        }
       

    }
    
    @Test
    public void getPidForItem() throws NamingException
    {
        PIDProviderIf pidProvider = new PIDProvider();
        String pid = null;
        
        try
        {
            pid = pidProvider.getPid("escidoc:206185", Type.ITEM, null);
        }
        catch (PIDProviderException e)
        {
            fail(e.getMessage());
        }

        assertTrue(pid != null && !"".equals(pid));    
        assertTrue(pidProvider.getTotalNumberOfPidsRequested() > 0);
    }
    
    @Test
    @Ignore
    public void getPidForComponent() throws NamingException
    {
        PIDProviderIf pidProvider = new PIDProvider();
        String pid = null;
        
        try
        {
            pid = pidProvider.getPid("escidoc:1578036", Type.COMPONENT, "EXT097.pdf");
        }
        catch (PIDProviderException e)
        {
            fail(e.getMessage());
        }

        assertTrue(pid != null && !"".equals(pid));
        assertTrue(pidProvider.getTotalNumberOfPidsRequested() > 1);
    }
    
    
}
