package de.mpg.escidoc.main;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.util.TransformationReport;

public class ComponentPidTransformerTest
{
	private static TransformationReport report = null; 
	private static ComponentPidTransformer pidMigr = null;
	
	private static Logger logger = Logger.getLogger(ComponentPidTransformerTest.class);
	
	@BeforeClass
	public static void before() throws Exception
	{
		FileUtils.deleteQuietly(new File(ComponentPidTransformer.LOCATION_FILE_XML));
		
		pidMigr = new ComponentPidTransformer();
		pidMigr.createLocationFile(new File("src/test/resources"));	
		
		report = new TransformationReport(); 
	}
	
	@Before
	public void setUp() throws Exception
	{
		report.clear();
			
		FileUtils.writeStringToFile(new File(ComponentPidTransformer.SUCCESS_FILE_LOG), "", false);
		
	}
	
	@Test
	public void testStoreLocation() throws Exception
	{
		pidMigr.createLocationFile(new File("src/test/resources"));		
		assertTrue(new File(ComponentPidTransformer.LOCATION_FILE_XML).exists());
	}
	
	@Test
	//
	// release version 2 - two components: escidoc_2111688 external url, escidoc_2111687 internal_managed
	public void testUpdatePid_2111689() throws Exception
	{
		assertTrue(new File(ComponentPidTransformer.LOCATION_FILE_XML).exists());
		pidMigr.transform(new File("src/test/resources/item/escidoc_2111689"));
		
		assertTrue(pidMigr.getReport().getErrorList().size() == 0);
		assertTrue("Expected 1 returned " +  pidMigr.getReport().getFilesTotal(), pidMigr.getReport().getFilesTotal() == 1);
		assertTrue("Expected 1 returned " +  pidMigr.getReport().getFilesMigrationDone(), pidMigr.getReport().getFilesMigrationDone() == 1);
		
		Iterator<String> it = FileUtils.readLines(new File(ComponentPidTransformer.SUCCESS_FILE_LOG), "UTF-8").iterator(); 
		while(it.hasNext())
		{
			String line = it.next();
			logger.info(line);
			assertTrue(line.contains("escidoc:2111689 hdl:11858/00-001M-0000-0025-7377-9"));	
		}
	}
	
	@Test
	//
	// release version 1 - one components: escidoc_2169636 internal_managed
	// submitted version 2 - one components: escidoc_2170639 internal_managed
	// release version 3 - one components: escidoc_2170641 internal_managed
	public void testUpdatePid_2169637() throws Exception
	{
		assertTrue(new File(ComponentPidTransformer.LOCATION_FILE_XML).exists());
		pidMigr.transform(new File("src/test/resources/item/escidoc_2169637"));
		
		assertTrue(pidMigr.getReport().getErrorList().size() == 0);
		assertTrue("Expected 2 returned " +  pidMigr.getReport().getFilesTotal(), pidMigr.getReport().getFilesTotal() == 2);
		assertTrue("Expected 2 returned " +  pidMigr.getReport().getFilesMigrationDone(), pidMigr.getReport().getFilesMigrationDone() == 2);
		
		Iterator<String> it = FileUtils.readLines(new File(ComponentPidTransformer.SUCCESS_FILE_LOG), "UTF-8").iterator(); 
		while(it.hasNext())
		{
			String line = it.next();
			logger.info(line);
			assertTrue(line.contains("escidoc:2169637 hdl:11858/00-001Z-0000-0026-5162-7")
					|| line.contains("escidoc:2169637 hdl:11858/00-001Z-0000-0026-515F-2"));	
		}
	}

	
	
	@Test
	//
	// release version 1 - no component
	public void testUpdatePid_2110501() throws Exception
	{
		assertTrue(new File(ComponentPidTransformer.LOCATION_FILE_XML).exists());
		pidMigr.transform(new File("src/test/resources/item/escidoc_2110501"));
		
		assertTrue(pidMigr.getReport().getErrorList().size() == 0);
		
		Iterator<String> it = FileUtils.readLines(new File(ComponentPidTransformer.SUCCESS_FILE_LOG), "UTF-8").iterator(); 
		while(it.hasNext())
		{
			String line = it.next();
			logger.info(line);
			assertTrue(line.isEmpty());	
		}
	}
	
	@Test
	//
	// release version 1 - one component escidoc_2110507
	public void testUpdatePid_2110508() throws Exception
	{
		assertTrue(new File(ComponentPidTransformer.LOCATION_FILE_XML).exists());
		pidMigr.transform(new File("src/test/resources/item/escidoc_2110508"));
		
		assertTrue(pidMigr.getReport().getErrorList().size() == 0);
		
		Iterator<String> it = FileUtils.readLines(new File(ComponentPidTransformer.SUCCESS_FILE_LOG), "UTF-8").iterator(); 
		while(it.hasNext())
		{
			String line = it.next();
			logger.info(line);
			assertTrue(line.contains("escidoc:2110508 hdl:11858/00-001M-0000-0025-0ADE-6"));	
		}
	}
	
	@Test
	//
	// release version 1 - no component
	public void testUpdatePid_2110518() throws Exception
	{
		assertTrue(new File(ComponentPidTransformer.LOCATION_FILE_XML).exists());
		pidMigr.transform(new File("src/test/resources/item/escidoc_2110518"));
		
		assertTrue(pidMigr.getReport().getErrorList().size() == 0);
		
		Iterator<String> it = FileUtils.readLines(new File(ComponentPidTransformer.SUCCESS_FILE_LOG), "UTF-8").iterator(); 
		while(it.hasNext())
		{
			String line = it.next();
			logger.info(line);
			assertTrue(line.isEmpty());	
		}
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
