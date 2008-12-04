package de.mpg.escidoc.services.syndicationmanager;

import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import org.xml.sax.SAXException;

import de.mpg.escidoc.services.syndicationmanager.feed.Feed;

public class Syndication implements SyndicationHandler {

	private String explainXML;
	private Feeds feeds;

	public Syndication() throws IOException, SyndicationManagerException 
	{
		explainXML = Utils.getResourceAsString("./resources/feeds.xml");
		feeds = Feeds.parseFeedsHeaders("./resources/feeds-digester-rules.xml",
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

	public String[] getFeedFormatList(String feedId) {

		String ft = feeds.getFeedById(feedId).getFeedTypes();
		StringTokenizer st = new StringTokenizer(ft, ",");
		String[] result = new String[st.countTokens()];
		int i = 0;
		while (st.hasMoreTokens())
			result[i++] = st.nextToken().trim();
		return result;
	}

	public byte[] getFeed(String feedId, String feedFormat) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public byte[] getFeed(String uri) throws SyndicationManagerException {
		
		Feed f = feeds.matchFeedByUri(uri);
		byte[] result = f.generateFeed(uri);
		
		return null;
	}

	public static void main(String[] args) throws IOException, SyndicationManagerException {
		Syndication synd = new Syndication();
		System.out.println("explainFeedsXML" + synd.explainFeedsXML());

		System.out.println("feedList---");
		for (String f : synd.getFeedList())
			System.out.println("feed:" + f);

		System.out.println("feedFormatList---");
		for (String f : synd.getFeedList()) {
			System.out.println("---feed:" + f);
			for (String ff : synd.getFeedFormatList(f)) {
				System.out.println("---format:" + ff);
			}

		}

	}

}
