package de.mpg.mpdl.inge.aa.web.client;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author haarlaender
 * 
 */
public class EscidocAaLogoutClient extends LogoutClient {
  @Override
  protected String getLogoutUrl(HttpServletRequest request, HttpServletResponse response) throws Exception {
    //    String originalTarget = request.getParameter("target");
    //
    //    String redirectUrl =
    //        Config.getProperty("escidoc.framework_access.login.url") + "/aa/logout" + "?target=" + URLEncoder.encode(originalTarget, "UTF-8");
    //
    //    return redirectUrl;

    return null;
  }
}
