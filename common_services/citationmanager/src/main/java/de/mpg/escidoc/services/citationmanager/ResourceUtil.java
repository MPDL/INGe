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
	private static final Logger logger = Logger.getLogger(ResourceUtil.class);
	
    public final static String RESOURCES_DIRECTORY_LOCAL = "src/main/resources/";
    public final static String RESOURCES_DIRECTORY_JAR = "";
    public final static String CLASS_DIRECTORY = "target/classes/";

    
    public final static String DATASOURCES_DIRECTORY = "DataSources/";
    public final static String CITATIONSTYLES_DIRECTORY = "CitationStyles/";
    public static final String SCHEMAS_DIRECTORY = "Schemas/";
    public static final String SORTINGS_DIRECTORY = "Transformations/";
    
    public final static String FONTSTYLES_FILENAME = "FontStyles";
    
    public final static String EXPLAIN_FILE = "explain-styles.xml";
   

    /**
     * Gets a resource as InputStream.
     *
     * @param fileName The path and name of the file relative from the working directory.
     * @return The resource as InputStream.
     * @throws FileNotFoundException Thrown if the resource cannot be located.
     */
    public static File getResourceAsFile(final String fileName) throws FileNotFoundException
    {
        File file = null;
        if (ResourceUtil.class.getClassLoader().getResource(fileName) != null)
       	{
        	file = new File(ResourceUtil.class.getClassLoader().getResource(fileName).getFile());
       	}
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
     * Returns path to the directory of the classes
     * depending on the run context (TOBE implemented further) 
     *     
     * @return path
     * @throws IOException 
     */
    public static String getPathToClasses() throws IOException
    {
    	String classString = ResourceUtil.class.getName().replace(".", "/") + ".class";
        String result = ResourceUtil.class.getClassLoader().getResource(classString).getFile().replace(classString, "");
        // jar context!!!
//        if (!result.equals(ResourceUtil.class.getClassLoader().getResource(".")))
        if ( result.indexOf(".jar!") == -1 )
        {
        	return result;
        }
        else
        {
        	return RESOURCES_DIRECTORY_JAR;
        }
    }


    /**
     * Returns path to the resources directory 
     *     
     * @return path
     * @throws IOException 
     */
    public static String getPathToResources() throws IOException
    {
        return
    		getPathToClasses().replace(CLASS_DIRECTORY, RESOURCES_DIRECTORY_LOCAL);
    }
    
    
    /**
     * Returns path to the Citation Styles directory 
     *     
     * @return path
     * @throws IOException 
     */
    public static String getPathToCitationStyles() throws IOException 
    {
        return
			getPathToResources() + CITATIONSTYLES_DIRECTORY;
    }

   

    /**
     * Returns path to the Data Sources directory 
     *     
     * @return path
     * @throws IOException 
     */
    public static String getPathToDataSources() throws IOException 
    {
    	return
    		getPathToResources() + DATASOURCES_DIRECTORY;
    }    
    
    /**
     * Returns path to the Schemas directory 
     *     
     * @return path
     * @throws IOException 
     */
    public static String getPathToSchemas() throws IOException 
    {
        return
			getPathToResources() + SCHEMAS_DIRECTORY;
    }

    /**
     * Returns path to the Transformations directory 
     *     
     * @return path
     * @throws IOException 
     */
    public static String getPathToTransformations() throws IOException 
    {
    	return
    	getPathToResources() + SORTINGS_DIRECTORY;
    }
    
//	/**
//	 * Generates file location (URI) independent of service location: 
//	 * in jboss .ear or stand alone  
//	 * @param fileName is a file name
//	 * @return file location
//	 * @throws IOException
//	 */
//	public static String getUriToResources(final String fileName) throws IOException
//	{
//		URL fileURL = ResourceUtil.class.getClassLoader().getResource(fileName);
//		String fileLoc;
//
//		if (fileURL == null)
//		{
//			fileLoc = (new File(".")).getAbsolutePath() + "/" + fileName;
//		}
//		else
//		{
//			fileLoc = fileURL.toString();
//		}
//		return fileLoc;
//	}     

	/**
	 * Gets URI to the resources
	 * @return uri to the resources
	 * @throws IOException
	 */
	public static String getUriToResources() throws IOException
	{
		return 
			getPathToClasses().equals(RESOURCES_DIRECTORY_JAR) ?
					RESOURCES_DIRECTORY_JAR : 
					RESOURCES_DIRECTORY_LOCAL;
					
		
	}     
	

}
