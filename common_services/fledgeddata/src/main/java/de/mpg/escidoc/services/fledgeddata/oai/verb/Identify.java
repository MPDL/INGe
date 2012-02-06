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
package de.mpg.escidoc.services.fledgeddata.oai.verb;

import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.fledgeddata.oai.exceptions.BadArgumentException;


/**
 * This class represents an Identify response on either the server or
 * on the client
 *
 * @author Jeffrey A. Young, OCLC Online Computer Library Center
 * @author Friederike Kleinfercher, MPDL
 */
public class Identify extends ServerVerb 
{	
    private static ArrayList validParamNames = new ArrayList();
    private static final Logger LOGGER = Logger.getLogger(Identify.class);
    
    static 
    {
        validParamNames.add("verb");
    }
    
    /**
     * Construct the xml response on the server side to the Identify request of a client.
     *
     * @param properties
     * @param request the servlet request
     * @return a String containing the xml response
     */
    public static String construct(Properties properties, HttpServletRequest request, HttpServletResponse response)    		
    {
    	
    	LOGGER.debug("[FDS] ---- construct response for Identify verb ----");
    	// Properties
        String baseURL = properties.getProperty("oai.baseURL",baseURL = request.getRequestURL().toString());
        String styleSheet = properties.getProperty("oai.styleSheet");
        String description = properties.getProperty("oai.description","undefined");
        String responseDate = createResponseDate(new Date());
        String repositoryName = properties.getProperty("Identify.repositoryName", "undefined");
        String adminMail = properties.getProperty("Identify.adminEmail", "undefined");
        String earliestDateStamp = properties.getProperty("Identify.earliestDatestamp", "undefined");
        String deletedRecords = properties.getProperty("Identify.deletedRecord", "undefined");
        //String granularity = properties.getProperty("AbstractCatalog.granularity");
        String repositoryIdentifier = properties.getProperty("oai.repositoryIdentifier", "undefined");
        String sampleIdentifier = properties.getProperty("oai.sampleIdentifier", "undefined");
        
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        
        if (styleSheet != null) 
        {
            sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"");
            sb.append(styleSheet);
            sb.append("\"?>");
        }
        
        sb.append("<OAI-PMH xmlns=\"http://www.openarchives.org/OAI/2.0/\"");
        sb.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        sb.append(" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/");
        sb.append(" http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\">");
        sb.append("<responseDate>" + responseDate + "</responseDate>");        
        sb.append(getRequestElement(request, validParamNames, baseURL));
        
        if (hasBadArguments(request, validParamNames.iterator(), validParamNames)) 
        {
            sb.append(new BadArgumentException().getMessage());
        } 
        else 
        {
            sb.append("<Identify>");
            	sb.append("<repositoryName>" + repositoryName + "</repositoryName>");
            	sb.append("<baseURL>" + baseURL + "</baseURL>");
            	sb.append("<protocolVersion>2.0</protocolVersion>");
            	sb.append("<adminEmail>" + adminMail + "</adminEmail>");
            	sb.append("<earliestDatestamp>" + earliestDateStamp + "</earliestDatestamp>");
            	sb.append("<deletedRecord>" + deletedRecords + "</deletedRecord>");
            	//sb.append("<granularity>" + granularity + "</granularity>");
            	//sb.append("<compression>gzip</compression>");
            	//sb.append("<compression>deflate</compression>");
            	sb.append("<description>");
            		sb.append("<oai-identifier xmlns=\"http://www.openarchives.org/OAI/2.0/oai-identifier\"");
            			sb.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
            			sb.append(" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/oai-identifier http://www.openarchives.org/OAI/2.0/oai-identifier.xsd\">");
            			sb.append("<scheme>oai</scheme>");
            			sb.append("<repositoryIdentifier>" + repositoryIdentifier + "</repositoryIdentifier>");
            			sb.append("<delimiter>:</delimiter>");
            			sb.append("<sampleIdentifier>" + sampleIdentifier + "</sampleIdentifier>");
            		sb.append("</oai-identifier>");
            	sb.append("</description>");
            	sb.append("<description>" + description+ "</description>");
            sb.append("</Identify>");
        }
        sb.append("</OAI-PMH>");
        response.setContentType("text/xml; charset=UTF-8");
        
        return sb.toString();
    }
}
