package org.fao.oa.ingestion.utils;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

@SuppressWarnings("restriction")
public class EimsItemParser implements ComponentParser
{
    public void parseNOTWORKING(XMLStreamReader staxXmlReader) throws XMLStreamException
    {
        Stax.moveTo("identifier", staxXmlReader);
        String identifier = staxXmlReader.getElementText();
        System.out.println("identifier: " + identifier);
        Stax.moveTo("jobno", staxXmlReader);
        String jobno = staxXmlReader.getElementText();
        System.out.println("job number: " + jobno);
        Stax.moveTo("langkey", staxXmlReader);
        String langkey = staxXmlReader.getElementText();
        System.out.println("language key: " + langkey);
        Stax.moveTo("dep_date", staxXmlReader);
        String dep_date = staxXmlReader.getElementText();
        System.out.println("dep:date: " + dep_date);
        Stax.moveTo("waicent_date", staxXmlReader);
        String waicent_date = staxXmlReader.getElementText();
        System.out.println("waicent_date: " + waicent_date);
        Stax.moveTo("waicent_published", staxXmlReader);
        String waicent_published = staxXmlReader.getElementText();
        System.out.println("waicent_published: " + waicent_published);
        Stax.moveTo("maintype", staxXmlReader);
        String maintype = staxXmlReader.getElementText();
        System.out.println("maintype: " + maintype);
        Stax.moveTo("language", staxXmlReader);
        String language = staxXmlReader.getElementText();
        System.out.println("language: " + language);
        Stax.moveTo("title", staxXmlReader);
        String titleLang = staxXmlReader.getAttributeValue(0);
        String title = staxXmlReader.getElementText();
        System.out.println("title: " + titleLang + "  " + title);
        Stax.moveTo("subtitle", staxXmlReader);
        String subtitleLang = staxXmlReader.getAttributeValue(0);
        String subtitle = staxXmlReader.getElementText();
        System.out.println("subtitle: " + subtitleLang + "  " + subtitle);
        Stax.moveTo("abstract", staxXmlReader);
        String abstrLang = staxXmlReader.getAttributeValue(0);
        String abstr = staxXmlReader.getElementText();
        System.out.println("abstract: " + abstrLang + "  " + abstr);
        Stax.moveTo("author", staxXmlReader);
        String author = staxXmlReader.getElementText();
        System.out.println("author: " + author);
        Stax.moveTo("fao", staxXmlReader);
        String fao = staxXmlReader.getElementText();
        System.out.println("FAO: " + fao);
        Stax.moveTo("publisher", staxXmlReader);
        String publisher = staxXmlReader.getElementText();
        System.out.println("Publisher: " + publisher);
        Stax.moveTo("type", staxXmlReader);
        String type = staxXmlReader.getElementText();
        System.out.println("publication type: " + type);
        Stax.moveTo("date", staxXmlReader);
        String dareScheme = staxXmlReader.getAttributeValue(0);
        String date = staxXmlReader.getElementText();
        Stax.moveTo("pages", staxXmlReader);
        String pages = staxXmlReader.getElementText();
        Stax.moveTo("isbn", staxXmlReader);
        String isbn = staxXmlReader.getElementText();
        Stax.moveTo("issn", staxXmlReader);
        String issn = staxXmlReader.getElementText();
        Stax.moveTo("noInSerie", staxXmlReader);
        String noInSerie = staxXmlReader.getElementText();
        Stax.moveTo("division", staxXmlReader);
        String division = staxXmlReader.getElementText();
        Stax.moveTo("department", staxXmlReader);
        String department = staxXmlReader.getElementText();
        Stax.moveTo("service", staxXmlReader);
        String service = staxXmlReader.getElementText();
        Stax.moveTo("PDF_URL", staxXmlReader);
        String pdf_urlNote = staxXmlReader.getAttributeValue(0);
        String pdf_url = staxXmlReader.getElementText();
        Stax.moveTo("hardcopy", staxXmlReader);
        String hardcopy = staxXmlReader.getElementText();
        Stax.moveTo("budgetcode", staxXmlReader);
        String budgetcode = staxXmlReader.getElementText();
        Stax.moveTo("bib_reference", staxXmlReader);
        String bib_reference = staxXmlReader.getElementText();
        Stax.moveTo("remarks", staxXmlReader);
        String remarks = staxXmlReader.getElementText();
        Stax.moveTo("AGRIS", staxXmlReader);
        String agris = staxXmlReader.getElementText();
        Stax.moveTo("ispartofseries", staxXmlReader);
        String ispartofseries = staxXmlReader.getElementText();
    }

    public void parse(XMLStreamReader parser) throws XMLStreamException
    {
        while (true)
        {
            int event = parser.next();
            if (event == XMLStreamConstants.END_DOCUMENT)
            {
                parser.close();
                break;
            }
            if (event == XMLStreamConstants.START_ELEMENT)
            {
                System.out.print(parser.getLocalName());
                if (parser.getAttributeCount() > 0)
                {
                    for (int a = 0; a < parser.getAttributeCount(); a++)
                    {
                        System.out.print(" (" + parser.getAttributeLocalName(a) + ": " + parser.getAttributeValue(a));
                    }
                    System.out.print("): ");
                }
                else
                {
                    System.out.print(": ");
                }
            }
            
            if (event == XMLStreamConstants.CHARACTERS)
            {
                if (parser.getTextLength() > 0)
                {
                System.out.println(parser.getText());
                }
                else
                {
                    System.out.println("");
                }
            }
            
        }
    }
}
