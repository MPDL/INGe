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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import de.mpg.escidoc.services.fledgeddata.FetchImeji;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.BadArgumentException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.BadResumptionTokenException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.CannotDisseminateFormatException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.IdDoesNotExistException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.NoItemsMatchException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.NoMetadataFormatsException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.NoSetHierarchyException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.OAIInternalServerError;
import de.mpg.escidoc.services.fledgeddata.oai.verb.ServerVerb;

/**
 * AbstractCatalog is the generic interface between OAICat and any arbitrary
 * database. Implement this interface to have OAICat work with your database.
 *
 * @author Jeffrey A. Young, OCLC Online Computer Library Center
 */
public class oaiCatalog 
{ 
    /**
     * optional property to limit the life of resumptionTokens (<0 indicates no limit)
     **/
    private int millisecondsToLive = -1;
    
    /**
     * Index into VALID_GRANULARITIES and FROM_GRANULARITIES
     */
    private int supportedGranularityOffset = -1;
    
    /**
     * All possible valid granularities
     */
    private static final String[] VALID_GRANULARITIES = {
        "YYYY-MM-DD",
        "YYYY-MM-DDThh:mm:ssZ"
    };
    
    /**
     * minimum valid 'from' granularities
     */
    private static final String[] FROM_GRANULARITIES = {
        "0000-01-01",
        "0000-01-01T00:00:00Z"
    };

    /**
     * get the optional millisecondsToLive property (<0 indicates no limit)
     **/
    public int getMillisecondsToLive() { return millisecondsToLive; }
    

    
    public void setSupportedGranularityOffset(int i) {
        supportedGranularityOffset = i;
    }
    
    /**
     * Convert the requested 'from' parameter to the finest granularity supported
     * by this repository.
     * @exception BadArgumentException one or more of the arguments are bad.
     */
    public String toFinestFrom(String from) 
    throws BadArgumentException {
            System.out.println("AbstractCatalog.toFinestFrom: from=" + from);
            System.out.println("                            target=" + VALID_GRANULARITIES[supportedGranularityOffset]);
            
        if (from.length() > VALID_GRANULARITIES[supportedGranularityOffset].length()) {
            throw new BadArgumentException();
        }
        if (from.length() != VALID_GRANULARITIES[supportedGranularityOffset].length()) {
            StringBuffer sb = new StringBuffer(from);
            if (sb.charAt(sb.length()-1) == 'Z')
                sb.setLength(sb.length()-1);
            
            sb.append(FROM_GRANULARITIES[supportedGranularityOffset].substring(sb.length()));
            from = sb.toString();
        }
        
        if (!isValidGranularity(from)) {
            throw new BadArgumentException();
        }
        
        return from;
    }
    
