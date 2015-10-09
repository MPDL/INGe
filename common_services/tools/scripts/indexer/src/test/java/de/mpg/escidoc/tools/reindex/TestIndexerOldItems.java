package de.mpg.escidoc.tools.reindex;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Fieldable;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
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
		
		indexer = new Indexer(new File("src/test/resources/olditems"));
		indexer.init();

		indexer.setCreateIndex(true);
		indexer.prepareIndex();
		indexer.getIndexingReport().clear();
	}
	
	@Before
	public void setUp() throws Exception
	{
		LocationHelper.getLocation("escidoc_persistent22");
		
	}

	// item escidoc:76990 with one component ( escidoc:76991 )
	// component has no checksum element
	// causes heap space exception
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
		Set<Fieldable> fields = null;
		switch(indexer.currentIndexMode)
		{
			case LATEST_RELEASE:
				fields = fieldMap.get("xml_representation");				
				break;
			case LATEST_VERSION:
				fields = fieldMap.get("aa_xml_representation");
				break;
		}	
		
		assertTrue(fields != null);
		assertTrue(fields.size() == 1);
		
		Fieldable xml_representation = fields.iterator().next();
		
		assertTrue(!xml_representation.stringValue().contains("checksum"));
	}

}
