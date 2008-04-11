package de.mpg.escidoc.pubman.init;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import de.mpg.escidoc.pubman.util.statistics.InitStatistics;

public class InitializerServlet extends HttpServlet {

	@Override
	public void init() throws ServletException {
	    try
        {
            new InitStatistics().init();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
		
	}

}