    /**
     * Convert the requested 'until' paramter to the finest granularity supported
     * by this repository
     * @exception BadArgumentException one or more of the arguments are bad.
     */
    public String toFinestUntil(String until)
    throws BadArgumentException {
        if (until.length() == VALID_GRANULARITIES[supportedGranularityOffset].length()) {
            if (!isValidGranularity(until))
                throw new BadArgumentException();
            return until;
        }
        if (until.length() > VALID_GRANULARITIES[supportedGranularityOffset].length()) {
            throw new BadArgumentException();
        }
        
        StringBuffer sb = new StringBuffer(until);
        if (sb.charAt(sb.length()-1) == 'Z')
            sb.setLength(sb.length()-1);
        
        if (sb.length() < VALID_GRANULARITIES[0].length()) {
            while (sb.length() < 4) sb.append("9");
            switch (sb.length()) {
            case 4: // YYYY
                sb.append("-");
            case 5: // YYYY-
                sb.append("12");
            case 7: // YYYY-MM
                sb.append("-");
            case 8: // YYYY-MM-
                sb.append("31");
                break;
                
            case 6: // YYYY-M
            case 9: // YYYY-MM-D
                throw new BadArgumentException();
            }
        }
        
        until = sb.toString();
        if (until.length() == VALID_GRANULARITIES[supportedGranularityOffset].length()) {
            if (!isValidGranularity(until))
                throw new BadArgumentException();
            return until;
        }
        
        if (sb.length() < VALID_GRANULARITIES[1].length()) {
            switch (sb.length()) {
            case 10: // YYYY-MM-DD
                sb.append("T");
            case 11: // YYYY-MM-DDT
                sb.append("23");
            case 13: // YYYY-MM-DDThh
                sb.append(":");
            case 14: // YYYY-MM-DDThh:
                sb.append("59");
//              case 16: // YYYY-MM-DDThh:mm
//              sb.append("Z");
//              break;
                
//              case 12: // YYYY-MM-DDTh
//              case 15: // YYYY-MM-DDThh:m
//              throw new BadGranularityException();
//              }
//              }
                
//              until = sb.toString();
//              if (until.length() == VALID_GRANULARITIES[supportedGranularityOffset].length()) {
//              if (!isValidGranularity(until))
//              throw new BadGranularityException();
//              return until;
//              }
                
//              if (sb.charAt(sb.length()-1) == 'Z')
//              sb.setLength(sb.length()-1); // remove the trailing 'Z'
                
//              if (sb.length() < VALID_GRANULARITIES[2].length()) {
//              switch (sb.length()) {
            case 16: // YYYY-MM-DDThh:mm
                sb.append(":");
            case 17: // YYYY-MM-DDThh:mm:
                sb.append("59");
            case 19: // YYYY-MM-DDThh:mm:ss
                sb.append("Z");
                break;
                
            case 18: // YYYY-MM-DDThh:mm:s
                throw new BadArgumentException();
            }
        }
        
//      until = sb.toString();
//      if (until.length() == VALID_GRANULARITIES[supportedGranularityOffset].length()) {
//      if (!isValidGranularity(until))
//      throw new BadGranularityException();
//      return until;
//      }
        
//      if (sb.charAt(sb.length()-1) == 'Z')
//      sb.setLength(sb.length()-1); // remove the trailing 'Z'
        
//      switch (sb.length()) {
//      case 19: // YYYY-MM-DDThh:mm:ss
//      sb.append(".");
//      case 20: // YYYY-MM-DDThh:mm:ss.
//      sb.append("0");
//      case 21: // YYYY-MM-DDThh:mm:ss.s
//      sb.append("Z");
//      break;
//      }
        
        until = sb.toString();
        if (!isValidGranularity(until))
            throw new BadArgumentException();
        return until;
    }
    
    /**
     * Does the specified date conform to the supported granularity of this repository?
     * @param date a UTC date
     * @return true if date conforms to the supported granularity of this repository,
     * false otherwise.
     */
    private boolean isValidGranularity(String date) {
        if (date.length() > VALID_GRANULARITIES[supportedGranularityOffset].length())
            return false;
        
        if (date.length() < VALID_GRANULARITIES[0].length()
                || !Character.isDigit(date.charAt(0)) // YYYY
                || !Character.isDigit(date.charAt(1))
                || !Character.isDigit(date.charAt(2))
                || !Character.isDigit(date.charAt(3))
                || date.charAt(4) != '-'
                    || !Character.isDigit(date.charAt(5)) // MM
                    || !Character.isDigit(date.charAt(6))
                    || date.charAt(7) != '-'
                        || !Character.isDigit(date.charAt(8)) // DD
                        || !Character.isDigit(date.charAt(9))) {
            return false;
        }
        
        if (date.length() > VALID_GRANULARITIES[0].length()) {
            if (date.charAt(10) != 'T'
                || date.charAt(date.length()-1) != 'Z'
                    || !Character.isDigit(date.charAt(11)) // hh
                    || !Character.isDigit(date.charAt(12))
                    || date.charAt(13) != ':'
                        || !Character.isDigit(date.charAt(14)) // mm
                        || !Character.isDigit(date.charAt(15))
//                      ) {
//                      return false;
//                      }
//                      }
                        
//                      if (date.length() > VALID_GRANULARITIES[1].length()) {
//                      if (
                        || date.charAt(16) != ':'
                            || !Character.isDigit(date.charAt(17)) // ss
                            || !Character.isDigit(date.charAt(18))) {
                return false;
            }
        }
        
//      if (date.length() > VALID_GRANULARITIES[2].length()) {
//      if (date.charAt(19) != '.'
//      || !Character.isDigit(date.charAt(20))) { // s
//      return false;
//      }
        
//      }
        return true;
    }
    
    
    /**
     * Retrieve the list of supported Sets. This should probably be initialized
     * by the constructor from the properties object that is passed to it.
     *
     * @return a Map object containing <setSpec> values as the Map keys and 
     * <setName> values for the corresponding the Map values.
     * @exception NoSetHierarchyException No sets are defined for this repository
     * @exception OAIInternalServerError An error occurred
     * @throws CannotDisseminateFormatException 
     * @throws IdDoesNotExistException 
     */
    public static List listSets(Properties properties) throws NoSetHierarchyException, OAIInternalServerError
    {
		String repository = properties.getProperty("oai.repositoryName", "undefined");
		if (repository.equalsIgnoreCase("imeji"))
		{
			return FetchImeji.listSets(properties);
		}
		if (repository.equalsIgnoreCase("escidoc"))
		{
			return null;
			//TODO
			//return fetcheSciDoc.listSets(Properties properties);
		}
		return null;
    }

