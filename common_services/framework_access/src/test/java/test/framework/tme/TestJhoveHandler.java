package test.framework.tme;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.framework.TestBase;
import de.escidoc.www.services.tme.JhoveHandler;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class TestJhoveHandler extends TestBase
{
    /**
     * Constants for queries.
     */
    protected static final String SEARCH_RETRIEVE = "searchRetrieve";
    protected static final String QUERY = "query";
    protected static final String VERSION = "version";
    protected static final String OPERATION = "operation";
    
    private static JhoveHandler jhoveHandler = null;
    
    private Logger logger = Logger.getLogger(getClass());
    
    @Before
    public void setUp() throws Exception
    {           
        jhoveHandler = ServiceLocator.getJhoveHandler(loginSystemAdministrator());
    }
    
    @After
    public void tearDown() throws Exception
    {           
        //logout();
    }

   

    @Test
    public void extract()
    {
        String req1 =    
                "<request xmlns:xlink=\"http://www.w3.org/1999/xlink\">"        
                +  "<file xlink:type=\"simple\" xlink:title=\"\" xlink:href=\"http://dev-pubman.mpdl.mpg.de/pubman/item/escidoc:524168:2/component/escidoc:524166/1471-2202-11-90.xml\" />"
                +  "</request>";
                             
        try
        {
            String xml = jhoveHandler.extract(req1);
            assertTrue(xml != null);
            logger.info(xml);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        String req2 = 
                "<request xmlns:xlink=\"http://www.w3.org/1999/xlink\">"        
                        +  "<file xlink:type=\"simple\" xlink:title=\"\" xlink:href=\"http://pubman.mpdl.mpg.de/pubman/item/escidoc:1232421:4/component/escidoc:1835851/EPSR_1_2009_Scharpf.pdf\" />"
                        +  "</request>"; 
        
        try
        {
            String xml = jhoveHandler.extract(req2);
            assertTrue(xml != null);
            logger.info(xml);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        String req3 = 
                "<request xmlns:xlink=\"http://www.w3.org/1999/xlink\">"        
                        +  "<file xlink:type=\"simple\" xlink:title=\"\" xlink:href=\"http://dev-pubman.mpdl.mpg.de/pubman/item/escidoc:530002:1/component/escidoc:530001/performance.xlsx\" />"
                        +  "</request>"; 
        
        try
        {
            String xml = jhoveHandler.extract(req3);
            assertTrue(xml != null);
            logger.info(xml);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
