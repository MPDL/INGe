package org.fao.oa.ingestion.utils;

import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

@SuppressWarnings("restriction")
public class StaxParser implements ComponentParser {

    private Map delegates;
    
    public StaxParser() {
      delegates = new HashMap();
    }
    
    public void parse(XMLStreamReader staxXmlReader) throws XMLStreamException{

      for (int event = staxXmlReader.next(); event != XMLStreamConstants.END_DOCUMENT; event = staxXmlReader.next()) {
        if (event == XMLStreamConstants.START_ELEMENT) {
          String element = staxXmlReader.getLocalName();
          if (delegates.containsKey(element)) {
            ComponentParser parser = (ComponentParser) delegates.get(element);
            parser.parse(staxXmlReader);
          } 
        }
      }

    }
    
    public void registerParser(String name,ComponentParser parser) {
      delegates.put(name,parser);
    }

}
