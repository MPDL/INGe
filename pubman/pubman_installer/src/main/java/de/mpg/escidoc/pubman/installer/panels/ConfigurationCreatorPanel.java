package de.mpg.escidoc.pubman.installer.panels;

import java.awt.LayoutManager2;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;

import com.izforge.izpack.gui.IzPanelLayout;
import com.izforge.izpack.gui.LabelFactory;
import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import com.izforge.izpack.installer.IzPanel;

import de.mpg.escidoc.pubman.installer.ConeDataset;
import de.mpg.escidoc.pubman.installer.Configuration;
import de.mpg.escidoc.pubman.installer.InitialDataset;
import de.mpg.escidoc.pubman.installer.Installer;

public class ConfigurationCreatorPanel extends IzPanel
{
   private static final long serialVersionUID = 3257848774955905587L;
   
   private Configuration configuration = null;
   
   private boolean isValid = true;
   
   /**
    * The constructor.
    *
    * @param parent The parent.
    * @param idata  The installation data.
 * @throws IOException 
    */
   public ConfigurationCreatorPanel(InstallerFrame parent, InstallData idata) throws IOException
   {
       this(parent, idata, new IzPanelLayout());
   }

   /**
    * Creates a new HelloPanel object with the given layout manager. Valid layout manager are the
    * IzPanelLayout and the GridBagLayout. New panels should be use the IzPanelLaout. If lm is
    * null, no layout manager will be created or initialized.
    *
    * @param parent The parent IzPack installer frame.
    * @param idata  The installer internal data.
    * @param layout layout manager to be used with this IzPanel
 * @throws IOException 
    */

   public ConfigurationCreatorPanel(InstallerFrame parent, InstallData idata, LayoutManager2 layout) throws IOException
   {
       super(parent, idata, layout);
       // We create and put the labels
       String str;
       str = "Writting configuration...";
       JLabel welcomeLabel = LabelFactory.create(str, parent.icons.getImageIcon("host"), LEADING);
      
       add(welcomeLabel, NEXT_LINE);
      
       getLayoutHelper().completeLayout();
       
       configuration = new Configuration("configuration/pubman.properties");
   }

   /**
    * Indicates wether the panel has been validated or not.
    *
    * @return Always true.
    */
   public boolean isValidated()
   {
       return isValid;
   }
   
   private void storeConfiguration() throws IOException {
       Map<String, String> userConfigValues = new HashMap<String, String>();
       userConfigValues.put(Configuration.KEY_CORESERVICE_URL, idata.getVariable("CoreserviceUrl"));
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
       userConfigValues.put(Configuration.KEY_PM_STYLESHEET_STANDARD_APPLY, idata.getVariable("StyleSheetStandardApply"));
       userConfigValues.put(Configuration.KEY_PM_STYLESHEET_STANDARD_URL, idata.getVariable("StyleSheetStandardURL"));
       userConfigValues.put(Configuration.KEY_PM_STYLESHEET_STANDARD_TYPE, idata.getVariable("StyleSheetStandardType"));
       userConfigValues.put(Configuration.KEY_PM_STYLESHEET_CONTRAST_APPLY, idata.getVariable("StyleSheetContrastApply"));
       userConfigValues.put(Configuration.KEY_PM_STYLESHEET_CONTRAST_URL, idata.getVariable("StyleSheetContrastURL"));
       userConfigValues.put(Configuration.KEY_PM_STYLESHEET_CONTRAST_TYPE, idata.getVariable("StyleSheetContrastType"));
       userConfigValues.put(Configuration.KEY_PM_STYLESHEET_CLASSIC_APPLY, idata.getVariable("StyleSheetClassicApply"));
       userConfigValues.put(Configuration.KEY_PM_STYLESHEET_CLASSIC_URL, idata.getVariable("StyleSheetClassicURL"));
       userConfigValues.put(Configuration.KEY_PM_STYLESHEET_CLASSIC_TYPE, idata.getVariable("StyleSheetClassicType"));
       userConfigValues.put(Configuration.KEY_PM_FAVICON_URL, idata.getVariable("FavIconURL"));
       userConfigValues.put(Configuration.KEY_PM_FAVICON_APPLY, idata.getVariable("FavIconApply"));
       configuration.setProperties(userConfigValues);
       configuration.store(idata.getInstallPath() + "/jboss-4.2.2.GA/server/default/conf/pubman.properties");
   }
   
   private void checkContentModel() throws Exception {
       InitialDataset dataset = new InitialDataset( new URL(idata.getVariable("CoreserviceUrl") ), 
               idata.getVariable("CoreserviceAdminUser"), idata.getVariable("CoreserviceAdminPassword") );
       dataset.retrieveContentModel(Installer.CHECK_CONTENT_MODEL);
   }
   
   /**
    * evaluates if some data has to be inserted into cone database
    * @return boolean true if data has to be inserted into cone database
    */
   private boolean haveToInsertConeData()
   {
	   boolean insertConeData = false;
	   
	   if(idata.getVariable("ConeCreateJournals").equals("true") 
			   || idata.getVariable("ConeCreateLanguages").equals("true") 
			   || idata.getVariable("ConeCreateDDC").equals("true") 
			   || idata.getVariable("ConeCreateMimetypes").equals("true") 
			   || idata.getVariable("ConeCreateEscidocMimeTypes").equals("true"))
	   {
		   insertConeData = true;
	   }
	   return insertConeData;
   }
   
