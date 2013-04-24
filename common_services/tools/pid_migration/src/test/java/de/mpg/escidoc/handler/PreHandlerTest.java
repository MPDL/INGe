package de.mpg.escidoc.handler;

import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.escidoc.handler.PreHandler.PublicStatus;
import de.mpg.escidoc.handler.PreHandler.Type;

public class PreHandlerTest
{
    private PreHandler preHandler;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        org.apache.log4j.BasicConfigurator.configure();
    }
     
    @Test
    public void item() throws Exception
    {
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        
        File file = new File("src/test/resources/item_sav/escidoc_1479027");
        
        preHandler = new PreHandler();
        parser.parse(file, preHandler);
        
        assertTrue(preHandler.getLastCreatedRelsExtId().equals("RELS-EXT.25"));
        assertTrue(preHandler.getLastCreatedRelsExtTimestamp().equals("2013-03-05T13:10:44.422Z"));
        assertTrue(preHandler.getLastVersionHistoryTimestamp().equals("2013-03-05T13:06:27.236Z"));
        assertTrue(preHandler.getObjectType().equals(Type.ITEM));
        assertTrue(preHandler.getPublicStatus().equals(PublicStatus.RELEASED));
        assertTrue(preHandler.getVersionStatus().equals(PublicStatus.RELEASED));
        assertTrue(preHandler.getReleaseNumber().equals("5"));
        assertTrue(preHandler.getVersionNumber().equals("5"));
        assertTrue(preHandler.getEscidocId().equals("escidoc:1479027"));
        assertTrue(preHandler.getTitle().startsWith("The immediate and chronic influence of spatio-temporal"));
        
        file = new File("src/test/resources/item_sav/itemReleasedOnce");
        
        preHandler = new PreHandler();
        parser.parse(file, preHandler);
        
        assertTrue(preHandler.getLastCreatedRelsExtId().equals("RELS-EXT.9"));
        assertTrue(preHandler.getLastCreatedRelsExtTimestamp().equals("2013-03-13T09:39:45.022Z"));
        assertTrue(preHandler.getLastVersionHistoryTimestamp().equals("2013-03-13T09:39:43.050Z"));
        assertTrue(preHandler.getObjectType().equals(Type.ITEM));
        assertTrue(preHandler.getPublicStatus().equals(PublicStatus.RELEASED));
        assertTrue(preHandler.getVersionStatus().equals(PublicStatus.RELEASED));
        assertTrue(preHandler.getReleaseNumber().equals("1"));
        assertTrue(preHandler.getVersionNumber().equals("1"));
        assertTrue(preHandler.getEscidocId().equals("escidoc:1648303"));
        assertTrue(preHandler.getTitle().equals("Higher-Order Tensors in Diffusion MRI"));
        
        file = new File("src/test/resources/item_sav/itemPublicStatusPending");
        
        preHandler = new PreHandler();
        parser.parse(file, preHandler);
        
        assertTrue(preHandler.getLastCreatedRelsExtId().equals("RELS-EXT.1"));
        assertTrue(preHandler.getLastCreatedRelsExtTimestamp().equals("2013-02-28T15:51:02.693Z"));
        assertTrue(preHandler.getLastVersionHistoryTimestamp().equals(""));
        assertTrue(preHandler.getObjectType().equals(Type.ITEM));
        assertTrue(preHandler.getPublicStatus().equals(PublicStatus.PENDING));
        assertTrue(preHandler.getVersionStatus().equals(PublicStatus.PENDING));
        assertTrue(preHandler.getReleaseNumber().equals(""));
        assertTrue(preHandler.getVersionNumber().equals("1"));       
        assertTrue(preHandler.getEscidocId().equals("escidoc:1648168"));
        assertTrue(preHandler.getTitle().equals("Rest Search Documentation"));
        
        file = new File("src/test/resources/item_sav/itemReleasedTwiceNowSubmitted");
        
        preHandler = new PreHandler();
        parser.parse(file, preHandler);
        
        assertTrue(preHandler.getLastCreatedRelsExtId().equals("RELS-EXT.23"));
        assertTrue(preHandler.getLastCreatedRelsExtTimestamp().equals("2013-04-08T11:39:23.429Z"));
        assertTrue(preHandler.getLastVersionHistoryTimestamp().equals("2013-04-08T11:38:20.443Z"));
        assertTrue(preHandler.getObjectType().equals(Type.ITEM));
        assertTrue(preHandler.getPublicStatus().equals(PublicStatus.RELEASED));
        assertTrue(preHandler.getVersionStatus().equals(PublicStatus.SUBMITTED));
        assertTrue(preHandler.getReleaseNumber().equals("2"));
        assertTrue(preHandler.getVersionNumber().equals("3"));
        assertTrue(preHandler.getEscidocId().equals("escidoc:1647170"));
        assertTrue(preHandler.getTitle().equals("Understanding foxml II"));
    }
    
    @Test
    public void component() throws Exception
    {
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        
        File file = new File("src/test/resources/component_sav/escidoc_1494882");
        
        preHandler = new PreHandler();
        parser.parse(file, preHandler);
        
        assertTrue(preHandler.getLastCreatedRelsExtId().equals("RELS-EXT.1"));
        assertTrue(preHandler.getLastCreatedRelsExtTimestamp().equals("2012-07-27T14:00:11.197Z"));
        assertTrue(preHandler.getObjectType().equals(Type.COMPONENT));
        assertTrue(preHandler.getTitle().equals("2815.pdf"));
        
        file = new File("src/test/resources/component_sav/escidoc_418001");
        
        preHandler = new PreHandler();
        parser.parse(file, preHandler);
        
        assertTrue(preHandler.getLastCreatedRelsExtId().equals("RELS-EXT.1"));
        assertTrue(preHandler.getLastCreatedRelsExtTimestamp().equals("2013-02-28T13:00:51.424Z"));
        assertTrue(preHandler.getObjectType().equals(Type.COMPONENT));
        assertTrue(preHandler.getTitle().equals("server.log"));
    }
}
