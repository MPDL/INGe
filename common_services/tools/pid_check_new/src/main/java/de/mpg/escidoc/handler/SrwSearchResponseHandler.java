package de.mpg.escidoc.handler;

import java.util.HashMap;

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
    private String itemUrl = "";
    
    private String pidToSearchFor = "";
    
    private boolean isObjectPid = false;
    private boolean isVersionPid = false;
    private boolean isComponentPid = false;
    
    private StringBuffer currentContent;  
    
    private String currentPropVersion;
    private String matchingPropVersion;
    
    private String currentComponentAttributes;
    private String matchingComponentAttributes;
    private String currentComponentTitle;
    
    
    private HashMap<String, String> results = new HashMap<String, String>();

    
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
            
            currentComponentAttributes = attributes.getValue("xlink:href"); 
            currentComponentTitle = attributes.getValue("xlink:title");  
        }
        else if ("escidocItem:item".equals(qName))
        {
            itemUrl = attributes.getValue("xlink:href"); // pattern /item/<escidocId>
            
            results.put("itemUrl", itemUrl.replace("/ir", ""));
        }
        else if ("prop:version".equals(qName))
        {
            String s = attributes.getValue("xlink:href");
            
            currentPropVersion = s;
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
        
        if (inPropPid && !inComponent)
        {
            currentContent.append(ch, start, length);
            propPid = currentContent.toString();
            if (propPid.equalsIgnoreCase(pidToSearchFor))
            {
                isObjectPid = true;
            }
            logger.debug("propPid =<" + propPid + ">");

        }  
        else if (inVersionPid)
        {
            currentContent.append(ch, start, length);
            versionPid = currentContent.toString();
            if (versionPid.equalsIgnoreCase(pidToSearchFor))
            {
                isVersionPid = true;
                matchingPropVersion = currentPropVersion;
                results.put("matchingVersionUrl", matchingPropVersion.replace("/ir", "")); // pattern /item/<escidocId>:<version>
            }
            logger.debug("versionPid =<" + versionPid + ">");
            logger.debug("currentPropVersion =<" + currentPropVersion + ">");
        }
        else if (inPropPid && inComponent)
        {
            currentContent.append(ch, start, length);
            propPid = currentContent.toString();
            if (propPid.equalsIgnoreCase(pidToSearchFor))
            {
                isComponentPid = true;
                matchingComponentAttributes = currentComponentAttributes;
                results.put("matchingComponentUrl", matchingComponentAttributes.replace("/ir", "").replace("/components", "") + "/" + currentComponentTitle); // pattern /item/<escidocId>/component/<componentId>/<title>
            }
            logger.debug("versionPid =<" + versionPid + ">");
            logger.debug("currentPropVersion =<" + currentPropVersion + ">");
        }
    }

    public void setPidToSearchFor(String pid)
    {
        this.pidToSearchFor = pid;        
    }
    
    public boolean isObjectPid()
    {
        return this.isObjectPid;
    }
    
    public boolean isVersionPid()
    {
        return this.isVersionPid;
    }
    
    public boolean isComponentPid()
    {
        return this.isComponentPid;
    }

    public String getItemUrl()
    {
        return results.get("itemUrl");
    }

    public String getVersionUrl()
    {
        return results.get("matchingVersionUrl");
    }

    public String getComponentUrl()
    {
        return results.get("matchingComponentUrl");
    }
}
