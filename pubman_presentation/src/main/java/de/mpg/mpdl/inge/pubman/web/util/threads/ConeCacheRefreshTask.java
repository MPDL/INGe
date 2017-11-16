package de.mpg.mpdl.inge.pubman.web.util.threads;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.inge.cone_cache.ConeCache;

@Component
public class ConeCacheRefreshTask {
  private static final Logger logger = Logger.getLogger(ConeCacheRefreshTask.class);

  public ConeCacheRefreshTask() {}

  @Scheduled(cron = "${inge.cron.conecache.refresh}")
  public void run() {
    logger.info("CRON: CONE-Cache refresh task starts...");

    try {
      ConeCache.refreshCache();
    } catch (Exception e) {
      logger.error("Error in CONE-Cache refresh task.", e);
    }

    logger.info("CRON: CONE-Cache refresh task finished.");
  }
}
