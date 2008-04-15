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

package de.mpg.escidoc.services.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * Utility class to deal with resources such as files and directories. Either on the file system or in jar files.
 *
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 131 $ $LastChangedDate: 2007-11-21 18:53:43 +0100 (Wed, 21 Nov 2007) $
 */
public class ResourceUtil
{
	
	private static Logger logger = Logger.getLogger(ResourceUtil.class);
	
    /**
     * Hidden constructor.
     */
    protected ResourceUtil()
    {

    }

    /**
     * Gets a resource as InputStream.
     *
     * @param fileName The path and name of the file relative from the working directory.
     * @return The resource as InputStream.
     * @throws FileNotFoundException Thrown if the resource cannot be located.
     */
    public static File getResourceAsFile(final String fileName) throws FileNotFoundException
    {
    	URL url = ResourceUtil.class.getClassLoader().getResource(fileName);
    	File file = null;
    	if (url != null)
    	{
    		logger.debug("Resource found: " + url.getFile());
    		file = new File(url.getFile());
    	}
        
        if (file == null)
        {
        	
        	logger.debug("Resource not found, getting file.");
        	
            file = new File(fileName);
            if (!file.exists())
            {
            	throw new FileNotFoundException("File '" + fileName + "' not found.");
            }
        }
        return file;
    }

    /**
     * Gets a resource as InputStream.
     *
     * @param fileName The path and name of the file relative from the working directory.
     * @return The resource as InputStream.
     * @throws FileNotFoundException Thrown if the resource cannot be located.
     */
    public static InputStream getResourceAsStream(final String fileName) throws FileNotFoundException
    {
        InputStream fileIn;
        fileIn = ResourceUtil.class.getClassLoader().getResourceAsStream(fileName);
        if (fileIn == null)
        {
            fileIn = new FileInputStream(fileName);
        }
        return fileIn;
    }

    /**
     * Gets a resource as String.
     *
     * @param fileName The path and name of the file relative from the working directory.
     * @return The resource as String.
     * @throws IOException Thrown if the resource cannot be located.
     */
    public static String getResourceAsString(final String fileName) throws IOException
    {
        InputStream fileIn = getResourceAsStream(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(fileIn, "UTF-8"));
        String line = null;
        String result = "";
        while ((line = br.readLine()) != null)
        {
            result += line + "\n";
        }
        return result;
    }

    /**
     * Gets an array of files containing the files in a given directory.
     * 
     * @param dir The name of the directory.
     * @return Array of files.
     */
    public static File[] getFilenamesInDirectory(String dir) throws IOException
    {
    	File dirFile = getResourceAsFile(dir);
    	
    	logger.debug("dirFile: " + dirFile + " : " + dirFile.isDirectory() + " : " + dirFile.exists());
    	
    	if (dirFile == null)
    	{
    		return null;
    	}
    	else if (!dirFile.isDirectory())
    	{
    		throw new IOException("The given path is not a directory.");
    	}
    	else
    	{
    		ArrayList<File> fileArray = new ArrayList<File>();
    		String[] fileNames = dirFile.list();
    		for (int i = 0; i < fileNames.length; i++) {
    			fileArray.add(new File(dirFile.getAbsolutePath() + "/" + fileNames[i]));
			}
    		return fileArray.toArray(new File[]{});
    	}
    }
    
}
