/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.installer.panels;

import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import com.izforge.izpack.Pack;
import com.izforge.izpack.gui.IzPanelLayout;
import com.izforge.izpack.gui.LabelFactory;
import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;

import de.escidoc.www.services.aa.RoleHandler;
import de.mpg.escidoc.pubman.installer.ConeDataset;
import de.mpg.escidoc.pubman.installer.ConeInsertProcess;
import de.mpg.escidoc.pubman.installer.Configuration;
import de.mpg.escidoc.pubman.installer.InitialDataset;
import de.mpg.escidoc.pubman.installer.util.LabelPanel;
import de.mpg.escidoc.pubman.installer.util.Utils;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class ConfigurationCreatorPanel extends ConfigurationPanel
{
    
    private static final long serialVersionUID = 3257848774955905587L;
    private static final String JBOSS_CONF_PATH = "/jboss/server/default/conf/";
    
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
    
    private Configuration configPubman = null;
    private Configuration configAuth = null;
    ConeInsertProcess coneInsertProcess;
    private LabelPanel conePanel;
    private boolean success;
    private static Logger logger = Logger.getLogger(ConfigurationCreatorPanel.class);

    /**
     * The constructor.
     * 
     * @param parent The parent.
     * @param idata The installation data.
     * @throws IOException
     */
    public ConfigurationCreatorPanel(InstallerFrame parent, InstallData idata) throws IOException, Exception
    {
        this(parent, idata, new IzPanelLayout());
    }

    /**
     * Creates a new HelloPanel object with the given layout manager. Valid layout manager are the IzPanelLayout and the
     * GridBagLayout. New panels should be use the IzPanelLaout. If lm is null, no layout manager will be created or
     * initialized.
     * 
     * @param parent The parent IzPack installer frame.
     * @param idata The installer internal data.
     * @param layout layout manager to be used with this IzPanel
     * @throws Exception 
     */
    public ConfigurationCreatorPanel(InstallerFrame parent, InstallData idata, LayoutManager2 layout)
            throws ServiceException, URISyntaxException, Exception
    {
        super(parent, idata, layout);
        // We create and put the labels
        JLabel welcomeLabel = LabelFactory.create("Writing configuration...", parent.icons.getImageIcon("host"),
                LEADING);
        add(welcomeLabel, NEXT_LINE);
        getLayoutHelper().completeLayout();
        configPubman = new Configuration("pubman.properties");
        configAuth = new Configuration("auth.properties");
    }
    
    /**
     * Indicates whether the panel has been validated or not.
     * 
     * @return Always true.
     */
    public boolean isValidated()
    {
        return isPanelValid;
    }

    private void storeConfiguration() throws IOException, URISyntaxException, NoSuchAlgorithmException, GeneralSecurityException, Exception
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
        // also store in local pubman properties
        configPubman.store("pubman.properties");
        
        configAuth.setProperties(authConfigValues);
        configAuth.storeProperties("auth.properties", idata.getInstallPath() + JBOSS_CONF_PATH + "auth.properties");
        configAuth.storeProperties("auth.properties", idata.getInstallPath() + JBOSS_CONF_PATH + "cone.properties");
        // also store in local auth properties, cone properties
        configAuth.store("auth.properties");
        
        configAuth.storeXml("conf.xml", idata.getInstallPath() + JBOSS_CONF_PATH + "conf.xml");
        
        // create a private - public key pair
        this.createKeys();
        
        // update framework policies
        this.updatePolicies();
        
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
            throw new IOException("Unexpected error", e);
        }
        finally
        {
            oout.close();
        }
    }
    
    public void updatePolicies() throws Exception
    {
        logger.info("******************************************* Starting updatePolicies");
        
        roleHandler = ServiceLocator.getRoleHandler(loginSystemAdministrator());
        
        try
        {
            String out = null;
            
            // update role-moderator, role-depositor and role-privileged-viewer according to PubMan requests
            out = doUpdate(ESCIDOC_ROLE_MODERATOR, "datasetObjects/role_moderator.xml");                          
            out = doUpdate(ESCIDOC_ROLE_DEPOSITOR, "datasetObjects/role_depositor.xml");
 
            // cone roles, policies...  check first if they already exists         
            out = doCreateOrUpdate(ESCIDOC_ROLE_CONE_OPEN_VOCABULARY_EDITOR_NAME, "datasetObjects/role_cone_open_vocabulary_editor.xml");           
            out = doCreateOrUpdate(ESCIDOC_ROLE_CONE_CLOSED_VOCABULARY_EDITOR_NAME, "datasetObjects/role_cone_closed_vocabulary_editor.xml");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new IOException("Error in updatePolicies ", e);
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
        String userName = PropertyReader.getProperty("framework.admin.username");
        String password = PropertyReader.getProperty("framework.admin.password");
        
        logger.info("username <" + userName + "> password <" + password + ">");
        return AdminHelper.loginUser(PropertyReader.getProperty("framework.admin.username"), PropertyReader.getProperty("framework.admin.password"));
    }
    private void createDataset() throws Exception
    {
        String ouExternalObjectId = null;
        InitialDataset dataset = new InitialDataset(new URL(idata.getVariable("CoreserviceUrl")),
                idata.getVariable("CoreserviceAdminUser"), idata.getVariable("CoreserviceAdminPassword"));
        ouExternalObjectId = dataset.createAndOpenOrganizationalUnit("datasetObjects/ou_external.xml");
        String publicationContentModelId = dataset.createContentModel("datasetObjects/cm_publication.xml");
        configPubman.setProperty(Configuration.KEY_PUBLICATION_CM, publicationContentModelId);
        String importTaskContentModelId = dataset.createContentModel("datasetObjects/cm_import_task.xml");
        configPubman.setProperty(Configuration.KEY_IMPORT_TASK_CM, importTaskContentModelId);
        String ouDefaultObjectId = dataset.createAndOpenOrganizationalUnit("datasetObjects/ou_default.xml");
        configPubman.setProperty(Configuration.KEY_EXTERNAL_OU, ouExternalObjectId);
        idata.setVariable("ExternalOrganisationID", ouExternalObjectId);
        String contextObjectId = dataset.createAndOpenContext("datasetObjects/context.xml", ouDefaultObjectId);
        String userModeratorId = dataset.createUser("datasetObjects/user_moderator.xml",
                idata.getVariable("InitialUserPassword"), ouDefaultObjectId);
        String userDepositorId = dataset.createUser("datasetObjects/user_depositor.xml",
                idata.getVariable("InitialUserPassword"), ouDefaultObjectId);
        dataset.createGrantForUser("datasetObjects/grant_moderator.xml", userModeratorId, contextObjectId);
        dataset.createGrantForUser("datasetObjects/grant_depositor.xml", userDepositorId, contextObjectId);
    }

    /**
     * evaluates if some data has to be inserted into cone database
     * 
     * @return boolean true if data has to be inserted into cone database
     */
    private boolean haveToInsertConeData()
    {
        boolean insertConeData = false;
        if (idata.getVariable("ConeCreateJournals").equals("true")
                || idata.getVariable("ConeCreateLanguages").equals("true")
                || idata.getVariable("ConeCreateDDC").equals("true")
                || idata.getVariable("ConeCreateMimetypes").equals("true")
                || idata.getVariable("ConeCreateEscidocMimeTypes").equals("true")
                || idata.getVariable("ConeCreateCcLicenses").equals("true"))
        {
            insertConeData = true;
        }
        return insertConeData;
    }

    /**
     * evaluates if some data has to be inserted into cone database
     * 
     * @return boolean true if data has to be inserted into cone database
     */
    private boolean haveToInstallInitialDataset()
    {
        for (Pack pack : idata.selectedPacks)
        {
            if (pack.name.equals("Initial Dataset"))
            {
                return true;
            }
        }
        return false;
    }

    private void insertConeData() throws Exception
    {
        if (this.haveToInsertConeData() == true)
        {
            ConeDataset coneDataset = new ConeDataset(idata);
            this.coneInsertProcess = new ConeInsertProcess(coneDataset, idata, this);
            getLayoutHelper().completeLayout();
            this.coneInsertProcess.start();
            return;
        }
    }

    public void panelActivate()
    {
        parent.lockPrevButton();
        parent.lockNextButton();
        success = true;
        this.textArea = new JTextArea();
        // JScrollPane pane = new JScrollPane(textArea);
        // this.add(pane);
        // textArea.append("Performing configuration and data ingestion. This may take a while...\n");
        // textArea.append("The 'Next' button will be activated after all data has been inserted.\n");
        // textArea.append("\n\n");
        if (haveToInstallInitialDataset())
        {
            LabelPanel datasetPanel = new LabelPanel("Creating initial dataset", false);
            add(datasetPanel, NEXT_LINE);
            try
            {
                storeConfiguration();
                createKeys();
                createDataset();
                datasetPanel.setEndLabel("Initial dataset created successfully!", LabelPanel.ICON_SUCCESS);
            }
            catch (Exception e)
            {
                datasetPanel.setEndLabel("Error while creating initial dataset!", LabelPanel.ICON_ERROR);
                String hint = "Please rerun installation and ensure that the eSciDoc coreservice is running and the correct coreservice credentials are provided in the installer.";
                datasetPanel.addToTextArea(e.toString() + ": " + e.getMessage() + "\n" + hint);
                datasetPanel.setMinimumSize(new Dimension(700, 100));
                logger.error("Error while creating initial dataset!", e);
                success = false;
            }
            revalidate();
        }
        if (haveToInsertConeData())
        {
            conePanel = new LabelPanel("Writing CoNE data to database. This process may take several minutes.", true);
            add(conePanel, NEXT_LINE);
            try
            {
                insertConeData();
            }
            catch (Exception e)
            {
                logger.error("Error while writing CoNE data to database!", e);
            }
            revalidate();
        }
        LabelPanel propertiesModelPanel = new LabelPanel("Writing configuration (pubman.properties)", false);
        add(propertiesModelPanel, NEXT_LINE);
        try
        {
            storeConfiguration();
            propertiesModelPanel.setEndLabel("Configuration written successfully!", LabelPanel.ICON_SUCCESS);
        }
        catch (Exception e)
        {
            propertiesModelPanel.setEndLabel("Error. Property file could not be written!", LabelPanel.ICON_ERROR);
            propertiesModelPanel.addToTextArea(e.toString() + ": " + e.getMessage());
            logger.error("Property file could not be written!", e);
            success = false;
        }
        revalidate();
        setPanelValid(success);
    }

    public void coneDataInsertedSuccessfully()
    {
        conePanel.showProgressBar(false);
        conePanel.setEndLabel("CoNE data written and processed!", LabelPanel.ICON_SUCCESS);
        // always set to true for now
        setPanelValid(success);
        if (success)
        {
            parent.unlockNextButton();
        }
        else
        {
            parent.unlockPrevButton();
        }
        revalidate();
    }

    public void coneInsertionError(Exception e)
    {
        conePanel.showProgressBar(false);
        conePanel.setEndLabel("Error. CoNE data could not be inserted.", LabelPanel.ICON_ERROR);
        conePanel.addToTextArea(e.toString() + ": " + e.getMessage());
        success = false;
        // always set to true for now
        setPanelValid(success);
        if (success)
        {
            parent.unlockNextButton();
        }
        else
        {
            parent.unlockPrevButton();
        }
        revalidate();
    }
}
