package de.mpg.mpdl.inge.inge_validation.web;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.inge_validation.ItemValidating;

public class RefreshTask extends Thread {

  private static final Logger LOG = Logger.getLogger(RefreshTask.class);

  private boolean terminate = false;

  @Override
  public void run() {
    try {
      this.setName("Validation Refresh Task");

      final int timeout = Integer
          .parseInt(PropertyReader.getProperty(Properties.ESCIDOC_VALIDATION_REFRESH_INTERVAL));

      LOG.info("Starting RefreshTask");
      
      while (!this.terminate) {
        LOG.info("Starting refresh of validation database.");
        Context ctx = new InitialContext();
        ItemValidating itemValidating =
            (ItemValidating) ctx.lookup(Properties.JNDI_ITEM_VALIDATING_BEAN);
        itemValidating.refreshValidationSchemaCache();
        LOG.info("Finished refresh of validation database.");
        Thread.sleep(timeout * 60 * 1000);
      }
    } catch (InterruptedException e) {
      LOG.warn("RefreshTask InterruptedException angefordert.");
      this.terminate = true;
    } catch (Exception e) {
      LOG.error("REFRESH_TASK:\n{}", e);
    }

    LOG.info("RefreshTask terminated.");
  }
}
