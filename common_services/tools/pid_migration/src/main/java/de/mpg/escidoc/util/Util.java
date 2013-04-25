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
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.escidoc.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;


/**
 * The Util-class offers some static functions that are often needed
 * 
 * @author Matthias Walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class Util
{
	private static final String PROPERTIES_FILE = "src/main/resources/PidMigration.properties";

	private static Properties properties = null;

	public static String getProperty(final String key)
	{
		if (properties == null)
		{
			loadProperties();
		}
		if (System.getProperty(key) != null)
		{
			properties.put(key, System.getProperty(key));
		}

		return properties.getProperty(key);
	}

	public static void loadProperties()
	{
		ClassLoader loader = null;
		InputStream in = null;
		try
		{
			if (loader == null)
			{
				loader = Util.class.getClassLoader();
			}
			InputStream is = loader.getResourceAsStream(PROPERTIES_FILE);
			if (is != null)
			{
				properties = new Properties();
				properties.load(is);
			}
		}
		catch (Exception e)
		{
			properties = null;
		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (Throwable ignore)
				{
					ignore.printStackTrace();
				}
			}
		}
	}

	// transforms an InputString into a String
	public static String inputStreamToString(InputStream in)
	{
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		try
		{
			while ((line = bufferedReader.readLine()) != null)
			{
				stringBuilder.append(line + "\n");
			}
			bufferedReader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return stringBuilder.toString();
	}

	// delivers an Instance of HttpClient
	public static HttpClient getHttpClient()
	{
		HttpClient httpClient = new HttpClient();
		httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		return httpClient;
	}
	
	public static String getValueFromXml(String key, char endChar, String xml)
    {
        String result = "";
        String searchString = key;
        int index = xml.indexOf(searchString);
        if (index > 0)
        {
            xml = xml.substring(index + searchString.length());
            index = xml.indexOf(endChar);
            if (index > 0)
            {
                result = xml.substring(0, index);
            }
        }
        return result;
    } 
	
	  /**
	   * Count files in a directory (including files in all subdirectories)
	   * @param directory the directory to start in
	   * @return the total number of files
	   */
    public static int countFilesInDirectory(File directory)
    {
        int count = 0;
        for (File file : directory.listFiles())
        {
            if (file.isFile())
            {
                count++;
            }
            if (file.isDirectory())
            {
                count += countFilesInDirectory(file);
            }
        }
        return count;
    }

}
