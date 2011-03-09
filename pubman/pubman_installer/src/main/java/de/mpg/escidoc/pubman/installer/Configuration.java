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
* Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/
package de.mpg.escidoc.pubman.installer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * @author endres
 *
 */
public class Configuration
{
    /** */
    private Properties properties = null;
    /** logging instance */
    private Logger logger = null;
    
    public static final String KEY_MAILSERVER = "escidoc.pubman_presentation.email.mailservername";
    public static final String KEY_MAIL_SENDER = "escidoc.pubman_presentation.email.sender";
    public static final String KEY_MAIL_USE_AUTHENTICATION = "escidoc.pubman_presentation.email.withauthentication";
    public static final String KEY_MAILUSER = "escidoc.pubman_presentation.email.authenticationuser";
    public static final String KEY_MAILUSERPW = "escidoc.pubman_presentation.email.authenticationpwd";
    public static final String KEY_INSTANCEURL = "escidoc.pubman.instance.url";
    public static final String KEY_CORESERVICE_URL = "escidoc.framework_access.framework.url";
    public static final String KEY_CORESERVICE_LOGIN_URL = "escidoc.framework_access.login.url";
    public static final String KEY_CORESERVICE_ADMINUSERNAME = "framework.admin.username";
    public static final String KEY_CORESERVICE_ADMINPW = "framework.admin.password";
    public static final String KEY_EXTERNAL_OU = "escidoc.pubman.external.organisation.id";
    public static final String KEY_PUBLICATION_CM = "escidoc.framework_access.content-model.id.publication";
    public static final String KEY_IMPORT_TASK_CM = "escidoc.import.task.content-model";
    public static final String KEY_CONE_SERVER = "escidoc.cone.database.server.name";
    public static final String KEY_CONE_DATABASE = "escidoc.cone.database.name";
    public static final String KEY_CONE_PORT = "escidoc.cone.database.server.port";
    public static final String KEY_CONE_USER = "escidoc.cone.database.user.name";
    public static final String KEY_CONE_PW = "escidoc.cone.database.user.password";
    public static final String KEY_PM_STYLESHEET_STANDARD_URL = "escidoc.pubman.stylesheet.standard.url";
    public static final String KEY_PM_STYLESHEET_STANDARD_TYPE = "escidoc.pubman.stylesheet.standard.type";
    public static final String KEY_PM_STYLESHEET_STANDARD_APPLY = "escidoc.pubman.stylesheet.standard.apply";
    public static final String KEY_PM_STYLESHEET_CONTRAST_URL = "escidoc.pubman.stylesheet.contrast.url";
    public static final String KEY_PM_STYLESHEET_CONTRAST_TYPE = "escidoc.pubman.stylesheet.contrast.type";
    public static final String KEY_PM_STYLESHEET_CONTRAST_APPLY = "escidoc.pubman.stylesheet.contrast.apply";
    public static final String KEY_PM_STYLESHEET_CLASSIC_URL = "escidoc.pubman.stylesheet.classic.url";
    public static final String KEY_PM_STYLESHEET_CLASSIC_TYPE = "escidoc.pubman.stylesheet.classic.type";
    public static final String KEY_PM_STYLESHEET_CLASSIC_APPLY = "escidoc.pubman.stylesheet.classic.apply";
    // PubMan Logo URL
    public static final String KEY_PM_LOGO_URL = "escidoc.pubman.logo.url";
    public static final String KEY_PM_LOGO_APPLY = "escidoc.pubman.logo.apply";
    public static final String KEY_PM_FAVICON_URL = "escidoc.pubman.favicon.url";
    public static final String KEY_PM_FAVICON_APPLY = "escidoc.pubman.favicon.apply";
    public static final String KEY_UNAPI_DOWNLOAD_SERVER = "escidoc.unapi.download.server";
    public static final String KEY_UNAPI_VIEW_SERVER = "escidoc.unapi.view.server";
    // Panel 8
    public static final String KEY_VIEW_ITEM_SIZE = "escidoc.pubman_presentation.viewFullItem.defaultSize";
    public static final String KEY_POLICY_LINK = "escidoc.pubman.policy.url";
    public static final String KEY_CONTACT_LINK = "escidoc.pubman.contact.url";
    public static final String KEY_ACCESS_LOGIN_LINK = "escidoc.framework_access.login.url";
    public static final String KEY_BLOG_NEWS_LINK = "escidoc.pubman.blog.news";
    public static final String KEY_VOCAB_LINK = "escidoc.cone.subjectVocab";
    public static final String KEY_ACCESS_CONF_GENRES_LINK = "escidoc.pubman.genres.configuration";
    // Panel 9
    public static final String KEY_TASK_INT_LINK = "escidoc.pubman.sitemap.task.interval";
    public static final String KEY_MAX_ITEMS_LINK = "escidoc.pubman.sitemap.max.items";
    public static final String KEY_RETRIEVE_ITEMS_LINK = "escidoc.pubman.sitemap.retrieve.items";
    public static final String KEY_RETRIEVE_TIMEOUT_LINK = "escidoc.pubman.sitemap.retrieve.timeout";
    // Panel 10
    public static final String KEY_SORT_KEYS_LINK = "escidoc.search.and.export.default.sort.keys";
    public static final String KEY_SORT_ORDER_LINK = "escidoc.search.and.export.default.sort.order";
    public static final String KEY_MAX_RECORDS_LINK = "escidoc.search.and.export.maximum.records";
    // Panel 12 NEW NEW NEW
    public static final String KEY_PB_HOME_CONTENT_URL = "escidoc.pubman.home.content.url";
    public static final String KEY_PB_SURVEY_URL = "escidoc.pubman.survey.url";
    public static final String KEY_PB_SURVEY_TITLE = "escidoc.pubman.survey.title";
    public static final String KEY_PB_SURVEY_TEXT = "escidoc.pubman.survey.text";
   
