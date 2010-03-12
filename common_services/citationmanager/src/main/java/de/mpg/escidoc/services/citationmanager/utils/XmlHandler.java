package de.mpg.escidoc.services.citationmanager.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlHandler extends DefaultHandler {

	private static final Logger logger = Logger.getLogger(XmlHandler.class);
	String currentElement = null;
	String citationStyle = null;
	String idType = null;
	private Map<Pair, String> citationStyleMap;
	int counter = 0;
	Pair journalIdTypeValue;

	/**
	 * Start reading an XML-File. 
	 * Creates a Map to hold the JournalId-Value-Pair and the corresponding citation style.
	 */
	@Override
	public void startDocument() throws SAXException {
		citationStyleMap = new HashMap<Pair, String>();
		super.startDocument();
	}

	/**
	 * Gets every element and set it to currentElement.
	 */
	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		if ("".equals(uri)) {
			currentElement = name;
		} else {
			currentElement = localName;
		}
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}

	@Override
	public void endElement(String arg0, String arg1, String arg2)
	throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(arg0, arg1, arg2);

	}

	/**
	 * Gets the values of the elements. 
	 * For every id-type of journal a new Pair is created. 
	 * The key is set to the idType. The value is set to the value of the id. 
	 * The journalIdTypeValue and the citationStyle are putted in a HashMap. 
	 */
	public void characters(char ch[], int start, int length) {
		String tempString = new String(ch, start, length);

		if (currentElement.equals("citation-style")& !tempString.trim().equals("")) {
			citationStyle = tempString;
		} else if (currentElement.equals("type")& !tempString.trim().equals("")) {

			idType = tempString.substring(tempString.lastIndexOf("/") + 1);
			journalIdTypeValue = new Pair();
			journalIdTypeValue.setKey(idType);

		} else if (currentElement.equals("value") & !tempString.trim().equals("")) {
			journalIdTypeValue.setValue(tempString);
			citationStyleMap.put(journalIdTypeValue, citationStyle);
		}
	}


	/**
	 * Returns a HashMap with JournalId-Value-Pair and 
	 * the corresponding citation style.
	 * @return
	 */
	public Map<Pair,String> getCitationStyleMap(){
		return this.citationStyleMap;
	}


}
