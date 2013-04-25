package de.mpg.escidoc.util;

import static org.junit.Assert.*;

import org.junit.Test;

import de.mpg.escidoc.util.SQLQuerier;

public class SQLQuerierTest
{
    @Test
    public void test() throws Exception
    {
        SQLQuerier querier = new  SQLQuerier();
        
        assertTrue(querier != null);
        
        String itemId = querier.getItemIdForComponent("escidoc:1720130");
        
        assertTrue(itemId != null && itemId.equals("escidoc:1491519"));
    }
}
