package org.fao.oa.ingestion.utils;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

@SuppressWarnings("restriction")
public interface ComponentParser {
	  
	  /**
	   * Parse some XML data using the supplied stax reader.
	   * @param staxXmlReader STAX reader.
	   * @throws XMLStreamException
	   */
	  public void parse(XMLStreamReader staxXmlReader) throws XMLStreamException;
	 
	}
