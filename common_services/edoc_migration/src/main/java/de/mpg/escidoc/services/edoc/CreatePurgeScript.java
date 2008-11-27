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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.edoc;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.mpg.escidoc.services.framework.PropertyReader;


/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class CreatePurgeScript
{
    private static final Logger logger = Logger.getLogger(CreatePurgeScript.class);
    
    private static String CORESERVICES_URL;
    private static final String IMPORT_CONTEXT = "escidoc:31013";
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        CORESERVICES_URL = PropertyReader.getProperty("escidoc.framework_access.framework.url");
        
        logger.info("Querying core-services...");
        HttpClient httpClient = new HttpClient();
        GetMethod getMethod = new GetMethod(CORESERVICES_URL + "/srw/search/escidoc_all?maximumRecords=10000&query=escidoc.context.objid=" + IMPORT_CONTEXT);
        httpClient.executeMethod(getMethod);
        String response = getMethod.getResponseBodyAsString();
        logger.info("...done!");
        
        //System.out.println(response);
        
        logger.info("Transforming result...");
        XSLTTransform transform = new XSLTTransform();
        File stylesheet = new File("src/main/resources/itemlist2purgescript.xslt");
        FileOutputStream outputStream = new FileOutputStream("purge.sh");
        transform.transform(response, stylesheet, outputStream);
        logger.info("...done!");
        
        logger.info("Finished!");
    }
}
