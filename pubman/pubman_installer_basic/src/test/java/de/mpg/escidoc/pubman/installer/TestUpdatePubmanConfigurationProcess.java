package de.mpg.escidoc.pubman.installer;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.pubman.installer.panels.IConfigurationCreatorPanel;
import de.mpg.escidoc.pubman.installer.panels.JUnitConfigurationPanel;



public class TestUpdatePubmanConfigurationProcess
{
    private static final String JBOSS_DEF_PATH = "/jboss/server/default/";
    private static String installPath = "c:/escidoc.pubman";
    
    private static StartEscidocProcess escidocProcess;
    private UpdatePubmanConfigurationProcess updateProcess;
    private static IConfigurationCreatorPanel panel = new JUnitConfigurationPanel();
    
    
    @BeforeClass
    public static void setUp() throws Exception
    {   
        escidocProcess = new StartEscidocProcess(panel);
        escidocProcess.start();
    }
    
    @Before
    public void setUpBeforeMethod() throws Exception
    {       
        // create dummy pubman_ear
        FileUtils.writeStringToFile(new File(installPath + JBOSS_DEF_PATH + "pubman_ear.ear"), "xxxxx");
    }
    
    @AfterClass
    public static void tearDown() throws Exception
    {       
        escidocProcess.stopEscidoc();       
        
        Thread.currentThread().sleep(1000*30);
    }

    @Test
    public void testDeployPubmanEar() throws Exception
    {
        updateProcess = new UpdatePubmanConfigurationProcess();
        updateProcess.setInstallPath(installPath);
        updateProcess.deployPubmanEar();
        
        File deployDir = new File(installPath + JBOSS_DEF_PATH + "deploy");
        File pubmanEar = FileUtils.listFiles(deployDir, new String[] {"ear"}, false)
                .iterator().next(); 

        assertTrue(pubmanEar.exists());
    }
    
    @Test
    public void testUpdatePubmanConfiguration() throws Exception
    {       
        updateProcess = new UpdatePubmanConfigurationProcess(
                                                panel, escidocProcess, true);
        updateProcess.start();
        Thread.currentThread().sleep(3*60*1000);
    }
}
