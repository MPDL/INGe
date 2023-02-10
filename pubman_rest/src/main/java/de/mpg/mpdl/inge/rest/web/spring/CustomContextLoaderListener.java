package de.mpg.mpdl.inge.rest.web.spring;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogic;

public class CustomContextLoaderListener extends ContextLoaderListener {


  public CustomContextLoaderListener(WebApplicationContext context) {
    super(context);
  }

  public CustomContextLoaderListener() {}

  @Override
  protected ApplicationContext loadParentContext(ServletContext servletContext) {

    //WebApplicationContext servletContext =  WebApplicationContextUtils.getWebApplicationContext(servletContext);

    //AnnotationConfigWebApplicationContext parentContext = new AnnotationConfigWebApplicationContext();
    //parentContext.register(AppConfigPubmanLogic.class);

    /*
    Resource res = new ClassPathResource("beanRefContext.xml");
    BeanFactory factory = new XmlBeanFactory(res);
    ApplicationContext parentContext = (ApplicationContext) factory.getBean("ear.context");
    */
    ApplicationContext parentContext = (ApplicationContext) AppConfigPubmanLogic.getRootContextBeanFactory().getBean("ear.context");
    //ApplicationContext parentContext = (ApplicationContext) ((WebApplicationContext) servletContext).getBean("context.ear");
    return parentContext;
  }

}
