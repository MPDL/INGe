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
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.TransformerFactoryImpl;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.mpg.escidoc.services.fledgeddata.oai.exceptions.OAIInternalServerError;
import de.mpg.escidoc.services.fledgeddata.oai.verb.ServerVerb;


/**
 * Utility methods for OAICat and OAIHarvester
 */
public class OAIUtil {
    private static DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    private static TransformerFactory tFactory = TransformerFactory.newInstance();
    static {
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
     * Transform a DOM Node into an XML String
     * @param node
     * @param omitXMLDeclaration
     * @return an XML String representation of the specified Node
     * @throws TransformerException
     */
    public static String toString(Node node, boolean omitXMLDeclaration)
    throws TransformerException {
        StringWriter writer = new StringWriter();
        Transformer transformer =
            getThreadedIdentityTransformer(omitXMLDeclaration);
        Source source = new DOMSource(node);
        Result result = new StreamResult(writer);
        transformer.transform(source, result);
        return writer.toString();
    }
    
    /**
     * Get a thread-safe Transformer without an assigned transform. This is useful
     * for transforming a DOM Document into XML text.
     * @param omitXmlDeclaration 
     * @return an "identity" Transformer assigned to the current thread
     * @throws TransformerConfigurationException
     */
    public static Transformer getThreadedIdentityTransformer(
            boolean omitXmlDeclaration)
    throws TransformerConfigurationException {
        return getTransformer(omitXmlDeclaration, (String) null);
    }
    
    /**
     * Get a thread-safe Transformer.
     * @param omitXmlDeclaration
     * @param xslURL
     * @return a thread-safe Transformer
     * @throws TransformerConfigurationException
     */
    public static Transformer getTransformer(boolean omitXmlDeclaration,
            String xslURL)
    throws TransformerConfigurationException {
        return getTransformer(omitXmlDeclaration, true, xslURL);
    }
    
    /**
     * @param omitXmlDeclaration
     * @param standalone
     * @param xslURL 
     * @return a Transformer for the specified XSL document
     * @throws TransformerConfigurationException
     */
    public static Transformer getTransformer(
            boolean omitXmlDeclaration,
            boolean standalone, String xslURL)
    throws TransformerConfigurationException {
        Transformer transformer = null;
        if (xslURL == null) {
            transformer = tFactory.newTransformer(); // "never null"
        } else {
            Source xslSource = null;
            if (xslURL.startsWith("file://")) {
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(xslURL.substring(6));
                xslSource = new StreamSource(is);
            } else {
                xslSource = new StreamSource(xslURL);
            }
            transformer = tFactory.newTransformer(xslSource);
        }
//      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.STANDALONE,
                standalone?"yes":"no");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
                omitXmlDeclaration?"yes":"no");
        return transformer;
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
        	propUrl = OAIUtil.class.getClassLoader().getResource("oai.properties");
        }
        catch (Exception e)
        {
            Logger.getLogger(OAIUtil.class).warn("WARNING: oai.properties not found: " + e.getMessage());

        }
        if (propUrl != null)
        {
            System.out.println("properties URI is "+propUrl.toString());
            InputStream in = getInputStream("oai.properties");
            properties.load(in);
            in.close();
        }
            
        Logger.getLogger(OAIUtil.class).info("Properties loaded.");
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
    public String xsltTransform(String xsltUri, String itemXML) throws RuntimeException
    {       
        TransformerFactory factory = new TransformerFactoryImpl();
        StringWriter writer = new StringWriter();
        System.out.println("Transform with xslt: " + xsltUri);
        
        try
        {
            ClassLoader cl = this.getClass().getClassLoader();
            InputStream in = getInputStream(xsltUri);
            Transformer transformer = factory.newTransformer(new StreamSource(in));   
            StringReader xmlSource = new StringReader(itemXML);
            transformer.transform(new StreamSource(xmlSource), new StreamResult(writer));
        }
        catch (TransformerException e)
        {
        	System.out.println(e.getMessage());
            throw new RuntimeException();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not find xslt: " + xsltUri);
        }

        return writer.toString();
    }
    
    public String craeteNativeOaiRecord (String xml, String identifier)
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
		if (properties.getProperty("Repository.harvestable") != null 
				&& properties.getProperty("Repository.harvestable").equalsIgnoreCase("true")) 
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
