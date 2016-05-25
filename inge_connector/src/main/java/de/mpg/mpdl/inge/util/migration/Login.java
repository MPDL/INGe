package de.mpg.mpdl.inge.util.migration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.stream.Collectors;

import javax.xml.rpc.ServiceException;

import org.apache.axis.encoding.Base64;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.methods.PostMethod;

import de.escidoc.core.client.Authentication;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.client.exceptions.application.security.AuthenticationException;

/**
 * TODO Description.
 * 
 * @author frank (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class Login {
  protected String userHandle;
  private static final int NUMBER_OF_URL_TOKENS = 2;

  protected String getHandle(String uid, String pwd) throws MalformedURLException,
      AuthenticationException, TransportException {
    String handle = null;
    String base = "https://coreservice.mpdl.mpg.de";
    URL login = new URL(base);
    Authentication auth = new Authentication(login, uid, pwd);
    handle = auth.getHandle();
    return handle;
  }

  protected String login2target(String handle, String target) {
    String response = null;
    String base = "https://coreservice.mpdl.mpg.de";

    Cookie handleCookie = new Cookie(base, "escidocCookie", handle);
    HttpURLConnection conn = null;
    BufferedReader reader = null;
    try {
      conn = (HttpURLConnection) new URL(base + target).openConnection();
      conn.setRequestProperty("Cookie", handleCookie.toExternalForm());
      reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      response = reader.lines().collect(Collectors.joining("\n"));
      return response;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
        }
      }
      if (conn != null) {
        conn.disconnect();
      }
    }
    return null;
  }

  /**
   * Logs in the given user with the given password.
   * 
   * @param userid The id of the user to log in.
   * @param password The password of the user to log in.
   * @param target
   * @return The handle for the logged in user.
   * @throws HttpException
   * @throws IOException
   * @throws ServiceException
   * @throws URISyntaxException
   */
  protected String login(String userid, String password, String target) throws HttpException,
      IOException, ServiceException, URISyntaxException {
    String frameworkUrl = "https://coreservice.mpdl.mpg.de";

    HttpClient client = new HttpClient();
    client.getHostConfiguration().setHost(frameworkUrl);
    client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
    PostMethod login = new PostMethod(frameworkUrl + "/aa/j_spring_security_check");
    login.addParameter("j_username", userid);
    login.addParameter("j_password", password);
    client.executeMethod(login);
    // System.out.println("Login form post: " +
    // login.getStatusLine().toString());
    login.releaseConnection();
    CookieSpec cookiespec = CookiePolicy.getDefaultSpec();
    Cookie[] logoncookies =
        cookiespec.match("coreservice.mpdl.mpg.de", 0, "/", false, client.getState().getCookies());
    // System.out.println("Logon cookies:");
    Cookie sessionCookie = logoncookies[0];
    /*
     * if (logoncookies.length == 0) { System.out.println("None"); } else { for (int i = 0; i <
     * logoncookies.length; i++) { System.out.println("- " + logoncookies[i].toString()); } }
     */
    PostMethod postMethod = new PostMethod(frameworkUrl + "/aa/login");
    postMethod.addParameter("target", frameworkUrl + target);
    client.getState().addCookie(sessionCookie);
    client.executeMethod(postMethod);

    CookieSpec spec = CookiePolicy.getDefaultSpec();
    Cookie[] postcookies =
        spec.match("coreservice.mpdl.mpg.de", 0, "/", false, client.getState().getCookies());
    // System.out.println("Logon cookies:");
    Cookie anyCookie = postcookies[0];
    /*
     * if (postcookies.length == 0) { System.out.println("None"); } else { for (int i = 0; i <
     * postcookies.length; i++) { System.out.println("- " + postcookies[i].toString()); } }
     */
    // System.out.println("Login second post: " +
    // postMethod.getStatusLine().toString());

    userHandle = null;
    String response = null;
    Header[] headers = postMethod.getResponseHeaders();
    for (int i = 0; i < headers.length; ++i) {
      if ("Location".equals(headers[i].getName())) {
        String location = headers[i].getValue();
        int index = location.indexOf('=');
        userHandle = new String(Base64.decode(location.substring(index + 1, location.length())));
        Cookie handleCookie = new Cookie(frameworkUrl, "escidocCookie", userHandle);
        // System.out.println("And this is my Keks: " +
        // handleCookie.toExternalForm());
        HttpURLConnection conn = (HttpURLConnection) new URL(location).openConnection();
        HttpURLConnection.setFollowRedirects(true);

        conn.setRequestProperty("Cookie", handleCookie.toExternalForm());
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        response = reader.lines().collect(Collectors.joining("\n"));

      }
    }
    if (userHandle == null) {
      throw new ServiceException("User not logged in.");
    }
    return response;
  }

  /**
   * Logs in the system administrator and returns the corresponding user handle.
   * 
   * @return A handle for the logged in user.
   * @throws Exception
   */
  public String loginSysAdmin() throws Exception {
    return getHandle("roland", "***REMOVED***");
  }


  public String getUserHandle() {
    return userHandle;
  }

  public void setUserHandle(String userHandle) {
    this.userHandle = userHandle;
  }
}
