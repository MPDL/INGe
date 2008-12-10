package de.mpg.escidoc.pubman.init;

import java.io.File;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.helpers.DefaultHandler;

import de.mpg.escidoc.services.common.util.ResourceUtil;

public class GenreServlet extends HttpServlet 
{
	
	public void init() throws ServletException 
	{
		try 
		{
			
			File file = ResourceUtil.getResourceAsFile("WEB-INF/classes/Genres.xml");
			String dir = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separator));
			
			System.out.println("Dir: " + dir);
			
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
