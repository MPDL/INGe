package de.mpg.mpdl.inge.pubman.web.spring;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.sun.faces.config.ConfigureListener;

import de.mpg.mpdl.inge.pubman.web.util.filter.SitemapFilter;
import de.mpg.mpdl.inge.pubman.web.util.servlet.GenreServlet;
import de.mpg.mpdl.inge.pubman.web.util.servlet.RedirectServlet;
import jakarta.faces.webapp.FacesServlet;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;

public class WebAppInitializer implements WebApplicationInitializer {
  public void onStartup(ServletContext servletContext) {
    // Create the 'root' Spring application context
    //AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
    //rootContext.register(AppConfigPubmanLogic.class);
    // Manage the lifecycle of the root application context
    //servletContext.addListener(new ContextLoaderListener(rootContext));


    AnnotationConfigWebApplicationContext myctx = new AnnotationConfigWebApplicationContext();
    myctx.register(AppConfigPubmanPresentation.class);
    myctx.setServletContext(servletContext);

    // Spring
    servletContext.addListener(new CustomContextLoaderListener(myctx));
    servletContext.addListener(new RequestContextListener());
    // Use ear.context (pubman_logic) as shared context between all webapps in the ear.
    // ear.context is defined in beanRefContext.xml in module pubman_logic
    //servletContext.setInitParameter(ContextLoader.LOCATOR_FACTORY_KEY_PARAM, "ear.context");


    // JSF
    servletContext.addListener(ConfigureListener.class);
    servletContext.setInitParameter("com.sun.faces.forceLoadConfiguration", "true");
    servletContext.setInitParameter("jakarta.faces.PROJECT_STAGE", "Production");
    servletContext.setInitParameter("jakarta.faces.DEFAULT_SUFFIX", ".jsp");
    servletContext.setInitParameter("jakarta.faces.FACELETS_VIEW_MAPPINGS", "*.jsp");
    servletContext.setInitParameter("jakarta.faces.STATE_SAVING_METHOD", "client");
    servletContext.setInitParameter("jakarta.faces.CONFIG_FILES", "/WEB-INF/navigation.xml,/WEB-INF/managed-beans.xml");

    ServletRegistration.Dynamic facesServlet = servletContext.addServlet("Faces Servlet", new FacesServlet());
    facesServlet.addMapping("/faces/*");
    facesServlet.setLoadOnStartup(2);

    // Sitemap
    FilterRegistration.Dynamic sitemapFilter = servletContext.addFilter("Sitemap Filter", SitemapFilter.class);
    sitemapFilter.addMappingForUrlPatterns(null, false, "/*");

    // Genre Servlet
    ServletRegistration.Dynamic genreServlet = servletContext.addServlet("Genre Servlet", GenreServlet.class);
    genreServlet.setLoadOnStartup(1);

    // Redirect Servlet
    ServletRegistration.Dynamic redirectServlet = servletContext.addServlet("Redirect Servlet", RedirectServlet.class);
    redirectServlet.addMapping("/item/*");
  }

}
