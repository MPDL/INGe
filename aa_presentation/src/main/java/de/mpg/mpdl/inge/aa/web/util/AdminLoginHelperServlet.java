package de.mpg.mpdl.inge.aa.web.util;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.aa.Config;
import de.mpg.mpdl.inge.aa.TanStore;
import de.mpg.mpdl.inge.aa.web.client.IngeAaClientFinish;

@SuppressWarnings("serial")
public class AdminLoginHelperServlet extends HttpServlet {

  private final static Logger logger = LogManager.getLogger(AdminLoginHelperServlet.class);

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doPost(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String tan = getTan(request);

    String username = request.getParameter("username");
    String password = request.getParameter("password");

    String token;
    try {
      token = IngeAaClientFinish.loginInInge(username, password);
      if (token != null) {
        String aaInstanceUrl = Config.getProperty("inge.aa.instance.url");
        response.sendRedirect(aaInstanceUrl + "clientReturn?target="
            + URLDecoder.decode(request.getParameter("target"), StandardCharsets.UTF_8.toString()) + "&token=" + token + "&tan=" + tan);
      } else {
        response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      }
    } catch (Exception e) {
      logger.error("Error loggin in admin user.", e);
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
    }
  }

  private static String getTan(HttpServletRequest request) {
    String tan;
    do {
      tan = TanStore.createTan(request.getSession().getId());
    } while (!TanStore.storeTan(tan));
    return tan;
  }
}
