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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package test;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * Test class to check Querier implementation.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class RetrieveTest
{
    private static final Logger logger = Logger.getLogger(RetrieveTest.class);
    
    private static String serviceUrl;
    private static HttpClient client;
    
    @BeforeClass
    public static void setUp() throws Exception
    {
        serviceUrl = PropertyReader.getProperty("escidoc.cone.service.url");
        client = new HttpClient();
    }

    @Test
    public void testAllAction() throws Exception
    {
        logger.debug("Query: " + serviceUrl + "options/languages/all");
        GetMethod method = new GetMethod(serviceUrl + "options/languages/all");
        client.executeMethod(method);
        
        assertTrue("Request did not return 200 Ok status", method.getStatusCode() == 200);
        
        String result = getResponseAsString(method);
        
        String[] lines = result.trim().split("\n");
        
        for (String line : lines)
        {
            if (!"".equals(line.trim()))
            {
                assertTrue("Illegal line: '" + line + "'", line.contains("|"));
                String[] parts = line.split("\\|");
                assertTrue("Illegal line: '" + line + "'", parts[0] != null && !"".equals(parts[0]));
                assertTrue("Illegal line: '" + line + "'", parts[1] != null && !"".equals(parts[1]));
            }
        }

    }

    @Test
    public void testQueryAction() throws Exception
    {
        logger.debug("Query: " + serviceUrl + "html/dcc/query?q=b");
        GetMethod method = new GetMethod(serviceUrl + "html/ddc/query?q=b");
        client.executeMethod(method);
        
        assertTrue("Request did not return 200 Ok status, but " + method.getStatusCode(), method.getStatusCode() == 200);
        
        String result = getResponseAsString(method);

        assertTrue("Response does not contain <html :" + result, result.contains("<html"));
        assertTrue("Response does not contain <li>:" + result, result.contains("<li>"));
        
        logger.debug("Result: " + result);

    }

    @Test
    public void testDetailAction() throws Exception
    {
        logger.debug("Query: " + serviceUrl + "jquery/persons/all");
        GetMethod method = new GetMethod(serviceUrl + "jquery/persons/all");
        client.executeMethod(method);
        String result = getResponseAsString(method);
        
        logger.debug("Result: " + result);
        
        String line = result.trim().split("\\n")[0];
        
        logger.debug("Line: " + line);
        
        String id = line.split("\\|")[1];
        
        logger.debug("ID: " + id);
        
        logger.debug("Query: " + serviceUrl + "rdf/persons/details/" + id);
        method = new GetMethod(serviceUrl + "rdf/persons/details/" + id);
        client.executeMethod(method);
        
        assertTrue("Request did not return 200 Ok status, but " + method.getStatusCode(), method.getStatusCode() == 200);
        
        result = getResponseAsString(method);
        
        logger.debug("Result: " + result);

    }

    /**
     * @param method
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    private String getResponseAsString(GetMethod method) throws IOException, UnsupportedEncodingException
    {
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        byte[] inputBuffer = new byte[1024];
        int read;
        
        InputStream inputStream = method.getResponseBodyAsStream();
        
        while ((read = inputStream.read(inputBuffer)) != -1)
        {
            resultStream.write(inputBuffer, 0, read);
        }
        return new String(resultStream.toByteArray(), "UTF-8");
    }
}
