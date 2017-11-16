package de.mpg.mpdl.inge.cone_cache;

import org.apache.log4j.Logger;

public class ConeCacheInitTask extends Thread {

  private static final Logger logger = Logger.getLogger(ConeCacheInitTask.class);

  @Override
  public void run() {
    logger.info("INIT: CONE-Cache refresh task starts...");

    try {
      ConeCache.refreshCache();
    } catch (Exception e) {
      logger.error("Error in CONE-Cache refresh task.", e);
    }

    logger.info("INIT: CONE-Cache refresh task finished.");
  }
}
