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
 * Feed collection class for eSciDoc syndication manager.  
 *    
 * @author Vlad Makarenko  (initial creation) 
 * @author $Author$ (last modification)
 * $Revision$
 * $LastChangedDate$ 
 */

package de.mpg.escidoc.services.syndication;

import java.net.URL;
import java.util.ArrayList; 
import java.util.List; 
 
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.log4j.Logger; 
 
import de.mpg.escidoc.services.syndication.feed.Feed;

public class Feeds 
{
	
    private static final Logger logger = Logger.getLogger(Feeds.class);
	
    /* Comments for the entire feeds collection */
    private String comments;
    
    /* Feed array */
	private List<Feed> feeds = new ArrayList<Feed>();
	
	
	/**
	 * Comments getter
	 * @return <code>comments
	 */
	public String getComments() 
	{
		return comments;
	}

	/**
	 * Comments setter
	 * @param comments
	 */
	public void setComments(String comments) 
	{
		this.comments = comments;
	}

	/**
	 * Feeds getter.
	 * @return feeds
	 */
	public List<Feed> getFeeds() 
	{
		return feeds;
	}

	/**
	 * Feeds setter.
	 * @param feeds
	 */
	public void setFeeds(List<Feed> feeds) 
	{
		this.feeds = feeds;
	}

	/**
	 * Add new <code>feed</code>.
	 * @param feed
	 */
	public void addFeed( Feed f )
	{ 
		feeds.add( f );
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		String str = "";
		for ( Feed f : (List<Feed>) feeds )
			str += f.toString() + "\n";
		return str; 
	}

	/**
	 * Find feed which URI exactly equals to <code>feedId</code>
	 * @param feedId - URI is used for the value
	 * @return
	 */
	public Feed getFeedByUri( String feedId ) 
	{
		for ( Feed f : (List<Feed>) feeds )
			if ( feedId.equals( f.getUri() ) )
				return f;
		return null; 
	}
	
	/**
	 * Find feed which URI matches <code>uriMatcher</code>.
	 * <code>uriMatcher</code> should be calculated before the method usage.
	 * @see de.mpg.escidoc.services.syndication.feed.Feed#generateUriMatcher(String) generateUriMatcher 
	 * @param uri is URI to be matched
	 * @return matched <code>feed</code>
	 */
	public Feed matchFeedByUri( String uri ) 
	{
		for ( Feed f : (List<Feed>) feeds )
		{
			if ( uri.matches( f.getUriMatcher()) )
				return f;
		}
		return null; 
	}
	
    
    /**
     * Unmarshalling of the feed definitions XML file on hand of the feeds digester rules   
     * @param rulesFileName is URI of the digester rules file 
     * @param feedsFileName is URI of the feed definitions file 
     * @return feed collection class
     * @throws SyndicationException
     */
    public static Feeds readFeedsFromXml(String rulesFileName, String feedsFileName ) throws SyndicationException  
    {
		URL rules = Feeds.class.getClassLoader().getResource( rulesFileName );

		Digester digester = DigesterLoader.createDigester( rules );
		digester.setNamespaceAware(false);
		
		URL input = Feeds.class.getClassLoader().getResource( feedsFileName );
		Feeds fs = null;
		try 
		{
			fs = (Feeds) digester.parse( input  );
		} 
		catch (Exception e) 
		{
			throw new SyndicationException("Cannot parse: " + feedsFileName, e);
		}

    	return fs;
    }	
    
	
}
