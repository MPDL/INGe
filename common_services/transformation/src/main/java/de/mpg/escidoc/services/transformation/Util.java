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
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.transformation;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Only for DOM Debugging
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import javax.xml.parsers.DocumentBuilder;

import net.sf.saxon.dom.DocumentBuilderFactoryImpl;

import org.apache.axis.encoding.Base64;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.log4j.Logger;
import org.apache.tika.Tika;
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
    private static long coneSessionTimestamp = 0;
    private static String coneSession = null;
    
    /**
     * Hide constructor of static class
     */
    private Util()
    {
        
    }
    
    // Jasper styles enum
    public static enum Styles { APA, AJP, Default }; 
    
    /**
     * Converts a simpleLiteral Objects into a String Object.
     * @param sl as SimpleLiteral
     * @return String
     */
    public static String simpleLiteralTostring(SimpleLiteral sl)
    {
        return sl.toString().substring(sl.toString().indexOf(">") + 1, sl.toString().lastIndexOf("<"));
    }
    
    /**
     * Creates a format xml out of a format array.
     * @param formats as Format[]
     * @return xml as String
     */
    public static String createFormatsXml(Format[] formats)
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
    public static boolean isFormatEqual(Format src1, Format src2)
    {   
        if (src1 == null || src2 == null)
        {
            return false;
        }
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
     * Converts a Format Vector into a Format Array.
     * @param formatsV as Vector
     * @return Format[]
     */
    public static Format[] formatVectorToFormatArray(Vector<Format> formatsV)
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
    public static Vector<Format> getRidOfDuplicatesInVector(Vector<Format> dirtyVector)
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
                if (isFormatEqual(format1, format2))
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
    public static boolean containsFormat(Format[] formatArray, Format format)
    {
        if (formatArray == null || format == null)
        {
            return false;
        }
        for (int i = 0; i < formatArray.length; i++) 
        {
            Format tmp = formatArray[i];
            if (isFormatEqual(format, tmp))
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
    public static Format[] mergeFormats(Vector<Format[]> allFormatsV)
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
        tmpV = getRidOfDuplicatesInVector(tmpV);
        return formatVectorToFormatArray(tmpV);
    }
    
    /**
     * Normalizes a given mimetype.
     * @param mimetype
     * @return
     */
    public static String normalizeMimeType(String mimetype)
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
        String queryUrl = null;
        try
        {
            System.out.println("queryCone: " + model);
            
            documentBuilder = DocumentBuilderFactoryImpl.newInstance().newDocumentBuilder();
        
            Document document = documentBuilder.newDocument();
            Element element = document.createElement("cone");
            document.appendChild(element);
            
            queryUrl = PropertyReader.getProperty("escidoc.cone.service.url")
                 + model + "/query?format=jquery&q=" + URLEncoder.encode(query, "UTF-8");
            String detailsUrl = PropertyReader.getProperty("escidoc.cone.service.url")
                + model + "/resource/$1?format=rdf";
            HttpClient client = new HttpClient();
            GetMethod method = new GetMethod(queryUrl);
            
            String coneSession = getConeSession();
            
            if (coneSession != null)
            {
                method.setRequestHeader("Cookie", "JSESSIONID=" + coneSession);
            }
            ProxyHelper.executeMethod(client, method);
            
            if (method.getStatusCode() == 200)
            {
                String[] results = method.getResponseBodyAsString().split("\n");
                for (String result : results)
                {
                    if (!"".equals(result.trim()))
                    {
                        String id = result.split("\\|")[1];
                        // TODO "&redirect=true" must be reinserted again
                        GetMethod detailMethod = new GetMethod(id + "?format=rdf&eSciDocUserHandle=" + Base64.encode(AdminHelper.getAdminUserHandle().getBytes("UTF-8")));
                        detailMethod.setFollowRedirects(true);
                        
                        
                        if (coneSession != null)
                        {
                            detailMethod.setRequestHeader("Cookie", "JSESSIONID=" + coneSession);
                        }
                        ProxyHelper.executeMethod(client, detailMethod);
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
            else
            {
                logger.error("Error querying CoNE: Status "
                        + method.getStatusCode() + "\n" + method.getResponseBodyAsString());
            }
            
            return document;
        }
        catch (Exception e)
        {
            logger.error("Error querying CoNE service. This is normal during unit tests. (" + queryUrl +
            		") .Otherwise it should be clarified if any measures have to be taken.", e);
            logger.debug("Stacktrace", e);
            return null;
            //throw new RuntimeException(e);
        }
    }

    public static List<String> queryConeForJava(String model, String query)
    {

        List<String> returnSet = new ArrayList<String>();
        String queryUrl = null;
        try
        {
            System.out.println("queryCone: " + model);
            
            queryUrl = PropertyReader.getProperty("escidoc.cone.service.url")
                 + model + "/query?format=jquery&q=" + URLEncoder.encode(query, "UTF-8");
            HttpClient client = new HttpClient();
            GetMethod method = new GetMethod(queryUrl);
            
            String coneSession = getConeSession();
            
            if (coneSession != null)
            {
                method.setRequestHeader("Cookie", "JSESSIONID=" + coneSession);
            }
            ProxyHelper.executeMethod(client, method);
            
            if (method.getStatusCode() == 200)
            {
                String[] results = method.getResponseBodyAsString().split("\n");
                for (String result : results)
                {
                    if (!"".equals(result.trim()))
                    {
                        String nextId = result.split("\\|")[1];
                        if (!returnSet.contains(nextId))
                        {
                            returnSet.add(nextId);
                        }
                    }
                }
            }
            else
            {
                logger.error("Error querying CoNE: Status "
                        + method.getStatusCode() + "\n" + method.getResponseBodyAsString());
            }
            
            return returnSet;
        }
        catch (Exception e)
        {
            logger.error("Error querying CoNE service. This is normal during unit tests. (" + queryUrl +
                    ") .Otherwise it should be clarified if any measures have to be taken.", e);
            logger.debug("Stacktrace", e);
            return null;
            //throw new RuntimeException(e);
        }
    }

    public static String getConeSession() throws Exception
    {
        long now = new Date().getTime();
        if (coneSession == null || (now - coneSessionTimestamp) > 1000 * 60 * 30)
        {
            String queryUrl = PropertyReader.getProperty("escidoc.cone.service.url");
            HttpClient client = new HttpClient();
            GetMethod method = new GetMethod(queryUrl);
            try
            {
                client.executeMethod(method);
            }
            catch (Exception e) {
                logger.warn("Error while retrieving CoNE session", e);
                return null;
            }
            Header[] cookies = method.getResponseHeaders("Set-Cookie");
            if (cookies != null && cookies.length > 0)
            {
                for (Header cookie : cookies)
                {
                    if (cookie.getValue().startsWith("JSESSIONID="))
                    {
                        int end = cookie.getValue().indexOf(";", 11);
                        if (end >= 0)
                        {
                            coneSession = cookie.getValue().substring(11, end);
                            coneSessionTimestamp = now;
                            logger.info("Refreshing CoNE session: " + coneSession);
                            return coneSession;
                        }
                    }
                }
            }
            return null;
        }
        else
        {
            return coneSession;
        }
    }

    /**
     * Queries the CoNE service and transforms the result into a DOM node.
     * 
     * @param model The type of object (e.g. "persons")
     * @param name The query string.
     * @param ou Specialty for persons
     * @param coneSession A JSESSIONID to not produce a new session with each call.
     * @return A DOM node containing the results.
     */
    public static Node queryConeExact(String model, String name, String ou)
    {
        DocumentBuilder documentBuilder;

        try
        {
            System.out.println("queryConeExact: " + model);
            
            documentBuilder = DocumentBuilderFactoryImpl.newInstance().newDocumentBuilder();
        
            Document document = documentBuilder.newDocument();
            Element element = document.createElement("cone");
            document.appendChild(element);
            
            String queryUrl = PropertyReader.getProperty("escidoc.cone.service.url")
                 + model + "/query?format=jquery&dc:title=\"" + URLEncoder.encode(name, "UTF-8")
                 + "\"&escidoc:position/eprints:affiliatedInstitution=" + URLEncoder.encode(ou, "UTF-8") + "";
            String detailsUrl = PropertyReader.getProperty("escidoc.cone.service.url")
                + model + "/resource/$1?format=rdf";
            HttpClient client = new HttpClient();
            client.getParams().setParameter(HttpClientParams.ALLOW_CIRCULAR_REDIRECTS, true);
            GetMethod method = new GetMethod(queryUrl);
            
            String coneSession = getConeSession();
            
            if (coneSession != null)
            {
                method.setRequestHeader("Cookie", "JSESSIONID=" + coneSession);
            }
            ProxyHelper.executeMethod(client, method);
            logger.info("CoNE query: " + queryUrl + " returned " + method.getResponseBodyAsString());
            if (method.getStatusCode() == 200)
            {
                ArrayList<String> results = new ArrayList<String>();
                results.addAll(Arrays.asList(method.getResponseBodyAsString().split("\n")));
                queryUrl = PropertyReader.getProperty("escidoc.cone.service.url")
                    + model + "/query?format=jquery&dcterms:alternative=\"" + URLEncoder.encode(name, "UTF-8")
                    + "\"&escidoc:position/eprints:affiliatedInstitution=" + URLEncoder.encode(ou, "UTF-8");
                client = new HttpClient();
                method = new GetMethod(queryUrl);
                if (coneSession != null)
                {
                    method.setRequestHeader("Cookie", "JSESSIONID=" + coneSession);
                }
                ProxyHelper.executeMethod(client, method);
                logger.info("CoNE query: " + queryUrl + " returned " + method.getResponseBodyAsString());
                if (method.getStatusCode() == 200)
                {
                    results.addAll(Arrays.asList(method.getResponseBodyAsString().split("\n")));
                    Set<String> oldIds = new HashSet<String>();
                    for (String result : results)
                    {
                        if (!"".equals(result.trim()))
                        {
                            String id = result.split("\\|")[1];
                            if (!oldIds.contains(id))
                            {
                                // TODO "&redirect=true" must be reinserted again
                                GetMethod detailMethod = new GetMethod(id + "?format=rdf&eSciDocUserHandle="  + Base64.encode(AdminHelper.getAdminUserHandle().getBytes("UTF-8")));
                                detailMethod.setFollowRedirects(true);
                                
                                ProxyHelper.setProxy(client, detailsUrl.replace("$1", id));
                                client.executeMethod(detailMethod);
                                // TODO "&redirect=true" must be reinserted again
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
                                oldIds.add(id);
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
                    "Otherwise it should be clarified if any measures have to be taken.", e);
            return null;
            //throw new RuntimeException(e);
        }
    }
    
    
    /**
     * Queries the CoNE service and transforms the result into a DOM node.
     * 
     * @param model The type of object (e.g. "persons")
     * @param name The query string.
     * @param ou Specialty for persons
     * @param coneSession A JSESSIONID to not produce a new session with each call.
     * @return A DOM node containing the results.
     */
    public static Node queryConeExactWithIdentifier (String model, String identifier, String ou)
    {
        DocumentBuilder documentBuilder;
        
        try
        {
            System.out.println("queryConeExact: " + model);
            
            documentBuilder = DocumentBuilderFactoryImpl.newInstance().newDocumentBuilder();
            
            Document document = documentBuilder.newDocument();
            Element element = document.createElement("cone");
            document.appendChild(element);
            
            String queryUrl = PropertyReader.getProperty("escidoc.cone.service.url")
                    + model + "/query?format=jquery&dc:identifier/rdf:value=\"" + URLEncoder.encode(identifier, "UTF-8")
                    + "\"&escidoc:position/eprints:affiliatedInstitution=" + URLEncoder.encode(ou, "UTF-8") + "";
            String detailsUrl = PropertyReader.getProperty("escidoc.cone.service.url")
                    + model + "/resource/$1?format=rdf";
            HttpClient client = new HttpClient();
            client.getParams().setParameter(HttpClientParams.ALLOW_CIRCULAR_REDIRECTS, true);
            GetMethod method = new GetMethod(queryUrl);
            
            String coneSession = getConeSession();
            
            if (coneSession != null)
            {
                method.setRequestHeader("Cookie", "JSESSIONID=" + coneSession);
            }
            ProxyHelper.executeMethod(client, method);
            logger.info("CoNE query: " + queryUrl + " returned " + method.getResponseBodyAsString());
            if (method.getStatusCode() == 200)
            {
                ArrayList<String> results = new ArrayList<String>();
                results.addAll(Arrays.asList(method.getResponseBodyAsString().split("\n")));
                Set<String> oldIds = new HashSet<String>();
                for (String result : results)
                {
                    if (!"".equals(result.trim()))
                    {
                        String id = result.split("\\|")[1];
                        if (!oldIds.contains(id))
                        {
                            // TODO "&redirect=true" must be reinserted again
                            GetMethod detailMethod = new GetMethod(id + "?format=rdf&eSciDocUserHandle="  + Base64.encode(AdminHelper.getAdminUserHandle().getBytes("UTF-8")));
                            detailMethod.setFollowRedirects(true);
                            
                            ProxyHelper.setProxy(client, detailsUrl.replace("$1", id));
                            client.executeMethod(detailMethod);
                            // TODO "&redirect=true" must be reinserted again
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
                            oldIds.add(id);
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
    
    public static String getMimetype(String filename)
    {
    	/*
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
        */
    	
    	
    	try {
			Tika tika = new Tika();
			String mimetype = tika.detect(filename);
			return mimetype;
		} catch (Exception e) {
			logger.error("Error while detecting mimetype of filename: " + filename, e);
		}
    	
     // Error querying CoNE, return default mimetype
        return "application/octet-stream";
    } 
    
    /**
     * This methods reads out the style information from the format name.
     * @param type
     * @return type of style (APA | AJP | Default)
     */
    public static Styles getStyleInfo(Format format)
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
    
    /**
     *  Command line transformation.
     *  
     * @param see usage
     * @throws Exception Any exception
     */
    public static void main(String[] args) throws Exception
    {
        if (args.length < 7)
        {
            System.out.println("Usage: ");
            System.out.println("Util <srcFile> <srcFormatName> <srcFormatType> <srcFormatEncoding> <trgFormatName> <trgFormatType> <trgFormatEncoding> [<trgFile> [<service>]]");
            System.out.println("Example:");
            System.out.println("Util /tmp/source.xml eDoc application/xml UTF-8 eSciDoc application/xml UTF-8 /tmp/target.xml");
        }
        else
        {
            // Init
            Transformation transformation = new TransformationBean(true);
            Format srcFormat = new Format(args[1], args[2], args[3]);
            Format trgFormat = new Format(args[4], args[5], args[6]);
            File srcFile = new File(args[0]);
            OutputStream trgStream;
            if (args.length > 7)
            {
                trgStream = new FileOutputStream(new File(args[7]));
            }
            else
            {
                trgStream = System.out;
            }
            String service = "eSciDoc";
            if (args.length > 8)
            {
                service = args[8];
            }
            
            // Get file content
            FileInputStream inputStream = new FileInputStream(srcFile);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048];
            int read;
            while ((read = inputStream.read(buffer)) != -1)
            {
                outputStream.write(buffer, 0, read);
            }
            byte[] content = outputStream.toByteArray();
            
            // Transform
            byte[] result = transformation.transform(content, srcFormat, trgFormat, service);
            
            // Stream back
            trgStream.write(result);
            trgStream.close();
            
        }
        
    }

}
