package de.mpg.escidoc.tools.reindex;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class TestIndexerBig extends TestBase
{
	
	@BeforeClass
	@Ignore
	public static void setUpBeforeClass() throws Exception
	{
		/*
		extractor = new FullTextExtractor();
		
		extractor.init(new File("C:/Test/data/datastreams/2015"));
		extractor.extractFulltexts(new File("C:/Test/data/datastreams/2015"));
		*/
		indexer = new Indexer(new File("C:/Test/data/objects/2015"), "escidoc_all");
		indexer.createDatabase();
		indexer.prepareIndex();
		
		referenceIndexPath = "C:/tmp/jboss/server/default/data/index/lucene/escidoc_all";
		
	}

	@Test
	public void test_2015_03() throws Exception
	{
		indexer.indexItemsStart(new File("C:/Test/data/objects/2015"));
		indexer.finalizeIndex();
		
		super.verify();
	}

}
