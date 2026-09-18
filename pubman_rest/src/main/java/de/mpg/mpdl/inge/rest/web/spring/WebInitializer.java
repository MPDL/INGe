package de.mpg.mpdl.inge.rest.web.spring;

import de.mpg.mpdl.inge.rest.spring.PubmanRestConfiguration;
import de.mpg.mpdl.inge.rest.spring.WebConfiguration;
import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogic;
import jakarta.servlet.Filter;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

  private static final Logger logger = LogManager.getLogger(WebInitializer.class);

  @Override
  protected Class<?>[] getRootConfigClasses() {
    // Root context: Core services, DB/JPA, ActiveMQ, ES
    return new Class[] {AppConfigPubmanLogic.class, PubmanRestConfiguration.class};
  }

  @Override
  protected Class<?>[] getServletConfigClasses() {
    // Web context: Controllers, MVC config, Swagger/SpringDoc
    return new Class<?>[] {WebConfiguration.class};
  }

  @Override
  protected String[] getServletMappings() {
    return new String[] {"/"};
  }

  @Override
  protected Filter[] getServletFilters() {
    CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
    encodingFilter.setEncoding("UTF-8");
    encodingFilter.setForceEncoding(true);

    return new Filter[] {encodingFilter, new AuthCookieToHeaderFilter()};
  }
}
