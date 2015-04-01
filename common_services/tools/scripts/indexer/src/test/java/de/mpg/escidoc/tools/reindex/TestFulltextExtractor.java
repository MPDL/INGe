package de.mpg.escidoc.tools.reindex;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class TestFulltextExtractor
{
	
	static FullTextExtractor extractor; 

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		extractor = new FullTextExtractor();
	}
	
	@Before
	public void setUp() throws IOException
	{
		extractor.getStatistic().clear();		
		
		for (File f : new File(extractor.getFulltextPath()).listFiles())
		{
			try
			{
				FileUtils.forceDelete(f);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testFilesOk() throws Exception
	{	
		extractor.init(new File("src/test/resources/19/escidoc_20017+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_20017+content+content.0"));
		
		assertTrue((new File(extractor.getFulltextPath(), "escidoc_20017+content+content.0.txt")).exists());
		assertTrue(extractor.getStatistic().getFilesTotal() == 1);
		assertTrue(extractor.getStatistic().getFilesErrorOccured() == 0);
		assertTrue(extractor.getStatistic().getFilesExtractionDone() == 1);
		assertTrue(extractor.getStatistic().getFilesSkipped() == 0);
		assertTrue(extractor.getStatistic().getErrorList().size() == 0);
		
		// only with iText successful 
		extractor.init(new File("src/test/resources/19/escidoc_28177+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_28177+content+content.0"));
		
		assertTrue((new File(extractor.getFulltextPath(), "escidoc_28177+content+content.0.txt")).exists());
		assertTrue(extractor.getStatistic().getFilesTotal() == 1);
		assertTrue(extractor.getStatistic().getFilesErrorOccured() == 0);
		assertTrue(extractor.getStatistic().getFilesExtractionDone() == 2);
		assertTrue(extractor.getStatistic().getFilesSkipped() == 0);
		assertTrue(extractor.getStatistic().getErrorList().size() == 0);
	}

	@Test
	public void testFilesOk1() throws Exception
	{	
		extractor.init(new File("src/test/resources/20/escidoc_2110752+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/20/escidoc_2110752+content+content.0"));
		
		assertTrue((new File(extractor.getFulltextPath(), "escidoc_2110752+content+content.0.txt")).exists());
		assertTrue(extractor.getStatistic().getFilesTotal() == 1);
		assertTrue(extractor.getStatistic().getFilesErrorOccured() == 0);
		assertTrue(extractor.getStatistic().getFilesExtractionDone() == 1);
		assertTrue(extractor.getStatistic().getFilesSkipped() == 0);
		assertTrue(extractor.getStatistic().getErrorList().size() == 0);
	}

	
	@Test
	public void testDirOk() throws Exception
	{
		
		extractor.init(new File("src/test/resources/19"));
		extractor.extractFulltexts(new File("src/test/resources/19"));
		
		assertTrue((new File(extractor.getFulltextPath(), "escidoc_28177+content+content.0.txt")).exists());
		assertTrue((new File(extractor.getFulltextPath(), "escidoc_20017+content+content.0.txt")).exists());
		
		assertTrue("Expected 2 Found " + extractor.getStatistic().getFilesTotal(), extractor.getStatistic().getFilesTotal() == 2);
		assertTrue("Expected 0 Found " + extractor.getStatistic().getFilesErrorOccured(), extractor.getStatistic().getFilesErrorOccured() == 0);
		assertTrue(extractor.getStatistic().getFilesExtractionDone() == 2);
		assertTrue(extractor.getStatistic().getFilesSkipped() == 0);
		assertTrue(extractor.getStatistic().getErrorList().size() == 0);
	}
	
	@Test
	public void testDirWithHtmlFile() throws Exception
	{
		
		extractor.init(new File("C:/Test/datastreams-2013-2015-03-09/X/fedora/data/datastreams/2013/0102/15/33"));
		extractor.extractFulltexts(new File("C:/Test/datastreams-2013-2015-03-09/X/fedora/data/datastreams/2013/0102/15/33"));
		
		assertTrue(!(new File(extractor.getFulltextPath(), "escidoc_1587192+content+content.0.txt")).exists());
		
		assertTrue(extractor.getStatistic().getFilesTotal() == 1);
		assertTrue("expected <1> got <" + extractor.getStatistic().getFilesErrorOccured() + ">", extractor.getStatistic().getFilesErrorOccured() == 1) ;
		assertTrue(extractor.getStatistic().getFilesExtractionDone() == 0);
		assertTrue(extractor.getStatistic().getFilesSkipped() == 0);
		assertTrue(extractor.getStatistic().getErrorList().size() == 1);
	}
	
	@Test
	public void testLastModifiedFile1() throws Exception
	{
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		
		// extract all modified since 2015-03-12. Thetestfiles have lastModificationDate 2015-03-14.
		extractor.init(new File("src/test/resources/19/escidoc_20017+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_20017+content+content.0"), f.parse("2015-03-12").getTime());
		
		assertTrue((new File(extractor.getFulltextPath(), "escidoc_20017+content+content.0.txt")).exists());
		assertTrue(extractor.getStatistic().getFilesTotal() == 1);
		assertTrue(extractor.getStatistic().getFilesErrorOccured() == 0);
		assertTrue(extractor.getStatistic().getFilesExtractionDone() == 1);
		assertTrue(extractor.getStatistic().getFilesSkipped() == 0);
		assertTrue(extractor.getStatistic().getErrorList().size() == 0);

	}
	
	@Test
	public void testLastModifiedFile2() throws Exception
	{
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		
		extractor.init(new File("src/test/resources/19/escidoc_20017+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_20017+content+content.0"), f.parse("2016-03-12").getTime());
		
		assertTrue(!(new File(extractor.getFulltextPath(), "escidoc_20017+content+content.0.txt")).exists());
		assertTrue(extractor.getStatistic().getFilesTotal() == 1);
		assertTrue(extractor.getStatistic().getFilesErrorOccured() == 0);
		assertTrue(extractor.getStatistic().getFilesExtractionDone() == 0);
		assertTrue(extractor.getStatistic().getFilesSkipped() == 1);
		assertTrue(extractor.getStatistic().getErrorList().size() == 0);

	}
	
	
}
