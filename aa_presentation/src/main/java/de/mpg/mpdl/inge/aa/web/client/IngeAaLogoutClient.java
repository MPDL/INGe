package de.mpg.mpdl.inge.aa.web.client;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import de.mpg.mpdl.inge.aa.Aa;

/**
 * 
 * @author haarlaender
 * 
 */
public class IngeAaLogoutClient extends LogoutClient {
  @Override
  protected String getLogoutUrl(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String originalTarget = request.getParameter("target");

    Aa aa = new Aa(request);
    if (aa.getAuthenticationVO() != null) {
      IngeAaClientFinish.logoutInInge(aa.getAuthenticationVO().getToken());
    }

    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }

    return originalTarget;
  }
}
