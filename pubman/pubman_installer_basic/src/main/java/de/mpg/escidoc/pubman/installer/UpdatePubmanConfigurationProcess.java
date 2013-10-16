package de.mpg.escidoc.pubman.installer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;

import com.izforge.izpack.installer.InstallData;

import de.escidoc.www.services.aa.RoleHandler;
import de.mpg.escidoc.pubman.installer.panels.ConfigurationCreatorPanel;
import de.mpg.escidoc.pubman.installer.util.Utils;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class UpdatePubmanConfigurationProcess extends Thread
{
    private ConfigurationCreatorPanel panel;

    private Configuration configPubman = null;
    private Configuration configAuth = null;
    private InstallData idata;
    private boolean createDataset;
    private Thread startEscidocThread;
    
    private static final String JBOSS_CONF_PATH = "/jboss/server/default/conf/";
    
    private static final String ESCIDOC_ROLE_MODERATOR = "escidoc:role-moderator";
    private static final String ESCIDOC_ROLE_DEPOSITOR = "escidoc:role-depositor";
    
    private static final String ESCIDOC_ROLE_CONE_OPEN_VOCABULARY_EDITOR_NAME = "CoNE-Open-Vocabulary-Editor";
    private static final String ESCIDOC_ROLE_CONE_CLOSED_VOCABULARY_EDITOR_NAME = "CoNE-Closed-Vocabulary-Editor";
    
    private static final String INDEX_PROPERTIES = "index.properties";
    
    private static RoleHandler roleHandler = null;
    
    private static Logger logger = Logger.getLogger(UpdatePubmanConfigurationProcess.class);
    
    public UpdatePubmanConfigurationProcess()
    {      
    }
    
    public UpdatePubmanConfigurationProcess(ConfigurationCreatorPanel panel, Thread startEscidocThread, boolean b) throws IOException
    {      
        this.panel = panel;
        this.configPubman = new Configuration("pubman.properties");
        this.configAuth = new Configuration("auth.properties");
        this.createDataset = b;
        this.idata = panel.getInstallData();
        
        this.startEscidocThread = startEscidocThread;
        
        this.setName("UpdatePubmanConfigurationProcess");
    }

    public void updatePubmanConfiguration() throws Exception
    {
        this.panel.getTextArea().append("Starting update...\n");
        logger.info("Updating PubMan configuration continuing..");
                 
        if (this.createDataset)
        {     
            storeConfiguration();
            createDataset();                
        }  
        storeConfiguration();      
    }
       
    public void run()
    {
        try
        {
            startEscidocThread.join(3*60*1000);
        }
        catch (InterruptedException  e1)
        {
            logger.error("Timeout when waiting for eSciDoc Framework start....");
            panel.processFinishedWithError("Timeout when waiting for eSciDoc Framework start", e1, this.getName());
            return;
        }
        
        try
        {
            updatePubmanConfiguration();
            String message = (createDataset ? "PubMan configuration updated successfully and initial dataset created! " : "PubMan configuration updated successfully!");
            panel.processFinishedSuccessfully(message, this.getName());
        }            
        catch(Exception e)
        {
            panel.processFinishedWithError("Error or timeout when updateing PubMan configuration!", e, this.getName());
            logger.error("Error during updating PubMan configuration", e);
        }
    }
    
    private void storeConfiguration() throws Exception
    {
        Map<String, String> userConfigValues = new HashMap<String, String>();
        Map<String, String> authConfigValues = new HashMap<String, String>();
        
        userConfigValues.put(Configuration.KEY_CORESERVICE_URL, idata.getVariable("CoreserviceUrl"));
        userConfigValues.put(Configuration.KEY_CORESERVICE_LOGIN_URL, idata.getVariable("CoreserviceUrl"));
        userConfigValues.put(Configuration.KEY_CORESERVICE_ADMINUSERNAME, idata.getVariable("CoreserviceAdminUser"));
        userConfigValues.put(Configuration.KEY_CORESERVICE_ADMINPW, idata.getVariable("CoreserviceAdminPassword"));
        userConfigValues.put(Configuration.KEY_INSTANCEURL, idata.getVariable("InstanceUrl"));
        userConfigValues.put(Configuration.KEY_MAILSERVER, idata.getVariable("MailHost"));
        userConfigValues.put(Configuration.KEY_MAIL_SENDER, idata.getVariable("MailSenderAdress"));
        userConfigValues.put(Configuration.KEY_MAIL_USE_AUTHENTICATION, idata.getVariable("MailUseAuthentication"));
        userConfigValues.put(Configuration.KEY_MAILUSER, idata.getVariable("MailUsername"));
        userConfigValues.put(Configuration.KEY_MAILUSERPW, idata.getVariable("MailPassword"));
        userConfigValues.put(Configuration.KEY_CONE_SERVER, idata.getVariable("ConeHost"));
        userConfigValues.put(Configuration.KEY_CONE_PORT, idata.getVariable("ConePort"));
        userConfigValues.put(Configuration.KEY_CONE_DATABASE, idata.getVariable("ConeDatabase"));
        userConfigValues.put(Configuration.KEY_CONE_USER, idata.getVariable("ConeUser"));
        userConfigValues.put(Configuration.KEY_CONE_PW, idata.getVariable("ConePassword"));
        userConfigValues.put(Configuration.KEY_EXTERNAL_OU, idata.getVariable("ExternalOrganisationID"));
        // stylesheets PubMan 
        userConfigValues.put(Configuration.KEY_PM_STYLESHEET_STANDARD_APPLY,
                idata.getVariable("StyleSheetStandardApply"));
        userConfigValues.put(Configuration.KEY_PM_STYLESHEET_STANDARD_URL, idata.getVariable("StyleSheetStandardURL"));
        userConfigValues
                .put(Configuration.KEY_PM_STYLESHEET_STANDARD_TYPE, idata.getVariable("StyleSheetStandardType"));
        userConfigValues.put(Configuration.KEY_PM_STYLESHEET_CONTRAST_APPLY,
                idata.getVariable("StyleSheetContrastApply"));
        userConfigValues.put(Configuration.KEY_PM_STYLESHEET_CONTRAST_URL, idata.getVariable("StyleSheetContrastURL"));
        userConfigValues
                .put(Configuration.KEY_PM_STYLESHEET_CONTRAST_TYPE, idata.getVariable("StyleSheetContrastType"));
        userConfigValues
                .put(Configuration.KEY_PM_STYLESHEET_CLASSIC_APPLY, idata.getVariable("StyleSheetClassicApply"));
        userConfigValues.put(Configuration.KEY_PM_STYLESHEET_CLASSIC_URL, idata.getVariable("StyleSheetClassicURL"));
        userConfigValues.put(Configuration.KEY_PM_STYLESHEET_CLASSIC_TYPE, idata.getVariable("StyleSheetClassicType"));
        userConfigValues
            .put(Configuration.KEY_PM_STYLESHEET_SPECIAL_APPLY, idata.getVariable("StyleSheetSpecialApply"));
        userConfigValues.put(Configuration.KEY_PM_STYLESHEET_SPECIAL_URL, idata.getVariable("StyleSheetSpecialURL"));
        userConfigValues.put(Configuration.KEY_PM_STYLESHEET_SPECIAL_TYPE, idata.getVariable("StyleSheetSpecialType"));
        // stylesheets common
        userConfigValues.put(Configuration.KEY_CM_STYLESHEET_STANDARD_APPLY,
                idata.getVariable("StyleSheetStandardApply"));
        userConfigValues.put(Configuration.KEY_CM_STYLESHEET_STANDARD_URL, idata.getVariable("StyleSheetStandardURL"));
        userConfigValues
                .put(Configuration.KEY_CM_STYLESHEET_STANDARD_TYPE, idata.getVariable("StyleSheetStandardType"));
        userConfigValues.put(Configuration.KEY_CM_STYLESHEET_CONTRAST_APPLY,
                idata.getVariable("StyleSheetContrastApply"));
        userConfigValues.put(Configuration.KEY_CM_STYLESHEET_CONTRAST_URL, idata.getVariable("StyleSheetContrastURL"));
        userConfigValues
                .put(Configuration.KEY_CM_STYLESHEET_CONTRAST_TYPE, idata.getVariable("StyleSheetContrastType"));
        userConfigValues
                .put(Configuration.KEY_CM_STYLESHEET_CLASSIC_APPLY, idata.getVariable("StyleSheetClassicApply"));
        userConfigValues.put(Configuration.KEY_CM_STYLESHEET_CLASSIC_URL, idata.getVariable("StyleSheetClassicURL"));
        userConfigValues.put(Configuration.KEY_CM_STYLESHEET_CLASSIC_TYPE, idata.getVariable("StyleSheetClassicType"));
        userConfigValues
            .put(Configuration.KEY_CM_STYLESHEET_SPECIAL_APPLY, idata.getVariable("StyleSheetSpecialApply"));
        userConfigValues.put(Configuration.KEY_CM_STYLESHEET_SPECIAL_URL, idata.getVariable("StyleSheetSpecialURL"));
        userConfigValues.put(Configuration.KEY_CM_STYLESHEET_SPECIAL_TYPE, idata.getVariable("StyleSheetSpecialType"));
        // PumMan Logo URL
        userConfigValues.put(Configuration.KEY_PM_LOGO_URL, idata.getVariable("PubManLogoURL"));
        userConfigValues.put(Configuration.KEY_PM_LOGO_APPLY, idata.getVariable("PubManLogoApply"));
        userConfigValues.put(Configuration.KEY_PM_FAVICON_URL, idata.getVariable("FavIconURL"));
        userConfigValues.put(Configuration.KEY_PM_FAVICON_APPLY, idata.getVariable("FavIconApply"));
        
        userConfigValues.put(Configuration.KEY_UNAPI_DOWNLOAD_SERVER, idata.getVariable("InstanceUrl") + "/dataacquisition/download/unapi");
        userConfigValues.put(Configuration.KEY_UNAPI_VIEW_SERVER, idata.getVariable("InstanceUrl") + "/dataacquisition/view/unapi");
        
        // Panel 6
        userConfigValues.put(Configuration.KEY_PUBLICATION_CM, idata.getVariable("escidoc.framework_access.content-model.id.publication"));
        // Panel 11
        userConfigValues.put(Configuration.KEY_IMPORT_TASK_CM, idata.getVariable("escidoc.import.task.content-model"));
        // Panel 8
        userConfigValues.put(Configuration.KEY_VIEW_ITEM_SIZE, idata.getVariable("escidoc.pubman_presentation.viewFullItem.defaultSize"));
        userConfigValues.put(Configuration.KEY_POLICY_LINK, idata.getVariable("escidoc.pubman.policy.url"));
        userConfigValues.put(Configuration.KEY_CONTACT_LINK, idata.getVariable("escidoc.pubman.contact.url"));
        
        // Login URL
        if (idata.getVariable("escidoc.framework_access.login.url") == null || "".equals(idata.getVariable("escidoc.framework_access.login.url")))
        {
            userConfigValues.put(Configuration.KEY_ACCESS_LOGIN_LINK, idata.getVariable("CoreserviceUrl"));
        }
        else
        {
            userConfigValues.put(Configuration.KEY_ACCESS_LOGIN_LINK, idata.getVariable("escidoc.framework_access.login.url"));
        }
        userConfigValues.put(Configuration.KEY_BLOG_NEWS_LINK, idata.getVariable("escidoc.pubman.blog.news"));
        userConfigValues.put(Configuration.KEY_VOCAB_LINK, idata.getVariable("escidoc.cone.subjectVocab"));
        userConfigValues.put(Configuration.KEY_ACCESS_CONF_GENRES_LINK, idata.getVariable("escidoc.pubman.genres.configuration"));
        // Panel 9
        userConfigValues.put(Configuration.KEY_TASK_INT_LINK, idata.getVariable("escidoc.pubman.sitemap.task.interval"));
        userConfigValues.put(Configuration.KEY_MAX_ITEMS_LINK, idata.getVariable("escidoc.pubman.sitemap.max.items"));
        userConfigValues.put(Configuration.KEY_RETRIEVE_ITEMS_LINK, idata.getVariable("escidoc.pubman.sitemap.retrieve.items"));
        userConfigValues.put(Configuration.KEY_RETRIEVE_TIMEOUT_LINK, idata.getVariable("escidoc.pubman.sitemap.retrieve.timeout"));
        // Panel 10
        userConfigValues.put(Configuration.KEY_SORT_KEYS_LINK, idata.getVariable("escidoc.search.and.export.default.sort.keys"));
        userConfigValues.put(Configuration.KEY_SORT_ORDER_LINK, idata.getVariable("escidoc.search.and.export.default.sort.order"));
        userConfigValues.put(Configuration.KEY_MAX_RECORDS_LINK, idata.getVariable("escidoc.search.and.export.maximum.records"));
        // Panel 12 : Home Page Content and Survey Advertisements
        userConfigValues.put(Configuration.KEY_PB_HOME_CONTENT_URL, idata.getVariable("escidoc.pubman.home.content.url"));
        userConfigValues.put(Configuration.KEY_PB_SURVEY_URL, idata.getVariable("escidoc.pubman.survey.url"));
        userConfigValues.put(Configuration.KEY_PB_SURVEY_TITLE, idata.getVariable("escidoc.pubman.survey.title"));
        userConfigValues.put(Configuration.KEY_PB_SURVEY_TEXT, idata.getVariable("escidoc.pubman.survey.text"));
       
        // Others
        userConfigValues.put(Configuration.KEY_CONE_SERVICE_URL, idata.getVariable("InstanceUrl") + "/cone/");
        userConfigValues.put(Configuration.KEY_SYNDICATION_SERVICE_URL, idata.getVariable("InstanceUrl") + "/syndication/");
        
        //Authentication
        authConfigValues.put(Configuration.KEY_CORESERVICE_URL, idata.getVariable("CoreserviceUrl"));
        authConfigValues.put(Configuration.KEY_CORESERVICE_LOGIN_URL, idata.getVariable("CoreserviceUrl"));
        authConfigValues.put(Configuration.KEY_AUTH_INSTANCE_URL, idata.getVariable("InstanceUrl") + "/auth/");
        authConfigValues.put(Configuration.KEY_AUTH_DEFAULT_TARGET, idata.getVariable("InstanceUrl") + "/auth/clientLogin");
        authConfigValues.put(Configuration.KEY_AUTH_PRIVATE_KEY_FILE, idata.getVariable("AAPrivateKeyFile"));
        authConfigValues.put(Configuration.KEY_AUTH_PUBLIC_KEY_FILE, idata.getVariable("AAPublicKeyFile"));
        authConfigValues.put(Configuration.KEY_AUTH_CONFIG_FILE, idata.getVariable("AAConfigFile"));
        authConfigValues.put(Configuration.KEY_AUTH_IP_TABLE, idata.getVariable("AAIPTable"));
        authConfigValues.put(Configuration.KEY_AUTH_CLIENT_START_CLASS, idata.getVariable("AAClientStartClass"));
        authConfigValues.put(Configuration.KEY_AUTH_CLIENT_FINISH_CLASS, idata.getVariable("AAClientFinishClass"));        
        
        configPubman.setProperties(userConfigValues);
        configPubman.storeProperties("pubman.properties", idata.getInstallPath() + JBOSS_CONF_PATH + "pubman.properties");

        // update framework policies and set the role identifier properties for the two CoNE roles
        this.updatePolicies(authConfigValues);
        
        configAuth.setProperties(authConfigValues);
        configAuth.storeProperties("auth.properties", idata.getInstallPath() + JBOSS_CONF_PATH + "auth.properties");
        configAuth.storeProperties("auth.properties", idata.getInstallPath() + JBOSS_CONF_PATH + "cone.properties");
        
        configAuth.storeXml("conf.xml", idata.getInstallPath() + JBOSS_CONF_PATH + "conf.xml");
        
        // create a private - public key pair
        this.createKeys();
        
        // copy stylesheets for indexing to jboss index configuration
        this.updateIndexConfiguration();
        
        // ... and update PropertyReader
        PropertyReader.loadProperties();
    }

    private void createKeys() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException
    {
        logger.info("Creating keys..");
        
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.genKeyPair();
        KeyFactory fact = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec pub = fact.getKeySpec(kp.getPublic(), RSAPublicKeySpec.class);
        RSAPrivateKeySpec priv = fact.getKeySpec(kp.getPrivate(), RSAPrivateKeySpec.class);
        saveToFile(idata.getInstallPath() + JBOSS_CONF_PATH + "public.key", pub.getModulus(), pub.getPublicExponent());
        saveToFile(idata.getInstallPath() + JBOSS_CONF_PATH + "private.key", priv.getModulus(), priv.getPrivateExponent());
    }

    private static void saveToFile(String fileName, BigInteger mod, BigInteger exp) throws IOException
    {
        logger.info("SaveToFile " + fileName);
        
        ObjectOutputStream oout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
        try
        {
            oout.writeObject(mod);
            oout.writeObject(exp);
        }
        catch (Exception e)
        {
            throw new IOException("Unexpected error when saving key file", e);
        }
        finally
        {
            oout.close();
        }
    }
    
    public void updatePolicies(Map<String, String> config) throws Exception
    {
        logger.info("******************************************* Starting updatePolicies");
        
        roleHandler = ServiceLocator.getRoleHandler(loginSystemAdministrator());
        
        try
        {
            String out = null;
            
            // update role-moderator, role-depositor according to PubMan requests
            out = doUpdate(ESCIDOC_ROLE_MODERATOR, "datasetObjects/role_moderator.xml");                          
            out = doUpdate(ESCIDOC_ROLE_DEPOSITOR, "datasetObjects/role_depositor.xml");
 
            // cone roles, policies...  check first if they already exists         
            out = doCreateOrUpdate(ESCIDOC_ROLE_CONE_OPEN_VOCABULARY_EDITOR_NAME, "datasetObjects/role_cone_open_vocabulary_editor.xml", config);  
            String roleOpenVocId = Utils.getValueFromXml("objid=\"", out);
            logger.info("Setting " + Configuration.KEY_CONE_ROLE_OPEN_VOCABULARY_ID + "<" + roleOpenVocId + ">");
            config.put(Configuration.KEY_CONE_ROLE_OPEN_VOCABULARY_ID, roleOpenVocId);
            
            out = doCreateOrUpdate(ESCIDOC_ROLE_CONE_CLOSED_VOCABULARY_EDITOR_NAME, "datasetObjects/role_cone_closed_vocabulary_editor.xml", config);
            String roleClosedVocId = Utils.getValueFromXml("objid=\"", out);
            logger.info("Setting " + Configuration.KEY_CONE_ROLE_CLOSED_VOCABULARY_ID + "<" + roleClosedVocId + ">");
            config.put(Configuration.KEY_CONE_ROLE_CLOSED_VOCABULARY_ID, roleClosedVocId);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Exception("Error in updatePolicies ", e);
        }
    }
    
    private String doUpdate(String roleId, String templateFileName) throws Exception
    {    
        logger.info("******************************************* Starting doUpdate for " + roleId);
        String lastModDate = "";
        String out = null;
        
        String oldPolicy = roleHandler.retrieve(roleId);
        lastModDate = Utils.getValueFromXml("last-modification-date=\"", oldPolicy);
        logger.info("policy <" + roleId + "> has to be updated");
        logger.info("oldDate: " + lastModDate);
        
        String newPolicy = Utils.getResourceAsXml(templateFileName);
        newPolicy = newPolicy.replaceAll("template_last_modification_date", lastModDate);
        
        out = roleHandler.update(roleId, newPolicy);
        
        String newDate = Utils.getValueFromXml("last-modification-date=\"", out);
        
        logger.info("newDate: " + newDate);
        logger.info("******************************************* Ended doUpdate for " + roleId);
        return out;
    }
    
    private String doCreateOrUpdate(String roleName, String templateFileName, Map<String, String> config) throws Exception
    {    
        logger.info("******************************************* Starting doCreateOrUpdate for " + roleName);
        
        boolean update = false;
        String out = null;
        String roleId = null;
        HashMap<java.lang.String, String[]> map = new HashMap<java.lang.String, String[]>();
        
        // filter for "properties/name"=roleName
        map.put(Utils.OPERATION, new String[]{Utils.SEARCH_RETRIEVE});
        map.put(Utils.VERSION, new String[]{"1.1"});
        map.put(Utils.QUERY, new String[]{"\"/properties/name\"=" + roleName});
        
        
        String policies = roleHandler.retrieveRoles(map);
        // roleName occurs as value of a <prop:name> element in SearchRequestResponse -> already exists
        if ((Utils.getValueFromXml("<prop:name>", '<', policies)).equalsIgnoreCase(roleName))
        {
            update = true; 
        }
        
        if (update)
        {
            logger.info("policy <" + roleName + "> has to be updated");
            roleId = Utils.getValueFromXml("objid=\"", policies);
            return doUpdate(roleId, templateFileName);
        }
        else
        {
            logger.info("policy <" + roleName + "> has to be created");
            String newPolicy = Utils.getResourceAsXml(templateFileName);
            newPolicy = newPolicy.replaceAll("template_last_modification_date", "");
            newPolicy = newPolicy.replaceAll("last-modification-date=\"\"", "");
            out = roleHandler.create(newPolicy);  
            roleId = Utils.getValueFromXml("objid=\"", policies);
        }
        
        String newDate = Utils.getValueFromXml("last-modification-date=\"", out);
        logger.info("newDate: " + newDate);  
        logger.info("******************************************* Ended doCreateOrUpdate for " + roleName);
        return out;
    }
    
    private void updateIndexConfiguration() throws Exception
    {
        StringBuffer out = new StringBuffer(4096);
        File indexProperties = new File(new StringBuffer(2048).append(JBOSS_CONF_PATH).append("search/config/index/escidoc_all").toString(), 
                                                        INDEX_PROPERTIES);
        
        LineIterator lit = new LineIterator(new FileReader(indexProperties));
        
        while(lit.hasNext())
        {
            String line = lit.nextLine();
            
            if (line.endsWith("escidocXmlToLucene"))
            {
                line = line.replaceAll("escidocXmlToLucene", "mpdlEscidocXmlToLucene");
            }
            out.append(line);
        }
        
        FileUtils.writeStringToFile(indexProperties, out.toString());       
    }
    
    private void createDataset() throws Exception
    {
        InitialDataset dataset = new InitialDataset(new URL(idata.getVariable("CoreserviceUrl")),
                idata.getVariable("CoreserviceAdminUser"), idata.getVariable("CoreserviceAdminPassword"));
               
        String publicationContentModelId = dataset.createContentModel("datasetObjects/cm_publication.xml");
        configPubman.setProperty(Configuration.KEY_PUBLICATION_CM, publicationContentModelId);
        String importTaskContentModelId = dataset.createContentModel("datasetObjects/cm_import_task.xml");
        configPubman.setProperty(Configuration.KEY_IMPORT_TASK_CM, importTaskContentModelId);
        
        String ouExternalObjectId = dataset.createAndOpenOrganizationalUnit("datasetObjects/ou_external.xml");
        String ouDefaultObjectId = dataset.createAndOpenOrganizationalUnit("datasetObjects/ou_default.xml");
        configPubman.setProperty(Configuration.KEY_EXTERNAL_OU, ouExternalObjectId);
        idata.setVariable("ExternalOrganisationID", ouExternalObjectId);
        configPubman.setProperty(Configuration.KEY_DEFAULT_OU, ouDefaultObjectId);
        
        String contextObjectId = dataset.createAndOpenContext("datasetObjects/context.xml", ouDefaultObjectId);
        
        String userModeratorId = dataset.createUser("datasetObjects/user_moderator.xml",
                idata.getVariable("InitialUserPassword"), ouDefaultObjectId, contextObjectId);
        String userDepositorId = dataset.createUser("datasetObjects/user_depositor.xml",
                idata.getVariable("InitialUserPassword"), ouDefaultObjectId, contextObjectId);
    }
    

    /**
     * Utility method. Logs in the system administrator and returns the corresponding user handle.
     * 
     * @return A handle for the logged in user.
     * @throws Exception
     */
    private static String loginSystemAdministrator() throws Exception
    {
        String userName = PropertyReader.getProperty("framework.admin.username");
        String password = PropertyReader.getProperty("framework.admin.password");
        
        logger.info("username <" + userName + "> password <" + password + ">");
        return AdminHelper.loginUser(PropertyReader.getProperty("framework.admin.username"), PropertyReader.getProperty("framework.admin.password"));
    }
}
