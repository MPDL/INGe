package de.mpg.mpdl.inge.pubman.web.util.threads;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SiteMapTaskScheduler {

  @Scheduled(cron = "${inge.cron.pubman.sitemap}")
  public void createSiteMap() {
    new SiteMapTask().run();
  }

}
