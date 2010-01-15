package de.mpg.escidoc.pubman.installer.panels;

import java.awt.LayoutManager2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JSeparator;

import com.izforge.izpack.gui.IzPanelLayout;
import com.izforge.izpack.gui.LabelFactory;
import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import com.izforge.izpack.installer.IzPanel;

import de.mpg.escidoc.pubman.installer.Configuration;
import de.mpg.escidoc.pubman.installer.InitialDataset;
import de.mpg.escidoc.pubman.installer.Installer;

public class ConeConfigurationPanel extends IzPanel implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4676947351443726470L;

	
	private Configuration configuration = null;
   private String ouExternalObjectId = null;
   boolean isValid = true;
   private JLabel emptyLabel = LabelFactory.create(" ", LEADING);
   private JCheckBox checkBoxJournals;
   private JCheckBox checkBoxLanguages;
   private JCheckBox checkBoxDDC;
   private JCheckBox checkBoxMimeTypes;
   private JCheckBox checkBoxEscidocMimeTypes;
      
	   /**
	    * The constructor.
	    *
	    * @param parent The parent.
	    * @param idata  The installation data.
	 * @throws IOException 
	    */
	   public ConeConfigurationPanel(InstallerFrame parent, InstallData idata) throws IOException
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

	   public ConeConfigurationPanel(InstallerFrame parent, InstallData idata, LayoutManager2 layout) throws IOException
	   {
	       super(parent, idata, layout);
	       checkBoxJournals = null;
	       checkBoxLanguages = null;
	       checkBoxDDC = null;
	       checkBoxMimeTypes = null;
	       checkBoxEscidocMimeTypes = null;
	       // We create and put the labels
	       String str;
	       str = "Collecting Information for CoNE configuration...";
	       JLabel welcomeLabel = LabelFactory.create(str, parent.icons.getImageIcon("information"), LEADING);
	      
	       add(welcomeLabel, NEXT_LINE);
	       add(emptyLabel, NEXT_LINE);
	       add(emptyLabel, NEXT_LINE);
	      
	       getLayoutHelper().completeLayout();
	       
	       configuration = new Configuration("pubman.properties");
	       
	   }

	   /**
	    * Indicates whether the panel has been validated or not.
	    *
	    * @return Always true.
	    */
	   public boolean isValidated()
	   {
	       return isValid;
	   }
	   
	   public void panelActivate() {
	       
	       JLabel label = LabelFactory.create("Please select the data types you want to have installed into CoNE.", parent.icons.getImageIcon("host"), LEADING);
	       add(label, NEXT_LINE);
	       add(emptyLabel, NEXT_LINE);
	       checkBoxJournals = new JCheckBox("Journals");
	       checkBoxJournals.addActionListener(this);
	       add(checkBoxJournals, NEXT_LINE);
	       checkBoxLanguages = new JCheckBox("Languages");
	       checkBoxLanguages.addActionListener(this);
	       add(checkBoxLanguages, NEXT_LINE);
	       checkBoxDDC = new JCheckBox("DDC");
	       checkBoxDDC.addActionListener(this);
	       add(checkBoxDDC, NEXT_LINE);
	       checkBoxMimeTypes = new JCheckBox("Mimetypes");
	       checkBoxMimeTypes.addActionListener(this);
	       add(checkBoxMimeTypes, NEXT_LINE);
	       checkBoxEscidocMimeTypes = new JCheckBox("eSciDoc Mimetypes");
	       checkBoxEscidocMimeTypes.addActionListener(this);
	       add(checkBoxEscidocMimeTypes, NEXT_LINE);
	       getLayoutHelper().completeLayout();
	   }
	   
	   public void actionPerformed(ActionEvent e)
	    {
		// store settings in iData
		   idata.setVariable("ConeCreateJournals", String.valueOf(checkBoxJournals.isSelected()));
		   idata.setVariable("ConeCreateLanguages", String.valueOf(checkBoxLanguages.isSelected()));
		   idata.setVariable("ConeCreateDDC", String.valueOf(checkBoxDDC.isSelected()));
		   idata.setVariable("ConeCreateMimetypes", String.valueOf(checkBoxMimeTypes.isSelected()));
		   idata.setVariable("ConeCreateEscidocMimeTypes", String.valueOf(checkBoxEscidocMimeTypes.isSelected()));
	    
	    }
}
