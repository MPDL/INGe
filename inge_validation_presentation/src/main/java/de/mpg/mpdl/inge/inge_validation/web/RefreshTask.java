package de.mpg.mpdl.inge.inge_validation.web;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.inge_validation.ItemValidatingService;
import de.mpg.mpdl.inge.util.PropertyReader;

public class RefreshTask extends Thread {

  private static final Logger LOG = Logger.getLogger(RefreshTask.class);

  private boolean terminate = false;

  @Override
  public void run() {
    try {
      this.setName("Validation Refresh Task");

      final int timeout =
          Integer.parseInt(PropertyReader
              .getProperty(Properties.ESCIDOC_VALIDATION_REFRESH_INTERVAL));

      RefreshTask.LOG.info("Starting RefreshTask");

      while (!this.terminate) {
        RefreshTask.LOG.info("Starting refresh of validation database.");
        ItemValidatingService.refreshValidationSchemaCache();
        RefreshTask.LOG.info("Finished refresh of validation database.");
        Thread.sleep(timeout * 60 * 1000);
      }
    } catch (final InterruptedException e) {
      RefreshTask.LOG.warn("RefreshTask InterruptedException angefordert.");
      this.terminate = true;
    } catch (final Exception e) {
      RefreshTask.LOG.error("REFRESH_TASK:\n{}", e);
    }

    RefreshTask.LOG.info("RefreshTask terminated.");
  }
}
