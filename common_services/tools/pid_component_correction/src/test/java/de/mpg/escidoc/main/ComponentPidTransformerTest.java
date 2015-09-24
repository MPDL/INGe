package de.mpg.escidoc.main;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.escidoc.util.TransformationReport;

public class ComponentPidTransformerTest
{
	private static TransformationReport report = null; 
	private static ComponentPidTransformer pidMigr = null;
	
	@BeforeClass
	public static void before() throws IOException
	{
		FileUtils.forceDelete(new File("./locationFile.xml"));
	}
	
	@Before
	public void setUp()
	{
		report = new TransformationReport(); 
		report.clear();
		pidMigr = new ComponentPidTransformer();
		
	}
	
	@Test
	public void testStoreLocation() throws Exception
	{
		pidMigr.storeLocation(new File("src/test/resources"));		
		assertTrue(new File("./locationFile.xml").exists());
	}
	
	@Test
	public void testUpdatePid() throws Exception
	{
		pidMigr.storeLocation(new File("src/test/resources"));		
		assertTrue(new File("./locationFile.xml").exists());
		
		pidMigr.transform(new File("src/test/resources"));
	}

}
