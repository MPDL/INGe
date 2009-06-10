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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;


/**
 * @author endres
 *
 */
public class Installer
{    
    private Logger logger = null;
    
    /** values to be asked from the user and updated in the configuration */
    private Map<String, String> userConfigValues = null;
    /** jboss install directory path */
    private String jbossInstallPath;
    /** user defined default password */
    private String defaultUserPassword = null;
    /** configuration of pubman */
    private Configuration config = null ;
    /** content model to be checked */
    private static final String CHECK_CONTENT_MODEL = "escidoc:persistent4";
    /** filename of ear */
    private static final String PUBMAN_EAR_FILENAME = "bin/pubman_ear.ear";
    /** filename of validation */
    private static final String VALIDATION_FILENAME = "config/validation-ds.xml";
    /** jboss relative deploy path */
    private static final String JBOSS_DEPLOY = "/server/default/deploy";   
    /** jboss relative conf path */
    private static final String jBOSS_CONF = "/server/default/conf";
    /**
     * Default constructor
     * @throws IOException 
     */
    public Installer() throws IOException {
        logger = Logger.getLogger(Installer.class);   
        userConfigValues = new HashMap<String, String>();
        config = new Configuration("configuration/pubman.properties");
    }
    
    public void install() throws IOException, ServiceException, Exception {
        printStartMessage();
        askUserIfContentModelAvailable();
        collectDataFromUser();
        config.setProperties(userConfigValues);
        createInitialData();
        checkContextModel();
        installFiles();  
    }
      
    public static void main(String[] args)
    {   Installer installer = null;
        try {
           installer = new Installer();
        }
        catch (Exception e){
           System.out.println("Severe problems. Aborting program.");
        }
    
        try
        {
            installer.install();
        } 
        
        catch (Exception e)
        {
            System.out.println("Program aborted. See installer.log for errors.");
            installer.logger.error(e);
        }
        System.out.println("Installer terminated successfully. See installer.log for additional info.");
    }
    
