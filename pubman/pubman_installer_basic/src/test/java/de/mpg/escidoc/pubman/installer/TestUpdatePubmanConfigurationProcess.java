package de.mpg.escidoc.pubman.installer;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.escidoc.pubman.installer.panels.IConfigurationCreatorPanel;
import de.mpg.escidoc.pubman.installer.panels.JUnitConfigurationPanel;



public class TestUpdatePubmanConfigurationProcess
{
    private static final String JBOSS_DEF_PATH = "/jboss/server/default/";
    private static String installPath = "c:/escidoc.pubman";
    
    private UpdatePubmanConfigurationProcess updateProcess;
    
    
    @BeforeClass
    public static void setUp() throws Exception
    {       
        // create dummy pubman_ear
        FileUtils.writeStringToFile(new File(installPath + JBOSS_DEF_PATH + "pubman_ear.ear"), "xxxxx");
    }

    @Test
    public void testDeployPubmanEar() throws Exception
    {
        UpdatePubmanConfigurationProcess updateProcess = new UpdatePubmanConfigurationProcess();
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
        IConfigurationCreatorPanel panel = new JUnitConfigurationPanel();
        StartEscidocProcess startProcess = new StartEscidocProcess(panel);
        startProcess.run();
        
        UpdatePubmanConfigurationProcess updateProcess = new UpdatePubmanConfigurationProcess(
                                                panel, new StartEscidocProcess(panel), false);
        updateProcess.start();
        Thread.currentThread().sleep(4*60*1000);
    }
}
