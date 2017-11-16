package de.mpg.mpdl.inge.pubman.web.util.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.service.pubman.impl.SimpleStatisticsService;

@SuppressWarnings("serial")
public class InitializerServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(InitializerServlet.class);

  // private SiteMapTask siteMapTask;

  /**
   * {@inheritDoc}
   */
  @Override
  public void init() throws ServletException {
    // initialize report definitions for statistics
    try {
      // call method as thread. If coreservice and PubMan are deployed ion the same jboss, this
      // method is blocked until both applications are completely deployed
      new Thread() {
        @Override
        public void run() {
          SimpleStatisticsService.initReportDefinitionsInFramework();
        }
      }.start();
    } catch (final Exception e) {
      InitializerServlet.logger.error("Problem with initializing statistics system", e);
    }

    // initialize google sitemap creation
    /*
     * try { this.siteMapTask = new SiteMapTask(); this.siteMapTask.start(); } catch (final
     * Exception e) { InitializerServlet.logger.error("Problem with google sitemap creation", e); }
     */
  }

  @Override
  public void destroy() {
    super.destroy();
    // InitializerServlet.logger.info("Signalled to terminate Sitemap creation task.");
    // this.siteMapTask.terminate();
  }
}
