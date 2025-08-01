package de.mpg.mpdl.inge.rest.web.spring;

import java.io.IOException;
import java.util.*;

import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.rest.web.exceptions.PubmanRestExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
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

  private static final Logger logger = LogManager.getLogger(AuthCookieToHeaderFilter.class);

  private UserAccountService userAccountService;

  @Override
  public void init(FilterConfig filterConfig) {
    this.userAccountService =
        WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext()).getBean(UserAccountService.class);

  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
    if (null == httpServletRequest.getHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER)
        || httpServletRequest.getHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER).isEmpty()) {

      Cookie[] cookies = httpServletRequest.getCookies();

      boolean userCookieSet = false;
      if (null != cookies) {
        for (Cookie cookie : cookies) {
          if (COOKIE_NAME.equals(cookie.getName())) {

            String token = cookie.getValue();

            try {
              //Add token from cookie as authorization header, only if it's valid and not an anonymous cookie (means claim 'id' is set)
              DecodedJWT jwtToken = this.userAccountService.verifyToken(token);
              if (!jwtToken.getClaim("id").isNull()) {
                logger.debug("Found valid token in cookie \"" + COOKIE_NAME + "\", copying it to Authorization header");
                HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(httpServletRequest);
                requestWrapper.addHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER, token);
                request = requestWrapper;
                userCookieSet = true;
              }
            } catch (AuthenticationException e) {
              logger.debug("Found token in cookie \"" + COOKIE_NAME + "\", but it did not verify. Trying to cancel cookie", e);
              cookie.setValue(null);
              cookie.setMaxAge(0);
              cookie.setPath("/");
              httpServletResponse.addCookie(cookie);
              setInvalidTokenResponse(httpServletResponse, e);
              return;
            }



          }
        }
      }

      //If no unanonymous cookie is found, add anonymous ip-based token as authorization header
      if (!userCookieSet) {
        try {
          logger.debug("Found no valid user cookie \"" + COOKIE_NAME + "\", trying to login as ip-based user");
          //Create an anonymous token containing the ip adress
          Principal principal = this.userAccountService.login(httpServletRequest, (HttpServletResponse) response);
          if (null != principal) {
            HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(httpServletRequest);
            requestWrapper.addHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER, principal.getJwToken());
            request = requestWrapper;
          }

        } catch (Exception e) {
          logger.error("Error logging in anonymous users during rest request", e);
        }
      }

    }
    //Authorization header is set, check if token is valid
    else {
      String token = httpServletRequest.getHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER);
      try {
        DecodedJWT jwtToken = this.userAccountService.verifyToken(token);
      } catch (Exception e) {
        logger.debug("Token found in Authorization Header, but it did not verify.", e);
        setInvalidTokenResponse(httpServletResponse, e);
        return;
      }
    }

    chain.doFilter(request, response);


  }

  private void setInvalidTokenResponse(HttpServletResponse httpServletResponse, Exception e) throws IOException {
    httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    Map<String, Object> jsonException = new LinkedHashMap<>();
    jsonException.put("tokenInvalid", true);
    PubmanRestExceptionHandler.buildExceptionMessage(e, jsonException, HttpStatus.UNAUTHORIZED);
    httpServletResponse.setContentType("application/json");
    httpServletResponse.setCharacterEncoding("UTF-8");
    httpServletResponse.getWriter().write(MapperFactory.getObjectMapper().writeValueAsString(jsonException));
  }

  @Override
  public void destroy() {}



  // https://stackoverflow.com/questions/2811769/adding-an-http-header-to-the-request-in-a-servlet-filter
  // http://sandeepmore.com/blog/2010/06/12/modifying-http-headers-using-java/
  // http://bijubnair.blogspot.de/2008/12/adding-header-information-to-existing.html
  /**
   * allow adding additional header entries to a request
   *
   * @author wf
   *
   */
  public static class HeaderMapRequestWrapper extends HttpServletRequestWrapper {
    /**
     * construct a wrapper for this request
     *
     * @param request
     */
    public HeaderMapRequestWrapper(HttpServletRequest request) {
      super(request);
    }

    private final Map<String, String> headerMap = new HashMap<>();

    /**
     * add a header with given name and value
     *
     * @param name
     * @param value
     */
    public void addHeader(String name, String value) {
      this.headerMap.put(name, value);
    }

    @Override
    public String getHeader(String name) {
      String headerValue = super.getHeader(name);
      if (this.headerMap.containsKey(name)) {
        headerValue = this.headerMap.get(name);
      }
      return headerValue;
    }

    /**
     * get the Header names
     */
    @Override
    public Enumeration<String> getHeaderNames() {
      List<String> names = Collections.list(super.getHeaderNames());
      names.addAll(this.headerMap.keySet());
      return Collections.enumeration(names);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
      List<String> values = Collections.list(super.getHeaders(name));
      if (this.headerMap.containsKey(name)) {
        values.add(this.headerMap.get(name));
      }
      return Collections.enumeration(values);
    }

  }


}
