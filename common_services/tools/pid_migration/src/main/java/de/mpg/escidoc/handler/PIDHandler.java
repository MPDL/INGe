package de.mpg.escidoc.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import javax.naming.NamingException;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.mpg.escidoc.handler.PreHandler.Type;
import de.mpg.escidoc.main.PIDMigrationManager;
import de.mpg.escidoc.main.PIDProviderIf;
import de.mpg.escidoc.util.Util;

public class PIDHandler extends IdentityHandler
{
    private static Logger logger = Logger.getLogger(PIDHandler.class);

    protected PreHandler preHandler;
    private PIDMigrationManager pidMigrationManager;
    private PIDProviderIf pidProvider;
    
    protected boolean inRelsExt = false;
    protected boolean inObjectPid = false;
    protected boolean inVersionPidOrReleasePid = false;
    protected boolean inVersionHistoryPid = false;
    
    // flag indicating if a modify has taken place.
    protected boolean updateDone = false;
    
    protected static final String DUMMY_HANDLE = "someHandle";
    
    protected Map<String, String> replaceMap = new HashMap<String, String>();
    
    public PIDHandler(PreHandler preHandler) throws Exception 
    {
        this.preHandler = preHandler;
        
        preHandler.getVersionNumber();
        preHandler.getReleaseNumber();
        preHandler.getEscidocId();
        
        this.init();       
    }
    
    private void init() throws Exception
    {
        Class<?> pidProviderClass = Class.forName(Util.getProperty("escidoc.pidprovider.class"));
        
        this.pidProvider = (PIDProviderIf)pidProviderClass.newInstance();                
        this.pidProvider.init();
    }
    
    public void setPIDMigrationManager(PIDMigrationManager mgr)
    {
        this.pidMigrationManager = mgr;
    }
    
    @Override
    public void startDocument() throws SAXException
    {
        append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
    {
        logger.debug("startElement uri=<" + uri + "> localName = <" + localName + "> name = <" + name + "> attributes = <" + attributes + ">");
        
        super.startElement(uri, localName, name, attributes);
        
        if (("foxml:datastream".equals(name) && "RELS-EXT".equals(attributes.getValue("ID"))))
        { 
            inRelsExt = true;
            logger.debug(" startElement inRelsExt= " + inRelsExt);
        }
        else if (inRelsExt && "prop:pid".equals(name))
        {
            inObjectPid = true;
        }
        else if (inRelsExt && ("version:pid".equals(name) || "release:pid".equals(name)))
        {
            inVersionPidOrReleasePid = true;
        }
        else if ("escidocVersions:pid".equals(name) && attributes.getValue("timestamp").equals(preHandler.getLastVersionHistoryTimestamp()))
        { 
            inVersionHistoryPid = true;
        }
    }

    @Override
    public void content(String uri, String localName, String name, String content) throws SAXException
    {
        logger.debug("content      uri=<" + uri + "> localName = <" + localName + "> name = <" + name + "> content = <"
                + content + ">");
        
        // fallback if pidcache isn't reachable
        String oldContent = content;
        
        if (inObjectPid )
        {            
            try
            {
                content = getPid(content);
            }
            catch (PIDProviderException e)
            {
                pidMigrationManager.onError(e);
            }
            inObjectPid = false;
        }
        else if (inVersionPidOrReleasePid)
        {
            try
            {
                content = getPid(content);
            }
            catch (PIDProviderException e)
            {
                pidMigrationManager.onError(e);
            }
            inVersionPidOrReleasePid = false;
        }
        else if (inVersionHistoryPid)
        {          
            try
            {
                content = getPid(content);
            }
            catch (PIDProviderException e)
            {
                pidMigrationManager.onError(e);
            }
            inVersionHistoryPid = false;
        }
        
        if (!content.equals(oldContent))
        {
            updateDone = true;
        }
        
        super.content(uri, localName, name, content );
    }

    private String getPid(String content) throws PIDProviderException
    {
        Matcher m = AssertionHandler.handlePattern.matcher(content);
        // already a real pid
        if (m.matches())
        {
            return content;
        }
        
        // pid attribute has already been requested
        if (replaceMap.get(content) != null)
        {
            return replaceMap.get(content);
        }
        
        // pid has to be requested
        try
        {
            String oldContent = content;
            
            content = pidProvider.getPid(preHandler.getEscidocId(), preHandler.getObjectType());
            content = doReplace(content);
            replaceMap.put(oldContent, content);
        }
        catch (HttpException e)
        {
            logger.warn("Error getting PID for content <" + content + ">", e);
            throw new PIDProviderException(e);
        }
        catch (IOException e)
        {
            logger.warn("Error getting PID for content <" + content + ">", e);
            throw new PIDProviderException(e);
        }
        return content;
    }

    // helper method to treat the following cases:
    // if a PID is fetched from PidCache Service, a plain PID without prefix hdl: is returned. If already a PID is available in the content parameter, 
    // the content already starts with the prefix.
    private String doReplace(String content)
    {
        if (content == null || "".equals(content))
            return "";
        
        if (content.startsWith("hdl"))
            return content;
            
        return "hdl:" + content;
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException
    {
        logger.debug("endElement   uri=<" + uri + "> localName = <" + localName + "> name = <" + name + "> ");
        if ("foxml:datastream".equals(name))
        {
            inRelsExt = false;
        } 
        
        super.endElement(uri, localName, name);
    }
    
    public boolean isUpdateDone()
    {
        return updateDone;
    }
}
