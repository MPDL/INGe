package de.mpg.escidoc.pubman.init;

import java.io.FileWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.mpg.escidoc.pubman.util.ShortContentHandler;

public class GenreHandler extends ShortContentHandler 
{

	private String genre = null;
	private FileWriter fileWriter = null;
	private Stack<String> stack = new Stack<String>();
	private String dir = null;
	
	private LinkedHashMap<String, String> map = null;
	private LinkedHashMap<String, String> defaultMap = new LinkedHashMap<String, String>();
	
	public GenreHandler(String dir)
	{
		this.dir = dir;
	}
	
	@Override
	public void content(String uri, String localName, String name,
			String content) {
		// TODO Auto-generated method stub
		super.content(uri, localName, name, content);
		System.out.println("content " + content);
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, name);
		
		try
		{
			if ("genre".equals(name))
			{
				fileWriter = new FileWriter(dir + "/Genre_" + genre + ".properties");
				
				for (String key : map.keySet())
				{
					fileWriter.append(key.replace("-", "_"));
					fileWriter.append("=");
					fileWriter.append(map.get(key));
					fileWriter.append("\n");
				}
				
				fileWriter.flush();
				fileWriter.close();
				fileWriter = null;
				
				map = null;
			}
			else if ("group".equals(name) || "field".equals(name))
			{
				stack.pop();
			}
		}
		catch (Exception e) {
			throw new SAXException(e);
		}
		
		System.out.println("end " + name);
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		super.startElement(uri, localName, name, attributes);
		
		try
		{
			if ("default-configuration".equals(name))
			{
				map = defaultMap;
			}
			if ("genre".equals(name))
			{
				genre = attributes.getValue("id");
				if ("DEFAULT".equals(genre))
				{
					map = defaultMap;
				}
				else
				{
					map = (LinkedHashMap<String, String>) defaultMap.clone();
				}
			}
			if ("group".equals(name) || "field".equals(name))
			{
				stack.push(attributes.getValue("id"));
				String currentStack = "";
				for (String element : stack) {
					currentStack += element + "_";
				}
				
				for (int i = 0; i < attributes.getLength(); i++) {
					String key = currentStack + attributes.getQName(i);
					String value = attributes.getValue(i);
					map.put(key, value);
				}
			}
		}
		catch (Exception e) {
			throw new SAXException(e);
		}
		System.out.println("start " + name);
	}
	

}
