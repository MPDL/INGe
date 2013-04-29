package de.mpg.escidoc.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import de.mpg.escidoc.handler.PreHandler.Type;

/**
 * 
 * TODO Description
 *
 * @author sieders (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class AssertionHandler extends PIDHandler
{
    
    static final String DUMMY_HANDLE_FOUND_FOR_OBJECT_PID = "dummy handle found for object PID";
    static final String DUMMY_HANDLE_FOUND_FOR_VERSION_HISTORY = "dummy handle found for versionHistory";
    static final String DUMMY_HANDLE_FOUND_FOR_VERSION_OR_RELEASE_PID = "dummy handle found for version or release PID";
    
    static final Pattern handlePattern = 
            Pattern.compile("hdl:[0-9]{5}/[0-9]{2}-[0-9A-Z]{4}-[0-9A-F]{4}-[0-9A-F]{4}-[0-9A-F]{4}-[0-9A-F]{1}");
    
    private static Logger logger = Logger.getLogger(AssertionHandler.class);
    
    
    public AssertionHandler(PreHandler preHandler) throws Exception
    {
        super(preHandler);
    }
    
    @Override
    public void content(String uri, String localName, String name, String content) throws SAXException
    {
        logger.debug("content      uri=<" + uri + "> localName = <" + localName + "> name = <" + name + "> content = <"
                + content + ">");
        
        Matcher m = handlePattern.matcher(content);
        
        if (inObjectPid )
        {
            if (content.contains(DUMMY_HANDLE) || !m.matches()) 
            {
                logger.warn("<" + content + "> " + DUMMY_HANDLE_FOUND_FOR_OBJECT_PID);
                throw new SAXException("<" + content + "> " + DUMMY_HANDLE_FOUND_FOR_OBJECT_PID);
            }
            inObjectPid = false;
        }
        else if (inVersionPidOrReleasePid)
        {
            
                if (content.contains(DUMMY_HANDLE) || !m.matches())
                {
                    logger.warn("<" + content + "> " + DUMMY_HANDLE_FOUND_FOR_VERSION_OR_RELEASE_PID);
                    throw new SAXException("<" + content + "> " + DUMMY_HANDLE_FOUND_FOR_VERSION_OR_RELEASE_PID);
                }                   
                
            inVersionPidOrReleasePid = false;
        } 
        else if (inVersionHistoryPid)
        {          
            if (content.contains(DUMMY_HANDLE) || !m.matches())
            {
                logger.warn("<" + content + "> " + DUMMY_HANDLE_FOUND_FOR_VERSION_HISTORY);
                throw new SAXException("<" + content + "> " + DUMMY_HANDLE_FOUND_FOR_VERSION_HISTORY);
            }
            inVersionHistoryPid = false;
        }
        
        super.content(uri, localName, name, content );
    }
}
