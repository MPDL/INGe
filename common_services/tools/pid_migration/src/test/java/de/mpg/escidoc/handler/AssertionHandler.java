package de.mpg.escidoc.handler;

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
    
    private static Logger logger = Logger.getLogger(AssertionHandler.class);
    
    
    public AssertionHandler(PreHandler preHandler) throws NamingException
    {
        super(preHandler);
    }
    
    @Override
    public void content(String uri, String localName, String name, String content) throws SAXException
    {
        logger.debug("content      uri=<" + uri + "> localName = <" + localName + "> name = <" + name + "> content = <"
                + content + ">");
        
        if (!(preHandler.getObjectType().equals(Type.ITEM) || preHandler.getObjectType().equals(Type.COMPONENT) || content.contains(DUMMY_HANDLE)))
        {
            super.content(uri, localName, name, content);
            return;
        }
        
        if (inObjectPid )
        {
            if (content.contains(DUMMY_HANDLE)) 
            {
                logger.warn("<" + content + "> " + DUMMY_HANDLE_FOUND_FOR_OBJECT_PID);
                throw new SAXException("<" + content + "> " + DUMMY_HANDLE_FOUND_FOR_OBJECT_PID);
            }
            inObjectPid = false;
        }
        else if (inVersionPidOrReleasePid)
        {
            if ("".equals(versionAndReleasePid))
            {
                if (content.contains(DUMMY_HANDLE))
                {
                    logger.warn("<" + content + "> " + DUMMY_HANDLE_FOUND_FOR_VERSION_OR_RELEASE_PID);
                    throw new SAXException("<" + content + "> " + DUMMY_HANDLE_FOUND_FOR_VERSION_OR_RELEASE_PID);
                }                   
                versionAndReleasePid = content;
            }
            else
            {
                content = versionAndReleasePid;
            }
            inVersionPidOrReleasePid = false;
        } 
        else if (inVersionHistoryPid)
        {          
            if (content.contains(DUMMY_HANDLE) || !content.equals(versionAndReleasePid))
            {
                logger.warn("<" + content + "> " + DUMMY_HANDLE_FOUND_FOR_VERSION_HISTORY);
                throw new SAXException("<" + content + "> " + DUMMY_HANDLE_FOUND_FOR_VERSION_HISTORY);
            }
            inVersionHistoryPid = false;
        }
        
        super.content(uri, localName, name, content );
    }
}
