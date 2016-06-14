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

import de.mpg.escidoc.services.util.PropertyReader;

/**
 * TODO Description.
 * 
 * @author frank (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class Login {
  protected String userHandle;

  protected String getHandle(String uid, String pwd) throws MalformedURLException,
      AuthenticationException, TransportException, ServiceException, URISyntaxException {
    String handle = null;
    String base = PropertyReader.getFrameworkUrl();
    URL login = new URL(base);
    Authentication auth = new Authentication(login, uid, pwd);
    handle = auth.getHandle();
    return handle;
  }

  protected String login2target(String handle, String target) throws ServiceException,
      URISyntaxException {
    String response = null;
    String base = PropertyReader.getFrameworkUrl();

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
   * Logs in the system administrator and returns the corresponding user handle.
   * 
   * @return A handle for the logged in user.
   * @throws Exception
   */
  public String loginSysAdmin() throws Exception {
    return getHandle("roland", PropertyReader.getProperty("framework.admin.password"));
  }


  public String getUserHandle() {
    return userHandle;
  }

  public void setUserHandle(String userHandle) {
    this.userHandle = userHandle;
  }
}
