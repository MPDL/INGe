package test.pubman;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.SearchRetrieveRecordVO;
import de.mpg.escidoc.services.common.valueobjects.SearchRetrieveResponseVO;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class RetrieveItemTest
{
    private Logger logger = Logger.getLogger(getClass());
    
    /**
     * Map of key - value pairs containing the filter definition
     */    
    private static final HashMap<String, String[]> filterMap = new HashMap<String, String[]>();
    
    /**
     * The handle of the logged in user or null if not logged in.
     */
    private String userHandle;
    
    private XmlTransforming xmlTransforming;
    
    /**
     * Constants for queries.
     */
    protected static final String SEARCH_RETRIEVE = "searchRetrieve";
    protected static final String QUERY = "query";
    protected static final String VERSION = "version";
    protected static final String OPERATION = "operation";
    
    
    
    
    @Test
    @Ignore
    public void retrieveItemById() throws Exception
    {
        filterMap.put(OPERATION, new String[]{SEARCH_RETRIEVE});
        filterMap.put(VERSION, new String[]{"1.1"});
        filterMap.put(QUERY, new String[]{"\"/id\"=escidoc:2006"});
        
        logger.debug("Filter=" + filterMap.entrySet().toString());
        long zeit = -System.currentTimeMillis();
        String items = ServiceLocator.getItemHandler().retrieveItems(filterMap);
        zeit += System.currentTimeMillis();
        logger.info("retrieveItemById(" + filterMap + ")->" + zeit + "ms");
        logger.debug("ContentItems =" + items);
        assertNotNull(items);
    }


    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveItems(java.lang.String)}.
     */
    @Test
    public void retrievePendingContentItems() throws Exception
    {
        filterMap.put(OPERATION, new String[]{SEARCH_RETRIEVE});
        filterMap.put(VERSION, new String[]{"1.1"});

        String q1 = "\"/properties/public-status\"=pending";
        filterMap.put(QUERY, new String[]{q1});
        
        logger.debug("Filter=" + filterMap.entrySet().toString());

        String items = ServiceLocator.getItemHandler(userHandle).retrieveItems(filterMap);

        logger.debug("ContentItems =" + items);
        assertNotNull(items);
    }
    
    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveItems(java.lang.String)}.
     */
    @Test
    public void retrievePendingContentItemsSortByDescending() throws Exception
    {
        filterMap.put(OPERATION, new String[]{SEARCH_RETRIEVE});
        filterMap.put(VERSION, new String[]{"1.1"});

        String q1 = "\"/properties/public-status\"=pending";
        String q2 = "sortBy " + "\"/id\"/sort.descending";
        filterMap.put(QUERY, new String[]{q1 + " " + q2});
        
        String items = ServiceLocator.getItemHandler(userHandle).retrieveItems(filterMap);        
        logger.info("ContentItems =" + items);
        SearchRetrieveResponseVO response = xmlTransforming.transformToSearchRetrieveResponse(items);
        assertNotNull(items);
    }
    
    /**
     * Test method for {@link de.fiz.escidoc.om.ItemHandlerLocal#retrieveItems(java.lang.String)}.
     */
    @Test
    public void retrievePendingContentItemsSortByAscending() throws Exception
    {
        filterMap.put(OPERATION, new String[]{SEARCH_RETRIEVE});
        filterMap.put(VERSION, new String[]{"1.1"});

        String q1 = "\"/properties/public-status\"=pending";
        String q2 = "sortBy " + "\"/id\"/sort.ascending";
        filterMap.put(QUERY, new String[]{q1 + " " + q2});
                
        String items = ServiceLocator.getItemHandler(userHandle).retrieveItems(filterMap);
        logger.info("ContentItems =" + items);
        assertNotNull(items);
        
        SearchRetrieveResponseVO response = xmlTransforming.transformToSearchRetrieveResponse(items);
        List<SearchRetrieveRecordVO> results = response.getRecords();
        ItemVO last = null;
        for(SearchRetrieveRecordVO rec : results)
        { 
            ItemVO item = (ItemVO)rec.getData();
            if (last != null && item != null)
                assertTrue(last.getPid().compareTo(item.getPid()) > 0);
            last = item;
        }

        assertTrue(true);
    }
    
    /**
     * Logs in as the default user before each test case.
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception
    {
        filterMap.clear();
        userHandle = AdminHelper.loginUser(PropertyReader.getProperty("framework.scientist.username"), PropertyReader.getProperty("framework.scientist.password"));
        
        xmlTransforming = (XmlTransforming)getService(XmlTransforming.SERVICE_NAME);
    }

    /**
     * Logs the user out after each test case.
     * 
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception
    {
        ServiceLocator.getUserManagementWrapper(userHandle).logout();
        userHandle = null;
    }
    
    /**
     * Helper method to retrieve a EJB service instance. The name to be passed to the method is normally
     * 'ServiceXY.SERVICE_NAME'.
     * 
     * @return instance of the EJB service
     * @throws NamingException
     */
    protected static Object getService(String serviceName) throws NamingException
    {
        InitialContext context = new InitialContext();
        Object serviceInstance = context.lookup(serviceName);
        assertNotNull(serviceInstance);
        return serviceInstance;
    }

}
