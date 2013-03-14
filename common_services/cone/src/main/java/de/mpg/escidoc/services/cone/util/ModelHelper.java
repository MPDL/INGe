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

package de.mpg.escidoc.services.cone.util;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jrdf.graph.PredicateNode;

import de.mpg.escidoc.services.cone.ModelList;
import de.mpg.escidoc.services.cone.ModelList.Model;
import de.mpg.escidoc.services.cone.ModelList.Predicate;
import de.mpg.escidoc.services.cone.ModelList.ModelResult;
import de.mpg.escidoc.services.cone.Querier;
import de.mpg.escidoc.services.cone.QuerierFactory;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * Helper class for result pattern.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ModelHelper
{

    private static final Logger logger = Logger.getLogger(ModelHelper.class);
    
    private static final String REGEX_BRACKETS = "<[^>]+>";
    
    private static final ReplacePattern[] replacePattern = new ReplacePattern[]
    {
        new ReplacePattern("AND\\{[^,\\}]+(,[^,\\}]+)*\\}", "a"),
        new ReplacePattern("AND\\{,[^\\{\\}]*\\}|AND\\{[^\\{\\}]*,\\}|AND\\{[^\\{\\}]*,,[^\\{\\}]*\\}", ""),
        new ReplacePattern("OR\\{[^\\{\\}]*[^,\\{\\}]+[^\\{\\}]*\\}", "o"),
        new ReplacePattern("OR\\{,*\\}", ""),
        new ReplacePattern("NOT\\{\\}", "n"),
        new ReplacePattern("NOT\\{[^\\{\\}]+\\}", "")
    };
    
    /**
     * Hide constructor.
     */
    private ModelHelper()
    {
        
    }
    
    
    
    
    
    
    
    
    public static List<Pair<ResultEntry>> buildObjectFromPatternNew(String modelName, String currentSubject, TreeFragment poMap, boolean loggedIn) throws Exception
    {
     
        Model model = ModelList.getInstance().getModelByAlias(modelName);
        
        Set<String> languages = getLanguagesForResults(model, poMap, loggedIn);
        
        List<Pair<ResultEntry>> results = new ArrayList<Pair<ResultEntry>>();
        
        for (ModelResult modelResult : model.getResults())
        {
        	
            if (languages.size() == 0)
            {
            	List<Map<String, List<LocalizedTripleObject>>> permutationMaps = getPermutations(model, poMap, modelResult, loggedIn, "");
            	
            	results.addAll(getReplaceResult(modelResult, permutationMaps, null));	
    
            }
            else
            {
            	for (String lang : languages)
                {
            		List<Map<String, List<LocalizedTripleObject>>> permutationMaps = getPermutations(model, poMap, modelResult, loggedIn, lang);

                    if (!"".equals(lang))
                    {
                    	results.addAll(getReplaceResult(modelResult, permutationMaps, lang));	
                    }
                    else
                    {
                    	results.addAll(getReplaceResult(modelResult, permutationMaps, null));	
                    }
                }
            }
        }
        
        
        for(Pair<ResultEntry> res : results)
        {
        	logger.info("Result: type=" + res.getValue().getType() + " || " + "result=" + res.getValue().getValue() + " || sortResult=" + res.getValue().getSortResult() + " || lang=" + res.getValue().getLanguage());
        }
        
        return results;
    }
    
    
    
    private static  List<Pair<ResultEntry>> getReplaceResult(ModelResult modelResult, List<Map<String, List<LocalizedTripleObject>>> permutationMaps, String lang)
    {
    	List<ResultEntry> resList = new ArrayList<ResultEntry>();
    	for(Map<String, List<LocalizedTripleObject>> map : permutationMaps)
    	{
    		String result = getResultStringFromPattern(modelResult.getResultPattern(), map);
        	ResultEntry replaceResult = new ResultEntry(result);
        	replaceResult.setType(modelResult.getType());
        	replaceResult.setLanguage(lang);
        	
        	if(modelResult.getSortPattern()!=null && !modelResult.getSortPattern().trim().isEmpty())
        	{
        		String sortKey = getResultStringFromPattern(modelResult.getSortPattern(), map);
        		replaceResult.setSortResult(sortKey);
        	}
        	resList.add(replaceResult);
        	
    	}
    	
    	//remove duplicates and empty results
    	List<ResultEntry> resListWithoutDuplicates = new ArrayList<ResultEntry>();
    	for(ResultEntry res : resList)
    	{
    		if(!resListWithoutDuplicates.contains(res) && res.getValue()!= null && !res.getValue().isEmpty())
    		{
    			resListWithoutDuplicates.add(res);
    		}
    	}
    	
    	List<Pair<ResultEntry>> pairResultList = new ArrayList<Pair<ResultEntry>>();
    	for(ResultEntry res : resListWithoutDuplicates)
    	{
    		pairResultList.add(new Pair<ResultEntry>(null, res));
    	}
    	
    	
    	return pairResultList;
    }
    
    
    public static List<Map<String, List<LocalizedTripleObject>>> getPermutations(Model model, TreeFragment poMap, ModelResult modelResult, boolean loggedIn, String lang)
    {
    	//logger.info("----------------------------Get Permutations-----------------------------------\n" + modelResult.getResultPattern() + "\n" + "------------------------------------------------------------------------------");
    	List<Map<String, List<LocalizedTripleObject>>> permutationList = new ArrayList<Map<String, List<LocalizedTripleObject>>>();
    	permutationList.add(new HashMap<String, List<LocalizedTripleObject>>());
    	return getPermutations(model, null, poMap, modelResult, loggedIn, lang, permutationList, "");
    }
    
   
    public static List<Map<String, List<LocalizedTripleObject>>> getPermutations(Model model, Predicate superPredicate, TreeFragment poMap, ModelResult modelResult, boolean loggedIn, String lang, List<Map<String, List<LocalizedTripleObject>>> permutationList, String prefix)
    {

    	for (String predicateName : poMap.keySet())
        {
    		String regex = "(<|\\|)" + Pattern.quote(predicateName) + "(>|\\|)";
    		Pattern p = Pattern.compile(regex);
    		Matcher resultPatternMatcher = p.matcher(modelResult.getResultPattern());
    		Matcher sortPatternMatcher = null;
    		if(modelResult.getSortPattern()!=null)
    		{
    			sortPatternMatcher = p.matcher(modelResult.getSortPattern());
    		}
    		
    		if(resultPatternMatcher.find() ||
    		    (sortPatternMatcher!=null && sortPatternMatcher.find()))
    		{
	       	    //logger.info("Starting with predicate: " + predicateName);
	       		Predicate predicate = null;
	       	    if(superPredicate!=null)
	            {
	       	    	predicate = superPredicate.getPredicate(predicateName);
	            }
	            else
	            {
	            	predicate = model.getPredicate(predicateName);
	            }
	       	   
	           
	            List<Map<String, List<LocalizedTripleObject>>> newPermutationList = new ArrayList<Map<String, List<LocalizedTripleObject>>>();
	            
            	 for (LocalizedTripleObject value : poMap.get(predicate.getId()))
                 {
            		 try
                     {
                         if (!predicate.isResource() && (value instanceof TreeFragment && (lang.equals(value.getLanguage()) || value.getLanguage() == null || "".equals(value.getLanguage()) || ("".equals(lang) && value.getLanguage().equals(PropertyReader.getProperty("escidoc.cone.language.default"))))))
                         {
                             TreeFragment treeValue = (TreeFragment) value;
                             
                             newPermutationList.addAll(getPermutations(model, predicate, treeValue, modelResult, loggedIn, lang, permutationList, prefix + predicate.getId() + "|"));
                            
                         }
                         else if (predicate.isResource() && value instanceof TreeFragment && predicate.isIncludeResource())
                         {
                             Querier querier = QuerierFactory.newQuerier(loggedIn);
                             TreeFragment treeFragment = querier.details(predicate.getResourceModel(), ((TreeFragment)value).getSubject(), lang);
                             querier.release();
                             Model newModel = ModelList.getInstance().getModelByAlias(predicate.getResourceModel());
                             
                             newPermutationList.addAll(getPermutations(newModel, null, treeFragment, modelResult, loggedIn, lang, permutationList, prefix + predicate.getId() + "|"));
                             
                         }
                         else if (predicate.isResource() && value instanceof LocalizedString && predicate.isIncludeResource())
                         {
                             Querier querier = QuerierFactory.newQuerier(loggedIn);
                             TreeFragment treeFragment = querier.details(predicate.getResourceModel(), ((LocalizedString)value).getValue(), lang);
                             querier.release();
                             Model newModel = ModelList.getInstance().getModelByAlias(predicate.getResourceModel());
                             
                             newPermutationList.addAll(getPermutations(newModel, null, treeFragment, modelResult, loggedIn, lang, permutationList, prefix + predicate.getId() + "|"));
                             
                         }
                         else
                         {
                        	
                        	 if (lang.equals(value.getLanguage()) || "".equals(value.getLanguage()) || (!predicate.isLocalized() && value.getLanguage() == null) || ("".equals(lang) && (value.getLanguage() == null || value.getLanguage().equals(PropertyReader.getProperty("escidoc.cone.language.default")))))
                             {
                        	 
	                        	 for(Map<String, List<LocalizedTripleObject>> currentMap : permutationList)
	                        	 {
	                        		 Map<String, List<LocalizedTripleObject>> newMap = new HashMap<String, List<LocalizedTripleObject>>();
	                        		 newMap.putAll(currentMap);
	                        		 List<LocalizedTripleObject> list = new ArrayList<LocalizedTripleObject>();
	                        		 list.add(value);
	                        		 newMap.put(prefix + predicate.getId(), list);
	                        		 newPermutationList.add(newMap);
	                        	 }
                             }
                         }     
                     }
                     catch (Exception e)
                     {
                         throw new RuntimeException(e);
                     }

                 }
            	 if(newPermutationList.size()>0)
            	 {
            		 permutationList = newPermutationList;
            	 }
    		}
        }
    	return permutationList;
    }
    
    
    
    
    
    
    
    private static  String getResultStringFromPattern(String pattern, Map<String, List<LocalizedTripleObject>> permutationMap)
    {
    	 StringBuffer result = new StringBuffer();
    	 String[] patternPieces = pattern.split("\\n");
         for (String line : patternPieces)
         {
        	 //logger.info("------------------------------------- patternLine: " + line +"---------------------------------------------------");
        	 List<ResultEntry> strings = new ArrayList<ResultEntry>();
             strings.add(new ResultEntry(line));

             //then replace al,l others 
             for (String predicateName : permutationMap.keySet())
             {
            	 
            	//List<String> strings = new ArrayList<String>();
                 if (line.contains("<" + predicateName + ">"))
                 {
                     for (LocalizedTripleObject value : permutationMap.get(predicateName))
                     {

                           line = line.replace("<" + predicateName + ">", value.toString().replace(":", "&#x3A;").replace(",", "&#x2C;"));

                     }
                 }

             	}
            

                 String singleString = line;
                 singleString = singleString.replaceAll(REGEX_BRACKETS, "");
                 String newString = null;
                 while (!singleString.equals(newString))
                 {
                     newString = singleString;
                     singleString = replaceTokens(singleString);
                 }
                 if (singleString.startsWith(":"))
                 {
                     singleString = "";
                 }
                 else if (singleString.contains(":"))
                 {
                     singleString = singleString.substring(singleString.indexOf(":") + 1);
                 }
                 
                line = singleString.replace("&#x3A;", ":").replace("&#x2C;", ",");
                
                result.append(line);

         }
         
    	return result.toString();
    }
    
    
    
    
    
    /**
     * Reads the result pattern from the {@link Model} and builds up results.
     * 
     * @param modelName A string representing the model.
     * @param currentSubject The identifier of the resource.
     * @param poMap The data.
     * @return A list of {@link Pair} containing the results in different languages.
     * @throws Exception Any exception.
     */
    /*
    public static List<Pair<LocalizedString>> buildObjectFromPattern(String modelName, String currentSubject, TreeFragment poMap, boolean loggedIn) throws Exception
    {
     
        Model model = ModelList.getInstance().getModelByAlias(modelName);
        
        Set<String> languages = getLanguagesForResults(model, poMap, loggedIn);
        
        List<Pair<LocalizedString>> results = new ArrayList<Pair<LocalizedString>>();
        
        for (Result modelResult : model.getResults())
        {
        	
            if (languages.size() == 0)
            {
            	List<ReplaceResult> result = getResultsFromPattern(model, modelResult.getResultPattern(), poMap, "", loggedIn, false, null);
                
            	
                for (ReplaceResult string : result)
                {
                	logger.info("Result: " + string.getResult());
                	if(modelResult.getSortPattern()!=null)
                	{
                		
                		List<ReplaceResult> sortResult = getResultsFromPattern(model, modelResult.getSortPattern(), poMap, "", loggedIn, true, string.getValueMap());
                		logger.info("Sort result: " + sortResult);
                	}
                	
                	results.add(new Pair<LocalizedString>(null, new LocalizedString(string.getResult())));
                }
                
            }
            else
            {
                for (String lang : languages)
                {
                	List<ReplaceResult> result = getResultsFromPattern(model, modelResult.getResultPattern(), poMap, lang, loggedIn, false, null);

                    if (!"".equals(lang))
                    {
                    	for (ReplaceResult string : result)
                        {
                            if (!"".equals(string))
                            {
                            	logger.info("Result: " + string.getResult());
                            	
                            	if(modelResult.getSortPattern()!=null)
                            	{
                            		List<ReplaceResult> sortResult = getResultsFromPattern(model, modelResult.getSortPattern(), poMap, "", loggedIn, true, string.getValueMap());
                            		logger.info("Sort result: " + sortResult);
                            	}
                            	
                                results.add(new Pair<LocalizedString>(lang, new LocalizedString(string.getResult())));
                            }
                        }
                    }
                    else
                    {
                    	for (ReplaceResult string : result)
                        {
                            if (!"".equals(string))
                            {
                            	logger.info("Result: " + string.getResult());
                            	
                            	if(modelResult.getSortPattern()!=null)
                            	{
                            		List<ReplaceResult> sortResult = getResultsFromPattern(model, modelResult.getSortPattern(), poMap, "", loggedIn, true, string.getValueMap());
                            		logger.info("Sort result: " + sortResult);
                            	}
                            	
                                results.add(new Pair<LocalizedString>(null, new LocalizedString(string.getResult())));
                            }
                        }
                    }
                }
            }
        }
        return results;
    }
    
    
    
    private static  List<ReplaceResult> getResultsFromPattern(Model model, String pattern, Map<String, List<LocalizedTripleObject>> originPoMap, String lang, boolean loggedIn, boolean sort, Map<String, List<LocalizedTripleObject>> sortPoMap)
    {
    	 String[] patternPieces = pattern.split("\\n");
    	 
	
    	 //List<String> result = new ArrayList<String>();
    	 List<ReplaceResult> result = new ArrayList<ReplaceResult>();
    	 result.add(new ReplaceResult(""));
         
         //logger.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX---PATTERN.---XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
         
         for (String line : patternPieces)
         {
        	 //logger.info("------------------------------------- patternLine: " + line +"---------------------------------------------------");
        	 List<ReplaceResult> strings = new ArrayList<ReplaceResult>();
             strings.add(new ReplaceResult(line));
             
             
             
             // First replace predicates which match predicates from sort map 
             if(sortPoMap!=null)
             {
	             for (String predicateName : sortPoMap.keySet())
	             {
	            	
	                 List<ReplaceResult> newStrings = new ArrayList<ReplaceResult>();
	                 
	                 for (ReplaceResult string : strings)
	                 {
	                	 List<ReplaceResult> rep = simpleReplacePattern(sortPoMap, string.getResult(), predicateName, string); 
	                	 newStrings.addAll(rep);
	                 }
	                 
	                 if (newStrings.size() > 0)
	                 {
	                     strings = newStrings;
	                 }
	             }
             
             }
   
             //then replace al,l others 
             for (String predicateName : originPoMap.keySet())
             {
            	 //logger.info("Predicate: " + predicateName);
                 Predicate predicate = model.getPredicate(predicateName);
                 if (!sort && predicate == null)
                 {
                     logger.warn("Predicate not found: " + predicateName);
                 }
                 
                 List<ReplaceResult> newStrings = new ArrayList<ReplaceResult>();
                 
                 for (ReplaceResult string : strings)
                 {
                	 List<ReplaceResult> rep = null;
                	 
                	 rep = replacePattern(originPoMap, predicate, lang, loggedIn, string); 
                	 newStrings.addAll(rep);
                	 //logger.info("Replace Result After adding: " + newStrings);
                	 
                 }
                 
                 if (newStrings.size() > 0)
                 {
                     strings = newStrings;
                 }
                 
                // logger.info("strings: " + strings);
             }
            
            // logger.info("strings before regex: " + strings);
            List<ReplaceResult> newResult = new ArrayList<ReplaceResult>();
             //logger.info("Replace tokens");
             for (ReplaceResult res : strings)
             {
            	 String string = res.getResult();
                 String singleString = string;
                 singleString = singleString.replaceAll(REGEX_BRACKETS, "");
                 String newString = null;
                 while (!singleString.equals(newString))
                 {
                     newString = singleString;
                     singleString = replaceTokens(singleString);
                 }
                 if (singleString.startsWith(":"))
                 {
                     singleString = "";
                 }
                 else if (singleString.contains(":"))
                 {
                     singleString = singleString.substring(singleString.indexOf(":") + 1);
                 }
                 
                 newResult.add(new ReplaceResult(singleString.replace("&#x3A;", ":").replace("&#x2C;", ","), res.getValueMap()));
                 
             }
             
             //logger.info("strings: " + strings);
             //logger.info("newResult: " + newResult);
             //logger.info("Remove duplicates");
             
             
             strings = new ArrayList<ReplaceResult>();
             
             //logger.info("Strings before duplicate check :" + newResult);
             
             
             for (ReplaceResult string : newResult)
             {
                 if (!strings.contains(string))
                 {
                     strings.add(string);
                 }
             }
             
             //logger.info("Strings after duplicate check :" + strings);
             //logger.info("strings: " + strings);
             //logger.info("newResult: " + newResult);
             //logger.info("Adding old results");
             
            
             //For sorting, reduce list to one entry containing all resulting strings of the pattern
             // The predicate maps can be ignored for soprt results and don't have to be in the result
             if(sortPoMap!=null)
             {
            	 StringBuffer singleResult = new StringBuffer();
            	
            	 for (ReplaceResult string : strings)
                 {
            		 singleResult.append(string.getResult());
                 }
            	 strings = new ArrayList<ReplaceResult>();
            	 strings.add(new ReplaceResult(singleResult.toString()));
             }
             
             newResult = new ArrayList<ReplaceResult>();

             for (ReplaceResult oldResult : result)
             {
                 for (ReplaceResult string : strings)
                 {
                	 
                	//logger.info("------ Merging " + oldResult.getResult() + " with " + string.getResult()+ " ------");
                	 
                    //logger.info("Old: " + oldResult.getValueMap());
                	//logger.info("Current: " + string.getValueMap());
                	 Map<String, List<LocalizedTripleObject>> mergedMap = new HashMap<String, List<LocalizedTripleObject>>();
                 	
 
                     mergedMap.putAll(oldResult.getValueMap());
                     mergedMap.putAll(string.getValueMap());
                	 
                	//logger.info("Merged: " + mergedMap);
                	 
                	 
                	
                    
                	 newResult.add (new ReplaceResult(oldResult.getResult() + string.getResult(), mergedMap ));
                 }
             }
             result = newResult;
             //logger.info("strings: " + strings);
             //logger.info("newResult: " + newResult);
         }

    	return result;
    	
    	
    }
    */

    /**
     * @param modelName
     * @param poMap
     * @param languages
     * @return
     * @throws Exception
     */
    private static Set<String> getLanguagesForResults(Model model, TreeFragment poMap, boolean loggedIn) throws Exception
    {
        Set<String> languages = new HashSet<String>();
        
        if (model.isLocalizedResultPattern())
        {
            for (String key : poMap.keySet())
            {
                List<LocalizedTripleObject> objects = poMap.get(key);
                for (LocalizedTripleObject object : objects)
                {
                    if (object.getLanguage() == null)
                    {
                        languages.add("");
                    }
                    else
                    {
                        languages.add(object.getLanguage());
                    }
                    if (object instanceof TreeFragment && model.getPredicate(key).isResource())
                    {
                        Querier querier = QuerierFactory.newQuerier(loggedIn);
                        Model subModel = ModelList.getInstance().getModelByAlias(model.getPredicate(key).getResourceModel());
                        TreeFragment subResource = querier.details(subModel.getName(), ((TreeFragment) object).getSubject(), "*");
                        languages.addAll(getLanguagesForResults(subModel, subResource, loggedIn));
                        querier.release();
                    }
                }
            }
        }
        if (model.isGlobalResultPattern())
        {
            languages.add("");
        }
        return languages;
    }

    /**
     * @param modelName
     * @param poMap
     * @param languages
     * @return
     * @throws Exception
     */
    private static Set<String> getLanguagesForMatches(Model model, TreeFragment poMap, boolean loggedIn) throws Exception
    {
        Set<String> languages = new HashSet<String>();
        
        if (model.isLocalizedMatches())
        {
            for (String key : poMap.keySet())
            {
                List<LocalizedTripleObject> objects = poMap.get(key);
                for (LocalizedTripleObject object : objects)
                {
                    if (object.getLanguage() == null)
                    {
                        languages.add("");
                    }
                    else
                    {
                        languages.add(object.getLanguage());
                    }
                    if (object instanceof TreeFragment && model.getPredicate(key).isResource())
                    {
                        Querier querier = QuerierFactory.newQuerier(loggedIn);
                        Model subModel = ModelList.getInstance().getModelByAlias(model.getPredicate(key).getResourceModel());
                        TreeFragment subResource = querier.details(subModel.getName(), ((TreeFragment) object).getSubject(), "*");
                        languages.addAll(getLanguagesForMatches(subModel, subResource, loggedIn));
                        querier.release();
                    }
                }
            }
        }
        if (model.isGlobalMatches())
        {
            languages.add("");
        }
        return languages;
    }

    /*
    private static  List<ReplaceResult> replacePattern(Map<String, List<LocalizedTripleObject>> poMap, Predicate predicate, String lang, boolean loggedIn, ReplaceResult oldReplaceResult)
    {
        return replacePattern(poMap, predicate, lang, loggedIn, "", oldReplaceResult);
    }
    
    */
    /**
     * @param poMap
     * @param line
     * @param strings
     * @param predicate
     * @return
     */
    /*
    private static List<ReplaceResult> replacePattern(Map<String, List<LocalizedTripleObject>> poMap, Predicate predicate, String lang, boolean loggedIn, String prefix, ReplaceResult oldReplaceResult)
    {
    	//logger.info("----------------Replacing-----------------");
    	//logger.info("Predicate: " + predicate);
    	//Map<String, TreeFragment>  replacedPatternMap = new LinkedHashMap<String, TreeFragment>();
    	List<ReplaceResult> replaceResults = new ArrayList<ReplaceResult>();
        String line = oldReplaceResult.getResult();
    	//List<String> strings = new ArrayList<String>();
        if (line.contains("<" + prefix + predicate.getId() + ">"))
        {
            for (LocalizedTripleObject value : poMap.get(predicate.getId()))
            {
                try
                {
                    if (lang.equals(value.getLanguage()) || "".equals(value.getLanguage()) || (!predicate.isLocalized() && value.getLanguage() == null) || ("".equals(lang) && (value.getLanguage() == null || value.getLanguage().equals(PropertyReader.getProperty("escidoc.cone.language.default")))))
                    {
                        String newPart = line.replace("<" + prefix + predicate.getId() + ">", value.toString().replace(":", "&#x3A;").replace(",", "&#x2C;"));
                        
                        //If pattern without conditons contains predicate, add value to possible sorting criterias of this result part 
                        
                        ReplaceResult replaceRes = new ReplaceResult(newPart);
                        
                        if(oldReplaceResult!=null)
                    	{
                        	//logger.info("Add to " +  newPart + " " + oldReplaceResult.getValueMap());
                    		replaceRes.getValueMap().putAll(oldReplaceResult.getValueMap());
                    	}
                        
                        if(replaceTokens(line).contains(("<" + prefix + predicate.getId() + ">")))
                        {
                        	
                        	
                        	 //logger.info("Added: " + value.toString() + " to map " + newPart);

                             List<LocalizedTripleObject> subList = new ArrayList<LocalizedTripleObject>();
                             subList.add(value);
                            
                             replaceRes.getValueMap().put(prefix + predicate.getId(), subList);
                        }
                        //replacedPatternMap.put(newPart, sortingTreeFrag);
                        //strings.add(newPart);
                        replaceResults.add(replaceRes);
                    }
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        else if (line.contains("<" + prefix + predicate.getId() + "|"))
        {
            for (LocalizedTripleObject value : poMap.get(predicate.getId()))
            {
                try
                {
                    if (!predicate.isResource() && (value instanceof TreeFragment && (lang.equals(value.getLanguage()) || value.getLanguage() == null || "".equals(value.getLanguage()) || ("".equals(lang) && value.getLanguage().equals(PropertyReader.getProperty("escidoc.cone.language.default"))))))
                    {
                        TreeFragment treeValue = (TreeFragment) value;
                        for (String subPredicateName : treeValue.keySet())
                        {
                        	replaceResults.addAll(replacePattern(treeValue,  predicate.getPredicate(subPredicateName), lang, loggedIn, prefix + predicate.getId() + "|", oldReplaceResult));
                        	//logger.info("Replace Results after " + prefix + subPredicateName + ": " +replaceResults);
                        }
                    }
                    else if (predicate.isResource() && value instanceof TreeFragment && predicate.isIncludeResource())
                    {
                        Querier querier = QuerierFactory.newQuerier(loggedIn);
                        TreeFragment treeFragment = querier.details(predicate.getResourceModel(), ((TreeFragment)value).getSubject(), lang);
                        querier.release();
                        Model newModel = ModelList.getInstance().getModelByAlias(predicate.getResourceModel());
                        for (String subPredicateName : treeFragment.keySet())
                        {
                        	replaceResults.addAll(replacePattern(treeFragment,  newModel.getPredicate(subPredicateName), lang, loggedIn, prefix + predicate.getId() + "|", oldReplaceResult));
                        }
                    }
                    else if (predicate.isResource() && value instanceof LocalizedString && predicate.isIncludeResource())
                    {
                        Querier querier = QuerierFactory.newQuerier(loggedIn);
                        TreeFragment treeFragment = querier.details(predicate.getResourceModel(), ((LocalizedString)value).getValue(), lang);
                        querier.release();
                        Model newModel = ModelList.getInstance().getModelByAlias(predicate.getResourceModel());
                        for (String subPredicateName : treeFragment.keySet())
                        {
                        	replaceResults.addAll(replacePattern(treeFragment, newModel.getPredicate(subPredicateName), lang, loggedIn, prefix + predicate.getId() + "|", oldReplaceResult));
                        }
                    }
                        
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        }

        return replaceResults;
    }
    
    */
    
    /**Simple version of pattern replace, used for sorting maps
     * 
     */
    /*
    private static List<ReplaceResult> simpleReplacePattern(Map<String, List<LocalizedTripleObject>> poMap, String line, String predicateName, ReplaceResult oldReplaceResult)
    {
    		List<ReplaceResult> replaceResults = new ArrayList<ReplaceResult>();
            //List<String> strings = new ArrayList<String>();
            if (line.contains("<" + predicateName + ">"))
            {
                for (LocalizedTripleObject value : poMap.get(predicateName))
                {
                    try
                    {
                        
                            String newPart = line.replace("<" + predicateName + ">", value.toString().replace(":", "&#x3A;").replace(",", "&#x2C;"));
                            
                            ReplaceResult replaceRes = new ReplaceResult(newPart);

                            if(replaceTokens(line).contains(("<" + predicateName + ">")))
                            {

                                 List<LocalizedTripleObject> subList = new ArrayList<LocalizedTripleObject>();
                                 subList.add(value);
                                 replaceRes.getValueMap().put(predicateName, subList);
                            }
                            replaceResults.add(replaceRes);
                        
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            }
            
            

            return replaceResults;
    	
    	
    	
    	
    	
    
    }
    
*/
    /**
     * @param string
     * @return
     */
    private static String replaceTokens(String string)
    {
        if (string == null)
        {
            return null;
        }
        String newString;
        do
        {
            newString = string;
            for (ReplacePattern pattern : replacePattern)
            {
                Matcher matcher = pattern.getPattern().matcher(string);
                if (matcher.find())
                {
                    string = matcher.replaceFirst(pattern.getReplace());
                    break;
                }
            }
        }
        while (!string.equals(newString));
        
        return string;
    }

    public static String escapeForItqlObject(String result)
    {
        return result.replace("'", "\\'");
    }
    
    public static String escapeForSqlObject(String result)
    {
        return result.replace("'", "\\'");
    }

    public static List<Pair<LocalizedString>> buildMatchStringFromModel(String modelName, String id, TreeFragment values, boolean loggedIn) throws Exception
    {
        Set<String> languages = new HashSet<String>();
        Model model = ModelList.getInstance().getModelByAlias(modelName);
        
        List<Pair<LocalizedString>> results = new ArrayList<Pair<LocalizedString>>();
        
        languages = getLanguagesForMatches(model, values, loggedIn);

        for (String lang : languages)
        {
            String matchString = id + getMatchString(model.getPredicates(), values, lang, loggedIn);
            Pair<LocalizedString> pair = new Pair<LocalizedString>(lang, new LocalizedString(matchString));
            results.add(pair);
        }
        
        return results;
    }
    
    public static String getMatchString(List<Predicate> predicates, TreeFragment values, String lang, boolean loggedIn) throws Exception
    {
        StringWriter result = new StringWriter();
        
        for (Predicate predicate : predicates)
        {
            if (predicate.isSearchable() && values.get(predicate.getId()) != null && values.get(predicate.getId()).size() > 0)
            {
                for (LocalizedTripleObject value : values.get(predicate.getId()))
                {
                    if (value.getLanguage() == null || "".equals(value.getLanguage()) || lang.equals(value.getLanguage()))
                    {
                        if (predicate.isResource() && value instanceof TreeFragment)
                        {
                            Querier querier = QuerierFactory.newQuerier(loggedIn);
                            
                            String id = ((TreeFragment) value).getSubject();
                            
                            TreeFragment treeFragment = querier.details(predicate.getResourceModel(), id, "*");
                            Model newModel = ModelList.getInstance().getModelByAlias(predicate.getResourceModel());
                            result.append(getMatchString(newModel.getPredicates(), treeFragment, lang, loggedIn));
                            querier.release();
                        }
                        else if (value instanceof LocalizedString)
                        {
                            result.append("|");
                            result.append(((LocalizedString) value).getValue());
                        }
                        else if (value instanceof TreeFragment)
                        {
                            result.append(getMatchString(predicate.getPredicates(), (TreeFragment) value, lang, loggedIn));
                        }
                    }
                }
            }
        }
        return result.toString();
    }
    
    
    public static void printOutMap(Map<String, TreeFragment> map)
    {
    	if(map!=null)
    	{
    		for(Entry<String, TreeFragment> entry : map.entrySet())
        	{
        		logger.info("Map Key: " + entry.getKey());
        		if(entry.getValue()!=null)
        		{
        			logger.info("Map value: " + entry.getValue().toString2());
        		}
        		else
        		{
        			logger.info("Map value: " + entry.getValue());
        		}
        		
        	}
    	}
    	
    }
}
