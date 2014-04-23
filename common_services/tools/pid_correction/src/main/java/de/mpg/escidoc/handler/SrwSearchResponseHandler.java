package de.mpg.escidoc.handler;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SrwSearchResponseHandler extends DefaultHandler
{
    private static Logger logger = Logger.getLogger(SrwSearchResponseHandler.class);
    
    private boolean inPropPid = false;
    private boolean inVersionPid = false;
    private boolean inComponent = false;
    
    private String propPid = "";
    private String versionPid = "";
    
    private String pidToSearchFor = "";
    
    private boolean isObjectPid = false;
    private boolean isComponentPid = false;
    
    private StringBuffer currentContent;    
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        logger.debug("startElement uri=<" + uri + "> localName = <" + localName + "> qName = <" + qName + "> attributes = <" + attributes + ">");
        
        if ("prop:pid".equals(qName))
        {
            inPropPid = true;
        }
        else if ("version:pid".equals(qName))
        {
            inVersionPid = true;
        }
        else if ("escidocComponents:component".equals(qName))
        {
            inComponent = true;
        }
        
        currentContent = new StringBuffer();
    }
    
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        logger.debug("endElement   uri=<" + uri + "> localName = <" + localName + "> qName = <" + qName + ">");
        
        if ("prop:pid".equals(qName))
        {
            inPropPid = false;
        }
        else if ("version:pid".equals(qName))
        {
            inVersionPid = false;
        }
        else if ("escidocComponents:component".equals(qName))
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
        if (inPropPid)
        {
            currentContent.append(ch, start, length);
            propPid = currentContent.toString();
            if (propPid.equals(pidToSearchFor))
            {
                isObjectPid = true;
            }
            logger.debug("propPid =<" + propPid + ">");
        }  
        else if (inVersionPid)
        {
            currentContent.append(ch, start, length);
            versionPid = currentContent.toString();
            logger.debug("versionPid =<" + versionPid + ">");
        }
    }
    
    public String getObjectPid()
    {
        return propPid;
    }
    
    public String getVersionPid()
    {
        return versionPid;
    }

    public void setPidToSearchFor(String pid)
    {
        this.pidToSearchFor = pid;        
    }
}
