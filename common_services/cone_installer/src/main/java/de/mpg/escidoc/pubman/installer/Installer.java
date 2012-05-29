/*
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
package de.mpg.escidoc.pubman.installer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import com.izforge.izpack.installer.InstallerBase;

import de.mpg.escidoc.pubman.installer.util.ResourceUtil;

/**
 * @author endres
 */
public class Installer extends InstallerBase
{
    private Logger logger = null;
    /** values to be asked from the user and updated in the configuration */
    private Map<String, String> userConfigValues = null;
    /** jboss install directory path */
    private String jbossInstallPath;
    /** user defined default password */
    private String defaultUserPassword = null;
    /** configuration of pubman */
    private Configuration config = null;
    /** filename of ear */
    private static final String PUBMAN_EAR_FILENAME = "bin/pubman_ear.ear";
    /** filename of pid cache data source */
    private static final String PID_CACHE_FILENAME = "config/pid-cache-ds.xml";
    /** filename of validation datasource */
    private static final String VALIDATION_FILENAME = "config/validation-ds.xml";
    /** jboss relative deploy path */
    private static final String JBOSS_DEPLOY = "/server/default/deploy";
    /** jboss relative conf path */
    private static final String JBOSS_CONF = "/server/default/conf";
    /** driver class for the cone database */
    private static final String CONE_DB_DRIVER_CLASS = "org.postgresql.Driver";
    /** connection type for the cone database */
    private static final String CONE_DB_CONNECTION_TYPE = "jdbc:postgresql://";
    /** SQL script for creating cone database structure */
    private static final String CONE_CREATE_SCRIPT = "coneData/database_create.sql";
    /** SQL script for creating indexing cone database */
    private static final String CONE_INDEX_SCRIPT = "coneData/database_index.sql";
    /** SQL script for insertig ddc data into cone database */
    private static final String CONE_INSERT_DDC = "coneData/ddc.sql";
    /** SQL script for insertig ddc data into cone database */
    private static final String CONE_INSERT_ESCIDOC_MIMETYPES = "coneData/escidoc_mimetypes.sql";
    /** SQL script for insertig escidoc mimetypes into cone database */
    private static final String CONE_INSERT_JOURNALS = "coneData/journals.sql";
    /** SQL script for insertig languages into cone database */
    private static final String CONE_INSERT_LANGUAGES = "coneData/languages.sql";
    /** SQL script for insertig mimetypes into cone database */
    private static final String CONE_INSERT_MIMETYPES = "coneData/mimetypes.sql";
    /** the connection to the cone DB */
    private Connection connection;

    /**
     * Default constructor
     * 
     * @throws IOException
     */
    public Installer() throws IOException
    {
        logger = Logger.getLogger(Installer.class);
        userConfigValues = new HashMap<String, String>();
        config = new Configuration("pubman.properties");
    }

    public void install() throws IOException, ServiceException, Exception
    {
        printStartMessage();
        collectCoreserviceDataFromUser();
        collectPubmanDataFromUser();
        config.setProperties(userConfigValues);
        createInitialData();
        installFiles();
    }

    public static void main(String[] args)
    {
        Installer installer = null;
        try
        {
            installer = new Installer();
        }
        catch (Exception e)
        {
            System.out.println("Severe problems. Aborting program.");
        }
        try
        {
            installer.install();
            System.out.println("Installer terminated successfully. See installer.log for additional info.");
        }
        catch (Exception e)
        {
            System.out.println("Program aborted. See installer.log for errors.");
            installer.logger.error(e);
        }
    }

    public void printStartMessage()
    {
        System.out.println("PubMan Installer");
        System.out.println("-------------------");
        System.out.println("This PubMan installer is used to install the needed PubMan files to a JBoss server"
                + " and to install an initial dataset on the used coreservice instance.");
        System.out.println("Prerequisites for this installer:");
        System.out.println("- JBoss 4.2.2 server");
        System.out.println("- coreservice instance 1.1 with known admin user");
    }

    public void installFiles() throws Exception
    {
        config.store("pubman.properties");
        String deployDir = jbossInstallPath + JBOSS_DEPLOY;
        String confDir = jbossInstallPath + JBOSS_CONF;
        copyFile("pubman.properties", confDir);
        copyFile(VALIDATION_FILENAME, deployDir);
        copyFile(PID_CACHE_FILENAME, deployDir);
        Thread.sleep(1000);
        copyFile(PUBMAN_EAR_FILENAME, deployDir);
    }

    public void collectCoreserviceDataFromUser() throws IOException
    {
        System.out.println("---Coreservice Settings---");
        userConfigValues.put(Configuration.KEY_CORESERVICE_URL, fetchDataValueFromUser("Enter coreservice URL: "));
        userConfigValues.put(Configuration.KEY_CORESERVICE_ADMINUSERNAME,
                fetchDataValueFromUser("Enter coreservice admin username: "));
        userConfigValues.put(Configuration.KEY_CORESERVICE_ADMINPW,
                fetchDataValueFromUser("Enter coreservice admin password: "));
        System.out.println();
    }

