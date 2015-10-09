package de.mpg.escidoc.services.fledgeddata.sitemap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.mpg.escidoc.services.fledgeddata.Util;
import de.mpg.escidoc.services.fledgeddata.oai.verb.ListIdentifiers;

/**
 * This class creates sitemaps out of an oai call for all identifiers of an item.
 * The sitemap will be created according to the time interval defined in the properties file and
 * will be split each time when reaching 50.000 entries.
 * @author kleinfe1
 *
 */
public class Sitemap extends Thread
{
	private static final Logger LOGGER = Logger.getLogger(Sitemap.class);
	private static FileWriter fileWriter = null;
	private static List<File> files = new ArrayList<File>();
	private static Properties properties;
	final static int maxItemsPerFile = 50000;
	private boolean signal = false;

	/**
     * {@inheritDoc}
     */
    public void run()
    {
    	String appPath;
    	boolean success = false;
    	
        try
        {          
            Integer interval = Integer.parseInt(properties.getProperty("Sitemap.creationInterval"));
            String baseURL = properties.getProperty("baseURL");
 
            changeFile();
            createSitemap();

            try
            {
                appPath = Util.getResourceAsFile("index.jsp").getAbsolutePath();
            }
            catch (Exception e)
            {
            	LOGGER.error("[FDS] index.jsp was not found in web root, terminating sitemap task", e);
                return;
            }
            appPath = appPath.substring(0, appPath.lastIndexOf(System.getProperty("file.separator")) + 1);

            if (files.size() == 1)
            {
                File finalFile = new File(appPath + "sitemap.xml");
                try
                {
                    finalFile.delete();
                }
                catch (Exception e)
                {
                    // Unable to delete file, it probably didn't exist
                }
                fileWriter = new FileWriter(appPath + "sitemap.xml");
                if ((int) files.get(0).length() > 0)
                {
                	this.copySiteMap(files.get(0), finalFile, (int) files.get(0).length(), true);
                }
            }
            else
            {
            	//Create sitemap index page      	
                File indexFile = File.createTempFile("sitemap", ".xml");
                FileWriter indexFileWriter = new FileWriter(indexFile);
                
                indexFileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" "
                        + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                        + "xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 "
                        + "http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\">\n");
                
                for (int i = 0; i < files.size(); i++)
                {
                    File finalFile = new File(appPath + "sitemap" + (i + 1) + ".xml");
                    try
                    {
                        finalFile.delete();
                    }
                    catch (Exception e)
                    {
                        // Unable to delete file, it probably didn't exist
                    }
                    if ((int) files.get(i).length() > 0)
                    {
                    	this.copySiteMap(files.get(i), finalFile, (int) files.get(i).length(), true);
                    }
                    
                    indexFileWriter.write("\t<sitemap>\n\t\t<loc>"
                            + baseURL + "/sitemap"
                            + (i + 1) + ".xml</loc>\n\t\t<lastmod>"
                            + new Date().toString() + "</lastmod>\n\t</sitemap>\n");
                    
                }
                
                indexFileWriter.write("</sitemapindex>\n");
                indexFileWriter.flush();
                indexFileWriter.close();
                
                File finalFile = new File(appPath + "sitemap.xml");
                LOGGER.info("[FDS] sitemap file: " + finalFile.getAbsolutePath());
                try
                {
                    finalFile.delete();
                }
                catch (Exception e)
                {
                    // Unable to delete file, it probably didn't exist
                }
                success = this.copySiteMap(indexFile, finalFile, (int) indexFile.length(), true);
                LOGGER.debug("[FDS] Renaming succeeded: " + success);
            }

            LOGGER.info("[FDS] Finished creating Sitemap.");

            //Create new sitemaps according to the interval in the properties file
            sleep(interval * 60 * 1000);            
            if (!signal)
            {
                Thread nextThread = new Sitemap();
                nextThread.start();
            }
        }
        catch (Exception e)
        {
            LOGGER.error("[FDS] Error creating Sitemap", e);
        }
    }
	
	
	private static void createSitemap() throws ParserConfigurationException, SAXException, IOException
	{
		LOGGER.info("[FDS] Start sitemap creation.");
		
		String listIdentifiersXml = "";
		InputSource is = new InputSource();
		DocumentBuilderFactory docFact = DocumentBuilderFactory.newInstance();
		int recCount = 0;
		
		//Call oai method
		listIdentifiersXml = ListIdentifiers.construct(properties, null, null);		
        DocumentBuilder bd = docFact.newDocumentBuilder();      
        is.setCharacterStream(new StringReader(listIdentifiersXml.toLowerCase().trim()));
        Document doc = bd.parse(is);
  
        //Read in the xml tags
        NodeList locNodes = doc.getElementsByTagName("identifier");
        NodeList dateNodes = doc.getElementsByTagName("datestamp");

        if (locNodes == null || dateNodes==null)
    	{
    		throw new SAXException ("[FDS] xml document does not contain necessary elements.");
    	}
	        //Write the sitemap xml
        	for (int i=0; i< locNodes.getLength(); i++)
	    	{
	    		fileWriter.write("<url>");
	    		fileWriter.write("<loc>" + locNodes.item(i).getTextContent() + "</loc>");
	    		fileWriter.write("<lastmod>" + dateNodes.item(i).getTextContent() + "</lastmod>");
	    		fileWriter.write("</url>");
	    		recCount ++;
	    		
		    	//Check if file needs to be split
		    	if (recCount - maxItemsPerFile >= 0)
		        {
		    		changeFile();
		    		recCount = 0;
		        }
	    	}
	    	fileWriter.write("</urlset>");	    	
	        fileWriter.flush();
	        fileWriter.close();
	}
	
    private static void changeFile()
    {
        try
        {
            if (fileWriter != null)
            {
            	//close 'full' file
            	fileWriter.write("</urlset>");
                fileWriter.flush();
                fileWriter.close();
            }
            
            File file = File.createTempFile("sitemap", ".xml");
            fileWriter = new FileWriter(file);
            files.add(file);
            
            //start next file
            fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                    + "xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 "
                    + "http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\">\n");
        }
        catch (Exception e)
        {
            LOGGER.error("[FDS] Error creating sitemap file.", e);
        }
    }
	
    private boolean copySiteMap(File src, File dest, int bufSize,
            boolean force) throws IOException {
        boolean successful = false;
        if(dest.exists()) {
            if(force) {
                dest.delete();
            } else {
                throw new IOException(
                        "Cannot overwrite existing file: " + dest.getName());
            }
        }
        byte[] buffer = new byte[bufSize];
        int read = 0;
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(src);
            out = new FileOutputStream(dest);
            while(true) {
                read = in.read(buffer);
                if (read == -1) {
                    break;
                }
                out.write(buffer, 0, read);
                successful = true;
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                    successful |= src.delete();
                }
                finally {
                    if (out != null) {
                        out.close();
                    }
                }
            }
        }
        return successful;
    }

    public static Properties getProperties() {
		return properties;
	}

	public static void setProperties(Properties properties) {
		Sitemap.properties = properties;
	}
	
    /**
     * Signals this thread to finish itself.
     */
    public void terminate()
    {
        LOGGER.info("[FDS] Sitemap creation task signalled to terminate.");
        signal = true;
    }
	
}