    /**
     * TODO
     * @param from
     * @param until
     * @param set
     * @param properties
     * @return
     * @throws BadArgumentException
     * @throws CannotDisseminateFormatException
     * @throws NoItemsMatchException
     * @throws NoSetHierarchyException
     * @throws OAIInternalServerError
     */
    public String listIdentifiers(String from, String until, String set, Properties properties)
    		throws BadArgumentException, CannotDisseminateFormatException, NoItemsMatchException,
    			   NoSetHierarchyException, OAIInternalServerError
    {
    	String repository = properties.getProperty("oai.repositoryName", "undefined");
    	if (repository.equalsIgnoreCase("imeji"))
    	{
    		return FetchImeji.listIdentifiers(from, until, set, properties);
    	}
    	if (repository.equalsIgnoreCase("escidoc"))
    	{
    		return null;
    		//TODO
    		//return fetcheSciDoc.listIdentifiers(String from, String until, String set, String metadataPrefix);
    	}
    	return null;
    }
    
    /**
     * TODO
     * @param resumptionToken
     * @return
     * @throws BadResumptionTokenException
     * @throws OAIInternalServerError
     */
    public static Map listIdentifiers(String resumptionToken)
    throws BadResumptionTokenException, OAIInternalServerError 
    {
    	//TODO
        Map listRecordsMap = new HashMap();

        return listRecordsMap;
    }

    /**
     * Retrieve the specified metadata for the specified identifier
     *
     * @param identifier the OAI identifier
     * @return the String containing the result record.
     * @exception IdDoesNotExistException The specified identifier doesn't exist.
     * @exception CannotDisseminateFormatException The identifier exists, but doesn't support
     * the specified metadataPrefix.
     * @exception OAIInternalServerError signals an http status code 500 problem
     */
    public static String getRecord(String identifier, String metadataPrefix, Properties properties)
    		throws IdDoesNotExistException, CannotDisseminateFormatException, OAIInternalServerError
    {
    	String repository = properties.getProperty("oai.repositoryName", "undefined");
    	if (repository.equalsIgnoreCase("imeji"))
    		
    	{
    		return FetchImeji.getRecord(identifier, metadataPrefix, properties);
    	}
    	if (repository.equalsIgnoreCase("escidoc"))
    	{
    		return null;
    		//TODO
    		//return fetcheSciDoc.getRecord(identifier, metadataPrefix, properties);
    	}
    	return null;
    }
    
