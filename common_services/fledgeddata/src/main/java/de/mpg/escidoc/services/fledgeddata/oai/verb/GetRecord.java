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

import de.mpg.escidoc.services.fledgeddata.oai.OAIUtil;
import de.mpg.escidoc.services.fledgeddata.oai.oaiCatalog;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.BadArgumentException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.CannotDisseminateFormatException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.IdDoesNotExistException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.OAIInternalServerError;

/**
 * This class represents a GetRecord response on either the server or
 * the client.
 *
 * @author Jeffrey A. Young, OCLC Online Computer Library Center
 * @author Friederike Kleinfercher, MPDL
 */
public class GetRecord extends ServerVerb 
{
    private static ArrayList validParamNames = new ArrayList();
    private static final Logger LOGGER = Logger.getLogger(GetRecord.class);
    
    static 
    {
		validParamNames.add("verb");
		validParamNames.add("identifier");
		validParamNames.add("metadataPrefix");
    }
    
    /**
     * Construct the xml response on the server-side.
     *
     * @param properties
     * @param request the servlet request
     * @return a String containing the XML response
     * @exception OAIBadRequestException an http 400 status error occurred
     * @exception OAINotFoundException an http 404 status error occurred
     * @exception OAIInternalServerError an http 500 status error occurred
     */
    public static String construct(Properties properties, HttpServletRequest request, HttpServletResponse response)      
    {
    	LOGGER.debug("[FDS] ---- construct response for GetRecord verb ----");
    	
    	//Properties
        String baseURL = properties.getProperty("oai.baseURL", baseURL = request.getRequestURL().toString());
        String styleSheet = properties.getProperty("oai.styleSheet");
        String extraXmlns = properties.getProperty("oai.extraXmlns");
        
        //Variables
        StringBuffer sb = new StringBuffer();
        boolean harvestable = OAIUtil.isHarvestable(properties);
        
        //Parameters
        String identifier = request.getParameter("identifier");
        String metadataPrefix = request.getParameter("metadataPrefix");
        
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
        
        try 
        {
		    if (metadataPrefix == null || metadataPrefix.length() == 0
		    		|| identifier == null || identifier.length() == 0
		    		|| hasBadArguments(request, validParamNames.iterator(), validParamNames)) 
		    {
		    	throw new BadArgumentException();
		    }
		    else
		    {
				if (! harvestable) 
				{         
				    sb.append("<request verb=\"GetRecord\">" + baseURL + "</request>");
				    sb.append("<error code=\"badArgument\">Database is unavailable for harvesting</error>");
				}
				else 
				{		        
					String record = oaiCatalog.getRecord(identifier, metadataPrefix, properties);
					if (record != null) 
					{
					    sb.append(getRequestElement(request, validParamNames, baseURL));
					    sb.append("<GetRecord>" + record + "</GetRecord>");
					} 
					else 
					{
					    throw new IdDoesNotExistException(identifier);
					}
				}
		    }
        } catch (BadArgumentException e) {
		    sb.append("<request verb=\"GetRecord\">");
		    sb.append(baseURL);
		    sb.append("</request>");
		    sb.append(e.getMessage());
		} catch (CannotDisseminateFormatException e) {
		    sb.append(getRequestElement(request, validParamNames, baseURL));
		    sb.append(e.getMessage());
		} catch (IdDoesNotExistException e) {
		    sb.append(getRequestElement(request, validParamNames, baseURL));
		    sb.append(e.getMessage());
		} catch (OAIInternalServerError e) {
		    sb.append(getRequestElement(request, validParamNames, baseURL));
		    sb.append(e.getMessage());
			e.printStackTrace();
		}
        sb.append("</OAI-PMH>");
        response.setContentType("text/xml; charset=UTF-8");
        
        return sb.toString();
    }
}
