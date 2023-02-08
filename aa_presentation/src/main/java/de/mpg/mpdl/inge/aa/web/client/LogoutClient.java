package de.mpg.mpdl.inge.aa.web.client;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class LogoutClient extends Client {

  private static final Logger logger = Logger.getLogger(LogoutClient.class);

  @Override
  protected void process(HttpServletRequest request, HttpServletResponse response) throws Exception {

    request.getSession().removeAttribute("authentication");

    try {
      response.sendRedirect(getLogoutUrl(request, response));
    } catch (IllegalStateException ise) {
      logger.warn("Caught IllegalStateException: DEBUG for more info");
      logger.debug("LogoutClient tried to send a redirect, but there was probably already a header defined.");
    }


  }

  protected String getLogoutUrl(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String target = request.getParameter("target");

    if (target != null) {
      return target;
    } else {
      logger.warn("No query parameter 'target' found for logging out.");
      return null;
    }
  }

}
