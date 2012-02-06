/**
 * Copyright 2006 OCLC Online Computer Library Center Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or
 * agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mpg.escidoc.services.fledgeddata.oai;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.TransformerFactoryImpl;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.mpg.escidoc.services.fledgeddata.Util;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.CannotDisseminateFormatException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.IdDoesNotExistException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.OAIInternalServerError;
import de.mpg.escidoc.services.fledgeddata.oai.verb.ServerVerb;


/**
 * Utility methods for OAICat and OAIHarvester
 */
public class OAIUtil 
{
	private static final Logger LOGGER = Logger.getLogger(OAIUtil.class);
    private static DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

    static 
    {
        dbFactory.setNamespaceAware(true);
    }
    
    /**
     * XML encode a string.
     * @param s any String
     * @return the String with &amp;, &lt;, and &gt; encoded for use in XML.
     */
    public static String xmlEncode(String s) {
        StringBuffer sb = new StringBuffer();

        for (int i=0; i<s.length(); ++i) {
            char c = s.charAt(i);
            switch (c) {
            case '&':
                sb.append("&amp;");
                break;
            case '<':
                sb.append("&lt;");
                break;
            case '>':
                sb.append("&gt;");
                break;
            case '"':
                sb.append("&quot;");
                break;
            case '\'':
                sb.append("&apos;");
                break;
            default:
                sb.append(c);
                break;
            }
        }
        return sb.toString();
    }
    
    /**
     * Load the properties from the location defined by the system property
     *
     * @throws IOException If the properties file could not be found neither in the file system nor in the classpath. 
     * @throws URISyntaxException 
     */
    public static Properties loadProperties() throws IOException, URISyntaxException
    {
        Properties properties = new Properties();
        URL propUrl = null;
        try
        {
        	propUrl = Util.class.getClassLoader().getResource("fds.properties");
        }
        catch (Exception e)
        {
        	LOGGER.error("[FDS] ERROR reading properties: " + propUrl);
        }
        if (propUrl != null)
        {            
            InputStream in = getInputStream("fds.properties");
            properties.load(in);
            in.close();
        }
            
        LOGGER.info("[FDS] Fledged data service properties loaded.");
        return properties;
    }
    
    /**
     * Retrieves the Inputstream of the given file path.
     * First the resource is searched in the file system, if this fails it is searched using the classpath.
     *
     * @param filepath The path of the file to open.
     * @return The inputstream of the given file path.
     * @throws IOException If the file could not be found neither in the file system nor in the classpath.
     */
    private static InputStream getInputStream(String filepath) throws IOException
    {
        InputStream instream = null;
        String fileLocation = null;
        // First try to search in file system
        try
        {
            instream = new FileInputStream(filepath);
            fileLocation = (new File(filepath)).getAbsolutePath();
        }
        catch (Exception e)
        {
            // try to get resource from classpath
            URL url = OAIUtil.class.getClassLoader().getResource(filepath);
            if (url != null)
            {
                instream = url.openStream();
                fileLocation = url.getFile();
            }
        }
        if (instream == null)
        {
            throw new FileNotFoundException(filepath);
        }
        return instream;
    }
    
    /**
     * Metadata transformation method.
     * @param xsltUri
     * @param itemXML
     * @return transformed metadata as String
     * @throws IOException 
     */
    public String xsltTransform(String xsltName, String itemXML, String type) throws RuntimeException
    {       
        TransformerFactory factory = new TransformerFactoryImpl();
        StringWriter writer = new StringWriter();
        LOGGER.info("[FDS] Transform xml with xslt: " + xsltName);
        
        try
        {
            InputStream in = getInputStream(xsltName);
            Transformer transformer = factory.newTransformer(new StreamSource(in));            
            StringReader xmlSource = new StringReader(itemXML);
            transformer.setParameter("type", type);
            transformer.transform(new StreamSource(xmlSource), new StreamResult(writer));
        }
        catch (TransformerException e)
        {
            throw new RuntimeException();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not find xslt: " + xsltName);
        }

        return writer.toString();
    }
    
    /**
     * Create a single oai record.
     * @param xml
     * @param identifier
     * @return
     */
    public String createNativeOaiRecord (String xml, String identifier)
    {
    	StringBuffer sb = new StringBuffer();
    	String responseDate = ServerVerb.createResponseDate(new Date());

    	xml = xml.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
    	
    	sb.append("<header>");
    		sb.append("<identifier>" + "oai:"+identifier + "</identifier>");
    		sb.append("<datestamp>" + responseDate + "</datestamp>");
    		sb.append("<setSpec>" + parseCollection(xml) + "</setSpec>"); 
    	sb.append("</header>");
    	sb.append("<metadata>");
    		sb.append(xml);
    	sb.append("</metadata>");
    	
    	return sb.toString();
    }
    
