package de.mpg.escidoc.handler;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.naming.NamingException;

import org.junit.Ignore;
import org.junit.Test;

public class PIDProviderTest
{
	PIDProvider p = null;

	@Test
	@Ignore
	public void test1() throws Exception
	{
		p = new PIDProvider();
		
		String s = p.updateComponentPid("escidoc:656742", 
				"6", 
				"escidoc:656740", 
				"11858/00-001Z-0000-0023-5323-D", 
				"7818.html");
		
		
	}
	
	
	@Test
	public void test2() throws Exception
	{
		p = new PIDProvider();
		
		String s = p.updateComponentPid("escidoc:760634", 
				"4", 
				"escidoc:762001", 
				"11858/00-001Z-0000-0024-44F6-E", 
				"BGC2144.pdf");
		
		
	}
}
