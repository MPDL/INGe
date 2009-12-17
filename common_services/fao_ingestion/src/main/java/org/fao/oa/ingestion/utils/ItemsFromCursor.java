package org.fao.oa.ingestion.utils;

import java.io.FileInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;

import com.sun.xml.stream.events.XMLEventAllocatorImpl;

@SuppressWarnings("restriction")
public class ItemsFromCursor {
    static XMLEventAllocator allocator = null;

    public static void get(String filename) throws Exception {

        try {
            XMLInputFactory xmlif = XMLInputFactory.newInstance();
            System.out.println("FACTORY: " + xmlif);
            xmlif.setEventAllocator(new XMLEventAllocatorImpl());
            allocator = xmlif.getEventAllocator();

            XMLStreamReader xmlr = xmlif.createXMLStreamReader(
                        filename,
                        new FileInputStream(filename));

            int eventType = xmlr.getEventType();

            while (xmlr.hasNext()) {
                eventType = xmlr.next();

                if ((eventType == XMLStreamConstants.START_ELEMENT)
                        && xmlr.getLocalName()
                                   .equals("item")) {
                    StartElement event = getXMLEvent(xmlr)
                                             .asStartElement();
                    System.out.println("EVENT: " + event.toString());
                }
            }
        } catch (XMLStreamException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /** Get the immutable XMLEvent from given XMLStreamReader using XMLEventAllocator */
    private static XMLEvent getXMLEvent(XMLStreamReader reader)
        throws XMLStreamException {
        return allocator.allocate(reader);
    }
}

