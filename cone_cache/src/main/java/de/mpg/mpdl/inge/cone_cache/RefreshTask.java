package de.mpg.mpdl.inge.cone_cache;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.util.PropertyReader;

public class RefreshTask extends Thread {

  private static final Logger logger = Logger.getLogger(RefreshTask.class);

  private boolean terminate = false;

  @Override
  public void run() {
    try {
      this.setName("Validation Refresh Task");

      final int timeout =
          Integer.parseInt(PropertyReader
              .getProperty(Properties.ESCIDOC_VALIDATION_REFRESH_INTERVAL));

      logger.info("Starting RefreshTask");

      while (!this.terminate) {
        logger.info("Starting refresh of validation database <- Refresh Task.");
        ConeCache.getInstance().refreshCache();
        logger.info("Finished refresh of validation database <- Refresh Task.");
        Thread.sleep(timeout * 60 * 1000);
      }
    } catch (final InterruptedException e) {
      logger.warn("RefreshTask InterruptedException angefordert.");
      this.terminate = true;
    } catch (final Exception e) {
      logger.error("REFRESH_TASK:\n{}", e);
    }

    logger.info("RefreshTask terminated.");
  }
}
