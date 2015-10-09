package de.mpg.escidoc.services.common.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.transformationLight.TransformationInitializer;



public class PropertyReader 

{	private static Logger logger = Logger.getLogger(TransformationInitializer.class);   
	
	private static Properties properties;
	private static final String DEFAULT_PROPERTY_FILE = "transformation.properties";


	/**
	 * Gets the value of a property for the given key from the system properties
	 * or the escidoc property file. It is always tried to get the requested
	 * property value from the system properties. This option gives the
	 * opportunity to set a specific property temporary using the system
	 * properties. If the requested property could not be obtained from the
	 * system properties the escidoc property file is accessed. (For details on
	 * access to the properties file see class description.)
	 * 
	 * @param key
	 *            The key of the property.
	 * @return The value of the property.
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static String getProperty(String key) throws IOException, URISyntaxException 
	{
		// First check system properties
		String value = System.getProperty(key);
		
		if (value != null) 
		{
			return value;
		}
		// Check properties file
		if (properties == null) 
		{
			loadProperties();
		}
		// Get the property
		value = properties.getProperty(key);
		return value;
	}

	public static Properties getProperties() throws IOException, URISyntaxException 
	{
		if (properties == null) 
		{
			loadProperties();
		}

		return properties;
	}

	/**
	 * Load the properties from the location defined by the system property
	 * <code>pubman.properties.file</code>. If this property is not set the
	 * default file path <code>pubman.properties</code> is used.
	 * 
	 * @throws IOException
	 *             If the properties file could not be found neither in the file
	 *             system nor in the classpath.
	 * @throws URISyntaxException
	 */
	public static void loadProperties() throws IOException, URISyntaxException 
	{
		InputStream instream = getInputStream(DEFAULT_PROPERTY_FILE);
		properties = new Properties();
		properties.load(instream);
	}

	/**
	 * Retrieves the Inputstream of the given file path. First the resource is
	 * searched in the file system, if this fails it is searched using the
	 * classpath.
	 * 
	 * @param filepath
	 *            The path of the file to open.
	 * @return The inputstream of the given file path.
	 * @throws IOException
	 *             If the file could not be found neither in the file system nor
	 *             in the classpath.
	 */
	public static InputStream getInputStream(String filepath) throws IOException 
	{
		InputStream instream = null;
		
		// First try to search in file system
		try 
		{
			instream = new FileInputStream(filepath);
			logger.info("Property search in filepath: " + filepath);
		} 
		catch (Exception e) 
		{
			// try to get resource from classpath
			URL url = PropertyReader.class.getClassLoader().getResource(filepath);
			logger.info("Property search in classpath: " + url);
			if (url != null) 
			{
				instream = url.openStream();
			}
			else
			{
	        	String server = System.getProperty("catalina.base");
	        	String path = server + "/conf/"  + filepath;
	        	instream = new FileInputStream(path);
	            
	        	logger.info("Property search for tomcat: " + path);
			}
		}
		if (instream == null) {
			throw new FileNotFoundException(filepath);
		}
		return instream;
	}
}

