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
import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * Utility class to deal with resources such as files and directories. Either on the file system or in jar files.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
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
        // TODO: When the calling class is defined in a war file, this method
        // is unable to find a class loader for it. The commented-out code below was an attempt
        // to manage this behaviour but wasn't successful. Maybe it gives some ideas for the next
        // to try it.
        
        InputStream fileIn;
        fileIn = ResourceUtil.class.getClassLoader().getResourceAsStream(fileName);
//        try
//        {
            if (fileIn == null)
            {
                fileIn = new FileInputStream(fileName);
            }
//        }
//        catch (FileNotFoundException fnfe) {
//            
//            StackTraceElement[] elements = fnfe.getStackTrace();
//            
//            for (int i = 0; i < elements.length; i++)
//            {
//                
//                logger.debug("Element method: " + elements[i].getMethodName());
//                logger.debug("Element class: " + elements[i].getClassName());
//                logger.debug("ResourceUtil class: " + ResourceUtil.class.getName());
//                
//                if (elements[i].getClassName().equals(ResourceUtil.class.getName())
//                        && "getResourceAsStream".equals(elements[i].getMethodName()))
//                {
//                    while (i < elements.length && elements[i].getClassName().equals(ResourceUtil.class.getName()))
//                    {
//                        i++;
//                    }
//                    if (i < elements.length)
//                    {
//                        try
//                        {
//                            Class cls = Class.forName(elements[i].getClassName());
//                            fileIn = cls.getClassLoader().getResourceAsStream(fileName);
//                        }
//                        catch (ClassNotFoundException cnfe) {
//                            logger.error("Error creating caller class", cnfe);
//                        }
//                        if (fileIn == null)
//                        {
//                            throw new FileNotFoundException(fileName + " not found");
//                        }
//                    }
//                    else
//                    {
//                        throw new FileNotFoundException(fileName + " not found");
//                    }
//                }
//            }
//            
//        }
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
     * @throws IOException Thrown if file is not found.
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
            
            for (int i = 0; i < fileNames.length; i++)
            {
                File file = new File(dirFile.getAbsolutePath() + "/" + fileNames[i]);
                
                if (file.isFile())
                {
                    fileArray.add(file);
                }
                else
                {
                    fileArray.addAll(Arrays.asList(getFilenamesInDirectory(file.getAbsolutePath())));
                }
            }
            return fileArray.toArray(new File[]{});
        }
    }

}