    public void collectPubmanDataFromUser() throws IOException
    {
        System.out.println("---General Settings---");
        jbossInstallPath = fetchDataValueFromUser("Enter the installation path of Jboss: ");
        defaultUserPassword = fetchDataValueFromUser("Enter the default password for users to be created: ");
        userConfigValues.put(Configuration.KEY_INSTANCEURL,
                fetchDataValueFromUser("Enter the instance URL PubMan will be running on: "));
        System.out.println();
        System.out.println("---Mailserver Settings---");
        userConfigValues.put(Configuration.KEY_MAILSERVER, fetchDataValueFromUser("Enter mailserver hostname: "));
        userConfigValues.put(Configuration.KEY_MAIL_SENDER, fetchDataValueFromUser("Enter sender adress: "));
        userConfigValues.put(Configuration.KEY_MAIL_USE_AUTHENTICATION,
                fetchDataValueFromUser("Shall authentication be used (true/false): "));
        userConfigValues.put(Configuration.KEY_MAILUSER, fetchDataValueFromUser("Enter mailserver username: "));
        userConfigValues.put(Configuration.KEY_MAILUSERPW, fetchDataValueFromUser("Enter mailserver password: "));
        System.out.println();
        System.out.println("---CoNE Settings---");
        userConfigValues.put(Configuration.KEY_CONE_SERVER, fetchDataValueFromUser("Enter CoNE database hostname: "));
        userConfigValues.put(Configuration.KEY_CONE_PORT,
                fetchDataValueFromUser("Enter CoNE database listening port: "));
        userConfigValues.put(Configuration.KEY_CONE_USER, fetchDataValueFromUser("Enter CoNE database user name: "));
        userConfigValues.put(Configuration.KEY_CONE_PW, fetchDataValueFromUser("Enter CoNE database user password: "));
        System.out.println();
    }

    private String fetchDataValueFromUser(String queryText) throws IOException
    {
        System.out.print(queryText);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String input;
        input = in.readLine();
        return input;
    }

    public void createInitialData() throws Exception
    {
        System.out.println("Creating initial dataset...");
        InitialDataset dataset = new InitialDataset(new URL(config.getProperty(Configuration.KEY_CORESERVICE_URL)),
                config.getProperty(Configuration.KEY_CORESERVICE_ADMINUSERNAME),
                config.getProperty(Configuration.KEY_CORESERVICE_ADMINPW));
        String ouExternalObjectId = dataset.createAndOpenOrganizationalUnit("datasetObjects/ou_external.xml");
        String ouDefaultObjectId = dataset.createAndOpenOrganizationalUnit("datasetObjects/ou_default.xml");
        config.setProperty(Configuration.KEY_EXTERNAL_OU, ouExternalObjectId);
        String publicationContentModelId = dataset.createContentModel("datasetObjects/cm_publication.xml");
        config.setProperty(Configuration.KEY_PUBLICATION_CM, publicationContentModelId);
        String importTaskContentModelId = dataset.createContentModel("datasetObjects/cm_import_task.xml");
        config.setProperty(Configuration.KEY_IMPORT_TASK_CM, importTaskContentModelId);
        String contextObjectId = dataset.createAndOpenContext("datasetObjects/context.xml", ouDefaultObjectId);
        String userModeratorId = dataset.createUser("datasetObjects/user_moderator.xml", defaultUserPassword,
                ouDefaultObjectId);
        String userDepositorId = dataset.createUser("datasetObjects/user_depositor.xml", defaultUserPassword,
                ouDefaultObjectId);
        dataset.createGrantForUser("datasetObjects/grant_moderator.xml", userModeratorId, contextObjectId);
        dataset.createGrantForUser("datasetObjects/grant_depositor.xml", userDepositorId, contextObjectId);
        // createInitialStatisticData(dataset);
    }

