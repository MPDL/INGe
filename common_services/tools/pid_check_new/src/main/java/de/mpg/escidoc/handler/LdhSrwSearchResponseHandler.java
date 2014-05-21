package de.mpg.escidoc.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class LdhSrwSearchResponseHandler extends DefaultHandler
{
    private static Logger logger = Logger.getLogger(SrwSearchResponseHandler.class);

    private boolean inComponent = false;
    
    private StringBuffer currentContent;  
    
    private String currentLocator;
    private List<String> locators = new ArrayList<String>();
    
    private String escidocId;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        logger.debug("startElement uri=<" + uri + "> localName = <" + localName + "> qName = <" + qName + "> attributes = <" + attributes + ">");
        
        if ("escidocComponents:component".equals(qName))
        {
            inComponent = true;
        }
        else if (inComponent && "escidocComponents:content".equals(qName) && "external-url".equals(attributes.getValue("storage")))
        {
            currentLocator = attributes.getValue("xlink:href"); 
            logger.info("locator found <" + currentLocator + ">");
            locators.add(escidocId + " | " + currentLocator);
        }
        else if (!inComponent && "escidocItem:item".equals(qName))
        {
            escidocId = attributes.getValue("xlink:href").replace("/ir/item/", "");
        }
        
        currentContent = new StringBuffer();
    }
    
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        logger.debug("endElement   uri=<" + uri + "> localName = <" + localName + "> qName = <" + qName + ">");
        
        if ("escidocComponents:component".equals(qName))
        {
            inComponent = false;
        }
        
        currentContent = null;       
    }
    
    @Override
    public final void characters(char[] ch, int start, int length) throws SAXException
    {   
        if (currentContent == null)
            return;
    }

    public List<String> getLocators()
    {
        return locators;
    }  
}
