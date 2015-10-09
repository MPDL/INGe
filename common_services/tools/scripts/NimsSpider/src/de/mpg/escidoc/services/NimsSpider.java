/**
 * 
 */
package de.mpg.escidoc.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author franke
 *
 */
public class NimsSpider extends DefaultHandler {

	private String lastId = null;
	private String lastCreated = null;
	private File currentFile = null;
	private Phase phase = null;
	private boolean inRelsExt = false;
	private boolean copyItem = false;
	private String contextId = null;
	private String objPath = null;
	private String streamPath = null;
	private String objTrgPath = null;
	private String streamTrgPath = null;
	
	List<String> contextList = new ArrayList<String>();
	List<String> components = new ArrayList<String>();
	List<String> tmpComponents = new ArrayList<String>();
	
	private enum Phase {
		INIT, PARSE
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args)  throws Exception {

		if (args.length == 5)
		{
			NimsSpider nimsSpider = new NimsSpider(args[0], args[1], args[2], args[3], args[4]);
		}
		else
		{
			System.err.println("Ouch!");
		}

	}

	public NimsSpider(String objPath, String streamPath, String objTrgPath, String streamTrgPath, String contexts) throws Exception
	{
		this.objPath = objPath;
		this.streamPath = streamPath;
		this.objTrgPath = objTrgPath;
		this.streamTrgPath = streamTrgPath;
		for (String context : contexts.split(",")) {
			contextList.add(context);
		}
		File file = new File(objPath);
		
		search(file);
		
		findComponents(file);
		
		findDatastreams(new File(streamPath));
		
	}
	
	private void search(File dir) throws Exception
	{
		File[] files = dir.listFiles();
		Arrays.sort(files, new FileComparator());
		for (File file : files) {
			if (file.isDirectory())
			{
				search(file);
			}
			else
			{
				currentFile = file;
				SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
				System.out.print(file + " ...");
				try
				{
					phase = Phase.INIT;
					parser.parse(file, this);
					
					phase = Phase.PARSE;
					parser.parse(file, this);
					
					lastCreated = null;
					lastId = null;
					
					if (copyItem)
					{
						System.out.print(" copying ...");
						String path = file.getAbsolutePath().replace(objPath, objTrgPath);
						copy(file, path);
					}
					else
					{
						System.out.print(" ignored ...");
					}
					
					contextId = null;
					copyItem = false;
					tmpComponents.clear();
				}
				catch (Exception e) {
					System.err.println("Error :" + e.getMessage());
				}
				currentFile = null;
			}
		}
	}

	private void findComponents(File dir) throws Exception
	{
		File[] files = dir.listFiles();
		Arrays.sort(files, new FileComparator());
		for (File file : files) {
			if (file.isDirectory())
			{
				findComponents(file);
			}
			else
			{
				if (components.contains(file.getName()))
				{
					String path = file.getAbsolutePath().replace(objPath, objTrgPath);
					copy(file, path);
				}
			}
		}
	}

	private void findDatastreams(File dir) throws Exception
	{
		File[] files = dir.listFiles();
		Arrays.sort(files, new FileComparator());
		for (File file : files) {
			if (file.isDirectory())
			{
				findDatastreams(file);
			}
			else
			{
				String name = file.getName().split("\\+")[0];
				if (components.contains(name))
				{
					String path = file.getAbsolutePath().replace(streamPath, streamTrgPath);
					copy(file, path);
				}
			}
		}
	}
	
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		
		if (phase == Phase.INIT)
		{
			if ("foxml:datastreamVersion".equals(qName))
			{
				String id = attributes.getValue("ID");
				String created = attributes.getValue("CREATED");
				
				if (id.startsWith("RELS-EXT.") && (lastCreated == null || lastCreated.compareTo(created) < 0))
				{
					lastCreated = created;
					lastId = id;
				}
			}
		}
		else if (phase == Phase.PARSE)
		{
			if ("foxml:datastreamVersion".equals(qName) && lastId != null && lastId.equals(attributes.getValue("ID")))
			{
				inRelsExt = true;
			}
			
			if ("rdf:type".equals(qName) && inRelsExt)
			{
				String type = attributes.getValue("rdf:resource");
				if ("http://escidoc.de/core/01/resources/OrganizationalUnit".equals(type))
				{
					copyItem = true;
				}

			}
			
			if ("srel:context".equals(qName) && inRelsExt)
			{
				contextId = attributes.getValue("rdf:resource").substring(12);
				if (contextList.contains(contextId))
				{
					copyItem = true;
					components.addAll(tmpComponents);
				}

			}
			
			if ("srel:component".equals(qName) && inRelsExt)
			{
				if (contextId == null)
				{
					tmpComponents.add(attributes.getValue("rdf:resource").substring(12).replace(":", "_"));
				}
				else if (copyItem)
				{
					components.add(attributes.getValue("rdf:resource").substring(12).replace(":", "_"));
				}
			}
		}
	}

	
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("foxml:datastreamVersion".equals(qName))
		{
			inRelsExt = false;
		}
	}

	@Override
	public void endDocument() throws SAXException {
		System.out.println(currentFile + " : " + lastId);
	}

	public class FileComparator implements Comparator<File>
	{

		@Override
		public int compare(File o1, File o2) {
			if (o1 == null && o2 == null)
			{
				return 0;
			}
			else if (o1 == null)
			{
				return -1;
			}
			else
			{
				return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
			}

		}
		
		
	}
	
	private void copy(File file, String path) throws Exception
	{
		
		String[] pathArr = path.split("/|\\\\");
		String currentPath = "";

		System.out.println("Copying " + file + " to " + path);
		
		for (int i = 0; i < pathArr.length - 1; i++) {
			if (i > 0)
			{
				currentPath += System.getProperty("file.separator");
			}
			currentPath += pathArr[i];
			if (!"".equals(currentPath))
			{
				File dir = new File(currentPath);
				if (!(dir.exists()) )
				{
					System.out.println("Creating directory " + dir);
					dir.mkdir();
				}
			}
		}
		
		byte[] buffer = new byte[2048];
		int read = 0;
		FileInputStream fis = new FileInputStream(file);
		FileOutputStream fos = new FileOutputStream(path);
		while ((read = fis.read(buffer)) >= 0)
		{
			fos.write(buffer, 0, read);
		}
		fis.close();
		fos.close();
		System.out.println("Copying complete");
	}
	
}