    /**
     * Retrieve a list of records that satisfy the specified criteria
     *
     * @param from beginning date in the form of YYYY-MM-DD or null if earliest
     * date is desired
     * @param until ending date in the form of YYYY-MM-DD or null if latest
     * date is desired
     * @param set set name or null if no set is desired
     * @return a Map object containing an optional "resumptionToken" key/value
     * pair and a "records" Iterator object. The "records" Iterator contains a
     * set of Records objects.
     * @exception BadArgumentException one or more of the arguments are bad.
     * @exception CannotDisseminateFormatException the requested metadataPrefix isn't supported
     * @exception NoItemsMatchException no items fit the criteria
     * @exception NoSetHierarchyException sets aren't defined for this repository
     * @exception OAIInternalServerError signals an http status code 500 problem
     */
    public static String listRecords(String metadataPrefix, Properties properties, String from, String until, String set)
    		throws NoItemsMatchException, CannotDisseminateFormatException, OAIInternalServerError
    {
    	String repository = properties.getProperty("oai.repositoryName", "undefined");
    	if (repository.equalsIgnoreCase("imeji"))
    		
    	{
    		return FetchImeji.listRecords(metadataPrefix, properties, from ,until, set);
    	}
    	if (repository.equalsIgnoreCase("escidoc"))
    	{
    		return null;
    		//TODO
    		//return fetcheSciDoc.listRecords(metadataPrefix, properties, from ,until, set);
    	}
    	return null;
    }
    
    /**
     * Retrieve the next set of records associated with the resumptionToken
     *
     * @param resumptionToken implementation-dependent format taken from the
     * previous listRecords() Map result.
     * @return a Map object containing an optional "resumptionToken" key/value
     * pair and a "records" Iterator object. The "records" Iterator contains a
     * set of Records objects.
     * @exception BadResumptionTokenException The resumptionToken is bad.
     * @exception OAIInternalServerError signals an http status code 500 problem
     */
    public static Map listRecords(String resumptionToken)
    throws BadResumptionTokenException, OAIInternalServerError 
    {
    	//TODO
//        Map listIdentifiersMap = listIdentifiers(resumptionToken);
//        resumptionToken = (String)listIdentifiersMap.get("resumptionToken");
//        Iterator identifiers = (Iterator)listIdentifiersMap.get("identifiers");
//        String metadataPrefix = (String)listIdentifiersMap.get("metadataPrefix");
//        
        Map listRecordsMap = new HashMap();
//        ArrayList records = new ArrayList();
//        
//        while (identifiers.hasNext()) {
//            String identifier = (String)identifiers.next();
//            try {
//                records.add(getRecord(identifier, metadataPrefix));
//            } catch (IdDoesNotExistException e) {
//                throw new OAIInternalServerError("GetRecord failed to retrieve identifier '"
//                        + identifier + "'");
//            } catch (CannotDisseminateFormatException e) {
//                // someone cheated
//                throw new BadResumptionTokenException();
//            }
//        }
//        listRecordsMap.put("records", records.iterator());
//        if (resumptionToken != null) {
//            listRecordsMap.put("resumptionToken", resumptionToken);
//        }
        return listRecordsMap;
    }
//    
//    public Map getResumptionMap(String resumptionToken) {
////        return getResumptionMap(resumptionToken, -1, -1);
////    }
////    
////    public Map getResumptionMap(String resumptionToken, int completeListSize, int cursor) {
////        Map resumptionMap = null;
////        if (resumptionToken != null) {
////            resumptionMap = new HashMap();
////            resumptionMap.put("resumptionToken", resumptionToken);
////            if (millisecondsToLive > 0) {
//////              Date now = new Date();
////                Date then = new Date((new Date()).getTime() + millisecondsToLive);
////                resumptionMap.put("expirationDate", ServerVerb.createResponseDate(then));
////            }
////            if (completeListSize >= 0) {
////                resumptionMap.put("completeListSize", Integer.toString(completeListSize));
////            }
////            if (cursor >= 0) {
////                resumptionMap.put("cursor", Integer.toString(cursor));
////            }
////        }
//        return resumptionMap;
//    }
}