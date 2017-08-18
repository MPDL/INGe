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

import de.mpg.mpdl.inge.pubman.web.sword.PubManDepositServlet;
import de.mpg.mpdl.inge.pubman.web.sword.PubManServiceDocumentServlet;
import de.mpg.mpdl.inge.pubman.web.util.filter.SessionTimeoutFilter;
import de.mpg.mpdl.inge.pubman.web.util.filter.SitemapFilter;
import de.mpg.mpdl.inge.pubman.web.util.listener.PubManSessionListener;
import de.mpg.mpdl.inge.pubman.web.util.servlet.GenreServlet;
import de.mpg.mpdl.inge.pubman.web.util.servlet.ImportSurveyerServlet;
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
    servletContext.setInitParameter(ContextLoader.LOCATOR_FACTORY_KEY_PARAM, "ear.context");


    // SessionListener
    servletContext.addListener(PubManSessionListener.class);


    // Sitemap
    FilterRegistration.Dynamic sitemapFilter =
        servletContext.addFilter("Sitemap Filter", SitemapFilter.class);
    sitemapFilter.addMappingForServletNames(null, false, "Faces Servlet");


    // JSF
    servletContext.addListener(ConfigureListener.class);
    servletContext.setInitParameter("javax.faces.PROJECT_STAGE", "Production");
    servletContext.setInitParameter("javax.faces.DEFAULT_SUFFIX", ".jsp");
    servletContext.setInitParameter("javax.faces.FACELETS_VIEW_MAPPINGS", "*.jsp");
    servletContext.setInitParameter("javax.faces.STATE_SAVING_METHOD", "client");
    servletContext.setInitParameter("javax.faces.CONFIG_FILES",
        "/WEB-INF/navigation.xml,/WEB-INF/managed-beans.xml");
    FilterRegistration.Dynamic sessionTimeoutFilter =
        servletContext.addFilter("SessionTimeoutFilter", SessionTimeoutFilter.class);
    sessionTimeoutFilter.addMappingForServletNames(null, false, "Faces Servlet");

    ServletRegistration.Dynamic facesServlet =
        servletContext.addServlet("Faces Servlet", new FacesServlet());
    facesServlet.addMapping("/faces/*");
    facesServlet.setLoadOnStartup(2);


    // Statistic Servlet
    ServletRegistration statisticChartServlet =
        servletContext.addServlet("StatisticChartServlet", StatisticChartServlet.class);
    statisticChartServlet.addMapping("/statisticchart/*");


    // Genre Servlet
    ServletRegistration.Dynamic genreServlet =
        servletContext.addServlet("Genre Servlet", GenreServlet.class);
    genreServlet.setLoadOnStartup(1);


    // Import Surveyor
    ServletRegistration.Dynamic importSurveyor =
        servletContext.addServlet("Import Surveyer Servlet", ImportSurveyerServlet.class);
    importSurveyor.setLoadOnStartup(3);


    // Redirect Servlet
    ServletRegistration.Dynamic redirectServlet =
        servletContext.addServlet("Redirect Servlet", RedirectServlet.class);
    redirectServlet.addMapping("/item/*");


    // SWORD
    ServletRegistration.Dynamic swordServiceDocumentServlet =
        servletContext.addServlet("servicedocument", PubManServiceDocumentServlet.class);
    swordServiceDocumentServlet.addMapping("/sword-app/servicedocument");

    ServletRegistration.Dynamic swordDepositServlet =
        servletContext.addServlet("servicedocument", PubManDepositServlet.class);
    swordServiceDocumentServlet.addMapping("/sword-app/deposit");

    servletContext.setInitParameter("server-class",
        "de.mpg.mpdl.inge.pubman.web.sword.PubManSwordServer");
    servletContext.setInitParameter("authentication-method", "Basic");


  }
}
