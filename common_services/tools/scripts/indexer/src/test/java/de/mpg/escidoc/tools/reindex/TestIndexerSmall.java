package de.mpg.escidoc.tools.reindex;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Fieldable;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestIndexerSmall
{
	protected static Indexer indexer;
	protected static FullTextExtractor extractor;
	protected static Validator validator;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		
		extractor = new FullTextExtractor();
		
		extractor.init(new File("src/test/resources/19/escidoc_2110752+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2110752+content+content.0"));
		
		extractor.init(new File("src/test/resources/19/escidoc_2111415+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2111415+content+content.0"));
		
		extractor.init(new File("src/test/resources/19/escidoc_2110507+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2110507+content+content.0"));
		
		extractor.init(new File("src/test/resources/19/escidoc_2111497+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2111497+content+content.0"));
		
		extractor.init(new File("src/test/resources/19/escidoc_2111498+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2111498+content+content.0"));
		
		extractor.init(new File("src/test/resources/19/escidoc_2111499+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2111499+content+content.0"));
		
		extractor.init(new File("src/test/resources/19/escidoc_2111713+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2111713+content+content.0"));
		
		extractor.init(new File("src/test/resources/19/escidoc_2111687+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2111687+content+content.0"));
		
		extractor.init(new File("src/test/resources/19/escidoc_2120374+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2120374+content+content.0"));
		
		
	}
	
	@Before
	public void setUp() throws Exception
	{
		indexer = new Indexer(new File("src/test/resources/20"));
		indexer.init();

		indexer.prepareIndex();
		indexer.getIndexingReport().clear();
		
		validator = new Validator(indexer);
		validator.setReferencePath("C:/tmp/jboss/server/default/data/index/lucene/escidoc_all");
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
		
		assertTrue("Expected 16 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 16);
		
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue("Is "+ indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType(), 
				indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == 55);
	}
	
	@Test
	// escidoc_2110501 without component
	// has reference
	public void testItemWithoutComponent() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110501"));
		indexer.finalizeIndex();
		
		validator.compareToReferenceIndex();
		
		assertTrue("Expected 1 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(indexer.getIndexingReport().getErrorList().size() == 0);
	}
	
	// escidoc:2110541 item with 1 components (escidoc:2111415 internal, public visibility)
	// has reference
	@Test
	public void testItemWithVisibleComponent() throws Exception
	{	
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110541"));
		indexer.finalizeIndex();
		
		validator.compareToReferenceIndex();
		
		assertTrue("Expected 1 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(indexer.getIndexingReport().getErrorList().size() == 0);
	}
	
	// escidoc:2095302 item with 1 locator escidoc:2095301
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
		
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		
		Set<Fieldable> fields = fieldMap.get("stored_filename1");
		assertTrue(fields == null);
		assertTrue(fieldMap.get("stored_filename1") == null);
		assertTrue(fieldMap.get("stored_fulltext1") == null);
		
		assertTrue(fieldMap.get("stored_filename") == null);
		assertTrue(fieldMap.get("stored_fulltext") == null);
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
		
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		
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
		
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		
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
		
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		
		assertTrue(fieldMap == null);
	}
	
	// escidoc:2110486 released item with locator escidoc:2110485
	// has no reference
	@Test
	public void testReleased_2110486_locator() throws Exception
	{

		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110486"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 1 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		assertTrue(fieldMap != null);
		
		Set<Fieldable> fields = fieldMap.get("stored_filename1");
		assertTrue(fields == null);
		assertTrue(fieldMap.get("stored_filename1") == null);
		assertTrue(fieldMap.get("stored_fulltext1") == null);
		
		assertTrue(fieldMap.get("stored_filename") == null);
		assertTrue(fieldMap.get("stored_fulltext") == null);
		
		validator.compareToReferenceIndex();
		
		assertTrue(Arrays.toString(indexer.getIndexingReport().getErrorList().toArray()), 
				indexer.getIndexingReport().getErrorList().size() == 0);
	}
	
	// escidoc:2110474 released item (2 versions)
	// has reference
	@Test
	public void testReleasedItemWithoutComponent() throws Exception
	{

		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110474"));
		indexer.finalizeIndex();
		
		validator.compareToReferenceIndex();
		
		assertTrue("Expected 1 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(Arrays.toString(indexer.getIndexingReport().getErrorList().toArray()), 
				indexer.getIndexingReport().getErrorList().size() == 0);
		
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		assertTrue(fieldMap != null);
		
		
	}
	
	// escidoc:2110495 released item (1 locator escidoc:2110494)
	// has reference
	@Test
	public void testReleasedItem_2110495() throws Exception
	{

		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110495"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 1 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		assertTrue(fieldMap != null);
		
		Set<Fieldable> fields = fieldMap.get("stored_filename1");
		assertTrue(fields == null);
		assertTrue(fieldMap.get("stored_filename1") == null);
		assertTrue(fieldMap.get("stored_fulltext1") == null);
		
		assertTrue(fieldMap.get("stored_filename") == null);
		assertTrue(fieldMap.get("stored_fulltext") == null);
		
		validator.compareToReferenceIndex();	
		
		assertTrue(Arrays.toString(indexer.getIndexingReport().getErrorList().toArray()), 
				indexer.getIndexingReport().getErrorList().size() == 0);
	}
	
	// escidoc:2110508 released item (1 component escidoc:2110507)
	// has reference
	@Test
	public void testReleasedItem_2110508() throws Exception
	{

		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110508"));
		indexer.finalizeIndex();
		
		validator.compareToReferenceIndex();
		
		assertTrue("Expected 1 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(Arrays.toString(indexer.getIndexingReport().getErrorList().toArray()), 
				indexer.getIndexingReport().getErrorList().size() == 0);
		
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		assertTrue(fieldMap != null);
			
			
		}
	
	// escidoc:2110529 released item with locator (escidoc:2110533), many authors
	// has reference
	@Test
	public void testReleasedItem_2110529_locator() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110529"));
		indexer.finalizeIndex();
		
		validator.compareToReferenceIndex();
		
		assertTrue("Expected 1 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		assertTrue(fieldMap != null);	
		
		assertTrue(Arrays.toString(indexer.getIndexingReport().getErrorList().toArray()), 
				indexer.getIndexingReport().getErrorList().size() == 0);
	}
	
	// escidoc:2110549 released item with locator (escidoc:2110548)
	// has reference
	@Test
	public void testReleasedItem_2110549_locator() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110549"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 1 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		assertTrue(fieldMap != null);		
		
		validator.compareToReferenceIndex();	
		assertTrue(Arrays.toString(indexer.getIndexingReport().getErrorList().toArray()), 
				indexer.getIndexingReport().getErrorList().size() == 0);
	}
	
	// escidoc:2110608 withdrawn item
	// has no reference in escidoc_all index because of withdrawn status
	@Test
	public void testWithdrawnItem_2110608() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110608"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 0 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 0);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == 1);
		
	}
	
	// escidoc:2111614 released item without component
	// has reference, but indexer errors
	@Test
	public void testReleasedItem_2111614() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2111614"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 1 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == 0);
		
		//super.verify();
		
	}
	
	// escidoc:2111689 released item with 2 component; escidoc:2111688 locator and escidoc:2111687 component with pdf
	// has reference
	@Test
	public void testReleasedItem_2111689() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2111689"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 1 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == 0);
		
		validator.compareToReferenceIndex();
		assertTrue(Arrays.toString(indexer.getIndexingReport().getErrorList().toArray()), 
				indexer.getIndexingReport().getErrorList().size() == 0);
	}
	
	// escidoc:2111495 released item with 4 component; escidoc:2111493 locator and escidoc:2111497, 2111498, 2111499 components with pdf
	// has reference
	@Test
	public void testReleasedItem_2111495() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2111495"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 1 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == 0);
		
		validator.compareToReferenceIndex();
		assertTrue(Arrays.toString(indexer.getIndexingReport().getErrorList().toArray()), 
				indexer.getIndexingReport().getErrorList().size() == 0);
	}
	
	// escidoc:2111711 released item with 3 component; escidoc:2111710 locator and escidoc:2111713 pdf (audience), escidoc:2111714 htm (public)
	// has reference
	@Test
	public void testReleasedItem_2111711() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2111711"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 1 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == 0);
		
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		
		// one component has audience visibility, the other is of mime-type text/html (non supported for text extraction) 
		Set<Fieldable> fields = fieldMap.get("stored_filename");
		assertTrue(fields == null);
		assertTrue(fieldMap.get("stored_filename") == null);
		assertTrue(fieldMap.get("stored_fulltext") == null);
		
		
		validator.compareToReferenceIndex();
		assertTrue(Arrays.toString(indexer.getIndexingReport().getErrorList().toArray()), 
				indexer.getIndexingReport().getErrorList().size() == 0);
	}
	
	// escidoc:2120373 released item with 1 component; escidoc:2120374 text/plain (public)
	// has reference
	@Test
	public void testReleasedItem_2120373() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2120373"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 1 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == 0);
		
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		
		// one component has audience visibility, the other is of mime-type text/html (non supported for text extraction) 
		Set<Fieldable> fields = fieldMap.get("stored_filename");
		assertTrue(fields != null);
		assertTrue(fieldMap.get("stored_filename") != null);
		assertTrue(fieldMap.get("stored_fulltext") != null);
		
		Iterator<Fieldable> it = fieldMap.get("stored_fulltext").iterator();
		boolean found = false;
		while (it.hasNext())
		{
			Fieldable f = (Fieldable)it.next();
			
			if (f.stringValue().contains("Book"))
			{
				found = true;
				break;
			}
		}
		assertTrue(found);
		
		validator.compareToReferenceIndex();
		assertTrue(Arrays.toString(indexer.getIndexingReport().getErrorList().toArray()), 
				indexer.getIndexingReport().getErrorList().size() == 0);
	}

}
