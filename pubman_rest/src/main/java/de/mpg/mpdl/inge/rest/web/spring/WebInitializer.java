package de.mpg.mpdl.inge.rest.web.spring;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import de.mpg.mpdl.inge.rest.spring.WebConfiguration;


public class WebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer implements WebApplicationInitializer {

  @Override

  protected Class<?>[] getRootConfigClasses() {
    return new Class[] {};// {PubmanRestConfiguration.class};
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
    return new Filter[] {encodingFilter, new AuthCookieToHeaderFilter()};
  }

  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
    // Use ear.context (pubman_logic) as shared context between all webapps in the ear.
    // ear.context is defined in beanRefContext.xml in module pubman_logic
    //servletContext.setInitParameter(ContextLoader.LOCATOR_FACTORY_KEY_PARAM, "ear.context");

    // Create the 'root' Spring application context
    AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
    rootContext.register(WebConfiguration.class);
    // Manage the lifecycle of the root application context
    servletContext.addListener(new CustomContextLoaderListener(rootContext));



    super.onStartup(servletContext);
  }


}
