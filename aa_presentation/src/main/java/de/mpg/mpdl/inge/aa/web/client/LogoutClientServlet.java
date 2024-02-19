package de.mpg.mpdl.inge.aa.web.client;

import de.mpg.mpdl.inge.util.PropertyReader;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author haarlaender
 *
 */
@SuppressWarnings("serial")
public class LogoutClientServlet extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    doPost(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    try {
      String clientClassName = PropertyReader.getProperty(PropertyReader.INGE_AA_CLIENT_LOGOUT_CLASS);
      if (null != clientClassName) {

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
