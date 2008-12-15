package de.mpg.escidoc.services.syndicationmanager;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.sun.mail.iap.ByteArray;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

import de.mpg.escidoc.services.syndicationmanager.feed.Feed;

public class Syndication implements SyndicationHandler {

    private Logger logger = Logger.getLogger(Syndication.class);
	
	
	private String explainXML;
	private Feeds feeds;

	public Syndication() throws IOException, SyndicationManagerException 
	{
		explainXML = Utils.getResourceAsString("./resources/feeds.xml");
		feeds = Feeds.readFeedsFromXml("./resources/feeds-digester-rules.xml",
				"./resources/feeds.xml");
	}

	public String explainFeedsXML() {
		return explainXML;
	}

	public String[] getFeedList() {
		List fs = feeds.getFeeds();
		String[] fl = new String[(int) fs.size()];
		int i = 0;
		for (Feed f : (List<Feed>)fs) {
			fl[i++] = f.getUri();
		}
		return fl;
	}

	public String[] getFeedFormatList(String uri) 
	{
		String ft = feeds.matchFeedByUri(uri).getFeedTypes();
		return splitFeedTypes(ft);
	}
	
//	public String[] getFeedFormatListById(String uri) 
//	{
//		String ft = feeds.getFeedByUri(uri).getFeedTypes();
//		return splitFeedTypes(ft);
//	}

	
	private String[] splitFeedTypes(String feedTypes)
	{
		StringTokenizer st = new StringTokenizer(feedTypes, ",");
		String[] result = new String[st.countTokens()];
		int i = 0;
		while (st.hasMoreTokens())
			result[i++] = st.nextToken().trim();
		return result;
	}
	
	public byte[] getFeed(String uri) throws SyndicationManagerException, IOException, URISyntaxException, FeedException 
	{
		 
		Feed f = feeds.matchFeedByUri(uri);
		f.populateEntries(uri);
		
		Writer writer = new StringWriter();
        SyndFeedOutput output = new SyndFeedOutput();
        output.output(f, writer);
        
		return writer.toString().getBytes();
	}

}
