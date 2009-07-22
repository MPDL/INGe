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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

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
    public static final String KEY_CORESERVICE_ADMINUSERNAME = "framework.admin.username";
    public static final String KEY_CORESERVICE_ADMINPW = "framework.admin.password";
    public static final String KEY_EXTERNAL_OU = "escidoc.pubman.external.organisation.id";
    public static final String KEY_CONE_SERVER = "escidoc.cone.database.server.name";
    public static final String KEY_CONE_PORT = "escidoc.cone.database.server.port";
    public static final String KEY_CONE_USER = "escidoc.cone.database.user.name";
    public static final String KEY_CONE_PW = "escidoc.cone.database.user.password";
    
    public Configuration(String fileName) throws IOException
    {
        logger = Logger.getLogger(Configuration.class);
        InputStream inStream = getClass().getClassLoader().getResourceAsStream(fileName);
        properties = new Properties();
        properties.load(inStream);
        logger.info("Created Configuration instance with following attributes: " + properties.toString());
    }
    
    public void store(String FileName) throws IOException
    {
        FileOutputStream outStream = new FileOutputStream(FileName);
        this.properties.store(outStream, "header");
    }
    
    public void setProperty( String key, String value)
    {
        properties.setProperty(key, value);
    }
    
    public String getProperty( String key )
    {
        return properties.getProperty(key);
    }
    
    public void setProperties(Map<String, String> props) {
        Iterator it = props.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            setProperty((String)pairs.getKey(), (String)pairs.getValue());
        }
    }
}
