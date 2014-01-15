package de.mpg.escidoc.pubman.installer;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;

import de.mpg.escidoc.pubman.installer.panels.JUnitConfigurationPanel;

public class TestStartEscidocProcess
{
    private StartEscidocProcess process = null;
    
    @After
    public void tearDown() throws Exception
    {
        process.stopEscidoc();
        
        Thread.currentThread().sleep(1000*30);
       
    }
    
   
    @Test
    public void test() throws Exception
    {
        process = new StartEscidocProcess(new JUnitConfigurationPanel());
        
        process.startEscidocAndWaitTillFinished();
        
        assertTrue(process.isStartFinished());
    }
}