    /**
     * Create a list of oai records.
     * @param xml
     * @return
     * @throws IOException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     */
    public String createNativeOaiRecords (String xml) throws SAXException, IOException, ParserConfigurationException
    {
    	StringBuffer sb = new StringBuffer();
    	String responseDate = ServerVerb.createResponseDate(new Date());
    	
        DocumentBuilderFactory docFact = DocumentBuilderFactory.newInstance();
        DocumentBuilder bd = docFact.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml.toLowerCase().trim()));
        Document doc = bd.parse(is);

        NodeList rootNodes = doc.getElementsByTagName("imeji:image");
		NodeList metadataNodes = doc.getElementsByTagName("imeji:metadataset");
		NodeList collNodes = doc.getElementsByTagName("imeji:collection");
		NodeList dateNodes = doc.getElementsByTagName("imeji:creationdate");
		if (rootNodes == null || metadataNodes == null || collNodes== null || dateNodes==null)
		{
			throw new SAXException ("[FDS] xml document does not contain necessary elements.");
		}
		
		//Create the formatted xml
		for (int i=0; i< rootNodes.getLength(); i++)
		{
			sb.append("<record>");
	    	sb.append("<header>");
	    		sb.append("<identifier>" + rootNodes.item(i).getAttributes().item(0).getNodeValue() + "</identifier>");
	    		sb.append("<datestamp>" + dateNodes.item(i).getTextContent() + "</datestamp>");
	    		sb.append("<setSpec>" + collNodes.item(i).getAttributes().item(0).getNodeValue() + "</setSpec>"); 
	    	sb.append("</header>");
	    	sb.append("<metadata>");
		        try
		        {
		           //Set up the output transformer
		          TransformerFactory transfac = TransformerFactory.newInstance();
		          Transformer trans = transfac.newTransformer();
		          trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		          trans.setOutputProperty(OutputKeys.INDENT, "yes");

		          // Print the DOM node
		          StringWriter sw = new StringWriter();
		          StreamResult result = new StreamResult(sw);
		          DOMSource source = new DOMSource(metadataNodes.item(i));
		          trans.transform(source, result);
		    	
		    	  sb.append(sw.toString());
		    	
		        }
		        catch (TransformerException e)
		        {
		          e.printStackTrace();
		        }
		    	sb.append("</metadata>");
		    sb.append("</record>");
		}
    	return sb.toString();
    }
    
    /**
     * Create a list of oai records.
     * @param xml
     * @return
     * @throws IOException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     * @throws OAIInternalServerError 
     * @throws CannotDisseminateFormatException 
     * @throws IdDoesNotExistException 
     */
    public String createNativeOaiRecordsFromSet (String xml, String set, Properties properties) throws SAXException, IOException, 
				    	ParserConfigurationException, 
				    	IdDoesNotExistException, 
				    	CannotDisseminateFormatException, 
				    	OAIInternalServerError
    {
    	StringBuffer sb = new StringBuffer();
    	String responseDate = ServerVerb.createResponseDate(new Date());
    	
        DocumentBuilderFactory docFact = DocumentBuilderFactory.newInstance();
        DocumentBuilder bd = docFact.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml.toLowerCase().trim()));
        Document doc = bd.parse(is);

        NodeList rootNodes = doc.getElementsByTagName("imeji:images");
		NodeList metadataNodes = doc.getElementsByTagName("imeji:metadataset");
		NodeList collNodes = doc.getElementsByTagName("imeji:collection");
		NodeList dateNodes = doc.getElementsByTagName("imeji:creationdate");
		if (rootNodes == null || metadataNodes == null || collNodes== null || dateNodes==null)
		{
			throw new SAXException ("[FDS] xml document does not contain necessary elements.");
		}
		
		//Create the formatted xml
		for (int i=0; i< rootNodes.getLength(); i++)
		{
			String id = rootNodes.item(i).getAttributes().item(0).getNodeValue();
			sb.append("<record>");
		    	sb.append("<header>");
		    		sb.append("<identifier>" + id + "</identifier>");
		    		//sb.append("<datestamp>" + dateNodes.item(i).getTextContent() + "</datestamp>");
		    		sb.append("<setSpec>" + set + "</setSpec>"); 
		    	sb.append("</header>");
		    	sb.append("<metadata>");
		    		String record = oaiCatalog.getRecord(id, "imeji", properties);
		    		sb.append(record);
			    sb.append("</metadata>");
		    sb.append("</record>");
		}
    	return sb.toString();
    }
    
    /**
     * Create a list of oai headers.
     * @param xml
     * @return
     * @throws IOException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     */
    public String createOaiHeader (String xml) throws SAXException, IOException, ParserConfigurationException
    {
    	StringBuffer sb = new StringBuffer();
    	String responseDate = ServerVerb.createResponseDate(new Date());
    	
        DocumentBuilderFactory docFact = DocumentBuilderFactory.newInstance();
        DocumentBuilder bd = docFact.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml.toLowerCase().trim()));
        Document doc = bd.parse(is);

        NodeList rootNodes = doc.getElementsByTagName("imeji:image");
		NodeList dateNodes = doc.getElementsByTagName("imeji:creationdate");
		NodeList collNodes = doc.getElementsByTagName("imeji:collection");
		if (rootNodes == null || collNodes== null || dateNodes==null)
		{
			throw new SAXException ("[FDS] xml document does not contain necessary elements.");
		}
		
		//Create the formatted xml
		for (int i=0; i< rootNodes.getLength(); i++)
		{
		    	sb.append("<header>");
		    		sb.append("<identifier>" + rootNodes.item(i).getAttributes().item(0).getNodeValue() + "</identifier>");
		    		sb.append("<datestamp>" + dateNodes.item(i).getTextContent() + "</datestamp>");
		    		sb.append("<setSpec>" + collNodes.item(i).getAttributes().item(0).getNodeValue() + "</setSpec>"); 
		    	sb.append("</header>");
		}
    	return sb.toString();
    }
    
    /**
     * Create a list of oai headers when a set parameter was provided in the request.
     * @param xml
     * @return
     * @throws IOException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     */
    public String createOaiHeaderFromSet (String xml, String set) throws SAXException, IOException, ParserConfigurationException
    {
    	StringBuffer sb = new StringBuffer();
    	String responseDate = ServerVerb.createResponseDate(new Date());
    	
        DocumentBuilderFactory docFact = DocumentBuilderFactory.newInstance();
        DocumentBuilder bd = docFact.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml.toLowerCase().trim()));
        Document doc = bd.parse(is);

        NodeList rootNodes = doc.getElementsByTagName("imeji:images");
		NodeList collNodes = doc.getElementsByTagName("imeji:collection");
		//TODO catch error when node is missing
		
		//Create the formatted xml
		for (int i=0; i< rootNodes.getLength(); i++)
		{
		    	sb.append("<header>");
		    		sb.append("<identifier>" + rootNodes.item(i).getAttributes().item(0).getNodeValue() + "</identifier>");
		    		//sb.append("<datestamp>" + dateNodes.item(i).getTextContent() + "</datestamp>");
		    		sb.append("<setSpec>" + set + "</setSpec>");
		    	sb.append("</header>");
		}
    	return sb.toString();
    }
    
    private String parseCollection (String xml)
    {
    	String col = "";
    	// magic number 31 is offset of: <imeji:collection rdf:resource=" as an quick and dirty way to retrieve the collection
    	int from = xml.indexOf("imeji:collection") + 31;
    	int until = xml.indexOf("\"", from);
    	col = xml.substring(from, until);
    	
    	return col;
    }
    
    
    public static boolean isHarvestable(Properties properties)
    {
		if (properties.getProperty("Repository.harvestable", "true").equalsIgnoreCase("true")) 
		{
			return true;
		} 
		return false;
    }
    
    public String constructFetchUrl (String url, String identifier) throws OAIInternalServerError
    {
    	String fetchUrl = "";
    	final String id_token = "FETCH_ID";

    	
    	if (!url.contains(id_token))
    	{
    		throw new OAIInternalServerError("Placeholder FETCH_ID not set in Repository.oai.fetchURL");
    	}
    	
    	fetchUrl = url.replace(id_token, identifier);  
    	fetchUrl = fetchUrl.replace(" ", "%20");
    	    	
    	if (fetchUrl.equals(""))
    	{
    		throw new OAIInternalServerError("Repository.oai.fetchURL not set in properties file.");
    	}
    	return fetchUrl;
    }
}