    private void createInitialStatisticData(InitialDataset dataset) throws Exception
    {
        String authorAggrId = dataset.createAggregation("datasetObjects/statistics/aggregation_authors.xml");
        String itemAggrId = dataset.createAggregation("datasetObjects/statistics/aggregation_items.xml");
        String orgAggrId = dataset.createAggregation("datasetObjects/statistics/aggregation_organizations.xml");
        String searchKeywordsAggrId = dataset
                .createAggregation("datasetObjects/statistics/aggregation_search_keywords.xml");
        String usersAggrId = dataset.createAggregation("datasetObjects/statistics/aggregation_users.xml");
        String authorCountryRepDefId = dataset.createReportDefinition(
                "datasetObjects/statistics/report_definition_author_retrievals_country.xml", authorAggrId);
        String authorTimeRepDefId = dataset.createReportDefinition(
                "datasetObjects/statistics/report_definition_author_retrievals_month_year_logged.xml", authorAggrId);
        String itemCountryRepDefId = dataset.createReportDefinition(
                "datasetObjects/statistics/report_definition_item_retrievals_country.xml", itemAggrId);
        String itemTimeRepDefId = dataset.createReportDefinition(
                "datasetObjects/statistics/report_definition_item_retrievals_month_year_logged.xml", itemAggrId);
        String orgCountryRepDefId = dataset.createReportDefinition(
                "datasetObjects/statistics/report_definition_org_retrievals_country.xml", orgAggrId);
        String orgTimeRepDefId = dataset.createReportDefinition(
                "datasetObjects/statistics/report_definition_org_retrievals_month_year_logged.xml", orgAggrId);
        String topSearchKeywordsRepDefId = dataset.createReportDefinition(
                "datasetObjects/statistics/report_definition_top_search_keywords.xml", searchKeywordsAggrId);
    }

    private void copyFile(String fromFileName, String toFileName) throws IOException
    {
        File fromFile = new File(fromFileName);
        File toFile = new File(toFileName);
        if (!fromFile.exists())
            throw new IOException("FileCopy: " + "no such source file: " + fromFileName);
        if (!fromFile.isFile())
            throw new IOException("FileCopy: " + "can't copy directory: " + fromFileName);
        if (!fromFile.canRead())
            throw new IOException("FileCopy: " + "source file is unreadable: " + fromFileName);
        if (toFile.isDirectory())
            toFile = new File(toFile, fromFile.getName());
        if (toFile.exists())
        {
            if (!toFile.canWrite())
                throw new IOException("FileCopy: " + "destination file is unwriteable: " + toFileName);
            System.out.print("Overwrite existing file " + toFile.getName() + "? (Y/N): ");
            System.out.flush();
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String response = in.readLine();
            if (!response.equals("Y") && !response.equals("y"))
                throw new IOException("FileCopy: " + "existing file was not overwritten.");
        }
        else
        {
            String parent = toFile.getParent();
            if (parent == null)
                parent = System.getProperty("user.dir");
            File dir = new File(parent);
            if (!dir.exists())
                throw new IOException("FileCopy: " + "destination directory doesn't exist: " + parent);
            if (dir.isFile())
                throw new IOException("FileCopy: " + "destination is not a directory: " + parent);
            if (!dir.canWrite())
                throw new IOException("FileCopy: " + "destination directory is unwriteable: " + parent);
        }
        FileInputStream from = null;
        FileOutputStream to = null;
        try
        {
            from = new FileInputStream(fromFile);
            to = new FileOutputStream(toFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = from.read(buffer)) != -1)
                to.write(buffer, 0, bytesRead); // write
        }
        finally
        {
            if (from != null)
                try
                {
                    from.close();
                }
                catch (IOException e)
                {
                    ;
                }
            if (to != null)
                try
                {
                    to.close();
                }
                catch (IOException e)
                {
                    ;
                }
        }
    }

    public String getResourceAsString(final String fileName) throws FileNotFoundException, Exception
    {
        StringBuffer buffer = new StringBuffer();
        InputStream is = null;
        BufferedReader br = null;
        String line;
        try
        {
            is = getClass().getClassLoader().getResourceAsStream(fileName);
            br = new BufferedReader(new InputStreamReader(is));
            while (null != (line = br.readLine()))
            {
                buffer.append(line);
                buffer.append("\n");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (br != null)
                    br.close();
                if (is != null)
                    is.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return buffer.toString();
    }

    /**
     * This method cheks which data has to be inserted into cone service and inserts it
     */
    private void insertConeData()
    {
    }

    public void runConeScript(String sqlScript) throws Exception
    {
        logger.info("Initializing import database");
        Class.forName(CONE_DB_DRIVER_CLASS);
        connection = DriverManager.getConnection(
                CONE_DB_CONNECTION_TYPE + userConfigValues.get(Configuration.KEY_CONE_SERVER) + ":"
                        + userConfigValues.get(Configuration.KEY_CONE_PORT) + "/"
                        + userConfigValues.get(Configuration.KEY_CONE_DATABASE),
                userConfigValues.get(Configuration.KEY_CONE_USER), userConfigValues.get(Configuration.KEY_CONE_PW));
        String dbScript = ResourceUtil.getResourceAsString(sqlScript);
        String[] queries = dbScript.split(";");
        try
        {
            for (String query : queries)
            {
                logger.debug("Executing statement: " + query);
                Statement statement = connection.createStatement();
                statement.executeUpdate(query);
                statement.close();
            }
        }
        catch (SQLException e)
        {
            logger.debug("Error description", e);
            logger.info("Import database is set up already");
        }
        logger.info("Import database initialized");
    }
}
