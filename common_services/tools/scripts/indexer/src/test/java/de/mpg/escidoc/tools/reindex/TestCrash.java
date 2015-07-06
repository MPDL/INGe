package de.mpg.escidoc.tools.reindex;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestCrash
{
	protected static Indexer indexer;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}
	
	@Before
	public void setUp() throws Exception
	{
		indexer = new Indexer(new File("src/test/resources/crash"));
		indexer.init();

		indexer.setCreateIndex(true);
		indexer.prepareIndex();
		indexer.getIndexingReport().clear();
	}

	@Test
	public void test1() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/crash/escidoc_1859279"));
		indexer.finalizeIndex();
		
		assertTrue(indexer.getIndexingReport().getErrorList().size() != 0);
		assertTrue(indexer.getIndexingReport().getFilesIndexingDone() == 0);
	}
	
	@Test
	public void test2() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/crash/escidoc_1859278"));
		indexer.finalizeIndex();
		
		assertTrue(indexer.getIndexingReport().getErrorList().size() != 0);
		assertTrue(indexer.getIndexingReport().getFilesIndexingDone() == 0);
	}
	
	@Test
	public void test_1859264() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/crash/1205/10/07/escidoc_1859264"));
		indexer.finalizeIndex();
		
		assertTrue(indexer.getIndexingReport().getErrorList().size() != 0);
		assertTrue(indexer.getIndexingReport().getFilesIndexingDone() == 0);
	}
	
	@Test
	public void testAll() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/crash/1205"));
		indexer.finalizeIndex();
		
		assertTrue(indexer.getIndexingReport().getErrorList().size() != 0);
	}
	
	@Test
	public void test_1205_11() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/crash/1205/11"));
		indexer.finalizeIndex();
		
		assertTrue(indexer.getIndexingReport().getErrorList().size() != 0);
	}

}
