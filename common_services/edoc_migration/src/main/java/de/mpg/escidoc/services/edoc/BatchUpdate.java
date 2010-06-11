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

package de.mpg.escidoc.services.edoc;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;

import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;


/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class BatchUpdate
{
    private static final Logger logger = Logger.getLogger(CreatePurgeScript.class);
    
    private static String CORESERVICES_URL;
    // QA
    //private static final String IMPORT_CONTEXT = "escidoc:31013";
    
    // Live
    private static final String IMPORT_CONTEXT = "escidoc:57277";
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        CORESERVICES_URL = PropertyReader.getProperty("escidoc.framework_access.framework.url");
        
        String userHandle = AdminHelper.loginUser("import_user", "");
        
        logger.info("Querying core-services...");
        HttpClient httpClient = new HttpClient();
        String filter = "<param><filter name=\"http://escidoc.de/core/01/structural-relations/context\">" + IMPORT_CONTEXT + "</filter><order-by>http://escidoc.de/core/01/properties/creation-date</order-by><limit>0</limit></param>";
 
        logger.info("Filter: " + filter);
        
        PostMethod postMethod = new PostMethod(CORESERVICES_URL + "/ir/items/filter");
        
        postMethod.setRequestBody(filter);
        
        httpClient.executeMethod(postMethod);
        
//        GetMethod getMethod = new GetMethod(CORESERVICES_URL + "/ir/item/escidoc:100220");
//        getMethod.setRequestHeader("Cookie", "escidocCookie=" + userHandle);
//        httpClient.executeMethod(getMethod);
        
        String response = postMethod.getResponseBodyAsString();
        logger.info("...done!");
        
        System.out.println(response);
        
        while (response.contains("<escidocItem:item"))
        {
            
            int startPos = response.indexOf("<escidocItem:item");
            int endPos = response.indexOf("</escidocItem:item>");

            String item = response.substring(startPos, endPos + 19);
            
            response = response.substring(endPos + 19);
            
            startPos = item.indexOf("xlink:href=\"");
            endPos = item.indexOf("\"", startPos + 12);
            
            String objId = item.substring(startPos + 12, endPos);

            System.out.print(objId);
            
            if (item.contains("escidoc:22019"))
            {
                item = item.replaceAll("escidoc:22019", "escidoc:55222");
            
                PutMethod putMethod = new PutMethod(CORESERVICES_URL + objId);
                
                putMethod.setRequestHeader("Cookie", "escidocCookie=" + userHandle);
                putMethod.setRequestEntity(new StringRequestEntity(item));
                
                httpClient.executeMethod(putMethod);
                
                String result = putMethod.getResponseBodyAsString();
                
                //System.out.println(item);

                startPos = result.indexOf("last-modification-date=\"");
                endPos = result.indexOf("\"", startPos + 24);
                String modDate = result.substring(startPos + 24, endPos);
                //System.out.println("modDate: " + modDate);
                String param = "<param last-modification-date=\"" + modDate + "\"><url>http://pubman.mpdl.mpg.de/pubman/item/" + objId.substring(4) + "</url></param>";
                postMethod = new PostMethod(CORESERVICES_URL + objId + "/assign-version-pid");
                postMethod.setRequestHeader("Cookie", "escidocCookie=" + userHandle);
                postMethod.setRequestEntity(new StringRequestEntity(param));
                httpClient.executeMethod(postMethod);
                result = postMethod.getResponseBodyAsString();
                //System.out.println("Result: " + result);
                
                startPos = result.indexOf("last-modification-date=\"");
                endPos = result.indexOf("\"", startPos + 24);
                modDate = result.substring(startPos + 24, endPos);
                //System.out.println("modDate: " + modDate);
                param = "<param last-modification-date=\"" + modDate + "\"><comment>Batch repair of organizational unit identifier</comment></param>";
                postMethod = new PostMethod(CORESERVICES_URL + objId + "/submit");
                postMethod.setRequestHeader("Cookie", "escidocCookie=" + userHandle);
                postMethod.setRequestEntity(new StringRequestEntity(param));
                httpClient.executeMethod(postMethod);
                result = postMethod.getResponseBodyAsString();
                //System.out.println("Result: " + result);
                
                startPos = result.indexOf("last-modification-date=\"");
                endPos = result.indexOf("\"", startPos + 24);
                modDate = result.substring(startPos + 24, endPos);
                //System.out.println("modDate: " + modDate);
                param = "<param last-modification-date=\"" + modDate + "\"><comment>Batch repair of organizational unit identifier</comment></param>";
                postMethod = new PostMethod(CORESERVICES_URL + objId + "/release");
                postMethod.setRequestHeader("Cookie", "escidocCookie=" + userHandle);
                postMethod.setRequestEntity(new StringRequestEntity(param));
                httpClient.executeMethod(postMethod);
                result = postMethod.getResponseBodyAsString();
                //System.out.println("Result: " + result);
                System.out.println("...changed");
            }
            else
            {
                System.out.println("...not affected");
            }
        }
        
    }
    

}