    public void askUserIfContentModelAvailable() throws FileNotFoundException, Exception {
        System.out.println("-------------------");
        System.out.println("Before continuing with the installation and creating of the initial dataset,");
        System.out.println("are you sure the PubMan content model with the identifier 'escidoc:persistent4");
        System.out.print("is available in the coreservice instance? (y/n)");
        System.out.flush();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                System.in));
        String response = in.readLine();
        if (!response.equals("Y") && !response.equals("y")) {
            System.out.println("This means, you have to manual ingest the content model into the coreservice instance.");
            System.out.println("Be aware, that you need root rights to the host, coreservice is running and you should know what you are doing on that host. ;)");
            System.out.println("Ingest the following xml into the fedora system:");
            System.out.println( getResourceAsString("datasetObjects/escidoc_persistent4.xml") );
            System.out.println("Now do a recache in the coreservice admin tool. Reachable via the coreservice homepage.");
            System.out.println("Finally run this tool again.");
            throw new Exception();
        }
        else {
            System.out.println("Good. Then we will proceed with the installation.");
        }        
    }
    
    public void printStartMessage() {
        System.out.println("PubMan Installer");
        System.out.println("-------------------");
        System.out.println("This PubMan installer is used to install the needed PubMan files to a JBoss server" +
        		" and to install an initial dataset on the used coreservice instance.");
        System.out.println("Prerequisites for this installer:");
        System.out.println("- JBoss 4.2.2 server");
        System.out.println("- coreservice instance 1.1 with known admin user");
        System.out.println("- ingested content model, name escidoc:persistent4 into coreservice");
    }
    
    
    
    public void installFiles() throws IOException {
        config.store("pubman.properties"); 
        String deployDir = jbossInstallPath + JBOSS_DEPLOY;
        String confDir = jbossInstallPath + jBOSS_CONF;
        copyFile("pubman.property", confDir);
        copyFile(PUBMAN_EAR_FILENAME, deployDir);
        copyFile(VALIDATION_FILENAME, deployDir);
    }
    
    public void collectDataFromUser() throws IOException {
        System.out.println("---General Settings---");
        jbossInstallPath = fetchDataValueFromUser("Enter the installation path of Jboss: ");
        defaultUserPassword = fetchDataValueFromUser("Enter the default password for users to be created: ");
        userConfigValues.put(Configuration.KEY_INSTANCEURL, 
                fetchDataValueFromUser("Enter the instance URL PubMan will be running on: "));
        System.out.println();
        
        System.out.println("---Coreservice Settings---");
        userConfigValues.put(Configuration.KEY_CORESERVICE_URL, 
                fetchDataValueFromUser("Enter coreservice URL: "));
        userConfigValues.put(Configuration.KEY_CORESERVICE_ADMINUSERNAME, 
                fetchDataValueFromUser("Enter coreservice admin username: "));
        userConfigValues.put(Configuration.KEY_CORESERVICE_ADMINPW, 
                fetchDataValueFromUser("Enter coreservice admin password: "));   
        System.out.println();
        
        System.out.println("---Mailserver Settings---");
        userConfigValues.put(Configuration.KEY_MAILSERVER, 
                fetchDataValueFromUser("Enter mailserver hostname: "));
        userConfigValues.put(Configuration.KEY_MAIL_SENDER, 
                fetchDataValueFromUser("Enter sender adress: ")); 
        userConfigValues.put(Configuration.KEY_MAIL_USE_AUTHENTICATION, 
                fetchDataValueFromUser("Shall authentication be used (true/false): "));
        userConfigValues.put(Configuration.KEY_MAILUSER, 
                fetchDataValueFromUser("Enter mailserver username: "));
        userConfigValues.put(Configuration.KEY_MAILUSERPW, 
                fetchDataValueFromUser("Enter mailserver password: "));
        System.out.println();
        
        System.out.println("---CoNE Settings---");
        userConfigValues.put(Configuration.KEY_CONE_SERVER, 
                fetchDataValueFromUser("Enter CoNE database hostname: "));
        userConfigValues.put(Configuration.KEY_CONE_PORT, 
                fetchDataValueFromUser("Enter CoNE database listening port: ")); 
        userConfigValues.put(Configuration.KEY_CONE_USER, 
                fetchDataValueFromUser("Enter CoNE database user name: ")); 
        userConfigValues.put(Configuration.KEY_CONE_PW, 
                fetchDataValueFromUser("Enter CoNE database user password: "));
        System.out.println();
    }
    
    private String fetchDataValueFromUser(String queryText) throws IOException {
        System.out.print(queryText);
        BufferedReader in = new BufferedReader( new InputStreamReader( System.in )); 
        String input; 
        input = in.readLine();
        return input;
    }
    
    public void createInitialData() throws Exception {
        InitialDataset dataset = new InitialDataset(
                new URL( config.getProperty(Configuration.KEY_CORESERVICE_URL) ),
                config.getProperty(Configuration.KEY_CORESERVICE_ADMINUSERNAME),
                config.getProperty(Configuration.KEY_CORESERVICE_ADMINPW));
        
        String ouExternalObjectId = dataset.createAndOpenOrganizationalUnit("datasetObjects/ou_external.xml");
        String ouDefaultObjectId = dataset.createAndOpenOrganizationalUnit("datasetObjects/ou_default.xml");
        
        config.setProperty(Configuration.KEY_EXTERNAL_OU, ouExternalObjectId);
        
        String contextObjectId = dataset.createAndOpenContext("datasetObjects/context.xml", ouDefaultObjectId);
        
        String userModeratorId = dataset.createUser("datasetObjects/user_moderator.xml", 
                defaultUserPassword, ouDefaultObjectId);
        String userDepositorId = dataset.createUser("datasetObjects/user_depositor.xml", 
                defaultUserPassword, ouDefaultObjectId);
        
        dataset.createGrantForUser(
                "datasetObjects/grant_moderator.xml", userModeratorId, contextObjectId);
        dataset.createGrantForUser(
                "datasetObjects/grant_depositor.xml", userDepositorId, contextObjectId);
    }
    
    public void checkContextModel() throws FileNotFoundException, Exception {
        System.out.println("Checking if context (escidoc:persistent4) is available...");
        if( isContentModelValid() == true) {
            System.out.println("Good. Context is available.");
        }
        else {
            System.out.println("Context is not available. Please ingest the following xml in fedora " +
            		"and do a recache on the eSciDoc core infrastructure before running PubMan.");
            System.out.println();
            
            System.out.println( getResourceAsString("datasetObjects/escidoc_persistent4.xml") );
        }
    }
    
    private boolean isContentModelValid() {
        try
        {
            InitialDataset dataset = new InitialDataset(
                    new URL( config.getProperty(Configuration.KEY_CORESERVICE_URL) ),
                    config.getProperty(Configuration.KEY_CORESERVICE_ADMINUSERNAME),
                    config.getProperty(Configuration.KEY_CORESERVICE_ADMINPW));
            
            dataset.retrieveContentModel(CHECK_CONTENT_MODEL);
            return true;
        } 
        catch (Exception e)
        {
            return false;
        }
    }
    
    private void copyFile(String fromFileName, String toFileName) throws IOException {
        File fromFile = new File(fromFileName);
        File toFile = new File(toFileName);

        if (!fromFile.exists())
            throw new IOException("FileCopy: " + "no such source file: "
                    + fromFileName);
        if (!fromFile.isFile())
            throw new IOException("FileCopy: " + "can't copy directory: "
                    + fromFileName);
        if (!fromFile.canRead())
            throw new IOException("FileCopy: " + "source file is unreadable: "
                    + fromFileName);

        if (toFile.isDirectory())
            toFile = new File(toFile, fromFile.getName());

        if (toFile.exists()) {
            if (!toFile.canWrite())
                throw new IOException("FileCopy: "
                        + "destination file is unwriteable: " + toFileName);
            System.out.print("Overwrite existing file " + toFile.getName()
                    + "? (Y/N): ");
            System.out.flush();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    System.in));
            String response = in.readLine();
            if (!response.equals("Y") && !response.equals("y"))
                throw new IOException("FileCopy: "
                        + "existing file was not overwritten.");
        } else {
            String parent = toFile.getParent();
            if (parent == null)
                parent = System.getProperty("user.dir");
            File dir = new File(parent);
            if (!dir.exists())
                throw new IOException("FileCopy: "
                        + "destination directory doesn't exist: " + parent);
            if (dir.isFile())
                throw new IOException("FileCopy: "
                        + "destination is not a directory: " + parent);
                if (!dir.canWrite())
                    throw new IOException("FileCopy: "
                            + "destination directory is unwriteable: " + parent);
        }

        FileInputStream from = null;
        FileOutputStream to = null;
        try {
            from = new FileInputStream(fromFile);
            to = new FileOutputStream(toFile);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = from.read(buffer)) != -1)
                to.write(buffer, 0, bytesRead); // write
        } 
        finally {
            if (from != null)
                try {
                    from.close();
                } catch (IOException e) {
                    ;
                }
                if (to != null)
       try {
           to.close();
       } catch (IOException e) {
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
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (br != null)
                    br.close();
                if (is != null)
                    is.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return buffer.toString();
    }
}
