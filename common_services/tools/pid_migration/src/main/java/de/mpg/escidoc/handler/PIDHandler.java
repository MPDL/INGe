package de.mpg.escidoc.handler;

import java.io.IOException;
import java.util.regex.Matcher;

import javax.naming.NamingException;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.mpg.escidoc.handler.PreHandler.Type;
import de.mpg.escidoc.main.PIDMigrationManager;
import de.mpg.escidoc.main.PIDProviderIf;

public class PIDHandler extends IdentityHandler
{
    private static Logger logger = Logger.getLogger(PIDHandler.class);

    protected PreHandler preHandler;
    private PIDMigrationManager pidMigrationManager;
    private PIDProviderIf pidProvider;
    
    protected boolean inLastRelsExt = false;
    protected boolean inObjectPid = false;
    protected boolean inVersionPidOrReleasePid = false;
    protected boolean inVersionHistoryPid = false;
    
    // flag indicating if a modify has taken place.
    protected boolean updateDone = false;
    
    protected String versionAndReleasePid = "";
    
    protected static final String DUMMY_HANDLE = "someHandle";
    
    public PIDHandler(PreHandler preHandler) throws NamingException
    {
        this.preHandler = preHandler;
        this.pidProvider = new PIDProviderMock();
       
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
        
        if ("foxml:datastreamVersion".equals(name) && preHandler.getLastCreatedRelsExtId() != null 
                && preHandler.getLastCreatedRelsExtId().equals(attributes.getValue("ID")))
        {
            inLastRelsExt = true;
        }
        else if (inLastRelsExt && "prop:pid".equals(name))
        {
            inObjectPid = true;
        }
        else if (inLastRelsExt && ("version:pid".equals(name) || "release:pid".equals(name)))
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
            content = getPid(content, oldContent);
            inObjectPid = false;
        }
        else if (inVersionPidOrReleasePid)
        {
            if ("".equals(versionAndReleasePid))
            {
                content = getPid(content, oldContent);
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
            content = versionAndReleasePid;
            inVersionHistoryPid = false;
        }
        
        if (!content.equals(oldContent))
        {
            updateDone = true;
        }
        
        super.content(uri, localName, name, content );
    }

    private String getPid(String content, String oldContent)
    {
        Matcher m = AssertionHandler.handlePattern.matcher(content);
        if (m.matches())
        {
            return doReplace(content);
        }
        
        try
        {
            content = pidProvider.getPid();
            content = doReplace(content);
        }
        catch (HttpException e)
        {
            logger.warn("Error getting PID for content <" + content + ">", e);
            return oldContent;
        }
        catch (IOException e)
        {
            logger.warn("Error getting PID for content <" + content + ">", e);
            return oldContent;
        }
        return content;
    }
    
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
        if ("foxml:datastreamVersion".equals(name))
        {
            inLastRelsExt = false;
        } 
        
        super.endElement(uri, localName, name);
    }
    
    public boolean isUpdateDone()
    {
        return updateDone;
    }
}
