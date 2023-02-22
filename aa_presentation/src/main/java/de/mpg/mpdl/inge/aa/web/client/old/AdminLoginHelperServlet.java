package de.mpg.mpdl.inge.aa.web.client.old;

import javax.servlet.http.HttpServlet;

@SuppressWarnings("serial")
public class AdminLoginHelperServlet extends HttpServlet {

  //  private static final Logger logger = Logger.getLogger(AdminLoginHelperServlet.class);
  //
  //  @Override
  //  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
  //    doPost(request, response);
  //  }
  //
  //  @Override
  //  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
  //    String username = request.getParameter("username");
  //    String password = request.getParameter("password");
  //
  //    String token;
  //    try {
  //      token = IngeAaClientFinish.loginInInge(username, password);
  //      if (token != null) {
  //        String aaInstanceUrl = PropertyReader.getProperty(PropertyReader.INGE_AA_INSTANCE_URL);
  //        String tan = TanStore.getNewTan();
  //        response.sendRedirect(aaInstanceUrl + "clientReturn?target="
  //            + URLDecoder.decode(request.getParameter("target"), StandardCharsets.UTF_8.toString()) + "&token=" + token + "&tan=" + tan);
  //      } else {
  //        response.setStatus(HttpStatus.SC_UNAUTHORIZED);
  //      }
  //    } catch (Exception e) {
  //      logger.error("Error loggin in admin user.", e);
  //      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
  //    }
  //  }
  //  //
  //  //  private static String getTan() {
  //  //    String tan;
  //  //    do {
  //  //      tan = TanStore.createTan();
  //  //    } while (!TanStore.storeTan(tan));
  //  //    return tan;
  //  //  }
}
