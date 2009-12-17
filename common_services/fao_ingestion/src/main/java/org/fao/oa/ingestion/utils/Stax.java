package org.fao.oa.ingestion.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;

import javanet.staxutils.helpers.EventMatcher;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

@SuppressWarnings("restriction")
public class Stax {

	public static void main(String... strings) {
		String filename = IngestionProperties.get("eims.export.file.location")
				+ "single_item.xml";
		/*
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader reader;
		try {
			reader = factory
					.createXMLStreamReader(new FileInputStream(filename));
		
		while (reader.hasNext()) {
			switch (reader.next()) {
			case XMLStreamConstants.START_ELEMENT:
				getStartElement(reader);
				break;
			case XMLStreamConstants.END_ELEMENT:
				getEndElement(reader);
				break;
			case XMLStreamConstants.CHARACTERS:
				getCharacters(reader);
				break;
			default:
			}
		}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		*/
		Stax stax = new Stax();
		try {
			stax.testReadEimsItem(filename);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private static void getCharacters(XMLStreamReader xmlStreamReader) {
	    System.out.print(xmlStreamReader.getText().trim());
	  }
	 
	 
	  private static void getEndElement(XMLStreamReader xmlStreamReader) {
	    System.out.print("</" + xmlStreamReader.getName() + ">\n");
	  }
	 
	 
	  private static void getStartElement(XMLStreamReader xmlStreamReader) {
	    System.out.print("<" + xmlStreamReader.getName());
	    int cnt = xmlStreamReader.getAttributeCount();
	    for (int i = 0; i < cnt; i++) {
	    	if (xmlStreamReader.getAttributePrefix(i) != "")
	    	{
	      System.out
	        .print(" " + xmlStreamReader.getAttributePrefix(i) + ":" + xmlStreamReader.getAttributeLocalName(i) + "=\"" + xmlStreamReader.getAttributeValue(i) + "\"");
	    	}
	    	else
	    	{
	    		System.out
		        .print(" " + xmlStreamReader.getAttributeLocalName(i) + "=\"" + xmlStreamReader.getAttributeValue(i) + "\"");
	    	}
	      if (i + 1 < cnt) {
	        System.out.print(" ");
	      }
	    }
	    System.out.print(">");
	  }
	  
	  public static void moveTo(String target, XMLStreamReader reader) throws XMLStreamException {

		    for (int event = reader.next(); event != XMLStreamConstants.END_DOCUMENT; event = reader.next()) {

		      if ((event == XMLStreamConstants.START_ELEMENT) && (reader.getLocalName().equals(target))) {
		        return;
		      }
		    }
		  }
		  
		  public static void writeElement(XMLStreamWriter writer, String elementName, String value) throws Exception {
		    writer.writeStartElement(elementName);
		    writer.writeCharacters(value);
		    writer.writeEndElement();
		  }
		  
		  public void testReadEimsItem(String filename) throws Exception {

			    XMLInputFactory factory = (XMLInputFactory) XMLInputFactory.newInstance();
			    XMLStreamReader staxXmlReader = (XMLStreamReader) factory.createXMLStreamReader(new FileReader(filename));

			    StaxParser parser = new StaxParser();
			    parser.registerParser("item", new EimsItemParser());

			    parser.parse(staxXmlReader);

			  }
}
