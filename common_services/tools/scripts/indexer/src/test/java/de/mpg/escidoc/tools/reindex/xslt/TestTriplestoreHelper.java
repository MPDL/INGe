package de.mpg.escidoc.tools.reindex.xslt;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.escidoc.tools.util.xslt.TriplestoreHelper;

public class TestTriplestoreHelper
{
	private static TriplestoreHelper helper;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		helper = TriplestoreHelper.getInstance();
	}

	@Test
	public void testGetTableName()
	{
		assertTrue(helper.getContextOuRelationName() != null);
		assertTrue(helper.getContextOuRelationName().startsWith("t"));
	}
	
	@Test
	public void testGetOrganizationFor()
	{
		assertTrue(helper.getOrganizationFor("escidoc:1703283") != null);
		assertTrue(helper.getOrganizationFor("escidoc:1703283").equals("<info:fedora/escidoc:1664137>"));
	}

}
