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
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.citationmanager.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.sf.jasperreports.engine.JRException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.citationmanager.ProcessCitationStyles;
import de.mpg.escidoc.services.citationmanager.data.Loaders;
import de.mpg.escidoc.services.citationmanager.data.Writers;
import de.mpg.escidoc.services.transformation.TransformationBean;

/**
 * Utility class to deal with resources such as files and directories. Either on the file system or in jar files.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
/**
 * @author vlad
 * 
 */
public class ResourceUtil
{
	private static final Logger logger = Logger.getLogger(ResourceUtil.class);
	
	public final static String CLASS_DIRECTORY = "target/classes/";
	
    public final static String RESOURCES_DIRECTORY_LOCAL = "src/main/resources/";
//    public final static String RESOURCES_DIRECTORY_LOCAL = "target/classes/";
//    public final static String RESOURCES_DIRECTORY_JAR = "resources/";
    public final static String RESOURCES_DIRECTORY_JAR = "";

//    public final static String TEST_RESOURCES_DIRECTORY_LOCAL = "src/test/resources/";
    public final static String TEST_RESOURCES_DIRECTORY_LOCAL = "target/test-classes/";

    
    public final static String DATASOURCES_DIRECTORY = "DataSources/";
    public final static String CITATIONSTYLES_DIRECTORY = "CitationStyles/";
    public static final String SCHEMAS_DIRECTORY = "Schemas/";
    public static final String SORTINGS_DIRECTORY = "Transformations/";
    public static final String TRANSFORMATIONS_DIRECTORY = "Transformations/";
    
    public final static String FONTSTYLES_FILENAME = "FontStyles";
    
    public final static String EXPLAIN_FILE = "explain-styles.xml";
    
    public final static String CITATION_STYLE_PROCESSING_XSL = "escidoc-cscl2cs-processing.xsl";
    
    public final static String CITATION_STYLE_XML = "CitationStyle.xml";
    
    public final static String CITATION_STYLE_XSL = "CitationStyle.xsl";
    
    
    //the hash of the export-formats explain
    private static Map<String, Map<String, String>> exportFormatsHash = null;

    //file extensions hash
    private static final Map<String, String> formatExtensions =   
    	new HashMap<String, String>()   
    	{  
			{  
                put("txt", "txt");
                put("pdf", "pdf");
                put("rtf", "rtf");
                put("html_plain", "html");
                put("html_styled", "html");
                put("odt", "odt");
                put("snippet", "xml");
                put("escidoc_snippet", "xml");
                put("xml", "xml");
                put("escidoc_xml", "xml");
	    	}  
    	};    
    
    //TransformationBean placeholder
    private static TransformationBean tb = null; 

    
	/**
	 * Populates exportFormatsHash (only once)
	 * @return populated exportFormatsHash
	 * @throws CitationStyleManagerException 
	 */
	private static Map<String, Map<String, String>> getOutputFormatsHash() throws CitationStyleManagerException
    {
    	if (exportFormatsHash == null) 
    	{
    		exportFormatsHash = new HashMap<String, Map<String,String>>();
    		for (String cs: XmlHelper.getListOfStyles())
    		{
    			Map<String, String> ofh = new HashMap<String, String>();
    			for (String[] of: XmlHelper.getOutputFormatList(cs))
    			{
    				ofh.put(of[0], of[1]);  
    			}
    			exportFormatsHash.put(cs, ofh);
    		}
    	}
    	return exportFormatsHash;
    }
    
	/**
	 * Returns TransformationBean (singleton)
	 * @return TransformationBean
	 */
	public static TransformationBean getTransformationBean() 
	{
    	return tb == null ? tb = new TransformationBean(true) : tb;
    }

	
	 /**
     * Returns the name of the selected file according to name of format.
     */
    public static String getExtensionByName(String name)
    {
    	name = name == null || name.trim().equals("") ? "" : name.trim();  
    	return formatExtensions.containsKey(name) ? formatExtensions.get(name) : formatExtensions.get("pdf");   
    }
    
	
	
    /**
     * Checks whether the csName is in the list of Citation Styles
     * @param cs - Citation Style name 
     * @return <code>true</code> or <code>false</code>
     * @throws CitationStyleManagerException
     */
    public static boolean isCitationStyle(String cs) throws CitationStyleManagerException 
	{
		Utils.checkCondition( !Utils.checkVal(cs), "Empty name of the citation style");
		 
		return getOutputFormatsHash().containsKey(cs);
		
	}    
	
