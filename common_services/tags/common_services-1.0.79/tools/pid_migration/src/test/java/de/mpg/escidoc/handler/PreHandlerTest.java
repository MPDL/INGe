package de.mpg.escidoc.handler;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.handler.PreHandler.Status;
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
        assertTrue(preHandler.getObjectType().equals(Type.ITEM));
        assertTrue(preHandler.getPublicStatus().equals(Status.RELEASED));
        assertTrue(preHandler.getVersionStatus().equals(Status.RELEASED));
        assertTrue(preHandler.getReleaseNumber().equals("5"));
        assertTrue(preHandler.getVersionNumber().equals("5"));
        assertTrue(preHandler.getEscidocId().equals("escidoc:1479027"));
        assertTrue(preHandler.getTitle().startsWith("The immediate and chronic influence of spatio-temporal"));
        
        Map<String, String> m = preHandler.getAttributeMapFor("RELS-EXT.15");
        
        assertTrue(m.get("version:number").equals("4"));
        assertTrue(m.get("prop:public-status").equals("submitted"));
        assertTrue(preHandler.getVersionNumber("RELS-EXT.15").equals("4"));
        assertTrue(preHandler.getReleaseNumber("RELS-EXT.15").equals(""));
        assertTrue(preHandler.getPublicStatus("RELS-EXT.15").equals(Status.SUBMITTED));
        
        m = preHandler.getAttributeMapFor("RELS-EXT.23");
        
        assertTrue(m.get("prop:public-status").equals("released"));
        assertTrue(m.get("version:number").equals("5"));
        assertTrue(m.get("release:number").equals("4"));
        
        file = new File("src/test/resources/item_sav/escidoc_61195");
        
        preHandler = new PreHandler();
        parser.parse(file, preHandler);
        
        assertTrue(preHandler.getLastCreatedRelsExtId().equals("RELS-EXT.105"));
        assertTrue(preHandler.getObjectType().equals(Type.ITEM));
        assertTrue(preHandler.getPublicStatus().equals(Status.RELEASED));
        assertTrue(preHandler.getVersionStatus().equals(Status.RELEASED));
        assertTrue(preHandler.getReleaseNumber().equals("14"));
        assertTrue(preHandler.getVersionNumber().equals("14"));
        assertTrue(preHandler.getEscidocId().equals("escidoc:61195"));
        assertTrue(preHandler.getTitle().startsWith("Contact and"));
        
        m = preHandler.getAttributeMapFor("RELS-EXT.56");
        
        assertTrue(m.get("version:number").equals("9"));
        assertTrue(m.get("prop:public-status").equals("released"));
        assertTrue(preHandler.getPublicStatus("RELS-EXT.56").equals(Status.RELEASED));
        assertTrue(preHandler.getVersionStatus("RELS-EXT.56").equals(Status.PENDING));
        assertTrue(preHandler.getReleaseNumber("RELS-EXT.56").equals("8"));
        assertTrue(preHandler.getVersionNumber("RELS-EXT.56").equals("9"));
        
        m = preHandler.getAttributeMapFor("RELS-EXT.23");
        
        assertTrue(preHandler.getPublicStatus("RELS-EXT.23").equals(Status.RELEASED));
        assertTrue(preHandler.getVersionStatus("RELS-EXT.23").equals(Status.RELEASED));
        assertTrue(preHandler.getReleaseNumber("RELS-EXT.23").equals("4"));
        assertTrue(preHandler.getVersionNumber("RELS-EXT.23").equals("4"));
        
         
        file = new File("src/test/resources/item_sav/itemReleasedOnce");
        
        preHandler = new PreHandler();
        parser.parse(file, preHandler);
        
        assertTrue(preHandler.getLastCreatedRelsExtId().equals("RELS-EXT.9"));
        assertTrue(preHandler.getLastCreatedRelsExtTimestamp().equals("2013-03-13T09:39:45.022Z"));
        assertTrue(preHandler.getObjectType().equals(Type.ITEM));
        assertTrue(preHandler.getPublicStatus().equals(Status.RELEASED));
        assertTrue(preHandler.getVersionStatus().equals(Status.RELEASED));
        assertTrue(preHandler.getReleaseNumber().equals("1"));
        assertTrue(preHandler.getVersionNumber().equals("1"));
        assertTrue(preHandler.getEscidocId().equals("escidoc:1648303"));
        assertTrue(preHandler.getTitle().equals("Higher-Order Tensors in Diffusion MRI"));
        
        file = new File("src/test/resources/item_sav/itemPublicStatusPending");
        
        preHandler = new PreHandler();
        parser.parse(file, preHandler);
        
        assertTrue(preHandler.getLastCreatedRelsExtId().equals("RELS-EXT.1"));
        assertTrue(preHandler.getLastCreatedRelsExtTimestamp().equals("2013-02-28T15:51:02.693Z"));
        assertTrue(preHandler.getObjectType().equals(Type.ITEM));
        assertTrue(preHandler.getPublicStatus().equals(Status.PENDING));
        assertTrue(preHandler.getVersionStatus().equals(Status.PENDING));
        assertTrue(preHandler.getReleaseNumber().equals(""));
        assertTrue(preHandler.getVersionNumber().equals("1"));       
        assertTrue(preHandler.getEscidocId().equals("escidoc:1648168"));
        assertTrue(preHandler.getTitle().equals("Rest Search Documentation"));
        
        file = new File("src/test/resources/item_sav/itemReleasedTwiceNowSubmitted");
        
        preHandler = new PreHandler();
        parser.parse(file, preHandler);
        
        assertTrue(preHandler.getLastCreatedRelsExtId().equals("RELS-EXT.23"));
        assertTrue(preHandler.getLastCreatedRelsExtTimestamp().equals("2013-04-08T11:39:23.429Z"));
        assertTrue(preHandler.getObjectType().equals(Type.ITEM));
        assertTrue(preHandler.getPublicStatus().equals(Status.RELEASED));
        assertTrue(preHandler.getVersionStatus().equals(Status.SUBMITTED));
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
       /* 
        preHandler = new PreHandler();
        parser.parse(file, preHandler);
        
        assertTrue(preHandler.getLastCreatedRelsExtId().equals("RELS-EXT.1"));
        assertTrue(preHandler.getLastCreatedRelsExtTimestamp().equals("2012-07-27T14:00:11.197Z"));
        assertTrue(preHandler.getObjectType().equals(Type.COMPONENT));
        assertTrue(preHandler.getTitle().equals("2815.pdf"));
        assertTrue(!preHandler.isObjectPidToInsert("RELS-EXT.0"));
        assertTrue(!preHandler.isObjectPidToInsert("RELS-EXT.1"));
        
        file = new File("src/test/resources/component_sav/escidoc_418001");
        
        preHandler = new PreHandler();
        parser.parse(file, preHandler);
        
        assertTrue(preHandler.getLastCreatedRelsExtId().equals("RELS-EXT.1"));
        assertTrue(preHandler.getLastCreatedRelsExtTimestamp().equals("2013-02-28T13:00:51.424Z"));
        assertTrue(preHandler.getObjectType().equals(Type.COMPONENT));
        assertTrue(preHandler.getTitle().equals("server.log"));
        assertTrue(!preHandler.isObjectPidToInsert("RELS-EXT.0"));
        assertTrue(!preHandler.isObjectPidToInsert("RELS-EXT.1"));*/
        
        file = new File("src/test/resources/component_sav/escidoc_52093");
        
        preHandler = new PreHandler();
        parser.parse(file, preHandler);
        
        assertTrue(preHandler.getLastCreatedRelsExtId().equals("RELS-EXT.8"));
        assertTrue(preHandler.getObjectType().equals(Type.COMPONENT));
        assertTrue(preHandler.getTitle().equals("372285.pdf"));
        
        assertTrue(!preHandler.isObjectPidToInsert("RELS-EXT.0"));
        assertTrue(!preHandler.isObjectPidToInsert("RELS-EXT.1"));
        assertTrue(!preHandler.isObjectPidToInsert("RELS-EXT.2"));
        assertTrue(preHandler.isObjectPidToInsert("RELS-EXT.3"));
        assertTrue(preHandler.isObjectPidToInsert("RELS-EXT.4"));
        assertTrue(preHandler.isObjectPidToInsert("RELS-EXT.5"));
        assertTrue(preHandler.isObjectPidToInsert("RELS-EXT.6"));
        assertTrue(preHandler.isObjectPidToInsert("RELS-EXT.7"));
        assertTrue(preHandler.isObjectPidToInsert("RELS-EXT.8"));
        
        file = new File("src/test/resources/component_sav/escidoc_61196_old");
        
        preHandler = new PreHandler();
        parser.parse(file, preHandler);
        
        assertTrue(preHandler.getLastCreatedRelsExtId().equals("RELS-EXT.10"));
        assertTrue(preHandler.getObjectType().equals(Type.COMPONENT));
        assertTrue(preHandler.getTitle().equals("http://dx.doi.org/10.1016/j.lingua.2007.10.026"));
        assertTrue(!preHandler.isObjectPidToInsert("RELS-EXT.0"));
        assertTrue(!preHandler.isObjectPidToInsert("RELS-EXT.1"));
        assertTrue(!preHandler.isObjectPidToInsert("RELS-EXT.2"));
        assertTrue(!preHandler.isObjectPidToInsert("RELS-EXT.3"));
        assertTrue(!preHandler.isObjectPidToInsert("RELS-EXT.4"));
        assertTrue(!preHandler.isObjectPidToInsert("RELS-EXT.5"));
        assertTrue(!preHandler.isObjectPidToInsert("RELS-EXT.6"));
        assertTrue(preHandler.isObjectPidToInsert("RELS-EXT.7"));
        assertTrue(preHandler.isObjectPidToInsert("RELS-EXT.8"));
        assertTrue(preHandler.isObjectPidToInsert("RELS-EXT.9"));
        assertTrue(preHandler.isObjectPidToInsert("RELS-EXT.10"));
        
    }
    
    @Test
    public void otherObjectTypes() throws Exception
    {
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        
        File file = new File("src/test/resources/content-model_sav/escidoc_persistent4");
        
        preHandler = new PreHandler();
        parser.parse(file, preHandler);
        
        assertTrue(preHandler.getObjectType().equals(Type.CONTENTMODEL));
        
        file = new File("src/test/resources/content-model_sav/escidoc_importtask1");
        
        preHandler = new PreHandler();
        parser.parse(file, preHandler);
        
        assertTrue(preHandler.getObjectType().equals(Type.CONTENTMODEL));
        
        file = new File("src/test/resources/context_sav/escidoc_persistent3");
        
        preHandler = new PreHandler();
        parser.parse(file, preHandler);
        
        assertTrue(preHandler.getObjectType().equals(Type.CONTEXT));
    }
}
