package de.mpg.escidoc.pubman.installer.panels;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.escidoc.www.services.aa.RoleHandler;
import de.mpg.escidoc.pubman.installer.util.Utils;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class TestRoleHandlerUpdate
{
    private static final String ESCIDOC_ROLE_MODERATOR = "escidoc:role-moderator";
    private static final String ESCIDOC_ROLE_DEPOSITOR = "escidoc:role-depositor";
    
    private static final String ESCIDOC_ROLE_CONE_OPEN_VOCABULARY_EDITOR_NAME = "CoNE-Open-Vocabulary-Editor";
    private static final String ESCIDOC_ROLE_CONE_CLOSED_VOCABULARY_EDITOR_NAME = "CoNE-Closed-Vocabulary-Editor";
    
    /**
     * Constants for queries.
     */
    protected static final String SEARCH_RETRIEVE = "searchRetrieve";
    protected static final String QUERY = "query";
    protected static final String VERSION = "version";
    protected static final String OPERATION = "operation";
    
    private static RoleHandler roleHandler = null;
    
    private Logger logger = Logger.getLogger(getClass());
    
    @BeforeClass
    @Ignore
    public static void setUp() throws Exception
    {    
        Properties p = PropertyReader.getProperties();
        
        p.setProperty("escidoc.common.framework.url", "http://localhost:8080");
        p.setProperty("escidoc.framework_access.framework.url", "http://localhost:8080");
        p.setProperty("escidoc.framework_access.login.url", "http://localhost:8080");
        p.setProperty("framework.admin.username", "roland");
        p.setProperty("framework.admin.password", "dnalor");
        
        roleHandler = ServiceLocator.getRoleHandler(loginSystemAdministrator());
    }

    @Test
    @Ignore
    public void retrieve()
    {
        try
        {
            String xml = roleHandler.retrieve("escidoc:role-moderator");
            assertTrue(xml != null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @Test
    @Ignore
    public void update()
    {
        logger.info("******************************************* Starting update");
        try
        {
            // update role-moderator, role-depositor and role-privileged-viewer according to PubMan requests
            String out = doUpdate(ESCIDOC_ROLE_MODERATOR, "datasetObjects/role_moderator.xml");  
            assertTrue(out.contains("Moderator-policy-retrieveUserAccount"));
                        
            out = doUpdate(ESCIDOC_ROLE_DEPOSITOR, "datasetObjects/role_depositor.xml");
 
            // cone roles, policies...  check first if they already exists         
            out = doCreateOrUpdate(ESCIDOC_ROLE_CONE_OPEN_VOCABULARY_EDITOR_NAME, "datasetObjects/role_cone_open_vocabulary_editor.xml");           
            out = doCreateOrUpdate(ESCIDOC_ROLE_CONE_CLOSED_VOCABULARY_EDITOR_NAME, "datasetObjects/role_cone_closed_vocabulary_editor.xml");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Caught exception");
        }
    }
    
    private String doUpdate(String ruleId, String templateFileName) throws Exception
    {    
        logger.info("******************************************* Starting doUpdate for " + ruleId);
        String lastModDate = "";
        String out = null;
        
        String oldPolicy = roleHandler.retrieve(ruleId);
        lastModDate = Utils.getValueFromXml("last-modification-date=\"", oldPolicy);
        logger.info("policy <" + ruleId + "> has to be updated");
        logger.info("oldDate: " + lastModDate);
        
        String newPolicy = Utils.getResourceAsXml(templateFileName);
        newPolicy = newPolicy.replaceAll("template_last_modification_date", lastModDate);
        
        out = roleHandler.update(ruleId, newPolicy);
        
        String newDate = Utils.getValueFromXml("last-modification-date=\"", out);
        logger.info("newDate: " + newDate);
        logger.info("******************************************* Ended doUpdate for " + ruleId);
        return out;
    }
    
    private String doCreateOrUpdate(String roleName, String templateFileName) throws Exception
    {    
        logger.info("******************************************* Starting doCreateOrUpdate for " + roleName);
        
        boolean update = false;
        String out = null;
        HashMap<java.lang.String, String[]> map = new HashMap<java.lang.String, String[]>();
        
        // filter for "properties/name"=roleName
        map.put(OPERATION, new String[]{SEARCH_RETRIEVE});
        map.put(VERSION, new String[]{"1.1"});
        map.put(QUERY, new String[]{"\"/properties/name\"=" + roleName});
        
        
        String policies = roleHandler.retrieveRoles(map);
        // roleName occurs as value of a <prop:name> element in SearchRequestResponse -> already exists
        if ((Utils.getValueFromXml("<prop:name>", '<', policies)).equalsIgnoreCase(roleName))
        {
            update = true; 
        }
        
        if (update)
        {
            logger.info("policy <" + roleName + "> has to be updated");
            String roleId = Utils.getValueFromXml("objid=\"", policies);
            return doUpdate(roleId, templateFileName);
        }
        else
        {
            logger.info("policy <" + roleName + "> has to be created");
            String newPolicy = Utils.getResourceAsXml(templateFileName);
            newPolicy = newPolicy.replaceAll("template_last_modification_date", "");
            newPolicy = newPolicy.replaceAll("last-modification-date=\"\"", "");
            out = roleHandler.create(newPolicy);    
        }
        
        String newDate = Utils.getValueFromXml("last-modification-date=\"", out);
        logger.info("newDate: " + newDate);  
        logger.info("******************************************* Ended doCreateOrUpdate for " + roleName);
        return out;
    }

    /**
     * Logs in the user roland who is a system administrator and returns the corresponding user handle.
     * 
     * @return A handle for the logged in user.
     * @throws Exception
     */
    private static String loginSystemAdministrator() throws Exception
    {
        return AdminHelper.loginUser(PropertyReader.getProperty("framework.admin.username"), PropertyReader.getProperty("framework.admin.password"));
    }
}
