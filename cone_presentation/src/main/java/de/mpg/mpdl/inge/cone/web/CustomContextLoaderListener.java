package de.mpg.mpdl.inge.cone.web;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogic;
import jakarta.servlet.ServletContext;

public class CustomContextLoaderListener extends ContextLoaderListener {

  public CustomContextLoaderListener(WebApplicationContext context) {
    super(context);
  }

  public CustomContextLoaderListener() {}

  @Override
  protected ApplicationContext loadParentContext(ServletContext servletContext) {
    return (ApplicationContext) AppConfigPubmanLogic.getRootContextBeanFactory().getBean("ear.context");
  }

}
