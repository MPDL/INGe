package de.mpg.escidoc.pubman.installer;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.mpg.escidoc.pubman.installer.panels.JUnitConfigurationPanel;

public class TestStartEscidocProcess
{
    private StartEscidocProcess process = new StartEscidocProcess(new JUnitConfigurationPanel());
    
   
    @Test
    public void test() throws Exception
    {
        process.start();
        
        do
        {
            Thread.currentThread().sleep(5000);
        }
        while (!process.isStartFinished());
        
        assertTrue(process.isStartFinished());
    }
}
