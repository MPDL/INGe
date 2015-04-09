package de.mpg.escidoc.tools.reindex;

import static org.junit.Assert.assertTrue;

import java.io.File;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class TestIndexerSmall extends TestBase
{
	protected static FullTextExtractor extractor;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		indexer = new Indexer(new File("../test-classes/20"), "escidoc_all");
		indexer.createDatabase();
		indexer.prepareIndex();
		extractor = new FullTextExtractor();
		
		extractor.init(new File("../test-classes/19/escidoc_2110752+content+content.0"));
		extractor.extractFulltexts(new File("../test-classes/19/escidoc_2110752+content+content.0"));
		
		referenceIndexPath = "C:/tmp/jboss/server/default/data/index/lucene/escidoc_all";
		
	}

	

	@Test
	// escidoc_2110119 with component escidoc_2110752
	public void test() throws Exception
	{
		indexer.indexItemsStart(new File("../test-classes/20"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 1 Found " + indexer.getItemCount(), indexer.getItemCount() == 1);
	}

}
