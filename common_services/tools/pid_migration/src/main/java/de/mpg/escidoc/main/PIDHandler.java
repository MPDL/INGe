package de.mpg.escidoc.main;

import java.io.IOException;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.mpg.escidoc.main.PreHandler.Type;

public class PIDHandler extends IdentityHandler
{
    private static Logger logger = Logger.getLogger(PIDHandler.class);

    private PreHandler preHandler;
    private PIDMigrationManager pidMigrationManager;
    
    private boolean inLastRelsExt = false;
    private boolean inObjectPid = false;
    private boolean inVersionPidOrReleasePid = false;
    private boolean inVersionNumber = false;
    private boolean inReleaseNumber = false;
    private boolean inVersionHistory = false;
    private boolean inVersionHistoryPid = false;
    
    private String versionAndReleasePid = "";
    private String versionNumber = "";
    private String releaseNumber = "";
    
    private static final String DUMMY_HANDLE = "someHandle";
    
    public PIDHandler(PreHandler preHandler)
    {
        this.preHandler = preHandler;
    }
    
    public void setPIDMigrationManager(PIDMigrationManager mgr)
    {
        this.pidMigrationManager = mgr;
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
        else if (inLastRelsExt && ("version:number".equals(name)))
        {
            inVersionNumber = true;
        }
        else if (inLastRelsExt && ("version:number".equals(name)))
        {
            inReleaseNumber = true;
        }
        else if ("escidocVersions:version".equals(name) && attributes.getValue("objid").endsWith(":" + versionNumber))
        { 
            inVersionHistoryPid = true;
        }
    }

    @Override
    public void content(String uri, String localName, String name, String content) throws SAXException
    {
        logger.debug("content      uri=<" + uri + "> localName = <" + localName + "> name = <" + name + "> content = <"
                + content + ">");
        
        // fallback if pidcache isn't reachable, if (objectType != ITEM or COMPONENT), if object already has a real PID
        String oldContent = content;
        
        if (!(preHandler.getObjectType().equals(Type.ITEM) || preHandler.getObjectType().equals(Type.COMPONENT) || content.contains(DUMMY_HANDLE)))
        {
            super.content(uri, localName, name, content);
            return;
        }
        
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
        else if (inVersionNumber)
        {
            versionNumber = content;
            inVersionNumber = false;
        }
        else if (inReleaseNumber)
        {
            releaseNumber = content;
            inReleaseNumber = false;
        }
        else if (inVersionHistoryPid)
        {
            if ("escidocVersions:pid".equals(name))
            {
                 content = versionAndReleasePid;
            }
            inVersionHistoryPid = false;
        }
        
        super.content(uri, localName, name, content );
    }

    private String getPid(String content, String oldContent)
    {
        try
        {
            content = pidMigrationManager.getPid();
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

}
