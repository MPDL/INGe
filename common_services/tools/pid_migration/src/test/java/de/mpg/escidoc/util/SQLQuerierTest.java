package de.mpg.escidoc.util;

import static org.junit.Assert.*;

import org.junit.Test;

import de.mpg.escidoc.util.SQLQuerier;

public class SQLQuerierTest
{
    @Test
    public void testGetItemForComponent() throws Exception
    {
        SQLQuerier querier = new  SQLQuerier();
        
        assertTrue(querier != null);
        
        String itemId = querier.getItemIdForComponent("escidoc:1720130");       
        assertTrue(itemId != null && itemId.equals("escidoc:1491519"));
    }
    
    @Test
    public void testUpdateTripleStorePidTable() throws Exception
    {
        SQLQuerier querier = new  SQLQuerier();
       
        querier.updateTripleStorePidTable("hdl:11858/00-ZZZZ-0000-0000-43A3-2", "escidoc:832229");
        
    }
}
