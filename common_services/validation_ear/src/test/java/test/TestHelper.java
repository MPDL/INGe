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
 * Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package test;

import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.validation.xmltransforming.ValidationTransforming;

/**
 * Helper class for all test classes.
 *
 * @author Johannes Müller (initial)
 * @author $Author: mfranke $ (last change)
 * @version $Revision: 123 $ $LastChangedDate: 2007-11-14 10:58:02 +0100 (Wed, 14 Nov 2007) $
 */
public class TestHelper
{
    @SuppressWarnings("unused")
    private static final Logger LOGGER = Logger.getLogger(TestHelper.class);

    private static Properties properties = null;
    private static long logStartTime;
    private static String timeLogComment;
    /**
     * Hidden constructor.
     */
    protected TestHelper()
    {
    }

    /**
     * Reads test properties from test.properties file.
     * @param name The name of the property.
     * @return The value of the property. null if the property is not set.
     */
    public static final String getTestProperty(final String name)
    {
        if (properties == null)
        {
            properties = new Properties();
            try
            {
                properties.load(ResourceUtil.getResourceAsStream("src/test/resources/test.properties"));
            }
            catch (Exception e)
            {
                LOGGER.error("Error reading test properties file", e);
            }
        }
        return properties.getProperty(name);
    }

    /**
     * Helper method to retrieve XmlTransforming instance.
     *
     * @return instance of getXmlTransforming
     * @throws NamingException Thrown when the service was not found by the expected service name.
     */
    public static XmlTransforming getXmlTransforming() throws NamingException
    {
        InitialContext context = new InitialContext();
        XmlTransforming xmlTransforming = (XmlTransforming) context.lookup(XmlTransforming.SERVICE_NAME);
        assertNotNull(xmlTransforming);
        return xmlTransforming;
    }

    /**
     * Helper method to retrieve ValidationTransforming instance.
     *
     * @return instance of get ValidationTransforming
     * @throws NamingException Thrown when the service was not found by the expected service name.
     */
    public static ValidationTransforming getValidationTransforming() throws NamingException
    {
        InitialContext context = new InitialContext();
        ValidationTransforming vTransforming = (ValidationTransforming) context
                .lookup(ValidationTransforming.SERVICE_NAME);
        assertNotNull(vTransforming);
        return vTransforming;
    }

    /**
     * Interface to the validation service REST interface.
     *
     * @param params Parameters such as the validation point.
     * @param content The item XML.
     * @return The report XML.
     */
    public static String callRestWebservice(final NameValuePair[] params, final String content)
    {
        try
        {
            HttpClient client = new HttpClient();
            String url = "http://localhost:8080/validation/rest/validateItemXml";
            if (params != null)
            {
                for (int i = 0; i < params.length; i++)
                {
                    if (i == 0)
                    {
                        url += "?";
                    }
                    else
                    {
                        url += "&";
                    }
                    url += params[i].getName() + "=" + params[i].getValue();
                }
            }
            PostMethod method = new PostMethod(url);
            method.setRequestEntity(new StringRequestEntity(content, null, "UTF-8"));
            client.executeMethod(method);
            return method.getResponseBodyAsString();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error", e);
        }
    }

    /**
     * Interface to the validation service SOAP interface.
     *
     * @param params Parameters such as the validation point.
     * @return The report XML.
     */
    public static String callSoapWebservice(final Object[] params)
    {
        try
        {
            String endpoint = getTestProperty("webservice.url") + getTestProperty("webservice.soap.path");
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(new java.net.URL(endpoint));
            call.setOperationName(new QName(null, "validateItemXml"));
            String ret = (String) call.invoke(params);
            return ret;

        }
        catch (Exception e)
        {
            throw new RuntimeException("Error", e);
        }
    }

    /**
     * Initialize time logging.
     * @param comment A string denoting the start position.
     */
    public static void initTimeLog(final String comment)
    {
        logStartTime = new Date().getTime();
        timeLogComment = comment;
    }

    /**
     * Create a time log entry.
     * @param comment A string denoting the currently executed action.
     */
    public static void logTime(final String comment)
    {
        logTime(comment, -1);
    }

    /**
     * Create a time log entry with a restrictive with a warning limit.
     * @param comment A string denoting the currently executed action.
     * @param warnTime A time span in milliseconds. If the action took longer than this, a warning message is displayed.
     */
    public static void logTime(final String comment, final long warnTime)
    {
        logTime(comment, warnTime, -1);
    }

    /**
     * Create a time log entry.
     * @param comment A string denoting the currently executed action.
     * @param warnTime A time span in milliseconds. If the action took longer than this, a warning message is displayed.
     * @param maxTime A time span in milliseconds. If the action took longer than this, the test will fail.
     */
    public static void logTime(final String comment, final long warnTime, final long maxTime)
    {
        long actualTime = (new Date()).getTime();
        long duration = actualTime - logStartTime;
        LOGGER.info("TimeLog: [" + timeLogComment + "] -> [" + comment + "] = " + duration + "ms.");
        if (maxTime != -1 && duration > maxTime)
        {
            throw new RuntimeException(
                    "Maximal execution time exceeded (max. time was "
                    + maxTime
                    + ", exec. took "
                    + duration + ")");
        }
        if (warnTime != -1 && duration > warnTime)
        {
            LOGGER.warn(
                    "Expected execution time exceeded (exp. time was "
                    + warnTime
                    + ", exec. took "
                    + duration + ")");
        }
        logStartTime = actualTime;
        timeLogComment = comment;
    }
}
