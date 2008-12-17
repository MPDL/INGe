package de.mpg.escidoc.services.syndicationmanager.feed;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.log4j.Logger;

import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndPerson;
import com.sun.syndication.feed.synd.SyndPersonImpl;

import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.ItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.interfaces.SearchResultElement;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.framework.PropertyReader; 
import de.mpg.escidoc.services.search.Search;
import de.mpg.escidoc.services.search.bean.SearchBean;
import de.mpg.escidoc.services.search.query.ItemContainerSearchResult;
import de.mpg.escidoc.services.search.query.PlainCqlQuery;
import de.mpg.escidoc.services.syndicationmanager.SyndicationManagerException;
import de.mpg.escidoc.services.syndicationmanager.Utils;


public class Feed extends SyndFeedImpl { 


    private static final Logger logger = Logger.getLogger(Feed.class);

    /** EJB instance of search service. */
    
    private Search itemContainerSearch = new SearchBean();
    
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
	
	private Map paramHash = new HashMap<String, String>();
	
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
	
	
	private void populateParamsFromUri(String uri) throws SyndicationManagerException
	{
		Utils.checkName(uri, "Uri is empty");
		
		String um = getUriMatcher();
		Utils.checkName(um, "Uri matcher is empty");
		
		Matcher m = Pattern.compile(um, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(uri);
		if (m.find())
		{
			for (int i = 0; i < m.groupCount(); i++) 
				paramHash.put((String)paramList.get( i ), m.group(i + 1));
		}
		
	}
	
	public String populateQueryWithParams() throws SyndicationManagerException
	{
		String q = getQuery();
		Utils.checkName(q, "Query pattern is empty");
		
		String um = getUriMatcher();
		Utils.checkName(um, "Uri matcher is empty");

		for ( String key : (Set<String>) paramHash.keySet() )
			q = q.replaceAll(Utils.quoteReplacement(key) , (String) paramHash.get(key) );
		
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

	
	public void populateEntries(String uri) throws SyndicationManagerException
	{
 
		logger.info("uri:" + uri);
		
		populateParamsFromUri(uri);
		
		//set feedType
		String ft = (String) paramHash.get("${feedType}");
		if ( ! Utils.findInList(getFeedTypes().split(","), ft) )
		{
			throw new SyndicationManagerException("Requested feed type: " + ft + " is not supported");
		}
		setFeedType( ft );
		
		
		//prepare search request
		String retrieveQuery = populateQueryWithParams();
		logger.info("Generated uri:" + retrieveQuery);
		
        PlainCqlQuery pcq = new PlainCqlQuery(retrieveQuery);
           
		pcq.setMaximumRecords(new NonNegativeInteger(String.valueOf(getMaximumRecords())));
        pcq.setSortKeys(getSortKeys());
        
        logger.info("MaximumRecords():" + pcq.getMaximumRecords());
        
        //perform search
        ItemContainerSearchResult result = null;
		try 
		{
			result = itemContainerSearch.searchForItemContainer(pcq);
		} 
		catch (Exception e) 
		{
			throw new SyndicationManagerException("Problems by ItemContainerSearch: ", e);
		}
		
		List<SearchResultElement> results = result.getResultList();        
        logger.info("found items: "+  results.size());

        //populate entires
	 	setEntries(transformToEntryList(results));
        
        //Set PublishedDate of the feed to the PublishedDate of the latest  Entry
        //or, if no entires presented, to the current date
	 	List e = getEntries();
        setPublishedDate(
        		Utils.checkList(e)  ?
        		  ((SyndEntry)e.get(0)).getPublishedDate() :
        		  new Date()
        );
        
	}
	
	
	
	private List transformToEntryList(List results)
	{
	
		List entries = new ArrayList();
        XmlTransforming xt = new XmlTransformingBean();
        
        for( int i = 0; i < results.size(); i++ ) {
        	//check if we have found an item
        	if( results.get( i ) instanceof ItemResultVO ) {
        		// cast to PubItemResultVO 
        		ItemResultVO ir = (ItemResultVO)results.get( i );
        	 	PubItemVO pi = new PubItemVO( ir );
        	 	MdsPublicationVO md = pi.getMetadata();
        	 	
        	 	SyndEntry se = new SyndEntryImpl();

        	 	//Content
        	 	SyndContent scont = new SyndContentImpl();
        	 	scont.setType("application/xml");
        	 	try {
//        	 		logger.info("XML"  + xt.transformToItem( pi ));
        	 		// For the initial implementation 
        	 		// 1) the complete PubItem will be in the enty, 
        	 		// not the md-record since no transformation md-record -> XML is implemented
        	 		// 2) CDATA is used for atom/rss compatibility
        	 		// TODO: resolve the issues
					scont.setValue(
							"<![CDATA[" + 
								xt.transformToItem( pi ) +
							"]]>"
					);
				} 
        	 	catch (TechnicalException e) 
				{
					throw new RuntimeException("Cannot transform to XML: ", e);
				}; 
				
        	 	se.setContents(Arrays.asList(scont));
        	 	
        	 	//Title
        	 	se.setTitle( md.getTitle().getValue() );
        	 	
        	 	//Description ??? optional
        	 	List abs = md.getAbstracts();
        	 	SyndContent sc = new SyndContentImpl();
        	 	if ( Utils.checkList(abs) )
        	 	{
        	 		sc.setValue(((TextVO)abs.get(0)).getValue());
        	 	}
        	 	else
        	 	{
        	 		sc.setValue( md.getTitle().getValue() );
        	 	}	
        	 	
        	 	se.setDescription(sc);
        	 		
        	 	//Category
        	 	TextVO subj = md.getSubject();
        	 	if( subj != null && Utils.checkVal( subj.getValue() ) )
        	 	{
            	 	List categories = new ArrayList();
        	 		SyndCategory scat = new SyndCategoryImpl();
        	 		scat.setName(subj.getValue());
        	 		categories.add(scat);
        	 		se.setCategories( categories );
        	 	}
        	 		
        	 	
        	 	if ( Utils.checkList(md.getCreators())  )
        	 	{
        	 		List authors = new ArrayList();
        	 		List contributors = new ArrayList();
        	 		SyndPerson sp;

        	 		for (  CreatorVO creator : (List<CreatorVO>) md.getCreators()  )
        	 		{
        	 			//					String crs = creator.getPerson() != null  ?
        	 			//							creator.getPerson().getCompleteName() : 
        	 			//								creator.getOrganization().getName().getValue();  
        	 			String crs = creator.getPerson() != null  ?
        	 					Utils.join( Arrays.asList(
        	 						 creator.getPerson().getFamilyName()
        	 						,creator.getPerson().getGivenName()        	 						
        	 					), ", ")
        	 						: creator.getOrganization().getName().getValue();  
//        	 					logger.info("cerator--->" + crs);
//        	 					logger.info("Role--->" + creator.getRole());

        	 					if ( creator.getRole() == CreatorRole.AUTHOR )
        	 					{
        	 						//Authors
        	 						sp = new SyndPersonImpl();
        	 						sp.setName(crs);
        	 						authors.add(sp);
        	 					} 
        	 					else // ( creator.getRole() == CreatorRole.CONTRIBUTOR )
        	 					{
        	 						//Contributors
        	 						sp = new SyndPersonImpl();
        	 						sp.setName(crs);
        	 						contributors.add(sp);
        	 					}

        	 		} 
        	 		se.setAuthors(authors);
        	 		se.setContributors(contributors);
        	 	}
        	 	
        	 	//Contents ???
        	 	//se.setContents(contents)
        	 	
        	 	//Link to the PubItem http://dev-pubman.mpdl.mpg.de:8080/pubman/item/escidoc:12713:2
        	 	String pubmanUrl;
				try {
					pubmanUrl = PropertyReader.getProperty("escidoc.pubman.instance.url");
				} 
				catch (Exception e) 
				{
					throw new RuntimeException("cannot load property: escidoc.pubman.instance.url", e);
				}
        	 	
        	 	se.setLink( pubmanUrl + "/item/" + pi.getLatestRelease().getObjectIdAndVersion() );
        	 	
        	 	//Uri ????
        	 	se.setUri( se.getLink() );
        	 	
        	 	//Entry UpdatedDate ??? 
        	 	se.setUpdatedDate( pi.getModificationDate() );
        	 	
        	 	//Entry PublishedDate ???
        	 	se.setPublishedDate( pi.getLatestRelease().getModificationDate() );
        	
        	 	entries.add(se);
        	 	
        	}
        	
        }	
        return entries;
        
	}
	
	
}
