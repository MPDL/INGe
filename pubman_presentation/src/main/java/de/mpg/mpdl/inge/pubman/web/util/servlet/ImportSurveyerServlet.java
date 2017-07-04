package de.mpg.mpdl.inge.pubman.web.util.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.pubman.web.util.threads.ImportSurveyor;

@SuppressWarnings("serial")
public class ImportSurveyerServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(ImportSurveyerServlet.class);

  private ImportSurveyor importSurveyor;

  /**
   * {@inheritDoc}
   */
  @Override
  public void init() throws ServletException {
    // surveyes import database
    try {
      this.importSurveyor = new ImportSurveyor();
      ImportSurveyerServlet.logger.info("Start ImportSurveyer task...");
      this.importSurveyor.start();
    } catch (final Exception e) {
      ImportSurveyerServlet.logger.error("Problem with ImportSurveyer task", e);
    }
  }

  @Override
  public void destroy() {
    super.destroy();
    ImportSurveyerServlet.logger.info("Signalled to terminate ImportSurveyer task.");
    this.importSurveyor.terminate();
  }
}