    public Configuration(String fileName) throws IOException
    {
        logger = Logger.getLogger(Configuration.class);
        InputStream inStream = getClass().getClassLoader().getResourceAsStream(fileName);
        properties = new Properties();
        properties.load(inStream);
        System.getProperties().putAll(properties);
        logger.info("Created Configuration instance with following attributes: " + properties.toString());
    }
    
    public void store(String fileName) throws IOException
    {
        File dir = new File(fileName).getParentFile();
        if ((dir == null || !dir.exists()) && fileName.contains("/"))
        {
            createDir(fileName.substring(0, fileName.lastIndexOf("/")));
        }
        FileOutputStream outStream = new FileOutputStream(fileName);
        this.properties.store(outStream, "PubMan configuration file");
        outStream.close();
        //
        /*
        FileOutputStream outStream2 = new FileOutputStream("pubman.properties");
        this.properties.store(outStream2, "Automatic created configuration file");
        outStream2.close();
        */
        //Reload property reader
    }
    
    public static void createDir(String path)
    {
        File dir = new File(path);
        File parent = dir.getParentFile();
        if (parent == null || !parent.exists())
        {
            createDir(path.substring(0, path.lastIndexOf("/")));
        }
        dir.mkdir();
    }

    public void setProperty( String key, String value)
    {
        logger.info("Setting property " +  key + "=" + value);
        properties.setProperty(key, value);
        System.setProperty(key, value);
    }
    
    public String getProperty( String key )
    {
        return properties.getProperty(key);
    }
    
    public void setProperties(Map<String, String> props) {
        cleanup(props);
    	properties.putAll(props);
    	System.getProperties().putAll(props);
    }

    private void cleanup(Map<String, String> props)
    {
        Set<String> keys = new HashSet<String>();
        keys.addAll(props.keySet());
        for (String key : keys)
        {
            if (props.get(key) == null)
            {
                props.remove(key);
            }
        }
    }
}
