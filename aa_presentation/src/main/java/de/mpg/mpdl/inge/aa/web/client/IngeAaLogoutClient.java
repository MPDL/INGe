package de.mpg.mpdl.inge.aa.web.client;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.mpg.mpdl.inge.aa.Aa;
import de.mpg.mpdl.inge.aa.AuthenticationVO;
import de.mpg.mpdl.inge.aa.Config;

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
