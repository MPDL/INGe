package org.fao.oa.ingestion.utils;

import java.io.FileInputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

@SuppressWarnings("restriction")
public class XmlStreamFilter implements javax.xml.stream.StreamFilter {

    /**
     *
     * @param filename
     * @throws Exception
     */
    public static void filter(String filename) throws Exception {
        

        XMLInputFactory xmlif = null;

        try {
            xmlif = XMLInputFactory.newInstance();
            xmlif.setProperty(
                    XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,
                    Boolean.TRUE);
            xmlif.setProperty(
                    XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES,
                    Boolean.FALSE);
            xmlif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
            xmlif.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println("FACTORY: " + xmlif);
        System.out.println("filename = " + filename);

        try {
            FileInputStream fis = new FileInputStream(filename);

            XMLStreamReader xmlr = xmlif.createFilteredReader(
                        xmlif.createXMLStreamReader(fis),
                        new XmlStreamFilter());

            int eventType = xmlr.getEventType();
            printEventType(eventType);

            while (xmlr.hasNext()) {
                eventType = xmlr.next();
                printEventType(eventType);
                printName(xmlr, eventType);
                printText(xmlr);

                if (xmlr.isStartElement()) {
                    printAttributes(xmlr);
                }

                printPIData(xmlr);
                System.out.println("-----------------------------");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @param eventType
     * @return
     */
    public static final String getEventTypeString(int eventType) {
        switch (eventType) {
        case XMLEvent.START_ELEMENT:
            return "START_ELEMENT";

        case XMLEvent.END_ELEMENT:
            return "END_ELEMENT";

        case XMLEvent.PROCESSING_INSTRUCTION:
            return "PROCESSING_INSTRUCTION";

        case XMLEvent.CHARACTERS:
            return "CHARACTERS";

        case XMLEvent.COMMENT:
            return "COMMENT";

        case XMLEvent.START_DOCUMENT:
            return "START_DOCUMENT";

        case XMLEvent.END_DOCUMENT:
            return "END_DOCUMENT";

        case XMLEvent.ENTITY_REFERENCE:
            return "ENTITY_REFERENCE";

        case XMLEvent.ATTRIBUTE:
            return "ATTRIBUTE";

        case XMLEvent.DTD:
            return "DTD";

        case XMLEvent.CDATA:
            return "CDATA";
        }

        return "UNKNOWN_EVENT_TYPE";
    }

    private static void printEventType(int eventType) {
        System.out.print("EVENT TYPE(" + eventType + "):");
        System.out.println(getEventTypeString(eventType));
    }

    private static void printName(
        XMLStreamReader xmlr,
        int eventType) {
        if (xmlr.hasName()) {
            System.out.println("HAS NAME: " + xmlr.getLocalName());
        } else {
            System.out.println("HAS NO NAME");
        }
    }

    private static void printText(XMLStreamReader xmlr) throws Exception {
        if (xmlr.hasText()) {
            System.out.println("HAS TEXT: " + xmlr.getText());
        } else {
            System.out.println("HAS NO TEXT");
        }
    }
    
    private static void printElementText(XMLStreamReader xmlr) throws Exception {
        if (xmlr.isCharacters()) {
            System.out.println("HAS TEXT: " + xmlr.getElementText());
        } else {
            System.out.println("HAS NO TEXT");
        }    
        
    }

    private static void printPIData(XMLStreamReader xmlr) {
        if (xmlr.getEventType() == XMLEvent.PROCESSING_INSTRUCTION) {
            System.out.println(" PI target = " + xmlr.getPITarget());
            System.out.println(" PI Data = " + xmlr.getPIData());
        }
    }

    private static void printAttributes(XMLStreamReader xmlr) {
        if (xmlr.getAttributeCount() > 0) {
            System.out.println("\nHAS ATTRIBUTES: ");

            int count = xmlr.getAttributeCount();

            for (int i = 0; i < count; i++) {
                QName name = xmlr.getAttributeName(i);
                String namespace = xmlr.getAttributeNamespace(i);
                String type = xmlr.getAttributeType(i);
                String prefix = xmlr.getAttributePrefix(i);
                String value = xmlr.getAttributeValue(i);

                System.out.println("ATTRIBUTE-PREFIX: " + prefix);
                System.out.println("ATTRIBUTE-NAMESP: " + namespace);
                System.out.println("ATTRIBUTE-NAME:   " + name.toString());
                System.out.println("ATTRIBUTE-VALUE:  " + value);
                System.out.println("ATTRIBUTE-TYPE:  " + type);
                System.out.println();
            }
        } else {
            System.out.println("HAS NO ATTRIBUTES");
        }
    }

    /**
     * Accept only StartElement and EndElement events,Filters out rest of the events.
     * @param reader
     * @return
     */
    public boolean accept(XMLStreamReader reader) {
        if (!reader.isStartElement()  && !reader.isCharacters()) {
            return false;
        } else {
            return true;
        }
    }
}