    /**
     * Checks Output Format (<code>of</code>) availability for  Citation Style (<code>cs</code>) 
     * @param cs - Citation Style name
     * @param of - Output Format name
     * @return <code>true</code> or <code>false</code>
     * @throws CitationStyleManagerException
     */
    public static boolean citationStyleHasOutputFormat(String cs, String of) throws CitationStyleManagerException 
    {
		Utils.checkCondition( !Utils.checkVal(cs), "Empty name of the citation style");
		
		Utils.checkCondition( !Utils.checkVal(of), "Empty name of the output format");
		
    	return  getOutputFormatsHash().get(cs).containsKey(of);
    }
    
	/**
	 * Returns the list of the output formats
	 * for the citation style <code>csName</code> 
	 * @param csName is name of citation style
	 * @return list of the output formats 
	 * @throws CitationStyleManagerException
	 */
	public static String[] getOutputFormats(String csName) throws CitationStyleManagerException 
	{
		Utils.checkCondition( !Utils.checkVal(csName), "Empty name of the citation style");
		
//		Map<String, String> ofh = getOutputFormatsHash().get(csName);
//		String[] ofl = new String[ ofh.size() ];
//		int i = 0;
//		for (String f: ofh.keySet())
//		{
//			ofl[i++] = f;
//		}
		return (String[]) getOutputFormatsHash().get(csName).keySet().toArray(new String[0]);	    
	}
	
