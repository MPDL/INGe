package de.mpg.escidoc.main;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.escidoc.util.TransformationReport;

public class ComponentPidTransformerTest
{
	private static TransformationReport report = null; 
	private static ComponentPidTransformer pidMigr = null;
	
	private static Logger logger = Logger.getLogger(ComponentPidTransformerTest.class);
	
	@BeforeClass
	public static void before() throws IOException
	{
		FileUtils.deleteQuietly(new File(ComponentPidTransformer.LOCATION_FILE_XML));
	}
	
	@Before
	public void setUp() throws IOException
	{
		report = new TransformationReport(); 
		report.clear();
		pidMigr = new ComponentPidTransformer();
		
		FileUtils.writeStringToFile(new File(ComponentPidTransformer.SUCCESS_FILE_LOG), "", false);
		
	}
	
	@Test
	public void testStoreLocation() throws Exception
	{
		pidMigr.createLocationFile(new File("src/test/resources"));		
		assertTrue(new File(ComponentPidTransformer.LOCATION_FILE_XML).exists());
	}
	
	@Test
	public void testUpdatePid() throws Exception
	{
		pidMigr.createLocationFile(new File("src/test/resources"));
		assertTrue(new File(ComponentPidTransformer.LOCATION_FILE_XML).exists());
		pidMigr.transform(new File("src/test/resources/item"));
		
		assertTrue(pidMigr.getReport().getErrorList().size() == 0);
		assertTrue("Expected 5 returned " +  pidMigr.getReport().getFilesTotal(), pidMigr.getReport().getFilesTotal() == 5);
		assertTrue("Expected 4 returned " +  pidMigr.getReport().getFilesMigrationDone(), pidMigr.getReport().getFilesMigrationDone() == 4);
		
		Iterator<String> it = FileUtils.readLines(new File(ComponentPidTransformer.SUCCESS_FILE_LOG), "UTF-8").iterator(); 
		while(it.hasNext())
		{
			String line = it.next();
			logger.info(line);
			assertTrue(line.contains("escidoc:2110508 hdl:11858/00-001M-0000-0025-0ADE-6")
					|| line.contains("escidoc:2111689 hdl:11858/00-001M-0000-0025-7377-9")
					|| line.contains("escidoc:2169637 hdl:11858/00-001Z-0000-0026-515F-2")
					|| line.contains("escidoc:2169637 hdl:11858/00-001Z-0000-0026-5162-7"));	
		}
	}

}
