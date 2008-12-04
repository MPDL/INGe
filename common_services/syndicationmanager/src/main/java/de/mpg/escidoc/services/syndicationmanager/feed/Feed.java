package de.mpg.escidoc.services.syndicationmanager.feed;

import de.mpg.escidoc.services.search.Search;
import de.mpg.escidoc.services.search.bean.SearchBean;
import de.mpg.escidoc.services.search.query.ItemContainerSearchResult;
import  de.mpg.escidoc.services.search.query.PlainCqlQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Logger; 

import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndPerson;

import de.mpg.escidoc.services.syndicationmanager.SyndicationManagerException;
import de.mpg.escidoc.services.syndicationmanager.Utils;


public class Feed extends SyndFeedImpl { 


    private static final Logger logger = Logger.getLogger(Feed.class);
	
	//Search CQL query 
	//see: http://www.escidoc-project.de/documentation/Soap_api_doc_SB_Search.pdf
	private String query;
	
	//Sort keys 
	private String sortKeys;
	
	//Records limit
	private String maximumRecords;  
	
	//List of the all available types for the feed
	private String feedTypes;
	
	
	private String uriMatcher; 

	
	
	private List paramList = new ArrayList<String>();
	
	public String getQuery()  
	{
		return query;
	}
	
	
	public void setQuery(String query) 
	{
		this.query = query;
	}

	public String getSortKeys() {
		return sortKeys;
	}


	public void setSortKeys(String sortKeys) {
		this.sortKeys = sortKeys;
	}


	public String getMaximumRecords() {
		return maximumRecords;
	}


	public void setMaximumRecords(String maximumRecords) {
		this.maximumRecords = maximumRecords;
	}


	public String getFeedTypes() 
	{
		return feedTypes;
	}

	public void setFeedTypes(String feedTypes) 
	{
		this.feedTypes = feedTypes;
	}

	public String getUriMatcher() {
		return uriMatcher;
	}


	public void setUriMatcher(String uriMatcher) {
		this.uriMatcher = uriMatcher;
	}
	

	public List getParamList() {
		return paramList;
	}



	public void setParamList(List paramList) {
		this.paramList = paramList;
	}



	// add repeatable elements
	public void addCategory( SyndCategory sc ) 
	{
	    getCategories().add( sc );  
	}
	
	
	public void addAuthor( SyndPerson sp ) 
	{
		getAuthors().add( sp );  
	}
	
	@Override
	public void setUri(String uri) {
		super.setUri(uri);
		generateUriMatcher(uri);
	}



	public void generateUriMatcher(final String uri)  
	{
		String result = new String(uri);
		
		//property regexp in uri
		String regexp = "(\\$\\{\\w+?\\})";
		
		Matcher m = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(uri);
		while (m.find())
		{
			String param = m.group(1);
			getParamList().add(param);
			result = result.replaceFirst(Utils.quoteReplacement(param), "\\(.+\\)?");
		}
		setUriMatcher(result);
	}
	
	
	public String populateQueryWithValues(String uri) throws SyndicationManagerException
	{
		Utils.checkName(uri, "Uri is empty");
		
		String q = getQuery();
		Utils.checkName(q, "Query pattern is empty");
		
		String um = getUriMatcher();
		Utils.checkName(um, "Uri matcher is empty");
		
		Matcher m = Pattern.compile(um, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(uri);
		if (m.find())
		{
			for (int i = 0; i < m.groupCount(); i++) 
			{
				q = q.replaceAll(
						Utils.quoteReplacement(
								(String)paramList.get( i )
						)
						, m.group(i + 1)
				);
			}
		}
		
		return q;
	}
	
	
	public String toString()
	{
		
		String str =
				"[" 
				+ 	"query: " + getQuery() + "\n"
				+ 	"feedTypes: " + getFeedTypes() + "\n"
				+ 	"uriMatcher: " + getUriMatcher() + "\n"
				+ 	"paramList: " + paramList + "\n"
				+ 	super.toString()
				+ "]"
			;
		
		return str;
	}

	
	
	public byte[] generateFeed(String uri) throws SyndicationManagerException 
	{
		byte[] res = null;
 
		logger.info("uri:" + uri);
		String retrieveQuery = populateQueryWithValues(uri);
		logger.info("Generated uri:" + retrieveQuery);
		
        PlainCqlQuery pcq = new PlainCqlQuery(retrieveQuery);
           
		logger.info("MaximumRecords():" + getMaximumRecords());
		pcq.setMaximumRecords(new NonNegativeInteger(String.valueOf(getMaximumRecords())));
//        pcq.setSortKeys(getSortKeys());    
 
        SearchBean sb = new SearchBean();
        ItemContainerSearchResult result = null;
		try 
		{ 
			result = sb.searchForItemContainer(pcq);
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block ???????
			e.printStackTrace();
		}
        List pubItemList = result.getResultList();
        
        logger.info("found items: "+  pubItemList.size());
		
		return null;
	}
	
	
}
