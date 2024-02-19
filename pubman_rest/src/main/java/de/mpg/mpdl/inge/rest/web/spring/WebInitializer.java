package de.mpg.mpdl.inge.rest.web.spring;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import de.mpg.mpdl.inge.rest.spring.WebConfiguration;
import jakarta.servlet.Filter;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;


public class WebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer implements WebApplicationInitializer {

  private static final Logger logger = LogManager.getLogger(WebInitializer.class);

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
    WebApplicationContext context = getSpringDocContext();
    servletContext.addListener(new CustomContextLoaderListener(context));

    super.onStartup(servletContext);
  }

  private AnnotationConfigWebApplicationContext getSpringDocContext() {
    AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
    context.register(WebConfiguration.class);
    context.register(this.getClass(), org.springdoc.webmvc.ui.SwaggerConfig.class,
        org.springdoc.core.properties.SwaggerUiConfigProperties.class, org.springdoc.core.properties.SwaggerUiOAuthProperties.class,
        org.springdoc.webmvc.core.configuration.SpringDocWebMvcConfiguration.class,
        org.springdoc.webmvc.core.configuration.MultipleOpenApiSupportConfiguration.class,
        org.springdoc.core.configuration.SpringDocConfiguration.class, org.springdoc.core.properties.SpringDocConfigProperties.class,
        org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.class);

    return context;
  }



}
