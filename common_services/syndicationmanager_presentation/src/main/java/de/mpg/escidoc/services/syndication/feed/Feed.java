/*
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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

/**
 * Feed class for eSciDoc syndication manager.  
 *    
 * @author Vlad Makarenko  (initial creation) 
 * @author $Author:$ (last modification)
 * $Revision:$
 * $LastChangedDate:$ 
 */

package de.mpg.escidoc.services.syndication.feed;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndImage;
import com.sun.syndication.feed.synd.SyndPerson;
import com.sun.syndication.feed.synd.SyndPersonImpl;

import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;

import de.mpg.escidoc.services.syndication.SyndicationException;
import de.mpg.escidoc.services.syndication.Utils;


public class Feed extends SyndFeedImpl 
{ 

	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(Feed.class);

    /** EJB instance of search service. */
//    private Search itemContainerSearch = new SearchBeanLocal();
//    @EJB
//    private Search itemContainerSearch;
    
    private static final String CDATA = "<![CDATA[%s]]>"; 
    
	//Search CQL query 
	//see: http://www.escidoc-project.de/documentation/Soap_api_doc_SB_Search.pdf
	private String query;
	
	//Sort keys 
	private String sortKeys;
	
	//Records limit
	private String maximumRecords;  
	
	//List of the all available types for the feed
	private String feedTypes;
	
	//uriMatcher is RegExp for URI matching
	private String uriMatcher; 
	
	//use caching if "true", do not use, otherwise
	private String cachingStatus;

	//TTL for until the channel, it will be recached after  
	private String cachingTtl;
	
	//List of the parameters generated according to the URI
	private List paramList = new ArrayList<String>();
	
	//Hash of the parameters/values
	private Map<String, String> paramHash = new HashMap<String, String>();
	
	
	/**
	 * Query getter.
	 * @return <code>query</code>
	 */
	public String getQuery()  
	{
		return query;
	}
	
	
	/**
	 * Query setter. 
	 * @param query
	 */
	public void setQuery(String query) 
	{
		this.query = query;
	}

	/**
	 * SortKeys getter
	 * @return <code>sortKeys</code>
	 */
	public String getSortKeys() {
		return sortKeys;
	}


	/**
	 * SortKeys setter.
	 * @param sortKeys
	 */
	public void setSortKeys(String sortKeys) {
		this.sortKeys = sortKeys;
	}


	/**
	 * MaximumRecords getter.
	 * @return <code>MaximumRecords</code>
	 */
	public String getMaximumRecords() {
		return maximumRecords;
	}


	/**
	 * MaximumRecords setter.
	 * @param maximumRecords
	 */
	public void setMaximumRecords(String maximumRecords) {
		this.maximumRecords = maximumRecords;
	}


	/**
	 * FeedTypes getter.
	 * @return <code>FeedTypes</code>
	 */
	public String getFeedTypes() 
	{
		return feedTypes;
	}

	/**
	 * FeedTypes setter.
	 * @param feedTypes
	 */
	public void setFeedTypes(String feedTypes) 
	{
		this.feedTypes = feedTypes;
	}

	/**
	 * UriMatcher getter.
	 * @return <code>UriMatcher</code>
	 */
	public String getUriMatcher() {
		return uriMatcher;
	}


	/**
	 * UriMatcher setter.
	 * @param uriMatcher
	 */
	public void setUriMatcher(String uriMatcher) {
		this.uriMatcher = uriMatcher;
	}
	

	/**
	 * CachingStatus getter.
	 * @return <code>CachingStatus</code>
	 */
	public String getCachingStatus() {
		return cachingStatus;
	}


	/**
	 * CachingStatus setter.
	 * @param status
	 */
	public void setCachingStatus(String status) {
		this.cachingStatus = status;
	}


	/**
	 * CachingTtl getter.
	 * @return <code>CachingTtl</code>
	 */
	public String getCachingTtl() {
		return cachingTtl;
	}


