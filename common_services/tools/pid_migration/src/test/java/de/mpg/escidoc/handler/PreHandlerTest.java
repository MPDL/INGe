package de.mpg.escidoc.handler;

import static org.junit.Assert.*;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.mpg.escidoc.handler.PreHandler.Type;

public class PreHandlerTest
{
    private PreHandler preHandler = new PreHandler();
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        org.apache.log4j.BasicConfigurator.configure();
    }
     
    @Test
    public void item() throws Exception
    {
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        
        File file = new File("src/test/resources/item/escidoc_1479027.sav");
               
        parser.parse(file, preHandler);
        
        assertTrue(preHandler.getLastCreatedRelsExtId().equals("RELS-EXT.25"));
        assertTrue(preHandler.getLastCreatedRelsExtTimestamp().equals("2013-03-05T13:10:44.422Z"));
        assertTrue(preHandler.getLastVersionHistoryTimestamp().equals("2013-03-05T13:06:27.236Z"));
        assertTrue(preHandler.getObjectType().equals(Type.ITEM));
        
        file = new File("src/test/resources/item/itemReleasedOnce.sav");
        
        parser.parse(file, preHandler);
        
        assertTrue(preHandler.getLastCreatedRelsExtId().equals("RELS-EXT.9"));
        assertTrue(preHandler.getLastCreatedRelsExtTimestamp().equals("2013-03-13T09:39:45.022Z"));
        assertTrue(preHandler.getLastVersionHistoryTimestamp().equals("2013-03-13T09:39:43.050Z"));
        assertTrue(preHandler.getObjectType().equals(Type.ITEM));
    }
    
    @Test
    public void component() throws Exception
    {
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        
        File file = new File("src/test/resources/component/escidoc_1494882.sav");
               
        parser.parse(file, preHandler);
        
        assertTrue(preHandler.getLastCreatedRelsExtId().equals("RELS-EXT.1"));
        assertTrue(preHandler.getLastCreatedRelsExtTimestamp().equals("2012-07-27T14:00:11.197Z"));
        assertTrue(preHandler.getObjectType().equals(Type.COMPONENT));
        
        file = new File("src/test/resources/component/escidoc_418001.sav");
        
        parser.parse(file, preHandler);
        
        assertTrue(preHandler.getLastCreatedRelsExtId().equals("RELS-EXT.1"));
        assertTrue(preHandler.getLastCreatedRelsExtTimestamp().equals("2013-02-28T13:00:51.424Z"));
        assertTrue(preHandler.getObjectType().equals(Type.COMPONENT));
    }
}
