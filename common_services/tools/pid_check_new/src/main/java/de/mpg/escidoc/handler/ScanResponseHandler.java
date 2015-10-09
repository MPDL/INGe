package de.mpg.escidoc.handler;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Collect all Terms found by a scan operation in a Set. In our case the Terms are handles.
 * Checks the occurence of each handle to be used exactly one time. If the handle is used more than one time it is stored in an extra Set.
 * 
 * @author sieders
 *
 */
public class ScanResponseHandler extends DefaultHandler
{
	private static Logger logger = Logger.getLogger(SrwSearchResponseHandler.class);

    private boolean inTerm = false;
    private boolean inValue = false;
    private boolean inNumberOfRecords = false;
    
	private StringBuffer currentContent;  
	private String currentPid;
	
	private Set<String> pids = new HashSet<String>();
	private Set<String> pidsUsedSeveralTimes = new HashSet<String>();
	
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        logger.debug("startElement uri=<" + uri + "> localName = <" + localName + "> qName = <" + qName + "> attributes = <" + attributes + ">");
        
        if ("term".equals(qName))
        {
            inTerm = true;
        }
        else if (inTerm && "value".equals(qName))
        {
            inValue = true;
        }
        else if (inTerm && "numberOfRecords".equals(qName))
        {
        	inNumberOfRecords = true;
        }
        
        currentContent = new StringBuffer();
    }
    
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        logger.debug("endElement   uri=<" + uri + "> localName = <" + localName + "> qName = <" + qName + ">");
        
        if ("term".equals(qName))
        {
            inTerm = false;
        }
        else if ("value".equals(qName))
        {
        	inValue = false;
        }
        else if ("numberOfRecords".equals(qName))
        {
        	inNumberOfRecords = false;
        }
        
        currentContent = null;       
    }
    
    @Override
    public final void characters(char[] ch, int start, int length) throws SAXException
    {   
        if (currentContent == null)
            return;
        
		if (inTerm && inValue)
        {
            currentContent.append(ch, start, length);
            currentPid = currentContent.toString();
            
            logger.debug("currentPid =<" + currentPid + ">");
            
            this.pids.add(currentPid);

        }  
        else if (inTerm && inNumberOfRecords)
        {
        	currentContent.append(ch, start, length);
        	
        	if (!"1".equals(currentContent.toString()))
        	{
        		logger.error("Handle <" + currentPid + "> exists <" + currentContent.toString() + "> times");
        		this.pidsUsedSeveralTimes.add(currentPid);
        	}
        
        		
        }
    }

    /**
     * Get all pids found in ScanResponse.
     * @return
     */
    public Set<String> getPids()
    {
        return this.pids;
    }  
    
    /**
     * Gets the pids wich are used more than one time.
     * @return
     */
    public Set<String> getPidsUsedSeveralTimes()
    {
        return this.pidsUsedSeveralTimes;
    }  
	

}
