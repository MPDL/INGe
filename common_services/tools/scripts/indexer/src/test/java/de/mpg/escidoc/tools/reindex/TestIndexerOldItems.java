package de.mpg.escidoc.tools.reindex;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Fieldable;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.escidoc.tools.util.xslt.LocationHelper;

public class TestIndexerOldItems
{
	protected static Indexer indexer;
	protected static FullTextExtractor extractor;
	protected static Validator validator;
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		extractor = new FullTextExtractor();
		
		extractor.init(new File("src/test/resources/olditems/escidoc_76991+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/olditems/escidoc_76991+content+content.0"));
	}
	
	@Before
	public void setUp() throws Exception
	{
		indexer = new Indexer(new File("src/test/resources/olditems"));
		indexer.init();

		indexer.setCreateIndex(true);
		indexer.prepareIndex();
		indexer.getIndexingReport().clear();
		
		LocationHelper.getLocation("escidoc_persistent22");
		
	}

	// item escidoc:76990 with one component ( escidoc:76991 )
	// component has no checksum element
	// has no reference
	@Test
	public void testItem_76990() throws Exception
	{

		indexer.indexItemsStart(new File("src/test/resources/olditems/escidoc_76990"));
		indexer.finalizeIndex();
				
		assertTrue("Expected 1 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		
		validator = new Validator(indexer);
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		
		Set<Fieldable> fields = fieldMap.get("xml_representation");
		assertTrue(fields != null);
		assertTrue(fields.size() == 1);
		
		Fieldable xml_representation = fields.iterator().next();
		
		assertTrue(!xml_representation.stringValue().contains("checksum"));
	}
	
	// escidoc:2110541 item with 1 components (escidoc:2111415 internal, public visibility)
	// has reference
	@Test
	public void testItemWithVisibleComponent() throws Exception
	{	
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110541"));
		indexer.finalizeIndex();
		
		validator = new Validator(indexer);
		
		assertTrue("Expected 1 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesIndexingDone() == 1);
		
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		
		Set<Fieldable> fields = fieldMap.get("xml_representation");
		assertTrue(fields != null);
		assertTrue(fields.size() == 1);
		
		Fieldable xml_representation = fields.iterator().next();
		
		// check if checksum element exists
		assertTrue(xml_representation.stringValue().contains("checksum"));
		
	
	}


}
