package de.mpg.mpdl.inge.rest.web.spring;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

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

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // TODO Auto-generated method stub

  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    if (httpServletRequest.getHeader(AUTHZ_HEADER) == null || httpServletRequest.getHeader(AUTHZ_HEADER).isEmpty()) {

      Cookie[] cookies = httpServletRequest.getCookies();

      logger.info("Cookies: " + cookies);
      if (cookies != null) {
        for (Cookie cookie : cookies) {
          if (COOKIE_NAME.equals(cookie.getName())) {
            HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(httpServletRequest);
            requestWrapper.addHeader(AUTHZ_HEADER, cookie.getValue());
            request = requestWrapper;


          }
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