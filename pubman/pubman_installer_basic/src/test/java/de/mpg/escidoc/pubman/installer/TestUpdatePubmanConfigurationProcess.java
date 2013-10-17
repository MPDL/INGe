package de.mpg.escidoc.pubman.installer;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;


public class TestUpdatePubmanConfigurationProcess
{
    private static final String JBOSS_DEF_PATH = "/jboss/server/default/";
    private static Logger logger = Logger.getLogger(TestUpdatePubmanConfigurationProcess.class);
    private static String installPath = "c:/tmp1";
    
    private UpdatePubmanConfigurationProcess updateProcess = new UpdatePubmanConfigurationProcess();

    @Test
    public void testDeployPubmanEar() throws Exception
    {
        updateProcess.setInstallPath(installPath);
        
        updateProcess.deployPubmanEar();
        
        File srcDir = new File(installPath + JBOSS_DEF_PATH + "deploy");
        File pubmanEar = FileUtils.listFiles(srcDir, new String[] {"ear"}, false)
                .iterator().next(); 

        assertTrue(pubmanEar.exists());
    }
    
    @Test
    public void testUpdateIndexConfiguration() throws Exception
    {       
        updateProcess.setInstallPath(installPath);
        updateProcess.updateIndexConfiguration();
        
        assertTrue((new File(installPath + JBOSS_DEF_PATH + "conf/search/config/index/escidoc_all/index.properties").exists()));
        assertTrue(!(new File(installPath + JBOSS_DEF_PATH + "conf/search/config/index/escidoc_all/index.properties.bak").exists()));
        
        String fileContent = FileUtils.readFileToString((new File(installPath + JBOSS_DEF_PATH + "conf/search/config/index/escidoc_all/index.properties")));
        assertTrue(fileContent.contains("mpdlEscidocXmlToLucene"));    
        
        // do it twice
        updateProcess.updateIndexConfiguration();
        
        assertTrue((new File(installPath + JBOSS_DEF_PATH + "conf/search/config/index/escidoc_all/index.properties").exists()));
        assertTrue(!(new File(installPath + JBOSS_DEF_PATH + "conf/search/config/index/escidoc_all/index.properties.bak").exists()));
        
        fileContent = FileUtils.readFileToString((new File(installPath + JBOSS_DEF_PATH + "conf/search/config/index/escidoc_all/index.properties")));
        assertTrue(fileContent.contains("mpdlEscidocXmlToLucene"));        
    }
}
