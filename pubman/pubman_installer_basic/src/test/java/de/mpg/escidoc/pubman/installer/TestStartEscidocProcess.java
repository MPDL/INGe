package de.mpg.escidoc.pubman.installer;

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.pubman.installer.panels.JUnitConfigurationPanel;

public class TestStartEscidocProcess
{
    private StartEscidocProcess process = null;
    
   
    @Test
    @Ignore
    public void test() throws Exception
    {
        process = new StartEscidocProcess(new JUnitConfigurationPanel());
        process.start();
        
        do
        {
            Thread.currentThread().sleep(5000);
        }
        while (!process.isStartFinished());
        
        assertTrue(process.isStartFinished());
    }
}
