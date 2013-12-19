package de.mpg.escidoc.pubman.installer;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
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
    
    @AfterClass
    public static void tearDown() throws Exception
    {       
        // move back to deploy dummy pubman_ear
        FileUtils.moveFileToDirectory(new File(installPath + JBOSS_DEF_PATH + "deploy/" + "pubman_ear.ear"), new File(installPath + JBOSS_DEF_PATH + "pubman_ear.ear"), false);
    }

    @Test
    @Ignore
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
        startProcess.start();
        
        UpdatePubmanConfigurationProcess updateProcess = new UpdatePubmanConfigurationProcess(
                                                panel, startProcess, false);
        updateProcess.start();
        Thread.currentThread().sleep(4*60*1000);
    }
}
