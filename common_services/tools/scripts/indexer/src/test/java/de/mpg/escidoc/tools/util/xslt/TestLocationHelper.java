package de.mpg.escidoc.tools.util.xslt;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.escidoc.tools.reindex.Indexer;

public class TestLocationHelper
{
	protected static Indexer indexer;
	
	@BeforeClass
	public  static void setUp() throws Exception
	{
		indexer = new Indexer(new File("src/test/resources/20"));
		indexer.init();
		
		indexer.createDatabase();
	}
	
	@Test
	public void test()
	{
		assertTrue(LocationHelper.getLocation("escidoc:persistent13") != null);
		assertTrue("Is " + LocationHelper.getLocation("escidoc:persistent13"), 
				LocationHelper.getLocation("escidoc:persistent13").endsWith("indexer/src/test/resources/20/ous/escidoc_persistent13"));
		
		assertTrue("Is " + LocationHelper.getLocation("escidoc:24022"), 
				LocationHelper.getLocation("escidoc:24022").endsWith("indexer/src/test/resources/20/ous/escidoc_24022"));
		
		assertTrue(LocationHelper.getLocation("escidoc:persistent22") != null);
		assertTrue("Is " + LocationHelper.getLocation("escidoc:persistent22"), 
				LocationHelper.getLocation("escidoc:persistent22").endsWith("indexer/src/test/resources/20/ous/escidoc_persistent22"));
	}

}
