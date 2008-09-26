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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

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
     * Copies one bin file to other 
     * @param in An input file
     * @param out An output file
     * @throws IOException
     */
    public static void copyFileToFile(File in, File out) throws  IOException {
        int b;                // the byte read from the file
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(in));
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(out));
        while ((b = is.read( )) != -1) {
            os.write(b);
        }
        is.close( );
        os.close( );
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
//    	logger.info(":" + ResourceUtil.class.getClassLoader().getResource(classString).getFile() );
        String result = ResourceUtil.class.getClassLoader().getResource(classString).getFile().replace(classString, "");
        // jar context!!!
//        if (!result.equals(ResourceUtil.class.getClassLoader().getResource(".")))
        return 
        	result.indexOf(".jar!") == -1 ?
        		result : RESOURCES_DIRECTORY_JAR;
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
	
	
    /**
     * Load citman properties 
     * @param path - relative path to the prop file  
     * @param fileName - name of the prop file 
     * @return properties
     * @throws IOException 
     * @throws FileNotFoundException 
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static Properties getProperties(String path, String fileName) throws FileNotFoundException, IOException  
    {
    	InputStream is = ResourceUtil.getResourceAsStream(
    			ResourceUtil.getPathToResources()
    			+ (path!=null && !path.trim().equals("") ? path + "/": "")
    			+ fileName
    	); 
    	Properties props = new Properties();
    	props.load(is);
		return props;
    }	
    /**
     * Load citman properties 
     * @param fileName - file name of the properties  
     * @return properties
     * @throws FileNotFoundException
     * @throws IOException
     */
    
    public static Properties getProperties(String fileName) throws FileNotFoundException, IOException 
    {
    	return getProperties("", fileName);
    }	

}
