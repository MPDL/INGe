package de.mpg.escidoc.handler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
* TODO Description
*
* @author sieders (initial creation)
* @author $Author$ (last modification)
* @version $Revision$ $LastChangedDate$
*
*/

/*
 * Used to parse one single component. Stores the pid and filename
 */

public class ComponentHandler extends DefaultHandler
{
	private static Logger logger = Logger.getLogger(ComponentHandler.class);
	
	private boolean inRelsExt = false;
	private boolean inFile = false;
	
	private boolean inRelsExtAndPropPid = false;
    private boolean inFileAndDcTitle = false;
    
	private StringBuffer currentContent;   
	
	Map<String, String> s = new HashMap<String, String>();
	
	@Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        logger.debug("startElement uri=<" + uri + "> localName = <" + localName + "> qName = <" + qName + "> attributes = <" + attributes + ">");
        
        if ("foxml:datastreamVersion".equals(qName) && attributes.getValue("ID").startsWith("RELS-EXT"))
        { 
            inRelsExt = true;
            logger.debug(" startElement inRelsExt= " + inRelsExt);
        }
        else if ("prop:pid".equals(qName) && inRelsExt)
        {
            inRelsExtAndPropPid = true;
        }
        
        
        if ("foxml:datastream".equals(qName) && "escidoc".equals(attributes.getValue("ID")))
        { 
            inFile = true;
            logger.debug(" startElement inFile= " + inFile);
        }
        else if ("dc:title".equals(qName) && inFile)
        {
        	inFileAndDcTitle = true;
        }
        
        currentContent = new StringBuffer();
        
    }
	

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        logger.debug("endElement   uri=<" + uri + "> localName = <" + localName + "> qName = <" + qName + ">");
        
        if ("foxml:datastreamVersion".equals(qName) && inRelsExt)
        {
            inRelsExt = false;
            logger.debug(" endElement inRelsExt= " + inRelsExt);
        }
        else if ("prop:pid".equals(qName) && inRelsExt)
        {
            inRelsExtAndPropPid = false;
        }
        
        if ("foxml:datastream".equals(qName) && inFile)
        { 
            inFile = false;
            logger.debug(" startElement inFile= " + inFile);
        }
        else if ("dc:title".equals(qName) && inFile)
        {
        	inFileAndDcTitle = false;
        }
        
        currentContent = null;
    }
    
    @Override
    public final void characters(char[] ch, int start, int length) throws SAXException
    {   
        if (currentContent == null)
            return;

        currentContent.append(ch, start, length); 
        
        if (inRelsExtAndPropPid)
        {
            s.put("prop:pid", currentContent.toString());
            logger.debug("prop:pid =<" + currentContent.toString() + ">");
        }  
        else if (inFileAndDcTitle)
        {
        	s.put("dc:title", currentContent.toString());
            logger.debug("dc:title =<" + currentContent.toString() + ">");
        }    
    }
    
    public final String getPid()
    {
    	return s.get("prop:pid");
    }
    
    public final String getFilename()
    {
    	return s.get("dc:title");
    }
    
    public final Map<String, String> getComponentMap()
    {
    	return s;
    }
    

}
