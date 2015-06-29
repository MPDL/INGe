package de.mpg.escidoc.tools.reindex.xslt;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.mpg.escidoc.tools.util.xslt.LocationHelper;

public class TestLocationHelper
{
	@Test
	public void test()
	{
		assertTrue(LocationHelper.getLocation("escidoc:persistent35") != null);
		assertTrue("Is " + LocationHelper.getLocation("escidoc:persistent35"), 
				LocationHelper.getLocation("escidoc:persistent35").equals("C:/Test/data/objects/2015/ous/escidoc_persistent35"));

		
		assertTrue("Is " + LocationHelper.getLocation("escidoc:976549"), 
				LocationHelper.getLocation("escidoc:976549").equals("C:/Test/data/objects/2015/ous/escidoc_976549"));
	}

}
