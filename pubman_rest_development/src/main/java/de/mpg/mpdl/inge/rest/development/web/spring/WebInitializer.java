package de.mpg.mpdl.inge.rest.development.web.spring;

import jakarta.servlet.Filter;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import de.mpg.mpdl.inge.rest.development.spring.PubmanRestConfiguration;

public class WebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer implements WebApplicationInitializer {

  @Override
  protected Class<?>[] getRootConfigClasses() {
    return new Class[] {PubmanRestConfiguration.class};
  }

  @Override
  protected Class<?>[] getServletConfigClasses() {
    return null;
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
    return new Filter[] {encodingFilter};
  }

  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
    super.onStartup(servletContext);
  }

}
