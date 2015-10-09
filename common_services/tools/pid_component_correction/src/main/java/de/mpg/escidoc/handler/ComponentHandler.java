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
	public static final String DC_TITLE_KEY = "dc:title";
	public static final String PROP_PID_KEY = "prop:pid";
	public static final String FOXML_CONTENT_LOCATION_KEY = "foxml:contentLocation";

	private static Logger logger = Logger.getLogger(ComponentHandler.class);
	
	private boolean inRelsExt = false;
	private boolean inFile = false;
	private boolean inContent = false;
	
	private boolean inRelsExtAndPropPid = false;
    private boolean inFileAndDcTitle = false;
    
    private String contentLocation = "";
    
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
        else if (PROP_PID_KEY.equals(qName) && inRelsExt)
        {
            inRelsExtAndPropPid = true;
        }
        
        if ("foxml:datastream".equals(qName) && "content".equals(attributes.getValue("ID")))
        { 
            inContent = true;
            logger.debug(" startElement inContent= " + inContent);
        } 
        else if (FOXML_CONTENT_LOCATION_KEY.equals(qName) && inContent)
        {
        	contentLocation = attributes.getValue("TYPE");
        	s.put(FOXML_CONTENT_LOCATION_KEY, contentLocation);
        }
        
        if ("foxml:datastream".equals(qName) && "escidoc".equals(attributes.getValue("ID")))
        { 
            inFile = true;
            logger.debug(" startElement inFile= " + inFile);
        }
        else if (DC_TITLE_KEY.equals(qName) && inFile)
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
        else if (PROP_PID_KEY.equals(qName) && inRelsExt)
        {
            inRelsExtAndPropPid = false;
        }
        
        if ("foxml:datastream".equals(qName) && inContent)
        { 
            inContent = false;
            logger.debug(" endElement inContent= " + inContent);
        }
        
        if ("foxml:datastream".equals(qName) && inFile)
        { 
            inFile = false;
            logger.debug(" endElement inFile= " + inFile);
        }
        else if (DC_TITLE_KEY.equals(qName) && inFile)
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
            s.put(PROP_PID_KEY, currentContent.toString());
            logger.debug("prop:pid =<" + currentContent.toString() + ">");
        }  
        else if (inFileAndDcTitle)
        {
        	s.put(DC_TITLE_KEY, currentContent.toString());
            logger.debug("dc:title =<" + currentContent.toString() + ">");
        }    
    }
    
    public final String getPid()
    {
    	return s.get(PROP_PID_KEY);
    }
    
    public final String getFilename()
    {
    	return s.get(DC_TITLE_KEY);
    }
    
    public final String getContentLocation()
    {
    	return s.get(FOXML_CONTENT_LOCATION_KEY);
    }
    
    
    public final Map<String, String> getComponentMap()
    {
    	return s;
    }
    

}
