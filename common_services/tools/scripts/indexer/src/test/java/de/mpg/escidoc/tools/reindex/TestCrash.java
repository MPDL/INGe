package de.mpg.escidoc.tools.reindex;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;


/**
 * These tests conatin items with ambigous defined date fileds causing LastdateHelper exceptions.
 * Has been solved now by taking the first date by modifying the index stylesheet.
 * @author sieders
 *
 */
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

	// produces the LastdateHelper exception
	@Test
	public void test_1859279() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/crash/escidoc_1859279"));
		indexer.finalizeIndex();
		
		assertTrue(indexer.getIndexingReport().getErrorList().size() == 0);
		assertTrue(indexer.getIndexingReport().getFilesIndexingDone() == 1);
	}
	
	@Test
	public void test_1859282() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/crash/1205/10/55/escidoc_1859282"));
		indexer.finalizeIndex();
		
		assertTrue(indexer.getIndexingReport().getErrorList().size() == 0);
		assertTrue(indexer.getIndexingReport().getFilesIndexingDone() == 1);
	}
	
	@Test
	public void test_1859278() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/crash/escidoc_1859278"));
		indexer.finalizeIndex();
		
		assertTrue(indexer.getIndexingReport().getErrorList().size() == 0);
		assertTrue(indexer.getIndexingReport().getFilesIndexingDone() == 1);
	}
	
	@Test
	public void test_1859264() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/crash/1205/10/07/escidoc_1859264"));
		indexer.finalizeIndex();
		
		assertTrue(indexer.getIndexingReport().getErrorList().size() == 0);
		assertTrue(indexer.getIndexingReport().getFilesIndexingDone() == 1);
	}
	
	@Test
	public void testCrashAll() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/crash/1205"));
		indexer.finalizeIndex();
		
		assertTrue(indexer.getIndexingReport().getErrorList().size() == 0);
	}
	
	@Test
	@Ignore
	public void testCrashAll_Loop() throws Exception
	{
		int i = 0;
		do
		{
		indexer.indexItemsStart(new File("src/test/resources/crash/1205"));
		
		} while(i++ < 1000);
		
		indexer.finalizeIndex();
		
		assertTrue(indexer.getIndexingReport().getErrorList().size() != 0);
	}
	
	@Test
	public void test_1205_11() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/crash/1205/11"));
		indexer.finalizeIndex();
		
		assertTrue(indexer.getIndexingReport().getErrorList().size() == 0);
	}

}
