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
import java.io.FileOutputStream;
import java.io.IOException;
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
        collectDataFromUser();
        config.setProperties(userConfigValues);
        createInitialData();
        if( isContentModelValid() == true) {
            
        }
        else {
            
        }
        config.store("pubman.properties");
    }
      
    public static void main(String[] args)
    {
        try
        {
            Installer installer = new Installer();
            installer.install();
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void printStartMessage() {
        System.out.print("PubMan Installer");
        
        System.out.println();
    }
    
    private void collectDataFromUser() throws IOException {
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
    
    private void createInitialData() throws Exception {
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
        
        String grantModeratorId = dataset.createGrantForUser(
                "datasetObjects/grant_moderator.xml", userModeratorId, contextObjectId);
        String grantDepositorId = dataset.createGrantForUser(
                "datasetObjects/grant_depositor.xml", userDepositorId, contextObjectId);
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
}
