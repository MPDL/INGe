package de.mpg.escidoc.pubman.installer.panels;

import java.awt.LayoutManager2;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

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

public class DatasetCreatorPanel extends IzPanel
{
    /**
    *
    */
   private static final long serialVersionUID = 3257848774955905587L;

   /**
    * The constructor.
    *
    * @param parent The parent.
    * @param idata  The installation data.
    */
   public DatasetCreatorPanel(InstallerFrame parent, InstallData idata)
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
    */

   public DatasetCreatorPanel(InstallerFrame parent, InstallData idata, LayoutManager2 layout)
   {
       super(parent, idata, layout);
       // We create and put the labels
       String str;
       str = "Creating initial data...";
       JLabel welcomeLabel = LabelFactory.create(str, parent.icons.getImageIcon("host"), LEADING);
      
       add(welcomeLabel, NEXT_LINE);
      
       getLayoutHelper().completeLayout();
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
       Configuration config = new Configuration("configuration/pubman.properties");
       config.store(idata.getInstallPath() + "/jboss-4.2.2.GA/server/default/conf/pubman.properties");
   }
   
   private void checkContentModel() throws MalformedURLException, ServiceException, IOException {
       InitialDataset dataset = new InitialDataset( new URL(idata.getVariable("CoreserviceUrl") ), 
               idata.getVariable("CoreserviceAdminUser"), idata.getVariable("CoreserviceAdminPassword") );
     
   }
   
   private void createDataset() throws MalformedURLException, ServiceException, IOException {
       InitialDataset dataset = new InitialDataset( new URL(idata.getVariable("CoreserviceUrl") ), 
               idata.getVariable("CoreserviceAdminUser"), idata.getVariable("CoreserviceAdminPassword") );
   }
   
   public void panelActivate() {
       try {
           storeConfiguration();
       } 
       catch (IOException e) {
           e.printStackTrace();
       }
    
     try {
        createDataset();
     } 
     catch (Exception e) {
        e.printStackTrace();
    } 
    
       String str;
       str = idata.getVariable("CoreserviceUrl");
       JLabel welcomeLabel = LabelFactory.create(str, parent.icons.getImageIcon("host"), LEADING);

       add(welcomeLabel, NEXT_LINE);

       getLayoutHelper().completeLayout();
   }
}

