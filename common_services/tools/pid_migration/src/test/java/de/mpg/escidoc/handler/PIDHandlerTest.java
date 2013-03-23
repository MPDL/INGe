package de.mpg.escidoc.handler;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PIDHandlerTest
{
    static PIDHandler pidHandler;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        pidHandler = new PIDHandler(new PreHandler());
    }
    

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void test()
    {
    }
}
