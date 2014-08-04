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
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.OAIInternalServerError;


/**
 * This class represents a ListMetadataFormats verb on either
 * the client or on the server.
 *
 * @author Jeffrey A. Young, OCLC Online Computer Library Center
 * @author Friederike Kleinfercher, MPDL
 */
public class ListMetadataFormats extends ServerVerb 
{
    private static ArrayList validParamNames = new ArrayList();
    private static ArrayList requiredParamNames = new ArrayList();
    private static final Logger LOGGER = Logger.getLogger(ListMetadataFormats.class);
    
    static 
    {
        validParamNames.add("verb");
        validParamNames.add("identifier");
    }

    static 
    {
        requiredParamNames.add("verb");
    }

    /**
     * Server-side construction of the xml response
     *
     * @param context the servlet context
     * @param request the servlet request
     * @exception OAIBadRequestException an http 400 status code problem
     * @exception OAINotFoundException an http 404 status code problem
     * @exception OAIInternalServerError an http 500 status code problem
     */
    public static String construct(Properties properties, HttpServletRequest request, HttpServletResponse response)      
    {
    	LOGGER.debug("[FDS] ---- construct response for GetRecord verb ----");
    	
    	//Properties
        String baseURL = properties.getProperty("oai.baseURL", request.getRequestURL().toString());
        String styleSheet = properties.getProperty("oai.styleSheet");
        String nativeFormatName = properties.getProperty("Repository.nativeFormat.Name", "undefined");
        String nativeFormatSchema = properties.getProperty("Repository.nativeFormat.Schema", "undefined");
        String nativeFormatNs = properties.getProperty("Repository.nativeFormat.ns", "undefined");
        
        //Parameters
        String identifier = request.getParameter("identifier");
        
        //Variables
        StringBuffer sb = new StringBuffer();
        String oaiFormatName = "oai_dc";
        String oaiFormatSchema ="http://www.openarchives.org/OAI/2.0/oai_dc.xsd";
        String oaiFormatNs = "http://www.openarchives.org/OAI/2.0/oai_dc/";
        
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        
        if (styleSheet != null) 
        {
            sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"" + styleSheet + "\"?>");
        }
        
        sb.append("<OAI-PMH xmlns=\"http://www.openarchives.org/OAI/2.0/\"");
        sb.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        sb.append(" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/");
        sb.append(" http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\">");
        sb.append("<responseDate>" + createResponseDate(new Date()) + "</responseDate>");

        sb.append(getRequestElement(request, validParamNames, baseURL));
        if (hasBadArguments(request, requiredParamNames.iterator(),
                validParamNames)) {
            sb.append(new BadArgumentException().getMessage());
        } 
        else 
        {
        	//Native format
        	sb.append("<ListMetadataFormats>");             
                sb.append("<metadataFormat>");
                	sb.append("<metadataPrefix>" + nativeFormatName + "</metadataPrefix>");
                	sb.append("<schema>" + nativeFormatSchema + "</schema>");
                	sb.append("<metadataNamespace>" + nativeFormatNs + "</metadataNamespace>");
                sb.append("</metadataFormat>");
             sb.append("</ListMetadataFormats>");
         	//oai_dc format
         	sb.append("<ListMetadataFormats>");             
                 sb.append("<metadataFormat>");
                 	sb.append("<metadataPrefix>" + oaiFormatName + "</metadataPrefix>");
                 	sb.append("<schema>" + oaiFormatSchema + "</schema>");
                 	sb.append("<metadataNamespace>" + oaiFormatNs + "</metadataNamespace>");
                 sb.append("</metadataFormat>");
              sb.append("</ListMetadataFormats>");     
        }
        sb.append("</OAI-PMH>");
        response.setContentType("text/xml; charset=UTF-8");
        
        return sb.toString();
    }
}
