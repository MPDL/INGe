package org.fao.oa.ingestion.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class EimsItemSAXParser extends DefaultHandler
{
    public EimsItemSAXParser()
    {
        super();
    }

    public static void main(String... strings) throws SAXException, IOException, Exception
    {
        XMLReader reader = XMLReaderFactory.createXMLReader();
        //SAXParser reader =  SAXParserFactory.newInstance().newSAXParser();
        EimsItemSAXParser handler = new EimsItemSAXParser();
        //reader.setFeature("http://xml.org/say/features/validation", true);
        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);
        System.out.println(System.getProperty("org.xml.sax.driver"));
        System.out.println(reader.getClass().getName());
        //FileReader eimscdr = new FileReader(IngestionProperties.get("eims.export.file.location") + "eimsexport.xml");
        FileReader fr = new FileReader("/home/frank/data/AGRIS_FAO/20090910-FaodocExport/M-2.xml");
        reader.parse(new InputSource(fr));
    }

    public void startDocument()
    {
        //System.out.println("Start document");
    }

    public void endDocument()
    {
        //System.out.println("End document");
    }

    public void startElement(String uri, String name, String qName, Attributes atts)
    {
        /*
        if ("".equals(uri))
        {
            System.out.println(qName);
            for (int i = 0; i < atts.getLength(); i++)
            {
                String attname = atts.getQName(i);
                System.out.print(attname);
                String value = atts.getValue(i);
                System.out.println("\t" + value);
            }
        }
        else
        {
            System.out.println("{" + uri + "}" + name);
            for (int i = 0; i < atts.getLength(); i++)
            {
                String attname = atts.getQName(i);
                System.out.print(attname);
                String value = atts.getValue(i);
                System.out.println("\t" + value);
            }
        }
        */
        /*
        if (name.equalsIgnoreCase("identifier"))
        {
            System.out.println("eims identifier ");
        }
        */
    }

    public void endElement(String uri, String name, String qName)
    {
        /*
        if ("".equals(uri))
            System.out.println("End element: " + qName);
        else
            System.out.println("End element:   {" + uri + "}" + name);
            */
            
    }

    public void characters(char ch[], int start, int length)
    {
        //System.out.print("Characters:    \"");
        for (int i = start; i < start + length; i++)
        {
            /*
            switch (ch[i])
            {
                case '\\':
                    System.out.print("\\\\");
                    break;
                case '"':
                    System.out.print("\\\"");
                    break;
                case '\n':
                    System.out.print("\\n");
                    break;
                case '\r':
                    System.out.print("\\r");
                    break;
                case '\t':
                    System.out.print("\\t");
                    break;
                default:
                    System.out.print(ch[i]);
                    break;
            }
            */
            //System.out.print(ch[i]);
        }
        //System.out.print("\n");
    }

}
