package de.mpg.mpdl.inge.rest.web.spring;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.auth0.jwt.interfaces.DecodedJWT;

import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Reads cookie with authorization key and adds it as authorization header
 * 
 * @author haarlae1
 * 
 */
public class AuthCookieToHeaderFilter implements Filter {


  public static final String COOKIE_NAME = "inge_auth_token";
  public static final String AUTHZ_HEADER = "Authorization";

  private static final Logger logger = Logger.getLogger(AuthCookieToHeaderFilter.class);

  private UserAccountService userAccountService;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    this.userAccountService =
        WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext()).getBean(UserAccountService.class);

  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
    if (httpServletRequest.getHeader(AUTHZ_HEADER) == null || httpServletRequest.getHeader(AUTHZ_HEADER).isEmpty()) {

      Cookie[] cookies = httpServletRequest.getCookies();

      boolean userCookieSet = false;
      if (cookies != null) {
        for (Cookie cookie : cookies) {
          if (COOKIE_NAME.equals(cookie.getName())) {

            String token = cookie.getValue();

            try {
              //Add token from cookie as authorization header, only if it's valid and not an anonymous cookie (means claim 'id' is set)
              DecodedJWT jwtToken = userAccountService.verifyToken(token);
              if (!jwtToken.getClaim("id").isNull()) {
                logger.debug("Found valid token in cookie \"" + COOKIE_NAME + "\", copying it to Authorization header");
                HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(httpServletRequest);
                requestWrapper.addHeader(AUTHZ_HEADER, token);
                request = requestWrapper;
                userCookieSet = true;
              }
            } catch (AuthenticationException e) {
              logger.debug("Found token in cookie \"" + COOKIE_NAME + "\", but it did not verify. Trying to cancel cookie", e);
              cookie.setValue(null);
              cookie.setMaxAge(0);
              cookie.setPath("/");
              httpServletResponse.addCookie(cookie);
            }



          }
        }
      }

      //If no unanonymous cookie is found, add anonymous ip-based token as authorization header
      if (!userCookieSet) {
        try {
          logger.debug("Found no valid user cookie \"" + COOKIE_NAME + "\", trying to login as ip-based user");
          Principal principal = userAccountService.login(httpServletRequest, (HttpServletResponse) response);
          if (principal != null) {
            HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(httpServletRequest);
            requestWrapper.addHeader(AUTHZ_HEADER, principal.getJwToken());
            request = requestWrapper;
          }

        } catch (Exception e) {
          logger.error("Error logging in anonymous users during rest request", e);
        }
      }

    }


    chain.doFilter(request, response);


  }

  @Override
  public void destroy() {
    // TODO Auto-generated method stub

  }



  // https://stackoverflow.com/questions/2811769/adding-an-http-header-to-the-request-in-a-servlet-filter
  // http://sandeepmore.com/blog/2010/06/12/modifying-http-headers-using-java/
  // http://bijubnair.blogspot.de/2008/12/adding-header-information-to-existing.html
  /**
   * allow adding additional header entries to a request
   * 
   * @author wf
   * 
   */
  public class HeaderMapRequestWrapper extends HttpServletRequestWrapper {
    /**
     * construct a wrapper for this request
     * 
     * @param request
     */
    public HeaderMapRequestWrapper(HttpServletRequest request) {
      super(request);
    }

    private Map<String, String> headerMap = new HashMap<String, String>();

    /**
     * add a header with given name and value
     * 
     * @param name
     * @param value
     */
    public void addHeader(String name, String value) {
      headerMap.put(name, value);
    }

    @Override
    public String getHeader(String name) {
      String headerValue = super.getHeader(name);
      if (headerMap.containsKey(name)) {
        headerValue = headerMap.get(name);
      }
      return headerValue;
    }

    /**
     * get the Header names
     */
    @Override
    public Enumeration<String> getHeaderNames() {
      List<String> names = Collections.list(super.getHeaderNames());
      for (String name : headerMap.keySet()) {
        names.add(name);
      }
      return Collections.enumeration(names);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
      List<String> values = Collections.list(super.getHeaders(name));
      if (headerMap.containsKey(name)) {
        values.add(headerMap.get(name));
      }
      return Collections.enumeration(values);
    }

  }


}
