package de.mpg.mpdl.inge.aa.web.client;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.mpg.mpdl.inge.aa.Config;

/**
 * 
 * @author haarlaender
 * 
 */
@SuppressWarnings("serial")
public class LogoutClientServlet extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doPost(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      String clientClassName = Config.getProperty("inge.aa.client.logout.class");
      if (clientClassName != null) {

        Class<?> clientClass = Class.forName(clientClassName);
        LogoutClient client = (LogoutClient) clientClass.newInstance();
        client.process(request, response);
      } else {
        new LogoutClient().process(request, response);
      }
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
}
