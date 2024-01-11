package de.mpg.mpdl.inge.cone.web;

import de.mpg.mpdl.inge.aa.Aa;
import de.mpg.mpdl.inge.aa.AuthenticationVO.Role;
import jakarta.servlet.http.HttpServletRequest;

public class Login {

  /**
   * Hide constructor of the static class.
   */
  private Login() {}

  public static boolean checkLogin(HttpServletRequest request, boolean strict) {

    Aa aa = null;
    try {
      aa = new Aa(request);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    if (aa != null && aa.getAuthenticationVO() != null) {
      request.getSession().setAttribute("logged_in", Boolean.TRUE);
    } else {
      return false;
    }

    if (!strict) {
      return true;
    }

    boolean showWarning = true;
    String roleConeOpenVocabularyId = "CONE_OPEN_VOCABULARY_EDITOR";
    String roleConeClosedVocabularyId = "CONE_CLOSED_VOCABULARY_EDITOR";

    for (Role role : aa.getAuthenticationVO().getRoles()) {
      if ("SYSADMIN".equals(role.getKey())) {
        request.getSession().setAttribute("logged_in", Boolean.TRUE);
        request.getSession().setAttribute("user", aa.getAuthenticationVO());
        request.getSession().setAttribute("edit_open_vocabulary", Boolean.TRUE);
        request.getSession().setAttribute("edit_closed_vocabulary", Boolean.TRUE);
        showWarning = false;
        break;
      }
      if (roleConeOpenVocabularyId != null && roleConeOpenVocabularyId.equals(role.getKey())) {
        request.getSession().setAttribute("user", aa.getAuthenticationVO());
        request.getSession().setAttribute("logged_in", Boolean.TRUE);
        request.getSession().setAttribute("edit_open_vocabulary", Boolean.TRUE);
        showWarning = false;
        continue;
      }
      if (roleConeClosedVocabularyId != null && roleConeClosedVocabularyId.equals(role.getKey())) {
        request.getSession().setAttribute("user", aa.getAuthenticationVO());
        request.getSession().setAttribute("logged_in", Boolean.TRUE);
        request.getSession().setAttribute("edit_closed_vocabulary", Boolean.TRUE);
        showWarning = false;
        continue;
      }
    }

    return showWarning;

  }

  public static boolean getLoggedIn(HttpServletRequest request) {
    if (request.getSession().getAttribute("logged_in") != null
        && ((Boolean) request.getSession().getAttribute("logged_in")).booleanValue()) {
      return true;
    } else {
      checkLogin(request, true);
      return (request.getSession().getAttribute("logged_in") != null
          && ((Boolean) request.getSession().getAttribute("logged_in")).booleanValue());
    }
  }
}