	/**
	 * CachingTtl setter.
	 * @param cachingTtl
	 */
	public void setCachingTtl(String cachingTtl) {
		this.cachingTtl = cachingTtl;
	}


	/**
	 * ParamList getter
	 * @return <code>ParamList</code>
	 */
	public List getParamList() {
		return paramList;
	}



	/**
	 * ParamList setter.
	 * @param paramList
	 */
	public void setParamList(List paramList) {
		this.paramList = paramList;
	}



	/**
	 * Add new SyndCategory. 
	 * @param sc  
	 */
	public void addCategory( SyndCategory sc ) 
	{
	    getCategories().add( sc );  
	}
	
	
	/**
	 * Add new SyndPerson as Author.
	 * @param sp
	 */
	public void addAuthor( SyndPerson sp ) 
	{
		getAuthors().add( sp );  
	}
	
	/* (non-Javadoc)
	 * @see com.sun.syndication.feed.synd.SyndFeedImpl#setUri(java.lang.String)
	 */
	@Override
	public void setUri(String uri) {
		super.setUri(uri);
		generateUriMatcher(uri);
	}
	
	/* (non-Javadoc)
	 * @see com.sun.syndication.feed.synd.SyndFeedImpl#clone()
	 */
	@Override
	public Object clone(){
		Object clone = null;
		try 
		{
			clone = super.clone();
		} 
		catch(CloneNotSupportedException e) 
		{
			// should never happen
		}
		return clone;
	}



	/**
	 * Generate <code>UriMatcher</code> for the feed and list of the parameters <code>paramList</code>  
	 * according to the <code>uri</code>. 
	 * @param uri
	 */
	public void generateUriMatcher(final String uri)  
	{
		String result = new String(uri);
		
		//property regexp in uri
		String regexp = "(\\$\\{[\\w.]+?\\})";
		
		Matcher m = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(uri);
		while (m.find())
		{
			String param = m.group(1);
			paramList.add(param);
			result = result.replaceFirst(Utils.quoteReplacement(param), "\\(.+\\)?");
		}
		setUriMatcher(result);
	}
	
	
	/**
	 * Populate parameters with the values taken from the certain <code>uri</code> 
	 * and populate <code>paramHash</code> with the parameter/value paars.
	 * @param uri
	 * @throws SyndicationException
	 */
	private void populateParamsFromUri(String uri) throws SyndicationException
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

		//special handling of Organizational Unit Feed
		//TODO: should be resolved other way!
		if(getUriMatcher().equals("(.+)?/syndication/feed/(.+)?/publications/organization/(.+)?"))
		{
			TreeMap<String, String> outm = Utils.getOrganizationUnitTree();
			String oid = (String) paramHash.get("${organizationId}");
			for( Map.Entry<String, String> entry: outm.entrySet() )
			{
				if (entry.getValue().equals(oid))
				{
					paramHash.put("${organizationName}", entry.getKey());
				}
			}
		}
		
