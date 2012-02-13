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
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.fledgeddata.oai.OAIUtil;
import de.mpg.escidoc.services.fledgeddata.oai.oaiCatalog;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.BadArgumentException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.BadResumptionTokenException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.CannotDisseminateFormatException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.IdDoesNotExistException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.NoItemsMatchException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.NoMetadataFormatsException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.NoRecordsMatchException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.NoSetHierarchyException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.OAIInternalServerError;

/**
 * This class represents a ListIdentifiers verb on either the server or
 * on the client side.
 *
 * @author Jeffrey A. Young, OCLC Online Computer Library Center
 * @author Friederike Kleinfercher, MPDL
 */
public class ListIdentifiers extends ServerVerb 
{
	
    private static ArrayList validParamNames1 = new ArrayList();  
    private static ArrayList validParamNames2 = new ArrayList();
    private static ArrayList requiredParamNames1 = new ArrayList();
    private static ArrayList requiredParamNames2 = new ArrayList();
    private static final Logger LOGGER = Logger.getLogger(ListIdentifiers.class);
    
    static {
	validParamNames1.add("verb");
	validParamNames1.add("from");
	validParamNames1.add("until");
	validParamNames1.add("set");
	validParamNames1.add("metadataPrefix");
    }
    
    static {
	validParamNames2.add("verb");
	validParamNames2.add("resumptionToken");
    }
    
    static {
	requiredParamNames1.add("verb");
	requiredParamNames1.add("metadataPrefix");
    }
    
    static {
	requiredParamNames2.add("verb");
	requiredParamNames2.add("resumptionToken");
    }

    /**
     * http://www.openarchives.org/OAI/openarchivesprotocol.html#ListIdentifiers
     * @param properties
     * @param request
     * @param response
     * @return
     * @throws OAIInternalServerError
     * @throws TransformerException
     */
    public static String construct(Properties properties, HttpServletRequest request, HttpServletResponse response) 
    {   	
    	LOGGER.debug("[FDS] ---- construct response for ListIdentifiers verb ----");
    	
    	//Variables
		StringBuffer sb = new StringBuffer();
		boolean harvestable = OAIUtil.isHarvestable(properties);
		ArrayList validParamNames = null;
	    ArrayList requiredParamNames = null;
	    Map listIdentifiersMap = null;
	    String responseDate = createResponseDate(new Date());
	    oaiCatalog oaiCatalog = new oaiCatalog();
    	
    	//Properties
		String baseURL = properties.getProperty("oai.baseURL");
		String styleSheet = properties.getProperty("oai.styleSheet");
		
		//Parameters
		String oldResumptionToken = "";
		String from = "";
		String until = "";
		String set ="";		
		if (request != null)
		{
			//Parameters
			 oldResumptionToken = request.getParameter("resumptionToken");
			 from = request.getParameter("from");
			 until = request.getParameter("until");
			 set = request.getParameter("set");
		}
		
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");

		if (styleSheet != null) {
		    sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"");
		    sb.append(styleSheet);
		    sb.append("\"?>");
		}
		
	    sb.append("<OAI-PMH xmlns=\"http://www.openarchives.org/OAI/2.0/\"");
        sb.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        sb.append(" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/");
        sb.append(" http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\">");
        sb.append("<responseDate>" + createResponseDate(new Date()) + "</responseDate>");
	
