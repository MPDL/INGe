package de.mpg.escidoc.tools.util.xslt;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestTriplestoreHelper
{

	@Test
	public void testGetTableName()
	{
		assertTrue(TriplestoreHelper.getInstance().getContextOuRelationName() != null);
		assertTrue(TriplestoreHelper.getInstance().getContextOuRelationName().startsWith("t"));
	}
	
	@Test
	public void testGetOrganizationFor()
	{
		assertTrue(TriplestoreHelper.getInstance().getOrganizationFor("escidoc:1703283") != null);
		assertTrue(TriplestoreHelper.getInstance().getOrganizationFor("escidoc:1703283").equals("<info:fedora/escidoc:1664137>"));
	}

}
