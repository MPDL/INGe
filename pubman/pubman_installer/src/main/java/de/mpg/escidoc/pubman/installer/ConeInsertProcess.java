package de.mpg.escidoc.pubman.installer;

import java.io.File;

import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import com.izforge.izpack.gui.LabelFactory;
import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.IzPanel;

import de.mpg.escidoc.pubman.installer.panels.ConfigurationCreatorPanel;
import de.mpg.escidoc.pubman.installer.panels.ConfigurationPanel;
import de.mpg.escidoc.services.framework.PropertyReader;

public class ConeInsertProcess extends Thread 
{

	private ConeDataset coneDataset;
	private InstallData idata;
	private ConfigurationCreatorPanel panel;
	private static final String coneInsertDataFile = "/jboss/server/default/conf/initializeConeDatabase";
	private static Logger logger = Logger.getLogger(ConeInsertProcess.class);
	/**
	 * Public constructor
	 */
	public ConeInsertProcess()
	{
		
	}
	
	public ConeInsertProcess(ConeDataset coneDataset, InstallData idata, ConfigurationCreatorPanel panel)
	{
		this.coneDataset = coneDataset;
		this.idata = idata;
		this.panel = panel;
	}
	
	public void insertConeData() throws Exception
	{
		
			this.panel.getTextArea().append("Inserting CoNE data...\n");
			
		
			logger.info("Starting Cone Initialization");
			
			coneDataset.connectToDB("");
			
			// check if cone database already exists on the Postgres server or not. if not create it.
		   if(coneDataset.isConeDBAvailable(ConeDataset.CONE_CHECK_DATABASES) == false)
		   {
			   coneDataset.runConeScript(ConeDataset.CONE_CREATE_DATABASE);
		   }
		   coneDataset.disconnectFromDB();
		   
		   
		   coneDataset.connectToDB(idata.getVariable("ConeDatabase"));
		   // first create tables
		   coneDataset.runConeScript(ConeDataset.CONE_CREATE_SCRIPT);
		   
		   // then insert data if needed
		   if(idata.getVariable("ConeCreateJournals").equals("true"))
		   {
			   logger.info("Inserting journals");
			   coneDataset.runConeScript(ConeDataset.CONE_INSERT_JOURNALS);
		   }
		   if(idata.getVariable("ConeCreateLanguages").equals("true"))
		   {
			   logger.info("Inserting languages");
			   coneDataset.runConeScript(ConeDataset.CONE_INSERT_LANGUAGES);
		   }
		   if(idata.getVariable("ConeCreateDDC").equals("true"))
		   {
			   logger.info("Inserting DDC");
			   coneDataset.runConeScript(ConeDataset.CONE_INSERT_DDC);
		   }
		   if(idata.getVariable("ConeCreateMimetypes").equals("true"))
		   {
			   logger.info("Inserting Mimetypes");
			   coneDataset.runConeScript(ConeDataset.CONE_INSERT_MIMETYPES);
		   }
		   if(idata.getVariable("ConeCreateEscidocMimeTypes").equals("true"))
		   {
			   logger.info("Inserting eSciDoc Mimetypes");
			   coneDataset.runConeScript(ConeDataset.CONE_INSERT_ESCIDOC_MIMETYPES);
		   }
		   if(idata.getVariable("ConeCreateCcLicenses").equals("true"))
		   {
			   logger.info("Inserting cc licenses");
			   coneDataset.runConeScript(ConeDataset.CONE_INSERT_CC_LICENSES);
		   }
		   
		   // at least index the tables
		   logger.info("Indexing CoNe database");
		   coneDataset.runConeScript(ConeDataset.CONE_INDEX_SCRIPT);

		   coneDataset.disconnectFromDB();
		   
		   panel.getTextArea().append("Good. CoNE data inserted.\n");
		   
		   panel.getTextArea().append("Processing Cone Data...\n");
		   
		   //PropertyReader.
		   /*
		   logger.info("Processing CoNe database.");
		   coneDataset.processConeData();
		   logger.info("CoNE processing finished!");
			*/
		   panel.getTextArea().append("\n\n\n");
		   panel.getTextArea().append("DONE. You can proceed with 'Next' now.\n");
		   File pf = new File(idata.getInstallPath() + coneInsertDataFile);
		   File dir = pf.getParentFile();
		   if (dir == null || !dir.exists())
		   {
		       Configuration.createDir(pf.getAbsolutePath().substring(0, pf.getAbsolutePath().lastIndexOf("/")));
		   }
		   pf.createNewFile();
		   logger.info("Cone initialization finished successfully");
		
		
	}
	
	public void run()
	{
		
		try
		{
			insertConeData();
			panel.coneDataInsertedSuccessfully();
		}
			
		catch(Exception e)
		{
			panel.coneInsertionError(e);
			logger.error("Error during CoNe initialization", e);
		}
	}
}