		logger.info("parameters: " + paramHash);
		
	}
	
	/**
	 * Populate feed/channel element with the parameters from the <code>paramHash</code>  
	 * @param name - the name of the element, is only needed for check <code>value</code> message    
	 * @param value is value of the the element to be populated 
	 * @return populated <code>value</code>
	 * @throws SyndicationException
	 */
	public String populateFieldWithParams(String name, String value) throws SyndicationException
	{
		Utils.checkName(value, "Field <"+ name + "> is empty");
		
		Utils.checkCondition( paramHash.keySet().size()==0, "No parameters for Uri matcher");
		for ( String key : (Set<String>) paramHash.keySet() )
		{
			value = value.replaceAll(Utils.quoteReplacement(key) , (String) paramHash.get(key) );
		}
		return value;
	}
	
	
	/**
	 * List of feed/channel elements to be populated by @see #populateFieldWithParams(String, String)
	 * @throws SyndicationException
	 */
	private void populateFeedElementsWithParams() throws SyndicationException 
	{
		
		setTitle(populateFieldWithParams("title", getTitle()));
		setDescription(populateFieldWithParams("description", getDescription()));
		
		setLink(populateFieldWithParams("link", getLink()));
		setUri(populateFieldWithParams("uri", getUri()));
		
		setQuery(populateFieldWithParams("query", getQuery()));
		
		SyndImage si = getImage(); 
		si.setLink(populateFieldWithParams("image/link", si.getLink()));
		si.setUrl(populateFieldWithParams("image/url", si.getUrl()));
		setImage(si);
		
	}	
	
	public String toString()
	{
		
		String str =
				"[" 
				+ 	"query: " + getQuery() + "\n"
				+ 	"feedTypes: " + getFeedTypes() + "\n"
				+ 	"uriMatcher: " + getUriMatcher() + "\n"
				+ 	"paramList: " + paramList + "\n"
				+ 	"cachingStatus: " + cachingStatus + "\n"
				+ 	"cachingTtl: " + cachingTtl + "\n"
				+ 	super.toString()
				+ "]"
			;
		
		return str;
	}

	
	/**
	 * Main method for entries population of the feed/channel according to the <code>uri</code> 
	 * @param uri is URI for feed parameters
	 * @throws SyndicationException
	 */
	public void populateEntries(String uri) throws SyndicationException
	{
 
		logger.info("uri:" + uri);
		
		populateParamsFromUri(uri);
		
		//set feedType
		String ft = (String) paramHash.get("${feedType}");
		if ( ! Utils.findInList(getFeedTypes().split(","), ft) )
		{
			throw new SyndicationException("Requested feed type: " + ft + " is not supported");
		}
		setFeedType( ft );
		
		/* SET LIMITATIONS */
		//set setMaximumRecords to 15 for RSS 0.9, 0.91N, 091U 
		if ( isRSS_09_or_091N_or_091U() && Integer.parseInt(getMaximumRecords()) > 15 )
			setMaximumRecords("15");
		
		//length of the channel/image title of RSS 0.9  <= 40 
		if ( ft.equals("rss_0.9") )
		{
			setTitle(Utils.cutString(getTitle(), 40, "..."));
			getImage().setTitle(Utils.cutString(getImage().getTitle(), 40, "..."));
		}
		
		
		populateFeedElementsWithParams();
		
        
		//search for itemList 
		String itemListXml = performSearch( getQuery(), getMaximumRecords(), getSortKeys() );
        
        //populate entires
	 	setEntries( transformToEntryList( itemListXml ) );
	 	
        
        //Set PublishedDate of the feed to the PublishedDate of the latest  Entry
        //or, if no entires presented, to the current date
	 	List e = getEntries();
        setPublishedDate(
        		Utils.checkList(e)  ?
        		  ((SyndEntry)e.get(0)).getPublishedDate() :
        		  new Date()
        );
        
	}

	/**
	 * Search for the items for feed entries population. 
	 * The HTTP request to the SearchAndExport WEB interface is used 
	 * @param query is CQL query.
	 * @param maximumRecords is the limit of the search 
	 * @param sortKeys 
	 * @return item list XML
	 * @throws SyndicationException
	 */
	private String performSearch(String query, String maximumRecords, String sortKeys) throws SyndicationException 
	{
		
		URL url;
		try {
			url = new URL(
					paramHash.get("${baseUrl}") + "/search/SearchAndExport?" +
					"cqlQuery=" + URLEncoder.encode(query, "UTF-8") + 
					"&maximumRecords=" + URLEncoder.encode(maximumRecords, "UTF-8") + 
					"&sortKeys=" + URLEncoder.encode(sortKeys, "UTF-8") + 
					"&exportFormat=XML" +  
					"&sortOrder=descending" + 
					"&language=all"
			);
		} 
		catch (Exception e) 
		{
			throw new SyndicationException("Wrong URL:", e);
		}

		logger.info("Search URL:" + url.toString());
		Object content;
		URLConnection uconn;
		try 
		{
			uconn = url.openConnection();
			if ( !(uconn instanceof HttpURLConnection) )
	            throw new IllegalArgumentException(
	                "URL protocol must be HTTP." 
	            );
			HttpURLConnection conn = (HttpURLConnection)uconn;
	
			InputStream stream =  conn.getErrorStream( );
	        if ( stream != null )
	        {
	        	conn.disconnect();	
	        	throw new SyndicationException(Utils.getInputStreamAsString( stream ));
	        }
	        else if ( (content = conn.getContent( )) != null && content instanceof InputStream )
	            content = Utils.getInputStreamAsString( (InputStream)content );
	        else
	        {
	        	conn.disconnect();	
	        	throw new SyndicationException("Cannot retrieve content from the HTTP response");
	        }
	        conn.disconnect();
	        
			return (String)content;
		}
		catch (Exception e) 
		{
			throw new SyndicationException(e);
		}
		
	}

	
	/**
	 * Transformation method takes ItemList XML and transforms it to the list of 
	 * syndication entries (<code><List>SyndEntry</code>)  
	 * @param itemListXml 
	 * @return <List>SyndEntry
	 * @throws SyndicationException
	 */
	private List transformToEntryList(String itemListXml) throws SyndicationException
	{
		
		List entries = new ArrayList();
		XmlTransforming xt = new XmlTransformingBean();
		
		List<ItemVO> itemListVO = null; 
		try 
		{
			itemListVO = (List<ItemVO>) xt.transformToItemList(itemListXml);
		} 
		catch (Exception e) 
		{
			throw new SyndicationException("Cannot transform item list XML to List<ItemVO>:", e);
		}        
		
		for( ItemVO item: itemListVO ) 
		{
			PubItemVO pi = (PubItemVO) item;
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
						contentIsEncoded() ? 
							xt.transformToItem( pi ) :
							String.format(CDATA, xt.transformToItem( pi ))
				);
				if ("atom_0.3".equals(getFeedType()))
					scont.setMode(Content.XML);
			} 
			catch (TechnicalException e) 
			{
				throw new RuntimeException("Cannot transform to XML: ", e);
			}; 

			se.setContents(Arrays.asList(scont));

			//
			
			se.setTitle(
					// the title length rss_0.9, rss_0.91N, rss_0.91U  must be <=100	
					isRSS_09_or_091N_or_091U() ?
						Utils.cutString(md.getTitle().getValue(), 100, "...") :	
						md.getTitle().getValue() 
			);

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
			TextVO subj = md.getFreeKeywords(); 
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
//				List contributors = new ArrayList();
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

							sp = new SyndPersonImpl();
							sp.setName(crs);
							authors.add(sp);
							// TODO: authors & contributors separately