	/**
	 * Returns the mime-type for output format of the citation style
	 * @param csName is name of citation style
	 * @param outFormat is the output format 
	 * @return mime-type, or <code>null</code>, if no <code>mime-type</code> has been found    
	 * @throws CitationStyleManagerException if no <code>csName</code> or <code>outFormat</code> are defined 
	 */ 
	public static String getMimeType(String csName, String outFormat) throws CitationStyleManagerException{
		
		Utils.checkCondition( !Utils.checkVal(csName), "Empty name of the citation style");
		
		Utils.checkCondition( !Utils.checkVal(outFormat), "Empty name of the output format");
		
    	return  getOutputFormatsHash().get(csName).get(outFormat);
	}
        
    
    
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
        StringBuffer result = new StringBuffer();
        while ((line = br.readLine()) != null)
        {
        	result.append(line).append("\n");
        }
        return result.toString();
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
        //logger.debug("result:" + result);
        return 
        	result.indexOf(".jar!") == -1 ?
        	    //Decode necessary for windows paths
        		URLDecoder.decode(result, "cp1253") : RESOURCES_DIRECTORY_JAR;
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
     * Returns path to the test resources directory 
     *     
     * @return path
     * @throws IOException 
     */
    public static String getPathToTestResources() throws IOException
    {
    	return
    	getPathToClasses().replace(CLASS_DIRECTORY, TEST_RESOURCES_DIRECTORY_LOCAL);
    }
    
    
    /**
     * Returns path to the test resources directory of the citation style 
     *     
     * @param cs - CItation Style ID
     * @return
     * @throws IOException
     */
    public static String getPathToCitationStyleTestResources(String cs) throws IOException
    {
    	return getPathToTestResources() + CITATIONSTYLES_DIRECTORY + cs + "/";
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
     * Returns path to the Citation Style directory 
     * @param cs
     * @return
     * @throws IOException
     */
    public static String getPathToCitationStyle(String cs) throws IOException 
    {
    	return 
    		getPathToCitationStyles() + cs + "/";
    }
    
    /**
     * Returns path to the Citation Style Definition XML 
     * @param cs
     * @return
     * @throws IOException
     */
    public static String getPathToCitationStyleXML(String cs) throws IOException 
    {
    	return 
    	getPathToCitationStyle(cs) + CITATION_STYLE_XML;
    }
    
    /**
     * Returns path to the Citation Style compiled XSL 
     * @param cs
     * @return
     * @throws IOException
     */
    public static String getPathToCitationStyleXSL(String cs) throws IOException 
    {
    	return 
    	getPathToCitationStyle(cs) + CITATION_STYLE_XSL;
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
    	//    	getPathToResources() 
    		getPathToClasses() + TRANSFORMATIONS_DIRECTORY;
    }
    

	/**
	 * Gets URI to the resources
	 * @return uri to the resources
	 * @throws IOException
	 */
	public static String getUriToResources() throws IOException
	{
		return 
		RESOURCES_DIRECTORY_JAR.equals(getPathToClasses()) ?
			RESOURCES_DIRECTORY_JAR : 
			RESOURCES_DIRECTORY_LOCAL;
	}     
	
	
	/* 
	 * Explains citation styles and output types for them 
	 * @see de.mpg.escidoc.services.citationmanager.CitationStyleHandler#explainStyles()
	 */
	public static String getExplainStyles() throws CitationStyleManagerException 
	{
		String fileString = null;
		try 
		{
			fileString = getResourceAsString( getPathToSchemas() + ResourceUtil.EXPLAIN_FILE );
		} 
		catch (IOException e) 
		{
        	throw new CitationStyleManagerException(e);
		}
		return fileString;
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
    			+ path
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
    
    
    /**
     * Returns list of Citation Styles according 
     * to the content of the <code>CitaionStyles</code> directory  
     * @param root
     * @return list of Citation Styles
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public static String[] getCitationStylesList() throws IllegalArgumentException, IOException {
//    	File templPath = new File(getCsPath(root).toString());
    	File templPath = new File(getPathToCitationStyles());
    	
    	return templPath.list(
    			new FilenameFilter() {
    				public boolean accept(File dir, String name){
    					return (new File(dir, name)).isDirectory() && !name.startsWith(".");
    				}
    			}
    	);
    }

    /**
     * Deletes CitationStyleBundle
     * @param path
     * @param name A name of CitationStyle
     * @throws CitationStyleManagerException 
     * @throws IOException 
     * @throws IOException
     * @throws Exception
     */
    public static void deleteCitationStyleBundle(String name) throws IllegalArgumentException, CitationStyleManagerException, IOException {

    	Utils.checkName(name, "Empty name of CitationStyleBundle");

        File path = new File(getPathToCitationStyles() + "/" + name);
        if (!path.isDirectory()) {
            throw new IllegalArgumentException("deleteCitationStyleBundle: cannot find directory:" + path);
        }
        for (String f: path.list())
        	new File(path, f).delete();
        path.delete();
    }    
    
    /**
     * Creates new CitationStyle directory on hand of existende CitationStyle
     * @param newName A name of CitationStyle to be created
     * @param templName A name of template CitationStyle
     * @throws Exception
     * @throws JRException
     * @throws IOException  
     * @throws CitationStyleManagerException 
     * @throws SAXException 
     * @throws CitationStyleManagerException 
     */
    public static void createNewCitationStyle(ProcessCitationStyles pcs, String templName, String newName) throws JRException, IOException, CitationStyleManagerException, SAXException, CitationStyleManagerException 
    {
    	Utils.checkPcs(pcs);
    	Utils.checkName(templName, "Empty name of the template citation style");
    	Utils.checkName(newName, "Empty name of the new citation style");
    	
        // create new CitationStyle directory
    	String path = getPathToCitationStyles();
        File newPath = new File(path, newName);
        File templPath = new File(path, templName);
        if (newPath.mkdir()) {
            // take only files!!!
            String[] files = templPath.list(
                        new FilenameFilter() {
                            public boolean accept(File dir, String name){
                                return !(new File(dir, name)).isDirectory() && name.endsWith(".xml");
                            }
                        }
                    );
            for (String fn : files) 
                copyFileToFile(new File(templPath, fn), new File(newPath, fn));
            
        } else {
            throw new IOException("Cannot create new directory for CitationStyle:" + newPath );
        }
        //replace old name with new one
        Loaders.loadCitationStyleFromXml(pcs, newName);
        pcs.getCsc().getCitationStyleByName(templName).setName(newName);
        Writers.writeCitationStyleToXml(pcs, newName);
    }

    /**
     * Creates new CitationStyle directory on hand of DefaultCitationStyle XMLs
     * @param newName A name of CitationStyle to be created
     * @param templName A name of template CitationStyle
     * @throws Exception
     * @throws JRException
     * @throws SAXException 
     * @throws IOException 
     * @throws CitationStyleManagerException 
     */
    public static void createNewCitationStyle(ProcessCitationStyles pcs, String newName) throws JRException, CitationStyleManagerException, IOException, SAXException, CitationStyleManagerException 
    {
    	Utils.checkPcs(pcs);
        createNewCitationStyle(pcs, pcs.DEFAULT_STYLENAME, newName);
    }




}
