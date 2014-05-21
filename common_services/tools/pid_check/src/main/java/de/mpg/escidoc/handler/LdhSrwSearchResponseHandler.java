package de.mpg.escidoc.handler;

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

    public String getLocator()
    {
        return currentLocator;
    }
}
