package de.mpg.mpdl.inge.pubman.web.spring;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogic;

public class WebAppInitializer implements WebApplicationInitializer {
  public void onStartup(ServletContext servletContext) throws ServletException {


    /*
     * AnnotationConfigWebApplicationContext rootCtx = new AnnotationConfigWebApplicationContext();
     * rootCtx.register(AppConfigPubmanLogic.class); rootCtx.setBeanName("parent.context");
     * servletContext.addListener(new ContextLoaderListener(rootCtx));
     */

    AnnotationConfigWebApplicationContext myctx = new AnnotationConfigWebApplicationContext();
    myctx.register(AppConfigPubmanPresentation.class);
    // myctx.setParent(rootCtx);

    myctx.setServletContext(servletContext);
    servletContext.addListener(new ContextLoaderListener(myctx));

    servletContext.addListener(new RequestContextListener());

    // Use ear.context (pubman_logic) as shared context between all webapps in the ear.
    // ear.context is defined in beanRefContext.xml in module pubman_logic
    servletContext.setInitParameter(ContextLoader.LOCATOR_FACTORY_KEY_PARAM, "ear.context");
    /*
     * Dynamic dynamic = servletContext.addServlet("dispatcher", new DispatcherServlet(ctx));
     * dynamic.addMapping("/"); dynamic.setLoadOnStartup(1);
     */
  }
}
