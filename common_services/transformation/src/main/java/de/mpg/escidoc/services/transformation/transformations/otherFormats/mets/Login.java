package de.mpg.escidoc.services.transformation.transformations.otherFormats.mets;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;

import org.apache.axis.encoding.Base64;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.methods.PostMethod;

import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Login class for escidoc log ins.
 *
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class Login
{
    protected String userHandle;
    private static final int NUMBER_OF_URL_TOKENS = 2;

    /**
     * Login method for escidoc solutions.
     * @param userid
     * @param password
     * @return UserHandle
     * @throws HttpException
     * @throws IOException
     * @throws ServiceException
     * @throws URISyntaxException
     */
    protected String login(String userid, String password) throws HttpException, IOException, ServiceException,
            URISyntaxException
    {
        String frameworkUrl = ServiceLocator.getFrameworkUrl();
        StringTokenizer tokens = new StringTokenizer(frameworkUrl, "//");
        if (tokens.countTokens() != NUMBER_OF_URL_TOKENS)
        {
            throw new IOException("Url in the config file is in the wrong format, needs to be http://<host>:<port>");
        }
        tokens.nextToken();
        StringTokenizer hostPort = new StringTokenizer(tokens.nextToken(), ":");
        if (hostPort.countTokens() != NUMBER_OF_URL_TOKENS)
        {
            throw new IOException("Url in the config file is in the wrong format, needs to be http://<host>:<port>");
        }
        String host = hostPort.nextToken();
        int port = Integer.parseInt(hostPort.nextToken());
        HttpClient client = new HttpClient();
        client.getHostConfiguration().setHost(host, port, "http");
        client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        PostMethod login = new PostMethod(frameworkUrl + "/aa/j_spring_security_check");
        login.addParameter("j_username", userid);
        login.addParameter("j_password", password);
        client.executeMethod(login);
        // System.out.println("Login form post: " + login.getStatusLine().toString());
        login.releaseConnection();
        CookieSpec cookiespec = CookiePolicy.getDefaultSpec();
        Cookie[] logoncookies = cookiespec.match(host, port, "/", false, client.getState().getCookies());
        Cookie sessionCookie = logoncookies[0];
        PostMethod postMethod = new PostMethod("/aa/login");
        postMethod.addParameter("target", frameworkUrl);
        client.getState().addCookie(sessionCookie);
        client.executeMethod(postMethod);
        // System.out.println("Login second post: " + postMethod.getStatusLine().toString());
        if (HttpServletResponse.SC_SEE_OTHER != postMethod.getStatusCode())
        {
            throw new HttpException("Wrong status code: " + login.getStatusCode());
        }
        String userHandle = null;
        Header[] headers = postMethod.getResponseHeaders();
        for (int i = 0; i < headers.length; ++i)
        {
            if ("Location".equals(headers[i].getName()))
            {
                String location = headers[i].getValue();
                int index = location.indexOf('=');
                userHandle = new String(Base64.decode(location.substring(index + 1, location.length())));
            }
        }
        if (userHandle == null)
        {
            throw new ServiceException("User not logged in.");
        }
        return userHandle;
    }

    /**
     * Logs in the virr user and returns the corresponding user handle.
     * 
     * @return A handle for the logged in user.
     * @throws Exception
     */
    public String loginVirrUser() throws Exception
    {
        return login("virr_user", "PubManR2");
    }

    /**
     * Logs in the system administrator and returns the corresponding user handle.
     * @return A handle for the logged in user.
     * @throws Exception
     */
    public String loginSysAdmin() throws Exception
    {
        return login("roland", "beethoven");
    }
}
