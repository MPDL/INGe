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

import java.net.URI;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.mpg.escidoc.services.fledgeddata.oai.OAIUtil;


/**
 * ServerVerb is the parent class for each of the server-side OAI verbs.
 *
 * @author Jefffrey A. Young, OCLC Online Computer Library Center
 */
public abstract class ServerVerb 
{

    private int statusCode = HttpServletResponse.SC_OK; 	// http status
    private String message = null; 							// http response message

    /**
     * Complete XML response String
     */
    protected String xmlText = null;

    /**
     * Constructor
     */
    protected ServerVerb() { }

    public static void init(Properties properties) throws Exception {}

    /**
     * initialize the Verb from the specified xml text
     *
     * @param xmlText complete XML response string
     */
    protected void init(String xmlText) {
        this.xmlText = xmlText;
    }

    /**
     * Server-side verb constructor
     *
     * @param xmlText complete XML response string
     */
    public ServerVerb(String xmlText) {
        init(xmlText);
    }

    /**
     * Retrieve the http status code
     *
     * @return the http status code;
     */
    public int getStatus() { return statusCode; }

    /**
     * Retrieve the http status message
     *
     * @return the http status message;
     */
    public String getMessage() { return message; }

    /**
     * set the http status code and message
     *
     * @param statusCode the http status code
     * @param message the http status message
     */
    protected void setError(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    /**
     * Create an OAI response date from the specified date
     *
     * @param date the date to be transformed to an OAI response date
     * @return a String representation of the OAI response Date.
     */
    public static String createResponseDate(Date date) {
        StringBuffer sb = new StringBuffer();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        formatter.setTimeZone(tz);
        sb.append(formatter.format(date));
        return sb.toString();
    }

    protected static String getRequestElement(HttpServletRequest request,
            List validParamNames,
            String baseURL) {
        return getRequestElement(request, validParamNames, baseURL, false);
    }

    protected static String getRequestElement(HttpServletRequest request,
            List validParamNames,
            String baseURL,
            boolean xmlEncodeSetSpec) 
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<request");
        Enumeration params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String name = (String)params.nextElement();
            if (validParamNames.contains(name)) {
                String value = request.getParameter(name);
                if (value != null && value.length() > 0) {
                    sb.append(" ");
                    sb.append(name);
                    sb.append("=\"");
                    if (!xmlEncodeSetSpec && "set".equals(name)) {
//                      try {
                        sb.append(value);
//                      sb.append(URLEncoder.encode(value, "UTF-8"));
//                      } catch (UnsupportedEncodingException e) {
//                      e.printStackTrace();
//                      sb.append("UnsupportedEncodingException");
//                      }
                    } else {
                        sb.append(OAIUtil.xmlEncode(value));
                    }
                    sb.append("\"");
                }
            }
        }
        sb.append(">");
        sb.append(baseURL);
        sb.append("</request>");
        return sb.toString();
    }

    protected static boolean hasBadArguments(HttpServletRequest request,
            Iterator requiredParamNames,
            List validParamNames) {
        while (requiredParamNames.hasNext()) {
            String name = (String)requiredParamNames.next();
            String value = request.getParameter(name);
            if (value == null || value.length() == 0) {
                return true;
            }
        }
        Enumeration params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String name = (String)params.nextElement();
            if (!validParamNames.contains(name)) {
                return true;
            } else if (request.getParameterValues(name).length > 1) {
                return true;
            }
        }
        String identifier = request.getParameter("identifier");
        try {
            if (identifier != null && identifier.length() > 0) {
                identifier = URLEncoder.encode(identifier, "UTF-8");
                new URI(identifier);
            }
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    /**
     * Get the complete XML response for the verb request
     *
     * @return the complete XML response for the verb request
     */
    public String toString() {
        return xmlText;
    }

    public static HashMap getVerbs() 
    {
        HashMap serverVerbsMap = new HashMap();
        serverVerbsMap.put("ListRecords", ListRecords.class);
        serverVerbsMap.put("ListIdentifiers", ListIdentifiers.class);
        serverVerbsMap.put("GetRecord", GetRecord.class);
        serverVerbsMap.put("Identify", Identify.class);
        serverVerbsMap.put("ListMetadataFormats", ListMetadataFormats.class);
        serverVerbsMap.put("ListSets", ListSets.class);
        serverVerbsMap.put("BadVerb", BadVerb.class);
        return serverVerbsMap;
    }

}
