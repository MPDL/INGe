package de.mpg.escidoc.services.edoc;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.common.util.creators.Author;
import de.mpg.escidoc.services.common.util.creators.AuthorDecoder;

/**
 * 
 */

/**
 * @author kurt
 *
 */
public class EDocImport extends DefaultHandler
{

	StringWriter newXml = new StringWriter();
	boolean inCreatorstring = false;
	StringWriter creatorString = null;
	
	/**
	 * @param args
	 */
	
	
	public static void main(String[] args) throws Exception
	{
		
		String pathXml = args[0];
		String pathXslt = "edoc-to-escidoc.xslt";
		
		EDocImport eDocImport = new EDocImport(pathXml, pathXslt);
		
	}
	
	public EDocImport(String pathXml, String pathXslt) throws Exception
	{
		XSLTTransform transform = new XSLTTransform();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		File file = new File("./Test.txt");
		System.out.println(file.getAbsolutePath());
		
		transform.transform(ResourceUtil.getResourceAsFile(pathXml), ResourceUtil.getResourceAsFile(pathXslt), baos);

		String result = baos.toString("UTF-8");
		
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		
		parser.parse(new InputSource(new StringReader(result)), this);
		
		OutputStreamWriter fwout = new OutputStreamWriter(new FileOutputStream(new File("edoc_export_out.xml"), false), "UTF-8");
		String resString = getResult();		
		System.out.println(resString);
		fwout.write(resString);
		fwout.close();
	}

	private String getResult() {
		
		return newXml.toString();
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String string = new String(ch, start, length);
		if (inCreatorstring)
		{
			creatorString.append(string);
		}
		else
		{
			newXml.append(escape(string));
		}
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if ("creatorstring".equals(name))
		{
			try
			{
				AuthorDecoder authorDecoder = new AuthorDecoder(creatorString.toString());
				List<Author> authors = authorDecoder.getBestAuthorList();
				for(int i=0; i < authors.size(); i++){
					authors.get(i);
					newXml.append("<mdp:creator role=\"editor\">");
					newXml.append("<e:person>");	
					newXml.append("<e:complete-name>");
					newXml.append(escape(authors.get(i).getGivenName() + " " + authors.get(i).getSurname()));
					newXml.append("</e:complete-name>");
					newXml.append("<e:family-name>");
					newXml.append(escape(authors.get(i).getSurname()));
					newXml.append("</e:family-name>");
					newXml.append("<e:given-name>");
					newXml.append(escape(authors.get(i).getGivenName()));
					newXml.append("</e:given-name>");
					newXml.append("</e:person>");
					newXml.append("</mdp:creator>");
				}				
				
				
			}
			catch (Exception e) {
				e.printStackTrace();
				throw new SAXException(e);
			}
			creatorString = null;
			inCreatorstring = false;
		}
		else
		{
			newXml.append("</");
			newXml.append(name);
			newXml.append(">");
		}
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		if ("creatorstring".equals(name))
		{
			inCreatorstring = true;
			creatorString = new StringWriter();
			
		}
		else
		{
			
			newXml.append("<");
			newXml.append(name);
			for (int i = 0; i < attributes.getLength(); i++) {
				newXml.append(" ");
				newXml.append(attributes.getQName(i));
				newXml.append("=\"");
				newXml.append(escape(attributes.getValue(i)));
				newXml.append("\"");
			}
			newXml.append(">");

		}
			
	}
	
	public String escape(String input)
	{
		if(input != null){
			input = input.replace("&", "&amp;");
			input = input.replace("<", "&lt;");
			input = input.replace("\"", "&quot;");
		}
		return input;
	}

	
}
