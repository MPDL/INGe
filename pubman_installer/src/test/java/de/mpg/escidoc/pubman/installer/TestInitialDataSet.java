package de.mpg.escidoc.pubman.installer;

import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.escidoc.services.framework.PropertyReader;

public class TestInitialDataSet
{    
    private static InitialDataset ds;
    private String ouDefId = null;
    private String ctxDefId = null;
    
    @BeforeClass
    public static void setup() throws Exception
    {
        ds = new InitialDataset(new URL(PropertyReader.getProperty("escidoc.framework_access.framework.url")), 
                PropertyReader.getProperty("framework.admin.username"), PropertyReader.getProperty("framework.admin.password"));
    }
    
    
    /**
     * If the External Organization already exists, the id is returned, otherwise the ou is created and the id returned.
     * @throws Exception
     */
    @Test
    public void createAndOpenOrganizationalUnit() throws Exception
    {
        String extId = PropertyReader.getProperty("escidoc.pubman.external.organisation.id");
        String ret = ds.createAndOpenOrganizationalUnit("datasetObjects/ou_external.xml");
        
        assertTrue(extId == null ? ret != null : extId.equals(ret));
        
        ouDefId = ds.createAndOpenOrganizationalUnit("datasetObjects/ou_default.xml");
        
        assertTrue(ret != null);
    }
    
    @Test
    public void createContentModel() throws Exception
    {
        String cmId = PropertyReader.getProperty("escidoc.import.task.content-model");
        String ret = ds.createContentModel("datasetObjects/cm_import_task.xml");
        
        assertTrue("cmdId = " + cmId + " ret = " + ret, cmId == null ? ret != null : cmId.equals(ret));
        
        cmId = PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication");
        ret = ds.createContentModel("datasetObjects/cm_publication.xml");
        
        assertTrue("cmdId = " + cmId + " ret = " + ret, cmId == null ? ret != null : cmId.equals(ret));
    }
    
    @Test
    public void createContext() throws Exception
    {
        String ctxId = PropertyReader.getProperty("escidoc.framework_access.context.id.test");
        String ret = ds.createAndOpenContext("datasetObjects/context.xml", ouDefId);
        
        assertTrue("ctxId = " + ctxId + " ret = " + ret, ctxId == null ? ret != null : ctxId.equals(ret));
        
        ctxId = PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication");
        ret = ds.createContentModel("datasetObjects/cm_publication.xml");
        
        assertTrue("ctxId = " + ctxId + " ret = " + ret, ctxId == null ? ret != null : ctxId.equals(ret));
        
        ctxDefId = ret;
    }
    
    @Test
    public void createUser() throws Exception
    {
        String ret = ds.createUser("datasetObjects/user_depositor.xml", "pubman", ouDefId, ctxDefId);
        assertTrue(ret != null);
        ret = ds.createUser("datasetObjects/user_moderator.xml", "pubman", ouDefId, ctxDefId);
        assertTrue(ret != null);
    }
}