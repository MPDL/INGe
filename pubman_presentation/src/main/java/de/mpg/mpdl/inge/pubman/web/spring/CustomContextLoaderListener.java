package de.mpg.mpdl.inge.pubman.web.spring;

import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogic;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.servlet.ServletContext;

public class CustomContextLoaderListener extends ContextLoaderListener {


  public CustomContextLoaderListener(WebApplicationContext context) {
    super(context);
  }

  public CustomContextLoaderListener() {}

  @Override
  protected ApplicationContext loadParentContext(ServletContext servletContext) {

    //WebApplicationContext servletContext =  WebApplicationContextUtils.getWebApplicationContext(servletContext);


    /*
    Resource res = new ClassPathResource("beanRefContext.xml");
    BeanFactory factory = new XmlBeanFactory(res);
    ApplicationContext parentContext = (ApplicationContext) factory.getBean("ear.context");
    */


    ApplicationContext parentContext = (ApplicationContext) AppConfigPubmanLogic.PUBMAN_LOGIC_BEAN_FACTORY.getBean("ear.context");

    /*
    AnnotationConfigWebApplicationContext parentContext = new AnnotationConfigWebApplicationContext();
    parentContext.register(AppConfigPubmanLogic.class);
    parentContext.setServletContext(servletContext);
    
     */

    //ApplicationContext parentContext = (ApplicationContext) ((WebApplicationContext) servletContext).getBean("context.ear");
    return parentContext;
  }

}
