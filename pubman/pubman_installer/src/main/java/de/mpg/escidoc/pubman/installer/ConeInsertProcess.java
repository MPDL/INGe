package de.mpg.escidoc.pubman.installer;

import java.io.File;

import javax.swing.JLabel;
import javax.swing.JTextArea;

import com.izforge.izpack.gui.LabelFactory;
import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.IzPanel;

import de.mpg.escidoc.pubman.installer.panels.ConfigurationCreatorPanel;
import de.mpg.escidoc.pubman.installer.panels.ConfigurationPanel;

public class ConeInsertProcess extends Thread 
{

	private ConeDataset coneDataset;
	private InstallData idata;
	private ConfigurationPanel panel;
	private static final String coneInsertDataFile = "/jboss-4.2.2.GA/server/default/conf/initializeConeDatabase";
	/**
	 * Public constructor
	 */
	public ConeInsertProcess()
	{
		
	}
	
	public ConeInsertProcess(ConeDataset coneDataset, InstallData idata, ConfigurationPanel panel)
	{
		this.coneDataset = coneDataset;
		this.idata = idata;
		this.panel = panel;
	}
	
	public void run()
	{
			boolean successful = true;
			
			this.panel.getTextArea().append("Inserting CoNE data...\n");
			try
			{
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
			   
			   panel.getTextArea().append("Good. CoNE data inserted.\n");
			   panel.getTextArea().append("\n\n\n");
			   panel.getTextArea().append("DONE. You can proceed with 'Next' now.\n");
			   File pf = new File(idata.getInstallPath() + coneInsertDataFile);
			   pf.createNewFile();
			   successful = true;
			   panel.setValid(successful);
			}
			catch(Exception e)
			{
				successful = false;
				panel.setValid(successful);
				return;
			}
	}
}