   private void insertConeData() throws Exception
   {
	   if(this.haveToInsertConeData() == true)
	   {
		   ConeDataset coneDataset = new ConeDataset(idata.getVariable("ConeHost"), idata.getVariable("ConePort"), idata.getVariable("ConeDatabase"), idata.getVariable("ConeUser"), idata.getVariable("ConePassword"));
		   
		   // check if cone database already exists on the Postgres server or not. if not create it.
		   if(coneDataset.isConeDBAvailable(ConeDataset.CONE_CHECK_DATABASES) == false)
		   {
			   coneDataset.runConeScript(ConeDataset.CONE_CREATE_DATABASE);
		   }
			   
		   // first create tables
		   coneDataset.runConeScript(ConeDataset.CONE_CREATE_SCRIPT);
		   
		   // then insert data if needed
		   if(idata.getVariable("ConeCreateJournals").equals("true"))
		   {
			   coneDataset.runConeScript(ConeDataset.CONE_INSERT_JOURNALS);
		   }
		   if(idata.getVariable("ConeCreateLanguages").equals("true"))
		   {
			   coneDataset.runConeScript(ConeDataset.CONE_INSERT_LANGUAGES);
		   }
		   if(idata.getVariable("ConeCreateDDC").equals("true"))
		   {
			   coneDataset.runConeScript(ConeDataset.CONE_INSERT_DDC);
		   }
		   if(idata.getVariable("ConeCreateMimetypes").equals("true"))
		   {
			   coneDataset.runConeScript(ConeDataset.CONE_INSERT_MIMETYPES);
		   }
		   if(idata.getVariable("ConeCreateEscidocMimeTypes").equals("true"))
		   {
			   coneDataset.runConeScript(ConeDataset.CONE_INSERT_ESCIDOC_MIMETYPES);
		   }
		   
		   // at least index the tables
		   coneDataset.runConeScript(ConeDataset.CONE_INDEX_SCRIPT);
	   }
   }
   
   public void panelActivate() {
       boolean success = true;
       try {
           JLabel label = LabelFactory.create("Inserting CoNE data...", parent.icons.getImageIcon("host"), LEADING);
           add(label, NEXT_LINE);
           getLayoutHelper().completeLayout();
           insertConeData();
           JLabel label2 = LabelFactory.create("Good. CoNE data inserted.", parent.icons.getImageIcon("host"), LEADING);
           add(label2, NEXT_LINE);
           getLayoutHelper().completeLayout();
       } 
       catch( Exception e ) {
           JLabel welcomeLabel = LabelFactory.create("Error. CoNE data could not be inserted. Please see the log files for further information."
                   , parent.icons.getImageIcon("host"), LEADING);
           add(welcomeLabel, NEXT_LINE);
           JLabel welcomeLabel2 = LabelFactory.create("Please insert CoNE data manually."
                   , parent.icons.getImageIcon("host"), LEADING);
           add(welcomeLabel2, NEXT_LINE);
           getLayoutHelper().completeLayout();
           success = false;
       }
       
       try {
           JLabel label = LabelFactory.create("Checking content model...", parent.icons.getImageIcon("host"), LEADING);
           add(label, NEXT_LINE);
           getLayoutHelper().completeLayout();
           checkContentModel();
           JLabel label2 = LabelFactory.create("Good. Content Model checked.", parent.icons.getImageIcon("host"), LEADING);
           add(label2, NEXT_LINE);
           getLayoutHelper().completeLayout();
       } 
       catch( Exception e ) {
           JLabel welcomeLabel = LabelFactory.create("Error. Content model("+Installer.CHECK_CONTENT_MODEL+") not available."
                   , parent.icons.getImageIcon("host"), LEADING);
           add(welcomeLabel, NEXT_LINE);
           JLabel welcomeLabel2 = LabelFactory.create("Please ingest content model as described at first information page."
                   , parent.icons.getImageIcon("host"), LEADING);
           add(welcomeLabel2, NEXT_LINE);
           getLayoutHelper().completeLayout();
           success = false;
       }
      
       try {
           JLabel label = LabelFactory.create("Writting configuration...", parent.icons.getImageIcon("host"), LEADING);
           add(label, NEXT_LINE);
           getLayoutHelper().completeLayout();
           storeConfiguration();
           JLabel label2 = LabelFactory.create("Good. Configuration written.", parent.icons.getImageIcon("host"), LEADING);
           add(label2, NEXT_LINE);
           getLayoutHelper().completeLayout();
       } 
       catch( Exception e ) {
           JLabel welcomeLabel = LabelFactory.create("Error. Configuration error", parent.icons.getImageIcon("host"), LEADING);
           add(welcomeLabel, NEXT_LINE);
           getLayoutHelper().completeLayout();
           success = false;
       }
   }
}