        if (!harvestable) 
        {
        	sb.append("<request verb=\"ListIdentifiers\">");
        	sb.append(baseURL);
        	sb.append("</request>");
        	sb.append("<error code=\"badArgument\">Database is unavailable for harvesting</error>");
        } 
        else 
        {
        	//++++ Request without resumption token ++++++
		    if (oldResumptionToken == null || oldResumptionToken.equals("")) 
		    {
				validParamNames = validParamNames1;
				requiredParamNames = requiredParamNames1;

				try 
				{
				    if (from != null && from.length() > 0 && from.length() < 10) {
					throw new BadArgumentException();
				    }
				    if (until != null && until.length() > 0 && until.length() < 10) {
					throw new BadArgumentException();
				    }
				    if (from != null && until != null && from.length() != until.length()) {
					throw new BadArgumentException();
				    }
				    if (from == null || from.length() == 0) {
					from = "0001-01-01";
				    }
				    if (until == null || until.length() == 0) {
					until = "9999-12-31";
				    }
				    
				    //TODO: check from/until parameter				    
				    if (from.compareTo(until) > 0)
				    {
				    	throw new BadArgumentException();
				    }
				    
		            if (set != null)
		            {
		            	if (set.length()==0)
		            	{
		            		set=null;
		            	}
		            }
		                    
		            String record = oaiCatalog.listIdentifiers(from ,until, set, properties);
		            
					if (record != null && !record.equals("")) 
					{
						if (request != null)
						{
							sb.append(getRequestElement(request, validParamNames, baseURL));
						}
					    sb.append("<ListIdentifiers>" + record + "</ListIdentifiers>");
					} 
					else 
					{
					    throw new NoRecordsMatchException();
					}
		            
				} catch (NoItemsMatchException e) {
				    sb.append(e.getMessage());
				} catch (BadArgumentException e) {
				    sb.append("<request verb=\"ListIdentifiers\">");
				    sb.append(baseURL);
				    sb.append("</request>");
				    sb.append(e.getMessage());
				} catch (CannotDisseminateFormatException e) {
				    sb.append(getRequestElement(request, validParamNames, baseURL));
				    sb.append(e.getMessage());
				}catch (NoSetHierarchyException e) {
					sb.append(getRequestElement(request, validParamNames, baseURL));
					sb.append(e.getMessage());
				} catch (OAIInternalServerError e) {
				    sb.append(getRequestElement(request, validParamNames, baseURL));
				    sb.append(e.getMessage());
					e.printStackTrace();
				}
		    } 
		    
		    //++++ Request with resumption token ++++++
		    //Implement request token
		    else 
		    {
				validParamNames = validParamNames2;
				requiredParamNames = requiredParamNames2;
				if (hasBadArguments(request, requiredParamNames.iterator(), validParamNames)) 
				{
				    sb.append(getRequestElement(request, validParamNames, baseURL, false));
				} 
				else 
				{
				    try 
				    {
				    	listIdentifiersMap = oaiCatalog.listIdentifiers(oldResumptionToken);
				    } catch (Exception e) {
					//sb.append(getRequestElement(request, validParamNames, baseURL, xmlEncodeSetSpec));
					sb.append(e.getMessage());
				    }
				}
		    }
		    if (listIdentifiersMap != null) 
		    {
				if (hasBadArguments(request, requiredParamNames.iterator(), validParamNames)) 
				{
			    	sb.append(getRequestElement(request, validParamNames, baseURL, false));
				    sb.append(new BadArgumentException().getMessage());
				} 
				else 
				{
				    sb.append("<ListIdentifiers>\n");
				    Iterator records = (Iterator)listIdentifiersMap.get("records");
				    while (records.hasNext()) 
				    {
						sb.append((String)records.next());
						sb.append("\n");
				    }
				    Map newResumptionMap = (Map)listIdentifiersMap.get("resumptionMap");
				    if (newResumptionMap != null) 
				    {
						String newResumptionToken = (String)newResumptionMap.get("resumptionToken");
						String expirationDate = (String)newResumptionMap.get("expirationDate");
						String completeListSize = (String)newResumptionMap.get("completeListSize");
						String cursor = (String)newResumptionMap.get("cursor");
						sb.append("<resumptionToken");
						if (expirationDate != null) 
						{
						    sb.append(" expirationDate=\"" + expirationDate + "\"");
						}
						if (completeListSize != null) 
						{
						    sb.append(" completeListSize=\"" + completeListSize + "\"");
						}
						if (cursor != null) 
						{
						    sb.append(" cursor=\"" + cursor + "\"");
						}
						sb.append(">" + newResumptionToken + "</resumptionToken>");
				    } 
				    else 
				    	if (oldResumptionToken != null) 
				    	{
				    		sb.append("<resumptionToken />");
					    }
				    sb.append("</ListIdentifiers>");
				}
		    }
	}
    sb.append("</OAI-PMH>");
    return sb.toString();
	}

    
    private static void checkDate (String from, String until) throws BadArgumentException
    {
	    if (from != null && from.length() > 0 && from.length() < 10) 
	    {	throw new BadArgumentException(); }
	    if (until != null && until.length() > 0 && until.length() < 10) 
	    {throw new BadArgumentException();}
	    if (from != null && until != null && from.length() != until.length()) 
	    {throw new BadArgumentException();}
	    if (from == null || from.length() == 0) 
	    {from = "0001-01-01";}
	    if (until == null || until.length() == 0) 
	    {until = "9999-12-31";}
	    if (from.compareTo(until) > 0)
		{throw new BadArgumentException();}
	}
    
}
