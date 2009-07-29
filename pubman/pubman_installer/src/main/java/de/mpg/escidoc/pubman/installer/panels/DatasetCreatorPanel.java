package de.mpg.escidoc.pubman.installer.panels;

import java.awt.LayoutManager2;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.xml.rpc.ServiceException;

import com.izforge.izpack.Info;
import com.izforge.izpack.gui.IzPanelLayout;
import com.izforge.izpack.gui.LabelFactory;
import com.izforge.izpack.gui.LayoutConstants;
import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import com.izforge.izpack.installer.IzPanel;

import de.mpg.escidoc.pubman.installer.Configuration;
import de.mpg.escidoc.pubman.installer.InitialDataset;
import de.mpg.escidoc.pubman.installer.Installer;

public class DatasetCreatorPanel extends IzPanel
{
   private static final long serialVersionUID = 3257848774955905587L;
   
   private Configuration configuration = null;
   
   /**
    * The constructor.
    *
    * @param parent The parent.
    * @param idata  The installation data.
 * @throws IOException 
    */
   public DatasetCreatorPanel(InstallerFrame parent, InstallData idata) throws IOException
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

   public DatasetCreatorPanel(InstallerFrame parent, InstallData idata, LayoutManager2 layout) throws IOException
   {
       super(parent, idata, layout);
       // We create and put the labels
       String str;
       str = "Creating initial data...";
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
       return true;
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
       userConfigValues.put(Configuration.KEY_CONE_USER, idata.getVariable("ConeUser"));
       userConfigValues.put(Configuration.KEY_CONE_PW, idata.getVariable("ConePassword"));
       configuration.setProperties(userConfigValues);
       configuration.store(idata.getInstallPath() + "/jboss-4.2.2.GA/server/default/conf/pubman.properties");
   }
   
   private void checkContentModel() throws Exception {
       InitialDataset dataset = new InitialDataset( new URL(idata.getVariable("CoreserviceUrl") ), 
               idata.getVariable("CoreserviceAdminUser"), idata.getVariable("CoreserviceAdminPassword") );
       dataset.retrieveContentModel(Installer.CHECK_CONTENT_MODEL);
   }
   
   private void createDataset() throws Exception {
       InitialDataset dataset = new InitialDataset( new URL(idata.getVariable("CoreserviceUrl") ), 
               idata.getVariable("CoreserviceAdminUser"), idata.getVariable("CoreserviceAdminPassword") );
       
       String ouExternalObjectId = dataset.createAndOpenOrganizationalUnit("datasetObjects/ou_external.xml");
       String ouDefaultObjectId = dataset.createAndOpenOrganizationalUnit("datasetObjects/ou_default.xml");
       
       configuration.setProperty(Configuration.KEY_EXTERNAL_OU, ouExternalObjectId);      
       String contextObjectId = dataset.createAndOpenContext("datasetObjects/context.xml", ouDefaultObjectId);
       String userModeratorId = dataset.createUser("datasetObjects/user_moderator.xml", 
               idata.getVariable("InitialUserPassword"), ouDefaultObjectId);
       String userDepositorId = dataset.createUser("datasetObjects/user_depositor.xml", 
               idata.getVariable("InitialUserPassword"), ouDefaultObjectId);
       
       dataset.createGrantForUser(
               "datasetObjects/grant_moderator.xml", userModeratorId, contextObjectId);
       dataset.createGrantForUser(
               "datasetObjects/grant_depositor.xml", userDepositorId, contextObjectId);
   }
   
   public void panelActivate() {
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
           JLabel welcomeLabel = LabelFactory.create("Error. Content model("+Installer.CHECK_CONTENT_MODEL+") not available", parent.icons.getImageIcon("host"), LEADING);
           add(welcomeLabel, NEXT_LINE);
           getLayoutHelper().completeLayout();
       }
       
       try {
           JLabel label = LabelFactory.create("Creating dataset on coreservice...", parent.icons.getImageIcon("host"), LEADING);
           add(label, NEXT_LINE);
           getLayoutHelper().completeLayout();
           createDataset();
           JLabel label2 = LabelFactory.create("Good. Dataset created.", parent.icons.getImageIcon("host"), LEADING);
           add(label2, NEXT_LINE);
           getLayoutHelper().completeLayout();
       } 
       catch( Exception e ) {
           JLabel welcomeLabel = LabelFactory.create("Error. Dataset creation error", parent.icons.getImageIcon("host"), LEADING);
           add(welcomeLabel, NEXT_LINE);
           getLayoutHelper().completeLayout();
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
       }
   }
}

