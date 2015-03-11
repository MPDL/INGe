package de.mpg.escidoc.tools.reindex;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestFulltextExtractor
{
	
	static FullTextExtractor extractor; 

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		extractor = new FullTextExtractor(new File("src/test/resources/19"));
	}

	@Test
	public void testFile() throws Exception
	{
		extractor.extractFulltext(new File("src/test/resources/19/escidoc_20017+content+content.0"));
		
		assertTrue((new File(extractor.getFulltextPath(), "escidoc_20017+content+content.0.txt")).exists());
		assertTrue(extractor.getStatistic().getFilesErrorOccured() == 0);
		assertTrue(extractor.getStatistic().getFilesExtractionDone() == 1);
		assertTrue(extractor.getStatistic().getErrorList().size() == 0);
	}
	
	@Test
	public void testFileFailure() throws Exception
	{
		extractor.extractFulltext(new File("src/test/resources/19/escidoc_28177+content+content.0"));
		
		assertTrue(!(new File(extractor.getFulltextPath(), "escidoc_28177+content+content.0.txt")).exists());
		
		assertTrue(extractor.getStatistic().getFilesErrorOccured() == 1);
		assertTrue(extractor.getStatistic().getFilesExtractionDone() == 1);
		assertTrue(extractor.getStatistic().getErrorList().size() == 1);
	}
	
	public void tearDown() throws IOException
	{
		for (File f : new File(extractor.getFulltextPath()).listFiles())
		{
			FileUtils.forceDelete(f);
		}
		
		extractor.getStatistic().clear();
				
	}

}
