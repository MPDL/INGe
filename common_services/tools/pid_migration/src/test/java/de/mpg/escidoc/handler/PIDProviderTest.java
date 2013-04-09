package de.mpg.escidoc.handler;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.apache.commons.httpclient.HttpException;
import org.junit.Test;

import de.mpg.escidoc.main.PIDProviderIf;

public class PIDProviderTest
{
    PIDProviderIf pidProvider = new PIDProviderMock();
    
    @Test
    public void getPid()
    {
        int i = 1;
        String pid = null;
        
        do
        {
        try
        {
            pid = pidProvider.getPid();
        }
        catch (HttpException e)
        {
            fail(e.getMessage());
        }
        catch (IOException e)
        {
            fail(e.getMessage());
        }
    
        assertTrue(pid != null && pid.startsWith("hdl:1234"));
        
        if (i % 10 == 1)
            assertTrue(pid.endsWith("1111-1"));
        else if (i % 10 == 2)
            assertTrue(pid.endsWith("2222-2"));
        } while (i++ < 20);

    }
}
