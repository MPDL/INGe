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
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.installer.panels;

import java.awt.LayoutManager2;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import org.apache.tools.ant.taskdefs.Sleep;

import com.izforge.izpack.Pack;
import com.izforge.izpack.gui.IzPanelLayout;
import com.izforge.izpack.gui.LabelFactory;
import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import com.izforge.izpack.installer.IzPanel;

import de.mpg.escidoc.pubman.installer.ConeDataset;
import de.mpg.escidoc.pubman.installer.ConeInsertProcess;
import de.mpg.escidoc.pubman.installer.Configuration;
import de.mpg.escidoc.pubman.installer.InitialDataset;
import de.mpg.escidoc.pubman.installer.Installer;
import de.mpg.escidoc.pubman.installer.util.LabelPanel;
import de.mpg.escidoc.services.framework.PropertyReader;

public class ConfigurationCreatorPanel extends ConfigurationPanel
{
    private static final long serialVersionUID = 3257848774955905587L;
    private Configuration configuration = null;
    ConeInsertProcess coneInsertProcess;
    private LabelPanel conePanel;
    private boolean success;

    /**
     * The constructor.
     * 
     * @param parent The parent.
     * @param idata The installation data.
     * @throws IOException
     */
    public ConfigurationCreatorPanel(InstallerFrame parent, InstallData idata) throws IOException
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
     * @throws IOException
     */
    public ConfigurationCreatorPanel(InstallerFrame parent, InstallData idata, LayoutManager2 layout)
            throws IOException
    {
        super(parent, idata, layout);
        // We create and put the labels
        JLabel welcomeLabel = LabelFactory.create("Writing configuration...", parent.icons.getImageIcon("host"),
                LEADING);
        add(welcomeLabel, NEXT_LINE);
        getLayoutHelper().completeLayout();
        configuration = new Configuration("pubman.properties");
    }

    /**
     * Indicates wether the panel has been validated or not.
     * 
     * @return Always true.
     */
    public boolean isValidated()
    {
        return isPanelValid;
    }

    private void storeConfiguration() throws IOException, URISyntaxException
    {
        Map<String, String> userConfigValues = new HashMap<String, String>();
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
        userConfigValues.put(Configuration.KEY_PM_FAVICON_URL, idata.getVariable("FavIconURL"));
        userConfigValues.put(Configuration.KEY_PM_FAVICON_APPLY, idata.getVariable("FavIconApply"));
        
        userConfigValues.put(Configuration.KEY_UNAPI_DOWNLOAD_SERVER, idata.getVariable("InstanceUrl") + "/dataacquisition/download/unapi");
        userConfigValues.put(Configuration.KEY_UNAPI_VIEW_SERVER, idata.getVariable("InstanceUrl") + "/dataacquisition/view/unapi");
        
        // TODO:
        //
        //
        //
        
        configuration.setProperties(userConfigValues);
        configuration.store(idata.getInstallPath() + "/jboss/server/default/conf/pubman.properties");
        // also store in local pubman properties
        configuration.store("pubman.properties");
        // ... and update PropertyReader
        PropertyReader.loadProperties();
    }

    private void createDataset() throws Exception
    {
        String ouExternalObjectId = null;
        InitialDataset dataset = new InitialDataset(new URL(idata.getVariable("CoreserviceUrl")),
                idata.getVariable("CoreserviceAdminUser"), idata.getVariable("CoreserviceAdminPassword"));
        ouExternalObjectId = dataset.createAndOpenOrganizationalUnit("datasetObjects/ou_external.xml");
        String publicationContentModelId = dataset.createContentModel("datasetObjects/cm_publication.xml");
        configuration.setProperty(Configuration.KEY_PUBLICATION_CM, publicationContentModelId);
        String importTaskContentModelId = dataset.createContentModel("datasetObjects/cm_import_task.xml");
        configuration.setProperty(Configuration.KEY_IMPORT_TASK_CM, importTaskContentModelId);
        String ouDefaultObjectId = dataset.createAndOpenOrganizationalUnit("datasetObjects/ou_default.xml");
        configuration.setProperty(Configuration.KEY_EXTERNAL_OU, ouExternalObjectId);
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
            ConeDataset coneDataset = new ConeDataset(idata.getVariable("ConeHost"), idata.getVariable("ConePort"),
                    idata.getVariable("ConeDatabase"), idata.getVariable("ConeUser"), idata.getVariable("ConePassword"));
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
                createDataset();
                datasetPanel.setEndLabel("Initial dataset created successfully!", LabelPanel.ICON_SUCCESS);
            }
            catch (Exception e)
            {
                datasetPanel.setEndLabel("Error while creating initial dataset!", LabelPanel.ICON_ERROR);
                String hint = "Please rerun installation and ensure that the eSciDoc coreservice is running and the correct coreservice credentials are provided in the installer.";
                datasetPanel.addToTextArea(e.toString() + ": " + e.getMessage() + "\n" + hint);
                e.printStackTrace();
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
