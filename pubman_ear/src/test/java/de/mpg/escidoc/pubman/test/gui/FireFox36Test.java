package de.mpg.escidoc.pubman.test.gui;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;

import com.thoughtworks.selenium.DefaultSelenium;

public class FireFox36Test extends PubmanGuiTestcase
{

	public FireFox36Test() throws IOException {
		super();
	}
	
	@Before
	public void setUp() throws Exception {
//		selenium = new DefaultSelenium("localhost", 4444, "*chrome", "http://dev-pubman.mpdl.mpg.de/");
		selenium = new DefaultSelenium("localhost", 4444, "*firefox3 C:/Program Files (x86)/Mozilla Firefox/firefox.exe", "http://localhost:8080/");
		selenium.start();
	}


	@After
	public void tearDown() throws Exception {
		selenium.stop();
	}

}
