package de.mpg.escidoc.services.fledgeddata.sitemap;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class Sitemap
{
	private static final Logger LOGGER = Logger.getLogger(Sitemap.class);

	public static String createSitemap(String ListIdentifiersXml) throws Exception
	{
		StringBuffer sitemapXml = new StringBuffer();
		sitemapXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sitemapXml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\">");

		sitemapXml.append(createSitemapEntry(ListIdentifiersXml));
		
		sitemapXml.append("</urlset>");

		return sitemapXml.toString();
	}
	
	private static String createSitemapEntry(String xml) throws ParserConfigurationException, SAXException, IOException
	{
		StringBuffer sitemapXml = new StringBuffer();		
        DocumentBuilderFactory docFact = DocumentBuilderFactory.newInstance();
        DocumentBuilder bd = docFact.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml.toLowerCase().trim()));
        Document doc = bd.parse(is);

        //TODO: split > 50.000
        //TODO: Crone job        
        NodeList locNodes = doc.getElementsByTagName("identifier");
        NodeList dateNodes = doc.getElementsByTagName("datestamp");
		if (locNodes == null || dateNodes==null)
		{
			throw new SAXException ("[FDS] xml document does not contain necessary elements.");
		}
		
		for (int i=0; i< locNodes.getLength(); i++)
		{
			sitemapXml.append("<url>");
	    		sitemapXml.append("<loc>" + locNodes.item(i).getTextContent() + "</loc>");
	    		sitemapXml.append("<lastmod>" + dateNodes.item(i).getTextContent() + "</lastmod>");
	    	sitemapXml.append("</url>");
		}
		
		return sitemapXml.toString();
	}
}