package de.mpg.escidoc.tools.util.xslt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class LocationHelper
{
	private static Map<String, String> locations = Collections.synchronizedMap(new HashMap<String, String>());
	private static Properties properties = new Properties();
	
	private static Logger logger = Logger.getLogger(LocationHelper.class);
	
	private LocationHelper()
	{
		int numEntries = 0;
		String indexdbFile = "";
		
		try
		{
			InputStream s = LocationHelper.class.getClassLoader().getResourceAsStream("indexer.properties");
			properties.load(s);
			indexdbFile = properties.getProperty("index.db.file");
		} catch (FileNotFoundException e1)
		{
			indexdbFile = "target/classes/indexdb.xml";
		} catch (IOException e1)
		{
			indexdbFile = "./indexdb.xml";
		}

		try
		{
			List<String> entries = FileUtils.readLines(new File(indexdbFile), "UTF-8");
			
			Iterator<String> it = entries.iterator();

			StringBuilder objid = new StringBuilder(512);
			StringBuilder location = new StringBuilder(1024);

			while(it.hasNext())
			{
				String line = (String)it.next();
				StringTokenizer tok = new StringTokenizer(line, "\"", false);
				
				while(tok.hasMoreElements())
				{			
					String element = tok.nextToken();
					// murks
					if (element.startsWith("escidoc"))
					{
						objid.append(element);
					} else if (element.contains("/") && !element.contains(">"))
					{
						location.append(element);
					}
				}
				locations.put(objid.toString(), location.toString());
				numEntries++;
				
				objid.setLength(0);
				location.setLength(0);
			}
			
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info(numEntries + " entries put to map");
	}
	
	public static LocationHelper getInstance()
	{
		return LocationHelperHolder.instance;
	}
	
	public static String getLocation(String objid)
	{
		return LocationHelper.getInstance().doGetLocation(objid);
	}	
	
	private String doGetLocation(String objid)
	{
		String loc = locations.get(objid);
		
		if (loc == null)
		{
			logger.info("No location found for <" + objid + ">");
		}
		return loc;
	}
	
	private static class LocationHelperHolder
	{
		private static final LocationHelper instance = new LocationHelper();
	}
}
