package de.mpg.escidoc.pubman.init;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.apache.log4j.Logger;
import de.mpg.escidoc.services.pubman.PubItemSimpleStatistics;

public class InitializerServlet extends HttpServlet {
    
    Logger logger = Logger.getLogger(InitializerServlet.class);

	@Override
	public void init() throws ServletException {
	    
	    
	    
	    //initialize report definitions for statistics
	    try
        {
	        InitialContext initialContext = new InitialContext();
            PubItemSimpleStatistics statistics = (PubItemSimpleStatistics) initialContext.lookup(PubItemSimpleStatistics.SERVICE_NAME);
            statistics.initReportDefinitionsInFramework();
            
            
        }
        catch (Exception e)
        {
           logger.debug("Problem with initializing statistics system");
        }
		
	}

}
