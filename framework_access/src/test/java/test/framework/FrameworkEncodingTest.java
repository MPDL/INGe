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
package test.framework;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.junit.Test;

import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Test cases for Framework Encodings.
 * 
 * @author Johannes Mueller (initial creation)
 * @author $Author: pbroszei $ (last modification)
 * @version $Revision: 314 $ $LastChangedDate: 2007-11-07 13:12:14 +0100 (Wed, 07 Nov 2007) $
 * @revised by BrP: 03.09.2007
 */
public class FrameworkEncodingTest
{
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(FrameworkEncodingTest.class);

    /**
     * Tests whether the ISO-8859-1 character "�" is contained in the response body.
     * 
     * @throws HttpException
     * @throws IOException
     * @throws ServiceException
     */
    @Test
    public void testHttpGetEncoding() throws HttpException, IOException, ServiceException
    {
        // Retrieve organizational unit from the framework
        // This OU contains a description with a German umlaut "�" which will expose a wrong encoding:
        // The full description text is as follows:
        //
        // "Das Institut besteht seit M�rz 1994. Im Zentrum der
        // Forschungsarbeiten steht eine an systematischen Fragestellungen orientierte und
        // theoretisch angeleitete Wissenschaftsgeschichte."
        //
        GetMethod getMethod = new GetMethod(ServiceLocator.getFrameworkUrl()
                + "/oum/organizational-unit/escidoc:persistent1");
        HttpClient client = new HttpClient();
        client.executeMethod(getMethod);

        int statusCode = getMethod.getStatusCode();
        String responseBody = getMethod.getResponseBodyAsString();
        Header[] responseHeaders = getMethod.getResponseHeaders();

        byte[] responseBodyBytes = responseBody.getBytes();
        byte[] responseBodyUtf8Bytes = responseBody.getBytes("UTF-8");

        // assert HTTP status code is OK (200)
        assertEquals(statusCode, HttpServletResponse.SC_OK);

        if (logger.isDebugEnabled())
        {
            StringBuffer sb = new StringBuffer();
            sb.append("testHttpGetEncoding() - int statusCode=" + statusCode + "\n\n");

            sb.append("testHttpGetEncoding() - Header[] responseHeaders=\n######\n");
            for (Header header : responseHeaders)
            {
                sb.append(header);
            }
            sb.append("######\n\n");

            String responseBodyUtf = new String(responseBodyUtf8Bytes);
            sb.append("testHttpGetEncoding() - String responseBody:\n######\n" + responseBody + "\n######\n\n");
            sb.append("testHttpGetEncoding() - String responseBodyUtf:\n######\n" + responseBodyUtf + "\n######\n\n");
            logger.debug(sb.toString());
        }

        // if the size of the response body changes during conversion to UTF8, then it couldn't have been UTF8 before.
        assertEquals("Response body contains non-UTF-8 characters!", responseBodyBytes.length, responseBodyUtf8Bytes.length);
    }
}
