package test;

import org.apache.log4j.Logger;
import org.junit.Test;








public class TestContentModelSearch extends TestBase
{
    private static final int NUMBER_OF_URL_TOKENS = 2;
    
    private Logger logger = Logger.getLogger(getClass());
    
   
    public void retrieveVirrContainers() throws Exception
    {
        /*
        ContainerHandler ch = ServiceLocator.getContainerHandler(loginUser("virr_user", "PubManR2"));
        String filter = "<param><filter name=\"http://escidoc.de/core/01/structural-relations/content-model\">escidoc:VIRRcm1</filter></param>";
        String mvs = ch.retrieveContainers(filter);
        System.out.println(mvs);
        */
    }
    
    
    
    @Test
    public void testContentModelItem() throws Exception
    {
        
        retrieveVirrContainers();
        //String userHandle = loginUser("test_dep_lib", "");
        
        /*
        ReportHandler repHandler = ServiceLocator.getReportHandler("");
        
        String report = repHandler.retrieve("<report-parameters><report-definition objid=\"1\"/></report-parameters>");
        
        System.out.println(report);
        */
        
        /*
    
        String item = readFile("src/test/resources/test/item1.xml");
        long zeit = -System.currentTimeMillis();
        item = ServiceLocator.getItemHandler(userHandle).create(item);
        zeit += System.currentTimeMillis();
        logger.info("createContentItem()->" + zeit + "ms");
        logger.debug("ContentItem()=" + item);
        assertNotNull(item);
       
        
        String id = getId(item);
       
        item = ServiceLocator.getItemHandler(userHandle).retrieve(id);
        zeit += System.currentTimeMillis();
        logger.info("retrieveContentItem(" + id + ")->" + zeit + "ms");
        logger.debug("ContentItem(" + id + ")=" + item);
        assertNotNull(item);
        
        String md = getModificationDate(item);
       
        ServiceLocator.getItemHandler(userHandle).submit(id, createModificationDate(md));
        zeit += System.currentTimeMillis();
        logger.info("submitContentItem(" + id + ")->" + zeit + "ms");
        
       
        item = ServiceLocator.getItemHandler(userHandle).retrieve(id);
        zeit += System.currentTimeMillis();
        logger.info("retrieveContentItem(" + id + ")->" + zeit + "ms");
        logger.debug("ContentItem(" + id + ")=" + item);
        assertNotNull(item);
        
        
        md = getModificationDate(item);
        ServiceLocator.getItemHandler(userHandle).release(id, createModificationDate(md));
        zeit += System.currentTimeMillis();
        logger.info("releaseContentItem(" + id + ")->" + zeit + "ms");
        
        item = ServiceLocator.getItemHandler(userHandle).retrieve(id);
        zeit += System.currentTimeMillis();
        logger.info("retrieveContentItem(" + id + ")->" + zeit + "ms");
        logger.debug("ContentItem(" + id + ")=" + item);
        assertNotNull(item);
        
        
        String item2 = readFile("src/test/resources/test/item2.xml");
        md = getModificationDate(item);
        System.out.println(getModificationDate(item));
        

        
        String item2 = readFile("src/test/resources/test/item2.xml");
        String item = ServiceLocator.getItemHandler(userHandle).update("escidoc:2806", item2);
        //zeit += System.currentTimeMillis();
        logger.info(item);
        //logger.debug("ContentItem(" + id + ")=" + item);
        assertNotNull(item);
        */
        
        
        
        
    }

    
}
