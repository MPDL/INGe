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
		
		extractor = new FullTextExtractor();
		
		extractor.init(new File("src/test/resources/19/escidoc_2110752+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2110752+content+content.0"));
		
		extractor.init(new File("src/test/resources/19/escidoc_2111415+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2111415+content+content.0"));
		
		referenceIndexPath = "C:/tmp/jboss/server/default/data/index/lucene/escidoc_all";
		
	}

	

	@Test
	@Ignore
	// escidoc_2110119 with component escidoc_2110752
	// has no reference
	public void test() throws Exception
	{
		indexer = new Indexer(new File("src/test/resources/20"), "escidoc_all");
		indexer.createDatabase();
		indexer.prepareIndex();
		
		indexer.indexItemsStart(new File("src/test/resources/20"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 1 Found " + indexer.getItemCount(), indexer.getItemCount() == 1);
	}
	
	// escidoc:2110541 item mit 1 components (escidoc:2111415 internal, public visibility)
	@Test
	@Ignore
	public void test1() throws Exception
	{
		indexer = new Indexer(new File("src/test/resources/20"), "escidoc_all");
		indexer.createDatabase();
		indexer.prepareIndex();
		
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110541"));
		indexer.finalizeIndex();
		
		super.verify();
		
		assertTrue("Expected 1 Found " + indexer.getItemCount(), indexer.getItemCount() == 1);
	}
	
	// escidoc:2095302 item mit 1 external url 
	// has no reference
	@Test
	public void test2() throws Exception
	{
		indexer = new Indexer(new File("src/test/resources/20"), "escidoc_all");
		indexer.createDatabase();
		indexer.prepareIndex();
		
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2095302"));
		indexer.finalizeIndex();
				
		assertTrue("Expected 1 Found " + indexer.getItemCount(), indexer.getItemCount() == 1);
	}

}
