import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;

import de.mpg.escidoc.handler.AllPidsSrwSearchResponseHandler;


public class TestAllPidsSrwSearchResponseHandler
{

	@Test
	public void test() throws Exception
	{
		
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		
		AllPidsSrwSearchResponseHandler handler = new AllPidsSrwSearchResponseHandler();
        parser.parse(new File("src/test/resources/srw1.xml"), handler);
        
        Set<String> pids = handler.getPids();
        
        assertTrue(pids.size() == 3);
        assertTrue(pids.contains("escidoc:227005 | hdl:someHandle/test/escidoc:227005"));
        assertTrue(pids.contains("escidoc:227005 | hdl:someHandle/test/escidoc:227005:2"));
        assertTrue(pids.contains("escidoc:227005 | hdl:someHandle/test/escidoc:227004"));
        assertTrue(handler.getNumberOfPidsMissing() == 0);
        
        handler = new AllPidsSrwSearchResponseHandler();
        parser.parse(new File("src/test/resources/srw2.xml"), handler);
        
        pids = handler.getPids();
        
        assertTrue(pids.size() == 3);
        assertTrue(pids.contains("escidoc:485321 | hdl:11858/00-001Z-0000-0022-5C81-E"));
        assertTrue(pids.contains("escidoc:485321 | hdl:11858/00-001Z-0000-0022-61CE-D"));
        assertTrue(pids.contains("escidoc:485321 | hdl:11858/00-001Z-0000-0022-61CF-B"));
        assertTrue(handler.getNumberOfPidsMissing() == 0);
        
        handler = new AllPidsSrwSearchResponseHandler();
        parser.parse(new File("src/test/resources/srw3.xml"), handler);
        
        pids = handler.getPids();
        
        assertTrue(pids.size() == 3);
        assertTrue(pids.contains("escidoc:486028 | hdl:11858/00-001Z-0000-0022-5CB4-B"));
        assertTrue(pids.contains("escidoc:486028 | hdl:11858/00-001Z-0000-0022-681F-3"));
        assertTrue(pids.contains("escidoc:486028 | hdl:11858/00-001Z-0000-0022-6820-E"));
        assertTrue(handler.getNumberOfPidsMissing() == 0);
        
        handler = new AllPidsSrwSearchResponseHandler();
        parser.parse(new File("src/test/resources/escidoc_pid_missing"), handler);
        
        pids = handler.getPids();
        
        assertTrue(pids.size() == 2);
        assertTrue(pids.contains("escidoc:2053317 | hdl:11858/00-001Z-0000-0023-BC45-E"));
        assertTrue(pids.contains("escidoc:2053317 | hdl:11858/00-001Z-0000-0023-BF27-9"));
        assertTrue(handler.getNumberOfPidsMissing() == 1);
        
        handler = new AllPidsSrwSearchResponseHandler();
        parser.parse(new File("src/test/resources/escidoc_component_ext"), handler);
        
        pids = handler.getPids();
        
        assertTrue(pids.size() == 1);
        assertTrue(pids.contains("null | hdl:11858/00-001Z-0000-0023-D8A7-9"));
        assertTrue(handler.getNumberOfPidsMissing() == 0);
	}

}
