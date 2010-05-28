package de.mpg.escidoc.pubman.init;

import java.io.File;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.helpers.DefaultHandler;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.framework.PropertyReader;

public class GenreServlet extends HttpServlet 
{
	
	public void init() throws ServletException 
	{
		try 
		{
			
			File file = ResourceUtil.getResourceAsFile(PropertyReader.getProperty("escidoc.pubman.genres.configuration"));
			File defaultFile = ResourceUtil.getResourceAsFile("WEB-INF/classes/Genres.xml");
			String dir = defaultFile.getAbsolutePath().substring(0, defaultFile.getAbsolutePath().lastIndexOf(File.separator));
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			DefaultHandler handler = new GenreHandler(dir);
			
			parser.parse(file, handler);
		} 
		catch (Exception e) 
		{
			throw new ServletException(e);
		}
	}

}
