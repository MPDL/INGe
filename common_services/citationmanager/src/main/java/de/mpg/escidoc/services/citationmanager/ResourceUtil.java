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

package de.mpg.escidoc.services.citationmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Utility class to deal with resources such as files and directories. Either on the file system or in jar files.
 *
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 131 $ $LastChangedDate: 2007-11-21 18:53:43 +0100 (Wed, 21 Nov 2007) $
 */
public class ResourceUtil
{
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
        File file = new File(ResourceUtil.class.getClassLoader().getResource(fileName).toString());
        if (file == null)
        {
            file = new File(fileName);
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
        InputStream fileIn = null;
        
        File file = new File(fileName);
        if (file.exists())
        {
        	fileIn = new FileInputStream(fileName);
        }
        else
	    {
	    	fileIn = ResourceUtil.class.getClassLoader().getResourceAsStream(fileName);
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
     * Returns path to the directory of the class
     * depending on the run context  
     *     
     * @return path
     * @throws IOException 
     */
    public static String getClassRoot() throws IOException
    {
    	String classString = ResourceUtil.class.getName().replaceAll("\\.", "/") + ".class";
        String result = ResourceUtil.class.getClassLoader().getResource(classString).getFile().replace(classString, "");
        if (!result.equals(ResourceUtil.class.getClassLoader().getResource(".")))
        {
        	return result;
        }
        else
        {
        	return "";
        }
    }


//    /**
//     * Returns path to the resources directory 
//     *     
//     * @return path
//     * @throws IOException 
//     */
//    public static String getPathToResources() throws IOException
//    {
//        return
//    		getClassRoot().replace(ProcessCitationStyles.CLASS_DIRECTORY, ProcessCitationStyles.RESOURCES_DIRECTORY);
//    }
//    
//    
//    /**
//     * Returns path to the Citation Styles directory 
//     *     
//     * @return path
//     * @throws IOException 
//     */
//    public static String getPathToCitationStyles() throws IOException 
//    {
//        return
//			getPathToResources() + ProcessCitationStyles.CITATIONSTYLES_DIRECTORY;
//    }
//    
//    /**
//     * Returns path to the Data Sources directory 
//     *     
//     * @return path
//     * @throws IOException 
//     */
//    public static String getPathToDataSources() throws IOException 
//    {
//    	return
//    		getPathToResources() + ProcessCitationStyles.DATASOURCES_DIRECTORY;
//    }    
    
    
    
}
