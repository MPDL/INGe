package de.mpg.mpdl.inge.pubman.web.spring;

import javax.faces.webapp.FacesServlet;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.sun.faces.config.ConfigureListener;

import de.mpg.mpdl.inge.pubman.web.util.filter.SitemapFilter;
import de.mpg.mpdl.inge.pubman.web.util.servlet.GenreServlet;
import de.mpg.mpdl.inge.pubman.web.util.servlet.RedirectServlet;
import de.mpg.mpdl.inge.pubman.web.util.servlet.StatisticChartServlet;

public class WebAppInitializer implements WebApplicationInitializer {
  public void onStartup(ServletContext servletContext) throws ServletException {
    AnnotationConfigWebApplicationContext myctx = new AnnotationConfigWebApplicationContext();
    myctx.register(AppConfigPubmanPresentation.class);
    myctx.setServletContext(servletContext);

    // Spring
    servletContext.addListener(new ContextLoaderListener(myctx));
    servletContext.addListener(new RequestContextListener());
    // Use ear.context (pubman_logic) as shared context between all webapps in the ear.
    // ear.context is defined in beanRefContext.xml in module pubman_logic
    servletContext.setInitParameter(ContextLoader.LOCATOR_FACTORY_KEY_PARAM, "ear.context");

    // JSF
    servletContext.addListener(ConfigureListener.class);
    servletContext.setInitParameter("com.sun.faces.forceLoadConfiguration", "true");
    servletContext.setInitParameter("javax.faces.PROJECT_STAGE", "Production");
    servletContext.setInitParameter("javax.faces.DEFAULT_SUFFIX", ".jsp");
    servletContext.setInitParameter("javax.faces.FACELETS_VIEW_MAPPINGS", "*.jsp");
    servletContext.setInitParameter("javax.faces.STATE_SAVING_METHOD", "client");
    servletContext.setInitParameter("javax.faces.CONFIG_FILES", "/WEB-INF/navigation.xml,/WEB-INF/managed-beans.xml");

    ServletRegistration.Dynamic facesServlet = servletContext.addServlet("Faces Servlet", new FacesServlet());
    facesServlet.addMapping("/faces/*");
    facesServlet.setLoadOnStartup(2);

    // Sitemap
    FilterRegistration.Dynamic sitemapFilter = servletContext.addFilter("Sitemap Filter", SitemapFilter.class);
    sitemapFilter.addMappingForUrlPatterns(null, false, "/*");

    // Statistic Servlet
    ServletRegistration statisticChartServlet = servletContext.addServlet("Statistic Chart Servlet", StatisticChartServlet.class);
    statisticChartServlet.addMapping("/statisticchart/*");

    // Genre Servlet
    ServletRegistration.Dynamic genreServlet = servletContext.addServlet("Genre Servlet", GenreServlet.class);
    genreServlet.setLoadOnStartup(1);

    // Redirect Servlet
    ServletRegistration.Dynamic redirectServlet = servletContext.addServlet("Redirect Servlet", RedirectServlet.class);
    redirectServlet.addMapping("/item/*");

    //    // SWORD SP 20.12.2018 -> Not used
    //    ServletRegistration.Dynamic swordServiceDocumentServlet =
    //        servletContext.addServlet("Sword Service Document Servlet", PubManServiceDocumentServlet.class);
    //    swordServiceDocumentServlet.addMapping("/sword-app/servicedocument");
    //
    //    ServletRegistration.Dynamic swordDepositServlet = servletContext.addServlet("Sword Deposit Servlet", PubManDepositServlet.class);
    //    swordDepositServlet.addMapping("/sword-app/deposit");
    //
    //    servletContext.setInitParameter("server-class", "de.mpg.mpdl.inge.pubman.web.sword.PubManSwordServer");
    //    servletContext.setInitParameter("authentication-method", "Basic");
  }

}