//							if ( creator.getRole() == CreatorRole.AUTHOR )
//							{
//								//Authors
//								authors.add(sp);
//							} 
//							else // if ( creator.getRole() == CreatorRole.CONTRIBUTOR )
//							{
//								//Contributors
//								contributors.add(sp);
//							}

				} 
				se.setAuthors(authors);
//				se.setContributors(contributors);
			}

			//Contents ???
			//se.setContents(contents)

			se.setLink( paramHash.get("${baseUrl}") + "/pubman/item/" + pi.getLatestRelease().getObjectIdAndVersion() );

			//Uri ????
			se.setUri( se.getLink() );

			//Entry UpdatedDate ??? 
			se.setUpdatedDate( pi.getModificationDate() );

			//Entry PublishedDate ???
			se.setPublishedDate( pi.getLatestRelease().getModificationDate() );

			entries.add(se);

		}	
		return entries;
		
	}
	
	/**
	 * Check the encoding status of the <code>feedType</code>. 
	 * @return <code>true</code> for all RSS feeds, <code>false</code> otherwise 
	 */
	private boolean contentIsEncoded()
	{
		return getFeedType().contains("rss_");
	}
	
	/**
	 * <code>True</code> for RSS 0.9, 0.91*  
	 * @return
	 */
	private boolean isRSS_09_or_091N_or_091U()
	{
		return "rss_0.9".equals(getFeedType()) || getFeedType().contains("rss_0.91");
	}
	
	
}
