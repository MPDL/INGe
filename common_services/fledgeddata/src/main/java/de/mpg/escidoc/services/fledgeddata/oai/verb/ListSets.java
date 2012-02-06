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
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.fledgeddata.FetchImeji;
import de.mpg.escidoc.services.fledgeddata.oai.OAIUtil;
import de.mpg.escidoc.services.fledgeddata.oai.oaiCatalog;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.NoSetHierarchyException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.OAIInternalServerError;

/**
 * A ListSets OAI verb representation.
 *
 * @author Jeffrey A. Young, OCLC Online Computer Library Center
 * @author Friederike Kleinfercher, MPDL
 */
public class ListSets extends ServerVerb
{
    private static ArrayList validParamNames = new ArrayList();
    private static ArrayList requiredParamNames = new ArrayList();
    private static final Logger LOGGER = Logger.getLogger(ListSets.class);
    
    static 
    {
    	validParamNames.add("verb");
    	validParamNames.add("resumptionToken");
    }
    
    static 
    {
    	validParamNames.add("verb");
    }

    /**
     * construct ListSets response
     *
     * @param context the context object from the local OAI server
     * @param request the request object from the local OAI server
     * @exception OAIInternalServerError
     */
    public static String construct(Properties properties, HttpServletRequest request, HttpServletResponse response)      		
    {
    	LOGGER.debug("[FDS] ---- construct response for ListSets verb ----");
    	
    	//Properties
        String baseURL = properties.getProperty("oai.baseURL", baseURL = request.getRequestURL().toString());
        String styleSheet = properties.getProperty("oai.styleSheet");
        String extraXmlns = properties.getProperty("oai.extraXmlns");
        
        //Variables
        StringBuffer sb = new StringBuffer();
        boolean harvestable = OAIUtil.isHarvestable(properties);
		Map <String, String>set;
		List setList = new ArrayList();

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
 
        try 
        {
			if (! harvestable) 
			{         
				   sb.append("<request verb=\"ListSets\">" + baseURL + "</request>");
				   sb.append("<error code=\"badArgument\">Database is unavailable for harvesting</error>");
			}
			else 
			{		        
				setList = oaiCatalog.listSets(properties);
				if (setList != null && setList.size() > 0) 
				{
					   sb.append(getRequestElement(request, validParamNames, baseURL));
					   sb.append("<ListSets>");
					   for (int i=0; i<setList.size(); i++)
					   {
						   set = (Map<String, String>) setList.get(i);
						   sb.append("<set>");
						   sb.append("<setSpec>" + set.get("setSpec") + "</setSpec>");
						   sb.append("<setName>" + set.get("setName") + "</setName>");
						   sb.append("<setDescription>" + set.get("setDescription") + "</setDescription>");
						   sb.append("</set>");
					   }
					   
					   sb.append("</ListSets>");
				} 
				else 
				{
				    throw new NoSetHierarchyException();
				}
			}
		    
        } catch (OAIInternalServerError e) {
		    sb.append(getRequestElement(request, validParamNames, baseURL));
		    sb.append(e.getMessage());
			e.printStackTrace();
		} catch (NoSetHierarchyException e) {
		    sb.append(getRequestElement(request, validParamNames, baseURL));
		    sb.append(e.getMessage());
		}
        sb.append("</OAI-PMH>");
        response.setContentType("text/xml; charset=UTF-8");
        
        return sb.toString();
    }
}
