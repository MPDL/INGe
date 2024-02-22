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
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
    doPost(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
    try {
      String clientClassName = PropertyReader.getProperty(PropertyReader.INGE_AA_CLIENT_LOGOUT_CLASS);
      if (null != clientClassName) {

        Class<?> clientClass = Class.forName(clientClassName);
        LogoutClient client = (LogoutClient) clientClass.newInstance();
        client.process(req, resp);
      } else {
        new LogoutClient().process(req, resp);
      }
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
}
