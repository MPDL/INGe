package de.mpg.escidoc.tools.reindex;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class TestIndexerSmall extends TestBase
{
	protected static FullTextExtractor extractor;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		
		extractor = new FullTextExtractor();
		
		extractor.init(new File("src/test/resources/19/escidoc_2110752+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2110752+content+content.0"));
		
		extractor.init(new File("src/test/resources/19/escidoc_2111415+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2111415+content+content.0"));
		
		referenceIndexPath = "C:/tmp/jboss/server/default/data/index/lucene/escidoc_all";	
	}
	
	@Before
	public void setUp() throws Exception
	{
		indexer = new Indexer(new File("src/test/resources/20"), "escidoc_all");
		indexer.createDatabase();
		indexer.prepareIndex();
		indexer.getIndexingReport().clear();
	}

	

	@Test
	// escidoc_2110119 with component escidoc_2110752
	// escidoc:2110541 item mit 1 components (escidoc:2111415 internal, public visibility)
	// escidoc:2095302 item mit 1 locator
	// some items have no reference
	public void testDir() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 4 Found " + indexer.getItemCount(), indexer.getItemCount() == 4);
		
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesIndexingDone() == 4);
		assertTrue(indexer.getIndexingReport().getFilesSkipped() == 0);
	}
	
	@Test
	// escidoc_2110501 without component
	// has reference
	public void testItemWithoutComponent() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110501"));
		indexer.finalizeIndex();
		
		super.verify();
		
		assertTrue("Expected 1 Found " + indexer.getItemCount(), indexer.getItemCount() == 1);
		
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesSkipped() == 0);
	}
	
	// escidoc:2110541 item mit 1 components (escidoc:2111415 internal, public visibility)
	// has reference
	@Test
	public void testItemWithVisibleComponent() throws Exception
	{	
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110541"));
		indexer.finalizeIndex();
		
		super.verify();
		
		assertTrue("Expected 1 Found " + indexer.getItemCount(), indexer.getItemCount() == 1);
	}
	
	// escidoc:2095302 item with 1 locator
	// has no reference
	@Test
	public void testItemWithLocator() throws Exception
	{

		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2095302"));
		indexer.finalizeIndex();
				
		assertTrue("Expected 1 Found " + indexer.getItemCount(), indexer.getItemCount() == 1);
	}

}
