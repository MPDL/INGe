package de.mpg.escidoc.tools.reindex;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Fieldable;
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
		
		assertTrue("Expected 5 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 5);
		
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue("Is "+ indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType(), 
				indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == 14);
	}
	
	@Test
	// escidoc_2110501 without component
	// has reference
	public void testItemWithoutComponent() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110501"));
		indexer.finalizeIndex();
		
		super.verify();
		
		assertTrue("Expected 1 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
	}
	
	// escidoc:2110541 item with 1 components (escidoc:2111415 internal, public visibility)
	// has reference
	@Test
	public void testItemWithVisibleComponent() throws Exception
	{	
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110541"));
		indexer.finalizeIndex();
		
		super.verify();
		
		assertTrue("Expected 1 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
	}
	
	// escidoc:2095302 item with 1 locator
	// has no reference
	@Test
	public void testItemWithLocator() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2095302"));
		indexer.finalizeIndex();
				
		assertTrue("Expected 1 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		
		Map<String, Set<Fieldable>> fieldMap = super.getFieldsOfDocument();
		
		Set<Fieldable> fields = fieldMap.get("stored_filename1");
		assertTrue(fields == null);
		assertTrue(fieldMap.get("stored_filename1") == null);
		assertTrue(fieldMap.get("stored_fulltext1") == null);
	}
	
	// escidoc:2146780 item with 1 component (escidoc:2147085 internal-managed, visibility private)
	// has no reference
	@Test
	public void testItemWithPrivateComponent() throws Exception
	{

		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2146780"));
		indexer.finalizeIndex();
				
		assertTrue("Expected 1 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		
		Map<String, Set<Fieldable>> fieldMap = super.getFieldsOfDocument();
		
		Set<Fieldable> fields = fieldMap.get("stored_filename1");
		assertTrue(fields == null);
		fields = fieldMap.get("stored_fulltext");
		assertTrue(fields == null);
	}

	
	// escidoc:2110484 import task item with 2 component (escidoc:211083 and escidoc:2110482)
	// has no reference
	@Test
	public void testImportTaskItem() throws Exception
	{

		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110484"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 0 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 0);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		
		Map<String, Set<Fieldable>> fieldMap = super.getFieldsOfDocument();
		
		assertTrue(fieldMap == null);
	}
	
	// escidoc:2087580 item without component in status pending
	// has no reference
	@Test
	public void testPendingItem() throws Exception
	{

		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2087580"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 0 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 0);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesIndexingDone() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		
		Map<String, Set<Fieldable>> fieldMap = super.getFieldsOfDocument();
		
		assertTrue(fieldMap == null);
	}
	
	// escidoc:2110486 released item with locator escidoc:2110485
	// has no reference
	@Test
	@Ignore
	public void testReleasedItemWithLocator() throws Exception
	{

		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110486"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 1 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		
		Map<String, Set<Fieldable>> fieldMap = super.getFieldsOfDocument();
		assertTrue(fieldMap != null);
		
		Set<Fieldable> fields = fieldMap.get("stored_filename1");
		assertTrue(fields == null);
		assertTrue(fieldMap.get("stored_filename1") == null);
		assertTrue(fieldMap.get("stored_fulltext1") == null);
	}
	
	// escidoc:2110474 released item (2 versions)
	// has reference
	@Test
	public void testReleasedItemWithoutComponent() throws Exception
	{

		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110474"));
		indexer.finalizeIndex();
		
		super.verify();
		
		assertTrue("Expected 1 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		
		Map<String, Set<Fieldable>> fieldMap = super.getFieldsOfDocument();
		assertTrue(fieldMap != null);
		
		
	}

}
