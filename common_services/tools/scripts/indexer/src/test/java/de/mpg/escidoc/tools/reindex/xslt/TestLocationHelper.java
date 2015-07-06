package de.mpg.escidoc.tools.reindex.xslt;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import de.mpg.escidoc.tools.reindex.Indexer;
import de.mpg.escidoc.tools.reindex.Validator;
import de.mpg.escidoc.tools.util.xslt.LocationHelper;

public class TestLocationHelper
{
	protected static Indexer indexer;
	
	@Before
	public void setUp() throws Exception
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
				LocationHelper.getLocation("escidoc:persistent13").equals("C:/pubMan/projects/indexer/src/test/resources/20/ous/escidoc_persistent13"));

		
		assertTrue("Is " + LocationHelper.getLocation("escidoc:24022"), 
				LocationHelper.getLocation("escidoc:24022").equals("C:/pubMan/projects/indexer/src/test/resources/20/ous/escidoc_24022"));
		
		assertTrue(LocationHelper.getLocation("escidoc:persistent22") != null);
		assertTrue("Is " + LocationHelper.getLocation("escidoc:persistent22"), 
				LocationHelper.getLocation("escidoc:persistent22").equals("C:/pubMan/projects/indexer/src/test/resources/20/ous/escidoc_persistent22"));
	}

}
