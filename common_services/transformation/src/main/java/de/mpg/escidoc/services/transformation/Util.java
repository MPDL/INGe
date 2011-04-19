/*
*
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
* Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.transformation;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;

import net.sf.saxon.dom.DocumentBuilderFactoryImpl;

import org.apache.axis.encoding.Base64;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlString;
import org.purl.dc.elements.x11.SimpleLiteral;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.mpg.escidoc.metadataprofile.schema.x01.transformation.FormatType;
import de.mpg.escidoc.metadataprofile.schema.x01.transformation.FormatsDocument;
import de.mpg.escidoc.metadataprofile.schema.x01.transformation.FormatsType;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ProxyHelper;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/**
 * Helper methods for the transformation service.
 *
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class Util
{
    private static Logger logger = Logger.getLogger(Util.class);
    
    // Jasper styles enum
    public static enum Styles { APA, AJP, Default }; 
    
    /**
     * Converts a simpleLiteral Objects into a String Object.
     * @param sl as SimpleLiteral
     * @return String
     */
    public String simpleLiteralTostring(SimpleLiteral sl)
    {
        return sl.toString().substring(sl.toString().indexOf(">") + 1, sl.toString().lastIndexOf("<"));
    }
    
    /**
     * Creates a format xml out of a format array.
     * @param formats as Format[]
     * @return xml as String
     */
    public String createFormatsXml(Format[] formats)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            FormatsDocument xmlFormatsDoc = FormatsDocument.Factory.newInstance();
            FormatsType xmlFormats = xmlFormatsDoc.addNewFormats();
            for (int i = 0; i < formats.length; i++)
            {
                Format format = formats[i];
                FormatType xmlFormat = xmlFormats.addNewFormat();
                SimpleLiteral name = xmlFormat.addNewName();
                XmlString formatName = XmlString.Factory.newInstance();
                formatName.setStringValue(format.getName());
                name.set(formatName);
                SimpleLiteral type = xmlFormat.addNewType();
                XmlString formatType = XmlString.Factory.newInstance();
                formatType.setStringValue(format.getType());
                type.set(formatType);
                SimpleLiteral enc = xmlFormat.addNewEncoding();
                XmlString formatEnc = XmlString.Factory.newInstance();
                formatEnc.setStringValue(format.getEncoding());
                enc.set(formatEnc);
    
            }
            XmlOptions xOpts = new XmlOptions();
            xOpts.setSavePrettyPrint();
            xOpts.setSavePrettyPrintIndent(4);
            xOpts.setUseDefaultNamespace();
            xmlFormatsDoc.save(baos, xOpts);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return baos.toString();
    }

    /**
     * Checks if two Format Objects are equal.
     * @param src1
     * @param src2
     * @return true if equal, else false
     */
    public boolean isFormatEqual(Format src1, Format src2)
    {       
        if (!src1.getName().toLowerCase().trim().equals(src2.getName().toLowerCase().trim())) 
        { return false; }
        if (!src1.getType().toLowerCase().trim().equals(src2.getType().toLowerCase().trim())) 
        { return false; }
        if (src1.getEncoding().equals("*") || src2.getEncoding().equals("*"))
        {
            return true;
        }
        else 
        {
            if (!src1.getEncoding().toLowerCase().trim().equals(src2.getEncoding().toLowerCase().trim())) 
            { return false; }
            else 
            { return true; }
        }
    }
    
    /**
     * Converts a Aormat Vector into a Format Array.
     * @param formatsV as Vector
     * @return Format[]
     */
    public Format[] formatVectorToFormatArray(Vector<Format> formatsV)
    {
        Format[] formatsA = new Format[formatsV.size()];     
        for (int i = 0; i < formatsV.size(); i++)
        {
            formatsA[i] = (Format) formatsV.get(i);
        }     
        return formatsA;
    }
    
    /**
     * Eliminates duplicates in a Vector.
     * @param dirtyVector as Format Vector
     * @return Vector with unique entries
     */
    public Vector<Format> getRidOfDuplicatesInVector(Vector<Format> dirtyVector)
    {
        Vector<Format> cleanVector = new Vector<Format>();
        Format format1;
        Format format2;
        
        
        for (int i = 0; i < dirtyVector.size(); i++)
        {
            boolean duplicate = false;
            format1 = (Format) dirtyVector.get(i);
            for (int x = i + 1; x < dirtyVector.size(); x++)
            {
                format2 = (Format) dirtyVector.get(x);
                if (this.isFormatEqual(format1, format2))
                {
                    duplicate = true;
                }
            }
            if (!duplicate)
            {
                cleanVector.add(format1);  
            }
        }
        
        return cleanVector;
    }
    
    /**
     * Checks if a array contains a specific format object.
     * @param formatArray
     * @param format
     * @return true if the array contains the format object, else false
     */
    public boolean containsFormat(Format[] formatArray, Format format)
    {
        if (formatArray == null || format == null)
        {
            return false;
        }
        for (int i = 0; i < formatArray.length; i++) 
        {
            Format tmp = formatArray[i];
            if (this.isFormatEqual(format, tmp))
            {
                return true;
            }
        }
        return false;
    }
    

    /**
     * Merges a Vector of Format[] into Format[].
     * @param allFormatsV as Format[] Vector
     * @return Format[]
     */
    public Format[] mergeFormats(Vector<Format[]> allFormatsV)
    {
        Vector<Format> tmpV = new Vector<Format>();
        Format[] tmpA;
        
        for (int i = 0; i < allFormatsV.size(); i++)
        {
            tmpA = allFormatsV.get(i);
            if (tmpA != null)
            {
	            for (int x = 0; x < tmpA.length; x++)
	            {
	                tmpV.add(tmpA[x]);
	                //System.out.println(tmpA[x].getName());
	            }
            }
        }
        tmpV = this.getRidOfDuplicatesInVector(tmpV);
        return this.formatVectorToFormatArray(tmpV);
    }
    
    /**
     * Normalizes a given mimetype.
     * @param mimetype
     * @return
     */
    public String normalizeMimeType(String mimetype)
    {
        String thisMimetype = mimetype;
        if (mimetype.toLowerCase().equals("text/xml"))
        {
            thisMimetype = "application/xml";
        }
        if (mimetype.toLowerCase().equals("text/rtf"))
        {
            thisMimetype = "application/rtf";
        }
        if (mimetype.toLowerCase().equals("text/richtext"))
        {
            thisMimetype = "application/rtf";
        }
        return thisMimetype;
    }
    
    /**
     * Queries CoNE service and returns the result as DOM node.
     * The returned XML has the following structure:
     * <cone>
     *   <author>
     *     <familyname>Buxtehude-Mölln</familyname>
     *     <givenname>Heribert</givenname>
     *     <prefix>von und zu</prefix>
     *     <title>König</title>
     *   </author>
     *   <author>
     *     <familyname>Müller</familyname>
     *     <givenname>Peter</givenname>
     *   </author>
     * </authors>
     * 
     * @param authors
     * @return 
     */
    public static Node queryCone(String model, String query)
    {
        DocumentBuilder documentBuilder;

        try
        {
            documentBuilder = DocumentBuilderFactoryImpl.newInstance().newDocumentBuilder();
        
            Document document = documentBuilder.newDocument();
            Element element = document.createElement("cone");
            document.appendChild(element);
            
            String queryUrl = PropertyReader.getProperty("escidoc.cone.service.url")
                 + model + "/query?format=jquery&q=" + URLEncoder.encode(query, "ISO-8859-15");
            String detailsUrl = PropertyReader.getProperty("escidoc.cone.service.url")
                + model + "/resource/$1?format=rdf";
            HttpClient client = new HttpClient();
            GetMethod method = new GetMethod(queryUrl);
            ProxyHelper.executeMethod(client, method);
            if (method.getStatusCode() == 200)
            {
                String[] results = method.getResponseBodyAsString().split("\n");
                for (String result : results)
                {
                    if (!"".equals(result.trim()))
                    {
                        String id = result.split("\\|")[1];
                        GetMethod detailMethod = new GetMethod(id + "?format=rdf&eSciDocUserHandle=" + Base64.encode(AdminHelper.getAdminUserHandle().getBytes("UTF-8")));
                        
                        logger.info(detailMethod.getPath());
                        logger.info(detailMethod.getQueryString());
                        
                        ProxyHelper.setProxy(client, detailsUrl.replace("$1", id));
                        client.executeMethod(detailMethod);
                        if (detailMethod.getStatusCode() == 200)
                        {
                            Document details = documentBuilder.parse(detailMethod.getResponseBodyAsStream());
                            element.appendChild(document.importNode(details.getFirstChild(), true));

                        }
                        else
                        {
                            logger.error("Error querying CoNE: Status "
                                    + detailMethod.getStatusCode() + "\n" + detailMethod.getResponseBodyAsString());
                        }
                    }
                }
            }
            else
            {
                logger.error("Error querying CoNE: Status "
                        + method.getStatusCode() + "\n" + method.getResponseBodyAsString());
            }
            return document;
        }
        catch (Exception e)
        {
            logger.error("Error querying CoNE service. This is normal during unit tests. " +
                    "Otherwise it should be clarified if any measures have to be taken.");
            return null;
            //throw new RuntimeException(e);
        }
    }
    
    public static Node queryConeExact(String model, String name, String ou)
    {
        DocumentBuilder documentBuilder;

        try
        {
            documentBuilder = DocumentBuilderFactoryImpl.newInstance().newDocumentBuilder();
        
            Document document = documentBuilder.newDocument();
            Element element = document.createElement("cone");
            document.appendChild(element);
            
            String queryUrl = PropertyReader.getProperty("escidoc.cone.service.url")
                 + model + "/query?format=jquery&dc:title=\"" + URLEncoder.encode(name, "ISO-8859-15")
                 + "\"&escidoc:position/eprints:affiliatedInstitution=" + URLEncoder.encode(ou, "ISO-8859-15");
            String detailsUrl = PropertyReader.getProperty("escidoc.cone.service.url")
                + model + "/resource/$1?format=rdf";
            HttpClient client = new HttpClient();
            GetMethod method = new GetMethod(queryUrl);
            ProxyHelper.executeMethod(client, method);
            logger.info("CoNE query: " + queryUrl + " returned " + method.getResponseBodyAsString());
            if (method.getStatusCode() == 200)
            {
                ArrayList<String> results = new ArrayList<String>();
                results.addAll(Arrays.asList(method.getResponseBodyAsString().split("\n")));
                queryUrl = PropertyReader.getProperty("escidoc.cone.service.url")
                    + model + "/query?format=jquery&dcterms:alternative=\"" + URLEncoder.encode(name, "ISO-8859-15")
                    + "\"&escidoc:position/eprints:affiliatedInstitution=" + URLEncoder.encode(ou, "ISO-8859-15");
                client = new HttpClient();
                method = new GetMethod(queryUrl);
                ProxyHelper.executeMethod(client, method);
                logger.info("CoNE query: " + queryUrl + " returned " + method.getResponseBodyAsString());
                if (method.getStatusCode() == 200)
                {
                    results.addAll(Arrays.asList(method.getResponseBodyAsString().split("\n")));
                    for (String result : results)
                    {
                        if (!"".equals(result.trim()))
                        {
                            String id = result.split("\\|")[1];
                            GetMethod detailMethod = new GetMethod(id + "?format=rdf&eSciDocUserHandle="  + Base64.encode(AdminHelper.getAdminUserHandle().getBytes("UTF-8")));

                            ProxyHelper.setProxy(client, detailsUrl.replace("$1", id));
                            client.executeMethod(detailMethod);
                            logger.info("CoNE query: " + id + "?format=rdf&eSciDocUserHandle="  + Base64.encode(AdminHelper.getAdminUserHandle().getBytes("UTF-8")) + " returned " + detailMethod.getResponseBodyAsString());
                            if (detailMethod.getStatusCode() == 200)
                            {
                                Document details = documentBuilder.parse(detailMethod.getResponseBodyAsStream());
                                element.appendChild(document.importNode(details.getFirstChild(), true));
    
                            }
                            else
                            {
                                logger.error("Error querying CoNE: Status "
                                        + detailMethod.getStatusCode() + "\n" + detailMethod.getResponseBodyAsString());
                            }
                        }
                    }
                }
            }
            else
            {
                logger.error("Error querying CoNE: Status "
                        + method.getStatusCode() + "\n" + method.getResponseBodyAsString());
            }
            return document;
        }
        catch (Exception e)
        {
            logger.error("Error querying CoNE service. This is normal during unit tests. " +
                    "Otherwise it should be clarified if any measures have to be taken.");
            return null;
            //throw new RuntimeException(e);
        }
    }
    
    /**
     * Queries CoNE service and returns the result as DOM node.
     * The returned XML has the following structure:
     * <cone>
     *   <author>
     *     <familyname>Buxtehude-Mölln</familyname>
     *     <givenname>Heribert</givenname>
     *     <prefix>von und zu</prefix>
     *     <title>König</title>
     *   </author>
     *   <author>
     *     <familyname>Müller</familyname>
     *     <givenname>Peter</givenname>
     *   </author>
     * </authors>
     * 
     * @param Single instituteId for an institute without departments or list of Ids. Every department has his own Id.
     * @return 
     */
	public static Node queryReportPersonCone(String model, String query) {
		DocumentBuilder documentBuilder;
		String queryUrl;
		List<String> childIds = new ArrayList<String>();
		// get the childOUs if any in the query
		if (query.contains(" ")) {
			String[] result = query.split("\\s+");
			for (String s : result) {
				childIds.add(s);
			}
		}

		try {
			documentBuilder = DocumentBuilderFactoryImpl.newInstance()
					.newDocumentBuilder();

			Document document = documentBuilder.newDocument();
			Element element = document.createElement("cone");
			document.appendChild(element);

			queryUrl = PropertyReader.getProperty("escidoc.cone.service.url")
					+ model
					+ "/query?format=jquery&escidoc:position/dc:identifier=\""
					+ query + "\"&n=0";

			HttpClient client = new HttpClient();
			if (childIds.size() > 0) {
				// execute a method for every child ou
				for (String childId : childIds) {
					queryUrl = PropertyReader.getProperty("escidoc.cone.service.url") 
							+ model
							+ "/query?format=jquery&escidoc:position/dc:identifier=\""
							+ childId + "\"&n=0";
					executeGetMethod(client, queryUrl, documentBuilder, document, element);
				}
			} else {
				// there are no child ous, methid is called once
				executeGetMethod(client, queryUrl, documentBuilder, document, element);
			}

			return document;
		} catch (Exception e) {
			logger
					.error("Error querying CoNE service. This is normal during unit tests. "
							+ "Otherwise it should be clarified if any measures have to be taken.");
			return null;
		}
	}
	
	/**
     * Execute the GET-method.
     * @param client
     * @param queryUrl
     * @param documentBuilder
     * @param document
     * @param element
     * @return true if the array contains the format object, else false
     */
	private static void executeGetMethod(HttpClient client, String queryUrl,
			DocumentBuilder documentBuilder, Document document, Element element) {
		String previousUrl = null;
		try {
			GetMethod method = new GetMethod(queryUrl);
			ProxyHelper.executeMethod(client, method);
			logger.info("queryURL from executeGetMethod  " + queryUrl);

			if (method.getStatusCode() == 200) {
				String[] results = method.getResponseBodyAsString().split("\n");
				for (String result : results) {
					if (!"".equals(result.trim())) {
						String detailsUrl = result.split("\\|")[1];
						// if there is an alternative name, take only the first occurrence
						if (!detailsUrl.equalsIgnoreCase(previousUrl)){
							GetMethod detailMethod = new GetMethod(detailsUrl + "?format=rdf");
							previousUrl = detailsUrl;
						
							logger.info(detailMethod.getPath());
							logger.info(detailMethod.getQueryString());
                        
							ProxyHelper.setProxy(client, detailsUrl);
							client.executeMethod(detailMethod);
							if (detailMethod.getStatusCode() == 200)
							{
								Document details = documentBuilder.parse(detailMethod.getResponseBodyAsStream());
								element.appendChild(document.importNode(details.getFirstChild(), true));
							}
							else
							{
								logger.error("Error querying CoNE: Status "
										+ detailMethod.getStatusCode() + "\n" + detailMethod.getResponseBodyAsString());
							}
						}
					}
				}

			} else {
				logger.error("Error querying CoNE: Status "
						+ method.getStatusCode() + "\n"
						+ method.getResponseBodyAsString());
			}
		} catch (Exception e) {
			logger
					.error("Error querying CoNE service. This is normal during unit tests. "
							+ "Otherwise it should be clarified if any measures have to be taken.");
		}
	}
    
	public static Node querySSRNId(String conePersonUrl){
		DocumentBuilder documentBuilder;
		HttpClient client = new HttpClient();
		try {
			documentBuilder = DocumentBuilderFactoryImpl.newInstance()
			.newDocumentBuilder();

			Document document = documentBuilder.newDocument();
			Element element = document.createElement("cone");
			document.appendChild(element);
			GetMethod detailMethod = new GetMethod(conePersonUrl + "?format=rdf");
			ProxyHelper.setProxy(client, conePersonUrl);
			client.executeMethod(detailMethod);
			if (detailMethod.getStatusCode() == 200)
			{
				Document details = documentBuilder.parse(detailMethod.getResponseBodyAsStream());
				element.appendChild(document.importNode(details.getFirstChild(), true));
				return document;
			}
			else
			{
				logger.error("Error querying CoNE: Status "
						+ detailMethod.getStatusCode() + "\n" + detailMethod.getResponseBodyAsString());
				return null;
			}
			
		}catch (Exception e) {
			logger
			.error("Error querying CoNE service. This is normal during unit tests. "
					+ "Otherwise it should be clarified if any measures have to be taken.");
			return null;
		}
		
	}
	
    public static Node getSize(String url)
    {
        DocumentBuilder documentBuilder;
        
        HttpClient httpClient = new HttpClient();
        HeadMethod headMethod = new HeadMethod(url);
        
        try
        {
            logger.info("Getting size of " + url);
            ProxyHelper.executeMethod(httpClient, headMethod);
            
            if (headMethod.getStatusCode() != 200)
            {
                logger.warn("Wrong status code " + headMethod.getStatusCode() + " at " + url);
            }
            
            documentBuilder = DocumentBuilderFactoryImpl.newInstance().newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element element = document.createElement("size");
            document.appendChild(element);
            Header header = headMethod.getResponseHeader("Content-Length");
            if (header != null)
            {
                element.setTextContent(header.getValue());
                return document;
            }
            else
            {
                element.setTextContent("0");
                return document;
            }
            
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static String getMimetype(String suffix)
    {
        try
        {
            String queryUrl = PropertyReader.getProperty("escidoc.cone.service.url")
            + "jquery/escidocmimetypes/query?q=" + URLEncoder.encode(suffix, "ISO-8859-15");
            String detailsUrl = PropertyReader.getProperty("escidoc.cone.service.url")
                + "json/escidocmimetypes/details/";
            HttpClient client = new HttpClient();
            GetMethod method = new GetMethod(queryUrl);
            ProxyHelper.executeMethod(client, method);
            if (method.getStatusCode() == 200)
            {
                String[] results = method.getResponseBodyAsString().split("\n");
                for (String result : results)
                {
                    if (!"".equals(result.trim()))
                    {
                        String id = result.split("\\|")[1];
                        GetMethod detailMethod = new GetMethod(detailsUrl + id);
                        ProxyHelper.executeMethod(client, detailMethod);
                        if (detailMethod.getStatusCode() == 200)
                        {
                            String response = detailMethod.getResponseBodyAsString();
                            Pattern pattern = Pattern.compile("\"urn_cone_suffix\" : \"([^\"])\"");
                            Matcher matcher = pattern.matcher(response);
                            if (matcher.find())
                            {
                                pattern = Pattern.compile("\"http_purl_org_dc_elements_1_1_title\" : \"([^\"])\"");
                                matcher = pattern.matcher(response);
                                if (matcher.find())
                                {
                                    return matcher.group(1);
                                }
                                else
                                {
                                    logger.warn("Found matching mimetype suffix but no mimetype: " + response);
                                }
                            }
                        }
                        else
                        {
                            logger.error("Error querying CoNE: Status "
                                    + detailMethod.getStatusCode() + "\n" + detailMethod.getResponseBodyAsString());
                        }
                    }
                }
                // Suffix not found, return default mimetype
                return "application/octet-stream";
            }
            else
            {
                logger.error("Error querying CoNE: Status "
                        + method.getStatusCode() + "\n" + method.getResponseBodyAsString());
            }
        }
        catch (Exception e)
        {
            logger.error("Error getting mimetype", e);
        }
     // Error querying CoNE, return default mimetype
        return "application/octet-stream";
    } 
    
    /**
     * This methods reads out the style information from the format name.
     * @param type
     * @return type of style (APA | AJP | Default)
     */
    public Styles getStyleInfo(Format format)
    {
        if (format.getName().toLowerCase().contains("apa"))
        {
            return Styles.APA;
        }
        if (format.getName().toLowerCase().contains("ajp"))
        {
            return Styles.AJP;
        }
        else return Styles.Default;
    }
    
    public static void log(String str)
    {
        System.out.println(str);
    }
}
