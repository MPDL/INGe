package de.mpg.escidoc.pubman.installer;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;


public class TestUpdatePubmanConfigurationProcess
{
    private static final String JBOSS_DEF_PATH = "/jboss/server/default/";
    private static String installPath = "c:/escidoc.pubman";
    
    private static UpdatePubmanConfigurationProcess updateProcess = new UpdatePubmanConfigurationProcess();
    
    @BeforeClass
    public static void setUp() throws Exception
    {
        updateProcess.setInstallPath(installPath);
        
        // create dummy pubman_ear
        FileUtils.writeStringToFile(new File(installPath + JBOSS_DEF_PATH + "pubman_ear.ear"), "xxxxx");
    }

    @Test
    public void testDeployPubmanEar() throws Exception
    {
        updateProcess.deployPubmanEar();
        
        File deployDir = new File(installPath + JBOSS_DEF_PATH + "deploy");
        File pubmanEar = FileUtils.listFiles(deployDir, new String[] {"ear"}, false)
                .iterator().next(); 

        assertTrue(pubmanEar.exists());
    }
}
