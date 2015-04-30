package de.mpg.escidoc.tools.reindex;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class TestIndexerBig extends TestBase
{

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		indexer = new Indexer(new File("C:/Test/data/objects/2015/0310/09/37"), "escidoc_all");
		indexer.createDatabase();
		indexer.prepareIndex();
		
		referenceIndexPath = "C:/tmp/jboss/server/default/data/index/lucene/escidoc_all";
		
	}

	// escidoc:2123284 ohne components - escidoc:2123287 import task item mit 2 components
	@Test
	@Ignore
	public void test() throws Exception
	{
		indexer.indexItemsStart(new File("C:/Test/data/objects/2015/0331"));
		indexer.finalizeIndex();
		
		super.verify();
		
		assertTrue("Expected 1 Found " + indexer.getItemCount(), indexer.getItemCount() == 1);
	}

	// escidoc:2110490 item mit 2 components (escidoc:2110488 internal managed, audience visibility, escidoc:2110489 external managed)
	// ous used in the same subdirectory
	@Test
	public void test1() throws Exception
	{
		indexer.indexItemsStart(new File("C:/Test/data/objects/2015/0310/09/37"));
		indexer.finalizeIndex();
		
		super.verify();
		
		assertTrue("Expected 1 Found " + indexer.getItemCount(), indexer.getItemCount() == 1);
	}

}
