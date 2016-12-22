package de.mpg.mpdl.inge.inge_validation.web;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.inge_validation.ItemValidating;

public class RefreshTask extends Thread {

  private static final Logger logger = Logger.getLogger(RefreshTask.class);

  private boolean signal = false;

  @Override
  public void run() {
    this.setName("Validation Refresh Task");
    try {
      int timeout =
          Integer.parseInt(PropertyReader
              .getProperty(Properties.ESCIDOC_VALIDATION_REFRESH_INTERVAL));
      while (!signal) {
        Thread.sleep(timeout * 60 * 1000);
        logger.info("Starting refresh of validation database.");
        Context ctx = new InitialContext();
        ItemValidating itemValidating =
            (ItemValidating) ctx.lookup(Properties.JNDI_ITEM_VALIDATING_BEAN);
        itemValidating.refreshValidationSchemaCache();
        logger.info("Finished refresh of validation database.");

      }
      logger.info("Refresh task terminated.");
    } catch (Exception e) {
      logger.error("Error initializing refresh task", e);
    }
  }

  public void terminate() {
    logger.info("Refresh task signalled to terminate.");
    signal = true;
  }

}
