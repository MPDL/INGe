package de.mpg.escidoc.services.syndication;

import java.net.URL;
import java.util.ArrayList; 
import java.util.List; 
 
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.log4j.Logger; 
 
import de.mpg.escidoc.services.syndication.feed.Feed;

public class Feeds {
	
    private static final Logger logger = Logger.getLogger(Feeds.class);
	
	private List feeds = new ArrayList<Feed>();
	
	public List getFeeds() {
		return feeds;
	}

	public void setFeeds(List feeds) {
		this.feeds = feeds;
	}

	public void addFeed( Feed f )
	{ 
		feeds.add( f );
	}
	
	public String toString()
	{
		String str = "";
		for ( Feed f : (List<Feed>) feeds )
			str += f.toString() + "\n";
		return str; 
	}

	public Feed getFeedByUri( String feedId ) {
		for ( Feed f : (List<Feed>) feeds )
			if ( feedId.equals( f.getUri() ) )
				return f;
		return null; 
	}
	
	public Feed matchFeedByUri( String uri ) {
		for ( Feed f : (List<Feed>) feeds )
		{
			if ( uri.matches( f.getUriMatcher() ) )
				return f;
		}
		return null; 
	}
	
    
    public static Feeds readFeedsFromXml(String rulesFileName, String feedsFileName ) throws SyndicationManagerException  
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
			throw new SyndicationManagerException("Cannot parse: " + feedsFileName, e);
		}

    	return fs;
    }	
    
	
}
